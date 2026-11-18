package com.digi.uniprr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "manuscript_file_preview")
public class ManuscriptFilePreview {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "manuscript_id")
	private Integer manuscriptId;
	
	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;
	
	@Column(name = "manuscript_xml_path")
	private String manuscriptXmlpath;
	
	@Column(name = "manuscript_xhtml_path")
	private String manuscriptXhtmlPath;

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

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public String getManuscriptXmlpath() {
		return manuscriptXmlpath;
	}

	public void setManuscriptXmlpath(String manuscriptXmlpath) {
		this.manuscriptXmlpath = manuscriptXmlpath;
	}

	public String getManuscriptXhtmlPath() {
		return manuscriptXhtmlPath;
	}

	public void setManuscriptXhtmlPath(String manuscriptXhtmlPath) {
		this.manuscriptXhtmlPath = manuscriptXhtmlPath;
	}
	
}
