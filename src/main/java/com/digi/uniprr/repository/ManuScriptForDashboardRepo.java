package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuScriptForDashboard;


@Repository
@Transactional
public interface ManuScriptForDashboardRepo extends JpaRepository<ManuScriptForDashboard, Integer> {
	
//	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id IN :manuscriptStatusId AND m.is_active=1  AND m.journal_id=:journalId ORDER BY m.submission_date DESC", nativeQuery = true)
	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :manuscriptStatusId AND m.isActive=1  AND m.journalId=:journalId ")
	List<ManuScriptForDashboard> getManuscriptByStatusPage(List<Integer> manuscriptStatusId, Integer journalId,Pageable paging);
	
	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode")
	List<ManuScriptForDashboard> getManuscriptByStatusCodePage(List<Integer> mStatus, Integer journalId,String statusCode,Pageable paging);
	
	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode")
	List<ManuScriptForDashboard> getManuscriptByStatusCodeEICPage(List<Integer> mStatus, Integer journalId,String statusCode, Pageable paging);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId AND m.assignedUser=:userId AND m.statusMaster.statusValue=:statusCode")
	List<ManuScriptForDashboard> getManuscriptByStatusCodeEditorPage(List<Integer> mStatus, Integer journalId,String statusCode, Integer userId,Pageable paging);

	
	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.dueDate LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByStatusPageSearch(List<Integer> mStatus, Integer journalId,Pageable paging,String search);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.dueDate LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByStatusCodeEICPageSearch(List<Integer> mStatus, Integer journalId,String statusCode, Pageable paging, String search);

	
	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id=:mStatus AND m.journal_id=:journalId AND m.assigned_user=:userId AND m.is_active=1 ORDER BY m.submission_date DESC" , nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptByStatusAndUserPage(Integer mStatus, Integer journalId, Integer userId, Pageable paging);
		
	@Query(value="SELECT * FROM manuscript m WHERE m.manuscript_status_id IN :statusId AND m.is_active=1  AND m.journal_id=:journalId " , nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePage(List<Integer> statusId, Integer journalId, Pageable paging);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageReportAccepted(List<Integer> statusId, Integer journalId, Pageable paging);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageReportPublished(List<Integer> statusId, Integer journalId, Pageable paging);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageReportWip(List<Integer> statusId, Integer journalId, Pageable paging);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId  AND m.journalId=:journalId")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageReportWithdrawn(List<Integer> statusId, Integer journalId, Pageable paging);
	
	//@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) ORDER BY m.manuScriptId DESC")
	@Query("SELECT m FROM ManuScriptForDashboard m " +
		       "WHERE m.manuscriptStatusId IN :statusId " +
		       "AND m.journalId = :journalId " +
		       "AND m.isActive = 1 " +
		       "AND (m.assignedUser = :userId OR m.assignedUserRole = :roleId) " +
		       "ORDER BY CONVERT(SUBSTRING_INDEX(m.manuscriptCode, '_', -1), SIGNED) DESC, " +
		       "CONVERT(SUBSTRING_INDEX(SUBSTRING_INDEX(m.manuscriptCode, '_', 2), '_', -1), SIGNED) DESC")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRole(List<Integer> statusId, Integer journalId, Integer userId,Integer roleId, Pageable paging);
	
	@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) ORDER BY m.submissionDate DESC")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRoleReportAccepted(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId,Pageable paging);

