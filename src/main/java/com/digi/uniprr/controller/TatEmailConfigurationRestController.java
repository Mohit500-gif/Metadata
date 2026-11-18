package com.digi.uniprr.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.TatEmailConfigurationService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/tatEmailConfiguration")
public class TatEmailConfigurationRestController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	TatEmailConfigurationService tatEmailConfigurationService;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> create(@RequestBody EmailTriggerVO emailTriggerVO) {

		JSONObject obj = new JSONObject();

		LOGGER.info("Received request for email trigger creation | manuscriptId: {} | roleId: {} | taskId: {}",
				emailTriggerVO.getManuscriptId(),
				emailTriggerVO.getRoleId(),
				emailTriggerVO.getTaskId());

		try {

			// For Editor
			if (emailTriggerVO.getRoleId() != null
					&& emailTriggerVO.getRoleId().equals(2)
					&& !emailTriggerVO.getTaskId().equals(4)) {

				LOGGER.info("Processing editor email trigger flow | manuscriptId: {}",
						emailTriggerVO.getManuscriptId());

				ManuScript getManuScript = manuScriptRepo.getOne(emailTriggerVO.getManuscriptId());

				if (getManuScript != null) {

					LOGGER.info("Manuscript found | assignedUser: {}", getManuScript.getAssignedUser());

					emailTriggerVO.setReceiverRoleId(2);
					emailTriggerVO.setUserId(getManuScript.getAssignedUser());

					LOGGER.info("Creating email trigger for editor | userId: {}", emailTriggerVO.getUserId());

					tatEmailConfigurationService.create(emailTriggerVO);

					LOGGER.info("Email trigger created successfully for editor | userId: {}",
							emailTriggerVO.getUserId());

				} else {

					LOGGER.warn("No manuscript found for manuscriptId: {}",
							emailTriggerVO.getManuscriptId());
				}
			}

			// For Reviewer
			else if (emailTriggerVO.getTaskId() != null
					&& emailTriggerVO.getTaskId().equals(4)) {

				LOGGER.info("Processing reviewer email trigger flow");

				if (emailTriggerVO.getUserid() != null
						&& !emailTriggerVO.getUserid().isEmpty()) {

					LOGGER.info("Total reviewers to process: {}",
							emailTriggerVO.getUserid().size());

					emailTriggerVO.getUserid().stream().forEach(i -> {

						try {

							LOGGER.info("Creating reviewer email trigger | reviewerUserId: {}", i);

							emailTriggerVO.setUserId(i);

							tatEmailConfigurationService.create(emailTriggerVO);

							LOGGER.info("Reviewer email trigger created successfully | reviewerUserId: {}", i);

						} catch (Exception ex) {

							LOGGER.error("Failed to create reviewer email trigger | reviewerUserId: {}",
									i, ex);
						}
					});

				} else {

					LOGGER.warn("Reviewer flow triggered but reviewer user list is empty");
				}
			}

			// Default Flow
			else {

				LOGGER.info("Processing default email trigger flow");

				tatEmailConfigurationService.create(emailTriggerVO);

				LOGGER.info("Default email trigger created successfully");
			}

			obj.put("data", "Added Data Sucessfully");

			LOGGER.info("Email trigger request completed successfully");

			return new ResponseEntity<>(obj, HttpStatus.OK);

		} catch (Exception e) {

			LOGGER.error("Exception occurred while creating email trigger", e);

			obj.put("data", "Something went wrong");

			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/extendTat", method = RequestMethod.POST)
	public ResponseEntity<?> extendTat(@RequestBody List<EmailTriggerVO> emailTriggerVO) {
		JSONObject obj = new JSONObject();
		try {
			String result = tatEmailConfigurationService.extendTat(emailTriggerVO);
			obj.put("data", result);
			if (emailTriggerVO != null && !emailTriggerVO.isEmpty()) {
				emailTriggerVO.stream().forEach(i -> {
					i.setTaskId(23);
					i.setReviewerUserId(i.getUserId());
					i.setManagingEditorId(i.getManagingEditorId());
					i.setEditorUserId(i.getEditorUserId());
					i.setReceiverRoleId(i.getReceiverRoleId());
					if (i.getJournalMailTemplatesBody() != null && !i.getJournalMailTemplatesBody().isEmpty())
						i.setEmailTemplateBody(i.getJournalMailTemplatesBody());
					String message = emailTriggerService.emailTrigger(i);
					obj.put("Email trigger message", message);
				});
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong :" + e);
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

	}

	@RequestMapping(value = "/extendDuedateArticleStage", method = RequestMethod.POST)
	public ResponseEntity<?> extendDuedateArticleStage(@RequestBody EmailTriggerVO emailTriggerVO) {
		JSONObject obj = new JSONObject();
		try {
			String result = tatEmailConfigurationService.extendDueDateArticleStage(emailTriggerVO);
			obj.put("data", result);
			if (emailTriggerVO != null && result.equalsIgnoreCase("has been extended")) {
				String message = "";
				if(emailTriggerVO.getReceiverRoleId() == 4) {
				emailTriggerVO.setTaskId(emailTriggerVO.getTaskId());
				emailTriggerVO.setReviewerUserId(emailTriggerVO.getUserId());
				emailTriggerVO.setManagingEditorId(emailTriggerVO.getManagingEditorId());
				emailTriggerVO.setEditorUserId(emailTriggerVO.getEditorUserId());
				emailTriggerVO.setReceiverRoleId(emailTriggerVO.getReceiverRoleId());
				if (emailTriggerVO.getJournalMailTemplatesBody() != null
						&& !emailTriggerVO.getJournalMailTemplatesBody().isEmpty())
					emailTriggerVO.setEmailTemplateBody(emailTriggerVO.getJournalMailTemplatesBody());
				
				message = emailTriggerService.emailTrigger(emailTriggerVO);
				}
				if(emailTriggerVO.getReceiverRoleId() == 3) {
				emailTriggerVO.setTaskId(emailTriggerVO.getTaskId());
				emailTriggerVO.setReviewerUserId(emailTriggerVO.getUserId());
				emailTriggerVO.setManagingEditorId(emailTriggerVO.getManagingEditorId());
				emailTriggerVO.setEditorUserId(emailTriggerVO.getEditorUserId());
				emailTriggerVO.setReceiverRoleId(emailTriggerVO.getReceiverRoleId());
				if (emailTriggerVO.getJournalMailTemplatesBody() != null
						&& !emailTriggerVO.getJournalMailTemplatesBody().isEmpty())
					emailTriggerVO.setEmailTemplateBody(emailTriggerVO.getJournalMailTemplatesBody());
				message = emailTriggerService.emailTrigger(emailTriggerVO);
				}
				
				obj.put("Email trigger message", message);
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong :" + e);
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

	}

	@RequestMapping(value = "/manuscriptsForReviewAndRereview", method = RequestMethod.POST)
	public ResponseEntity<?> manuscriptsForReviewAndRereview(@RequestBody EmailTriggerVO emailTriggerVO) {
		JSONObject obj = new JSONObject();
		try {
			String result = tatEmailConfigurationService.manuscriptsForReviewAndRereview(emailTriggerVO);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

	}

}