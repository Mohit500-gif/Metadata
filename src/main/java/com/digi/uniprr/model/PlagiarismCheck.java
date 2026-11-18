package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "plagiarism_check")
public class PlagiarismCheck {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name = "file_number")
	private Integer fileNubmer;
	
	@Column(name = "session_id")
	private String sessionId;
	
	@Column(name = "file_id")
	private String fileId;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "doc_id")
	private String docId;
	
	
	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name="created_on")
	private Date createdDate;
	
    @Column(name = "retry_count")
    private Integer retryCount = 0; 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Integer getFileNubmer() {
		return fileNubmer;
	}

	public void setFileNubmer(Integer fileNubmer) {
		this.fileNubmer = fileNubmer;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	@Override
	public String toString() {
		return "PlagiarismCheck [id=" + id + ", journalId=" + journalId + ", manuscriptId=" + manuscriptId
				+ ", manuscriptVersion=" + manuscriptVersion + ", fileNubmer=" + fileNubmer + ", sessionId=" + sessionId
				+ ", fileId=" + fileId + ", url=" + url + ", docId=" + docId + ", isActive=" + isActive
				+ ", createdDate=" + createdDate + ", retryCount=" + retryCount + "]";
	}

	

}
