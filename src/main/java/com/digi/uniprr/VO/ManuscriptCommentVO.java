package com.digi.uniprr.VO;

import java.util.Date;

public class ManuscriptCommentVO {

	private Long id;
	private Integer manuscriptId;
	private String authorEmail;
	private Integer taskId;
	private String taskName;
	private String comment;
	private Date createdOn;
	private String uniprrTask;
	
	
	public ManuscriptCommentVO() {}
	public ManuscriptCommentVO(Long id, Integer manuscriptId, String authorEmail, Integer taskId, String taskName,
			String comment, Date createdOn, String uniprrTask) {
		super();
		this.id = id;
		this.manuscriptId = manuscriptId;
		this.authorEmail = authorEmail;
		this.taskId = taskId;
		this.taskName = taskName;
		this.comment = comment;
		this.createdOn = createdOn;
		this.uniprrTask = uniprrTask;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getUniprrTask() {
		return uniprrTask;
	}
	public void setUniprrTask(String uniprrTask) {
		this.uniprrTask = uniprrTask;
	}
}
