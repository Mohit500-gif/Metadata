package com.digi.uniprr.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.VO.RevisedManuscriptVo;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptTypeDetails;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.EditorRoleAccessRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.ManuscriptTypeDetailsRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.ArticleSyncService;
import com.digi.uniprr.service.CommentaryService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.JournalService;
import com.digi.uniprr.service.LetterToEditorService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ReviewerSuggestedService;
import com.digi.uniprr.service.RevisedManuscriptService;
import com.digi.uniprr.service.TatEmailConfigurationService;
import com.digi.uniprr.service.UserJournalService;
import com.digi.uniprr.service.UserRoleService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RevisedManuscriptController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    JournalService journalService;
    
    @Autowired
    ReviewerSuggestedService reviewerSuggestedService;
    
    @Autowired
    CommentaryService commentaryService;
    
    @Autowired
    LetterToEditorService letterToEditorService;
    
    @Autowired
    ArticleSyncService articleSyncService;
    
    @Autowired
    ManuscriptEditorRepo manuscriptEditorRepo;
	

	@Autowired
	CountriesService countriesService;

	@Autowired
	UserRoleService userRoleService;

	@Autowired
	UserJournalService userJournalService;

	@Autowired
	UserRoleRepo userRoleRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	JournalsRepo journalRepo;

	@Autowired
	UserLoginRepo userRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	EditorRoleAccessRepo editorRoleAccessRepo;

	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;
	
	@Autowired
	ManuScriptService manuscriptService;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;
	
    @Value("${copyRightUrlForCoAuthor}")
    private String copyRightUrlForCoAuthor;
    
    @Autowired
    RevisedManuscriptService revisedManuscriptService;
    
    @Autowired
    TatEmailConfigurationService tatEmailConfigurationService;
    
	@Autowired
	private ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;
	
	JSONObject obj = new JSONObject();
	
	
	@RequestMapping(value = "/revisedmanuscript", method = RequestMethod.POST)
	public ResponseEntity<?> revisedManuscript(@RequestBody RevisedManuscriptVo revisedManuscriptVo){
		
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
        	submitManuscript(revisedManuscriptVo, obj1);
            jsonObjects.add(obj1);
            create(revisedManuscriptVo, obj2);
            jsonObjects.add(obj2);
            processAuthoritiesHistory(revisedManuscriptVo, obj3);
            jsonObjects.add(obj3);
            return new ResponseEntity<>(jsonObjects, HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            obj.put("message", "Internal Server Error");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@RequestMapping(value = "/submitTechnicalModification", method = RequestMethod.POST)
	public ResponseEntity<?> technicalModification(@RequestBody RevisedManuscriptVo revisedManuscriptVo){
		
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
        	submitManuscript(revisedManuscriptVo, obj1);
            jsonObjects.add(obj1);
            create(revisedManuscriptVo, obj2);
            jsonObjects.add(obj2);
            processAuthoritiesHistory(revisedManuscriptVo, obj3);
            jsonObjects.add(obj3);
            return new ResponseEntity<>(jsonObjects, HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            obj.put("message", "Internal Server Error");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	    public void submitManuscript(RevisedManuscriptVo vo,JSONObject obj) {
	        log.info("Inside AuthorRestController submitManuscript");

	        vo.getManuScriptVO().setCopyRightUrlForCoAuthor(copyRightUrlForCoAuthor);

	        long startTime = Instant.now().toEpochMilli();
	        log.info("Request Time :" + Instant.now());


	        //Changes for UN-17 starts
	        JournalVO journalVO = journalService.getJournalById(vo.getManuScriptVO().getJournalId());

	        Integer count = reviewerSuggestedService.getReviewerSuggestionCount(vo.getManuScriptVO().getId(), 1,
	        		vo.getManuScriptVO().getJournalId()).intValue();

	        if (StringUtils.equalsIgnoreCase("Y", journalVO.getReviewerSuggestion()) && journalVO.getReviewerCount() > count) {
	            obj.put("count", journalVO.getReviewerCount());
	        }


	        // changes for UN-17 ends

	        try {

	            JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuscriptService.getManuscriptChargeByManuscriptId(vo.getManuScriptVO().getId(), vo.getManuScriptVO().getJournalId());
	            //added code to link commentary article to the main article.
	            if (StringUtils.equalsIgnoreCase(vo.getManuScriptVO().getManuscriptAbstract(), "Commentary")) {
	                commentaryService.linkCommentaryToManuscript(vo.getManuScriptVO().getParentManuscriptId(), vo.getManuScriptVO().getId(), vo.getManuScriptVO().getJournalId());
	            }
	            if (StringUtils.equalsIgnoreCase(vo.getManuScriptVO().getManuscriptAbstract(), "Authors' Response")) {
	                letterToEditorService.updateManuscriptToLTEArticle(vo.getManuScriptVO().getParentManuscriptId(), vo.getManuScriptVO().getId(), vo.getManuScriptVO().getJournalId());
	            }

	            String manuscriptCode = manuscriptService.submitManuscript(vo.getManuScriptVO(), journalManuscriptTypeCharge);
	            obj.put("manuscriptCode", manuscriptCode);

	            if (StringUtils.equalsIgnoreCase(vo.getManuScriptVO().getManuscriptAbstract(), "Commentary")
	                    || StringUtils.equalsIgnoreCase(vo.getManuScriptVO().getManuscriptAbstract(), "Authors' Response")) {
	                // Sync commentary and LTE linking information to Unitouch
	                ManuScript childMansucript = manuscriptService.findManuscriptById(vo.getManuScriptVO().getId());
	                ManuScript mainMansucript = manuscriptService.findManuscriptById(vo.getManuScriptVO().getParentManuscriptId());
	                articleSyncService
	                        .syncArticleDetails(mainMansucript.getManuscriptCode(), childMansucript.getManuscriptCode(), vo.getManuScriptVO().getManuscriptAbstract());
	            }

	            int userCountryStatus = Integer.parseInt(manuscriptService.checkDevelopingCountryUser(vo.getManuScriptVO().getAuthorId()));
	            obj.put("coauthor", manuscriptService.sendEmailToCoAuthorOnManuscript(vo.getManuScriptVO()));
	            String submissionCharge = "";
	            String paymentCountryRequired = "";
	            if (journalManuscriptTypeCharge != null) {
	                submissionCharge = journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null ? journalManuscriptTypeCharge.getCountryChargeOnSubmission() : "";
	            }
	            // Email Trigger
	            ManuScript manuScript = manuScriptRepo.getById(vo.getManuScriptVO().getId());

	            log.info("Inside AuthorRestController submitManuscript For Email Trigger");
	            if (vo.getManuScriptVO().getStatusCode().equals(2) || vo.getManuScriptVO().getStatusCode().equals(64) && vo.getManuScriptVO().getJournalId() != null && vo.getManuScriptVO().getAuthorId() != null
	                    && vo.getManuScriptVO().getId() != null) {
	                EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
//	                if (manuScript.getManuscriptStatusId() == 64
//	                        && (submissionCharge.equalsIgnoreCase("SameForAll")
//	                        || ((journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null ? journalManuscriptTypeCharge.getCountryChargeOnSubmission() : "")
//	                        .equalsIgnoreCase("FromDevelopingCountry")
//	                        && userCountryStatus == 0))) {
	                if (manuScript.getManuscriptStatusId() == 64) {

	                        ManuScript updatePayement = manuScriptRepo.getById(vo.getManuScriptVO().getId());
	                        updatePayement.setPaymentStatus("Submission Payment is required");
	                        manuScriptRepo.saveAndFlush(updatePayement);
	                        // Sending Mail To Author when charge is required on payment
	                        emailTriggerVO.setJournalId(vo.getManuScriptVO().getJournalId());
	                        emailTriggerVO.setUserId(vo.getManuScriptVO().getAuthorId());
	                        emailTriggerVO.setAuthorUserId(vo.getManuScriptVO().getAuthorId());
	                        emailTriggerVO.setManuscriptId(vo.getManuScriptVO().getId());
	                        emailTriggerVO.setManuscriptStatus(2);
	                        emailTriggerVO.setReceiverRoleId(4);
	                        emailTriggerVO.setTaskId(45);
	                        emailTriggerVO.setRoleId(4);
	                        log.info("Inside in AuthorRestController submitManuscript For Email Trigger Data are:->"
	                                + emailTriggerVO.toString());
	                        log.info("User Receiver Id:->" + vo.getManuScriptVO().getAuthorId());
	                        log.info("User Receiver Role Id:->" + 4);
	                        log.info("User Sender Role Id:->" + 4);
	                        log.info("User Sender Id:->" + vo.getManuScriptVO().getAuthorId());
	                        log.info("Task Id:->" + 45);
	                        String message = emailTriggerService.emailTrigger(emailTriggerVO);
	                        obj.put("data", message);
	                        log.info("Message of AuthorRestController submitManuscript For Email Trigger " + message);
	                        log.info("End of AuthorRestController submitManuscript For Email Trigger ");
	                }else {
	                    // Sending Mail To Author when there is no charge on submission
	                    emailTriggerVO.setJournalId(vo.getManuScriptVO().getJournalId());
	                    emailTriggerVO.setUserId(vo.getManuScriptVO().getAuthorId());
	                    emailTriggerVO.setAuthorUserId(vo.getManuScriptVO().getAuthorId());
	                    emailTriggerVO.setManuscriptId(vo.getManuScriptVO().getId());
	                    emailTriggerVO.setManuscriptStatus(2);
	                    emailTriggerVO.setReceiverRoleId(4);
	                    emailTriggerVO.setTaskId(2);
	                    emailTriggerVO.setRoleId(4);
	                    log.info("Inside in AuthorRestController submitManuscript For Email Trigger Data are:->"
	                            + emailTriggerVO.toString());
	                    log.info("User Receiver Id:->" + vo.getManuScriptVO().getAuthorId());
	                    log.info("User Receiver Role Id:->" + 4);
	                    log.info("User Sender Role Id:->" + 4);
	                    log.info("User Sender Id:->" + vo.getManuScriptVO().getAuthorId());
	                    log.info("Task Id:->" + 2);

	                    String message = emailTriggerService.emailTrigger(emailTriggerVO);
	                    obj.put("data", message);
	                    log.info("Message of AuthorRestController submitManuscript For Email Trigger " + message);
	                    log.info("End of AuthorRestController submitManuscript For Email Trigger ");
	                }

	                // Sending Email to Assign Editor And Managing Editor On that Journal
	                //	List<UserJournal> getAllUserJournal = userJournalRepo.getAllUserJournalList(vo.getJournalId());
	                ManuScript i = manuScriptRepo.getOne(vo.getManuScriptVO().getId());
	                if (i != null && !vo.getManuScriptVO().getStatusCode().equals(64)) {
	                    log.info("Inside in AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal");
	                    List<UserJournal> j = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 2);
	                    if (j != null && !j.isEmpty()) {
	                        j.stream().forEach(p -> {
	                            EmailTriggerVO emailTrigger = new EmailTriggerVO();
	                            emailTrigger.setJournalId(vo.getManuScriptVO().getJournalId());
	                            emailTrigger.setUserId(p.getUserId());
	                            emailTrigger.setManagingEditorId(p.getUserId());
	                            emailTrigger.setManuscriptId(vo.getManuScriptVO().getId());
	                            emailTrigger.setManuscriptStatus(2);
	                            log.info("User Receiver Role Id:->" + 2);
	                            emailTrigger.setReceiverRoleId(2);
	                            emailTrigger.setTaskId(13);
	                            emailTrigger.setRoleId(4);
	                            log.info(
	                                    "Inside in AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal Data are:->" + emailTriggerVO.toString());
	                            log.info("User Receiver Id:->" + p.getUserId());
	                            log.info("Task Id:->" + 13);
	                            String messageS = emailTriggerService.emailTrigger(emailTrigger);
	                            obj.put("data", messageS);
	                            log.info(
	                                    "Message of AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal "
	                                            + messageS);
	                            log.info(
	                                    "End of AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal ");
	                        });
	                    }
	                }

	            } else if (vo.getManuScriptVO().getStatusCode().equals(26) && vo.getManuScriptVO().getJournalId() != null && vo.getManuScriptVO().getAuthorId() != null
	                    && vo.getManuScriptVO().getId() != null) {
	                try {
	                    manuscriptEditorRepo.updateStatusInEditor(vo.getManuScriptVO().getStatusCode(), vo.getManuScriptVO().getId(), vo.getManuScriptVO().getManuscriptVersion());

	                } catch (Exception e) {
	                    log.info("Update status code :26 for manuscript editor table " + e);
	                }


	                log.info("Inside AuthorRestController submitManuscript Sending Mail To Author For Re submission ");
	                // Sending Mail To Author For Re submission
	                EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
	                emailTriggerVO.setJournalId(vo.getManuScriptVO().getJournalId());
	                emailTriggerVO.setUserId(vo.getManuScriptVO().getAuthorId());
	                emailTriggerVO.setAuthorUserId(vo.getManuScriptVO().getAuthorId());
	                emailTriggerVO.setManuscriptId(vo.getManuScriptVO().getId());
	                emailTriggerVO.setManuscriptStatus(26);
	                emailTriggerVO.setReceiverRoleId(4);
	                emailTriggerVO.setTaskId(9);
	                emailTriggerVO.setRoleId(4);
	                log.info(
	                        "Inside in AuthorRestController submitManuscript Sending Mail To Author For Re submission Data are:->"
	                                + emailTriggerVO.toString());
	                log.info("User Receiver Id:->" + vo.getManuScriptVO().getAuthorId());
	                log.info("User Receiver Role Id:->" + 4);
	                log.info("User Sender Role Id:->" + 4);
	                log.info("User Sender Id:->" + vo.getManuScriptVO().getAuthorId());
	                log.info("Task Id:->" + 9);
	                String message = emailTriggerService.emailTrigger(emailTriggerVO);
	                obj.put("data", message);
	                log.info(
	                        "Message of AuthorRestController submitManuscript Sending Mail To Author For Re submission Email Trigger "
	                                + message);
	                log.info(
	                        "End of AuthorRestController submitManuscript Sending Mail To Author For Re submission Email Trigger ");

	                // Sending Email to Assign Editor And Managing Editor On that Journal
	                //	List<UserJournal> getAllUserJournal = userJournalRepo.getAllUserJournalList(vo.getJournalId());
	                ManuScript i = manuScriptRepo.getOne(vo.getManuScriptVO().getId());
	                if (i != null) {
	                    log.info(
	                            "Inside AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission ");
	                    //	getAllUserJournal.stream().forEach(i -> {
	                    //	i.getUser().getRole().stream().forEach(j -> {
	                    //	if (j.getRoleId().equals(2)) {
	                    EmailTriggerVO emailTrigger = new EmailTriggerVO();
	                    emailTrigger.setJournalId(vo.getManuScriptVO().getJournalId());
	                    emailTrigger.setUserId(i.getAssignedUser());
	                    emailTrigger.setManagingEditorId(i.getAssignedUser());
	                    emailTrigger.setManuscriptId(vo.getManuScriptVO().getId());
	                    emailTrigger.setManuscriptStatus(26);
	                    //if (j.getRoleId().equals(2)) {
	                    log.info("User Receiver Role Id:->" + vo.getManuScriptVO().getAssignedUserRole());
//										emailTrigger.setReceiverRoleId(2);
	                    emailTrigger.setReceiverRoleId(vo.getManuScriptVO().getAssignedUserRole());
	                    //}
									/*else {
										log.info("User Receiver Role Id:->" + 3);
										emailTrigger.setReceiverRoleId(3);
									}*/
	                    emailTrigger.setTaskId(19);
	                    emailTrigger.setRoleId(4);
	                    log.info(
	                            "Inside in AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission Data are:->"
	                                    + emailTriggerVO.toString());
	                    log.info("User Receiver Id:->" + i.getAssignedUser());
	                    log.info("Task Id:->" + 13);
	                    String messageS = emailTriggerService.emailTrigger(emailTrigger);
	                    obj.put("data", messageS);
	                    log.info(
	                            "Message of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission Email Trigger "
	                                    + message);
	                    log.info(
	                            "End of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission Email Trigger ");
	                }
	                //});
	                //});
	                //}

	            }
	            if (vo.getManuScriptVO().getStatusCode().equals(50) && vo.getManuScriptVO().getJournalId() != null && vo.getManuScriptVO().getAuthorId() != null
	                    && vo.getManuScriptVO().getId() != null) {

	                try {
	                    manuscriptEditorRepo.updateStatusInEditor(vo.getManuScriptVO().getStatusCode(), vo.getManuScriptVO().getId(), vo.getManuScriptVO().getManuscriptVersion());

	                } catch (Exception e) {
	                    log.info("Update status code :50 for manuscript editor table " + e);
	                }

	                log.info(
	                        "Inside AuthorRestController submitManuscript Sending Mail To Author For Technical Modification ");
	                // Sending Mail To Author For Technical Modification
	                EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
	                emailTriggerVO.setJournalId(vo.getManuScriptVO().getJournalId());
	                emailTriggerVO.setUserId(vo.getManuScriptVO().getAuthorId());
	                emailTriggerVO.setAuthorUserId(vo.getManuScriptVO().getAuthorId());
	                emailTriggerVO.setManuscriptId(vo.getManuScriptVO().getId());
	                emailTriggerVO.setManuscriptStatus(50);
	                emailTriggerVO.setReceiverRoleId(4);
	                emailTriggerVO.setTaskId(14);
	                emailTriggerVO.setRoleId(4);
	                log.info(
	                        "Inside in AuthorRestController submitManuscript Sending Mail To Author For Technical Modification Data are:->"
	                                + emailTriggerVO.toString());
	                log.info("User Receiver Id:->" + vo.getManuScriptVO().getAuthorId());
	                log.info("User Receiver Role Id:->" + 4);
	                log.info("User Sender Role Id:->" + 4);
	                log.info("User Sender Id:->" + vo.getManuScriptVO().getAuthorId());
	                log.info("Task Id:->" + 9);
	                String message = emailTriggerService.emailTrigger(emailTriggerVO);
	                obj.put("data", message);
	                log.info(
	                        "Message of AuthorRestController submitManuscript Sending Mail To Author For Technical Modification Email Trigger "
	                                + message);
	                log.info(
	                        "End of AuthorRestController submitManuscript Sending Mail To Author For Technical Modification Email Trigger ");

	                // Sending Email to Assign Editor And Managing Editor On that Journal
	                //List<UserJournal> getAllUserJournal = userJournalRepo.getAllUserJournalList(vo.getJournalId());
	                ManuScript i = manuScriptRepo.getOne(vo.getManuScriptVO().getId());
	                if (i != null) {
	                    log.info(
	                            "Inside AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification ");
	                    //	getAllUserJournal.stream().forEach(i -> {
	                    //	i.getUser().getRole().stream().forEach(j -> {
	                    //	if (j.getRoleId().equals(2)) {
	                    EmailTriggerVO emailTrigger = new EmailTriggerVO();
	                    emailTrigger.setJournalId(vo.getManuScriptVO().getJournalId());
	                    emailTrigger.setUserId(i.getAssignedUser());
	                    emailTrigger.setManagingEditorId(i.getAssignedUser());
	                    emailTrigger.setManuscriptId(vo.getManuScriptVO().getId());
	                    emailTrigger.setManuscriptStatus(50);
	                    //	if (j.getRoleId().equals(2)) {
	                    log.info("User Receiver Role Id:->" + 2);
	                    emailTrigger.setReceiverRoleId(2);
									/*} else {
										log.info("User Receiver Role Id:->" + 3);
										emailTrigger.setReceiverRoleId(3);
									}*/
	                    emailTrigger.setTaskId(20);
	                    emailTrigger.setRoleId(4);
	                    log.info(
	                            "Inside in AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification Data are:->"
	                                    + emailTriggerVO.toString());
	                    log.info("User Receiver Id:->" + i.getAssignedUser());
	                    log.info("Task Id:->" + 13);
	                    String messageS = emailTriggerService.emailTrigger(emailTrigger);
	                    obj.put("data", messageS);
	                    log.info(
	                            "Message of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification Email Trigger "
	                                    + message);
	                    log.info(
	                            "End of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification Email Trigger ");
	                    //	}
	                    ///	});
	                    //	});
	                }

	            }

	            obj.put("message", "Data Updated Successfully");
	        } catch (Exception e) {
	            e.printStackTrace();
	            log.info("Error in AuthorRestController submitManuscript" + e);
	            log.info("Error Time :" + (Instant.now().toEpochMilli() - startTime));
	            obj.put("message", "Submission Date Updation Failure->");
	        }
	        log.info("End of AuthorRestController submitManuscript");
	        log.info("Response Time :" + (Instant.now().toEpochMilli() - startTime));
	    }

		private void processAuthoritiesHistory(RevisedManuscriptVo revisedManuscriptVo, JSONObject obj) {
			try {

				if (revisedManuscriptVo.getAuthoritiesHistoryVo().getPreviousUserRole() != null
						&& revisedManuscriptVo.getAuthoritiesHistoryVo().getPreviousUserRole().equals(4)
						&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(2)) {

					JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuscriptService
							.getManuscriptChargeByManuscriptId(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId(),
									revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
					ManuScript i = manuScriptRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());
					int userCountryStatus = Integer.parseInt(manuScriptRepo.getDevelopingCountryStatus(i.getAuthorId()));

					// Checking payment required for article
					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(2) && journalManuscriptTypeCharge != null) {
						if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
								? journalManuscriptTypeCharge.getIsChargesRequired()
								: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getChargeOnSubmission()
										: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
										? journalManuscriptTypeCharge.getChargeOnPublication()
										: "").equalsIgnoreCase("No")
								&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
										: "").equalsIgnoreCase("SameForAll"))
							revisedManuscriptVo.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
						if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
								? journalManuscriptTypeCharge.getIsChargesRequired()
								: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getChargeOnSubmission()
										: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
										? journalManuscriptTypeCharge.getChargeOnPublication()
										: "").equalsIgnoreCase("No")
								&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
										: "").equalsIgnoreCase("FromDevelopingCountry")) {
							if (userCountryStatus > 0)
								revisedManuscriptVo.getAuthoritiesHistoryVo().setManuscriptStatusId(2);
							else
								revisedManuscriptVo.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
						}

						if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
								? journalManuscriptTypeCharge.getIsChargesRequired()
								: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getChargeOnSubmission()
										: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
										? journalManuscriptTypeCharge.getChargeOnPublication()
										: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
										: "").equalsIgnoreCase("SameForAll"))
							revisedManuscriptVo.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
						if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
								? journalManuscriptTypeCharge.getIsChargesRequired()
								: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getChargeOnSubmission()
										: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
										? journalManuscriptTypeCharge.getChargeOnPublication()
										: "").equalsIgnoreCase("Yes")
								&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
										? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
										: "").equalsIgnoreCase("FromDevelopingCountry")) {
							if (userCountryStatus > 0)
								revisedManuscriptVo.getAuthoritiesHistoryVo().setManuscriptStatusId(2);
							else
								revisedManuscriptVo.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
						}
					}

					List<Integer> add = new ArrayList<>();
					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(64)) {
						List<UserJournal> getAdminList = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 1);
						add.add(getAdminList.get(0).getUserId());
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserId(add);
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserRole(1);

					} else {
						add.add(i.getAssignedUser());
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserId(add);
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
					}
					if (i != null) {
						// Due date for eic
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {

											if (l.getTaskId().equals(13)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
						String result = revisedManuscriptService.authoritiesHistory(revisedManuscriptVo);
						obj.put("data", result);
					}
				} else {
					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(46)) {
						// Requiring techincal modification
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(11)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}	
								}
							}
						}
					}
					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(50)) {
						// Author submit as techincal modification
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						ManuScript i = manuScriptRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());
						List<Integer> add = new ArrayList<>();
						add.add(i.getAssignedUser());
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserId(add);
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(20)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}
					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(51)
									|| revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(52)
									|| revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(68))) {
						// Author submit as techincal modification
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						ManuScript manu = manuScriptRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserId(Arrays.asList(manu.getAssignedUser()));
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(8)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}

					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(4)) {
						// Requiring re-submssion
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(10)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}
					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(26)) {
						// Author submit as re-submssion
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						ManuScript i = manuScriptRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptId());
						List<Integer> add = new ArrayList<>();
						add.add(i.getAssignedUser());
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserId(add);
						revisedManuscriptVo.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(19)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}

					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(3)) {
						// Eic assign editor
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(3)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}

					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(12)
									|| revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(13)
									|| revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(14)
									|| revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(15)
									|| revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(36)
									|| revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(60))) {
						// Eic/editor final descision after reviewed
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(8)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}

					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(35)) {
						// Eic/editor final descision after reviewed
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(7)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}

					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(74)) {
						// Assign editor after technical check
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											if (l.getTaskId().equals(3)) {
												revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
														: new Date().from(LocalDate.now().plusDays(l.getTat())
																.atStartOfDay(ZoneId.systemDefault()).toInstant()));
											}
										}
									}
								}
							}
						}
					}

					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&& revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(72)) {
						// Assign editor after technical check
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowWithout(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
					
					if (revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
							&&  revisedManuscriptVo.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(73)) {
						// Assign editor after technical check
						Journal getjournalDetails = journalRepo.getOne(revisedManuscriptVo.getAuthoritiesHistoryVo().getJournalId());
						if (getjournalDetails != null) {
							if (getjournalDetails.getJournalMetadata() != null) {
								if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
									List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowAfter(
											getjournalDetails.getJournalMetadata().getWorkflowId(),
											getjournalDetails.getJournalId());
									if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
										for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
											revisedManuscriptVo.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}

					String result = revisedManuscriptService.authoritiesHistory(revisedManuscriptVo);
					obj.put("data", result);
				}

			} catch (Exception e) {
				obj.put("data", "Something went wrong");
			}	
		}
		
		private void create(RevisedManuscriptVo revisedManuscriptVo,JSONObject obj) {
			try {
				if (revisedManuscriptVo.getEmailTriggerVO().getRoleId() != null && revisedManuscriptVo.getEmailTriggerVO().getRoleId().equals(2)
						&& !revisedManuscriptVo.getEmailTriggerVO().getTaskId().equals(4)) {
					ManuScript getManuScript = manuScriptRepo.getOne(revisedManuscriptVo.getEmailTriggerVO().getManuscriptId());
					if (getManuScript != null) {
						revisedManuscriptVo.getEmailTriggerVO().setReceiverRoleId(2);
						revisedManuscriptVo.getEmailTriggerVO().setUserId(getManuScript.getAssignedUser());
						tatEmailConfigurationService.create(revisedManuscriptVo.getEmailTriggerVO());
					}
				}
				// For Reviewer
				else if (revisedManuscriptVo.getEmailTriggerVO().getTaskId() != null && revisedManuscriptVo.getEmailTriggerVO().getTaskId().equals(4)) {
					if (revisedManuscriptVo.getEmailTriggerVO().getUserid() != null && !revisedManuscriptVo.getEmailTriggerVO().getUserid().isEmpty()) {
						revisedManuscriptVo.getEmailTriggerVO().getUserid().stream().forEach(i -> {
							revisedManuscriptVo.getEmailTriggerVO().setUserId(i);
							tatEmailConfigurationService.create(revisedManuscriptVo.getEmailTriggerVO());
						});
					}
				} else {
					tatEmailConfigurationService.create(revisedManuscriptVo.getEmailTriggerVO());
				}
				log.info("Fetching manuscript type details - manuscriptId: {}, manuscriptVersion: {}",
					    revisedManuscriptVo.getManuScriptVO().getId(),
					    revisedManuscriptVo.getManuScriptVO().getManuscriptVersion());
				ManuscriptTypeDetails details = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(revisedManuscriptVo.getManuScriptVO().getId());
				if (details != null && revisedManuscriptVo.getManuScriptVO().getManuscriptVersion() != null) {
					details.setManuscriptVersion(revisedManuscriptVo.getManuScriptVO().getManuscriptVersion());
				}
				manuscriptTypeDetailsRepo.save(details);
				obj.put("data", "Added Data Sucessfully");
			} catch (Exception e) {
				obj.put("data", "Something went wrong");
			}

		}
}
