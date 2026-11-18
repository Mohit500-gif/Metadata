package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.AuthorSubmissionFeedback;

@Repository
@Transactional
public interface AuthorSubmissionFeedbackRepo extends JpaRepository<AuthorSubmissionFeedback, Integer> {

	@Query("SELECT mt FROM AuthorSubmissionFeedback mt WHERE mt.authorId IN :authorId AND mt.journalId IN :journalId")
	AuthorSubmissionFeedback getAuthorSubmissionFeedbackDetails(List<Integer> authorId, List<Integer> journalId);
}
