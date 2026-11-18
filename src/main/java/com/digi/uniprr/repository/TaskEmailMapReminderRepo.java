package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.TaskEmailMapReminder;

public interface TaskEmailMapReminderRepo extends JpaRepository<TaskEmailMapReminder, Integer> {

	@Query ("SELECT te FROM TaskEmailMapReminder te WHERE te.taskId=:taskMasterId AND te.journalId=:journalId GROUP BY te.taskId")
	TaskEmailMapReminder getTaskEmailMapDetails(Integer taskMasterId, Integer journalId);

	@Query ("SELECT te FROM TaskEmailMapReminder te WHERE te.journalEmailTemplateId=:journalMailTemplateId 	AND te.taskId=:taskMasterId AND te.journalId=:journalId")
	TaskEmailMapReminder getUniqueTaskEmailMapData(Integer journalMailTemplateId,Integer taskMasterId,Integer journalId);

	@Query ("select te from TaskEmailMapReminder te where te.journalId=:journalId")
	List<TaskEmailMapReminder> getAutoMailesList(Integer journalId);

	@Query ("SELECT te FROM TaskEmailMapReminder te WHERE te.journalEmailTemplateId=:journalEmailTemplateId")
	List<TaskEmailMapReminder> getTaskEmailMapTemplateData(Integer journalEmailTemplateId);

}
