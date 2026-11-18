package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ManuscriptIssuesPlanner;

public interface ManuscriptIssuesPlannerRepo extends JpaRepository<ManuscriptIssuesPlanner, Integer>{

	@Query("Select mIp from ManuscriptIssuesPlanner mIp where mIp.issueId=:issueId AND mIp.manuscriptId=:manuscriptId AND mIp.manuscriptVersion=:manuscriptVersion AND mIp.isActive=1")
	ManuscriptIssuesPlanner getManuscriptIssuesPlannerList(Integer issueId,Integer manuscriptId,Integer manuscriptVersion);
	
	@Query("Select mIp from ManuscriptIssuesPlanner mIp where mIp.journalId=:journalId AND mIp.isActive=1")
	List<ManuscriptIssuesPlanner> getManuscripAssignList(Integer journalId);
	
	@Modifying
	@Query("UPDATE ManuscriptIssuesPlanner mIp set mIp.isActive=0 where mIp.id=:id")
	public void deleteManuscriptIssuesPlanner(Integer id);
}
