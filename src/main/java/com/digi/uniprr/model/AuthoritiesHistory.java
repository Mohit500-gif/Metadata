package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "authorities_history")
public class AuthoritiesHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name = "manuscript_status_id")
	private Integer manuscriptStatusId;
	
	@OneToOne
	@JoinColumn(name = "manuscript_status_id", updatable = false, insertable = false)
	private StatusMaster statusMaster;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "current_user_id")
	private Integer currentUserId;
	
	@Column(name = "current_user_role")
	private Integer currentUserRole;
	
	@Column(name = "comments_to_author")
	private String commentsToAuthor;
	
	@Column(name = "comments_to_editor")
	private String commentsToEditor;
	
	@Column(name = "comments_to_reviewer")
	private String commentsToReviewer;
	
	@Column(name = "comments_to_EIC")
	private String commentsToEIC;
	
	@Column(name = "previous_user_id")
	private Integer previousUserId;
	
	@Column(name = "previous_user_role")
	private Integer previousUserRole;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "due_date")
	private Date dueDate;

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

	public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
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

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public StatusMaster getStatusMaster() {
		return statusMaster;
	}

	public void setStatusMaster(StatusMaster statusMaster) {
		this.statusMaster = statusMaster;
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
	
}
