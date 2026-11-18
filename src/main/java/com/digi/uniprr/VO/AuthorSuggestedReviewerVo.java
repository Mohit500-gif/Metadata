package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class AuthorSuggestedReviewerVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer reviewerSuggestedId;
	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private Integer journalId;
	private String reviewerName;
	private String salutation;
	private String firstName;
	private String lastName;
	private String email;
	private String specialization;
	private String subSpecialization;
	private Date suggestedDate;
	private String isSuggested;
	private String country;
	private Integer opposedReviewerId;
	private String comments;
	private  String institution;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getSubSpecialization() {
		return subSpecialization;
	}

	public void setSubSpecialization(String subSpecialization) {
		this.subSpecialization = subSpecialization;
	}

	public Integer getReviewerSuggestedId() {
		return reviewerSuggestedId;
	}

	public void setReviewerSuggestedId(Integer reviewerSuggestedId) {
		this.reviewerSuggestedId = reviewerSuggestedId;
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

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getSuggestedDate() {
		return suggestedDate;
	}

	public void setSuggestedDate(Date suggestedDate) {
		this.suggestedDate = suggestedDate;
	}

	public String getIsSuggested() {
		return isSuggested;
	}

	public void setIsSuggested(String isSuggested) {
		this.isSuggested = isSuggested;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
		

	/**
	 * @return the opposedReviewerId
	 */
	public Integer getOpposedReviewerId() {
		return opposedReviewerId;
	}

	/**
	 * @param opposedReviewerId the opposedReviewerId to set
	 */
	public void setOpposedReviewerId(Integer opposedReviewerId) {
		this.opposedReviewerId = opposedReviewerId;
	}

	@Override
	public String toString() {
		return "AuthorSuggestedReviewerVo [reviewerSuggestedId=" + reviewerSuggestedId + ", manuscriptId="
				+ manuscriptId + ", manuscriptVersion=" + manuscriptVersion + ", journalId=" + journalId
				+ ", reviewerName=" + reviewerName + ", salutation=" + salutation + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", specialization=" + specialization
				+ ", subSpecialization=" + subSpecialization + ", suggestedDate=" + suggestedDate + ", isSuggested="
				+ isSuggested + "]";
	}

}
