package com.digi.uniprr.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.AssignEditorVO;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.EditorRoleAccessRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.AssignEditerService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.UserJournalService;
import com.digi.uniprr.service.UserRoleService;
import com.digi.uniprr.utils.AppConstants;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AssignEditorController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AssignEditerService assignEditerService;

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
	
	JSONObject obj = new JSONObject();
	
	
    @RequestMapping(value = "/assigneditor", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> assignUser(
            @RequestPart("assignEditorVO") AssignEditorVO assignEditorVO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
		
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
        	assignUserAndSendEmailTriggers(assignEditorVO, file, obj1, request);
            jsonObjects.add(obj1);
            processData(assignEditorVO, obj2);
            jsonObjects.add(obj2);
            processAuthoritiesHistory(assignEditorVO, obj3);
            jsonObjects.add(obj3);
            return new ResponseEntity<>(jsonObjects, HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            obj.put("message", "Internal Server Error");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	private void assignUserAndSendEmailTriggers(AssignEditorVO assignEditorVO,MultipartFile file, JSONObject obj,HttpServletRequest request) {

		try {
			ManuScript getManuScript = manuScriptRepo.getById(assignEditorVO.getManuscriptListVO().getManuScriptId());
			Integer assignedUserRole = getManuScript.getAssignedUserRole();
			Integer assigneduser = getManuScript.getAssignedUser();
			assignEditerService.assignUser(assignEditorVO,file,request);
			obj.put("data", "Assigned successfully");

			// Email Trigger to Eic/Editor While Assigning Reviewer
			if (assignEditorVO.getManuscriptListVO().getManuStatusId() != null && assignEditorVO.getManuscriptListVO().getManuStatusId().equals(8)) {
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
				emailTriggerVO.setUserId(assignEditorVO.getManuscriptListVO().getUserId());
				if (assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
					emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
				else
					emailTriggerVO.setEditorUserId(assignEditorVO.getManuscriptListVO().getUserId());
				emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
				emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
				emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
				emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
				emailTriggerVO.setTaskId(31);
				emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
				log.info("Inside in Email Trigger to Eic/Editor While Assigning Reviewer For Email Trigger Data are:->"
						+ emailTriggerVO.toString());
				log.info("User Receiver Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
				log.info("User Receiver Role Id:->" + assignEditorVO.getManuscriptListVO().getEditorRoleId());
				log.info("User Sender Role Id:->" + assignEditorVO.getManuscriptListVO().getEditorRoleId());
				log.info("User Sender Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
				log.info("Task Id:->" + 31);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				obj.put("data", "Assigned successfully");
				log.info(
						"Message of Email Trigger to Eic/Editor While Assigning Reviewer For Email Trigger " + message);
				log.info("End of assignUser Email Trigger to Eic/Editor While Assigning Reviewer For Email Trigger ");
			}
			
			// Email Trigger to Eic/Editor While Assigning Reviewer
			if(assignEditorVO.getManuscriptListVO().getManuStatusId() != null && assignEditorVO.getManuscriptListVO().getManuStatusId().equals(66))
			{
				EmailTriggerVO emailTriggerVO1 = new EmailTriggerVO();
				emailTriggerVO1.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
				emailTriggerVO1.setUserId(assignEditorVO.getManuscriptListVO().getUserId());
				emailTriggerVO1.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
				emailTriggerVO1.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
				emailTriggerVO1.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
				emailTriggerVO1.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
				emailTriggerVO1.setTaskId(69);
				emailTriggerVO1.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
				log.info("User Receiver Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
				log.info("User Receiver Role Id:->" + 2);
				log.info("User Sender Role Id:->" + 2);
				log.info("User Sender Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
				log.info("Task Id:->" + 30);
				String message1 = emailTriggerService.emailTrigger(emailTriggerVO1);
				log.info("Message of assignUser Role For Editor/EIC For Email Trigger " +message1);


			}

			// Email Trigger
			if (assignEditorVO.getManuscriptListVO().getRoleId() != null && assignEditorVO.getManuscriptListVO().getRoleId().equals(3)) {
				if (assignEditorVO.getManuscriptListVO().getUserId() != null) {
					// Mail will trigger to Default Editor-in-Chief when manuscript is sent for
					// Assign Editor action
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
					emailTriggerVO.setUserId(assignEditorVO.getManuscriptListVO().getUserId());
					emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
					emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
					emailTriggerVO.setCommentToEditor(assignEditorVO.getManuscriptListVO().getMessage());
					emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
					emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
					emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
					emailTriggerVO.setTaskId(30);
					log.info(
							"Inside in assignUser Role For Editor Mail will trigger to Default Editor-in-Chief when manuscript is sent for Assign Editor action For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("Task Id:->" + 30);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					obj.put("data", "Assigned successfully");
					log.info(
							"End of assignUser Role Mail will trigger to Default Editor when manuscript is sent for Assign Editor action For Editor For Email Trigger ");
				}

				log.info("Inside in assignUser Role For Editor For Email Trigger");
				if (assignEditorVO.getManuscriptListVO().getManuScriptId() != null && assignEditorVO.getManuscriptListVO().getUserId() != null
						&& assignEditorVO.getManuscriptListVO().getJournalId() != null && assignEditorVO.getManuscriptListVO().getAssignToID() != null
						&& !assignEditorVO.getManuscriptListVO().getAssignToID().isEmpty()) {
					assignEditorVO.getManuscriptListVO().getAssignToID().stream().forEach(i -> {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
						if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody() != null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody().isEmpty())
							 emailTriggerVO.setEmailTemplateBody(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody());
						if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject()!= null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject().isEmpty())
							emailTriggerVO.setEmailTemplateSubject(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject());
						
						emailTriggerVO.setUserId(i);
						emailTriggerVO.setEditorUserId(i);
						emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
						emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
						emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getRoleId());
						emailTriggerVO.setCommentToEditor(assignEditorVO.getManuscriptListVO().getMessage());
						emailTriggerVO.setTaskId(3);
						emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
						if(assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2)) {
							emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
							}
						log.info("Inside in assignUser Role For Editor For Email Trigger Data are:->"
								+ emailTriggerVO.toString());
						log.info("Task Id:->" + 3);
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						obj.put("data", message);
						obj.put("data", "Assigned successfully");
						log.info("End of assignUser Role For Editor For Email Trigger ");
					});

				}
			}
			
			if ((((assignEditorVO.getManuscriptListVO().getEditorRoleId() != null && assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2)) || (assignEditorVO.getManuscriptListVO().getRoleId() != null && assignEditorVO.getManuscriptListVO().getRoleId().equals(2))) && assignedUserRole == 3) 
					|| ((assignEditorVO.getManuscriptListVO().getEditorRoleId() != null && assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(3)) || (assignEditorVO.getManuscriptListVO().getRoleId() != null && assignEditorVO.getManuscriptListVO().getRoleId().equals(3))) && assignedUserRole == 3 && !assignEditorVO.getManuscriptListVO().getUserId().equals(assigneduser)) {
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
				emailTriggerVO.setUserId(assigneduser);
				emailTriggerVO.setAuthorUserId(getManuScript.getAuthorId());
				emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
				emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
				emailTriggerVO.setReceiverRoleId(assignedUserRole);
				emailTriggerVO.setTaskId(86);
				emailTriggerVO.setRoleId(assignedUserRole);
				emailTriggerVO.setEditorUserId(assigneduser);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				obj.put("data", "Assigned successfully");
				log.debug("Emil Trigger" + message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Some Error occured");
			obj.put("message", e.getMessage());
		}
	}
	
	private void processData(AssignEditorVO assignEditorVO, JSONObject obj) {
	    try {
	        if (assignEditorVO.getEmailTriggerVO().getRoleId() != null && assignEditorVO.getEmailTriggerVO().getRoleId().equals(2)
	                && !assignEditorVO.getEmailTriggerVO().getTaskId().equals(4)) {
	            ManuScript getManuScript = manuScriptRepo.getOne(assignEditorVO.getEmailTriggerVO().getManuscriptId());
	            if (getManuScript != null) {
	                assignEditorVO.getEmailTriggerVO().setReceiverRoleId(2);
	                assignEditorVO.getEmailTriggerVO().setUserId(getManuScript.getAssignedUser());
	                assignEditerService.create(assignEditorVO);
	            }
	        } else if (assignEditorVO.getEmailTriggerVO().getTaskId() != null && assignEditorVO.getEmailTriggerVO().getTaskId().equals(4)) {
	            if (assignEditorVO.getEmailTriggerVO().getUserid() != null && !assignEditorVO.getEmailTriggerVO().getUserid().isEmpty()) {
	                assignEditorVO.getEmailTriggerVO().getUserid().forEach(i -> {
	                    assignEditorVO.getEmailTriggerVO().setUserId(i);
	                    assignEditerService.create(assignEditorVO);
	                });
	            }
	        } else {
	            assignEditerService.create(assignEditorVO);
	        }
	        obj.put("data", "Added Data Successfully");
	    } catch (Exception e) {
	        obj.put("data", "Something went wrong");
	        log.error("An error occurred: {}", e.getMessage());
	    }
	}

	private void processAuthoritiesHistory(AssignEditorVO assignEditorVO, JSONObject obj) {
		try {

			if (assignEditorVO.getAuthoritiesHistoryVo().getPreviousUserRole() != null
					&& assignEditorVO.getAuthoritiesHistoryVo().getPreviousUserRole().equals(4)
					&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(2)) {

				JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuscriptService
						.getManuscriptChargeByManuscriptId(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId(),
								assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
				ManuScript i = manuScriptRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());
				int userCountryStatus = Integer.parseInt(manuScriptRepo.getDevelopingCountryStatus(i.getAuthorId()));

				// Checking payment required for article
				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(2) && journalManuscriptTypeCharge != null) {
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
						assignEditorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
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
							assignEditorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(2);
						else
							assignEditorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
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
						assignEditorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
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
							assignEditorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(2);
						else
							assignEditorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
					}
				}

				List<Integer> add = new ArrayList<>();
				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(64)) {
					List<UserJournal> getAdminList = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 1);
					add.add(getAdminList.get(0).getUserId());
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserRole(1);

				} else {
					add.add(i.getAssignedUser());
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
				}
				if (i != null) {
					// Due date for eic
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {

										if (l.getTaskId().equals(13)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
					String result = assignEditerService.authoritiesHistory(assignEditorVO);
					obj.put("data", result);
				}
			} else {
				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(46)) {
					// Requiring techincal modification
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(11)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}	
							}
						}
					}
				}
				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(50)) {
					// Author submit as techincal modification
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					ManuScript i = manuScriptRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());
					List<Integer> add = new ArrayList<>();
					add.add(i.getAssignedUser());
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(20)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}
				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(51)
								|| assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(52)
								|| assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(68))) {
					// Author submit as techincal modification
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					ManuScript manu = manuScriptRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserId(Arrays.asList(manu.getAssignedUser()));
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(8)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(4)) {
					// Requiring re-submssion
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(10)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}
				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(26)) {
					// Author submit as re-submssion
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					ManuScript i = manuScriptRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());
					List<Integer> add = new ArrayList<>();
					add.add(i.getAssignedUser());
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					assignEditorVO.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(19)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(3)) {
					// Eic assign editor
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(3)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(12)
								|| assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(13)
								|| assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(14)
								|| assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(15)
								|| assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(36)
								|| assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(60))) {
					// Eic/editor final descision after reviewed
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(8)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(35)) {
					// Eic/editor final descision after reviewed
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(7)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(74)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(3)) {
											assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(72)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowWithout(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
												: new Date().from(LocalDate.now().plusDays(l.getTat())
														.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									}
								}
							}
						}
					}
				}
				
				if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&&  assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(73)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowAfter(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										assignEditorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
												: new Date().from(LocalDate.now().plusDays(l.getTat())
														.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									}
								}
							}
						}
					}
				}

				String result = assignEditerService.authoritiesHistory(assignEditorVO);
				obj.put("data", result);
			}

		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}	
	}
}
