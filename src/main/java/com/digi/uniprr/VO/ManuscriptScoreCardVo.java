package com.digi.uniprr.VO;

public class ManuscriptScoreCardVo {

	private Integer id;

	private Integer manuScriptId;

	private Integer manuscriptVersion;

	private Double avgSyllablesCount;

	private Double avgWordLength;

	private Double difficultWordsRatio;

	private Double fleschKincaidGrade;

	private Double fleschReadingEase;

	private Double gunningFogIndex;

	private Double posRatio;

	private Double sentenceLength;

	private Double sentenceNo;

	private Double typeToTokenRatio;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getManuScriptId() {
		return manuScriptId;
	}

	public void setManuScriptId(Integer manuScriptId) {
		this.manuScriptId = manuScriptId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public Double getAvgSyllablesCount() {
		return avgSyllablesCount;
	}

	public void setAvgSyllablesCount(Double avgSyllablesCount) {
		this.avgSyllablesCount = avgSyllablesCount;
	}

	public Double getAvgWordLength() {
		return avgWordLength;
	}

	public void setAvgWordLength(Double avgWordLength) {
		this.avgWordLength = avgWordLength;
	}

	public Double getDifficultWordsRatio() {
		return difficultWordsRatio;
	}

	public void setDifficultWordsRatio(Double difficultWordsRatio) {
		this.difficultWordsRatio = difficultWordsRatio;
	}

	public Double getFleschKincaidGrade() {
		return fleschKincaidGrade;
	}

	public void setFleschKincaidGrade(Double fleschKincaidGrade) {
		this.fleschKincaidGrade = fleschKincaidGrade;
	}

	public Double getFleschReadingEase() {
		return fleschReadingEase;
	}

	public void setFleschReadingEase(Double fleschReadingEase) {
		this.fleschReadingEase = fleschReadingEase;
	}

	public Double getGunningFogIndex() {
		return gunningFogIndex;
	}

	public void setGunningFogIndex(Double gunningFogIndex) {
		this.gunningFogIndex = gunningFogIndex;
	}

	public Double getPosRatio() {
		return posRatio;
	}

	public void setPosRatio(Double posRatio) {
		this.posRatio = posRatio;
	}

	public Double getSentenceLength() {
		return sentenceLength;
	}

	public void setSentenceLength(Double sentenceLength) {
		this.sentenceLength = sentenceLength;
	}

	public Double getSentenceNo() {
		return sentenceNo;
	}

	public void setSentenceNo(Double sentenceNo) {
		this.sentenceNo = sentenceNo;
	}

	public Double getTypeToTokenRatio() {
		return typeToTokenRatio;
	}

	public void setTypeToTokenRatio(Double typeToTokenRatio) {
		this.typeToTokenRatio = typeToTokenRatio;
	}
	
}
