package com.digi.uniprr.VO;
public interface ManuscriptReportProjection {

    String getAuthorsJson();

    String getEditorName();
    String getEditorEmail();
    Integer getEditorId();
    String getEditorNo();

    String getReviewersJson();

    String getJournalTitle();
    Integer getManuscriptId();
    String getManuscriptCode();
    String getManuscriptTitle();
    String getDoi();
    String getManuscriptType();

    String getSubmissionDate();
    String getEditorialReview();
    String getInviteReviewer();
    String getAcceptReviewInvitation();
    String getReviewedManuscript();
    String getRevision();
    String getFinalDecision();
    String getAcceptedRejected();
    String getPublished();

    String getCountry();
    String getInstitutions();
    String getArticleStatus();
}