package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.MailQueueArchive;

public interface MailQueueArchiveRepo extends JpaRepository<MailQueueArchive, Integer>{
	
	@Query("SELECT m from MailQueueArchive m where m.mailTOUserId=:id AND m.mailToRole=:roleId AND m.journalId=:journalId AND m.isActive=:isActive ORDER BY m.id DESC")
	List<MailQueueArchive> getMailQueueArchiveUserId(Integer id,Integer roleId,Integer journalId,Integer isActive);
	
	@Query("SELECT m FROM MailQueueArchive m WHERE m.manuscriptId=:manuscriptId AND (m.isActive=1 OR m.isActive=2) ORDER BY m.id DESC")
	List<MailQueueArchive> getMailByManuscriptForAll(Integer manuscriptId);
}
