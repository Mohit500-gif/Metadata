package com.digi.uniprr.VO;

import java.util.List;

public class JournalPerformanceVo {

	private Integer journalId;
	private Integer year;
	private Integer roleId;

	private JournalPerfomanceForReviewerVO journalPerfomanceForReviewer;
	private JournalPerfomanceForAuthorVO journalPerfomanceForAuthor;
	private JournalPerfomanceForEditorVO journalPerfomanceForEditor;
	private JournalPerfomanceForAdminVO journalPerfomanceForAdmin;
	private List<ArticleDetailsVO> articleDetails;
	private List<ExternalArticleDetailsVO> externalArticleDetails;

	private float daysFirstSubmissionToAcceptance;

	private float daysFromAcceptanceToPublication;
	
	private float submissionToAcceptanceMax;
	private float submissionToAcceptanceMin;
	private float  acceptanceToPublicationMax;
	private float  acceptanceToPublicationMin;
	
	private float daysFromSubmissionToPublication;
	private float  submissionToPublicationMax;
	private float  submissionToPublicationMin;

	public float getSubmissionToAcceptanceMax() {
		return submissionToAcceptanceMax;
	}

	public void setSubmissionToAcceptanceMax(float submissionToAcceptanceMax) {
		this.submissionToAcceptanceMax = submissionToAcceptanceMax;
	}

	public float getSubmissionToAcceptanceMin() {
		return submissionToAcceptanceMin;
	}

	public void setSubmissionToAcceptanceMin(float submissionToAcceptanceMin) {
		this.submissionToAcceptanceMin = submissionToAcceptanceMin;
	}

	public float getAcceptanceToPublicationMax() {
		return acceptanceToPublicationMax;
	}

	public void setAcceptanceToPublicationMax(float acceptanceToPublicationMax) {
		this.acceptanceToPublicationMax = acceptanceToPublicationMax;
	}

	public float getAcceptanceToPublicationMin() {
		return acceptanceToPublicationMin;
	}

	public void setAcceptanceToPublicationMin(float acceptanceToPublicationMin) {
		this.acceptanceToPublicationMin = acceptanceToPublicationMin;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public JournalPerfomanceForReviewerVO getJournalPerfomanceForReviewer() {
		return journalPerfomanceForReviewer;
	}

	public void setJournalPerfomanceForReviewer(JournalPerfomanceForReviewerVO journalPerfomanceForReviewer) {
		this.journalPerfomanceForReviewer = journalPerfomanceForReviewer;
	}

	public JournalPerfomanceForAuthorVO getJournalPerfomanceForAuthor() {
		return journalPerfomanceForAuthor;
	}

	public void setJournalPerfomanceForAuthor(JournalPerfomanceForAuthorVO journalPerfomanceForAuthor) {
		this.journalPerfomanceForAuthor = journalPerfomanceForAuthor;
	}

	public JournalPerfomanceForEditorVO getJournalPerfomanceForEditor() {
		return journalPerfomanceForEditor;
	}

	public void setJournalPerfomanceForEditor(JournalPerfomanceForEditorVO journalPerfomanceForEditor) {
		this.journalPerfomanceForEditor = journalPerfomanceForEditor;
	}

	public JournalPerfomanceForAdminVO getJournalPerfomanceForAdmin() {
		return journalPerfomanceForAdmin;
	}

	public void setJournalPerfomanceForAdmin(JournalPerfomanceForAdminVO journalPerfomanceForAdmin) {
		this.journalPerfomanceForAdmin = journalPerfomanceForAdmin;
	}

	public List<ArticleDetailsVO> getArticleDetails() {
		return articleDetails;
	}

	public void setArticleDetails(List<ArticleDetailsVO> articleDetails) {
		this.articleDetails = articleDetails;
	}

	public List<ExternalArticleDetailsVO> getExternalArticleDetails() {
		return externalArticleDetails;
	}

	public void setExternalArticleDetails(List<ExternalArticleDetailsVO> externalArticleDetails) {
		this.externalArticleDetails = externalArticleDetails;
	}

	public float getDaysFirstSubmissionToAcceptance() {
		return daysFirstSubmissionToAcceptance;
	}

	public void setDaysFirstSubmissionToAcceptance(float daysFirstSubmissionToAcceptance) {
		this.daysFirstSubmissionToAcceptance = daysFirstSubmissionToAcceptance;
	}

	public float getDaysFromAcceptanceToPublication() {
		return daysFromAcceptanceToPublication;
	}

	public void setDaysFromAcceptanceToPublication(float daysFromAcceptanceToPublication) {
		this.daysFromAcceptanceToPublication = daysFromAcceptanceToPublication;
	}

	public float getDaysFromSubmissionToPublication() {
		return daysFromSubmissionToPublication;
	}

	public void setDaysFromSubmissionToPublication(float daysFromSubmissionToPublication) {
		this.daysFromSubmissionToPublication = daysFromSubmissionToPublication;
	}

	public float getSubmissionToPublicationMax() {
		return submissionToPublicationMax;
	}

	public void setSubmissionToPublicationMax(float submissionToPublicationMax) {
		this.submissionToPublicationMax = submissionToPublicationMax;
	}

	public float getSubmissionToPublicationMin() {
		return submissionToPublicationMin;
	}

	public void setSubmissionToPublicationMin(float submissionToPublicationMin) {
		this.submissionToPublicationMin = submissionToPublicationMin;
	}
	
	

}
