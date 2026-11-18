package com.digi.uniprr.controller;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.service.PublonsService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/Publons")
@Slf4j
public class PublonsRestController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PublonsService publonsService;
	
	@PostMapping("getReviewerById")
	public ResponseEntity<?> getReviewerById(@RequestBody IDVO id){
		JSONObject obj = new JSONObject();
		logger.info("Inside getReviewerById" );
		logger.info("Search Reviewer, reviewer Id " +id.getName());
		
		String str = publonsService.getReviewerById(id.getName());
		if(str!=null && !str.equalsIgnoreCase("")) {
			logger.info("Reviewer found with Id " +id.getName());
			return new ResponseEntity<>(str, HttpStatus.OK);
		}
		logger.info("Reviewer not found with Id " +id.getName());
		obj.put("data", "Data Not Found");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@GetMapping("/generateReviewCommentReport")
	public void generateReviewCommentReport() {
		publonsService.generateAndSendComment();
	}
}
