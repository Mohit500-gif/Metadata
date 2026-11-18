package com.digi.uniprr.VO;

import java.io.Serializable;

public class EditorRoleVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String role;
	
	private Integer editorAccess;
	
	private Integer isActive;

	public EditorRoleVo(Integer id, String role, Integer editorAccess, Integer isActive) {
		super();
		this.id = id;
		this.role = role;
		this.editorAccess = editorAccess;
		this.isActive = isActive;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getEditorAccess() {
		return editorAccess;
	}

	public void setEditorAccess(Integer editorAccess) {
		this.editorAccess = editorAccess;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "EditorRoleVo [id=" + id + ", role=" + role + ", editorAccess=" + editorAccess + ", isActive=" + isActive
				+ "]";
	}
	
}
