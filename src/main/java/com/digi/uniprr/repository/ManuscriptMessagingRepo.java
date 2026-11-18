package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ManuscriptMessaging;

public interface ManuscriptMessagingRepo extends JpaRepository<ManuscriptMessaging, Integer>{

	@Query("SELECT m FROM ManuscriptMessaging m WHERE m.toUserId=:toUserId AND m.toUserRole=:toUserRole OR m.fromUserRole=:toUserRole AND m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion")
	List<ManuscriptMessaging> getMessage(Integer toUserId ,Integer toUserRole,Integer manuscriptId, Integer manuscriptVersion );
	
	@Query("SELECT m FROM ManuscriptMessaging m WHERE m.toUserId=:toUserId AND m.toUserRole=:toUserRole AND m.journalId=:journalId")
	List<ManuscriptMessaging> getToMessage(Integer toUserId ,Integer toUserRole,Integer journalId);
	
	@Query("SELECT m FROM ManuscriptMessaging m WHERE m.fromUserId=:fromUserId AND m.fromUserRole=:fromUserRole AND m.journalId=:journalId ")
	List<ManuscriptMessaging> getMessageFromMessage(Integer fromUserId ,Integer fromUserRole,Integer journalId );
	
	
	@Query("SELECT m FROM ManuscriptMessaging m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.toUserId=:toUserId AND m.toUserRole=:toUserRole AND m.journalId=:journalId")
	List<ManuscriptMessaging> getDataByToManuscript(Integer manuscriptId, Integer manuscriptVersion, Integer toUserId,Integer toUserRole,Integer journalId);
	
	
	@Query("SELECT m FROM ManuscriptMessaging m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion AND m.fromUserId=:fromUserId AND m.fromUserRole=:fromUserRole AND m.journalId=:journalId")
	List<ManuscriptMessaging> getDataByFromManuscript(Integer manuscriptId, Integer manuscriptVersion, Integer fromUserId,Integer fromUserRole,Integer journalId);
	

	@Query("SELECT m FROM ManuscriptMessaging m WHERE (m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion) AND (m.fromUserId=:toUserId AND m.fromUserRole=:toUserRole OR m.toUserRole=:toUserRole)")
	List<ManuscriptMessaging> getDataByManuscript(Integer manuscriptId, Integer manuscriptVersion, Integer toUserId,Integer toUserRole);

	@Query("SELECT m FROM ManuscriptMessaging m WHERE m.manuscriptId=:manuscriptId AND m.manuscriptVersion=:manuscriptVersion")
	List<ManuscriptMessaging> getManuscriptVersionData(Integer manuscriptId, Integer manuscriptVersion);
}
