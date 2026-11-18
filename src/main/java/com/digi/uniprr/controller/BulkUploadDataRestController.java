package com.digi.uniprr.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.UserBulkUploadDataVo;
import com.digi.uniprr.service.BulkUploadDataService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class BulkUploadDataRestController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	BulkUploadDataService bulkUploadDataService;

	@PostMapping("userBulkUploadData")
	public ResponseEntity<?> userBulkUploadData(@ModelAttribute UserBulkUploadDataVo userBulkUploadDataVo,
			HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		try {
			JSONObject result = bulkUploadDataService.userBulkUploadData(userBulkUploadDataVo, request);

			return new ResponseEntity<Object>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.info("Exception" + e);
			e.printStackTrace();
			log.info("Exception in userBulkUploadData"+e);
			obj.put("data", "Error");
			return new ResponseEntity<Object>( obj,HttpStatus.OK);
		}
		
	}
}
