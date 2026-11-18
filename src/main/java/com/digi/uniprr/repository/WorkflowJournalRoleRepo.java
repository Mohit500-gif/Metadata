package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.WorkflowJournalRoleSeq;

public interface WorkflowJournalRoleRepo extends JpaRepository<WorkflowJournalRoleSeq, Integer> {

	@Query("SELECT w from WorkflowJournalRoleSeq w where w.workflowId=:workflowId AND w.journalId=:journalId")
	public List<WorkflowJournalRoleSeq> getByWorkflowId(Integer journalId, Integer workflowId);
	
	@Query("SELECT w from WorkflowJournalRoleSeq w where w.workflowId=:workflowId AND w.taskId=:taskId AND w.isActive=1")
	public WorkflowJournalRoleSeq getByWorkflowIdAndTaskId(Integer workflowId, Integer taskId);
	

}
