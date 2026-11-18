package com.digi.uniprr.repository;

import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digi.uniprr.model.PlagiarismCheck;

public interface PlagiarismCheckRepo extends JpaRepository<PlagiarismCheck, Integer> {

	@Query(value="SELECT * FROM plagiarism_check pc WHERE pc.is_active=0", nativeQuery = true)
	List<PlagiarismCheck> getInActiveData();

	@Query("SELECT pc FROM PlagiarismCheck pc WHERE pc.isActive=1 ORDER BY pc.createdDate DESC")
	List<PlagiarismCheck> getReport();

	@Query("SELECT pc FROM PlagiarismCheck pc WHERE pc.manuscriptId=:manuscriptId AND pc.manuscriptVersion=:manuscriptVersion AND pc.fileNubmer=:fileNumber AND pc.isActive=1")
	PlagiarismCheck getReportByManuscript(Integer manuscriptId, Integer manuscriptVersion, Integer fileNumber);

	@Query(value="SELECT pc.manuscript_id,pc.manuscript_version,m.manuscript_code,m.manuscript_title,m.manuscript_abstract,mt.manuscript_type,CONCAT(u.first_name,' ',u.last_name) AS author_name,pc.created_on, pc.url, pc.file_number  FROM plagiarism_check pc INNER JOIN manuscript m ON m.manuscript_id=pc.manuscript_id INNER JOIN users u ON u.id=m.manuscript_author_id INNER JOIN manuscript_typedetails mt ON mt.manuscript_id=m.manuscript_id WHERE pc.journal_id=:journalId ORDER BY pc.created_on DESC", nativeQuery = true)
	List<Tuple> getReportByJournal(Integer journalId);
	
//	@Query(value = "SELECT pc.manuscript_id, pc.manuscript_version, m.manuscript_code, m.manuscript_title, m.manuscript_abstract, mt.manuscript_type, CONCAT(u.first_name,' ',u.last_name) AS author_name, pc.created_on, pc.url, pc.file_number " +
//	        "FROM plagiarism_check pc " +
//	        "INNER JOIN manuscript m ON m.manuscript_id = pc.manuscript_id " +
//	        "INNER JOIN users u ON u.id = m.manuscript_author_id " +
//	        "INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id " +
//	        "WHERE pc.journal_id = :journalId " +
//	        "ORDER BY pc.created_on DESC ",
//	        countQuery = "SELECT COUNT(*) FROM plagiarism_check pc INNER JOIN manuscript m ON m.manuscript_id = pc.manuscript_id " +
//	                "INNER JOIN users u ON u.id = m.manuscript_author_id " +
//	                "INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id " +
//	                "WHERE pc.journal_id = :journalId",
//	        nativeQuery = true)
//	Page<Tuple> getReportByJournal(@Param("journalId") Integer journalId, Pageable pageable);
	
