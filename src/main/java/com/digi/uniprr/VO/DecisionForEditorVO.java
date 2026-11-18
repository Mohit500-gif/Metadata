package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class DecisionForEditorVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String editor;
	private Integer accepted;
	private Integer rejected;
	private Long daysFromAssignmentToSuggestingReviewers;
	private Long daysFromDecisionRequestToDecision;
	
	private Integer recommendationAccepted;
	private Integer recommendationRejected;
	private Integer recommendationRevision;
	
	private float avgDaysOfassignmentToInviting;
	private float maxDaysOfassignmentToInviting;
	private float minDaysOfassignmentToInviting;
	
	private float avgDaysOfassignmentToDecision;
	private float maxDaysOfassignmentToDecision;
	private float minDaysOfassignmentToDecision;
	
	private Integer articlesReviewed;
	private Integer timeFromReceiptToReview;
	private Integer numberOfTimesAFileUploaded;
	private Integer returnedOnTime;
	private Integer returnedLate;
	private Integer neverReturned;

	private Integer articlesForProof;
	private Integer proofReturnOnTime;

	private Date timeSpentOnSystem;
	private Integer timeSpentOnReviewer;
	private Integer timeSpentOnProof;
	private Integer numberOfLogout;
	private Integer reminders;
	private Integer numbersOfReminderSameArticle;

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public Integer getAccepted() {
		return accepted;
	}

	public void setAccepted(Integer accepted) {
		this.accepted = accepted;
	}

	public Integer getRejected() {
		return rejected;
	}

	public void setRejected(Integer rejected) {
		this.rejected = rejected;
	}

	public Long getDaysFromAssignmentToSuggestingReviewers() {
		return daysFromAssignmentToSuggestingReviewers;
	}

	public void setDaysFromAssignmentToSuggestingReviewers(Long daysFromAssignmentToSuggestingReviewers) {
		this.daysFromAssignmentToSuggestingReviewers = daysFromAssignmentToSuggestingReviewers;
	}

	public Long getDaysFromDecisionRequestToDecision() {
		return daysFromDecisionRequestToDecision;
	}

	public void setDaysFromDecisionRequestToDecision(Long daysFromDecisionRequestToDecision) {
		this.daysFromDecisionRequestToDecision = daysFromDecisionRequestToDecision;
	}

	public Integer getArticlesReviewed() {
		return articlesReviewed;
	}

	public void setArticlesReviewed(Integer articlesReviewed) {
		this.articlesReviewed = articlesReviewed;
	}

	public Integer getTimeFromReceiptToReview() {
		return timeFromReceiptToReview;
	}

	public void setTimeFromReceiptToReview(Integer timeFromReceiptToReview) {
		this.timeFromReceiptToReview = timeFromReceiptToReview;
	}

	public Integer getNumberOfTimesAFileUploaded() {
		return numberOfTimesAFileUploaded;
	}

	public void setNumberOfTimesAFileUploaded(Integer numberOfTimesAFileUploaded) {
		this.numberOfTimesAFileUploaded = numberOfTimesAFileUploaded;
	}

	public Integer getReturnedOnTime() {
		return returnedOnTime;
	}

	public void setReturnedOnTime(Integer returnedOnTime) {
		this.returnedOnTime = returnedOnTime;
	}

	public Integer getReturnedLate() {
		return returnedLate;
	}

	public void setReturnedLate(Integer returnedLate) {
		this.returnedLate = returnedLate;
	}

	public Integer getNeverReturned() {
		return neverReturned;
	}

	public void setNeverReturned(Integer neverReturned) {
		this.neverReturned = neverReturned;
	}

	public Integer getArticlesForProof() {
		return articlesForProof;
	}

	public void setArticlesForProof(Integer articlesForProof) {
		this.articlesForProof = articlesForProof;
	}

	public Integer getProofReturnOnTime() {
		return proofReturnOnTime;
	}

	public void setProofReturnOnTime(Integer proofReturnOnTime) {
		this.proofReturnOnTime = proofReturnOnTime;
	}

	public Date getTimeSpentOnSystem() {
		return timeSpentOnSystem;
	}

	public void setTimeSpentOnSystem(Date timeSpentOnSystem) {
		this.timeSpentOnSystem = timeSpentOnSystem;
	}

	public Integer getTimeSpentOnReviewer() {
		return timeSpentOnReviewer;
	}

	public void setTimeSpentOnReviewer(Integer timeSpentOnReviewer) {
		this.timeSpentOnReviewer = timeSpentOnReviewer;
	}

	public Integer getTimeSpentOnProof() {
		return timeSpentOnProof;
	}

	public void setTimeSpentOnProof(Integer timeSpentOnProof) {
		this.timeSpentOnProof = timeSpentOnProof;
	}

	public Integer getNumberOfLogout() {
		return numberOfLogout;
	}

	public void setNumberOfLogout(Integer numberOfLogout) {
		this.numberOfLogout = numberOfLogout;
	}

	public Integer getReminders() {
		return reminders;
	}

	public void setReminders(Integer reminders) {
		this.reminders = reminders;
	}

	public Integer getNumbersOfReminderSameArticle() {
		return numbersOfReminderSameArticle;
	}

	public void setNumbersOfReminderSameArticle(Integer numbersOfReminderSameArticle) {
		this.numbersOfReminderSameArticle = numbersOfReminderSameArticle;
	}

	public Integer getRecommendationAccepted() {
		return recommendationAccepted;
	}

	public void setRecommendationAccepted(Integer recommendationAccepted) {
		this.recommendationAccepted = recommendationAccepted;
	}

	public Integer getRecommendationRejected() {
		return recommendationRejected;
	}

	public void setRecommendationRejected(Integer recommendationRejected) {
		this.recommendationRejected = recommendationRejected;
	}

	public Integer getRecommendationRevision() {
		return recommendationRevision;
	}

	public void setRecommendationRevision(Integer recommendationRevision) {
		this.recommendationRevision = recommendationRevision;
	}
	
	

	public float getAvgDaysOfassignmentToInviting() {
		return avgDaysOfassignmentToInviting;
	}

	public void setAvgDaysOfassignmentToInviting(float avgDaysOfassignmentToInviting) {
		this.avgDaysOfassignmentToInviting = avgDaysOfassignmentToInviting;
	}

	public float getMaxDaysOfassignmentToInviting() {
		return maxDaysOfassignmentToInviting;
	}

	public void setMaxDaysOfassignmentToInviting(float maxDaysOfassignmentToInviting) {
		this.maxDaysOfassignmentToInviting = maxDaysOfassignmentToInviting;
	}

	public float getMinDaysOfassignmentToInviting() {
		return minDaysOfassignmentToInviting;
	}

	public void setMinDaysOfassignmentToInviting(float minDaysOfassignmentToInviting) {
		this.minDaysOfassignmentToInviting = minDaysOfassignmentToInviting;
	}

	public float getAvgDaysOfassignmentToDecision() {
		return avgDaysOfassignmentToDecision;
	}

	public void setAvgDaysOfassignmentToDecision(float avgDaysOfassignmentToDecision) {
		this.avgDaysOfassignmentToDecision = avgDaysOfassignmentToDecision;
	}

	public float getMaxDaysOfassignmentToDecision() {
		return maxDaysOfassignmentToDecision;
	}

	public void setMaxDaysOfassignmentToDecision(float maxDaysOfassignmentToDecision) {
		this.maxDaysOfassignmentToDecision = maxDaysOfassignmentToDecision;
	}

	public float getMinDaysOfassignmentToDecision() {
		return minDaysOfassignmentToDecision;
	}

	public void setMinDaysOfassignmentToDecision(float minDaysOfassignmentToDecision) {
		this.minDaysOfassignmentToDecision = minDaysOfassignmentToDecision;
	}

	@Override
	public String toString() {
		return "DecisionForEditorVO [editor=" + editor + ", accepted=" + accepted + ", rejected=" + rejected
				+ ", daysFromAssignmentToSuggestingReviewers=" + daysFromAssignmentToSuggestingReviewers
				+ ", daysFromDecisionRequestToDecision=" + daysFromDecisionRequestToDecision
				+ ", recommendationAccepted=" + recommendationAccepted + ", recommendationRejected="
				+ recommendationRejected + ", recommendationRevision=" + recommendationRevision
				+ ", avgDaysOfassignmentToInviting=" + avgDaysOfassignmentToInviting
				+ ", maxDaysOfassignmentToInviting=" + maxDaysOfassignmentToInviting
				+ ", minDaysOfassignmentToInviting=" + minDaysOfassignmentToInviting
				+ ", avgDaysOfassignmentToDecision=" + avgDaysOfassignmentToDecision
				+ ", maxDaysOfassignmentToDecision=" + maxDaysOfassignmentToDecision
				+ ", minDaysOfassignmentToDecision=" + minDaysOfassignmentToDecision + ", articlesReviewed="
				+ articlesReviewed + ", timeFromReceiptToReview=" + timeFromReceiptToReview
				+ ", numberOfTimesAFileUploaded=" + numberOfTimesAFileUploaded + ", returnedOnTime=" + returnedOnTime
				+ ", returnedLate=" + returnedLate + ", neverReturned=" + neverReturned + ", articlesForProof="
				+ articlesForProof + ", proofReturnOnTime=" + proofReturnOnTime + ", timeSpentOnSystem="
				+ timeSpentOnSystem + ", timeSpentOnReviewer=" + timeSpentOnReviewer + ", timeSpentOnProof="
				+ timeSpentOnProof + ", numberOfLogout=" + numberOfLogout + ", reminders=" + reminders
				+ ", numbersOfReminderSameArticle=" + numbersOfReminderSameArticle + "]";
	}

}
