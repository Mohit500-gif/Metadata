package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.Workflow;

public interface WorkflowRepo extends JpaRepository<Workflow, Integer> {

	@Query("SELECT m FROM Workflow m WHERE m.isActive=1 AND m.isMaster=1 ORDER BY LTRIM(m.name) ASC")
	List<Workflow> getTaskByWorkflowDetails();
	
	@Query("SELECT w FROM Workflow w WHERE w.name=:name AND w.isMaster=0")
	Workflow getWorkFlowByName(String name);
	
	@Query("SELECT w FROM Workflow w WHERE w.name=:name AND w.isActive=1")
	Workflow getWorkFlowByWorkFlowName(String name);

}
