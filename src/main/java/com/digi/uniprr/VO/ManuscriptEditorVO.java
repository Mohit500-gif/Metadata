package com.digi.uniprr.VO;

import org.springframework.web.multipart.MultipartFile;

public class ManuscriptEditorVO {

	private Integer manuscriptId;
	private String manuscriptCode;
	private Integer statusCode;

	@Override
	public String toString() {
		return "ManuscriptEditorVO{" +
				"manuscriptId=" + manuscriptId +
				", manuscriptCode='" + manuscriptCode + '\'' +
				", statusCode=" + statusCode +
				", manuscriptVersion=" + manuscriptVersion +
				", comments='" + comments + '\'' +
				", userId=" + userId +
				", roleId=" + roleId +
				", journalMailTemplatesBody='" + journalMailTemplatesBody + '\'' +
				", journalMailTemplatesSubject='" + journalMailTemplatesSubject + '\'' +
				", acceptForProductionOnAdmin='" + acceptForProductionOnAdmin + '\'' +
				", file=" + file +
				", waveOff='" + waveOff + '\'' +
				", managingEditorId=" + managingEditorId +
				", journalId=" + journalId +
				", editorId=" + editorId +
				", manuscriptStatus=" + manuscriptStatus +
				", commentsForAuthor='" + commentsForAuthor + '\'' +
				", taskId=" + taskId +
				", paymentDecline='" + paymentDecline + '\'' +
				'}';
	}

	private Integer manuscriptVersion;
	private String comments;
	private Integer userId;
	private Integer roleId;
	private String journalMailTemplatesBody;
	private String journalMailTemplatesSubject;
	private String acceptForProductionOnAdmin;
	MultipartFile file;
	private String waveOff;
	private Integer managingEditorId;
	private Integer journalId;
	private Integer editorId;
	private Integer manuscriptStatus;
	private String commentsForAuthor;
	private Integer taskId;
	private String paymentDecline;
	
	
	
	
	public Integer getManagingEditorId() {
		return managingEditorId;
	}
	public void setManagingEditorId(Integer managingEditorId) {
		this.managingEditorId = managingEditorId;
	}
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public Integer getEditorId() {
		return editorId;
	}
	public void setEditorId(Integer editorId) {
		this.editorId = editorId;
	}
	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}
	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}
	public String getCommentsForAuthor() {
		return commentsForAuthor;
	}
	public void setCommentsForAuthor(String commentsForAuthor) {
		this.commentsForAuthor = commentsForAuthor;
	}
	public Integer getManuscriptId() {
		return manuscriptId;
	}
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}
	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}
	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getManuscriptCode() {
		return manuscriptCode;
	}
	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}
	public String getJournalMailTemplatesBody() {
		return journalMailTemplatesBody;
	}
	public void setJournalMailTemplatesBody(String journalMailTemplatesBody) {
		this.journalMailTemplatesBody = journalMailTemplatesBody;
	}
	public String getAcceptForProductionOnAdmin() {
		return acceptForProductionOnAdmin;
	}
	public void setAcceptForProductionOnAdmin(String acceptForProductionOnAdmin) {
		this.acceptForProductionOnAdmin = acceptForProductionOnAdmin;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getWaveOff() {
		return waveOff;
	}
	public void setWaveOff(String waveOff) {
		this.waveOff = waveOff;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public String getJournalMailTemplatesSubject() {
		return journalMailTemplatesSubject;
	}
	public void setJournalMailTemplatesSubject(String journalMailTemplatesSubject) {
		this.journalMailTemplatesSubject = journalMailTemplatesSubject;
	}
	public String getPaymentDecline() {
		return paymentDecline;
	}
	public void setPaymentDecline(String paymentDecline) {
		this.paymentDecline = paymentDecline;
	}
	
	
	
}
