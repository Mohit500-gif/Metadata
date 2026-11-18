package com.digi.uniprr.VO;

import org.springframework.web.multipart.MultipartFile;

public class UserBulkUploadDataVo {

	private Integer journalId;
	private Integer roleId;
	private Integer editorRoleId;
	private MultipartFile xlsx;

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public MultipartFile getXlsx() {
		return xlsx;
	}

	public void setXlsx(MultipartFile xlsx) {
		this.xlsx = xlsx;
	}

	public Integer getEditorRoleId() {
		return editorRoleId;
	}

	public void setEditorRoleId(Integer editorRoleId) {
		this.editorRoleId = editorRoleId;
	}

}
