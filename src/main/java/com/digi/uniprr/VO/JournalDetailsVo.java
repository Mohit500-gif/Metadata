package com.digi.uniprr.VO;

import java.util.List;

import com.digi.uniprr.model.UserJournal;

public class JournalDetailsVo {

	private String country;

	@Override
	public String toString() {
		return "JournalDetailsVo{" +
				"country='" + country + '\'' +
				", countOfOriginalSubmission=" + countOfOriginalSubmission +
				", totalZeroVersion=" + totalZeroVersion +
				", totalResubmission=" + totalResubmission +
				", journalTitle='" + journalTitle + '\'' +
				", journalCode='" + journalCode + '\'' +
				", journalId=" + journalId +
				", totalManuscript=" + totalManuscript +
				", totalManagingEditor=" + totalManagingEditor +
				", totalEditor=" + totalEditor +
				", managingeditor=" + managingeditor +
				", editor=" + editor +
				", pageNo=" + pageNo +
				", limit=" + limit +
				", search='" + search + '\'' +
				", sortBy='" + sortBy + '\'' +
				", sort='" + sort + '\'' +
				", inReviewManuscript=" + inReviewManuscript +
				", acceptedManuscript=" + acceptedManuscript +
				", rejectedManuscript=" + rejectedManuscript +
				'}';
	}

	private Integer countOfOriginalSubmission;
	private Integer totalZeroVersion;
	private Integer totalResubmission;
	
	private String journalTitle;
	
	private String journalCode;
	
	private Integer journalId;
	
	private Integer totalManuscript;
	
	private List<UserVO> totalManagingEditor;
	
	private List<UserVO> totalEditor;
	
	private Integer managingeditor;
	
	private Integer editor;
	
	private Integer pageNo;
	private Integer limit;
	private String search;
	private String sortBy;
	private String sort;
	
	
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
	public Integer getManagingeditor() {
		return managingeditor;
	}
	public void setManagingeditor(Integer managingeditor) {
		this.managingeditor = managingeditor;
	}
	public Integer getEditor() {
		return editor;
	}
	public void setEditor(Integer editor) {
		this.editor = editor;
	}
	private Integer inReviewManuscript;
	
	private Integer acceptedManuscript;
	
	private Integer rejectedManuscript;	
	
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Integer getCountOfOriginalSubmission() {
		return countOfOriginalSubmission;
	}
	public void setCountOfOriginalSubmission(Integer countOfOriginalSubmission) {
		this.countOfOriginalSubmission = countOfOriginalSubmission;
	}
	public Integer getTotalZeroVersion() {
		return totalZeroVersion;
	}
	public void setTotalZeroVersion(Integer totalZeroVersion) {
		this.totalZeroVersion = totalZeroVersion;
	}
	public Integer getTotalResubmission() {
		return totalResubmission;
	}
	public void setTotalResubmission(Integer totalResubmission) {
		this.totalResubmission = totalResubmission;
	}
	public String getJournalCode() {
		return journalCode;
	}
	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}
	public Integer getTotalManuscript() {
		return totalManuscript;
	}
	public void setTotalManuscript(Integer totalManuscript) {
		this.totalManuscript = totalManuscript;
	}
	
	public List<UserVO> getTotalManagingEditor() {
		return totalManagingEditor;
	}
	public void setTotalManagingEditor(List<UserVO> totalManagingEditor) {
		this.totalManagingEditor = totalManagingEditor;
	}
	public List<UserVO> getTotalEditor() {
		return totalEditor;
	}
	public void setTotalEditor(List<UserVO> totalEditor) {
		this.totalEditor = totalEditor;
	}
	public Integer getInReviewManuscript() {
		return inReviewManuscript;
	}
	public void setInReviewManuscript(Integer inReviewManuscript) {
		this.inReviewManuscript = inReviewManuscript;
	}
	public Integer getAcceptedManuscript() {
		return acceptedManuscript;
	}
	public void setAcceptedManuscript(Integer acceptedManuscript) {
		this.acceptedManuscript = acceptedManuscript;
	}
	public Integer getRejectedManuscript() {
		return rejectedManuscript;
	}
	public void setRejectedManuscript(Integer rejectedManuscript) {
		this.rejectedManuscript = rejectedManuscript;
	}
	public String getJournalTitle() {
		return journalTitle;
	}
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}
}
