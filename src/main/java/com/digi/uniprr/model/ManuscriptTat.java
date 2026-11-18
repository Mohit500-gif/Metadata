package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Table(name="manuscript_tat")
@Entity
public class ManuscriptTat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name ="task_id")
	private Integer taskId;
	
	@Column(name = "tat_last_date")
	private Date tatLastDate; 
	
	@Column(name = "buffer_last_date")
	private Date bufferLastDate;
	
	@Column(name = "is_email_send")
	private Integer isEmailSend;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private Date createdOn;
	
	@Column(name = "is_active")
	private Integer isActive;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Date getTatLastDate() {
		return tatLastDate;
	}

	public void setTatLastDate(Date tatLastDate) {
		this.tatLastDate = tatLastDate;
	}

	public Date getBufferLastDate() {
		return bufferLastDate;
	}

	public void setBufferLastDate(Date bufferLastDate) {
		this.bufferLastDate = bufferLastDate;
	}

	public Integer getIsEmailSend() {
		return isEmailSend;
	}

	public void setIsEmailSend(Integer isEmailSend) {
		this.isEmailSend = isEmailSend;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	
}
