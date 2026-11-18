package com.digi.uniprr.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.digi.uniprr.VO.*;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;

public interface ManuScriptReviewerService {

    public String addReviewerDeatils(ManuScriptReviewer manuScriptReviewer);

    public Integer getInvitedreviewerCount(ManuScriptReviewerVO manuScriptReviewerVo);

    String updateReviewerDeatils(ManuScriptReviewerVO manuScriptReviewerVo);

    public boolean isExist(Integer manuscriptId, Integer reviewerId, Integer manuscriptVersion);

    JSONObject saveManuScriptReviewer(ManuScriptReviewerVO manuScriptReviewerVo);

    String addAllDetailsMultipleTableRCC(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    public List<ManuScriptReviewer> getManuscriptReviewer(UserJournalManuscriptVo userJournalManuscriptVo);

    public List<ManuScriptReviewer> getManuscriptReviewerListById(UserJournalManuscriptVo userJournalManuscriptVo);

    public void saveManuScriptReviewerDetails(ManuScriptReviewer manuscriptReviewer);

    public List<ManuScriptReviewerCommonVO> getReviewerByManuScriptIdAndVersion(Integer manuscriptId, Integer manuscriptVersion);

    public List<ReviewerAvailabilityStatusVO> getReviewerDataById(CommonVO id);

    public List<ManuScriptReviewerCommonVO> getReviewerDataByManuscriptVersion(Integer manuscriptId, Integer manuscriptVersion,
                                                                               Integer journalId);

    public List<AuthorSuggestedReviewerVo> authorSuggestedReviewer(Integer manuscriptId, Integer manuscriptVersion,
                                                                   Integer journalId);

    public List<AuthorSuggestedReviewerVo> authorSuggestedReviewerPagination(Integer manuscriptId, Integer manuscriptVersion,
                                                                             Integer journalId, Integer startIndex, Integer limit);

    public Integer authorSuggestedReviewerCount(Integer manuscriptId,
                                                Integer journalId);

    public List<ManuScriptReviewerCommonVO> getReviewerDatabaseSuggested(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    public Pair<Integer, List<ManuScriptReviewerCommonVO>> getReviewerGlobalSearch(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    public List<ManuScriptReviewerCommonVO> getAssignedReviewer(Integer manuscriptId, Integer manuscriptVersion,
                                                                Integer journalId);

    public List<ManuScriptReviewerCommonVO> getAssignedReviewerPagination(Integer manuscriptId, Integer manuscriptVersion,
                                                                          Integer journalId, Integer startIndex, Integer limit);

    public Integer getAssignedReviewerCount(Integer manuscriptId, Integer manuscriptVersion,
                                            Integer journalId);

    public List<ManuscriptListVO> getReviewedManuscript(IDVO id);

    public List<ManuscriptListVO> getNoResponseManuscript(IDVO id);

    public List<ManuscriptListVO> getCommentaryData(Integer journalId);

    public void updateInvitation(IDVO id);

    public List<ManuscriptListVO> getCommentaryInvitation(Integer journalId, Integer userId);

    public ManuscriptListVO getReviewerStatistics(Integer journalId, Integer userId);

    public List<ManuscriptListVO> getReviewerByJournal(Integer journalId);

    public List<ManuscriptListVO> getInReviewManuscript(IDVO id);

    public List<ManuscriptListVO> getReviewerOnManuscript(IDVO id);

    public void deActivateReviewer(IDVO id, Integer manuscriptReviewerId);

    public void decliendReview(IDVO id);


    public List<ManuScriptReviewerCommonVO> systemSuggestedReviewer(Integer manuscriptId, Integer manuscriptVersion,
                                                                    Integer journalId);


    public boolean isAssignExist(ManuScriptReviewerVO manuScriptReviewerVo);

    public boolean isDeclineExist(ManuScriptReviewerVO manuScriptReviewerVo);

    public List<ManuScriptReviewerVO> reviewerPreviousComment(IDVO id);

    public List<ManuScriptReviewerCommonVO> intrestedToReAccess(Integer manuscriptId, Integer manuscriptVersion,
                                                                Integer journalId);

    public List<ManuScriptReviewerCommonVO> intrestedToReAccessBySearch(Integer manuscriptId, Integer manuscriptVersion,
                                                                          Integer journalId, String search);

    public Integer intrestedToReAccessCount(Integer manuscriptId);

    public List<EditorDashboardVO> getReRivewManuscript(IDVO id);

    public ReviewerDashboardCountVO getReviewerDashboardCount(IDVO id);

    public List<EditorDashboardVO> getManuscriptForReview(IDVO id);

    public List<ManuscriptListVO> getReviewedManuscriptByManuscriptId(List<ManuScript> manuscriptId);

    public List<ManuscriptListVO> getNoResponseManuscriptByManuscriptId(List<ManuScript> manuscriptId);

    public List<ManuscriptListVO> getInReviewManuscriptByManuscriptId(List<ManuScript> manuscriptId);

    public String updateManuscriptReviewerResponse(ReviewerResponseVo reviewerResponseVo, HttpServletRequest request);

    public Date getDueDate(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    public List<ManuscriptReviewerCommentsVO> getReviewerDataByManuscriptId(Integer manuscriptId, Integer reviewerId);

    public List<ManuscriptListVO> getAllReviewedManuscript(IDVO id);

    public List<EditorDashboardVO> getReviewedManuscriptForReviewer(UserJournalManuscriptVo userJournalManuscriptVo);

    public List<ReviewerAvailabilityStatusVO> getReviewerStaticsData(IDVO id);

    public Integer totalReviewerSuggestedBtDatabaseCount(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    public Integer totalReviewerGlobalSearchCount(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

	/*public List<ManuScriptReviewerCommonVO> getReviewerOnRoleByJournalId(Integer manuscriptId, Integer manuscriptVersion,
			Integer journalId);*/

    public List<ManuscriptListVO> getReviewedManuscriptByManuscriptIdForDashboard(List<Integer> manuscriptId);

    public List<ManuscriptListVO> getNoResponseManuscriptByManuscriptIdForDashboard(List<Integer> manuscriptId);

    public List<ManuscriptListVO> getInReviewManuscriptByManuscriptIdForDashboard(List<Integer> manuscriptId);

    public boolean checkReviewCategory(List<ManuScriptReviewer> getAllDataByjournal, Integer manuscriptId, Integer manuscriptVersion, String checkFor);

    List<ReviewerPublonsCommentVO> findReviewerCommentsForPublons(Integer journalId);

    public List<AuthorSuggestedReviewerVo> authorOpposedReviewer(Integer manuscriptId,
                                                                 Integer journalId);

    public List<AuthorSuggestedReviewerVo> authorOpposedReviewerPagination(Integer manuscriptId,
                                                                           Integer journalId, Integer startIndex, Integer limit);

    Integer authorOpposedReviewerCount(Integer manuscriptId,
                                  Integer journalId);

    public List<ReviewerAvailabilityStatusVO> getReviewerStaticsDetails(IDVO id);

    void updateSentToPublonsYes(Integer id, List<Integer> idList);

    public String getOtherReviewerFlagValue(UserJournalManuscriptVo userJournalManuscriptVo);

    public ManuScriptReviewerInvitationVO systemSuggestedReviewerPaggingImpl(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    public List<ManuScriptReviewerCommonVO> getCommentaryDataForManuscript(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    Integer getCommentaryDataForManuscriptCount(ManuScriptReviewerCommonVO commentaryVo);

    List<ManuScriptReviewerCommonVO> getRefineReviewerData(ManuScriptReviewerCommonVO commentaryVo);

    List<ManuScriptReviewerCommonVO> getReviewerCommentData(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    ResponseEntity<Resource> getReviewedCertificateForReviewer(EditorDashboardVO requestVo);
}