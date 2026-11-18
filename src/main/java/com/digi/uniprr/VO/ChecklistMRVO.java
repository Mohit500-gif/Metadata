package com.digi.uniprr.VO;

import java.io.Serializable;

public class ChecklistMRVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer chkId;
	private Integer chkType;
	private String chckComments;
	
	public Integer getChkId() {
		return chkId;
	}
	public void setChkId(Integer chkId) {
		this.chkId = chkId;
	}
	public Integer getChkType() {
		return chkType;
	}
	public void setChkType(Integer chkType) {
		this.chkType = chkType;
	}
	public String getChckComments() {
		return chckComments;
	}
	public void setChckComments(String chckComments) {
		this.chckComments = chckComments;
	}
	@Override
	public String toString() {
		return "ChecklistMRVO [chkId=" + chkId + ", chkType=" + chkType + ", chckComments=" + chckComments + "]";
	}

}