	@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) ORDER BY m.submissionDate DESC")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRoleReportPublished(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId,Pageable paging);

	@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) ORDER BY m.submissionDate DESC")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRoleReportWip(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId,Pageable paging);

	
	@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%) ")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRoleSearch(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId,Pageable paging, String search);

	@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%) ORDER BY m.submissionDate DESC")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRoleSearchReportAccepted(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId,Pageable paging, String search);

	@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%) ORDER BY m.submissionDate DESC")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRoleSearchReportPublished(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId,Pageable paging, String search);

	@Query("select m from ManuScriptForDashboard m where m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND m.isActive=1 AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%) ORDER BY m.submissionDate DESC")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRoleSearchReportPublishedWip(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId,Pageable paging, String search);
	
	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageSearch(List<Integer> statusId, Integer journalId,Pageable paging,String search);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.statusMaster.statusValue LIKE %:search% OR m.productionDate LIKE %:search% OR m.commentForProduction LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageSearchReportAccepted(List<Integer> statusId, Integer journalId,Pageable paging,String search);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.statusMaster.statusValue LIKE %:search% OR m.productionDate LIKE %:search% OR m.commentForProduction LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageSearchReportPublished(List<Integer> statusId, Integer journalId,Pageable paging,String search);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.statusMaster.statusValue LIKE %:search% OR m.productionDate LIKE %:search% OR m.commentForProduction LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageSearchReportWip(List<Integer> statusId, Integer journalId,Pageable paging,String search);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.withdrawnRequestDate LIKE %:search% OR m.withdrawnDate LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageSearchReportWithdrawn(List<Integer> statusId, Integer journalId,Pageable paging,String search);

	
	@Query(value="SELECT COUNT(*) FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id WHERE m.manuscript_status_id IN :statusId AND m.is_active=1 AND m.journal_id=:journalId ",nativeQuery = true )
	Integer getTotalSbmittedManuscriptForDashboard(List<Integer> statusId, Integer journalId);
	
	@Query(value="SELECT COUNT(*) FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id WHERE m.manuscript_status_id IN :statusId AND m.is_active=1 AND m.assigned_user_role=:roleId AND m.journal_id=:journalId",nativeQuery = true )
	Integer getTotalSbmittedManuscriptForDashboardRole(List<Integer> statusId, Integer journalId, Integer roleId);
	
	@Query(value="SELECT COUNT(*) FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id INNER JOIN status_master s ON s.status_id = m.manuscript_status_id WHERE m.manuscript_status_id IN :statusId AND m.is_active=1 AND s.status_value=:statusCode AND m.assigned_user_role=:roleId AND m.journal_id=:journalId",nativeQuery = true )
	Integer getTotalFinalManuscriptForDashboardStatusCodeRole(List<Integer> statusId, Integer journalId, Integer roleId, String statusCode);
	
