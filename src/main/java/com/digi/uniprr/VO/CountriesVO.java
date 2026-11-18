package com.digi.uniprr.VO;

import java.io.Serializable;

public class CountriesVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
     private String name;
     
     private String capital;
     
     private Integer phonecode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = capital;
	}

	public Integer getPhonecode() {
		return phonecode;
	}

	public void setPhonecode(Integer phonecode) {
		this.phonecode = phonecode;
	}

	@Override
	public String toString() {
		return "CountriesVO [name=" + name + ", capital=" + capital + ", phonecode=" + phonecode + "]";
	}

	     
     
}
