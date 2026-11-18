package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CommentaryVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "CommentaryVo{" +
				"id=" + id +
				", userId=" + userId +
				", reviewersList=" + reviewersList +
				", usersList=" + usersList +
				", authorsList=" + authorsList +
				", sentUserName='" + sentUserName + '\'' +
				", sentUserEmail='" + sentUserEmail + '\'' +
				", userRole=" + userRole +
				", journalId=" + journalId +
				", manuscriptId=" + manuscriptId +
				", commentaryId=" + commentaryId +
				", manuscriptCode='" + manuscriptCode + '\'' +
				", manuscriptTitle='" + manuscriptTitle + '\'' +
				", manuscriptType='" + manuscriptType + '\'' +
				", userName='" + userName + '\'' +
				", roleName='" + roleName + '\'' +
				", submittedDate=" + submittedDate +
				", statusCode='" + statusCode + '\'' +
				", editorId=" + editorId +
				", sentEditorName='" + sentEditorName + '\'' +
				", sentEditorEmail='" + sentEditorEmail + '\'' +
				", editorRole=" + editorRole +
				", templateId=" + templateId +
				", sendEmailUrl='" + sendEmailUrl + '\'' +
				", journalMailTemplatesBody='" + journalMailTemplatesBody + '\'' +
				", journalMailTemplatesSubject='" + journalMailTemplatesSubject + '\'' +
				", reviewerSuggestedVO=" + reviewerSuggestedVO +
				", manuscriptStatusId=" + manuscriptStatusId +
				", manuscriptVersion=" + manuscriptVersion +
				", createdOn=" + createdOn +
				", approvedOn=" + approvedOn +
				", rejectOn=" + rejectOn +
				", action='" + action + '\'' +
				", filePath='" + filePath + '\'' +
				", fileType='" + fileType + '\'' +
				", fileSize=" + fileSize +
				", fileName='" + fileName + '\'' +
				'}';
	}

	private Integer id;
	
	private Integer userId;

	private List<CommentaryUsersVO> reviewersList;

	private List<CommentaryUsersVO> usersList;

	private List<CommentaryUsersVO> authorsList;
	
	private String sentUserName;
	
	private String sentUserEmail;
	
	private Integer userRole;
	
	private Integer journalId;
	
	private Integer manuscriptId;

	private Integer commentaryId;
	
	private String manuscriptCode;
	
	private String manuscriptTitle;
	
	private String manuscriptType;
	
	private String userName;

	private String roleName;

	private Date submittedDate;
	
	private String statusCode;
	
	private Integer editorId;
	
	private String sentEditorName;
	
	private String sentEditorEmail;
	
	private Integer editorRole;
	
	private Integer templateId;
	
	private String sendEmailUrl;
	
	private String journalMailTemplatesBody;
	
	private String journalMailTemplatesSubject;
	
	private List<ReviewerSuggestedVO> reviewerSuggestedVO;
	
	private Integer manuscriptStatusId;

	private Integer manuscriptVersion;
	
	private Date createdOn;

	private Date approvedOn;

	private Date rejectOn;

	private String action;

	private String filePath;

	private String fileType;

	private Integer fileSize;

	private String fileName;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Integer getUserRole() {
		return userRole;
	}

	public List<CommentaryUsersVO> getReviewersList() {
		return reviewersList;
	}

	public void setReviewersList(List<CommentaryUsersVO> reviewersList) {
		this.reviewersList = reviewersList;
	}

	public List<CommentaryUsersVO> getAuthorsList() {
		return authorsList;
	}

	public void setAuthorsList(List<CommentaryUsersVO> authorsList) {
		this.authorsList = authorsList;
	}

	public List<CommentaryUsersVO> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<CommentaryUsersVO> usersList) {
		this.usersList = usersList;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getEditorId() {
		return editorId;
	}

	public void setEditorId(Integer editorId) {
		this.editorId = editorId;
	}

	public Integer getEditorRole() {
		return editorRole;
	}

	public void setEditorRole(Integer editorRole) {
		this.editorRole = editorRole;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getSendEmailUrl() {
		return sendEmailUrl;
	}

	public void setSendEmailUrl(String sendEmailUrl) {
		this.sendEmailUrl = sendEmailUrl;
	}

	public String getJournalMailTemplatesBody() {
		return journalMailTemplatesBody;
	}

	public void setJournalMailTemplatesBody(String journalMailTemplatesBody) {
		this.journalMailTemplatesBody = journalMailTemplatesBody;
	}

	public List<ReviewerSuggestedVO> getReviewerSuggestedVO() {
		return reviewerSuggestedVO;
	}

	public void setReviewerSuggestedVO(List<ReviewerSuggestedVO> reviewerSuggestedVO) {
		this.reviewerSuggestedVO = reviewerSuggestedVO;
	}

	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
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

	public String getManuscriptType() {
		return manuscriptType;
	}

	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getSentUserName() {
		return sentUserName;
	}

	public void setSentUserName(String sentUserName) {
		this.sentUserName = sentUserName;
	}

	public String getSentUserEmail() {
		return sentUserEmail;
	}

	public void setSentUserEmail(String sentUserEmail) {
		this.sentUserEmail = sentUserEmail;
	}

	public String getSentEditorName() {
		return sentEditorName;
	}

	public void setSentEditorName(String sentEditorName) {
		this.sentEditorName = sentEditorName;
	}

	public String getSentEditorEmail() {
		return sentEditorEmail;
	}

	public void setSentEditorEmail(String sentEditorEmail) {
		this.sentEditorEmail = sentEditorEmail;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getCommentaryId() {
		return commentaryId;
	}

	public void setCommentaryId(Integer commentaryId) {
		this.commentaryId = commentaryId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Date getRejectOn() {
		return rejectOn;
	}

	public void setRejectOn(Date rejectOn) {
		this.rejectOn = rejectOn;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getJournalMailTemplatesSubject() {
		return journalMailTemplatesSubject;
	}

	public void setJournalMailTemplatesSubject(String journalMailTemplatesSubject) {
		this.journalMailTemplatesSubject = journalMailTemplatesSubject;
	}

}
