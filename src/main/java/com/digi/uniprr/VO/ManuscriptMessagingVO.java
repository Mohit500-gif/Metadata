package com.digi.uniprr.VO;

import java.util.Date;

public class ManuscriptMessagingVO {

	private Integer id;

	private Integer manuscriptId;

	private Integer manuscriptVersion;
	
	private String manuscriptCode;
	
	private String recomandation;

	private Integer journalId;

	private Integer fromUserId;

	private Integer fromUserRole;

	private String fromUserName;

	private Integer toUserId;

	private Integer toUserRole;

	private String toUserName;

	private String message;

	private String type;

	private Integer isSeen;

	private Integer isActive;

	private Date createdAt;
	
	private String subject;
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRecomandation() {
		return recomandation;
	}

	public void setRecomandation(String recomandation) {
		this.recomandation = recomandation;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(Integer fromUserId) {
		this.fromUserId = fromUserId;
	}

	public Integer getFromUserRole() {
		return fromUserRole;
	}

	public void setFromUserRole(Integer fromUserRole) {
		this.fromUserRole = fromUserRole;
	}

	public Integer getToUserId() {
		return toUserId;
	}

	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}

	public Integer getToUserRole() {
		return toUserRole;
	}

	public void setToUserRole(Integer toUserRole) {
		this.toUserRole = toUserRole;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	

	public Integer getIsSeen() {
		return isSeen;
	}

	public void setIsSeen(Integer isSeen) {
		this.isSeen = isSeen;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}
	

}
