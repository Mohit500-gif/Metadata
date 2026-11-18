package com.digi.uniprr.model;

import java.util.Date;

//import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "manuscript_file")
public class ManuScriptFile {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "manuscript_file_id", unique = true, nullable = false)
	private Integer manuscriptFileId;

	@OneToOne
	@JoinColumn(name = "manuscript_id", updatable = false, insertable = false)
	private ManuScript manuScript;

	@Column(name = "manuscript_id")
	private Integer manuScriptId;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "file_number")
	private Integer fileNumber;

	@Column(name = "file_name")
	private String fileName;
	
	@Column(name="file_active")
	private Integer fileActive;

	@Column(name = "file_status")
	private String fileStatus;

	@Column(name = "file_order")
	private Integer fileOrder;

	@Column(name = "file_path")
	private String filePath;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "file_size")
	private Integer fileSize;

	@Column(name = "file_version")
	private Integer fileVersion;

	@Column(name = "legend")
	private String legend;

	@Column(name = "is_active")
	private Integer isActive;

	@Column(name = "created_on")
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name = "updated_by")
	private Integer updatedBy;

	public Integer getManuscriptFileId() {
		return manuscriptFileId;
	}

	public void setManuscriptFileId(Integer manuscriptFileId) {
		this.manuscriptFileId = manuscriptFileId;
	}

	public ManuScript getManuScript() {
		return manuScript;
	}

	public void setManuScript(ManuScript manuScript) {
		this.manuScript = manuScript;
	}

	public Integer getManuScriptId() {
		return manuScriptId;
	}

	public void setManuScriptId(Integer manuScriptId) {
		this.manuScriptId = manuScriptId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getFileOrder() {
		return fileOrder;
	}

	public void setFileOrder(Integer fileOrder) {
		this.fileOrder = fileOrder;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public Integer getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
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

	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public Integer getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(Integer fileNumber) {
		this.fileNumber = fileNumber;
	}

	public Integer getFileActive() {
		return fileActive;
	}

	public void setFileActive(Integer fileActive) {
		this.fileActive = fileActive;
	}

	@Override
	public String toString() {
		return "ManuScriptFile [manuscriptFileId=" + manuscriptFileId + ", manuScript=" + manuScript + ", manuScriptId="
				+ manuScriptId + ", fileType=" + fileType + ", fileNumber=" + fileNumber + ", fileName=" + fileName
				+ ", fileActive=" + fileActive + ", fileStatus=" + fileStatus + ", fileOrder=" + fileOrder
				+ ", filePath=" + filePath + ", contentType=" + contentType + ", fileSize=" + fileSize
				+ ", fileVersion=" + fileVersion + ", legend=" + legend + ", isActive=" + isActive + ", createdOn="
				+ createdOn + ", createdBy=" + createdBy + ", updatedOn=" + updatedOn + ", updatedBy=" + updatedBy
				+ "]";
	}
}