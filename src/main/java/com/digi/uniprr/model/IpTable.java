package com.digi.uniprr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "iptable")
public class IpTable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "ip_start")
	private String ipStart;
	
	
	@Column(name = "ip_end")
	private String ipEnd;
	
	@Column(name = "dot_start")
	private String dotStart;
	
	@Column(name = "dot_end")
	private String dotEnd;
	
	@Column(name = "country_short")
	private String countryShort;
	
	
	@Column(name = "country")
	private String country;
	
	
	@Column(name = "country_type")
	private String countryType;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getIpStart() {
		return ipStart;
	}


	public void setIpStart(String ipStart) {
		this.ipStart = ipStart;
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
	
	
}
