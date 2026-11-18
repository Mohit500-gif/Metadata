package com.digi.uniprr.VO;

import java.util.Date;

public class JournalSubjectVo {

	private Integer id;

	private String journalSubject;

	private Integer journalId;

	private Integer isActive;

	private Date createdAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getJournalSubject() {
		return journalSubject;
	}

	public void setJournalSubject(String journalSubject) {
		this.journalSubject = journalSubject;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
