package com.digi.uniprr.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalIssuesPlannerVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.model.MeAvailability;
import com.digi.uniprr.service.ManagingEditorService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/managingEditor")
public class ManagingEditorRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManagingEditorService managingEditorService;

	@PostMapping("saveManagingEditorAvailability")
	public ResponseEntity<?> saveEditorAvailability(@RequestBody MeAvailability meAvailability) {
		log.info("Inside ManagingEditorRestController saveEditorAvailability");
		JSONObject obj = new JSONObject();
		try {
			managingEditorService.saveManagingEditorAvailability(meAvailability);
			obj.put("data", "Created successfully");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getManagingEditorWithAvailability", method = RequestMethod.GET)
	public List<ReviewerVO> getEditorWithAvailability() {
		log.info("Inside ManagingEditorRestController getEditorWithAvailability");
		List<ReviewerVO> managingEditorList = managingEditorService.getManagingEditorWithAvailability();
		return managingEditorList;
	}

	@PostMapping("getManagingEditorAvailability")
	public ResponseEntity<?> getEditorAvailability(@RequestBody IDVO id) {
		log.info("Inside ManagingEditorRestController getManagingEditorAvailability");
		JSONObject obj = new JSONObject();
		List<MeAvailability> managingEditorAvailabilityList = new ArrayList<>();
		try {
			managingEditorAvailabilityList = managingEditorService.getManagingEditorAvailabilityById(id.getId());
			obj.put("data", managingEditorAvailabilityList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping("deleteManagingEditorAvailability")
	public ResponseEntity<?> deleteEditorAvailability(@RequestBody IDVO id) {
		log.info("Inside ManagingEditorRestController deleteEditorAvailability");
		JSONObject obj = new JSONObject();
		try {
			managingEditorService.deleteManagingEditorAvailabilityById(id.getId());
			obj.put("data", "Data deleted successfully");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	// Here Journal Issues Planner Setup On Manging Editor

	// Create and Update Issues
	@PostMapping("createIssue")
	public ResponseEntity<?> createIssue(@RequestBody JournalIssuesPlannerVO journalIssuesPlannerVO) {
		log.info("Inside ManagingEditorRestController createIssue");
		JSONObject obj = new JSONObject();
		try {
			String message = managingEditorService.createIssue(journalIssuesPlannerVO);
			obj.put("data", message);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// get Issues List
	@PostMapping("getIssueList")
	public ResponseEntity<?> getIssueList(@RequestBody JournalIssuesPlannerVO journalIssuesPlannerVO) {
		log.info("Inside ManagingEditorRestController getIssueList");
		JSONObject obj = new JSONObject();
		try {
			List<JournalIssuesPlannerVO> list = managingEditorService.getIssueList(journalIssuesPlannerVO);
			if (list != null && !list.isEmpty()) {
				log.info("Data Found");
				obj.put("data",list);
			} else {
				log.info("Data Not Found");
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	// delete Issues
	@PostMapping("deleteIssue")
	public ResponseEntity<?> deleteIssue(@RequestBody JournalIssuesPlannerVO journalIssuesPlannerVO) {
		log.info("Inside ManagingEditorRestController deleteIssue");
		JSONObject obj = new JSONObject();
		try {
			String message = managingEditorService.deleteIssue(journalIssuesPlannerVO);
			obj.put("data", message);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
}
