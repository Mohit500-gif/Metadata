package com.digi.uniprr.service;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;

public interface EmailTriggerService {

	public String emailTrigger(EmailTriggerVO emailTriggerVO);

	public String sendContactSupportMail(IDVO idvo);

	public String resendReviewerInvitationMail(ManuscriptListVO manuscriptListVO);

	public String emailTriggerForReminder(EmailTriggerVO emailTriggerVO);
	
	public String sendOrcidAuthEmail(IDVO idvo);
}
