package com.digi.uniprr.VO;

public class PerCountryManuscriptVo {
	
	private Long manuscriptCount;
	private String country;
	public Long getManuscriptCount() {
		return manuscriptCount;
	}
	public void setManuscriptCount(Long manuscriptCount) {
		this.manuscriptCount = manuscriptCount;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public PerCountryManuscriptVo(Long manuscriptCount, String country) {
		super();
		this.manuscriptCount = manuscriptCount;
		this.country = country;
	}
	
}
