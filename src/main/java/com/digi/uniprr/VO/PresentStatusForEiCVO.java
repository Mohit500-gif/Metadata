package com.digi.uniprr.VO;

import java.util.List;

public class PresentStatusForEiCVO {
	private List<PresentStatusEicVo> presentStatusVo;
	private Integer totalSubmittedManuscripts;
	private Integer totalWithAssignedEditors;
	private Integer totalTechnicalModification;
	private Integer totalReSubmittedManuscripts;
	private Integer totalReviewersInvitedNoResponse;
	private Integer totalManuscriptsInReview;
	private Integer totalReviewedManuscripts;
	private Integer totalManuscriptsInRevision;
	private Integer totalRevisedManuscripts;
	private Integer totalManuscriptsInReReview;
	private Integer totalManuscriptsInReRevision;
	private Integer totalFinalDecision;
	private Integer acceptForProduction;
	private Integer rejectForProduction;
	private Integer requireTechnicalModification;
	private Integer requireResubmission;
	private Integer withdrawalReqAuthor;
	private String manuscriptType;
	private Integer journalId;

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getAcceptForProduction() {
		return acceptForProduction;
	}
	public void setAcceptForProduction(Integer acceptForProduction) {
		this.acceptForProduction = acceptForProduction;
	}
	public Integer getRejectForProduction() {
		return rejectForProduction;
	}
	public void setRejectForProduction(Integer rejectForProduction) {
		this.rejectForProduction = rejectForProduction;
	}
	public String getManuscriptType() {
		return manuscriptType;
	}
	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}
	public List<PresentStatusEicVo> getPresentStatusVo() {
		return presentStatusVo;
	}
	public void setPresentStatusVo(List<PresentStatusEicVo> presentStatusVo) {
		this.presentStatusVo = presentStatusVo;
	}
	public Integer getTotalSubmittedManuscripts() {
		return totalSubmittedManuscripts;
	}
	public void setTotalSubmittedManuscripts(Integer totalSubmittedManuscripts) {
		this.totalSubmittedManuscripts = totalSubmittedManuscripts;
	}
	public Integer getTotalWithAssignedEditors() {
		return totalWithAssignedEditors;
	}
	public void setTotalWithAssignedEditors(Integer totalWithAssignedEditors) {
		this.totalWithAssignedEditors = totalWithAssignedEditors;
	}
	public Integer getTotalTechnicalModification() {
		return totalTechnicalModification;
	}
	public void setTotalTechnicalModification(Integer totalTechnicalModification) {
		this.totalTechnicalModification = totalTechnicalModification;
	}
	public Integer getTotalReSubmittedManuscripts() {
		return totalReSubmittedManuscripts;
	}
	public void setTotalReSubmittedManuscripts(Integer totalReSubmittedManuscripts) {
		this.totalReSubmittedManuscripts = totalReSubmittedManuscripts;
	}
	public Integer getTotalReviewersInvitedNoResponse() {
		return totalReviewersInvitedNoResponse;
	}
	public void setTotalReviewersInvitedNoResponse(Integer totalReviewersInvitedNoResponse) {
		this.totalReviewersInvitedNoResponse = totalReviewersInvitedNoResponse;
	}
	public Integer getTotalManuscriptsInReview() {
		return totalManuscriptsInReview;
	}
	public void setTotalManuscriptsInReview(Integer totalManuscriptsInReview) {
		this.totalManuscriptsInReview = totalManuscriptsInReview;
	}
	public Integer getTotalReviewedManuscripts() {
		return totalReviewedManuscripts;
	}
	public void setTotalReviewedManuscripts(Integer totalReviewedManuscripts) {
		this.totalReviewedManuscripts = totalReviewedManuscripts;
	}
	public Integer getTotalManuscriptsInRevision() {
		return totalManuscriptsInRevision;
	}
	public void setTotalManuscriptsInRevision(Integer totalManuscriptsInRevision) {
		this.totalManuscriptsInRevision = totalManuscriptsInRevision;
	}
	public Integer getTotalRevisedManuscripts() {
		return totalRevisedManuscripts;
	}
	public void setTotalRevisedManuscripts(Integer totalRevisedManuscripts) {
		this.totalRevisedManuscripts = totalRevisedManuscripts;
	}
	public Integer getTotalManuscriptsInReReview() {
		return totalManuscriptsInReReview;
	}
	public void setTotalManuscriptsInReReview(Integer totalManuscriptsInReReview) {
		this.totalManuscriptsInReReview = totalManuscriptsInReReview;
	}
	public Integer getTotalManuscriptsInReRevision() {
		return totalManuscriptsInReRevision;
	}
	public void setTotalManuscriptsInReRevision(Integer totalManuscriptsInReRevision) {
		this.totalManuscriptsInReRevision = totalManuscriptsInReRevision;
	}
	public Integer getTotalFinalDecision() {
		return totalFinalDecision;
	}
	public void setTotalFinalDecision(Integer totalFinalDecision) {
		this.totalFinalDecision = totalFinalDecision;
	}
	public Integer getRequireResubmission() {
		return requireResubmission;
	}
	public void setRequireResubmission(Integer requireResubmission) {
		this.requireResubmission = requireResubmission;
	}
	public Integer getRequireTechnicalModification() {
		return requireTechnicalModification;
	}
	public void setRequireTechnicalModification(Integer requireTechnicalModification) {
		this.requireTechnicalModification = requireTechnicalModification;
	}
	public Integer getWithdrawalReqAuthor() {
		return withdrawalReqAuthor;
	}
	public void setWithdrawalReqAuthor(Integer withdrawalReqAuthor) {
		this.withdrawalReqAuthor = withdrawalReqAuthor;
	}
	
}
