package com.digi.uniprr.dto;

import java.util.List;

public class EmailChangeRequest {


    private String oldEmail;

    private String newEmail;

    // optional: null means all journals
    private List<Integer> journalIds;

    // getters / setters
    public String getOldEmail() { return oldEmail; }
    public void setOldEmail(String oldEmail) { this.oldEmail = oldEmail; }

    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
	public List<Integer> getJournalIds() {
		return journalIds;
	}
	public void setJournalIds(List<Integer> journalIds) {
		this.journalIds = journalIds;
	}

    
}
