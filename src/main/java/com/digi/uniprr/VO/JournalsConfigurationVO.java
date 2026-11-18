package com.digi.uniprr.VO;

import java.util.Date;
import java.util.List;

import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.Specialization;
import com.digi.uniprr.model.Subject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JournalsConfigurationVO {

	private Boolean isNew;

	private Integer journalId;

	private Integer workflowId;

	private List<ManuscriptTypeJournalVO> manuscriptTypeJournals;

	private Integer publisherId;

	private String journalCode;

	private String journalDoi;

	private String journalIssn;
	
	private String journalLanguage;

	private String publicationType;
	
	private String countryName;
	
	private String journalType;
	
	private String isSubmissionRequired;

	private Float citeScore;

	private String currency;

	private Float impactFactor;

	private Double acceptanceRate;

	private String journalEissn;

	private String journalEmail;

	private String journalEmailPassword;
	
	private String countryChargeOnSubmission;
	
	private String countryChargeOnPublication;
	
	private String journalAlias;

	private String journalAffilation;

	private String journalSubCategory;

	private String journalSubject;

	private String journalFrequency;

	private String copyrightContent;

	private String submissionMessage;

	private String journalSpeciality;

	private String journalReadership;

	private String journalBibliography;

	private String journalOpenAccess;

	private String journalCopernicusIndex;

	private String journalChargeFlag;

	private String figures;

	private String tables;

	private Integer journalMetadataId;

	private Double colourFigure;

	private String typeSetting;

	private String journalTitle;

	private String journalShortTitle;

	private String journalMeGroup;

	private Integer journalEditorGroup;

	private Integer journalWorkflowId;

	private String journalLogo;

	private String journalSubmission;

	private String chargeFlag;

	private String chargeAmount;

	private String fixedCharges;

	private Float colorCharges;

	private Float blackWhiteCharges;

	private Float tableCharges;

	private Float typeSettingCharges;

	private Float additionalCharges;

	private Float productionCharges;

	private Float fixedChargeAmount;

	private String chargeOnPublication;

	private String chargeOnSubmission;

	private Float publicationCharge;

	private Float submissionCharge;

	private String currencyOnSubmission;
	
	private String chargeOnCountry;

	private String currencyOnPublication;
	
	private String isChargesRequired;

	private String createdBy;

	private Date createOn;

	private String updateBy;

	private Date updatedOn;

	private String chargeOn;

	private List<Specialization> speciality;

	private List<Subject> subject;

	private List<JournalManuscriptTypeCharge> manusriptTypeCharge;

	private List<JournalManuscriptTypeChargeVo> manusriptTypeCharges;

	private String url;
	
	private String websiteUrl;

	private String pubCode;

	private String journalMode;
	
	private String currentIssueLink;
	
	private String authorInstructionsLink;
	
	private String aboutLink;
	
	private String thumbnailFilePath;

	private String publonsCode;

	private Integer journalManuscriptCount;

	private String reviewerSuggestion;

	private Integer reviewerCount;

	private Integer coAuthorCount;

	private List<String> trialArray;

	private String eicNameSignature;
	
	private String eicSignature;
	
	private String abstractingAndIndexing;

	@Override
	public String toString() {
		return "JournalsConfigurationVO [isNew=" + isNew + ", journalId=" + journalId + ", workflowId=" + workflowId
				+ ", manuscriptTypeJournals=" + manuscriptTypeJournals + ", publisherId=" + publisherId
				+ ", journalCode=" + journalCode + ", journalDoi=" + journalDoi + ", journalIssn=" + journalIssn
				+ ", journalLanguage=" + journalLanguage + ", publicationType=" + publicationType + ", countryName="
				+ countryName + ", journalType=" + journalType + ", isSubmissionRequired=" + isSubmissionRequired
				+ ", citeScore=" + citeScore + ", currency=" + currency + ", impactFactor=" + impactFactor
				+ ", acceptanceRate=" + acceptanceRate + ", journalEissn=" + journalEissn + ", journalEmail="
				+ journalEmail + ", journalEmailPassword=" + journalEmailPassword + ", countryChargeOnSubmission="
				+ countryChargeOnSubmission + ", countryChargeOnPublication=" + countryChargeOnPublication
				+ ", journalAlias=" + journalAlias + ", journalAffilation=" + journalAffilation
				+ ", journalSubCategory=" + journalSubCategory + ", journalSubject=" + journalSubject
				+ ", journalFrequency=" + journalFrequency + ", copyrightContent=" + copyrightContent
				+ ", submissionMessage=" + submissionMessage + ", journalSpeciality=" + journalSpeciality
				+ ", journalReadership=" + journalReadership + ", journalBibliography=" + journalBibliography
				+ ", journalOpenAccess=" + journalOpenAccess + ", journalCopernicusIndex=" + journalCopernicusIndex
				+ ", journalChargeFlag=" + journalChargeFlag + ", figures=" + figures + ", tables=" + tables
				+ ", journalMetadataId=" + journalMetadataId + ", colourFigure=" + colourFigure + ", typeSetting="
				+ typeSetting + ", journalTitle=" + journalTitle + ", journalShortTitle=" + journalShortTitle
				+ ", journalMeGroup=" + journalMeGroup + ", journalEditorGroup=" + journalEditorGroup
				+ ", journalWorkflowId=" + journalWorkflowId + ", journalLogo=" + journalLogo + ", journalSubmission="
				+ journalSubmission + ", chargeFlag=" + chargeFlag + ", chargeAmount=" + chargeAmount
				+ ", fixedCharges=" + fixedCharges + ", colorCharges=" + colorCharges + ", blackWhiteCharges="
				+ blackWhiteCharges + ", tableCharges=" + tableCharges + ", typeSettingCharges=" + typeSettingCharges
				+ ", additionalCharges=" + additionalCharges + ", productionCharges=" + productionCharges
				+ ", fixedChargeAmount=" + fixedChargeAmount + ", chargeOnPublication=" + chargeOnPublication
				+ ", chargeOnSubmission=" + chargeOnSubmission + ", publicationCharge=" + publicationCharge
				+ ", submissionCharge=" + submissionCharge + ", currencyOnSubmission=" + currencyOnSubmission
				+ ", chargeOnCountry=" + chargeOnCountry + ", currencyOnPublication=" + currencyOnPublication
				+ ", isChargesRequired=" + isChargesRequired + ", createdBy=" + createdBy + ", createOn=" + createOn
				+ ", updateBy=" + updateBy + ", updatedOn=" + updatedOn + ", chargeOn=" + chargeOn + ", speciality="
				+ speciality + ", subject=" + subject + ", manusriptTypeCharge=" + manusriptTypeCharge
				+ ", manusriptTypeCharges=" + manusriptTypeCharges + ", url=" + url + ", websiteUrl=" + websiteUrl
				+ ", pubCode=" + pubCode + ", journalMode=" + journalMode + ", journalManuscriptCount="
				+ journalManuscriptCount + "]";
	}
	
}
