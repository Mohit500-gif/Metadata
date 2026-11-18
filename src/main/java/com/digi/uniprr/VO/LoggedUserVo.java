package com.digi.uniprr.VO;

import java.util.Date;

public class LoggedUserVo {

	private Integer id;
	private Integer userId;
	private String userRole;
	private String userEmail;
	private String userName;
	private Integer journalId;
	private String loginStatus;
	private Integer status;
	private Date loggedInDate;
	private String ipAddress;
	private String currently;
	
	
	private String articlePhase;
	private Integer guestCommentory;
	private Integer vitalStatistics;
	private Integer metaAnalysis;
	private Integer bookFromMySelf;
	private Integer cents;
	private Integer others;
	private Integer totals;
	
	private String currentlyWith;
	private Integer more_14_days_overDue;
	private Integer days_14_overDue;
	private Integer days_7_overDue;
	private Integer less_14_days_overDue;
	private Integer due_today;
	private Integer not_yet_due;
	
	private Integer newArticleSubmitted;
	private Integer commentsSubmittedReviewer;
	private Integer revisedArticleSubmitted;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getLoggedInDate() {
		return loggedInDate;
	}

	public void setLoggedInDate(Date loggedInDate) {
		this.loggedInDate = loggedInDate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCurrently() {
		return currently;
	}

	public void setCurrently(String currently) {
		this.currently = currently;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getArticlePhase() {
		return articlePhase;
	}

	public void setArticlePhase(String articlePhase) {
		this.articlePhase = articlePhase;
	}

	public Integer getGuestCommentory() {
		return guestCommentory;
	}

	public void setGuestCommentory(Integer guestCommentory) {
		this.guestCommentory = guestCommentory;
	}

	public Integer getVitalStatistics() {
		return vitalStatistics;
	}

	public void setVitalStatistics(Integer vitalStatistics) {
		this.vitalStatistics = vitalStatistics;
	}

	public Integer getMetaAnalysis() {
		return metaAnalysis;
	}

	public void setMetaAnalysis(Integer metaAnalysis) {
		this.metaAnalysis = metaAnalysis;
	}

	public Integer getBookFromMySelf() {
		return bookFromMySelf;
	}

	public void setBookFromMySelf(Integer bookFromMySelf) {
		this.bookFromMySelf = bookFromMySelf;
	}

	public Integer getCents() {
		return cents;
	}

	public void setCents(Integer cents) {
		this.cents = cents;
	}

	public Integer getOthers() {
		return others;
	}

	public void setOthers(Integer others) {
		this.others = others;
	}

	public Integer getTotals() {
		return totals;
	}

	public void setTotals(Integer totals) {
		this.totals = totals;
	}

	public String getCurrentlyWith() {
		return currentlyWith;
	}

	public void setCurrentlyWith(String currentlyWith) {
		this.currentlyWith = currentlyWith;
	}

	public Integer getMore_14_days_overDue() {
		return more_14_days_overDue;
	}

	public void setMore_14_days_overDue(Integer more_14_days_overDue) {
		this.more_14_days_overDue = more_14_days_overDue;
	}

	public Integer getDays_14_overDue() {
		return days_14_overDue;
	}

	public void setDays_14_overDue(Integer days_14_overDue) {
		this.days_14_overDue = days_14_overDue;
	}

	public Integer getDays_7_overDue() {
		return days_7_overDue;
	}

	public void setDays_7_overDue(Integer days_7_overDue) {
		this.days_7_overDue = days_7_overDue;
	}

	public Integer getLess_14_days_overDue() {
		return less_14_days_overDue;
	}

	public void setLess_14_days_overDue(Integer less_14_days_overDue) {
		this.less_14_days_overDue = less_14_days_overDue;
	}

	public Integer getDue_today() {
		return due_today;
	}

	public void setDue_today(Integer due_today) {
		this.due_today = due_today;
	}

	public Integer getNot_yet_due() {
		return not_yet_due;
	}

	public void setNot_yet_due(Integer not_yet_due) {
		this.not_yet_due = not_yet_due;
	}

	public Integer getNewArticleSubmitted() {
		return newArticleSubmitted;
	}

	public void setNewArticleSubmitted(Integer newArticleSubmitted) {
		this.newArticleSubmitted = newArticleSubmitted;
	}

	public Integer getCommentsSubmittedReviewer() {
		return commentsSubmittedReviewer;
	}

	public void setCommentsSubmittedReviewer(Integer commentsSubmittedReviewer) {
		this.commentsSubmittedReviewer = commentsSubmittedReviewer;
	}

	public Integer getRevisedArticleSubmitted() {
		return revisedArticleSubmitted;
	}

	public void setRevisedArticleSubmitted(Integer revisedArticleSubmitted) {
		this.revisedArticleSubmitted = revisedArticleSubmitted;
	}

}
