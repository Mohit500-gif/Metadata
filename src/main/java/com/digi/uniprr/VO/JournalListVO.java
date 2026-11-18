package com.digi.uniprr.VO;

public class JournalListVO {
	
	private Integer journalId;
	
	private String journalTitle;

	@Override
	public String toString() {
		return "JournalListVO{" +
				"journalId=" + journalId +
				", journalTitle='" + journalTitle + '\'' +
				", journalCode='" + journalCode + '\'' +
				", journalShortTitle='" + journalShortTitle + '\'' +
				", publisherId=" + publisherId +
				", journalBlindType='" + journalBlindType + '\'' +
				", isChargingReq='" + isChargingReq + '\'' +
				", orcIdMandatory='" + orcIdMandatory + '\'' +
				", roleId=" + roleId +
				", isActive=" + isActive +
				", isSubmissionRequired='" + isSubmissionRequired + '\'' +
				", chargeOnSubmission='" + chargeOnSubmission + '\'' +
				", chargeOnPublication='" + chargeOnPublication + '\'' +
				", isChargeOnManuscriptType='" + isChargeOnManuscriptType + '\'' +
				", countryChargeOnPublication='" + countryChargeOnPublication + '\'' +
				", countryChargeOnSubmission='" + countryChargeOnSubmission + '\'' +
				", journalCountry='" + journalCountry + '\'' +
				'}';
	}

	private String journalCode;
	
	private String journalShortTitle;
	
	private Integer publisherId;
	
	private String journalBlindType;
	
	private String isChargingReq;
	
	private String orcIdMandatory;
	
	private Integer roleId;
	
	private Integer isActive;
	
	private String isSubmissionRequired;
	
	private String chargeOnSubmission;
	
	private String chargeOnPublication;
	
	private String isChargeOnManuscriptType;
	
	private String countryChargeOnPublication;
	
	private String countryChargeOnSubmission;
	
	private String journalCountry;
	
	public String getJournalCountry() {
		return journalCountry;
	}

	public void setJournalCountry(String journalCountry) {
		this.journalCountry = journalCountry;
	}

	public String getCountryChargeOnPublication() {
		return countryChargeOnPublication;
	}

	public void setCountryChargeOnPublication(String countryChargeOnPublication) {
		this.countryChargeOnPublication = countryChargeOnPublication;
	}

	public String getCountryChargeOnSubmission() {
		return countryChargeOnSubmission;
	}

	public void setCountryChargeOnSubmission(String countryChargeOnSubmission) {
		this.countryChargeOnSubmission = countryChargeOnSubmission;
	}

	public String getIsChargeOnManuscriptType() {
		return isChargeOnManuscriptType;
	}

	public void setIsChargeOnManuscriptType(String isChargeOnManuscriptType) {
		this.isChargeOnManuscriptType = isChargeOnManuscriptType;
	}

	public String getChargeOnSubmission() {
		return chargeOnSubmission;
	}

	public void setChargeOnSubmission(String chargeOnSubmission) {
		this.chargeOnSubmission = chargeOnSubmission;
	}

	public String getChargeOnPublication() {
		return chargeOnPublication;
	}

	public void setChargeOnPublication(String chargeOnPublication) {
		this.chargeOnPublication = chargeOnPublication;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getIsChargingReq() {
		return isChargingReq;
	}

	public void setIsChargingReq(String isChargingReq) {
		this.isChargingReq = isChargingReq;
	}

	public String getJournalBlindType() {
		return journalBlindType;
	}

	public void setJournalBlindType(String journalBlindType) {
		this.journalBlindType = journalBlindType;
	}

	public Integer getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Integer publisherId) {
		this.publisherId = publisherId;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public String getJournalShortTitle() {
		return journalShortTitle;
	}

	public void setJournalShortTitle(String journalShortTitle) {
		this.journalShortTitle = journalShortTitle;
	}

	public String getOrcIdMandatory() {
		return orcIdMandatory;
	}

	public void setOrcIdMandatory(String orcIdMandatory) {
		this.orcIdMandatory = orcIdMandatory;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getIsSubmissionRequired() {
		return isSubmissionRequired;
	}

	public void setIsSubmissionRequired(String isSubmissionRequired) {
		this.isSubmissionRequired = isSubmissionRequired;
	}
	
}
