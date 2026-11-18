package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.AuthorFeedbackVO;
import com.digi.uniprr.VO.FeedbackQuestionMasterVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;

public interface FeedbackQuestionMasterService {

	List<FeedbackQuestionMasterVO> getFeedbackList();

	List<FeedbackQuestionMasterVO> getFeedbackListById(Integer id);
	
	List<ManuscriptListVO> getCheckListById(Integer id,Integer manuscriptid);

	void createQuestions(List<FeedbackQuestionMasterVO> feedbackQuestionMasterVO);

	void updateeQuestions(FeedbackQuestionMasterVO feedbackQuestionMasterVO);

	void deleteQuestions(FeedbackQuestionMasterVO feedbackQuestionMasterVO);

	List<FeedbackQuestionMasterVO> geMasterList();

	List<FeedbackQuestionMasterVO> listByJournalAndRole(IDVO id);

	List<FeedbackQuestionMasterVO> getQuestionByManuscriptType(IDVO id);

	List<AuthorFeedbackVO> getAuthorFeedback(IDVO id);

}
