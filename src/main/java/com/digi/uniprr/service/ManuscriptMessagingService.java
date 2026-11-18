package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.DiscussionVo;
import com.digi.uniprr.VO.ManuscriptMessagingVO;
import com.digi.uniprr.model.Discussion;
import com.digi.uniprr.model.ManuscriptMessaging;

public interface ManuscriptMessagingService {

	ManuscriptMessaging sendMessage(ManuscriptMessagingVO manuscriptMessagingVO);
	
	List<ManuscriptMessagingVO> getMessage(ManuscriptMessagingVO manuscriptMessagingVO);

	List<DiscussionVo> getDiscussion(DiscussionVo discussionVo);

	String discussion(DiscussionVo discussionVo);

	String deleteDiscussion(DiscussionVo discussionVo);

	List<Discussion> getDataByJournalAndRole(ManuscriptMessagingVO manuscriptMessagingVO);

	List<ManuscriptMessaging> getDataByManuscript(ManuscriptMessagingVO manuscriptMessagingVO);
	
}
