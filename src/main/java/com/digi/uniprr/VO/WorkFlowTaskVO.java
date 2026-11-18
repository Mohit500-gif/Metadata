package com.digi.uniprr.VO;

public class WorkFlowTaskVO {

	private Integer id;
	private Integer workflowId;
	private String taskName;
	private String iconText;
	private Integer taskId;
	private Integer tat;
	private String emailFlag;
	private Integer createdBy;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public Integer getTat() {
		return tat;
	}
	public void setTat(Integer tat) {
		this.tat = tat;
	}
	public String getEmailFlag() {
		return emailFlag;
	}
	public void setEmailFlag(String emailFlag) {
		this.emailFlag = emailFlag;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getIconText() {
		return iconText;
	}
	public void setIconText(String iconText) {
		this.iconText = iconText;
	}
	
}
