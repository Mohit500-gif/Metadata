package com.digi.uniprr.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "impact_requests")
public class ImpactRequest {

	@Id
	private String doi;

	private String partner;
	private String event;
	private String fileName;
	private String state;
	private int status;
	private String message;

	@OneToMany(mappedBy = "impactRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ImpactUrl> urls;

	public ImpactRequest() {
    }
	
	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ImpactUrl> getUrls() {
		return urls;
	}

	public void setUrls(List<ImpactUrl> urls) {
		this.urls = urls;
	}

	public ImpactRequest(String doi, String partner, String event, String fileName, String state, int status,
			String message, List<ImpactUrl> urls) {
		this.doi = doi;
		this.partner = partner;
		this.event = event;
		this.fileName = fileName;
		this.state = state;
		this.status = status;
		this.message = message;
		this.urls = urls;
	}


	
}