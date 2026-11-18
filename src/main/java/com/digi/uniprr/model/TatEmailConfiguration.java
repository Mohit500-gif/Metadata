package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tat_email_configuration")
public class TatEmailConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;

	@Column(name = "journal_id")
	private Integer journalId;
	
	@ManyToOne
	@JoinColumn(name = "journal_id", updatable = false, insertable = false)
	private Journal journal;

	@Column(name = "manuscript_id")
	private Integer manuscriptId;

	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;

	@Column(name = "task_id")
	private Integer taskId;

	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name="accept_reviewer_url")
	private String acceptReviewerUrl;
	
	@Column(name="reject_reviewer_url")
	private String rejectReviewerUrl;

	@Column(name="view_reviewer_url")
	private String viewReviewerUrl;
	
	@Column(name = "managing_editor_id")
	private Integer managingEditorId;
	
	@Column(name = "editor_id")
	private Integer editorId;
	
	@Column(name="reviewer_id")
	private Integer reviewerId;
	
	@Column(name = "role_id")
	private Integer roleId;
	
	@Column(name = "reciver_role_id")
	private Integer reciverRoleId;
	
	@Column(name = "manuscript_status")
	private Integer manuscriptStatus;
	
	@Column(name = "notification_date")
	private Date notificationDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "buffered_end_date")
	private Date bufferedEndDate;

	@Column(name = "is_mail_send")
	private Integer isMailSend;

	@Column(name = "is_active")
	private Integer isActive;

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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getBufferedEndDate() {
		return bufferedEndDate;
	}

	public void setBufferedEndDate(Date bufferedEndDate) {
		this.bufferedEndDate = bufferedEndDate;
	}

	public Integer getIsMailSend() {
		return isMailSend;
	}

	public void setIsMailSend(Integer isMailSend) {
		this.isMailSend = isMailSend;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getAcceptReviewerUrl() {
		return acceptReviewerUrl;
	}

	public void setAcceptReviewerUrl(String acceptReviewerUrl) {
		this.acceptReviewerUrl = acceptReviewerUrl;
	}

	public String getRejectReviewerUrl() {
		return rejectReviewerUrl;
	}

	public void setRejectReviewerUrl(String rejectReviewerUrl) {
		this.rejectReviewerUrl = rejectReviewerUrl;
	}

	public String getViewReviewerUrl() {
		return viewReviewerUrl;
	}

	public void setViewReviewerUrl(String viewReviewerUrl) {
		this.viewReviewerUrl = viewReviewerUrl;
	}

	public Integer getManagingEditorId() {
		return managingEditorId;
	}

	public void setManagingEditorId(Integer managingEditorId) {
		this.managingEditorId = managingEditorId;
	}

	public Integer getEditorId() {
		return editorId;
	}

	public void setEditorId(Integer editorId) {
		this.editorId = editorId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getReciverRoleId() {
		return reciverRoleId;
	}

	public void setReciverRoleId(Integer reciverRoleId) {
		this.reciverRoleId = reciverRoleId;
	}

	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public Integer getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
	}

	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

}
