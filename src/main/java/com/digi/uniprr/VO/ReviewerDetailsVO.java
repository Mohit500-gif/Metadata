package com.digi.uniprr.VO;

import java.util.Date;

public class ReviewerDetailsVO {
	private String name;
	private String status;
	private Date dueDate;
	private Integer isActive;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Integer getIsActive() { return isActive;	}
	public void setIsActive(Integer isActive) {	this.isActive = isActive;}


}
