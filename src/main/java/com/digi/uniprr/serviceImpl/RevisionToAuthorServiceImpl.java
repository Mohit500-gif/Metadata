package com.digi.uniprr.serviceImpl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AssignEditorVO;
import com.digi.uniprr.VO.RevisionToAuthorVO;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ReviewerSuggestedRepo;
import com.digi.uniprr.service.RevisionToAuthorService;

@Service
public class RevisionToAuthorServiceImpl implements RevisionToAuthorService{
	
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ManuScriptRepo manuScriptRepo;
	
	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;
	
	@Autowired
	ReviewerSuggestedRepo reviewerSuggestedRepo;

	@Override
    public String authoritiesHistory(RevisionToAuthorVO revisionToAuthorVO) {

        if ((revisionToAuthorVO.getAuthoritiesHistoryVo().getCommentsToAuthor() == null || !StringUtils.isNotBlank(revisionToAuthorVO.getAuthoritiesHistoryVo().getCommentsToAuthor())) && revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() == 33) {
            ////// server response multiple time then comment To author in Final decision is blank ///////////////
            log.info("data not saved for manuscript:  " + revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());
            log.info("This logger for multiple entry for final decision ::");
            return "data not saved OR server response multiple time";
        } else if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() == 33) {
            log.info("comment To author in Final decision  :  " + revisionToAuthorVO.getAuthoritiesHistoryVo().getCommentsToAuthor());
        }

        if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() == 4) {
            Integer histCheck = authoritiesHistoryRepo.hasAuthoritiesHistoryByStatusId(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId(), 33);
            if (histCheck != null && histCheck >= 1) {
                log.info("manuscript : " + revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId() + " already in final decision, so can't revision");
                return "this manuscript already in final decision, so can't revision";
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////

        ManuScript updateManuscript = manuScriptRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());
        if (revisionToAuthorVO.getAuthoritiesHistoryVo().getCurrentUserId() != null && !revisionToAuthorVO.getAuthoritiesHistoryVo().getCurrentUserId().isEmpty()) {
        	revisionToAuthorVO.getAuthoritiesHistoryVo().getCurrentUserId().stream().forEach(j -> {
                AuthoritiesHistory i = new AuthoritiesHistory();

                if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
                        && (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(8) || revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(66))) {
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
                i.setCurrentUserRole(revisionToAuthorVO.getAuthoritiesHistoryVo().getCurrentUserRole());
                i.setManuscriptStatusId(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId());
                i.setPreviousUserId(revisionToAuthorVO.getAuthoritiesHistoryVo().getPreviousUserId());
                i.setPreviousUserRole(revisionToAuthorVO.getAuthoritiesHistoryVo().getPreviousUserRole());
                i.setManuscriptId(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());
                i.setManuscriptVersion(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptVersion());
                i.setJournalId(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
                i.setCommentsToAuthor(revisionToAuthorVO.getAuthoritiesHistoryVo().getCommentsToAuthor());
                i.setCommentsToEditor(revisionToAuthorVO.getAuthoritiesHistoryVo().getCommentsToEditor());
                i.setCommentsToReviewer(revisionToAuthorVO.getAuthoritiesHistoryVo().getCommentsToReviewer());
                i.setCommentsToEIC(revisionToAuthorVO.getAuthoritiesHistoryVo().getCommentsToEIC());
                i.setCreatedAt(new Date());
                i.setDueDate(revisionToAuthorVO.getAuthoritiesHistoryVo().getDueDate());
                AuthoritiesHistory authoritiesHistorySaved = authoritiesHistoryRepo.save(i);

                //Retrying the authority history saving if not saved
                authoritiesHistoryRepo.save(i);


                log.info("Status " + revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() + " Successfully saved in authoritiesHistory table for manuscript : " + revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());

                if (revisionToAuthorVO.getAuthoritiesHistoryVo().getDueDate() != null) {
                    updateManuscript.setDueDate(revisionToAuthorVO.getAuthoritiesHistoryVo().getDueDate());
                    manuScriptRepo.saveAndFlush(updateManuscript);
                }
            });

            return "Successfully saved";
        }
        return "Please current user id";
    }

	
}
