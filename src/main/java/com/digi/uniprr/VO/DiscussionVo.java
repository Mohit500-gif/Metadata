package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class DiscussionVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;

	private Integer journalId;

	private Integer manuscriptId;

	private Integer manuscriptVersion;
	
	private Integer userId;

	private Integer userRole;

	private String recommendation;

	private Integer discussionOnRole;

	private String subject;

	private String message;

	private String discussionVisibility;

	private Date createdDate;
	
	private String manuscriptCode;
	
	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
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

	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public Integer getDiscussionOnRole() {
		return discussionOnRole;
	}

	public void setDiscussionOnRole(Integer discussionOnRole) {
		this.discussionOnRole = discussionOnRole;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDiscussionVisibility() {
		return discussionVisibility;
	}

	public void setDiscussionVisibility(String discussionVisibility) {
		this.discussionVisibility = discussionVisibility;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "DiscussionVo [id=" + id + ", journalId=" + journalId + ", manuscriptId=" + manuscriptId
				+ ", manuscriptVersion=" + manuscriptVersion + ", userId=" + userId + ", userRole=" + userRole
				+ ", recommendation=" + recommendation + ", discussionOnRole=" + discussionOnRole + ", subject="
				+ subject + ", message=" + message + ", discussionVisibility=" + discussionVisibility + ", createdDate="
				+ createdDate + ", manuscriptCode=" + manuscriptCode + "]";
	}

	
}
