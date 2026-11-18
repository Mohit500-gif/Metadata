package com.digi.uniprr.controller;

import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.utils.AppConstants;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.model.Announcements;
import com.digi.uniprr.service.AnnouncementService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/announcement")
public class AnnouncementRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static String SUCCESS = "success";
	private static String FAILURE = "failed";
	
	@Autowired
	AnnouncementService announcementService; 
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getList")
	public ResponseEntity<?> getAnnouncementDataList(@RequestBody Announcements announcementsDto) {
		log.info("====== Calling getAnnouncementDataList ======");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST +" "+
				AppConstants.REQUEST_URI+" :: /announcement/getList"+
				AppConstants.REQUEST_BODY+" ::"+announcementsDto.toString());

		JSONObject obj = new JSONObject();
		List<Announcements> announcementList = new ArrayList<Announcements>();
		try {
			announcementList = announcementService.getAnnouncements(announcementsDto);
			obj.put("status", SUCCESS);
		} catch (Exception e) {
			log.error("Exception in getAnnouncementDataList" + e);
			obj.put("status", FAILURE);
		}
		obj.put("data", announcementList);
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping({ "/create", "/update" })
	public ResponseEntity<?> createAndUpdateAnnouncements(@RequestBody Announcements announcementsDto) {
		log.info("====== Calling createAndUpdateAnnouncements ======");

		JSONObject obj = new JSONObject();
		Announcements announcement = new Announcements();
		try {
			announcement = announcementService.createAndUpdateAnnouncements(announcementsDto);
			if(announcement != null) {
				obj.put("status", SUCCESS);
			} else {
				obj.put("status", FAILURE);
			}
		} catch (Exception e) {
			log.error("Exception in createAndUpdateAnnouncements" + e);
			obj.put("status", FAILURE);
		}
		obj.put("data", announcement);
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/inActive")
	public ResponseEntity<?> updateAnnouncementsForInActive(@RequestBody Announcements announcementsDto) {
		log.info("====== Calling updateAnnouncementsForInActive ======");

		JSONObject obj = new JSONObject();
		try {
			announcementService.updateAnnouncementsForInActive(announcementsDto);
			obj.put("status", SUCCESS);
		} catch (Exception e) {
			log.error("Exception in updateAnnouncementsForInActive" + e);
			obj.put("status", FAILURE);
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteAnnouncement(@PathVariable(value = "id") int id) {
		log.info("====== Calling deleteAnnouncement ======");

		JSONObject obj = new JSONObject();
		try {
			announcementService.deleteAnnouncement(id);
			obj.put("status", SUCCESS);
		} catch (Exception e) {
			log.error("Exception in deleteAnnouncement" + e);
			obj.put("status", FAILURE);
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}
}
