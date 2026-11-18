package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.AuthoritiesHistory;
@Repository
@Transactional
public interface AuthoritiesHistoryRepo extends JpaRepository<AuthoritiesHistory, Integer> {

	@Query("SELECT m FROM AuthoritiesHistory m WHERE m.manuscriptId=:manuscriptId AND m.journalId=:journalId ORDER BY m.id ASC")
	List<AuthoritiesHistory> getAuthoritiesHistory(Integer manuscriptId,Integer journalId);

	@Query(value="SELECT * FROM authorities_history m WHERE m.manuscript_id=:manuscriptId AND m.journal_id=:journalId ORDER BY m.id DESC LIMIT 1", nativeQuery = true)
	AuthoritiesHistory getPreStatus(Integer manuscriptId,Integer journalId);

	@Query("SELECT m FROM AuthoritiesHistory m WHERE m.manuscriptId=:manuscriptId AND m.journalId=:journalId AND m.currentUserId=:currentUserId AND m.manuscriptStatusId IS NOT null ORDER BY m.createdAt ASC")
	List<AuthoritiesHistory> getAuthoritiesHistoryForUser(Integer manuscriptId,Integer journalId, Integer currentUserId);


	@Modifying(clearAutomatically = true)
	@Query("UPDATE AuthoritiesHistory a set a.currentUserId=:currentUserId WHERE a.currentUserRole=2 AND a.manuscriptId IN :manuscriptId AND a.journalId=:journalId")
	void updateCurrentUserId(Integer currentUserId,List<Integer> manuscriptId,Integer journalId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE AuthoritiesHistory a set a.previousUserId=:currentUserId WHERE a.previousUserRole=2 AND a.manuscriptId IN :manuscriptId AND a.journalId=:journalId")
	void updatePreviousUserId(Integer currentUserId,List<Integer> manuscriptId,Integer journalId);

	@Query(value="SELECT DISTINCT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id=:statusId", nativeQuery = true)
	AuthoritiesHistory getAuthoritiesHistoryByStatusId(Integer manuscriptId,Integer statusId);
	//////////////////////////////////////////
	@Query(value="SELECT * FROM authorities_history m WHERE m.manuscript_id = :manuscriptId AND m.journal_id = :journalId ORDER BY m.id DESC LIMIT 1 OFFSET 1",nativeQuery = true)
	AuthoritiesHistory getPreAuthoritiesHistoryByStatusId(Integer manuscriptId,Integer journalId);

	@Query(value = "SELECT COUNT(*) > 0 FROM authorities_history WHERE manuscript_id = :manuscriptId AND manuscript_status_id = :statusId", nativeQuery = true)
	Integer hasAuthoritiesHistoryByStatusId(Integer manuscriptId, Integer statusId);
	///////////////////////////////////////////////////////
	@Query(value="SELECT * FROM authorities_history where id IN (SELECT MAX(id) FROM authorities_history where manuscript_id=:manuscriptId AND manuscript_status_id=:statusId)", nativeQuery = true)
	AuthoritiesHistory getAuthoritiesHistoryByStatusById(Integer manuscriptId,Integer statusId);

	@Query(value="SELECT m.manuscript_id, m.manuscript_version, m.manuscript_status_id, m.created_at, m.due_date FROM authorities_history m WHERE  m.journal_id = :journalId AND m.manuscript_status_id =:statusId ORDER BY m.id",nativeQuery = true)
	List<Tuple> getAuthoritiesHistoryByJOurnalId(Integer journalId, Integer statusId);


	@Query(value ="SELECT * FROM authorities_history m WHERE m.manuscript_id=:manuscriptId AND m.journal_id=:journalId AND m.manuscript_status_id IS NOT null ORDER BY m.created_at DESC LIMIT 1", nativeQuery = true)
	AuthoritiesHistory getAuthoritiesHistoryForArticleWithdrawn(Integer manuscriptId,Integer journalId);

	@Query(value ="SELECT * FROM authorities_history m WHERE m.manuscript_id=:manuscriptId AND m.journal_id=:journalId AND m.manuscript_status_id=:statusId AND m.manuscript_version=:manuscriptVersion AND m.current_user_id=:currentUserId", nativeQuery = true)
	AuthoritiesHistory getAuthoritiesHistoryForUpdateDueDate(Integer manuscriptId,Integer journalId, Integer statusId , Integer manuscriptVersion, Integer currentUserId);

	@Query(value="SELECT COUNT(*) FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id=:statusId", nativeQuery = true)
	Integer getAuthoritiesHistoryCount(Integer manuscriptId,Integer statusId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id=:statusId ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getAuthoritiesHistoryByStatusIdMultiple(Integer manuscriptId,Integer statusId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id IN (68,51,52) ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getAuthoritiesHistoryByStatusIdAcceptRejectRevision(Integer manuscriptId);

	@Query(value ="SELECT * FROM authorities_history m WHERE m.manuscript_id=:manuscriptId AND m.journal_id=:journalId AND m.manuscript_status_id IN(8,35,66) AND m.manuscript_version=:manuscriptVersion AND m.current_user_id=:currentUserId ORDER BY id DESC LIMIT 1", nativeQuery = true)
	List<AuthoritiesHistory> getAuthoritiesHistoryListForUpdateDueDate(Integer manuscriptId,Integer journalId, Integer manuscriptVersion, Integer currentUserId);

	@Query(value ="SELECT * FROM authorities_history m WHERE m.manuscript_id=:manuscriptId AND m.journal_id=:journalId AND m.manuscript_status_id IS NOT null ORDER BY m.id DESC LIMIT 1", nativeQuery = true)
	AuthoritiesHistory getAuthoritiesHistoryForAssignDate(Integer manuscriptId,Integer journalId);

	@Query(value ="SELECT previous_user_id FROM authorities_history m WHERE m.manuscript_id=:manuscriptId AND m.journal_id=:journalId AND m.previous_user_role = 2  ORDER BY m.id DESC LIMIT 1", nativeQuery = true)
	Integer getManagingEditorId(Integer manuscriptId,Integer journalId);

	@Query(value ="SELECT * FROM authorities_history m WHERE m.manuscript_id=:manuscriptId AND m.journal_id=:journalId AND m.previous_user_role = 2  ORDER BY m.id DESC LIMIT 1", nativeQuery = true)
	AuthoritiesHistory getManagingEditorObject(Integer manuscriptId,Integer journalId);

	// Added By Utkarsh
	@Query(value ="SELECT * FROM authorities_history a WHERE a.manuscript_id=:manuscriptId ORDER BY a.id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getByManuscriptId(Integer manuscriptId);

	@Query(value ="SELECT * FROM authorities_history a WHERE a.manuscript_id=:manuscriptId AND a.previous_user_role=3 AND a.previous_user_id=:editorUserId ORDER BY a.id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getByManuscriptIdAndRoleIdAndPreviousUserIdEditor(Integer manuscriptId, Integer editorUserId);

	@Query(value ="SELECT * FROM authorities_history a WHERE a.manuscript_id=:manuscriptId AND a.previous_user_role=2 and a.current_user_id=:currentUserId ORDER BY a.id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getByManuscriptIdAndRoleIdEIC(Integer manuscriptId, Integer currentUserId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id=:statusId AND previous_user_role=3 and previous_user_id=:editorId ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getAuthoritiesHistoryByStatusIdMultipleByRoleIdEditor(Integer manuscriptId, int statusId, Integer editorId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id in (51,52,68) AND previous_user_role=3 and previous_user_id=:editorId ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getAuthoritiesHistoryByStatusIdMultipleByRoleIdEditorAcceptRejectRevision(Integer manuscriptId, Integer editorId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id=:statusId AND previous_user_role=5 and current_user_id=:editorId ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getAuthorityHistoryForReviewerAndEditor(Integer manuscriptId, int statusId, Integer editorId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id=67 AND previous_user_role=5 ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getReviewersRemoved(Integer manuscriptId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id=67 AND previous_user_role=5 AND current_user_id=:editorId ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getReviewersRemovedForEditor(Integer manuscriptId, Integer editorId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id IN (12,13,14,15) AND previous_user_role=5 ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getReviewersAccepted(Integer manuscriptId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND manuscript_status_id IN (12,13,14,15) AND current_user_id=:editorId AND previous_user_role=5 ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getReviewersAcceptedForEditor(Integer manuscriptId, Integer editorId);

	@Query(value="SELECT * FROM authorities_history WHERE manuscript_id=:manuscriptId AND current_user_id=:editorId AND previous_user_role=3 AND current_user_role=3 ORDER BY id DESC", nativeQuery = true)
	List<AuthoritiesHistory> getByManuscriptIdEditorToEditor(Integer manuscriptId, Integer editorId);


	@Query(value = "select distinct(a.manuscript_id) from authorities_history a where manuscript_id IN (select m.manuscript_id from manuscript m where m.manuscript_status_id =:statusId and m.is_active=1 and updated_on > :today and updated_on < :next) and a.manuscript_status_id=:statusId and created_at > :today and created_at < :next" , nativeQuery = true)
	List<Integer> findManuscriptIds(int statusId, String today, String next);
	
	@Query(value = "SELECT DISTINCT manuscript_status_id FROM authorities_history WHERE manuscript_id = :manuscriptId", nativeQuery = true)
	List<Integer> findAllStatusIdsForManuscript(Integer manuscriptId);
	
	@Query(value = "select created_at from authorities_history where manuscript_id=:manuscriptId order by id desc limit 1",nativeQuery = true)
	Date getAssignedDate(Integer manuscriptId);

	@Modifying
	@Transactional
	@Query(value ="UPDATE authorities_history e SET e.current_user_id = :newUserId " +
			"WHERE e.current_user_id = :oldUserId AND e.manuscript_id IN (:manuscriptIds)",nativeQuery = true)
	int bulkUpdateAuthority(
			@Param("oldUserId") Integer oldUserId,
			@Param("newUserId") Integer newUserId,
			@Param("manuscriptIds") List<Integer> manuscriptIds);


	@Query(value="select * from ( select ah.*, RANK() OVER (PARTITION BY manuscript_id, journal_id order by id desc) as row_num " +
			"from authorities_history ah where journal_id =:journalId and manuscript_id is not null )x " +
			"where x.row_num = 1 " +
			"and x.journal_id =:journalId " +
			"and x.manuscript_status_id in (8,35) " +
			"and ( (x.current_user_id =:userId and x.current_user_role=5) " +
			"OR (x.previous_user_id =:userId and x.previous_user_role=5) ) ", nativeQuery = true)
	List<AuthoritiesHistory> getAllReviewerAssignedManuscripts(Integer journalId, Integer userId);

	@Query(value="select * from ( select ah.*, RANK() OVER (PARTITION BY manuscript_id, journal_id order by id desc) as row_num " +
			"from authorities_history ah where journal_id =:journalId and manuscript_id is not null )x " +
			"where x.row_num = 1 " +
			"and x.journal_id =:journalId " +
			"and ( (x.current_user_id =:userId and x.current_user_role=3) ) ", nativeQuery = true)
	List<AuthoritiesHistory> getAllEditorAssignedManuscripts(Integer journalId, Integer userId);

	@Query(value="select previous_user_id from authorities_history ah " +
			"where manuscript_id =:manuscriptId " +
			"and current_user_role = 3 and current_user_id =:userId " +
			"and previous_user_role = 2 order by id DESC limit 1", nativeQuery = true)
	Integer getLastAssignedEIC(Integer manuscriptId,Integer userId);
}
