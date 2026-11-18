package com.digi.uniprr.controller;



import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.service.ReminderService;
import com.digi.uniprr.utils.Scheduler;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/autoReminder")
public class AutoReminderController {
	
	@Autowired
	ReminderService reminderService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	JSONObject obj = new JSONObject();


	
	@GetMapping("reminderEditor")
	public ResponseEntity<?> reminderEditor(){
		log.info("Reminder for Editor");
		try {

			reminderService.autoReminderForEditor();
			return new ResponseEntity<>("Successfully Run", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("reminderAuthor")
	public ResponseEntity<?> reminderAuthor(){
		log.info("Reminder for Author");
		try {

			reminderService.autoReminderForAuthor();
			reminderService.autoWithdrawReminderForAuthor();
			return new ResponseEntity<>("Successfully Run", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("reminderReviewer")
	public ResponseEntity<?> reminderReviewer(){
		log.info("Reminder for Reviewer");
		try {

			reminderService.autoReminderForReviewer();
			return new ResponseEntity<>("Successfully Run", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("noResponseReminderEditor")
	public ResponseEntity<?> noResponseReminderEditor(){
		log.info("Reminder for Editor/EIC");
		try {

			reminderService.autoNoresponseReviewerReminderForEditor();
			return new ResponseEntity<>("Successfully Run", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}	
}