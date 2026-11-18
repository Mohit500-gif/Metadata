package com.digi.uniprr.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.DiscussionVo;
import com.digi.uniprr.VO.ManuscriptMessagingVO;
import com.digi.uniprr.model.Discussion;
import com.digi.uniprr.model.ManuscriptMessaging;
import com.digi.uniprr.service.ManuscriptMessagingService;

@RestController
@RequestMapping("/manuscriptMessaging")
public class ManuscriptMessagingRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManuscriptMessagingService manuscriptMessagingService;

	@PostMapping("sendMessage")
	public ResponseEntity<?> sendMessage(@RequestBody ManuscriptMessagingVO manuscriptMessagingVO) {
		log.info("Inside ManuscriptMessagingRestController sendMessage");
		JSONObject obj = new JSONObject();
		try {
			ManuscriptMessaging data = manuscriptMessagingService.sendMessage(manuscriptMessagingVO);
			if(data!=null) {
				obj.put("data", "Message send successfully");
			}
			
			log.info(obj.toString());
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PostMapping("getMessage")
	public ResponseEntity<?> getMessage(@RequestBody ManuscriptMessagingVO manuscriptMessagingVO) {
		log.info("Inside ManuscriptMessagingRestController getMessage");
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptMessagingVO> getManuscriptMessagingVOData = manuscriptMessagingService
					.getMessage(manuscriptMessagingVO);
			if (getManuscriptMessagingVOData != null && !getManuscriptMessagingVOData.isEmpty()) {
				obj.put("data", getManuscriptMessagingVOData);
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "data not found");
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@PostMapping("discussion")
	public ResponseEntity<?> discussion(@RequestBody DiscussionVo discussionVo) {
		log.info("Inside ManuscriptMessagingRestController sendMessage");
		JSONObject obj = new JSONObject();
		try {
			String createAndUpdatediscussion = manuscriptMessagingService
					.discussion(discussionVo);
				obj.put("data", createAndUpdatediscussion);
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@PostMapping("getDiscussion")
	public ResponseEntity<?> getDiscussion(@RequestBody DiscussionVo discussionVo) {
		log.info("Inside ManuscriptMessagingRestController sendMessage");
		JSONObject obj = new JSONObject();
		try {
			List<DiscussionVo> getManuscriptDiscussionVoData = manuscriptMessagingService
					.getDiscussion(discussionVo);
			if (getManuscriptDiscussionVoData != null && !getManuscriptDiscussionVoData.isEmpty()) {
				obj.put("data", getManuscriptDiscussionVoData);
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "data not found");
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PostMapping("deleteDiscussion")
	public ResponseEntity<?> deleteDiscussion(@RequestBody DiscussionVo discussionVo) {
		log.info("Inside ManuscriptMessagingRestController sendMessage");
		JSONObject obj = new JSONObject();
		try {
			String deleteDiscussion = manuscriptMessagingService
					.deleteDiscussion(discussionVo);
				obj.put("data", deleteDiscussion);
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@PostMapping("getDataByJournalAndRole")
	public ResponseEntity<?> getDataByJournalAndRole(@RequestBody ManuscriptMessagingVO manuscriptMessagingVO) {
		log.info("Inside ManuscriptMessagingRestController sendMessage");
		JSONObject obj = new JSONObject();
		try {
			List<Discussion> getManuscriptMessagingVOData = manuscriptMessagingService
					.getDataByJournalAndRole(manuscriptMessagingVO);
			if (getManuscriptMessagingVOData != null && !getManuscriptMessagingVOData.isEmpty()) {
				obj.put("data", getManuscriptMessagingVOData);
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "data not found");
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@PostMapping("getDataByManuscript")
	public ResponseEntity<?> getDataByManuscript(@RequestBody ManuscriptMessagingVO manuscriptMessagingVO) {
		log.info("Inside ManuscriptMessagingRestController sendMessage");
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptMessaging> getManuscriptMessagingVOData = manuscriptMessagingService.getDataByManuscript(manuscriptMessagingVO);
			if (getManuscriptMessagingVOData != null && !getManuscriptMessagingVOData.isEmpty()) {
				obj.put("data", getManuscriptMessagingVOData);
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "data not found");
				log.info(obj.toString());
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
