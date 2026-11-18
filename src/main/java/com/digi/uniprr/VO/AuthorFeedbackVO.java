package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.digi.uniprr.enums.Salutation;

public class AuthorFeedbackVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer journalId;

	private Integer userId;

	private List<Integer> questionId;

	private List<String> answer;

	private String response ="";

	private Date createdDate;

	private Integer isActive;

	private Salutation salutation;

	private String firstName ="";

	private String middleName = "";

	private String lastName = "";

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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<Integer> getQuestionId() {
		return questionId;
	}

	public void setQuestionId(List<Integer> questionId) {
		this.questionId = questionId;
	}

	public List<String> getAnswer() {
		return answer;
	}

	public void setAnswer(List<String> answer) {
		this.answer = answer;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "AuthorFeedbackVO [id=" + id + ", journalId=" + journalId + ", userId=" + userId + ", questionId="
				+ questionId + ", answer=" + answer + ", response=" + response + ", createdDate=" + createdDate
				+ ", isActive=" + isActive + ", salutation=" + salutation + ", firstName=" + firstName + ", middleName="
				+ middleName + ", lastName=" + lastName + "]";
	}

}
