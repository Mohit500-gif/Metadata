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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.AllCheckListVO;
import com.digi.uniprr.VO.CheckListMasterVO;
import com.digi.uniprr.VO.JournalBasedCheckListVO;
import com.digi.uniprr.model.CheckListMaster;
import com.digi.uniprr.service.CheckListMasterService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/checkListMaster")
public class CheckListMasterController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CheckListMasterService checkListMasterService;

	@RequestMapping(value = "/addSimpleCheckListMaster", method = RequestMethod.POST)
	public ResponseEntity<?> addSimpleCheckListMaster(@RequestBody CheckListMaster checkListMaster) {
		log.info("Inside In checkListMaster addSimpleCheckListMaster");
		JSONObject obj = new JSONObject();
		try {
			checkListMasterService.addChecklistDeatils(checkListMaster);
			obj.put("Added", "Added Data Sucessfully.");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("Addeded", "Does not exist table");
			return new ResponseEntity<Object>(obj, HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping("addCheckListMaster")
	public ResponseEntity<?> addCheckListMaster(@RequestBody CheckListMasterVO checkListMasterVO) {
		log.info("Inside In checkListMaster addCheckListMaster");
		JSONObject obj = new JSONObject();
		try {
			if (checkListMasterVO.getJournalId() != null) {
				boolean isCheckListExistInJournalLevel = false;
				isCheckListExistInJournalLevel = checkListMasterService
						.isCheckListExistInJournalLevel(checkListMasterVO);
				if (isCheckListExistInJournalLevel && checkListMasterVO.getChkid()==null) {
					obj.put("message", "Checklist already exists on journal level");
				} else {
					return new ResponseEntity<>(checkListMasterService.saveCheckListMaster(checkListMasterVO),
							HttpStatus.OK);
				}
			} else {
				boolean isCheckListExistInMaster = false;
				isCheckListExistInMaster = checkListMasterService.isCheckListExistInMaster(checkListMasterVO);
				if (isCheckListExistInMaster && checkListMasterVO.getChkid()==null) {
					obj.put("message", "Checklist already exists on matser level");
				} else {
					return new ResponseEntity<>(checkListMasterService.saveCheckListMaster(checkListMasterVO),
							HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			obj.put("message", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/listdetails", method = RequestMethod.GET)
	public ResponseEntity<?> checkMasterList() {
		log.info("Inside In checkListMaster checkMasterList");
		JSONObject obj = new JSONObject();
		AllCheckListVO checkvoList = checkListMasterService.getCheckListByRole();
		return new ResponseEntity<>(checkvoList, HttpStatus.OK);
	}

	@PostMapping("listCheckByRole")
	public ResponseEntity<?> getCheckByRole(@RequestParam Integer journalId) {
		log.info("Inside In checkListMaster getCheckByRole");
		JSONObject obj = new JSONObject();
		try {
			JournalBasedCheckListVO journalBasedCheckList = checkListMasterService.getCheckList(journalId);
			return new ResponseEntity<>(journalBasedCheckList, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping("listCheckByRoleAll")
	public ResponseEntity<?> getCheckByRoleAll(@RequestParam Integer journalId, @RequestParam String chkCategory) {
		log.info("Inside In checkListMaster getCheckByRoleAll");
		JSONObject obj = new JSONObject();
		List<CheckListMaster> checkList = new ArrayList<>();
		try {
			if (checkListMasterService.isExist(journalId, chkCategory))
				checkList = checkListMasterService.getCheckListAll(journalId, chkCategory);
			obj.put("data", checkList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/deletechecklist", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteManuScript(@RequestBody CheckListMaster checkListMaster) {
		log.info("Inside In checkListMaster deleteManuScript");
		JSONObject obj = new JSONObject();
		if (checkListMasterService.isExist(checkListMaster.getChkid())) {
			checkListMasterService.deletechecklistById(checkListMaster.getChkid());
			obj.put("message", "Checklist Item deleted");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
		obj.put("message", "Id does not exist");
		return new ResponseEntity<Object>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/sequenceBasedCheckListSave", method = RequestMethod.POST)
	public ResponseEntity<Object> sequenceBasedCheckListSave(@RequestBody List<CheckListMasterVO> checkListMasterVO) {
		log.info("Inside In checkListMaster sequenceBasedCheckListSave");
		JSONObject obj = new JSONObject();
		try {
			String message = checkListMasterService.sequenceBasedCheckListSave(checkListMasterVO);
			obj.put("date", message);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
		}
	}

}
