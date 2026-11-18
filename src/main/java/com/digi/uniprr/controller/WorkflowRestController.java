package com.digi.uniprr.controller;

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

import com.digi.uniprr.VO.WorkFlowSetUpVO;
import com.digi.uniprr.service.WorkflowRoleService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/workflow")
public class WorkflowRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	WorkflowRoleService workflowRoleService;

	// WorkFlow
	@PostMapping("workFlowSetUpOnMaster")
	public ResponseEntity<?> workFlowSetUpOnMaster(@RequestBody WorkFlowSetUpVO workFlowSetUpVO) {
		log.info("Inside WorkflowRestController workFlowSetUpOnMaster");
		JSONObject obj = new JSONObject();
		try {
			
			 boolean isWorkFlowExist = false;
			 isWorkFlowExist = workflowRoleService.isWorkFlowExist(workFlowSetUpVO);
			if(isWorkFlowExist && workFlowSetUpVO.getWorkFlowId()==null) {
				obj.put("data", "This work flow name is already exist");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}else {
			
			String message = workflowRoleService.workFlowUpdateForJournal(workFlowSetUpVO);
			if(message.equalsIgnoreCase("WorkFlow Updated Successfully")) {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}else {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			}
		} catch (Exception e) {
			obj.put("message", "Error");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@GetMapping("getMasterWorkFlowList")
	public ResponseEntity<?> getMasterWorkFlowList() {
		log.info("Inside WorkflowRestController getMasterWorkFlowList");
		JSONObject obj = new JSONObject();
		try {
			List<WorkFlowSetUpVO> list = workflowRoleService.getMasterWorkFlowList();
			if (list != null && !list.isEmpty()) {
				obj.put("data", list);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			else {
				obj.put("data", "data not found");
				return new ResponseEntity<>(obj, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			obj.put("data", "Error");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@GetMapping("deleteMasterWorkFlow")
	public ResponseEntity<?> deleteMasterWorkFlow(@RequestBody WorkFlowSetUpVO workFlowSetUpVO) {
		log.info("Inside WorkflowRestController deleteMasterWorkFlow");
		JSONObject obj = new JSONObject();
		try {
			String message = workflowRoleService.deleteMasterWorkFlow(workFlowSetUpVO);
			obj.put("data", message);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Error");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

}
