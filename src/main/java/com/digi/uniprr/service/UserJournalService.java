package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.UserJournalVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.UserJournal;

public interface UserJournalService {

	List<UserVO> getListById(UserJournalVO userJournalVO);
	
	List<UserVO> getUserListForJournal(UserJournalVO userJournalVO);
	
	UserJournal getUserJournalByJournal(Integer userId, Integer journalId);
 
	String saveUserJournal(UserJournal userJournal);
	
	String activateUserJournal(UserJournalVO userJournalVO);
	
	List<UserVO> getAssignedEditorListById(UserJournalVO userJournalVO);
}
