package com.digi.uniprr.VO;

import java.util.Date;

public class ManuscriptFilterVO {
	
	private Integer userId;
	
	private String manuscriptCode;
	
	private Integer journalId;
	
	private String journalTitle;
	
	private Integer manuscriptStatusId;
	
	private String manuscriptTitle;
	
	private Date submissionDateFrom;
	
	private Date submissionDateTo;
	
	private Integer activePhase;
	
	private String authorName;
	
	private String manuscriptKeyword;
	
	private String manuscriptAbstract;
	
	private String manuscriptType;
	
	private String correspondingAuthorEmailAddress;
	
	private String  manuscriptStatus;

	private Integer  roleId;
	
	private Integer completeSearchFlag;
	
	private Integer pageNumber;
	private Integer pageSize;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getManuscriptTitle() {
		return manuscriptTitle;
	}

	public void setManuscriptTitle(String manuscriptTitle) {
		this.manuscriptTitle = manuscriptTitle;
	}

	public Date getSubmissionDateFrom() {
		return submissionDateFrom;
	}

	public void setSubmissionDateFrom(Date submissionDateFrom) {
		this.submissionDateFrom = submissionDateFrom;
	}

	public Date getSubmissionDateTo() {
		return submissionDateTo;
	}

	public void setSubmissionDateTo(Date submissionDateTo) {
		this.submissionDateTo = submissionDateTo;
	}

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getActivePhase() {
		return activePhase;
	}

	public void setActivePhase(Integer activePhase) {
		this.activePhase = activePhase;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getManuscriptKeyword() {
		return manuscriptKeyword;
	}

	public void setManuscriptKeyword(String manuscriptKeyword) {
		this.manuscriptKeyword = manuscriptKeyword;
	}

	public String getManuscriptAbstract() {
		return manuscriptAbstract;
	}

	public void setManuscriptAbstract(String manuscriptAbstract) {
		this.manuscriptAbstract = manuscriptAbstract;
	}

	public String getManuscriptType() {
		return manuscriptType;
	}

	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}

	public String getCorrespondingAuthorEmailAddress() {
		return correspondingAuthorEmailAddress;
	}

	public void setCorrespondingAuthorEmailAddress(String correspondingAuthorEmailAddress) {
		this.correspondingAuthorEmailAddress = correspondingAuthorEmailAddress;
	}

	public String getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(String manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getCompleteSearchFlag() {
		return completeSearchFlag;
	}

	public void setCompleteSearchFlag(Integer completeSearchFlag) {
		this.completeSearchFlag = completeSearchFlag;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
