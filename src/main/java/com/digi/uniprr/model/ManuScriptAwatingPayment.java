package com.digi.uniprr.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "manuscript")
public class ManuScriptAwatingPayment implements Serializable{

	private static final long serialVersionUID = -8833127469093489350L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "manuscript_id")
	private Integer manuScriptId;
	
	@Column(name="doi")
	private String doi;
	
	@ManyToOne
	@JoinColumn(name = "journal_id", updatable = false, insertable = false)
	private Journal journal;

	@Column(name = "journal_id")
	private Integer journalId;

	@Column(name = "manuscript_code")
	private String manuscriptCode;
	
	@Column(name = "manuscript_checklist_response")
	private String manuscriptChecklistResponse;
	
	@Column(name = "manuscript_title")
	private String manuscriptTitle;

	@Column(name = "manuscript_abstract")
	private String manuscriptAbstract;

	@Column(name = "manuscript_running_head")
	private String manuscriptRunningHead;

	@Column(name = "manuscript_short_title")
	private String manuscriptShortTitle;
	
	@Column(name="trial_registration_no")
	private String trialRegistrationNo;

	@OneToOne
	@JoinColumn(name = "manuscript_author_id", updatable = false, insertable = false)
	private User user;

	@Column(name = "manuscript_origin")
	private String manuscriptOrigin;

	
	@OneToOne(mappedBy = "manuscript")
	private ManuscriptTypeDetails manuscriptTypeDetails;

	@Column(name = "manuscript_author_id")
	private Integer authorId;

	@OneToOne
	@JoinColumn(name = "manuscript_status_id", updatable = false, insertable = false)
	private StatusMaster statusMaster;

	@Column(name = "manuscript_status_id")
	private Integer manuscriptStatusId;

	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;

	@Column(name = "final_decision_date")
	private Date finalDecisionDate;
	
	@Column(name="production_date")
	private Date productionDate;

	@Column(name="publishing_date")
	private Date publishingDate;
	
	@Column(name = "submission_date")
	private Date submissionDate;
	
	@Column(name = "withdrawn_date")
	private Date withdrawnDate;

	@Column(name = "is_active")
	private Integer isActive;

	@Column(name = "submitted_by")
	private Integer submittedBy;

	@Column(name = "declaration_of_interest")
	private String declarationOfInterest;
	
	@Column(name="declaration_of_interest_content")
	private String declarationOfInterestContent;
	
	@Column(name = "manuscript_keywords")
	private String manuscriptKeywords;

	@Column(name = "copyright_agreement")
	private String copyrightAgreement;
	
	@Column(name = "copyright_agreement_content")
	private String copyrightAgreementContent;

	@Column(name = "created_on", nullable = false, updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name = "updated_by")
	private Integer updatedBy;
	
	@Column(name = "qr_code_path")
	private String qrCodePath;
	
	@Column(name = "qr_file_name")
	private String qrFileName;
	
	@Column(name = "input_file")
	private String inputFile;
	
	@Column(name = "output_file")
	private String outputFile;
	
	@Column(name = "comment_for_production")
	private String commentForProduction;
	
	@Column(name="comment_from_production")
	private String commentFromProduction;
	
	@Column(name = "withdrawal_comment")
	private String withdrawalComment;
	
	@Column(name = "link_manuscript_code")
	private String linkManuscriptCode;
	
	@Column(name = "assigned_user")
	private Integer assignedUser;
	
	@Column(name="assigned_user_role")
	private Integer assignedUserRole;
	
	@Column(name = "technical_modification_date")
	private Date technicalModificationDate;
	
	@Column(name = "resubmission_date")
	private Date resubmissionDate;
	
	@Column(name="due_date")
	private Date dueDate;
	
	@Column(name = "manuscript_task_id")
	private String manuscriptTaskId;
	
	@Column(name = "file_type")
	private String fileType;
	
	@Column(name = "payment_status")
	private String paymentStatus;
	
	@Column(name = "wave_off")
	private String waveOff;
	
	@Column(name = "rejected_file_name")
	private String rejectedFileName;
	
	@Column(name = "rejected_file_path")
	private String rejectedFilePath;
	
	@Column(name="rejected_date")
	private Date rejectedDate;
	
	@Column(name="author_proof_date")
	private Date authorProofDate;
	
	@Column(name="manuscript_status_before_withdrawn")
	private Integer manuscriptStatusBeforeWithdrawn;
	
	@Column(name="manuscript_status_before_reject")
	private Integer manuscriptStatusBeforeReject;
	
	@Column(name = "withdrawn_request_date")
	private Date withdrawnRequestDate;

	@Column(name ="commentary_id")
	private Integer commentaryId;

	@Column(name ="lte_manuscript_id")
	private Integer lteManuscriptId;

	public Integer getLteManuscriptId() {
		return lteManuscriptId;
	}

	public void setLteManuscriptId(Integer lteManuscriptId) {
		this.lteManuscriptId = lteManuscriptId;
	}

	public Integer getCommentaryId() {
		return commentaryId;
	}

	public void setCommentaryId(Integer commentaryId) {
		this.commentaryId = commentaryId;
	}

	public Integer getManuscriptStatusBeforeReject() {
		return manuscriptStatusBeforeReject;
	}

	public void setManuscriptStatusBeforeReject(Integer manuscriptStatusBeforeReject) {
		this.manuscriptStatusBeforeReject = manuscriptStatusBeforeReject;
	}

	public String getManuscriptTaskId() {
		return manuscriptTaskId;
	}

	public void setManuscriptTaskId(String manuscriptTaskId) {
		this.manuscriptTaskId = manuscriptTaskId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getLinkManuscriptCode() {
		return linkManuscriptCode;
	}

	public void setLinkManuscriptCode(String linkManuscriptCode) {
		this.linkManuscriptCode = linkManuscriptCode;
	}

	public Date getTechnicalModificationDate() {
		return technicalModificationDate;
	}

	public void setTechnicalModificationDate(Date technicalModificationDate) {
		this.technicalModificationDate = technicalModificationDate;
	}

	public Date getResubmissionDate() {
		return resubmissionDate;
	}

	public void setResubmissionDate(Date resubmissionDate) {
		this.resubmissionDate = resubmissionDate;
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

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public Integer getManuScriptId() {
		return manuScriptId;
	}

	public void setManuScriptId(Integer manuScriptId) {
		this.manuScriptId = manuScriptId;
	}

	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
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

	public String getManuscriptAbstract() {
		return manuscriptAbstract;
	}

	public void setManuscriptAbstract(String manuscriptAbstract) {
		this.manuscriptAbstract = manuscriptAbstract;
	}

	public String getManuscriptShortTitle() {
		return manuscriptShortTitle;
	}

	public void setManuscriptShortTitle(String manuscriptShortTitle) {
		this.manuscriptShortTitle = manuscriptShortTitle;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public StatusMaster getStatusMaster() {
		return statusMaster;
	}

	public void setStatusMaster(StatusMaster statusMaster) {
		this.statusMaster = statusMaster;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public Integer getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(Integer submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getManuscriptRunningHead() {
		return manuscriptRunningHead;
	}

	public void setManuscriptRunningHead(String manuscriptRunningHead) {
		this.manuscriptRunningHead = manuscriptRunningHead;
	}

	public ManuscriptTypeDetails getManuscriptTypeDetails() {
		return manuscriptTypeDetails;
	}

	public void setManuscriptTypeDetails(ManuscriptTypeDetails manuscriptTypeDetails) {
		this.manuscriptTypeDetails = manuscriptTypeDetails;
	}

	public String getManuscriptOrigin() {
		return manuscriptOrigin;
	}

	public void setManuscriptOrigin(String manuscriptOrigin) {
		this.manuscriptOrigin = manuscriptOrigin;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
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

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getQrCodePath() {
		return qrCodePath;
	}

	public void setQrCodePath(String qrCodePath) {
		this.qrCodePath = qrCodePath;
	}

	public String getQrFileName() {
		return qrFileName;
	}

	public void setQrFileName(String qrFileName) {
		this.qrFileName = qrFileName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDeclarationOfInterestContent() {
		return declarationOfInterestContent;
	}

	public void setDeclarationOfInterestContent(String declarationOfInterestContent) {
		this.declarationOfInterestContent = declarationOfInterestContent;
	}

	public String getManuscriptKeywords() {
		return manuscriptKeywords;
	}

	public void setManuscriptKeywords(String manuscriptKeywords) {
		this.manuscriptKeywords = manuscriptKeywords;
	}

	public String getCopyrightAgreementContent() {
		return copyrightAgreementContent;
	}

	public void setCopyrightAgreementContent(String copyrightAgreementContent) {
		this.copyrightAgreementContent = copyrightAgreementContent;
	}

	public String getTrialRegistrationNo() {
		return trialRegistrationNo;
	}

	public void setTrialRegistrationNo(String trialRegistrationNo) {
		this.trialRegistrationNo = trialRegistrationNo;
	}

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public Integer getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(Integer assignedUser) {
		this.assignedUser = assignedUser;
	}

	public String getManuscriptChecklistResponse() {
		return manuscriptChecklistResponse;
	}

	public void setManuscriptChecklistResponse(String manuscriptChecklistResponse) {
		this.manuscriptChecklistResponse = manuscriptChecklistResponse;
	}

	public String getCommentFromProduction() {
		return commentFromProduction;
	}

	public void setCommentFromProduction(String commentFromProduction) {
		this.commentFromProduction = commentFromProduction;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getAssignedUserRole() {
		return assignedUserRole;
	}

	public void setAssignedUserRole(Integer assignedUserRole) {
		this.assignedUserRole = assignedUserRole;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	public String getWaveOff() {
		return waveOff;
	}

	public void setWaveOff(String waveOff) {
		this.waveOff = waveOff;
	}

	
	public String getRejectedFileName() {
		return rejectedFileName;
	}

	public void setRejectedFileName(String rejectedFileName) {
		this.rejectedFileName = rejectedFileName;
	}

	public String getRejectedFilePath() {
		return rejectedFilePath;
	}

	public void setRejectedFilePath(String rejectedFilePath) {
		this.rejectedFilePath = rejectedFilePath;
	}
	
	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}
	
	
	public Date getAuthorProofDate() {
		return authorProofDate;
	}

	public void setAuthorProofDate(Date authorProofDate) {
		this.authorProofDate = authorProofDate;
	}
	
	

	public Integer getManuscriptStatusBeforeWithdrawn() {
		return manuscriptStatusBeforeWithdrawn;
	}

	public void setManuscriptStatusBeforeWithdrawn(Integer manuscriptStatusBeforeWithdrawn) {
		this.manuscriptStatusBeforeWithdrawn = manuscriptStatusBeforeWithdrawn;
	}

	public Date getWithdrawnRequestDate() {
		return withdrawnRequestDate;
	}

	public void setWithdrawnRequestDate(Date withdrawnRequestDate) {
		this.withdrawnRequestDate = withdrawnRequestDate;
	}

	@Override
	public String toString() {
		return "ManuScriptAwatingPayment [manuScriptId=" + manuScriptId + ", doi=" + doi + ", journal=" + journal
				+ ", journalId=" + journalId + ", manuscriptCode=" + manuscriptCode + ", manuscriptChecklistResponse="
				+ manuscriptChecklistResponse + ", manuscriptTitle=" + manuscriptTitle + ", manuscriptAbstract="
				+ manuscriptAbstract + ", manuscriptRunningHead=" + manuscriptRunningHead + ", manuscriptShortTitle="
				+ manuscriptShortTitle + ", trialRegistrationNo=" + trialRegistrationNo + ", user=" + user
				+ ", manuscriptOrigin=" + manuscriptOrigin + ", manuscriptTypeDetails=" + manuscriptTypeDetails
				+ ", authorId=" + authorId + ", statusMaster=" + statusMaster + ", manuscriptStatusId="
				+ manuscriptStatusId + ", manuscriptVersion=" + manuscriptVersion + ", finalDecisionDate="
				+ finalDecisionDate + ", productionDate=" + productionDate + ", publishingDate=" + publishingDate
				+ ", submissionDate=" + submissionDate + ", withdrawnDate=" + withdrawnDate + ", isActive=" + isActive
				+ ", submittedBy=" + submittedBy + ", declarationOfInterest=" + declarationOfInterest
				+ ", declarationOfInterestContent=" + declarationOfInterestContent + ", manuscriptKeywords="
				+ manuscriptKeywords + ", copyrightAgreement=" + copyrightAgreement + ", copyrightAgreementContent="
				+ copyrightAgreementContent + ", createdOn=" + createdOn + ", createdBy=" + createdBy + ", updatedOn="
				+ updatedOn + ", updatedBy=" + updatedBy + ", qrCodePath=" + qrCodePath + ", qrFileName=" + qrFileName
				+ ", inputFile=" + inputFile + ", outputFile=" + outputFile + ", commentForProduction="
				+ commentForProduction + ", commentFromProduction=" + commentFromProduction + ", withdrawalComment="
				+ withdrawalComment + ", linkManuscriptCode=" + linkManuscriptCode + ", assignedUser=" + assignedUser
				+ ", assignedUserRole=" + assignedUserRole + ", technicalModificationDate=" + technicalModificationDate
				+ ", resubmissionDate=" + resubmissionDate + ", dueDate=" + dueDate + ", manuscriptTaskId="
				+ manuscriptTaskId + ", fileType=" + fileType + ", paymentStatus=" + paymentStatus + ", waveOff="
				+ waveOff + ", rejectedFileName=" + rejectedFileName + ", rejectedFilePath=" + rejectedFilePath
				+ ", rejectedDate=" + rejectedDate + ", authorProofDate=" + authorProofDate
				+ ", manuscriptStatusBeforeWithdrawn=" + manuscriptStatusBeforeWithdrawn
				+ ", manuscriptStatusBeforeReject=" + manuscriptStatusBeforeReject + ", withdrawnRequestDate="
				+ withdrawnRequestDate + ", commentaryId=" + commentaryId + "]";
	}
		
}