package com.digi.uniprr.utils;

import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.VO.JournalListVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.UserJournal;

public class UserJournalListTransformationUtils {
	

	
	public static JournalListVO mapJournalModelToJournalListVO(UserJournal journal) {
		JournalListVO vo = new JournalListVO();
		vo.setJournalId(journal.getJournal().getJournalId());
		vo.setRoleId(journal.getRoleId());
		vo.setJournalTitle(journal.getJournal().getJournalTitle());
		vo.setJournalCode(journal.getJournal().getJournalCode());
		vo.setJournalShortTitle(journal.getJournal().getJournalShortTitle());
		vo.setRoleId(journal.getRoleId());
		if (journal.getJournal().getJournalMetadata() != null) {
			vo.setJournalBlindType(journal.getJournal().getJournalMetadata().getJournalBlindType());
			vo.setIsChargingReq(journal.getJournal().getJournalMetadata().getIsChargingRequired());
		}
		
		return vo;
	}
	
	public static List<JournalListVO> mapModelListToVoList(List<UserJournal> journalList) {
		List<JournalListVO> voList = new ArrayList<JournalListVO>();
		for (UserJournal journal : journalList) 
			if(journal.getIsActive() != null && journal.getIsActive().equals(1)) {
			voList.add(mapJournalModelToJournalListVO(journal));
			}
	    return voList;
	}
	
	public static List<JournalListVO> mapModelListToVoListForUser(List<UserJournal> journalList, Integer journalId) {
		List<JournalListVO> voList = new ArrayList<JournalListVO>();
		for (UserJournal journal : journalList) 
			if(journal.getIsActive() != null && journal.getIsActive().equals(1) && journal.getJournalId().equals(journalId)) {
			voList.add(mapJournalModelToJournalListVO(journal));
			}
	    return voList;
	}



}
