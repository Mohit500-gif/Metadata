package com.digi.uniprr.VO;

import java.util.Date;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

public class ReviewerAvailabilityStatusVO {
	
	private String name;
	
	private String manuscriptCode;
	
	private String manuscriptTitle;
	
	private Integer quality;
	
	private Integer timeliness;

	private Date unassignedDate;

	private Date startDate;

	private Date completeDate;

	private Date rejectDate;

	private Date acceptDate;

	private Integer isAssigned;

	private Integer isActive;

	private Date dueDate;
	
	private String manuscriptStatus;

	private String manuscriptPriority;
	
	private String competingInterests;
	
	private String messageFromManuscript;
	
	private String reviewerResponse;
	
	private String rejectionReason;

	private String commentsForReviewer;
	
	private String commentsToAuthor;
	
	private String commentsToEditor;
	
	private String decisionLetter;
	
	private String email;
	
	private Integer acceptedByReviewer;
	
	private Integer rejectedByReviewer;
	
	private Integer minorRevisionReviewer;
	
	private Integer majorRevisionReviewer;
	
	private Integer acceptReviewInvitation;
	
	private Integer rejectReviewInvitation;
	
	private Integer invitedManuscript;
	
	private Integer decliendAfterAcceptInvitation;

	private Integer articlesInHand;

	private String colour;

	private Integer reviewCompleted;

	private Integer totalCount;

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getReviewCompleted() {
		return reviewCompleted;
	}

	public void setReviewCompleted(Integer reviewCompleted) {
		this.reviewCompleted = reviewCompleted;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public Integer getArticlesInHand() {
		return articlesInHand;
	}

	public void setArticlesInHand(Integer articlesInHand) {
		this.articlesInHand = articlesInHand;
	}

	public Integer getInvitedManuscript() {
		return invitedManuscript;
	}

	public void setInvitedManuscript(Integer invitedManuscript) {
		this.invitedManuscript = invitedManuscript;
	}

	public Integer getDecliendAfterAcceptInvitation() {
		return decliendAfterAcceptInvitation;
	}

	public void setDecliendAfterAcceptInvitation(Integer decliendAfterAcceptInvitation) {
		this.decliendAfterAcceptInvitation = decliendAfterAcceptInvitation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAcceptedByReviewer() {
		return acceptedByReviewer;
	}

	public void setAcceptedByReviewer(Integer acceptedByReviewer) {
		this.acceptedByReviewer = acceptedByReviewer;
	}

	public Integer getRejectedByReviewer() {
		return rejectedByReviewer;
	}

	public void setRejectedByReviewer(Integer rejectedByReviewer) {
		this.rejectedByReviewer = rejectedByReviewer;
	}

	public Integer getMinorRevisionReviewer() {
		return minorRevisionReviewer;
	}

	public void setMinorRevisionReviewer(Integer minorRevisionReviewer) {
		this.minorRevisionReviewer = minorRevisionReviewer;
	}

	public Integer getMajorRevisionReviewer() {
		return majorRevisionReviewer;
	}

	public void setMajorRevisionReviewer(Integer majorRevisionReviewer) {
		this.majorRevisionReviewer = majorRevisionReviewer;
	}

	public Integer getAcceptReviewInvitation() {
		return acceptReviewInvitation;
	}

	public void setAcceptReviewInvitation(Integer acceptReviewInvitation) {
		this.acceptReviewInvitation = acceptReviewInvitation;
	}

	public Integer getRejectReviewInvitation() {
		return rejectReviewInvitation;
	}

	public void setRejectReviewInvitation(Integer rejectReviewInvitation) {
		this.rejectReviewInvitation = rejectReviewInvitation;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public String getManuscriptTitle() {
		return manuscriptTitle;
	}

	public void setManuscriptTitle(String manuscriptTitle) {
		this.manuscriptTitle = manuscriptTitle;
	}

	public Integer getQuality() {
		return quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	public Integer getTimeliness() {
		return timeliness;
	}

	public void setTimeliness(Integer timeliness) {
		this.timeliness = timeliness;
	}

	public Date getUnassignedDate() {
		return unassignedDate;
	}

	public void setUnassignedDate(Date unassignedDate) {
		this.unassignedDate = unassignedDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public Date getRejectDate() {
		return rejectDate;
	}

	public void setRejectDate(Date rejectDate) {
		this.rejectDate = rejectDate;
	}

	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public Integer getIsAssigned() {
		return isAssigned;
	}

	public void setIsAssigned(Integer isAssigned) {
		this.isAssigned = isAssigned;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(String manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public String getManuscriptPriority() {
		return manuscriptPriority;
	}

	public void setManuscriptPriority(String manuscriptPriority) {
		this.manuscriptPriority = manuscriptPriority;
	}

	public String getCompetingInterests() {
		return competingInterests;
	}

	public void setCompetingInterests(String competingInterests) {
		this.competingInterests = competingInterests;
	}

	public String getMessageFromManuscript() {
		return messageFromManuscript;
	}

	public void setMessageFromManuscript(String messageFromManuscript) {
		this.messageFromManuscript = messageFromManuscript;
	}

	public String getReviewerResponse() {
		return reviewerResponse;
	}

	public void setReviewerResponse(String reviewerResponse) {
		this.reviewerResponse = reviewerResponse;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getCommentsForReviewer() {
		return commentsForReviewer;
	}

	public void setCommentsForReviewer(String commentsForReviewer) {
		this.commentsForReviewer = commentsForReviewer;
	}

	public String getCommentsToAuthor() {
		return commentsToAuthor;
	}

	public void setCommentsToAuthor(String commentsToAuthor) {
		this.commentsToAuthor = commentsToAuthor;
	}

	public String getCommentsToEditor() {
		return commentsToEditor;
	}

	public void setCommentsToEditor(String commentsToEditor) {
		this.commentsToEditor = commentsToEditor;
	}

	public String getDecisionLetter() {
		return decisionLetter;
	}

	public void setDecisionLetter(String decisionLetter) {
		this.decisionLetter = decisionLetter;
	}
	
	
}
