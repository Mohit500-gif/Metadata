package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.UserLogins;

public interface UserLoginsRepo extends JpaRepository<UserLogins, Integer>{
	@Query("Select uL from UserLogins uL WHERE uL.userId=:userId")
	UserLogins loggedUser(Integer userId);
	
	@Query("Select uL from UserLogins uL WHERE uL.journalId=:journalId AND uL.status=1")
	List<UserLogins> getLogedUserDetails(Integer journalId);
	
	/****************** Present Status Reports *************/
	@Query("Select uL from UserLogins uL WHERE uL.journalId=:journalId AND uL.status=1")
	List<UserLogins> getPresentStatusReports(Integer journalId);

	@Query(value="Select * from user_logins ul LEFT JOIN manuscript m on ul.user_id=m.manuscript_author_id WHERE ul.journal_id=7 AND ul.status=1 AND ul.logged_in_date < m.submission_date",nativeQuery = true)
	List<UserLogins> getLastloginDetails(Integer journalId);

	@Query(value="Select * from user_logins ul LEFT JOIN manuscript_reviewer mr on ul.user_id=mr.reviewer_id WHERE ul.journal_id=7 AND ul.status=1 AND ul.logged_in_date < mr.assigned_date",nativeQuery = true)
	List<UserLogins> getLastLoginsReviewerComments(Integer journalId);

	@Query(value = "Select * from user_logins ul Left JOIN manuscript m on ul.user_id=m.manuscript_author_id WHERE ul.journal_id=7 AND ul.status=1 AND ul.logged_in_date < m.submission_date AND m.manuscript_status_id IN (24,4)",nativeQuery = true)
	List<UserLogins> getLastLoginsRevisedManuscript(Integer journalId);
}
