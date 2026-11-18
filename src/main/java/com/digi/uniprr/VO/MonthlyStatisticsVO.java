package com.digi.uniprr.VO;

import java.util.List;

public class MonthlyStatisticsVO {

	private Integer noOfArticlesSubmitted;
	private Integer noOfAuthorRegistered;
	private Integer noOfReviewersAdded;
	private Integer noOfArticlesReviewed;
	private List<IDVO> show;

	public Integer getNoOfArticlesSubmitted() {
		return noOfArticlesSubmitted;
	}

	public void setNoOfArticlesSubmitted(Integer noOfArticlesSubmitted) {
		this.noOfArticlesSubmitted = noOfArticlesSubmitted;
	}

	public Integer getNoOfAuthorRegistered() {
		return noOfAuthorRegistered;
	}

	public void setNoOfAuthorRegistered(Integer noOfAuthorRegistered) {
		this.noOfAuthorRegistered = noOfAuthorRegistered;
	}

	public Integer getNoOfReviewersAdded() {
		return noOfReviewersAdded;
	}

	public void setNoOfReviewersAdded(Integer noOfReviewersAdded) {
		this.noOfReviewersAdded = noOfReviewersAdded;
	}

	public Integer getNoOfArticlesReviewed() {
		return noOfArticlesReviewed;
	}

	public void setNoOfArticlesReviewed(Integer noOfArticlesReviewed) {
		this.noOfArticlesReviewed = noOfArticlesReviewed;
	}

	public List<IDVO> getShow() {
		return show;
	}

	public void setShow(List<IDVO> show) {
		this.show = show;
	}

}
