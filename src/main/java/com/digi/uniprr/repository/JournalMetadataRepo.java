package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.JournalMetadata;

public interface JournalMetadataRepo extends JpaRepository<JournalMetadata, Integer> {
//
//	@Query(value="SELECT * FROM journal_metadata a WHERE a.journal_id=:journalId",nativeQuery = true)
//	JournalMetadata metaDatalistByJournalId(Integer jornalId);
//	

	@Query("SELECT a FROM JournalMetadata a WHERE a.journalId=:jornalID")
	JournalMetadata metaDatalistByJournalId(Integer jornalID);

	@Modifying(clearAutomatically = true)
	@Query("update JournalMetadata a set a.journalBlindType=:type where a.journalId =:jornalId")
	void updateBlind(Integer jornalId, String type);

	// @Query("SELECT j from JournalMetadata j where j.journalSpeciality LIKE
	// %:specializationName% OR j.journalSubject LIKE %:subjectName%")
	@Query("SELECT s from JournalMetadata s where s.journalSpeciality IN :specializationName OR s.journalSubject IN :subjectName")
	List<JournalMetadata> getSpecilaizationByJournal(List<String> specializationName, List<String> subjectName);

	@Query("SELECT s from JournalMetadata s where s.journalSubject IN :subjectName")
	List<JournalMetadata> getSpecilaizationByJournalOne(List<String> subjectName);

	@Query("SELECT a FROM JournalMetadata a WHERE a.journalId IN :jornalID")
	List<JournalMetadata> getMetaDataByListOfJId(List<Integer> jornalID);

	@Query("SELECT a.journalId FROM JournalMetadata a WHERE a.publicationType =:publicationType")
	List<Integer> getJournalBySubscriptionType(String publicationType);

//	@Query("SELECT s.journalId from JournalMetadata s where s.journalSubject Like %:subjectName%")
//	List<Integer> getSpecilaizationBySubject(String subjectName);

	@Query(value = "SELECT a.* FROM journal_metadata a JOIN journal b ON a.journal_id = b.journal_id WHERE (a.journal_subject LIKE CONCAT( :subjectName3, '%') OR a.journal_subject LIKE CONCAT('%', :subjectName1, '%') OR a.journal_subject LIKE CONCAT('%', :subjectName2)) AND b.is_active = 1",nativeQuery = true)
	List<JournalMetadata> getSpecializationBySubject(String subjectName3, String subjectName1, String subjectName2);

	@Query("SELECT j.journalId from JournalMetadata j where j.journalSpeciality LIKE %:specializationName%")
	// @Query("SELECT s from JournalMetadata s where s.journalSpeciality IN
	// :specializationName OR s.journalSubject IN :subjectName")
	List<Integer> getSpecilaization(String specializationName);

	@Modifying(clearAutomatically = true)
	@Query("update JournalMetadata a set a.workflowId=:workflowId where a.journalId =:jornalId")
	void updateJournalWorkflow(Integer workflowId, Integer jornalId);

	@Query(value = "select a.* from journal_metadata a join journal b on a.journal_id=b.journal_id and b.is_active=1",nativeQuery = true)
	List<JournalMetadata> getSpecializationByActiveJournals();

}
