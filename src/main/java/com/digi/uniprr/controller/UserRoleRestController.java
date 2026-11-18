package com.digi.uniprr.controller;

import java.util.List;

import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.service.UserService;
import com.digi.uniprr.utils.AppConstants;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.UserRoleService;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/userRole")

public class UserRoleRestController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	UserRoleService userRoleService;
	
	@Autowired
	ManuScriptReviewerService manuScriptReviewerService;

	@RequestMapping(value = "/listById", method = RequestMethod.POST)
	public ResponseEntity<?> listById(@RequestParam("id") Integer id){
		
		List<User> userRole = userRoleService.getlistById(id);
		
		return new ResponseEntity<>(userRole, HttpStatus.OK);
		
	}
	
	@PostMapping("addUserRole")
	public ResponseEntity<?> addUserRole(@RequestBody IDVO id){
		JSONObject obj = new JSONObject();
		try {
			UserJournal userRole = userRoleService.addUserRole(id);
			manuScriptReviewerService.updateInvitation(id);
			if(userRole!=null) {
				obj.put("data", "User created");
			}else {
				obj.put("data", "User Already Exist");
			}
		}catch(Exception ex){
			log.info("Exeption : " +ex);
			obj.put("data", "Something went wrong");
		}
		
		return new ResponseEntity<>(obj, HttpStatus.OK);
		
	}

	@Autowired
	UserService userService;

	@PostMapping("isAcceessRole")
	public ResponseEntity<?> accessRole(@RequestBody IDVO id){
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /userRole/isAcceessRole"+
				AppConstants.REQUEST_BODY+" ::"+id.toString());
		JSONObject obj = new JSONObject();
		try {
			boolean access = userRoleService.accessRole(id.getUserId(),id.getRoleId(), id.getJournalId());
			if(access) {
				obj.put("isAccess", true);
			}
			else {
				obj.put("isAccess", false);
			}

			if(id.getEmail() != null && !id.getEmail().trim().equalsIgnoreCase("")) {
				UserVO userDetails = userService.getUserByEmailId(id.getEmail());
				if (userDetails != null) {
					obj.put("data", userDetails);
				} else {
					obj.put("data", "User not found");
				}
			}
		}catch(Exception ex) {
			log.info("Exeption : " +ex);
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	

}
