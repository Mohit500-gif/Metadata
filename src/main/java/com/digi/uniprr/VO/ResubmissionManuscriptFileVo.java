package com.digi.uniprr.VO;

import java.util.List;

public class ResubmissionManuscriptFileVo {

	private List<ManuscriptFileVO> manuscriptFileVO;
	
	private Integer manuScriptId;
	
	private Integer manuscriptVersion;
	
	private String submissionType;
	
	private String manuscriptChecklistResponse;

	public List<ManuscriptFileVO> getManuscriptFileVO() {
		return manuscriptFileVO;
	}

	public void setManuscriptFileVO(List<ManuscriptFileVO> manuscriptFileVO) {
		this.manuscriptFileVO = manuscriptFileVO;
	}

	public Integer getManuScriptId() {
		return manuScriptId;
	}

	public void setManuScriptId(Integer manuScriptId) {
		this.manuScriptId = manuScriptId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public String getSubmissionType() {
		return submissionType;
	}

	public void setSubmissionType(String submissionType) {
		this.submissionType = submissionType;
	}

	public String getManuscriptChecklistResponse() {
		return manuscriptChecklistResponse;
	}

	public void setManuscriptChecklistResponse(String manuscriptChecklistResponse) {
		this.manuscriptChecklistResponse = manuscriptChecklistResponse;
	}


	
}
