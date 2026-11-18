package com.digi.uniprr.VO;

import java.io.Serializable;

public class CheckListVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String chkKey;
	private Integer isActive;
	public String getChkKey() {
		return chkKey;
	}
	public void setChkKey(String chkKey) {
		this.chkKey = chkKey;
	}
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	@Override
	public String toString() {
		return "CheckListVO [chkKey=" + chkKey + ", isActive=" + isActive + "]";
	}
	
}
