package com.digi.uniprr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "impact_urls")
@JsonIgnoreProperties({"id"})
public class ImpactUrl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String role;
	private String link;

	@ManyToOne
	@JoinColumn(name = "impact_request_doi", nullable = false)
	@JsonIgnore
	private ImpactRequest impactRequest;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public ImpactRequest getImpactRequest() {
		return impactRequest;
	}

	public void setImpactRequest(ImpactRequest impactRequest) {
		this.impactRequest = impactRequest;
	}

}