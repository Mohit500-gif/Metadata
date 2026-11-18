package com.digi.uniprr.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "letter_to_editor")
public class LetterToEditor {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@OneToOne
	@JoinColumn(name = "manuscript_id", updatable = false, insertable = false)
	private ManuScript manuScript;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "user_role")
	private Integer userRole;
	
	@Column(name = "editor_id")
	private Integer editorId;
	
	@Column(name = "editor_role")
	private Integer editorRole;
	
	@Column(name = "created_on")
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "approved_on")
	private Date approvedDate;

	@Column(name = "reject_on")
	private Date rejectDate;

	@Column(name = "action")
	private String action;

	@Column(name ="file_path")
	private String filePath;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "file_size")
	private Integer fileSize;

	@Column(name ="file_name")
	private String fileName;

	@Column(name ="submitted_date")
	private Date submittedDate;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	public Integer getEditorId() {
		return editorId;
	}

	public void setEditorId(Integer editorId) {
		this.editorId = editorId;
	}

	public Integer getEditorRole() {
		return editorRole;
	}

	public void setEditorRole(Integer editorRole) {
		this.editorRole = editorRole;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public ManuScript getManuScript() {
		return manuScript;
	}

	public void setManuScript(ManuScript manuScript) {
		this.manuScript = manuScript;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Date getRejectDate() {
		return rejectDate;
	}

	public void setRejectDate(Date rejectDate) {
		this.rejectDate = rejectDate;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}
}
