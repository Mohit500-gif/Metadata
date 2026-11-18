package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class EmailTemplateDataVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String manuscriptId;
	private String authorName;
	private String journalTitle;
	private String journalCode;
	private String manuscriptTitle;
	private Date submissionDate;
	private String articleDownloadUrl;
	private String journalWebsiteUrl;
	private String productionWebsiteUrl;
	private String signature;
	private String adminName;
	private String url;
	private String editorName;
	private String managingEditorName;
	private String reviewerName;
	private String reviewerRecommendation;
	private String reviewerRating;
	private String editorDecission;
	private String editorDecissionComments;
	private String reviewerDecision;
	private String reviewerCommentsToAuthor;
	private String reviewerCommentsToEditor;
	private String authorSalutation;
	private String authorFirstName;
	private String authorLastName;
	private String reviewerSalutation;
	private String reviewerFirstName;
	private String reviewerLastName;
	private String editorSalutation;
	private String editorFirstName;
	private String editorLastName;
	private String adminSalutation;
	private String adminFirstName;
	private String commentToAuthor;
	private String adminLastName;
	private String managingEditorSalutation;
	private String managingEditorFirstName;
	private String managingEditorLastName;
	private String defaultEditor;
	private String defaultEditorFirstName;
	private String defaultEditorLastName;
	private String defaultEditorSalutation;
	private String journalEmail;
	private String acceptByReviewerUrl;
	private String rejectByReviewerUrl;
	private String viewByReviewerUrl;
	private String articleType;
	private String roleType;
	private Date reSubmissionDate;
	private Date dateSent;
	private String editorInchiefEmail;
	private String phaseName;
	private String commentToEditor;
	
	private String reviewerEmailId;
	private Date completionDueDate;
	private String countryName;
    private String eicFolderName;	
    private String authorImpactUrl;
    private Integer taskId;

	

	public Date getCompletionDueDate() {
		return completionDueDate;
	}

	public void setCompletionDueDate(Date completionDueDate) {
		this.completionDueDate = completionDueDate;
	}
	

	public String getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(String manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getManuscriptTitle() {
		return manuscriptTitle;
	}

	public void setManuscriptTitle(String manuscriptTitle) {
		this.manuscriptTitle = manuscriptTitle;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getArticleDownloadUrl() {
		return articleDownloadUrl;
	}

	public void setArticleDownloadUrl(String articleDownloadUrl) {
		this.articleDownloadUrl = articleDownloadUrl;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}

	public String getReviewerRecommendation() {
		return reviewerRecommendation;
	}

	public void setReviewerRecommendation(String reviewerRecommendation) {
		this.reviewerRecommendation = reviewerRecommendation;
	}

	public String getReviewerRating() {
		return reviewerRating;
	}

	public void setReviewerRating(String reviewerRating) {
		this.reviewerRating = reviewerRating;
	}

	public String getManagingEditorName() {
		return managingEditorName;
	}

	public void setManagingEditorName(String managingEditorName) {
		this.managingEditorName = managingEditorName;
	}

	public String getEditorDecission() {
		return editorDecission;
	}

	public void setEditorDecission(String editorDecission) {
		this.editorDecission = editorDecission;
	}

	public String getEditorDecissionComments() {
		return editorDecissionComments;
	}

	public void setEditorDecissionComments(String editorDecissionComments) {
		this.editorDecissionComments = editorDecissionComments;
	}

	public String getReviewerDecision() {
		return reviewerDecision;
	}

	public void setReviewerDecision(String reviewerDecision) {
		this.reviewerDecision = reviewerDecision;
	}

	public String getReviewerCommentsToAuthor() {
		return reviewerCommentsToAuthor;
	}

	public void setReviewerCommentsToAuthor(String reviewerCommentsToAuthor) {
		this.reviewerCommentsToAuthor = reviewerCommentsToAuthor;
	}

	public String getReviewerCommentsToEditor() {
		return reviewerCommentsToEditor;
	}

	public void setReviewerCommentsToEditor(String reviewerCommentsToEditor) {
		this.reviewerCommentsToEditor = reviewerCommentsToEditor;
	}

	public String getAuthorSalutation() {
		return authorSalutation;
	}

	public void setAuthorSalutation(String authorSalutation) {
		this.authorSalutation = authorSalutation;
	}

	public String getAuthorFirstName() {
		return authorFirstName;
	}

	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}

	public String getAuthorLastName() {
		return authorLastName;
	}

	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}

	public String getReviewerSalutation() {
		return reviewerSalutation;
	}

	public void setReviewerSalutation(String reviewerSalutation) {
		this.reviewerSalutation = reviewerSalutation;
	}

	public String getReviewerFirstName() {
		return reviewerFirstName;
	}

	public void setReviewerFirstName(String reviewerFirstName) {
		this.reviewerFirstName = reviewerFirstName;
	}

	public String getReviewerLastName() {
		return reviewerLastName;
	}

	public void setReviewerLastName(String reviewerLastName) {
		this.reviewerLastName = reviewerLastName;
	}

	public String getEditorSalutation() {
		return editorSalutation;
	}

	public void setEditorSalutation(String editorSalutation) {
		this.editorSalutation = editorSalutation;
	}

	public String getEditorFirstName() {
		return editorFirstName;
	}

	public void setEditorFirstName(String editorFirstName) {
		this.editorFirstName = editorFirstName;
	}

	public String getEditorLastName() {
		return editorLastName;
	}

	public void setEditorLastName(String editorLastName) {
		this.editorLastName = editorLastName;
	}

	public String getAdminSalutation() {
		return adminSalutation;
	}

	public void setAdminSalutation(String adminSalutation) {
		this.adminSalutation = adminSalutation;
	}

	public String getAdminFirstName() {
		return adminFirstName;
	}

	public void setAdminFirstName(String adminFirstName) {
		this.adminFirstName = adminFirstName;
	}

	public String getAdminLastName() {
		return adminLastName;
	}

	public void setAdminLastName(String adminLastName) {
		this.adminLastName = adminLastName;
	}

	public String getManagingEditorSalutation() {
		return managingEditorSalutation;
	}

	public void setManagingEditorSalutation(String managingEditorSalutation) {
		this.managingEditorSalutation = managingEditorSalutation;
	}

	public String getManagingEditorFirstName() {
		return managingEditorFirstName;
	}

	public void setManagingEditorFirstName(String managingEditorFirstName) {
		this.managingEditorFirstName = managingEditorFirstName;
	}

	public String getManagingEditorLastName() {
		return managingEditorLastName;
	}

	public void setManagingEditorLastName(String managingEditorLastName) {
		this.managingEditorLastName = managingEditorLastName;
	}

	public String getDefaultEditor() {
		return defaultEditor;
	}

	public void setDefaultEditor(String defaultEditor) {
		this.defaultEditor = defaultEditor;
	}

	public String getJournalEmail() {
		return journalEmail;
	}

	public void setJournalEmail(String journalEmail) {
		this.journalEmail = journalEmail;
	}

	public String getAcceptByReviewerUrl() {
		return acceptByReviewerUrl;
	}

	public void setAcceptByReviewerUrl(String acceptByReviewerUrl) {
		this.acceptByReviewerUrl = acceptByReviewerUrl;
	}

	public String getRejectByReviewerUrl() {
		return rejectByReviewerUrl;
	}

	public void setRejectByReviewerUrl(String rejectByReviewerUrl) {
		this.rejectByReviewerUrl = rejectByReviewerUrl;
	}

	public String getViewByReviewerUrl() {
		return viewByReviewerUrl;
	}

	public void setViewByReviewerUrl(String viewByReviewerUrl) {
		this.viewByReviewerUrl = viewByReviewerUrl;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public String getCommentToAuthor() {
		return commentToAuthor;
	}

	public void setCommentToAuthor(String commentToAuthor) {
		this.commentToAuthor = commentToAuthor;
	}

	public String getArticleType() {
		return articleType;
	}

	public void setArticleType(String articleType) {
		this.articleType = articleType;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public Date getReSubmissionDate() {
		return reSubmissionDate;
	}

	public void setReSubmissionDate(Date reSubmissionDate) {
		this.reSubmissionDate = reSubmissionDate;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	public String getEditorInchiefEmail() {
		return editorInchiefEmail;
	}

	public void setEditorInchiefEmail(String editorInchiefEmail) {
		this.editorInchiefEmail = editorInchiefEmail;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public String getJournalWebsiteUrl() {
		return journalWebsiteUrl;
	}

	public void setJournalWebsiteUrl(String journalWebsiteUrl) {
		this.journalWebsiteUrl = journalWebsiteUrl;
	}

	public String getProductionWebsiteUrl() {
		return productionWebsiteUrl;
	}

	public void setProductionWebsiteUrl(String productionWebsiteUrl) {
		this.productionWebsiteUrl = productionWebsiteUrl;
	}

	public String getDefaultEditorFirstName() {
		return defaultEditorFirstName;
	}

	public void setDefaultEditorFirstName(String defaultEditorFirstName) {
		this.defaultEditorFirstName = defaultEditorFirstName;
	}

	public String getDefaultEditorLastName() {
		return defaultEditorLastName;
	}

	public void setDefaultEditorLastName(String defaultEditorLastName) {
		this.defaultEditorLastName = defaultEditorLastName;
	}

	public String getDefaultEditorSalutation() {
		return defaultEditorSalutation;
	}

	public void setDefaultEditorSalutation(String defaultEditorSalutation) {
		this.defaultEditorSalutation = defaultEditorSalutation;
	}

	public String getCommentToEditor() {
		return commentToEditor;
	}

	public void setCommentToEditor(String commentToEditor) {
		this.commentToEditor = commentToEditor;
	}

	public String getReviewerEmailId() {
		return reviewerEmailId;
	}

	public void setReviewerEmailId(String reviewerEmailId) {
		this.reviewerEmailId = reviewerEmailId;
	}
	

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getEicFolderName() {
		return eicFolderName;
	}

	public void setEicFolderName(String eicFolderName) {
		this.eicFolderName = eicFolderName;
	}
	

	public String getAuthorImpactUrl() {
		return authorImpactUrl;
	}

	public void setAuthorImpactUrl(String authorImpactUrl) {
		this.authorImpactUrl = authorImpactUrl;
	}
	

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	@Override
	public String toString() {
		return "EmailTemplateDataVO [manuscriptId=" + manuscriptId + ", authorName=" + authorName + ", journalTitle="
				+ journalTitle + ", journalCode=" + journalCode + ", manuscriptTitle=" + manuscriptTitle
				+ ", submissionDate=" + submissionDate + ", articleDownloadUrl=" + articleDownloadUrl
				+ ", journalWebsiteUrl=" + journalWebsiteUrl + ", productionWebsiteUrl=" + productionWebsiteUrl
				+ ", signature=" + signature + ", adminName=" + adminName + ", url=" + url + ", editorName="
				+ editorName + ", managingEditorName=" + managingEditorName + ", reviewerName=" + reviewerName
				+ ", reviewerRecommendation=" + reviewerRecommendation + ", reviewerRating=" + reviewerRating
				+ ", editorDecission=" + editorDecission + ", editorDecissionComments=" + editorDecissionComments
				+ ", reviewerDecision=" + reviewerDecision + ", reviewerCommentsToAuthor=" + reviewerCommentsToAuthor
				+ ", reviewerCommentsToEditor=" + reviewerCommentsToEditor + ", authorSalutation=" + authorSalutation
				+ ", authorFirstName=" + authorFirstName + ", authorLastName=" + authorLastName
				+ ", reviewerSalutation=" + reviewerSalutation + ", reviewerFirstName=" + reviewerFirstName
				+ ", reviewerLastName=" + reviewerLastName + ", editorSalutation=" + editorSalutation
				+ ", editorFirstName=" + editorFirstName + ", editorLastName=" + editorLastName + ", adminSalutation="
				+ adminSalutation + ", adminFirstName=" + adminFirstName + ", commentToAuthor=" + commentToAuthor
				+ ", adminLastName=" + adminLastName + ", managingEditorSalutation=" + managingEditorSalutation
				+ ", managingEditorFirstName=" + managingEditorFirstName + ", managingEditorLastName="
				+ managingEditorLastName + ", defaultEditor=" + defaultEditor + ", defaultEditorFirstName="
				+ defaultEditorFirstName + ", defaultEditorLastName=" + defaultEditorLastName
				+ ", defaultEditorSalutation=" + defaultEditorSalutation + ", journalEmail=" + journalEmail
				+ ", acceptByReviewerUrl=" + acceptByReviewerUrl + ", rejectByReviewerUrl=" + rejectByReviewerUrl
				+ ", viewByReviewerUrl=" + viewByReviewerUrl + ", articleType=" + articleType + ", roleType=" + roleType
				+ ", reSubmissionDate=" + reSubmissionDate + ", dateSent=" + dateSent + ", editorInchiefEmail="
				+ editorInchiefEmail + ", phaseName=" + phaseName + ", commentToEditor=" + commentToEditor
				+ ", reviewerEmailId=" + reviewerEmailId + ", completionDueDate=" + completionDueDate + ", countryName="
				+ countryName + ", eicFolderName=" + eicFolderName + "]";
	}

}
