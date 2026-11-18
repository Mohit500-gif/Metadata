package com.digi.uniprr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name= "editor_role_access")
public class EditorRoleAccess {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "editor_role_id")
	private Integer editorRoleId;
	
	@Column(name = "editor_access_id")
	private String editorAccess;
	
	@Column(name = "is_active")
	private Integer isActive;
	
	
	@OneToOne
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

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

	public Integer getEditorRoleId() {
		return editorRoleId;
	}

	public void setEditorRoleId(Integer editorRoleId) {
		this.editorRoleId = editorRoleId;
	}

	public String getEditorAccess() {
		return editorAccess;
	}

	public void setEditorAccess(String editorAccess) {
		this.editorAccess = editorAccess;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
