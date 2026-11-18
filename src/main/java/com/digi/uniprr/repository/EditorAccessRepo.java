package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.EditorAccess;

public interface EditorAccessRepo extends JpaRepository<EditorAccess, Integer>{

	@Query("SELECT ea from EditorAccess ea where ea.isActive=1")
	List<EditorAccess> getActiveData();

	
	
}
