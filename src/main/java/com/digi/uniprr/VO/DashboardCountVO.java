package com.digi.uniprr.VO;

import java.io.Serializable;

public class DashboardCountVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer invitationCount;

	private Integer reviewCount;

	private Integer revisionCount;

	private Integer completedCount;

	public Integer getInvitationCount() {
		return invitationCount;
	}

	public void setInvitationCount(Integer invitationCount) {
		this.invitationCount = invitationCount;
	}

	public Integer getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}

	public Integer getRevisionCount() {
		return revisionCount;
	}

	public void setRevisionCount(Integer revisionCount) {
		this.revisionCount = revisionCount;
	}

	public Integer getCompletedCount() {
		return completedCount;
	}

	public void setCompletedCount(Integer completedCount) {
		this.completedCount = completedCount;
	}

	@Override
	public String toString() {
		return "DashboardCountVO [invitationCount=" + invitationCount + ", reviewCount=" + reviewCount
				+ ", revisionCount=" + revisionCount + ", completedCount=" + completedCount + "]";
	}

}