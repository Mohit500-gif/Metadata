package com.digi.uniprr.VO;

import java.util.List;

public class ManuScriptVO {

	private Integer parentManuscriptId;

	private Integer manuscriptId;

	private Integer manuscriptVersion;

	private Integer id;
	
	private Integer statusCode;

	private Integer version;

	private Integer journalId;
	
	private String manuscriptChecklistResponse;
	
	private String charge;

	private String journalName;

	private String title;

	private String shortTitle;
	
	private String manuscriptAbstract;
	
	private String submissionStatus;

	private Integer authorId;

	private Integer manuscriptTypeDetailsId;

	private String manuscriptFigures;

	private String manuscriptColorFigures;

	private String blackAndWhiteFigures;

	private String manuscriptTables;

	private String manuscriptWords;

	private String manuscriptPages;

	private String declarationOfInterest;
	
	private String declarationOfInterestText;

	private String copyrightAgreement;
	
	private String copyrightAgreementContent;

	private String CreatedOn;

	private String validate;
	
	private Integer userRole;
	
	private Integer assignedUser;

	private String assignTo;
	
	private String activationUrl;
	
	private String copyRightUrlForCoAuthor;
	
	private List<ReviewerSuggestedVO> reviewerSuggested;
	
	private Integer assignedUserRole;

	public Integer getParentManuscriptId() {
		return parentManuscriptId;
	}

	public void setParentManuscriptId(Integer parentManuscriptId) {
		this.parentManuscriptId = parentManuscriptId;
	}

	public Integer getAssignedUserRole() {
		return assignedUserRole;
	}

	public void setAssignedUserRole(Integer assignedUserRole) {
		this.assignedUserRole = assignedUserRole;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getDeclarationOfInterest() {
		return declarationOfInterest;
	}

	public void setDeclarationOfInterest(String declarationOfInterest) {
		this.declarationOfInterest = declarationOfInterest;
	}

	public String getCopyrightAgreement() {
		return copyrightAgreement;
	}

	public void setCopyrightAgreement(String copyrightAgreement) {
		this.copyrightAgreement = copyrightAgreement;
	}

	public String getCreatedOn() {
		return CreatedOn;
	}

	public void setCreatedOn(String createdOn) {
		CreatedOn = createdOn;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getJournalName() {
		return journalName;
	}

	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getManuscriptTypeDetailsId() {
		return manuscriptTypeDetailsId;
	}

	public void setManuscriptTypeDetailsId(Integer manuscriptTypeDetailsId) {
		this.manuscriptTypeDetailsId = manuscriptTypeDetailsId;
	}

	public String getManuscriptFigures() {
		return manuscriptFigures;
	}

	public void setManuscriptFigures(String manuscriptFigures) {
		this.manuscriptFigures = manuscriptFigures;
	}

	public String getManuscriptColorFigures() {
		return manuscriptColorFigures;
	}

	public void setManuscriptColorFigures(String manuscriptColorFigures) {
		this.manuscriptColorFigures = manuscriptColorFigures;
	}

	public String getBlackAndWhiteFigures() {
		return blackAndWhiteFigures;
	}

	public void setBlackAndWhiteFigures(String blackAndWhiteFigures) {
		this.blackAndWhiteFigures = blackAndWhiteFigures;
	}

	public String getManuscriptTables() {
		return manuscriptTables;
	}

	public void setManuscriptTables(String manuscriptTables) {
		this.manuscriptTables = manuscriptTables;
	}

	public String getManuscriptWords() {
		return manuscriptWords;
	}

	public void setManuscriptWords(String manuscriptWords) {
		this.manuscriptWords = manuscriptWords;
	}

	public String getManuscriptPages() {
		return manuscriptPages;
	}

	public void setManuscriptPages(String manuscriptPages) {
		this.manuscriptPages = manuscriptPages;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getCopyrightAgreementContent() {
		return copyrightAgreementContent;
	}

	public void setCopyrightAgreementContent(String copyrightAgreementContent) {
		this.copyrightAgreementContent = copyrightAgreementContent;
	}

	public String getActivationUrl() {
		return activationUrl;
	}

	public void setActivationUrl(String activationUrl) {
		this.activationUrl = activationUrl;
	}

	public String getCopyRightUrlForCoAuthor() {
		return copyRightUrlForCoAuthor;
	}

	public void setCopyRightUrlForCoAuthor(String copyRightUrlForCoAuthor) {
		this.copyRightUrlForCoAuthor = copyRightUrlForCoAuthor;
	}

	public String getManuscriptAbstract() {
		return manuscriptAbstract;
	}

	public void setManuscriptAbstract(String manuscriptAbstract) {
		this.manuscriptAbstract = manuscriptAbstract;
	}

	public String getSubmissionStatus() {
		return submissionStatus;
	}

	public void setSubmissionStatus(String submissionStatus) {
		this.submissionStatus = submissionStatus;
	}

	public String getManuscriptChecklistResponse() {
		return manuscriptChecklistResponse;
	}

	public void setManuscriptChecklistResponse(String manuscriptChecklistResponse) {
		this.manuscriptChecklistResponse = manuscriptChecklistResponse;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getDeclarationOfInterestText() {
		return declarationOfInterestText;
	}

	public void setDeclarationOfInterestText(String declarationOfInterestText) {
		this.declarationOfInterestText = declarationOfInterestText;
	}

	public Integer getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(Integer assignedUser) {
		this.assignedUser = assignedUser;
	}

	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	public List<ReviewerSuggestedVO> getReviewerSuggested() {
		return reviewerSuggested;
	}

	public void setReviewerSuggested(List<ReviewerSuggestedVO> reviewerSuggested) {
		this.reviewerSuggested = reviewerSuggested;
	}

}
