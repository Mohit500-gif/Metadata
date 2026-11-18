package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EmailTriggerVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer journalId;
	private Integer userId;
	private List<Integer> userid;
	private Integer editorUserId;
	private Integer reviewerUserId;
	private Integer managingEditorId;
	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private Integer taskId;
	private Integer roleId;
	private Integer manuscriptStatus;
	private Integer receiverRoleId;
	private Integer isActive;
	private Integer authorUserId;

	private Integer urlManuscript;
	private Integer urlReviewerId;
	private Integer urlJournalId;
	private Integer urlManuscriptStatus;
	private Integer urlManuscriptVersion;
	private String acceptByReviewerUrl;
	private String rejectByReviewerUrl;
	private String viewByReviewerUrl;

	private String emailTemplateBody;
	
	private String emailTemplateSubject;
	private String journalMailTemplatesSubject;

	private Date extendDate;
	
	private String commentToAuthor;
	private String commentToEditor;
	private String copyRightUrlForCoAuthor;
	private String journalMailTemplatesBody;
	
	private Integer authorityId;
	private Integer manuscriptStatusId;
	private String module_name;
	private Integer manuscriptReviewerId;
    private String eicFolderName;	
    private Date reviewerDueDate;
    
    

	public Date getReviewerDueDate() {
		return reviewerDueDate;
	}

	public void setReviewerDueDate(Date reviewerDueDate) {
		this.reviewerDueDate = reviewerDueDate;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getEditorUserId() {
		return editorUserId;
	}

	public void setEditorUserId(Integer editorUserId) {
		this.editorUserId = editorUserId;
	}

	public Integer getManagingEditorId() {
		return managingEditorId;
	}

	public void setManagingEditorId(Integer managingEditorId) {
		this.managingEditorId = managingEditorId;
	}

	public Integer getReviewerUserId() {
		return reviewerUserId;
	}

	public void setReviewerUserId(Integer reviewerUserId) {
		this.reviewerUserId = reviewerUserId;
	}

	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public Integer getReceiverRoleId() {
		return receiverRoleId;
	}

	public void setReceiverRoleId(Integer receiverRoleId) {
		this.receiverRoleId = receiverRoleId;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getUrlManuscript() {
		return urlManuscript;
	}

	public void setUrlManuscript(Integer urlManuscript) {
		this.urlManuscript = urlManuscript;
	}

	public Integer getUrlReviewerId() {
		return urlReviewerId;
	}

	public void setUrlReviewerId(Integer urlReviewerId) {
		this.urlReviewerId = urlReviewerId;
	}

	public Integer getUrlJournalId() {
		return urlJournalId;
	}

	public void setUrlJournalId(Integer urlJournalId) {
		this.urlJournalId = urlJournalId;
	}

	public Integer getUrlManuscriptStatus() {
		return urlManuscriptStatus;
	}

	public void setUrlManuscriptStatus(Integer urlManuscriptStatus) {
		this.urlManuscriptStatus = urlManuscriptStatus;
	}

	public Integer getUrlManuscriptVersion() {
		return urlManuscriptVersion;
	}

	public void setUrlManuscriptVersion(Integer urlManuscriptVersion) {
		this.urlManuscriptVersion = urlManuscriptVersion;
	}

	public String getAcceptByReviewerUrl() {
		return acceptByReviewerUrl;
	}

	public void setAcceptByReviewerUrl(String acceptByReviewerUrl) {
		this.acceptByReviewerUrl = acceptByReviewerUrl;
	}

	public String getRejectByReviewerUrl() {
		return rejectByReviewerUrl;
	}

	public void setRejectByReviewerUrl(String rejectByReviewerUrl) {
		this.rejectByReviewerUrl = rejectByReviewerUrl;
	}

	public String getViewByReviewerUrl() {
		return viewByReviewerUrl;
	}

	public void setViewByReviewerUrl(String viewByReviewerUrl) {
		this.viewByReviewerUrl = viewByReviewerUrl;
	}

	public String getEmailTemplateBody() {
		return emailTemplateBody;
	}

	public void setEmailTemplateBody(String emailTemplateBody) {
		this.emailTemplateBody = emailTemplateBody;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Date getExtendDate() {
		return extendDate;
	}

	public void setExtendDate(Date extendDate) {
		this.extendDate = extendDate;
	}

	public List<Integer> getUserid() {
		return userid;
	}

	public void setUserid(List<Integer> userid) {
		this.userid = userid;
	}

	public String getCommentToAuthor() {
		return commentToAuthor;
	}

	public void setCommentToAuthor(String commentToAuthor) {
		this.commentToAuthor = commentToAuthor;
	}

	public Integer getAuthorUserId() {
		return authorUserId;
	}

	public void setAuthorUserId(Integer authorUserId) {
		this.authorUserId = authorUserId;
	}

	public String getCopyRightUrlForCoAuthor() {
		return copyRightUrlForCoAuthor;
	}

	public void setCopyRightUrlForCoAuthor(String copyRightUrlForCoAuthor) {
		this.copyRightUrlForCoAuthor = copyRightUrlForCoAuthor;
	}

	public String getJournalMailTemplatesBody() {
		return journalMailTemplatesBody;
	}

	public void setJournalMailTemplatesBody(String journalMailTemplatesBody) {
		this.journalMailTemplatesBody = journalMailTemplatesBody;
	}

	public String getCommentToEditor() {
		return commentToEditor;
	}

	public void setCommentToEditor(String commentToEditor) {
		this.commentToEditor = commentToEditor;
	}
	
	public String getEmailTemplateSubject() {
		return emailTemplateSubject;
	}

	public void setEmailTemplateSubject(String emailTemplateSubject) {
		this.emailTemplateSubject = emailTemplateSubject;
	}
	
	public String getJournalMailTemplatesSubject() {
		return journalMailTemplatesSubject;
	}

	public void setJournalMailTemplatesSubject(String journalMailTemplatesSubject) {
		this.journalMailTemplatesSubject = journalMailTemplatesSubject;
	}

	public Integer getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(Integer authorityId) {
		this.authorityId = authorityId;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getManuscriptReviewerId() {
		return manuscriptReviewerId;
	}

	public void setManuscriptReviewerId(Integer manuscriptReviewerId) {
		this.manuscriptReviewerId = manuscriptReviewerId;
	}
	
	public String getEicFolderName() {
		return eicFolderName;
	}

	public void setEicFolderName(String eicFolderName) {
		this.eicFolderName = eicFolderName;
	}

	@Override
	public String toString() {
		return "EmailTriggerVO [journalId=" + journalId + ", userId=" + userId + ", userid=" + userid
				+ ", editorUserId=" + editorUserId + ", reviewerUserId=" + reviewerUserId + ", managingEditorId="
				+ managingEditorId + ", manuscriptId=" + manuscriptId + ", manuscriptVersion=" + manuscriptVersion
				+ ", taskId=" + taskId + ", roleId=" + roleId + ", manuscriptStatus=" + manuscriptStatus
				+ ", receiverRoleId=" + receiverRoleId + ", isActive=" + isActive + ", authorUserId=" + authorUserId
				+ ", urlManuscript=" + urlManuscript + ", urlReviewerId=" + urlReviewerId + ", urlJournalId="
				+ urlJournalId + ", urlManuscriptStatus=" + urlManuscriptStatus + ", urlManuscriptVersion="
				+ urlManuscriptVersion + ", acceptByReviewerUrl=" + acceptByReviewerUrl + ", rejectByReviewerUrl="
				+ rejectByReviewerUrl + ", viewByReviewerUrl=" + viewByReviewerUrl + ", emailTemplateBody="
				+ emailTemplateBody + ", emailTemplateSubject=" + emailTemplateSubject
				+ ", journalMailTemplatesSubject=" + journalMailTemplatesSubject + ", extendDate=" + extendDate
				+ ", commentToAuthor=" + commentToAuthor + ", commentToEditor=" + commentToEditor
				+ ", copyRightUrlForCoAuthor=" + copyRightUrlForCoAuthor + ", journalMailTemplatesBody="
				+ journalMailTemplatesBody + ", authorityId=" + authorityId + ", manuscriptStatusId="
				+ manuscriptStatusId + ", module_name=" + module_name + ", manuscriptReviewerId=" + manuscriptReviewerId
				+ ", eicFolderName=" + eicFolderName + ", reviewerDueDate=" + reviewerDueDate + "]";
	}

	
	
}