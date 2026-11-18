package com.digi.uniprr.repository;

import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuscriptTypeJournal;
@Repository
@Transactional
public interface ManuscriptTypeJournalsRepo extends JpaRepository<ManuscriptTypeJournal, Integer>{
	
	@Query("SELECT m from ManuscriptTypeJournal m where m.journalId=:id AND m.isActive=1 ORDER BY LTRIM(m.manutypeName) ASC")
	public List<ManuscriptTypeJournal> getManuscriptTypesFromJournalId(Integer id);
	
	@Modifying
	@Query("DELETE from ManuscriptTypeJournal m where m.journalId=:jId")
	void deleteByJournalId(Integer jId);
	
	@Query("SELECT m from ManuscriptTypeJournal m where m.journalId In :id AND m.isActive=1 GROUP BY m.manutypeName")
	public List<ManuscriptTypeJournal> getManuscriptTypesFromJournalIdList(List<Integer> id);

	@Query("SELECT m FROM ManuscriptTypeJournal m WHERE m.manutypeName=:manutypeName AND m.journalId=:journalId")
	public ManuscriptTypeJournal getJournalManuscriptTypeBy(String manutypeName,Integer journalId);
	
	@Query(value="SELECT * FROM manuscripttype_journal m WHERE m.manutype_name like %:manutypeName% AND m.journal_id=:journalId",nativeQuery = true )
	public ManuscriptTypeJournal getJournalManuscriptTypeBy1(String manutypeName,Integer journalId);

	@Query("SELECT m FROM ManuscriptTypeJournal m WHERE m.manutypeName=:trim AND m.journalId=:id AND m.isActive=1")
	public List<ManuscriptTypeJournal> getManuscriptTypesFromJournalIdAndName(Integer id, String trim);
	
	@Query(value="select mj.manutype_name, count(m.manuscript_status_id) as submitted"
			+ " , case when m.manuscript_status_id = 30 then sum(1) else 0 end as rejected"
			+ " , case when m.manuscript_status_id = 33 then sum(1) else 0 end as accepted"
			+ " from manuscripttype_journal mj"
			+ " join manuscript_typedetails md on mj.manutype_name = md.manuscript_type"
			+ " join manuscript m on m.manuscript_id = md.manuscript_id"
			+ " where mj.journal_id =:journalId and mj.is_active = 1 and m.journal_id =:journalId "
			+ " and YEAR(m.submission_date) =:year "
			+ " and (MONTH(m.submission_date) =:month or 0 =:month) "
			+ " group by mj.manutype_name order by mj.manutype_name", nativeQuery = true)
	List<Tuple> getAcceptRejectReporData(Integer journalId, Integer year, Integer month);
}
