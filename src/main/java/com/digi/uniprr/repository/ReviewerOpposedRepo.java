package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ReviewerOpposed;

public interface ReviewerOpposedRepo extends JpaRepository<ReviewerOpposed, Integer> {
	
	@Query("SELECT u from ReviewerOpposed u where u.email=:email AND u.manuscriptId=:manuscriptId AND u.manuscriptVersion=:manuscriptVersion AND u.journalId=:journalId")
	ReviewerOpposed findByEmail(String email,Integer manuscriptId,Integer manuscriptVersion,Integer journalId);
	
	@Query("SELECT u FROM ReviewerOpposed u WHERE u.manuscriptId=:manuscriptId AND u.journalId=:journalId")
	List<ReviewerOpposed> getauthorOpposedReviewer(Integer manuscriptId,Integer journalId);
	@Query(value = "SELECT * FROM reviewer_opposed u WHERE manuscript_id=:manuscriptId AND journal_id=:journalId limit :limit offset :startIndex",nativeQuery = true)
	List<ReviewerOpposed> getauthorOpposedReviewerPagination(Integer manuscriptId,Integer journalId, Integer startIndex, Integer limit);
	@Query("SELECT count(*) FROM ReviewerOpposed u WHERE u.manuscriptId=:manuscriptId AND u.journalId=:journalId")
	Integer getauthorOpposedReviewerCount(Integer manuscriptId,Integer journalId);
}
