package com.digi.uniprr.repository;

import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.VO.AdminJournalVo;
import com.digi.uniprr.model.Journal;

@Repository
@Transactional
public interface JournalsRepo extends JpaRepository<Journal, Integer> {
	
	@Query("SELECT j from Journal j WHERE j.isActive=1 ORDER BY LTRIM(j.journalTitle) ASC")
	public List<Journal> getActiveJournals();
	
	@Query(value="SELECT m.journal_title,m.journal_code,m.journal_id FROM journal m WHERE m.is_active=1 ORDER BY m.journal_title ASC", nativeQuery=true)
	public List<Tuple> getActiveJournalList();

	@Query("SELECT j from Journal j where j.journalTitle LIKE %:name% AND j.isActive=1")
	public List<Journal> getJournalByName(String name);
	
	@Query("Select j from Journal j where j.journalId=:id AND j.isActive=1")
	public Journal getJournalById(Integer id);
	
	@Query("Select j from Journal j where j.journalId IN :id AND j.isActive=1")
	public List<Journal> getJournalListById(List<Integer> id);
	
	@Query("SELECT j from Journal j WHERE j.createdBy=:userId")
	public List<Journal> getJournalByUserId(Integer userId);
	
	@Query("SELECT j from Journal j WHERE j.journalCode IN :journalCode AND j.isActive=1")
	public List<Journal> getByJournalCode(List<String> journalCode);
	
	@Query("Select j from Journal j where j.journalCode=:journalCode AND j.isActive=1")
	public List<Journal> getJournalByCode(String journalCode);
	
	@Query("Select j from Journal j where j.journalCode=:journalCode")
	public Journal getJournalByCodeValidateAcron(String journalCode);

	@Query("Select j from Journal j where j.journalCode=:name AND j.isActive=1")
	public Journal getJournalByCodeForUnitouch(String name);

	@Query("SELECT j from Journal j ORDER BY LTRIM(j.journalTitle) ASC")
	public List<Journal> getAllJournalList();
	
	@Query("SELECT new com.digi.uniprr.VO.AdminJournalVo(m.journalTitle AS journalTitle,m.journalCode AS journalCode,m.journalId AS journalId) FROM Journal m WHERE m.isActive=1 ORDER BY m.journalCode ASC")
	public List<AdminJournalVo> getActiveJournalList(Pageable paging);
	
	@Query("SELECT new com.digi.uniprr.VO.AdminJournalVo(m.journalTitle AS journalTitle,m.journalCode AS journalCode,m.journalId AS journalId) FROM Journal m WHERE (m.journalCode LIKE %:search% OR m.journalTitle LIKE %:search%) AND m.isActive=1 ORDER BY m.journalCode ASC")
	public List<AdminJournalVo> getActiveJournalListSearch(String search,Pageable paging);
	
	@Query("Select j from Journal j where j.isActive=1")
	public List<Journal> getActivejournal();
	
	@Query("SELECT COUNT(m) FROM Journal m WHERE (m.journalCode LIKE %:search% OR m.journalTitle LIKE %:search%) AND m.isActive=1 ORDER BY m.journalCode ASC")
	public int journalCountSearch(String search);
	
	@Query("SELECT COUNT(m) FROM Journal m WHERE m.isActive=1 ORDER BY m.journalCode ASC")
	public int journalCount();

	@Modifying
	@Query("update Journal m set m.reviewerSuggestion=:reviewerSuggestion, reviewerCount =:reviewerCount where m.journalId=:id")
	void updateJournalReviewerSetting(String reviewerSuggestion, Integer reviewerCount, Integer id);

	@Modifying
	@Query("update Journal m set m.correspondingCount =:coAuthorCount where m.journalId=:id")
	public void updateCoAuthorCount(Integer coAuthorCount, Integer id);

	@Query(value = "select charge_flag from journal where journal_id=:JournalId",nativeQuery = true)
	public String getChargeFlag(Integer JournalId); 
}
 