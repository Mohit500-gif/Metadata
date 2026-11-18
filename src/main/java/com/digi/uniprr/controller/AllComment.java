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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.CommentVO;
import com.digi.uniprr.VO.FileVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptRatingVo;
import com.digi.uniprr.VO.OverDueManuscriptVO;
import com.digi.uniprr.service.AllCommentService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/comment")
public class AllComment {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AllCommentService allCommentService;
	
	JSONObject obj = new JSONObject();
	
	
	@PostMapping("getAllComment")
	public ResponseEntity<?> getAllComment(@RequestBody IDVO id){
		log.info("comment getAllComment");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			CommentVO commentData = allCommentService.getAllComment(id);
			if(commentData != null) {
				List<CommentVO> listcommentData = new ArrayList<>();
				listcommentData.add(commentData);
				obj.put("data", listcommentData);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			return new ResponseEntity<>("Data not found", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	@PostMapping("getAllCommentAuthor")
	public ResponseEntity<?> getAllCommentAuthor(@RequestBody IDVO id){
		log.info("comment getAllComment");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			CommentVO commentData = allCommentService.getAllCommentAuthor(id);
			if(commentData != null) {
				List<CommentVO> listcommentData = new ArrayList<>();
				listcommentData.add(commentData);
				obj.put("data", listcommentData);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			return new ResponseEntity<>("Data not found", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
		}
		
	}
	
	@PostMapping("ratingSetup")
	public ResponseEntity<?> ratingSetup(@RequestBody ManuscriptRatingVo manuscriptRatingVo){
		log.info("comment getAllComment");
		try {
			String message = allCommentService.ratingSetup(manuscriptRatingVo);
			if(message != null) {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			return new ResponseEntity<>("Data not found", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("getRatingSetupData")
	public ResponseEntity<?> getRatingSetupData(@RequestBody ManuscriptRatingVo manuscriptRatingVo){
		log.info("comment getAllComment");
		try {
			ManuscriptRatingVo message = allCommentService.getRatingSetupData(manuscriptRatingVo);
			if(message != null) {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			return new ResponseEntity<>("Data not found", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("getOverDueManuscript")
	public ResponseEntity<?> getOverDueManuscript(@RequestBody IDVO id){
		log.info("comment getAllComment");
		try {
			OverDueManuscriptVO message = allCommentService.getOverDueManuscript(id.getJournalId());
			if(message != null) {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			return new ResponseEntity<>("Data not found", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	@PostMapping("getAllReviewerFileOnManuscript")
	public ResponseEntity<?> getAllReviewerFileOnManuscript(@RequestBody IDVO id){
		log.info("comment getAllReviewerFileOnManuscript");
		try {
			List<FileVo> message = allCommentService.getAllReviewerFileOnManuscript(id);
			if(message != null && !message.isEmpty()) {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}else {
				obj.put("data", "Data not found");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		}catch(Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("insertRole")
	public ResponseEntity<?> insertRole(){
		log.debug("TestOne");
		allCommentService.insertRole();
		return new ResponseEntity<>("Done", HttpStatus.OK);
		
		
	}
	
	
	@PostMapping("submitPaymentDetailsByEic")
	public ResponseEntity<?> submitPaymentDetailsByEic(@RequestBody IDVO id){
		log.info("comment getAllReviewerFileOnManuscript");
		try {
			String message = allCommentService.submitPaymentDetailsByEic(id);
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("getBlinkerComment")
	public ResponseEntity<?> getBlinkerComment(@RequestBody IDVO id){
		log.info("Blinker count ############");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			CommentVO commentData = allCommentService.getBlinkerComment(id);
			if(commentData != null) {
				List<CommentVO> listcommentData = new ArrayList<>();
				listcommentData.add(commentData);
				obj.put("data", listcommentData);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			return new ResponseEntity<>("Data not found", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
		}
		
	}
	//UN-10 CR changes
	@PostMapping("getOtherReviewersComment")
	public ResponseEntity<?> getOtherReviewersComment(@RequestBody IDVO id){
		log.info("comment getOtherReviewersComment");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			CommentVO commentData = allCommentService.getOtherReviewersComment(id);
			if(commentData != null) {
				List<CommentVO> listcommentData = new ArrayList<>();
				listcommentData.add(commentData);
				obj.put("data", listcommentData);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			return new ResponseEntity<>("Data not found", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
		}
		
	}

}
