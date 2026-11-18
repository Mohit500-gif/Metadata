package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ExpertiseVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer journalId;
	private String areaExpertise;
	private Date createdOn;
	private Integer createdBy;
	private Date updatedOn;
	private Integer updatedBy;
	private List<Integer> specialization;
	
	public List<Integer> getSpecialization() {
		return specialization;
	}

	public void setSpecialization(List<Integer> specialization) {
		this.specialization = specialization;
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

	public String getAreaExpertise() {
		return areaExpertise;
	}

	public void setAreaExpertise(String areaExpertise) {
		this.areaExpertise = areaExpertise;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "ExpertiseVO [id=" + id + ", journalId=" + journalId + ", areaExpertise=" + areaExpertise
				+ ", createdOn=" + createdOn + ", createdBy=" + createdBy + ", updatedOn=" + updatedOn + ", updatedBy="
				+ updatedBy + ", specialization=" + specialization + "]";
	}

}
