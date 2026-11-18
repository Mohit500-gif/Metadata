package com.digi.uniprr.VO;

import java.io.Serializable;

import javax.persistence.Column;

public class JournalManuscriptTypeChargeVo implements Serializable{

	private Integer id;

	private Integer journalId;

	private Integer manuscriptTypeId;

	private String manuscriptTypeName;

	private String fixedCharges;

	private Float fixedChargeAmount;



	private Float tablesCharges;

	private Float figureCharges;

	private Float colorFigureCharges;

	private Float typeSettingCharges;

	private Float additionalCharges;

	private String currency;

	private Float chargeAmount;

	private String chargeOnPublication;

	private String chargeOnSubmission;

	private String countryChargeOnSubmission;

	private String countryChargeOnPublication;

	private Float publicationCharge;

	private Float submissionCharge;

	private String currencyOnSubmission;

	private String currencyOnPublication;

	private String isChargesRequired;

	private Integer isActive;

	public String getPaymentCountriesOnSubmission() {
		return paymentCountriesOnSubmission;
	}

	public void setPaymentCountriesOnSubmission(String paymentCountriesOnSubmission) {
		this.paymentCountriesOnSubmission = paymentCountriesOnSubmission;
	}

	private String paymentCountriesOnSubmission;

	public String getPaymentCountriesOnPublishing() {
		return paymentCountriesOnPublishing;
	}

	public void setPaymentCountriesOnPublishing(String paymentCountriesOnPublishing) {
		this.paymentCountriesOnPublishing = paymentCountriesOnPublishing;
	}

	private String paymentCountriesOnPublishing;



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

	public Integer getManuscriptTypeId() {
		return manuscriptTypeId;
	}

	public void setManuscriptTypeId(Integer manuscriptTypeId) {
		this.manuscriptTypeId = manuscriptTypeId;
	}

	public String getManuscriptTypeName() {
		return manuscriptTypeName;
	}

	public void setManuscriptTypeName(String manuscriptTypeName) {
		this.manuscriptTypeName = manuscriptTypeName;
	}

	public String getFixedCharges() {
		return fixedCharges;
	}

	public void setFixedCharges(String fixedCharges) {
		this.fixedCharges = fixedCharges;
	}

	public Float getFixedChargeAmount() {
		return fixedChargeAmount;
	}

	public void setFixedChargeAmount(Float fixedChargeAmount) {
		this.fixedChargeAmount = fixedChargeAmount;
	}

	public Float getTablesCharges() {
		return tablesCharges;
	}

	public void setTablesCharges(Float tablesCharges) {
		this.tablesCharges = tablesCharges;
	}

	public Float getFigureCharges() {
		return figureCharges;
	}

	public void setFigureCharges(Float figureCharges) {
		this.figureCharges = figureCharges;
	}

	public Float getColorFigureCharges() {
		return colorFigureCharges;
	}

	public void setColorFigureCharges(Float colorFigureCharges) {
		this.colorFigureCharges = colorFigureCharges;
	}

	public Float getTypeSettingCharges() {
		return typeSettingCharges;
	}

	public void setTypeSettingCharges(Float typeSettingCharges) {
		this.typeSettingCharges = typeSettingCharges;
	}

	public Float getAdditionalCharges() {
		return additionalCharges;
	}

	public void setAdditionalCharges(Float additionalCharges) {
		this.additionalCharges = additionalCharges;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Float getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(Float chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getChargeOnPublication() {
		return chargeOnPublication;
	}

	public void setChargeOnPublication(String chargeOnPublication) {
		this.chargeOnPublication = chargeOnPublication;
	}

	public String getChargeOnSubmission() {
		return chargeOnSubmission;
	}

	public void setChargeOnSubmission(String chargeOnSubmission) {
		this.chargeOnSubmission = chargeOnSubmission;
	}

	public Float getPublicationCharge() {
		return publicationCharge;
	}

	public void setPublicationCharge(Float publicationCharge) {
		this.publicationCharge = publicationCharge;
	}

	public Float getSubmissionCharge() {
		return submissionCharge;
	}

	public void setSubmissionCharge(Float submissionCharge) {
		this.submissionCharge = submissionCharge;
	}

	public String getCurrencyOnSubmission() {
		return currencyOnSubmission;
	}

	public void setCurrencyOnSubmission(String currencyOnSubmission) {
		this.currencyOnSubmission = currencyOnSubmission;
	}

	public String getCurrencyOnPublication() {
		return currencyOnPublication;
	}

	public void setCurrencyOnPublication(String currencyOnPublication) {
		this.currencyOnPublication = currencyOnPublication;
	}

	public String getIsChargesRequired() {
		return isChargesRequired;
	}

	public void setIsChargesRequired(String isChargesRequired) {
		this.isChargesRequired = isChargesRequired;
	}

	public String getCountryChargeOnSubmission() {
		return countryChargeOnSubmission;
	}

	public void setCountryChargeOnSubmission(String countryChargeOnSubmission) {
		this.countryChargeOnSubmission = countryChargeOnSubmission;
	}

	public String getCountryChargeOnPublication() {
		return countryChargeOnPublication;
	}

	public void setCountryChargeOnPublication(String countryChargeOnPublication) {
		this.countryChargeOnPublication = countryChargeOnPublication;
	}

	@Override
	public String toString() {
		return "JournalManuscriptTypeChargeVo [id=" + id + ", journalId=" + journalId + ", manuscriptTypeId="
				+ manuscriptTypeId + ", manuscriptTypeName=" + manuscriptTypeName + ", fixedCharges=" + fixedCharges
				+ ", fixedChargeAmount=" + fixedChargeAmount + ", tablesCharges=" + tablesCharges + ", figureCharges="
				+ figureCharges + ", colorFigureCharges=" + colorFigureCharges + ", typeSettingCharges="
				+ typeSettingCharges + ", additionalCharges=" + additionalCharges + ", currency=" + currency
				+ ", chargeAmount=" + chargeAmount + ", chargeOnPublication=" + chargeOnPublication
				+ ", chargeOnSubmission=" + chargeOnSubmission + ", countryChargeOnSubmission="
				+ countryChargeOnSubmission + ", countryChargeOnPublication=" + countryChargeOnPublication
				+ ", publicationCharge=" + publicationCharge + ", submissionCharge=" + submissionCharge
				+ ", currencyOnSubmission=" + currencyOnSubmission + ", currencyOnPublication=" + currencyOnPublication
				+ ", isChargesRequired=" + isChargesRequired + ", isActive=" + isActive + ", paymentCountriesOnSubmission=" + paymentCountriesOnSubmission
				+ ", paymentCountriesOnPublishing="+ paymentCountriesOnPublishing +"]";
	}

}
