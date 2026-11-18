package com.digi.uniprr.VO;

import java.util.List;

public class PerformanceYearListVo {

	private List<PerformanceYearVo> performanceYearVo;
	private Integer totalSubmittedArticle;
	private Integer totalAccepted;
	private Integer totalRejected;
	private Integer totalUnderReview;
	private String totalAcceptedArticle;
	private String totalRejectedArticle;
	private String totalUnderReviewArticle;
	public List<PerformanceYearVo> getPerformanceYearVo() {
		return performanceYearVo;
	}
	public void setPerformanceYearVo(List<PerformanceYearVo> performanceYearVo) {
		this.performanceYearVo = performanceYearVo;
	}

	public Integer getTotalSubmittedArticle() {
		return totalSubmittedArticle;
	}
	public void setTotalSubmittedArticle(Integer totalSubmittedArticle) {
		this.totalSubmittedArticle = totalSubmittedArticle;
	}
	public Integer getTotalAccepted() {
		return totalAccepted;
	}
	public void setTotalAccepted(Integer totalAccepted) {
		this.totalAccepted = totalAccepted;
	}
	public Integer getTotalRejected() {
		return totalRejected;
	}
	public void setTotalRejected(Integer totalRejected) {
		this.totalRejected = totalRejected;
	}
	public Integer getTotalUnderReview() {
		return totalUnderReview;
	}
	public void setTotalUnderReview(Integer totalUnderReview) {
		this.totalUnderReview = totalUnderReview;
	}
	public String getTotalAcceptedArticle() {
		return totalAcceptedArticle;
	}
	public void setTotalAcceptedArticle(String totalAcceptedArticle) {
		this.totalAcceptedArticle = totalAcceptedArticle;
	}
	public String getTotalRejectedArticle() {
		return totalRejectedArticle;
	}
	public void setTotalRejectedArticle(String totalRejectedArticle) {
		this.totalRejectedArticle = totalRejectedArticle;
	}
	public String getTotalUnderReviewArticle() {
		return totalUnderReviewArticle;
	}
	public void setTotalUnderReviewArticle(String totalUnderReviewArticle) {
		this.totalUnderReviewArticle = totalUnderReviewArticle;
	}
	
}
