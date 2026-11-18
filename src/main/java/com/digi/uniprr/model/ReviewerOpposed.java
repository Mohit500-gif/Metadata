package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "reviewer_opposed")
public class ReviewerOpposed {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private Integer id;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name = "journal_id")
	private Integer journalId;
	
	@Column(name = "manuscript_status")
	private Integer manuscriptStatus;
	
	@Column(name = "salutation")
	private String salutation;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "subject")
	private String subject;
		
	@Column(name = "country")
	private String country;
	
	@Column(name = "specialization")
	private String specialization;
	
	@Column(name = "institution")
	private String institution;
	
	@Column(name = "opposed_by")
	private Integer opposedBy;
	
	@Column(name = "opposed_reviewer_id")
	private Integer opposedReviewerId;	
	
	@Column(name = "is_opposed")
	private String isOpposed;
	
	@CreationTimestamp
	@Column(name = "opposed_date")
	private Date opposedDate;
	
	@Column(name = "comments")
	private String comments;	

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the manuscriptId
	 */
	public Integer getManuscriptId() {
		return manuscriptId;
	}

	/**
	 * @param manuscriptId the manuscriptId to set
	 */
	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	/**
	 * @return the manuscriptVersion
	 */
	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	/**
	 * @param manuscriptVersion the manuscriptVersion to set
	 */
	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	/**
	 * @return the journalId
	 */
	public Integer getJournalId() {
		return journalId;
	}

	/**
	 * @param journalId the journalId to set
	 */
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	/**
	 * @return the manuscriptStatus
	 */
	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}

	/**
	 * @param manuscriptStatus the manuscriptStatus to set
	 */
	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	/**
	 * @return the salutation
	 */
	public String getSalutation() {
		return salutation;
	}

	/**
	 * @param salutation the salutation to set
	 */
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
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
	 * @return the specialization
	 */
	public String getSpecialization() {
		return specialization;
	}

	/**
	 * @param specialization the specialization to set
	 */
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	/**
	 * @return the opposedBy
	 */
	public Integer getOpposedBy() {
		return opposedBy;
	}

	/**
	 * @param opposedBy the opposedBy to set
	 */
	public void setOpposedBy(Integer opposedBy) {
		this.opposedBy = opposedBy;
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

	
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the isOpposed
	 */
	public String getIsOpposed() {
		return isOpposed;
	}

	/**
	 * @param isOpposed the isOpposed to set
	 */
	public void setIsOpposed(String isOpposed) {
		this.isOpposed = isOpposed;
	}

	
	/**
	 * @return the opposedDate
	 */
	public Date getOpposedDate() {
		return opposedDate;
	}

	/**
	 * @param opposedDate the opposedDate to set
	 */
	public void setOpposedDate(Date opposedDate) {
		this.opposedDate = opposedDate;
	}

	/**
	 * @return the institution
	 */
	public String getInstitution() {
		return institution;
	}

	/**
	 * @param institution the institution to set
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}		
	
}
