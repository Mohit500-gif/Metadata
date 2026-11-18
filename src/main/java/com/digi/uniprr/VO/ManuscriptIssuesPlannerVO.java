package com.digi.uniprr.VO;

import java.util.Date;

public class ManuscriptIssuesPlannerVO {
	private Integer id;
	private Integer issueId;
	private Integer manuscriptId;
	private String issueName;
	private Integer issueYear;
	private Integer issueMonth;
	private String issueVolume;
    private String journalName;
    private String manuscriptName;
	private Integer manuscriptVersion;
	private Integer journalId;
	private Integer createdBy;
	private Integer updatedBy;
	private Date createdDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getIssueId() {
		return issueId;
	}
	public void setIssueId(Integer issueId) {
		this.issueId = issueId;
	}
	public Integer getManuscriptId() {
		return manuscriptId;
	}
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}
	public String getIssueName() {
		return issueName;
	}
	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}
	public Integer getIssueYear() {
		return issueYear;
	}
	public void setIssueYear(Integer issueYear) {
		this.issueYear = issueYear;
	}
	public Integer getIssueMonth() {
		return issueMonth;
	}
	public void setIssueMonth(Integer issueMonth) {
		this.issueMonth = issueMonth;
	}
	public String getIssueVolume() {
		return issueVolume;
	}
	public void setIssueVolume(String issueVolume) {
		this.issueVolume = issueVolume;
	}
	public String getJournalName() {
		return journalName;
	}
	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}
	public String getManuscriptName() {
		return manuscriptName;
	}
	public void setManuscriptName(String manuscriptName) {
		this.manuscriptName = manuscriptName;
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
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
