package com.digi.uniprr.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.model.ReviewerRating;
import com.digi.uniprr.service.ReviewerRatingService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ReviewerRatingRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ReviewerRatingService reviewerRatingService;

	@PostMapping("createReviewerRating")
	public ResponseEntity<?> createReviewerRating(@RequestBody List<ReviewerRating> reviewerRating) {
		log.info("Inside in ReviewerRatingRestController createReviewerRating");
		JSONObject obj = new JSONObject();
		try {
			reviewerRatingService.reviewerRatingService(reviewerRating);
			obj.put("data", "successfully added");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "something went wrong");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}

	@PostMapping("getReviewerRating")
	public ResponseEntity<?> getReviewerRating(@RequestBody ReviewerRating reviewerRating) {
		log.info("Inside in ReviewerRatingRestController getReviewerRating");
		JSONObject obj = new JSONObject();
		try {
			List<ReviewerRating> list = reviewerRatingService.getReviewerRating(reviewerRating);
			if (list != null && !list.isEmpty()) {
				obj.put("data", list);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "data not found");
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "something went wrong");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}
}