	@Query(value = 
		    "SELECT pc.manuscript_id, pc.manuscript_version, m.manuscript_code, m.manuscript_title, m.manuscript_abstract, " +
		    "mt.manuscript_type, CONCAT(u.first_name,' ',u.last_name) AS author_name, pc.created_on, pc.url, pc.file_number " +
		    "FROM plagiarism_check pc " +
		    "INNER JOIN manuscript m ON m.manuscript_id = pc.manuscript_id " +
		    "INNER JOIN users u ON u.id = m.manuscript_author_id " +
		    "INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id " +
		    "WHERE pc.journal_id = :journalId " +
		    "AND (" +
		    "LOWER(m.manuscript_title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		    "LOWER(CONCAT(u.first_name, ' ', u.last_name)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		    "LOWER(m.manuscript_code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
		    ") " +
		    "ORDER BY pc.created_on DESC",
		    
		    countQuery = 
		    "SELECT COUNT(*) FROM plagiarism_check pc " +
		    "INNER JOIN manuscript m ON m.manuscript_id = pc.manuscript_id " +
		    "INNER JOIN users u ON u.id = m.manuscript_author_id " +
		    "INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id " +
		    "WHERE pc.journal_id = :journalId " +
		    "AND (" +
		    "LOWER(m.manuscript_title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		    "LOWER(CONCAT(u.first_name, ' ', u.last_name)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		    "LOWER(m.manuscript_code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
		    ")",
		    
		    nativeQuery = true)
		Page<Tuple> getReportByJournal(
		    @Param("journalId") Integer journalId,
		    @Param("searchTerm") String searchTerm,
		    Pageable pageable);


	
	@Query(value = "SELECT pc.manuscript_id, pc.manuscript_version, m.manuscript_code, "
			+ "m.manuscript_title, m.manuscript_abstract, mt.manuscript_type, "
			+ "CONCAT(u.first_name,' ',u.last_name) AS author_name, pc.created_on,"
			+ "pc.url, pc.file_number FROM plagiarism_check pc "
			+ "INNER JOIN manuscript m ON m.manuscript_id = pc.manuscript_id"
			+ " INNER JOIN users u ON u.id = m.manuscript_author_id "
			+ "INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id "
			+ "WHERE pc.journal_id = :journalId "
			+ "ORDER BY pc.created_on DESC",
		countQuery = "SELECT COUNT(*)"
				+ " FROM plagiarism_check pc "
				+ "INNER JOIN manuscript m ON m.manuscript_id = pc.manuscript_id "
				+ "WHERE pc.journal_id = :journalId",
		nativeQuery = true)
		Page<Tuple> getReportByJournalPage(Integer journalId, Pageable pageable);



	@Query("SELECT pc FROM PlagiarismCheck pc WHERE pc.manuscriptId=:manuscriptId AND pc.manuscriptVersion=:manuscriptVersion AND pc.fileNubmer=:fileNumber")
	PlagiarismCheck getReportByManuscriptAndNumber(Integer manuscriptId, Integer manuscriptVersion, Integer fileNumber);

	@Query("SELECT pc FROM PlagiarismCheck pc WHERE pc.manuscriptId=:manuscriptId AND pc.manuscriptVersion=:manuscriptVersion AND pc.fileNubmer=:isActive")
	PlagiarismCheck getDataByFileNumber(Integer manuscriptId, Integer manuscriptVersion, Integer isActive);

//	@Query("SELECT pc FROM PlagiarismCheck pc WHERE pc.manuscriptId=:manuscriptId AND pc.manuscriptVersion=:manuscriptVersion")
//	PlagiarismCheck getDataByFileNumber(Integer manuscriptId, Integer manuscriptVersion);

	@Query(value = "SELECT b.journal_title, count(*) as plagcheckcount, month(a.created_on) as checkmonth, year(a.created_on) as checkyear FROM plagiarism_check a, journal b  where a.journal_id = b.journal_id  group by b.journal_title, month(a.created_on), year(a.created_on)", nativeQuery = true)
	public List<Object[]> getAllPlagiarism();

	@Query(value = "SELECT b.journal_title, count(*) as plagcheckcount, month(a.created_on) as checkmonth, year(a.created_on) as checkyear FROM plagiarism_check a, journal b where a.journal_id = b.journal_id AND month(a.created_on)=:month AND year(a.created_on) =:year group by b.journal_title, month(a.created_on), year(a.created_on)", nativeQuery = true)
	public List<Object[]> getPlagiarismByMonthYear(Integer month, Integer year);

	@Query(value = "SELECT b.journal_title, count(*) as plagcheckcount, month(a.created_on) as checkmonth, year(a.created_on) as checkyear FROM plagiarism_check a, journal b where a.journal_id = b.journal_id AND year(a.created_on) =:year group by b.journal_title, month(a.created_on), year(a.created_on)", nativeQuery = true)
	public List<Object[]> getPlagiarismByYear(Integer year);

	@Query(value="SELECT * FROM plagiarism_check pc WHERE pc.url=''" ,nativeQuery = true )
	List<PlagiarismCheck> getListForURL();

	@Query(value = "SELECT count(*)  FROM plagiarism_check WHERE journal_id=:journalId AND year(created_on) =:year", nativeQuery = true)
	public Integer getYearlyPcount(Integer journalId ,Integer year );

	@Query(value = "SELECT count(*)  FROM plagiarism_check WHERE journal_id=:journalId AND month(created_on) =:month", nativeQuery = true)
	public Integer getMonthlyPcount(Integer journalId ,Integer month );
	
	@Modifying
	@Transactional
	@Query("DELETE FROM PlagiarismCheck p WHERE p.id = :id")
	void deleteByIdSafely(@Param("id") Integer id);

}
