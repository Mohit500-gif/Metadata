package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AuthoritiesHistoryVo implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;

	private Integer manuscriptId;

	private Integer manuscriptVersion;

	private Integer manuscriptStatusId;

	private String articleStatus;

	private Integer journalId;

	private List<Integer> currentUserId;

	private Integer currentUserid;

	private String currentUserName;

	private Integer currentUserRole;

	private Integer previousUserId;

	private String previousUserName;

	private Integer previousUserRole;

	private String commentsToAuthor;

	private String commentsToEditor;

	private String commentsToReviewer;
	
	private String commentsToEIC;

	private Date createdAt;
	
	private Date dueDate;
	
	private Date rejectedDate;
	public AuthoritiesHistoryVo() {
	}
	
	public AuthoritiesHistoryVo(Integer manuscriptId, Integer manuscriptVersion, Integer manuscriptStatusId, Date createdAt, Date dueDate)
	{   
		super();
		this.manuscriptId = manuscriptId;
		this.manuscriptVersion = manuscriptVersion;
		this.manuscriptStatusId = manuscriptStatusId;
		this.createdAt = createdAt;
		this.dueDate = dueDate;
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

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getCurrentUserRole() {
		return currentUserRole;
	}

	public void setCurrentUserRole(Integer currentUserRole) {
		this.currentUserRole = currentUserRole;
	}

	public Integer getPreviousUserId() {
		return previousUserId;
	}

	public void setPreviousUserId(Integer previousUserId) {
		this.previousUserId = previousUserId;
	}

	public Integer getPreviousUserRole() {
		return previousUserRole;
	}

	public void setPreviousUserRole(Integer previousUserRole) {
		this.previousUserRole = previousUserRole;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<Integer> getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(List<Integer> currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Integer getCurrentUserid() {
		return currentUserid;
	}

	public void setCurrentUserid(Integer currentUserid) {
		this.currentUserid = currentUserid;
	}

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public String getArticleStatus() {
		return articleStatus;
	}

	public void setArticleStatus(String articleStatus) {
		this.articleStatus = articleStatus;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public String getPreviousUserName() {
		return previousUserName;
	}

	public void setPreviousUserName(String previousUserName) {
		this.previousUserName = previousUserName;
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

	public String getCommentsToReviewer() {
		return commentsToReviewer;
	}

	public void setCommentsToReviewer(String commentsToReviewer) {
		this.commentsToReviewer = commentsToReviewer;
	}

	public String getCommentsToEIC() {
		return commentsToEIC;
	}

	public void setCommentsToEIC(String commentsToEIC) {
		this.commentsToEIC = commentsToEIC;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	@Override
	public String toString() {
		return "AuthoritiesHistoryVo [id=" + id + ", manuscriptId=" + manuscriptId + ", manuscriptVersion="
				+ manuscriptVersion + ", manuscriptStatusId=" + manuscriptStatusId + ", articleStatus=" + articleStatus
				+ ", journalId=" + journalId + ", currentUserId=" + currentUserId + ", currentUserid=" + currentUserid
				+ ", currentUserName=" + currentUserName + ", currentUserRole=" + currentUserRole + ", previousUserId="
				+ previousUserId + ", previousUserName=" + previousUserName + ", previousUserRole=" + previousUserRole
				+ ", commentsToAuthor=" + commentsToAuthor + ", commentsToEditor=" + commentsToEditor
				+ ", commentsToReviewer=" + commentsToReviewer + ", createdAt=" + createdAt + ", dueDate=" + dueDate
				+ ", rejectedDate=" + rejectedDate + "]";
	}


}
