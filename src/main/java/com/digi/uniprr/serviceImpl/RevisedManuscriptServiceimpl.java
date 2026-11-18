package com.digi.uniprr.serviceImpl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.digi.uniprr.controller.dao.UserDao;
import com.digi.uniprr.VO.RevisedManuscriptVo;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.EditorRoleAccessRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.OtherMailRepo;
import com.digi.uniprr.repository.ReviewerAvailabilityRepo;
import com.digi.uniprr.repository.ReviewerSuggestedRepo;
import com.digi.uniprr.repository.RoleRepo;
import com.digi.uniprr.repository.TatEmailConfigurationRepo;
import com.digi.uniprr.repository.UserGroupRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserLoginsRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.repository.UserSpecilizationRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.AuthoritiesHistoryService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuscriptEditorService;
import com.digi.uniprr.service.RevisedManuscriptService;
import com.digi.uniprr.utils.SendMailUtils;

@Service
public class RevisedManuscriptServiceimpl implements RevisedManuscriptService{
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserLoginRepo userLoginRepo;

	@Autowired
	AuthoritiesHistoryService authoritiesHistoryService;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;

	@Autowired
	Environment env;

	@Autowired
	OtherMailRepo otherMailRepo;

	@Autowired
	SendMailUtils mailUtils;

	@Autowired
	UserRoleRepo userRoleRepo;

	@Autowired
	UserGroupRepo userGroupRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ManuScriptRepo manuscriptRepo;

	@Autowired
	MailQueueRepo mailQueueRepo;

	@Autowired
	CountriesService countriesService;

	@Autowired
	ManuScriptReviewerService manuscriptReviewerService;

	@Autowired
	ManuscriptEditorService manuscriptEditorService;

	@Autowired
	ManuScriptService manuscriptService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	UserSpecilizationRepo userSpecilizationRepo;

	@Autowired
	UserDao userDao;

	@Autowired
	ReviewerAvailabilityRepo reviewerAvailabilityRepo;

	@Autowired
	UserLoginsRepo userLoginsRepo;

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	EditorRoleAccessRepo editorRoleAccessRepo;

	@Autowired
	ReviewerSuggestedRepo reviewerSuggestedRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	RoleRepo roleRepo;

	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

	@Autowired
	TatEmailConfigurationRepo tatEmailConfigurationRepo;

	
	@Value("${tempPassword}")
	String tempPassword;

	@Value("${oauthBaseUrl}")
	String oauthBaseUrl;

	@Value("${oauthClientId}")
	String oauthClientId;

	@Value("${oauthClientSecret}")
	String oauthClientSecret;

	@Autowired
	CountriesRepo countriesRepo;
	
	    public String authoritiesHistory(RevisedManuscriptVo revisedManuscriptVo) {

	        if ((revisedManuscriptVo.getAuthoritiesHistoryVo().getCommentsToAuthor() == null || !StringUtils.isNotBlank(revisedManuscriptVo.getAuthoritiesHistoryVo().getCommentsToAuthor())) && revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() == 33) {
	            ////// server response multiple time then comment To author in Final decision is blank ///////////////
	            log.info("data not saved for manuscript:  " + revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());
	            log.info("This logger for multiple entry for final decision ::");
	            return "data not saved OR server response multiple time";
	        } else if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() == 33) {
	            log.info("comment To author in Final decision  :  " + revisedManuscriptVo.getAuthoritiesHistoryVo().getCommentsToAuthor());
	        }

	        if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() == 4) {
	            Integer histCheck = authoritiesHistoryRepo.hasAuthoritiesHistoryByStatusId(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId(), 33);
	            if (histCheck != null && histCheck >= 1) {
	                log.info("manuscript : " + revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId() + " already in final decision, so can't revision");
	                return "this manuscript already in final decision, so can't revision";
	            }
	        }
	        ////////////////////////////////////////////////////////////////////////////////////

	        ManuScript updateManuscript = manuScriptRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());
	        if (revisedManuscriptVo.getAuthoritiesHistoryVo().getCurrentUserId() != null && !revisedManuscriptVo.getAuthoritiesHistoryVo().getCurrentUserId().isEmpty()) {
	        	revisedManuscriptVo.getAuthoritiesHistoryVo().getCurrentUserId().stream().forEach(j -> {
	                AuthoritiesHistory i = new AuthoritiesHistory();

	                if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
	                        && (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(8) || revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(66))) {
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
	                i.setCurrentUserRole(revisedManuscriptVo.getAuthoritiesHistoryVo().getCurrentUserRole());
	                i.setManuscriptStatusId(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId());
	                i.setPreviousUserId(revisedManuscriptVo.getAuthoritiesHistoryVo().getPreviousUserId());
	                i.setPreviousUserRole(revisedManuscriptVo.getAuthoritiesHistoryVo().getPreviousUserRole());
	                i.setManuscriptId(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());
	                i.setManuscriptVersion(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptVersion());
	                i.setJournalId(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
	                i.setCommentsToAuthor(revisedManuscriptVo.getAuthoritiesHistoryVo().getCommentsToAuthor());
	                i.setCommentsToEditor(revisedManuscriptVo.getAuthoritiesHistoryVo().getCommentsToEditor());
	                i.setCommentsToReviewer(revisedManuscriptVo.getAuthoritiesHistoryVo().getCommentsToReviewer());
	                i.setCommentsToEIC(revisedManuscriptVo.getAuthoritiesHistoryVo().getCommentsToEIC());
	                i.setCreatedAt(new Date());
	                i.setDueDate(revisedManuscriptVo.getAuthoritiesHistoryVo().getDueDate());
	                AuthoritiesHistory authoritiesHistorySaved = authoritiesHistoryRepo.save(i);

	                //Retrying the authority history saving if not saved
	                authoritiesHistoryRepo.save(i);


	                log.info("Status " + revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() + " Successfully saved in authoritiesHistory table for manuscript : " + revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());

	                if (revisedManuscriptVo.getAuthoritiesHistoryVo().getDueDate() != null) {
	                    updateManuscript.setDueDate(revisedManuscriptVo.getAuthoritiesHistoryVo().getDueDate());
	                    manuScriptRepo.saveAndFlush(updateManuscript);
	                }
	            });

	            return "Successfully saved";
	        }
	        return "Please current user id";
	    }

}
