package com.digi.uniprr.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "publishers")
public class Publishers {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "publisher_id")
	private Integer publisherId;

	@Column(name = "publisher_code")
	private String publisherCode;

	@Column(name = "publisher_name")
	private String publisherName;

	@Column(name = "publisher_site")
	private String publisherSite;
	
	@Column(name = "publishers_address")
	private String publishersAddress;

	@Column(name = "publisher_contact_email_id")
	private String publisherContactEmailId;
	
	@Column(name = "publisher_contact_number")
	private String publisherContactNumber;
	
	@Column(name = "publisher_logo")
	private String publisherLogo;
	
	@Column(name = "publisher_type")
	private String publisherType;
	
	@Column(name = "publishing_type")
	private String publishingType;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "created_on")
	@CreationTimestamp
	private Date createdOn;
	
	@Column(name = "updated_by")
	private Integer updatedBy;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	public Integer getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Integer publisherId) {
		this.publisherId = publisherId;
	}

	public String getPublisherCode() {
		return publisherCode;
	}

	public void setPublisherCode(String publisherCode) {
		this.publisherCode = publisherCode;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getPublisherSite() {
		return publisherSite;
	}

	public void setPublisherSite(String publisherSite) {
		this.publisherSite = publisherSite;
	}

	public String getPublishersAddress() {
		return publishersAddress;
	}

	public void setPublishersAddress(String publishersAddress) {
		this.publishersAddress = publishersAddress;
	}

	public String getPublisherContactEmailId() {
		return publisherContactEmailId;
	}

	public void setPublisherContactEmailId(String publisherContactEmailId) {
		this.publisherContactEmailId = publisherContactEmailId;
	}

	public String getPublisherLogo() {
		return publisherLogo;
	}

	public void setPublisherLogo(String publisherLogo) {
		this.publisherLogo = publisherLogo;
	}

	public String getPublisherType() {
		return publisherType;
	}

	public void setPublisherType(String publisherType) {
		this.publisherType = publisherType;
	}

	public String getPublishingType() {
		return publishingType;
	}

	public void setPublishingType(String publishingType) {
		this.publishingType = publishingType;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getPublisherContactNumber() {
		return publisherContactNumber;
	}

	public void setPublisherContactNumber(String publisherContactNumber) {
		this.publisherContactNumber = publisherContactNumber;
	}

	
	
}
