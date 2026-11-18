package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.JournalIssuesPlanner;

public interface JournalIssuesPlannerRepo extends JpaRepository<JournalIssuesPlanner, Integer>{

	@Query("SELECT jIp FROM JournalIssuesPlanner jIp WHERE jIp.journalId=:journalId AND jIp.isActive=1")
	List<JournalIssuesPlanner> getIssueList(Integer journalId);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE JournalIssuesPlanner jIp SET jIp.isActive=0 WHERE jIp.id=:id")
	void deleteIssue(Integer id);
	
	
	
	
}
