package com.digi.uniprr.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;

import com.digi.uniprr.enums.Salutation;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "orcid_id")
	private String orcidId;

	@Column(name = "researcher_id")
	private String researcherId;

	@Column(name = "trial_registration_no")
	private String trialRegistrationNo;

	@Column(name = "pubmed_id")
	private String pubmedId;

	@Column(name = "scopus_id")
	private String scopusId;

	@Column(name = "afflication")
	private String afflication;

	@Column(name = "academic")
	private String academic;

	@Column(name = "user_name")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "token")
	private String token;

	@Column(name = "user_email")
	private String email;

	@Column(name = "user_email_cc")
	private String emailCC;

	@Column(name = "user_sec_email")
	private String secondaryEmail;

	@Column(name = "user_sec_email_cc")
	private String secondaryEmailCC;

	@Enumerated(EnumType.STRING)
	@Column(name = "salutation")
	private Salutation salutation;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "user_type")
	private String userType;

	@Column(name = "institute")
	private String institute;

	@Column(name = "state")
	private String state;

	@Column(name = "address")
	private String address;

	@Column(name = "address2")
	private String addressTwo;

	@Column(name = "address3")
	private String addressThree;

	@Column(name = "department")
	private String department;

	@Column(name = "phone_no")
	private String phoneNumber;

	@Column(name = "mobile_no")
	private String mobileNumber;

	@Column(name = "fax_no")
	private String faxNumber;

	@Column(name = "is_active")
	private Integer isActive;

	@Column(name = "country_id")
	private Integer countryId;

	@OneToOne
	@JoinColumn(name = "country_id", updatable = false, insertable = false)
	private Countries countries;

	@Column(name = "city")
	private String city;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "area_interest")
	private String areaOfInterest;

	@Column(name = "notification_flag")
	private Integer notificationFlag;

	@Column(name = "publication_name")
	private String publicationName;

	@Column(name = "account_expired")
	private String accountExpired;

	@Column(name = "account_locked")
	private Integer accountLocked;

	@Column(name = "desgination")
	private String designation;

	@Column(name = "apply_as_reviewer")
	private Integer applyAsReviewer;

	@Column(name = "specialization")
	private String specialization;

	@Column(name = "experience")
	private String experience;

	@Column(name = "special_remark")
	private String specialRemark;

	@Column(name = "expertise")
	private String expertise;

	@Column(name = "email_notification")
	private Integer emailNotification;

	@Column(name = "sms_notification")
	private Integer smsNotification;

	@Column(name = "marketing_notification")
	private Integer marketingNotification;

	@Column(name = "product_notification")
	private Integer productNotification;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date", nullable = false, updatable = false)
	@CreationTimestamp
	private Date createdDate;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "updated_date")
	@UpdateTimestamp
	private Date updatedDate;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_group", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "group_id", referencedColumnName = "id") })
	@JsonIgnore
	private List<Groups> group;

	@OneToMany
	@JoinTable(name = "users_journals", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "journal_id", referencedColumnName = "journal_id") })
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private List<Journal> journal;

	@OneToMany
	@JoinTable(name = "users_journals", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private List<Role> role;

	@ManyToMany
	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "usr_id") }, inverseJoinColumns = {
			@JoinColumn(name = "id") })
	private List<UserRole> userRole;

	@ManyToMany
	@JoinTable(name = "users_journals", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "id") })
	private List<UserJournal> UserJournal;

	@OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private EditorRoleAccess editorRoleAccess;

	@Column(name = "five_best_paper")
	private String fiveBestPapers;

	@Column(name = "user_qualification")
	private String userQualification;

	@Column(name = "number_of_articles")
	private Integer numberOfArticles;
	
	@Column(name = "search_article_status")
	private Integer searchArticleStatus;

	@Column(name = "editor_access_details")
	private String editorAccessDetails;
	
	@Column(name = "country_code")
	private Integer countryCode;
	
	

	public Integer getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}

	public String getEditorAccessDetails() {
		return editorAccessDetails;
	}

	public void setEditorAccessDetails(String editorAccessDetails) {
		this.editorAccessDetails = editorAccessDetails;
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

	public Integer getApplyAsReviewer() {
		return applyAsReviewer;
	}

	public void setApplyAsReviewer(Integer applyAsReviewer) {
		this.applyAsReviewer = applyAsReviewer;
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

	public String getSpecialRemark() {
		return specialRemark;
	}

	public void setSpecialRemark(String specialRemark) {
		this.specialRemark = specialRemark;
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

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public List<Groups> getGroup() {
		return group;
	}

	public void setGroup(List<Groups> group) {
		this.group = group;
	}

	public List<Journal> getJournal() {
		return journal;
	}

	public void setJournal(List<Journal> journal) {
		this.journal = journal;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public List<UserRole> getUserRole() {
		return userRole;
	}

	public void setUserRole(List<UserRole> userRole) {
		this.userRole = userRole;
	}

	public EditorRoleAccess getEditorRoleAccess() {
		return editorRoleAccess;
	}

	public void setEditorRoleAccess(EditorRoleAccess editorRoleAccess) {
		this.editorRoleAccess = editorRoleAccess;
	}

	public String getFiveBestPapers() {
		return fiveBestPapers;
	}

	public void setFiveBestPapers(String fiveBestPapers) {
		this.fiveBestPapers = fiveBestPapers;
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

	public List<UserJournal> getUserJournal() {
		return UserJournal;
	}

	public void setUserJournal(List<UserJournal> userJournal) {
		UserJournal = userJournal;
	}

	public Countries getCountries() {
		return countries;
	}

	public void setCountries(Countries countries) {
		this.countries = countries;
	}
	

	public Integer getSearchArticleStatus() {
		return searchArticleStatus;
	}

	public void setSearchArticleStatus(Integer searchArticleStatus) {
		this.searchArticleStatus = searchArticleStatus;
	}


}
