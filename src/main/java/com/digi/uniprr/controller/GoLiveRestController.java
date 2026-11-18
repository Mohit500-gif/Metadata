package com.digi.uniprr.controller;
import com.digi.uniprr.utils.AppConstants;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.GoLiveVo;

import com.digi.uniprr.service.GoLiveService;

@RestController
public class GoLiveRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	GoLiveService goLiveService;
		
	@GetMapping("goLive")
	private ResponseEntity<?> getInstitution() {
		log.info("Inside In autoComplete getInstitution");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.GET+
				AppConstants.REQUEST_URI+" :: /goLive");
		JSONObject obj = new JSONObject();
		try {
			GoLiveVo list = goLiveService.getGoLiveData();
			
			if (list != null ) {
				obj.put("data", list);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			obj.put("message", "Data Not Found");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("message", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}
	
}
