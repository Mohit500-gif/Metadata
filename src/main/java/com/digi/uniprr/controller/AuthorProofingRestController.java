package com.digi.uniprr.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.service.AuthorProofingService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.MailQueueService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/authorProofing")
public class AuthorProofingRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuthorProofingService authorProofingService;

	@Autowired
	private EmailTriggerService emailTriggerService;

	@Autowired
	private ManuScriptRepo manuScriptRepo;
	
	@Autowired
	private MailQueueService mailQueueService;
	
	@Value("${spring.mail.username}")
	private String mailFrom;
	
	@Value("${support.email}")
	private String mailTo;
	
	@Value("${supportCC.email}")
	private String mailCC;
	
	@PostMapping("getManuscriptOutputFile")
	public ResponseEntity<?> getManuscriptOutputFile(@RequestBody IDVO vo) {
		log.info("Inside AuthorRestController of getManuscriptByactive");
		JSONObject obj = new JSONObject();
		try {
			String manu = authorProofingService.getManuscriptOutputFile(vo);
			if (manu != null && !manu.isEmpty()) {
				log.info("Data Found");
				obj.put("data", manu);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			} else {
				log.info("Data Not Found");
				obj.put("message", "Data Not Found");
			}

		} catch (Exception e) {
			log.info("Error");
			e.printStackTrace();
			obj.put("message", "Error");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@PostMapping("uploadManuscriptInputFile")
	public ResponseEntity<?> uploadManuscriptInputFile(@RequestParam("manuScriptId") Integer manuScriptId,
			MultipartFile[] files, @RequestParam("isRecheck") String isRecheck,
			@RequestParam("noChangeApproved") String noChangeApproved, @RequestParam("comment") String comment,
			@RequestParam("status") Integer status, HttpServletRequest request) {
		log.info("Inside AuthorRestController of uploadManuscriptInputFile");
		JSONObject obj = new JSONObject();
		try {
			int manuscriptStatusId = Integer.parseInt(request.getParameter("manuscriptStatus"));
			String file = authorProofingService.uploadManuscriptInputFile(manuScriptId, files, isRecheck, noChangeApproved, comment, manuscriptStatusId);
			log.info("Inside AuthorRestController - After Service layer call : " + file);

			if (file.contains("successfully uploaded") && manuScriptId != null) {
				ManuScript manuscript = manuScriptRepo.getById(manuScriptId);
				// authorproofing complete mal
				if (manuscript != null && status != null && status.equals(1)) {
					// Email Trigger
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(manuscript.getJournalId());
					emailTriggerVO.setUserId(manuscript.getAuthorId());
					emailTriggerVO.setAuthorUserId(manuscript.getAuthorId());
					emailTriggerVO.setManuscriptId(manuScriptId);
					emailTriggerVO.setManuscriptStatus(61);
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(38);
					emailTriggerVO.setRoleId(4);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					
					obj.put("data", message);
				}

				// author check mail complete
				if (manuscript != null && status != null && status.equals(0)) {
					// Email Trigger
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(manuscript.getJournalId());
					emailTriggerVO.setUserId(manuscript.getAuthorId());
					emailTriggerVO.setAuthorUserId(manuscript.getAuthorId());
					emailTriggerVO.setManuscriptId(manuScriptId);
					emailTriggerVO.setManuscriptStatus(62);
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(40);
					emailTriggerVO.setRoleId(4);
					log.info("User Receiver Id:->" + manuscript.getAuthorId());
					log.info("User Sender Id:->" + manuscript.getAuthorId());
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					
					obj.put("data", message);
				}
			}else {
				obj.put("data", file);
			}
		} catch (Exception e) {
			ManuScript manuscript = manuScriptRepo.getById(manuScriptId);
			log.error("Error in author check/proofing for Manuscript : " + manuscript.getManuscriptCode() + ", Exception :  " + e.getMessage(), e);
			
			String body = "Error in AuthorProofing for Manuscript  : "+manuscript.getManuscriptCode()+"\nFailure Reason: "+e.getMessage();
			String subject = "Error in AuthorProofing for Article "+manuscript.getManuscriptCode();
			Integer journalId = manuscript.getJournalId();
			Integer manuscriptId = manuscript.getManuScriptId();
			mailQueueService.insertMailQueue(mailFrom, mailTo, mailCC, journalId, manuscriptId, subject, body);
			
			obj.put("data", "Error in author check/proofing");
		}
		
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
}
