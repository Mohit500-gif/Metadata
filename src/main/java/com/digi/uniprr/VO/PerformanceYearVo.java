package com.digi.uniprr.VO;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PerformanceYearVo {

	private String articleType;
	private Integer submitted;
	private Integer acceptedArticle;
	private Integer rejectedArticle;
	private Integer underReviewArticle;
	private String accepted;
	private String rejected;
	private String underReview;

	public PerformanceYearVo(String articleType, BigInteger submitted, BigDecimal acceptedArticle, BigDecimal rejectedArticle) {
		this.articleType = articleType;
		this.submitted = submitted.intValue();
		this.acceptedArticle = acceptedArticle.intValue();
		this.rejectedArticle = rejectedArticle.intValue();
		if(submitted.intValue()-(acceptedArticle.intValue()+rejectedArticle.intValue())>=0) {
			this.underReviewArticle = submitted.intValue()-(acceptedArticle.intValue()+rejectedArticle.intValue());
		}else {
			this.underReviewArticle = 0;
		}
		
	}

	public String getArticleType() {
		return articleType;
	}

	public void setArticleType(String articleType) {
		this.articleType = articleType;
	}

	public Integer getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Integer submitted) {
		this.submitted = submitted;
	}

	public Integer getAcceptedArticle() {
		return acceptedArticle;
	}

	public void setAcceptedArticle(Integer acceptedArticle) {
		this.acceptedArticle = acceptedArticle;
	}

	public Integer getRejectedArticle() {
		return rejectedArticle;
	}

	public void setRejectedArticle(Integer rejectedArticle) {
		this.rejectedArticle = rejectedArticle;
	}

	public Integer getUnderReviewArticle() {
		return underReviewArticle;
	}

	public void setUnderReviewArticle(Integer underReviewArticle) {
		this.underReviewArticle = underReviewArticle;
	}

	public String getAccepted() {
		return accepted;
	}

	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}

	public String getRejected() {
		return rejected;
	}

	public void setRejected(String rejected) {
		this.rejected = rejected;
	}

	public String getUnderReview() {
		return underReview;
	}

	public void setUnderReview(String underReview) {
		this.underReview = underReview;
	}

}
