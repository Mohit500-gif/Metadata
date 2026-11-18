package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class ArticleRatingVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String manuscriptCode;
	private Date acceptedDate;
	private String articleTitle;
	private String authorName;
	private Integer rating;
	private String editorName;

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public Date getAcceptedDate() {
		return acceptedDate;
	}

	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	@Override
	public String toString() {
		return "ArticleRatingVo [manuscriptCode=" + manuscriptCode + ", acceptedDate=" + acceptedDate
				+ ", articleTitle=" + articleTitle + ", authorName=" + authorName + ", rating=" + rating
				+ ", editorName=" + editorName + "]";
	}

}
