package com.digi.uniprr.VO;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ManuScriptTrackVO {

	private Integer manuScriptId;
	private String journalTitle;
	private Integer journalId;
	private String authorName;
	private Integer authorId;
	private String manuScriptTitle;
	private String manuscriptCode;
	private Integer version;

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	private Date submissionDate;

	public Integer getManuScriptId() {
		return manuScriptId;
	}

	public void setManuScriptId(Integer manuScriptId) {
		this.manuScriptId = manuScriptId;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getManuScriptTitle() {
		return manuScriptTitle;
	}

	public void setManuScriptTitle(String manuScriptTitle) {
		this.manuScriptTitle = manuScriptTitle;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

}