package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.MailTemplates;

@Repository
@Transactional
public interface MailTemplatesRepo extends JpaRepository<MailTemplates, Integer> {

	@Query("select m from MailTemplates m where m.emailTemplateId=:id")
	List<MailTemplates> getMailTemplatesById(Integer id);
	
	@Query("SELECT m FROM MailTemplates m WHERE m.isActive=1 ORDER BY LTRIM(m.emailTemplateName) ASC")
	List<MailTemplates> getActiveMailTemplates();
	
	@Query("UPDATE MailTemplates m SET m.isActive=0 WHERE m.emailTemplateId=:id")
	@Modifying
	void deleteMailTemplate(Integer id);

	@Query("select m from MailTemplates m where m.emailTemplateName=:name")
	MailTemplates getMailTemplatesByName(String name);
	

}
