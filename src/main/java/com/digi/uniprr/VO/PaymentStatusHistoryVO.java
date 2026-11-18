package com.digi.uniprr.VO;

import java.util.Date;


public class PaymentStatusHistoryVO {
	
	private Integer id;

	private Integer journalId;
	
	private Integer manuscriptId;
	
	private Integer manuscriptStatusId;
	
	private Integer userId;
	
	private String ipAddress;
	
	private String comment;
	
	private Date craetedDate;

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

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCraetedDate() {
		return craetedDate;
	}

	public void setCraetedDate(Date craetedDate) {
		this.craetedDate = craetedDate;
	}

	@Override
	public String toString() {
		return "PaymentStatusHistoryVO [id=" + id + ", journalId=" + journalId + ", manuscriptId=" + manuscriptId
				+ ", manuscriptStatusId=" + manuscriptStatusId + ", userId=" + userId + ", ipAddress=" + ipAddress
				+ ", comment=" + comment + ", craetedDate=" + craetedDate + "]";
	}
		
}
