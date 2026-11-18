package com.digi.uniprr.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuScriptEditorCommonVO;
import com.digi.uniprr.VO.ManuscriptEditorVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.SendBackToAuthorVo;
import com.digi.uniprr.VO.UserJournalManuscriptVo;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptFileService;
import com.digi.uniprr.service.ManuscriptEditorService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/manuscriptEditor")
public class ManuscriptEditorRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManuscriptEditorService manuscriptEditorService;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	ManuScriptFileService manuScriptFileService;

	@Autowired
	ManuScriptFileRepo manuScriptFileRepo;
	
	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	Environment environment;

	@Value("${uniprrFolderPath}")
	private String UPLOAD_FOLDER;

	JSONObject obj = new JSONObject();

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> createManuscriptEditor(@RequestBody ManuscriptEditor manuscriptEditor) {
		try {
			manuscriptEditorService.createManuscriptEditor(manuscriptEditor);
			obj.put("data", "Data inserted...");
		} catch (Exception e) {
			log.debug("Exception: " +  e.getMessage());
			obj.put("data", "Something went wrong...");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getManuscriptEditor")
	public ResponseEntity<?> getManuscriptEditor(@RequestBody UserJournalManuscriptVo userJournalManuscriptVo) {

		List<ManuscriptEditor> manuscriptEditorList = manuscriptEditorService
				.getManuscriptEditor(userJournalManuscriptVo);
		obj.put("data", manuscriptEditorList);
		return new ResponseEntity<>(obj, HttpStatus.OK);

	}

	@PostMapping("addMultipleEditorRecord")
	public ResponseEntity<?> addMultipleEditorRecord(@RequestBody ManuScriptEditorCommonVO manuScriptEditorCommonVO) {
		try {
			manuscriptEditorService.addAllDetailsMultipleTableRCC(manuScriptEditorCommonVO);
			obj.put("data", "Data inserted Succesfully...");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.debug("Exception: " +  e.getMessage());
			obj.put("data", "Something went wrong..");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@PostMapping("getEditorRecord")
	public ResponseEntity<?> getEditorRecord(@RequestBody ManuscriptListVO manuscriptListVO) {
		try {
			List<ManuscriptListVO> getEditorRecord = manuscriptEditorService.getEditorRecord(manuscriptListVO);
			if (getEditorRecord != null && !getEditorRecord.isEmpty()) {
				obj.put("data", getEditorRecord);
			} else {
				obj.put("data", "data not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.debug("Exception: " +  e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something went wrong..");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@PostMapping("sendBackToAuthor")
	public ResponseEntity<?> sendBackToAuthor(@RequestBody ManuScriptEditorCommonVO manuScriptEditorCommonVO) {
		log.info("Inside ManuscriptEditorRestController sendBackToAuthor");
		try {
			ManuScript getManuscriptDetail = manuScriptRepo.getOne(manuScriptEditorCommonVO.getManuscriptId());
			Integer assignedUserRole = getManuscriptDetail.getAssignedUserRole();
			Integer assigneduser = getManuscriptDetail.getAssignedUser();
			
			manuscriptEditorService.addsendBackToAuthor(manuScriptEditorCommonVO);
			// Email Trigger
			if (getManuscriptDetail != null) {
				log.info("Inside in sendBackToAuthor For Email Trigger");
				if (manuScriptEditorCommonVO.getManuscriptId() != null && manuScriptEditorCommonVO.getEditorId() != null
						&& manuScriptEditorCommonVO.getJournalId() != null
						&& manuScriptEditorCommonVO.getManuscriptStatus().equals(4)) {
					log.info("Inside in sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger");
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(manuScriptEditorCommonVO.getJournalId());
					emailTriggerVO.setUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					if(manuScriptEditorCommonVO.getRoleId()!=null && manuScriptEditorCommonVO.getRoleId().equals(2)) {
						emailTriggerVO.setManagingEditorId(manuScriptEditorCommonVO.getEditorId());
					}else {
					emailTriggerVO.setEditorUserId(manuScriptEditorCommonVO.getEditorId());
					}
					emailTriggerVO.setManuscriptId(manuScriptEditorCommonVO.getManuscriptId());
					emailTriggerVO.setTaskId(10);
					emailTriggerVO.setRoleId(manuScriptEditorCommonVO.getRoleId());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setManuscriptStatus(manuScriptEditorCommonVO.getManuscriptStatus());
					log.info(
							"Inside in sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + getManuscriptDetail.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 3);
					log.info("User Sender Id:->" + manuScriptEditorCommonVO.getEditorId());
					log.info("Task Id:->" + 10);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
					log.info("Message of sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger "
							+ message);
					log.info("End of sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger ");
				} else if (manuScriptEditorCommonVO.getManuscriptId() != null
						&& manuScriptEditorCommonVO.getEditorId() != null
						&& manuScriptEditorCommonVO.getJournalId() != null
						&& manuScriptEditorCommonVO.getManuscriptStatus().equals(46)) {
					log.info("Inside in sendBackToAuthor as a Satus of Technical Modification For Email Trigger");
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(manuScriptEditorCommonVO.getJournalId());
					emailTriggerVO.setUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					if(manuScriptEditorCommonVO.getRoleId()!=null && manuScriptEditorCommonVO.getRoleId().equals(2)) {
						emailTriggerVO.setManagingEditorId(manuScriptEditorCommonVO.getEditorId());
					}else {
					emailTriggerVO.setEditorUserId(manuScriptEditorCommonVO.getEditorId());
					}
					emailTriggerVO.setManuscriptId(manuScriptEditorCommonVO.getManuscriptId());
					emailTriggerVO.setTaskId(11);
					emailTriggerVO.setRoleId(manuScriptEditorCommonVO.getRoleId());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setManuscriptStatus(manuScriptEditorCommonVO.getManuscriptStatus());
					log.info(
							"Inside in sendBackToAuthor as a Satus of Technical Modification For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + getManuscriptDetail.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 3);
					log.info("User Sender Id:->" + manuScriptEditorCommonVO.getEditorId());
					log.info("Task Id:->" + 11);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
					log.info("Message of sendBackToAuthor as a Satus of Technical Modification For Email Trigger "
							+ message);
					log.info("End of sendBackToAuthor as a Satus of Technical Modification For Email Trigger ");
				}
				
				if (manuScriptEditorCommonVO.getRoleId().equals(2) && assignedUserRole == 3) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuscriptDetail.getJournalId());
					emailTriggerVO.setUserId(assigneduser);
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setManuscriptId(getManuscriptDetail.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(manuScriptEditorCommonVO.getManuscriptStatus());
					emailTriggerVO.setReceiverRoleId(assignedUserRole);
					emailTriggerVO.setTaskId(86);
					emailTriggerVO.setRoleId(assignedUserRole);
					emailTriggerVO.setEditorUserId(assigneduser);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
				}
			}
			obj.put("data", "Data inserted Succesfully...");
		} catch (Exception e) {
			log.debug("Exception: " +  e.getMessage());
			e.printStackTrace();
			log.info("Some Error occured");
			obj.put("data", "Something went wrong..");
		}
		log.info("End of ManuscriptEditorRestController sendBackToAuthor");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("uploadSendBackToAuthor")
	public ResponseEntity<?> uploadSendBackToAuthor(@ModelAttribute SendBackToAuthorVo sendBackToAuthorVo,
			HttpServletRequest request) {
		try {
			ManuScript getManuscriptDetail = manuScriptRepo.getOne(sendBackToAuthorVo.getManuscriptId());
			Integer assignedUserRole = getManuscriptDetail.getAssignedUserRole();
			Integer assigneduser = getManuscriptDetail.getAssignedUser();
			
			String data = manuscriptEditorService.uploadSendBackToAuthor(sendBackToAuthorVo, request);
			// Email Trigger
			
			//Mail Will Trigger To Eic/Editor While Sending To Author While Sending t Revision Required
			if(sendBackToAuthorVo.getRoleId()!=null && sendBackToAuthorVo.getManuscriptStatus()!=null && sendBackToAuthorVo.getManuscriptStatus().equals(4)) {
				log.info("Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author");
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(sendBackToAuthorVo.getJournalId());
				emailTriggerVO.setUserId(sendBackToAuthorVo.getEditorId());
				if(sendBackToAuthorVo.getRoleId()!=null && sendBackToAuthorVo.getRoleId().equals(2)) {
					emailTriggerVO.setManagingEditorId(sendBackToAuthorVo.getEditorId());
				}else {
				emailTriggerVO.setEditorUserId(sendBackToAuthorVo.getEditorId());
				}
				emailTriggerVO.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
				emailTriggerVO.setTaskId(34);
				emailTriggerVO.setRoleId(sendBackToAuthorVo.getRoleId());
				emailTriggerVO.setReceiverRoleId(sendBackToAuthorVo.getRoleId());
				emailTriggerVO.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
				emailTriggerVO.setCommentToAuthor(sendBackToAuthorVo.getCommentsForAuthor());
				log.info(
						"Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author Email Trigger Data are:->"
								+ emailTriggerVO.toString());
				log.info("User Receiver Id:->" + sendBackToAuthorVo.getEditorId());
				log.info("User Receiver Role Id:->" + sendBackToAuthorVo.getRoleId());
				log.info("User Sender Role Id:->" + sendBackToAuthorVo.getRoleId());
				log.info("User Sender Id:->" + sendBackToAuthorVo.getEditorId());
				log.info("Task Id:->" + 29);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				log.debug("Emil Trigger" + message);
				log.info("Message of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger "
						+ message);
				log.info("End of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger ");
			}
			
			//Mail Will Trigger To Eic/Editor While Sending To Author While Sending to Technical Modification 
			if(sendBackToAuthorVo.getRoleId()!=null && sendBackToAuthorVo.getManuscriptStatus()!=null && sendBackToAuthorVo.getManuscriptStatus().equals(46)) {
				log.info("Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author");
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(sendBackToAuthorVo.getJournalId());
				emailTriggerVO.setUserId(sendBackToAuthorVo.getEditorId());
				if(sendBackToAuthorVo.getRoleId()!=null && sendBackToAuthorVo.getRoleId().equals(2)) {
					emailTriggerVO.setManagingEditorId(sendBackToAuthorVo.getEditorId());
				}else {
				emailTriggerVO.setEditorUserId(sendBackToAuthorVo.getEditorId());
				}
				emailTriggerVO.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
				emailTriggerVO.setTaskId(29);
				emailTriggerVO.setRoleId(sendBackToAuthorVo.getRoleId());
				emailTriggerVO.setReceiverRoleId(sendBackToAuthorVo.getRoleId());
				emailTriggerVO.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
				emailTriggerVO.setCommentToAuthor(sendBackToAuthorVo.getCommentsForAuthor());
//				if (sendBackToAuthorVo.getJournalMailTemplatesBody() != null && !sendBackToAuthorVo.getJournalMailTemplatesBody().isEmpty())
//					emailTriggerVO.setEmailTemplateBody(sendBackToAuthorVo.getJournalMailTemplatesBody());
				log.info(
						"Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author Email Trigger Data are:->"
								+ emailTriggerVO.toString());
				log.info("User Receiver Id:->" + sendBackToAuthorVo.getEditorId());
				log.info("User Receiver Role Id:->" + sendBackToAuthorVo.getRoleId());
				log.info("User Sender Role Id:->" + sendBackToAuthorVo.getRoleId());
				log.info("User Sender Id:->" + sendBackToAuthorVo.getEditorId());
				log.info("Task Id:->" + 29);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				log.debug("Emil Trigger" + message);
				log.info("Message of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger "
						+ message);
				log.info("End of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger ");
			}
			
			if (getManuscriptDetail != null) {
				log.info("Inside in sendBackToAuthor For Email Trigger");
				if (sendBackToAuthorVo.getManuscriptId() != null && sendBackToAuthorVo.getEditorId() != null
						&& sendBackToAuthorVo.getJournalId() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(4)) {
					log.info("Inside in sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger");
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(sendBackToAuthorVo.getJournalId());
					emailTriggerVO.setUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					if(sendBackToAuthorVo.getRoleId()!=null && sendBackToAuthorVo.getRoleId().equals(2)) {
						emailTriggerVO.setManagingEditorId(sendBackToAuthorVo.getEditorId());
					}else {
					emailTriggerVO.setEditorUserId(sendBackToAuthorVo.getEditorId());
					}
					emailTriggerVO.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
					emailTriggerVO.setTaskId(10);
					emailTriggerVO.setRoleId(sendBackToAuthorVo.getRoleId());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
					emailTriggerVO.setCommentToAuthor(sendBackToAuthorVo.getCommentsForAuthor());
					if (sendBackToAuthorVo.getJournalMailTemplatesBody() != null && !sendBackToAuthorVo.getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(sendBackToAuthorVo.getJournalMailTemplatesBody());
					if (sendBackToAuthorVo.getJournalMailTemplatesSubject()!= null && !sendBackToAuthorVo.getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(sendBackToAuthorVo.getJournalMailTemplatesSubject());
					
					log.info(
							"Inside in sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + getManuscriptDetail.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 3);
					log.info("User Sender Id:->" + sendBackToAuthorVo.getEditorId());
					log.info("Task Id:->" + 10);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
					log.info("Message of sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger "
							+ message);
					log.info("End of sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger ");
				} else if (sendBackToAuthorVo.getManuscriptId() != null && sendBackToAuthorVo.getEditorId() != null
						&& sendBackToAuthorVo.getJournalId() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(46)) {
					log.info("Inside in sendBackToAuthor as a Satus of Technical Modification For Email Trigger");
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(sendBackToAuthorVo.getJournalId());
					emailTriggerVO.setUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					if(sendBackToAuthorVo.getRoleId()!=null && sendBackToAuthorVo.getRoleId().equals(2)) {
						emailTriggerVO.setManagingEditorId(sendBackToAuthorVo.getEditorId());
					}else {
					emailTriggerVO.setEditorUserId(sendBackToAuthorVo.getEditorId());
					}
					emailTriggerVO.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
					emailTriggerVO.setTaskId(11);
					if (sendBackToAuthorVo.getJournalMailTemplatesBody() != null && !sendBackToAuthorVo.getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(sendBackToAuthorVo.getJournalMailTemplatesBody());
					if (sendBackToAuthorVo.getJournalMailTemplatesSubject()!= null && !sendBackToAuthorVo.getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(sendBackToAuthorVo.getJournalMailTemplatesSubject());

					emailTriggerVO.setRoleId(sendBackToAuthorVo.getRoleId());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
					emailTriggerVO.setCommentToAuthor(sendBackToAuthorVo.getCommentsForAuthor());
					log.info(
							"Inside in sendBackToAuthor as a Satus of Technical Modification For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + getManuscriptDetail.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 3);
					log.info("User Sender Id:->" + sendBackToAuthorVo.getEditorId());
					log.info("Task Id:->" + 11);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
					log.info("Message of sendBackToAuthor as a Satus of Technical Modification For Email Trigger "
							+ message);
					log.info("End of sendBackToAuthor as a Satus of Technical Modification For Email Trigger ");
				}
				
				if (sendBackToAuthorVo.getRoleId().equals(2) && assignedUserRole == 3) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuscriptDetail.getJournalId());
					emailTriggerVO.setUserId(assigneduser);
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setManuscriptId(getManuscriptDetail.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
					emailTriggerVO.setReceiverRoleId(assignedUserRole);
					emailTriggerVO.setTaskId(86);
					emailTriggerVO.setRoleId(assignedUserRole);
					emailTriggerVO.setEditorUserId(assigneduser);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
				}
			}
			obj.put("data", data);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong..");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/updateComment", method = RequestMethod.POST)
	public ResponseEntity<?> updateComment(@RequestBody ManuscriptEditorVO manuscriptEditorVO) {
		try {
			String message = manuscriptEditorService.updateComment(manuscriptEditorVO);
			if(message.equalsIgnoreCase("Comments updated successfully.")){
			obj.put("data", message);
			return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			log.debug("Exception: " +  e.getMessage());
			obj.put("data", "Something went wrong...");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/getAssignedEditor", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptByStatus(@RequestBody IDVO id) {
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptEditorService.getAssignedEditor(id);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("uploadFile")
	public ResponseEntity<?> uploadFile(@ModelAttribute SendBackToAuthorVo sendBackToAuthorVo,
			HttpServletRequest request) {
		try {
			String data = manuscriptEditorService.uploadFile(sendBackToAuthorVo, request);
			obj.put("data", data);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	}
	
