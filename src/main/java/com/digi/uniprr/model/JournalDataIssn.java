package com.digi.uniprr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "journal_data_issn")
public class JournalDataIssn {
	
	@Id
	@Column(name="journal_issn_url")
	private  String journalIssnUrl;
	
	@Column(name="info")
	private String info;

	public String getJournalIssnUrl() {
		return journalIssnUrl;
	}

	public void setJournalIssnUrl(String journalIssnUrl) {
		this.journalIssnUrl = journalIssnUrl;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	

}
