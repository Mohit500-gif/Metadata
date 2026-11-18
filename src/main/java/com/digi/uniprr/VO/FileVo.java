package com.digi.uniprr.VO;

import java.io.Serializable;

public class FileVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private Integer journalId;
	private Integer reviewerId;
	private String reviewerFileName;
	private String reviewerFileDisplayName;
	private String reviewerFilePath;
	private Integer reviewerFileSize;
	private String reviewerFirstName;
	private String reviewerLastName;
	private String reviewerMiddleName;
	private String reviewerEmail;
	private String commentToAuthor;
	private String commentToEditor;
	private String reviewerResponse;
	private String reviewerSalutation;
	private String reviewerUploadedDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
	}
	public String getReviewerFileName() {
		return reviewerFileName;
	}
	public void setReviewerFileName(String reviewerFileName) {
		this.reviewerFileName = reviewerFileName;
	}
	public String getReviewerFilePath() {
		return reviewerFilePath;
	}
	public void setReviewerFilePath(String reviewerFilePath) {
		this.reviewerFilePath = reviewerFilePath;
	}
	public Integer getReviewerFileSize() {
		return reviewerFileSize;
	}
	public void setReviewerFileSize(Integer reviewerFileSize) {
		this.reviewerFileSize = reviewerFileSize;
	}
	public String getReviewerFirstName() {
		return reviewerFirstName;
	}
	public void setReviewerFirstName(String reviewerFirstName) {
		this.reviewerFirstName = reviewerFirstName;
	}
	public String getReviewerLastName() {
		return reviewerLastName;
	}
	public void setReviewerLastName(String reviewerLastName) {
		this.reviewerLastName = reviewerLastName;
	}
	public String getReviewerMiddleName() {
		return reviewerMiddleName;
	}
	public void setReviewerMiddleName(String reviewerMiddleName) {
		this.reviewerMiddleName = reviewerMiddleName;
	}
	public String getReviewerEmail() {
		return reviewerEmail;
	}
	public void setReviewerEmail(String reviewerEmail) {
		this.reviewerEmail = reviewerEmail;
	}
	public String getCommentToAuthor() {
		return commentToAuthor;
	}
	public void setCommentToAuthor(String commentToAuthor) {
		this.commentToAuthor = commentToAuthor;
	}
	public String getCommentToEditor() {
		return commentToEditor;
	}
	public void setCommentToEditor(String commentToEditor) {
		this.commentToEditor = commentToEditor;
	}
	public String getReviewerResponse() {
		return reviewerResponse;
	}
	public void setReviewerResponse(String reviewerResponse) {
		this.reviewerResponse = reviewerResponse;
	}
	public String getReviewerSalutation() {
		return reviewerSalutation;
	}
	public void setReviewerSalutation(String reviewerSalutation) {
		this.reviewerSalutation = reviewerSalutation;
	}
	public String getReviewerUploadedDate() {
		return reviewerUploadedDate;
	}
	public void setReviewerUploadedDate(String reviewerUploadedDate) {
		this.reviewerUploadedDate = reviewerUploadedDate;
	}
	
	
	
	
	public String getReviewerFileDisplayName() {
		return reviewerFileDisplayName;
	}
	public void setReviewerFileDisplayName(String reviewerFileDisplayName) {
		this.reviewerFileDisplayName = reviewerFileDisplayName;
	}
	@Override
	public String toString() {
		return "FileVo [id=" + id + ", manuscriptId=" + manuscriptId + ", manuscriptVersion=" + manuscriptVersion
				+ ", journalId=" + journalId + ", reviewerId=" + reviewerId + ", reviewerFileName=" + reviewerFileName
				+ ", reviewerFilePath=" + reviewerFilePath + ", reviewerFileSize=" + reviewerFileSize
				+ ", reviewerFirstName=" + reviewerFirstName + ", reviewerLastName=" + reviewerLastName
				+ ", reviewerMiddleName=" + reviewerMiddleName + ", reviewerEmail=" + reviewerEmail
				+ ", commentToAuthor=" + commentToAuthor + ", commentToEditor=" + commentToEditor
				+ ", reviewerResponse=" + reviewerResponse + ", reviewerSalutation=" + reviewerSalutation
				+ ", reviewerUploadedDate=" + reviewerUploadedDate + "]";
	}
	

	
	
}
