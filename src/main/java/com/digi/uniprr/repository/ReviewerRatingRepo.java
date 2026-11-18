package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ReviewerRating;

public interface ReviewerRatingRepo extends JpaRepository<ReviewerRating, Integer>{

	@Query("SELECT r from ReviewerRating r where r.reviewerId=:reviewerId")
	List<ReviewerRating> getRatingByReviewerId(Integer reviewerId);
	
	@Query("SELECT r from ReviewerRating r where r.reviewerId IN :reviewerId")
	List<ReviewerRating> getRatingByReviewer(List<Integer> reviewerId);
	
	@Query("SELECT r from ReviewerRating r where r.reviewerId=:reviewerId AND r.manuscriptId=:manuscriptId AND r.manuscriptVersion=:manuscriptVersion")
	ReviewerRating getReviewerRatingOnManuscript(Integer reviewerId,Integer manuscriptId,Integer manuscriptVersion);

}
