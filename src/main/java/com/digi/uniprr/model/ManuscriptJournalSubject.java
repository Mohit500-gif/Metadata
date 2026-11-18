package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="manuscript_journal_subject")
public class ManuscriptJournalSubject {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@OneToOne
	@JoinColumn(name = "journal_subject_id", updatable = false, insertable = false)
	private JournalSubject journalSubject;
	
	@Column(name = "journal_subject_id")
	private Integer journalSubjectId;
	
	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name = "created_at")
	private Date createdAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public JournalSubject getJournalSubject() {
		return journalSubject;
	}

	public void setJournalSubject(JournalSubject journalSubject) {
		this.journalSubject = journalSubject;
	}

	public Integer getJournalSubjectId() {
		return journalSubjectId;
	}

	public void setJournalSubjectId(Integer journalSubjectId) {
		this.journalSubjectId = journalSubjectId;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
