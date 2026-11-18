package com.digi.uniprr.VO;

import java.io.Serializable;

public class AdminJournalVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String journalTitle;

	private String journalCode;

	private Integer journalId;

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public AdminJournalVo(String journalTitle, String journalCode, Integer journalId) {
		super();
		this.journalTitle = journalTitle;
		this.journalCode = journalCode;
		this.journalId = journalId;
	}

	@Override
	public String toString() {
		return "AdminJournalVo [journalTitle=" + journalTitle + ", journalCode=" + journalCode + ", journalId="
				+ journalId + "]";
	}

	
}
