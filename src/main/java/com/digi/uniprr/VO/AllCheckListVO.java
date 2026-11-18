package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;

public class AllCheckListVO implements Serializable {

	private static final long serialVersionUID = 1L;

	List<CheckListMasterSimpleVO> managingEditorCheckList;
	List<CheckListMasterSimpleVO> editorCheckList;
	List<CheckListMasterSimpleVO> reviewerCheckList;
	List<CheckListMasterSimpleVO> authorCheckList;

	public List<CheckListMasterSimpleVO> getManagingEditorCheckList() {
		return managingEditorCheckList;
	}

	public void setManagingEditorCheckList(List<CheckListMasterSimpleVO> managingEditorCheckList) {
		this.managingEditorCheckList = managingEditorCheckList;
	}

	public List<CheckListMasterSimpleVO> getEditorCheckList() {
		return editorCheckList;
	}

	public void setEditorCheckList(List<CheckListMasterSimpleVO> editorCheckList) {
		this.editorCheckList = editorCheckList;
	}

	public List<CheckListMasterSimpleVO> getReviewerCheckList() {
		return reviewerCheckList;
	}

	public void setReviewerCheckList(List<CheckListMasterSimpleVO> reviewerCheckList) {
		this.reviewerCheckList = reviewerCheckList;
	}

	public List<CheckListMasterSimpleVO> getAuthorCheckList() {
		return authorCheckList;
	}

	public void setAuthorCheckList(List<CheckListMasterSimpleVO> authorCheckList) {
		this.authorCheckList = authorCheckList;
	}

}
