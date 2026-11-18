package com.digi.uniprr.service;

import com.digi.uniprr.VO.LetterToEditorVo;
import com.digi.uniprr.model.ManuscriptDashboardList;

import java.util.Date;
import java.util.List;

public interface LetterToEditorService {


    List<ManuscriptDashboardList> getManuscriptByStatus(ManuscriptDashboardList letterToEditorVo);

    void updateManuscriptToLTEArticle(Integer manuscriptId, Integer letterToEditorId, Integer journalId);

    void inviteAuthorForResponse(LetterToEditorVo letterToEditorVo);

    List<LetterToEditorVo> getAuthorResponseList(LetterToEditorVo letterToEditorVo);

    List<LetterToEditorVo> getAuthorResponse(Integer manuscriptId, Integer journalId);

    void approveInvitationStatus(Integer id, Integer userId, Date approvedOn, Integer manuscriptId, Integer userRole, String action);

    void rejectInvitationStatus(Integer id, Integer userId, Date rejectOn, Integer manuscriptId, Integer userRole, String action);

    void submitInvitationStatus(Integer id, Integer userId, Date submittedDate, Integer manuscriptId, Integer userRole, String action);

    String deleteInvitation(LetterToEditorVo letterToEditorVo);

    Integer getManuscriptByStatusCount(ManuscriptDashboardList letterToEditorVo);

    List<String> getIssueList(Integer journalId);
}
