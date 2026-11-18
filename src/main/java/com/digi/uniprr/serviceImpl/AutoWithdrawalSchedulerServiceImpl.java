package com.digi.uniprr.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.service.AutoWithdrawalSchedulerService;
import com.digi.uniprr.utils.EmailTriggerTemplateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AutoWithdrawalSchedulerServiceImpl implements AutoWithdrawalSchedulerService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptRepo manuScriptRepo;

	@Autowired
	private ReminderTrackerRepo reminderTrackerRepo;

	@Autowired
	private TaskEmailMapReminderRepo taskEmailMapReminderRepo;

	@Autowired
	private TaskEmailMapRepo taskEmailMapRepo;

	@Autowired
	private MailQueueRepo mailQueueRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EmailTriggerTemplateUtils emailTriggerTemplateUtils;

	@Autowired
	private Environment env;

	// --- STATUS CONSTANTS ---
	private static final int TECHNICAL_MODIFICATION = 46;
	private static final int REVISION = 4;
	private static final int WITHDRAWN = 16;

	// --- ROLE CONSTANTS ---
	private static final int AUTHOR_ROLE_ID = 4;
	private static final int EDITOR_ROLE_ID = 2;
	private static final int EIC_ROLE_ID = 3;

	@Override
	@Transactional
	public String processAutoWithdrawalReminders() {
		logger.info("===== Starting Auto Withdrawal Scheduler =====");

		List<Integer> targetStatuses = Arrays.asList(TECHNICAL_MODIFICATION, REVISION);
		List<Tuple> manuscripts = manuScriptRepo.getAllManuscriptForWarningSchedular(targetStatuses);
		Date today = new Date();

		int reminderCount = 0;
		int withdrawnCount = 0;

		for (Tuple tuple : manuscripts) {
			try {
				Integer journalId = tuple.get(0, Integer.class);
				Integer manuscriptId = tuple.get(1, Integer.class);
				Integer version = tuple.get(2, Integer.class);
				Integer status = tuple.get(3, Integer.class);
				Integer assignedAuthor = tuple.get(4, Integer.class);
				Integer assignedUser = tuple.get(5, Integer.class);
				Date dueDate = tuple.get(8, Date.class);

				if (dueDate == null || dueDate.after(today))
					continue; // skip if extended or missing

				long daysPassed = ChronoUnit.DAYS.between(dueDate.toInstant(), today.toInstant());
				long weeksPassed = daysPassed / 7;

				if (weeksPassed >= 11 && weeksPassed < 13) {
					sendReminderEmail(journalId, manuscriptId, version, status, assignedAuthor, assignedUser);
					reminderCount++;
				} else if (weeksPassed >= 13) {
					autoWithdrawManuscript(journalId, manuscriptId, version, assignedAuthor, assignedUser, status);
					withdrawnCount++;
				}

			} catch (Exception e) {
				logger.error("Error processing manuscript {}", tuple.get(1, Integer.class), e);
			}
		}

		logger.info("Scheduler finished: {} reminders sent, {} manuscripts auto-withdrawn.",
				reminderCount, withdrawnCount);
		return "Auto Withdrawal Scheduler executed successfully.";
	}

	// ----------------------------------------------------------------
	// -------------------- SEND REMINDER EMAIL -----------------------
	// ----------------------------------------------------------------
	private void sendReminderEmail(Integer journalId, Integer manuscriptId, Integer version, Integer status,
			Integer assignedAuthor, Integer assignedUser) {

		try {
			int taskId = (status == TECHNICAL_MODIFICATION) ? 96 : 97;

			// 🔒 Prevent duplicate reminders in the same week
			if (mailAlreadySentThisWeek(manuscriptId, journalId, taskId)) {
				logger.info("⚠ Reminder for manuscript {} (task {}) already sent this week — skipping.", manuscriptId, taskId);
				return;
			}

			TaskEmailMapReminder map = taskEmailMapReminderRepo.getTaskEmailMapDetails(taskId, journalId);
			if (map == null || map.getJournalMailTemplates() == null) {
				logger.warn("No reminder template found for task {} journal {}", taskId, journalId);
				return;
			}

			queueReminderMail(journalId, manuscriptId, version, assignedAuthor, AUTHOR_ROLE_ID, taskId, map, status);
			logger.info("📧 Reminder queued for Author (userId={}) manuscript {}", assignedAuthor, manuscriptId);

		} catch (Exception e) {
			logger.error("❌ Error sending reminder email for manuscript {}", manuscriptId, e);
		}
	}

	// ----------------------------------------------------------------
	// -------------------- AUTO WITHDRAWAL ---------------------------
	// ----------------------------------------------------------------
	private void autoWithdrawManuscript(Integer journalId, Integer manuscriptId, Integer version,
			Integer assignedAuthor, Integer assignedUser, Integer status) {

		try {
			ManuScript manuscript = manuScriptRepo.findById(manuscriptId).orElse(null);
			if (manuscript == null)
				return;

			manuscript.setManuscriptStatusBeforeWithdrawn(status);
			manuscript.setManuscriptStatusId(WITHDRAWN);
			manuscript.setWithdrawnDate(new Date());
			manuScriptRepo.save(manuscript);

			logger.info("✅ Manuscript {} auto-withdrawn", manuscriptId);

			// Email to Author
			int authorTask = (status == TECHNICAL_MODIFICATION) ? 98 : 100;
			TaskEmailMap authorTemplate = taskEmailMapRepo.getTaskEmailMapDetails(authorTask, journalId);
			queueMail(journalId, manuscriptId, version, assignedAuthor, AUTHOR_ROLE_ID, authorTask, authorTemplate, status);

			// Email to Editors (Editor/EIC)
			Integer editorRole = userRepo.getEditorRoleForJournal(assignedUser, journalId);
			if (editorRole != null) {
				int editorTask = (status == TECHNICAL_MODIFICATION) ? 99 : 101;
				TaskEmailMap editorTemplate = taskEmailMapRepo.getTaskEmailMapDetails(editorTask, journalId);
				queueMail(journalId, manuscriptId, version, assignedUser, editorRole, editorTask, editorTemplate, status);
			}

		} catch (Exception e) {
			logger.error("❌ Error auto-withdrawing manuscript {}", manuscriptId, e);
		}
	}

	// ----------------------------------------------------------------
	// -------------------- MAIL QUEUE METHODS ------------------------
	// ----------------------------------------------------------------
	private void queueReminderMail(Integer journalId, Integer manuscriptId, Integer version, Integer userId,
			Integer roleId, Integer taskId, TaskEmailMapReminder map, Integer status)
			throws MalformedURLException, UnsupportedEncodingException {

		User user = userRepo.getByID(userId);
		if (user == null || user.getEmail() == null)
			return;

		String fromMail = getFromMail(map);
		EmailTriggerVO trigger = buildTriggerVO(journalId, manuscriptId, version, status, userId, taskId);
		String subject = emailTriggerTemplateUtils.getSubjectForReminder(trigger, map);
		String body = emailTriggerTemplateUtils.getAllDataForReminder(trigger, map);

		MailQueue mail = new MailQueue();
		mail.setIsActive(0);
		mail.setIsSeen(0);
		mail.setTaskId(taskId);
		mail.setJournalId(journalId);
		mail.setMail_send_date(new Date());
		mail.setMailFrom(fromMail);
		mail.setMailTo(user.getEmail());
		mail.setMailTOUserId(user.getId());
		mail.setMailToRole(roleId);
		mail.setMailSubject(subject);
		mail.setMailBody(body);
		mail.setManuscriptId(manuscriptId);
		mail.setManuscriptVersion(version);
		mail.setManuscriptStatus(status);

		mailQueueRepo.save(mail);
	}

	private void queueMail(Integer journalId, Integer manuscriptId, Integer version, Integer userId,
			Integer roleId, Integer taskId, TaskEmailMap template, Integer status)
			throws MalformedURLException, UnsupportedEncodingException {

		if (template == null || template.getJournalMailTemplates() == null)
			return;

		User user = userRepo.getByID(userId);
		if (user == null || user.getEmail() == null)
			return;

		String fromMail = getFromMail(template);
		EmailTriggerVO trigger = buildTriggerVO(journalId, manuscriptId, version, status, userId, taskId);
		String subject = emailTriggerTemplateUtils.getSubject(trigger, template);
		String body = emailTriggerTemplateUtils.getAllData(trigger, template);

		MailQueue mail = new MailQueue();
		mail.setIsActive(0);
		mail.setIsSeen(0);
		mail.setTaskId(taskId);
		mail.setJournalId(journalId);
		mail.setMail_send_date(new Date());
		mail.setMailFrom(fromMail);
		mail.setMailTo(user.getEmail());
		mail.setMailTOUserId(user.getId());
		mail.setMailToRole(roleId);
		mail.setMailSubject(subject);
		mail.setMailBody(body);
		mail.setManuscriptId(manuscriptId);
		mail.setManuscriptVersion(version);
		mail.setManuscriptStatus(status);

		mailQueueRepo.save(mail);
	}

	// ----------------------------------------------------------------
	// -------------------- HELPERS -----------------------------------
	// ----------------------------------------------------------------
	private boolean mailAlreadySentThisWeek(Integer manuscriptId, Integer journalId, Integer taskId) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		Date weekStart = cal.getTime();
		cal.add(Calendar.DAY_OF_WEEK, 6);
		Date weekEnd = cal.getTime();

		List<MailQueue> mails = mailQueueRepo.findMailsSentInDateRange(manuscriptId, taskId, journalId, weekStart, weekEnd);
		return mails != null && !mails.isEmpty();
	}

	private EmailTriggerVO buildTriggerVO(Integer journalId, Integer manuscriptId, Integer version,
			Integer status, Integer userId, Integer taskId) {
		EmailTriggerVO vo = new EmailTriggerVO();
		vo.setJournalId(journalId);
		vo.setManuscriptId(manuscriptId);
		vo.setManuscriptVersion(version);
		vo.setManuscriptStatus(status);
		vo.setReceiverRoleId(AUTHOR_ROLE_ID);
		vo.setAuthorUserId(userId);
		vo.setTaskId(taskId);
		return vo;
	}

	private String getFromMail(TaskEmailMapReminder map) {
		if (map.getJournalMailTemplates().getJournal().getJournalMetadata() != null
				&& map.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail() != null) {
			return map.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail();
		}
		return env.getProperty("fromMail");
	}

	private String getFromMail(TaskEmailMap map) {
		if (map.getJournalMailTemplates().getJournal().getJournalMetadata() != null
				&& map.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail() != null) {
			return map.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail();
		}
		return env.getProperty("fromMail");
	}
}
