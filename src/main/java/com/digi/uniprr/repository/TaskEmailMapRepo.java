package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.TaskEmailMap;

public interface TaskEmailMapRepo extends JpaRepository<TaskEmailMap, Integer> {

	@Query ("select te from TaskEmailMap te where te.taskId=:id")
	List<TaskEmailMap> customiseMailTemplate(Integer id);
	
	@Query ("select te from TaskEmailMap te where te.journalId=:journalId")
	List<TaskEmailMap> getAutoMailesList(Integer journalId);
	
	@Query ("SELECT te FROM TaskEmailMap te WHERE te.journalEmailTemplateId=:journalMailTemplateId 	AND te.taskId=:taskMasterId AND te.journalId=:journalId")
	TaskEmailMap getUniqueTaskEmailMapData(Integer journalMailTemplateId,Integer taskMasterId,Integer journalId);
	
	@Query ("SELECT te FROM TaskEmailMap te WHERE te.taskId=:taskMasterId AND te.journalId=:journalId")
	TaskEmailMap getTaskEmailMapDetails(Integer taskMasterId,Integer journalId);

	@Query ("SELECT te FROM TaskEmailMap te WHERE te.journalEmailTemplateId=:journalEmailTemplateId")
	List<TaskEmailMap> getTaskEmailMapTemplateData(Integer journalEmailTemplateId);

    @Query("SELECT t FROM TaskEmailMap t WHERE t.taskId = :taskId AND t.journalId = :journalId")
    TaskEmailMap findByTaskIdAndJournalId(Integer taskId, Integer journalId);
}
