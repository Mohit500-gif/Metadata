package com.digi.uniprr.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.digi.uniprr.VO.AssignAdminVO;
import com.digi.uniprr.VO.AutoMailersVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalHistoryDataVO;
import com.digi.uniprr.VO.JournalMailTemplatesVO;
import com.digi.uniprr.VO.JournalPlagiarismMasterVO;
import com.digi.uniprr.VO.JournalPlagiarismUpdateVO;
import com.digi.uniprr.VO.JournalSubjectVo;
import com.digi.uniprr.VO.JournalTatConfigurationVO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.VO.JournalsConfigurationVO;
import com.digi.uniprr.VO.KeywordsVO;
import com.digi.uniprr.VO.TurnAroundTimeVO;
import com.digi.uniprr.model.CharacterLimit;
import com.digi.uniprr.model.EmailTemplateData;
import com.digi.uniprr.model.FileType;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalMailTemplates;
import com.digi.uniprr.model.JournalconfigFileUpload;
import com.digi.uniprr.model.JournalconfigMasterfileType;
import com.digi.uniprr.model.Keywords;
import com.digi.uniprr.model.MailTemplates;
import com.digi.uniprr.model.TaskMaster;

public interface JournalConfigurationService {

	public Journal getJournalById(Integer id);

	public JournalsConfigurationVO mapJournalProperties(Journal journal);

	public Integer configureAndSaveJournal(JournalsConfigurationVO journalConfigurationVO);

	public void saveJournalMetadata(JournalsConfigurationVO journalsConfigurationVO, Integer journalId);

	public void saveJournalChargeDetails(JournalsConfigurationVO journalsConfigurationVO, Integer journalId);

	public void saveJournalBlindType(Integer id, String blindType);

	// Upload File

	public void deleteManuscriptTypeJournal(Integer id);

	public List<JournalconfigFileUpload> getFileList(Integer journalId);

	public String deleteJournalconfigFile(JournalVO journalVO);

	public JournalconfigFileUpload JournalconfigFile(Integer id);

	public boolean isIdExist(Integer id);

	public JournalconfigFileUpload saveFile(JournalconfigFileUpload journalconfigFileUpload);

	public List<JournalconfigMasterfileType> getFileTypeList();

	public String downloadJournalFile(JournalVO journalVO);

	ResponseEntity<Resource> downloadJournalFileWithFileType(JournalVO journalVO);

	// Email Template

	public List<MailTemplates> getEmailList();

	public String deleteEmail(Integer id);

	public boolean isEmailIdExist(Integer id);

	// KeyWords
	public List<KeywordsVO> getJournalKeyWordList(Integer journalId);

	public void saveKeyWords(List<Keywords> Keywords);

	public Keywords updateKeyword(Keywords keywords);

	public void updateKeywordList(List<KeywordsVO> keywordsVO);

	public List<Journal> getJournalsList();

	public void deleteKeyword(Integer id);

	// AutoMailers

	public List<JournalMailTemplates> getTemplate(Integer journalId);

	public String saveList(List<JournalconfigFileUpload> journalconfigFileUpload);

	List<JournalPlagiarismMasterVO> getjournalPlagiarismlist(Integer jornalId);

	void updatePlagiarism(List<JournalPlagiarismUpdateVO> journalPlagiarismUpdateVO);

	/********Asign Admin**********/
	public List<Integer> getAssignAdminIdList(Integer journalId);

	public void assignAdmin(AssignAdminVO assignAdminVO);

	public List<TurnAroundTimeVO> getjournalTatConfigurationList(TurnAroundTimeVO turnAroundTimeVO);

	public String saveJournalTatConfiguration(JournalTatConfigurationVO journalTatConfigurationVO);

	public List<EmailTemplateData> getEmailTemplateData();

	public String saveJournalMailTemplate(JournalMailTemplatesVO journalMailTemplatesVO);

	public boolean isJournalMailTemplate(JournalMailTemplatesVO journalMailTemplatesVO);

	public List<JournalMailTemplatesVO> getEmailTemplates(Integer journalId);

	public List<TaskMaster> getTaskName();

	public String saveAutoMailers(AutoMailersVO autoMailersVO);

	public Boolean isAutomailerExist(AutoMailersVO autoMailersVO);

	public List<AutoMailersVO> getAutoMailesList(AutoMailersVO autoMailersVO);

	public String deleteAutoMailer(AutoMailersVO autoMailersVO);

	public String updateJournalWorkflow(Integer workflowId, Integer jornalId);

	public List<JournalMailTemplatesVO> getEmailTemplatesForAutoMailers(IDVO id);

	public List<TaskMaster> getTaskNameForReminder();

	public boolean isAutomailerForReminderExist(AutoMailersVO autoMailersVO);

	public String saveAutoMailersForReminder(AutoMailersVO autoMailersVO);

	public List<AutoMailersVO> getAutoMailersListForReminder(AutoMailersVO autoMailersVO);

	public String deleteAutoMailerForReminder(AutoMailersVO autoMailersVO);

	public List<JournalSubjectVo> getJournalSubjectList(JournalSubjectVo journalSubjectVo);

	public String saveJournalSubject(JournalSubjectVo journalSubjectVo);

//	public boolean isSubjectExist(String journalSubject);

	public boolean isSubjectExist(String journalSubject, Integer journalId);


	public String deleteJournalSubject(JournalSubjectVo journalSubjectVo);

	public String bulkSaveJournalMailTemplate(List<JournalMailTemplatesVO> journalMailTemplatesVO);

	public void deleteJournalTemplate(JournalMailTemplatesVO journalMailTemplatesVO);

	public void saveJournalHistoryData(JournalHistoryDataVO journalHistoryDataVO);
	
	public CharacterLimit getCharacterLimit(JournalHistoryDataVO journalHistoryDataVO);


}
