package com.digi.uniprr.VO;

public class FailedIthenicateVo {
	
	private int journalId;
	private int manuscriptId;


	public int getJournalId() {
		return journalId;
	}


	public void setJournalId(int journalId) {
		this.journalId = journalId;
	}


	public int getManuscriptId() {
		return manuscriptId;
	}


	public void setManuscriptId(int manuscriptId) {
		this.manuscriptId = manuscriptId;
	}


	@Override
	public String toString() {
		return "GoLiveVo [journalId=" + journalId + ", manuscriptId=" + manuscriptId + "]";
	}
}
