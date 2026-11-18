package com.digi.uniprr.serviceImpl;

import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.service.CopyrightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CopyrightServiceImpl implements CopyrightService {

    private final ManuScriptRepo manuScriptRepo;
    private final ManuScriptCoAuthorRepo coAuthorRepo;
    private final UserRepo userRepo;
    private final MailQueueRepo mailQueueRepo;
    private final JournalMailTemplatesRepo journalMailTemplatesRepo;
    private final JournalsRepo journalRepo;
    private final TaskEmailMapRepo taskEmailMapRepo;

    @Value("${copyRightUrlForCoAuthor}")
    private String copyRightUrlForCoAuthor;

    @Value("${localUrl}")
    private String siteUrl;

    public CopyrightServiceImpl(
            ManuScriptRepo manuScriptRepo,
            ManuScriptCoAuthorRepo coAuthorRepo,
            UserRepo userRepo,
            MailQueueRepo mailQueueRepo,
            JournalMailTemplatesRepo journalMailTemplatesRepo,
            JournalsRepo journalRepo,
            TaskEmailMapRepo taskEmailMapRepo) {

        this.manuScriptRepo = manuScriptRepo;
        this.coAuthorRepo = coAuthorRepo;
        this.userRepo = userRepo;
        this.mailQueueRepo = mailQueueRepo;
        this.journalMailTemplatesRepo = journalMailTemplatesRepo;
        this.journalRepo = journalRepo;
        this.taskEmailMapRepo = taskEmailMapRepo;
    }

    @Override
    public String queueCopyrightEmails(Integer manuscriptId, Integer journalId, List<ManuScriptCoAuthor> authorsList,boolean flag) {

        ManuScript manuscript = manuScriptRepo.findById(manuscriptId).orElse(null);
        if (manuscript == null) {
            return "❌ Manuscript not found.";
        }

        Journal journal = journalRepo.findById(journalId).orElse(null);
        String fromMail = (journal != null && journal.getJournalMetadata() != null
                && journal.getJournalMetadata().getJournalEmail() != null)
                ? journal.getJournalMetadata().getJournalEmail()
                : "noreply@medknow.com";

        // 🔹 Step 1: Get mapped template via TaskEmailMap
        final int COPYRIGHT_TASK_ID = 102;
        TaskEmailMap map = taskEmailMapRepo.findByTaskIdAndJournalId(COPYRIGHT_TASK_ID, journalId);
        JournalMailTemplates template = null;
        if (map != null && map.getJournalEmailTemplateId() != null) {
            template = journalMailTemplatesRepo.findById(map.getJournalEmailTemplateId()).orElse(null);
        }

        int queuedCount = 0;

        // 🔹 Step 2: Process all co-authors (exclude primary)
        List<ManuScriptCoAuthor> coAuthors = authorsList.stream()
                .filter(a -> a.getIsCorrespondingAuthor().equals("N"))
                .collect(Collectors.toList());

        for (ManuScriptCoAuthor coAuthor : coAuthors) {
            try {
                User user = userRepo.findById(coAuthor.getCoAuthorId()).orElse(null);
                if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                    logger.warn("Skipping co-author id={} — invalid email", coAuthor.getCoAuthorId());
                    continue;
                }

                // 🔹 Avoid duplicate queue
                if(flag) {
                List<MailQueue> existing = mailQueueRepo
                        .findByManuscriptIdAndMailToAndTaskId(manuscriptId, user.getEmail(), COPYRIGHT_TASK_ID);
                if (!existing.isEmpty()) {
                    logger.info("⚠️ Mail already queued for {}", user.getEmail());
                    continue;
                }
                }

                // 🔹 Build copyright URL
                String copyrightLink = copyRightUrlForCoAuthor
                        + "?journal=" + journalId
                        + "&author=" + coAuthor.getId();

                // 🔹 Prepare placeholders
                String salutation = (user.getSalutation() != null) ? user.getSalutation().toString() : "";
                String firstName = Optional.ofNullable(user.getFirstName()).orElse("");
                String lastName = Optional.ofNullable(user.getLastName()).orElse("");
                String manuscriptTitle = Optional.ofNullable(manuscript.getManuscriptTitle()).orElse("");
                String journalTitle = (manuscript.getJournal() != null)
                        ? manuscript.getJournal().getJournalTitle() : "";
                String clickableCopyrightLink =
                        "<a href='" + copyrightLink + "' target='_blank'>" + copyrightLink + "</a>";
                String clickableSiteUrl =
                        "<a href='" + siteUrl + "' target='_blank'>" + siteUrl + "</a>";
                // 🔹 Build email body & subject
                String subject;
                String body;

                if (template != null) {
                    subject = Optional.ofNullable(template.getMailSubject())
                            .orElse("Copyright Agreement - " + manuscriptTitle);

                    body = template.getMailBody()
                            .replace("[AUTHOR_SALUTATION]", salutation)
                            .replace("[AUTHOR_FIRST_NAME]", firstName)
                            .replace("[AUTHOR_LAST_NAME]", lastName)
                            .replace("[MANUSCRIPT_TITLE]", manuscriptTitle)
                            .replace("[JOURNAL_TITLE]", journalTitle)
                            .replace("[COPYRIGHT_URL]", clickableCopyrightLink)
                            .replace("[USERNAME]", user.getEmail())
                            .replace("[URL]", clickableSiteUrl);
                } else {
                    // 🔹 Default fallback HTML
                    subject = "Copyright Agreement - " + manuscriptTitle;
                    body = "<p>Dear " + salutation + " " + lastName + ",</p>"
                            + "<p>You have been added as a co-author for the article titled <b>"
                            + manuscriptTitle + "</b> submitted to <b>" + journalTitle + "</b>.</p>"
                            + "<p>Please complete your copyright agreement by clicking below:</p>"
                            + "<p><a href='" + copyrightLink + "' target='_blank'>" + copyrightLink + "</a></p>"
                            + "<p>Your username: " + user.getEmail() + "</p>"
                            + "<br><p>Regards,<br>Editorial Team<br>" + journalTitle + "</p>";
                }

                // 🔹 Queue mail in mailQueue table
                MailQueue mail = new MailQueue();
                mail.setJournalId(journalId);
                mail.setManuscriptId(manuscriptId);
                mail.setMailTo(user.getEmail());
                mail.setMailFrom(fromMail);
                mail.setMailSubject(subject);
                mail.setMailBody(body);
                mail.setIsActive(0); // 0 = pending
                mail.setMail_send_date(new Date());
                mail.setModuleName("COPYRIGHT");
                mail.setTaskId(COPYRIGHT_TASK_ID);
                mail.setMailToRole(4);
                mail.setMailTOUserId(coAuthor.getCoAuthorId());
                mailQueueRepo.saveAndFlush(mail);

                // 🔹 Mark author email as sent
                coAuthor.setIsEmailSent(1);
                coAuthorRepo.updateIsEmailSentById(coAuthor.getId(), 1);

                queuedCount++;
                logger.info("Queued copyright email for {}", user.getEmail());

            } catch (Exception e) {
                logger.error("Error while queuing copyright mail for co-author {}", coAuthor.getCoAuthorId(), e);
            }
        }

        return queuedCount > 0
                ? "Copyright Email sent successfully."
                : "No co-authors were eligible for copyright emails.";
    }
    
    @Override
    public String resendCopyrightLink(Integer manuscriptId, Integer journalId, Integer coAuthorId) {
        ManuScriptCoAuthor coAuthor = coAuthorRepo.findById(coAuthorId).orElse(null);
        if (coAuthor == null) {
            return "❌ Co-author not found.";
        }

        List<ManuScriptCoAuthor> authorList = new ArrayList<>();
        authorList.add(coAuthor);

        // ✅ reuse your existing queueCopyrightEmails logic
        boolean mailFlag = false;
        return queueCopyrightEmails(manuscriptId, journalId, authorList,mailFlag);
    }
    
    public void triggerCopyrightConfirmationEmail(ManuScript manuscript, User user) {
        try {
            Integer journalId = manuscript.getJournal().getJournalId();
            final int CONFIRM_TASK_ID = 103;

            TaskEmailMap map = taskEmailMapRepo.findByTaskIdAndJournalId(CONFIRM_TASK_ID, journalId);
            JournalMailTemplates template = null;
            if (map != null && map.getJournalEmailTemplateId() != null) {
                template = journalMailTemplatesRepo.findById(map.getJournalEmailTemplateId()).orElse(null);
            }

            String fromMail = Optional.ofNullable(manuscript.getJournal().getJournalMetadata().getJournalEmail())
                    .orElse("noreply@medknow.com");

            String journalCode = Optional.ofNullable(manuscript.getJournal().getJournalCode()).orElse("");
            String manuscriptCode = Optional.ofNullable(manuscript.getManuscriptCode()).orElse("");
            String salutation = (user.getSalutation() != null) ? user.getSalutation().toString() : "";
            String firstName = Optional.ofNullable(user.getFirstName()).orElse("");
            String lastName = Optional.ofNullable(user.getLastName()).orElse("");
            String journalTitle = Optional.ofNullable(manuscript.getJournal().getJournalTitle()).orElse("");
            String manuscriptTitle = Optional.ofNullable(manuscript.getManuscriptTitle()).orElse("");

            String subject;
            String body;

            if (template != null) {
                subject = template.getMailSubject()
                        .replace("[JOURNAL_CODE]", journalCode)
                        .replace("[MANUSCRIPT_ID]", manuscriptCode);

                body = template.getMailBody()
                        .replace("[AUTHOR_SALUTATION]", salutation)
                        .replace("[AUTHOR_FIRST_NAME]", firstName)
                        .replace("[AUTHOR_LAST_NAME]", lastName)
                        .replace("[MANUSCRIPT_TITLE]", manuscriptTitle)
                        .replace("[JOURNAL_TITLE]", journalTitle);
            } else {
                subject = "[" + journalCode + "] Author Copyright Agreement Confirmation Letter: [" + manuscriptCode + "]";
                body = "<p>Dear " + salutation + " " + lastName + ",</p>"
                        + "<p>Thank you for completing your copyright agreement for manuscript <b>"
                        + manuscriptTitle + "</b> submitted to <b>" + journalTitle + "</b>.</p>"
                        + "<p>Your confirmation has been successfully recorded.</p>"
                        + "<br><p>Regards,<br>Editorial Team<br>" + journalTitle + "</p>";
            }

            // Avoid duplicates
            List<MailQueue> existing = mailQueueRepo
                    .findByManuscriptIdAndMailToAndTaskId(manuscript.getManuScriptId(), user.getEmail(), CONFIRM_TASK_ID);
            if (!existing.isEmpty()) {
                logger.info("⚠️ Confirmation mail already queued for {}", user.getEmail());
                return;
            }

            // Queue mail
            MailQueue mail = new MailQueue();
            mail.setJournalId(journalId);
            mail.setManuscriptId(manuscript.getManuScriptId());
            mail.setMailTo(user.getEmail());
            mail.setMailFrom(fromMail);
            mail.setMailSubject(subject);
            mail.setMailBody(body);
            mail.setIsActive(0);
            mail.setMail_send_date(new Date());
            mail.setModuleName("COPYRIGHT_CONFIRMATION");
            mail.setTaskId(CONFIRM_TASK_ID);
            mailQueueRepo.saveAndFlush(mail);

            logger.info("📩 Queued copyright confirmation mail for {} (JournalId={})", user.getEmail(), journalId);
        } catch (Exception e) {
            logger.error("❌ Failed to queue copyright confirmation email", e);
        }
    }

}
