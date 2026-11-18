package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AuthoritiesHistoryVo;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ReviewerSuggestedRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.AuthoritiesHistoryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AuthoritiesHistoryServiceImpl implements AuthoritiesHistoryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    AuthoritiesHistoryRepo authoritiesHistoryRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserJournalRepo userJournalRepo;

    @Autowired
    ManuScriptReviewerRepo manuScriptReviewerRepo;

    @Autowired
    ManuScriptRepo manuScriptRepo;

    @Autowired
    ReviewerSuggestedRepo reviewerSuggestedRepo;

    @Override
    public String authoritiesHistory(AuthoritiesHistoryVo authoritiesHistoryVo) {

        ///////////////////////////////////////////////////////////////////////////////////
        /**
         * Author: Naresh Verma
         * Date: 2023-10-20
         * Description: This code handles the final decision for a manuscript.
         */
        if ((authoritiesHistoryVo.getCommentsToAuthor() == null || !StringUtils.isNotBlank(authoritiesHistoryVo.getCommentsToAuthor())) && authoritiesHistoryVo.getManuscriptStatusId() == 33) {
            ////// server response multiple time then comment To author in Final decision is blank ///////////////
            logger.info("data not saved for manuscript:  " + authoritiesHistoryVo.getManuscriptId());
            logger.info("This logger for multiple entry for final decision ::");
            return "data not saved OR server response multiple time";
        } else if (authoritiesHistoryVo.getManuscriptStatusId() == 33) {
            logger.info("comment To author in Final decision  :  " + authoritiesHistoryVo.getCommentsToAuthor());
        }

        if (authoritiesHistoryVo.getManuscriptStatusId() == 4) {
            Integer histCheck = authoritiesHistoryRepo.hasAuthoritiesHistoryByStatusId(authoritiesHistoryVo.getManuscriptId(), 33);
            if (histCheck != null && histCheck >= 1) {
                logger.info("manuscript : " + authoritiesHistoryVo.getManuscriptId() + " already in final decision, so can't revision");
                return "this manuscript already in final decision, so can't revision";
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////

        ManuScript updateManuscript = manuScriptRepo.getOne(authoritiesHistoryVo.getManuscriptId());
        if (authoritiesHistoryVo.getCurrentUserId() != null && !authoritiesHistoryVo.getCurrentUserId().isEmpty()) {
            authoritiesHistoryVo.getCurrentUserId().stream().forEach(j -> {
                AuthoritiesHistory i = new AuthoritiesHistory();

                if (authoritiesHistoryVo.getManuscriptStatusId() != null
                        && (authoritiesHistoryVo.getManuscriptStatusId().equals(8) || authoritiesHistoryVo.getManuscriptStatusId().equals(66))) {
                    ReviewerSuggested reviewerSuggested = null;
                    reviewerSuggested = reviewerSuggestedRepo.findBySuggestedReviewerId(j);
                    if (reviewerSuggested != null && reviewerSuggested.getEmail() != null) {
                        User user = reviewerSuggestedRepo.findByEmail(reviewerSuggested.getEmail());
                        i.setCurrentUserId(user.getId());
                    } else {
                        i.setCurrentUserId(j);
                    }
                } else {
                    i.setCurrentUserId(j);
                }
                i.setCurrentUserRole(authoritiesHistoryVo.getCurrentUserRole());
                i.setManuscriptStatusId(authoritiesHistoryVo.getManuscriptStatusId());
                i.setPreviousUserId(authoritiesHistoryVo.getPreviousUserId());
                i.setPreviousUserRole(authoritiesHistoryVo.getPreviousUserRole());
                i.setManuscriptId(authoritiesHistoryVo.getManuscriptId());
                i.setManuscriptVersion(authoritiesHistoryVo.getManuscriptVersion());
                i.setJournalId(authoritiesHistoryVo.getJournalId());
                i.setCommentsToAuthor(authoritiesHistoryVo.getCommentsToAuthor());
                i.setCommentsToEditor(authoritiesHistoryVo.getCommentsToEditor());
                i.setCommentsToReviewer(authoritiesHistoryVo.getCommentsToReviewer());
                i.setCommentsToEIC(authoritiesHistoryVo.getCommentsToEIC());
                i.setCreatedAt(new Date());
                i.setDueDate(authoritiesHistoryVo.getDueDate());
                AuthoritiesHistory authoritiesHistorySaved = authoritiesHistoryRepo.save(i);

                //Retrying the authority history saving if not saved
                authoritiesHistoryRepo.save(i);


                logger.info("Status " + authoritiesHistoryVo.getManuscriptStatusId() + " Successfully saved in authoritiesHistory table for manuscript : " + authoritiesHistoryVo.getManuscriptId());

                if (authoritiesHistoryVo.getDueDate() != null) {
                    updateManuscript.setDueDate(authoritiesHistoryVo.getDueDate());
                    manuScriptRepo.saveAndFlush(updateManuscript);
                }
            });

            return "Successfully saved";
        }
        return "Please current user id";
    }


    @Override
    public List<AuthoritiesHistoryVo> getAuthoritiesHistory(AuthoritiesHistoryVo authoritiesHistoryVo) {
        List<AuthoritiesHistoryVo> p = new ArrayList<AuthoritiesHistoryVo>();
        List<AuthoritiesHistory> j = authoritiesHistoryRepo
                .getAuthoritiesHistory(authoritiesHistoryVo.getManuscriptId(), authoritiesHistoryVo.getJournalId());


        if (j != null && !j.isEmpty()) {
            j.stream().forEach(i -> {
                AuthoritiesHistoryVo k = new AuthoritiesHistoryVo();
                k.setCreatedAt(i.getCreatedAt());
                k.setCurrentUserid(i.getCurrentUserId());
                if (i.getJournalId() != null && i.getCurrentUserId() != null && i.getManuscriptStatusId() != null
                        && i.getManuscriptStatusId().equals(2)) {
                    if (i.getCurrentUserRole() != null && i.getCurrentUserRole().equals(2)) {
                        List<UserJournal> getEicList = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 2);
                        if (getEicList != null && !getEicList.isEmpty() && getEicList.size() > 1 && (i.getPreviousUserRole().equals(4) || i.getPreviousUserRole().equals(1))) {
                            k.setCurrentUserName("All" + " (" + getEicList.size() + ") ");
                        } else {
                            User l = userRepo.getUser(i.getCurrentUserId());
                            if (l != null) {
                                k.setCurrentUserName(l.getSalutation() + ". " + l.getFirstName() + " " + l.getLastName());
                            }
                        }
                    }
                } else if (i.getJournalId() != null && i.getCurrentUserId() != null && i.getManuscriptStatusId() != null
                        && i.getManuscriptStatusId().equals(64)) {
                    if (i.getCurrentUserRole() != null && i.getCurrentUserRole().equals(1)) {
                        List<UserJournal> getAdminList = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 1);
                        if (getAdminList != null && !getAdminList.isEmpty() && getAdminList.size() > 1) {
                            k.setCurrentUserName("All" + " (" + getAdminList.size() + ") ");
                        } else {
                            User l = userRepo.getUser(i.getCurrentUserId());
                            if (l != null) {
                                k.setCurrentUserName(l.getSalutation() + ". " + l.getFirstName() + " " + l.getLastName());
                            }
                        }
                    }
                } else {
                    User l = userRepo.getUser(i.getCurrentUserId());
                    if (l != null) {
                        k.setCurrentUserName(l.getSalutation() + ". " + l.getFirstName() + " " + l.getLastName());
                    }
                }

                ManuScript manuscript = manuScriptRepo.getOne(authoritiesHistoryVo.getManuscriptId());
                if (manuscript != null && i.getStatusMaster().getStatusValueForArticleLifeCycle().equalsIgnoreCase("Rejected")) {
                    k.setRejectedDate(manuscript.getRejectedDate());
                }

                k.setCurrentUserRole(i.getCurrentUserRole());
                k.setManuscriptStatusId(i.getManuscriptStatusId());
                k.setArticleStatus(i.getStatusMaster().getStatusValueForArticleLifeCycle());
                k.setJournalId(i.getJournalId());
                k.setManuscriptId(i.getManuscriptId());
                k.setCommentsToAuthor(i.getCommentsToAuthor());
                k.setCommentsToEditor(i.getCommentsToEditor());
                k.setCommentsToReviewer(i.getCommentsToReviewer());
                k.setCommentsToEIC(i.getCommentsToEIC());
                k.setManuscriptVersion(i.getManuscriptVersion());
                k.setPreviousUserId(i.getPreviousUserId());
                k.setDueDate(i.getDueDate());
                if (i.getPreviousUserId() != null) {
                    User o = userRepo.getUser(i.getPreviousUserId());
                    if (o != null) {
                        k.setPreviousUserName(o.getSalutation() + ". " + o.getFirstName() + " " + o.getLastName());
                    }
                }
                k.setPreviousUserRole(i.getPreviousUserRole());
                k.setId(i.getId());
                p.add(k);
            });
        }
        return p;
    }

}
