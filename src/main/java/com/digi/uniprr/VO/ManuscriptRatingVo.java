package com.digi.uniprr.VO;

public class ManuscriptRatingVo {

	private Integer id;

	private Integer manuscriptId;

	private Integer manusciptVersion;

	private Integer journalId;

	private Integer rating;

	private String ratingComments;

	private String thumbsRating;

	private Integer userId;

	private Integer roleId;

	private Integer createdBy;

	private Integer updatedBy;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getManusciptVersion() {
		return manusciptVersion;
	}

	public void setManusciptVersion(Integer manusciptVersion) {
		this.manusciptVersion = manusciptVersion;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getRatingComments() {
		return ratingComments;
	}

	public void setRatingComments(String ratingComments) {
		this.ratingComments = ratingComments;
	}

	public String getThumbsRating() {
		return thumbsRating;
	}

	public void setThumbsRating(String thumbsRating) {
		this.thumbsRating = thumbsRating;
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

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

}
