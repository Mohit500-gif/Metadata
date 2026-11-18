package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;

public class CommonVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Integer> roleId;
	
	private List<KeywordsVO> reviewer;
	
	private Integer journalId;

	public List<Integer> getRoleId() {
		return roleId;
	}

	public void setRoleId(List<Integer> roleId) {
		this.roleId = roleId;
	}

	public List<KeywordsVO> getReviewer() {
		return reviewer;
	}

	public void setReviewer(List<KeywordsVO> reviewer) {
		this.reviewer = reviewer;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	@Override
	public String toString() {
		return "CommonVO [roleId=" + roleId + ", reviewer=" + reviewer + ", journalId=" + journalId + "]";
	}
	
	
}
