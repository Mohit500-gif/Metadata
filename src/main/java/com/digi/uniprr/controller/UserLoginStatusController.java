package com.digi.uniprr.controller;

import com.digi.uniprr.utils.AppConstants;
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

import com.digi.uniprr.model.UserLoginStatus;
import com.digi.uniprr.service.UserLoginStatusService;

@CrossOrigin(origins = "*" , allowedHeaders = "*")
@RestController
public class UserLoginStatusController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	UserLoginStatusService userLoginStatusService;
	
	@PostMapping("userLoginStatus")
	public ResponseEntity<?> userLoginStatus(@RequestBody UserLoginStatus userLoginStatus) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /userLoginStatus"+
				AppConstants.REQUEST_BODY+" ::"+userLoginStatus.toString());
		JSONObject obj = new JSONObject();
		try {
			String data=userLoginStatusService.userLoginStatus(userLoginStatus);
			obj.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Error");
		}
		return new ResponseEntity<Object>( obj,HttpStatus.OK);
	}
}
