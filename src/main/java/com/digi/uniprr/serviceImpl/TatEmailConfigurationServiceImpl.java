package com.digi.uniprr.serviceImpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.TatEmailConfiguration;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.TatEmailConfigurationRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.TatEmailConfigurationService;

@Service
@Transactional
public class TatEmailConfigurationServiceImpl implements TatEmailConfigurationService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

	@Autowired
	TatEmailConfigurationRepo tatEmailConfigurationRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;

	@Override

	public void create(EmailTriggerVO emailTriggerVO) {

		LOGGER.info(
				"Initiating TAT email configuration creation | journalId: {} | manuscriptId: {} | taskId: {} | userId: {}",
				emailTriggerVO.getJournalId(),
				emailTriggerVO.getManuscriptId(),
				emailTriggerVO.getTaskId(),
				emailTriggerVO.getUserId());

		try {

			Journal journalData = journalsRepo.getOne(emailTriggerVO.getJournalId());

			if (journalData == null) {
				LOGGER.warn("Journal not found | journalId: {}", emailTriggerVO.getJournalId());
				return;
			}

			LOGGER.info("Journal fetched successfully | journalId: {}", emailTriggerVO.getJournalId());

			ManuScript getManuScriptDetails = manuScriptRepo.getById(emailTriggerVO.getManuscriptId());

			if (getManuScriptDetails == null) {
				LOGGER.warn("Manuscript not found | manuscriptId: {}", emailTriggerVO.getManuscriptId());
				return;
			}

			LOGGER.info("Manuscript fetched successfully | manuscriptId: {} | version: {}",
					emailTriggerVO.getManuscriptId(),
					getManuScriptDetails.getManuscriptVersion());

			// Workflow Trigger
			if (journalData.getJournalMetadata() != null
					&& journalData.getJournalMetadata().getWorkflowId() != null) {

				Integer workflowId = journalData.getJournalMetadata().getWorkflowId();

				LOGGER.info("Workflow found for journal | workflowId: {}", workflowId);

				List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getByWorkflowId(workflowId);

				if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {

					LOGGER.info("Workflow sequence records found | count: {}",
							listOfWorkflowRoleSeq.size());

					listOfWorkflowRoleSeq.stream().forEach(i -> {

						try {

							LOGGER.info("Processing workflow sequence | workflowTaskId: {} | tatDays: {}",
									i.getTaskId(),
									i.getTat());

							LOGGER.info(
									"Updating previous active TAT configurations | journalId: {} | manuscriptId: {} | version: {} | userId: {}",
									emailTriggerVO.getJournalId(),
									emailTriggerVO.getManuscriptId(),
									getManuScriptDetails.getManuscriptVersion(),
									emailTriggerVO.getUserId());

							tatEmailConfigurationRepo.updateTatEmailConfiguration(
									emailTriggerVO.getJournalId(),
									emailTriggerVO.getManuscriptId(),
									getManuScriptDetails.getManuscriptVersion(),
									emailTriggerVO.getUserId());

							if (i.getTaskId().equals(emailTriggerVO.getTaskId())) {

								LOGGER.info("Matching workflow task found | taskId: {}",emailTriggerVO.getTaskId());

								TatEmailConfiguration detailsTatEmailConfiguration =
										tatEmailConfigurationRepo.getTatEmailConfigurationDetails(
												emailTriggerVO.getJournalId(),
												emailTriggerVO.getManuscriptId(),
												getManuScriptDetails.getManuscriptVersion(),
												emailTriggerVO.getUserId(),
												emailTriggerVO.getTaskId());

								// Update Existing Record
								if (detailsTatEmailConfiguration != null) {

									LOGGER.info(
											"Existing TAT email configuration found. Updating existing record | manuscriptId: {} | userId: {}",
											emailTriggerVO.getManuscriptId(),
											emailTriggerVO.getUserId());

									detailsTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									detailsTatEmailConfiguration.setManuscriptId(emailTriggerVO.getManuscriptId());
									detailsTatEmailConfiguration.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
									detailsTatEmailConfiguration.setUserId(emailTriggerVO.getUserId());
									detailsTatEmailConfiguration.setTaskId(emailTriggerVO.getTaskId());

									detailsTatEmailConfiguration.setAcceptReviewerUrl(emailTriggerVO.getAcceptByReviewerUrl());
									detailsTatEmailConfiguration.setEditorId(emailTriggerVO.getEditorUserId());
									detailsTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									detailsTatEmailConfiguration.setManagingEditorId(emailTriggerVO.getManagingEditorId());
									detailsTatEmailConfiguration.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
									detailsTatEmailConfiguration.setReciverRoleId(emailTriggerVO.getReceiverRoleId());
									detailsTatEmailConfiguration.setRejectReviewerUrl(emailTriggerVO.getRejectByReviewerUrl());
									detailsTatEmailConfiguration.setReviewerId(emailTriggerVO.getReviewerUserId());
									detailsTatEmailConfiguration.setRoleId(emailTriggerVO.getRoleId());

									detailsTatEmailConfiguration.setViewReviewerUrl(emailTriggerVO.getViewByReviewerUrl());

									Date notificationDate = new Date().from(
											LocalDate.now()
													.plusDays(i.getTat() - 1)
													.atStartOfDay(ZoneId.systemDefault())
													.toInstant());

									Date endDate = new Date().from(
											LocalDate.now()
													.plusDays(i.getTat())
													.atStartOfDay(ZoneId.systemDefault())
													.toInstant());

									detailsTatEmailConfiguration.setNotificationDate(notificationDate);
									detailsTatEmailConfiguration.setEndDate(endDate);

									detailsTatEmailConfiguration.setIsMailSend(0);
									detailsTatEmailConfiguration.setIsActive(1);

									LOGGER.info(
											"Saving updated TAT email configuration | notificationDate: {} | endDate: {}",
											notificationDate,
											endDate);

									tatEmailConfigurationRepo.save(detailsTatEmailConfiguration);

									LOGGER.info(
											"TAT email configuration updated successfully | manuscriptId: {} | userId: {}",
											emailTriggerVO.getManuscriptId(),
											emailTriggerVO.getUserId());

								}

								// Create New Record
								else {

									LOGGER.info(
											"No existing TAT configuration found. Creating new configuration | manuscriptId: {} | userId: {}",
											emailTriggerVO.getManuscriptId(),
											emailTriggerVO.getUserId());

									TatEmailConfiguration addTatEmailConfiguration =
											new TatEmailConfiguration();

									addTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									addTatEmailConfiguration.setManuscriptId(emailTriggerVO.getManuscriptId());
									addTatEmailConfiguration.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
									addTatEmailConfiguration.setTaskId(emailTriggerVO.getTaskId());
									addTatEmailConfiguration.setUserId(emailTriggerVO.getUserId());

									addTatEmailConfiguration.setAcceptReviewerUrl(emailTriggerVO.getAcceptByReviewerUrl());
									addTatEmailConfiguration.setEditorId(emailTriggerVO.getEditorUserId());
									addTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									addTatEmailConfiguration.setManagingEditorId(emailTriggerVO.getManagingEditorId());
									addTatEmailConfiguration.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
									addTatEmailConfiguration.setReciverRoleId(emailTriggerVO.getReceiverRoleId());
									addTatEmailConfiguration.setRejectReviewerUrl(emailTriggerVO.getRejectByReviewerUrl());
									addTatEmailConfiguration.setReviewerId(emailTriggerVO.getReviewerUserId());
									addTatEmailConfiguration.setRoleId(emailTriggerVO.getRoleId());

									addTatEmailConfiguration.setViewReviewerUrl(emailTriggerVO.getViewByReviewerUrl());

									Date notificationDate = new Date().from(
											LocalDate.now()
													.plusDays(i.getTat() - 1)
													.atStartOfDay(ZoneId.systemDefault())
													.toInstant());

									Date endDate = new Date().from(
											LocalDate.now()
													.plusDays(i.getTat())
													.atStartOfDay(ZoneId.systemDefault())
													.toInstant());

									addTatEmailConfiguration.setNotificationDate(notificationDate);
									addTatEmailConfiguration.setEndDate(endDate);

									addTatEmailConfiguration.setIsMailSend(0);
									addTatEmailConfiguration.setIsActive(1);

									LOGGER.info(
											"Saving new TAT email configuration | notificationDate: {} | endDate: {}",
											notificationDate,
											endDate);

									tatEmailConfigurationRepo.save(addTatEmailConfiguration);

									LOGGER.info(
											"New TAT email configuration created successfully | manuscriptId: {} | userId: {}",
											emailTriggerVO.getManuscriptId(),
											emailTriggerVO.getUserId());
								}
							} else {

								LOGGER.debug(
										"Skipping workflow sequence due to task mismatch | workflowTaskId: {} | requestedTaskId: {}",
										i.getTaskId(),
										emailTriggerVO.getTaskId());
							}

						} catch (Exception ex) {

							LOGGER.error(
									"Exception while processing workflow sequence | taskId: {} | manuscriptId: {}",
									i.getTaskId(),
									emailTriggerVO.getManuscriptId(),
									ex);
						}
					});

				} else {

					LOGGER.warn("No workflow sequence found for workflowId: {}", workflowId);
				}

			} else {
				LOGGER.warn("Workflow ID not configured for journalId: {}",emailTriggerVO.getJournalId());
			}

			LOGGER.info("TAT email configuration creation process completed successfully");
		} catch (Exception e) {
			LOGGER.error(
					"Exception occurred while creating TAT email configuration | manuscriptId: {} | taskId: {}",
					emailTriggerVO.getManuscriptId(),
					emailTriggerVO.getTaskId(),
					e);
			e.printStackTrace();

		}
	}

	@Override
	public String extendTat(List<EmailTriggerVO> emailTriggerVO) {
		
		Date newDueDate = new Date() ;
		if (emailTriggerVO != null && !emailTriggerVO.isEmpty()) {
			for (EmailTriggerVO j : emailTriggerVO){
				TatEmailConfiguration i = tatEmailConfigurationRepo.getTatEmailConfigurationDetails(j.getJournalId(),
						j.getManuscriptId(), j.getManuscriptVersion(), j.getUserId(), j.getTaskId());
				if (i != null) {
					Calendar c = Calendar.getInstance();
					c.setTime(j.getExtendDate());
					c.add(Calendar.DATE, -1);
					i.setNotificationDate(c.getTime());
					i.setEndDate(j.getExtendDate());
					tatEmailConfigurationRepo.save(i);
				}
				ManuScriptReviewer k = manuScriptReviewerRepo.getManuScriptReviewerData(j.getManuscriptReviewerId(),j.getManuscriptId(),
						j.getManuscriptVersion(), j.getJournalId(), j.getUserId());
				if (k != null) {
					k.setDueDate(j.getExtendDate());
					manuScriptReviewerRepo.save(k);
				}
				
				if(newDueDate.before(j.getExtendDate()))
				{
				newDueDate = j.getExtendDate();
				}
				
				List<AuthoritiesHistory> authoritiesHistoryList = new ArrayList<>();
				authoritiesHistoryList = authoritiesHistoryRepo.getAuthoritiesHistoryListForUpdateDueDate(
						j.getManuscriptId(), j.getJournalId(), j.getManuscriptVersion(), j.getUserId());
				for (AuthoritiesHistory authoritiesHistory : authoritiesHistoryList) {
					authoritiesHistory.setDueDate(j.getExtendDate());
					authoritiesHistoryRepo.saveAndFlush(authoritiesHistory);
				}
			}
			
			ManuScript manuscr = manuScriptRepo.getById(emailTriggerVO.get(0).getManuscriptId());
			manuscr.setDueDate(newDueDate);
			return "Date has been extended";
		}
		return "There is no data to extend";
	}

	@Override
	public String extendDueDateArticleStage(EmailTriggerVO emailTriggerVO) {
		if (emailTriggerVO != null) {

			if (emailTriggerVO.getTaskId() != null) {
				TatEmailConfiguration i = tatEmailConfigurationRepo.getTatEmailConfigurationDetails(
						emailTriggerVO.getJournalId(), emailTriggerVO.getManuscriptId(),
						emailTriggerVO.getManuscriptVersion(), emailTriggerVO.getUserId(), emailTriggerVO.getTaskId());
				if (i != null) {
					Calendar c = Calendar.getInstance();
					c.setTime(emailTriggerVO.getExtendDate());
					c.add(Calendar.DATE, -1);
					i.setNotificationDate(c.getTime());
					i.setEndDate(emailTriggerVO.getExtendDate());
					tatEmailConfigurationRepo.save(i);
				}
			}
			if (null!= emailTriggerVO.getExtendDate()) {
				manuScriptRepo.updateDueDate(emailTriggerVO.getManuscriptId(), emailTriggerVO.getExtendDate());
			}
			AuthoritiesHistory authoritiesHistory = new AuthoritiesHistory();
			authoritiesHistory = authoritiesHistoryRepo.getById(emailTriggerVO.getAuthorityId());
			authoritiesHistory.setDueDate(emailTriggerVO.getExtendDate());
			authoritiesHistoryRepo.saveAndFlush(authoritiesHistory);
			return "Date has been extended";
		}
		return "There is no data to extend";

	}

	@Override
	public String manuscriptsForReviewAndRereview(EmailTriggerVO emailTriggerVO) {
		Journal journalData = journalsRepo.getOne(emailTriggerVO.getJournalId());
		ManuScript getManuScriptDetails = manuScriptRepo.getById(emailTriggerVO.getManuscriptId());
		if (emailTriggerVO.getManuscriptStatus() != null && emailTriggerVO.getManuscriptStatus().equals(35)) {
			List<ManuScriptReviewer> dataList = manuScriptReviewerRepo
					.getReRivewManuscriptForEmail(emailTriggerVO.getJournalId(), emailTriggerVO.getUserId());
			if (dataList != null && !dataList.isEmpty()) {
				emailTriggerVO.setTaskId(25);
				if (journalData.getJournalMetadata().getWorkflowId() != null) {
					List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo
							.getByWorkflowId(journalData.getJournalMetadata().getWorkflowId());
					if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
						listOfWorkflowRoleSeq.stream().forEach(i -> {

							tatEmailConfigurationRepo.updateTatEmailConfiguration(emailTriggerVO.getJournalId(),
									emailTriggerVO.getManuscriptId(), getManuScriptDetails.getManuscriptVersion(),
									emailTriggerVO.getUserId());
							if (i.getTaskId().equals(emailTriggerVO.getTaskId())) {
								TatEmailConfiguration detailsTatEmailConfiguration = tatEmailConfigurationRepo
										.getTatEmailConfigurationDetails(emailTriggerVO.getJournalId(),
												emailTriggerVO.getManuscriptId(),
												getManuScriptDetails.getManuscriptVersion(), emailTriggerVO.getUserId(),
												emailTriggerVO.getTaskId());
								if (detailsTatEmailConfiguration != null) {
									detailsTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									detailsTatEmailConfiguration.setManuscriptId(emailTriggerVO.getManuscriptId());
									detailsTatEmailConfiguration
											.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
									detailsTatEmailConfiguration.setUserId(emailTriggerVO.getUserId());
									detailsTatEmailConfiguration.setTaskId(emailTriggerVO.getTaskId());

									detailsTatEmailConfiguration
											.setAcceptReviewerUrl(emailTriggerVO.getAcceptByReviewerUrl());
									detailsTatEmailConfiguration.setEditorId(emailTriggerVO.getEditorUserId());
									detailsTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									detailsTatEmailConfiguration
											.setManagingEditorId(emailTriggerVO.getManagingEditorId());
									detailsTatEmailConfiguration
											.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
									detailsTatEmailConfiguration.setReciverRoleId(emailTriggerVO.getReceiverRoleId());
									detailsTatEmailConfiguration
											.setRejectReviewerUrl(emailTriggerVO.getRejectByReviewerUrl());
									detailsTatEmailConfiguration.setReviewerId(emailTriggerVO.getReviewerUserId());
									detailsTatEmailConfiguration.setRoleId(emailTriggerVO.getRoleId());

									detailsTatEmailConfiguration
											.setViewReviewerUrl(emailTriggerVO.getViewByReviewerUrl());

									detailsTatEmailConfiguration.setNotificationDate(
											new Date().from(LocalDate.now().plusDays(i.getTat() - 1)
													.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									detailsTatEmailConfiguration.setEndDate(new Date().from(LocalDate.now()
											.plusDays(i.getTat()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
									detailsTatEmailConfiguration.setIsMailSend(0);
									detailsTatEmailConfiguration.setIsActive(1);
									tatEmailConfigurationRepo.save(detailsTatEmailConfiguration);
								} else {
									TatEmailConfiguration addTatEmailConfiguration = new TatEmailConfiguration();
									addTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									addTatEmailConfiguration.setManuscriptId(emailTriggerVO.getManuscriptId());
									addTatEmailConfiguration
											.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
									addTatEmailConfiguration.setTaskId(emailTriggerVO.getTaskId());
									addTatEmailConfiguration.setUserId(emailTriggerVO.getUserId());

									addTatEmailConfiguration
											.setAcceptReviewerUrl(emailTriggerVO.getAcceptByReviewerUrl());
									addTatEmailConfiguration.setEditorId(emailTriggerVO.getEditorUserId());
									addTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									addTatEmailConfiguration.setManagingEditorId(emailTriggerVO.getManagingEditorId());
									addTatEmailConfiguration.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
									addTatEmailConfiguration.setReciverRoleId(emailTriggerVO.getReceiverRoleId());
									addTatEmailConfiguration
											.setRejectReviewerUrl(emailTriggerVO.getRejectByReviewerUrl());
									addTatEmailConfiguration.setReviewerId(emailTriggerVO.getReviewerUserId());
									addTatEmailConfiguration.setRoleId(emailTriggerVO.getRoleId());

									addTatEmailConfiguration.setViewReviewerUrl(emailTriggerVO.getViewByReviewerUrl());

									addTatEmailConfiguration.setNotificationDate(
											new Date().from(LocalDate.now().plusDays(i.getTat() - 1)
													.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									addTatEmailConfiguration.setEndDate(new Date().from(LocalDate.now()
											.plusDays(i.getTat()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
									addTatEmailConfiguration.setIsMailSend(0);
									addTatEmailConfiguration.setIsActive(1);
									tatEmailConfigurationRepo.save(addTatEmailConfiguration);
								}
							}

						});

					}
				}
			} else {
				emailTriggerVO.setTaskId(24);
				if (journalData.getJournalMetadata().getWorkflowId() != null) {
					List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo
							.getByWorkflowId(journalData.getJournalMetadata().getWorkflowId());
					if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
						listOfWorkflowRoleSeq.stream().forEach(i -> {

							tatEmailConfigurationRepo.updateTatEmailConfiguration(emailTriggerVO.getJournalId(),
									emailTriggerVO.getManuscriptId(), getManuScriptDetails.getManuscriptVersion(),
									emailTriggerVO.getUserId());
							if (i.getTaskId().equals(emailTriggerVO.getTaskId())) {
								TatEmailConfiguration detailsTatEmailConfiguration = tatEmailConfigurationRepo
										.getTatEmailConfigurationDetails(emailTriggerVO.getJournalId(),
												emailTriggerVO.getManuscriptId(),
												getManuScriptDetails.getManuscriptVersion(), emailTriggerVO.getUserId(),
												emailTriggerVO.getTaskId());
								if (detailsTatEmailConfiguration != null) {
									detailsTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									detailsTatEmailConfiguration.setManuscriptId(emailTriggerVO.getManuscriptId());
									detailsTatEmailConfiguration
											.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
									detailsTatEmailConfiguration.setUserId(emailTriggerVO.getUserId());
									detailsTatEmailConfiguration.setTaskId(emailTriggerVO.getTaskId());

									detailsTatEmailConfiguration
											.setAcceptReviewerUrl(emailTriggerVO.getAcceptByReviewerUrl());
									detailsTatEmailConfiguration.setEditorId(emailTriggerVO.getEditorUserId());
									detailsTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									detailsTatEmailConfiguration
											.setManagingEditorId(emailTriggerVO.getManagingEditorId());
									detailsTatEmailConfiguration
											.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
									detailsTatEmailConfiguration.setReciverRoleId(emailTriggerVO.getReceiverRoleId());
									detailsTatEmailConfiguration
											.setRejectReviewerUrl(emailTriggerVO.getRejectByReviewerUrl());
									detailsTatEmailConfiguration.setReviewerId(emailTriggerVO.getReviewerUserId());
									detailsTatEmailConfiguration.setRoleId(emailTriggerVO.getRoleId());

									detailsTatEmailConfiguration
											.setViewReviewerUrl(emailTriggerVO.getViewByReviewerUrl());

									detailsTatEmailConfiguration.setNotificationDate(
											new Date().from(LocalDate.now().plusDays(i.getTat() - 1)
													.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									detailsTatEmailConfiguration.setEndDate(new Date().from(LocalDate.now()
											.plusDays(i.getTat()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
									detailsTatEmailConfiguration.setIsMailSend(0);
									detailsTatEmailConfiguration.setIsActive(1);
									tatEmailConfigurationRepo.save(detailsTatEmailConfiguration);
								} else {
									TatEmailConfiguration addTatEmailConfiguration = new TatEmailConfiguration();
									addTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									addTatEmailConfiguration.setManuscriptId(emailTriggerVO.getManuscriptId());
									addTatEmailConfiguration
											.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
									addTatEmailConfiguration.setTaskId(emailTriggerVO.getTaskId());
									addTatEmailConfiguration.setUserId(emailTriggerVO.getUserId());

									addTatEmailConfiguration
											.setAcceptReviewerUrl(emailTriggerVO.getAcceptByReviewerUrl());
									addTatEmailConfiguration.setEditorId(emailTriggerVO.getEditorUserId());
									addTatEmailConfiguration.setJournalId(emailTriggerVO.getJournalId());
									addTatEmailConfiguration.setManagingEditorId(emailTriggerVO.getManagingEditorId());
									addTatEmailConfiguration.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
									addTatEmailConfiguration.setReciverRoleId(emailTriggerVO.getReceiverRoleId());
									addTatEmailConfiguration
											.setRejectReviewerUrl(emailTriggerVO.getRejectByReviewerUrl());
									addTatEmailConfiguration.setReviewerId(emailTriggerVO.getReviewerUserId());
									addTatEmailConfiguration.setRoleId(emailTriggerVO.getRoleId());

									addTatEmailConfiguration.setViewReviewerUrl(emailTriggerVO.getViewByReviewerUrl());

									addTatEmailConfiguration.setNotificationDate(
											new Date().from(LocalDate.now().plusDays(i.getTat() - 1)
													.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									addTatEmailConfiguration.setEndDate(new Date().from(LocalDate.now()
											.plusDays(i.getTat()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
									addTatEmailConfiguration.setIsMailSend(0);
									addTatEmailConfiguration.setIsActive(1);
									tatEmailConfigurationRepo.save(addTatEmailConfiguration);
								}
							}

						});

					}
				}
			}

		}
		return "Tat set up";
	}

}