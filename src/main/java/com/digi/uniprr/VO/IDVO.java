package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonFormat;

public class IDVO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9073291519396217182L;
	private Integer userId;
	private Integer roleId;
	private Integer isActive;
	private String city;
	private Integer id;
	private String ipAddress;
	private String ipEnd;
	private String dotStart;
	private String dotEnd;
	private String countryShort;
	private String country;
	private String countryType;
	private String comment;
	private String emailTemplateForm;
	private Integer journalId;
	private Integer taskId;
	private String manuscriptStatus;
	private Integer year;
	private List<Integer> manuscriptStatusId;
	private List<Integer> journalIdList;
	private List<Integer> authorIdList;
	private JSONObject feedback;
	private Integer version;
	private List<Integer> yearList;
	private Integer month;
	private String journalMailTemplatesBody;
	private String journalMailTemplatesSubject;
	private String name;
	private String manuscriptCode;
	private List<String> journalCode;
	private List<Integer> roleIdList;
	private JSONObject policies;
	private Integer createdBy;
	private String token;
	private String email;
	private String password;
	private Integer manuId;
	private String presentDate;
	private Integer isReviewerActive;
	private String ratingName;
	private Integer ratingNumber;
	private Integer manuscriptId;
	private Integer manuscriptVersion;
	private String fileType;
	private String ediotrialDashboard;
	private String copyrightAgreement;
	private String copyrightAgreementContent;
	private Date reviewerAssignDate;
	private Date reviewerUnAssignDate;
	private Date reviewerStartDate;
	private Date reviewerCompleteDate;
	private Date reviewerRejectDate;
	private Integer editor_id;
	private String editorAccessDetails;
	private String folder;
	private String stepName;
    private int page = 0;   // default
    private int size = 10;  // default
    private String searchTerm;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone="Asia/Kolkata")
	private Date reviewerDueDate;

	private Date editorAssignDate;
	private Date editorDecisionDate;

	private Date managingEditorAssignDate;
	private Date managingEditoDecisionDate;

	private String fromMail;

	private String toMail;

	private String subject;

	private String mailBody;

	private Integer currentUserId;
	private Integer previousUserId;

	private Integer countEditor;
	private Integer countManagingEditor;
	private Integer countAuthor;
	private Integer countRefree;
	private String salutation;
	private String firstName;
	private String middleName;
	private String lastName;
	private String waveOff;
	private String specialization;
	private String experties;
	private Integer countryId;
	private Integer manuscriptReviewerId;
	
	private Integer pageNo;
	private Integer limit;
	private String search;
	private String sortBy;
	private String sort;
	
	private String readOrcid;

	private String alphabet;

	public String getReadOrcid() {
		return readOrcid;
	}

	public void setReadOrcid(String readOrcid) {
		this.readOrcid = readOrcid;
	}

	public String getEditorAccessDetails() {
		return editorAccessDetails;
	}

	public void setEditorAccessDetails(String editorAccessDetails) {
		this.editorAccessDetails = editorAccessDetails;
	}

	public String getName() {
		return name;
	}

	public Integer getCountEditor() {
		return countEditor;
	}

	public void setCountEditor(Integer countEditor) {
		this.countEditor = countEditor;
	}

	public Integer getCountManagingEditor() {
		return countManagingEditor;
	}

	public void setCountManagingEditor(Integer countManagingEditor) {
		this.countManagingEditor = countManagingEditor;
	}

	public Integer getCountAuthor() {
		return countAuthor;
	}

	public void setCountAuthor(Integer countAuthor) {
		this.countAuthor = countAuthor;
	}

	public Integer getCountRefree() {
		return countRefree;
	}

	public void setCountRefree(Integer countRefree) {
		this.countRefree = countRefree;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<Integer> getJournalIdList() {
		return journalIdList;
	}

	public void setJournalIdList(List<Integer> journalIdList) {
		this.journalIdList = journalIdList;
	}

	public List<Integer> getYearList() {
		return yearList;
	}

	public void setYearList(List<Integer> yearList) {
		this.yearList = yearList;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public List<String> getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(List<String> journalCode) {
		this.journalCode = journalCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public List<Integer> getRoleIdList() {
		return roleIdList;
	}

	public void setRoleIdList(List<Integer> roleIdList) {
		this.roleIdList = roleIdList;
	}

	public List<Integer> getAuthorIdList() {
		return authorIdList;
	}

	public void setAuthorIdList(List<Integer> authorIdList) {
		this.authorIdList = authorIdList;
	}

	public JSONObject getFeedback() {
		return feedback;
	}

	public void setFeedback(JSONObject feedback) {
		this.feedback = feedback;
	}

	public JSONObject getPolicies() {
		return policies;
	}

	public void setPolicies(JSONObject policies) {
		this.policies = policies;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
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

	public Integer getManuId() {
		return manuId;
	}

	public void setManuId(Integer manuId) {
		this.manuId = manuId;
	}

	public String getRatingName() {
		return ratingName;
	}

	public void setRatingName(String ratingName) {
		this.ratingName = ratingName;
	}

	public Integer getRatingNumber() {
		return ratingNumber;
	}

	public void setRatingNumber(Integer ratingNumber) {
		this.ratingNumber = ratingNumber;
	}

	public String getPresentDate() {
		return presentDate;
	}

	public void setPresentDate(String presentDate) {
		this.presentDate = presentDate;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getEdiotrialDashboard() {
		return ediotrialDashboard;
	}

	public void setEdiotrialDashboard(String ediotrialDashboard) {
		this.ediotrialDashboard = ediotrialDashboard;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getEmailTemplateForm() {
		return emailTemplateForm;
	}

	public void setEmailTemplateForm(String emailTemplateForm) {
		this.emailTemplateForm = emailTemplateForm;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getReviewerAssignDate() {
		return reviewerAssignDate;
	}

	public void setReviewerAssignDate(Date reviewerAssignDate) {
		this.reviewerAssignDate = reviewerAssignDate;
	}

	public Date getReviewerUnAssignDate() {
		return reviewerUnAssignDate;
	}

	public void setReviewerUnAssignDate(Date reviewerUnAssignDate) {
		this.reviewerUnAssignDate = reviewerUnAssignDate;
	}

	public Date getReviewerStartDate() {
		return reviewerStartDate;
	}

	public void setReviewerStartDate(Date reviewerStartDate) {
		this.reviewerStartDate = reviewerStartDate;
	}

	public Date getReviewerCompleteDate() {
		return reviewerCompleteDate;
	}

	public void setReviewerCompleteDate(Date reviewerCompleteDate) {
		this.reviewerCompleteDate = reviewerCompleteDate;
	}

	public Date getReviewerRejectDate() {
		return reviewerRejectDate;
	}

	public void setReviewerRejectDate(Date reviewerRejectDate) {
		this.reviewerRejectDate = reviewerRejectDate;
	}

	public Date getReviewerDueDate() {
		return reviewerDueDate;
	}

	public void setReviewerDueDate(Date reviewerDueDate) {
		this.reviewerDueDate = reviewerDueDate;
	}

	public Date getEditorAssignDate() {
		return editorAssignDate;
	}

	public void setEditorAssignDate(Date editorAssignDate) {
		this.editorAssignDate = editorAssignDate;
	}

	public Date getEditorDecisionDate() {
		return editorDecisionDate;
	}

	public void setEditorDecisionDate(Date editorDecisionDate) {
		this.editorDecisionDate = editorDecisionDate;
	}

	public Date getManagingEditorAssignDate() {
		return managingEditorAssignDate;
	}

	public void setManagingEditorAssignDate(Date managingEditorAssignDate) {
		this.managingEditorAssignDate = managingEditorAssignDate;
	}

	public Date getManagingEditoDecisionDate() {
		return managingEditoDecisionDate;
	}

	public void setManagingEditoDecisionDate(Date managingEditoDecisionDate) {
		this.managingEditoDecisionDate = managingEditoDecisionDate;
	}

	public String getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(String manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public Integer getIsReviewerActive() {
		return isReviewerActive;
	}

	public void setIsReviewerActive(Integer isReviewerActive) {
		this.isReviewerActive = isReviewerActive;
	}

	public String getFromMail() {
		return fromMail;
	}

	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}

	public String getToMail() {
		return toMail;
	}

	public void setToMail(String toMail) {
		this.toMail = toMail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Integer getPreviousUserId() {
		return previousUserId;
	}

	public void setPreviousUserId(Integer previousUserId) {
		this.previousUserId = previousUserId;
	}

	public List<Integer> getManuscriptStatusId() {
		return manuscriptStatusId;
	}

	public void setManuscriptStatusId(List<Integer> manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}

	public String getJournalMailTemplatesBody() {
		return journalMailTemplatesBody;
	}

	public void setJournalMailTemplatesBody(String journalMailTemplatesBody) {
		this.journalMailTemplatesBody = journalMailTemplatesBody;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIpEnd() {
		return ipEnd;
	}

	public void setIpEnd(String ipEnd) {
		this.ipEnd = ipEnd;
	}

	public String getDotStart() {
		return dotStart;
	}

	public void setDotStart(String dotStart) {
		this.dotStart = dotStart;
	}

	public String getDotEnd() {
		return dotEnd;
	}

	public void setDotEnd(String dotEnd) {
		this.dotEnd = dotEnd;
	}

	public String getCountryShort() {
		return countryShort;
	}

	public void setCountryShort(String countryShort) {
		this.countryShort = countryShort;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryType() {
		return countryType;
	}

	public void setCountryType(String countryType) {
		this.countryType = countryType;
	}

	public String getWaveOff() {
		return waveOff;
	}

	public void setWaveOff(String waveOff) {
		this.waveOff = waveOff;
	}


	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getExperties() {
		return experties;
	}

	public void setExperties(String experties) {
		this.experties = experties;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public Integer getManuscriptReviewerId() {
		return manuscriptReviewerId;
	}

	public void setManuscriptReviewerId(Integer manuscriptReviewerId) {
		this.manuscriptReviewerId = manuscriptReviewerId;
	}
	

	public Integer getEditor_id() {
		return editor_id;
	}

	public void setEditor_id(Integer editor_id) {
		this.editor_id = editor_id;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public String getAlphabet() {
		return alphabet;
	}

	public void setAlphabet(String alphabet) {
		this.alphabet = alphabet;
	}

	public String getJournalMailTemplatesSubject() {
		return journalMailTemplatesSubject;
	}

	public void setJournalMailTemplatesSubject(String journalMailTemplatesSubject) {
		this.journalMailTemplatesSubject = journalMailTemplatesSubject;
	}
	
	

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	@Override
	public String toString() {
		return "IDVO [userId=" + userId + ", roleId=" + roleId + ", isActive=" + isActive + ", city=" + city + ", id="
				+ id + ", ipAddress=" + ipAddress + ", ipEnd=" + ipEnd + ", dotStart=" + dotStart + ", dotEnd=" + dotEnd
				+ ", countryShort=" + countryShort + ", country=" + country + ", countryType=" + countryType
				+ ", comment=" + comment + ", emailTemplateForm=" + emailTemplateForm + ", journalId=" + journalId
				+ ", taskId=" + taskId + ", manuscriptStatus=" + manuscriptStatus + ", year=" + year
				+ ", manuscriptStatusId=" + manuscriptStatusId + ", journalIdList=" + journalIdList + ", authorIdList="
				+ authorIdList + ", feedback=" + feedback + ", version=" + version + ", yearList=" + yearList
				+ ", month=" + month + ", journalMailTemplatesBody=" + journalMailTemplatesBody
				+ ", journalMailTemplatesSubject=" + journalMailTemplatesSubject + ", name=" + name
				+ ", manuscriptCode=" + manuscriptCode + ", journalCode=" + journalCode + ", roleIdList=" + roleIdList
				+ ", policies=" + policies + ", createdBy=" + createdBy + ", token=" + token + ", email=" + email
				+ ", password=" + password + ", manuId=" + manuId + ", presentDate=" + presentDate
				+ ", isReviewerActive=" + isReviewerActive + ", ratingName=" + ratingName + ", ratingNumber="
				+ ratingNumber + ", manuscriptId=" + manuscriptId + ", manuscriptVersion=" + manuscriptVersion
				+ ", fileType=" + fileType + ", ediotrialDashboard=" + ediotrialDashboard + ", copyrightAgreement="
				+ copyrightAgreement + ", copyrightAgreementContent=" + copyrightAgreementContent
				+ ", reviewerAssignDate=" + reviewerAssignDate + ", reviewerUnAssignDate=" + reviewerUnAssignDate
				+ ", reviewerStartDate=" + reviewerStartDate + ", reviewerCompleteDate=" + reviewerCompleteDate
				+ ", reviewerRejectDate=" + reviewerRejectDate + ", editor_id=" + editor_id + ", editorAccessDetails="
				+ editorAccessDetails + ", folder=" + folder + ", reviewerDueDate=" + reviewerDueDate
				+ ", editorAssignDate=" + editorAssignDate + ", editorDecisionDate=" + editorDecisionDate
				+ ", managingEditorAssignDate=" + managingEditorAssignDate + ", managingEditoDecisionDate="
				+ managingEditoDecisionDate + ", fromMail=" + fromMail + ", toMail=" + toMail + ", subject=" + subject
				+ ", mailBody=" + mailBody + ", currentUserId=" + currentUserId + ", previousUserId=" + previousUserId
				+ ", countEditor=" + countEditor + ", countManagingEditor=" + countManagingEditor + ", countAuthor="
				+ countAuthor + ", countRefree=" + countRefree + ", salutation=" + salutation + ", firstName="
				+ firstName + ", middleName=" + middleName + ", lastName=" + lastName + ", waveOff=" + waveOff
				+ ", specialization=" + specialization + ", experties=" + experties + ", countryId=" + countryId
				+ ", manuscriptReviewerId=" + manuscriptReviewerId + ", pageNo=" + pageNo + ", limit=" + limit
				+ ", search=" + search + ", sortBy=" + sortBy + ", sort=" + sort + ", readOrcid=" + readOrcid
				+ ", alphabet=" + alphabet + "]";
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	
}