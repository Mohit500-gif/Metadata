package com.digi.uniprr.VO;

import java.util.Date;

public class ReviewerReportDataVO {

	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private Integer reviewerId;
	private Integer manuscriptStatus;
	private Integer isActive;
	private Date dueDate;
	private Date assignedDate;
	private Date completeDate;
	private String reviewerName;
	public ReviewerReportDataVO(Integer manuscriptId, Integer manuscriptVersion, Integer reviewerId, Date assignedDate , Date dueDate) {
		this.manuscriptId = manuscriptId;
		this.manuscriptVersion = manuscriptVersion;
		this.reviewerId = reviewerId;
		this.assignedDate = assignedDate;
		this.dueDate = dueDate;
	}
	
	public ReviewerReportDataVO(Integer manuscriptId, Integer manuscriptVersion, Integer reviewerId, Integer manuscriptStatus, Integer isActive , Date dueDate) {
		this.manuscriptId = manuscriptId;
		this.manuscriptVersion = manuscriptVersion;
		this.reviewerId = reviewerId;
		this.manuscriptStatus = manuscriptStatus;
		this.isActive = isActive;
		this.dueDate = dueDate;
	}
	
	public ReviewerReportDataVO(Integer manuscriptId, Integer manuscriptVersion, Integer reviewerId, Integer manuscriptStatus, Integer isActive , Date dueDate, Date assignedDate, Date completeDate, String reviewerName) {
		this.manuscriptId = manuscriptId;
		this.manuscriptVersion = manuscriptVersion;
		this.reviewerId = reviewerId;
		this.manuscriptStatus = manuscriptStatus;
		this.isActive = isActive;
		this.dueDate = dueDate;
		this.assignedDate = assignedDate;
		this.completeDate = completeDate;
		this.reviewerName = reviewerName;
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
	public Integer getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
	}
	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}
	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}
	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}

}
