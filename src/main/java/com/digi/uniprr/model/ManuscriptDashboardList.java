package com.digi.uniprr.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.digi.uniprr.VO.ManagingEditorVO;
import com.digi.uniprr.VO.ManuscriptManagingEditorVO;

public class ManuscriptDashboardList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Integer> manuscriptStatusId;

	private Integer manuStatusId;
	
	private Integer roleId;
	
	private Integer EditorId;
	
	private Integer userId;
	
	private String manuScriptType;

	private String manuscripCode;
	
	private String statusCode;

    private String manuscripTitle;

	private Integer journalId;

    private Integer manuscriptVersion;
	
	private Date submissionDate;
	
	private Date assignDate;
	
	private String reportType;
	
	private Date dueDate;
	
	private Date productionDate;
    private String manuStatusCode;
    private String commentForProduction;
	private Integer manuScriptId;
	private Integer manuscriptAuthorId;
	private String authorName;
	private String editorName;
	private Date rejectedDate;
	private String rejectedBy;
	private Integer manuscriptStatusBeforeReject;
	private String reviewerFileName;	
	private String reviewerFilePath;
	
	private Integer pageNo;
	private Integer limit;
	private String search;
	private String sortBy;
	private String sort;
	
	private Date publishingDate;
	private String issueName;

	private Integer commentaryId;
	private Integer lteManuscriptId;
	
	private List<ManagingEditorVO> editorList;
	
	private Integer managingEditorId;
	private Integer activeEditorFlag;

	private String submitDate;
	private String acceptDate;
	
	private Integer manuscriptStatusBeforeWithdrawn;	
	private Date withdrawnRequestDate;
	private String withdrawalComment;
	private Date withdrawnDate;
	private Integer previousUserId;
	private Integer currentUserId;
	private Integer currentUserRole;
	private Integer previousUserRole;
	private String EICFolder;
	private Integer previousStatus;
	
	private String manWidrawnStatus;
	
	private boolean showTechnicalModification;
	
	public String getManWidrawnStatus() {
		return manWidrawnStatus;
	}

	public void setManWidrawnStatus(String manWidrawnStatus) {
		this.manWidrawnStatus = manWidrawnStatus;
	}

	public Integer getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(Integer previousStatus) {
		this.previousStatus = previousStatus;
	}

	public String getEICFolder() {
		return EICFolder;
	}

	public void setEICFolder(String eICFolder) {
		EICFolder = eICFolder;
	}

	public String getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

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

	public List<ManagingEditorVO> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<ManagingEditorVO> editorList) {
		this.editorList = editorList;
	}

	public Integer getManagingEditorId() {
		return managingEditorId;
	}

	public void setManagingEditorId(Integer managingEditorId) {
		this.managingEditorId = managingEditorId;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
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

	
	public Integer getManuScriptId() {
		return manuScriptId;
	}

	public void setManuScriptId(Integer manuScriptId) {
		this.manuScriptId = manuScriptId;
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
	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
	

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public String getManuStatusCode() {
		return manuStatusCode;
	}

	public void setManuStatusCode(String manuStatusCode) {
		this.manuStatusCode = manuStatusCode;
	}

	public String getCommentForProduction() {
		return commentForProduction;
	}

	public void setCommentForProduction(String commentForProduction) {
		this.commentForProduction = commentForProduction;
	}

	public Date getPublishingDate() {
		return publishingDate;
	}

	public void setPublishingDate(Date publishingDate) {
		this.publishingDate = publishingDate;
	}
	
	

	public String getIssueName() {
		return issueName;
	}

	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}

	public Date getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public String getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(String rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public Integer getManuscriptStatusBeforeReject() {
		return manuscriptStatusBeforeReject;
	}

	public void setManuscriptStatusBeforeReject(Integer manuscriptStatusBeforeReject) {
		this.manuscriptStatusBeforeReject = manuscriptStatusBeforeReject;
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

	public Integer getActiveEditorFlag() {
		return activeEditorFlag;
	}

	public void setActiveEditorFlag(Integer activeEditorFlag) {
		this.activeEditorFlag = activeEditorFlag;
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

	public String getWithdrawalComment() {
		return withdrawalComment;
	}

	public void setWithdrawalComment(String withdrawalComment) {
		this.withdrawalComment = withdrawalComment;
	}

	public Date getWithdrawnDate() {
		return withdrawnDate;
	}

	public void setWithdrawnDate(Date withdrawnDate) {
		this.withdrawnDate = withdrawnDate;
	}

	public Integer getEditorId() {
		return EditorId;
	}

	public void setEditorId(Integer editorId) {
		EditorId = editorId;
	}

	public Integer getPreviousUserId() {
		return previousUserId;
	}

	public void setPreviousUserId(Integer previousUserId) {
		this.previousUserId = previousUserId;
	}

	public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Integer getCurrentUserRole() {
		return currentUserRole;
	}

	public void setCurrentUserRole(Integer currentUserRole) {
		this.currentUserRole = currentUserRole;
	}

	public Integer getPreviousUserRole() {
		return previousUserRole;
	}

	public void setPreviousUserRole(Integer previousUserRole) {
		this.previousUserRole = previousUserRole;
	}
	
	

	public boolean isShowTechnicalModification() {
		return showTechnicalModification;
	}

	public void setShowTechnicalModification(boolean showTechnicalModification) {
		this.showTechnicalModification = showTechnicalModification;
	}

	@Override
	public String toString() {
		return "ManuscriptDashboardList [manuscriptStatusId=" + manuscriptStatusId + ", manuStatusId=" + manuStatusId
				+ ", roleId=" + roleId + ", EditorId=" + EditorId + ", userId=" + userId + ", manuScriptType="
				+ manuScriptType + ", manuscripCode=" + manuscripCode + ", statusCode=" + statusCode
				+ ", manuscripTitle=" + manuscripTitle + ", journalId=" + journalId + ", manuscriptVersion="
				+ manuscriptVersion + ", submissionDate=" + submissionDate + ", assignDate=" + assignDate
				+ ", reportType=" + reportType + ", dueDate=" + dueDate + ", productionDate=" + productionDate
				+ ", manuStatusCode=" + manuStatusCode + ", commentForProduction=" + commentForProduction
				+ ", manuScriptId=" + manuScriptId + ", manuscriptAuthorId=" + manuscriptAuthorId + ", authorName="
				+ authorName + ", editorName=" + editorName + ", rejectedDate=" + rejectedDate + ", rejectedBy="
				+ rejectedBy + ", manuscriptStatusBeforeReject=" + manuscriptStatusBeforeReject + ", reviewerFileName="
				+ reviewerFileName + ", reviewerFilePath=" + reviewerFilePath + ", pageNo=" + pageNo + ", limit="
				+ limit + ", search=" + search + ", sortBy=" + sortBy + ", sort=" + sort + ", publishingDate="
				+ publishingDate + ", issueName=" + issueName + ", commentaryId=" + commentaryId + ", lteManuscriptId="
				+ lteManuscriptId + ", editorList=" + editorList + ", managingEditorId=" + managingEditorId
				+ ", activeEditorFlag=" + activeEditorFlag + ", submitDate=" + submitDate + ", acceptDate=" + acceptDate
				+ ", manuscriptStatusBeforeWithdrawn=" + manuscriptStatusBeforeWithdrawn + ", withdrawnRequestDate="
				+ withdrawnRequestDate + ", withdrawalComment=" + withdrawalComment + ", withdrawnDate=" + withdrawnDate
				+ ", previousUserId=" + previousUserId + ", currentUserId=" + currentUserId + ", currentUserRole="
				+ currentUserRole + ", previousUserRole=" + previousUserRole + ", EICFolder=" + EICFolder
				+ ", previousStatus=" + previousStatus + ", manWidrawnStatus=" + manWidrawnStatus
				+ ", showTechnicalModification=" + showTechnicalModification + "]";
	}

	
}