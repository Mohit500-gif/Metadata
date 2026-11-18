package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.AuthorFeedback;
@Repository
@Transactional
public interface AuthorFeedbackRepo extends JpaRepository<AuthorFeedback, Integer>{
	
	@Query(value ="SELECT * FROM author_feedback a WHERE a.created_on IN(SELECT MAX(b.created_on) AS MaxDate FROM author_feedback b WHERE b.journal_id=:journalId  AND b.role_id=:roleId GROUP BY b.user_id)", nativeQuery = true)
	List<AuthorFeedback> getAuthorFeedback(Integer journalId, Integer roleId);
}
