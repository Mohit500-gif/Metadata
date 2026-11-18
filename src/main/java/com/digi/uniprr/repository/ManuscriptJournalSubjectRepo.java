package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuscriptJournalSubject;
@Repository
@Transactional
public interface ManuscriptJournalSubjectRepo extends JpaRepository<ManuscriptJournalSubject, Integer>{

	@Query("SELECT m FROM ManuscriptJournalSubject m WHERE m.manuscriptId=:manuscriptId AND m.isActive=:isActive")
	List<ManuscriptJournalSubject> findByManuscriptId(Integer manuscriptId, Integer isActive);

	@Modifying
	@Query("UPDATE ManuscriptJournalSubject m SET m.isActive=0 WHERE m.manuscriptId=:manuscriptId")
	void disableManuscriptJournalSubject(Integer manuscriptId);
	
	@Query("SELECT m FROM ManuscriptJournalSubject m WHERE m.manuscriptId=:manuscriptId AND m.journalSubjectId=:journalSubjectId")
	ManuscriptJournalSubject getManuscriptJournalSubject(Integer manuscriptId, Integer journalSubjectId);
}
