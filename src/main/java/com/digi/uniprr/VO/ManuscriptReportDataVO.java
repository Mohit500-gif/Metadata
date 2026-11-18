package com.digi.uniprr.VO;

import java.util.Date;

public class ManuscriptReportDataVO {

	private Integer manuscriptId;
	private String manuscriptCode;
	private Integer journalId;
	private Integer manuscriptVersion;
	private Integer roleId;
	private String manuscriptType;
	private Integer manuscriptStatus;
	private Integer isActive;
	private Date dueDate;
	
	public ManuscriptReportDataVO() {
	}
	
	public ManuscriptReportDataVO(Integer manuscriptId, Integer journalId,  Integer manuscriptStatus) {
		this.manuscriptId = manuscriptId;
		this.journalId = journalId;
		this.manuscriptStatus = manuscriptStatus;
	}
	
	public ManuscriptReportDataVO(Integer manuscriptId, String manuscriptCode, Integer manuscriptVersion, Integer roleId, String manuscriptType, Integer manuscriptStatus, Integer isActive , Date dueDate) {
		this.manuscriptId = manuscriptId;
		this.manuscriptCode = manuscriptCode;
        this.manuscriptVersion = manuscriptVersion;
        this.roleId =  roleId;
		this.manuscriptType = manuscriptType;
		this.manuscriptStatus = manuscriptStatus;
		this.isActive = isActive;
		this.dueDate = dueDate;
	}
	
	public ManuscriptReportDataVO(Integer manuscriptId, String manuscriptCode, Integer manuscriptVersion, Integer manuscriptStatus, Integer isActive , Date dueDate) {
		this.manuscriptId = manuscriptId;
		this.manuscriptCode = manuscriptCode;
        this.manuscriptVersion = manuscriptVersion;
		this.manuscriptStatus = manuscriptStatus;
		this.isActive = isActive;
		this.dueDate = dueDate;
	}
	
	
	public Integer getManuscriptId() {
		return manuscriptId;
	}
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}
	public String getManuscriptCode() {
		return manuscriptCode;
	}
	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}
	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}
	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}
	public String getManuscriptType() {
		return manuscriptType;
	}
	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}
	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}
	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}
	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	
	

}
