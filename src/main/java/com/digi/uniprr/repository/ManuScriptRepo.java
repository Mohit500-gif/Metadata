package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.VO.ManuscriptIdCodeProjection;
import com.digi.uniprr.VO.PerCountryManuscriptVo;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;

@Repository
@Transactional
public interface ManuScriptRepo extends JpaRepository<ManuScript, Integer> {

	@Query("select m from ManuScript m where m.manuScriptId=:id And m.isActive=1")
	List<ManuScript> getManuScripById(Integer id);

	ManuScript findByManuscriptCode(String manuscriptCode);

	@Modifying
	@Query(value = "update manuscript m set m.manuscript_status_id=:manuscriptStatus, m.assigned_user=:assignedUser,m.assigned_user_role=:roleId where m.manuscript_id=:manuScriptId", nativeQuery = true)
	void updateStage1(Integer manuscriptStatus, Integer manuScriptId,Integer assignedUser, Integer roleId);

	@Modifying
	@Query(value = "update manuscript m set m.manuscript_status_id=:manuscriptStatus where m.manuscript_id=:manuScriptId", nativeQuery = true)
	void updateStage(Integer manuscriptStatus, Integer manuScriptId);

	@Modifying
	@Query(value = "update manuscript m set m.due_date=:dueDate where m.manuscript_id=:manuScriptId", nativeQuery = true)
	void updateDueDate(Integer manuScriptId, Date dueDate);

	@Modifying
	@Query(value = "update manuscript m set m.eic_folder=:EICFolder where m.manuscript_id=:manuScriptId", nativeQuery = true)
	void updateEICFolder(Integer manuScriptId,String EICFolder);

	@Modifying
	@Query(value = "update manuscript m set m.eic_folder=:EICFolder where m.manuscript_code IN :manuScriptId", nativeQuery = true)
	void updateEICFolderInBulk(List<String> manuScriptId,String EICFolder);

	@Query(value = "select m from ManuScript m where m.manuscriptStatusId=:manuscriptStatus")
	List<ManuScript> getListByStatusId(Integer manuscriptStatus);

	@Query(value = "select m from ManuScript m where m.manuscriptCode=:manuscriptCode")
	ManuScript getManuscriptByManuscriptCode(String manuscriptCode);

