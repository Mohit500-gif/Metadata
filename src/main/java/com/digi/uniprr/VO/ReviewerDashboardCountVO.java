package com.digi.uniprr.VO;

import java.io.Serializable;

public class ReviewerDashboardCountVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer invitationCount;
	
	private Integer reviewCount;

	private Integer reReviewCount;

	private Integer reviewedCount;

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

	public Integer getReReviewCount() {
		return reReviewCount;
	}

	public void setReReviewCount(Integer reReviewCount) {
		this.reReviewCount = reReviewCount;
	}

	public Integer getReviewedCount() {
		return reviewedCount;
	}

	public void setReviewedCount(Integer reviewedCount) {
		this.reviewedCount = reviewedCount;
	}

	@Override
	public String toString() {
		return "ReviewerDashboardCountVO [invitationCount=" + invitationCount + ", reviewCount=" + reviewCount
				+ ", reReviewCount=" + reReviewCount + ", reviewedCount=" + reviewedCount + "]";
	}

	
	
}
