package com.digi.uniprr.controller;
import com.digi.uniprr.utils.AppConstants;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.ContactUsVO;
import com.digi.uniprr.service.ContactUsService;

@RestController
public class ContactUsRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	 ContactUsService contactUsService;
		
	@RequestMapping(value="contactUs", method = RequestMethod.POST)
	private ResponseEntity<?> sendContactUsEmail(@RequestBody ContactUsVO contact) {
		log.info(" Send contact email to journal");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST +" "+
				AppConstants.REQUEST_URI+" :: /contactUs"+
				AppConstants.REQUEST_BODY+" ::"+contact.toString());
		JSONObject obj = new JSONObject();
		try {
			    String status = contactUsService.saveContactus(contact);
			    		    
			
			if (status.equals("Success")) {
				obj.put("message","Success");
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
