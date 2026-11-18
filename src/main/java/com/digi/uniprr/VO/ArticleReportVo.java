package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ArticleReportVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date from;
	private Date to;
	private List<JournalListVO> journal;
	private String journalTitle;
	private String journalCode;
	private Integer  totalSubmissions;
	private Integer totalRejections;
	private Integer totalAcceptance;
	private Integer totalArticleInPeerReview;
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public String getJournalTitle() {
		return journalTitle;
	}
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}
	public String getJournalCode() {
		return journalCode;
	}
	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}
	public Integer getTotalSubmissions() {
		return totalSubmissions;
	}
	public void setTotalSubmissions(Integer totalSubmissions) {
		this.totalSubmissions = totalSubmissions;
	}
	public Integer getTotalRejections() {
		return totalRejections;
	}
	public void setTotalRejections(Integer totalRejections) {
		this.totalRejections = totalRejections;
	}
	public Integer getTotalAcceptance() {
		return totalAcceptance;
	}
	public void setTotalAcceptance(Integer totalAcceptance) {
		this.totalAcceptance = totalAcceptance;
	}
	public Integer getTotalArticleInPeerReview() {
		return totalArticleInPeerReview;
	}
	public void setTotalArticleInPeerReview(Integer totalArticleInPeerReview) {
		this.totalArticleInPeerReview = totalArticleInPeerReview;
	}
	public List<JournalListVO> getJournal() {
		return journal;
	}
	public void setJournal(List<JournalListVO> journal) {
		this.journal = journal;
	}
	@Override
	public String toString() {
		return "ArticleReportVo [from=" + from + ", to=" + to + ", journal=" + journal + ", journalTitle="
				+ journalTitle + ", journalCode=" + journalCode + ", totalSubmissions=" + totalSubmissions
				+ ", totalRejections=" + totalRejections + ", totalAcceptance=" + totalAcceptance
				+ ", totalArticleInPeerReview=" + totalArticleInPeerReview + "]";
	}
	
	
	
}
