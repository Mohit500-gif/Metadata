package com.digi.uniprr.VO;

import java.io.Serializable;

public class BundleTrackerManuscriptVo implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private String errorMessage;
	private Integer manuscriptId;
	private String bundleStatus;

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Integer getManuscriptId() {
		return manuscriptId;
	}
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}
	public String getBundleStatus() {
		return bundleStatus;
	}
	public void setBundleStatus(String bundleStatus) {
		this.bundleStatus = bundleStatus;
	}
	@Override
	public String toString() {
		return "BundleTrackerManuscriptVo [errorMessage=" + errorMessage + ", manuscriptId=" + manuscriptId
				+ ", bundleStatus=" + bundleStatus + "]";
	}

}
