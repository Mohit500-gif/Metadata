package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.JournalManuscriptTypeVo;
import com.digi.uniprr.model.ManuscriptType;
import com.digi.uniprr.model.ManuscriptTypeJournal;

public interface ManuscriptTypeService {
	
	List<ManuscriptType> getManuscriptTypeList();
	
	void saveManuscriptType(ManuscriptType type);
	
	void deleteManuscriptType(Integer id);
	
	ManuscriptType getManuscriptTypeById(Integer id);
	
	boolean isManuscriptTypeExist(ManuscriptType manuscriptType);
	
	void saveDataInDb(String manusciptType);
	
	ManuscriptType getManuscriptTypeByName(String name);

	boolean isJournalManuscriptTypeExist(JournalManuscriptTypeVo manuscriptType);

	void saveJournalManuscriptType(JournalManuscriptTypeVo manuscriptType);

	void deleteJournalManuscriptType(JournalManuscriptTypeVo manuscriptType);

	List<ManuscriptTypeJournal> getJournalManuscriptType(JournalManuscriptTypeVo manuscriptType);

	List<ManuscriptType> getListByJournal(Integer journalId);

	List<ManuscriptType> getListByJournalId(Integer journalId);

}
