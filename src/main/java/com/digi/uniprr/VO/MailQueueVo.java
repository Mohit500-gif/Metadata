package com.digi.uniprr.VO;

import java.util.Date;

import com.digi.uniprr.model.Journal;

public class MailQueueVo {

	private Integer id;

	private Integer journalId;

	private Journal journal;

	private Integer manuscriptId;

	private Integer manuscriptVersion;

	private Integer mailToRole;

	private Integer manuscriptStatus;

	private Integer mailTOUserId;

	private String mailTo;

	private String mailFrom;

	private String mailSubject;

	private String mailBody;

	private String mailCC;

	private String mailBCC;

	private Integer isSeen;

	private Integer isActive;

	private Date mail_send_date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

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

	public Integer getMailToRole() {
		return mailToRole;
	}

	public void setMailToRole(Integer mailToRole) {
		this.mailToRole = mailToRole;
	}

	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public Integer getMailTOUserId() {
		return mailTOUserId;
	}

	public void setMailTOUserId(Integer mailTOUserId) {
		this.mailTOUserId = mailTOUserId;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public String getMailCC() {
		return mailCC;
	}

	public void setMailCC(String mailCC) {
		this.mailCC = mailCC;
	}

	public String getMailBCC() {
		return mailBCC;
	}

	public void setMailBCC(String mailBCC) {
		this.mailBCC = mailBCC;
	}

	public Integer getIsSeen() {
		return isSeen;
	}

	public void setIsSeen(Integer isSeen) {
		this.isSeen = isSeen;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Date getMail_send_date() {
		return mail_send_date;
	}

	public void setMail_send_date(Date mail_send_date) {
		this.mail_send_date = mail_send_date;
	}

}
