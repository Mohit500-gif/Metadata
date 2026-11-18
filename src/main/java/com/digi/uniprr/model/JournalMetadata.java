package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "journal_metadata")
@Getter
@Setter
public class JournalMetadata {

	@Id
	@Column(name = "metadata_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer metadataId;

	@OneToOne
	@JoinColumn(name = "journal_id", insertable = false, updatable = false)
	private Journal journal;

	@Column(name = "journal_id")
	private Integer journalId;

	@Column(name = "journal_email")
	private String journalEmail;

	@Column(name = "journal_email_password")
	private String journalEmailPassword;

	@Column(name = "journal_language")
	private String journalLanguage;
	
	@Column(name = "journal_type")
	private String journalType;
	
	@Column(name ="charge_on_country")
	private String chargeOnCountry;

	@Column(name = "journal_alias")
	private String journalAlias;
	
	@Column(name = "is_submission_required")
	private String isSubmissionRequired;

	@Column(name = "workflow_id")
	private Integer workflowId;

	@Column(name = "affiliated_to")
	private String affilitaedTo;

	@Column(name = "bibliography")
	private String bibliography;

	@Column(name = "code")
	private String code;

	@Column(name = "publication_type")
	private String publicationType;

	@Column(name = "colour_figures")
	private Double colourFigures;

	@Column(name = "cite_score")
	private Float citeScore;

	@Column(name = "impact_factor")
	private Float impactFactor;

	@Column(name = "acceptance_rate")
	private Double acceptanceRate;

	@Column(name = "journal_doi")
	private String journalDOI;

	@Column(name = "journal_eissn")
	private String journalEissn;

	@Column(name = "journal_issn")
	private String journalIssn;

	@Column(name = "journal_frequency")
	private String journalFrequency;

	@Column(name = "copyright_content")
	private String copyrightContent;

	@Column(name = "journal_index_copernicus")
	private String journalIndexCopernicus;

	@Column(name = "open_access")
	private String openAccess;

	@Column(name = "is_chargingreq")
	private String isChargingRequired;

	@Column(name = "charge_on")
	private String chargeOn;

	@Column(name = "journal_readership")
	private String journalReadership;

	@Column(name = "journal_blind_type")
	private String journalBlindType;

	@Column(name = "journal_speciality")
	private String journalSpeciality;

	@Column(name = "journal_sub_category")
	private String journalSubCategory;

	@Column(name = "journal_subject")
	private String journalSubject;

	@Column(name = "url")
	private String url;
	
	@Column(name ="website_url")
	private String websiteUrl;

	@Column(name = "tblcost")
	private Double tblcost;

	@Column(name = "extend_due_date")
	private Integer extendDueDate;

	@Column(name = "typesetting")
	private Double typseStting;

	@Column(name = "suggest_reviewer")
	private String suggestReviewer;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name = "journal_mode")
	private String journalMode;

	@Column(name = "submission_message")
	private String submissionMessage;
	@Column(name = "current_issue_link")
	private String currentIssueLink;

	@Column(name = "author_instructions_link")
	private String authorInstructionsLink;
	
	@Column(name = "about_link")
	private String aboutLink;
	
	@Column(name = "thumbnail_path")
	private String thumbnailPath;
	
	@Column(name = "publons_code")
	private String publonsCode;

	@Column(name = "eic_name_signature")
	private String eicNameSignature;
	
	@Column(name = "eic_signature")
	private String eicSignature;
	
	@Column(name= "has_signature")
	private boolean hasSignature;
	
	@Column(name = "new_reviewer_certificate_enabled", nullable = false)
	private boolean newReviewerCertificateEnabled = false;

	
}
