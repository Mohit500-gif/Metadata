package com.digi.uniprr.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptIssuesPlannerVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.model.EditorAccess;
import com.digi.uniprr.model.EditorAvailability;
import com.digi.uniprr.service.EditorAccessService;
import com.digi.uniprr.service.EditorService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class EditorRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	JSONObject obj = new JSONObject();

	@Autowired
	EditorService editorService;
	
	@Autowired
	EditorAccessService editorAccessService;

	@PostMapping("getManuScriptDataByStatus")
	public ResponseEntity<?> getManuScriptDataByStatus(@RequestBody ManuscriptListVO manuscriptListVO) {
		log.info("Inside EditorRestController getManuScriptDataByStatus");
		return new ResponseEntity<>(editorService.getManuScriptDataByStatus(manuscriptListVO), HttpStatus.OK);
	}

	@PostMapping("updateManuScriptByAuthor")
	public ResponseEntity<?> updateManuScriptByAuthor(@RequestBody ManuscriptListVO manuscriptListVO) {
		log.info("Inside EditorRestController updateManuScriptByAuthor");
		try {
			editorService.updateManuScriptByAuthor(manuscriptListVO);
			obj.put("message", "updated ManuScript Status");
		} catch (Exception e) {
			obj.put("message", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	
	@PostMapping("sendBackToEIC")
	public ResponseEntity<?> sendBackToEIC(@ModelAttribute ManuscriptListVO manuscriptListVO, HttpServletRequest request) {
		log.info("Inside EditorRestController updateManuScriptByAuthor");
		try {
			
			String message = editorService.disableAllEditor(manuscriptListVO,  request);
			if(message.equalsIgnoreCase("Article already processed.")){
			obj.put("data", message);
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
			} else {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			obj.put("message", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);

		}
	}

	// Editor Availability

	@PostMapping("saveEditorAvailability")
	public ResponseEntity<?> saveEditorAvailability(@RequestBody EditorAvailability editorAvailability) {
		log.info("Inside EditorRestController saveEditorAvailability");
		JSONObject obj = new JSONObject();
		try {
			editorService.saveEditorAvailability(editorAvailability);
			obj.put("data", "Created successfully");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getEditorWithAvailability", method = RequestMethod.GET)
	public List<ReviewerVO> getEditorWithAvailability() {
		log.info("Inside EditorRestController getEditorWithAvailability");
		List<ReviewerVO> editorList = editorService.getEditorWithAvailability();
		return editorList;
	}

	@PostMapping("getEditorAvailability")
	public ResponseEntity<?> getEditorAvailability(@RequestBody IDVO id) {
		log.info("Inside EditorRestController getEditorAvailability");
		JSONObject obj = new JSONObject();
		List<EditorAvailability> editorAvailabilityList = new ArrayList<>();
		try {
			editorAvailabilityList = editorService.getEditorAvailabilityById(id.getId());
			obj.put("data", editorAvailabilityList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping("deleteEditorAvailability")
	public ResponseEntity<?> deleteEditorAvailability(@RequestBody IDVO id) {
		log.info("Inside EditorRestController deleteEditorAvailability");
		JSONObject obj = new JSONObject();
		try {
			editorService.deleteEditorAvailabilityById(id.getId());
			obj.put("data", "Data deleted successfully");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	// Here Journal Issues Planner Setup On Editor

	// Create And Update Issue For Manuscript

	@PostMapping("createIssueForManuscript")
	public ResponseEntity<?> createIssueForManuscript(
			@RequestBody ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO) {
		log.info("Inside EditorRestController deleteEditorAvailability");
		JSONObject obj = new JSONObject();
		try {
			String data = editorService.createIssueForManuscript(manuscriptIssuesPlannerVO);
			obj.put("data", data);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Get list
	@PostMapping("getIssueListForManuscript")
	public ResponseEntity<?> getIssueListForManuscript(
			@RequestBody ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO) {
		log.info("Inside EditorRestController deleteEditorAvailability");
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptIssuesPlannerVO> listOfAssignManuscript = editorService
					.getIssueListForManuscript(manuscriptIssuesPlannerVO);
			if (listOfAssignManuscript != null && !listOfAssignManuscript.isEmpty()) {
				obj.put("data", listOfAssignManuscript);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// Delete manuscriptIssue Assign
	@PostMapping("deleteAssginManuscriptONIssue")
	public ResponseEntity<?> deleteAssginManuscriptONIssue(
			@RequestBody ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO) {
		log.info("Inside EditorRestController deleteEditorAvailability");
		JSONObject obj = new JSONObject();
		try {
			String data = editorService.deleteAssginManuscriptONIssue(manuscriptIssuesPlannerVO);
			obj.put("data", data);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@GetMapping("getEditorAccess")
	public ResponseEntity<?> getEditorAccess() {
		log.info("Inside EditorRestController getEditorAccess");
		JSONObject obj = new JSONObject();
		try {
			List<EditorAccess> data = editorAccessService.getActiveData();
			if(data!=null && !data.isEmpty())
				obj.put("data", data);
			else
				obj.put("data", "No data found");
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

}