//	@Query(value="SELECT COUNT(*) FROM manuscript m WHERE m.manuscript_status_id IN :statusId AND m.is_active=1 AND m.journal_id=:journalId AND (m.assigned_user=:userId OR m.assigned_user_role=:roleId)",nativeQuery = true )
//	Integer getTotalResbmittedManuscriptForDashboard(List<Integer> statusId, Integer userId, Integer roleId, Integer journalId);

	@Query(value="SELECT COUNT(*) FROM manuscript m WHERE m.manuscript_status_id IN :statusId AND m.is_active=1 AND m.journal_id=:journalId ",nativeQuery = true )
	Integer getTotalResbmittedManuscriptForDashboard(List<Integer> statusId, Integer journalId);
	
	@Query(value="SELECT COUNT(*) FROM manuscript m WHERE m.manuscript_status_id IN :statusId AND m.journal_id=:journalId ",nativeQuery = true )
	Integer getTotalWithdrawnManuscriptForDashboard(List<Integer> statusId, Integer journalId);
	
	
	@Query(value="SELECT COUNT(*) FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id m WHERE m.manuscript_status_id IN :statusId AND m.is_active=1 AND m.journal_id=:journalId AND m.assigned_user=:userId AND m.assigned_user_role=:roleId",nativeQuery = true )
	Integer getTotalResbmittedManuscriptForDashboardEditor(List<Integer> statusId, Integer userId, Integer roleId, Integer journalId);
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%  OR m.statusMaster.statusValue LIKE %:search% OR m.productionDate LIKE %:search% OR m.commentForProduction LIKE %:search%)")
	Integer getTotalSbmittedManuscriptForDashboardSearch(List<Integer> statusId, Integer journalId, String search);

	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%  OR m.statusMaster.statusValue LIKE %:search% OR m.rejectedDate LIKE %:search%)")
	Integer getTotalRejectedManuscriptSearch(List<Integer> statusId, Integer journalId, String search);	
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.withdrawnDate LIKE %:search% OR m.withdrawnRequestDate LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getTotalWithdrawnManuscriptSearch(List<Integer> statusId, Integer journalId, String search);	
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getTotalResbmittedManuscriptForDashboardSearch(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId, String search);

	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode AND (m.assignedUser=:userId OR m.assignedUserRole=:roleId) AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getTotalFinalManuscriptForDashboardStatusCodeSearch(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId, String statusCode, String search);
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%  OR m.statusMaster.statusValue LIKE %:search% OR m.productionDate LIKE %:search% OR m.commentForProduction LIKE %:search%)")
	Integer getTotalFinalManuscriptForDashboardStatusCodeSearch(List<Integer> statusId, Integer journalId, String statusCode, String search);
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1  AND m.journalId=:journalId AND m.assignedUser=:userId AND m.assignedUserRole=:roleId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getTotalResbmittedManuscriptForDashboardEditorSearch(List<Integer> statusId, Integer journalId, Integer userId, Integer roleId, String search);

	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode")
	Integer getTotalManuscriptByStatusCode(List<Integer> mStatus, Integer journalId,String statusCode);
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode")
	Integer getTotalManuscriptByStatusCodeEIC(List<Integer> mStatus, Integer journalId, String statusCode);

	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16) AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptForDashboard> getTotalManuscriptByStatusCodeEditor(List<Integer> mStatus, Integer journalId, Integer userId, String statusCode);
	
	@Query("SELECT m.statusMaster.statusValue FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.journalId=:journalId GROUP BY m.statusMaster.statusValue")
	List<String> getManuscriptStatusList(List<Integer> mStatus, Integer journalId);

	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN (8,12,13,14,35,36) AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getTotalINManuscriptForDashboardSearch(Integer journalId, String search);
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :i AND m.isActive=1  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getAssignedManuscriptForDashboardSearch(Integer i, Integer journalId, String search);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId=:mStatus AND m.isActive=1 AND m.assignedUserRole=:roleId AND m.assignedUser=:userId AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageSearchByUser(Integer mStatus, Integer journalId,
			Pageable paging, String search, Integer userId, Integer roleId);
	
	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId=:mStatus AND m.isActive=1 AND m.assignedUserRole=:roleId  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageSearchByRole(Integer mStatus, Integer journalId,
			Pageable paging, String search, Integer roleId);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version,m.manuscript_title, m.assigned_user,mt.manuscript_type , m.submission_date, m.manuscript_status_id, concat(u.first_name,' ', u.last_name) as name, m.eic_folder, m.manuscript_author_id FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id INNER JOIN users u ON u.id=m.manuscript_author_id WHERE m.journal_id =:journalId AND m.is_active=1 AND m.manuscript_status_id=:mStatus AND m.assigned_user_role=:roleId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%)", nativeQuery =true)
	List<Tuple> getManuscriptByJournalTupleSearch(Integer mStatus, Integer journalId, String search, Integer roleId);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId=:mStatus AND m.isActive=1 AND m.assignedUserRole=:roleId AND m.assignedUser=:userId AND m.journalId=:journalId ")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageByUser(Integer mStatus, Integer journalId,
			Pageable paging, Integer userId, Integer roleId);
	
	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId=:mStatus AND m.isActive=1 AND m.assignedUserRole=:roleId AND m.journalId=:journalId ")
	List<ManuScriptForDashboard> getManuscriptByJournalAndRolePageByRole(Integer mStatus, Integer journalId,
			Pageable paging, Integer roleId);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version,m.manuscript_title, m.assigned_user,mt.manuscript_type , m.submission_date, m.manuscript_status_id, concat(u.first_name,' ', u.last_name) as name, m.eic_folder, m.manuscript_author_id FROM manuscript m INNER JOIN manuscript_typedetails mt ON mt.manuscript_id = m.manuscript_id INNER JOIN users u ON u.id=m.manuscript_author_id where m.journal_id=:journalId AND m.is_active=1 AND m.manuscript_status_id=:mStatus AND m.manuscript_code IS NOT null AND m.assigned_user_role=:roleId",nativeQuery = true)
	List<Tuple> getManuscriptByJournalAndIdEICTuple(Integer mStatus, Integer journalId, Integer roleId);

	@Query(value="SELECT COUNT(*) FROM manuscript m WHERE m.manuscript_status_id IN :manuscriptStatusId AND m.is_active=1 AND m.assigned_user_role=:roleId AND m.journal_id=:journalId AND m.assigned_user=:userId",nativeQuery = true )
	Integer getTotalSbmittedManuscriptForDashboardByUser(List<Integer> manuscriptStatusId, Integer journalId,
			Integer userId, Integer roleId);
	
	@Query(value="SELECT COUNT(*) FROM manuscript m WHERE m.manuscript_status_id IN :manuscriptStatusId AND m.is_active=1 AND m.assigned_user_role=:roleId AND m.journal_id=:journalId",nativeQuery = true )
	Integer getTotalSbmittedManuscriptForDashboardByRole(List<Integer> manuscriptStatusId, Integer journalId,
			 Integer roleId);
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16) AND m.is_active=1 AND me.send_back_eic=0 AND m.journal_id=:journalId AND me.editor_id=:userId GROUP BY m.manuscript_id",nativeQuery = true )
	List<ManuScriptForDashboard> getTotalManuscriptForEditorByUser(List<Integer> manuscriptStatusId, Integer journalId,
			Integer userId);
	
	@Query(value="SELECT m.manuscript_id FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE m.manuscript_status_id IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id",nativeQuery = true )
	List<Tuple> getTotalManuscriptCont(List<Integer> manuscriptStatusId, Integer journalId,
			Integer userId);

	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1 AND m.assignedUserRole=:roleId AND  m.assignedUser=:userId AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getTotalSbmittedManuscriptForDashboardSearchByUser(List<Integer> statusId, Integer journalId,
			String search, Integer userId, Integer roleId);
	
	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :statusId AND m.isActive=1 AND m.assignedUserRole=:roleId  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%)")
	Integer getTotalSbmittedManuscriptForDashboardSearchByRole(List<Integer> statusId, Integer journalId,
			String search, Integer roleId);
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :statusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptForDashboard> getTotalManuscriptForEditorSearchByUser(List<Integer> statusId, Integer journalId,
			String search, Integer userId);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1  AND m.assignedUser=:userId  AND m.journalId=:journalId AND (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.submissionDate LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search% OR m.dueDate LIKE %:search%)")
	List<ManuScriptForDashboard> getManuscriptByStatusPageSearchByUser(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageSearchByUser(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version,m.manuscript_title, m.assigned_user,mt.manuscript_type , m.submission_date, m.manuscript_status_id, CONCAT(u.first_name,' ', u.last_name) AS NAME, m.assigned_user_role, me.managing_editor_id, m.updated_on , m.due_date, m.manuscript_author_id  FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE m.manuscript_status_id IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id", nativeQuery = true)
	List<Tuple> getManuscriptForEditorSearchTuple(List<Integer> mStatus,Integer journalId, String search, Integer userId);
	
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16) AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id ORDER BY mt.manuscript_type DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageSearchByUserManuscriptTypeDESC(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id ORDER BY sm.status_value DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageSearchByUserStatusValueDESC(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id ORDER BY u.first_name DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageSearchByUserFrstNameDESC(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)   AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id ORDER BY mt.manuscript_type ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageSearchByUserManuscriptTypeASC(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0  AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id ORDER BY sm.status_value ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageSearchByUserStatusValueASC(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId); 

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :mStatus AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16) AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND (m.manuscript_title LIKE %:search% OR m.manuscript_code LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR m.submission_date LIKE %:search% OR mt.manuscript_type LIKE %:search%) GROUP BY m.manuscript_id ORDER BY u.first_name ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageSearchByUserFrstNameASC(List<Integer> mStatus,Integer journalId, Pageable paging, String search, Integer userId);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :manuscriptStatusId AND m.isActive=1 AND m.assignedUser=:userId AND m.journalId=:journalId ")
	List<ManuScriptForDashboard> getManuscriptByStatusPageByUser(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageByUser(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version,m.manuscript_title, m.assigned_user,mt.manuscript_type , m.submission_date, m.manuscript_status_id, CONCAT(u.first_name,' ', u.last_name) AS NAME, m.assigned_user_role, me.managing_editor_id, m.updated_on , m.due_date, m.manuscript_author_id FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE m.manuscript_status_id IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id", nativeQuery = true)
	List<Tuple> getManuscriptForEditorTuple(List<Integer> manuscriptStatusId, Integer journalId, Integer userId);
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id ORDER BY mt.manuscript_type DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageByUserManuscriptTypeDESC(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id ORDER BY u.first_name DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageByUserFrstNameDESC(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id ORDER BY sm.status_value DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageByUserStatusValueDESC(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id ORDER BY mt.manuscript_type ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageByUserManuscriptTypeASC(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id ORDER BY u.first_name ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageByUserFrstNameASC(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId  GROUP BY m.manuscript_id ORDER BY sm.status_value ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorPageByUserStatusValueASC(List<Integer> manuscriptStatusId, Integer journalId, Pageable paging, Integer userId);

	@Query("SELECT m FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :manuscriptStatusId AND m.isActive=1  AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode AND m.assignedUser=:userId")
	List<ManuScriptForDashboard> getManuscriptByStatusCodePageByUser(List<Integer> manuscriptStatusId,
			Integer journalId, String statusCode, Pageable paging, Integer userId);
	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorCodePageByUser(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Pageable paging, Integer userId);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_code, m.manuscript_version,m.manuscript_title, m.assigned_user,mt.manuscript_type , m.submission_date, m.manuscript_status_id, CONCAT(u.first_name,' ', u.last_name) AS NAME, m.assigned_user_role, me.managing_editor_id, m.updated_on , m.due_date, m.manuscript_author_id FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE m.manuscript_status_id IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id", nativeQuery = true)
	List<Tuple> getManuscriptForEditorCodeTuple(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Integer userId);

	
	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id ORDER BY mt.manuscript_type DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorCodePageByUserManuscriptTypeDESC(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id ORDER BY u.first_name DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorCodePageByUserFrstNameDESC(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id ORDER BY sm.status_value DESC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorCodePageByUserStatusValueDESC(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id ORDER BY mt.manuscript_type ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorCodePageByUserManuscriptTypeASC(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id ORDER BY u.first_name ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorCodePageByUserFrstNameASC(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Pageable paging, Integer userId);

	@Query(value="SELECT * FROM manuscript m INNER JOIN manuscript_editor me ON me.manuscript_id=m.manuscript_id INNER JOIN users u ON m.manuscript_author_id=u.id INNER JOIN status_master sm ON sm.status_id = m.manuscript_status_id INNER JOIN manuscript_typedetails mt ON m.manuscript_id=mt.manuscript_id WHERE me.manuscript_status IN :manuscriptStatusId AND m.manuscript_status_id NOT IN (57,61,62,65,59,33,30,16)  AND m.is_active=1 AND me.send_back_eic=0 AND me.editor_id=:userId AND m.journal_id=:journalId AND sm.status_value=:statusCode GROUP BY m.manuscript_id ORDER BY sm.status_value ASC", nativeQuery = true)
	List<ManuScriptForDashboard> getManuscriptForEditorCodePageByUserStatusValueASC(List<Integer> manuscriptStatusId,Integer journalId, String statusCode, Pageable paging, Integer userId);

	@Query("SELECT COUNT(*) FROM ManuScriptForDashboard m WHERE m.manuscriptStatusId IN :mStatus AND m.isActive=1 AND m.assignedUser=:userId AND m.journalId=:journalId AND m.statusMaster.statusValue=:statusCode")
	Integer getTotalManuscriptByStatusCodeByUser(List<Integer> mStatus, Integer journalId, String statusCode,
			Integer userId);
	
	@Query(value="SELECT COUNT(*) FROM manuscript m WHERE m.manuscript_status_id IN :statusId AND m.is_active=1 AND m.journal_id=:journalId AND (m.assigned_user=:userId OR m.assigned_user_role=:roleId)",nativeQuery = true )
	Integer getTotalResbmittedManuscriptForDashboard(List<Integer> statusId, Integer journalId,Integer userId, Integer roleId);

	@Query("select distinct m.issueName from ManuScriptForDashboard m where m.journalId=:journalId")
	List<String> getDistinctIssueList(Integer journalId);

	@Query("select count(m) from ManuScriptForDashboard m "
			+ " LEFT JOIN User u ON u.id=m.authorId"
			+ " LEFT JOIN ManuscriptTypeDetails mt ON m.manuScriptId = mt.manuscriptId"
			+ " WHERE m.journalId=:jId AND m.manuscriptStatusId IN :statusId "
			+ " AND m.manuscriptCode LIKE %:msCode% "
			+ " AND (:issue IS NULL OR :issue = '' OR m.issueName = :issue)"
			+ " AND (u.firstName LIKE %:author% OR u.lastName LIKE %:author%)"
			+ " AND (:submitDate IS NULL OR date(m.submissionDate) = :submitDate)"
			+ " AND (:assignDate IS NULL OR date(m.productionDate) = :assignDate)"
			+ " AND mt.manuscriptType LIKE %:msType%"
			+ " AND m.manuscriptTitle LIKE %:msTitle%"

			//			+ " AND m.assignedUserRole=:roleId "
			+ " AND m.isActive=1")
	Integer searchLTECount(List<Integer> statusId, Integer jId, String msCode, String issue, String author, Date submitDate, Date assignDate, String msType, String msTitle);

	@Query("select m from ManuScriptForDashboard m "
			+ " LEFT JOIN User u ON u.id=m.authorId"
			+ " LEFT JOIN ManuscriptTypeDetails mt ON m.manuScriptId = mt.manuscriptId"
			+ " WHERE m.journalId=:jId AND m.manuscriptStatusId IN :statusId "
			+ " AND m.manuscriptCode LIKE %:msCode% "
			+ " AND (:issue IS NULL OR :issue = '' OR m.issueName = :issue)"
			+ " AND (u.firstName LIKE %:author% OR u.lastName LIKE %:author%)"
			+ " AND (:submitDate IS NULL OR date(m.submissionDate) = :submitDate)"
			+ " AND (:assignDate IS NULL OR date(m.productionDate) = :assignDate)"
			+ " AND mt.manuscriptType LIKE %:msType%"
			+ " AND m.manuscriptTitle LIKE %:msTitle%"
			+ " AND m.isActive=1")
	List<ManuScriptForDashboard> searchLTE(List<Integer> statusId, Integer jId, String msCode, String issue, String author, Date submitDate, Date assignDate, String msType, String msTitle, Pageable paging);

}