package com.digi.uniprr.VO;

import java.io.Serializable;

public class AuthorCountryVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String countryName;
	private Integer manuscriptsSubmitted;

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getManuscriptsSubmitted() {
		return manuscriptsSubmitted;
	}

	public void setManuscriptsSubmitted(Integer manuscriptsSubmitted) {
		this.manuscriptsSubmitted = manuscriptsSubmitted;
	}

	@Override
	public String toString() {
		return "AuthorCountryVo [countryName=" + countryName + ", manuscriptsSubmitted=" + manuscriptsSubmitted + "]";
	}

}
