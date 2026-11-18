package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.digi.uniprr.enums.YNStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "journal")
public class Journal {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "journal_id")
	private Integer journalId;

	@OneToOne(mappedBy = "journal")
	private JournalChargeDetails journalChargeDetails;
	
	@ManyToOne
	@JoinColumn(name = "publisher_id", insertable = false, updatable = false)
	private Publishers publisher;
	
	@Column(name = "publisher_id")
	private Integer publisherId;
	
	@OneToOne(mappedBy = "journal", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private JournalMetadata journalMetadata;
	
	@OneToOne(mappedBy = "journal", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private JournalPolicies journalPolicies;
	
	@OneToOne(mappedBy = "journal")
	private JournalChargeDetails charge;
	
	@Column(name = "journal_code")
	private String journalCode;
	
	@Column(name="country_name")
	private String countryName;
	
	@Column(name = "journal_manuscript_count")
	private Integer journalManuscriptCount;
	
	@Column(name = "journal_manuscript_year")
	private Integer journalManuscriptYear;

	@Column(name = "journal_title")
	private String journalTitle;
	
	@Column(name="journal_shrt_title")
	private String journalShortTitle;

	@Column(name = "journal_me_group")
	private Integer journalMeGroup;

	@Column(name = "journal_editor_group")
	private Integer journalEditorGroup;

	@Column(name = "journal_workflow_id")
	private Integer journalWorkflowId;

	@Column(name = "journal_logo")
	private String journalLogo;

	@Column(name = "charge_flag")
	private String chargeFlag;

	@Column(name = "extend_due_date")
	private Integer extendDueDate;

	@Column(name = "journal_managing_editor")
	private Integer journalManagingEditor;

	@Column(name = "reviewer_selection")
	private YNStatus reviewerSelection;
	
	@Column(name="is_active")
	private Integer isActive;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "created_on")
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "created_by")
	private Integer createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name = "updated_by")
	private Integer updatedBy;
	
	@Column(name = "journal_pub_code")
	private String pubCode;

	@Column(name ="reviewer_suggestion_flag")
	private String reviewerSuggestion;

	@Column(name ="reviewer_count")
	private Integer reviewerCount;

	@Column(name = "corresponding_count")
	private Integer correspondingCount;

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Publishers getPublisher() {
		return publisher;
	}

	public void setPublisher(Publishers publisher) {
		this.publisher = publisher;
	}

	public Integer getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Integer publisherId) {
		this.publisherId = publisherId;
	}
	
	public JournalMetadata getJournalMetadata() {
		return journalMetadata;
	}

	public void setJournalMetadata(JournalMetadata journalMetadata) {
		this.journalMetadata = journalMetadata;
	}

	public String getJournalShortTitle() {
		return journalShortTitle;
	}

	public void setJournalShortTitle(String journalShortTitle) {
		this.journalShortTitle = journalShortTitle;
	}

	public JournalChargeDetails getCharge() {
		return charge;
	}

	public void setCharge(JournalChargeDetails charge) {
		this.charge = charge;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public Integer getJournalMeGroup() {
		return journalMeGroup;
	}

	public void setJournalMeGroup(Integer journalMeGroup) {
		this.journalMeGroup = journalMeGroup;
	}

	public Integer getJournalEditorGroup() {
		return journalEditorGroup;
	}

	public void setJournalEditorGroup(Integer journalEditorGroup) {
		this.journalEditorGroup = journalEditorGroup;
	}

	public Integer getJournalWorkflowId() {
		return journalWorkflowId;
	}

	public void setJournalWorkflowId(Integer journalWorkflowId) {
		this.journalWorkflowId = journalWorkflowId;
	}

	public String getJournalLogo() {
		return journalLogo;
	}

	public void setJournalLogo(String journalLogo) {
		this.journalLogo = journalLogo;
	}

	public String getChargeFlag() {
		return chargeFlag;
	}

	public void setChargeFlag(String chargeFlag) {
		this.chargeFlag = chargeFlag;
	}

	public Integer getExtendDueDate() {
		return extendDueDate;
	}

	public void setExtendDueDate(Integer extendDueDate) {
		this.extendDueDate = extendDueDate;
	}

	public Integer getJournalManagingEditor() {
		return journalManagingEditor;
	}

	public void setJournalManagingEditor(Integer journalManagingEditor) {
		this.journalManagingEditor = journalManagingEditor;
	}

	public YNStatus getReviewerSelection() {
		return reviewerSelection;
	}	

	public void setReviewerSelection(YNStatus reviewerSelection) {
		this.reviewerSelection = reviewerSelection;
	}
	
	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public JournalChargeDetails getJournalChargeDetails() {
		return journalChargeDetails;
	}

	public void setJournalChargeDetails(JournalChargeDetails journalChargeDetails) {
		this.journalChargeDetails = journalChargeDetails;
	}

	public String getPubCode() {
		return pubCode;
	}

	public void setPubCode(String pubCode) {
		this.pubCode = pubCode;
	}

	public JournalPolicies getJournalPolicies() {
		return journalPolicies;
	}

	public void setJournalPolicies(JournalPolicies journalPolicies) {
		this.journalPolicies = journalPolicies;
	}

	public Integer getJournalManuscriptCount() {
		return journalManuscriptCount;
	}

	public void setJournalManuscriptCount(Integer journalManuscriptCount) {
		this.journalManuscriptCount = journalManuscriptCount;
	}

	public Integer getJournalManuscriptYear() {
		return journalManuscriptYear;
	}

	public void setJournalManuscriptYear(Integer journalManuscriptYear) {
		this.journalManuscriptYear = journalManuscriptYear;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getReviewerSuggestion() {
		return reviewerSuggestion;
	}

	public void setReviewerSuggestion(String reviewerSuggestion) {
		this.reviewerSuggestion = reviewerSuggestion;
	}

	public Integer getReviewerCount() {
		return reviewerCount;
	}

	public void setReviewerCount(Integer reviewerCount) {
		this.reviewerCount = reviewerCount;
	}

	public Integer getCorrespondingCount() {
		return correspondingCount;
	}

	public void setCorrespondingCount(Integer correspondingCount) {
		this.correspondingCount = correspondingCount;
	}	
}