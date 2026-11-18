package com.digi.uniprr.VO;

public class BundleStatusResponse {
    private String bundleStatus;
    private String failureReason;
	public String getBundleStatus() {
		return bundleStatus;
	}
	public void setBundleStatus(String bundleStatus) {
		this.bundleStatus = bundleStatus;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

    
}

