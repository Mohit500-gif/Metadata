package com.digi.uniprr.VO;

import java.util.List;

public class PresentStatusVO {
	
	public List<LoggedUserVo> loginUser;
	public List<LoggedUserVo> workingProgressStatus;
	public List<LoggedUserVo> showingResultOfyear;
	public List<LoggedUserVo> lastLogin;
	
	public List<LoggedUserVo> getLoginUser() {
		return loginUser;
	}
	public void setLoginUser(List<LoggedUserVo> loginUser) {
		this.loginUser = loginUser;
	}
	public List<LoggedUserVo> getWorkingProgressStatus() {
		return workingProgressStatus;
	}
	public void setWorkingProgressAStatus(List<LoggedUserVo> workingProgressStatus) {
		this.workingProgressStatus = workingProgressStatus;
	}
	public List<LoggedUserVo> getShowingResultOfyear() {
		return showingResultOfyear;
	}
	public void setShowingResultOfyear(List<LoggedUserVo> showingResultOfyear) {
		this.showingResultOfyear = showingResultOfyear;
	}
	public List<LoggedUserVo> getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(List<LoggedUserVo> lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	

}
