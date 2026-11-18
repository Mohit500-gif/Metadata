package com.digi.uniprr.model;

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

import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "user_role")
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "role_id", updatable = false, insertable = false)
	private Role roleId;
	
	@Column(name = "role_id")
	private Integer role;
	
	@ManyToMany(mappedBy = "userRole")
    private List<User> userData;

	/*
	 * @OneToOne
	 * 
	 * @JoinTable(name = "id") private User userDetails;
	 */

	@Column(name = "usr_id")
	private Integer user;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "is_active")
	private Integer isActive;

	@Column(name = "updated_on")
	@CreatedDate
	private Date updatedOn;
	
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

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Role getRoleId() {
		return roleId;
	}

	public void setRoleId(Role roleId) {
		this.roleId = roleId;
	}

	/*
	 * public User getUserDetails() { return userDetails; }
	 * 
	 * public void setUserDetails(User userDetails) { this.userDetails =
	 * userDetails; }
	 */

}
