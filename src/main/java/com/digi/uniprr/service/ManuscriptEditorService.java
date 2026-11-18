package com.digi.uniprr.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuScriptEditorCommonVO;
import com.digi.uniprr.VO.ManuscriptEditorVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.SendBackToAuthorVo;
import com.digi.uniprr.VO.UserJournalManuscriptVo;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptEditor;

public interface ManuscriptEditorService {

	public void createManuscriptEditor(ManuscriptEditor manuscriptEditor);

	public List<ManuscriptEditor> getManuscriptEditor(UserJournalManuscriptVo userJournalManuscriptVo);

	public List<ManuscriptEditor> getManuscriptEditorListById(UserJournalManuscriptVo userJournalManuscriptVo);

	public void saveManuScriptReviewerDetails(ManuscriptEditor manuscriptEditor);

	public void updateEditorDetails(ManuscriptListVO manuscriptListVO);

	public ManuscriptEditor findManuscriptEditorDetails(Integer manuScriptId, Integer manuscriptVersion,
			Integer journalId, Integer userId, Integer assignToId);
	
	public String addAllDetailsMultipleTableRCC(ManuScriptEditorCommonVO manuScriptEditorCommonVO);

	public void addsendBackToAuthor(ManuScriptEditorCommonVO manuScriptEditorCommonVO);

	public List<ManuscriptListVO> getEditorRecord(ManuscriptListVO manuscriptListVO);

	public String updateComment(ManuscriptEditorVO manuscriptEditor);

	public List<ManuscriptListVO> getAssignedEditor(IDVO id);

	public String uploadSendBackToAuthor(SendBackToAuthorVo sendBackToAuthorVo,HttpServletRequest request);

	public String uploadFile(SendBackToAuthorVo sendBackToAuthorVo, HttpServletRequest request);
}
