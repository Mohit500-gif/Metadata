package com.digi.uniprr.VO;

import java.util.Date;
import java.util.List;

import com.digi.uniprr.enums.Salutation;

public class UserVO {
	private Integer id;

	private String orcidId;
	
	private Integer journal;
	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	private Integer userRole;
	
	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	private Integer someUserStatus;
	
	private String researcherId;
	
	private String trialRegistrationNo;
	
	private String pubmedId;
	
	private String scopusId;
	
	private String afflication;
	
	private String academic;

	private String username;

	private String password;

	private String token;

	private String email;

	private String emailCC;

	private String secondaryEmail;

	private String secondaryEmailCC;

	private Salutation salutation;

	private String firstName;

	private String middleName;

	private String lastName;

	private String userType;

	private String institute;

	private String state;

	private String address;

	private String addressTwo;

	private String addressThree;

	private String department;

	private String phoneNumber;

	private String mobileNumber;

	private String faxNumber;

	private Integer isActive;

	private Integer countryId;
	
	private String country;

	private String city;

	private String postalCode;

	private String areaOfInterest;

	private Integer notificationFlag;

	private String publicationName;

	private String accountExpired;

	private Integer accountLocked;

	private String designation;

	private String specialization;

	private String experience;

	private String expertise;

	private Integer emailNotification;

	private Integer smsNotification;

	private Integer marketingNotification;

	private Integer productNotification;

	private List<ExpertiseVO> expertiseData;

	private List<RoleVO> role;

	private List<GroupVO> group;

	private List<Integer> roleId;

	private List<Integer> groupId;

	private List<JournalListVO> journalVO;

	private String specialRemark;

	private List<Integer> journalId;

	private String fiveBestPapers;

	private String journalCode;

	private String userQualification;

	private Integer numberOfArticles;

	private String signUpAsAnAuthorWithAcronym;

	private String activationUrl;
	
	private Integer editorRoleId;
	
	private List<String> editorRoleAccess;
	
	private String isCoAuthor;
	
	private Integer manuscriptId;
	
	private String journalActivationUrl;
	
	private String forgetPasswordUrl;
	
	private Integer updatedBy;
	
   private Integer searchArticleStatus;

	private Integer rowId;

	private String validSalutation;

	private Boolean isBulkUpload = false;

	private Boolean isUpdate = false;

	private Boolean isJournalMap = false;
	
	private String isCorrespondingAuthor;
	   
	private Integer coAuthorID;

	private Boolean isExist;


	private Integer articlesInHandCount;

	private Integer articlesInReviewCount;

	private Date lastReviewedDate;

	private String editorAccessDetails;
	private Integer countryCode;
	
	
	