	@Query(value = "select * from manuscript where manuscript_id=:id", nativeQuery = true)
	List<ManuScript> trackManuScript(Integer id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM ManuScript m WHERE m.manuScriptId = :id")
	Optional<ManuScript> fetchManuScript(@Param("id") Integer id);
	
	@Query(value = "select * from manuscript where manuscript_id=:id and journal_id=:journalId and submission_date=:submissionDate", nativeQuery = true)
	List<ManuScript> gettrackManuScript(Integer id, Integer journalId, Date submissionDate);

	@Query(value = "select * from manuscript where submission_date between :submissionDateTo and :submissionDateFrom", nativeQuery = true)
	List<ManuScript> gettrackManuScriptDate(String submissionDateTo, String submissionDateFrom);

	@Query(value = "SELECT * FROM manuscript WHERE manuscript_title LIKE %:manuscriptTitle%", nativeQuery = true)
	List<ManuScript> gettrackManuScriptTitle(@Param("manuscriptTitle") String manuscriptTitle);

	@Query(value = "SELECT * FROM manuscript where manuscript_code=:manuscriptCode", nativeQuery = true)
	List<ManuScript> gettrackManuScriptCode(String manuscriptCode);

	@Query(value = "SELECT * FROM manuscript WHERE journal_id IN (SELECT journal_id FROM journal WHERE journal_title LIKE %:journalTitle%)", nativeQuery = true)
	List<ManuScript> gettrackJTitle(@Param("journalTitle") String journalTitle);

	@Query(value = "SELECT COUNT(m.manuscript_title) AS manuscriptCount FROM manuscript m WHERE is_active = 1 and m.manuscript_title=:manuscriptTitle AND m.journal_id=:journalId and  m.manuscript_id <> :manuscript_id and m.manuscript_status_id <> 16;", nativeQuery = true)
	int countTitleAllStatus(String manuscriptTitle, Integer journalId, Integer manuscript_id);
	
	@Query(value = "SELECT COUNT(m.manuscript_title) AS manuscriptCount FROM manuscript m WHERE m.is_active = 1 "
			+ "AND m.manuscript_title = :manuscriptTitle"
			+ "AND m.journal_id = :journalId "
			+ "AND m.manuscript_id <> :manuscript_id "
			+ "AND m.manuscript_status_id <> 1 "
			+ " and m.manuscript_status_id <> 16", nativeQuery = true)
	int countTitleExcludeIncomplete(String manuscriptTitle,
			                        Integer journalId,
			                        Integer manuscript_id);
	
	@Query(value = "SELECT m.manuscript_title FROM manuscript m WHERE is_active = 1 AND m.journal_id = :journalId AND m.manuscript_id <> :manuscriptId AND m.manuscript_status_id <> 1", nativeQuery = true)
	List<String> findTitlesByJournalIdExcludingManuscriptId(Integer journalId, Integer manuscriptId);

	/******** Get Track ManuScript By Auhtor *********/
	@Query(value = "select * from manuscript where manuscript_code=:manuscriptCode and manuscript_title=:manuscriptTitle and submission_date=:submissionDateFrom and and submission_date=:submissionDateTo", nativeQuery = true)
	List<ManuScript> gettrackManuScriptAuthor(String manuscriptCode, String manuscriptTitle, Date submissionDateFrom,
			Date submissionDateTo);

	/******* Reviewer DashBoard *******/

	@Query(value = "SELECT * FROM manuscript WHERE (manuscript_status_id=8 OR manuscript_status_id=66) AND manuscript_id IN (SELECT manuscript_id FROM manuscript_reviewer WHERE reviewer_id=:reviewerId)", nativeQuery = true)
	List<ManuScript> reviewerInvitationList(Integer reviewerId);

	@Query(value = "SELECT * FROM manuscript WHERE manuscript_status_id=12 AND manuscript_id IN (SELECT manuscript_id FROM manuscript_reviewer WHERE reviewer_id=:reviewerId)", nativeQuery = true)
	List<ManuScript> reviewerReviewList(Integer reviewerId);

	@Query(value = "SELECT * FROM manuscript WHERE manuscript_status_id in (14,15) AND manuscript_id IN (SELECT manuscript_id FROM manuscript_reviewer WHERE reviewer_id=:reviewerId)", nativeQuery = true)
	List<ManuScript> reviewerRevisionList(Integer reviewerId);

	@Query(value = "SELECT * FROM manuscript WHERE manuscript_status_id=20 AND manuscript_id IN (SELECT manuscript_id FROM manuscript_reviewer WHERE reviewer_id=:reviewerId)", nativeQuery = true)
	List<ManuScript> reviewerCompletedList(Integer reviewerId);

	@Modifying
	@Query(value = "UPDATE manuscript SET manuscript_status_id=12 WHERE manuscript_id=:id ", nativeQuery = true)
	void reviewerAccept(Integer id);

	@Modifying
	@Query(value = "UPDATE manuscript SET manuscript_status_id=13 WHERE manuscript_id=:id ", nativeQuery = true)
	void rejectReviewer(Integer id);

	/***** Author Dashboard ******/
	@Query(value = "SELECT * from manuscript WHERE manuscript_author_id=:authorId AND manuscript_status_id IN (1, 14, 15)", nativeQuery = true)
	List<ManuScript> authorTask(Integer authorId);

	@Query(value = "SELECT * from manuscript WHERE manuscript_author_id=:authorId AND manuscript_status_id IN (3, 8, 9, 10, 11, 12, 13)", nativeQuery = true)
	List<ManuScript> authorUnderReview(Integer authorId);

	@Query(value = "SELECT * from manuscript WHERE manuscript_author_id=:authorId AND manuscript_status_id IN (19, 32)", nativeQuery = true)
	List<ManuScript> authorManuscriptAccepted(Integer authorId);

	@Query(value = "SELECT * from manuscript WHERE manuscript_author_id=:authorId AND manuscript_status_id IN (18, 30)", nativeQuery = true)
	List<ManuScript> authorManuscriptRejected(Integer authorId);

	@Query("SELECT m from ManuScript m WHERE m.manuscriptStatusId =2 AND m.journalId=:id")
	List<ManuScript> getEditorSubmitted(Integer id);

	@Query("SELECT m from ManuScript m WHERE m.manuscriptStatusId =26 AND m.journalId=:id")
	List<ManuScript> getEditorReSubmitted(Integer id);

	@Query("SELECT mr from ManuScriptReviewer mr WHERE (mr.manuscriptStatus =8 OR mr.manuscriptStatus =35 OR mr.manuscriptStatus =66)  AND mr.journalId=:id")
	List<ManuScriptReviewer> getEditorSentForReview(Integer id);

	@Query(value = "SELECT mr from ManuScriptReviewer mr WHERE mr.manuscriptStatus =14 OR mr.manuscriptStatus =15 OR mr.manuscriptStatus =37 OR mr.manuscriptStatus =38 AND mr.journalId=:id")
	List<ManuScriptReviewer> getEditorAllList(Integer id);


	@Query(value = "SELECT manuscript_id FROM manuscript WHERE manuscript_id IN (SELECT manuscript_id FROM manuscript_reviewer WHERE author_id=:reviewerId)", nativeQuery = true)
	List<Integer> getManuscriptIdListByReviewerId(Integer reviewerId);

	@Query("select m from ManuScript m where m.manuscriptStatusId=:manuscriptStatusId AND m.isActive=1  AND m.journalId=:journalId ORDER BY m.submissionDate DESC")
	List<ManuScript> getManuscriptByStatus(Integer manuscriptStatusId, Integer journalId);
	
	@Query(value = "select manuscript_status_id from manuscript where manuscript_code=:manuscriptCode",nativeQuery = true)
	Integer getManuscriptStatus(String manuscriptCode);

	@Query("select m from ManuScript m where m.manuscriptStatusId=:manuscriptStatusId AND m.isActive=1  AND m.journalId=:journalId AND m.manuScriptId In:manuscriptId ORDER BY m.submissionDate DESC")
	List<ManuScript> getManuscriptByStatusList(Integer manuscriptStatusId, Integer journalId,List<Integer> manuscriptId);

	///
	@Modifying(clearAutomatically = true)
	@Query("update ManuScript wrs set wrs.manuscriptStatusId =:statusCode where wrs.manuScriptId =:manuscriptId and wrs.manuscriptVersion=:manuscriptVersion")
	void updateManuScriptEditor(Integer manuscriptId, Integer manuscriptVersion, Integer statusCode);

	@Query(value = "SELECT * FROM manuscript a WHERE a.journal_id=:journalId", nativeQuery = true)
	List<ManuScript> getUniqueManuScript(Integer journalId);

	@Query(value = "select m from ManuScript m where m.manuScriptId=:id")
	List<ManuScript> getAllDetail(Integer id);

	@Query(value = "select m from ManuScript m where m.manuScriptId=:manuscriptId")
	List<ManuScript> getAllDetails(Integer manuscriptId);

	@Query(value = "select m from ManuScript m where m.authorId=:userId AND m.journalId=:journalId AND m.manuscriptStatusId=:statusId AND m.isActive=1")
	List<ManuScript> getManuScriptDataByID(Integer userId, Integer journalId, Integer statusId);

	@Query(value = "select m from ManuScript m where m.assignedUser=:userId AND m.journalId=:journalId AND m.manuscriptStatusId=:statusId AND m.isActive=1")
	List<ManuScript> getManuScriptDataByUserId(Integer userId, Integer journalId, Integer statusId);

	@Query(value = "select m from ManuScript m where m.journalId=:journalId AND m.manuscriptStatusId=:statusId AND m.isActive=1")
	List<ManuScript> getManuScriptWithdrawal(Integer journalId, Integer statusId);

	@Query(value = "select m from ManuScript m where m.journalId=:journalId AND m.manuscriptStatusId=:manuStatusId")
	List<ManuScript> getManuScriptDataByStatus(Integer journalId, Integer manuStatusId);

	@Query("SELECT m from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId ORDER BY m.submissionDate DESC")
	List<ManuScript> authorDashboardData(Integer authorId, List<Integer> statusList,Integer journalId);

    @Query("SELECT m from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId")
    List<ManuScript> authorDashboardArticle(Integer authorId, List<Integer> statusList, Integer journalId, Pageable paging);

//	@Query("SELECT m from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId")
//	List<ManuScript> authorDashboardArticle(Integer authorId, List<Integer> statusList,Integer journalId, Pageable paging);

    @Query("SELECT m from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.withdrawnRequestDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.updatedOn LIKE %:search% OR m.dueDate LIKE %:search%)")
    List<ManuScript> authorDashboardSearchArticle(Integer authorId, List<Integer> statusList, Integer journalId, String search, Pageable paging);

//	@Query("SELECT m from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.withdrawnRequestDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.updatedOn LIKE %:search% OR m.dueDate LIKE %:search%)")
//	List<ManuScript> authorDashboardSearchArticle(Integer authorId, List<Integer> statusList,Integer journalId, String search, Pageable paging);

    @Query("SELECT COUNT(m) from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId")
    Integer authorDashboardArticleCount(Integer authorId, List<Integer> statusList, Integer journalId);

//	@Query("SELECT COUNT(m) from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId")
//	Integer authorDashboardArticleCount(Integer authorId, List<Integer> statusList,Integer journalId);

    @Query("SELECT COUNT(m) from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.withdrawnRequestDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.updatedOn LIKE %:search% OR m.dueDate LIKE %:search%)")
    Integer authorDashboardSearchArticleCount(Integer authorId, List<Integer> statusList, Integer journalId, String search);

//	@Query("SELECT COUNT(m) from ManuScript m WHERE m.authorId=:authorId And m.isActive=1 AND m.manuscriptStatusId IN :statusList AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.withdrawnRequestDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.updatedOn LIKE %:search% OR m.dueDate LIKE %:search%)")
//	Integer authorDashboardSearchArticleCount(Integer authorId, List<Integer> statusList,Integer journalId, String search);

	@Modifying(clearAutomatically = true)
	@Query("update ManuScript wrs set wrs.manuscriptStatusId =:statusId where wrs.manuScriptId =:manuscriptId and wrs.authorId=:authorId")
	void updateManuScriptByAuthor(Integer manuscriptId, Integer authorId, Integer statusId);

	@Modifying(clearAutomatically = true)
	@Query("update ManuScript wrs set wrs.manuscriptStatusId =:statuscode where wrs.manuScriptId =:manuStatusId")
	void updateManuScriptStatusById(Integer manuStatusId, Integer statuscode);

	@Query("select m from ManuScript m where m.manuscriptStatusId IN :manuStatusId AND m.journalId IN (SELECT j.journalId from Journal j where j.journalId IN (select uj.journalId from UserJournal uj where uj.userId = :userId))")
	List<ManuScript> getManuscriptByUser(Integer userId, List<Integer> manuStatusId);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId")
	List<ManuScript> getManuscriptByJournalList(List<Integer> journalId);

	@Query("Select m from ManuScript m where m.journalId =:journalId AND m.isActive=1")
	List<ManuScript> getManuScriptByJournalId(Integer journalId);

	@Query("Select m from ManuScript m where m.manuScriptId=:id AND m.manuscriptVersion=:version")
	List<ManuScript> getManuScripByVersion(Integer id, Integer version);

	@Query("Select m from ManuScript m where m.authorId=:id")
	List<ManuScript> getManuScripAuthorId(Integer id);

	@Query("SELECT m FROM ManuScript m WHERE m.manuScriptId IN :id")
	List<ManuScript> getManuscriptList(List<Integer> id);

	@Query("SELECT m FROM ManuScript m WHERE m.manuscriptTitle =:title AND m.isActive=1")
	List<ManuScript> titleIsExist(String title);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId =:journalId AND m.manuscriptVersion=0 AND m.isActive=1  AND m.user IS NOT NULL AND YEAR(m.submissionDate)=:year")
	List<ManuScript> getArticlesSubmittedOnJournalOfZeroVersion(Integer journalId,Integer year);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId =:journalId AND m.isActive=1  AND m.user IS NOT NULL AND YEAR(m.submissionDate)=:year")
	List<ManuScript> getArticlesSubmittedOnJournal(Integer journalId,Integer year);
	//1
	@Query(value="SELECT  a.manuscript_code ,a.manuscript_title FROM manuscript a WHERE  a.journal_id=:journalId AND year(a.submission_date)=:year AND a.submission_date IS NOT NULL  ORDER BY a.submission_date asc",nativeQuery = true)
	List<Object[]> TotalNumberOfArticlesSubmitted(Integer journalId,Integer year);
	//2
	@Query(value = "WITH FirstSubmission AS (SELECT  a.manuscript_id, MIN(a.created_at) AS submission_dt\r\n"
			+ "FROM authorities_history a WHERE a.journal_id =:journalId AND a.manuscript_status_id = 2 \r\n"
			+ "AND a.manuscript_id IS NOT NULL  GROUP BY a.manuscript_id),InviteDates \r\n"
			+ "AS (SELECT a.manuscript_id,  a.created_at AS invite_dt FROM authorities_history a WHERE a.journal_id =:journalId \r\n"
			+ "AND a.manuscript_status_id IN (8) AND YEAR(a.created_at) =:year AND a.manuscript_id IS NOT NULL\r\n"
			+ ")SELECT sub.manuscript_id, sub.submission_dt, inv.invite_dt\r\n"
			+ "FROM FirstSubmission sub LEFT JOIN InviteDates inv\r\n"
			+ "ON sub.manuscript_id = inv.manuscript_id  WHERE inv.invite_dt IS NOT NULL",nativeQuery = true)
	List<Object[]> NumberOfDaysFromSubmissionToInvitingReviewers(Integer journalId,Integer year);
	//3
	@Query(value = "SELECT b.manuscript_id,b.assigned_date AS invite_dt ,b.complete_date\r\n"
			+ ",DATEDIFF(b.complete_date,b.assigned_date) AS days_diff FROM manuscript_reviewer b WHERE  b.manuscript_status IN (12,13,14,15)  AND b.journal_id=:journalId\r\n"
			+ "AND manuscript_version =0 AND year(b.complete_date)=:year", nativeQuery = true)
	List<Object[]> getInviteAndCompleteDates(Integer journalId,Integer year);
	//4
	@Query(value = "SELECT  a.manuscript_id, a.created_at AS rev_complete_dt,b.created_at AS decision_dt\r\n"
			+ "FROM authorities_history a INNER JOIN authorities_history b ON a.manuscript_id = b.manuscript_id\r\n"
			+ "INNER JOIN manuscript m ON a.manuscript_id = m.manuscript_id WHERE \r\n"
			+ "a.journal_id =:journalId AND a.manuscript_status_id IN (12, 13, 14, 15) AND b.manuscript_status_id IN (30, 33 ,63)\r\n"
			+ "AND YEAR(b.created_at) =:year AND a.manuscript_version = 0 AND NOT EXISTS (\r\n"
			+ "SELECT 1 FROM authorities_history c WHERE c.manuscript_id = a.manuscript_id AND c.manuscript_status_id = 4\r\n"
			+ "AND c.created_at BETWEEN a.created_at AND b.created_at)",nativeQuery = true)
	List<Object[]> getFilteredDecisionData(Integer journalId,Integer year);
	//5
	@Query(value = "SELECT COALESCE(subm.manuscript_id, firstDescion.manuscript_id) AS manuscript_id,\r\n"
			+ "subm.subm_dt,firstDescion.firstDescion_dt FROM (SELECT a.manuscript_id, \r\n"
			+ "a.previous_user_id,a.created_at AS subm_dt FROM authorities_history a \r\n"
			+ "WHERE a.journal_id =:journalId AND a.manuscript_status_id IN (2) GROUP BY \r\n"
			+ "a.manuscript_id) AS subm LEFT JOIN (SELECT a.manuscript_id,a.current_user_id,\r\n"
			+ "MIN(a.created_at) AS firstDescion_dt, a.manuscript_status_id FROM authorities_history a \r\n"
			+ "WHERE a.journal_id =:journalId AND a.manuscript_status_id IN (4, 30, 33) \r\n"
			+ "AND YEAR(a.created_at) =:year GROUP BY a.manuscript_id) AS firstDescion \r\n"
			+ "ON subm.manuscript_id = firstDescion.manuscript_id WHERE subm.manuscript_id IS NOT NULL \r\n"
			+ "AND DATE(firstDescion.firstDescion_dt) >= DATE(subm.subm_dt) AND firstDescion.firstDescion_dt \r\n"
			+ "IS NOT NULL AND subm.manuscript_id NOT IN (SELECT DISTINCT manuscript_id \r\n"
			+ "FROM authorities_history WHERE journal_id =:journalId AND manuscript_status_id IN (4, 30, 33) \r\n"
			+ "AND YEAR(created_at) !=:year)ORDER BY subm.manuscript_id",nativeQuery = true)
	List<Object[]> getSubmissionToFirstDecisionData(Integer journalId,Integer year);
////////////////////////////////////////////////////////////////////////////////////////////////////////	
	//6
	@Query(value = "WITH RankedRevisions AS ( SELECT  a1.manuscript_id,a1.created_at AS revision,a2.created_at AS revision_com,\r\n"
			+ " ROW_NUMBER() OVER (PARTITION BY a1.manuscript_id ORDER BY a1.created_at ASC) AS row_num FROM \r\n"
			+ " authorities_history a1 JOIN  authorities_history a2 ON a1.manuscript_id = a2.manuscript_id\r\n"
			+ " WHERE YEAR(a2.created_at) =:year AND a1.manuscript_status_id = 4 AND a2.manuscript_status_id = 26\r\n"
			+ "  AND a1.created_at < a2.created_at AND a1.journal_id =:journalId\r\n"
			+ "),FilteredRows AS (SELECT a.manuscript_id, a.created_at AS editor_rev,\r\n"
			+ " ROW_NUMBER() OVER (PARTITION BY a.manuscript_id ORDER BY a.created_at ASC) AS row_num FROM\r\n"
			+ "  authorities_history a WHERE YEAR(a.created_at) =:year \r\n"
			+ "AND a.journal_id =:journalId AND a.created_at > (SELECT MIN(revision_com)  FROM  RankedRevisions r \r\n"
			+ "WHERE r.manuscript_id = a.manuscript_id ))\r\n"
			+ "SELECT r.manuscript_id,r.revision,r.revision_com,f.editor_rev FROM RankedRevisions r\r\n"
			+ "LEFT JOIN FilteredRows f ON r.manuscript_id = f.manuscript_id AND r.row_num = f.row_num\r\n"
			+ "WHERE r.row_num = 1 ORDER BY r.revision",nativeQuery = true)
	List<Object[]> getAuthorRevisionToCompletionData(Integer journalId,Integer year);
	
	@Query(value = "SELECT distinct a.manuscript_id,a.manuscript_version,a.created_at AS revision_dt FROM authorities_history a WHERE a.journal_id=:journalId AND a.manuscript_status_id=4 AND a.manuscript_version=0",nativeQuery = true)
	List<Object[]> findFirstRevision(Integer journalId);
	
	@Query(value = "SELECT DISTINCT  b.manuscript_id,b.manuscript_version,b.created_at AS revision_com_dt FROM authorities_history b WHERE b.journal_id=:journalId AND b.manuscript_status_id=26 AND year(b.created_at)=:year AND b.manuscript_version=1",nativeQuery = true)
	List<Object[]> findFirstRevisionCompletedByYear(Integer journalId,Integer year);
	////////////////////7////////////////////////////////////////////////////////////////////////////
	@Query(value = "SELECT ah.manuscript_id, MIN(ah.created_at) AS first_revision_completed\r\n"
			+ "FROM authorities_history ah\r\n"
			+ "WHERE ah.manuscript_status_id = 26 AND ah.journal_id =:journalId\r\n"
			+ "GROUP BY ah.manuscript_id;",nativeQuery = true)
	List<Object[]> findFirstRevisionCompleted(Integer journalId);
	
	@Query(value = "SELECT ah.manuscript_id, MIN(ah.created_at) AS first_descion_dt_before_2024\r\n"
			+ "FROM authorities_history ah\r\n"
			+ "WHERE ah.manuscript_status_id IN (30,33) OR (ah.manuscript_status_id = 4 AND ah.manuscript_version = 1)\r\n"
			+ "AND ah.journal_id = :journalId\r\n"
			+ "AND YEAR(ah.created_at) < :year\r\n"
			+ "GROUP BY ah.manuscript_id;",nativeQuery = true)
	List<Object[]> findFirstDecisionBeforeYear(Integer journalId,Integer year); 
	
	@Query(value = "SELECT ah.manuscript_id, MIN(ah.created_at) AS first_descion_dt_2024\r\n"
			+ "FROM authorities_history ah\r\n"
			+ "WHERE ah.manuscript_status_id IN (30,33) OR (ah.manuscript_status_id = 4 AND ah.manuscript_version = 1)\r\n"
			+ "AND ah.journal_id = :journalId\r\n"
			+ "AND YEAR(ah.created_at) = :year\r\n"
			+ "GROUP BY ah.manuscript_id",nativeQuery = true)
	List<Object[]> findFirstDecisionInYear(Integer journalId,Integer year); 
/////////////////////////////////////////////////////////////////////////////////////////////////////////	
	@Query(value ="SELECT DISTINCT m.manuscript_id, m.manuscript_code, m.manuscript_title  FROM authorities_history a LEFT JOIN manuscript m ON a.manuscript_id = m.manuscript_id WHERE a.journal_id=:journalId AND a.manuscript_status_id IN (66) AND year(a.created_at)=:year",nativeQuery = true)
	List<Object[]> getTotalNumberOFArticleReview(Integer journalId, Integer year);
	
	@Query(value = "WITH RankedRevisions AS (\r\n"
			+ "SELECT a1.manuscript_id,a1.created_at AS re_REV,a2.created_at AS REVIWED,\r\n"
			+ "ROW_NUMBER() OVER (PARTITION BY a1.manuscript_id ORDER BY a1.created_at ASC) AS row_num\r\n"
			+ "FROM authorities_history a1 JOIN authorities_history a2 ON a1.manuscript_id = a2.manuscript_id\r\n"
			+ "WHERE year(a1.created_at)=:year \r\n"
			+ "AND a1.manuscript_status_id = 66  \r\n"
			+ "AND a2.manuscript_status_id IN (12,13,14,15) \r\n"
			+ "AND a1.created_at < a2.created_at AND a1.journal_id =:journalId\r\n"
			+ ")SELECT manuscript_id,re_REV,REVIWED FROM RankedRevisions WHERE row_num = 1",nativeQuery = true)
	List<Object[]> getReReviewsComplete(Integer journalId, Integer year);
	
    // Query for the first status (66) - Review status
    @Query(value = "SELECT distinct a.manuscript_id,a.created_at,a.current_user_id  FROM authorities_history a WHERE a.manuscript_status_id = 66 AND a.journal_id =:journalId order by id asc",nativeQuery = true)
    List<Object[]> findReviewStatus(Integer journalId);
	
	@Query(value = "WITH RankedRevisions AS (SELECT a1.manuscript_id,(SELECT MIN(a2.created_at)FROM authorities_history a2\r\n"
			+ "WHERE a2.manuscript_id = a1.manuscript_id AND a2.manuscript_status_id = 26 \r\n"
			+ "AND a2.created_at >= a1.created_at) AS revision_completed,\r\n"
			+ "ROW_NUMBER() OVER (PARTITION BY a1.manuscript_id ORDER BY a1.created_at ASC) AS row_num\r\n"
			+ "FROM authorities_history a1 WHERE a1.manuscript_status_id = 4),\r\n"
			+ "FinalDecisions AS (SELECT manuscript_id,MIN(created_at) AS final_decision_date FROM authorities_history\r\n"
			+ "WHERE manuscript_status_id IN (30, 33) AND journal_id =:journalId AND year(created_at)=:year GROUP BY manuscript_id)\r\n"
			+ "SELECT r.manuscript_id,r.revision_completed AS second_revision_completion_date,d.final_decision_date FROM RankedRevisions r \r\n"
			+ "JOIN FinalDecisions d ON r.manuscript_id = d.manuscript_id WHERE r.row_num = 2 AND r.revision_completed IS NOT NULL \r\n"
			+ "AND d.final_decision_date >= r.revision_completed ORDER BY r.manuscript_id",nativeQuery = true)
	List<Object[]> getSecondRevisionCompletionToFinalDecision(Integer journalId, Integer year);
	
	@Query(value = "SELECT a.manuscript_id, a.created_at AS submission_dt, b.created_at AS final_dec_dt\r\n"
			+ "FROM authorities_history a INNER JOIN authorities_history b ON a.manuscript_id = b.manuscript_id\r\n"
			+ "WHERE a.journal_id =:journalId AND a.manuscript_status_id = 2 AND b.manuscript_status_id IN (30, 33)\r\n"
			+ "AND YEAR(b.created_at) =:year",nativeQuery = true)
	List<Object[]> getSubmissionToFinalDecision(Integer journalId, Integer year);
	
	@Query(value = "SELECT m.manuscript_id, m.submission_date,m.production_date AS acceptance_dt FROM manuscript m \r\n"
			+ "WHERE m.journal_id=:journalId AND year(m.production_date)=:year AND m.production_date IS NOT NULL",nativeQuery = true)
	List<Object[]> getSubmissionToAcceptance(Integer journalId, Integer year);
	
	@Query(value = "SELECT m.manuscript_id,m.production_date AS acceptance_dt, m.publishing_date FROM manuscript m \r\n"
			+ "WHERE m.journal_id=:journalId AND year(m.publishing_date)=:year AND m.publishing_date IS NOT NULL",nativeQuery = true)
	List<Object[]> getAcceptanceToPublication(Integer journalId, Integer year);
	
	@Query(value = "SELECT m.manuscript_id, m.submission_date,m.publishing_date FROM manuscript m \r\n"
			+ "WHERE m.journal_id=:journalId AND year(m.publishing_date)=:year AND m.production_date IS NOT NULL ",nativeQuery = true)
	List<Object[]> getSubmissionToPublication(Integer journalId, Integer year);


	@Query("SELECT m FROM ManuScript m WHERE m.journalId=:journalId AND m.isActive=1 AND YEAR(m.submissionDate)=:year GROUP BY m.authorId")
	List<ManuScript> getTotalNumberOfAuthor(Integer journalId,Integer year);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND m.isActive=1 AND YEAR(m.finalDecisionDate) IN :year AND m.manuscriptStatusId=33")
	List<ManuScript> getAcceptedArticlesList(List<Integer> journalId,List<Integer> year);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND m.isActive=1 AND YEAR(m.finalDecisionDate) IN :year AND m.manuscriptStatusId=30")
	List<ManuScript> getRejectedArticleList(List<Integer> journalId,List<Integer> year);

	@Query("SELECT m FROM ManuScript m WHERE m.manuScriptId IN :manuscriptId AND YEAR(m.submissionDate) IN :year AND m.journalId IN :journalId AND m.isActive=1")
	List<ManuScript> getPerformanceForYearList(List<Integer> manuscriptId,List<Integer> journalId,List<Integer> year);

	@Query("SELECT m FROM ManuScript m WHERE m.manuScriptId IN :manuscriptId  AND m.journalId IN :journalId  AND YEAR(m.submissionDate) IN :year AND MONTH(m.submissionDate)=:month AND m.isActive=1")
	List<ManuScript> getPerformanceForYearAndMonthList(List<Integer> manuscriptId,List<Integer> journalId,List<Integer> year,Integer month);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND m.isActive=1")
	List<ManuScript> getManuscriptList(List<Integer> journalId,List<Integer> year);

	//@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND m.isActive=1")

	@Query(value="SELECT concat(ROUND(( COUNT(m.manuscript_origin )  * 100 /(SELECT COUNT(manuscript_origin) FROM manuscript  WHERE journal_id IN :journalId AND YEAR(submission_date) IN :year AND is_active=1)),2),'%') AS abroadPercentage FROM manuscript m WHERE m.journal_id IN :journalId AND YEAR(m.submission_date) IN :year AND m.is_active=1 AND m.manuscript_origin NOT in ('NA','India','') ",nativeQuery = true)
	String getNumberOfManuscriptsFromAbroadPercentList(List<Integer> journalId,List<Integer> year);

	@Query(value="SELECT concat(ROUND(( COUNT( m.manuscript_origin )  * 100 /(SELECT COUNT(manuscript_id) FROM manuscript  WHERE journal_id IN :journalId AND YEAR(submission_date) IN :year AND is_active=1)),2),'%') AS percentage FROM manuscript m WHERE m.journal_id IN :journalId AND YEAR(m.submission_date) IN :year AND m.is_active=1 AND m.manuscript_origin NOT in ('NA','India','') AND m.assigned_user IN (SELECT id FROM users WHERE country_id !=101 OR country_id !='') AND m.manuscript_id IN (SELECT manuscript_id FROM manuscript_typedetails WHERE manuscript_type='Original Article') ",nativeQuery = true)
	String getNumberOfManuscriptsOrignalFromAbroadPercentList(List<Integer> journalId,List<Integer> year);

	@Query(value="SELECT COUNT(manuscript_id) AS NumberOriginalArticleAbroad FROM manuscript m WHERE m.journal_id IN :journalId AND YEAR(m.submission_date) IN :year AND m.is_active=1 AND m.manuscript_origin NOT in ('NA','India','') AND  m.manuscript_id IN (SELECT manuscript_id FROM manuscript_typedetails WHERE manuscript_type ='Original Article') and m.assigned_user IN (SELECT id FROM users WHERE country_id IN (SELECT c.id FROM countries c,journal j WHERE c.name=j.country_name AND c.id !=101 OR c.id !='')) ",nativeQuery = true)
	int getNumberOfManuscriptsOrignalFromAbroadList(List<Integer> journalId,List<Integer> year);

	//@Query("SELECT new com.digi.uniprr.VO.PerCountryManuscriptVo(COUNT(m.manuScriptId) AS manuscriptCount ,m.manuscriptOrigin AS country) FROM ManuScript m WHERE m.journalId IN :journalId  AND YEAR(m.submissionDate) IN :year AND MONTH(m.submissionDate)=:month AND m.isActive=1 AND m.manuscriptStatusId!=1 GROUP BY m.manuscriptOrigin")
	@Query("SELECT new com.digi.uniprr.VO.PerCountryManuscriptVo(COUNT(r.reviewerId) AS manuscriptCount,c.name AS country) FROM ManuScriptReviewer r,User u,Countries c WHERE r.manuscriptStatus IN (12,13,14,15) AND r.journalId IN :journalId AND YEAR(r.assignedDate) IN :year AND MONTH(r.assignedDate)=:month AND u.id=r.reviewerId AND c.id=u.countryId AND r.isActive=1 GROUP BY u.countryId")
	List<PerCountryManuscriptVo> getManuscriptListPerCountryReviewer(List<Integer> journalId,List<Integer> year,Integer month);


	@Query("SELECT new com.digi.uniprr.VO.PerCountryManuscriptVo(COUNT(m.manuScriptId) AS manuscriptCount ,m.manuscriptOrigin AS country) FROM ManuScript m WHERE m.journalId IN :journalId  AND YEAR(m.submissionDate) IN :year AND MONTH(m.submissionDate)=:month AND m.isActive=1 AND m.manuscriptStatusId!=1 GROUP BY m.manuscriptOrigin")
	List<PerCountryManuscriptVo> getManuscriptListPerCountry(List<Integer> journalId,List<Integer> year,Integer month);

	@Query("SELECT new com.digi.uniprr.VO.PerCountryManuscriptVo(COUNT(r.reviewerId) AS manuscriptCount,c.name AS country) FROM ManuScriptReviewer r,User u,Countries c WHERE r.manuscriptStatus IN (12,13,14,15) AND r.journalId IN :journalId AND YEAR(r.assignedDate) IN :year AND u.id=r.reviewerId AND c.id=u.countryId AND r.isActive=1 GROUP BY u.countryId")
	List<PerCountryManuscriptVo> getManuscriptListPerCountryAllReviewer(List<Integer> journalId,List<Integer> year);

	@Query("SELECT new com.digi.uniprr.VO.PerCountryManuscriptVo(COUNT(m.manuScriptId) AS manuscriptCount ,m.manuscriptOrigin AS country) FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year  AND m.isActive=1 AND m.manuscriptStatusId!=1 GROUP BY m.manuscriptOrigin")
	List<PerCountryManuscriptVo> getManuscriptListPerCountryAll(List<Integer> journalId,List<Integer> year);
	
	@Query(value = "SELECT COUNT(DISTINCT m.manuscript_author_id) AS uniqueAuthors, "
//	        + "COUNT(*) - COUNT(DISTINCT m.manuscript_author_id) AS moreThanOneSubmitCount, "
	        + "SUM(CASE WHEN m.manuscript_origin NOT IN ('India','NA','') THEN 1 ELSE 0 END) AS fromAbroadCount, "
	        + "SUM(CASE WHEN m.manuscript_origin NOT IN ('India','NA','') AND td.manuscript_type = 'Original Article' THEN 1 ELSE 0 END) AS originalFromAbroadCount, "
	        + "CONCAT(ROUND(SUM(CASE WHEN m.manuscript_origin NOT IN ('India','NA','') THEN 1 ELSE 0 END) * 100.0 / COUNT(m.manuscript_id), 2), '%') AS fromAbroadPercentage, "
	        + "CONCAT(ROUND(SUM(CASE WHEN m.manuscript_origin NOT IN ('India','NA','') AND td.manuscript_type = 'Original Article' THEN 1 ELSE 0 END) * 100.0 / COUNT(m.manuscript_id), 2), '%') AS originalFromAbroadPercentage "
	        + "FROM manuscript m "
	        + "LEFT JOIN manuscript_typedetails td ON m.manuscript_id = td.manuscript_id "
	        + "WHERE m.journal_id IN :journalIds AND YEAR(m.submission_date) IN :years AND m.is_active = 1", 
	        nativeQuery = true)
	List<Object[]> getAuthorPerformanceSummary(@Param("journalIds") List<Integer> journalIds, @Param("years") List<Integer> years);

			
	@Query(value = "SELECT m.manuscript_origin AS country, COUNT(*) "
			+ "FROM manuscript m WHERE m.journal_id IN :journalIds AND " 
			+ "YEAR(m.submission_date) IN :years AND m.is_active = 1 "
			+ "ROUP BY m.manuscript_origin", nativeQuery = true)
	List<Object[]> getCountryWiseManuscriptStats(@Param("journalIds") List<Integer> journalIds, @Param("years") List<Integer> years);

			
	@Query(value = "SELECT u.id, u.first_name, u.last_name, COUNT(*) AS manuscript_count "
			+ "FROM manuscript m JOIN users u ON m.manuscript_author_id = u.id "
			+ "WHERE m.journal_id IN :journalIds AND YEAR(m.submission_date) IN :years AND m.is_active = 1 "
			+ "GROUP BY u.id HAVING COUNT(*) > 1", nativeQuery = true)
	List<Object[]> getAuthorsWithMoreThanOneSubmission(@Param("journalIds") List<Integer> journalIds, @Param("years") List<Integer> years);

//	@Query("SELECT new com.digi.uniprr.VO.PerAuthorManuscriptSubmitVo(COUNT(m.manuScriptId) AS manuscriptCount ,m.user AS authorId)  FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND m.isActive=1 GROUP BY m.user")
	@Query(value="SELECT COUNT(m.manuscript_id) , m.manuscript_author_id FROM manuscript m WHERE m.journal_id IN :journalI AND YEAR(m.submission_date) IN :yea  AND m.is_active=1 GROUP BY m.manuscript_author_id HAVING COUNT(m.manuscript_id) >1", nativeQuery = true)
	List<Object[]> getPerAuthorManuscriptSubmit(List<Integer> journalI,List<Integer> yea);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND MONTH(m.submissionDate)=:month AND m.isActive=1")
	List<ManuScript> getTotalNumberOFManuscriptSubmitted(List<Integer> journalId,List<Integer> year,Integer month);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND m.isActive=1")
	List<ManuScript> getTotalNumberOFManuscriptSubmittedInYear(List<Integer> journalId,List<Integer> year);

	@Query("SELECT m FROM ManuScript m WHERE m.journalId=:journalId AND m.manuscriptStatusId!=1")
	List<ManuScript> getManuscriptBYJournalId(Integer journalId);

	@Query(value ="SELECT * FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id!=1 AND m.manuscript_code IS NOT NULL ORDER  BY m.manuscript_id DESC LIMIT 1", nativeQuery = true)
	ManuScript getSingleManuscriptRecord(Integer journalId);

	// Use for making manuscript code
	@Query("SELECT m FROM ManuScript m WHERE m.journalId=:journalId AND m.manuscriptStatusId!=1 ORDER BY m.manuscriptCode DESC")
	List<ManuScript> getManuscriptBYJournalIdForMakingCode(Integer journalId);

	@Query("SELECT m FROM ManuScript m WHERE m.authorId IN :authorId AND YEAR(m.submissionDate) IN :year AND m.isActive=1")
	List<ManuScript> getMnauscriptListByAuthorId(List<Integer> authorId,List<Integer> year);

	/**************** // Reviewers Performance // *****************/

	@Query(value ="SELECT * FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5)  and Year(m.submission_date) IN :year  group by mr.reviewer_id ORDER BY mr.reviewer_id",nativeQuery = true)
	List<ManuScript> getNumberOFReviewerUsedNumberAll(List<Integer> journalId,List<Integer> year);

	@Query(value ="select * from manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5) AND YEAR(m.submission_date) IN :year  and mr.manuscript_status IN (12,13,14,15,30,35,36,44,60) GROUP BY mr.reviewer_id ORDER BY mr.reviewer_id ",nativeQuery = true)
	List<ManuScript> getNumberOfReviewersRespondedAll(List<Integer> journalId, List<Integer> year);


	@Query(value ="SELECT * FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5) and Year(m.submission_date) IN :year AND MONTH(m.submission_date)=:month group by mr.reviewer_id ORDER BY mr.reviewer_id",nativeQuery = true)
	List<ManuScript> getNumberOFReviewerUsedNumber(List<Integer> journalId,List<Integer> year,Integer month);

	@Query(value ="SELECT mr.reviewer_id FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId  and Year(m.submission_date) IN :year AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5) AND MONTH(m.submission_date)=:month group by mr.reviewer_id ORDER BY mr.reviewer_id",nativeQuery = true)
	List<Integer> getNumberOFReviewerUsedId(List<Integer> journalId,List<Integer> year,Integer month);


	@Query(value ="select * from manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5)  AND YEAR(m.submission_date) IN :year AND MONTH(m.submission_date)=:month and mr.manuscript_status IN (12,13,14,15) GROUP BY mr.reviewer_id ORDER BY mr.reviewer_id",nativeQuery = true)
	List<ManuScript> getNumberOfReviewersResponded(List<Integer> journalId, List<Integer> year,Integer month);

	@Query(value ="SELECT * FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND Year(mr.assigned_date) = Year(mr.complete_date)IN :year AND MONTH(mr.assigned_date)=:month",nativeQuery = true)
	List<ManuScript> getReviewersOnTime(List<Integer> journalId,List<Integer> year,Integer month);

	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND Year(mr.assigned_date) IN :year AND MONTH(mr.assigned_date)=:month AND mr.manuscript_status=8 ",nativeQuery = true)
	List<ManuScript> getRetrunLate(List<Integer> journalId,List<Integer> year, Integer month);

	@Query(value="SELECT * FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND Year(mr.due_date) = Year(mr.complete_date)IN :year AND MONTH(mr.due_date)=:month",nativeQuery = true)
	List<ManuScript> getNeverReturned(List<Integer> journalId, List<Integer> year, Integer month);

	/******** Monthly Statstics ******/
	//@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND MONTH(m.submissionDate)=:month AND m.manuscriptStatusId IN(12,13,14,15,39,30,33)")
	//List<ManuScript> getTotalNumberOFArticleReviewed(List<Integer> journalId, List<Integer> year,Integer month);
	@Query(value ="SELECT COUNT( DISTINCT (mr.manuscript_id)) FROM manuscript_reviewer mr JOIN manuscript mrr ON mrr.manuscript_id = mr.manuscript_id WHERE mr.journal_id = :journalId AND YEAR(mrr.submission_date) IN :year AND MONTH(mrr.submission_date)= :month AND mr.manuscript_status IN (12,13,14,15,39,30)",nativeQuery = true)
	Integer getTotalNumberOFArticleReviewed(List<Integer> journalId, List<Integer> year,Integer month);

//	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND m.manuscriptStatusId IN(12,13,14,15,39,30,33)")
//	List<ManuScript> getTotalNumberOFArticleReviewedInYear(List<Integer> journalId, List<Integer> year);
	@Query(value ="SELECT COUNT( DISTINCT (mr.manuscript_id)) FROM manuscript_reviewer mr JOIN manuscript mrr ON mrr.manuscript_id = mr.manuscript_id WHERE mr.journal_id = :journalId AND YEAR(mrr.submission_date) IN :year AND mr.manuscript_status IN (12,13,14,15,39,30)",nativeQuery = true)
	Integer getTotalNumberOFArticleReviewedInYear(List<Integer> journalId, List<Integer> year);

	@Query(value="SELECT * FROM manuscript m JOIN users u on  m.manuscript_author_id= u.id where m.manuscript_id=:manuId",nativeQuery = true)
	List<ManuScript> getAuthorListmanuscript(Integer manuId);

	@Query("SELECT m FROM ManuScript m WHERE m.manuScriptId IN :manuId AND m.manuscriptVersion IN :version")
	List<ManuScript> getManuscriptListByIdAndVersion(Integer manuId, Integer version);

	@Query("SELECT m FROM ManuScript m WHERE m.manuScriptId IN :manuId AND m.manuscriptVersion = :version")
	ManuScript getManuscriptByIdAndVersion(Integer manuId, Integer version);

	@Modifying
	@Query("UPDATE ManuScript m SET m.qrCodePath=:filepath,m.qrFileName=:filename WHERE m.manuScriptId IN :manuScriptId")
	void saveQrcode(Integer manuScriptId, String filepath, String filename);

	@Query("SELECT m FROM ManuScript m WHERE m.manuScriptId IN :manuscriptId")
	List<ManuScript> getSubmittedManuscriptsList(List<Integer> manuscriptId);

	@Query(value = "select m from ManuScript m where m.journalId=:journalId AND m.manuscriptStatusId=:manuStatusId AND m.isActive=1")
	List<ManuScript> getManuScriptDataByStatusIsActive(Integer journalId, List<Integer> manuStatusId);

	@Modifying(clearAutomatically = true)
	@Query("update ManuScript wrs set wrs.manuscriptStatusId =:statusCode, wrs.commentForProduction=:comments where wrs.manuScriptId =:manuscriptId and wrs.manuscriptVersion=:manuscriptVersion")
	void updateManuScriptEditorComment(Integer manuscriptId, Integer manuscriptVersion, Integer statusCode,
			String comments);

	@Query("SELECT m FROM ManuScript m INNER JOIN FETCH m.manuscriptTypeDetails j where m.isActive=1 AND m.journalId=:journalId AND TRIM(j.manuscriptType)=TRIM(:manutypeName)")
	List<ManuScript> getByManuscriptType(Integer journalId, String manutypeName);

	@Query("SELECT m FROM ManuScript m where m.manuScriptId=:id")
	ManuScript getById(Integer id);

	@Query(value="SELECT m.manuscript_title FROM manuscript m where m.manuscript_id=:id", nativeQuery=true)
	String getByIdForLteManuscriptTitile(Integer id);

	@Query(value="SELECT m.manuscript_author_id FROM manuscript m where m.manuscript_id=:id", nativeQuery=true)
	Integer getAuthorId(Integer id);

	@Query(value="SELECT u.user_email FROM manuscript m INNER JOIN users u ON u.id=m.manuscript_author_id where m.manuscript_id=:id", nativeQuery=true)
	String getAuthorEmail(Integer id);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.manuscriptCode IS NOT NULL AND m.isActive=1 GROUP BY TRIM(m.manuscriptTypeDetails.manuscriptType)")
	List<ManuScript> getManuscriptTypeByJournal(Integer journalId);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version, m.assigned_user_role, mt.manuscript_type , m.manuscript_status_id , m.is_active , m.due_date FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id WHERE m.journal_id =:journalId AND m.is_active=1", nativeQuery =true)
	List<Tuple> getManuscriptByJournal(Integer journalId);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version , m.manuscript_status_id , m.is_active , m.due_date FROM manuscript m  WHERE m.journal_id =:journalId AND m.assigned_user_role=:roleId AND m.assigned_user=:userId AND m.is_active=1 AND m.manuscript_code IS NOT NULL", nativeQuery =true)
	List<Tuple> getManuscriptByJournalRoleUSer(Integer journalId, Integer roleId, Integer userId);

	@Modifying(clearAutomatically = true)
	@Query("update ManuScript wrs set wrs.manuscriptStatusId =:statusCode, wrs.withdrawalComment=:comments where wrs.manuScriptId =:manuscriptId")
	void updateManuScriptStatusAndCommentById(Integer manuscriptId, Integer statusCode, String comments);

	@Query("SELECT m FROM ManuScript m Join FETCH m.manuscriptTypeDetails md where m.journalId=:journalId AND m.manuscriptStatusId=:manuId AND m.isActive=1 AND m.manuscriptCode IS NOT NULL AND TRIM(md.manuscriptType)=TRIM(:name)")
	List<ManuScript> getManuscriptBytype(String name, Integer journalId, Integer manuId);

	@Query("select m from ManuScript m where m.manuscriptStatusId=:mStatus AND m.journalId=:journalId AND m.assignedUser=:userId AND m.isActive=1 ORDER BY m.submissionDate DESC")
	List<ManuScript> getManuscriptByStatusAndUser(Integer mStatus, Integer journalId, Integer userId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ManuScript m SET m.assignedUser=:i , m.assignedUserRole=:assignedUserRole where m.manuScriptId=:manuScriptId")
	void updateAssignedId(Integer manuScriptId, Integer i,Integer assignedUserRole);

	@Query("SELECT m FROM ManuScript m Join FETCH m.manuscriptTypeDetails md where m.journalId=:journalId AND m.isActive=1 AND TRIM(md.manuscriptType)=TRIM(:name)")
	List<ManuScript> getManuscriptByManuscriptType(String name, Integer journalId);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.assignedUser=:userId AND m.isActive=1 AND m.manuscriptCode IS NOT null")
	List<ManuScript> getManuscriptByJournalAndId(Integer journalId, Integer userId);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.assignedUser=:userId AND m.isActive=1 AND m.manuscriptStatusId=8 AND m.manuscriptCode IS NOT null")
	List<ManuScript> getManuscriptByJournalAndIdEIC(Integer journalId, Integer userId);

	@Modifying(clearAutomatically = true)
	@Query("update ManuScript wrs set wrs.manuscriptStatusId =:statusCode, wrs.productionDate=:date, wrs.commentForProduction=:comments, wrs.waveOff=:waveOf where wrs.manuScriptId =:manuscriptId and wrs.manuscriptVersion=:manuscriptVersion")
	void updateManuScriptEditorComment(Integer manuscriptId, Integer manuscriptVersion, Integer statusCode,String comments, Date date,String waveOf );

	@Query("Select m from ManuScript m where m.journalId =:journalId AND m.manuscriptCode is not null AND m.isActive=1")
	List<ManuScript> getManuScriptByJournalIdActive(Integer journalId);

	@Query("Select m from ManuScript m where m.journalId =:journalId AND m.isActive=1 AND m.manuscriptStatusId IN (8,66) AND m.manuscriptCode IS NOT NULL")
	List<ManuScript> getAllReviewedManuscript(Integer journalId);

	@Query(value="SELECT * from manuscript m where m.submission_date BETWEEN :from AND :to AND m.manuscript_status_id!=1 AND  m.is_active=1 GROUP BY m.journal_id",nativeQuery = true)
	List<ManuScript> getUniqueJournalWhichHasSubmissionBetweenDate(Date from,Date to);

	@Query(value="Select COUNT(m.manuscript_id) from manuscript m where m.submission_date BETWEEN :from AND :to AND  m.journal_id=:journalId AND m.manuscript_status_id!=1 AND  m.is_active=1",nativeQuery = true)
	Integer getTotalSubmissionOnJournalBetweenDate(String from,String to,Integer journalId);

	@Query(value="Select COUNT(m.manuscript_id) from manuscript m where m.rejected_date BETWEEN :from AND :to AND  m.journal_id=:journalId AND m.manuscript_status_id=30 AND  m.is_active=1",nativeQuery = true)
	Integer getTotalRejectionOnJournalBetweenDate(String from,String to,Integer journalId);

	@Query(value="Select COUNT(m.manuscript_id) from manuscript m where m.production_date BETWEEN :from AND :to AND  m.journal_id=:journalId AND m.manuscript_status_id IN (33,57,59,61,62,65) AND  m.is_active=1",nativeQuery = true)
	Integer getTotalAcceptanceOnJournalBetweenDate(String from,String to,Integer journalId);

	@Query("Select COUNT(m.manuScriptId) from ManuScript m where m.journalId=:journalId AND m.manuscriptStatusId not IN (1,30,33,57,59,61,62,65) AND  m.isActive=1")
	Integer getTotalArticleInPeerReviewOnJournalBetweenDate(Integer journalId);

	@Query(value="Select COUNT(m.manuscript_id) from manuscript m where m.submission_date BETWEEN :from AND :to AND m.journal_id=:journalId AND m.manuscript_status_id not IN (1,30,33,57,59,61,62,65) AND  m.is_active=1",nativeQuery = true)
	Integer getTotalArticleInPeerReviewOnJournalBetweenDate(String from,String to,Integer journalId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ManuScript m SET m.assignedUser=:currentUserId WHERE m.assignedUser=:previoususerId OR m.assignedUser IS NULL AND m.journalId=:journalId AND m.manuscriptStatusId!=1")
	void updatePreviousAssginUserToCurrentAssignUsedId(Integer currentUserId,Integer previoususerId,Integer journalId);

	@Query("SELECT m.manuScriptId FROM ManuScript m WHERE m.assignedUser=:currentUserId AND m.journalId=:journalId AND m.manuscriptStatusId!=1")
	List<Integer> getAllManuscriptIdToUpdateAuthoritirsHistory(Integer currentUserId,Integer journalId);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id!=1 AND m.is_active=1",nativeQuery = true)
    Integer getTotalManuscriptCountOnJournalActive(Integer journalId);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id=33 AND m.is_active=1",nativeQuery = true)
    Integer getTotalAcceptedManuscriptCountOnJournalActive(Integer journalId);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id=30 AND m.is_active=1",nativeQuery = true)
    Integer getTotalRejectedManuscriptCountOnJournalActive(Integer journalId);

	@Query("select m from ManuScript m where m.manuscriptStatusId=:mStatus AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) ORDER BY m.submissionDate DESC")
	List<ManuScript> getManuscriptByJournalAndRole(Integer mStatus, Integer journalId, Integer userId, Integer roleId);


	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id WHERE m.manuscript_status_id=:mStatus AND m.journal_id=:journalId AND m.is_active=1 AND me.is_active=1 AND me.editor_id=:userId ORDER BY m.submission_date DESC", nativeQuery=true)
	List<ManuScript> getManuscriptByJournalEditor(Integer mStatus, Integer journalId, Integer userId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ManuScript m SET m.assignedUserRole=2 WHERE m.assignedUser=:userId AND m.journalId=:journalId AND m.manuscriptCode IS NOT NULL")
	void updateRoleByid(Integer userId, Integer journalId);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND m.manuscriptCode IS NOT null AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId)")
	List<ManuScript> getManuscriptByJournalAndIdAndRole(Integer journalId, Integer userId, Integer roleId);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.manuscriptCode IS NOT null AND m.assignedUserRole=:roleId")
	List<ManuScript> getManuscriptByJournalAndIdEICAndRole(Integer journalId, Integer roleId);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version,m.manuscript_title, m.assigned_user,mt.manuscript_type , m.submission_date, m.manuscript_status_id, concat(u.first_name,' ', u.last_name) as name, m.eic_folder, m.manuscript_author_id FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id INNER JOIN users u ON u.id=m.manuscript_author_id where m.journal_id=:journalId AND m.is_active=1 AND (m.manuscript_status_id=8 OR m.manuscript_status_id=66) AND m.assigned_user_role=:roleId",nativeQuery = true)
	List<Tuple> getManuscriptByJournalAndIdEICTuple(Integer journalId, Integer roleId);

//	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.manuscriptCode IS NOT null AND (m.noResponseNotification IS null OR m.noResponseNotification=0)")
//	List<ManuScript> getManuscriptForNoResponseNotf(Integer journalId);
	
	@Query(value = " SELECT m.* FROM manuscript m LEFT JOIN mail_queue mq "
			+ "ON m.manuscript_id = mq.manuscript_id "
			+ "AND m.manuscript_version = mq.manuscript_version "
			+ "AND mq.task_id = 64 WHERE m.journal_id = :journalId AND m.is_active = 1 "
			+ "AND m.manuscript_code IS NOT NULL AND m.manuscript_status_id IN (8, 66)"
			+ "AND mq.id IS NULL", nativeQuery = true)
			List<ManuScript> getNoResponseManuscriptsNotYetNotified(@Param("journalId") Integer journalId);


	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.manuscriptCode IS NOT null")
	List<ManuScript> getManuscriptForNoResponseReminder(Integer journalId);


	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.manuscriptCode IS NOT null AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId)")
	List<ManuScript> getManuscriptByJournalAndIdEICAndRole(Integer journalId, Integer userId, Integer roleId, Pageable paging);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.manuscriptCode IS NOT null AND m.assignedUser=:userId AND m.assignedUserRole=:roleId")
	List<ManuScript> getManuscriptByJournalAndIdEditorAndRole(Integer journalId, Integer userId, Integer roleId);


	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getManuscriptByJournalAndIdEICAndRoleByManuId(Integer journalId, Integer roleId,String search);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version,m.manuscript_title, m.assigned_user,mt.manuscript_type , m.submission_date, m.manuscript_status_id, concat(u.first_name,' ', u.last_name) as name, m.eic_folder, m.manuscript_author_id FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id INNER JOIN users u ON u.id=m.manuscript_author_id WHERE m.journal_id =:journalId AND m.is_active=1 AND (m.manuscript_status_id=8 OR m.manuscript_status_id=66) AND m.assigned_user_role=:roleId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%)", nativeQuery =true)
	List<Tuple> getManuscriptByJournalTuple(Integer journalId, Integer roleId,String search);


	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.assignedUser=:userId AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getManuscriptByJournalAndIdEditorAndRoleByManuId(Integer journalId, Integer userId, Integer roleId,String search);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.assignedUser=:userId AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getManuscriptByJournalAndIdEditorAndRoleByManuIdPaging(Integer journalId, Integer userId, Integer roleId,String search, Pageable paging);

	@Query("SELECT m FROM ManuScript m where m.isActive=1 AND m.manuscriptStatusId=:manuScriptId")
	List<ManuScript> getAllPayedManuscript(Integer manuScriptId);

	@Query(value = "SELECT COUNT(*) FROM users U,countries C WHERE U.COUNTRY_ID=C.id AND C.is_developed='N' AND U.ID=:userId", nativeQuery = true)
	String getDevelopingCountryStatus(Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN journal j ON j.journal_id=m.journal_id WHERE m.is_active=1 AND m.manuscript_status_id IN :statusId AND m.manuscript_code IS NOT NULL AND m.assigned_user_role=:roleId AND m.assigned_user IS NOT NULL AND j.is_active=1" , nativeQuery = true)
	List<ManuScript> getAllManuscriptByUserRole(Integer roleId, List<Integer> statusId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN journal j ON j.journal_id=m.journal_id WHERE m.is_active=1 AND m.manuscript_status_id IN :statusId AND m.manuscript_code IS NOT NULL AND m.assigned_user IS NOT NULL AND j.is_active=1" , nativeQuery = true)
	List<ManuScript> getAllManuscriptForAuthorByUserRole(List<Integer> statusId);

	@Query(value="SELECT m.journal_id,m.manuscript_id,m.manuscript_version,m.manuscript_status_id,m.manuscript_author_id,m.updated_on,jm.journal_email ,u.user_email, m.due_date, m.assigned_user FROM manuscript m INNER JOIN journal j ON j.journal_id=m.journal_id INNER JOIN journal_metadata jm ON jm.journal_id = j.journal_id INNER JOIN users u ON u.id=m.manuscript_author_id where m.is_active=1 AND m.manuscript_status_id IN :statusId AND m.manuscript_code IS NOT NULL AND m.assigned_user IS NOT NULL AND j.is_active=1",nativeQuery = true)
	List<Tuple> getAllManuscriptForReminder(List<Integer> statusId);
	
	@Query(value="SELECT m.journal_id,m.manuscript_id,m.manuscript_version,m.manuscript_status_id,m.manuscript_author_id,m.updated_on,jm.journal_email ,u.user_email,m.due_date,m.manuscript_code FROM manuscript m INNER JOIN journal j ON j.journal_id=m.journal_id INNER JOIN journal_metadata jm ON jm.journal_id = j.journal_id INNER JOIN users u ON u.id=m.manuscript_author_id where m.is_active=1 AND m.manuscript_status_id IN :statusId AND m.manuscript_code IS NOT NULL AND m.assigned_user IS NOT NULL AND j.is_active=1 AND DATE(m.due_date) < CURDATE()",nativeQuery = true)
	List<Tuple> getAllManuscriptForReminderForSchedular(List<Integer> statusId);
	
	@Query(value="SELECT m.journal_id,m.manuscript_id,m.manuscript_version,m.manuscript_status_id,m.manuscript_author_id,m.assigned_user,jm.journal_email ,u.user_email,m.due_date,m.manuscript_code FROM manuscript m INNER JOIN journal j ON j.journal_id=m.journal_id INNER JOIN journal_metadata jm ON jm.journal_id = j.journal_id INNER JOIN users u ON u.id=m.manuscript_author_id where m.is_active=1 AND m.manuscript_status_id IN :statusId AND m.manuscript_code IS NOT NULL AND m.assigned_user IS NOT NULL AND j.is_active=1 AND m.journal_id IN (7,259,385) AND DATE(m.due_date) < CURDATE()",nativeQuery = true)
	List<Tuple> getAllManuscriptForWarningSchedular(List<Integer> statusId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN journal j ON j.journal_id=m.journal_id WHERE m.is_active=1 AND m.manuscript_status_id IN :statusId AND m.manuscript_code IS NOT NULL AND m.assigned_user_role IN :roleId AND m.assigned_user IS NOT NULL AND j.is_active=1" , nativeQuery = true)
	List<ManuScript> getAllManuscriptForReviewerByUserRole(List<Integer> statusId, List<Integer> roleId);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id=:statusId AND m.is_active=1",nativeQuery = true)
    Integer getTotalSubmittedManuscriptCountOnJournalActive(Integer journalId, Integer statusId);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.assigned_user_role=:roleId AND m.manuscript_status_id=:statusId AND m.is_active=1",nativeQuery = true)
    Integer getTotalReSubmittedManuscriptCountOnJournalActive(Integer journalId, Integer roleId, Integer statusId);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id=:statusId AND m.assigned_user_role=:roleId AND m.is_active=1",nativeQuery = true)
    Integer getTotalSubmittedManuscriptCountByJournalIdAndStatusId(Integer journalId, Integer statusId, Integer roleId);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id=:statusId AND m.is_active=1",nativeQuery = true)
    Integer getTotalManuscriptCountByJournalIdAndStatusId(Integer journalId, Integer statusId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ManuScript m SET m.assignedUserRole=2,m.assignedUser=:userId,m.manuscriptStatusId=:statusId,m.updatedOn=CURDATE() WHERE m.manuScriptId=:manuscriptId")
	void updateForSendBackEic(Integer manuscriptId,Integer userId, Integer statusId);


	@Modifying(clearAutomatically = true)
	@Query("update ManuScript wrs set wrs.manuscriptStatusId =:statusCode, wrs.manuscriptStatusBeforeWithdrawn =:statusBeforeWithdrawn, wrs.withdrawalComment=:comments,withdrawnRequestDate = :withdrawnRequestDate where wrs.manuScriptId =:manuscriptId")
	void updateManuScriptStatusForWthdrawn(Integer manuscriptId, Integer statusCode, String comments, Integer statusBeforeWithdrawn,Date withdrawnRequestDate);

	@Query(value="SELECT COUNT(m.manuscript_id) AS id FROM manuscript m WHERE m.journal_id=:journalId AND m.manuscript_status_id IN :statusId AND m.is_active=1",nativeQuery = true)
    Integer getCountWithdrawnManuscript(Integer journalId, List<Integer> statusId);

	@Query("Select m from ManuScript m where m.manuscriptCode =:manuscripCode")
	ManuScript getManuScriptCode(String manuscripCode);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.assignedUser=:userId AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getManuscriptByJournalAndIdEICAndRoleSearch(Integer journalId, Integer userId, Integer roleId, String search);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND m.assignedUser=:userId AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getManuscriptJournalAndEditorAndRoleSearch(Integer journalId, Integer userId, Integer roleId, String search);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getManuscriptByJournalAndIdEditorAndRoleSearch(Integer journalId, Integer userId, Integer roleId, String search);

	@Query(value = "SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId)")
	List<ManuScript> getManuscriptByJournalAndIdEICAndRoleByManuIdPagination(Integer journalId, Integer userId, Integer roleId, Pageable paging);


	@Query(value = "SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND (m.manuscriptStatusId=8 OR m.manuscriptStatusId=66) AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getManuscriptByJournalAndIdEICAndRoleByManuIdPaginationSearch(Integer journalId, Integer userId, Integer roleId,String search, Pageable paging);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND m.manuscriptStatusId=3 AND m.assignedUser=:userId AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScript> getAssignedEditorPaginationSearch(Integer journalId, Integer userId, int roleId,String search, Pageable paging);

	@Query("SELECT m FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND m.manuscriptStatusId=3 AND m.assignedUser=:userId AND m.assignedUserRole=:roleId ")
	List<ManuScript> getAssignedEditorPagination(Integer journalId, Integer userId, int roleId, Pageable paging);

	@Query("SELECT COUNT(*) FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND m.manuscriptStatusId=3 AND m.assignedUser=:userId AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getAssignedEditorCountSearch(Integer journalId, Integer userId, int roleId,String search);

	@Query("SELECT COUNT(*) FROM ManuScript m where m.journalId=:journalId AND m.isActive=1 AND m.manuscriptStatusId=3 AND m.assignedUser=:userId AND m.assignedUserRole=:roleId ")
	Integer getAssignedEditorCount(Integer journalId, Integer userId, int roleId);

	@Query("select m from ManuScript m where m.journalId=:id and m.manuScriptId=:manuscript_id AND m.isActive=1")
	ManuScript getChecklistOnDatabase(Integer id, Integer manuscript_id);

	@Query(value = "SELECT * FROM manuscript WHERE manuscript_id = :manuscriptId",nativeQuery = true)
	ManuScript getFindByManuScriptDetail(Integer manuscriptId);
	
	@Query(value = "SELECT m.manuscript_code FROM manuscript m WHERE m.manuscript_id IN :ids",nativeQuery = true)
	List<String> getManuscriptCodesByIds(List<Integer> ids);
	
	@Query(value = "SELECT m.manuscript_code FROM manuscript m WHERE m.manuscript_id = :id",nativeQuery = true)
	String getManuscriptCodesById(Integer id);

	@Query(value = "SELECT eic_folder FROM manuscript WHERE manuscript_id = :manuscriptId",nativeQuery = true)
	String getEICFolder(Integer manuscriptId);

	@Query("select m from ManuScript m where m.manuScriptId NOT IN(33,57,61,65,59,2,1,56) AND m.manuscriptCode IS NOT NULL")
	List<ManuScript> getManuscriptForAFPCheck();

	@Query(value = "SELECT COUNT(m.manuscript_id)  FROM manuscript m WHERE m.journal_id IN :journalI AND YEAR(m.submission_date) IN :yea  AND m.is_active=1 AND m.manuscript_author_id=:authorId ", nativeQuery = true)
	Integer getPerAuthorManuscriptSubmitAuthorId(List<Integer> journalI, List<Integer> yea, Integer authorId);

	@Query(value = "SELECT COUNT(*) FROM (SELECT m.manuscript_author_id FROM manuscript m WHERE m.journal_id IN :journalId \r\n"
			+ "AND YEAR(m.submission_date) IN :year AND m.is_active = 1 GROUP BY m.manuscript_author_id "
			+ "HAVING COUNT(m.manuscript_id) > 1) AS sub", nativeQuery = true)
	Integer getPerAuthorManuscriptSubmitDetails(List<Integer> journalId, List<Integer> year);

//	@Query("SELECT m FROM ManuScript m WHERE m.journalId IN :journalId AND YEAR(m.submissionDate) IN :year AND m.manuscriptStatusId IN(12,13,14,15,39,30,33)")
//	List<ManuScript> getTotalNumberOFArticleReview(Integer journalId, Integer year);
	

	@Query(value = "SELECT avg(DATEDIFF(Date(production_date),Date(submission_date))) AS totalsum FROM manuscript m where m.journal_id =:journalId AND  YEAR(m.production_date) =:year AND  YEAR(m.submission_date) =:year and manuscript_status_id IN (33,57,59,61,62,65)", nativeQuery = true)
	Double getTotalNumberOFDaysSubmissionToAcceptance(Integer journalId,Integer year);

	@Query(value = "SELECT CASE WHEN max(DATEDIFF(Date(production_date),Date(submission_date)))  IS null THEN 0 ELSE max(DATEDIFF(Date(production_date),Date(submission_date))) END FROM manuscript m where m.journal_id =:journalId AND  YEAR(m.production_date) =:year AND YEAR(m.submission_date) =:year AND m.manuscript_status_id IN (33,57,59,61,62,65)", nativeQuery = true)
	float
	getTotalNumberOFDaysSubmissionToAcceptanceMax(Integer journalId,Integer year);

	@Query(value = "SELECT CASE WHEN min(DATEDIFF(Date(production_date),Date(submission_date)))  IS null THEN 0 ELSE min(DATEDIFF(Date(production_date),Date(submission_date))) END FROM manuscript m where m.journal_id =:journalId AND  YEAR(m.production_date) =:year AND YEAR(m.submission_date) =:year AND m.manuscript_status_id IN (33,57,59,61,62,65)", nativeQuery = true)
	float getTotalNumberOFDaysSubmissionToAcceptanceMin(Integer journalId,Integer year);

	@Query(value = "SELECT avg(DATEDIFF(Date(publishing_date),Date(production_date))) AS totalsum FROM manuscript m where m.journal_id =:journalId AND  YEAR(m.submission_date) =:year and manuscript_status_id in (57,62,61,65,59)", nativeQuery = true)
	Double getTotalNumberOFDaysAcceptanceToPublication(Integer journalId,Integer year);

	@Query(value = "SELECT CASE WHEN max(DATEDIFF(Date(publishing_date),Date(production_date)))  IS null THEN 0 ELSE max(DATEDIFF(Date(publishing_date),Date(production_date))) END FROM manuscript m where m.journal_id =:journalId AND  YEAR(m.submission_date) =:year and manuscript_status_id in (57,62,61,65,59)", nativeQuery = true)
	float getTotalNumberOFDaysAcceptanceToPublicationMax(Integer journalId,Integer year);

	@Query(value = "SELECT CASE WHEN min(DATEDIFF(Date(publishing_date),Date(production_date)))  IS null THEN 0 ELSE min(DATEDIFF(Date(publishing_date),Date(production_date))) END FROM manuscript m where m.journal_id =:journalId AND YEAR(m.submission_date) =:year and manuscript_status_id in (57,62,61,65,59)", nativeQuery = true)
	float getTotalNumberOFDaysAcceptanceToPublicationMin(Integer journalId,Integer year);

	@Query("SELECT m FROM ManuScript m Join FETCH m.manuscriptTypeDetails md where m.journalId=:journalId AND  m.manuscriptStatusId = 3 AND m.isActive=1 AND TRIM(md.manuscriptType)=TRIM(:name)")
	List<ManuScript> getManuscriptAssignedEditorbyType(String name, Integer journalId);

	@Query(value="SELECT *FROM manuscript m where  m.manuscript_id=:manuscriptId AND m.journal_id IN :journalId and m.assigned_user=:editorId AND m.manuscript_status_id IN (4,46,50,26,8,3)", nativeQuery = true)
	List<ManuScript> getmanManuScriptsdata(List<Integer> journalId,Integer manuscriptId, Integer editorId);

	@Query(value="SELECT *FROM manuscript m where  m.manuscript_id=:manuscriptId AND m.journal_id IN :journalId and m.assigned_user=:editorId AND m.manuscript_status_id IN (51,30,33,52,68)", nativeQuery = true)
	List<ManuScript> getmanManuScriptsdataDecision(List<Integer> journalId,Integer manuscriptId, Integer editorId);

	@Query("SELECT m FROM ManuScript m Join FETCH m.manuscriptTypeDetails md where m.journalId=:journalId AND m.isActive=1 AND TRIM(md.manuscriptType)=TRIM(:name) and  m.manuscriptStatusId IN (51,55,54,52,68,72,73) ")
	List<ManuScript> getManuscriptByFinalDecisionType(String name, Integer journalId);

	@Modifying
	@Query(value = "update manuscript m set m.commentary_id=:manuscriptId where m.manuscript_id=:commentaryId and m.journal_id=:journalId", nativeQuery = true)
    void linkCommentaryToManuscript(Integer manuscriptId, Integer commentaryId, Integer journalId);

	@Query("SELECT m FROM ManuScript m WHERE m.commentaryId =:id")
    List<ManuScript> getCommentaryIds(Integer id);

	@Modifying
	@Query(value = "update manuscript m set m.lte_manuscript_id=:manuscriptId where m.manuscript_id=:lteManuscriptId and m.journal_id=:journalId", nativeQuery = true)
	void updateManuscriptToLTEArticle(Integer manuscriptId, Integer lteManuscriptId, Integer journalId);

	@Query("SELECT m FROM ManuScript m "
			+ " JOIN  ManuScript c ON c.commentaryId = m.manuScriptId"
			+ " WHERE c.manuscriptCode =:commentaryMSCode")
	ManuScript findManuscriptByCommentary(String commentaryMSCode);

	@Query("SELECT m FROM ManuScript m "
			+ " JOIN  ManuScript lte ON lte.lteManuscriptId = m.manuScriptId"
			+ " WHERE lte.manuscriptCode =:authorResponseMSCode")
	ManuScript findManuscriptByAuthorResponse(String authorResponseMSCode);

	@Query("SELECT m FROM ManuScript m "
			+ " JOIN  ManuScript lte ON lte.lteManuscriptId = m.manuScriptId"
			+ " WHERE lte.manuscriptCode =:lteMSCode")
	List<ManuScript> findManuscriptByLTE(String lteMSCode);

	@Query("SELECT m FROM ManuScript m "
			+ " JOIN  ManuScript lte ON lte.manuScriptId = m.lteManuscriptId"
			+ " WHERE lte.manuscriptCode =:lteMSCode")
	List<ManuScript> findAuthorResponseByLTE(String lteMSCode);


	@Query(value="SELECT manuscript_id, assigned_user, journal_id, manuscript_code FROM manuscript WHERE journal_id != 20857 AND assigned_user = 21350" , nativeQuery = true)
	List<Tuple> getIncorrectAssignedManuscript();


	@Query(value="SELECT m.manuscript_id ,m.journal_id, m.manuscript_status_id FROM manuscript m  where m.journal_id IN (:journalId) AND m.is_active=1 AND m.manuscript_status_id!=1 ",nativeQuery = true)
	List<Tuple> getManuscriptForAdmin(List<Integer> journalId);
	
	@Query(value="select m.manuscript_id from manuscript m where m.manuscript_status_id = :statusId and m.is_active=1 and updated_on > :today and updated_on < :next", nativeQuery = true)
	List<Integer> findManuscriptIdsByStatus(int statusId,String today, String next);

	@Query(value = "select journal_id from manuscript where manuscript_id=:manuscripId",nativeQuery = true)
	Integer getJournalId(Integer manuscripId);
	
//    @Query(value = "SELECT CONCAT(b.first_name,' ',b.last_name) AS NameofAuthor,b.user_email AS author_email,b.id as authorId, b.mobile_no AS author_no,CONCAT(c.first_name,' ',c.last_name) AS Nameofeditor,c.user_email AS editor_email,c.id as editorId, c.mobile_no AS editor_no,r.Nameofreviewers,f.journal_title,a.manuscript_id as manuscriptId ,a.manuscript_code,a.manuscript_title,a.doi,g.manuscript_type,a.submission_date,h.Editorial_Review,h.Invite_reviewer,h.Accept_Review_Invitation,h.Reviewed_Manuscript,h.Revision,h.Final_Decision,a.production_date AS Accepted_Rejected,a.publishing_date AS Published FROM manuscript a LEFT JOIN users b ON a.manuscript_author_id=b.id LEFT JOIN users c ON a.assigned_user=c.id LEFT JOIN journal f ON f.journal_id=a.journal_id LEFT JOIN manuscript_typedetails g ON g.manuscript_id=a.manuscript_id LEFT JOIN (SELECT manuscript_id,MAX(CASE WHEN manuscript_status_id=2 THEN created_at END) AS Editorial_Review,MAX(CASE WHEN manuscript_status_id=8 THEN created_at END) AS Invite_reviewer,MAX(CASE WHEN manuscript_status_id=35 THEN created_at END) AS Accept_Review_Invitation,MAX(CASE WHEN manuscript_status_id IN (12,13,14,15) THEN created_at END) AS Reviewed_Manuscript,MAX(CASE WHEN manuscript_status_id=4 THEN created_at END) AS Revision,MAX(CASE WHEN manuscript_status_id IN (30,33) THEN created_at END) AS Final_Decision FROM authorities_history GROUP BY manuscript_id) h ON h.manuscript_id=a.manuscript_id LEFT JOIN (SELECT d.manuscript_id,GROUP_CONCAT(CONCAT(e.first_name,' ',e.last_name) SEPARATOR ', ') AS Nameofreviewers FROM manuscript_reviewer d JOIN users e ON e.id=d.reviewer_id GROUP BY d.manuscript_id) r ON r.manuscript_id=a.manuscript_id WHERE a.manuscript_code IS NOT NULL AND DATE(a.submission_date) BETWEEN :startDate AND :endDate ORDER BY a.submission_date ASC", nativeQuery = true)
//    List<Object[]> findReportBySubmissionDate(@Param("startDate") String startDate,
//            @Param("endDate") String endDate);

//	 @Query(value = "SELECT CONCAT(b.first_name,' ',b.last_name) AS NameofAuthor,b.user_email AS author_email,b.id AS authorId,b.mobile_no AS author_no,CONCAT(c.first_name,' ',c.last_name) AS Nameofeditor,c.user_email AS editor_email,c.id AS editorId,c.mobile_no AS editor_no,r.Nameofreviewers,f.journal_title, a.manuscript_id AS manuscriptId,a.manuscript_code,a.manuscript_title,a.doi,g.manuscript_type,a.submission_date,h.Editorial_Review,h.Invite_reviewer,h.Accept_Review_Invitation,h.Reviewed_Manuscript,h.Revision,h.Final_Decision,a.production_date AS Accepted_Rejected,a.publishing_date AS Published FROM manuscript a LEFT JOIN users b ON a.manuscript_author_id = b.id LEFT JOIN users c ON a.assigned_user = c.id LEFT JOIN journal f ON f.journal_id = a.journal_id LEFT JOIN manuscript_typedetails g ON g.manuscript_id = a.manuscript_id LEFT JOIN (SELECT manuscript_id,MAX(CASE WHEN manuscript_status_id = 2  THEN created_at END) AS Editorial_Review,MAX(CASE WHEN manuscript_status_id = 8  THEN created_at END) AS Invite_reviewer,MAX(CASE WHEN manuscript_status_id = 35 THEN created_at END) AS Accept_Review_Invitation,MAX(CASE WHEN manuscript_status_id IN (12,13,14,15) THEN created_at END) AS Reviewed_Manuscript,MAX(CASE WHEN manuscript_status_id = 4  THEN created_at END) AS Revision, MAX(CASE WHEN manuscript_status_id IN (30,33) THEN created_at END) AS Final_Decision FROM authorities_history GROUP BY manuscript_id) h ON h.manuscript_id = a.manuscript_id LEFT JOIN (SELECT mr.manuscript_id,CONCAT('[',GROUP_CONCAT(JSON_OBJECT('reviewer_id', mr.reviewer_id,'reviewer_email', u.user_email,'reviewer_mobile_no', u.mobile_no)),']' ) AS Nameofreviewers FROM manuscript_reviewer mr JOIN users u ON u.id = mr.reviewer_id WHERE mr.is_active = 1 GROUP BY mr.manuscript_id ) r ON r.manuscript_id = a.manuscript_id WHERE a.manuscript_code IS NOT NULL AND DATE(a.submission_date) BETWEEN :startDate AND :endDate ORDER BY a.submission_date ASC", nativeQuery = true)
//	List<Object[]> findReportBySubmissionDate(@Param("startDate") String startDate,
//	@Param("endDate") String endDate);
	
//    @Query(
//            value = "SELECT a.manuscript_id FROM manuscript a " +
//                    "WHERE a.manuscript_code IS NOT NULL " +
//                    "AND a.submission_date BETWEEN :startDate AND :endDate",
//
//            countQuery = "SELECT COUNT(*) FROM manuscript a " +
//                         "WHERE a.manuscript_code IS NOT NULL " +
//                         "AND a.submission_date BETWEEN :startDate AND :endDate",
//
//            nativeQuery = true
//        )
//        Page<Long> findManuscriptIds(
//                @Param("startDate") String startDate,
//                @Param("endDate") String endDate,
//                Pageable pageable
//        );

        // ✅ STEP 2: FETCH FULL DATA FOR IDS
        @Query(
            value =
            "SELECT auth.authorsJson, CONCAT(c.first_name,' ',c.last_name), c.user_email, c.id, c.mobile_no, " +
            "r.Nameofreviewers, f.journal_title, a.manuscript_id, a.manuscript_code, a.manuscript_title, a.doi, g.manuscript_type, " +
            "a.submission_date, h.Editorial_Review, h.Invite_reviewer, h.Accept_Review_Invitation, h.Reviewed_Manuscript, " +
            "h.Revision, h.Final_Decision, a.production_date, a.publishing_date, a.manuscript_origin, inst.institutions, sm.status_value " +

            "FROM manuscript a " +

            // ===== AUTHORS =====
            "LEFT JOIN ( " +
            " SELECT x.manuscript_id, CONCAT('[', GROUP_CONCAT(JSON_OBJECT( " +
            "   'authorOrder', x.author_order, " +
            "   'authorId', x.author_id, " +
            "   'authorName', x.author_name, " +
            "   'authorEmail', x.author_email, " +
            "   'authorMobile', x.author_mobile, " +
            "   'corresponding', x.is_corresponding " +
            " ) ORDER BY x.author_order), ']') AS authorsJson " +
            " FROM ( " +
            "   SELECT m.manuscript_id, 0 AS author_order, u.id AS author_id, " +
            "          CONCAT(u.first_name,' ',u.last_name) AS author_name, " +
            "          u.user_email AS author_email, u.mobile_no AS author_mobile, TRUE AS is_corresponding " +
            "   FROM manuscript m JOIN users u ON u.id = m.manuscript_author_id " +

            "   UNION ALL " +

            "   SELECT mc.coauth_mansucripts_id AS manuscript_id, mc.author_order, u.id, " +
            "          CONCAT(u.first_name,' ',u.last_name), u.user_email, u.mobile_no, " +
            "          CASE WHEN mc.is_corresponding_author = 'Y' THEN TRUE ELSE FALSE END " +
            "   FROM manuscript_coauthordetails mc " +
            "   JOIN users u ON u.id = mc.coauth_id " +
            "   WHERE mc.coauth_status = 1 " +
            " ) x GROUP BY x.manuscript_id " +
            ") auth ON auth.manuscript_id = a.manuscript_id " +

            // ===== EDITOR =====
            "LEFT JOIN users c ON a.assigned_user = c.id " +

            // ===== JOURNAL =====
            "LEFT JOIN journal f ON f.journal_id = a.journal_id " +

            // ===== TYPE =====
            "LEFT JOIN manuscript_typedetails g ON g.manuscript_id = a.manuscript_id " +

            // ===== STATUS =====
            "LEFT JOIN status_master sm ON sm.status_id = a.manuscript_status_id " +

            // ===== TIMELINE =====
            "LEFT JOIN ( " +
            " SELECT manuscript_id, " +
            " MAX(CASE WHEN manuscript_status_id = 2 THEN created_at END) AS Editorial_Review, " +
            " MAX(CASE WHEN manuscript_status_id = 8 THEN created_at END) AS Invite_reviewer, " +
            " MAX(CASE WHEN manuscript_status_id = 35 THEN created_at END) AS Accept_Review_Invitation, " +
            " MAX(CASE WHEN manuscript_status_id IN (12,13,14,15) THEN created_at END) AS Reviewed_Manuscript, " +
            " MAX(CASE WHEN manuscript_status_id = 4 THEN created_at END) AS Revision, " +
            " MAX(CASE WHEN manuscript_status_id IN (30,33) THEN created_at END) AS Final_Decision " +
            " FROM authorities_history GROUP BY manuscript_id " +
            ") h ON h.manuscript_id = a.manuscript_id " +

            // ===== REVIEWERS =====
            "LEFT JOIN ( " +
            " SELECT mr.manuscript_id, CONCAT('[', GROUP_CONCAT(JSON_OBJECT( " +
            "   'reviewer_id', mr.reviewer_id, " +
            "   'reviewer_email', u.user_email, " +
            "   'reviewer_mobile_no', u.mobile_no " +
            " )), ']') AS Nameofreviewers " +
            " FROM manuscript_reviewer mr " +
            " JOIN users u ON u.id = mr.reviewer_id " +
            " WHERE mr.is_active = 1 GROUP BY mr.manuscript_id " +
            ") r ON r.manuscript_id = a.manuscript_id " +

            // ===== INSTITUTIONS (FIXED) =====
            "LEFT JOIN ( " +
            " SELECT t.manuscript_id, u.institute AS institutions " +
            " FROM ( " +
            "   SELECT mc.coauth_mansucripts_id AS manuscript_id, mc.coauth_id, 1 AS priority " +
            "   FROM manuscript_coauthordetails mc " +
            "   WHERE mc.is_corresponding_author = 'Y' AND mc.coauth_status = 1 " +

            "   UNION ALL " +

            "   SELECT m.manuscript_id AS manuscript_id, m.manuscript_author_id, 2 AS priority " +
            "   FROM manuscript m " +
            " ) t " +

            " JOIN ( " +
            "   SELECT manuscript_id, MIN(priority) AS min_priority " +
            "   FROM ( " +
            "       SELECT mc.coauth_mansucripts_id AS manuscript_id, 1 AS priority " +
            "       FROM manuscript_coauthordetails mc " +
            "       WHERE mc.is_corresponding_author = 'Y' AND mc.coauth_status = 1 " +

            "       UNION ALL " +

            "       SELECT m.manuscript_id, 2 FROM manuscript m " +
            "   ) p GROUP BY manuscript_id " +
            " ) pick ON pick.manuscript_id = t.manuscript_id AND pick.min_priority = t.priority " +

            " JOIN users u ON u.id = t.coauth_id " +
            ") inst ON inst.manuscript_id = a.manuscript_id " +

            // ✅ FILTER BY IDS (IMPORTANT)
            "WHERE a.manuscript_id IN (:ids)",

            nativeQuery = true
        )
        List<Object[]> findReportByIds(@Param("ids") List<Long> ids);
        
        @Query(
        	    value =
        	        "SELECT a.manuscript_id " +
        	        "FROM manuscript a " +
        	        "WHERE a.manuscript_code IS NOT NULL " +
        	        "AND a.submission_date >= STR_TO_DATE(:startDate, '%Y-%m-%d') " +
        	        "AND a.submission_date < DATE_ADD(" +
        	        "    STR_TO_DATE(:endDate, '%Y-%m-%d'), INTERVAL 1 DAY) " +
        	        "ORDER BY a.submission_date DESC, a.manuscript_id DESC",

        	    countQuery =
        	        "SELECT COUNT(*) " +
        	        "FROM manuscript a " +
        	        "WHERE a.manuscript_code IS NOT NULL " +
        	        "AND a.submission_date >= STR_TO_DATE(:startDate, '%Y-%m-%d') " +
        	        "AND a.submission_date < DATE_ADD(" +
        	        "    STR_TO_DATE(:endDate, '%Y-%m-%d'), INTERVAL 1 DAY)",

        	    nativeQuery = true
        	)
        	Page<Object> findManuscriptIds(
        	        @Param("startDate") String startDate,
        	        @Param("endDate") String endDate,
        	        Pageable pageable
        	);

        @Query(
        	    value =
        	        "SELECT " +
        	        "a.manuscript_id, " +                                  // 0
        	        "a.manuscript_code, " +                                // 1
        	        "a.manuscript_title, " +                               // 2
        	        "a.doi, " +                                            // 3
        	        "a.submission_date, " +                                // 4
        	        "a.production_date, " +                                // 5
        	        "a.publishing_date, " +                                // 6
        	        "a.manuscript_origin, " +                              // 7
        	        "CONCAT(u.first_name, ' ', u.last_name), " +           // 8
        	        "u.user_email, " +                                     // 9
        	        "u.id, " +                                             // 10
        	        "u.mobile_no, " +                                      // 11
        	        "j.journal_title, " +                                  // 12
        	        "(SELECT GROUP_CONCAT(DISTINCT TRIM(mt.manuscript_type) " +
        	        "SEPARATOR ', ') " +
        	        " FROM manuscript_typedetails mt " +
        	        " WHERE mt.manuscript_id = a.manuscript_id), " +       // 13
        	        "u.institute, " +                                      // 14
        	        "sm.status_value, " +                                  // 15
        	        "c.name, " +                                           // 16
        	        "a.rejected_date " +                                   // 17
        	        "FROM manuscript a " +
        	        "LEFT JOIN users u ON u.id = a.assigned_user " +
        	        "LEFT JOIN journal j ON j.journal_id = a.journal_id " +
        	        "LEFT JOIN status_master sm " +
        	        "ON sm.status_id = a.manuscript_status_id " +
        	        "LEFT JOIN countries c ON c.id = u.country_id " +
        	        "WHERE a.manuscript_id IN (:ids)",
        	    nativeQuery = true
        	)
        	List<Object[]> findBaseData(@Param("ids") List<Long> ids);

        	@Query(
        		    value =
        		        "SELECT x.manuscript_id, " +
        		        "x.author_order, " +
        		        "x.author_id, " +
        		        "x.author_name, " +
        		        "x.author_email, " +
        		        "x.author_mobile, " +
        		        "x.is_corresponding, " +
        		        "x.institution, " +
        		        "x.country " +
        		        "FROM ( " +

        		        " SELECT m.manuscript_id, 0 AS author_order, " +
        		        " u.id AS author_id, " +
        		        " CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), " +
        		        " NULLIF(TRIM(u.last_name), '')) AS author_name, " +
        		        " u.user_email AS author_email, " +
        		        " u.mobile_no AS author_mobile, " +
        		        " CASE WHEN EXISTS ( " +
        		        "   SELECT 1 FROM manuscript_coauthordetails mc " +
        		        "   WHERE mc.coauth_mansucripts_id = m.manuscript_id " +
        		        "   AND mc.coauth_status = 1 " +
        		        "   AND UPPER(TRIM(COALESCE(mc.is_corresponding_author,''))) " +
        		        "       IN ('Y','YES','1','TRUE') " +
        		        " ) THEN FALSE ELSE TRUE END AS is_corresponding, " +
        		        " u.institute AS institution, " +
        		        " c.name AS country " +
        		        " FROM manuscript m " +
        		        " JOIN users u ON u.id = m.manuscript_author_id " +
        		        " LEFT JOIN countries c ON c.id = u.country_id " +
        		        " WHERE m.manuscript_id IN (:ids) " +

        		        " UNION ALL " +

        		        " SELECT mc.coauth_mansucripts_id, mc.author_order, " +
        		        " u.id, " +
        		        " CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), " +
        		        " NULLIF(TRIM(u.last_name), '')), " +
        		        " u.user_email, u.mobile_no, " +
        		        " CASE " +
        		        " WHEN UPPER(TRIM(COALESCE(mc.is_corresponding_author,''))) " +
        		        "      IN ('Y','YES','1','TRUE') THEN TRUE " +
        		        " WHEN UPPER(TRIM(COALESCE(mc.is_corresponding_author,''))) " +
        		        "      IN ('N','NO','0','FALSE') THEN FALSE " +
        		        " ELSE NULL END, " +
        		        " u.institute, c.name " +
        		        " FROM manuscript_coauthordetails mc " +
        		        " JOIN users u ON u.id = mc.coauth_id " +
        		        " LEFT JOIN countries c ON c.id = u.country_id " +
        		        " WHERE mc.coauth_status = 1 " +
        		        " AND mc.coauth_mansucripts_id IN (:ids) " +

        		        ") x " +
        		        "ORDER BY x.manuscript_id, x.author_order",
        		    nativeQuery = true
        		)
        		List<Object[]> findAuthors(@Param("ids") List<Long> ids);
        		@Query(
        			    value =
        			        "SELECT mr.manuscript_id, " +
        			        "mr.reviewer_id, " +
        			        "CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), " +
        			        "NULLIF(TRIM(u.last_name), '')) AS reviewer_name, " +
        			        "u.user_email, " +
        			        "u.mobile_no, " +
        			        "u.institute, " +
        			        "c.name AS country " +
        			        "FROM manuscript_reviewer mr " +
        			        "JOIN users u ON u.id = mr.reviewer_id " +
        			        "LEFT JOIN countries c ON c.id = u.country_id " +
        			        "WHERE mr.is_active = 1 " +
        			        "AND mr.manuscript_id IN (:ids) " +
        			        "ORDER BY mr.manuscript_id, mr.reviewer_id",
        			    nativeQuery = true
        			)
        			List<Object[]> findReviewers(@Param("ids") List<Long> ids);

            @Query(
                value =
                    "SELECT manuscript_id, " +
                    "MAX(CASE WHEN manuscript_status_id = 2 " +
                    "THEN created_at END), " +
                    "MAX(CASE WHEN manuscript_status_id = 8 " +
                    "THEN created_at END), " +
                    "MAX(CASE WHEN manuscript_status_id = 35 " +
                    "THEN created_at END), " +
                    "MAX(CASE WHEN manuscript_status_id IN (12,13,14,15) " +
                    "THEN created_at END), " +
                    "MAX(CASE WHEN manuscript_status_id = 4 " +
                    "THEN created_at END), " +
                    "MAX(CASE WHEN manuscript_status_id IN (30,33) " +
                    "THEN created_at END) " +
                    "FROM authorities_history " +
                    "WHERE manuscript_id IN (:ids) " +
                    "GROUP BY manuscript_id",
                nativeQuery = true
            )
            List<Object[]> findTimeline(@Param("ids") List<Long> ids);

	
    @Modifying
    @Query(value = "UPDATE manuscript SET manuscript_author_id = :newUserId WHERE manuscript_author_id = :oldUserId", nativeQuery = true)
    void updateAuthor(@Param("oldUserId") Integer oldUserId, @Param("newUserId") Integer newUserId);

    @Modifying
    @Query(value = "UPDATE manuscript SET assigned_user = :newUserId WHERE assigned_user = :oldUserId", nativeQuery = true)
    void updateAssignedUser(@Param("oldUserId") Integer oldUserId, @Param("newUserId") Integer newUserId);

    @Query("SELECT m.manuscriptCode FROM ManuScript m " +
    	       "WHERE m.authorId = :authorId "
    	       + "AND (COALESCE(:journalIds, NULL) IS NULL OR m.journalId IN :journalIds) "
    	       + "AND m.manuscriptCode IS NOT NULL AND m.manuscriptCode <> ''")
    	List<String> findManuscriptCodesByAuthorId(@Param("authorId") Integer authorId,  @Param("journalIds") List<Integer> journalIds);

    @Modifying
    @Query("UPDATE ManuScript m SET m.authorId = :newUserId WHERE m.authorId = :oldUserId AND m.journalId = :journalId")
    void updateAuthorByJournal(@Param("oldUserId") Integer oldUserId,
                               @Param("newUserId") Integer newUserId,
                               @Param("journalId") Integer journalId);

    @Modifying
    @Query("UPDATE ManuScript m SET m.assignedUser = :newUserId WHERE m.assignedUser = :oldUserId AND m.journalId = :journalId")
    void updateAssignedUserByJournal(@Param("oldUserId") Integer oldUserId,
                                     @Param("newUserId") Integer newUserId,
                                     @Param("journalId") Integer journalId);



//    @Query("SELECT m.manuScriptId AS manuScriptId, m.manuscriptCode AS manuscriptCode "
//    		+ "FROM ManuScript m WHERE (:hasJournals = false OR m.journalId IN :journalIds)")
//    	List<ManuscriptIdCodeProjection> findManuscriptIdsAndCodesByJournals(
//    	        @Param("journalIds") List<Integer> journalIds,
//    	        @Param("hasJournals") boolean hasJournals
//    	);
    
    @Query("SELECT m.manuScriptId AS manuScriptId, m.manuscriptCode AS manuscriptCode "
    		+ "FROM ManuScript m WHERE  m.journalId = :journalId AND m.manuscriptCode IS NOT null")
    	List<ManuscriptIdCodeProjection> findManuscriptIdsAndCodesByJournal(
    	        @Param("journalId") Integer journalId
    	);

	@Modifying
	@Query(value = "update manuscript m set m.is_active=0 where m.assigned_user_role=5 and m.assigned_user=:userId and m.journal_id=:journalId", nativeQuery = true)
	void disableManuscriptForReviewer(Integer journalId, Integer userId);






}