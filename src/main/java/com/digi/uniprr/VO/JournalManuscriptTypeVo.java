package com.digi.uniprr.VO;

import java.util.Date;

public class JournalManuscriptTypeVo {
	private Integer mtjId;

	private String manutypeName;

	private Integer journalId;

	private Integer manuTypeOrder;

	private Integer isAbstract;

	private Integer isActive;

	private Date createdOn;

	private Integer createdBy;

	private Date updatedOn;

	private Integer updatedBy;

	public Integer getMtjId() {
		return mtjId;
	}

	public void setMtjId(Integer mtjId) {
		this.mtjId = mtjId;
	}

	public String getManutypeName() {
		return manutypeName;
	}

	public void setManutypeName(String manutypeName) {
		this.manutypeName = manutypeName;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getManuTypeOrder() {
		return manuTypeOrder;
	}

	public void setManuTypeOrder(Integer manuTypeOrder) {
		this.manuTypeOrder = manuTypeOrder;
	}

	public Integer getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(Integer isAbstract) {
		this.isAbstract = isAbstract;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
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

}
