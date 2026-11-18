package com.digi.uniprr.controller;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.service.EmailTriggerService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class EmailTriggerRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@PostMapping("emailTrigger")
	public ResponseEntity<?> emailTrigger(@RequestBody EmailTriggerVO emailTriggerVO) {
		JSONObject obj = new JSONObject();
		log.info("Inside in EmailTriggerRestController ");
		try {
			// Email Trigger For Editor Decision
			if (emailTriggerVO.getEditorUserId() != null && !emailTriggerVO.getTaskId().equals(7)) {
				log.info("Inside in Email Trigger For Editor Decision ");
				log.info("Email Trigger For Editor Decision Data are:->" + emailTriggerVO.toString());
				ManuScript getManuScriptData = manuScriptRepo.getOne(emailTriggerVO.getManuscriptId());
				if (getManuScriptData != null) {
					EmailTriggerVO addEmailTriggerVO = new EmailTriggerVO();
					addEmailTriggerVO.setUserId(getManuScriptData.getAuthorId());
					log.info("User Receiver Id:->" + getManuScriptData.getAuthorId());
					addEmailTriggerVO.setJournalId(emailTriggerVO.getJournalId());
					addEmailTriggerVO.setEditorUserId(emailTriggerVO.getEditorUserId());
					emailTriggerVO.setReceiverRoleId(4);
					log.info("User Receiver Role Id:->" + 4);
					addEmailTriggerVO.setManuscriptId(emailTriggerVO.getManuscriptId());
					ManuscriptEditor getManuscriptEditorData = manuscriptEditorRepo.getManuscriptEditorDetails(
							emailTriggerVO.getManuscriptId(), emailTriggerVO.getEditorUserId(),
							emailTriggerVO.getJournalId(),emailTriggerVO.getManuscriptVersion());
					if (getManuscriptEditorData != null) {
						addEmailTriggerVO.setManuscriptStatus(getManuscriptEditorData.getStatusMaster().getId());
					}
					addEmailTriggerVO.setRoleId(3);
					log.info("User Sender Role Id:->" + 3);
					log.info("User Sender Id:->" + emailTriggerVO.getEditorUserId());
					addEmailTriggerVO.setTaskId(8);
					log.info("Task Id:->" + 8);
					String message = emailTriggerService.emailTrigger(addEmailTriggerVO);
					obj.put("data", message);
					log.info("Message of Email Trigger For Editor Decision " + message);
					log.info("End of Email Trigger For Editor Decision ");
				}
			}
			// Email Trigger For Reviewer Decision
			else if (emailTriggerVO.getEditorUserId() != null && emailTriggerVO.getTaskId().equals(7)) {
				log.info("Inside in Email Trigger For Reviewer Decision ");
				log.info("Email Trigger For Reviewer Decision Data are:->" + emailTriggerVO.toString());
				ManuScriptReviewer getManuScriptReviewer = manuScriptReviewerRepo.getManuScriptReviewerDecision(
						emailTriggerVO.getManuscriptId(), emailTriggerVO.getManuscriptVersion(),
						emailTriggerVO.getReviewerUserId(), emailTriggerVO.getJournalId());
				if (getManuScriptReviewer != null && getManuScriptReviewer.getEditorId() != null) {
					EmailTriggerVO addEmailTriggerVO = new EmailTriggerVO();
					addEmailTriggerVO.setUserId(emailTriggerVO.getEditorUserId());
					log.info("User Receiver Id:->" + getManuScriptReviewer.getEditorId());
					addEmailTriggerVO.setJournalId(emailTriggerVO.getJournalId());
					addEmailTriggerVO.setReviewerUserId(emailTriggerVO.getReviewerUserId());
					addEmailTriggerVO.setReceiverRoleId(emailTriggerVO.getReceiverRoleId());
					log.info("User Receiver Role Id:->" + 3);
					addEmailTriggerVO.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
					addEmailTriggerVO.setManuscriptId(emailTriggerVO.getManuscriptId());
					addEmailTriggerVO.setRoleId(5);
					log.info("User Sender Role Id:->" + 5);
					log.info("User Sender Id:->" + emailTriggerVO.getReviewerUserId());
					
					ManuScript getManuScript1= manuScriptRepo.getById(emailTriggerVO.getManuscriptId());
					if(getManuScript1!=null) {	
						if(getManuScript1.getManuscriptStatusId().equals(66)){
							addEmailTriggerVO.setTaskId(82);
							log.info("Task Id:->" + 82);
						}else
						{
							addEmailTriggerVO.setTaskId(7);
							log.info("Task Id:->" + 7);
						}
						
						
					}
					
//					addEmailTriggerVO.setTaskId(7);
//					log.info("Task Id:->" + 7);
					String message = emailTriggerService.emailTrigger(addEmailTriggerVO);
					obj.put("data", message);
					log.info("Message of Email Trigger For Reviewer Decision " + message);
					log.info("End of Email Trigger For Reviewer Decision ");
				}

				if (getManuScriptReviewer != null) {
					EmailTriggerVO addEmailTriggerVO = new EmailTriggerVO();
					addEmailTriggerVO.setUserId(emailTriggerVO.getReviewerUserId());
					log.info("User Receiver Id:->" + emailTriggerVO.getReviewerUserId());
					addEmailTriggerVO.setJournalId(emailTriggerVO.getJournalId());
					addEmailTriggerVO.setReviewerUserId(emailTriggerVO.getReviewerUserId());
					addEmailTriggerVO.setReceiverRoleId(5);
					log.info("User Receiver Role Id:->" + 5);
					addEmailTriggerVO.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
					addEmailTriggerVO.setManuscriptId(emailTriggerVO.getManuscriptId());
					addEmailTriggerVO.setRoleId(5);
					log.info("User Sender Role Id:->" + 5);
					log.info("User Sender Id:->" + emailTriggerVO.getReviewerUserId());
					
					ManuScript getManuScript1= manuScriptRepo.getById(emailTriggerVO.getManuscriptId());
					if(getManuScript1!=null) {	
						if(getManuScript1.getManuscriptStatusId().equals(66)){
							addEmailTriggerVO.setTaskId(81);
							log.info("Task Id:->" + 81);
						}else
						{
							addEmailTriggerVO.setTaskId(27);
							log.info("Task Id:->" + 27);
						}
						
						
					}
					
//					addEmailTriggerVO.setTaskId(27);
//					log.info("Task Id:->" + 27);
					String message = emailTriggerService.emailTrigger(addEmailTriggerVO);
					obj.put("data", message);
					log.info("Message of Email Trigger For Reviewer Acknowledgement " + message);
					log.info("End of Email Trigger For Reviewer Acknowledgement ");
				}

			} else {
				// Email Trigger For Author Save In Draft
				log.info("Inside in Email Trigger For Author Save In Draft");
				log.info("Email Trigger For Author Save In Draft Data are:->" + emailTriggerVO.toString());
				emailTriggerVO.setManuscriptStatus(1);
				emailTriggerVO.setReceiverRoleId(4);
				emailTriggerVO.setAuthorUserId(emailTriggerVO.getUserId());
				log.info("User Receiver user Id:->" + emailTriggerVO.getUserId());
				log.info("User Receiver Role id:->" + 4);
				log.info("Task Id:->" + emailTriggerVO.getTaskId());
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				log.info("Message of Email Trigger For Author Save In Draft " + message);
				log.info("End of Email Trigger For Author Save In Draft ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Error");
			log.info("Error in EmailTriggerRestController :->" + e);
			return new ResponseEntity<Object>(obj, HttpStatus.EXPECTATION_FAILED);
		}
		log.info("End of EmailTriggerRestController ");
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@PostMapping("sendContactSupportMail")
	public ResponseEntity<?> sendContactSupportMail(@RequestBody IDVO idvo) {
		JSONObject obj = new JSONObject();
		try {
			String data = emailTriggerService.sendContactSupportMail(idvo);
			obj.put("data", data);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@PostMapping("resendReviewerInvitationMail")
	public ResponseEntity<?> resendReviewerInvitationMail(@RequestBody ManuscriptListVO manuscriptListVO) {
		JSONObject obj = new JSONObject();
		try {
			String data = emailTriggerService.resendReviewerInvitationMail(manuscriptListVO);
			obj.put("data", data);
		} catch (Exception e) {
			log.info("Exception" + e);
			e.printStackTrace();
			obj.put("data", "Error");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@PostMapping("emailTriggerForReminder")
	public ResponseEntity<?> emailTriggerForReminder(@RequestBody EmailTriggerVO emailTriggerVO) {
		JSONObject obj = new JSONObject();
		log.info("Inside in EmailTriggerRestController ");
		try {
			
			
			if(emailTriggerVO.getEmailTemplateBody() !=null )
			{
				emailTriggerVO.setEmailTemplateBody("");	
			}
			
			if(emailTriggerVO.getRoleId() ==4 && emailTriggerVO.getReceiverRoleId()==2 && emailTriggerVO.getTaskId()==50)
			{
				if (emailTriggerVO.getJournalMailTemplatesBody() != null && !emailTriggerVO.getJournalMailTemplatesBody().isEmpty())
					emailTriggerVO.setEmailTemplateBody(emailTriggerVO.getJournalMailTemplatesBody());	
			}
			else
			{
				if (emailTriggerVO.getJournalMailTemplatesBody() != null && !emailTriggerVO.getJournalMailTemplatesBody().isEmpty())
					emailTriggerVO.setEmailTemplateBody(emailTriggerVO.getJournalMailTemplatesBody());
				
			}

			String message = emailTriggerService.emailTriggerForReminder(emailTriggerVO);
			
			obj.put("data", message);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Error");
			log.info("Error in EmailTriggerRestController :->" + e);
			return new ResponseEntity<Object>(obj, HttpStatus.EXPECTATION_FAILED);
		}
		log.info("End of EmailTriggerRestController ");
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

}
