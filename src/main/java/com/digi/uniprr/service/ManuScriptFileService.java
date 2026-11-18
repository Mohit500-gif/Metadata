package com.digi.uniprr.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.digi.uniprr.VO.AuthorProofFileVo;
import com.digi.uniprr.VO.FunderDetailsVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptCommentVO;
import com.digi.uniprr.VO.ManuscriptFileVO;
import com.digi.uniprr.VO.ResubmissionManuscriptFileVo;
import com.digi.uniprr.model.ManuScriptFile;

public interface ManuScriptFileService 
{
    public String createManuScriptFile(ManuScriptFile manuScriptFile);
    
    public List<ManuScriptFile> getManuScriptFileDetails();
    
    public String updateManuScriptFileDetails(ManuScriptFile manuScriptFile);
    
    public String deleteManuScriptFileById(Integer id);
    
    public boolean isIdExist(Integer id);

	public List<ManuScriptFile> getFileDetailsById(ManuScriptFile manuScriptFile);

	public ManuScriptFile saveFile(ManuScriptFile manuScriptFile);
	
	void saveAll(List<ManuScriptFile> manuScriptFiles);
	
	public ManuScriptFile getListByManuscriptId(Integer id);

	public ManuScriptFile getManuScriptFile(ManuScriptFile manuscriptFile);
	
	public List<ManuscriptFileVO> getManuscriptByactive(Integer manuscriptId,Integer manuscriptVersion);

	public String deleteManuscriptFile(Integer manuscriptFileId,HttpServletRequest request);
	
	public String downloadFileByManuScriptFileID(Integer manuscriptFileId);
	
	public String updateManuscriptFileUpload(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo,HttpServletRequest request);
	
	public List<FunderDetailsVO> getResearchFunders(FunderDetailsVO funderDetailsVO);
	
	public void saveManuscriptFunders(List<FunderDetailsVO> funderDetailsVO);
	
	public List<FunderDetailsVO> getManuscriptFunders(FunderDetailsVO funderDetailsVO);
	
	/****************Author Feedback Form Submission****************/
	public String authorSubmissionFeedBack(IDVO Vo);

	public void copyAllFile(IDVO id);

	public Map<String, AuthorProofFileVo> readFileFromFolder(String folderPath, String stepName);

	ResponseEntity<Resource> downloadProofFile(String filePath);
	
	ResponseEntity<Resource> downloadTechnicalFile(String filePath);
	
	public List<ManuscriptFileVO> getMainDocumentByIdVersion(IDVO id);
	
	public int yearlyPcount(Integer journalId, Integer year);
	
	public int monthlyPcount(Integer journalId, Integer month);

	public List<ManuscriptCommentVO> getCommentsByManuscriptId(Integer manuscriptId);
	public List<ManuscriptFileVO> getManuscriptByactiveAlt(Integer manuscriptId, Integer manuscriptVersion);

	ResponseEntity<Resource> downloadFile(Integer manuscriptFileId,HttpServletRequest request);

	ResponseEntity<Resource> previewFile(Integer manuscriptFileId,HttpServletRequest request);

	
}
