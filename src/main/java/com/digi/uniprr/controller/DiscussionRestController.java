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
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.DiscussionVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.service.DiscussionService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/discussion")
public class DiscussionRestController {
	
	@Autowired
	DiscussionService discussionService;
	
	@PostMapping("queryToEditor")
	public ResponseEntity<?> queryToEditor(@RequestBody DiscussionVo discussionVo) {
		JSONObject obj = new JSONObject();
		try {
			discussionService.queryToEditor(discussionVo);
			obj.put("data", "Query send successfully");
		}catch(Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getQueryById")
	public ResponseEntity<?> getQueryById(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<DiscussionVo> discussionVoList = discussionService.getQueryById(id);
			if(discussionVoList!=null && !discussionVoList.isEmpty()) {
				obj.put("data", discussionVoList);
			}else {
				obj.put("data", "Data not found");
			}
		}catch(Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getOutBox")
	public ResponseEntity<?> getOutBox(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<DiscussionVo> discussionVoList = discussionService.getOutBox(id);
			if(discussionVoList!=null && !discussionVoList.isEmpty()) {
				obj.put("data", discussionVoList);
			}else {
				obj.put("data", "Data not found");
			}
		}catch(Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
}
