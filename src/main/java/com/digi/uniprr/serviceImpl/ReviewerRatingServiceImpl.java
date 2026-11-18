package com.digi.uniprr.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.ReviewerRating;
import com.digi.uniprr.repository.ReviewerRatingRepo;
import com.digi.uniprr.service.ReviewerRatingService;

@Service
@Transactional
public class ReviewerRatingServiceImpl implements ReviewerRatingService {

	@Autowired
	ReviewerRatingRepo reviewerRatingRepo;

	@Override
	public void reviewerRatingService(List<ReviewerRating> reviewerRating) {
		reviewerRatingRepo.saveAll(reviewerRating);
	}

	@Override
	public List<ReviewerRating> getReviewerRating(ReviewerRating reviewerRating) {
		return reviewerRatingRepo.getRatingByReviewerId(reviewerRating.getReviewerId());
	}
	
}
