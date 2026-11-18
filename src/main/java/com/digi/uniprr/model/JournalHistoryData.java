package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "journal_hist")
public class JournalHistoryData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "journal_history_id")
	private Integer journalHistoryId;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "section")
	private String section;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "comment")
	private String comment;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "updated_on")
	@CreationTimestamp
	private Date updatedOn;


	public Integer getJournalHistoryId() {
		return journalHistoryId;
	}

	public void setJournalHistoryId(Integer journalHistoryId) {
		this.journalHistoryId = journalHistoryId;
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

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	
	
}
