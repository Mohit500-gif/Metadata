package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.JournalManuscriptTypeCharge;

public interface JournalManuscriptTypeChargeRepo  extends JpaRepository<JournalManuscriptTypeCharge, Integer>{

	@Query("SELECT j FROM JournalManuscriptTypeCharge j where j.journalId=:journalId AND j.manuscriptTypeId=:manuscriptTypeId")
	JournalManuscriptTypeCharge getJournalManuscriptTypeCharge(Integer journalId,Integer manuscriptTypeId);
	
	@Query("SELECT j FROM JournalManuscriptTypeCharge j where j.journalId=:journalId AND j.manuscriptTypeId=:manuscriptTypeId AND j.isActive=1")
	JournalManuscriptTypeCharge getJournalManuscriptTypeChargeIsActive(Integer journalId,Integer manuscriptTypeId);
	
	@Query("SELECT j FROM JournalManuscriptTypeCharge j where j.journalId=:journalId")
	List<JournalManuscriptTypeCharge> getJournalManuscriptTypeChargeList(Integer journalId);
	
	@Modifying
	@Query("DELETE FROM JournalManuscriptTypeCharge j where j.journalId=:journalId")
	void deleteJournalManuscriptTypeCharge(Integer journalId);

	
	@Modifying(clearAutomatically = true)
	@Query("update JournalManuscriptTypeCharge ud set ud.isActive =0 where ud.journalId =:journalId") 
	void updateJournalManuscriptTypeCharge(Integer journalId);
	
	@Query(value= "SELECT * FROM journal_manuscript_type_charge as a WHERE a.journal_id=:journalId AND a.is_active=1",nativeQuery = true)
	List<JournalManuscriptTypeCharge> getJournalChargeDetailByJournalId(Integer journalId);
	
	// Get manuscript type charge with the help of manuscriptId
	@Query(value= "SELECT  jmt.* FROM  journal_manuscript_type_charge as jmt,manuscripttype_journal as mj,manuscript as ms,manuscript_typedetails as mst WHERE ms.manuscript_id=mst.manuscript_id AND mst.manuscript_type=mj.manutype_name AND mj.mjt_id=jmt.manuscript_type_id AND ms.manuscript_id=:manuscriptId AND jmt.journal_id=:journalId AND jmt.is_active=1",nativeQuery = true)
	JournalManuscriptTypeCharge getJournalManuscriptTypeChargeByManuiscriptId(Integer manuscriptId, Integer journalId);
	
}
