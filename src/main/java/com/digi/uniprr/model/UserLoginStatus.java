package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "user_login_status")
public class UserLoginStatus {
	@Override
	public String toString() {
		return "UserLoginStatus{" +
				"id=" + id +
				", userId=" + userId +
				", journalId=" + journalId +
				", journalCode='" + journalCode + '\'' +
				", status='" + status + '\'' +
				", reason='" + reason + '\'' +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", roleId='" + roleId + '\'' +
				", ip='" + ip + '\'' +
				", url='" + url + '\'' +
				", date=" + date +
				'}';
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "journal_id")
	private Integer journalId;

	@Column(name = "journal_code")
	private String journalCode;

	@Column(name = "status")
	private String status;
	
	@Column(name="reason")
	private String reason;
	
	@Column(name="password")
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "role_id")
	private String roleId;

	@Column(name = "ip")
	private String ip;

	@Column(name = "url")
	private String url;

	@Column(name = "date")
	@CreationTimestamp
	private Date date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

}
