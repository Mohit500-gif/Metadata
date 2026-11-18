package com.digi.uniprr.VO;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.enums.Salutation;

public class ManuscriptListVO {

	private List<Integer> manuscriptStatusId;

	private Integer manuStatusId;
	
	private Integer editorUserId;

	private String manuScriptType;

	private Integer editorRoleId;

	private String statusValueForAuthor;

	private String statusValueForEditor;

	private String orcidId;

	private String researcherId;
	
	private String mobileNumber;
	
	private Integer countryCode;

	private String trialRegistrationNo;

	private String manuscriptChecklistResponse;

	private String manuStatusCode;

	private Integer managingEditorId;

	private String firstpagefile;

	private String qr_code;

	private String manuscripCode;

	private String doi;

	private String manuscripShortTitle;

	private String manuscripTitle;

	private String manuscriptAbstract;

	private String manuscriptRunningHead;

	private Integer journalId;

	private String address;

	private Integer manuscriptVersion;
	
	private Integer templateId;

	private Date submissionDate;

	private Integer manuscriptAuthorId;

	private Integer submittedBy;

	private Integer createdBy;

	private Date createdOn;

	private Integer updatedBy;

	private Date updatedOn;
	
	private Date assignDate;

	private Date dueDate;

	private Integer manuScriptId;

	private String authorName;

	private String salutation;

	private Salutation salutatio;

	private String email;

	private List<ImageVO> image;

	private List<AuthorVO> auhtor;

	private String institution;

	private String journalCode;

	private String journalTitle;

	private Integer roleId;

	private Integer userId;

	private String keywords;

	private String articleFile;

	private Integer assignToId;

	private List<Integer> assignToID;

	private String message;

	private String word;

	private String blackAndWhiteFigure;

	private String colorFigures;

	private String table;

	private String page;

	private String isChargeRequired;

	private List<ManuscriptManagingEditorVO> managingEditor;

	private List<ManagingEditorVO> editorList;

	private Date finalDecisionDate;

	private Date publishingDate;

	private Date withdrawnDate;

	private Date acceptedDate;

	private Date reviewDate;
	
	private Date productionDate;
	
	MultipartFile file;


	private String acceptByReviewerUrl;
	private String rejectByReviewerUrl;
	private String viewByReviewerUrl;

	private String declarationOfInterest;
	private String declarationOfInterestContent;

	private String editorStatus;

	private String sendEmailUrl;

	private List<ReviewerSuggestedVO> reviewerSuggestedVO;

	private String journalMailTemplatesBody;
	private String journalMailTemplatesSubject;

	
	private Integer repliedOnTime;
	private Integer repliedAfterDueDate;
	private Integer didNotReply;
	private Integer currentlyInHand;
	private String copyrightAgreement;

	private String copyrightAgreementContent;

	private Integer someUserDeclined;

	private String commentForProduction;

	private String withdrawalComment;

	private String editorName;
	
	private Integer reviewdCount;

	private Integer totalReviewCount;

	private Integer blinkerCount;
	
	private String authorCountry;
	
	private String reviewerFileName;
	
	private String reviewerFilePath;
	
	private Date rejectedDate;
	private String rejectedBy;
	
	private Date palagiarismCreatedDate;
	
	private String palagiarismURL;
	
	private Integer fileNumber;
	
	private Integer manuscriptStatusBeforeWithdrawn;
	private Integer manuscriptStatusBeforeReject;
	
	private Date resubmissionDate;

	private String subjects;
	private String subject;
    private String sendBackEicComment;
	private Integer pageNo;
	private Integer limit;
	private String search;
	private String sortBy;
	private String sort;
	private List<ReviewerDetailsVO> manuScriptReviewers;
	private String otherReviewer;
	private Date withdrawnRequestDate;
	private List<ManuScriptTrackVO> commentaryId;
	private ManuScriptTrackVO lteManuscriptId;
	private String EIC_Folder;
	private Integer previousStatus;
	private boolean showTechnicalModification;

