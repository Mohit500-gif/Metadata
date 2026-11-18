package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalFileTypeVO;
import com.digi.uniprr.model.JournalFileType;

public interface JournalFileTypeService {

	public List<JournalFileType> getDetails();
	
	public List<JournalFileTypeVO> getDataByJournal(IDVO id);

}
