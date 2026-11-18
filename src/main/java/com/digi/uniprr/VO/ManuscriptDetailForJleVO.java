package com.digi.uniprr.VO;

public class ManuscriptDetailForJleVO {

	private Integer manuscript;
	private Integer mansucriptVersion;
	private String ManuscriptID;
	private String JID;  
	private String AID;
	private String AuthorName;  
	private String DocHead;
	private String Volume; 
	private String Issue;
	private String NoOfPages; 
	private String NoOfFigures;  
	private String TAT;
	private String Copyediting;  
	private String PublishingYear;   
	private String Remark;
	private String FilePath;
	private String copyeditingType;
	
	
	
	public String getCopyeditingType() {
		return copyeditingType;
	}
	public void setCopyeditingType(String copyeditingType) {
		this.copyeditingType = copyeditingType;
	}
	public Integer getManuscript() {
		return manuscript;
	}
	public void setManuscript(Integer manuscript) {
		this.manuscript = manuscript;
	}
	public Integer getMansucriptVersion() {
		return mansucriptVersion;
	}
	public void setMansucriptVersion(Integer mansucriptVersion) {
		this.mansucriptVersion = mansucriptVersion;
	}
	public String getManuscriptID() {
		return ManuscriptID;
	}
	public void setManuscriptID(String manuscriptID) {
		ManuscriptID = manuscriptID;
	}
	public String getJID() {
		return JID;
	}
	public void setJID(String jID) {
		JID = jID;
	}
	public String getAID() {
		return AID;
	}
	public void setAID(String aID) {
		AID = aID;
	}
	public String getAuthorName() {
		return AuthorName;
	}
	public void setAuthorName(String authorName) {
		AuthorName = authorName;
	}
	public String getDocHead() {
		return DocHead;
	}
	public void setDocHead(String docHead) {
		DocHead = docHead;
	}
	public String getVolume() {
		return Volume;
	}
	public void setVolume(String volume) {
		Volume = volume;
	}
	public String getIssue() {
		return Issue;
	}
	public void setIssue(String issue) {
		Issue = issue;
	}
	public String getNoOfPages() {
		return NoOfPages;
	}
	public void setNoOfPages(String noOfPages) {
		NoOfPages = noOfPages;
	}
	public String getNoOfFigures() {
		return NoOfFigures;
	}
	public void setNoOfFigures(String noOfFigures) {
		NoOfFigures = noOfFigures;
	}
	public String getTAT() {
		return TAT;
	}
	public void setTAT(String tAT) {
		TAT = tAT;
	}
	public String getCopyediting() {
		return Copyediting;
	}
	public void setCopyediting(String copyediting) {
		Copyediting = copyediting;
	}
	public String getPublishingYear() {
		return PublishingYear;
	}
	public void setPublishingYear(String publishingYear) {
		PublishingYear = publishingYear;
	}
	public String getRemark() {
		return Remark;
	}
	public void setRemark(String remark) {
		Remark = remark;
	}
	public String getFilePath() {
		return FilePath;
	}
	public void setFilePath(String filePath) {
		FilePath = filePath;
	}
	
}
