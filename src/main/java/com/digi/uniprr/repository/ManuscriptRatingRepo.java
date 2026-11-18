package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuscriptRating;
@Repository
@Transactional
public interface ManuscriptRatingRepo extends JpaRepository<ManuscriptRating, Integer> {

	@Query("SELECT m FROM ManuscriptRating m WHERE m.userId=:userId AND m.roleId=:roleId AND m.manuscriptId=:manuscriptId")
	ManuscriptRating getManuscriptRating(Integer userId,Integer roleId,Integer manuscriptId);
	
	@Query("SELECT m FROM ManuscriptRating m WHERE m.journalId IN :journalId AND YEAR(m.createdOn) In :year AND m.rating >:ratingNumber ")
	List<ManuscriptRating> getArticleRatingGreaterThan(List<Integer> journalId,List<Integer> year ,Integer ratingNumber);
	
	@Query("SELECT m FROM ManuscriptRating m WHERE m.journalId IN :journalId AND YEAR(m.createdOn) In :year AND m.rating <:ratingNumber ")
	List<ManuscriptRating> getArticleRatingLessThan(List<Integer> journalId,List<Integer> year ,Integer ratingNumber);
	
	@Query("SELECT m FROM ManuscriptRating m WHERE m.journalId IN :journalId AND YEAR(m.createdOn) In :year AND m.rating =:ratingNumber ")
	List<ManuscriptRating> getArticleRatingEqualTo(List<Integer> journalId,List<Integer> year ,Integer ratingNumber);
}
