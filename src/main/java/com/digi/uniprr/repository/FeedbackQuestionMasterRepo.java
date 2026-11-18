package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.FeedbackQuestionMaster;

public interface FeedbackQuestionMasterRepo extends JpaRepository<FeedbackQuestionMaster, Integer> {

	@Query("select f from FeedbackQuestionMaster f where f.journalId=:rm_id AND f.isActive=1")
	List<FeedbackQuestionMaster> getFeedbackListById(Integer rm_id);

	@Query("select f from FeedbackQuestionMaster f where f.journalId='-1' AND f.isActive=1")
	List<FeedbackQuestionMaster> geMasterList();

	@Query("select f from FeedbackQuestionMaster f where f.isActive=1")
	List<FeedbackQuestionMaster> getActiveData();

	@Query("select f from FeedbackQuestionMaster f where f.journalId=:journalId AND f.role=:role AND f.type='FEEDBACK' AND f.isActive=1")
	List<FeedbackQuestionMaster> listByJournalAndRole(Integer journalId, String role);

	@Query("select f from FeedbackQuestionMaster f where f.journalId=:journalId AND f.uploadForm='YES' AND f.isActive=1")
	List<FeedbackQuestionMaster> getDataByJournal(Integer journalId);

	@Query("select f from FeedbackQuestionMaster f where f.journalId=:journalId AND f.role='REVIEWER' AND f.isActive=1 AND f.revisedManuscript=:code")
	List<FeedbackQuestionMaster> getFeedbackListByIdReviewer(Integer journalId, String code);
	
	@Query("select f from FeedbackQuestionMaster f where f.journalId=:journalId AND f.role='REVIEWER' AND f.isActive=1")
	List<FeedbackQuestionMaster> getFeedbackListByIdReviewerForNonRevision(Integer journalId);

}
