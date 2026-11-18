package com.digi.uniprr.VO;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.digi.uniprr.model.ManuScript;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ManuscriptReviewerCommentsVO {
	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private Integer reviewerId;
	private String decisionLetter;
	private String commentsToEditor;
	private String commentsToAuthor;
	private String manuscriptPriority;
	private String competingInterests;
	private String requiresCommentary;
	private String writecommentary;
	private Integer isInvitedForCommentary;
	private String reasses;
	private String messageFromManuscript;
	private String reviewerResponse;
	private Date createdOn;
	private Date dueDate;
	private Integer createdBy;
	private Date updatedOn;
	private Integer updatedBy;
	private String userName;
	private String manuscriptStatus;
	private String reviewerFileName;
	private String reviewerFilePath;
	private Integer reviewerFileSize;
	private Integer isActive;
	private String rejectionReason;
	private String commentsForReviewer;
	private Date assignDate;
	private Integer statusId;

	public void setStatusId(Integer statusId)
	{
		this.statusId=statusId;
	}
	public Integer getStatusId()
	{
		return this.statusId;
	}
	
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public Integer getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
	}
	public String getDecisionLetter() {
		return decisionLetter;
	}
	public void setDecisionLetter(String decisionLetter) {
		this.decisionLetter = decisionLetter;
	}
	public String getCommentsToEditor() {
		return commentsToEditor;
	}
	public void setCommentsToEditor(String commentsToEditor) {
		this.commentsToEditor = commentsToEditor;
	}
	public String getCommentsToAuthor() {
		return commentsToAuthor;
	}
	public void setCommentsToAuthor(String commentsToAuthor) {
		this.commentsToAuthor = commentsToAuthor;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public Integer getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getManuscriptStatus() {
		return manuscriptStatus;
	}
	public void setManuscriptStatus(String manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}
	public String getManuscriptPriority() {
		return manuscriptPriority;
	}
	public void setManuscriptPriority(String manuscriptPriority) {
		this.manuscriptPriority = manuscriptPriority;
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
	public String getRequiresCommentary() {
		return requiresCommentary;
	}
	public void setRequiresCommentary(String requiresCommentary) {
		this.requiresCommentary = requiresCommentary;
	}
	public String getWritecommentary() {
		return writecommentary;
	}
	public void setWritecommentary(String writecommentary) {
		this.writecommentary = writecommentary;
	}
	public Integer getIsInvitedForCommentary() {
		return isInvitedForCommentary;
	}
	public void setIsInvitedForCommentary(Integer isInvitedForCommentary) {
		this.isInvitedForCommentary = isInvitedForCommentary;
	}
	public String getReasses() {
		return reasses;
	}
	public void setReasses(String reasses) {
		this.reasses = reasses;
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
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public String getRejectionReason() {
		return rejectionReason;
	}
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
	public String getCommentsForReviewer() {
		return commentsForReviewer;
	}
	public void setCommentsForReviewer(String commentsForReviewer) {
		this.commentsForReviewer = commentsForReviewer;
	}
	public Date getAssignDate() {
		return assignDate;
	}
	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}
	
}
