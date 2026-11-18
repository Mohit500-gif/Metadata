package com.digi.uniprr.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.websocket.server.PathParam;

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
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.ForgotPassword;



@RestController
@SuppressWarnings({ "unchecked" })
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ForgotPasswordRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ForgotPassword forgotPassword;

	@Autowired
	UserRepo userRepo;
	
	@Autowired
	UserJournalRepo userJournalRepo;

	@PostMapping("forgotPassword")
	public ResponseEntity<?> forgotUserPassword(@RequestBody UserVO user) {
		log.info("Inside ForgotPasswordRestController forgotUserPassword");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST +" "+
				AppConstants.REQUEST_URI+" :: /forgotPassword"+
				AppConstants.REQUEST_BODY+" ::"+user.toString());
		JSONObject obj = new JSONObject();
		try {
			User existingUser = userRepo.findByEmail(user.getEmail());
			if (existingUser != null) {
				if(existingUser.getAccountLocked()!=null && existingUser.getAccountLocked()==1) {
					List<UserJournal> getUserJournalDeatils=userJournalRepo.getUserJournalByJournalUNiqueList(existingUser.getId(), user.getJournal());
					if(getUserJournalDeatils!=null && !getUserJournalDeatils.isEmpty()) {
						String token = UUID.randomUUID().toString();
						forgotPassword.sendMailForPassword(user.getEmail(), token,existingUser.getSalutation() + "." + " " + existingUser.getFirstName() + " " + existingUser.getLastName(),user.getForgetPasswordUrl(), user.getJournal(),existingUser.getId());
						obj.put("message", "Password reset link send");
					}else {
						obj.put("message", "This "+user.getEmail()+ " is not associated with this jorunal!");
					}
				}else {
					obj.put("message", "Your email ("+user.getEmail()+") id is not verified, Please check your email for verification link. ");	
					//obj.put("message", "Verify your email through email verification link sent on "+user.getEmail());	
				}
			} else {
				obj.put("message", "This "+user.getEmail()+ " does not exist!");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

	@PostMapping("resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody User user) {
		log.info("Inside ForgotPasswordRestController resetPassword");
		JSONObject obj = new JSONObject();
		List<User> usersDetailsList = forgotPassword.findUserDetailsByToken(user.getToken());
		if (usersDetailsList!=null && !usersDetailsList.isEmpty()) {
			forgotPassword.saveUpdatedPassword(user.getToken(), user.getPassword());
			obj.put("message", "password has been updated successfully.");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			obj.put("message", "token is expired.");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}
	
	@PostMapping("getResetPasswordLink")
	public ResponseEntity<?> getResetPasswordLink(@RequestBody IDVO idvo) {
		log.info("Inside ForgotPasswordRestController resetPassword");
		JSONObject obj = new JSONObject();
		try {
			String getResetPasswordLink = forgotPassword.getResetPasswordLink(idvo);
			obj.put("data", getResetPasswordLink);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}

}