	public Integer getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(Integer previousStatus) {
		this.previousStatus = previousStatus;
	}

	
	public String getEIC_Folder() {
		return EIC_Folder;
	}
	public void setEIC_Folder(String eIC_Folder) {
		EIC_Folder = eIC_Folder;
	}
	public ManuscriptListVO() {}
	public ManuscriptListVO(Integer manuScriptId, Integer manuscriptVersion,String manuscripCode, String manuscripTitle, String manuScriptType, String editorName) {
		this.manuScriptId = manuScriptId;
		this.manuscriptVersion = manuscriptVersion;
		this.manuscripCode = manuscripCode;
		this.manuscripTitle = manuscripTitle;
		this.manuScriptType = manuScriptType;
		this.editorName = editorName;
	}
	
	public ManuscriptListVO(Integer manuScriptId, Integer manuscriptVersion,String manuscripCode, String manuscripTitle, String manuscriptAbstract, String manuScriptType, String authorName, Date palagiarismCreatedDate, String palagiarismURL, Integer fileNumber) {
		this.manuScriptId = manuScriptId;
		this.manuscriptVersion = manuscriptVersion;
		this.manuscripCode = manuscripCode;
		this.manuscripTitle = manuscripTitle;
		this.manuscriptAbstract = manuscriptAbstract;
		this.manuScriptType = manuScriptType;
		this.authorName = authorName;
		this.palagiarismCreatedDate = palagiarismCreatedDate;
		this.palagiarismURL = palagiarismURL;
		this.fileNumber = fileNumber;
	}
	
	public ManuscriptListVO(Integer manuScriptId,String manuscripCode, Integer manuscriptVersion, String manuscripTitle, Integer editorUserId,String manuScriptType, Date submissionDate, Integer manuStatusId, String authorName, String EIC_Folder, Integer manuscriptAuthorId) {
		this.manuScriptId = manuScriptId;
		this.manuscripCode = manuscripCode;
		this.manuscriptVersion = manuscriptVersion;
		this.manuscripTitle = manuscripTitle;
		this.editorUserId = editorUserId;
		this.manuScriptType = manuScriptType;
		this.submissionDate = submissionDate;
		this.manuStatusId = manuStatusId;
		this.authorName = authorName;
		this.EIC_Folder = EIC_Folder;
		this.manuscriptAuthorId = manuscriptAuthorId;
	}
	
	public ManuscriptListVO(Integer manuScriptId,String manuscripCode, Integer manuscriptVersion, String manuscripTitle, 
			Integer editorUserId,String manuScriptType, Date submissionDate, Integer manuStatusId, String authorName, 
			Integer editorRoleId, Integer managingEditorId, Date assignDate, Date dueDate, Integer manuscriptAuthorId) {
		this.manuScriptId = manuScriptId;
		this.manuscripCode = manuscripCode;
		this.manuscriptVersion = manuscriptVersion;
		this.manuscripTitle = manuscripTitle;
		this.editorUserId = editorUserId;
		this.manuScriptType = manuScriptType;
		this.submissionDate = submissionDate;
		this.manuStatusId = manuStatusId;
		this.authorName = authorName;
		this.editorRoleId = editorRoleId;
		this.managingEditorId = managingEditorId;
		this.assignDate = assignDate;
		this.dueDate = dueDate;
		this.manuscriptAuthorId = manuscriptAuthorId;
	}
	
	public ManuScriptTrackVO getLteManuscriptId() {
		return lteManuscriptId;
	}

	public void setLteManuscriptId(ManuScriptTrackVO lteManuscriptId) {
		this.lteManuscriptId = lteManuscriptId;
	}

	public String getOtherReviewer() {
		return otherReviewer;
	}

	public void setOtherReviewer(String otherReviewer) {
		this.otherReviewer = otherReviewer;
	}

	public String getAuthorCountry() {
		return authorCountry;
	}

	public void setAuthorCountry(String authorCountry) {
		this.authorCountry = authorCountry;
	}

	public Integer getReviewdCount() {
		return reviewdCount;
	}

	public void setReviewdCount(Integer reviewdCount) {
		this.reviewdCount = reviewdCount;
	}

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getWithdrawalComment() {
		return withdrawalComment;
	}

	public void setWithdrawalComment(String withdrawalComment) {
		this.withdrawalComment = withdrawalComment;
	}

	public String getCommentForProduction() {
		return commentForProduction;
	}

	public void setCommentForProduction(String commentForProduction) {
		this.commentForProduction = commentForProduction;
	}

	public Integer getSomeUserDeclined() {
		return someUserDeclined;
	}

