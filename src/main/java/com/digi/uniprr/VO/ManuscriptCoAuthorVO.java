package com.digi.uniprr.VO;

import com.digi.uniprr.enums.Salutation;

public class ManuscriptCoAuthorVO {

	private Integer id;

	private Integer coAuthorId;

	private String orcidId;

	private String researcherId;

	private String trialRegistrationNo;

	private Salutation salutation;

	private String orcIdMandatory;

	private String firstName;

	private String middleName;

	private String lastName;

	private String email;

	private String address;

	private String addressTwo;

	private String addressThree;

	private String institution;
	
	private String academic;

	private String type;

	private Integer order;

	private Integer manuscriptId;

	private String authorType;

	private Integer correspondingAuthor;

	private String activationUrl;

	private String copyrightAgreement;

	private String copyrightAgreementContent;
	private String coAuthorFullName;
	
	private String state;
	
	private String city;
	
	private Integer countryId;
	
	private String isCorrespondingAuthor;
	
	private Integer coAuthorStatus;
	
	private String scopusId;
    private String expertise;
    
    private String institute;
	
    private String specialization;
    
    private String pubmedId;
    
    private String mobileNumber;
    private String phoneNumber;
    private String postalCode;
    
    private Integer countryCode;
    
	public Integer getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getPubmedId() {
		return pubmedId;
	}

	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getExpertise() {
		return expertise;
	}

	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}

	public String getScopusId() {
		return scopusId;
	}

	public void setScopusId(String scopusId) {
		this.scopusId = scopusId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCoAuthorId() {
		return coAuthorId;
	}

	public void setCoAuthorId(Integer coAuthorId) {
		this.coAuthorId = coAuthorId;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressTwo() {
		return addressTwo;
	}

	public void setAddressTwo(String addressTwo) {
		this.addressTwo = addressTwo;
	}

	public String getAddressThree() {
		return addressThree;
	}

	public void setAddressThree(String addressThree) {
		this.addressThree = addressThree;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getCorrespondingAuthor() {
		return correspondingAuthor;
	}

	public void setCorrespondingAuthor(Integer correspondingAuthor) {
		this.correspondingAuthor = correspondingAuthor;
	}

	public String getAuthorType() {
		return authorType;
	}

	public void setAuthorType(String authorType) {
		this.authorType = authorType;
	}

	public String getActivationUrl() {
		return activationUrl;
	}

	public void setActivationUrl(String activationUrl) {
		this.activationUrl = activationUrl;
	}

	public String getCopyrightAgreement() {
		return copyrightAgreement;
	}

	public void setCopyrightAgreement(String copyrightAgreement) {
		this.copyrightAgreement = copyrightAgreement;
	}

	public String getCopyrightAgreementContent() {
		return copyrightAgreementContent;
	}

	public void setCopyrightAgreementContent(String copyrightAgreementContent) {
		this.copyrightAgreementContent = copyrightAgreementContent;
	}

	public String getCoAuthorFullName() {
		return coAuthorFullName;
	}

	public void setCoAuthorFullName(String coAuthorFullName) {
		this.coAuthorFullName = coAuthorFullName;
	}

	public String getOrcidId() {
		return orcidId;
	}

	public void setOrcidId(String orcidId) {
		this.orcidId = orcidId;
	}

	public String getResearcherId() {
		return researcherId;
	}

	public void setResearcherId(String researcherId) {
		this.researcherId = researcherId;
	}

	public String getTrialRegistrationNo() {
		return trialRegistrationNo;
	}

	public void setTrialRegistrationNo(String trialRegistrationNo) {
		this.trialRegistrationNo = trialRegistrationNo;
	}

	public String getOrcIdMandatory() {
		return orcIdMandatory;
	}

	public void setOrcIdMandatory(String orcIdMandatory) {
		this.orcIdMandatory = orcIdMandatory;
	}

	public String getAcademic() {
		return academic;
	}

	public void setAcademic(String academic) {
		this.academic = academic;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String getIsCorrespondingAuthor() {
		return isCorrespondingAuthor;
	}

	public void setIsCorrespondingAuthor(String isCorrespondingAuthor) {
		this.isCorrespondingAuthor = isCorrespondingAuthor;
	}

	public Integer getCoAuthorStatus() {
		return coAuthorStatus;
	}

	public void setCoAuthorStatus(Integer coAuthorStatus) {
		this.coAuthorStatus = coAuthorStatus;
	}
	
}
