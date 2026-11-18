package com.digi.uniprr.VO;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.digi.uniprr.model.StatusMaster;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TrackManuScriptVO {

	private Integer manuscriptId;
	private String manuscriptCode;
	private String manuscriptValue;
	private String userName;
	private String manuscriptTitle;
	private Integer authorId;
	private Integer journalId;
	private Integer manuscriptStatusId;
	private Integer manuscriptVersion;
	private Integer submittedBy;
	private Integer updatedBy;
	private Integer createdBy;
	private Date createdOn;
	private Date updatedOn;
	// @Temporal(TemporalType.DATE)
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	private Date submissionDate;

	public String getStatusValueForAuthor() {
		return statusValueForAuthor;
	}

	public void setStatusValueForAuthor(String statusValueForAuthor) {
		this.statusValueForAuthor = statusValueForAuthor;
	}

	public String getStatusValueForEditor() {
		return statusValueForEditor;
	}

	public void setStatusValueForEditor(String statusValueForEditor) {
		this.statusValueForEditor = statusValueForEditor;
	}

	private Integer activePhase;

	private String authorName;

	private String statusValueForAuthor;

	private String statusValueForEditor;

	private String manuscriptKeyword;

	private String manuscriptAbstract;

	private String manuscriptType;

	private String correspondingAuthorEmailAddress;

	private String manuscriptStatus;

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public String getManuscriptValue() {
		return manuscriptValue;
	}

	public void setManuscriptValue(String manuscriptValue) {
		this.manuscriptValue = manuscriptValue;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getManuscriptTitle() {
		return manuscriptTitle;
	}

	public void setManuscriptTitle(String manuscriptTitle) {
		this.manuscriptTitle = manuscriptTitle;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Integer getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(Integer submittedBy) {
		this.submittedBy = submittedBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
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

}
