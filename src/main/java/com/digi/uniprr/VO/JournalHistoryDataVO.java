package com.digi.uniprr.VO;

public class JournalHistoryDataVO {

	private Integer journalId;

	private String section;

	private String userName;

	private String comment;

	private String charLimitTitle;
	
	private String charLimitAbstract;
	
	private String charLimitRunTitle;

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCharLimitTitle() {
		return charLimitTitle;
	}

	public void setCharLimitTitle(String charLimitTitle) {
		this.charLimitTitle = charLimitTitle;
	}

	public String getCharLimitAbstract() {
		return charLimitAbstract;
	}

	public void setCharLimitAbstract(String charLimitAbstract) {
		this.charLimitAbstract = charLimitAbstract;
	}

	public String getCharLimitRunTitle() {
		return charLimitRunTitle;
	}

	public void setCharLimitRunTitle(String charLimitRunTitle) {
		this.charLimitRunTitle = charLimitRunTitle;
	}

	
}
