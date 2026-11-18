package com.digi.uniprr.VO;

import org.springframework.web.multipart.MultipartFile;

public class ReviewerResponseVo {

	private Integer reviewerUserId;
	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private Integer journalId;
	private Integer manuscriptStatusId;
	private String commentsToAuthor;
	private String commentsToEditor;
	private String manuscriptPriorty;
	private String competingInterests;
	private String messageFromManuscript;
	private String reviewerResponse;
	private String decisionLetter;
    private String requireCommentary;
    private String writeCommentary;
    private String reasses;
    private MultipartFile file;
    private String publonCredit;
    
	public Integer getReviewerUserId() {
		return reviewerUserId;
	}
	public void setReviewerUserId(Integer reviewerUserId) {
		this.reviewerUserId = reviewerUserId;
	}
	public Integer getManuscriptId() {
		return manuscriptId;
	}
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}
	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}
	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}
	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}
	public String getCommentsToAuthor() {
		return commentsToAuthor;
	}
	public void setCommentsToAuthor(String commentsToAuthor) {
		this.commentsToAuthor = commentsToAuthor;
	}
	public String getCommentsToEditor() {
		return commentsToEditor;
	}
	public void setCommentsToEditor(String commentsToEditor) {
		this.commentsToEditor = commentsToEditor;
	}
	public String getManuscriptPriorty() {
		return manuscriptPriorty;
	}
	public void setManuscriptPriorty(String manuscriptPriorty) {
		this.manuscriptPriorty = manuscriptPriorty;
	}
	public String getCompetingInterests() {
		return competingInterests;
	}
	public void setCompetingInterests(String competingInterests) {
		this.competingInterests = competingInterests;
	}
	public String getMessageFromManuscript() {
		return messageFromManuscript;
	}
	public void setMessageFromManuscript(String messageFromManuscript) {
		this.messageFromManuscript = messageFromManuscript;
	}
	public String getReviewerResponse() {
		return reviewerResponse;
	}
	public void setReviewerResponse(String reviewerResponse) {
		this.reviewerResponse = reviewerResponse;
	}
	public String getDecisionLetter() {
		return decisionLetter;
	}
	public void setDecisionLetter(String decisionLetter) {
		this.decisionLetter = decisionLetter;
	}
	public String getRequireCommentary() {
		return requireCommentary;
	}
	public void setRequireCommentary(String requireCommentary) {
		this.requireCommentary = requireCommentary;
	}
	public String getWriteCommentary() {
		return writeCommentary;
	}
	public void setWriteCommentary(String writeCommentary) {
		this.writeCommentary = writeCommentary;
	}
	public String getReasses() {
		return reasses;
	}
	public void setReasses(String reasses) {
		this.reasses = reasses;
	}
	
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getPublonCredit() {
		return publonCredit;
	}
	public void setPublonCredit(String publonCredit) {
		this.publonCredit = publonCredit;
	}
}
