package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.JournalIssuesPlannerVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.model.MeAvailability;

public interface ManagingEditorService {

	void saveManagingEditorAvailability(MeAvailability meAvailability);

	List<ReviewerVO> getManagingEditorWithAvailability();

	List<MeAvailability> getManagingEditorAvailabilityById(Integer id);

	void deleteManagingEditorAvailabilityById(Integer id);
	
	//Here Journal Issues Planner Setup On Manging Editor
	
	public String  createIssue(JournalIssuesPlannerVO journalIssuesPlannerVO);
	
	public List<JournalIssuesPlannerVO> getIssueList(JournalIssuesPlannerVO journalIssuesPlannerVO);
	
	public String deleteIssue(JournalIssuesPlannerVO journalIssuesPlannerVO);

}