	public Integer getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}

	public UserVO() {}
	
	public UserVO(Integer id,  String firstName, String lastName, String email, String department, String specialization, String expertise, Integer isActive) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.department = department;
		this.specialization = specialization;
		this.expertise = expertise;
		this.isActive = isActive;

	}
	
	public UserVO(Integer id,  Integer journal, Integer userRole) {
		super();
		this.id = id;
		this.journal = journal;
		this.userRole = userRole;

	}
	
	

	public UserVO(Integer id, String orcidId, String username, String email, Salutation salutation, String firstName,
			String middleName, String lastName, String institute, String address, String phoneNumber,
			String mobileNumber, String faxNumber, String country, String city, String specialization,String expertise, String editorAccessDetails) {
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
		this.editorAccessDetails = editorAccessDetails;
	}

	public String getEditorAccessDetails() {
		return editorAccessDetails;
	}

	public void setEditorAccessDetails(String editorAccessDetails) {
		this.editorAccessDetails = editorAccessDetails;
	}

	public Date getLastReviewedDate() {
		return lastReviewedDate;
	}

	public void setLastReviewedDate(Date lastReviewedDate) {
		this.lastReviewedDate = lastReviewedDate;
	}

	public Integer getArticlesInHandCount() {
		return articlesInHandCount;
	}

	public void setArticlesInHandCount(Integer articlesInHandCount) {
		this.articlesInHandCount = articlesInHandCount;
	}

	public Integer getArticlesInReviewCount() {
		return articlesInReviewCount;
	}

	public void setArticlesInReviewCount(Integer articlesInReviewCount) {
		this.articlesInReviewCount = articlesInReviewCount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public String getSecondaryEmailCC() {
		return secondaryEmailCC;
	}

	public void setSecondaryEmailCC(String secondaryEmailCC) {
		this.secondaryEmailCC = secondaryEmailCC;
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

	@Override
	public String toString() {
		return "UserVO{" +
				"id=" + id +
				", orcidId='" + orcidId + '\'' +
				", journal=" + journal +
				", accessToken='" + accessToken + '\'' +
				", userRole=" + userRole +
				", someUserStatus=" + someUserStatus +
				", researcherId='" + researcherId + '\'' +
				", trialRegistrationNo='" + trialRegistrationNo + '\'' +
				", pubmedId='" + pubmedId + '\'' +
				", scopusId='" + scopusId + '\'' +
				", afflication='" + afflication + '\'' +
				", academic='" + academic + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", token='" + token + '\'' +
				", email='" + email + '\'' +
				", emailCC='" + emailCC + '\'' +
				", secondaryEmail='" + secondaryEmail + '\'' +
				", secondaryEmailCC='" + secondaryEmailCC + '\'' +
				", salutation=" + salutation +
				", firstName='" + firstName + '\'' +
				", middleName='" + middleName + '\'' +
				", lastName='" + lastName + '\'' +
				", userType='" + userType + '\'' +
				", institute='" + institute + '\'' +
				", state='" + state + '\'' +
				", address='" + address + '\'' +
				", addressTwo='" + addressTwo + '\'' +
				", addressThree='" + addressThree + '\'' +
				", department='" + department + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", mobileNumber='" + mobileNumber + '\'' +
				", faxNumber='" + faxNumber + '\'' +
				", isActive=" + isActive +
				", countryId=" + countryId +
				", country='" + country + '\'' +
				", city='" + city + '\'' +
				", postalCode='" + postalCode + '\'' +
				", areaOfInterest='" + areaOfInterest + '\'' +
				", notificationFlag=" + notificationFlag +
				", publicationName='" + publicationName + '\'' +
				", accountExpired='" + accountExpired + '\'' +
				", accountLocked=" + accountLocked +
				", designation='" + designation + '\'' +
				", specialization='" + specialization + '\'' +
				", experience='" + experience + '\'' +
				", expertise='" + expertise + '\'' +
				", emailNotification=" + emailNotification +
				", smsNotification=" + smsNotification +
				", marketingNotification=" + marketingNotification +
				", productNotification=" + productNotification +
				", expertiseData=" + expertiseData +
				", role=" + role +
				", group=" + group +
				", roleId=" + roleId +
				", groupId=" + groupId +
				", journalVO=" + journalVO +
				", specialRemark='" + specialRemark + '\'' +
				", journalId=" + journalId +
				", fiveBestPapers='" + fiveBestPapers + '\'' +
				", journalCode='" + journalCode + '\'' +
				", userQualification='" + userQualification + '\'' +
				", numberOfArticles=" + numberOfArticles +
				", signUpAsAnAuthorWithAcronym='" + signUpAsAnAuthorWithAcronym + '\'' +
				", activationUrl='" + activationUrl + '\'' +
				", editorRoleId=" + editorRoleId +
				", editorRoleAccess=" + editorRoleAccess +
				", isCoAuthor='" + isCoAuthor + '\'' +
				", manuscriptId=" + manuscriptId +
				", journalActivationUrl='" + journalActivationUrl + '\'' +
				", forgetPasswordUrl='" + forgetPasswordUrl + '\'' +
				", updatedBy=" + updatedBy +
				", searchArticleStatus=" + searchArticleStatus +
				", rowId=" + rowId +
				", validSalutation='" + validSalutation + '\'' +
				", isBulkUpload=" + isBulkUpload +
				", isUpdate=" + isUpdate +
				", isJournalMap=" + isJournalMap +
				", isCorrespondingAuthor='" + isCorrespondingAuthor + '\'' +
				", coAuthorID=" + coAuthorID +
				", isExist=" + isExist +
				", articlesInHandCount=" + articlesInHandCount +
				", articlesInReviewCount=" + articlesInReviewCount +
				", lastReviewedDate=" + lastReviewedDate +
				", editorAccessDetails='" + editorAccessDetails + '\'' +
				", countryCode=" + countryCode +
				'}';
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getAreaOfInterest() {
		return areaOfInterest;
	}

	public void setAreaOfInterest(String areaOfInterest) {
		this.areaOfInterest = areaOfInterest;
	}

	public Integer getNotificationFlag() {
		return notificationFlag;
	}

	public void setNotificationFlag(Integer notificationFlag) {
		this.notificationFlag = notificationFlag;
	}

	public String getPublicationName() {
		return publicationName;
	}

	public void setPublicationName(String publicationName) {
		this.publicationName = publicationName;
	}

	public String getAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(String accountExpired) {
		this.accountExpired = accountExpired;
	}

	public Integer getAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(Integer accountLocked) {
		this.accountLocked = accountLocked;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getExpertise() {
		return expertise;
	}

	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}

	public Integer getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(Integer emailNotification) {
		this.emailNotification = emailNotification;
	}

	public Integer getSmsNotification() {
		return smsNotification;
	}

	public void setSmsNotification(Integer smsNotification) {
		this.smsNotification = smsNotification;
	}

	public Integer getMarketingNotification() {
		return marketingNotification;
	}

	public void setMarketingNotification(Integer marketingNotification) {
		this.marketingNotification = marketingNotification;
	}

	public Integer getProductNotification() {
		return productNotification;
	}

	public void setProductNotification(Integer productNotification) {
		this.productNotification = productNotification;
	}

	public List<ExpertiseVO> getExpertiseData() {
		return expertiseData;
	}

	public void setExpertiseData(List<ExpertiseVO> expertiseData) {
		this.expertiseData = expertiseData;
	}

	public Integer getSomeUserStatus() {
		return someUserStatus;
	}

	public void setSomeUserStatus(Integer someUserStatus) {
		this.someUserStatus = someUserStatus;
	}

	public List<RoleVO> getRole() {
		return role;
	}

	public void setRole(List<RoleVO> role) {
		this.role = role;
	}

	public List<GroupVO> getGroup() {
		return group;
	}

	public void setGroup(List<GroupVO> group) {
		this.group = group;
	}

	public List<Integer> getRoleId() {
		return roleId;
	}

	public void setRoleId(List<Integer> roleId) {
		this.roleId = roleId;
	}

	public List<Integer> getGroupId() {
		return groupId;
	}

	public void setGroupId(List<Integer> groupId) {
		this.groupId = groupId;
	}

	public List<JournalListVO> getJournalVO() {
		return journalVO;
	}

	public void setJournalVO(List<JournalListVO> journalVO) {
		this.journalVO = journalVO;
	}

	public String getSpecialRemark() {
		return specialRemark;
	}

	public void setSpecialRemark(String specialRemark) {
		this.specialRemark = specialRemark;
	}

	public List<Integer> getJournalId() {
		return journalId;
	}

	public void setJournalId(List<Integer> journalId) {
		this.journalId = journalId;
	}

	public String getFiveBestPapers() {
		return fiveBestPapers;
	}

	public void setFiveBestPapers(String fiveBestPapers) {
		this.fiveBestPapers = fiveBestPapers;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public String getUserQualification() {
		return userQualification;
	}

	public void setUserQualification(String userQualification) {
		this.userQualification = userQualification;
	}

	public Integer getNumberOfArticles() {
		return numberOfArticles;
	}

	public void setNumberOfArticles(Integer numberOfArticles) {
		this.numberOfArticles = numberOfArticles;
	}

	public String getSignUpAsAnAuthorWithAcronym() {
		return signUpAsAnAuthorWithAcronym;
	}

	public void setSignUpAsAnAuthorWithAcronym(String signUpAsAnAuthorWithAcronym) {
		this.signUpAsAnAuthorWithAcronym = signUpAsAnAuthorWithAcronym;
	}

	public String getActivationUrl() {
		return activationUrl;
	}

	public void setActivationUrl(String activationUrl) {
		this.activationUrl = activationUrl;
	}

	public Integer getEditorRoleId() {
		return editorRoleId;
	}

	public void setEditorRoleId(Integer editorRoleId) {
		this.editorRoleId = editorRoleId;
	}

	public List<String> getEditorRoleAccess() {
		return editorRoleAccess;
	}

	public void setEditorRoleAccess(List<String> editorRoleAccess) {
		this.editorRoleAccess = editorRoleAccess;
	}

	public String getIsCoAuthor() {
		return isCoAuthor;
	}

	public void setIsCoAuthor(String isCoAuthor) {
		this.isCoAuthor = isCoAuthor;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
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

	public String getJournalActivationUrl() {
		return journalActivationUrl;
	}

	public void setJournalActivationUrl(String journalActivationUrl) {
		this.journalActivationUrl = journalActivationUrl;
	}

	public String getForgetPasswordUrl() {
		return forgetPasswordUrl;
	}

	public void setForgetPasswordUrl(String forgetPasswordUrl) {
		this.forgetPasswordUrl = forgetPasswordUrl;
	}

	public Integer getJournal() {
		return journal;
	}

	public void setJournal(Integer journal) {
		this.journal = journal;
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

	public String getAfflication() {
		return afflication;
	}

	public void setAfflication(String afflication) {
		this.afflication = afflication;
	}

	public String getAcademic() {
		return academic;
	}

	public void setAcademic(String academic) {
		this.academic = academic;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getSearchArticleStatus() {
		return searchArticleStatus;
	}

	public void setSearchArticleStatus(Integer searchArticleStatus) {
		this.searchArticleStatus = searchArticleStatus;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public String getValidSalutation() {
		return validSalutation;
	}

	public void setValidSalutation(String validSalutation) {
		this.validSalutation = validSalutation;
	}

	public Boolean getIsBulkUpload() {
		return isBulkUpload;
	}

	public void setIsBulkUpload(Boolean isBulkUpload) {
		this.isBulkUpload = isBulkUpload;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Boolean getIsJournalMap() {
		return isJournalMap;
	}

	public void setIsJournalMap(Boolean isJournalMap) {
		this.isJournalMap = isJournalMap;
	}

	public String getIsCorrespondingAuthor() {
		return isCorrespondingAuthor;
	}

	public void setIsCorrespondingAuthor(String isCorrespondingAuthor) {
		this.isCorrespondingAuthor = isCorrespondingAuthor;
	}

	public Integer getCoAuthorID() {
		return coAuthorID;
	}

	public void setCoAuthorID(Integer coAuthorID) {
		this.coAuthorID = coAuthorID;
	}

	public Boolean getIsExist() {
		return isExist;
	}

	public void setIsExist(Boolean isExist) {
		this.isExist = isExist;
	}

	
}
