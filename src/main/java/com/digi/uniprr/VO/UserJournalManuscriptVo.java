package com.digi.uniprr.VO;

import java.util.List;

public class UserJournalManuscriptVo {
	@Override
	public String toString() {
		return "UserJournalManuscriptVo{" +
				"manuscriptStatusId=" + manuscriptStatusId +
				", journalId=" + journalId +
				", userId=" + userId +
				", roleId=" + roleId +
				", pageNo=" + pageNo +
				", limit=" + limit +
				", search='" + search + '\'' +
				", sortBy='" + sortBy + '\'' +
				", sort='" + sort + '\'' +
				'}';
	}

	private List<Integer> manuscriptStatusId;
	private Integer journalId;
	private Integer userId;
	private Integer roleId;
	
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
	public List<Integer> getManuscriptStatusId() {
		return manuscriptStatusId;
	}
	public void setManuscriptStatusId(List<Integer> manuscriptStatusId) {
		this.manuscriptStatusId = manuscriptStatusId;
	}
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
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
	
}
