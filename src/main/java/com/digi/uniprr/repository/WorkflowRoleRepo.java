package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.WorkflowRoleSeq;

@Repository
@Transactional
public interface WorkflowRoleRepo extends JpaRepository<WorkflowRoleSeq, Integer> {

	@Query("SELECT w from WorkflowRoleSeq w where w.workflowId=:workflowId AND w.journalId=0 AND w.isActive=1")
	public List<WorkflowRoleSeq> getByWorkflowId(Integer workflowId);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.workflowId=:workflowId AND w.journalId=:journalId AND w.isActive=1")
	public List<WorkflowRoleSeq> getJournalWorkflow(Integer workflowId, Integer journalId);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.workflowId=:workflowId AND w.journalId=:journalId AND w.taskId=92 AND w.isActive=1")
	public List<WorkflowRoleSeq> getJournalWorkflowWithout(Integer workflowId, Integer journalId);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.workflowId=:workflowId AND w.journalId=:journalId AND w.taskId=93 AND w.isActive=1")
	public List<WorkflowRoleSeq> getJournalWorkflowAfter(Integer workflowId, Integer journalId);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.journalId=:journalId AND w.isActive=1")
	public List<WorkflowRoleSeq> getWorkflowByJournalId(Integer journalId);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.journalId=:journalId")
	public List<WorkflowRoleSeq> getWorkflowByJournalIdForCreateWorkflow(Integer journalId);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.id=:id AND w.isActive=1")
	public WorkflowRoleSeq getWorkflowRoleTaskByid(Integer id);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.workflowId=:workflowId AND w.taskId=:taskId AND w.isActive=1")
	public WorkflowRoleSeq getByWorkflowIdAndTaskId(Integer workflowId, Integer taskId);
	
	@Query("SELECT w from WorkflowRoleSeq w where w.taskId=:taskId AND w.journalId=:journalId AND w.isActive=1")
	public WorkflowRoleSeq getJournalWorkflowIdTaskId(Integer taskId, Integer journalId);
	

}
