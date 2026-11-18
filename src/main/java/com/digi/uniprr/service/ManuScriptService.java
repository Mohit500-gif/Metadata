package com.digi.uniprr.service;

import java.util.Date;
import java.util.List;

import com.digi.uniprr.VO.ArticleInfoAPI;
import com.digi.uniprr.VO.AuthorDashboardCountVO;
import com.digi.uniprr.VO.AuthorDashboardVO;
import com.digi.uniprr.VO.EICDashBoardListVO;
import com.digi.uniprr.VO.EditorDashboardVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuScriptVO;
import com.digi.uniprr.VO.ManuscriptCoAuthorVO;
import com.digi.uniprr.VO.ManuscriptDataApiVo;
import com.digi.uniprr.VO.ManuscriptDetailForJleVO;
import com.digi.uniprr.VO.ManuscriptEditorVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptSimilarityVo;
import com.digi.uniprr.VO.ManuscriptTypeJournalVO;
import com.digi.uniprr.VO.ManuscriptTypeTitleAbstractVO;
import com.digi.uniprr.VO.ManuscriptsDashboardCountVO;
import com.digi.uniprr.VO.ManuscriptsEditorDashboardCountVO;
import com.digi.uniprr.VO.ReviewerDecisionVO;
import com.digi.uniprr.VO.UserJournalManuscriptVo;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.ManscriptScore;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptTypeDetails;
import com.digi.uniprr.model.ManuscriptVisualization;

public interface ManuScriptService {

	public List<ManuScript> getManuScriptList();

	public ManuScript findManuscriptById(int manuscriptId);

	public ManuScript findManuscriptByCode(String manuscriptCode);

	public ManuScript createManuScript(ManuScript manuScript);

	public String updateManuScript(ManuScript manuScript);

	public String deleteManuScriptById(Integer id);

	public boolean isExist(Integer id);

	public List<ManuScript> getManuScriptListById(Integer id);
	
	public ManuScriptVO getManuscriptData(IDVO id);
	
	public ManuScriptVO getManuscriptDataForReviewer(IDVO id);

	public void updateStage(ManuScript manuScript);

	public List<ManuScript> getListByStatusId(Integer manuscriptId);

	public List<ManuScript> trackManuScript(Integer id);

	public List<ManuScript> gettrackManuScriptList(Integer id, Integer journalId, Date date);

	public List<ManuScript> gettrackManuScriptListDate(String submissionDateTo, String submissionDateFrom);

	public List<ManuScript> gettrackManuScriptListTitle(String manuscriptTitle);

	public List<ManuScript> gettrackManuScriptListCode(String manuscriptCode);

	public List<ManuScript> gettrackManuScriptJtitle(String journalTitle);

	public List<ManuScript> getAllManuScriptRecord();

// Reviewer Dashboard
	public List<ManuScript> reviewerInvitationList(Integer reviewerId);

	public List<ManuScript> reviewerReviewList(Integer reviewerId);

	public List<ManuScript> reviewerRevisionList(Integer reviewerId);

	public List<ManuScript> reviewerCompletedList(Integer reviewerId);

	public void reviewerAccept(IDVO vo);

	public void reviewerReject(IDVO vo);

//Author Dashboard
	public List<AuthorDashboardVO> authorTask(Integer authorId);

	public List<AuthorDashboardVO> authorUnderReview(Integer authorId);

	public List<AuthorDashboardVO> authorManuscriptAccepted(Integer authorId);

	public List<AuthorDashboardVO> authorManuscriptRejected(Integer authorId);

//Editor Dashboard
	public List<ManuScript> getEditorSubmitted(Integer id);

	public List<ManuScript> getEditorReSubmitted(Integer groupId);

	public List<ManuScriptReviewer> getEditorSentForReview(Integer groupId);

	public List<ManuScriptReviewer> getEditorAllList(Integer groupId);

//
	public String updateManuScriptEditor(ManuscriptEditorVO manuscriptEditorVO, JournalManuscriptTypeCharge journalManuscriptTypeCharge);

	public List<ManuScript> getUniqueManuScript(Integer journalId);

	public List<ManuscriptListVO> getManuscriptByStatus(ManuscriptListVO manuscriptVO);
	
	public List<ManuscriptListVO> getManuscriptByJournalAndStatus(ManuscriptListVO manuscriptVO);

	public List<ManuscriptListVO> getManuscriptByStatusForEIC(ManuscriptListVO manuscriptVO);

	public List<EditorDashboardVO> getManuscriptByData(UserJournalManuscriptVo userJournalManuscriptVo);
	
	public List<EditorDashboardVO> getManuscriptForAuthor(UserJournalManuscriptVo userJournalManuscriptVo);
	
	public Integer getManuscriptAuthorCount(UserJournalManuscriptVo userJournalManuscriptVo);
	
	public Integer getManuscriptAuthorSearchCount(UserJournalManuscriptVo userJournalManuscriptVo);

	public List<EditorDashboardVO> getAssignedManuscriptData(UserJournalManuscriptVo userJournalManuscriptVo);

	public void saveManuscriptDetails(ManuScript manuscriptData);

	public List<ManuscriptTypeJournalVO> getManuscriptTypeJournalListByJournalId(Integer manuscriptId);

	/*********** Manuscript Author Form save **********/
	public ManuscriptTypeTitleAbstractVO getManuscriptTypeAbstractDetails(IDVO vo);

	public void saveTypeTitleAbstractDetails(ManuscriptTypeTitleAbstractVO vo, ManuScript manuscript);

	public List<ManuscriptCoAuthorVO> getCoAuthorList(Integer manuscriptId);

