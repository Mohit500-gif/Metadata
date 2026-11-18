package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptEditor;
@Repository
@Transactional
public interface ManuscriptEditorRepo extends JpaRepository<ManuscriptEditor, Integer> {

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptStatus=:manuscriptStatus AND m.editorId=:editorId AND m.journalId=:journalId")
	List<ManuscriptEditor> getManuscriptEditor(Integer manuscriptStatus, Integer editorId, Integer journalId);

	@Query("SELECT me FROM ManuscriptEditor me WHERE me.manuscriptStatus=:status AND me.journalId=:journalId")
	List<ManuscriptEditor> getManuscriptEditorById(Integer status, Integer journalId);
	
	@Query("SELECT me.managingEditorId FROM ManuscriptEditor me WHERE me.manuscriptId=:manuscriptId AND me.isActive=1")
	Integer getManuscriptEditorList(Integer manuscriptId);
	
	@Query(value="SELECT me.editor_id FROM manuscript_editor me WHERE me.manuscript_id=:manuscriptId AND me.is_active=1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
	Integer getManuscriptEditorListFileR(Integer manuscriptId);
	
	@Query(value="SELECT DISTINCT me.editor_id FROM manuscript_editor me WHERE me.manuscript_id=:manuscriptId", nativeQuery = true)
	List<Integer> getListOfEditor(Integer manuscriptId);
	

	@Modifying
	@Query("UPDATE ManuscriptEditor m SET m.manuscriptStatus=:manuscriptStatus")
	void updateDetails(Integer manuscriptStatus);

	@Modifying
	@Query("UPDATE ManuscriptEditor m SET m.manuscriptStatus=:manuscriptStatus where m.manuscriptId=:manuscriptId and m.manuscriptVersion=:manuscriptVersion")
	void updateStatusInEditor(Integer manuscriptStatus,Integer manuscriptId,Integer manuscriptVersion);
	
	@Modifying
	@Query("UPDATE ManuscriptEditor m SET m.isActive=0 WHERE m.manuscriptId=:manuscriptId")
	void disableEditor(Integer manuscriptId);
	
	@Modifying
	@Query("UPDATE ManuscriptEditor m SET m.sendBackEic=1 WHERE m.manuscriptId=:manuscriptId AND m.editorId=:editorId ")
	void updateSendBackEic(Integer manuscriptId, Integer editorId);

	@Query("SELECT me from ManuscriptEditor me where me.journalId=:journalId AND me.manuscriptId=:manuScriptId AND me.isActive=1")
	List<ManuscriptEditor> getByJournalAndManuscript(Integer manuScriptId, Integer journalId);
	
	@Query(value = "SELECT * FROM manuscript_editor r WHERE r.journal_id=:journalId AND r.manuscript_id=:manuscriptId AND r.editor_id=:editorId AND r.manuscript_version=:manuscriptVersion AND r.is_active=1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
	ManuscriptEditor getManuscriptEditorDeat(Integer journalId,Integer manuscriptId,Integer manuscriptVersion,Integer editorId);

	@Modifying
	@Query("UPDATE ManuscriptEditor me SET me.manuscriptStatus=:manuStatusId where me.id=:id")
	void updateEditorDetails(Integer manuStatusId, Integer id);

	//@Query("SELECT me FROM ManuscriptEditor me WHERE me.manuscriptId=:manuScriptId AND me.manuscriptVersion=:manuscriptVersion AND me.journalId=:journalId AND me.editorId=:assignToId")
	@Query(value="SELECT *FROM manuscript_editor me WHERE me.manuscript_id=:manuScriptId AND me.manuscript_version=:manuscriptVersion AND me.journal_id=:journalId AND me.editor_id=:assignToId ORDER BY id desc LIMIT 1",nativeQuery = true)
	ManuscriptEditor findManuscriptEditorDetails(Integer manuScriptId, Integer manuscriptVersion, Integer journalId,
			Integer assignToId);

	@Query(value = "SELECT * FROM manuscript_editor r WHERE r.manuscript_id=:manuscriptId AND r.editor_id=:editorId AND r.manuscript_version=:manuscriptVersion", nativeQuery = true)
	List<ManuscriptEditor> getmanuScriptEditorByIdAddMultiple(Integer manuscriptId, Integer editorId,
			Integer manuscriptVersion);

	@Query("SELECT me FROM ManuscriptEditor me WHERE me.manuscriptId=:manuScriptId AND me.manuscriptVersion=:manuscriptVersion AND me.journalId=:journalId AND me.editorId IN:assignToId")
	List<ManuscriptEditor> findManuscriptEditorDetailsList(Integer manuScriptId, Integer manuscriptVersion,
			Integer journalId, List<Integer> assignToId);

	@Modifying
	@Query("UPDATE ManuscriptEditor me SET me.manuscriptStatus=:manuStatusId where me.id IN:manuscriptReviewrId")
	void updateEditorDetailsList(Integer manuStatusId, List<Integer> manuscriptReviewrId);

	@Query("SELECT me FROM ManuscriptEditor me WHERE me.manuscriptId=:manuScriptId AND me.manuscriptVersion=:manuscriptVersion ORDER BY me.id DESC" )
	List<ManuscriptEditor> getByVersion(Integer manuScriptId, Integer manuscriptVersion);

	@Query("SELECT me from ManuscriptEditor me WHERE me.editorId=:id")
	List<ManuscriptEditor> getManuscriptEditorById(Integer id);

	@Query("SELECT me from ManuscriptEditor me WHERE me.manuscriptId=:id")
	List<ManuscriptEditor> getManuscriptEditor(Integer id);
	
	@Query("SELECT me from ManuscriptEditor me WHERE me.manuscriptId=:id AND me.editorId=:editorId")
	List<ManuscriptEditor> getManuscriptEditorOnly(Integer id, Integer editorId);
	
//	@Query("SELECT me from ManuscriptEditor me WHERE me.manuscriptId=:id AND m.isActive=1")
//	List<ManuscriptEditor> getManuscriptEditorPresenteic(Integer id);

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.journalId=:journalId AND m.editorId=:editorId AND m.isActive=1")
	List<ManuscriptEditor> getManuscriptList(Integer journalId, Integer editorId);

	/*************** // Editor Performance // ****************/
	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.assigned_date) IN :year AND MONTH(me.assigned_date)=:month", nativeQuery = true)
	List<ManuscriptEditor> getTotalEditorByYear(List<Integer> journalId, List<Integer> year, Integer month);

	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.production_date) IN :year AND MONTH(me.production_date)=:month AND me.manuscript_status IN(33,57,61,62,65) ", nativeQuery = true)
	List<ManuscriptEditor> getArticlesForAccepted(List<Integer> journalId, List<Integer> year, Integer month);

	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.production_date) IN :year AND me.manuscript_status IN(33,57,61,62,65) ", nativeQuery = true)
	List<ManuscriptEditor> getArticlesForAcceptedAll(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.rejected_date) IN :year AND MONTH(me.rejected_date)=:month AND me.manuscript_status=30 ", nativeQuery = true)
	List<ManuscriptEditor> getArticlesForRejected(List<Integer> journalId, List<Integer> year, Integer month);

	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.assigned_date) IN :year AND MONTH(me.assigned_date)=:month AND me.manuscript_status IN (4,46,50,26,8,3)  ", nativeQuery = true)
	List<ManuscriptEditor> getAssignmentToSuggestingReviewers(List<Integer> journalId, List<Integer> year, Integer month);
	
	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.rejected_date) IN :year AND me.manuscript_status=30 ", nativeQuery = true)
	List<ManuscriptEditor> getArticlesForRejectedAll(List<Integer> journalId, List<Integer> year);

	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.assigned_date) IN :year AND me.manuscript_status IN (4,46,50,26,8,3)  ", nativeQuery = true)
	List<ManuscriptEditor> getAssignmentToSuggestingReviewersAll(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.assigned_date) IN :year AND MONTH(me.assigned_date)=:month AND me.manuscript_status IN (3,51,30,33,52,68)  ", nativeQuery = true)
	List<ManuscriptEditor> getAssignmentToDecision(List<Integer> journalId, List<Integer> year, Integer month);

	@Query(value = "SELECT * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.assigned_date) IN :year AND me.manuscript_status IN (3,51,30,33,52,68)  ", nativeQuery = true)
	List<ManuscriptEditor> getAssignmentToDecisionAll(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT distinct * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.assigned_date) IN :year AND MONTH(me.assigned_date)=:month GROUP BY me.editor_id", nativeQuery = true)
	List<ManuscriptEditor> getEditorName(List<Integer> journalId, List<Integer> year, Integer month);

	@Query(value = "SELECT distinct * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE m.journal_id IN :journalId AND Year(me.assigned_date) IN :year  GROUP BY me.editor_id", nativeQuery = true)
	List<ManuscriptEditor> getEditorNameAll(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT * FROM manuscript_editor r WHERE r.journal_id=:journalId AND r.manuscript_id=:manuScriptId AND r.editor_id=:editorId AND r.manuscript_version=:versionId AND r.is_active=1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
	ManuscriptEditor getManuscriptEditorDetails(Integer manuScriptId, Integer editorId, Integer journalId, Integer versionId);

	@Query("SELECT me FROM ManuscriptEditor me WHERE me.manuscriptId=:manuScriptId AND me.journalId=:journalId")
	List<ManuscriptEditor> getRemarks(Integer manuScriptId, Integer journalId);

	@Modifying(clearAutomatically = true)
	@Query("update ManuscriptEditor wrs set wrs.manuscriptStatus =:statusCode,wrs.commentsForAuthor=:comments,wrs.rejectedDate=:date where wrs.manuscriptId =:manuscriptId and wrs.manuscriptVersion=:manuscriptVersion")
	void updateManuScriptEditorCommentsR(Integer manuscriptId, Integer manuscriptVersion, Integer statusCode,
			String comments,Date date);
	
	@Modifying(clearAutomatically = true)
	@Query("update ManuscriptEditor wrs set wrs.manuscriptStatus =:statusCode,wrs.commentsForAuthor=:comments,wrs.productionDate=:date where wrs.manuscriptId =:manuscriptId and wrs.manuscriptVersion=:manuscriptVersion")
	void updateManuScriptEditorCommentsP(Integer manuscriptId, Integer manuscriptVersion, Integer statusCode,
			String comments,Date date);

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptId IN :manuscriptId AND m.manuscriptStatus=3")
	List<ManuscriptEditor> getAssignedEditorList(List<Integer> manuscriptId);

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptId =:manuscriptId AND m.manuscriptVersion=:manuscriptVersion")
	List<ManuscriptEditor> getAllAssignedEdiorsOnManuscript(Integer manuscriptId, Integer manuscriptVersion);

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptStatus IN :manuscriptStatusId AND m.journalId=:journalId AND m.isActive=1 GROUP BY m.manuscriptId, m.manuscriptVersion")
	List<ManuscriptEditor> getEditorRecord(List<Integer> manuscriptStatusId, Integer journalId);

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptId IN :manuscriptId")
	List<ManuscriptEditor> getAssignedEditorListByManuscriptList(List<Integer> manuscriptId);
	
	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptId IN :manuscriptId GROUP BY manuscriptId")
	List<ManuscriptEditor> getAssignedEditorListByManuscriptListByStatus(List<Integer> manuscriptId);

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.journalId=:journalId AND m.editorId=:userId AND m.isActive=1 AND m.manuscriptStatus=:i")
	List<ManuscriptEditor> getAssignedEditor(Integer journalId, Integer userId, Integer i);

	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptId IN :manuscriptId AND (m.manuscriptStatus=51 OR m.manuscriptStatus=55 OR m.manuscriptStatus=52 OR m.manuscriptStatus=68 OR m.manuscriptStatus=54)")
	List<ManuscriptEditor> getFinalDecisionByType(List<Integer> manuscriptId);
	
	@Query("SELECT m FROM ManuscriptEditor m WHERE m.journalId=:journalId AND m.editorId=:editorId AND m.manuscriptStatus=8 AND m.isActive=1")
	List<ManuscriptEditor> getManuscriptListEditor(Integer journalId, Integer editorId);

	@Modifying(clearAutomatically = true)
	@Query("update ManuscriptEditor wrs set wrs.managingEditorId=:currentUserId WHERE wrs.managingEditorId=:previousUserId AND wrs.journalId=:journalId")
	void updatePreviousManagingEditor(Integer currentUserId, Integer previousUserId, Integer journalId);
	
	@Query("SELECT m FROM ManuscriptEditor m WHERE m.manuscriptId =:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.isActive=1")
	ManuscriptEditor getAllAssignedEdiorsOnManuscriptActive(Integer manuscriptId, Integer manuscriptVersion);
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id WHERE (m.manuscript_status_id=8 OR m.manuscript_status_id=66) AND m.journal_id=:journalId AND m.is_active=1 AND me.is_active=1 AND me.editor_id=:editorId AND m.assigned_user_role=3 ORDER BY m.submission_date DESC;", nativeQuery = true )
	List<ManuscriptEditor> getReviewedManuscriptListEditor(Integer journalId, Integer editorId);
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id WHERE (m.manuscript_status_id=8 OR m.manuscript_status_id=66) AND m.journal_id=:journalId AND m.is_active=1 AND me.is_active=1 AND me.editor_id=:editorId AND m.assigned_user_role=3 ORDER BY m.submission_date DESC;", nativeQuery = true )
	List<ManuscriptEditor> getReviewedManuscriptListEditorpageing(Integer journalId, Integer editorId, Pageable paging);
	
	
	@Query(value="SELECT count(*) FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id WHERE me.manuscript_id=:manuscriptId", nativeQuery = true )
	Integer getEditorCount(Integer manuscriptId);
	
	@Query("SELECT me from ManuscriptEditor me where me.journalId=:journalId AND me.manuscriptVersion=:manuscriptVersion AND me.manuscriptId=:manuScriptId AND me.isActive=1")
	   ManuscriptEditor getEditorByJournalAndManuscript(Integer manuScriptId, Integer journalId, Integer manuscriptVersion );

	@Query("SELECT me from ManuscriptEditor me where me.journalId=:journalId AND me.manuscriptId=:manuScriptId AND me.isActive=1")
	List<ManuscriptEditor> getReviewedManuscriptListEditorpageingSearch(Integer journalId,
			Pageable paging, Integer manuScriptId);

	@Query(value="SELECT * FROM manuscript_editor m WHERE m.manuscript_id=:manuscriptId ORDER BY m.id DESC LIMIT 1",nativeQuery = true)
	ManuscriptEditor getManuscriptEditorDataForRevision(Integer manuscriptId);
	
	@Query(value = "SELECT CONCAT(first_name,' ',last_name) AS Editor_name from manuscript m, "
			+ "  users s,users_journals j WHERE s.id=m.assigned_user AND s.id=j.user_id "
			+ "  AND m.manuscript_id=:manuScriptId AND m.journal_id =:journalId LIMIT 1 ", nativeQuery = true)
	String getEditorNameRatingReport(Integer manuScriptId,Integer journalId);
	
	@Query(value = "SELECT distinct * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE "
			+ "m.journal_id IN :journalId AND ((Year(me.production_date) IN :year AND MONTH(me.production_date)=:month) OR "
			+ "(Year(me.rejected_date) IN :year AND MONTH(me.rejected_date)=:month)) GROUP BY me.editor_id", nativeQuery = true)
	List<ManuscriptEditor> getEditorName1(List<Integer> journalId, List<Integer> year, Integer month);
	
	
	@Query(value = "SELECT b.id, CONCAT(b.first_name, ' ', b.last_name) AS editor_nm, \r\n"
			+ "COUNT(DISTINCT CASE WHEN a.manuscript_status_id = 33 THEN a.manuscript_id END) AS accepted_no, \r\n"
			+ "COUNT(DISTINCT CASE WHEN a.manuscript_status_id = 30 THEN a.manuscript_id END) AS rejected_no,\r\n"
			+ "COUNT(DISTINCT CASE WHEN a.manuscript_status_id = 51 THEN a.manuscript_id END) AS rec_accept,\r\n"
			+ "COUNT(DISTINCT CASE WHEN a.manuscript_status_id = 52 THEN a.manuscript_id END) AS rec_Reject,\r\n"
			+ "COUNT(DISTINCT CASE WHEN a.manuscript_status_id = 68 THEN a.manuscript_id END) AS rec_Revision\r\n"
			+ "FROM authorities_history a INNER JOIN users b ON a.previous_user_id = b.id WHERE a.journal_id IN :journalId "
			+ "AND YEAR(a.created_at) IN :year AND (:month IS NULL OR :month = 0 OR MONTH(a.created_at) = :month) AND a.previous_user_role=3 "
			+ "GROUP BY b.first_name, b.last_name",nativeQuery = true)
	List<Object[]> findEditorPerformance(List<Integer> journalId, List<Integer> year, Integer month);
	
	@Query(value="WITH assign_editor AS (SELECT a.current_user_id, a.current_user_role, a.manuscript_id, "
			+ "max(a.created_at) AS assign_dt FROM authorities_history a WHERE a.journal_id IN :journalId AND "
			+ "YEAR(a.created_at) IN :year AND AND (:month IS NULL OR :month = 0 OR MONTH(a.created_at) = :month) "
			+ "AND a.manuscript_status_id = 3 AND a.manuscript_id IS NOT NULL GROUP BY a.manuscript_id), "
			+ "InviteDates AS (SELECT a.manuscript_id, a.created_at AS invite_dt, a.previous_user_id "
			+ "FROM authorities_history a WHERE a.journal_id IN :journalId AND a.manuscript_status_id IN (8) "
			+ "AND YEAR(a.created_at) IN :year AND AND (:month IS NULL OR :month = 0 OR MONTH(a.created_at) = :month) "
			+ "AND a.manuscript_id IS NOT NULL) SELECT DISTINCT sub.manuscript_id, inv.previous_user_id, sub.assign_dt, "
			+ "inv.invite_dt, DATEDIFF(inv.invite_dt, sub.assign_dt) AS avg_diff FROM assign_editor sub "
			+ "LEFT JOIN InviteDates inv ON sub.manuscript_id = inv.manuscript_id WHERE inv.previous_user_id =:editorId AND "
			+ "sub.current_user_role = 3 AND inv.invite_dt IS NOT NULL AND inv.invite_dt > sub.assign_dt "
			+ "ORDER BY inv.invite_dt;",nativeQuery = true)
	List<Object[]> getAssignmentToInvitingData(List<Integer> journalId, List<Integer> year, Integer month,Integer editorId);
	
	@Query(value="WITH assign_editor AS (SELECT a.current_user_id,a.current_user_role, a.manuscript_id, MIN(a.created_at) AS assign_dt "
			+ "FROM authorities_history a WHERE a.journal_id IN :journalId AND a.manuscript_status_id = 3 AND "
			+ "YEAR(a.created_at) IN :year AND (:month IS NULL OR :month = 0 OR MONTH(a.created_at) = :month) "
			+ "AND a.manuscript_id IS NOT NULL GROUP BY a.manuscript_id), InviteDates AS (SELECT a.manuscript_id, "
			+ "a.created_at AS final_dt FROM authorities_history a WHERE a.journal_id IN :journalId "
			+ "AND a.manuscript_status_id IN (30,33,51,52,68) AND YEAR(a.created_at) IN :year "
			+ "AND (:month IS NULL OR :month = 0 OR MONTH(a.created_at) = :month) AND a.manuscript_id IS NOT NULL) "
			+ "SELECT sub.manuscript_id, sub.assign_dt, inv.final_dt, DATEDIFF(inv.final_dt, sub.assign_dt) AS avg_diff "
			+ "FROM assign_editor sub LEFT JOIN InviteDates inv ON sub.manuscript_id = inv.manuscript_id "
			+ "WHERE sub.current_user_id = :editorId and sub.current_user_role=3 and inv.final_dt IS NOT NULL AND inv.final_dt > sub.assign_dt "
			+ "ORDER BY inv.final_dt",nativeQuery = true)
	List<Object[]> getAssignmentToDecisionData(List<Integer> journalId, List<Integer> year, Integer month,Integer editorId);

	@Query(value="WITH RankedAssignments AS (SELECT DISTINCT a.manuscript_id, b.current_user_id, \r\n"
			+ "b.created_at AS assign_editor_dt, a.created_at AS invite_dt, \r\n"
			+ "ROW_NUMBER() OVER (PARTITION BY a.manuscript_id ORDER BY a.created_at ASC) AS row_num \r\n"
			+ "FROM authorities_history a INNER JOIN authorities_history b ON a.manuscript_id = b.manuscript_id \r\n"
			+ "WHERE a.journal_id IN :journalId AND YEAR(b.created_at) IN :year \r\n"
			+ "AND (:month IS NULL OR :month = 0 OR MONTH(b.created_at) = :month) \r\n"
			+ "AND b.current_user_id = a.previous_user_id AND b.manuscript_status_id = 3 \r\n"
			+ "AND a.manuscript_status_id = 8 AND a.created_at > b.created_at AND a.previous_user_role = 3 \r\n"
			+ "AND b.current_user_role = 3), RankedDecisions AS \r\n"
			+ "(SELECT ra.manuscript_id, ra.current_user_id, ra.assign_editor_dt, ra.invite_dt, \r\n"
			+ "c.created_at AS final_decision_dt, ra.row_num, \r\n"
			+ "ROW_NUMBER() OVER (PARTITION BY ra.manuscript_id ORDER BY c.created_at ASC) AS decision_row_num \r\n"
			+ "FROM RankedAssignments ra LEFT JOIN authorities_history c ON ra.manuscript_id = c.manuscript_id \r\n"
			+ "AND c.manuscript_status_id IN (30, 33, 51, 52, 68) AND c.previous_user_role = 3) \r\n"
			+ "SELECT manuscript_id, current_user_id, assign_editor_dt, invite_dt, final_decision_dt \r\n"
			+ "FROM RankedDecisions WHERE current_user_id =:editorId and  decision_row_num = 1 \r\n"
			+ "AND row_num = 1 AND final_decision_dt > invite_dt \r\n"
			+ "GROUP BY manuscript_id, current_user_id, assign_editor_dt, invite_dt, final_decision_dt;",nativeQuery = true)
	List<Object[]> getAssignmentToInvitingTODecisionData(List<Integer> journalId, List<Integer> year, Integer month,Integer editorId);

	@Query(value = "SELECT distinct * FROM manuscript m JOIN manuscript_editor me on m.manuscript_id=me.manuscript_id WHERE "
			+ "m.journal_id IN :journalId AND (Year(me.production_date) IN :year OR Year(me.rejected_date) IN :year)  GROUP BY me.editor_id", nativeQuery = true)
	List<ManuscriptEditor> getEditorNameAll1(List<Integer> journalId, List<Integer> year);

	
	@Query("SELECT m FROM ManuscriptEditor m  WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuscriptEditor t2 where t2.manuscriptId=m.manuscriptId)")
	List<ManuscriptEditor> getManuscriptEditorList(Integer journalId, List<Integer> manuscriptIdList);

	@Query(value="select distinct(a.manuscript_id) from manuscript_editor a where manuscript_id IN (select m.manuscript_id from manuscript m where m.manuscript_status_id =3 and m.is_active=1 and updated_on > :today and updated_on < :next) and a.manuscript_status=3 and created_at > :today and created_at < :next", nativeQuery = true)
	List<Integer> getAssignedEditorListForSchedule(String today, String next);

    @Modifying
    @Query(value = "UPDATE manuscript_editor SET editor_id = :newUserId WHERE editor_id = :oldUserId", nativeQuery = true)
    void updateEditorUser(@Param("oldUserId") Integer oldUserId, @Param("newUserId") Integer newUserId);
    
    @Query("SELECT COUNT(e) FROM ManuscriptEditor e WHERE e.manuscriptId = :manuscriptId AND e.editorId = :userId")
    int existsByManuscriptAndUser(@Param("manuscriptId") Integer manuscriptId, @Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE ManuscriptEditor e SET e.editorId = :newUserId " +
           "WHERE e.manuscriptId = :manuscriptId AND e.editorId = :oldUserId")
    void updateSingleEditorUser(@Param("oldUserId") Integer oldUserId,
                                @Param("newUserId") Integer newUserId,
                                @Param("manuscriptId") Integer manuscriptId);

    @Modifying
    @Transactional
    @Query(
        value =
            "UPDATE manuscript_editor e\r\n"
            + "SET e.editor_id = :newUserId\r\n"
            + "WHERE e.editor_id = :oldUserId\r\n"
            + "AND e.manuscript_id IN (:manuscriptIds)\r\n"
            + "AND NOT EXISTS (\r\n"
            + "      SELECT 1 FROM (\r\n"
            + "          SELECT * FROM manuscript_editor\r\n"
            + "      ) x\r\n"
            + "      WHERE x.manuscript_id = e.manuscript_id\r\n"
            + "      AND x.editor_id = :newUserId\r\n"
            + ")",
        nativeQuery = true
    )
    int bulkUpdateEditor(
            @Param("oldUserId") Integer oldUserId,
            @Param("newUserId") Integer newUserId,
            @Param("manuscriptIds") List<Integer> manuscriptIds
    );

	@Modifying
	@Query("UPDATE ManuscriptEditor m SET m.sendBackEic=1, m.isActive=0 WHERE m.journalId=:journalId AND m.editorId=:editorId and m.isActive=1")
	void updateSendBackEicForAllManuscripts(Integer journalId, Integer editorId);


}