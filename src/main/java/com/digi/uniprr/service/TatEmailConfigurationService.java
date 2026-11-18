package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.EmailTriggerVO;

public interface TatEmailConfigurationService {

	void create(EmailTriggerVO emailTriggerVO);

	String extendTat(List<EmailTriggerVO> emailTriggerVO);
	
	String extendDueDateArticleStage(EmailTriggerVO emailTriggerVO);

	String manuscriptsForReviewAndRereview(EmailTriggerVO emailTriggerVO);

}
