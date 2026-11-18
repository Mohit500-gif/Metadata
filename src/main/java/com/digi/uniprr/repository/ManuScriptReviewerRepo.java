package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.VO.*;
import com.digi.uniprr.model.ManuScriptReviewer;

@Repository
@Transactional
public interface ManuScriptReviewerRepo extends JpaRepository<ManuScriptReviewer, Integer> {

	@Query(value = "SELECT * FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.reviewer_id=:reviewerId AND r.manuscript_version=:manuscriptVersion AND r.is_active=1", nativeQuery = true)
	List<ManuScriptReviewer> getmanuScriptReviewerById(Integer manuscriptId, Integer reviewerId,
			Integer manuscriptVersion);
	
	@Query(value = "SELECT COUNT(*) FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.reviewer_id=:reviewerId AND r.manuscript_version=:manuscriptVersion AND r.journal_id=:journalId AND r.manuscript_status IN(8,66) AND r.is_active=1", nativeQuery = true)
	Integer getInvitedreviewer(Integer manuscriptId, Integer reviewerId, Integer journalId,
			Integer manuscriptVersion);
	
	@Query(value = "SELECT COUNT(*) FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_version=:manuscriptVersion AND r.journal_id=:journalId AND r.is_active=1", nativeQuery = true)
	Integer getRemovedReViewerCount(Integer manuscriptId, Integer journalId,
			Integer manuscriptVersion);
	
	@Query(value = "SELECT COUNT(*) FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.journal_id=:journalId", nativeQuery = true)
	Integer getReviewerCount(Integer manuscriptId, Integer journalId);
	
	@Query(value = "SELECT * FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_version=:manuscriptVersion AND r.is_active=0", nativeQuery = true)
	List<ManuScriptReviewer> getmanuScriptReviewerToMailAndRemove(Integer manuscriptId, Integer manuscriptVersion);

	@Modifying
	@Query(value = "INSERT INTO manuscript_reviewer (manuscript_id,manuscript_version,reviewer_id,editor_id,manuscript_status,publon_credit,assigned_date,journal_id,is_assigned) VALUES(:manuscriptId, :manuscriptVersion, :reviewerId, :editorId, :manuscriptStatus, :publonCredit, :assignedDate, :journalId, :isAssigned) ", nativeQuery = true)
	void saveManuScriptReviewer(Integer manuscriptId, Integer manuscriptVersion, Integer reviewerId, Integer editorId,
			Integer manuscriptStatus, String publonCredit, Date assignedDate, Integer journalId, Integer isAssigned);

