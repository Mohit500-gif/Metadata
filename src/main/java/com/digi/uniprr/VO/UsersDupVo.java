package com.digi.uniprr.VO;

import com.digi.uniprr.model.Countries;

public class UsersDupVo {
	private Integer id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private String specialization;
	private Integer isActive;
	private Integer countryId;
	private Countries countries;
	private String expertise;
	private String institute;

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	public Integer getCountryId() {
		return countryId;
	}
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	public Countries getCountries() {
		return countries;
	}
	public void setCountries(Countries countries) {
		this.countries = countries;
	}
	public String getExpertise() {
		return expertise;
	}
	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}

	public UsersDupVo(Integer id, String firstName, String middleName, String lastName, String email,
			String specialization, Integer isActive, Integer countryId, Countries countries, String expertise, String institute) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.specialization = specialization;
		this.isActive = isActive;
		this.countryId = countryId;
		this.countries = countries;
		this.expertise = expertise;
		this.institute= institute;
	}
	
		
}
