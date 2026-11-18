package com.digi.uniprr.VO;

import java.io.Serializable;

public class AuthorDashboardCountVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer technicalModificationCount;
	
	private Integer submittedManuscriptCount;

	private Integer revisionCount;

	private Integer withdrawlRequestCount;
	
	private Integer decisionCount;
	
	private Integer checkingProofingCount;
	
	private Integer incompleteSubmissionCount;
	
	private Integer paymentRequiredCount;

	public Integer getTechnicalModificationCount() {
		return technicalModificationCount;
	}

	public void setTechnicalModificationCount(Integer technicalModificationCount) {
		this.technicalModificationCount = technicalModificationCount;
	}
	
	public Integer getSubmittedManuscriptCount() {
		return submittedManuscriptCount;
	}

	public void setSubmittedManuscriptCount(Integer submittedManuscriptCount) {
		this.submittedManuscriptCount = submittedManuscriptCount;
	}

	public Integer getRevisionCount() {
		return revisionCount;
	}

	public void setRevisionCount(Integer revisionCount) {
		this.revisionCount = revisionCount;
	}

	public Integer getWithdrawlRequestCount() {
		return withdrawlRequestCount;
	}

	public void setWithdrawlRequestCount(Integer withdrawlRequestCount) {
		this.withdrawlRequestCount = withdrawlRequestCount;
	}

	public Integer getDecisionCount() {
		return decisionCount;
	}

	public void setDecisionCount(Integer decisionCount) {
		this.decisionCount = decisionCount;
	}

	public Integer getCheckingProofingCount() {
		return checkingProofingCount;
	}

	public void setCheckingProofingCount(Integer checkingProofingCount) {
		this.checkingProofingCount = checkingProofingCount;
	}

	public Integer getIncompleteSubmissionCount() {
		return incompleteSubmissionCount;
	}

	public void setIncompleteSubmissionCount(Integer incompleteSubmissionCount) {
		this.incompleteSubmissionCount = incompleteSubmissionCount;
	}

	public Integer getPaymentRequiredCount() {
		return paymentRequiredCount;
	}

	public void setPaymentRequiredCount(Integer paymentRequiredCount) {
		this.paymentRequiredCount = paymentRequiredCount;
	}

	@Override
	public String toString() {
		return "AuthorDashboardCountVO [technicalModificationCount=" + technicalModificationCount
				+ ", submittedManuscriptCount=" + submittedManuscriptCount + ", revisionCount=" + revisionCount
				+ ", withdrawlRequestCount=" + withdrawlRequestCount + ", decisionCount=" + decisionCount
				+ ", checkingProofingCount=" + checkingProofingCount + ", incompleteSubmissionCount="
				+ incompleteSubmissionCount + ", paymentRequiredCount=" + paymentRequiredCount + "]";
	}
	
}
