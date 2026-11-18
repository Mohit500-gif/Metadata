package com.digi.uniprr.VO;

import java.util.List;

public class ManuScriptReviewerInvitationVO {
	private List<ManuScriptReviewerCommonVO> manuScriptReviewerCommonVO;
	private int totalCount;
	
	public List<ManuScriptReviewerCommonVO> getManuScriptReviewerCommonVO() {
		return manuScriptReviewerCommonVO;
	}
	public void setManuScriptReviewerCommonVO(List<ManuScriptReviewerCommonVO> manuScriptReviewerCommonVO) {
		this.manuScriptReviewerCommonVO = manuScriptReviewerCommonVO;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	
}
