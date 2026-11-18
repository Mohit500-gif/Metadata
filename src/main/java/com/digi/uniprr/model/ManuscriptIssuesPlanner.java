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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "manuscript_issues_planner")
public class ManuscriptIssuesPlanner {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "issue_id", insertable = false, updatable = false)
	private JournalIssuesPlanner journalIssuesPlanner;
	
	@Column(name = "issue_id")
	private Integer issueId;
	
	@OneToOne
	@JoinColumn(name = "manuscript_id", insertable = false, updatable = false)
	private ManuScript manuScript;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date")
	@CreationTimestamp
	private Date createdDate;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "updated_date")
	@UpdateTimestamp
	private Date updatedDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public JournalIssuesPlanner getJournalIssuesPlanner() {
		return journalIssuesPlanner;
	}

	public void setJournalIssuesPlanner(JournalIssuesPlanner journalIssuesPlanner) {
		this.journalIssuesPlanner = journalIssuesPlanner;
	}

	public Integer getIssueId() {
		return issueId;
	}

	public void setIssueId(Integer issueId) {
		this.issueId = issueId;
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

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public ManuScript getManuScript() {
		return manuScript;
	}

	public void setManuScript(ManuScript manuScript) {
		this.manuScript = manuScript;
	}

}
