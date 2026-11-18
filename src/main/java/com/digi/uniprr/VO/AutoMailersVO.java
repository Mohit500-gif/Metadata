package com.digi.uniprr.VO;

import java.io.Serializable;

public class AutoMailersVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private  Integer taskMasterId;
	private  Integer journalMailTemplateId;
	private Integer journalId;
	private String emailTemplateForm;
	private Integer taskEmailMapId;
	private String taskName;
	private String emailTemplateName;
	private String emailTemplateBody;
	private Integer updatedBy;
	private Integer createdBy;
	
	public Integer getTaskMasterId() {
		return taskMasterId;
	}
	public void setTaskMasterId(Integer taskMasterId) {
		this.taskMasterId = taskMasterId;
	}
	public Integer getJournalMailTemplateId() {
		return journalMailTemplateId;
	}
	public void setJournalMailTemplateId(Integer journalMailTemplateId) {
		this.journalMailTemplateId = journalMailTemplateId;
	}
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public String getEmailTemplateForm() {
		return emailTemplateForm;
	}
	public void setEmailTemplateForm(String emailTemplateForm) {
		this.emailTemplateForm = emailTemplateForm;
	}
	public Integer getTaskEmailMapId() {
		return taskEmailMapId;
	}
	public void setTaskEmailMapId(Integer taskEmailMapId) {
		this.taskEmailMapId = taskEmailMapId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getEmailTemplateName() {
		return emailTemplateName;
	}
	public void setEmailTemplateName(String emailTemplateName) {
		this.emailTemplateName = emailTemplateName;
	}
	public Integer getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public String getEmailTemplateBody() {
		return emailTemplateBody;
	}
	public void setEmailTemplateBody(String emailTemplateBody) {
		this.emailTemplateBody = emailTemplateBody;
	}
	@Override
	public String toString() {
		return "AutoMailersVO [taskMasterId=" + taskMasterId + ", journalMailTemplateId=" + journalMailTemplateId
				+ ", journalId=" + journalId + ", emailTemplateForm=" + emailTemplateForm + ", taskEmailMapId="
				+ taskEmailMapId + ", taskName=" + taskName + ", emailTemplateName=" + emailTemplateName
				+ ", emailTemplateBody=" + emailTemplateBody + ", updatedBy=" + updatedBy + ", createdBy=" + createdBy
				+ "]";
	}
	
	
}
