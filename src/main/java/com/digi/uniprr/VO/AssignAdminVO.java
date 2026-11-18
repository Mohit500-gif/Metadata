package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;

public class AssignAdminVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer journalId;
	
	private Integer role;
	
	private List<Integer> userId;

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public List<Integer> getUserId() {
		return userId;
	}

	public void setUserId(List<Integer> userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "AssignAdminVO [journalId=" + journalId + ", role=" + role + ", userId=" + userId + "]";
	}
	
	
	
}
