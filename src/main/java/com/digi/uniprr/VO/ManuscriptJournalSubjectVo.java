package com.digi.uniprr.VO;

import java.util.Date;

public class ManuscriptJournalSubjectVo {

private Integer id;
	
	private Integer manuscriptId;
	
	private Integer manuscriptVersion;
	
	private Integer journalId;
	
	private Integer journalSubjectId;
	
	private String journalSubjectName;
	
	private Integer isActive;
	
	private Date createdAt;

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

	public Integer getJournalSubjectId() {
		return journalSubjectId;
	}

	public void setJournalSubjectId(Integer journalSubjectId) {
		this.journalSubjectId = journalSubjectId;
	}

	public String getJournalSubjectName() {
		return journalSubjectName;
	}

	public void setJournalSubjectName(String journalSubjectName) {
		this.journalSubjectName = journalSubjectName;
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
