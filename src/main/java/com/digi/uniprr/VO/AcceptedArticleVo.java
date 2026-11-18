package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class AcceptedArticleVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer manuscriptId;
	private String manuscriptCode;
	private String articleTitle;
	private String articleType;
	private Date likelyDateOfPublishing;
	private Date acceptedDate;

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

	public Date getLikelyDateOfPublishing() {
		return likelyDateOfPublishing;
	}

	public void setLikelyDateOfPublishing(Date likelyDateOfPublishing) {
		this.likelyDateOfPublishing = likelyDateOfPublishing;
	}

	public Date getAcceptedDate() {
		return acceptedDate;
	}

	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	@Override
	public String toString() {
		return "AcceptedArticleVo [manuscriptId=" + manuscriptId + ", manuscriptCode=" + manuscriptCode
				+ ", articleTitle=" + articleTitle + ", articleType=" + articleType + ", likelyDateOfPublishing="
				+ likelyDateOfPublishing + ", acceptedDate=" + acceptedDate + "]";
	}

}
