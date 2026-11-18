package com.digi.uniprr.VO;

import java.io.Serializable;

public class UserExportVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	
	private String firstName;

	private String middleName;

	private String lastName;
	
	private String userEmail;
	
	private String roleName;
	
	private String editorRole;
	
	private String responsiblities;
	
	private String journalAcronym;
	
	private String academicAffiliations;
	
	private String institution;
	
	private String researchId;
	
	private String orcidId;
	
	private String pubmedId;
	
	private String scopusId;
	
	private String specialization;
	
	private String subSpecialization;
	
	private String address;
	
	private String city;
	
	private String state;
	
	private String countryName;
	
	private String postalCode;
	
	private String phoneNumber;
	
	private String faxNumber;
	
	private String mobileNumber;
	
	
	
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


	public String getSpecialization() {
		return specialization;
	}


	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}


	public String getUserEmail() {
		return userEmail;
	}


	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}


	public String getCountryName() {
		return countryName;
	}


	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}


	public String getRoleName() {
		return roleName;
	}


	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getJournalAcronym() {
		return journalAcronym;
	}


	public void setJournalAcronym(String journalAcronym) {
		this.journalAcronym = journalAcronym;
	}

	public String getAcademicAffiliations() {
		return academicAffiliations;
	}


	public void setAcademicAffiliations(String academicAffiliations) {
		this.academicAffiliations = academicAffiliations;
	}


	public String getInstitution() {
		return institution;
	}


	public void setInstitution(String institution) {
		this.institution = institution;
	}


	public String getResearchId() {
		return researchId;
	}


	public void setResearchId(String researchId) {
		this.researchId = researchId;
	}


	public String getOrcidId() {
		return orcidId;
	}


	public void setOrcidId(String orcidId) {
		this.orcidId = orcidId;
	}


	public String getPubmedId() {
		return pubmedId;
	}


	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}


	public String getScopusId() {
		return scopusId;
	}


	public void setScopusId(String scopusId) {
		this.scopusId = scopusId;
	}

	public String getSubSpecialization() {
		return subSpecialization;
	}


	public void setSubSpecialization(String subSpecialization) {
		this.subSpecialization = subSpecialization;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getPostalCode() {
		return postalCode;
	}


	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getFaxNumber() {
		return faxNumber;
	}


	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}


	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEditorRole() {
		return editorRole;
	}


	public void setEditorRole(String editorRole) {
		this.editorRole = editorRole;
	}


	public String getResponsiblities() {
		return responsiblities;
	}


	public void setResponsiblities(String responsiblities) {
		this.responsiblities = responsiblities;
	}


	public UserExportVo(String title, String firstName, String middleName, String lastName, String userEmail,
			String roleName, String editorRole, String responsiblities, String journalAcronym,
			String academicAffiliations, String institution, String researchId, String orcidId, String pubmedId,
			String scopusId, String specialization, String subSpecialization, String address, String city, String state,
			String countryName, String postalCode, String phoneNumber, String faxNumber, String mobileNumber) {
		super();
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.userEmail = userEmail;
		this.roleName = roleName;
		this.editorRole = editorRole;
		this.responsiblities = responsiblities;
		this.journalAcronym = journalAcronym;
		this.academicAffiliations = academicAffiliations;
		this.institution = institution;
		this.researchId = researchId;
		this.orcidId = orcidId;
		this.pubmedId = pubmedId;
		this.scopusId = scopusId;
		this.specialization = specialization;
		this.subSpecialization = subSpecialization;
		this.address = address;
		this.city = city;
		this.state = state;
		this.countryName = countryName;
		this.postalCode = postalCode;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.mobileNumber = mobileNumber;
	}


}
