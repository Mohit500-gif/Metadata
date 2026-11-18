package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.JournalSubject;

public interface JournalSubjectRepo extends JpaRepository<JournalSubject, Integer>{

	List<JournalSubject> findByJournalId(Integer journalId);

	JournalSubject findByjournalSubject(String journalSubject);

	@Query(value = "select * from journal_subject where journal_subject=:journalSubject and journal_id=:journalId and is_active=1", nativeQuery = true)
	JournalSubject findByjournalSubjectAndJournalId(String journalSubject, Integer journalId);

	@Query ("select a from JournalSubject a where a.journalId=:journalId And  a.isActive=:isActive")
	List<JournalSubject> findByJournalId(Integer journalId, Integer isActive);


}
