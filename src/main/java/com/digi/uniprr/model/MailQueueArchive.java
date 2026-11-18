package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="mail_queue_archive")
public class MailQueueArchive {

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@ManyToOne
	@JoinColumn(name = "journal_id", updatable = false, insertable = false)
	private Journal journal;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name = "mail_to_role")
	private Integer mailToRole;
	
	@Column(name="task_id")
	private Integer taskId;
	
	@Column(name = "manuscript_status")
	private Integer manuscriptStatus;
	
	@Column(name = "mail_to_userid")
	private Integer mailTOUserId;
	
	@Column(name = "mail_to")
	private String mailTo;
	
	@Column(name = "mail_from")
	private String mailFrom;
	
	@Column(name = "mail_subject")
	private String mailSubject;
	
	@Column(name = "mail_body")
	private String mailBody;
	
	@Column(name = "mail_cc")
	private String mailCC;
	
	@Column(name = "mail_bcc")
	private String mailBCC;
	
	@Column(name = "is_seen")
	private Integer isSeen;
	
	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name = "module_name")
	private String moduleName;
	
	@Column(name = "mail_send_date")
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

	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
}
