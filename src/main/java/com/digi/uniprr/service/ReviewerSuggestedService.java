package com.digi.uniprr.service;

import com.digi.uniprr.VO.ReviewerSuggestedVO;
import com.digi.uniprr.VO.ReviewerSuggestionVO;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.User;

public interface ReviewerSuggestedService {

	public User getByEmail(String email);
	
	public ReviewerSuggested getByEmailId(String email);
	
	public String createUser(ReviewerSuggestionVO reviewerSuggestionVO);
	
	public String createReviewerSuggestedArray(ReviewerSuggestedVO reviewerSuggestedVO);

	public boolean isDeclineExist(ReviewerSuggestionVO reviewerSuggestionVO);
	public boolean isInviteExist(ReviewerSuggestionVO reviewerSuggestionVO);
	public boolean isAcceptExist(ReviewerSuggestionVO reviewerSuggestionVO);

	public String updateReviewerSuggested(ReviewerSuggestedVO reviewerSuggestedVO);

	public String deleteReviewerSuggested(ReviewerSuggestedVO reviewerSuggestedVO);

	public Long getReviewerSuggestionCount(Integer id, Integer versionId, Integer journalId);
	
	public String createReviewerOpposedArray(ReviewerSuggestedVO reviewerSuggestedVO);
	
	public String updateReviewerOpposed(ReviewerSuggestedVO reviewerSuggestedVO);

	public String deleteReviewerOpposed(ReviewerSuggestedVO reviewerSuggestedVO);
	
}
