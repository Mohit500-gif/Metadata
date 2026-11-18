package com.digi.uniprr.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.ManuscriprtTatVO;
import com.digi.uniprr.service.ManuscriptTatService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/manuscriptTatRestController")
public class ManuscriptTatRestController {
	
	@Autowired
	ManuscriptTatService manuscriptTatService;
	
	JSONObject obj = new JSONObject();
	
	@PostMapping("setTatForManuscript")
	public ResponseEntity<?> setTatForManuscript(@RequestBody ManuscriprtTatVO manuscriprtTatVO) {
		String msg = manuscriptTatService.setTatForManuscript(manuscriprtTatVO);
		obj.put("data", msg);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
}
