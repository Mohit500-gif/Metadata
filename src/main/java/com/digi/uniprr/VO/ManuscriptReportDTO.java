package com.digi.uniprr.VO;

import java.util.List;

public class ManuscriptReportDTO {
//    private String authorName;
//    private String authorEmail;
//    private Integer authorId;
//    private String authorNo;
    private List<AuthorForReportDTO> authors;
    private String country;
    private String institutions;
    private String articleStatus;
    private String editorName;
    private String editorEmail;
    private Integer editorId;
    private String editorNo;
//    private String reviewers;
    private List<ReviewerForReportDTO> reviewers;
    private String journalTitle;
    private Integer manuscriptId;
    private String manuscriptCode;
    private String manuscriptTitle;
    private String doi;
    private String manuscriptType;
    private String submissionDate;
    private String editorialReview;
    private String inviteReviewer;
    private String acceptReviewInvitation;
    private String reviewedManuscript;
    private String revision;
    private String finalDecision;
    private String acceptedRejected;
    private String published;
    private String rejectedDate;
    private ManuscriptTimelineDTO timeline;
    
	public String getEditorName() {
		return editorName;
	}
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}
	public String getEditorEmail() {
		return editorEmail;
	}
	public void setEditorEmail(String editorEmail) {
		this.editorEmail = editorEmail;
	}
	public String getEditorNo() {
		return editorNo;
	}
	public void setEditorNo(String editorNo) {
		this.editorNo = editorNo;
	}
	public List<ReviewerForReportDTO> getReviewers() {
	    return reviewers;
	}

	public void setReviewers(List<ReviewerForReportDTO> reviewers) {
	    this.reviewers = reviewers;
	}
	public String getJournalTitle() {
		return journalTitle;
	}
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}
	public String getManuscriptCode() {
		return manuscriptCode;
	}
	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}
	public String getManuscriptTitle() {
		return manuscriptTitle;
	}
	public void setManuscriptTitle(String manuscriptTitle) {
		this.manuscriptTitle = manuscriptTitle;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public String getManuscriptType() {
		return manuscriptType;
	}
	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}
	public String getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}
	public String getEditorialReview() {
		return editorialReview;
	}
	public void setEditorialReview(String editorialReview) {
		this.editorialReview = editorialReview;
	}
	public String getInviteReviewer() {
		return inviteReviewer;
	}
	public void setInviteReviewer(String inviteReviewer) {
		this.inviteReviewer = inviteReviewer;
	}
	public String getAcceptReviewInvitation() {
		return acceptReviewInvitation;
	}
	public void setAcceptReviewInvitation(String acceptReviewInvitation) {
		this.acceptReviewInvitation = acceptReviewInvitation;
	}
	public String getReviewedManuscript() {
		return reviewedManuscript;
	}
	public void setReviewedManuscript(String reviewedManuscript) {
		this.reviewedManuscript = reviewedManuscript;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getFinalDecision() {
		return finalDecision;
	}
	public void setFinalDecision(String finalDecision) {
		this.finalDecision = finalDecision;
	}
	public String getAcceptedRejected() {
		return acceptedRejected;
	}
	public void setAcceptedRejected(String acceptedRejected) {
		this.acceptedRejected = acceptedRejected;
	}
	public String getPublished() {
		return published;
	}
	public void setPublished(String published) {
		this.published = published;
	}
	
	public Integer getEditorId() {
		return editorId;
	}
	public void setEditorId(Integer editorId) {
		this.editorId = editorId;
	}
	public Integer getManuscriptId() {
		return manuscriptId;
	}
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}
	public ManuscriptTimelineDTO getTimeline() {
		return timeline;
	}
	public void setTimeline(ManuscriptTimelineDTO timeline) {
		this.timeline = timeline;
	}
	public List<AuthorForReportDTO> getAuthors() {
		return authors;
	}
	public void setAuthors(List<AuthorForReportDTO> authors) {
		this.authors = authors;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getInstitutions() {
		return institutions;
	}
	public void setInstitutions(String institutions) {
		this.institutions = institutions;
	}
	public String getArticleStatus() {
		return articleStatus;
	}
	public void setArticleStatus(String articleStatus) {
		this.articleStatus = articleStatus;
	}
	public String getRejectedDate() {
		return rejectedDate;
	}
	public void setRejectedDate(String rejectedDate) {
		this.rejectedDate = rejectedDate;
	}
    
	
}
