package com.digi.uniprr.utils;

import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.VO.ManuscriptTypeJournalVO;
import com.digi.uniprr.model.ManuscriptTypeJournal;

public class ManuscriptTypeJournalTransformationUtils {
	
	public static ManuscriptTypeJournal mapVOToModel(ManuscriptTypeJournalVO vo) {
		ManuscriptTypeJournal mtj = new ManuscriptTypeJournal();
		mtj.setMtjId(vo.getMtjId());
		mtj.setManutypeName(vo.getManutypeName());
		mtj.setManuscriptTypeId(vo.getManuscriptTypeId());
		mtj.setIsActive(vo.getIsActive());
		mtj.setIsAbstract(vo.getIsAbstract());
		mtj.setTrialNumberMandatory(vo.getTrialNumberMandatory());
		return mtj;
	}
	
	public static ManuscriptTypeJournalVO mapModelTOVO(ManuscriptTypeJournal mtj) {
		ManuscriptTypeJournalVO vo = new ManuscriptTypeJournalVO();
		vo.setMtjId(mtj.getMtjId());
		vo.setManutypeName(mtj.getManutypeName());
		vo.setIsActive(mtj.getIsActive());
		vo.setIsAbstract(mtj.getIsAbstract());
		vo.setTrialNumberMandatory(mtj.getTrialNumberMandatory());
		return vo;
	}
	
	public static List<ManuscriptTypeJournal> mapVOListToModelList(List<ManuscriptTypeJournalVO> voList) {
		List<ManuscriptTypeJournal> list = new ArrayList<ManuscriptTypeJournal>();
		for (ManuscriptTypeJournalVO vo : voList) 
			list.add(mapVOToModel(vo));
		
		return list;
	}
	
	public static List<ManuscriptTypeJournalVO> mapModelListToVOList(List<ManuscriptTypeJournal> manuscriptTypeJournalList) {
		List<ManuscriptTypeJournalVO> list = new ArrayList<ManuscriptTypeJournalVO>();
		for(ManuscriptTypeJournal manuscriptTypeJournal : manuscriptTypeJournalList)
			list.add(mapModelTOVO(manuscriptTypeJournal));
		
		return list;
	}
 
}
