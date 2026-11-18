package com.digi.uniprr.VO;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewerPerformanceDTO {

	@JsonProperty("reviewerId")
	private Integer reviewerId;
	@JsonProperty("refreename")
	private String refreename;
	@JsonProperty("totalArticlesReviewed")
    private String totalArticlesReviewed;
	@JsonProperty("avgDaysToCompleteReview")
    private String avgDaysToCompleteReview;
	@JsonProperty("reviewsOnTime")
    private String reviewsOnTime;
	@JsonProperty("reviewsLate")
    private String reviewsLate;
	@JsonProperty("reviewsNeverReturned")
    private String reviewsNeverReturned;
	@JsonProperty("avgRemindersSent")
    private String avgRemindersSent;
	@JsonProperty("avgRatingQuality")
    private String avgRatingQuality;
	@JsonProperty("avgRatingTimeliness")
    private String avgRatingTimeliness;

	public String getRefreename() {
		return refreename;
	}
	public void setRefreename(String refreename) {
		this.refreename = refreename;
	}
	public String getTotalArticlesReviewed() {
		return totalArticlesReviewed;
	}
	public void setTotalArticlesReviewed(String totalArticlesReviewed) {
		this.totalArticlesReviewed = totalArticlesReviewed;
	}
	public String getAvgDaysToCompleteReview() {
		return avgDaysToCompleteReview;
	}
	public void setAvgDaysToCompleteReview(String avgDaysToCompleteReview) {
		this.avgDaysToCompleteReview = avgDaysToCompleteReview;
	}
	public String getReviewsOnTime() {
		return reviewsOnTime;
	}
	public void setReviewsOnTime(String reviewsOnTime) {
		this.reviewsOnTime = reviewsOnTime;
	}
	public String getReviewsLate() {
		return reviewsLate;
	}
	public void setReviewsLate(String reviewsLate) {
		this.reviewsLate = reviewsLate;
	}
	public String getReviewsNeverReturned() {
		return reviewsNeverReturned;
	}
	public void setReviewsNeverReturned(String reviewsNeverReturned) {
		this.reviewsNeverReturned = reviewsNeverReturned;
	}
	public String getAvgRemindersSent() {
		return avgRemindersSent;
	}
	public void setAvgRemindersSent(String avgRemindersSent) {
		this.avgRemindersSent = avgRemindersSent;
	}
	public String getAvgRatingQuality() {
		return avgRatingQuality;
	}
	public void setAvgRatingQuality(String avgRatingQuality) {
		this.avgRatingQuality = avgRatingQuality;
	}
	public String getAvgRatingTimeliness() {
		return avgRatingTimeliness;
	}
	public void setAvgRatingTimeliness(String avgRatingTimeliness) {
		this.avgRatingTimeliness = avgRatingTimeliness;
	}
	public Integer getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
	}
	@Override
	public String toString() {
		return "ReviewerPerformanceDTO [reviewerId=" + reviewerId + ", refreename=" + refreename
				+ ", totalArticlesReviewed=" + totalArticlesReviewed + ", avgDaysToCompleteReview="
				+ avgDaysToCompleteReview + ", reviewsOnTime=" + reviewsOnTime + ", reviewsLate=" + reviewsLate
				+ ", reviewsNeverReturned=" + reviewsNeverReturned + ", avgRemindersSent=" + avgRemindersSent
				+ ", avgRatingQuality=" + avgRatingQuality + ", avgRatingTimeliness=" + avgRatingTimeliness + "]";
	}
	
	
 
    
    
}
