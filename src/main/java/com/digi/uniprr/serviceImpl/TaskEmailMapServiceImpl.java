package com.digi.uniprr.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalMailTemplatesVO;
import com.digi.uniprr.model.TaskEmailMap;
import com.digi.uniprr.model.TaskEmailMapReminder;
import com.digi.uniprr.repository.TaskEmailMapReminderRepo;
import com.digi.uniprr.repository.TaskEmailMapRepo;
import com.digi.uniprr.service.TaskEmailMapService;

@Service
@Transactional
public class TaskEmailMapServiceImpl implements TaskEmailMapService {
	@Autowired
	private TaskEmailMapRepo taskEmailMapRepo;
	
	@Autowired
	private TaskEmailMapReminderRepo taskEmailMapReminderRepo;

	@Override
	public List<TaskEmailMap> customiseMailTemplate(Integer id) {
		return taskEmailMapRepo.customiseMailTemplate(id);
	}

	@Override
	public TaskEmailMap saveTaskEmailMap(TaskEmailMap taskEmailMap) {
		return taskEmailMapRepo.save(taskEmailMap);
	}

	@Override
	public void deleteTaskEmailMap(Integer id) {
		taskEmailMapRepo.deleteById(id);

	}

	@Override
	public JournalMailTemplatesVO getEmailTemplateByTaskAndJournal(IDVO idvo) {
		TaskEmailMap i = taskEmailMapRepo.getTaskEmailMapDetails(idvo.getTaskId(),
				idvo.getJournalId());
		if (i != null && i.getJournalMailTemplates().getEmailTemplateForm()
				.equalsIgnoreCase("NOTIFICATION")) {

			JournalMailTemplatesVO jVo = new JournalMailTemplatesVO();
			jVo.setJournalEmailTemplateId(i.getJournalMailTemplates().getJournalEmailTemplateId());
			jVo.setEmailTemplateName(i.getJournalMailTemplates().getEmailTemplateName());
			jVo.setTemplatePlaceholderId(i.getJournalMailTemplates().getTemplatePlaceholderId());
			jVo.setJournalId(i.getJournalMailTemplates().getJournalId());
			jVo.setEmailTemplateForm(i.getJournalMailTemplates().getEmailTemplateForm());
			jVo.setIsActive(i.getJournalMailTemplates().getIsActive());
			jVo.setMailBcc(i.getJournalMailTemplates().getMailBcc());
			jVo.setMailBody(i.getJournalMailTemplates().getMailBody());
			jVo.setMailcc(i.getJournalMailTemplates().getMailcc());
			jVo.setMailSubject(i.getJournalMailTemplates().getMailSubject());
			jVo.setMailTo(i.getJournalMailTemplates().getMailTo());
			jVo.setMailFrom(i.getJournalMailTemplates().getMailFrom());
			jVo.setCreatedOn(i.getJournalMailTemplates().getCreatedOn());
			jVo.setUpdatedOn(i.getJournalMailTemplates().getUpdatedOn());

			return jVo;

		}
		return null;
	}

	@Override
	public JournalMailTemplatesVO getEmailTemplateByTaskAndJournalForReminder(IDVO idvo) {
		TaskEmailMapReminder i = taskEmailMapReminderRepo.getTaskEmailMapDetails(idvo.getTaskId(),
				idvo.getJournalId());
		if (i != null && i.getJournalMailTemplates().getEmailTemplateForm()
				.equalsIgnoreCase("REMINDER")) {
			JournalMailTemplatesVO jVo = new JournalMailTemplatesVO();
			jVo.setJournalEmailTemplateId(i.getJournalMailTemplates().getJournalEmailTemplateId());
			jVo.setEmailTemplateName(i.getJournalMailTemplates().getEmailTemplateName());
			jVo.setTemplatePlaceholderId(i.getJournalMailTemplates().getTemplatePlaceholderId());
			jVo.setJournalId(i.getJournalMailTemplates().getJournalId());
			jVo.setEmailTemplateForm(i.getJournalMailTemplates().getEmailTemplateForm());
			jVo.setIsActive(i.getJournalMailTemplates().getIsActive());
			jVo.setMailBcc(i.getJournalMailTemplates().getMailBcc());
			jVo.setMailBody(i.getJournalMailTemplates().getMailBody());
			jVo.setMailcc(i.getJournalMailTemplates().getMailcc());
			jVo.setMailSubject(i.getJournalMailTemplates().getMailSubject());
			jVo.setMailTo(i.getJournalMailTemplates().getMailTo());
			jVo.setMailFrom(i.getJournalMailTemplates().getMailFrom());
			jVo.setCreatedOn(i.getJournalMailTemplates().getCreatedOn());
			jVo.setUpdatedOn(i.getJournalMailTemplates().getUpdatedOn());
//			if (idvo.getTaskId()==13) {
//				jVo.setModule_name("AuthorReminderSubmissionWithoutPayment");
//			}else
//			{
//				jVo.setModule_name("AuthorReminderSubmissionWithPayment");
//			}
			return jVo;
		}
		return null;
	}

}
