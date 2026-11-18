package com.digi.uniprr.VO;

public class JournalBasedCheckListVO {

	private AllCheckListVO masterLevelCheckList;
	private AllCheckListVO journalLevelCheckList;
	public AllCheckListVO getMasterLevelCheckList() {
		return masterLevelCheckList;
	}
	public void setMasterLevelCheckList(AllCheckListVO masterLevelCheckList) {
		this.masterLevelCheckList = masterLevelCheckList;
	}
	public AllCheckListVO getJournalLevelCheckList() {
		return journalLevelCheckList;
	}
	public void setJournalLevelCheckList(AllCheckListVO journalLevelCheckList) {
		this.journalLevelCheckList = journalLevelCheckList;
	}
	
}
