package com.digi.uniprr.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.UserJournalVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.service.UserJournalService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/userJournal")
public class UserJournalRestController {

	@Autowired
	UserJournalService userJournalService;
	JSONObject obj = new JSONObject();

	@RequestMapping(value = "/roleJournalById", method = RequestMethod.POST)
	public ResponseEntity<?> roleJournalById(@RequestBody UserJournalVO userJournalVO) {
		List<UserVO> list=userJournalService.getListById(userJournalVO);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/userListForJournal", method = RequestMethod.POST)
	public ResponseEntity<?> userListForJournal(@RequestBody UserJournalVO userJournalVO) {
		List<UserVO> list=userJournalService.getUserListForJournal(userJournalVO);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/assignedEditorByManuscriptId", method = RequestMethod.POST)
	public ResponseEntity<?> assignedEditorByManuscriptId(@RequestBody UserJournalVO userJournalVO) {
		List<UserVO> list=userJournalService.getAssignedEditorListById(userJournalVO);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@PostMapping("activateUserJournal")
	public ResponseEntity<?> activateUserJournal(@RequestBody UserJournalVO userJournalVO) {
		try {
			String message = userJournalService.activateUserJournal(userJournalVO);
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}

	}
}
