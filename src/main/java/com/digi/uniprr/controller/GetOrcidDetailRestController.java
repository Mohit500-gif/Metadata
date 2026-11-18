package com.digi.uniprr.controller;

import java.util.List;

import com.digi.uniprr.VO.UserDetailsVO;
import com.digi.uniprr.config.HttpClientGenerator;
import com.digi.uniprr.utils.AppConstants;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
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

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.service.OrcidService;
import com.digi.uniprr.service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/orcid")
public class GetOrcidDetailRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	OrcidService orcidService;

	@Autowired
	UserService userService;

	@RequestMapping(value = "/orcidDetail", method = RequestMethod.POST)
	public ResponseEntity<?> getOrcidDetail(@RequestBody IDVO id) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /orcid/orcidDetail"+
				AppConstants.REQUEST_BODY+" :: "+id.toString());
		JSONObject obj = new JSONObject();
		log.info("Inside GetOrcidDetailRestController getOrcidDetail");
		String data = orcidService.getOrcidDetail(id.getName());
		if (!data.equalsIgnoreCase("Something went wrong")) {
			log.info("data found regarding orcid id" + id);
			return new ResponseEntity<>(data, HttpStatus.OK);
		}
		log.info("data not found regarding orcid id" + id);
		obj.put("data", "Something went wrong");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

