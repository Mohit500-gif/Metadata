package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ManuscriptTat;

public interface ManuscriptTatRepo extends JpaRepository<ManuscriptTat, Integer>{
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ManuscriptTat m SET m.isActive=0 WHERE m.journalId =:journalId AND m.manuscriptId =:manuscriptId AND m.manuscriptVersion =:ManuscriptVersion")
	void disablePreviousTat(Integer journalId, Integer manuscriptId, Integer ManuscriptVersion);

	@Query("SELECT m from ManuscriptTat m where m.isActive=1")
	List<ManuscriptTat> getDataForMail();
}
