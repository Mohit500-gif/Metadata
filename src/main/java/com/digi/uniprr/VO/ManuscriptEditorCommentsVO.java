package com.digi.uniprr.VO;

import java.util.Date;

public class ManuscriptEditorCommentsVO {

	private Integer id;

	private Integer manuscriptId;

	private Integer manuscriptVersion;
	
	private String manuscriptStatus;

	private Integer status;

	private String comments;

	private String createdBy;

	private Date createdOn;

	private String updatedBy;

	private Date updatedOn;
	
	private String userName;
	
	private String decisionLetter;
	//Added Mudit
	
	private String commentsForEditor;
	
	private String commentsForReviewer;
	
	private String commentsForAuthor;
	
	private String commentsForManagingEditor;

	private String reviewerFileName;
	private String reviewerFilePath;
	private Integer reviewerFileSize;
	private String reviewerFileType;
	private String sendBackEicComment;
	private Date assignDate;
	private String commentRole;
	private Integer statusId;
	private Integer currentRole;

	public void setCurrentRole(Integer currentRole)
	{
		this.currentRole=currentRole;
	}

	public Integer getCurrentRole()
	{
		return this.currentRole;
	}
	public Integer getStatusId()
	{
		return this.statusId;
	}

	public void setStatusId(Integer statusId)
	{
		this.statusId=statusId;
	}

	public String getCommentRole()
	{
		return this.commentRole;
	};

	public void setCommentRole(String commentRole)
	{
		this.commentRole=commentRole;
	}

	public String getReviewerFileType() {
		return reviewerFileType;
	}

	public void setReviewerFileType(String reviewerFileType) {
		this.reviewerFileType = reviewerFileType;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getCommentsForEditor() {
		return commentsForEditor;
	}

	public void setCommentsForEditor(String commentsForEditor) {
		this.commentsForEditor = commentsForEditor;
	}

	public String getCommentsForReviewer() {
		return commentsForReviewer;
	}

	public void setCommentsForReviewer(String commentsForReviewer) {
		this.commentsForReviewer = commentsForReviewer;
	}

	public String getCommentsForAuthor() {
		return commentsForAuthor;
	}

	public void setCommentsForAuthor(String commentsForAuthor) {
		this.commentsForAuthor = commentsForAuthor;
	}

	public String getCommentsForManagingEditor() {
		return commentsForManagingEditor;
	}

	public void setCommentsForManagingEditor(String commentsForManagingEditor) {
		this.commentsForManagingEditor = commentsForManagingEditor;
	}

	public String getDecisionLetter() {
		return decisionLetter;
	}

	public void setDecisionLetter(String decisionLetter) {
		this.decisionLetter = decisionLetter;
	}

	public String getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(String manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public String getSendBackEicComment() {
		return sendBackEicComment;
	}

	public void setSendBackEicComment(String sendBackEicComment) {
		this.sendBackEicComment = sendBackEicComment;
	}

	public Date getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}
	

}
