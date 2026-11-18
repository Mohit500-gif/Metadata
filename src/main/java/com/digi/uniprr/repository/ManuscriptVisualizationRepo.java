package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ManuscriptVisualization;

public interface ManuscriptVisualizationRepo extends JpaRepository<ManuscriptVisualization, Integer> {

	@Query("SELECT mv FROM ManuscriptVisualization mv WHERE mv.manuscriptId=:manuScriptId  AND mv.manuscriptVersion=:manuscriptVersion  AND mv.journalId=:journalId")
	ManuscriptVisualization getManuscriptVisualizationDetails(Integer manuScriptId, Integer manuscriptVersion,
			Integer journalId);
}
