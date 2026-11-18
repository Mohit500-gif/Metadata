package com.digi.uniprr.repository;

import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuscriptTypeDetails;

@Repository
@Transactional
public interface ManuscriptTypeDetailsRepo extends JpaRepository<ManuscriptTypeDetails, Integer> {

	@Query("SELECT mt FROM ManuscriptTypeDetails mt WHERE mt.manuscriptId=:manuscriptId")
	List<ManuscriptTypeDetails> getManuscriptTypeDetailsById(Integer manuscriptId);
	
	@Query("SELECT mt FROM ManuscriptTypeDetails mt WHERE mt.manuscriptId=:manuscriptId")
	ManuscriptTypeDetails getDetailsFromManuscriptId(Integer manuscriptId);
	
	@Query("SELECT mt FROM ManuscriptTypeDetails mt WHERE mt.manuscriptId=:manuscriptId AND mt.manuscriptVersion=:manuscriptVersion")
	ManuscriptTypeDetails getDetailsFromManuscriptIdForBundaling(Integer manuscriptId,Integer manuscriptVersion);

	@Query(value="SELECT * FROM manuscript_typedetails m WHERE m.manuscript_type=:manuscriptType", nativeQuery = true)
	List<ManuscriptTypeDetails> getManuscriptTypeDetailsListByManuscriptType(String manuscriptType);
	
	@Query("SELECT m FROM ManuscriptTypeDetails m WHERE m.manuscriptType=:manuscriptType")
	List<ManuscriptTypeDetails> getManuscriptTypeDetailsListByManuscriptTypeByPresentDate(String manuscriptType);

	
//	@Query(value="	select TRIM(md.manuscript_type)," + 
//			"			COALESCE(SUM(case when YEAR(m.submission_date) =:year and (MONTH(m.submission_date) =:month or 0 =:month)  then 1 END),0)as submitted," + 
//			"			COALESCE(SUM(case when m.manuscript_status_id IN (33,57,59,61,62,65) AND (YEAR(m.production_date) =:year and (MONTH(m.production_date) =:month or 0 =:month)) then 1 end),0)as accepted," +
//			"			COALESCE(SUM(case when m.manuscript_status_id = 30 AND (YEAR(m.rejected_date) =:year and (MONTH(m.rejected_date) =:month or 0 =:month))  then 1 END),0)as rejected" +
//			"			from manuscript_typedetails md " + 
//			" 			join manuscript m on m.manuscript_id = md.manuscript_id" + 
//			"			where m.journal_id =:journalId and m.is_active = 1 AND m.manuscript_status_id !=1" + 
//			"			AND ((YEAR(m.submission_date) =:year and (MONTH(m.submission_date) =:month or 0 =:month))" + 
//			"			OR (YEAR(m.rejected_date) =:year and (MONTH(m.rejected_date) =:month or 0 =:month))" + 
//			"			OR (YEAR(m.production_date) =:year and (MONTH(m.production_date) =:month or 0 =:month)))" + 
//			"			group by TRIM(md.manuscript_type) order by TRIM(md.manuscript_type)", nativeQuery = true)
//	List<Tuple> getAcceptRejectReportData(Integer journalId, Integer year, Integer month);
	
	@Query(value="select TRIM(md.manuscript_type),count(m.manuscript_status_id) as submitted, "
			+ "COALESCE(SUM(case when m.manuscript_status_id IN (33,57,59,61,62,65,69,70) then 1 end),0)as accepted,"
			+ "COALESCE(SUM(case when m.manuscript_status_id = 30 then 1 END),0)as rejected "
			+ "from manuscript_typedetails md join manuscript m on m.manuscript_id = md.manuscript_id "
			+ "where m.journal_id =:journalId and m.is_active = 1 AND m.manuscript_status_id !=1 AND "
			+ "(YEAR(m.submission_date) = :year and (MONTH(m.submission_date) =:month or 0 =:month)) "
			+ "group by TRIM(md.manuscript_type) order by TRIM(md.manuscript_type)", nativeQuery = true)
	List<Tuple> getAcceptRejectReportData(Integer journalId, Integer year, Integer month);

	
}