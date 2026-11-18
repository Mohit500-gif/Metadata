package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AuthorPerformanceVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer totalAuthorsRegistered;
	private Integer numberofAuthorsSubmittedManuscripts;
	private Integer numberofAuthorsSubmittedMoreThanOneManuscripts;
	private Integer numberOfManuscriptsFromAbroad;
	private Integer numberOfOriginalArticlesFromAbroad;
	private List<AuthorCountryVo> show;

	List<IndividualAutorDetailsVO> authorsSubmittedMoreThanOneManuscripts;
	
	
	private String NumberOfMFromAbroadPercentage;
	private String NumberOfMOrignalFromAbroadPercentage;

	
	
	public String getNumberOfMFromAbroadPercentage() {
		return NumberOfMFromAbroadPercentage;
	}

	public void setNumberOfMFromAbroadPercentage(String numberOfMFromAbroadPercentage) {
		NumberOfMFromAbroadPercentage = numberOfMFromAbroadPercentage;
	}

	public String getNumberOfMOrignalFromAbroadPercentage() {
		return NumberOfMOrignalFromAbroadPercentage;
	}

	public void setNumberOfMOrignalFromAbroadPercentage(String numberOfMOrignalFromAbroadPercentage) {
		NumberOfMOrignalFromAbroadPercentage = numberOfMOrignalFromAbroadPercentage;
	}

	public Integer getTotalAuthorsRegistered() {
		return totalAuthorsRegistered;
	}

	public void setTotalAuthorsRegistered(Integer totalAuthorsRegistered) {
		this.totalAuthorsRegistered = totalAuthorsRegistered;
	}

	public Integer getNumberofAuthorsSubmittedManuscripts() {
		return numberofAuthorsSubmittedManuscripts;
	}

	public void setNumberofAuthorsSubmittedManuscripts(Integer numberofAuthorsSubmittedManuscripts) {
		this.numberofAuthorsSubmittedManuscripts = numberofAuthorsSubmittedManuscripts;
	}

	public Integer getNumberofAuthorsSubmittedMoreThanOneManuscripts() {
		return numberofAuthorsSubmittedMoreThanOneManuscripts;
	}

	public void setNumberofAuthorsSubmittedMoreThanOneManuscripts(
			Integer numberofAuthorsSubmittedMoreThanOneManuscripts) {
		this.numberofAuthorsSubmittedMoreThanOneManuscripts = numberofAuthorsSubmittedMoreThanOneManuscripts;
	}

	public Integer getNumberOfManuscriptsFromAbroad() {
		return numberOfManuscriptsFromAbroad;
	}

	public void setNumberOfManuscriptsFromAbroad(Integer numberOfManuscriptsFromAbroad) {
		this.numberOfManuscriptsFromAbroad = numberOfManuscriptsFromAbroad;
	}

	public Integer getNumberOfOriginalArticlesFromAbroad() {
		return numberOfOriginalArticlesFromAbroad;
	}

	public void setNumberOfOriginalArticlesFromAbroad(Integer numberOfOriginalArticlesFromAbroad) {
		this.numberOfOriginalArticlesFromAbroad = numberOfOriginalArticlesFromAbroad;
	}

	public List<AuthorCountryVo> getShow() {
		return show;
	}

	public void setShow(List<AuthorCountryVo> show) {
		this.show = show;
	}

	public List<IndividualAutorDetailsVO> getAuthorsSubmittedMoreThanOneManuscripts() {
		return authorsSubmittedMoreThanOneManuscripts;
	}

	public void setAuthorsSubmittedMoreThanOneManuscripts(
			List<IndividualAutorDetailsVO> authorsSubmittedMoreThanOneManuscripts) {
		this.authorsSubmittedMoreThanOneManuscripts = authorsSubmittedMoreThanOneManuscripts;
	}

	@Override
	public String toString() {
		return "AuthorPerformanceVo [totalAuthorsRegistered=" + totalAuthorsRegistered
				+ ", numberofAuthorsSubmittedManuscripts=" + numberofAuthorsSubmittedManuscripts
				+ ", numberofAuthorsSubmittedMoreThanOneManuscripts=" + numberofAuthorsSubmittedMoreThanOneManuscripts
				+ ", numberOfManuscriptsFromAbroad=" + numberOfManuscriptsFromAbroad
				+ ", numberOfOriginalArticlesFromAbroad=" + numberOfOriginalArticlesFromAbroad + ", show=" + show + "]";
	}

}
