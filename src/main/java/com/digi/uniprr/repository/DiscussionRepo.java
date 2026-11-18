package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.Discussion;

public interface DiscussionRepo extends JpaRepository<Discussion, Integer> {

	@Query("SELECT m FROM Discussion m WHERE m.journalId=:journalId AND m.userId=:userId AND m.userRole=:userRole GROUP BY m.manuscriptId,m.manuscriptVersion")
	List<Discussion> getDiscussionList(Integer journalId, Integer userId, Integer userRole);

	@Query("SELECT m FROM Discussion m WHERE m.id=:id AND m.journalId=:journalId AND m.userId=:userId AND m.userRole=:userRole AND m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion")
	Discussion getManuscriptDiscussion(Integer id, Integer journalId, Integer userId, Integer userRole,
			Integer manuscriptId, Integer manuscriptVersion);

	@Query("SELECT d FROM Discussion d WHERE d.journalId=:journalId AND d.discussionOnRole=:toUserRole GROUP BY d.manuscriptId, d.manuscriptVersion")
	List<Discussion> getDataForReviewer(Integer journalId, Integer toUserRole);

	@Query("SELECT m FROM Discussion m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion ORDER BY m.createdDate ASC")
	List<Discussion> getDataByManuscript(Integer manuscriptId, Integer manuscriptVersion);

	@Query("SELECT d FROM Discussion d WHERE d.journalId=:journalId AND d.userRole=:toUserRole GROUP BY d.manuscriptId, d.manuscriptVersion")
	List<Discussion> getDataForEIC(Integer journalId, Integer toUserRole);

	@Query("SELECT d FROM Discussion d WHERE d.receiverId=:userId AND d.discussionOnRole=:taskId AND d.userRole =:roleId")
	List<Discussion> getQueryById(Integer userId, Integer roleId, Integer taskId);

	@Query("SELECT d FROM Discussion d WHERE d.userRole =:roleId AND d.userId=:userId")
	List<Discussion> getOutBox(Integer userId, Integer roleId);
}
