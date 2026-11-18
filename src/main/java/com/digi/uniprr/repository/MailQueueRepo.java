package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.MailQueue;

@Repository
public interface MailQueueRepo extends JpaRepository<MailQueue, Integer> {

	@Query("SELECT m from MailQueue m where m.mailTOUserId=:id AND m.mailToRole=:roleId AND m.journalId=:journalId AND m.isActive=:isActive ORDER BY m.id DESC")
	List<MailQueue> getMailQueueUserId(Integer id,Integer roleId,Integer journalId,Integer isActive);

	@Modifying
	@Query("Update MailQueue m set m.isSeen=1 where m.id=:id ")
	void getMailIsSeen(Integer id);
	
	@Query(value="SELECT * FROM mail_queue m WHERE m.is_active=0 ORDER BY m.id DESC LIMIT 100", nativeQuery =  true)
	List<MailQueue> getAllEmailToSent();

	@Query("SELECT m FROM MailQueue m WHERE m.mailTo=:name AND m.isSeen=0")
	List<MailQueue> getMailListMailTo(String name);
	
	@Query("SELECT m FROM MailQueue m WHERE m.mailFrom=:name AND m.isSeen=0")
	List<MailQueue> getMailListMailFrom(String name);
	
	@Query("SELECT m FROM MailQueue m WHERE m.id=:id")
	MailQueue getMailData(Integer id);

	@Query("SELECT m FROM MailQueue m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.isActive=1")
	List<MailQueue> getMailByManuscript(Integer manuscriptId, Integer manuscriptVersion);
	
	@Query("SELECT m FROM MailQueue m WHERE m.manuscriptId=:manuscriptId AND (m.isActive=1 OR m.isActive=2) AND m.mailSubject not like '%Bundling process failed for manuscript%' ORDER BY m.id DESC")
	List<MailQueue> getMailByManuscriptForAll(Integer manuscriptId);
	
	@Query("SELECT m FROM MailQueue m WHERE m.mailTOUserId=:mailTOUserId AND m.journalId=:journalId AND m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion And m.manuscriptStatus=:manuscriptStatus")
	List<MailQueue> resendReviewerInvitationMail(Integer mailTOUserId,Integer journalId,Integer manuscriptId,Integer manuscriptVersion,Integer manuscriptStatus);
	
	@Query("SELECT m from MailQueue m where m.mailTOUserId=:id AND m.mailToRole=:roleId AND m.journalId=:journalId AND m.isActive=:isActive AND m.isSeen=0 ORDER BY m.id DESC")
	List<MailQueue> getMailQueueUserIdAndIsNotSeen(Integer id,Integer roleId,Integer journalId,Integer isActive);
	
	@Query("SELECT m FROM MailQueue m WHERE m.mail_send_date < :getForDate ")
	List<MailQueue> getMailListForArchive(Date getForDate);
	
	@Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM MailQueue m WHERE m.manuscriptId = :manuscriptId AND m.mailTOUserId = :mailTOUserId AND m.taskId = :taskId")
	boolean existsByManuscriptIdAndMailToUserIdAndTaskId(Integer manuscriptId,Integer mailTOUserId,Integer taskId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mail_queue mw "
    		+ "SET mw.mail_to_userid = :newUserId, mw.mail_to = :newEmail "
    		+ "WHERE mw.mail_to_role = 4 "
    		+ "AND mw.mail_to = :oldEmail "
    		+ "AND (:journalId IS NULL OR mw.journal_id = :journalId)", nativeQuery = true)
    void updateMailTo(String oldEmail, String newEmail, Integer newUserId, Integer journalId);
    
    @Query("SELECT m FROM MailQueue m " +
            "WHERE m.manuscriptId = :manuscriptId AND m.taskId = :taskId AND m.journalId = :journalId " +
            "AND m.mail_send_date BETWEEN :startDate AND :endDate")
     List<MailQueue> findMailsSentInDateRange(Integer manuscriptId, Integer taskId, Integer journalId,
                                              Date startDate, Date endDate);

    List<MailQueue> findByManuscriptIdAndMailToAndTaskId(Integer manuscriptId, String mailTo, Integer taskId);

    boolean existsByManuscriptIdAndManuscriptVersionAndTaskId(Integer manuscriptId, Integer manuscriptVersion, Integer taskId);
    
	@Modifying
	@Query(value="update mail_queue m set m.is_active=0 where m.journal_id=:currJournalId and m.mail_to_role=:roleId and m.mail_to_userid=:userId ", nativeQuery =  true)
	void deactivateUserForJournalAndRole(int currJournalId, int roleId, Integer userId);

	@Query(value ="select max(mail_send_date) from mail_queue where manuscript_id=:manuscriptId and task_id=:taskId", nativeQuery =  true)
	Date getMailDate(Integer manuscriptId,Integer taskId);
	
}
