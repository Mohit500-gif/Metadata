package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class LetterToEditorVo implements Serializable {


	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "LetterToEditorVo{" +
				"id=" + id +
				", userId=" + userId +
				", journalId=" + journalId +
				", manuscriptId=" + manuscriptId +
				", lteManuscriptId=" + lteManuscriptId +
				", authorId=" + authorId +
				", userRole=" + userRole +
				", editorId=" + editorId +
				", editorRole=" + editorRole +
				", templateId=" + templateId +
				", journalMailTemplatesBody='" + journalMailTemplatesBody + '\'' +
				", journalMailTemplatesSubject='" + journalMailTemplatesSubject + '\'' +
				", manuscriptStatusId=" + manuscriptStatusId +
				", manuscriptVersion=" + manuscriptVersion +
				", manuscriptCode='" + manuscriptCode + '\'' +
				", manuscriptTitle='" + manuscriptTitle + '\'' +
				", manuscriptType='" + manuscriptType + '\'' +
				", createdOn=" + createdOn +
				", approvedOn=" + approvedOn +
				", rejectOn=" + rejectOn +
				", action='" + action + '\'' +
				", filePath='" + filePath + '\'' +
				", fileType='" + fileType + '\'' +
				", fileSize=" + fileSize +
				", fileName='" + fileName + '\'' +
				", sentEditorName='" + sentEditorName + '\'' +
				", sentEditorEmail='" + sentEditorEmail + '\'' +
				", submittedDate=" + submittedDate +
				", statusCode='" + statusCode + '\'' +
				", userName='" + userName + '\'' +
				", roleName='" + roleName + '\'' +
				", templateName='" + templateName + '\'' +
				'}';
	}

	private Integer id;
	
	private Integer userId;

	private Integer journalId;
	
	private Integer manuscriptId;

	private Integer lteManuscriptId;

	private Integer authorId;

	private Integer userRole;

	private Integer editorId;

	private Integer editorRole;

	private Integer templateId;

	private String journalMailTemplatesBody;

	private String journalMailTemplatesSubject;

	private Integer manuscriptStatusId;

	private Integer manuscriptVersion;

	private String manuscriptCode;

	private String manuscriptTitle;

	private String manuscriptType;

	private Date createdOn;

	private Date approvedOn;

	private Date rejectOn;

	private String action;

	private String filePath;

	private String fileType;

	private Integer fileSize;

	private String fileName;

	private String sentEditorName;

	private String sentEditorEmail;

	private Date submittedDate;

	private String statusCode;

	private String userName;

	private String roleName;

	private String templateName;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public String getManuscriptTitle() {
		return manuscriptTitle;
	}

	public void setManuscriptTitle(String manuscriptTitle) {
		this.manuscriptTitle = manuscriptTitle;
	}

	public String getManuscriptType() {
		return manuscriptType;
	}

	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getSentEditorName() {
		return sentEditorName;
	}

	public void setSentEditorName(String sentEditorName) {
		this.sentEditorName = sentEditorName;
	}

	public String getSentEditorEmail() {
		return sentEditorEmail;
	}

	public void setSentEditorEmail(String sentEditorEmail) {
		this.sentEditorEmail = sentEditorEmail;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getJournalMailTemplatesBody() {
		return journalMailTemplatesBody;
	}

	public void setJournalMailTemplatesBody(String journalMailTemplatesBody) {
		this.journalMailTemplatesBody = journalMailTemplatesBody;
	}

	public String getJournalMailTemplatesSubject() {
		return journalMailTemplatesSubject;
	}

	public void setJournalMailTemplatesSubject(String journalMailTemplatesSubject) {
		this.journalMailTemplatesSubject = journalMailTemplatesSubject;
	}

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Date getRejectOn() {
		return rejectOn;
	}

	public void setRejectOn(Date rejectOn) {
		this.rejectOn = rejectOn;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

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

	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getLteManuscriptId() {
		return lteManuscriptId;
	}

	public void setLteManuscriptId(Integer lteManuscriptId) {
		this.lteManuscriptId = lteManuscriptId;
	}
}
