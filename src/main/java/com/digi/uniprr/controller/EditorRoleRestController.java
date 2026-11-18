package com.digi.uniprr.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EditorRoleVo;
import com.digi.uniprr.model.EditorRole;
import com.digi.uniprr.service.EditorRoleService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("editorRole")
public class EditorRoleRestController {
	
	@Autowired
	EditorRoleService editorRoleService;
	
	JSONObject obj = new JSONObject();
	
	@GetMapping("getEditorRoleList")
	public ResponseEntity<?> getEditorRoleList(){
		List<EditorRoleVo> editorRoleData = editorRoleService.getEditorRoleList();
		
		if(editorRoleData!=null && !editorRoleData.isEmpty()) {
			obj.put("data", editorRoleData);
		}else {
			obj.put("data", "Data Not Found");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
}