//	@RequestMapping(value = "/getOrcidID", method = RequestMethod.POST)
//	public ResponseEntity<?> getOrcidID(@RequestBody IDVO id) {
//		JSONObject obj = new JSONObject();
//		log.info("Inside GetOrcidDetailRestController getOrcidDetail");
//		try {
//			log.info("Name in id:: "+id.getName()+", Read Orcid in id:: "+id.getReadOrcid());
//			String data = orcidService.getOrcidID(id.getName(), id.getReadOrcid());
//			if (!data.equalsIgnoreCase("Something went wrong")) {
////				obj.put("orcidData", data);
////				return new ResponseEntity<>(obj, HttpStatus.OK);
//
//				log.info("data found regarding orcid id" + id);
//				String test[] = data.split(":");
//				String count = test[test.length - 1];
//				count = count.replace("}", "").replace("'", "").replace("\"", "").replace("\"", "");
//				log.debug(count);
//				UserVO userDetails = userService.getUserByOrcidId(count);
//				UserVO proxyEditorData=new UserVO();
//				if(userDetails !=null){
//					proxyEditorData=userService.getUserByEmailId(userDetails.getEmail());
//					log.info("orcid:::: email :: "+userDetails.getEmail()+", userDetails:: "+proxyEditorData);
//				}
//				else {
//					userDetails = new UserVO();
//					userDetails.setEmail("");
//				}
//				log.info("orcid:::: for count :: "+count+", userDetails:: "+userDetails);
//
//				// Getting the auth token for this user
//				HttpPost httpRequest = new HttpPost(AppConstants.APP_LOGIN_URL);
//				httpRequest.setHeader(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
//				httpRequest.setHeader(AppConstants.AUTHORIZATION, AppConstants.BASIC_AUTH);
//				String payload = "{\"msisdn\":\"" + userDetails.getEmail() + "\",\"deviceId\":\"device\"}";
//				httpRequest.setEntity(new StringEntity(payload));
//				HttpResponse httpResponse = HttpClientGenerator.httpClient.execute(httpRequest);
//				HttpEntity httpEntity = httpResponse.getEntity();
//				String jsonResponse = EntityUtils.toString(httpEntity);
//				String accessToken = String.valueOf(new javaxt.json.JSONObject(jsonResponse).get("object"));
//				log.info("orcid:::: for payload :: "+payload+", jsonResponse:: "+jsonResponse);
//				if (userDetails.getEmail() != "") {
//					if (accessToken != null && !accessToken.isEmpty()) {
//						userDetails.setAccessToken(accessToken);
//					}
//					obj.put("userData", userDetails);
//					if (proxyEditorData!=null)
//					{
//						obj.put("proxyEditorData",proxyEditorData);
//					}
////					obj.put("orcidData", data);
//					return new ResponseEntity<>(obj, HttpStatus.OK);
//				} else {
//					obj.put("orcidData", data);
//					return new ResponseEntity<>(obj, HttpStatus.OK);
//				}
//
//			} else {
//				log.info("data not found regarding orcid id" + id);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			log.info("Getting exception in orcid id ::::: " + ex.getMessage());
//			obj.put("data", "Something went wrong");
//
//		}
//		return new ResponseEntity<>(obj, HttpStatus.OK);
//	}
	
	
	@RequestMapping(value = "/getOrcidID", method = RequestMethod.POST)
	public ResponseEntity<?> getOrcidID(@RequestBody IDVO id) {
	    JSONObject obj = new JSONObject();
	    log.info("Inside GetOrcidDetailRestController getOrcidDetail");
	    try {
	        log.info("Name in id:: " + id.getName() + ", Read Orcid in id:: " + id.getReadOrcid());
	        String data = orcidService.getOrcidID(id.getName(), id.getReadOrcid());
	        if (!data.equalsIgnoreCase("Something went wrong")) {

	        	// Step 1: Parse JSON
	        	org.json.JSONObject json = new org.json.JSONObject(data);

	        	// Step 2: Get ORCID safely
	        	String orcid = json.optString("orcid");

	        	if (orcid == null || orcid.isEmpty()) {
	        	    log.error("❌ ORCID not found in response");
	        	    obj.put("data", "Something went wrong");
	        	    return new ResponseEntity<>(obj, HttpStatus.OK);
	        	}


	        	List<UserVO> userDetailsList = userService.getUsersByOrcidId(orcid);


	            if (userDetailsList != null && !userDetailsList.isEmpty()) {
	                if (userDetailsList.size() == 1) {
	                    // ---------- Single user (existing flow) ----------
	                    UserVO userDetails = userDetailsList.get(0);
	                    UserVO proxyEditorData = userService.getUserByEmailId(userDetails.getEmail());

	                    // Get access token (same as before)
	                    String accessToken = getAccessTokenForEmail(userDetails.getEmail());
	                    if (accessToken != null && !accessToken.isEmpty()) {
	                        userDetails.setAccessToken(accessToken);
	                    }

	                    obj.put("userData", userDetails);
	                    if (proxyEditorData != null) {
	                        obj.put("proxyEditorData", proxyEditorData);
	                    }
	                    return new ResponseEntity<>(obj, HttpStatus.OK);

	                } else {
	                    // ---------- Multiple users ----------
	                    for (UserVO u : userDetailsList) {
	                        String accessToken = getAccessTokenForEmail(u.getEmail());
	                        if (accessToken != null && !accessToken.isEmpty()) {
	                            u.setAccessToken(accessToken);
	                        }
	                    }
	                    obj.put("userDataList", userDetailsList);
	                    return new ResponseEntity<>(obj, HttpStatus.OK);
	                }
	            } else {
	                // No users mapped → return ORCID data only
	                obj.put("orcidData", data);
	                return new ResponseEntity<>(obj, HttpStatus.OK);
	            }

	        } else {
	            log.info("data not found regarding orcid id" + id);
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        log.info("Getting exception in orcid id ::::: " + ex.getMessage());
	        obj.put("data", "Something went wrong");
	    }
	    return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	/**
	 * 🔹 Helper method: Generate accessToken (same logic as before).
	 */
	private String getAccessTokenForEmail(String email) {
	    try {
	        HttpPost httpRequest = new HttpPost(AppConstants.APP_LOGIN_URL);
	        httpRequest.setHeader(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
	        httpRequest.setHeader(AppConstants.AUTHORIZATION, AppConstants.BASIC_AUTH);
	        String payload = "{\"msisdn\":\"" + email + "\",\"deviceId\":\"device\"}";
	        httpRequest.setEntity(new StringEntity(payload));

	        HttpResponse httpResponse = HttpClientGenerator.httpClient.execute(httpRequest);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        String jsonResponse = EntityUtils.toString(httpEntity);

	        String accessToken = String.valueOf(new javaxt.json.JSONObject(jsonResponse).get("object"));
	        log.info("Generated accessToken for email: " + email);
	        return accessToken;
	    } catch (Exception e) {
	        log.error("Error generating accessToken for email " + email + ": " + e.getMessage());
	        return null;
	    }
	}


	@RequestMapping(value = "/rolesByOrcid", method = RequestMethod.POST)
	public ResponseEntity<?> getRolesByOrcid(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		log.info("Inside GetOrcidDetailRestController getRolesByOrcid");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /orcid/rolesByOrcid"+
				AppConstants.REQUEST_BODY+" :: "+id.toString());
		List<Role> data = orcidService.getRolesByOrcid(id.getUserId(), id.getJournalId());
		
		if (data.size() > 0) {
			obj.put("data", data);
			log.info("data found regarding orcid id" + id.getEmail());
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		log.info("data not found regarding orcid id" + id.getEmail());
		obj.put("data", "Something went wrong");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
}
