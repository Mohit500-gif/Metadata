package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "reminder_tracker")
public class ReminderTracker {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_status_id")
	private Integer manuscriptStatusId;
	
	@Column(name = "assigned_user")
	private Integer assignedUser;
	
	@Column(name = "reminder_no")
	private Integer reminderNo;
	
	@Column(name = "reminder_status")
	private Integer reminderStatus;
	
	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "updated_date")
	private Date updatedDate;

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

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(Integer assignedUser) {
		this.assignedUser = assignedUser;
	}

	public Integer getReminderNo() {
		return reminderNo;
	}

	public void setReminderNo(Integer reminderNo) {
		this.reminderNo = reminderNo;
	}

	public Integer getReminderStatus() {
		return reminderStatus;
	}

	public void setReminderStatus(Integer reminderStatus) {
		this.reminderStatus = reminderStatus;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "ReminderTracker [id=" + id + ", journalId=" + journalId + ", manuscriptId=" + manuscriptId
				+ ", manuscriptStatusId=" + manuscriptStatusId + ", assignedUser=" + assignedUser + ", reminderNo="
				+ reminderNo + ", reminderStatus=" + reminderStatus + ", isActive=" + isActive + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", updatedBy=" + updatedBy + ", updatedDate="
				+ updatedDate + "]";
	}

	
}
