package com.digi.uniprr.service;

import java.util.Date;
import java.util.List;

import com.digi.uniprr.VO.CommentaryVo;
import com.digi.uniprr.VO.ManuScriptReviewerCommonVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.exception.HttpCommunicationException;

public interface CommentaryService {

	void inviteCommentary(CommentaryVo commentaryVo);

	List<CommentaryVo> getCommentary(CommentaryVo commentaryVo);

    void updateUser(CommentaryVo userVo);

    List<CommentaryVo> getCommentaryList(Integer manuscriptId, Integer journalId);

    void linkCommentaryToManuscript(Integer manuscriptId, Integer commentaryId, Integer journalId);

    String deleteInvitation(CommentaryVo userVo);


    void approveInvitationStatus(Integer id,Integer userId, Date approvedOn, Integer manuscriptId, Integer userRole,String action);

    void rejectInvitationStatus(Integer id,Integer userId, Date rejectOn, Integer manuscriptId, Integer userRole,String action);

    void submitInvitationStatus(Integer id, Integer userId, Date submittedDate, Integer manuscriptId, Integer userRole, String action) throws HttpCommunicationException;

    List<ManuScriptReviewerCommonVO> getReviewerDatabaseSuggested(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    List<ManuScriptReviewerCommonVO> commentaryGlobalSearch(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);

    Integer totalReviewerSuggestedBtDatabaseCount(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO);
}