	public void setSomeUserDeclined(Integer someUserDeclined) {
		this.someUserDeclined = someUserDeclined;
	}

	public Integer getRepliedOnTime() {
		return repliedOnTime;
	}

	public void setRepliedOnTime(Integer repliedOnTime) {
		this.repliedOnTime = repliedOnTime;
	}

	public Integer getRepliedAfterDueDate() {
		return repliedAfterDueDate;
	}

	public void setRepliedAfterDueDate(Integer repliedAfterDueDate) {
		this.repliedAfterDueDate = repliedAfterDueDate;
	}

	public Integer getDidNotReply() {
		return didNotReply;
	}

	public void setDidNotReply(Integer didNotReply) {
		this.didNotReply = didNotReply;
	}

	public Integer getCurrentlyInHand() {
		return currentlyInHand;
	}

	public void setCurrentlyInHand(Integer currentlyInHand) {
		this.currentlyInHand = currentlyInHand;
	}

	public List<ManagingEditorVO> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<ManagingEditorVO> editorList) {
		this.editorList = editorList;
	}

	public List<ManuscriptManagingEditorVO> getManagingEditor() {
		return managingEditor;
	}

	public void setManagingEditor(List<ManuscriptManagingEditorVO> managingEditor) {
		this.managingEditor = managingEditor;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getManuscriptAbstract() {
		return manuscriptAbstract;
	}

	public void setManuscriptAbstract(String manuscriptAbstract) {
		this.manuscriptAbstract = manuscriptAbstract;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getManuScriptType() {
		return manuScriptType;
	}

	public void setManuScriptType(String manuScriptType) {
		this.manuScriptType = manuScriptType;
	}

	public String getManuStatusCode() {
		return manuStatusCode;
	}

	public void setManuStatusCode(String manuStatusCode) {
		this.manuStatusCode = manuStatusCode;
	}

	public Integer getManuStatusId() {
		return manuStatusId;
	}

	public void setManuStatusId(Integer manuStatusId) {
		this.manuStatusId = manuStatusId;
	}

	public List<Integer> getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(List<Integer> manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getManuscripCode() {
		return manuscripCode;
	}

	public void setManuscripCode(String manuscripCode) {
		this.manuscripCode = manuscripCode;
	}

	public String getManuscripTitle() {
		return manuscripTitle;
	}

	public void setManuscripTitle(String manuscripTitle) {
		this.manuscripTitle = manuscripTitle;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getManuscriptAuthorId() {
		return manuscriptAuthorId;
	}

	public void setManuscriptAuthorId(Integer manuscriptAuthorId) {
		this.manuscriptAuthorId = manuscriptAuthorId;
	}

	public Integer getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(Integer submittedBy) {
		this.submittedBy = submittedBy;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getManuScriptId() {
		return manuScriptId;
	}

	public void setManuScriptId(Integer manuScriptId) {
		this.manuScriptId = manuScriptId;
	}

	public Integer getAssignToId() {
		return assignToId;
	}

	public void setAssignToId(Integer assignToId) {
		this.assignToId = assignToId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getManuscriptRunningHead() {
		return manuscriptRunningHead;
	}

	public void setManuscriptRunningHead(String manuscriptRunningHead) {
		this.manuscriptRunningHead = manuscriptRunningHead;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getBlackAndWhiteFigure() {
		return blackAndWhiteFigure;
	}

	public void setBlackAndWhiteFigure(String blackAndWhiteFigure) {
		this.blackAndWhiteFigure = blackAndWhiteFigure;
	}

	public String getColorFigures() {
		return colorFigures;
	}

	public void setColorFigures(String colorFigures) {
		this.colorFigures = colorFigures;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<Integer> getAssignToID() {
		return assignToID;
	}

	public void setAssignToID(List<Integer> assignToID) {
		this.assignToID = assignToID;
	}

	public String getManuscripShortTitle() {
		return manuscripShortTitle;
	}

	public void setManuscripShortTitle(String manuscripShortTitle) {
		this.manuscripShortTitle = manuscripShortTitle;
	}

	public String getIsChargeRequired() {
		return isChargeRequired;
	}

	public void setIsChargeRequired(String isChargeRequired) {
		this.isChargeRequired = isChargeRequired;
	}

	public Date getFinalDecisionDate() {
		return finalDecisionDate;
	}

	public void setFinalDecisionDate(Date finalDecisionDate) {
		this.finalDecisionDate = finalDecisionDate;
	}

	public Date getPublishingDate() {
		return publishingDate;
	}

	public void setPublishingDate(Date publishingDate) {
		this.publishingDate = publishingDate;
	}

	public Date getWithdrawnDate() {
		return withdrawnDate;
	}

	public void setWithdrawnDate(Date withdrawnDate) {
		this.withdrawnDate = withdrawnDate;
	}

	public Salutation getSalutatio() {
		return salutatio;
	}

	public void setSalutatio(Salutation salutatio) {
		this.salutatio = salutatio;
	}

	public List<ImageVO> getImage() {
		return image;
	}

	public void setImage(List<ImageVO> image) {
		this.image = image;
	}

	public List<AuthorVO> getAuhtor() {
		return auhtor;
	}

	public void setAuhtor(List<AuthorVO> auhtor) {
		this.auhtor = auhtor;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getArticleFile() {
		return articleFile;
	}

	public void setArticleFile(String articleFile) {
		this.articleFile = articleFile;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public Date getAcceptedDate() {
		return acceptedDate;
	}

	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getFirstpagefile() {
		return firstpagefile;
	}

	public void setFirstpagefile(String firstpagefile) {
		this.firstpagefile = firstpagefile;
	}

	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
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

	public String getDeclarationOfInterest() {
		return declarationOfInterest;
	}

	public void setDeclarationOfInterest(String declarationOfInterest) {
		this.declarationOfInterest = declarationOfInterest;
	}

	public String getDeclarationOfInterestContent() {
		return declarationOfInterestContent;
	}

	public void setDeclarationOfInterestContent(String declarationOfInterestContent) {
		this.declarationOfInterestContent = declarationOfInterestContent;
	}

	public String getSendEmailUrl() {
		return sendEmailUrl;
	}

	public void setSendEmailUrl(String sendEmailUrl) {
		this.sendEmailUrl = sendEmailUrl;
	}

	public List<ReviewerSuggestedVO> getReviewerSuggestedVO() {
		return reviewerSuggestedVO;
	}

	public void setReviewerSuggestedVO(List<ReviewerSuggestedVO> reviewerSuggestedVO) {
		this.reviewerSuggestedVO = reviewerSuggestedVO;
	}

	public String getEditorStatus() {
		return editorStatus;
	}

	public void setEditorStatus(String editorStatus) {
		this.editorStatus = editorStatus;
	}

	public String getJournalMailTemplatesBody() {
		return journalMailTemplatesBody;
	}

	public void setJournalMailTemplatesBody(String journalMailTemplatesBody) {
		this.journalMailTemplatesBody = journalMailTemplatesBody;
	}

	public String getOrcidId() {
		return orcidId;
	}

	public void setOrcidId(String orcidId) {
		this.orcidId = orcidId;
	}

	public String getResearcherId() {
		return researcherId;
	}

	public void setResearcherId(String researcherId) {
		this.researcherId = researcherId;
	}

	public String getTrialRegistrationNo() {
		return trialRegistrationNo;
	}

	public void setTrialRegistrationNo(String trialRegistrationNo) {
		this.trialRegistrationNo = trialRegistrationNo;
	}

	public String getCopyrightAgreement() {
		return copyrightAgreement;
	}

	public void setCopyrightAgreement(String copyrightAgreement) {
		this.copyrightAgreement = copyrightAgreement;
	}

	public String getCopyrightAgreementContent() {
		return copyrightAgreementContent;
	}

	public void setCopyrightAgreementContent(String copyrightAgreementContent) {
		this.copyrightAgreementContent = copyrightAgreementContent;
	}

	public String getManuscriptChecklistResponse() {
		return manuscriptChecklistResponse;
	}

	public void setManuscriptChecklistResponse(String manuscriptChecklistResponse) {
		this.manuscriptChecklistResponse = manuscriptChecklistResponse;
	}

	public Integer getEditorRoleId() {
		return editorRoleId;
	}

	public void setEditorRoleId(Integer editorRoleId) {
		this.editorRoleId = editorRoleId;
	}

	public String getStatusValueForAuthor() {
		return statusValueForAuthor;
	}

	public void setStatusValueForAuthor(String statusValueForAuthor) {
		this.statusValueForAuthor = statusValueForAuthor;
	}

	public String getStatusValueForEditor() {
		return statusValueForEditor;
	}

	public void setStatusValueForEditor(String statusValueForEditor) {
		this.statusValueForEditor = statusValueForEditor;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getManagingEditorId() {
		return managingEditorId;
	}

	public void setManagingEditorId(Integer managingEditorId) {
		this.managingEditorId = managingEditorId;
	}

	public Integer getEditorUserId() {
		return editorUserId;
	}

	public void setEditorUserId(Integer editorUserId) {
		this.editorUserId = editorUserId;
	}

	public String getReviewerFileName() {
		return reviewerFileName;
	}

	public void setReviewerFileName(String reviewerFileName) {
		this.reviewerFileName = reviewerFileName;
	}

	public String getReviewerFilePath() {
		return reviewerFilePath;
	}

	public void setReviewerFilePath(String reviewerFilePath) {
		this.reviewerFilePath = reviewerFilePath;
	}

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public Date getPalagiarismCreatedDate() {
		return palagiarismCreatedDate;
	}

	public void setPalagiarismCreatedDate(Date palagiarismCreatedDate) {
		this.palagiarismCreatedDate = palagiarismCreatedDate;
	}

	public String getPalagiarismURL() {
		return palagiarismURL;
	}

	public void setPalagiarismURL(String palagiarismURL) {
		this.palagiarismURL = palagiarismURL;
	}

	public String getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(String rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public Integer getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(Integer fileNumber) {
		this.fileNumber = fileNumber;
	}

	public Integer getManuscriptStatusBeforeWithdrawn() {
		return manuscriptStatusBeforeWithdrawn;
	}

	public void setManuscriptStatusBeforeWithdrawn(Integer manuscriptStatusBeforeWithdrawn) {
		this.manuscriptStatusBeforeWithdrawn = manuscriptStatusBeforeWithdrawn;
	}

	public Integer getManuscriptStatusBeforeReject() {
		return manuscriptStatusBeforeReject;
	}

	public void setManuscriptStatusBeforeReject(Integer manuscriptStatusBeforeReject) {
		this.manuscriptStatusBeforeReject = manuscriptStatusBeforeReject;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Integer getTotalReviewCount() {
		return totalReviewCount;
	}

	public void setTotalReviewCount(Integer totalReviewCount) {
		this.totalReviewCount = totalReviewCount;
	}

	public Integer getBlinkerCount() {
		return blinkerCount;
	}

	public void setBlinkerCount(Integer blinkerCount) {
		this.blinkerCount = blinkerCount;
	}

	public Date getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}

	public Date getResubmissionDate() {
		return resubmissionDate;
	}

	public void setResubmissionDate(Date resubmissionDate) {
		this.resubmissionDate = resubmissionDate;
	}

	public List<ReviewerDetailsVO> getManuScriptReviewers() {
		return manuScriptReviewers;
	}

	public void setManuScriptReviewers(List<ReviewerDetailsVO> manuScriptReviewers) {
		this.manuScriptReviewers = manuScriptReviewers;
	}

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSendBackEicComment() {
		return sendBackEicComment;
	}
	public void setSendBackEicComment(String sendBackEicComment) {
		this.sendBackEicComment = sendBackEicComment;
	}
	public Date getWithdrawnRequestDate() {
		return withdrawnRequestDate;
	}

	public void setWithdrawnRequestDate(Date withdrawnRequestDate) {
		this.withdrawnRequestDate = withdrawnRequestDate;
	}

	public List<ManuScriptTrackVO> getCommentaryId() {
		return commentaryId;
	}

	public void setCommentaryId(List<ManuScriptTrackVO> commentaryId) {
		this.commentaryId = commentaryId;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getJournalMailTemplatesSubject() {
		return journalMailTemplatesSubject;
	}
	public void setJournalMailTemplatesSubject(String journalMailTemplatesSubject) {
		this.journalMailTemplatesSubject = journalMailTemplatesSubject;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isShowTechnicalModification() {
		return showTechnicalModification;
	}

	public void setShowTechnicalModification(boolean showTechnicalModification) {
		this.showTechnicalModification = showTechnicalModification;
	}
	
	
}