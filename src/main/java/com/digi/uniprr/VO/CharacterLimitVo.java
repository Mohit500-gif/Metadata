package com.digi.uniprr.VO;


public class CharacterLimitVo {
   
	private Integer journalId;
	
    private String title;
	
	private String runningTitle;
	
	private String abstractTitle;

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRunningTitle() {
		return runningTitle;
	}

	public void setRunningTitle(String runningTitle) {
		this.runningTitle = runningTitle;
	}

	public String getAbstractTitle() {
		return abstractTitle;
	}

	public void setAbstractTitle(String abstractTitle) {
		this.abstractTitle = abstractTitle;
	}
	
	
}
