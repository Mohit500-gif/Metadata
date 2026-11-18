package com.digi.uniprr.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digi.uniprr.utils.AppConstants;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.MailQueueCountVO;
import com.digi.uniprr.VO.MailQueueVo;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.MailQueueArchive;
import com.digi.uniprr.service.MailQueueService;
import com.digi.uniprr.VO.BulkEmailVO;
@RestController
@RequestMapping("/mailQueue")
public class MailQueueController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MailQueueService mailQueueService;
	
	@Value("${enableMailArchive}")
	boolean enableMailArchive;
	
	@Value("${totalMonthToBeArchived}")
	int totalMonthToBeArchived;

	JSONObject obj = new JSONObject();
	
	@PostMapping("createMailQueue")
	public ResponseEntity<?> createMailQueue(@RequestBody MailQueue mailQueue) {
		try {
			mailQueueService.createMailQueue(mailQueue);
			obj.put("data", "MailQueue Created...");
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getMailQueue")
	public ResponseEntity<?> getMailQueue(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /mailQueue/getMailQueue"+
				AppConstants.REQUEST_BODY+" :: "+id.toString());
		try {
			List<MailQueue> mailQueue = mailQueueService.getMailQueueUserId(id);
			if (mailQueue != null && !mailQueue.isEmpty()) {
				List<MailQueueVo> add = new ArrayList<>();
				mailQueue.stream().forEach(i -> {
					MailQueueVo b = new MailQueueVo();
					b.setId(i.getId());
					b.setIsActive(i.getIsActive());
					b.setIsSeen(i.getIsSeen());
					b.setJournalId(i.getJournalId());
					b.setMail_send_date(i.getMail_send_date());
					b.setMailBCC(i.getMailBCC());
					b.setMailBody(i.getMailBody());
					b.setMailCC(i.getMailCC());
					b.setMailFrom(i.getMailFrom());
					b.setMailSubject(i.getMailSubject());
					b.setMailTo(i.getMailTo());
					b.setMailToRole(i.getMailToRole());
					b.setMailTOUserId(i.getMailTOUserId());
					b.setManuscriptId(i.getManuscriptId());
					b.setManuscriptStatus(i.getManuscriptStatus());
					b.setManuscriptVersion(i.getManuscriptVersion());
					add.add(b);
				});
				obj.put("data", add);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getMailQueueArchive")
	public ResponseEntity<?> getMailQueueArchive(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<MailQueueArchive> mailQueue = mailQueueService.getMailQueueArchiveUserId(id);
			if (mailQueue != null && !mailQueue.isEmpty()) {
				List<MailQueueVo> add = new ArrayList<>();
				mailQueue.stream().forEach(i -> {
					MailQueueVo b = new MailQueueVo();
					b.setId(i.getId());
					b.setIsActive(i.getIsActive());
					b.setIsSeen(i.getIsSeen());
					b.setJournalId(i.getJournalId());
					b.setMail_send_date(i.getMail_send_date());
					b.setMailBCC(i.getMailBCC());
					b.setMailBody(i.getMailBody());
					b.setMailCC(i.getMailCC());
					b.setMailFrom(i.getMailFrom());
					b.setMailSubject(i.getMailSubject());
					b.setMailTo(i.getMailTo());
					b.setMailToRole(i.getMailToRole());
					b.setMailTOUserId(i.getMailTOUserId());
					b.setManuscriptId(i.getManuscriptId());
					b.setManuscriptStatus(i.getManuscriptStatus());
					b.setManuscriptVersion(i.getManuscriptVersion());
					add.add(b);
				});
				obj.put("data", add);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getMailIsSeen")
	public ResponseEntity<?> getMailIsSeen(@RequestBody IDVO id) {
		try {
			mailQueueService.getMailIsSeen(id.getId());
			obj.put("data", "MailIsSeen Succesfully ...");
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getMailList")
	public ResponseEntity<?> getMailList(@RequestBody IDVO id) {
		try {
			List<MailQueue> mailTo = mailQueueService.getMailListMailTo(id.getName());
			List<MailQueue> mailFrom = mailQueueService.getMailListMailFrom(id.getName());
			if (mailTo != null && !mailTo.isEmpty()) {
				obj.put("mailTo", mailTo);
			} else {
				obj.put("mailTo", "No data found");
			}

			if (mailFrom != null && !mailFrom.isEmpty()) {
				obj.put("mailFrom", mailFrom);
			} else {
				obj.put("mailFrom", "No data found");
			}
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getMailByManuscript")
	public ResponseEntity<?> getMailByManuscript(@RequestBody IDVO id) {
		try {
			List<MailQueueVo> mailQueue = mailQueueService.getMailByManuscript(id);
			if (mailQueue != null && !mailQueue.isEmpty()) {
				obj.put("data", mailQueue);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getMailQueueCount")
	public ResponseEntity<?> getMailQueueCount(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			MailQueueCountVO mailQueueCount = mailQueueService.getMailQueueCount(id);
			if(mailQueueCount!=null){
				obj.put("status", true);
				obj.put("data", mailQueueCount);
			}else {
				obj.put("status", false);
			}
			
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@Scheduled(cron="${mailArchiveCron}")
	public void mailArchiveScheduler() {
		if(enableMailArchive) {
			log.info("...........Mail Archive process initiated................");
			mailQueueService.moveMailDataToArchive();
			log.info("...........Mail Archive process finished................");			
		}else {
			log.info("...........Mail Archive process disabled................");
		}
	}
	
	@GetMapping("getCurrentNotificationDuration")
	public ResponseEntity<?> getCurrentNotificationDuration() {
		JSONObject obj = new JSONObject();
		int response = 0;
		try {
			response = totalMonthToBeArchived;
		} catch (Exception e) {
			log.error("getCurrentNotificationDuration error {} {}",e.getMessage(),e);
			e.printStackTrace();
		}
		obj.put("data", response);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
    @PostMapping("createBulkMailQueue")
    public ResponseEntity<?> createMailQueue(@RequestBody BulkEmailVO bulkEmail) {
        try {

            bulkEmail.getEmailIds().forEach(emailId -> {
                MailQueue mailQueue = new MailQueue();
                mailQueue.setMailTo(emailId.getEmail());
				mailQueue.setMailToRole(emailId.getRoleId());
                mailQueue.setMailTOUserId(emailId.getUserId());
                mailQueue.setMailSubject(bulkEmail.getMailSubject());
                mailQueue.setMailBody(bulkEmail.getMailBody());
                mailQueue.setMailFrom(bulkEmail.getMailFrom());
                mailQueue.setJournalId(bulkEmail.getJournalId());
                mailQueue.setModuleName("Bulk Email");
                mailQueue.setMail_send_date(new Date());
                mailQueue.setManuscriptId(bulkEmail.getManuscriptId());
                mailQueueService.createMailQueue(mailQueue);
            });
            obj.put("data", "The mailing process initiated successfully..It may take couple of minutes to complete.");
        } catch (Exception e) {
            obj.put("data", "Something Went Wrong");
            return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}
