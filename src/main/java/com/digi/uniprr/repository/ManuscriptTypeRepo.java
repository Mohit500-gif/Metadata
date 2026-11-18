package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ManuscriptType;

public interface ManuscriptTypeRepo extends JpaRepository<ManuscriptType, Integer>{
	
	@Query("SELECT m FROM ManuscriptType m WHERE m.isActive=1")
	public List<ManuscriptType> getActiveManuscriptTypes();
	
	@Query("SELECT m FROM ManuscriptType m WHERE m.name=:name")
	public ManuscriptType getManuscriptTypeBy(String name);

	@Query("SELECT m FROM ManuscriptType m WHERE m.name=:name AND m.journalId=:journalId")
	public ManuscriptType getManuscriptTypeByJournal(String name, Integer journalId);

	@Query("SELECT m FROM ManuscriptType m WHERE m.journalId IN :journalId  ORDER BY LTRIM(m.name) ASC")
	public List<ManuscriptType> getActiveManuscriptTypesByJourna(List<Integer> journalId);

	@Query("SELECT m FROM ManuscriptType m WHERE m.journalId =:journalId AND m.isActive=1")
	public List<ManuscriptType> getListByJournalId(Integer journalId);

}
