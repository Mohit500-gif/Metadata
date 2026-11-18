package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;

public class FeedbackQuestionMasterVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String question;
	
	private List<String> answer;
	
	private List<String> manuscriptType;
	
	private String questionType;
	
	private Integer userId;
	
	private String role;
	
	private Integer journalId;
	
	private String type;
	
	private String revisedManuscript;
	
	private String uploadForm;
	
	private String formIsMandatory;
	
	private String formName;
	
	private String comment;
	
	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getUploadForm() {
		return uploadForm;
	}

	public void setUploadForm(String uploadForm) {
		this.uploadForm = uploadForm;
	}

	public String getFormIsMandatory() {
		return formIsMandatory;
	}

	public void setFormIsMandatory(String formIsMandatory) {
		this.formIsMandatory = formIsMandatory;
	}

	public List<String> getManuscriptType() {
		return manuscriptType;
	}

	public void setManuscriptType(List<String> manuscriptType) {
		this.manuscriptType = manuscriptType;
	}

	public String getRevisedManuscript() {
		return revisedManuscript;
	}

	public void setRevisedManuscript(String revisedManuscript) {
		this.revisedManuscript = revisedManuscript;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getAnswer() {
		return answer;
	}

	public void setAnswer(List<String> answer) {
		this.answer = answer;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "FeedbackQuestionMasterVO [id=" + id + ", question=" + question + ", answer=" + answer
				+ ", manuscriptType=" + manuscriptType + ", questionType=" + questionType + ", userId=" + userId
				+ ", role=" + role + ", journalId=" + journalId + ", type=" + type + ", revisedManuscript="
				+ revisedManuscript + ", uploadForm=" + uploadForm + ", formIsMandatory=" + formIsMandatory
				+ ", formName=" + formName + ", comment=" + comment + "]";
	}
	
	
}

