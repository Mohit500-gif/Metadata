package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.model.ReviewerRating;

public interface ReviewerRatingService {

	void reviewerRatingService(List<ReviewerRating> reviewerRating);

	List<ReviewerRating> getReviewerRating(ReviewerRating reviewerRating);
}
