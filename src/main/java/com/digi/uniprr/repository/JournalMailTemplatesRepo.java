package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digi.uniprr.model.JournalMailTemplates;

public interface JournalMailTemplatesRepo extends JpaRepository<JournalMailTemplates, Integer> {

	@Query(value="select * from journal_mail_templates m where m.journal_email_template_id=:id" , nativeQuery = true)
	List<JournalMailTemplates> getJournalMailTemplatesById(Integer id);

	@Query("SELECT ju FROM JournalMailTemplates ju  WHERE ju.journalId=:journalId")
	public List<JournalMailTemplates> getTemplate(Integer journalId);
	
	@Query("SELECT ju FROM JournalMailTemplates ju  WHERE ju.journalId=:journalId AND ju.emailTemplateName=:emailTemplateName")
	public JournalMailTemplates getUniqueJournalMailTemplates(Integer journalId,String emailTemplateName);
	
	@Query("SELECT ju FROM JournalMailTemplates ju  WHERE ju.journalId=:journalId AND ju.emailTemplateForm=:emailTemplateForm ORDER BY LTRIM(ju.emailTemplateName) ASC")
	public List<JournalMailTemplates> getUniqueJournalMailTemplatesList(Integer journalId,String emailTemplateForm);

	@Query("SELECT ju FROM JournalMailTemplates ju  WHERE ju.emailTemplateName=:emailTemplateName")
	public List<JournalMailTemplates> getEmailTemaplatesByName(String emailTemplateName);

    @Query("SELECT t FROM JournalMailTemplates t " +
            "WHERE t.journalId = :journalId " +
            "AND t.emailTemplateName = :templateName " +
            "AND t.isActive = 'Y'")
     JournalMailTemplates findActiveTemplate(@Param("journalId") Integer journalId,
                                             @Param("templateName") String templateName);
}
