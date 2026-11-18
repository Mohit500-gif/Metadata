package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.User;

public interface ReviewerSuggestedRepo extends JpaRepository<ReviewerSuggested, Integer> {
	 
	@Query("SELECT u from User u where u.email=:email")
	User findByEmail(String email);

	@Query("SELECT u from ReviewerSuggested u where u.email=:email AND u.manuscriptId=:manuscriptId AND u.manuscriptVersion=:manuscriptVersion AND u.journalId=:journalId")
	ReviewerSuggested findByEmail(String email,Integer manuscriptId,Integer manuscriptVersion,Integer journalId);
	
	@Query("SELECT u from ReviewerSuggested u where u.email=:email")
	ReviewerSuggested findByEmailId(String email);
	
	@Query("SELECT u from ReviewerSuggested u where u.id=:id")
	ReviewerSuggested findBySuggestedReviewerId(Integer id);

	@Query("SELECT u FROM ReviewerSuggested u WHERE u.manuscriptId=:manuscriptId AND u.journalId=:journalId")
	List<ReviewerSuggested>   getauthorSuggestedReviewer(Integer manuscriptId,Integer journalId);
	@Query(value = "SELECT * FROM reviewer_suggested WHERE manuscript_id=:manuscriptId AND journal_id=:journalId limit :limit offset :startIndex",nativeQuery = true)
	List<ReviewerSuggested>   getauthorSuggestedReviewerPagination(Integer manuscriptId,Integer journalId, Integer startIndex, Integer limit);
	@Query("SELECT u FROM ReviewerSuggested u WHERE u.manuscriptId=:manuscriptId AND u.journalId=:journalId")
	Integer getauthorSuggestedReviewerCount(Integer manuscriptId,Integer journalId);

	@Query("SELECT u FROM ReviewerSuggested u WHERE u.manuscriptId=:manuscriptId AND u.journalId=:journalId")
	List<ReviewerSuggested>   getAuthorReviewerSuggested(Integer manuscriptId,Integer journalId);

	// Added by Utkarsh
	@Query("SELECT u from ReviewerSuggested u where u.email=:email")
	List<ReviewerSuggested> findByEmailIdList(String email);
}
