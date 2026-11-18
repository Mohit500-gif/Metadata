package com.digi.uniprr.utils;

import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.VO.JournalListVO;
import com.digi.uniprr.model.Journal;

public class JournalListTransformationUtils {
	
	public static JournalListVO mapJournalModelToJournalListVO(Journal journal) {
		JournalListVO vo = new JournalListVO();
		vo.setJournalId(journal.getJournalId());
		vo.setJournalTitle(journal.getJournalTitle());
		vo.setJournalCode(journal.getJournalCode());
		vo.setIsActive(journal.getIsActive());
		vo.setJournalShortTitle(journal.getJournalShortTitle());
		if (journal.getJournalMetadata() != null) {
			vo.setJournalBlindType(journal.getJournalMetadata().getJournalBlindType());
			vo.setIsChargingReq(journal.getJournalMetadata().getIsChargingRequired());
			vo.setIsSubmissionRequired(journal.getJournalMetadata().getIsSubmissionRequired());
		}
		if(journal.getJournalPolicies()!=null) {
			vo.setOrcIdMandatory(journal.getJournalPolicies().getOrcIdMandatory());
		}
		return vo;
	}
	
	public static List<JournalListVO> mapModelListToVoList(List<Journal> journalList) {
		List<JournalListVO> voList = new ArrayList<JournalListVO>();
		for (Journal journal : journalList) 
			if(journal.getIsActive().equals(1)) {
			voList.add(mapJournalModelToJournalListVO(journal));
			}
	    return voList;
	}
	
	public static List<JournalListVO> mapModelListToVoListForAllJournal(List<Journal> journalList) {
		List<JournalListVO> voList = new ArrayList<JournalListVO>();
		for (Journal journal : journalList) 
			voList.add(mapJournalModelToJournalListVO(journal));
	    return voList;
	}

}
