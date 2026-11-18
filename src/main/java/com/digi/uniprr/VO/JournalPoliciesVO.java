package com.digi.uniprr.VO;

import java.util.Date;

public class JournalPoliciesVO {

	private Integer id;

	@Override
	public String toString() {
		return "JournalPoliciesVO{" +
				"id=" + id +
				", journalId=" + journalId +
				", blindType='" + blindType + '\'' +
				", manuscriptsStatusMail='" + manuscriptsStatusMail + '\'' +
				", submissionRequiresCme='" + submissionRequiresCme + '\'' +
				", authorProfileTime='" + authorProfileTime + '\'' +
				", copyrightForm='" + copyrightForm + '\'' +
				", withdrawManuscript='" + withdrawManuscript + '\'' +
				", firstPageTechnicalModificationRevision='" + firstPageTechnicalModificationRevision + '\'' +
				", checkListTechnicalModificationRevision='" + checkListTechnicalModificationRevision + '\'' +
				", apcSubmissionCharge='" + apcSubmissionCharge + '\'' +
				", invitedReviewers='" + invitedReviewers + '\'' +
				", orcIdMandatory='" + orcIdMandatory + '\'' +
				", reminderFrequencyAuthor=" + reminderFrequencyAuthor +
				", reminderFrequencyReviewers=" + reminderFrequencyReviewers +
				", reminderFrequencyEicEditor=" + reminderFrequencyEicEditor +
				", createdBy=" + createdBy +
				", createdAt=" + createdAt +
				", modifyAuthorDetailsTechnicalModificationRevision='" + modifyAuthorDetailsTechnicalModificationRevision + '\'' +
				", otherReviewersFlag='" + otherReviewersFlag + '\'' +
				", journalArticleAutoWithdrawWeeks=" + journalArticleAutoWithdrawWeeks +
				'}';
	}

	private Integer journalId;
	
	private String blindType;

	private String manuscriptsStatusMail;

	private String submissionRequiresCme;

	private String authorProfileTime;

	private String copyrightForm;

	private String withdrawManuscript;

	private String firstPageTechnicalModificationRevision;

	private String checkListTechnicalModificationRevision;

	private String apcSubmissionCharge;

	private String invitedReviewers;

	private String orcIdMandatory;
	
    private Integer reminderFrequencyAuthor;
	
	private Integer reminderFrequencyReviewers;
	
	private Integer reminderFrequencyEicEditor;

	private Integer createdBy;

	private Date createdAt;
	
	private String modifyAuthorDetailsTechnicalModificationRevision;
	
	private String copyrightMandatoryForAllAuthors;
	
	private Integer journalArticleAutoWithdrawWeeks;


	public Integer getJournalArticleAutoWithdrawWeeks() {
		return journalArticleAutoWithdrawWeeks;
	}

	public void setJournalArticleAutoWithdrawWeeks(Integer journalArticleAutoWithdrawWeeks) {
		this.journalArticleAutoWithdrawWeeks = journalArticleAutoWithdrawWeeks;
	}

	public String getOtherReviewersFlag() {
		return otherReviewersFlag;
	}

	public void setOtherReviewersFlag(String otherReviewersFlag) {
		this.otherReviewersFlag = otherReviewersFlag;
	}

	private String otherReviewersFlag;



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getManuscriptsStatusMail() {
		return manuscriptsStatusMail;
	}

	public void setManuscriptsStatusMail(String manuscriptsStatusMail) {
		this.manuscriptsStatusMail = manuscriptsStatusMail;
	}

	public String getSubmissionRequiresCme() {
		return submissionRequiresCme;
	}

	public void setSubmissionRequiresCme(String submissionRequiresCme) {
		this.submissionRequiresCme = submissionRequiresCme;
	}

	public String getAuthorProfileTime() {
		return authorProfileTime;
	}

	public void setAuthorProfileTime(String authorProfileTime) {
		this.authorProfileTime = authorProfileTime;
	}

	public String getCopyrightForm() {
		return copyrightForm;
	}

	public void setCopyrightForm(String copyrightForm) {
		this.copyrightForm = copyrightForm;
	}

	public String getWithdrawManuscript() {
		return withdrawManuscript;
	}

	public void setWithdrawManuscript(String withdrawManuscript) {
		this.withdrawManuscript = withdrawManuscript;
	}

	public String getFirstPageTechnicalModificationRevision() {
		return firstPageTechnicalModificationRevision;
	}

	public void setFirstPageTechnicalModificationRevision(String firstPageTechnicalModificationRevision) {
		this.firstPageTechnicalModificationRevision = firstPageTechnicalModificationRevision;
	}

	public String getCheckListTechnicalModificationRevision() {
		return checkListTechnicalModificationRevision;
	}

	public void setCheckListTechnicalModificationRevision(String checkListTechnicalModificationRevision) {
		this.checkListTechnicalModificationRevision = checkListTechnicalModificationRevision;
	}

	public String getApcSubmissionCharge() {
		return apcSubmissionCharge;
	}

	public void setApcSubmissionCharge(String apcSubmissionCharge) {
		this.apcSubmissionCharge = apcSubmissionCharge;
	}

	public String getInvitedReviewers() {
		return invitedReviewers;
	}

	public void setInvitedReviewers(String invitedReviewers) {
		this.invitedReviewers = invitedReviewers;
	}

	public String getOrcIdMandatory() {
		return orcIdMandatory;
	}

	public void setOrcIdMandatory(String orcIdMandatory) {
		this.orcIdMandatory = orcIdMandatory;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public String getBlindType() {
		return blindType;
	}

	public void setBlindType(String blindType) {
		this.blindType = blindType;
	}
	
	public Integer getReminderFrequencyAuthor() {
		return reminderFrequencyAuthor;
	}

	public void setReminderFrequencyAuthor(Integer reminderFrequencyAuthor) {
		this.reminderFrequencyAuthor = reminderFrequencyAuthor;
	}

	public Integer getReminderFrequencyReviewers() {
		return reminderFrequencyReviewers;
	}

	public void setReminderFrequencyReviewers(Integer reminderFrequencyReviewers) {
		this.reminderFrequencyReviewers = reminderFrequencyReviewers;
	}

	public Integer getReminderFrequencyEicEditor() {
		return reminderFrequencyEicEditor;
	}

	public void setReminderFrequencyEicEditor(Integer reminderFrequencyEicEditor) {
		this.reminderFrequencyEicEditor = reminderFrequencyEicEditor;
	}

	public String getModifyAuthorDetailsTechnicalModificationRevision() {
		return modifyAuthorDetailsTechnicalModificationRevision;
	}

	public void setModifyAuthorDetailsTechnicalModificationRevision(
			String modifyAuthorDetailsTechnicalModificationRevision) {
		this.modifyAuthorDetailsTechnicalModificationRevision = modifyAuthorDetailsTechnicalModificationRevision;
	}

	public String getCopyrightMandatoryForAllAuthors() {
		return copyrightMandatoryForAllAuthors;
	}

	public void setCopyrightMandatoryForAllAuthors(String copyrightMandatoryForAllAuthors) {
		this.copyrightMandatoryForAllAuthors = copyrightMandatoryForAllAuthors;
	}
	
	
	
}
