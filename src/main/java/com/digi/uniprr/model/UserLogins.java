package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_logins")
public class UserLogins {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@OneToOne
	@JoinColumn(name="user_id",insertable = false, updatable = false)
	private User user;
	
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "user_role")
	private String userRole;

	@Column(name = "journal_id")
	private Integer journalId;

	@Column(name = "status")
	private Integer status;

	@Column(name = "logged_in_date")
	private Date loggedInDate;

	@Column(name = "logged_out_date")
	private Date loggedOutDate;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "currently")
	private String currently;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getLoggedInDate() {
		return loggedInDate;
	}

	public void setLoggedInDate(Date loggedInDate) {
		this.loggedInDate = loggedInDate;
	}

	public Date getLoggedOutDate() {
		return loggedOutDate;
	}

	public void setLoggedOutDate(Date loggedOutDate) {
		this.loggedOutDate = loggedOutDate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCurrently() {
		return currently;
	}

	public void setCurrently(String currently) {
		this.currently = currently;
	}

	@Override
	public String toString() {
		return "UserLogins [id=" + id + ", user=" + user + ", userId=" + userId + ", userRole=" + userRole
				+ ", journalId=" + journalId + ", status=" + status + ", loggedInDate=" + loggedInDate
				+ ", loggedOutDate=" + loggedOutDate + ", ipAddress=" + ipAddress + ", currently=" + currently + "]";
	}

}
