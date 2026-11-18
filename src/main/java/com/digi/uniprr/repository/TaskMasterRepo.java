package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.TaskMaster;

public interface TaskMasterRepo extends JpaRepository<TaskMaster, Integer> {

	@Query("SELECT t FROM TaskMaster t WHERE t.isActive=1 ORDER BY LTRIM(t.taskName) ASC")
	List<TaskMaster>  getTaskNameForReminder();
	
	@Query("SELECT t FROM TaskMaster t ORDER BY LTRIM(t.taskName) ASC")
	List<TaskMaster>  getTaskNameForNotification();
}
