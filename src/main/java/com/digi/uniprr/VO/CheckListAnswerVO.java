package com.digi.uniprr.VO;

import java.io.Serializable;

public class CheckListAnswerVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String chkKey;
	private String chkRule;
	public String getChkKey() {
		return chkKey;
	}
	public void setChkKey(String chkKey) {
		this.chkKey = chkKey;
	}
	public String getChkRule() {
		return chkRule;
	}
	public void setChkRule(String chkRule) {
		this.chkRule = chkRule;
	}
	@Override
	public String toString() {
		return "CheckListAnswerVO [chkKey=" + chkKey + ", chkRule=" + chkRule + "]";
	}
	
	
}
