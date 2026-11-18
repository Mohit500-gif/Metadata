package com.digi.uniprr.VO;

import java.util.Date;
import java.util.List;

public class RejectedArticlesVo {

	private Integer manuscriptId;
	private String manuscriptCode;
	private String articleTitle;
	private String articleType;
	private String author;
	private Date submissionDate;
	private Date rejectionDate;
	private List<String> remarks;
	public Integer getManuscriptId() {
		return manuscriptId;
	}
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public String getArticleType() {
		return articleType;
	}
	public void setArticleType(String articleType) {
		this.articleType = articleType;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public Date getRejectionDate() {
		return rejectionDate;
	}
	public void setRejectionDate(Date rejectionDate) {
		this.rejectionDate = rejectionDate;
	}
	public List<String> getRemarks() {
		return remarks;
	}
	public void setRemarks(List<String> remarks) {
		this.remarks = remarks;
	}
	public String getManuscriptCode() {
		return manuscriptCode;
	}
	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}
	
}
