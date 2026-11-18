package com.digi.uniprr.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ReminderTracker;

@Repository
public interface ReminderTrackerRepo extends JpaRepository<ReminderTracker, Integer> {

	@Query(value = "SELECT * FROM reminder_tracker rt WHERE rt.journal_id=:journalId AND rt.manuscript_id=:manuscriptId AND rt.assigned_user=:assignedUser AND rt.manuscript_status_id=:manuscriptStatusId ORDER BY id DESC LIMIT 1", nativeQuery = true)
	ReminderTracker getReminderTrackerData(Integer journalId, Integer manuscriptId, Integer assignedUser,
			Integer manuscriptStatusId);

	@Query(value = "SELECT due_date FROM authorities_history ah WHERE ah.manuscript_id = :manuscriptId "
			+ "AND ah.manuscript_version = :manuscriptVersion " + "AND ah.journal_id = :journalId "
			+ "AND ah.manuscript_status_id = :manuscriptStatus " + "AND ah.current_user_id = :assignedUser "
			+ "ORDER BY ah.id DESC LIMIT 1", nativeQuery = true)
	Date findDueDate(@Param("manuscriptId") int manuscriptId, @Param("manuscriptVersion") int manuscriptVersion,
			@Param("journalId") int journalId, @Param("manuscriptStatus") int manuscriptStatus,
			@Param("assignedUser") Integer assignedUser);

}
