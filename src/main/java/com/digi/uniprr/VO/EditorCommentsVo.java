package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.digi.uniprr.enums.Salutation;

public class EditorCommentsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private Integer editorUserId;
	private String editorName;
	private Integer editorRoleId;
	private Integer statusId;
	private String comment;
	private Date commentDate;
	private String fileType;
	private String fileName;
	private String fileDisplayName;
	private String filePath;
	private Integer fileSize;
	private List<FileVo> reviewerFile;
	private Salutation salutation;
	private String firstName;
	private String middleName;
	private String lastName;

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

	public Integer getEditorUserId() {
		return editorUserId;
	}

	public void setEditorUserId(Integer editorUserId) {
		this.editorUserId = editorUserId;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public Integer getEditorRoleId() {
		return editorRoleId;
	}

	public void setEditorRoleId(Integer editorRoleId) {
		this.editorRoleId = editorRoleId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public List<FileVo> getReviewerFile() {
		return reviewerFile;
	}

	public void setReviewerFile(List<FileVo> reviewerFile) {
		this.reviewerFile = reviewerFile;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	
	public String getFileDisplayName() {
		return fileDisplayName;
	}

	public void setFileDisplayName(String fileDisplayName) {
		this.fileDisplayName = fileDisplayName;
	}

	@Override
	public String toString() {
		return "EditorCommentsVo [manuscriptId=" + manuscriptId + ", manuscriptVersion=" + manuscriptVersion
				+ ", editorUserId=" + editorUserId + ", editorName=" + editorName + ", editorRoleId=" + editorRoleId
				+ ", statusId=" + statusId + ", comment=" + comment + ", commentDate=" + commentDate + ", fileType="
				+ fileType + ", fileName=" + fileName + ", filePath=" + filePath + ", fileSize=" + fileSize
				+ ", reviewerFile=" + reviewerFile + ", salutation=" + salutation + ", firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName + "]";
	}
	
}
