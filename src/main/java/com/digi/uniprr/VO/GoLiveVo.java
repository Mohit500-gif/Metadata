package com.digi.uniprr.VO;

public class GoLiveVo {
	
	private String liveLongMessage;
	private String liveShortMessage;
	private int liveStatus;
	private String startDownTime;
	private String upTime;

	public String getLiveLongMessage() {
		return liveLongMessage;
	}
	public void setLiveLongMessage(String liveLongMessage) {
		this.liveLongMessage = liveLongMessage;
	}
	public String getLiveShortMessage() {
		return liveShortMessage;
	}
	public void setLiveShortMessage(String liveShortMessage) {
		this.liveShortMessage = liveShortMessage;
	}
	public int getLiveStatus() {
		return liveStatus;
	}
	public void setLiveStatus(int liveStatus) {
		this.liveStatus = liveStatus;
	}
	public String getStartDownTime() {
		return startDownTime;
	}
	public void setStartDownTime(String startDownTime) {
		this.startDownTime = startDownTime;
	}
	public String getUpTime() {
		return upTime;
	}
	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}
	@Override
	public String toString() {
		return "GoLiveVo [liveLongMessage=" + liveLongMessage + ", liveShortMessage=" + liveShortMessage
				+ ", liveStatus=" + liveStatus + ", startDownTime=" + startDownTime + ", upTime=" + upTime + "]";
	}

	

}
