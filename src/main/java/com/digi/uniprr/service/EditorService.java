package com.digi.uniprr.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.digi.uniprr.VO.ManuscriptIssuesPlannerVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.model.EditorAvailability;

public interface EditorService {

	List<ManuscriptListVO> getManuScriptDataByStatus(ManuscriptListVO  manuscriptListVO);
	
	void updateManuScriptByAuthor(ManuscriptListVO  manuscriptListVO);
	
	public String disableAllEditor(ManuscriptListVO  manuscriptListVO,  HttpServletRequest request);
	
	void saveEditorAvailability(EditorAvailability editorAvailability);
	
	List<EditorAvailability> getEditorAvailabilityById(Integer id);

	void deleteEditorAvailabilityById(Integer id);
	
	List<ReviewerVO> getEditorWithAvailability();
	
	// Here Journal Issues Planner Setup On Editor
	
	public String createIssueForManuscript(ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO);
	
	public List<ManuscriptIssuesPlannerVO> getIssueListForManuscript(ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO);
	
	public String deleteAssginManuscriptONIssue(ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO);
}
