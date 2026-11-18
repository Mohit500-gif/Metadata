package com.digi.uniprr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "go_live")
public class GoLive {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer liveId;
	
	@Column(name = "live_status")
	private Integer liveStatus;
	
	@Column(name="live_long_message")
	private String liveLongMessage;
	
	@Column(name="live_short_message")
	private String liveShortMessage;
	
	@Column(name="live_down_time")
	private String liveDownTime;
	
	@Column(name="up_time")	
	private String upTime;

	public Integer getLiveStatus() {
		return liveStatus;
	}

	public void setLiveStatus(Integer liveStatus) {
		this.liveStatus = liveStatus;
	}

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

	public String getLiveDownTime() {
		return liveDownTime;
	}

	public void setLiveDownTime(String liveDownTime) {
		this.liveDownTime = liveDownTime;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}
	

}
