package com.digi.uniprr.service;

import com.digi.uniprr.VO.AuthorFeedbackVO;
import com.digi.uniprr.VO.IDVO;

public interface AuthorFeedbackService {

	public void saveAuthorFeedback(AuthorFeedbackVO authorFeedbackVO);

	public void saveAuthorFeedback(IDVO id);
}
