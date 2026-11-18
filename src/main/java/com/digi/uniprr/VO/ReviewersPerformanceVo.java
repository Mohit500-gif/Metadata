package com.digi.uniprr.VO;

import java.util.List;


public class ReviewersPerformanceVo {

	private Integer totalReferees;
	private Integer numberOfReviewersUsedArticle;
	private Integer numberOfReviewersResponded;
	private Integer reviewersOnTime;
	private Integer returnedLate;
	private Integer neverReturned;
	private Integer requiredReminders;
	private Integer reviewersDeclined;
	private Integer reviewersRemoved;
	private Integer reviewersTerminated;
	private List<AuthorCountryVo> show;
	private List<ManuScriptReviewerCommonVO> getIndividualRefereePerformance;
	private List<ReviewerPerformanceDTO> getIndividualRefereePerformances;
	private List<UserReportVO> user;
	private List<UserReportVO> reviewerNo;
	
	

	public List<UserReportVO> getReviewerNo() {
		return reviewerNo;
	}

	public void setReviewerNo(List<UserReportVO> reviewerNo) {
		this.reviewerNo = reviewerNo;
	}

	public Integer getTotalReferees() {
		return totalReferees;
	}

	public void setTotalReferees(Integer totalReferees) {
		this.totalReferees = totalReferees;
	}

	public Integer getNumberOfReviewersUsedArticle() {
		return numberOfReviewersUsedArticle;
	}

	public void setNumberOfReviewersUsedArticle(Integer numberOfReviewersUsedArticle) {
		this.numberOfReviewersUsedArticle = numberOfReviewersUsedArticle;
	}

	public Integer getNumberOfReviewersResponded() {
		return numberOfReviewersResponded;
	}

	public void setNumberOfReviewersResponded(Integer numberOfReviewersResponded) {
		this.numberOfReviewersResponded = numberOfReviewersResponded;
	}

	public Integer getReviewersOnTime() {
		return reviewersOnTime;
	}

	public void setReviewersOnTime(Integer reviewersOnTime) {
		this.reviewersOnTime = reviewersOnTime;
	}

	public Integer getNeverReturned() {
		return neverReturned;
	}

	public void setNeverReturned(Integer neverReturned) {
		this.neverReturned = neverReturned;
	}
	

	public Integer getReviewersDeclined() {
		return reviewersDeclined;
	}

	public void setReviewersDeclined(Integer reviewersDeclined) {
		this.reviewersDeclined = reviewersDeclined;
	}

	public Integer getReviewersRemoved() {
		return reviewersRemoved;
	}

	public void setReviewersRemoved(Integer reviewersRemoved) {
		this.reviewersRemoved = reviewersRemoved;
	}

	public Integer getReviewersTerminated() {
		return reviewersTerminated;
	}

	public void setReviewersTerminated(Integer reviewersTerminated) {
		this.reviewersTerminated = reviewersTerminated;
	}

	public Integer getRequiredReminders() {
		return requiredReminders;
	}

	public void setRequiredReminders(Integer requiredReminders) {
		this.requiredReminders = requiredReminders;
	}

	public List<AuthorCountryVo> getShow() {
		return show;
	}

	public void setShow(List<AuthorCountryVo> show) {
		this.show = show;
	}

	public List<ManuScriptReviewerCommonVO> getGetIndividualRefereePerformance() {
		return getIndividualRefereePerformance;
	}

	public void setGetIndividualRefereePerformance(List<ManuScriptReviewerCommonVO> getIndividualRefereePerformance) {
		this.getIndividualRefereePerformance = getIndividualRefereePerformance;
	}

	public Integer getReturnedLate() {
		return returnedLate;
	}

	public void setReturnedLate(Integer returnedLate) {
		this.returnedLate = returnedLate;
	}

	public List<ReviewerPerformanceDTO> getGetIndividualRefereePerformances() {
		return getIndividualRefereePerformances;
	}

	public void setGetIndividualRefereePerformances(List<ReviewerPerformanceDTO> getIndividualRefereePerformances) {
		this.getIndividualRefereePerformances = getIndividualRefereePerformances;
	}

	public List<UserReportVO> getUser() {
		return user;
	}

	public void setUser(List<UserReportVO> user) {
		this.user = user;
	}
	

}
