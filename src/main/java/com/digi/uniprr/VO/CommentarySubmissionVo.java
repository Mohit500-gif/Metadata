package com.digi.uniprr.VO;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class CommentarySubmissionVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer journalId;
	private Integer manuscriptVersion;
	private Integer manuscriptStatusId;
	private Integer commentaryUserId;
	private String fileStatus;
	private String submissionStatus;
	private String  manuscriptTtile;
	private String manuscriptAbstract;
	private String referneceMansucriptCode;
	private String manuscriptType;
	private MultipartFile firstPage;
	private MultipartFile manuscriptFile;
	private String manuscriptChecklistResponse;
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public String getManuscriptTtile() {
		return manuscriptTtile;
	}
	public void setManuscriptTtile(String manuscriptTtile) {
		this.manuscriptTtile = manuscriptTtile;
	}
	public String getManuscriptAbstract() {
		return manuscriptAbstract;
	}
	public void setManuscriptAbstract(String manuscriptAbstract) {
		this.manuscriptAbstract = manuscriptAbstract;
	}
	public String getReferneceMansucriptCode() {
		return referneceMansucriptCode;
	}
	public void setReferneceMansucriptCode(String referneceMansucriptCode) {
		this.referneceMansucriptCode = referneceMansucriptCode;
	}
	public String getManuscriptType() {
		return manuscriptType;
	}
	public void setManuscriptType(String manuscriptType) {
		this.manuscriptType = manuscriptType;
	}
	public MultipartFile getFirstPage() {
		return firstPage;
	}
	public void setFirstPage(MultipartFile firstPage) {
		this.firstPage = firstPage;
	}
	public MultipartFile getManuscriptFile() {
		return manuscriptFile;
	}
	public void setManuscriptFile(MultipartFile manuscriptFile) {
		this.manuscriptFile = manuscriptFile;
	}
	public Integer getCommentaryUserId() {
		return commentaryUserId;
	}
	public void setCommentaryUserId(Integer commentaryUserId) {
		this.commentaryUserId = commentaryUserId;
	}
	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}
	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}
	public Integer getManuscriptStatusId() {
		return manuscriptStatusId;
	}
	public void setManuscriptStatusId(Integer manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}
	public String getSubmissionStatus() {
		return submissionStatus;
	}
	public void setSubmissionStatus(String submissionStatus) {
		this.submissionStatus = submissionStatus;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public String getManuscriptChecklistResponse() {
		return manuscriptChecklistResponse;
	}
	public void setManuscriptChecklistResponse(String manuscriptChecklistResponse) {
		this.manuscriptChecklistResponse = manuscriptChecklistResponse;
	}
	@Override
	public String toString() {
		return "CommentarySubmissionVo [journalId=" + journalId + ", manuscriptVersion=" + manuscriptVersion
				+ ", manuscriptStatusId=" + manuscriptStatusId + ", commentaryUserId=" + commentaryUserId
				+ ", fileStatus=" + fileStatus + ", submissionStatus=" + submissionStatus + ", manuscriptTtile="
				+ manuscriptTtile + ", manuscriptAbstract=" + manuscriptAbstract + ", referneceMansucriptCode="
				+ referneceMansucriptCode + ", manuscriptType=" + manuscriptType + ", firstPage=" + firstPage
				+ ", manuscriptFile=" + manuscriptFile + ", manuscriptChecklistResponse=" + manuscriptChecklistResponse
				+ "]";
	}
	
	
}
