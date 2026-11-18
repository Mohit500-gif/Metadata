package com.digi.uniprr.VO;

import java.util.List;

public class SubmisionReportVO {

	private Integer journalId;
	private List<JournalDetailsVo> journalDetails;
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public List<JournalDetailsVo> getJournalDetails() {
		return journalDetails;
	}
	public void setJournalDetails(List<JournalDetailsVo> journalDetails) {
		this.journalDetails = journalDetails;
	}
	
}
