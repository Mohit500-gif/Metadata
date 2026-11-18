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

import com.digi.uniprr.VO.ManuScriptVO;
import com.digi.uniprr.VO.PerCountryManuscriptVo;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptForStatus;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptEditor;

@Repository
@Transactional
public interface ManuScriptForStatusRepo extends JpaRepository<ManuScriptForStatus, Integer> {


	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id=:manuscriptStatusId AND m.is_active=1  AND m.journal_id=:journalId ORDER BY m.submission_date DESC", nativeQuery = true)
	List<ManuScriptForStatus> getManuscriptByStatus(Integer manuscriptStatusId, Integer journalId);
	

	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id=:mStatus AND m.journal_id=:journalId AND m.assigned_user=:userId AND m.is_active=1 ORDER BY m.submission_date DESC" , nativeQuery = true)
	List<ManuScriptForStatus> getManuscriptByStatusAndUser(Integer mStatus, Integer journalId, Integer userId);
		
	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id=:mStatus AND m.is_active=1  AND m.journal_id=:journalId AND m.assigned_user=:userId AND m.assigned_user_role=:roleId ORDER BY m.submission_date DESC" , nativeQuery = true)
	List<ManuScriptForStatus> getManuscriptByJournalAndRole(Integer mStatus, Integer journalId, Integer userId, Integer roleId);
	
	
	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id IN :manuscriptStatusId AND m.is_active=1  AND m.journal_id=:journalId ORDER BY m.submission_date DESC", nativeQuery = true)
	List<ManuScriptForStatus> getManuscriptByStatusForEIC(List<Integer> manuscriptStatusId, Integer journalId);
	

	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id IN :mStatus AND m.journal_id=:journalId AND m.assigned_user=:userId AND m.is_active=1 ORDER BY m.submission_date DESC" , nativeQuery = true)
	List<ManuScriptForStatus> getManuscriptByStatusAndUserForEIC(List<Integer> mStatus, Integer journalId, Integer userId);
		
	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id IN :mStatus AND m.is_active=1  AND m.journal_id=:journalId AND (m.assigned_user=:userId OR m.assigned_user_role=:roleId) ORDER BY m.submission_date DESC" , nativeQuery = true)
	List<ManuScriptForStatus> getManuscriptByJournalAndRoleForEIC(List<Integer> mStatus, Integer journalId, Integer userId, Integer roleId);

	@Query(value="SELECT m.manuscript_id,m.manuscript_version,m.manuscript_code,m.manuscript_title, mt.manuscript_type , CONCAT(u.first_name,' ', u.last_name) AS editorName FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id INNER JOIN users u ON u.id = m.assigned_user  WHERE m.journal_id =:journalId AND m.is_active = 1 AND m.manuscript_code IS NOT NULL AND m.manuscript_status_id IN :mStatus" , nativeQuery = true)
	List<Tuple> getManuscriptByJournalAndStatus(List<Integer> mStatus, Integer journalId);
	
}