	public String saveCoAuthorOrderList(List<ManuscriptCoAuthorVO> voList);

	public ManuscriptTypeDetails getManuscriptTypeDetailsByManuscriptId(Integer manuscriptId);

	public void saveWordCountDetails(ManuScriptVO vo);

	public void deleteCoAuthor(Integer authorId);

	public String submitManuscript(ManuScriptVO vo, JournalManuscriptTypeCharge journalManuscriptTypeCharge);
	
	public String sendEmailToCoAuthorOnManuscript(ManuScriptVO vo);

	public List<ReviewerDecisionVO> getReviewerDecision(UserJournalManuscriptVo userJournalManuscriptVo);

	public void resubmissionSaveTypeTitleAbstract(ManuscriptTypeTitleAbstractVO vo);

	public void resubmissionSaveWordCountDetails(ManuScriptVO vo);

	/*************************
	 * Manuscript Score Card
	 *********************************/

	public ManscriptScore getManuScriptScoreCardDeatils(IDVO idvo);

	public boolean titleIsExist(ManuscriptTypeTitleAbstractVO vo);

	public ManuscriptVisualization getManuScriptVisualizationDeatils(IDVO idvo);

	/********************* Manuscript Status ************/
	public List<ManuscriptListVO> getManuscriptStatusListForAuthor(IDVO idvo);

	public void deleteCoAuthorList(Integer id);

	public ManuscriptListVO getManuscriptDataIdAndVersion(Integer mansuscriptId, Integer manuscriptVersion, String comment);

	public boolean craeatedXmlFileCreate(ManuscriptListVO data, String operation);
	
	public boolean submitForAFP(ManuscriptListVO data, String operation);

	public boolean craeatedZipFileCreate(ManuscriptListVO data);

	public ManuscriptListVO getManuscriptDataIdAndVersionZipfile(Integer mansuscriptId, Integer manuscriptVersion);

	public void updateUrl(ManuScript manuscript);

	public String coAuthorSendMail(ManuScriptVO vo);

	public String withdrawnAllReviewerInvitation(ManuscriptEditorVO manuscriptEditorVO);

	public List<ManuscriptListVO> getManuscriptByType(IDVO id);

	public List<ManuscriptListVO> getAssignedEditorbyType(IDVO id);

	public List<ManuscriptListVO> getReviewedByType(IDVO id);

	public List<ManuscriptListVO> getNoResponseByType(IDVO id);

	public List<ManuscriptListVO> getInReviewByType(IDVO id);

	public List<ManuscriptListVO> getFinalDecisionByType(IDVO id);

	public String insertDataThroughUnitouch(ArticleInfoAPI articleInfoAPI);

	public ManuscriptDetailForJleVO getManuscriptDetailForJle(IDVO id);

	public Boolean pushDataForJle(ManuscriptDetailForJleVO manuscriptDetailForJle);

	public boolean craeatedXmlFileCreateForJle(ManuscriptListVO manuscriptListVO, String operation);

	public ManuScript updateChargeComment(IDVO id);

	public List<ManuscriptListVO> getAllPayedManuscript(ManuscriptListVO manuscriptVO);

//	public ManuscriptTypeJournalVO getManuscriptCharge(Integer id, String manuscriptType);

	public ManuscriptTypeJournalVO getManuscriptCharge(Integer id, String manuscriptType, Integer manuscriptId);

	public JournalManuscriptTypeCharge getManuscriptChargeByManuscriptId(Integer id, Integer journalId );
	
	public String saveExitManuscript(ManuScriptVO vo);
	
	public String checkDevelopingCountryUser(Integer userId);
	
	public List<ManuscriptListVO> getManuscriptByStatusForEditorTotalManuscript(ManuscriptListVO manuscriptVO);
		
	public ManuscriptsDashboardCountVO getAllManuscriptsDashboardCount(EICDashBoardListVO eicDashboardVO);
	
	public ManuscriptsEditorDashboardCountVO getAllManuscriptsEditorDashboardCount(EICDashBoardListVO eicDashboardVO);
	
	public String rejectForWithdrwan(ManuscriptEditorVO manuscriptEditorVO, ManuScript getManuScriptData );
	
	public String undoForRejectedManuscript(ManuscriptEditorVO manuscriptEditorVO, ManuScript getManuScriptData );
	
	public Integer getWithdrawnCount(IDVO id);

	public void updateUrlwithoutfile(ManuScript manuscript);
	
	public List<ManuscriptListVO> getManuscriptDataForAFP();
	
	void updateManuscriptDetails(ManuscriptDataApiVo manuscriptDataApiVo);
	
	public void checkManuscriptForAFP();

	public List<ManuscriptTypeJournalVO> getArticleTypeListByJournalId(Integer journalId);

    void updateArticleNumberFlag(ManuscriptTypeJournalVO vo);

	ManuScript findManuscriptByCommentary(String commentaryMSCode);

	ManuScript findManuscriptByAuthorResponse(String authorResponseMSCode);

	List<ManuScript> findManuscriptByLTE(String lteMSCode);

	List<ManuScript> findAuthorResponseByLTE(String lteMSCode);
	
	AuthorDashboardCountVO getManuscriptDashboardCountForAuthor(UserJournalManuscriptVo userJournalManuscriptVo);

	void updateStatus(ManuScript manuscript);

	int getManuscriptCountByTitleAndJournals(ManuscriptTypeTitleAbstractVO manuScriptVO); 
	
	ManuscriptSimilarityVo checkTitleSimilarity(ManuscriptTypeTitleAbstractVO vo);
	   
}