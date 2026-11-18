package com.digi.uniprr.service;
import java.util.List;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.User;


public interface ForgotPassword {
	
	public String sendMailForPassword(String userEmail, String token,String userName,String forgetPasswordUrl,Integer journalId,Integer userId);
	
	public List<User> findUserDetailsByToken(String token);
	
	public void saveUpdatedPassword(String token, String password);

	public String getResetPasswordLink(IDVO idvo);
	
}
