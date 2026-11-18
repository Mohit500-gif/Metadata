package com.digi.uniprr.VO;

import java.io.Serializable;

public class ContactUsVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private  Integer id;
	private  String title;
	private  String roleType;
	private  String firstName;
	private  String lastName;
	private  String email;
	private  String subject;
	private  String journalName;
	private  String message;
	private  Integer journalId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getJournalName() {
		return journalName;
	}
	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	@Override
	public String toString() {
		return "ContactUsVO [id=" + id + ", title=" + title + ", roleType=" + roleType + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", subject=" + subject + ", journalName="
				+ journalName + ", message=" + message + ", journalId=" + journalId + "]";
	}
	
	
	}
