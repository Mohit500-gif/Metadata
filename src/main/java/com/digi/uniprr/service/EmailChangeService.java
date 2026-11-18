package com.digi.uniprr.service;

import com.digi.uniprr.VO.ManuscriptIdCodeProjection;
import com.digi.uniprr.dto.EmailChangeRequest;
import com.digi.uniprr.dto.SearchResultDTO;
import com.digi.uniprr.integration.UnitouchClient;
import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailChangeService {

    private final UserRepo userRepository;
    private final UserJournalRepo userJournalRepository;
    private final EmailChangeHistoryRepository historyRepository;
    private final ManuScriptRepo manuscriptRepository;
    private final ManuScriptCoAuthorRepo coAuthorRepository;
    private final MailQueueRepo mailQueueRepository;
    private final ManuScriptReviewerRepo reviewerRepository;
    private final ManuscriptEditorRepo editorRepository;
    private final UnitouchClient unitouchClient;
    private final AsyncEmailUpdateService asyncService;

    @Autowired
    UserRoleRepo userRoleRepo;

    @Autowired
    AuthoritiesHistoryRepo authoritiesHistoryRepo;

    public EmailChangeService(UserRepo userRepository,
                              UserJournalRepo userJournalRepository,
                              EmailChangeHistoryRepository historyRepository,
                              ManuScriptRepo manuscriptRepository,
                              ManuScriptCoAuthorRepo coAuthorRepository,
                              MailQueueRepo mailQueueRepository,
                              ManuScriptReviewerRepo reviewerRepository,
                              ManuscriptEditorRepo editorRepository,
                              UnitouchClient unitouchClient,
                              AsyncEmailUpdateService asyncService) {

        this.userRepository = userRepository;
        this.userJournalRepository = userJournalRepository;
        this.historyRepository = historyRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.coAuthorRepository = coAuthorRepository;
        this.mailQueueRepository = mailQueueRepository;
        this.reviewerRepository = reviewerRepository;
        this.editorRepository = editorRepository;
        this.unitouchClient = unitouchClient;
        this.asyncService = asyncService;
    }

    // Search only active journals
    public SearchResultDTO searchByOldEmail(String oldEmail) {
        if (oldEmail == null || oldEmail.trim().isEmpty())
            throw new IllegalArgumentException("Old email cannot be empty.");

        User oldUser = userRepository.getEmail(oldEmail.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Old email not found: " + oldEmail));

        List<UserJournal> links = userJournalRepository.findActiveByUserId(oldUser.getId());

        List<SearchResultDTO.JournalInfo> journals =
                links.stream()
                        .filter(uj -> uj.getJournal() != null)
                        .filter(uj -> uj.getJournal().getIsActive() != null && uj.getJournal().getIsActive() == 1)
                        .map(uj -> new SearchResultDTO.JournalInfo(
                                uj.getJournal().getJournalId(),
                                uj.getJournal().getJournalTitle()
                        ))
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        SearchResultDTO.JournalInfo::getJournalId,
                                        j -> j,
                                        (a, b) -> a
                                ),
                                m -> new ArrayList<>(m.values())
                        ));

        logger.info("Found {} active journals for {}", journals.size(), oldEmail);
        return new SearchResultDTO(oldEmail, journals);
    }


    // Main process
    @Transactional
    public String processEmailChange(EmailChangeRequest req, Integer adminUserId) {

        String oldEmail = req.getOldEmail().trim().toLowerCase();
        String newEmail = req.getNewEmail().trim().toLowerCase();
        List<Integer> journalIds = req.getJournalIds();

        validateEmailFormat(newEmail);

        User oldUser = userRepository.getEmail(oldEmail)
                .orElseThrow(() -> new IllegalArgumentException("Old email not found"));

        Optional<User> newUserOpt = userRepository.getEmail(newEmail);

        boolean hasJournals = (journalIds != null && !journalIds.isEmpty());



        User newUser;
        if (!newUserOpt.isPresent()) {
            newUser = userRepository.save(copyUserDetails(oldUser, newEmail, adminUserId));

            for(UserRole roleObj: oldUser.getUserRole()){
                logger.info("Created new role for user: {}", roleObj.getRole());
                UserRole newUserRole = new UserRole();
                newUserRole.setRole(roleObj.getRole());
                newUserRole.setUser(newUser.getId());
                newUserRole.setIsActive(1);
                newUserRole.setUpdatedOn(new Date());
                userRoleRepo.save(newUserRole);
            }

            logger.info("Created new user: {}", newUser.getId());

            loggergerHistory(oldUser.getId(), newUser.getId(), oldEmail, newEmail, null,
                    adminUserId, "Created new user and reassigned references.");
        } else {
            newUser = newUserOpt.get();
        }

        Integer oldUserId = oldUser.getId();
        Integer newUserId = newUser.getId();

        if (journalIds == null || journalIds.isEmpty()) {
            journalIds = userJournalRepository.findActiveByUserId(oldUserId)
                    .stream()
                    .map(uj -> uj.getJournal().getJournalId())
                    .collect(Collectors.toList());
        }
        if(journalIds.isEmpty()){
            return "User is not active or does not exist";
        }
        List<String> manuscriptCodes=null;
        for (Integer journalId : journalIds) {
//            List<ManuscriptIdCodeProjection> manuscripts =
//                    manuscriptRepository.findManuscriptIdsAndCodesByJournals(
//                            hasJournals ? journalIds : Collections.emptyList(),
//                            hasJournals
//                    );
            List<ManuscriptIdCodeProjection> manuscripts =
                    manuscriptRepository.findManuscriptIdsAndCodesByJournal(journalId);

            List<Integer> manuscriptIds = manuscripts.stream()
                    .map(ManuscriptIdCodeProjection::getManuScriptId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            manuscriptCodes = manuscripts.stream()
                    .map(ManuscriptIdCodeProjection::getManuscriptCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            logger.info("Fetched {} manuscripts for {} → {}", manuscripts.size(), oldEmail, manuscriptCodes);
            processJournalTransfer(oldUser, newUser, journalId, adminUserId,
                    oldEmail, newEmail, manuscriptIds);
        }

        // Async UniTouch sync
        asyncService.asyncUnitouchSync(unitouchClient, oldEmail, newEmail, manuscriptCodes);

        return newUserOpt.isPresent()
                ? "Successfully reassigned to existing user."
                : "New user created and reassigned successfully.";
    }


    // Copy user for new account
    private User copyUserDetails(User oldUser, String newEmail, Integer adminUserId) {

        User newUser = new User();

        newUser.setUsername(newEmail);
        newUser.setEmail(newEmail);
        newUser.setPassword(oldUser.getPassword());
        newUser.setToken(oldUser.getToken());
        newUser.setCreatedBy(adminUserId != null ? adminUserId : oldUser.getId());
        newUser.setCreatedDate(new Date());
        newUser.setIsActive(1);

        // Copy all remaining properties
        newUser.setOrcidId(oldUser.getOrcidId());
        newUser.setResearcherId(oldUser.getResearcherId());
        newUser.setTrialRegistrationNo(oldUser.getTrialRegistrationNo());
        newUser.setPubmedId(oldUser.getPubmedId());
        newUser.setScopusId(oldUser.getScopusId());
        newUser.setAfflication(oldUser.getAfflication());
        newUser.setAcademic(oldUser.getAcademic());
        newUser.setEmailCC(oldUser.getEmailCC());
        newUser.setSecondaryEmail(oldUser.getSecondaryEmail());
        newUser.setSecondaryEmailCC(oldUser.getSecondaryEmailCC());
        newUser.setSalutation(oldUser.getSalutation());
        newUser.setFirstName(oldUser.getFirstName());
        newUser.setMiddleName(oldUser.getMiddleName());
        newUser.setLastName(oldUser.getLastName());
        newUser.setUserType(oldUser.getUserType());
        newUser.setInstitute(oldUser.getInstitute());
        newUser.setState(oldUser.getState());
        newUser.setAddress(oldUser.getAddress());
        newUser.setAddressTwo(oldUser.getAddressTwo());
        newUser.setAddressThree(oldUser.getAddressThree());
        newUser.setDepartment(oldUser.getDepartment());
        newUser.setPhoneNumber(oldUser.getPhoneNumber());
        newUser.setMobileNumber(oldUser.getMobileNumber());
        newUser.setFaxNumber(oldUser.getFaxNumber());
        newUser.setCountryId(oldUser.getCountryId());
        newUser.setCity(oldUser.getCity());
        newUser.setPostalCode(oldUser.getPostalCode());
        newUser.setAreaOfInterest(oldUser.getAreaOfInterest());
        newUser.setNotificationFlag(oldUser.getNotificationFlag());
        newUser.setPublicationName(oldUser.getPublicationName());
        newUser.setAccountExpired(oldUser.getAccountExpired());
        newUser.setAccountLocked(oldUser.getAccountLocked());
        newUser.setDesignation(oldUser.getDesignation());
        newUser.setApplyAsReviewer(oldUser.getApplyAsReviewer());
        newUser.setSpecialization(oldUser.getSpecialization());
        newUser.setExperience(oldUser.getExperience());
        newUser.setSpecialRemark(oldUser.getSpecialRemark());
        newUser.setExpertise(oldUser.getExpertise());
        newUser.setEmailNotification(oldUser.getEmailNotification());
        newUser.setSmsNotification(oldUser.getSmsNotification());
        newUser.setMarketingNotification(oldUser.getMarketingNotification());
        newUser.setProductNotification(oldUser.getProductNotification());
        newUser.setCountryCode(oldUser.getCountryCode());
        newUser.setEditorAccessDetails(oldUser.getEditorAccessDetails());
        newUser.setSearchArticleStatus(oldUser.getSearchArticleStatus());
        newUser.setUserQualification(oldUser.getUserQualification());
        newUser.setFiveBestPapers(oldUser.getFiveBestPapers());
        newUser.setNumberOfArticles(oldUser.getNumberOfArticles());

        return newUser;
    }

    private void processJournalTransfer(User oldUser,
                                        User newUser,
                                        Integer journalId,
                                        Integer adminUserId,
                                        String oldEmail,
                                        String newEmail,
                                        List<Integer> manuscriptIds) {

        Integer oldUserId = oldUser.getId();
        Integer newUserId = newUser.getId();

        logger.info("Processing journal {} (old={}, new={})", journalId, oldUserId, newUserId);

        try {
            int exists = userJournalRepository.existsNewUserMapping(newUserId, journalId);

            if (exists > 0) {
                userJournalRepository.deactivateOldUserJournal(oldUserId, journalId);
                userJournalRepository.activateExistingNewUser(newUserId, journalId, adminUserId);
            } else {
                userJournalRepository.replaceOldUserWithNew(oldUserId, newUserId, journalId, adminUserId);
            }

        } catch (Exception e) {
            logger.error("Error updating user_journal: {}", e.getMessage());
        }

        safeExecute(() ->
                        manuscriptRepository.updateAuthorByJournal(oldUserId, newUserId, journalId),
                "manuscript updateAuthor");

        safeExecute(() ->
                        manuscriptRepository.updateAssignedUserByJournal(oldUserId, newUserId, journalId),
                "manuscript updateAssignedUser");

        if (!manuscriptIds.isEmpty()) {

            logger.info("🔍 Starting coauthor/reviewer/editor updates for {} manuscripts (oldUserId={}, newUserId={})",
                    manuscriptIds.size(), oldUserId, newUserId);

            // CO-AUTHOR UPDATE
            safeExecute(() -> {
                long start = System.currentTimeMillis();
                logger.info("➡️ [START] CoAuthor update for {} manuscriptIds", manuscriptIds.size());

                int updated = coAuthorRepository.bulkUpdateCoAuthor(oldUserId, newUserId, manuscriptIds);

                long end = System.currentTimeMillis();
                logger.info("✔️ [DONE] CoAuthor update completed. Rows updated = {} (Time={} ms)",
                        updated, (end - start));
            }, "coauthor update");


            // REVIEWER UPDATE
            safeExecute(() -> {
                long start = System.currentTimeMillis();
                logger.info("➡️ [START] Reviewer update for {} manuscriptIds", manuscriptIds.size());

                int updated = reviewerRepository.bulkUpdateReviewer(oldUserId, newUserId, manuscriptIds);

                long end = System.currentTimeMillis();
                logger.info("✔️ [DONE] Reviewer update completed. Rows updated = {} (Time={} ms)",
                        updated, (end - start));
            }, "reviewer update");


            // EDITOR UPDATE
            safeExecute(() -> {
                long start = System.currentTimeMillis();
                logger.info("➡️ [START] Editor update for {} manuscriptIds", manuscriptIds.size());

                int updated = editorRepository.bulkUpdateEditor(oldUserId, newUserId, manuscriptIds);

                long end = System.currentTimeMillis();
                logger.info("✔️ [DONE] Editor update completed. Rows updated = {} (Time={} ms)",
                        updated, (end - start));
            }, "editor update");

            // Authority History UPDATE
            safeExecute(() -> {
                long start = System.currentTimeMillis();
                logger.info("➡️ [START] Authority History update for {} manuscriptIds", manuscriptIds.size());

                int updated = authoritiesHistoryRepo.bulkUpdateAuthority(oldUserId, newUserId, manuscriptIds);

                long end = System.currentTimeMillis();
                logger.info("✔️ [DONE] Authority History update completed. Rows updated = {} (Time={} ms)",
                        updated, (end - start));
            }, "Authority History update");
        }


        safeExecute(() ->
                        mailQueueRepository.updateMailTo(oldEmail, newEmail, newUserId, journalId),
                "mailQueue update");

        loggergerHistory(oldUserId, newUserId, oldEmail, newEmail, journalId,
                adminUserId, "Email updated for journal");
    }

    private void validateEmailFormat(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.compile(regex).matcher(email).matches())
            throw new IllegalArgumentException("Invalid email: " + email);
    }

    private void safeExecute(Runnable action, String name) {
        try {
            action.run();
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            logger.warn("Skipped {} due to constraint issue: {}", name, e.getMessage());
        } catch (Exception e) {
            logger.error("Error in {}: {}", name, e.getMessage());
        }
    }

    private void loggergerHistory(Integer oldUserId, Integer newUserId, String oldEmail, String newEmail,
                               Integer journalId, Integer adminUserId, String remarks) {

        try {
            EmailChangeHistory h = new EmailChangeHistory();
            h.setOldEmail(oldEmail);
            h.setNewEmail(newEmail);
            h.setOldUserId(oldUserId);
            h.setNewUserId(newUserId);
            h.setJournalId(journalId);
            h.setActionBy(adminUserId);
            h.setActionDate(LocalDateTime.now());
            h.setRemarks(remarks);
            historyRepository.save(h);

        } catch (Exception e) {
            logger.error("Failed to logger history: {}", e.getMessage());
        }
    }
}
