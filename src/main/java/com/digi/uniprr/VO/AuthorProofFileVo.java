package com.digi.uniprr.VO;

import java.io.Serializable;
public class AuthorProofFileVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private String filePath;
	private String fileCreatedDate;
	
	
	public AuthorProofFileVo() {};
	public AuthorProofFileVo(String fileName, String filePath, String fileCreatedDate) {
		super();
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileCreatedDate = fileCreatedDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileCreatedDate() {
		return fileCreatedDate;
	}
	public void setFileCreatedDate(String fileCreatedDate) {
		this.fileCreatedDate = fileCreatedDate;
	}
	@Override
	public String toString() {
		return "AuthorProoFileVo [fileName=" + fileName + ", filePath=" + filePath + ", fileCreatedDate="
				+ fileCreatedDate + "]";
	}
}