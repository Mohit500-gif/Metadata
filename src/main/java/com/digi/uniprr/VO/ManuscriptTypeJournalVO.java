package com.digi.uniprr.VO;

import javax.persistence.Column;

public class ManuscriptTypeJournalVO {

	private Integer mtjId;

	private String manutypeName;

	private Integer manuscriptTypeId;

	private Integer isActive;

	private Integer isAbstract;

	private String fixedCharges;

	private Float fixedChargeAmount;

	private Float tablesCharges;

	private Float figureCharges;

	private Float colorFigureCharges;

	private Float typeSettingCharges;

	private Float additionalCharges;

	private String chargeOnPublication;

	private String chargeOnSubmission;

	private Float publicationCharge;

	private Float submissionCharge;

	private String currencyOnSubmission;

	private String currencyOnPublication;

	private String isChargesRequired;

	private String currency;

	private Float chargeAmount;

	private String isCountryChargeOnPublication;

	private String isCountryChargeOnSubmission;

	private String trialNumberMandatory;

	private Integer journalId;

	public boolean isPaymentFlag() {
		return paymentFlag;
	}

	public void setPaymentFlag(boolean paymentFlag) {
		this.paymentFlag = paymentFlag;
	}

	private boolean paymentFlag;

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getTrialNumberMandatory() {
		return trialNumberMandatory;
	}

	public void setTrialNumberMandatory(String trialNumberMandatory) {
		this.trialNumberMandatory = trialNumberMandatory;
	}

	public String getIsCountryChargeOnPublication() {
		return isCountryChargeOnPublication;
	}

	public void setIsCountryChargeOnPublication(String isCountryChargeOnPublication) {
		this.isCountryChargeOnPublication = isCountryChargeOnPublication;
	}

	public String getIsCountryChargeOnSubmission() {
		return isCountryChargeOnSubmission;
	}

	public void setIsCountryChargeOnSubmission(String isCountryChargeOnSubmission) {
		this.isCountryChargeOnSubmission = isCountryChargeOnSubmission;
	}

	public Integer getMtjId() {
		return mtjId;
	}

	public void setMtjId(Integer mtjId) {
		this.mtjId = mtjId;
	}

	public String getManutypeName() {
		return manutypeName;
	}

	public void setManutypeName(String manutypeName) {
		this.manutypeName = manutypeName;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(Integer isAbstract) {
		this.isAbstract = isAbstract;
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

	public Integer getManuscriptTypeId() {
		return manuscriptTypeId;
	}

	public void setManuscriptTypeId(Integer manuscriptTypeId) {
		this.manuscriptTypeId = manuscriptTypeId;
	}

	@Override
	public String toString() {
		return "ManuscriptTypeJournalVO [mtjId=" + mtjId + ", manutypeName=" + manutypeName + ", manuscriptTypeId="
				+ manuscriptTypeId + ", isActive=" + isActive + ", isAbstract=" + isAbstract + ", fixedCharges="
				+ fixedCharges + ", fixedChargeAmount=" + fixedChargeAmount + ", tablesCharges=" + tablesCharges
				+ ", figureCharges=" + figureCharges + ", colorFigureCharges=" + colorFigureCharges
				+ ", typeSettingCharges=" + typeSettingCharges + ", additionalCharges=" + additionalCharges
				+ ", chargeOnPublication=" + chargeOnPublication + ", chargeOnSubmission=" + chargeOnSubmission
				+ ", publicationCharge=" + publicationCharge + ", submissionCharge=" + submissionCharge
				+ ", currencyOnSubmission=" + currencyOnSubmission + ", currencyOnPublication=" + currencyOnPublication
				+ ", isChargesRequired=" + isChargesRequired + ", currency=" + currency + ", chargeAmount="
				+ chargeAmount + ", isCountryChargeOnPublication=" + isCountryChargeOnPublication
				+ ", isCountryChargeOnSubmission=" + isCountryChargeOnSubmission
				+ ", paymentFlag="+ paymentFlag + "]";
	}

}
