package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "journal_policies")
public class JournalPolicies {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "journal_id")
	private Integer journalId;

	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "journal_id", insertable = false, updatable = false)
	private Journal journal;

	@Column(name = "manuscripts_status_mail")
	private String manuscriptsStatusMail;

	@Column(name = "submission_requires_cme")
	private String submissionRequiresCme;

	@Column(name = "author_profile_time")
	private String authorProfileTime;

	@Column(name = "copyright_form")
	private String copyrightForm;

	@Column(name = "withdraw_manuscript")
	private String withdrawManuscript;

	@Column(name = "first_page_technical_modification_revision")
	private String firstPageTechnicalModificationRevision;

	@Column(name = "check_list_technical_modification_revision")
	private String checkListTechnicalModificationRevision;
	
	@Column(name = "apc_submission_charge")
	private String apcSubmissionCharge;

	@Column(name = "invited_reviewers")
	private String invitedReviewers;

	@Column(name = "orc_id_mandatory")
	private String orcIdMandatory;
	
	@Column(name="reminder_frequency_authors")
	@ColumnDefault("0")
	private Integer reminderFrequencyAuthor;
	
	@Column(name="reminder_frequency_reviewers")
	@ColumnDefault("0")
	private Integer reminderFrequencyReviewers;
	
	@Column(name="reminder_frequency_eic_editor")
	@ColumnDefault("0")
	private Integer reminderFrequencyEicEditor;
	
	@Column(name="journal_article_auto_withdraw_weeks")
	@ColumnDefault("0")
	private Integer journalArticleAutoWithdrawWeeks;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_at")
	@CreationTimestamp
	private Date createdAt;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "updated_at")
	private Date updatedAt;

	@Column(name = "other_reviewers_flag")
	private String otherReviewersFlag;

	@Column(name = "modifyauthor_details_technicalmodification_revision")
	private String modifyAuthorDetailsTechnicalModificationRevision;
	
	@Column(name = "copyright_mandatory_for_all_authors")
	private String copyrightMandatoryForAllAuthors; // Yes / No

	
	public String getOtherReviewersFlag() {
		return otherReviewersFlag;
	}

	public void setOtherReviewersFlag(String otherReviewersFlag) {
		this.otherReviewersFlag = otherReviewersFlag;
	}

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

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
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
	
	public Integer getJournalArticleAutoWithdrawWeeks() {
		return journalArticleAutoWithdrawWeeks;
	}

	public void setJournalArticleAutoWithdrawWeeks(Integer journalArticleAutoWithdrawWeeks) {
		this.journalArticleAutoWithdrawWeeks = journalArticleAutoWithdrawWeeks;
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
