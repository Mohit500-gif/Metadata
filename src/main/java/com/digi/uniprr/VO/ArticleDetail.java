package com.digi.uniprr.VO;

import java.io.Serializable;

public class ArticleDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer article_id;
	private String article_title;
	private String article_doi;
	private String aid;
	private String article_type_cd;
	private Integer article_pages;
	private String accepted_date;
	private String article_comment;
	private String submissionDate;
	private String CommentoForProduction;
	private String keywords;
	private String review;
	public Integer getArticle_id() {
		return article_id;
	}
	public void setArticle_id(Integer article_id) {
		this.article_id = article_id;
	}
	public String getArticle_title() {
		return article_title;
	}
	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}
	public String getArticle_doi() {
		return article_doi;
	}
	public void setArticle_doi(String article_doi) {
		this.article_doi = article_doi;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getArticle_type_cd() {
		return article_type_cd;
	}
	public void setArticle_type_cd(String article_type_cd) {
		this.article_type_cd = article_type_cd;
	}
	public Integer getArticle_pages() {
		return article_pages;
	}
	public void setArticle_pages(Integer article_pages) {
		this.article_pages = article_pages;
	}
	public String getAccepted_date() {
		return accepted_date;
	}
	public void setAccepted_date(String accepted_date) {
		this.accepted_date = accepted_date;
	}
	public String getArticle_comment() {
		return article_comment;
	}
	public void setArticle_comment(String article_comment) {
		this.article_comment = article_comment;
	}
	public String getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}
	public String getCommentoForProduction() {
		return CommentoForProduction;
	}
	public void setCommentoForProduction(String commentoForProduction) {
		CommentoForProduction = commentoForProduction;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	@Override
	public String toString() {
		return "ArticleDetail [article_id=" + article_id + ", article_title=" + article_title + ", article_doi="
				+ article_doi + ", aid=" + aid + ", article_type_cd=" + article_type_cd + ", article_pages="
				+ article_pages + ", accepted_date=" + accepted_date + ", article_comment=" + article_comment
				+ ", submissionDate=" + submissionDate + ", CommentoForProduction=" + CommentoForProduction
				+ ", keywords=" + keywords + ", review=" + review + "]";
	}
	
	
}
