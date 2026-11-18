package com.digi.uniprr.VO;

import java.util.Date;

import javax.persistence.Column;

import com.digi.uniprr.model.ManuScript;

public class ManuscriptFileVO {
	
	private Integer manuscriptFileId;
	
	private ManuScript manuScript;
	
	private Integer fileNumber;
	
	private Integer manuScriptId;
	
	private Integer manuscriptVersion;
	
	private String fileType;
	
	private String fileName;
	
	private Integer fileOrder;
	
	private String filePath;
	
	private String contentType;
	
	private Integer fileSize;
	
	private String fileStatus;
	
	private Integer fileVersion;
	
    private String legend;
	
	private Date createdOn;
	
	private Integer createdBy;
	
	private Date updatedOn;
	
	private Integer updatedBy;
	
	private String parlagiarismURL;
	private int  parlagiarismIsActive;

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

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

	public void setFileSize(int fileSizeInKB) {
		this.fileSize = fileSizeInKB;
	}

	public Integer getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
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

	public String getParlagiarismURL() {
		return parlagiarismURL;
	}

	public void setParlagiarismURL(String parlagiarismURL) {
		this.parlagiarismURL = parlagiarismURL;
	}

	public int getParlagiarismIsActive() {
		return parlagiarismIsActive;
	}

	public void setParlagiarismIsActive(int parlagiarismIsActive) {
		this.parlagiarismIsActive = parlagiarismIsActive;
	}
	

}
