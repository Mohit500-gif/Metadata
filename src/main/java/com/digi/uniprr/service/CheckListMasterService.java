package com.digi.uniprr.service;

import java.util.List;

import org.json.simple.JSONObject;

import com.digi.uniprr.VO.AllCheckListVO;
import com.digi.uniprr.VO.CheckListMasterVO;
import com.digi.uniprr.VO.JournalBasedCheckListVO;
import com.digi.uniprr.model.CheckListMaster;

public interface CheckListMasterService {
	
	public String addChecklistDeatils(CheckListMaster checkListMaster);
	
	JSONObject saveCheckListMaster(CheckListMasterVO checkListMasterVO);

	AllCheckListVO getCheckListByRole();
	
	JournalBasedCheckListVO getCheckList(Integer journalId);
	
	List<CheckListMaster> getCheckListAll(Integer journalId, String chkCategory);

	boolean isExist(Integer journalId, String chkCategory);
	
	public String deletechecklistById(Integer chkid);
	
	public boolean isExist(Integer chkid);
	
	public String sequenceBasedCheckListSave(List<CheckListMasterVO> checkListMasterVO);
	
	public boolean isCheckListExistInMaster(CheckListMasterVO checkListMasterVO);
	
	public boolean isCheckListExistInJournalLevel(CheckListMasterVO checkListMasterVO);
	
}
