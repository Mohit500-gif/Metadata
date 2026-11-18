package com.digi.uniprr.controller;

import java.util.List;

import com.digi.uniprr.utils.AppConstants;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.ReviewerSuggestedVO;
import com.digi.uniprr.VO.ReviewerSuggestionVO;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ReviewerSuggestedService;
import com.digi.uniprr.service.UserService;
import com.digi.uniprr.model.ManuScript;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/reviewerSuggested")
public class ReviewerSuggestedController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserService userService;

	@Autowired
	ReviewerSuggestedService reviewerSuggestedService;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;
	
	@Autowired
	ManuScriptRepo manuScriptRepo;

	@RequestMapping(value = "/createDecline", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody ReviewerSuggestionVO reviewerSuggestionVO) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /reviewerSuggested/createDecline"+
				AppConstants.REQUEST_BODY+" ::"+reviewerSuggestionVO.toString());
		JSONObject obj = new JSONObject();
		try {

			boolean isAcceptExist = false;
			boolean isDeclineExist = false;
			boolean isInviteExist = false;


			Integer invitedReviewerCount = manuScriptReviewerRepo.getInvitedreviewer(
					reviewerSuggestionVO.getManuscriptId(), reviewerSuggestionVO.getReviewerId(),
					reviewerSuggestionVO.getJournalId(), reviewerSuggestionVO.getManuscriptVersion());
			if (invitedReviewerCount <= 1) {
				isDeclineExist = reviewerSuggestedService.isDeclineExist(reviewerSuggestionVO);
				isInviteExist=reviewerSuggestedService.isInviteExist(reviewerSuggestionVO);
				isAcceptExist = reviewerSuggestedService.isAcceptExist(reviewerSuggestionVO);
			}
			if (isDeclineExist && !isInviteExist) {
				obj.put("data", "Task is already decline.");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}

			if (isAcceptExist) {
				obj.put("data", "Task is already accepted,link expire.");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}

			if (reviewerSuggestionVO.getBoxesofboxes() == "") {
				reviewerSuggestedService.createUser(reviewerSuggestionVO);
			} else {
				for (int i = 0; i <= reviewerSuggestionVO.getReviewerSuggested().size() - 1; i++) {
					User isExist = userService
							.getByEmail(reviewerSuggestionVO.getReviewerSuggested().get(i).getEmail());
					if (isExist != null) {
						obj.put("data", "User already exist  .");
						return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
					}
				}
				reviewerSuggestedService.createUser(reviewerSuggestionVO);
			}

			// Email Trigger
			if (reviewerSuggestionVO.getManuscriptId() != null && reviewerSuggestionVO.getJournalId() != null
					&& reviewerSuggestionVO.getReviewerId() != null) {
				ManuScriptReviewer getManuScriptReviewerDetails = manuScriptReviewerRepo
						.getManuScriptReviewerDeclineManuscript(reviewerSuggestionVO.getManuscriptId(),
								reviewerSuggestionVO.getManuscriptVersion(), reviewerSuggestionVO.getReviewerId(),
								reviewerSuggestionVO.getJournalId());
				String message = "";
				if (getManuScriptReviewerDetails != null) {
					if (reviewerSuggestionVO.getManuscriptStatus().equals(36)) {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(reviewerSuggestionVO.getJournalId());
						emailTriggerVO.setUserId(getManuScriptReviewerDetails.getEditorId());
						emailTriggerVO.setReviewerUserId(reviewerSuggestionVO.getReviewerId());
						emailTriggerVO.setManuscriptId(reviewerSuggestionVO.getManuscriptId());
						emailTriggerVO.setManuscriptStatus(reviewerSuggestionVO.getManuscriptStatus());
						emailTriggerVO.setReceiverRoleId(getManuScriptReviewerDetails.getEditorRoleId());
						if (getManuScriptReviewerDetails.getEditorRoleId() != null
								&& getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
							emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
						} else {
							emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());

						}
						
						ManuScript getManuScript1= manuScriptRepo.getById(reviewerSuggestionVO.getManuscriptId());
						if(getManuScript1!=null) {	
						if(getManuScript1.getManuscriptStatusId().equals(66)) {
							emailTriggerVO.setTaskId(80);
						}
						else {
							emailTriggerVO.setTaskId(6);
						}
						}
//						emailTriggerVO.setTaskId(6);
						emailTriggerVO.setRoleId(5);
						message = emailTriggerService.emailTrigger(emailTriggerVO);
					}

					if (reviewerSuggestionVO.getManuscriptStatus().equals(36)) {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(reviewerSuggestionVO.getJournalId());
						emailTriggerVO.setUserId(getManuScriptReviewerDetails.getReviewerId());
						emailTriggerVO.setReviewerUserId(reviewerSuggestionVO.getReviewerId());
						emailTriggerVO.setManuscriptId(reviewerSuggestionVO.getManuscriptId());
						emailTriggerVO.setManuscriptStatus(reviewerSuggestionVO.getManuscriptStatus());
						emailTriggerVO.setReceiverRoleId(5);
						if (getManuScriptReviewerDetails.getEditorRoleId() != null
								&& getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
							emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
						} else {
							emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());
						}
						
						ManuScript getManuScript1= manuScriptRepo.getById(reviewerSuggestionVO.getManuscriptId());
						if(getManuScript1!=null) {	
						if(getManuScript1.getManuscriptStatusId().equals(66)) {
							emailTriggerVO.setTaskId(79);
						}
						else {
							emailTriggerVO.setTaskId(33);
						}
						}
//						emailTriggerVO.setTaskId(33);
						emailTriggerVO.setRoleId(5);
						message = emailTriggerService.emailTrigger(emailTriggerVO);
					}
				}
				
				obj.put("data", message);
			}

		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something Went Wrong ...");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		obj.put("data", "User successfully created.");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/createReviewerSuggestedArray", method = RequestMethod.POST)
	public ResponseEntity<?> createReviewerSuggestedArray(@RequestBody ReviewerSuggestedVO reviewerSuggestedVO) {
		JSONObject obj = new JSONObject();
		try {

			String data = reviewerSuggestedService.createReviewerSuggestedArray(reviewerSuggestedVO);
			obj.put("data", data);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something Went Wrong.....");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		obj.put("data", "ReviewerSuggested successfully created.");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateReviewerSuggested", method = RequestMethod.POST)
	public ResponseEntity<?> updateReviewerSuggested(@RequestBody ReviewerSuggestedVO reviewerSuggestedVO) {
		JSONObject obj = new JSONObject();
		try {
			String data = reviewerSuggestedService.updateReviewerSuggested(reviewerSuggestedVO);
			obj.put("data", data);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/deleteReviewerSuggested", method = RequestMethod.POST)
	public ResponseEntity<?> deleteReviewerSuggested(@RequestBody ReviewerSuggestedVO reviewerSuggestedVO) {
		JSONObject obj = new JSONObject();
		try {
			String data = reviewerSuggestedService.deleteReviewerSuggested(reviewerSuggestedVO);
			obj.put("data", data);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/createReviewerOpposedArray", method = RequestMethod.POST)
	public ResponseEntity<?> createReviewerOpposedArray(@RequestBody ReviewerSuggestedVO reviewerSuggestedVO) {
		JSONObject obj = new JSONObject();
		try {
			String data = reviewerSuggestedService.createReviewerOpposedArray(reviewerSuggestedVO);
			obj.put("data", data);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something Went Wrong.....");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		obj.put("data", "ReviewerOpposed successfully created.");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateReviewerOpposed", method = RequestMethod.POST)
	public ResponseEntity<?> updateReviewerOpposed(@RequestBody ReviewerSuggestedVO reviewerSuggestedVO) {
		JSONObject obj = new JSONObject();
		try {
			String data = reviewerSuggestedService.updateReviewerOpposed(reviewerSuggestedVO);
			obj.put("data", data);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/deleteReviewerOpposed", method = RequestMethod.POST)
	public ResponseEntity<?> deleteReviewerOpposed(@RequestBody ReviewerSuggestedVO reviewerSuggestedVO) {
		JSONObject obj = new JSONObject();
		try {
			String data = reviewerSuggestedService.deleteReviewerOpposed(reviewerSuggestedVO);
			obj.put("data", data);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}
}
