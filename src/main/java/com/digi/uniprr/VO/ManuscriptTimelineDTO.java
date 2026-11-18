package com.digi.uniprr.VO;

public class ManuscriptTimelineDTO {
	
	private StageTimelineDTO submission;
    private StageTimelineDTO editorialReview;
    private StageTimelineDTO inviteReviewer;
    private StageTimelineDTO acceptReviewInvitation;
    private StageTimelineDTO reviewedManuscript;
    private StageTimelineDTO revisions;
    private StageTimelineDTO finalDecision;
    private StageTimelineDTO acceptedRejected;
    private StageTimelineDTO published;
    
	public StageTimelineDTO getSubmission() {
		return submission;
	}
	public void setSubmission(StageTimelineDTO submission) {
		this.submission = submission;
	}
	
	public StageTimelineDTO getEditorialReview() {
		return editorialReview;
	}
	public void setEditorialReview(StageTimelineDTO editorialReview) {
		this.editorialReview = editorialReview;
	}
	
	public StageTimelineDTO getInviteReviewer() {
		return inviteReviewer;
	}
	public void setInviteReviewer(StageTimelineDTO inviteReviewer) {
		this.inviteReviewer = inviteReviewer;
	}
	
	public StageTimelineDTO getAcceptReviewInvitation() {
		return acceptReviewInvitation;
	}
	public void setAcceptReviewInvitation(StageTimelineDTO acceptReviewInvitation) {
		this.acceptReviewInvitation = acceptReviewInvitation;
	}
	
	public StageTimelineDTO getReviewedManuscript() {
		return reviewedManuscript;
	}
	public void setReviewedManuscript(StageTimelineDTO reviewedManuscript) {
		this.reviewedManuscript = reviewedManuscript;
	}
	
	public StageTimelineDTO getRevisions() {
		return revisions;
	}
	public void setRevisions(StageTimelineDTO revisions) {
		this.revisions = revisions;
	}
	
	public StageTimelineDTO getFinalDecision() {
		return finalDecision;
	}
	public void setFinalDecision(StageTimelineDTO finalDecision) {
		this.finalDecision = finalDecision;
	}
	
	public StageTimelineDTO getAcceptedRejected() {
		return acceptedRejected;
	}
	public void setAcceptedRejected(StageTimelineDTO acceptedRejected) {
		this.acceptedRejected = acceptedRejected;
	}
	
	public StageTimelineDTO getPublished() {
		return published;
	}
	public void setPublished(StageTimelineDTO published) {
		this.published = published;
	}
    
    

}
