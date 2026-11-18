package com.digi.uniprr.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.AssignEditorVO;
import com.digi.uniprr.VO.AuthoritiesHistoryVo;

public interface AssignEditerService {
	
	public void assignUser(AssignEditorVO assignEditorVO,MultipartFile file, HttpServletRequest request) throws Exception;
	
	public void create(AssignEditorVO assignEditorVO);
	
	public String authoritiesHistory(AssignEditorVO assignEditorVO);

}
