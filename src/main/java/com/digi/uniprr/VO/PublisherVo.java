package com.digi.uniprr.VO;

public class PublisherVo {

	private Integer publisherId;
	private String publisherCode;
	private String publisherName;
	private String publisherSite;
	private String publisherAddress;
	private String publisherContactEmailId;
	private String publisherContactNumber;
	private String publisherLogo;
	private String publisherType;
	private String publishingType;
	private Integer createdBy;
	private Integer updatedBy;

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

	public String getPublisherAddress() {
		return publisherAddress;
	}

	public void setPublisherAddress(String publisherAddress) {
		this.publisherAddress = publisherAddress;
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

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	

	public String getPublisherContactNumber() {
		return publisherContactNumber;
	}

	public void setPublisherContactNumber(String publisherContactNumber) {
		this.publisherContactNumber = publisherContactNumber;
	}

	@Override
	public String toString() {
		return "PublisherVo [publisherId=" + publisherId + ", publisherCode=" + publisherCode + ", publisherName="
				+ publisherName + ", publisherSite=" + publisherSite + ", publisherAddress=" + publisherAddress
				+ ", publisherContactEmailId=" + publisherContactEmailId + ", publisherContactNumber="
				+ publisherContactNumber + ", publisherLogo=" + publisherLogo + ", publisherType=" + publisherType
				+ ", publishingType=" + publishingType + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + "]";
	}
	
}
