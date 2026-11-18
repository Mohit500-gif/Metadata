package com.digi.uniprr.VO;

public class JournalIssuesPlannerVO {
	private Integer id;
	private Integer journalId;
	private String issueName;
	private Integer issueYear;
	private Integer issueMonth;
	private String issueVolume;
	private Integer createdBy;
	private Integer updatedBy;

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

}
