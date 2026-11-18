package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.DiscussionVo;
import com.digi.uniprr.VO.IDVO;

public interface DiscussionService {

	void queryToEditor(DiscussionVo discussionVo);

	List<DiscussionVo> getQueryById(IDVO id);

	List<DiscussionVo> getOutBox(IDVO id);

}
