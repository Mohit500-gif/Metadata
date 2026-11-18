package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.TatEmailConfiguration;

public interface TatEmailConfigurationRepo extends JpaRepository<TatEmailConfiguration, Integer> {

	@Query(value="SELECT * FROM tat_email_configuration m WHERE m.journal_id=:journalId AND m.manuscript_id=:manuscriptId AND m.manuscript_version=:manuscriptVersion AND m.user_id=:userId AND m.task_id=:taskId ORDER BY m.id ASC LIMIT 1", nativeQuery=true)
	TatEmailConfiguration getTatEmailConfigurationDetails(Integer journalId, Integer manuscriptId,Integer manuscriptVersion,Integer userId,Integer taskId);

	@Query("SELECT m FROM TatEmailConfiguration m WHERE m.isMailSend=:isMailSend AND m.isActive=:isActive")
    List<TatEmailConfiguration> getAllEmailForNotification(Integer isMailSend,Integer isActive);
	
	@Query("SELECT m FROM TatEmailConfiguration m WHERE m.id=:id")
	TatEmailConfiguration getEmailTemplateid(Integer id);

	@Modifying(clearAutomatically = true)
	@Query("update TatEmailConfiguration ud set ud.isActive =0 where ud.journalId =:journalId AND ud.manuscriptId =:manuscriptId AND ud.manuscriptVersion =:manuscriptVersion AND ud.userId =:userId")
	void updateTatEmailConfiguration(Integer journalId, Integer manuscriptId,Integer manuscriptVersion,Integer userId);
}