	@Modifying(clearAutomatically = true)
	@Query("update ManuScriptReviewer wrs set wrs.isActive =:active where wrs.manuscriptId =:manuscriptId and wrs.manuscriptVersion=:manuscriptVersion")
	void updateManuScriptEditorReviewer(Integer manuscriptId, Integer manuscriptVersion, Integer active);

	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:reviewerId AND is_active=1 AND m.manuscript_status=:manuscriptStatus AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16,58)) GROUP BY m.manuscript_id order by m.assigned_date desc", nativeQuery = true)
	List<ManuScriptReviewer> getManuscriptReviewer(Integer manuscriptStatus, Integer journalId, Integer reviewerId);	
	
	@Query(value="SELECT COUNT(*) FROM (SELECT COUNT(*) FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:reviewerId AND is_active=1 AND m.manuscript_status IN :manuscriptStatus AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16,58)) GROUP BY m.manuscript_id) AS inviteCount", nativeQuery = true)
	Integer getManuscriptReviewerCount(List<Integer> manuscriptStatus, Integer journalId, Integer reviewerId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptStatus=:manuscriptStatus AND m.journalId=:journalId AND m.isActive=1")
	List<ManuScriptReviewer> getManuscriptReviewerListById(Integer manuscriptStatus, Integer journalId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId=:journalId GROUP BY m.reviewerId")
	List<ManuScriptReviewer> getManuscriptReviewerListByJournalId(Integer journalId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuScriptId AND m.manuscriptVersion=:manuscriptVersion AND m.journalId=:journalId AND m.reviewerId=:assignToId AND m.isActive=1")
	ManuScriptReviewer findManuscriptReviewerDetails(Integer manuScriptId, Integer manuscriptVersion, Integer journalId,
			Integer assignToId);
	
	@Query(value="SELECT * FROM manuscript_reviewer r WHERE r.manuscript_id=:manuScriptId AND r.reviewer_id=:assignToId AND r.manuscript_version=:manuscriptVersion AND r.journal_id=:journalId AND r.is_active=1 ORDER BY r.id DESC LIMIT 1", nativeQuery = true)
	ManuScriptReviewer findReviewerForAssign(Integer manuScriptId, Integer manuscriptVersion, Integer journalId,
			Integer assignToId);

	@Modifying
	@Query("UPDATE ManuScriptReviewer m SET m.manuscriptStatus=:manuStatusId,m.isActive=1,m.dueDate=:duedate WHERE m.Id=:id")
	void updateReviewerDetails(Integer manuStatusId, Integer id, Date duedate);

	@Modifying
	@Query("UPDATE ManuScriptReviewer m SET m.manuscriptStatus=:manuStatusId WHERE m.Id=:id")
	void updateReviewerStatus(Integer manuStatusId, Integer id);
	
	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion")
	List<ManuScriptReviewer> getManuscriptReviewerListByManuScriptIdAndVersion(Integer manuscriptId,
			Integer manuscriptVersion);
	
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.manuscript_id=:manuscriptId AND m.manuscript_status IN(8,35,66) AND m.manuscript_version=:manuscriptVersion AND m.is_active=1 ORDER BY m.due_Date DESC LIMIT 1", nativeQuery = true)
	ManuScriptReviewer getManuscriptInReviewDueDate(Integer manuscriptId,
			Integer manuscriptVersion);
	
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.manuscript_id=:manuscriptId AND m.manuscript_version=:manuscriptVersion AND m.manuscript_status IN(12,13,15,14) AND m.is_active=1 ORDER BY m.due_Date DESC LIMIT 1", nativeQuery = true)
	ManuScriptReviewer getManuscriptReviewedDueDate(Integer manuscriptId,
			Integer manuscriptVersion);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND (m.manuscriptStatus=8 OR m.manuscriptStatus=35 OR m.manuscriptStatus=66) ")
	List<ManuScriptReviewer> getManuscriptReviewerListForblinker(Integer manuscriptId,
			Integer manuscriptVersion);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.isActive=1")
	List<ManuScriptReviewer> getManuscriptReviewerListByManuScriptIdAndVersionIsActive(Integer manuscriptId,
			Integer manuscriptVersion);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.reviewerId=:reviewerId")
	List<ManuScriptReviewer> getDataReviewerAndMnuscriptId(Integer manuscriptId, Integer reviewerId);

	@Query("SELECT r FROM ManuScriptReviewer r WHERE r.manuscriptId=:manuscriptId")
	List<ManuScriptReviewer> getByManuscriptId(Integer manuscriptId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.manuscriptStatus IN :manuStatusId")
	List<ManuScriptReviewer> getManuscriptReviewerListByManuScriptIdAndStatus(Integer manuscriptId,
			Integer manuscriptVersion, List<Integer> manuStatusId);

//	@Query("Select mr from ManuScriptReviewer mr where mr.reviewerId=:reviewerId AND mr.journalId =:journalId")
	@Query(value="SELECT * FROM manuscript_reviewer m  WHERE m.journal_id=:journalId AND m.reviewer_id=:reviewerId AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,33)) GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptReviewer> getReviewerDataById(Integer reviewerId, Integer journalId);

	@Query("Select mr from ManuScriptReviewer mr where mr.reviewerId=:reviewerId AND mr.journalId =:journalId")
	List<ManuScriptReviewer> getReviewerDataByIdForCompleteDate(Integer reviewerId, Integer journalId);
	
	@Query("Select mr from ManuScriptReviewer mr where mr.reviewerId=:reviewerId AND mr.manuscriptStatus IN :manusriptStatusId AND  mr.journalId =:journalId AND mr.isActive=1")
	List<ManuScriptReviewer> getReviewerDataById(Integer reviewerId, List<Integer> manusriptStatusId,
			Integer journalId);

	@Query("Select mr from ManuScriptReviewer mr where mr.manuscriptId =:manuscriptId AND mr.manuscriptVersion =:manuscriptVersion AND mr.journalId =:journalId")
	List<ManuScriptReviewer> getReviewerDataByManuscriptVersion(Integer manuscriptId, Integer manuscriptVersion,
			Integer journalId);

	@Query("Select mr from ManuScriptReviewer mr where mr.manuscriptId =:manuscriptId AND mr.manuscriptVersion =:manuscriptVersion AND mr.journalId =:journalId AND mr.reviewerId=:reviewerId AND mr.isActive=1")
	ManuScriptReviewer getReviewerDataByManuscriptVersionAssigned(Integer manuscriptId, Integer manuscriptVersion,
			Integer journalId, Integer reviewerId);

	@Query("Select mr from ManuScriptReviewer mr where mr.journalId =:journalId AND mr.manuscriptId !=:manuscriptId OR mr.manuscriptVersion !=:manuscriptVersion")
	List<ManuScriptReviewer> getAssignedReviewer(Integer manuscriptId, Integer manuscriptVersion, Integer journalId);

	@Query("Select mr from ManuScriptReviewer mr where mr.manuscriptId =:manuscriptId AND mr.manuscriptVersion =:manuscriptVersion AND mr.manuscriptStatus NOT IN (12,13,14,15)")
	List<ManuScriptReviewer> withdrawnAllReviewerInvitation(Integer manuscriptId, Integer manuscriptVersion);

	@Query("Select mr from ManuScriptReviewer mr where mr.manuscriptId =:manuscriptId AND mr.manuscriptVersion =:manuscriptVersion AND mr.isActive=1")
	List<ManuScriptReviewer> getWorkingReviewerList(Integer manuscriptId, Integer manuscriptVersion);

	
	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId")
	List<ManuScriptReviewer> getManuscriptReviewerListByJournal(List<Integer> journalId);

	@Query("Select mr from ManuScriptReviewer mr where mr.journalId =:journalId AND mr.manuscriptId !=:manuscriptId OR mr.manuscriptVersion !=:manuscriptVersion GROUP BY mr.reviewerId")
	List<ManuScriptReviewer> getUniqueUnassignedReviewer(Integer manuscriptId, Integer manuscriptVersion,
			Integer journalId);

	@Query("Select mr from ManuScriptReviewer mr where mr.journalId =:journalId AND mr.manuscriptId =:manuscriptId AND mr.manuscriptVersion =:manuscriptVersion AND mr.isActive=1")
	List<ManuScriptReviewer> getUniqueUnassignedReviewerData(Integer manuscriptId, Integer manuscriptVersion,
			Integer journalId);
	@Query(value = "Select * from manuscript_reviewer where journal_id =:journalId AND manuscript_id =:manuscriptId AND manuscript_version =:manuscriptVersion AND is_active=1 limit :limit offset :startIndex",nativeQuery = true)
	List<ManuScriptReviewer> getUniqueUnassignedReviewerDataPagination(Integer manuscriptId, Integer manuscriptVersion,
															 Integer journalId, Integer startIndex, Integer limit);
	@Query("Select count(*) from ManuScriptReviewer mr where mr.journalId =:journalId AND mr.manuscriptId =:manuscriptId AND mr.manuscriptVersion =:manuscriptVersion AND mr.isActive=1")
	Integer getUniqueUnassignedReviewerCount(Integer manuscriptId, Integer manuscriptVersion,
																	   Integer journalId);

	@Query(value="SELECT mr.manuscript_id ,mr.manuscript_version, mr.reviewer_id , mr.manuscript_status , mr.is_active , mr.due_date FROM manuscript_reviewer mr  WHERE mr.journal_id=:journalId", nativeQuery = true)
	List<Tuple> getManuscriptReviewerByJournalId(Integer journalId);
	
	@Query(value="SELECT mr.manuscript_id ,mr.manuscript_version, mr.reviewer_id , mr.manuscript_status , mr.is_active , mr.due_date FROM manuscript_reviewer mr  WHERE mr.editor_id=:editorId AND mr.journal_id=:journalId AND mr.manuscript_version = (SELECT MAX(m.manuscript_version) FROM manuscript_reviewer m where m.manuscript_id=mr.manuscript_id);", nativeQuery = true)
	List<Tuple> getManuscriptReviewerByJournalIdEditorId(Integer journalId, Integer editorId);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id,m.assigned_date , m.due_date FROM manuscript_reviewer m  WHERE m.journal_id=:journalId AND m.is_active=1 ORDER BY m.due_Date DESC", nativeQuery = true)
	List<Tuple> getManuscriptJournalIdTuple(Integer journalId);

	@Query(value="SELECT mr.manuscript_id ,mr.manuscript_version, mr.reviewer_id , mr.manuscript_status , mr.is_active , mr.due_date FROM manuscript_reviewer mr  WHERE mr.manuscript_id IN : manuscriptId", nativeQuery = true)
	List<Tuple> getManuscriptReviewerBymanuscriptId(List<Integer> manuscriptId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId")
	ManuScriptReviewer getManuscriptByReviewerByManuscript(Integer manuscriptId);
	
	@Query("SELECT new com.digi.uniprr.VO.ReviewersTakenTimeVO(COUNT(mr.manuscriptId) AS manuscriptCount ,sum(DATEDIFF(mr.assignedDate,ms.submissionDate)) AS totalsum) FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptVersion=0 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	ReviewersTakenTimeVO getReviewersTakenTime(Integer journalId, Integer year);
	
	@Query("SELECT  CASE WHEN max(DATEDIFF(mr.assignedDate,ms.submissionDate))  IS null THEN 0 ELSE max(DATEDIFF(mr.assignedDate,ms.submissionDate)) END FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptVersion=0 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	float getReviewersTakenTimeMax(Integer journalId, Integer year);
	
	@Query("SELECT  CASE WHEN min(DATEDIFF(mr.assignedDate,ms.submissionDate))  IS null THEN 0 ELSE min(DATEDIFF(mr.assignedDate,ms.submissionDate)) END FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptVersion=0 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	float getReviewersTakenTimeMin(Integer journalId, Integer year);

	@Query("SELECT new com.digi.uniprr.VO.ReviewersTakenTimeVO(COUNT(mr.manuscriptId) AS manuscriptCount ,sum(DATEDIFF(mr.completeDate,mr.startDate)) AS totalsum ) FROM ManuScript ms , ManuScriptReviewer mr WHERE  ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptVersion=0 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	ReviewersTakenTimeVO getDaysTakenByReviewers(Integer journalId, Integer year);
	
	@Query("SELECT  CASE WHEN max(DATEDIFF(mr.completeDate,mr.startDate))  IS null THEN 0 ELSE max(DATEDIFF(mr.completeDate,mr.startDate)) END FROM ManuScript ms , ManuScriptReviewer mr WHERE  ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptVersion=0 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	float getDaysTakenByReviewersMax(Integer journalId, Integer year);
	
	@Query("SELECT  CASE WHEN min(DATEDIFF(mr.completeDate,mr.startDate))  IS null THEN 0 ELSE min(DATEDIFF(mr.completeDate,mr.startDate)) END  FROM ManuScript ms , ManuScriptReviewer mr WHERE  ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptVersion=0 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	float getDaysTakenByReviewersMin(Integer journalId, Integer year);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId=:journalId  AND YEAR(m.assignedDate)=:year GROUP BY m.reviewerId")
	List<ManuScriptReviewer> getTotalReviewer(Integer journalId, Integer year);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status , m.is_active , m.due_date , m.assigned_date, m.complete_date, concat(u.first_name,' ',u.last_name) as reviewer_name FROM manuscript_reviewer m INNER JOIN users u ON u.id=m.reviewer_id WHERE m.journal_id IN :journalId AND YEAR(m.assigned_date) IN :year AND MONTH(m.assigned_date)=:month", nativeQuery = true)
	List<Tuple> getTotalReviewerByMonthReviewer(List<Integer> journalId, List<Integer> year, Integer month);
	
	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId  AND YEAR(m.assignedDate) IN :year AND MONTH(m.assignedDate)=:month AND m.reviewerId IN (SELECT userId FROM UserJournal WHERE roleId =5 AND isActive=1)")
	List<ManuScriptReviewer> getTotalReviewerByMonth(List<Integer> journalId, List<Integer> year, Integer month);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.reviewerId=:reviewerUserId AND m.journalId=:journalId AND m.isActive=1 AND m.manuscriptStatus=35")
	ManuScriptReviewer getManuScriptReviewerEditor(Integer manuscriptId, Integer manuscriptVersion,
			Integer reviewerUserId, Integer journalId);
	
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.manuscript_id=:manuscriptId AND m.manuscript_version=:manuscriptVersion AND m.reviewer_id=:reviewerUserId AND m.journal_id=:journalId AND m.is_active=1 ORDER BY id LIMIT 1", nativeQuery=true)
	ManuScriptReviewer getManuScriptReviewerDecision(Integer manuscriptId, Integer manuscriptVersion,
			Integer reviewerUserId, Integer journalId);

//	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.reviewerId=:reviewerUserId AND m.journalId=:journalId AND m.isActive=1 AND m.manuscriptStatus=36 ORDER BY m.rejectDate DESC")
//	ManuScriptReviewer getManuScriptReviewerDeclineManuscript(Integer manuscriptId, Integer manuscriptVersion,
//			Integer reviewerUserId, Integer journalId);

	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.manuscript_id=:manuscriptId AND m.manuscript_version=:manuscriptVersion AND m.reviewer_id=:reviewerUserId AND m.journal_id=:journalId AND m.is_active=1 AND m.manuscript_status=36 ORDER BY m.reject_date desc limit 1",nativeQuery = true)
	ManuScriptReviewer getManuScriptReviewerDeclineManuscript(Integer manuscriptId, Integer manuscriptVersion,
															  Integer reviewerUserId, Integer journalId);
	
	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.reviewerId=:reviewerUserId AND m.journalId=:journalId AND m.isActive=1 AND m.manuscriptStatus=60 ORDER BY m.id DESC")
	List<ManuScriptReviewer> getManuScriptReviewerDeclineManuscriptDecline(Integer manuscriptId, Integer manuscriptVersion,
			Integer reviewerUserId, Integer journalId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:manuId AND m.isActive=1")
	List<ManuScriptReviewer> getReviewedManuscript(Integer manuId);
	
	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.manuscriptId=:manuId AND m.isActive=1")
	List<ManuScriptReviewer> getReviewedManu(Integer manuId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId=:manuId group by m.manuscriptId, m.manuscriptVersion")
	List<ManuScriptReviewer> getReviewedManuscriptUnique(Integer manuId);

	@Query(value = "SELECT * FROM manuscript_reviewer a INNER JOIN (SELECT manuscript_id, MAX(manuscript_version) manuscript_version FROM manuscript_reviewer GROUP BY manuscript_id) b ON a.manuscript_id = b.manuscript_id AND a.manuscript_version = b.manuscript_version WHERE a.journal_id =:journalId AND a.reviewer_id=:reviewerId AND a.is_active=1 AND a.manuscript_status!=60 AND a.manuscript_status!=8", nativeQuery = true)
	List<ManuScriptReviewer> getRereviewData(Integer journalId, Integer reviewerId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.id=:manuReviewerId AND m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.journalId=:journalId AND m.reviewerId=:reviewerId")
	ManuScriptReviewer getManuScriptReviewerData(Integer manuReviewerId, Integer manuscriptId, Integer manuscriptVersion, Integer journalId,
			Integer reviewerId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.isActive=0")
	List<ManuScriptReviewer> getDeactivateManuscipt(Integer journalId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:manuId")
	List<ManuScriptReviewer> getManuscriptByJournal(Integer manuId);

	@Query(value = "SELECT * FROM manuscript_reviewer m  WHERE m.journal_id =:journalId AND m.write_commentary='Yes' GROUP BY m.manuscript_id,m.reviewer_id", nativeQuery = true)
	List<ManuScriptReviewer> getCommentaryData(Integer journalId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.reviewerId=:userId AND m.isInvitedForCommentary=1")
	List<ManuScriptReviewer> getCommentaryInvitation(Integer journalId, Integer userId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.reviewerId=:userId")
	List<ManuScriptReviewer> getReviewerStatistics(Integer journalId, Integer userId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptStatus=:manuscriptStatus AND m.journalId=:journalId AND m.isActive=1 GROUP BY m.manuscriptId, m.manuscriptVersion")
	List<ManuScriptReviewer> getInReviewManuscript(Integer manuscriptStatus, Integer journalId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptStatus=:manuscriptStatus AND m.journalId=:journalId AND m.manuscriptId In:manuscriptID AND m.isActive=1 GROUP BY m.manuscriptId, m.manuscriptVersion")
	List<ManuScriptReviewer> getInReviewManuscriptList(Integer manuscriptStatus, Integer journalId,
			List<Integer> manuscriptID);

	@Query("select t1 from ManuScriptReviewer t1 where t1.journalId=:manuId AND t1.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=t1.manuscriptId) GROUP BY t1.manuscriptId")
	List<ManuScriptReviewer> getManuscriptByJournalGroupByManuId(Integer manuId);

	@Modifying
	@Query("UPDATE ManuScriptReviewer m SET m.isActive=0 WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.reviewerId=:reviewerId AND m.Id=:id")
	void deActivateReviewer(Integer manuscriptId, Integer manuscriptVersion, Integer reviewerId,Integer id);

	@Modifying
	@Query("UPDATE ManuScriptReviewer m SET m.manuscriptStatus=:manuId WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.reviewerId=:userId AND m.journalId=:journalId AND m.manuscriptStatus=35")
	void decliendReview(Integer journalId, Integer manuId, Integer manuscriptVersion, Integer userId,
			Integer manuscriptId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.reviewerId=:reviewerId AND m.manuscriptStatus=:manuscriptStatus AND m.journalId=:journalId")
	List<ManuScriptReviewer> isAssignExist(Integer manuscriptId, Integer manuscriptVersion, Integer reviewerId,
			Integer manuscriptStatus, Integer journalId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.manuscriptId=:manuscriptId AND m.reviewerId=:userId ORDER BY m.manuscriptVersion DESC")
	List<ManuScriptReviewer> reviewerPreviousComment(Integer userId, Integer manuscriptId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.manuscriptId=:manuscriptId AND m.manuscriptStatus IN(12,13,14,15,36,30,60) GROUP BY m.reviewerId")
	List<ManuScriptReviewer> intrestedToReAccess(Integer manuscriptId);
	
	@Query(value = "SELECT m.* " +
			          "FROM manuscript_reviewer m " +
			          "INNER JOIN ( " +
			          "  SELECT reviewer_id, MAX(id) AS max_id " +
			          "  FROM manuscript_reviewer " +
			          "  WHERE manuscript_id = :manuscriptId " +
			          "    AND manuscript_status IN (12,13,14,15,36,30,60) " +
			          "  GROUP BY reviewer_id " +
			          ") t " +
			          "ON m.reviewer_id = t.reviewer_id " +
			          "AND m.id = t.max_id " +
			          "WHERE m.manuscript_id = :manuscriptId " +
			          "  AND m.manuscript_status IN (12,13,14,15,36,30,60)",
			          nativeQuery = true
			)
	List<ManuScriptReviewer> findLatestPerReviewerByMaxId(@Param("manuscriptId") Integer manuscriptId);


	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.manuscriptId=:manuscriptId AND m.manuscriptStatus IN(12,13,14,15,36,30,60) AND ( (m.id IS NULL OR m.id LIKE CONCAT('%', :search, '%')) OR (m.manuscriptVersion IS NULL OR m.manuscriptVersion LIKE CONCAT('%', :search, '%')) OR (m.journalId IS NULL OR m.journalId LIKE CONCAT('%', :search, '%')) OR (m.reviewerId IS NULL OR m.reviewerId LIKE CONCAT('%', :search, '%')) OR (m.editorId IS NULL OR m.editorId LIKE CONCAT('%', :search, '%')) OR (m.editorRoleId IS NULL OR m.editorRoleId LIKE CONCAT('%', :search, '%')) OR ( m.manuscriptStatus IS NULL OR m.manuscriptStatus LIKE CONCAT('%', :search, '%')) OR (m.publonCredit IS NULL OR m.publonCredit LIKE CONCAT('%', :search, '%')) OR (m.commentsToAuthor IS NULL OR m.commentsToAuthor LIKE CONCAT('%', :search, '%')) OR (m.commentsToEditor IS NULL OR m.commentsToEditor LIKE CONCAT('%', :search, '%')) OR (m.manuscriptPriority IS NULL OR m.manuscriptPriority LIKE CONCAT('%', :search, '%')) OR (m.competingInterests IS NULL OR m.competingInterests LIKE CONCAT('%', :search, '%')) OR (m.messageFromManuscript IS NULL OR m.messageFromManuscript LIKE CONCAT('%', :search, '%')) OR (m.reviewerResponse IS NULL OR m.reviewerResponse LIKE CONCAT('%', :search, '%')) OR (m.assignedDate IS NULL OR m.assignedDate LIKE CONCAT('%', :search, '%')) OR (m.unassignedDate IS NULL OR m.unassignedDate LIKE CONCAT('%', :search, '%')) OR (m.startDate IS NULL OR m.startDate LIKE CONCAT('%', :search, '%')) OR (m.completeDate IS NULL OR m.completeDate LIKE CONCAT('%', :search, '%')) OR (m.rejectDate IS NULL OR m.rejectDate LIKE CONCAT('%', :search, '%')) OR (m.acceptDate IS NULL OR m.acceptDate LIKE CONCAT('%', :search, '%')) OR (m.isAssigned IS NULL OR m.isAssigned LIKE CONCAT('%', :search, '%')) OR (m.isActive IS NULL OR m.isActive LIKE CONCAT('%', :search, '%')) OR (m.dueDate IS NULL OR m.dueDate LIKE CONCAT('%', :search, '%')) OR (m.rejectionReason IS NULL OR m.rejectionReason LIKE CONCAT('%', :search, '%')) OR (m.commentsForReviewer IS NULL OR m.commentsForReviewer LIKE CONCAT('%', :search, '%')) OR (m.decisionLetter IS NULL OR m.decisionLetter LIKE CONCAT('%', :search, '%')) OR (m.createdOn IS NULL OR m.createdOn LIKE CONCAT('%', :search, '%')) OR (m.createdBy IS NULL OR m.createdBy LIKE CONCAT('%', :search, '%')) OR (m.updatedOn IS NULL OR m.updatedOn LIKE CONCAT('%', :search, '%')) OR (m.requiresCommentary IS NULL OR m.requiresCommentary LIKE CONCAT('%', :search, '%')) OR (m.writeommentary IS NULL OR m.writeommentary LIKE CONCAT('%', :search, '%')) OR (m.isInvitedForCommentary IS NULL OR m.isInvitedForCommentary LIKE CONCAT('%', :search, '%')) OR (m.reasses IS NULL OR m.reasses LIKE CONCAT('%', :search, '%')) OR (m.sentoPublons IS NULL OR m.sentoPublons LIKE CONCAT('%', :search, '%'))) GROUP BY m.reviewerId")
	List<ManuScriptReviewer> intrestedToReAccessBySearch(Integer manuscriptId, String search);

	@Query("SELECT count(*) FROM ManuScriptReviewer m  WHERE m.manuscriptId=:manuscriptId AND m.manuscriptStatus IN(12,13,14,15,36,30,60) GROUP BY m.reviewerId")
	Integer intrestedToReAccessCount(Integer manuscriptId);

	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND (( SELECT COUNT(1) FROM manuscript_reviewer t WHERE t.manuscript_id=m.manuscript_id AND t.reviewer_id=m.reviewer_id)>1 OR m.complete_date IS NOT null) AND m.is_active=1 AND m.manuscript_status=35 AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16)) GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptReviewer> getReRivewManuscriptForEmail(Integer journalId, Integer userId);
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND (( SELECT COUNT(1) FROM manuscript_reviewer t WHERE t.manuscript_id=m.manuscript_id AND t.reviewer_id=m.reviewer_id and t.manuscript_status in (35,12,13,14,15))>1 OR m.complete_date IS NOT null) AND m.is_active=1 AND m.manuscript_status=35 AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16,58)) GROUP BY m.manuscript_id order by m.assigned_date desc", nativeQuery = true)
	List<ManuScriptReviewer> getReRivewManuscript(Integer journalId, Integer userId);
	
	@Query(value="SELECT COUNT(*) FROM (SELECT COUNT(*) FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND (( SELECT COUNT(1) FROM manuscript_reviewer t WHERE t.manuscript_id=m.manuscript_id AND t.reviewer_id=m.reviewer_id and t.manuscript_status in (35,12,13,14,15))>1 OR m.complete_date IS NOT null) AND m.is_active=1 AND m.manuscript_status=35 AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16,58)) GROUP BY m.manuscript_id) AS reReviewCount", nativeQuery = true)
	Integer getReRivewManuscriptCount(Integer journalId, Integer userId);

	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND (( SELECT COUNT(1) FROM manuscript_reviewer t WHERE t.manuscript_id=m.manuscript_id AND t.reviewer_id=m.reviewer_id and t.manuscript_status in (35,12,13,14,15))<=1 and m.complete_date IS null ) AND m.is_active=1 AND m.manuscript_status=35 AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16,58)) GROUP BY m.manuscript_id order by m.assigned_date desc", nativeQuery = true)
	List<ManuScriptReviewer> getManuscriptForReview(Integer journalId, Integer userId);

	@Query(value = "SELECT * FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND m.is_active=1 AND m.manuscript_status=35 AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16)) GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptReviewer> getAllManuscriptsForReview(Integer journalId, Integer userId);
	
	@Query(value="SELECT COUNT(*) FROM (SELECT COUNT(*) FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND (( SELECT COUNT(1) FROM manuscript_reviewer t WHERE t.manuscript_id=m.manuscript_id AND t.reviewer_id=m.reviewer_id and t.manuscript_status in (35,12,13,14,15))<=1 and m.complete_date IS null ) AND m.is_active=1 AND m.manuscript_status=35 AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16,58)) GROUP BY m.manuscript_id) AS inReviewCount", nativeQuery = true)
	Integer getManuscriptForReviewCount(Integer journalId, Integer userId);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=m.manuscriptId)")
	List<ManuScriptReviewer> getManuscriptByJournalEditor(Integer journalId, List<Integer> manuscriptIdList);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status , m.is_active , m.due_date , m.assigned_date, m.complete_date, concat(u.first_name,' ',u.last_name) as reviewer_name FROM manuscript_reviewer m INNER JOIN users u ON u.id=m.reviewer_id  WHERE m.journal_id=:journalId AND m.manuscript_id IN :manuscriptIdList AND m.manuscript_version=(SELECT max(t2.manuscript_version) from manuscript_reviewer t2 where t2.manuscript_id=m.manuscript_id)",nativeQuery = true )
	List<Tuple> getManuscriptByJournalEditorTuple(Integer journalId, List<Integer> manuscriptIdList);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=m.manuscriptId)")
	List<ManuScriptReviewer> getManuscriptByJournalGroupByManuIdEditor(Integer journalId,
			List<Integer> manuscriptIdList);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=m.manuscriptId) AND m.isActive=1")
	List<ManuScriptReviewer> getReviewedManuscriptEditor(Integer journalId, List<Integer> manuscriptIdList);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status , m.is_active , m.due_date , m.assigned_date, m.complete_date, concat(u.first_name,' ',u.last_name) as reviewer_name FROM manuscript_reviewer m INNER JOIN users u ON u.id=m.reviewer_id  WHERE m.journal_id=:journalId AND m.manuscript_id IN :manuscriptIdList AND m.manuscript_version=(SELECT max(t2.manuscript_version) from manuscript_reviewer t2 where t2.manuscript_id=m.manuscript_id) AND m.is_active=1",nativeQuery = true )
	List<Tuple> getReviewedManuscriptByTuple(Integer journalId, List<Integer> manuscriptIdList);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=m.manuscriptId)")
	List<ManuScriptReviewer> getReviewedManuscriptUniqueEditor(Integer journalId, List<Integer> manuscriptIdList);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=m.manuscriptId)")
	List<ManuScriptReviewer> getReviewedManuscriptUniqueEditorPaging(Integer journalId, List<Integer> manuscriptIdList,
			Pageable paging);

	@Query(value = "SELECT * FROM manuscript_reviewer msr WHERE msr.manuscript_version=(SELECT MAX(mr.manuscript_version) FROM manuscript_reviewer mr WHERE msr.manuscript_id=mr.manuscript_id) AND msr.is_active=1 AND msr.manuscript_id IN :manuscriptId", nativeQuery = true)
	List<ManuScriptReviewer> getReviewedManuscriptByManuscriptList(List<Integer> manuscriptId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId IN :manuscriptId group by m.manuscriptId, m.manuscriptVersion")
	List<ManuScriptReviewer> getReviewedManuscriptUniqueByManuscriptList(List<Integer> manuscriptId);

	@Query("select t1 from ManuScriptReviewer t1 where t1.manuscriptId IN:manuscriptId AND t1.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=t1.manuscriptId) GROUP BY t1.manuscriptId")
	List<ManuScriptReviewer> getManuscriptByJournalByManuscriptList(List<Integer> manuscriptId);

	@Query("select t1 from ManuScriptReviewer t1 where t1.manuscriptId IN:manuscriptId AND t1.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=t1.manuscriptId) GROUP BY t1.manuscriptId")
	List<ManuScriptReviewer> getManuscriptByJournalGroupByManuIdByManuscriptList(List<Integer> manuscriptId);

	@Query(value = "SELECT * FROM manuscript_reviewer msr WHERE msr.manuscript_version=(SELECT MAX(mr.manuscript_version) FROM manuscript_reviewer mr WHERE msr.manuscript_id=mr.manuscript_id) AND msr.manuscript_id IN :manuscriptId", nativeQuery = true)
	List<ManuScriptReviewer> getReviewedManuscriptByManuscriptListNoResponse(List<Integer> manuscriptId);

	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status , m.is_active , m.due_date , m.assigned_date, m.complete_date, concat(u.first_name,' ',u.last_name) as reviewer_name FROM manuscript_reviewer m INNER JOIN users u ON u.id=m.reviewer_id  WHERE m.journal_id=:journalId AND m.manuscript_id IN :manuscriptIdList AND m.manuscript_version=(SELECT max(t2.manuscript_version) from manuscript_reviewer t2 where t2.manuscript_id=m.manuscript_id) AND m.is_active=1 AND (m.manuscript_status=8 OR m.manuscript_status=35 OR m.manuscript_status=66)  AND m.due_date >= CURDATE() GROUP BY m.manuscript_id, m.manuscript_version", nativeQuery=true)
	List<Tuple> getInReviewManuscriptByTuple(Integer journalId, List<Integer> manuscriptIdList);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status  FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.manuscript_id IN :manuscriptIdList AND m.manuscript_version=(SELECT max(t2.manuscript_version) from manuscript_reviewer t2 where t2.manuscript_id=m.manuscript_id) AND m.is_active=1 AND (m.manuscript_status=8 OR m.manuscript_status=35 OR m.manuscript_status=66)  AND m.due_date >= CURDATE() GROUP BY m.manuscript_id, m.manuscript_version", nativeQuery=true)
	List<Tuple> getInReviewManuscriptByCount(Integer journalId, List<Integer> manuscriptIdList);

	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=m.manuscriptId) AND m.isActive=1 AND (m.manuscriptStatus=8 OR m.manuscriptStatus=35 OR m.manuscriptStatus=66)  AND m.dueDate >= CURDATE() GROUP BY m.manuscriptId, m.manuscriptVersion")
	List<ManuScriptReviewer> getInReviewManuscriptForEditor(Integer journalId, List<Integer> manuscriptIdList);
	
	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.manuscriptId IN :manuscriptIdList AND m.manuscript.manuscriptVersion=m.manuscriptVersion AND m.isActive=1 AND (m.manuscriptStatus=8 OR m.manuscriptStatus=35 OR m.manuscriptStatus=66)  AND m.dueDate >= CURDATE() GROUP BY m.manuscriptId, m.manuscriptVersion")
	List<ManuScriptReviewer> getInReviewManuscript(List<Integer> manuscriptIdList);

	
	@Query("SELECT m FROM ManuScriptReviewer m  WHERE m.journalId=:journalId AND m.manuscriptId IN :manuscriptIdList AND m.manuscriptVersion=(SELECT max(t2.manuscriptVersion) from ManuScriptReviewer t2 where t2.manuscriptId=m.manuscriptId) AND m.isActive=1 AND (m.manuscriptStatus=8 OR m.manuscriptStatus=35 OR m.manuscriptStatus=66) AND m.dueDate >= CURDATE() GROUP BY m.manuscriptId, m.manuscriptVersion")
	List<ManuScriptReviewer> getInReviewManuscriptForEditorPaging(Integer journalId, List<Integer> manuscriptIdList,
			Pageable paging);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId =:manuscriptId AND m.manuscriptVersion =:manuscriptVersion AND (m.manuscriptStatus=8 OR m.manuscriptStatus=35 OR m.manuscriptStatus=66)")
	List<ManuScriptReviewer> getUnreviewedData(Integer manuscriptId, Integer manuscriptVersion);
	
	@Query(value = 
		    "SELECT mr.* FROM manuscript_reviewer mr " +
		    "JOIN (SELECT manuscript_id, MAX(complete_date) AS max_created_at " +
		    "      FROM manuscript_reviewer " +
		    "      WHERE journal_id = :journalId AND reviewer_id = :userId AND manuscript_status IN (:manuscriptStatusId) " +
		    "      AND complete_date IS NOT NULL GROUP BY manuscript_id) AS grouped_max " +
		    "ON mr.manuscript_id = grouped_max.manuscript_id AND mr.complete_date = grouped_max.max_created_at " +
		    "WHERE mr.journal_id = :journalId AND mr.reviewer_id = :userId AND mr.manuscript_status IN (:manuscriptStatusId) order by mr.assigned_date desc",
		    nativeQuery = true)	List<ManuScriptReviewer> getReviewedManuscriptForReviewer(List<Integer> manuscriptStatusId, Integer journalId,
			Integer userId);
	
	@Query(value="SELECT COUNT(*) FROM (SELECT COUNT(*) FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND m.manuscript_status IN :manuscriptStatusId AND m.complete_date IS NOT NULL GROUP BY m.manuscript_id) AS totalReviewedCount", nativeQuery = true)
	Integer getReviewedManuscriptForReviewerCount(List<Integer> manuscriptStatusId, Integer journalId,
			Integer userId);
	
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.manuscript_id=:manuscriptId AND m.manuscript_version =:manuscriptVersion AND m.reviewer_id=:userId AND m.is_active=1 AND m.manuscript_status IN :manuscriptStatusId", nativeQuery = true)
	List<ManuScriptReviewer> getReviewerForManuscript(List<Integer> manuscriptStatusId, Integer manuscriptId,
			Integer userId, Integer manuscriptVersion);
	
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.manuscript_id=:manuscriptId AND m.reviewer_id=:userId AND m.manuscript_status IN :manuscriptStatusId", nativeQuery = true)
	List<ManuScriptReviewer> getReviewerForIntrAcess(List<Integer> manuscriptStatusId, Integer manuscriptId,
			Integer userId);
	
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND m.manuscript_status IN :manuscriptStatusId AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16)) GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptReviewer> getRevieweManuscriptForStatic(List<Integer> manuscriptStatusId, Integer journalId,
			Integer userId);
	
	@Query(value="SELECT * FROM manuscript_reviewer m WHERE m.journal_id=:journalId AND m.reviewer_id=:userId AND m.is_active=1 AND m.manuscript_status IN :manuscriptStatusId AND manuscript_id not IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30,65,59,33,57,61,62,16)) GROUP BY m.manuscript_id", nativeQuery = true)
	List<ManuScriptReviewer> getRevieweManuscriptForStaticActive(List<Integer> manuscriptStatusId, Integer journalId,
			Integer userId);
	
	@Query("Select r.reviewerId from ManuScriptReviewer r where r.manuscriptId=:manuscriptId AND r.manuscriptVersion=:manuscriptVersion AND r.isActive=1 AND r.manuscriptStatus IN(8,35,66)")
	List<Integer> getInprocessReviewerList(Integer manuscriptId, Integer manuscriptVersion);
	
	@Query("Select r from ManuScriptReviewer r where r.manuscriptId=:manuscriptId AND r.manuscriptVersion=:manuscriptVersion AND r.isActive=1 AND r.manuscriptStatus IN(8,35,66)")
	List<ManuScriptReviewer> getInpogressReviewerList(Integer manuscriptId, Integer manuscriptVersion);

	@Modifying(clearAutomatically = true)
	@Query("update ManuScriptReviewer m set m.editorId=:currentUserId WHERE m.editorId=:previousUserId AND m.journalId=:journalId AND m.editorRoleId=2")
	void updatePreviousManagingEditor(Integer currentUserId, Integer previousUserId, Integer journalId);

	@Query("SELECT r FROM ManuScriptReviewer r WHERE r.manuscriptId=:manuscriptId AND r.manuscriptVersion=:manuscriptVersion AND r.journalId=:journalId AND r.isActive=1")
	List<ManuScriptReviewer> getmanuScriptReviewer(Integer manuscriptId, Integer manuscriptVersion, Integer journalId);

	@Query("SELECT r FROM ManuScriptReviewer r WHERE r.manuscriptId=:manuscriptId AND r.journalId=:journalId")
	List<ManuScriptReviewer> getmanuScriptAllReviewer(Integer manuscriptId, Integer journalId);

	@Query(value = "SELECT * FROM manuscript_reviewer mr WHERE mr.manuscript_id=:manuscriptId AND mr.journal_id=:journalId AND mr.manuscript_version=(SELECT MAX(mrw.manuscript_version) FROM manuscript_reviewer mrw WHERE mrw.manuscript_id=:manuscriptId AND mrw.journal_id=:journalId AND mrw.is_active=1)", nativeQuery = true)
	List<ManuScriptReviewer> getReviewerDataForManuscript(Integer manuscriptId, Integer journalId);

	@Query("SELECT r FROM ManuScriptReviewer r WHERE r.manuscriptId=:manuscriptId AND r.isActive=1 AND r.journalId=:journalId AND r.manuscriptVersion=:manuscriptVersion AND r.manuscriptStatus IN :statusId")
	List<ManuScriptReviewer> getmanuScriptReviewerForReminder(Integer manuscriptId, Integer journalId,
			List<Integer> statusId, Integer manuscriptVersion);
	
	@Query("SELECT r FROM ManuScriptReviewer r WHERE r.manuscriptId=:manuscriptId AND r.isActive=1 AND r.journalId=:journalId AND r.manuscriptVersion=:manuscriptVersion AND r.manuscriptStatus IN :statusId AND DATE(r.dueDate) < CURDATE()")
	List<ManuScriptReviewer> getmanuScriptReviewerForReminderForSchedular(Integer manuscriptId, Integer journalId,
			List<Integer> statusId, Integer manuscriptVersion);
	
	@Query("SELECT r.dueDate FROM ManuScriptReviewer r WHERE r.manuscriptId=:manuscriptId AND r.isActive=1 AND r.journalId=:journalId AND r.manuscriptVersion=:manuscriptVersion AND r.manuscriptStatus IN (8,66,35) AND r.reviewerId=:reviewerId")
	Date getmanuScriptReviewerForDueDate(Integer manuscriptId, Integer journalId,
			Integer manuscriptVersion,Integer reviewerId);

	@Query("SELECT COUNT(*) FROM ManuScriptReviewer r WHERE r.manuscriptId=:manuscriptId AND r.isActive=1 AND r.journalId=:journalId AND r.manuscriptVersion=:manuscriptVersion")
	Integer activeReviewerCount(Integer manuscriptId, Integer journalId, Integer manuscriptVersion);


	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year AND completeDate > dueDate")
	List<ManuScriptReviewer> getAllManuscriptLateReturn(List<Integer> journalId, List<Integer> year);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND  YEAR(m.assignedDate) IN :year AND m.manuscriptStatus IN (35,8,66) AND m.completeDate is null")
	List<ManuScriptReviewer> getAllManuscriptNeverReturn(List<Integer> journalId, List<Integer> year);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year AND MONTH(m.assignedDate)=:month AND completeDate > dueDate")
	List<ManuScriptReviewer> getManuscriptLateReturn(List<Integer> journalId, List<Integer> year, Integer month);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year AND MONTH(m.assignedDate)=:month AND m.manuscriptStatus IN (35,8,66) AND m.completeDate is null")
	List<ManuScriptReviewer> getManuscriptNeverReturn(List<Integer> journalId, List<Integer> year, Integer month);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year AND MONTH(m.assignedDate)=:month AND completeDate < dueDate")
	List<ManuScriptReviewer> getManuscriptOnTime(List<Integer> journalId, List<Integer> year, Integer month);

	@Query("SELECT  distinct(m.reviewerId) FROM ManuScriptReviewer m WHERE m.journalId IN :journalId  AND YEAR(m.assignedDate) IN :year AND MONTH(m.assignedDate)=:month")
	List<Integer> getUsersTotalReviewerByMonthDetails(List<Integer> journalId, List<Integer> year, Integer month);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year AND MONTH(m.assignedDate)=:month AND reviewerId =:reviewerId ")
	List<ManuScriptReviewer> getmanManuScriptReviewer(List<Integer> journalId, List<Integer> year, Integer month,
			Integer reviewerId);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status , m.is_active , m.due_date , m.assigned_date, m.complete_date, concat(u.first_name,' ',u.last_name) as reviewer_name FROM manuscript_reviewer m INNER JOIN users u ON u.id=m.reviewer_id WHERE m.journal_id IN :journalId AND YEAR(m.assigned_date) IN :year", nativeQuery = true)
	List<Tuple> getTotalReviewerByYearReviewer(List<Integer> journalId, List<Integer> year);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId  AND YEAR(m.assignedDate) IN :year AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1)")
	List<ManuScriptReviewer> getTotalReviewerByYear(List<Integer> journalId, List<Integer> year);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE  m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year AND completeDate < dueDate")
	List<ManuScriptReviewer> getAllManuscriptOnTime(List<Integer> journalId, List<Integer> year);

	@Query("SELECT  distinct(m.reviewerId) FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year")
	List<Integer> getUsersTotalReviewerByJournalId(List<Integer> journalId, List<Integer> year);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId AND m.reviewerId IN (SELECT u.userId FROM UserJournal u WHERE u.roleId =5 AND u.isActive=1) AND YEAR(m.assignedDate) IN :year AND reviewerId =:reviewerId ")
	List<ManuScriptReviewer> getallManuScriptReviewer(List<Integer> journalId, List<Integer> year, Integer reviewerId);

	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId  AND YEAR(m.assignedDate) IN :year AND MONTH(m.assignedDate)=:month AND manuscriptId =:manuscriptId AND editorId =:editorId ")
	List<ManuScriptReviewer> getmanManuScripts(List<Integer> journalId, List<Integer> year, Integer month,
			Integer manuscriptId, Integer editorId);
	
	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.journalId IN :journalId  AND YEAR(m.assignedDate) IN :year  AND manuscriptId =:manuscriptId AND editorId =:editorId ")
	List<ManuScriptReviewer> getmanManuScriptsAll(List<Integer> journalId, List<Integer> year,Integer manuscriptId, Integer editorId);


	//@Query("SELECT new com.digi.uniprr.VO.EditorTakenTimeVO(COUNT(mr.manuscriptId) AS manuscriptCount ,sum(DATEDIFF(mr.assignedDate,ms.submissionDate)) AS totalsum) FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptStatusId IN (4,26) AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	@Query("SELECT  new com.digi.uniprr.VO.EditorTakenTimeVO(COUNT(a.manuscriptId) AS manuscriptCount ,sum(DATEDIFF(b.createdAt,a.createdAt)) AS totalsum) FROM AuthoritiesHistory a,AuthoritiesHistory b WHERE a.manuscriptId=b.manuscriptId AND a.manuscriptVersion=b.manuscriptVersion AND a.manuscriptStatusId IN (26) AND YEAR(a.createdAt)=:year AND YEAR(b.createdAt)=:year AND a.journalId=b.journalId AND a.journalId=:journalId AND b.manuscriptStatusId IN (4,66,51,52,68,33,3,30)AND b.manuscriptVersion=1  ORDER BY b.id asc")
	EditorTakenTimeVO getEditorsTakenTime(Integer journalId, Integer year);
	
//	@Query("SELECT  CASE WHEN max(DATEDIFF(mr.assignedDate,ms.submissionDate))  IS null THEN 0 ELSE max(DATEDIFF(mr.assignedDate,ms.submissionDate)) END FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptStatusId IN (4,26) AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	@Query("SELECT   CASE WHEN max(DATEDIFF(b.createdAt,a.createdAt))  IS null THEN 0 ELSE max(DATEDIFF(b.createdAt,a.createdAt)) END FROM AuthoritiesHistory a,AuthoritiesHistory b WHERE a.manuscriptId=b.manuscriptId AND a.manuscriptVersion=b.manuscriptVersion AND a.manuscriptStatusId IN (26) AND YEAR(a.createdAt)=:year AND YEAR(b.createdAt)=:year AND a.journalId=b.journalId AND a.journalId=:journalId AND b.manuscriptStatusId IN (4,66,51,52,68,33,3,30)AND b.manuscriptVersion=1  ORDER BY b.id asc")
	float getEditorsTakenTimeMax(Integer journalId, Integer year);
	
//	@Query("SELECT  CASE WHEN min(DATEDIFF(mr.assignedDate,ms.submissionDate))  IS null THEN 0 ELSE min(DATEDIFF(mr.assignedDate,ms.submissionDate)) END FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptStatusId IN (4,26) AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	@Query("SELECT  CASE WHEN min(DATEDIFF(b.createdAt,a.createdAt))  IS null THEN 0 ELSE min(DATEDIFF(b.createdAt,a.createdAt)) END FROM AuthoritiesHistory a,AuthoritiesHistory b WHERE a.manuscriptId=b.manuscriptId AND a.manuscriptVersion=b.manuscriptVersion AND a.manuscriptStatusId IN (26) AND YEAR(a.createdAt)=:year AND YEAR(b.createdAt)=:year AND a.journalId=b.journalId AND a.journalId=:journalId AND b.manuscriptStatusId IN (4,66,51,52,68,33,3,30)AND b.manuscriptVersion=1  ORDER BY b.id asc")
	float getEditorsTakenTimeMin(Integer journalId, Integer year);

//	@Query("SELECT new com.digi.uniprr.VO.AuthorTakenTimeVO(COUNT(mr.manuscriptId) AS manuscriptCount ,sum(DATEDIFF(mr.assignedDate,ms.submissionDate)) AS totalsum) FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptStatusId=26 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
//	@Query(value="SELECT new com.digi.uniprr.VO.AuthorTakenTimeVO(COUNT(a.manuscript_id) AS manuscriptCount, sum(DATEDIFF(date(b.created_at),date(a.created_at))) AS totalsum) FROM authorities_history a,authorities_history b WHERE a.manuscript_id=b.manuscript_id AND a.manuscript_version=0 AND b.manuscript_version=1 AND a.manuscript_status_id IN (4) AND a.id IN (SELECT max(p.id) FROM authorities_history p WHERE p.manuscript_status_id=4 AND YEAR(p.created_at)=:year AND YEAR(p.created_at)=:year AND p.journal_id=:journalId AND p.manuscript_version=0 AND p.manuscript_id=a.manuscript_id) AND YEAR(a.created_at)=:year AND YEAR(b.created_at)=:year AND a.journal_id=b.journal_id AND a.journal_id=:journalId AND b.manuscript_status_id IN (26)", nativeQuery = true)
	@Query("SELECT new com.digi.uniprr.VO.AuthorTakenTimeVO(COUNT(a.manuscriptId) AS manuscriptCount, sum(DATEDIFF(date(b.createdAt),date(a.createdAt))) AS totalsum) FROM AuthoritiesHistory a,AuthoritiesHistory b WHERE a.manuscriptId=b.manuscriptId AND a.manuscriptVersion=0 AND b.manuscriptVersion=1 AND a.manuscriptStatusId IN (4) AND a.id IN (SELECT max(p.id) FROM AuthoritiesHistory p WHERE p.manuscriptStatusId=4 AND YEAR(p.createdAt)=:year AND YEAR(p.createdAt)=:year AND p.journalId=:journalId AND p.manuscriptVersion=0 AND p.manuscriptId=a.manuscriptId) AND YEAR(a.createdAt)=:year AND YEAR(b.createdAt)=:year AND a.journalId=b.journalId AND a.journalId=:journalId AND b.manuscriptStatusId IN (26)")
	AuthorTakenTimeVO getAuthorTakenTime(Integer journalId, Integer year);
	
//	@Query("SELECT  CASE WHEN max(DATEDIFF(mr.assignedDate,ms.submissionDate))  IS null THEN 0 ELSE max(DATEDIFF(mr.assignedDate,ms.submissionDate)) END FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptStatusId=26 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	@Query("SELECT CASE WHEN max(DATEDIFF(date(b.createdAt),date(a.createdAt)))  IS null THEN 0 ELSE max(DATEDIFF(date(b.createdAt),date(a.createdAt))) END FROM AuthoritiesHistory a,AuthoritiesHistory b WHERE a.manuscriptId=b.manuscriptId AND a.manuscriptVersion=0 AND b.manuscriptVersion=1 AND a.manuscriptStatusId IN (4) AND a.id IN (SELECT max(p.id) FROM AuthoritiesHistory p WHERE p.manuscriptStatusId=4 AND YEAR(p.createdAt)=:year AND YEAR(p.createdAt)=:year AND p.journalId=:journalId AND p.manuscriptVersion=0 AND p.manuscriptId=a.manuscriptId) AND YEAR(a.createdAt)=:year AND YEAR(b.createdAt)=:year AND a.journalId=b.journalId AND a.journalId=:journalId AND b.manuscriptStatusId IN (26)")
	float getAuthorTakenTimeMax(Integer journalId, Integer year);
	
//	@Query("SELECT  CASE WHEN min(DATEDIFF(mr.assignedDate,ms.submissionDate))  IS null THEN 0 ELSE min(DATEDIFF(mr.assignedDate,ms.submissionDate)) END FROM ManuScript ms , ManuScriptReviewer mr  WHERE ms.manuScriptId=mr.manuscriptId AND ms.journalId=:journalId AND ms.manuscriptStatusId=26 AND YEAR(mr.assignedDate)=:year AND YEAR(ms.submissionDate)=:year")
	@Query("SELECT CASE WHEN min(DATEDIFF(date(b.createdAt),date(a.createdAt)))  IS null THEN 0 ELSE min(DATEDIFF(date(b.createdAt),date(a.createdAt))) END FROM AuthoritiesHistory a,AuthoritiesHistory b WHERE a.manuscriptId=b.manuscriptId AND a.manuscriptVersion=0 AND b.manuscriptVersion=1 AND a.manuscriptStatusId IN (4) AND a.id IN (SELECT max(p.id) FROM AuthoritiesHistory p WHERE p.manuscriptStatusId=4 AND YEAR(p.createdAt)=:year AND YEAR(p.createdAt)=:year AND p.journalId=:journalId AND p.manuscriptVersion=0 AND p.manuscriptId=a.manuscriptId) AND YEAR(a.createdAt)=:year AND YEAR(b.createdAt)=:year AND a.journalId=b.journalId AND a.journalId=:journalId AND b.manuscriptStatusId IN (26)")
	float getAuthorTakenTimeMin(Integer journalId, Integer year);

	@Query("Select new com.digi.uniprr.VO.ReviewerPublonsCommentVO(u.firstName as reviewerFirstName, u.lastName as reviewerLastName, u.email as reviewerEmail, m.manuscriptCode, m.manuscriptVersion, m.manuscriptTitle, m.manuscriptAbstract, "
			+ " mr.assignedDate, mr.acceptDate, mr.completeDate, sm.statusValue as reviewerRecomendation, mr.commentsToAuthor, u1.firstName as editorFirstName, u1.lastName as editorLastName, mr.sentoPublons as reviewerResponse, mr.id) "
			+ " FROM ManuScriptReviewer mr "
			+ " JOIN StatusMaster sm ON sm.id = mr.manuscriptStatus"
			+ " JOIN User u ON u.id = mr.reviewerId"
			+ " JOIN ManuScript m ON m.manuScriptId = mr.manuscriptId"
			+ " JOIN User u1 ON u1.id = mr.editorId "
			+ " WHERE mr.completeDate IS NOT NULL AND mr.publonCredit = 'Y' and mr.isActive = 1 and mr.sentoPublons = 'N' AND mr.journalId =:journalId ")
	List<ReviewerPublonsCommentVO> findReviewerCommentsForPublons(Integer journalId);
	
	@Query("SELECT r FROM ManuScriptReviewer r WHERE r.journalId=:journalId AND r.reviewerId=:userId")
	List<ManuScriptReviewer> articleCountsPerReviewer(Integer journalId, Integer userId);

	@Modifying
	@Query("UPDATE ManuScriptReviewer mr SET mr.sentoPublons = 'Y' WHERE mr.sentoPublons = 'N' and mr.journalId =:journalId and mr.id IN (:idList)")
	void updateSentToPublonsYes(Integer journalId, List<Integer> idList);
	
	@Query("SELECT m FROM ManuScriptReviewer m WHERE m.manuscriptId =:manuscriptId AND m.manuscriptVersion = :manuscriptVersion AND m.reviewerId = :reviewerId Order By m.Id DESC  ")
	List<ManuScriptReviewer> isArticleReviewedByReviewer(Integer manuscriptId,Integer manuscriptVersion, Integer reviewerId);

	@Query(value = "SELECT m.* FROM manuscript_reviewer m  WHERE m.journal_id =:journalId and m.manuscript_id =:manuscriptId AND m.write_commentary='Yes' and m.id in ( select  max(r.id) as id from manuscript_reviewer r where r.manuscript_id = m.manuscript_id  group by r.reviewer_id ) AND  m.reviewer_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<ManuScriptReviewer> getCommentaryDataForManuscript(Integer journalId,Integer manuscriptId, Pageable pageable);

	@Query(value = "SELECT count(*) FROM manuscript_reviewer m  WHERE m.journal_id =:journalId and m.manuscript_id =:manuscriptId AND m.write_commentary='Yes' and m.id in ( select  max(r.id) as id from manuscript_reviewer r where r.manuscript_id = m.manuscript_id  group by r.reviewer_id ) AND  m.reviewer_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	Integer getCommentaryDataForManuscriptCount(Integer journalId,Integer manuscriptId);

	@Query(value = "SELECT m.* FROM manuscript_reviewer m  INNER JOIN users u ON u.id=m.reviewer_id INNER JOIN countries cu ON cu.id=u.country_id WHERE m.journal_id =:journalId and m.manuscript_id =:manuscriptId AND m.write_commentary='Yes' AND (u.user_email LIKE %:search% OR u.specialization LIKE %:search% OR u.expertise LIKE %:search% OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR cu.name LIKE %:search%) AND  m.reviewer_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<ManuScriptReviewer> getRefineReviewerData(Integer journalId,Integer manuscriptId,String search, Pageable pageable);

	@Query(value = "SELECT count(*) FROM manuscript_reviewer m  INNER JOIN users u ON u.id=m.reviewer_id INNER JOIN countries cu ON cu.id=u.country_id WHERE m.journal_id =:journalId and m.manuscript_id =:manuscriptId AND m.write_commentary='Yes' AND (u.user_email LIKE %:search% OR u.specialization LIKE %:search% OR u.expertise LIKE %:search% OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR cu.name LIKE %:search%) AND  m.reviewer_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	Integer getRefineReviewerDataCount(Integer journalId,Integer manuscriptId,String search);

	@Query(value = "SELECT m.* FROM manuscript_reviewer m  WHERE m.journal_id =:journalId and m.manuscript_id =:manuscriptId and comments_to_editor is not null", nativeQuery = true)
	List<ManuScriptReviewer> getReviewerCommentData(Integer journalId,Integer manuscriptId, Pageable pageable);

	@Query(value="select count(*) from manuscript_reviewer mr where mr.reviewer_id=:userId and mr.manuscript_id=:manuscriptId order by mr.created_on asc",nativeQuery = true)
	Integer getAcceptedCountForManuscriptReviewer(Integer userId, Integer manuscriptId);

	////////////////////////////********reviewer Performance Data****************//////////////////////////
	
	@Query(value = "select count(*) from authorities_history where journal_id IN :journalId AND manuscript_status_id in (8,66) AND year(created_at) IN :year AND month(created_at) =:month", nativeQuery = true)
	Integer numberOfReviewersInvitedByMonth(List<Integer> journalId, List<Integer> year, Integer month);
	@Query(value = "select count(*) from authorities_history where journal_id IN :journalId AND manuscript_status_id in (8,66) AND year(created_at) IN :year", nativeQuery = true)
	Integer numberOfReviewersInvitedByYear(List<Integer> journalId, List<Integer> year);
	
//	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year and month(mr.assigned_date) =:month AND mr.complete_date IS NOT NULL", nativeQuery = true)
//	Integer numberOfReviewersCompletedReviewByMonth(List<Integer> journalId, List<Integer> year, Integer month);
//	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year and mr.complete_date IS NOT NULL", nativeQuery = true)
//	Integer numberOfReviewersCompletedReviewByYear(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_completed_at, b.due_date \r\n"
			+ "        FROM authorities_history a \r\n"
			+ "        LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id \r\n"
			+ "        WHERE a.journal_id IN :journalId \r\n"
			+ "        AND a.manuscript_status_id IN (12, 13, 14, 15) \r\n"
			+ "        AND YEAR(b.created_at) IN :year \r\n"
			+ "        AND MONTH(b.created_at)=:month \r\n"
			+ "        AND b.manuscript_status_id = 8 \r\n"
			+ "        group by review_completed_at", nativeQuery = true)
	List<Object[]> getReviewerPerformanceMonthTotalReviewed(List<Integer> journalId, List<Integer> year, Integer month);
	
	@Query(value = "SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_completed_at, b.due_date \r\n"
			+ "        FROM authorities_history a \r\n"
			+ "        LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id \r\n"
			+ "        WHERE a.journal_id IN :journalId \r\n"
			+ "        AND a.manuscript_status_id IN (12, 13, 14, 15) \r\n"
			+ "        AND YEAR(b.created_at) IN :year \r\n"
			+ "        AND b.manuscript_status_id = 8 \r\n"
			+ "        group by review_completed_at", nativeQuery = true)
	List<Object[]> getReviewerPerformanceYearTotalReviewed(List<Integer> journalId, List<Integer> year);
	
	
//	@Query(value = "SELECT mr.assigned_date, mr.complete_date, mr.due_date FROM \r\n"
//			+ "manuscript_reviewer mr JOIN (SELECT manuscript_id, MAX(complete_date) AS max_created_at \r\n"
//			+ "FROM manuscript_reviewer WHERE journal_id IN :journalId\r\n"
//			+ "AND manuscript_status IN (12,13,14,15) AND complete_date IS NOT NULL GROUP BY manuscript_id) \r\n"
//			+ "AS grouped_max ON mr.manuscript_id = grouped_max.manuscript_id \r\n"
//			+ "AND mr.complete_date = grouped_max.max_created_at WHERE mr.journal_id IN :journalId \r\n"
//			+ " AND mr.manuscript_status IN (12,13,14,15) \r\n"
//			+ "AND YEAR(mr.assigned_date) IN :year and month(mr.assigned_date) = :month", nativeQuery = true)
//	List<Object[]> getReviewerPerformanceMonthTotalReviewed(List<Integer> journalId, List<Integer> year, Integer month);
//	
//	@Query(value = "SELECT mr.assigned_date, mr.complete_date, mr.due_date FROM \r\n"
//			+ "manuscript_reviewer mr JOIN (SELECT manuscript_id, MAX(complete_date) AS max_created_at \r\n"
//			+ "FROM manuscript_reviewer WHERE journal_id IN :journalId\r\n"
//			+ "AND manuscript_status IN (12,13,14,15) AND complete_date IS NOT NULL GROUP BY manuscript_id) \r\n"
//			+ "AS grouped_max ON mr.manuscript_id = grouped_max.manuscript_id \r\n"
//			+ "AND mr.complete_date = grouped_max.max_created_at WHERE mr.journal_id IN :journalId \r\n"
//			+ " AND mr.manuscript_status IN (12,13,14,15) \r\n"
//			+ "AND YEAR(mr.assigned_date) IN :year", nativeQuery = true)
//	List<Object[]> getReviewerPerformanceYearTotalReviewed(List<Integer> journalId, List<Integer> year);
	
	
//	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year and month(mr.assigned_date) =:month AND mr.complete_date IS NULL AND manuscript_status in (8,66,35) AND is_active=1",nativeQuery = true)
//	Integer numberOfReviewsNeverReturnedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
//	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year AND mr.complete_date IS NULL AND manuscript_status in (8,66,35) AND is_active=1",nativeQuery = true)
//	Integer numberOfReviewsNeverReturnedByYear(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT COUNT(*) AS reviews_never_returned FROM manuscript_reviewer m WHERE m.journal_id IN :journalId AND year(m.assigned_date) IN :year AND MONTH(m.assigned_date)=:month AND m.is_active = 1 AND m.manuscript_status IN (8, 66, 35) AND m.manuscript_id NOT IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30, 65, 59, 33, 57, 61, 62, 16)) AND ((m.manuscript_status = 35 AND ((SELECT COUNT(1) FROM manuscript_reviewer t WHERE t.manuscript_id = m.manuscript_id AND t.reviewer_id = m.reviewer_id AND t.manuscript_status IN (35, 12, 13, 14, 15)) <= 1 AND m.complete_date IS NULL)) OR m.manuscript_status IN (8, 66, 35))",nativeQuery = true)
	Integer numberOfReviewsNeverReturnedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
	@Query(value = "SELECT COUNT(*) AS reviews_never_returned FROM manuscript_reviewer m WHERE m.journal_id IN :journalId AND year(m.assigned_date) IN :year AND m.is_active = 1 AND m.manuscript_status IN (8, 66, 35) AND m.manuscript_id NOT IN (SELECT ms.manuscript_id FROM manuscript ms WHERE ms.manuscript_status_id IN (30, 65, 59, 33, 57, 61, 62, 16)) AND ((m.manuscript_status = 35 AND ((SELECT COUNT(1) FROM manuscript_reviewer t WHERE t.manuscript_id = m.manuscript_id AND t.reviewer_id = m.reviewer_id AND t.manuscript_status IN (35, 12, 13, 14, 15)) <= 1 AND m.complete_date IS NULL)) OR m.manuscript_status IN (8, 66, 35))",nativeQuery = true)
	Integer numberOfReviewsNeverReturnedByYear(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year and month(mr.assigned_date) =:month AND manuscript_status in (36,60)",nativeQuery = true)
	Integer numberOfReviewsDeclinedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year AND mr.complete_date IS NULL AND manuscript_status in (36,60)",nativeQuery = true)
	Integer numberOfReviewsDeclinedByYear(List<Integer> journalId, List<Integer> year);
	
//	@Query(value = "SELECT COUNT(*) AS row_count FROM (SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_completed_at, b.due_date, b.manuscript_id, a.previous_user_id FROM authorities_history a LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id and a.previous_user_id=b.current_user_id WHERE a.journal_id IN :journalId AND a.manuscript_status_id IN (36, 60) AND YEAR(b.created_at) IN :year AND MONTH(b.created_at) = :month AND b.manuscript_status_id IN (8, 66) GROUP BY review_completed_at) AS temp",nativeQuery = true)
//	Integer numberOfReviewsDeclinedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
//	@Query(value = "SELECT COUNT(*) AS row_count FROM (SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_completed_at, b.due_date, b.manuscript_id, a.previous_user_id FROM authorities_history a LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id and a.previous_user_id=b.current_user_id WHERE a.journal_id IN :journalId AND a.manuscript_status_id IN (36, 60) AND YEAR(b.created_at) IN :year AND b.manuscript_status_id IN (8, 66) GROUP BY review_completed_at) AS temp",nativeQuery = true)
//	Integer numberOfReviewsDeclinedByYear(List<Integer> journalId, List<Integer> year);
	
//	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr join manuscript m on m.manuscript_id=mr.manuscript_id WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year and month(mr.assigned_date) =:month and mr.is_active=0 and m.manuscript_status_id in (8,66,35,36,60,12,13,14,15)",nativeQuery = true)
//	Integer numberOfReviewsRemovedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
//	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr join manuscript m on m.manuscript_id=mr.manuscript_id WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year and mr.is_active=0 and m.manuscript_status_id in (8,66,35,36,60,12,13,14,15);",nativeQuery = true)
//	Integer numberOfReviewsRemovedByYear(List<Integer> journalId, List<Integer> year);
	
//	@Query(value = "SELECT count(*) FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND MONTH(b.created_at) =:month AND b.manuscript_status_id IN (67)",nativeQuery = true)
//	Integer numberOfReviewsRemovedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
//	@Query(value = "SELECT count(*) FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND b.manuscript_status_id IN (67)",nativeQuery = true)
//	Integer numberOfReviewsRemovedByYear(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT COUNT(*) AS row_count FROM (SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_completed_at, b.due_date, b.manuscript_id, a.previous_user_id FROM authorities_history a LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id and a.previous_user_id=b.current_user_id WHERE a.journal_id IN :journalId AND a.manuscript_status_id IN (67) AND YEAR(b.created_at) IN :year AND MONTH(b.created_at) = :month AND b.manuscript_status_id IN (8, 66) GROUP BY review_completed_at) AS temp",nativeQuery = true)
	Integer numberOfReviewsRemovedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
	@Query(value = "SELECT COUNT(*) AS row_count FROM (SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_completed_at, b.due_date, b.manuscript_id, a.previous_user_id FROM authorities_history a LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id and a.previous_user_id=b.current_user_id WHERE a.journal_id IN :journalId AND a.manuscript_status_id IN (67) AND YEAR(b.created_at) IN :year AND b.manuscript_status_id IN (8, 66) GROUP BY review_completed_at) AS temp",nativeQuery = true)
	Integer numberOfReviewsRemovedByYear(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_decline, c.created_at AS review_remove, b.due_date, b.manuscript_id, a.previous_user_id FROM authorities_history a LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id AND a.previous_user_id = b.current_user_id LEFT JOIN authorities_history c ON b.manuscript_id = c.manuscript_id AND c.previous_user_id = b.current_user_id WHERE a.journal_id IN :journalId AND a.manuscript_status_id IN (36,60) AND c.manuscript_status_id IN (67) AND YEAR(b.created_at) IN :year AND MONTH(b.created_at) = :month AND b.manuscript_status_id IN (8, 66)",nativeQuery = true)
	List<Object[]> getDeclinedAndRemovedData(List<Integer> journalId, List<Integer> year, Integer month);
	@Query(value = "SELECT DISTINCT b.created_at AS review_invited_at, a.created_at AS review_decline, c.created_at AS review_remove, b.due_date, b.manuscript_id, a.previous_user_id FROM authorities_history a LEFT JOIN authorities_history b ON a.manuscript_id = b.manuscript_id AND a.previous_user_id = b.current_user_id LEFT JOIN authorities_history c ON b.manuscript_id = c.manuscript_id AND c.previous_user_id = b.current_user_id WHERE a.journal_id IN :journalId AND a.manuscript_status_id IN (36,60) AND c.manuscript_status_id IN (67) AND YEAR(b.created_at) IN :year AND b.manuscript_status_id IN (8, 66)",nativeQuery = true)
	List<Object[]> getDeclinedAndRemovedData(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT count(*) FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND MONTH(b.created_at) = :month AND b.manuscript_status_id = 8 AND b.manuscript_id IN (SELECT DISTINCT d.manuscript_id FROM manuscript_reviewer d WHERE d.journal_id IN :journalId AND d.is_active=0 AND YEAR(d.assigned_date) IN :year AND MONTH(d.assigned_date) = :month) AND b.current_user_id NOT IN (SELECT a.previous_user_id FROM authorities_history a WHERE a.journal_id IN :journalId AND YEAR(a.created_at) IN :year AND MONTH(a.created_at) = :month AND a.manuscript_status_id IN (12, 13, 14, 15, 11, 35, 60, 67, 36) AND a.previous_user_role = 5 AND a.manuscript_id = b.manuscript_id) AND b.current_user_role = 5",nativeQuery = true)
	Integer numberOfReviewsTerminatedByMonth(List<Integer> journalId, List<Integer> year, Integer month);	
	@Query(value = "SELECT count(*) FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND b.manuscript_status_id = 8 AND b.manuscript_id IN (SELECT DISTINCT d.manuscript_id FROM manuscript_reviewer d WHERE d.journal_id IN :journalId AND d.is_active=0 AND YEAR(d.assigned_date) IN :year) AND b.current_user_id NOT IN (SELECT a.previous_user_id FROM authorities_history a WHERE a.journal_id IN :journalId AND YEAR(a.created_at) IN :year AND a.manuscript_status_id IN (12, 13, 14, 15, 11, 35, 60, 67, 36) AND a.previous_user_role = 5 AND a.manuscript_id = b.manuscript_id) AND b.current_user_role = 5",nativeQuery = true)
	Integer numberOfReviewsTerminatedByYear(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) IN :year and month(mr.assigned_date) =:month AND mr.complete_date IS NOT NULL AND DATE(mr.complete_date) <= DATE(mr.due_date)", nativeQuery = true)
	Integer numberOfReviewsOnTimeByMonth(List<Integer> journalId, List<Integer> year, Integer month);
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id in :journalId AND year(mr.assigned_date) in :year AND mr.complete_date IS NOT NULL AND DATE(mr.complete_date) <= DATE(mr.due_date)", nativeQuery = true)
	Integer numberOfReviewsOnTimeByYear(List<Integer> journalId, List<Integer> year);
	
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND year(mr.assigned_date) in :year and month(mr.assigned_date) = :month AND mr.complete_date IS NOT NULL AND DATE(mr.complete_date) > DATE(mr.due_date)", nativeQuery = true)
	Integer numberOfReviewsLateByMonth(List<Integer> journalId, List<Integer> year, Integer month);
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year AND mr.complete_date IS NOT NULL AND DATE(mr.complete_date) > DATE(mr.due_date)", nativeQuery = true)
	Integer numberOfReviewsLateByYear(List<Integer> journalId, List<Integer> year);

	
	@Query(value = "SELECT COUNT(*) FROM mail_queue m WHERE  m.mail_to_role = 5 AND m.manuscript_status IN (8,66,35) AND m.module_name in ('null','AutoReminderReviewer') AND YEAR(m.mail_send_date) IN :year AND MONTH(m.mail_send_date) = :month AND m.manuscript_id IN (SELECT DISTINCT b.manuscript_id FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND MONTH(b.created_at) = :month AND b.manuscript_status_id IN (8,66))", nativeQuery = true)
	Integer numberOfRemindersSentByMonth(List<Integer> journalId, List<Integer> year, Integer month);
	@Query(value = "SELECT COUNT(*) FROM mail_queue m WHERE  m.mail_to_role = 5 AND m.manuscript_status IN (8,66,35) AND m.module_name in ('null','AutoReminderReviewer') AND YEAR(m.mail_send_date) IN :year AND m.manuscript_id IN (SELECT DISTINCT b.manuscript_id FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND b.manuscript_status_id IN (8,66))", nativeQuery = true)
	Integer numberOfRemindersSentByYear(List<Integer> journalId, List<Integer> year);
	
	
	
	
	@Query(value = "SELECT mr.reviewer_id FROM manuscript_reviewer mr JOIN users r ON mr.reviewer_id = r.id left join authorities_history a on a.manuscript_id=mr.manuscript_id WHERE mr.journal_id IN :journalId AND YEAR(a.created_at) IN :year and month(a.created_at) =:month GROUP BY r.id", nativeQuery =true)
	List<Integer> findReviewerIdsByJournalAndYearAndMonth(List<Integer> journalId, List<Integer> year, Integer month);
	@Query(value = "SELECT mr.reviewer_id FROM manuscript_reviewer mr JOIN users r ON mr.reviewer_id = r.id left join authorities_history a on a.manuscript_id=mr.manuscript_id WHERE mr.journal_id IN :journalId AND YEAR(a.created_at) IN :year GROUP BY r.id", nativeQuery = true)
	List<Integer> findReviewerIdsByJournalAndYear(List<Integer> journalId, List<Integer> year);

	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id in :journalId and mr.reviewer_id =:reviewerId AND YEAR(mr.assigned_date) in :year and month(mr.assigned_date) =:month and mr.complete_date IS NOT NULL",nativeQuery = true)
	Integer findTotalArticlesInvitedByMonth(List<Integer> journalId, Long reviewerId, List<Integer> year,Integer month);
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id in :journalId and mr.reviewer_id =:reviewerId AND YEAR(mr.assigned_date) in :year and mr.complete_date IS NOT NULL",nativeQuery = true)
	Integer findTotalArticlesInvitedByYear(List<Integer> journalId, Long reviewerId, List<Integer> year);

	@Query(value = "SELECT AVG(DATEDIFF(mr.complete_date, mr.start_date)) AS avg_days_to_complete_review FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year and month(mr.assigned_date) = :month and mr.reviewer_id =:reviewerId AND mr.complete_date IS NOT NULL",nativeQuery = true)
	Double findAvgDaysToCompleteReviewByMonth(List<Integer> journalId, Long reviewerId, List<Integer> year,Integer month);
	@Query(value = "SELECT AVG(DATEDIFF(mr.complete_date, mr.start_date)) AS avg_days_to_complete_review FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year and mr.reviewer_id =:reviewerId AND mr.complete_date IS NOT NULL",nativeQuery = true)
	Double findAvgDaysToCompleteReviewByYear(List<Integer> journalId, Long reviewerId, List<Integer> year);

	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year and month(mr.assigned_date) =:month AND mr.reviewer_id =:reviewerId AND mr.complete_date <= mr.due_date AND mr.manuscript_status IN (12, 13, 14, 15)", nativeQuery = true)
	Integer numberOfReviewsOnTimeByMonth(List<Integer> journalId, Long reviewerId, List<Integer> year,Integer month);
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year AND mr.reviewer_id =:reviewerId AND mr.complete_date <= mr.due_date AND mr.manuscript_status IN (12, 13, 14, 15)", nativeQuery = true)
	Integer numberOfReviewsOnTimeByYear(List<Integer> journalId, Long reviewerId, List<Integer> year);

	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year and month(mr.assigned_date) =:month AND mr.reviewer_id =:reviewerId AND mr.complete_date > mr.due_date AND mr.manuscript_status IN (12, 13, 14, 15)", nativeQuery = true)
	Integer numberOfReviewsLateByMonth(List<Integer> journalId, Long reviewerId, List<Integer> year,Integer month);
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year AND mr.reviewer_id = :reviewerId AND mr.complete_date IS NOT NULL AND mr.complete_date > mr.due_date AND mr.manuscript_status IN (12, 13, 14, 15)", nativeQuery = true)
	Integer numberOfReviewsLateByYear(List<Integer> journalId, Long reviewerId, List<Integer> year);

	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year and month(mr.assigned_date) =:month AND mr.reviewer_id =:reviewerId AND mr.complete_date IS NULL",nativeQuery = true)
	Integer numberOfReviewsNeverReturnedByMonth(List<Integer> journalId, Long reviewerId, List<Integer> year,Integer month);
	@Query(value = "SELECT count(*) FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND YEAR(mr.assigned_date) IN :year AND mr.reviewer_id =:reviewerId AND mr.complete_date IS NULL",nativeQuery = true)
	Integer numberOfReviewsNeverReturnedByYear(List<Integer> journalId, Long reviewerId, List<Integer> year);

	@Query(value = "SELECT (COUNT(*) / COUNT(DISTINCT m.manuscript_id)) AS average_reminders_per_manuscript FROM reminder_tracker rt JOIN manuscript m ON m.manuscript_id = rt.manuscript_id WHERE YEAR(m.submission_date) in :year and month(m.submission_date) =:month AND rt.journal_id IN :journalId AND rt.assigned_user =:reviewerId",nativeQuery = true)
	Double numberOfRemindersSentByMonth(List<Integer> journalId, Integer reviewerId, List<Integer> year,Integer month);
	@Query(value = "SELECT (COUNT(*) / COUNT(DISTINCT m.manuscript_id)) AS average_reminders_per_manuscript FROM reminder_tracker rt JOIN manuscript m ON m.manuscript_id = rt.manuscript_id WHERE YEAR(m.submission_date) in :year AND rt.journal_id IN :journalId AND rt.assigned_user =:reviewerId",nativeQuery = true)
	Double numberOfRemindersSentByYear(List<Integer> journalId, Integer reviewerId, List<Integer> year);

	@Query(value = "select avg(quality) from reviewer_rating rr join manuscript_reviewer mr on mr.reviewer_id=rr.reviewer_id where mr.journal_id IN :journalId and YEAR(mr.assigned_date) IN :year and month(mr.assigned_date) =:month and mr.reviewer_id =:reviewerId",nativeQuery = true)
	Double findAvgRatingQualityByMonth(List<Integer> journalId, Long reviewerId, List<Integer> year,Integer month);
	@Query(value = "select avg(quality) from reviewer_rating rr join manuscript_reviewer mr on mr.reviewer_id=rr.reviewer_id where mr.journal_id IN :journalId and YEAR(mr.assigned_date) IN :year and mr.reviewer_id =:reviewerId",nativeQuery = true)
	Double findAvgRatingQualityByYear(List<Integer> journalId, Long reviewerId, List<Integer> year);

	@Query(value = "select avg(timeliness) from reviewer_rating rr join manuscript_reviewer mr on mr.reviewer_id=rr.reviewer_id where mr.journal_id IN :journalId and YEAR(mr.assigned_date) IN :year and month(mr.assigned_date) =:month and mr.reviewer_id =:reviewerId",nativeQuery = true)
	Double findAvgRatingTimelinessByMonth(List<Integer> journalId, Long reviewerId, List<Integer> year,Integer month);
	@Query(value = "select avg(timeliness) from reviewer_rating rr join manuscript_reviewer mr on mr.reviewer_id=rr.reviewer_id where mr.journal_id IN :journalId and YEAR(mr.assigned_date) IN :year and mr.reviewer_id=:reviewerId",nativeQuery = true)
	Double findAvgRatingTimelinessByYear(List<Integer> journalId, Long reviewerId, List<Integer> year);
	
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status , m.is_active , m.due_date , m.assigned_date, m.complete_date, concat(u.first_name,' ',u.last_name) as reviewer_name FROM manuscript_reviewer m INNER JOIN users u ON u.id=m.reviewer_id WHERE m.journal_id IN :journalId AND YEAR(m.complete_date) IN :year AND m.manuscript_status IN (12, 13, 14, 15)", nativeQuery = true)
	List<Tuple> getTotalReviewerByYearReviewerResponded(List<Integer> journalId, List<Integer> year);
	
	@Query(value="SELECT m.manuscript_id ,m.manuscript_version, m.reviewer_id , m.manuscript_status , m.is_active , m.due_date , m.assigned_date, m.complete_date, concat(u.first_name,' ',u.last_name) as reviewer_name FROM manuscript_reviewer m INNER JOIN users u ON u.id=m.reviewer_id WHERE m.journal_id IN :journalId AND YEAR(m.complete_date) IN :year AND MONTH(m.complete_date)=:month AND m.manuscript_status IN (12, 13, 14, 15)", nativeQuery = true)
	List<Tuple> getTotalReviewerByMonthReviewerResponded(List<Integer> journalId, List<Integer> year, Integer month);

	@Query(value = "WITH reviews_never_returned AS (\n" +
		"    SELECT m.reviewer_id,\n" +
		"        COUNT(DISTINCT m.manuscript_id) AS reviews_never_returned\n" +
		"    FROM manuscript_reviewer m\n" +
		"    WHERE m.journal_id IN (:journalId)\n" +
		"        AND YEAR(m.assigned_date) IN (:year)\n" +
		"        AND (:month IS NULL OR :month = 0 OR MONTH(m.assigned_date) = :month)\n" +
		"        AND m.is_active = 1\n" +
		"        AND m.manuscript_status IN (8, 66, 35)\n" +
		"\n" +
		"        -- Replace NOT IN with NOT EXISTS (safer & faster)\n" +
		"        AND NOT EXISTS (\n" +
		"            SELECT 1\n" +
		"            FROM manuscript ms\n" +
		"            WHERE ms.manuscript_id = m.manuscript_id\n" +
		"              AND ms.manuscript_status_id IN (30, 65, 59, 33, 57, 61, 62, 16)\n" +
		"        )\n" +
		"\n" +
		"        -- Special condition for status 35\n" +
		"        AND (\n" +
		"            m.manuscript_status IN (8, 66)\n" +
		"            OR (\n" +
		"                m.manuscript_status = 35\n" +
		"                AND m.complete_date IS NULL\n" +
		"                AND (\n" +
		"                    SELECT COUNT(1)\n" +
		"                    FROM manuscript_reviewer t\n" +
		"                    WHERE t.manuscript_id = m.manuscript_id\n" +
		"                      AND t.reviewer_id = m.reviewer_id\n" +
		"                      AND t.manuscript_status IN (35, 12, 13, 14, 15)\n" +
		"                ) <= 1\n" +
		"            )\n" +
		"        )\n" +
		"    GROUP BY m.reviewer_id\n" +
		")\n" +
		"\n" +
		"SELECT \n" +
		"    mr.reviewer_id,\n" +
		"    CONCAT(u.first_name, ' ', u.last_name) AS reviewer_name,\n" +
		"    COALESCE(rnr.reviews_never_returned, 0) AS reviews_never_returned,\n" +
		"    AVG(rr.quality) AS avg_rating_quality,\n" +
		"    AVG(rr.timeliness) AS avg_rating_timeliness\n" +
		"\n" +
		"FROM manuscript_reviewer mr\n" +
		"\n" +
		"JOIN users u \n" +
		"    ON mr.reviewer_id = u.id\n" +
		"\n" +
		"LEFT JOIN reviewer_rating rr \n" +
		"    ON rr.reviewer_id = mr.reviewer_id\n" +
		"\n" +
		"LEFT JOIN reviews_never_returned rnr \n" +
		"    ON rnr.reviewer_id = mr.reviewer_id\n" +
		"\n" +
		"WHERE mr.journal_id IN (:journalId)\n" +
		"    AND mr.reviewer_id IN (:reviewerIds)\n" +
		"    AND YEAR(mr.updated_on) IN (:year)\n" +
		"    AND (:month IS NULL OR :month = 0 OR MONTH(mr.updated_on) = :month)\n" +
		"\n" +
		"GROUP BY \n" +
		"    mr.reviewer_id,\n" +
		"    reviewer_name,\n" +
		"    rnr.reviews_never_returned;",
		nativeQuery = true)
List<Object[]> fetchReviewerPerformanceDataWithReminders(List<Integer> journalId, List<Integer> reviewerIds, List<Integer> year, Integer month);

//@Query(value = "SELECT mr.assigned_date, mr.complete_date, mr.due_date FROM manuscript_reviewer mr JOIN (SELECT manuscript_id, MAX(complete_date) AS max_created_at FROM manuscript_reviewer WHERE journal_id IN :journalId AND reviewer_id = :reviewerId AND manuscript_status IN (12,13,14,15) AND complete_date IS NOT NULL GROUP BY manuscript_id) AS grouped_max ON mr.manuscript_id = grouped_max.manuscript_id AND mr.complete_date = grouped_max.max_created_at WHERE mr.journal_id IN :journalId AND mr.reviewer_id = :reviewerId AND mr.manuscript_status IN (12,13,14,15) AND YEAR(mr.complete_date) IN :year AND (:month IS NULL OR :month = 0 OR MONTH(mr.complete_date) = :month)", 
//        nativeQuery = true)
//List<Object[]> getReviewerPerformanceReviewed(List<Integer> journalId, List<Integer> year, Integer month, Integer reviewerId);

@Query(value = "SELECT mr.assigned_date, mr.complete_date, mr.due_date FROM manuscript_reviewer mr WHERE mr.journal_id IN :journalId AND mr.manuscript_status IN (12,13,14,15) AND YEAR(mr.complete_date) IN :year AND (:month IS NULL OR :month = 0 OR MONTH(mr.complete_date) = :month) and mr.reviewer_id = :reviewerId", 
nativeQuery = true)
List<Object[]> getReviewerPerformanceReviewed(List<Integer> journalId, List<Integer> year, Integer month, Integer reviewerId);
	@Query(value = "SELECT COUNT(*) FROM mail_queue m WHERE  m.mail_to_role = 5 AND mail_to_userid = :reviewerId AND m.module_name in ('null','AutoReminderReviewer') AND Year(m.mail_send_date) IN :year AND (:month IS NULL OR :month = 0 OR MONTH(m.mail_send_date) = :month) AND m.manuscript_id IN (SELECT DISTINCT b.manuscript_id FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND  (:month IS NULL OR :month = 0 OR MONTH(b.created_at) = :month) AND b.manuscript_status_id IN (8,66) AND b.current_user_id = :reviewerId)", 
	nativeQuery = true)
	int getTotalReminderCount(List<Integer> journalId, List<Integer> year, Integer month,Integer reviewerId);
	
	// Query to get distinct manuscripts
	@Query(value = "SELECT count(DISTINCT m.manuscript_id) FROM mail_queue m WHERE  m.mail_to_role = 5 AND mail_to_userid = :reviewerId And m.module_name in ('null','AutoReminderReviewer') AND Year(m.mail_send_date) IN :year AND (:month IS NULL OR :month = 0 OR MONTH(m.mail_send_date) = :month) AND m.manuscript_id IN (SELECT DISTINCT b.manuscript_id FROM authorities_history b WHERE b.journal_id IN :journalId AND YEAR(b.created_at) IN :year AND  (:month IS NULL OR :month = 0 OR MONTH(b.created_at) = :month) AND b.manuscript_status_id IN (8,66) AND b.current_user_id = :reviewerId)", 
	nativeQuery = true)
	int getDistinctManuscriptCount(List<Integer> journalId, List<Integer> year, Integer month,Integer reviewerId);

	
//@Query(value = "WITH reminder_cte AS ( " +
//        "SELECT rt.assigned_user, COUNT(*) / COUNT(DISTINCT m.manuscript_id) AS avg_reminders " +
//        "FROM reminder_tracker rt " +
//        "JOIN manuscript m ON m.manuscript_id = rt.manuscript_id " +
//        "WHERE YEAR(m.submission_date) IN :year " +
//        "AND (:month IS NULL OR :month = 0 OR MONTH(m.submission_date) = :month) " +
//        "AND rt.journal_id IN :journalId " +
//        "GROUP BY rt.assigned_user " +
//        ") " +
//        "SELECT CONCAT(r.first_name, ' ', r.last_name) AS reviewer_name, " +
//        "SUM(CASE WHEN mr.complete_date IS NOT NULL THEN 1 ELSE 0 END) AS total_articles_reviewed, " +
//        "AVG(DATEDIFF(mr.complete_date, mr.start_date)) AS avg_days_to_complete_review, " +
//        "SUM(CASE WHEN DATE(mr.complete_date) <= DATE(mr.due_date) THEN 1 ELSE 0 END) AS reviews_on_time, " +
//        "SUM(CASE WHEN DATE(mr.complete_date) > DATE(mr.due_date) THEN 1 ELSE 0 END) AS reviews_late, " +
//        "SUM(CASE WHEN mr.complete_date IS NULL THEN 1 ELSE 0 END) AS reviews_never_returned, " +
//        "AVG(rr.quality) AS avg_rating_quality, " +
//        "AVG(rr.timeliness) AS avg_rating_timeliness, " +
//        "rc.avg_reminders AS avg_reminders " +
//        "FROM manuscript_reviewer mr " +
//        "LEFT JOIN reviewer_rating rr ON rr.reviewer_id = mr.reviewer_id " +
//        "JOIN users r ON mr.reviewer_id = r.id " +
//        "LEFT JOIN reminder_cte rc ON rc.assigned_user = mr.reviewer_id " +
//        "WHERE mr.journal_id IN :journalId " +
//        "AND mr.reviewer_id IN :reviewerIds " +
//        "AND YEAR(mr.assigned_date) IN :year " +
//        "AND (:month IS NULL OR :month = 0 OR MONTH(mr.assigned_date) = :month) " +
//        "GROUP BY reviewer_name",
//    nativeQuery = true)
//List<Object[]> fetchReviewerPerformanceDataWithReminders(List<Integer> journalId, List<Integer> reviewerIds, List<Integer> year, Integer month);
	@Query(value = "SELECT mr.reviewer_id, " +
	        "COUNT(mr.id) AS total_articles_reviewed, " +
	        "AVG(DATEDIFF(mr.complete_date, mr.start_date)) AS avg_days_to_complete_review, " +
	        "SUM(CASE WHEN mr.complete_date <= mr.due_date THEN 1 ELSE 0 END) AS reviews_on_time, " +
	        "SUM(CASE WHEN mr.complete_date > mr.due_date THEN 1 ELSE 0 END) AS reviews_late, " +
	        "SUM(CASE WHEN mr.complete_date IS NULL THEN 1 ELSE 0 END) AS reviews_never_returned, " +
	        "AVG(rr.quality) AS avg_rating_quality, " +
	        "AVG(rr.timeliness) AS avg_rating_timeliness, " +
	        "(SELECT COUNT(*) / COUNT(DISTINCT m.manuscript_id) " +
	        "FROM reminder_tracker rt " +
	        "JOIN manuscript m ON m.manuscript_id = rt.manuscript_id " +
	        "WHERE YEAR(m.submission_date) IN :year " +
	        "AND (:month IS NULL OR :month = 0 OR MONTH(m.submission_date) = :month) " +
	        "AND rt.journal_id IN :journalId " +
	        "AND rt.assigned_user = mr.reviewer_id) AS avg_reminders " +
	        "FROM manuscript_reviewer mr " +
	        "INNER JOIN reviewer_rating rr ON rr.reviewer_id = mr.reviewer_id " +
	        "WHERE mr.journal_id IN :journalId " +
	        "AND mr.reviewer_id IN :reviewerIds " +
	        "AND YEAR(mr.assigned_date) IN :year " +
	        "AND (:month IS NULL OR :month = 0 OR MONTH(mr.assigned_date) = :month) " +
	        "GROUP BY mr.reviewer_id", nativeQuery = true)
	Stream<Object[]> fetchReviewerPerformanceDataStream(List<Integer> journalId, List<Integer> reviewerIds, List<Integer> year, Integer month);

    @Modifying
    @Query(value = "UPDATE manuscript_reviewer SET reviewer_id = :newUserId WHERE reviewer_id = :oldUserId", nativeQuery = true)
    void updateReviewerUser(@Param("oldUserId") Integer oldUserId, @Param("newUserId") Integer newUserId);
    
    @Query("SELECT COUNT(r) FROM ManuScriptReviewer r WHERE r.manuscriptId = :manuscriptId AND r.reviewerId = :userId")
    int existsByManuscriptAndUser(@Param("manuscriptId") Integer manuscriptId, @Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE ManuScriptReviewer r SET r.reviewerId = :newUserId " +
           "WHERE r.manuscriptId = :manuscriptId AND r.reviewerId = :oldUserId")
    void updateSingleReviewerUser(@Param("oldUserId") Integer oldUserId,
                                  @Param("newUserId") Integer newUserId,
                                  @Param("manuscriptId") Integer manuscriptId);
    
    @Modifying
    @Transactional
    @Query(
        value =
            "UPDATE manuscript_reviewer r\r\n"
            + "SET r.reviewer_id = :newUserId\r\n"
            + "WHERE r.reviewer_id = :oldUserId\r\n"
            + "AND r.manuscript_id IN (:manuscriptIds)\r\n"
            + "AND NOT EXISTS (\r\n"
            + "      SELECT 1 FROM (\r\n"
            + "          SELECT * FROM manuscript_reviewer\r\n"
            + "      ) x\r\n"
            + "      WHERE x.manuscript_id = r.manuscript_id\r\n"
            + "      AND x.reviewer_id = :newUserId\r\n"
            + ")",
        nativeQuery = true
    )
    int bulkUpdateReviewer(
            @Param("oldUserId") Integer oldUserId,
            @Param("newUserId") Integer newUserId,
            @Param("manuscriptIds") List<Integer> manuscriptIds
    );

	@Modifying(clearAutomatically = true)
	@Query(value="UPDATE manuscript_reviewer SET manuscript_status =:rejectStatusId WHERE reviewer_id = :currentUserId and journal_id:journalId and manuscript_id IN (:manuscriptIds)", nativeQuery = true)
	void updateManuscriptReviewerRejectStatus(Integer journalId, Integer currentUserId, List<Integer> manuscriptIds, Integer rejectStatusId);


}