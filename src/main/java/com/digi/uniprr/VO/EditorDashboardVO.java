package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class EditorDashboardVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer manuscriptId;

	private String manuscriptTitle;
	
	private String editorUsername;

	private Date submittedDate;
	
	private Date withdrawnDate;
	
	private Date assignedDate;
	
	private Date unassignedDate;
	
	private Date startDate;
	
	private Date completeDate;
	
	private Date rejectDate;
	
	private Date acceptDate;
	
	private Date dueDate;
	
	private Date updateDate;
	
	private String manuscriptCode;

	private String journalCode;

	private String statusValue;
	
	private String statusValueForAuthor;
	
	private String statusValueForEditor;

	private Integer statusId;

	private String journalTitle;
	
	private String manuscriptAbstract;

	private String manuscriptType;

	private String authorName;

	private Integer authorId;

	private Integer manuscriptVersion;
	
	private String statusValueForUser;
	
	private Integer journalId;
	
	private String statusForAuthor;
	
	private String withdrawalComment;
	
	private Integer editorId;
	
	private Integer editorRoleId;
	
	private String commentForProduction;
	
	private String commentFromProduction;
	
	private Date productionDate;
	
	private Date rejectedDate;
	
	private Date authorProofDate;
	
	private String rejectedFileName;
	
	private String rejectedFilePath;
	
	private Integer manuscriptStatusBeforeWithdrawn;
	
	private Date withdrawnRequestDate;
	
	private boolean isImpactArticle;
	
	private String authorLink;

	

	
	
	public String getCommentForProduction() {
		return commentForProduction;
	}

	public void setCommentForProduction(String commentForProduction) {
		this.commentForProduction = commentForProduction;
	}

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public String getWithdrawalComment() {
		return withdrawalComment;
	}

	public void setWithdrawalComment(String withdrawalComment) {
		this.withdrawalComment = withdrawalComment;
	}

	public String getStatusValueForUser() {
		return statusValueForUser;
	}

	public void setStatusValueForUser(String statusValueForUser) {
		this.statusValueForUser = statusValueForUser;
	}

	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Date getUnassignedDate() {
		return unassignedDate;
	}

	public void setUnassignedDate(Date unassignedDate) {
		this.unassignedDate = unassignedDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public Date getRejectDate() {
		return rejectDate;
	}

	public void setRejectDate(Date rejectDate) {
		this.rejectDate = rejectDate;
	}

	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getManuscriptType() {
		return manuscriptType;
	}

	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public String getManuscriptTitle() {
		return manuscriptTitle;
	}

	public void setManuscriptTitle(String manuscriptTitle) {
		this.manuscriptTitle = manuscriptTitle;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public Date getWithdrawnDate() {
		return withdrawnDate;
	}

	public void setWithdrawnDate(Date withdrawnDate) {
		this.withdrawnDate = withdrawnDate;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getStatusForAuthor() {
		return statusForAuthor;
	}

	public void setStatusForAuthor(String statusForAuthor) {
		this.statusForAuthor = statusForAuthor;
	}

	public String getManuscriptAbstract() {
		return manuscriptAbstract;
	}

	public void setManuscriptAbstract(String manuscriptAbstract) {
		this.manuscriptAbstract = manuscriptAbstract;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getEditorId() {
		return editorId;
	}

	public void setEditorId(Integer editorId) {
		this.editorId = editorId;
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

	public String getCommentFromProduction() {
		return commentFromProduction;
	}

	public void setCommentFromProduction(String commentFromProduction) {
		this.commentFromProduction = commentFromProduction;
	}

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
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

	public String getEditorUsername() {
		return editorUsername;
	}

	public void setEditorUsername(String editorUsername) {
		this.editorUsername = editorUsername;
	}
	
	public Date getwithdrawnRequestDate() {
		return withdrawnRequestDate;
	}

	public void setwithdrawnRequestDate(Date withdrawnRequestDate) {
		this.withdrawnRequestDate = withdrawnRequestDate;
	}
	
	

	public boolean isImpactArticle() {
		return isImpactArticle;
	}

	public void setImpactArticle(boolean isImpactArticle) {
		this.isImpactArticle = isImpactArticle;
	}

	public String getAuthorLink() {
		return authorLink;
	}

	public void setAuthorLink(String authorLink) {
		this.authorLink = authorLink;
	}

	@Override
	public String toString() {
		return "EditorDashboardVO [manuscriptId=" + manuscriptId + ", manuscriptTitle=" + manuscriptTitle
				+ ", editorUsername=" + editorUsername + ", submittedDate=" + submittedDate + ", withdrawnDate="
				+ withdrawnDate + ", assignedDate=" + assignedDate + ", unassignedDate=" + unassignedDate
				+ ", startDate=" + startDate + ", completeDate=" + completeDate + ", rejectDate=" + rejectDate
				+ ", acceptDate=" + acceptDate + ", dueDate=" + dueDate + ", updateDate=" + updateDate
				+ ", manuscriptCode=" + manuscriptCode + ", journalCode=" + journalCode + ", statusValue=" + statusValue
				+ ", statusValueForAuthor=" + statusValueForAuthor + ", statusValueForEditor=" + statusValueForEditor
				+ ", statusId=" + statusId + ", journalTitle=" + journalTitle + ", manuscriptAbstract="
				+ manuscriptAbstract + ", manuscriptType=" + manuscriptType + ", authorName=" + authorName
				+ ", authorId=" + authorId + ", manuscriptVersion=" + manuscriptVersion + ", statusValueForUser="
				+ statusValueForUser + ", journalId=" + journalId + ", statusForAuthor=" + statusForAuthor
				+ ", withdrawalComment=" + withdrawalComment + ", editorId=" + editorId + ", editorRoleId="
				+ editorRoleId + ", commentForProduction=" + commentForProduction + ", commentFromProduction="
				+ commentFromProduction + ", productionDate=" + productionDate + ", rejectedDate=" + rejectedDate
				+ ", authorProofDate=" + authorProofDate + ", rejectedFileName=" + rejectedFileName
				+ ", rejectedFilePath=" + rejectedFilePath + ", manuscriptStatusBeforeWithdrawn="
				+ manuscriptStatusBeforeWithdrawn + ", withdrawnRequestDate=" + withdrawnRequestDate + "]";
	}	
	
	
}
