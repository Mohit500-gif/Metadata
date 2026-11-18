package com.digi.uniprr.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users_journals")
public class UserJournal implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "journal_id", insertable = false, updatable = false)
	private Journal journal;

	@Column(name = "journal_id")
	private Integer journalId;

//	@OneToOne
//	@JoinColumn(name = "role_id", insertable = false, updatable = false)
	@Transient
	@JsonIgnore
	private Role role;

	@OneToOne
	@JoinColumn(name = "role_id", updatable = false, insertable = false)
	private Role role_id;
	
	@Column(name = "role_id")
	private Integer roleId;

	@OneToOne
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@ManyToMany(mappedBy = "UserJournal")
	private List<User> userData;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name="reason_for_delete")
	private String reasonForDelete;

	public List<User> getUserData() {
		return userData;
	}

	public void setUserData(List<User> userData) {
		this.userData = userData;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
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

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	
	public String getReasonForDelete() {
		return reasonForDelete;
	}

	public void setReasonForDelete(String reasonForDelete) {
		this.reasonForDelete = reasonForDelete;
	}
	

	@Override
	public String toString() {
		return "UserJournal [id=" + id + ", journal=" + journal + ", journalId=" + journalId + ", role=" + role
				+ ", roleId=" + roleId + ", user=" + user + ", userData=" + userData + ", userId=" + userId
				+ ", updatedBy=" + updatedBy + ", updatedDate=" + updatedDate + ", isActive=" + isActive
				+ ", reasonForDelete=" + reasonForDelete + "]";
	}

	public Role getRole_id() {
		return role_id;
	}

	public void setRole_id(Role role_id) {
		this.role_id = role_id;
	}


}
