package com.digi.uniprr.VO;

import com.digi.uniprr.enums.Salutation;

public class UserReportVO {
	
    private Integer id;	

	private String orcidId;

	private String username;

	private String email;

	private String firstName;

	private String middleName;

	private String lastName;

	private String country;

	private String specialization;

	private String expertise;

	private String specialRemark;

	private String mobileNumber;

	private String institute;
	
	private Integer smsNotification;
	
	private String emailCC;
	
	private Integer searchArticleStatus;
	
	private String guest;
	
	private String faxNumber;
	
	private String phoneNumber;
	
	private String city;
	
	private String address;
	
//	private Salutation salutation;
	
	private String salutation;
	
	private String door;
	
	private Boolean isResponded =false;
	public UserReportVO() {}
	
	public UserReportVO(Integer id, String orcidId, String username, String email, String salutation, String firstName,
			String middleName, String lastName, String institute, String address, String phoneNumber,
			String mobileNumber, String faxNumber, String country, String city, String specialization,String expertise) {
		super();
		this.id = id;
		this.orcidId = orcidId;
		this.username = username;
		this.email = email;
		this.salutation = salutation;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.institute = institute;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.mobileNumber = mobileNumber;
		this.faxNumber = faxNumber;
		this.country = country;
		this.city = city;
		this.specialization = specialization;
		this.expertise = expertise;
	}

	public String getOrcidId() {
		return orcidId;
	}

	public void setOrcidId(String orcidId) {
		this.orcidId = orcidId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getExpertise() {
		return expertise;
	}

	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}

	public String getSpecialRemark() {
		return specialRemark;
	}

	public void setSpecialRemark(String specialRemark) {
		this.specialRemark = specialRemark;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}
	
	public Integer getSmsNotification() {
		return smsNotification;
	}

	public void setSmsNotification(Integer smsNotification) {
		this.smsNotification = smsNotification;
	}
	
	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public Integer getSearchArticleStatus() {
		return searchArticleStatus;
	}

	public void setSearchArticleStatus(Integer searchArticleStatus) {
		this.searchArticleStatus = searchArticleStatus;
	}

	public String getGuest() {
		return guest;
	}

	public void setGuest(String guest) {
		this.guest = guest;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		this.door = door;
	}

	public Boolean getIsResponded() {
		return isResponded;
	}

	public void setIsResponded(Boolean isResponded) {
		this.isResponded = isResponded;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
}