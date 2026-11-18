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
import com.digi.uniprr.service.ReviewManuscriptService;
import com.digi.uniprr.utils.Scheduler;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/autoReviewe")
public class AutoRevieweController {
	
	@Autowired
	ReviewManuscriptService reviewManuscriptService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	JSONObject obj = new JSONObject();
	
	@GetMapping("reviewManuscript")
	public ResponseEntity<?> reminderEditor(){
		log.info("Start Reviewing manuscript for auto promote to Reviewed or No response for review");
		try {
			
			String status = reviewManuscriptService.reviewManuscript();
			if(status.equalsIgnoreCase("SUCCESS")) {
			return new ResponseEntity<>("Successfully Run", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Something went wrong. Please connect with support team.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
		
}