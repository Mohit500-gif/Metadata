package com.digi.uniprr.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.digi.uniprr.VO.FailedIthenicateVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.PlagiarismListVO;
import com.digi.uniprr.VO.PlagiarismVO;
import com.digi.uniprr.model.PlagiarismCheck;

public interface CheckPlagiarismService {

	String uploadFile (IDVO id);

	String activePlagiarism(Integer id);

	List<PlagiarismCheck> getReport();
	
	List<PlagiarismCheck> getInActiveData();
	
	PlagiarismCheck checkPlagiarismReport(IDVO id);
	
	String checkCopyscape(IDVO id);

	boolean checkIfExist(IDVO id);

	List<ManuscriptListVO> getPlagiarismReport(IDVO id);
	
	List<PlagiarismListVO> getPlagiarism(PlagiarismVO pvo);
	
	List<PlagiarismCheck> getListForURLGeneration();
	
	void deletePalagrismById(Integer id);
	
	void insertFailedPalagarism(FailedIthenicateVo failedIthenicateVo);
	
	List<PlagiarismCheck> getInactivedata();
	String uploadFileForInProgress (IDVO id);

	Page<ManuscriptListVO> getPlagiarismReportPaged(IDVO id, Pageable pageable);
	
	List<ManuscriptListVO> getPlagiarismReportAll(IDVO id);



}
