package com.digi.uniprr.serviceImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.AssignEditorVO;
import com.digi.uniprr.VO.AuthoritiesHistoryVo;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.ReviewerSuggestedVO;
import com.digi.uniprr.controller.dao.UserDao;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.ManuscriptEditorComment;
import com.digi.uniprr.model.OtherMail;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.TatEmailConfiguration;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.EditorRoleAccessRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorCommentRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.OtherMailRepo;
import com.digi.uniprr.repository.ReviewerAvailabilityRepo;
import com.digi.uniprr.repository.ReviewerSuggestedRepo;
import com.digi.uniprr.repository.RoleRepo;
import com.digi.uniprr.repository.TatEmailConfigurationRepo;
import com.digi.uniprr.repository.UserGroupRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserLoginsRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.repository.UserSpecilizationRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.AssignEditerService;
import com.digi.uniprr.service.AuthoritiesHistoryService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuscriptEditorService;
import com.digi.uniprr.utils.SendMailUtils;

@Service
@Transactional
public class AssignEditorServiceImpl implements AssignEditerService{
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserLoginRepo userLoginRepo;

	@Autowired
	AuthoritiesHistoryService authoritiesHistoryService;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;

	@Autowired
	Environment env;

	@Autowired
	OtherMailRepo otherMailRepo;

	@Autowired
	SendMailUtils mailUtils;

	@Autowired
	UserRoleRepo userRoleRepo;

	@Autowired
	UserGroupRepo userGroupRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ManuScriptRepo manuscriptRepo;

	@Autowired
	MailQueueRepo mailQueueRepo;

	@Autowired
	CountriesService countriesService;

	@Autowired
	ManuScriptReviewerService manuscriptReviewerService;

	@Autowired
	ManuscriptEditorService manuscriptEditorService;

	@Autowired
	ManuScriptService manuscriptService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	UserSpecilizationRepo userSpecilizationRepo;

	@Autowired
	UserDao userDao;

	@Autowired
	ReviewerAvailabilityRepo reviewerAvailabilityRepo;

	@Autowired
	UserLoginsRepo userLoginsRepo;

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	EditorRoleAccessRepo editorRoleAccessRepo;

	@Autowired
	ReviewerSuggestedRepo reviewerSuggestedRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	RoleRepo roleRepo;

	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

	@Autowired
	TatEmailConfigurationRepo tatEmailConfigurationRepo;
	
	@Autowired
	ManuscriptEditorCommentRepo manuscriptEditorCommentRepo;

	@Autowired
	Environment environment;
	
	@Value("${tempPassword}")
	String tempPassword;

	@Value("${oauthBaseUrl}")
	String oauthBaseUrl;

	@Value("${oauthClientId}")
	String oauthClientId;

	@Value("${oauthClientSecret}")
	String oauthClientSecret;
	
	@Value("${uniprrFolderPath}")
	private String UPLOAD_FOLDER;

	@Autowired
	CountriesRepo countriesRepo;
	
	@Override
	public void assignUser(AssignEditorVO assignEditorVO,MultipartFile file,HttpServletRequest request) throws Exception {

		if (assignEditorVO.getManuscriptListVO().getRoleId() == 5) {
			if (assignEditorVO.getManuscriptListVO().getAssignToID() != null && !assignEditorVO.getManuscriptListVO().getAssignToID().isEmpty()) {

				ManuScript data1 = manuscriptRepo.getById(assignEditorVO.getManuscriptListVO().getManuScriptId());
				if(!data1.getAssignedUserRole().equals(assignEditorVO.getManuscriptListVO().getEditorRoleId())){
					ManuscriptEditor manuscriptEditor = manuscriptEditorRepo.getAllAssignedEdiorsOnManuscriptActive(assignEditorVO.getManuscriptListVO().getManuScriptId(), assignEditorVO.getManuscriptListVO().getManuscriptVersion());
					if(manuscriptEditor != null && manuscriptEditor.getManuscriptStatus() != 51 && manuscriptEditor.getManuscriptStatus() != 52 && manuscriptEditor.getManuscriptStatus() != 68) {
						manuscriptEditorRepo.disableEditor(assignEditorVO.getManuscriptListVO().getManuScriptId());
					}
					List<ManuScriptReviewer> reviewerList = manuScriptReviewerRepo.getInpogressReviewerList(assignEditorVO.getManuscriptListVO().getManuScriptId(), assignEditorVO.getManuscriptListVO().getManuscriptVersion());
					for (ManuScriptReviewer manuScriptReviewer : reviewerList) {
						manuScriptReviewer.setEditorRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
						manuScriptReviewer.setEditorId(assignEditorVO.getManuscriptListVO().getUserId());
						manuScriptReviewerRepo.save(manuScriptReviewer);
					}
				}
				for (Integer assignToId : assignEditorVO.getManuscriptListVO().getAssignToID()) {
					User getUserData = userRepo.getUserData(assignToId);
					if (getUserData != null) {
						ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
								assignEditorVO.getManuscriptListVO().getJournalId(), assignEditorVO.getManuscriptListVO().getManuScriptId(),
								assignEditorVO.getManuscriptListVO().getManuscriptVersion(), assignEditorVO.getManuscriptListVO().getUserId());
						if (p != null) {
							p.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
							manuscriptEditorRepo.save(p);
						}
						ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo.findReviewerForAssign(
								assignEditorVO.getManuscriptListVO().getManuScriptId(), assignEditorVO.getManuscriptListVO().getManuscriptVersion(),
								assignEditorVO.getManuscriptListVO().getJournalId(), assignToId);
						if (manuscriptReviewer != null && (manuscriptReviewer.getManuscriptStatus().equals(8) || manuscriptReviewer.getManuscriptStatus().equals(66) || manuscriptReviewer.getManuscriptStatus().equals(35))) {
							UserJournal userJournal = userJournalRepo.getUserJournal(assignToId,
									assignEditorVO.getManuscriptListVO().getJournalId(), 5);
							if (userJournal != null) {
								userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
								userJournal.setIsActive(1);
								userJournalRepo.save(userJournal);
							} else {
								UserJournal userJournalData = new UserJournal();
								userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								userJournalData.setUserId(assignToId);
								userJournalData.setRoleId(5);
								userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
								userJournalData.setIsActive(1);
								userJournalRepo.save(userJournalData);
							}
							manuScriptReviewerRepo.updateReviewerDetails(assignEditorVO.getManuscriptListVO().getManuStatusId(),
									manuscriptReviewer.getId(), assignEditorVO.getManuscriptListVO().getDueDate());
						} else {
							ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
							manuscriptReviewerData.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
							manuscriptReviewerData.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
							manuscriptReviewerData.setReviewerId(assignToId);
							manuscriptReviewerData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
							manuscriptReviewerData.setEditorId(assignEditorVO.getManuscriptListVO().getUserId());
							manuscriptReviewerData.setEditorRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
							manuscriptReviewerData.setCommentsForReviewer(assignEditorVO.getManuscriptListVO().getMessage());
							manuscriptReviewerData.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
							if (assignEditorVO.getManuscriptListVO().getDueDate() == null || assignEditorVO.getManuscriptListVO().getDueDate().equals("")
									|| assignEditorVO.getManuscriptListVO().getDueDate().equals("NaN-NaN-NaN")) {
								manuscriptReviewerData.setDueDate(new Date().from(
										LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
							} else {
								manuscriptReviewerData.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
							}
							UserJournal userJournal = userJournalRepo.getUserJournal(assignToId,
									assignEditorVO.getManuscriptListVO().getJournalId(), 5);
							if (userJournal != null) {
								userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
								userJournal.setIsActive(1);
								userJournalRepo.save(userJournal);
							} else {
								UserJournal userJournalData = new UserJournal();
								userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								userJournalData.setUserId(assignToId);
								userJournalData.setRoleId(5);
								userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
								userJournalData.setIsActive(1);
								userJournalRepo.save(userJournalData);
							}
							manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);
						}
						if (assignEditorVO.getManuscriptListVO().getManuStatusId() == 8 || assignEditorVO.getManuscriptListVO().getManuStatusId() == 66) {
							manuscriptRepo.updateStage(assignEditorVO.getManuscriptListVO().getManuStatusId(),
									assignEditorVO.getManuscriptListVO().getManuScriptId());
							if (assignEditorVO.getManuscriptListVO().getEditorRoleId() != null
									&& assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
								manuscriptRepo.updateAssignedId(assignEditorVO.getManuscriptListVO().getManuScriptId(),
										assignEditorVO.getManuscriptListVO().getUserId(), 2);

						}

						// Email Trigger
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
						emailTriggerVO.setUserId(assignToId);
						emailTriggerVO.setReviewerUserId(assignToId);

						if (assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
							emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
						else
							emailTriggerVO.setEditorUserId(assignEditorVO.getManuscriptListVO().getUserId());

						emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
						emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
						emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getRoleId());
						if (assignEditorVO.getManuscriptListVO().getTemplateId() != null) {
							emailTriggerVO.setTaskId(assignEditorVO.getManuscriptListVO().getTemplateId());
						} else {
							emailTriggerVO.setTaskId(4);
						}
						emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());

						// For The Sending Url To Reviewer
						if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody() != null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody().isEmpty())
							emailTriggerVO.setEmailTemplateBody(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody());
						if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject()!= null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject().isEmpty())
							emailTriggerVO.setEmailTemplateSubject(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject());

						emailTriggerVO.setUrlManuscript(assignEditorVO.getManuscriptListVO().getManuScriptId());
						emailTriggerVO.setUrlManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
						emailTriggerVO.setUrlJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
						emailTriggerVO.setUrlReviewerId(assignToId);
						emailTriggerVO.setUrlManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
						emailTriggerVO.setAcceptByReviewerUrl(assignEditorVO.getManuscriptListVO().getAcceptByReviewerUrl());
						emailTriggerVO.setRejectByReviewerUrl(assignEditorVO.getManuscriptListVO().getRejectByReviewerUrl());
						emailTriggerVO.setViewByReviewerUrl(assignEditorVO.getManuscriptListVO().getViewByReviewerUrl());
						emailTriggerVO.setExtendDate(assignEditorVO.getManuscriptListVO().getDueDate());

						log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
								+ emailTriggerVO.toString());
						log.info("User Receiver Id:->" + assignToId);
						log.info("User Receiver Role Id:->" + assignEditorVO.getManuscriptListVO().getRoleId());
						log.info("User Sender Role Id:->" + assignEditorVO.getManuscriptListVO().getEditorRoleId());
						log.info("User Sender Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
						log.info("Task Id:->" + 4);
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
						log.info("End of assignUser Role For Reviewer For Email Trigger ");


					} else {
						ReviewerSuggested getReviewerSuggestedData = reviewerSuggestedRepo.getOne(assignToId);
						// Create User
						User getUserDatas = userRepo.findByEmail(getReviewerSuggestedData.getEmail());
						if (getUserDatas != null) {
							ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
									assignEditorVO.getManuscriptListVO().getJournalId(), assignEditorVO.getManuscriptListVO().getManuScriptId(),
									assignEditorVO.getManuscriptListVO().getManuscriptVersion(), assignEditorVO.getManuscriptListVO().getUserId());
							if (p != null) {
								p.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								manuscriptEditorRepo.save(p);
							}
							ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
									.findManuscriptReviewerDetails(assignEditorVO.getManuscriptListVO().getManuScriptId(),
											assignEditorVO.getManuscriptListVO().getManuscriptVersion(), assignEditorVO.getManuscriptListVO().getJournalId(),
											getUserDatas.getId());

							if (manuscriptReviewer != null) {
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										assignEditorVO.getManuscriptListVO().getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);
								}
								manuScriptReviewerRepo.updateReviewerDetails(assignEditorVO.getManuscriptListVO().getManuStatusId(),
										manuscriptReviewer.getId(), assignEditorVO.getManuscriptListVO().getDueDate());
							} else {
								ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
								manuscriptReviewerData.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
								manuscriptReviewerData.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
								manuscriptReviewerData.setReviewerId(getUserDatas.getId());
								manuscriptReviewerData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								manuscriptReviewerData.setEditorId(assignEditorVO.getManuscriptListVO().getUserId());
								manuscriptReviewerData.setEditorRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
								manuscriptReviewerData.setCommentsForReviewer(assignEditorVO.getManuscriptListVO().getMessage());
								manuscriptReviewerData.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								if (assignEditorVO.getManuscriptListVO().getDueDate() == null || assignEditorVO.getManuscriptListVO().getDueDate().equals("")
										|| assignEditorVO.getManuscriptListVO().getDueDate().equals("NaN-NaN-NaN")) {
									manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
											.atStartOfDay(ZoneId.systemDefault()).toInstant()));
								} else {
									manuscriptReviewerData.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
								}
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										assignEditorVO.getManuscriptListVO().getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);
								}
								manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);
							}
							if (assignEditorVO.getManuscriptListVO().getManuStatusId() == 8 || assignEditorVO.getManuscriptListVO().getManuStatusId() == 66) {
								manuscriptRepo.updateStage(assignEditorVO.getManuscriptListVO().getManuStatusId(),
										assignEditorVO.getManuscriptListVO().getManuScriptId());
								if (assignEditorVO.getManuscriptListVO().getEditorRoleId() != null
										&& assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
									manuscriptRepo.updateAssignedId(assignEditorVO.getManuscriptListVO().getManuScriptId(),
											assignEditorVO.getManuscriptListVO().getUserId(), 2);
							}

							// Email Trigger
							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
							emailTriggerVO.setUserId(getUserDatas.getId());
							emailTriggerVO.setReviewerUserId(getUserDatas.getId());

							if (assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
								emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
							else
								emailTriggerVO.setEditorUserId(assignEditorVO.getManuscriptListVO().getUserId());

							emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
							emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
							emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getRoleId());
							if (assignEditorVO.getManuscriptListVO().getTemplateId() != null) {
								emailTriggerVO.setTaskId(assignEditorVO.getManuscriptListVO().getTemplateId());
							} else {
								emailTriggerVO.setTaskId(4);
							}
							emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());

							// For The Sending Url To Reviewer
							if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody() != null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody().isEmpty())
								emailTriggerVO.setEmailTemplateBody(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody());
							if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject()!= null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject().isEmpty())
								emailTriggerVO.setEmailTemplateSubject(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject());
							emailTriggerVO.setUrlManuscript(assignEditorVO.getManuscriptListVO().getManuScriptId());
							emailTriggerVO.setUrlManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
							emailTriggerVO.setUrlJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
							emailTriggerVO.setUrlReviewerId(getUserDatas.getId());
							emailTriggerVO.setUrlManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
							emailTriggerVO.setAcceptByReviewerUrl(assignEditorVO.getManuscriptListVO().getAcceptByReviewerUrl());
							emailTriggerVO.setRejectByReviewerUrl(assignEditorVO.getManuscriptListVO().getRejectByReviewerUrl());
							emailTriggerVO.setViewByReviewerUrl(assignEditorVO.getManuscriptListVO().getViewByReviewerUrl());

							log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
							log.info("User Receiver Id:->" + getUserDatas.getId());
							log.info("User Receiver Role Id:->" + assignEditorVO.getManuscriptListVO().getRoleId());
							log.info("User Sender Role Id:->" + assignEditorVO.getManuscriptListVO().getEditorRoleId());
							log.info("User Sender Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
							log.info("Task Id:->" + 4);
							String message = emailTriggerService.emailTrigger(emailTriggerVO);
							log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
							log.info("End of assignUser Role For Reviewer For Email Trigger ");
						} else {

							// Create New Reviewer
							User createUser = new User();
							createUser.setAccountLocked(1);
							createUser.setIsActive(1);
							createUser.setSalutation(
									Enum.valueOf(Salutation.class, getReviewerSuggestedData.getSalutation()));
							createUser.setFirstName(getReviewerSuggestedData.getFirstName());
							createUser.setLastName(getReviewerSuggestedData.getLastName());
							createUser.setUsername(getReviewerSuggestedData.getEmail());
							createUser.setEmail(getReviewerSuggestedData.getEmail());
							createUser.setSpecialization(getReviewerSuggestedData.getSpecialization());
							createUser.setExpertise(getReviewerSuggestedData.getSubject());
							createUser.setCountryId(getReviewerSuggestedData.getCountry());
							String token = UUID.randomUUID().toString();
							createUser.setPassword(passwordEncoder.encode(tempPassword.trim()));
							createUser.setToken(token);
							User savedUser = userLoginRepo.save(createUser);

							// Send Reviewer Invitation For Creating Account
							ManuScript getManuscriptData = manuscriptRepo.getOne(assignEditorVO.getManuscriptListVO().getManuScriptId());
							if (getManuscriptData != null) {
								List<String> journalWebsiteUrl = new ArrayList<>();
								if (getManuscriptData.getJournal().getJournalMetadata() != null) {
									journalWebsiteUrl.add(getManuscriptData.getJournal().getJournalMetadata().getUrl());
								}

								sendMailForActivationForReviewer(getReviewerSuggestedData.getEmail(), token,
										assignEditorVO.getManuscriptListVO().getSendEmailUrl(), getReviewerSuggestedData.getSalutation(),
										getReviewerSuggestedData.getFirstName(), getReviewerSuggestedData.getLastName(),
										getManuscriptData.getManuscriptTitle(),
										getManuscriptData.getJournal().getJournalTitle(),
										getManuscriptData.getJournal().getJournalId(),
										getManuscriptData.getJournal().getJournalCode(), journalWebsiteUrl);
							}

							if (savedUser != null) {
								ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
										assignEditorVO.getManuscriptListVO().getJournalId(), assignEditorVO.getManuscriptListVO().getManuScriptId(),
										assignEditorVO.getManuscriptListVO().getManuscriptVersion(), assignEditorVO.getManuscriptListVO().getUserId());
								if (p != null) {
									p.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
									manuscriptEditorRepo.save(p);
								}

								// Now Assign
								ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
										.findManuscriptReviewerDetails(assignEditorVO.getManuscriptListVO().getManuScriptId(),
												assignEditorVO.getManuscriptListVO().getManuscriptVersion(),
												assignEditorVO.getManuscriptListVO().getJournalId(), savedUser.getId());

								if (manuscriptReviewer != null) {
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											assignEditorVO.getManuscriptListVO().getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuScriptReviewerRepo.updateReviewerDetails(assignEditorVO.getManuscriptListVO().getManuStatusId(),
											manuscriptReviewer.getId(), assignEditorVO.getManuscriptListVO().getDueDate());
								} else {
									ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
									manuscriptReviewerData.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
									manuscriptReviewerData
											.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
									manuscriptReviewerData.setReviewerId(savedUser.getId());
									manuscriptReviewerData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
									manuscriptReviewerData.setEditorId(assignEditorVO.getManuscriptListVO().getUserId());
									manuscriptReviewerData.setEditorRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
									manuscriptReviewerData.setCommentsForReviewer(assignEditorVO.getManuscriptListVO().getMessage());
									manuscriptReviewerData.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
									if (assignEditorVO.getManuscriptListVO().getDueDate() == null
											|| assignEditorVO.getManuscriptListVO().getDueDate().equals("")
											|| assignEditorVO.getManuscriptListVO().getDueDate().equals("NaN-NaN-NaN"))
										manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
												.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									else
										manuscriptReviewerData.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											assignEditorVO.getManuscriptListVO().getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);
								}
								if (assignEditorVO.getManuscriptListVO().getManuStatusId() == 8
										|| assignEditorVO.getManuscriptListVO().getManuStatusId() == 66) {
									ManuScript getmanus = manuscriptRepo.getOne(assignEditorVO.getManuscriptListVO().getManuScriptId());
									if (getmanus != null) {
										getmanus.setManuscriptStatusId(assignEditorVO.getManuscriptListVO().getManuStatusId());
										manuscriptRepo.save(getmanus);
									}

								}

								// Email Trigger
								EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
								emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								emailTriggerVO.setUserId(savedUser.getId());
								emailTriggerVO.setReviewerUserId(savedUser.getId());

								if (assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
									emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
								else
									emailTriggerVO.setEditorUserId(assignEditorVO.getManuscriptListVO().getUserId());

								emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
								emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getRoleId());
								if (assignEditorVO.getManuscriptListVO().getTemplateId() != null) {
									emailTriggerVO.setTaskId(assignEditorVO.getManuscriptListVO().getTemplateId());
								} else {
									emailTriggerVO.setTaskId(4);
								}
								emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());

								// For The Sending Url To Reviewer
								if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody() != null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody().isEmpty())
									emailTriggerVO.setEmailTemplateBody(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody());
								if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject()!= null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject().isEmpty())
									emailTriggerVO.setEmailTemplateSubject(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject());
								emailTriggerVO.setUrlManuscript(assignEditorVO.getManuscriptListVO().getManuScriptId());
								emailTriggerVO.setUrlManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
								emailTriggerVO.setUrlJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								emailTriggerVO.setUrlReviewerId(savedUser.getId());
								emailTriggerVO.setUrlManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								emailTriggerVO.setAcceptByReviewerUrl(assignEditorVO.getManuscriptListVO().getAcceptByReviewerUrl());
								emailTriggerVO.setRejectByReviewerUrl(assignEditorVO.getManuscriptListVO().getRejectByReviewerUrl());
								emailTriggerVO.setViewByReviewerUrl(assignEditorVO.getManuscriptListVO().getViewByReviewerUrl());

								log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
										+ emailTriggerVO.toString());
								log.info("User Receiver Id:->" + savedUser.getId());
								log.info("User Receiver Role Id:->" + assignEditorVO.getManuscriptListVO().getRoleId());
								log.info("User Sender Role Id:->" + assignEditorVO.getManuscriptListVO().getEditorRoleId());
								log.info("User Sender Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
								log.info("Task Id:->" + 4);
								String message = emailTriggerService.emailTrigger(emailTriggerVO);
								log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
								log.info("End of assignUser Role For Reviewer For Email Trigger ");

							}
						}

					}
				}
			} else if (assignEditorVO.getManuscriptListVO().getReviewerSuggestedVO() != null
					&& !assignEditorVO.getManuscriptListVO().getReviewerSuggestedVO().isEmpty()) {
				String authorEmail  = manuscriptRepo.getAuthorEmail(assignEditorVO.getManuscriptListVO().getManuScriptId());
				List<String> coAuthorEmailList = manuScriptCoAuthorRepo.getCoAuthorEmail(assignEditorVO.getManuscriptListVO().getManuScriptId());
				coAuthorEmailList.add(authorEmail);
				int j = 0;
				for (ReviewerSuggestedVO i : assignEditorVO.getManuscriptListVO().getReviewerSuggestedVO()) {
					if (i.getEmail() != null && !i.getEmail().isEmpty() && !coAuthorEmailList.contains(i.getEmail())) {
						User getUserDatas = userRepo.findByEmail(i.getEmail());
						if (getUserDatas != null) {
							ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
									assignEditorVO.getManuscriptListVO().getJournalId(), assignEditorVO.getManuscriptListVO().getManuScriptId(),
									assignEditorVO.getManuscriptListVO().getManuscriptVersion(), assignEditorVO.getManuscriptListVO().getUserId());
							if (p != null) {
								p.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								manuscriptEditorRepo.save(p);
							}
							ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
									.findManuscriptReviewerDetails(assignEditorVO.getManuscriptListVO().getManuScriptId(),
											assignEditorVO.getManuscriptListVO().getManuscriptVersion(), assignEditorVO.getManuscriptListVO().getJournalId(),
											getUserDatas.getId());

							if (manuscriptReviewer != null) {
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										assignEditorVO.getManuscriptListVO().getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);


								}
								manuScriptReviewerRepo.updateReviewerDetails(assignEditorVO.getManuscriptListVO().getManuStatusId(),
										manuscriptReviewer.getId(), assignEditorVO.getManuscriptListVO().getDueDate());
							} else {
								ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
								manuscriptReviewerData.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
								manuscriptReviewerData.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
								manuscriptReviewerData.setReviewerId(getUserDatas.getId());
								manuscriptReviewerData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								manuscriptReviewerData.setEditorId(assignEditorVO.getManuscriptListVO().getUserId());
								manuscriptReviewerData.setEditorRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
								manuscriptReviewerData.setCommentsForReviewer(assignEditorVO.getManuscriptListVO().getMessage());
								manuscriptReviewerData.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								if (assignEditorVO.getManuscriptListVO().getDueDate() == null || assignEditorVO.getManuscriptListVO().getDueDate().equals("")
										|| assignEditorVO.getManuscriptListVO().getDueDate().equals("NaN-NaN-NaN")) {
									manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
											.atStartOfDay(ZoneId.systemDefault()).toInstant()));
								}
								else {
									manuscriptReviewerData.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
								}
								if(manuscriptReviewerData.getDueDate()==null)
								{
									manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
											.atStartOfDay(ZoneId.systemDefault()).toInstant()));
								}
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										assignEditorVO.getManuscriptListVO().getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);
								}
								manuscriptReviewerData.setIsActive(1);
								manuScriptReviewerRepo.save(manuscriptReviewerData);
								ManuScriptReviewer manuScriptReviewer=manuScriptReviewerRepo
										.findManuscriptReviewerDetails(assignEditorVO.getManuscriptListVO().getManuScriptId(),
												assignEditorVO.getManuscriptListVO().getManuscriptVersion(),assignEditorVO.getManuscriptListVO().getJournalId(),
												getUserDatas.getId());

							}
							if (assignEditorVO.getManuscriptListVO().getManuStatusId() == 8 || assignEditorVO.getManuscriptListVO().getManuStatusId() == 66) {
//								manuscriptRepo.updateStage(assignEditorVO.getManuscriptListVO().getManuStatusId(),
//										assignEditorVO.getManuscriptListVO().getManuScriptId());
								// Added code By Utkarsh
								ManuScript manuscript= manuscriptRepo.getById(assignEditorVO.getManuscriptListVO().getManuScriptId());
								if(assignEditorVO.getManuscriptListVO().getManuStatusId()==8)
								{
									manuscript.setManuscriptStatusId(8);
								} else{
									manuscript.setManuscriptStatusId(66);
								}
								manuscriptRepo.save(manuscript);
								if (assignEditorVO.getManuscriptListVO().getEditorRoleId() != null
										&& assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
									manuscriptRepo.updateAssignedId(assignEditorVO.getManuscriptListVO().getManuScriptId(),
											assignEditorVO.getManuscriptListVO().getUserId(), 2);
							}


							//earlier code
//							ReviewerSuggested authorSuggestedReviewer = reviewerSuggestedRepo.findByEmailId(i.getEmail());

							//changed code by Utkarsh
							List<ReviewerSuggested> authorSuggestedReviewer = reviewerSuggestedRepo.findByEmailIdList(i.getEmail());
							boolean status =false;
							if(!authorSuggestedReviewer.isEmpty()) {
								//Authorities History
								status=true;
								AuthoritiesHistoryVo add = new AuthoritiesHistoryVo();
								add.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
								add.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
								add.setManuscriptStatusId(assignEditorVO.getManuscriptListVO().getManuStatusId());
								add.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								List<Integer> id = new ArrayList<>();
								id.add(getUserDatas.getId());
								add.setCurrentUserId(id);
								add.setCurrentUserRole(5);
								add.setPreviousUserId(assignEditorVO.getManuscriptListVO().getUserId());
								add.setPreviousUserRole(assignEditorVO.getManuscriptListVO().getEditorRoleId());
								add.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
								String result = authoritiesHistoryService.authoritiesHistory(add);
								log.info("Authorities:->" + result);
							}

							// Added code By Utkarsh
							ManuScriptReviewer manuScriptReviewer=manuScriptReviewerRepo
									.findManuscriptReviewerDetails(assignEditorVO.getManuscriptListVO().getManuScriptId(),
											assignEditorVO.getManuscriptListVO().getManuscriptVersion(),assignEditorVO.getManuscriptListVO().getJournalId(),
											getUserDatas.getId());
							if(manuScriptReviewer!=null && !status)
							{
								//Authorities History
								AuthoritiesHistoryVo add = new AuthoritiesHistoryVo();
								add.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
								add.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
								add.setManuscriptStatusId(assignEditorVO.getManuscriptListVO().getManuStatusId());
								add.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								List<Integer> id = new ArrayList<>();
								id.add(getUserDatas.getId());
								add.setCurrentUserId(id);
								add.setCurrentUserRole(5);
								add.setPreviousUserId(assignEditorVO.getManuscriptListVO().getUserId());
								add.setPreviousUserRole(assignEditorVO.getManuscriptListVO().getEditorRoleId());
								add.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
								String result = authoritiesHistoryService.authoritiesHistory(add);
								log.info("Authorities:->" + result);
							}

							// Email Trigger
							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
							emailTriggerVO.setUserId(getUserDatas.getId());
							emailTriggerVO.setReviewerUserId(getUserDatas.getId());
							if (assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
								emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
							else
								emailTriggerVO.setEditorUserId(assignEditorVO.getManuscriptListVO().getUserId());

							emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
							emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
							emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getRoleId());
							if (assignEditorVO.getManuscriptListVO().getTemplateId() != null) {
								emailTriggerVO.setTaskId(assignEditorVO.getManuscriptListVO().getTemplateId());
							} else {
								emailTriggerVO.setTaskId(4);
							}
							emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());

							// For The Sending Url To Reviewer
							emailTriggerVO.setUrlManuscript(assignEditorVO.getManuscriptListVO().getManuScriptId());
							emailTriggerVO.setUrlManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
							emailTriggerVO.setUrlJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
							emailTriggerVO.setUrlReviewerId(getUserDatas.getId());
							emailTriggerVO.setUrlManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
							emailTriggerVO.setAcceptByReviewerUrl(assignEditorVO.getManuscriptListVO().getAcceptByReviewerUrl());
							emailTriggerVO.setRejectByReviewerUrl(assignEditorVO.getManuscriptListVO().getRejectByReviewerUrl());
							emailTriggerVO.setViewByReviewerUrl(assignEditorVO.getManuscriptListVO().getViewByReviewerUrl());
							if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody() != null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody().isEmpty())
								emailTriggerVO.setEmailTemplateBody(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody());
							if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject()!= null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject().isEmpty())
								emailTriggerVO.setEmailTemplateSubject(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject());

							log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
							log.info("User Receiver Id:->" + getUserDatas.getId());
							log.info("User Receiver Role Id:->" + assignEditorVO.getManuscriptListVO().getRoleId());
							log.info("User Sender Role Id:->" + assignEditorVO.getManuscriptListVO().getEditorRoleId());
							log.info("User Sender Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
							log.info("Task Id:->" + 4);
							String message = emailTriggerService.emailTrigger(emailTriggerVO);
							log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
							log.info("End of assignUser Role For Reviewer For Email Trigger ");
						} else {
							// Create New Reviewer
							User createUser = new User();
							createUser.setAccountLocked(1);
							createUser.setIsActive(1);
							createUser.setSalutation(Enum.valueOf(Salutation.class, i.getSalutation()));
							createUser.setFirstName(i.getFirstname());
							createUser.setLastName(i.getLastname());
							createUser.setUsername(i.getEmail());
							createUser.setSpecialization(i.getSpecialization());
							createUser.setExpertise(i.getSubject());
							if (i.getCountry() != null && !i.getCountry().isEmpty()) {
								Countries countries = countriesService.getCountryByName(i.getCountry().trim());
								if (countries != null) {
									createUser.setCountryId(countries.getId());
								}
							}
							createUser.setEmail(i.getEmail());
							String token = UUID.randomUUID().toString();
							createUser.setPassword(passwordEncoder.encode(tempPassword.trim()));
							createUser.setToken(token);
							User savedUser = userLoginRepo.save(createUser);

							// Send Reviewer Invitation For Creating Account
							ManuScript getManuscriptData = manuscriptRepo.getOne(assignEditorVO.getManuscriptListVO().getManuScriptId());
							if (getManuscriptData != null) {
								List<String> journalWebsiteUrl = new ArrayList<>();
								if (getManuscriptData.getJournal().getJournalMetadata() != null) {
									journalWebsiteUrl.add(getManuscriptData.getJournal().getJournalMetadata().getUrl());
								}

								sendMailForActivationForReviewer(i.getEmail(), token,
										assignEditorVO.getManuscriptListVO().getSendEmailUrl(), i.getSalutation(), i.getFirstname(),
										i.getLastname(), getManuscriptData.getManuscriptTitle(),
										getManuscriptData.getJournal().getJournalTitle(),
										getManuscriptData.getJournal().getJournalId(),
										getManuscriptData.getJournal().getJournalCode(), journalWebsiteUrl);
							}

							if (savedUser != null) {
								ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
										assignEditorVO.getManuscriptListVO().getJournalId(), assignEditorVO.getManuscriptListVO().getManuScriptId(),
										assignEditorVO.getManuscriptListVO().getManuscriptVersion(), assignEditorVO.getManuscriptListVO().getUserId());
								if (p != null) {
									p.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
									manuscriptEditorRepo.save(p);
								}

								// Now Assign
								ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
										.findManuscriptReviewerDetails(assignEditorVO.getManuscriptListVO().getManuScriptId(),
												assignEditorVO.getManuscriptListVO().getManuscriptVersion(),
												assignEditorVO.getManuscriptListVO().getJournalId(), savedUser.getId());

								if (manuscriptReviewer != null) {
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											assignEditorVO.getManuscriptListVO().getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuScriptReviewerRepo.updateReviewerDetails(assignEditorVO.getManuscriptListVO().getManuStatusId(),
											manuscriptReviewer.getId(), assignEditorVO.getManuscriptListVO().getDueDate());
								} else {
									ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
									manuscriptReviewerData.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
									manuscriptReviewerData
											.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
									manuscriptReviewerData.setReviewerId(savedUser.getId());
									manuscriptReviewerData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
									manuscriptReviewerData.setEditorId(assignEditorVO.getManuscriptListVO().getUserId());
									manuscriptReviewerData.setEditorRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());
									manuscriptReviewerData.setCommentsForReviewer(assignEditorVO.getManuscriptListVO().getMessage());
									manuscriptReviewerData.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
									if (assignEditorVO.getManuscriptListVO().getDueDate() == null
											|| assignEditorVO.getManuscriptListVO().getDueDate().equals("")
											|| assignEditorVO.getManuscriptListVO().getDueDate().equals("NaN-NaN-NaN"))
										manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
												.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									else
										manuscriptReviewerData.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											assignEditorVO.getManuscriptListVO().getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);


								}
								if (assignEditorVO.getManuscriptListVO().getManuStatusId() == 8
										|| assignEditorVO.getManuscriptListVO().getManuStatusId() == 66) {
									manuscriptRepo.updateStage(assignEditorVO.getManuscriptListVO().getManuStatusId(),
											assignEditorVO.getManuscriptListVO().getManuScriptId());
									if (assignEditorVO.getManuscriptListVO().getEditorRoleId() != null
											&& assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
										manuscriptRepo.updateAssignedId(assignEditorVO.getManuscriptListVO().getManuScriptId(),
												assignEditorVO.getManuscriptListVO().getUserId(), 2);
								}

								//Authorities History
								AuthoritiesHistoryVo add = new AuthoritiesHistoryVo();
								add.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
								add.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
								add.setManuscriptStatusId(assignEditorVO.getManuscriptListVO().getManuStatusId());
								add.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								List<Integer> id = new ArrayList<>();
								id.add(savedUser.getId());
								add.setCurrentUserId(id);
								add.setCurrentUserRole(5);
								add.setPreviousUserId(assignEditorVO.getManuscriptListVO().getUserId());
								add.setPreviousUserRole(assignEditorVO.getManuscriptListVO().getEditorRoleId());
								add.setDueDate(assignEditorVO.getManuscriptListVO().getDueDate());
								String result = authoritiesHistoryService.authoritiesHistory(add);
								log.info("Authorities:->" + result);

								// Email Trigger
								EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
								emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								emailTriggerVO.setUserId(savedUser.getId());
								emailTriggerVO.setReviewerUserId(savedUser.getId());
								if (assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2))
									emailTriggerVO.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
								else
									emailTriggerVO.setEditorUserId(assignEditorVO.getManuscriptListVO().getUserId());

								emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
								emailTriggerVO.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								emailTriggerVO.setReceiverRoleId(assignEditorVO.getManuscriptListVO().getRoleId());
								if (assignEditorVO.getManuscriptListVO().getTemplateId() != null) {
									emailTriggerVO.setTaskId(assignEditorVO.getManuscriptListVO().getTemplateId());
								} else {
									emailTriggerVO.setTaskId(4);
								}
								emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getEditorRoleId());

								// For The Sending Url To Reviewer
								emailTriggerVO.setUrlManuscript(assignEditorVO.getManuscriptListVO().getManuScriptId());
								emailTriggerVO.setUrlManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
								emailTriggerVO.setUrlJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
								emailTriggerVO.setUrlReviewerId(savedUser.getId());
								emailTriggerVO.setUrlManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
								emailTriggerVO.setAcceptByReviewerUrl(assignEditorVO.getManuscriptListVO().getAcceptByReviewerUrl());
								emailTriggerVO.setRejectByReviewerUrl(assignEditorVO.getManuscriptListVO().getRejectByReviewerUrl());
								emailTriggerVO.setViewByReviewerUrl(assignEditorVO.getManuscriptListVO().getViewByReviewerUrl());
								if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody() != null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody().isEmpty())
									emailTriggerVO.setEmailTemplateBody(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesBody());
								if (assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject()!= null && !assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject().isEmpty())
									emailTriggerVO.setEmailTemplateSubject(assignEditorVO.getManuscriptListVO().getJournalMailTemplatesSubject());

								log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
										+ emailTriggerVO.toString());
								log.info("User Receiver Id:->" + savedUser.getId());
								log.info("User Receiver Role Id:->" + assignEditorVO.getManuscriptListVO().getRoleId());
								log.info("User Sender Role Id:->" + assignEditorVO.getManuscriptListVO().getEditorRoleId());
								log.info("User Sender Id:->" + assignEditorVO.getManuscriptListVO().getUserId());
								log.info("Task Id:->" + 4);
								String message = emailTriggerService.emailTrigger(emailTriggerVO);
								log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
								log.info("End of assignUser Role For Reviewer For Email Trigger ");

							}

						}
						j++;
					}
				}

				if(j==0) {
					throw new Exception("Author/Co-author not become reviewer.");
				}
			}


		}
		else if (assignEditorVO.getManuscriptListVO().getRoleId() == 3) {
			ManuScript manu = manuscriptRepo.getFindByManuScriptDetail(assignEditorVO.getManuscriptListVO().getManuScriptId());
			if (assignEditorVO.getManuscriptListVO().getAssignToID() != null && !assignEditorVO.getManuscriptListVO().getAssignToID().isEmpty()) {
				if(assignEditorVO.getManuscriptListVO().getEIC_Folder() != null && (assignEditorVO.getManuscriptListVO().getEIC_Folder().equalsIgnoreCase("ReviewedManuscript") || assignEditorVO.getManuscriptListVO().getEIC_Folder().equalsIgnoreCase("ReviewersInvited") || assignEditorVO.getManuscriptListVO().getEIC_Folder().equalsIgnoreCase("ManuscriptInReview"))) {
					List<ManuScriptReviewer> listRev = manuScriptReviewerRepo.getmanuScriptReviewer(assignEditorVO.getManuscriptListVO().getManuScriptId(), assignEditorVO.getManuscriptListVO().getManuscriptVersion(),assignEditorVO.getManuscriptListVO().getJournalId());
					for (ManuScriptReviewer manuScriptReviewer : listRev) {
						if(manuScriptReviewer.getManuscriptStatus().equals(8) || manuScriptReviewer.getManuscriptStatus().equals(66) || manuScriptReviewer.getManuscriptStatus().equals(35)) {
							manuScriptReviewer.setIsActive(0);
							manuScriptReviewerRepo.save(manuScriptReviewer);

							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
							emailTriggerVO.setUserId(manuScriptReviewer.getReviewerId());
							emailTriggerVO.setReviewerUserId(manuScriptReviewer.getReviewerId());
							emailTriggerVO.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
							emailTriggerVO.setTaskId(87);
							emailTriggerVO.setReceiverRoleId(5);
							emailTriggerVO.setEditorUserId(manu.getAssignedUser());
							emailTriggerVO.setRoleId(assignEditorVO.getManuscriptListVO().getRoleId());
							emailTriggerVO.setManuscriptStatus(30);
							String message = emailTriggerService.emailTrigger(emailTriggerVO);
							log.info(" Email sent to Reviewer who has not reviewed the manuscript " + message);
						}
					}
				}
				assignEditorVO.getManuscriptListVO().getAssignToID().stream().forEach(i -> {

					List<ManuscriptEditor> data = manuscriptEditorRepo.getByVersion(
							assignEditorVO.getManuscriptListVO().getManuScriptId(), assignEditorVO.getManuscriptListVO().getManuscriptVersion());
					if (data != null && !data.isEmpty()) {
						for (ManuscriptEditor manuscriptEditor2 : data) {
							manuscriptEditor2.setIsActive(0);
							manuscriptEditorRepo.save(manuscriptEditor2);
						}
					}
					ManuscriptEditor manuscriptEditorData = new ManuscriptEditor();
					manuscriptEditorData.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
					manuscriptEditorData.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
					if(assignEditorVO.getManuscriptListVO().getEditorRoleId().equals(2)) {
						manuscriptEditorData.setManagingEditorId(assignEditorVO.getManuscriptListVO().getUserId());
					}else {
						manuscriptEditorData.setManagingEditorId(authoritiesHistoryRepo.getManagingEditorId(assignEditorVO.getManuscriptListVO().getManuScriptId(), assignEditorVO.getManuscriptListVO().getJournalId()));
					}
					manuscriptEditorData.setEditorId(i);
					manuscriptEditorData.setIsActive(1);
					manuscriptEditorData.setSendBackEic(0);
					manuscriptEditorData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
					manuscriptEditorData.setCommentsForEditor(assignEditorVO.getManuscriptListVO().getMessage());
					manuscriptEditorData.setAssignedDate(new Date());
					manuscriptEditorData.setManuscriptStatus(assignEditorVO.getManuscriptListVO().getManuStatusId());
					manuscriptEditorRepo.save(manuscriptEditorData);

					if (assignEditorVO.getManuscriptListVO().getManuStatusId() == 3) {
						manu.setManuscriptStatusId(assignEditorVO.getManuscriptListVO().getManuStatusId());
						manu.setAssignedUser(i);
						manu.setAssignedUserRole(3);
						assignEditorVO.getManuscriptListVO().setEIC_Folder(manu.getEICFolder());
						manu.setEICFolder(assignEditorVO.getManuscriptListVO().getEIC_Folder());
						manuscriptRepo.save(manu);
					}
					
					try {
				        ManuscriptEditorComment addManuscriptEditorComment = new ManuscriptEditorComment();
	
				        // ✅ File upload handling (optional file, mandatory comment)
				        if (file != null && !file.isEmpty()) {
				            log.info("-------- File upload detected --------");
	
				            String attachmentFolderPath = UPLOAD_FOLDER + File.separator + "UniprrAssignEditorFolder" + File.separator
				                    + "MNT_LWW_JOURNAL_" + "R" + assignEditorVO.getManuscriptListVO().getManuscriptVersion() + "_"
				                    + assignEditorVO.getManuscriptListVO().getJournalId() + "_" + assignEditorVO.getManuscriptListVO().getManuScriptId();
	
				            File dir = new File(attachmentFolderPath);
				            if (!dir.exists()) {
				                dir.mkdirs();
				            }
	
				            // save file
				            byte[] bytes = file.getBytes();
				            File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
				            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
				                stream.write(bytes);
				            }
	
				            // file size
				            double fileSizeinBytes = file.getSize();
				            double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
	
				            Integer port = request.getServerPort();
				            InetAddress inetAddress = InetAddress.getLocalHost();
				            String fileServerPath;
	
				            if ("AWS".equalsIgnoreCase(environment.getProperty("server.status"))) {
				                String Ip = "https://" + environment.getProperty("awsFileIp");
				                fileServerPath = Ip + File.separator + "UniprrAssignEditorFolder" + File.separator
				                        + "MNT_LWW_JOURNAL_" + "R" + assignEditorVO.getManuscriptListVO().getManuscriptVersion() + "_"
				                        + assignEditorVO.getManuscriptListVO().getJournalId() + "_" + assignEditorVO.getManuscriptListVO().getManuScriptId()
				                        + File.separator + file.getOriginalFilename();
				            } else {
				                String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
				                fileServerPath = Ip + File.separator + "UniprrAssignEditorFolder" + File.separator
				                        + "MNT_LWW_JOURNAL_" + "R" + assignEditorVO.getManuscriptListVO().getManuscriptVersion() + "_"
				                        + assignEditorVO.getManuscriptListVO().getJournalId() + "_" + assignEditorVO.getManuscriptListVO().getManuScriptId()
				                        + File.separator + file.getOriginalFilename();
				            }
	
				            addManuscriptEditorComment.setFilePath(fileServerPath);
				            addManuscriptEditorComment.setFileSize((int) fileSizeInKB);
				            addManuscriptEditorComment.setFileName(file.getOriginalFilename());
				            addManuscriptEditorComment.setFileType("Assign Editor");
				        }
	
	
				        // ✅ Save editor comment
				        addManuscriptEditorComment.setStatusId(3);
				        addManuscriptEditorComment.setCreatedAt(new Date());
				        addManuscriptEditorComment.setManuscriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
				        addManuscriptEditorComment.setManuscriptVersion(assignEditorVO.getManuscriptListVO().getManuscriptVersion());
				        addManuscriptEditorComment.setUserId(i);
				        addManuscriptEditorComment.setRoleId(3);
				        addManuscriptEditorComment.setComment(assignEditorVO.getManuscriptListVO().getMessage());
	
				        manuscriptEditorCommentRepo.save(addManuscriptEditorComment);
	
				        log.info("-------- Assign Editor Comment saved successfully --------");
				
					}catch (Exception e) {
						log.error("❌ Error while uploading file: {}", e.getMessage(), e);
					}
			    
					UserJournal uj = userJournalRepo.getUserJournal(i, assignEditorVO.getManuscriptListVO().getJournalId(), 3);
					if (uj != null) {
						uj.setIsActive(1);
						userJournalRepo.save(uj);
					} else {
						UserJournal userJournal = new UserJournal();
						userJournal.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
						userJournal.setUserId(i);
						userJournal.setUpdatedBy(assignEditorVO.getManuscriptListVO().getUserId());
						userJournal.setIsActive(1);
						userJournal.setRoleId(3);
						userJournalRepo.save(userJournal);
					}
				});
			}
		} else {
			ManuScript manuscriptData = new ManuScript();
			manuscriptData.setManuScriptId(assignEditorVO.getManuscriptListVO().getManuScriptId());
			manuscriptData.setJournalId(assignEditorVO.getManuscriptListVO().getJournalId());
			manuscriptService.saveManuscriptDetails(manuscriptData);
		}

	}
	
	public String sendMailForActivationForReviewer(String userEmail, String token1, String activationUrl,
			   String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
			   Integer journalId, String journalCode, List<String> journalWebsiteUrl) {
String token = UUID.randomUUID().toString();
userRepo.updateTokenByUser(userEmail, token);
String fromMailID = env.getProperty("spring.mail.username");
String toMailID = userEmail;
String mailSubject = "Reviewer account username and password creation link";
String mailBody = activateUserMailTemplateForReviewer(userEmail, token, activationUrl, salutation, firstName,
lastName, manuscriptTitle, journalTitle, journalId, journalCode, journalWebsiteUrl);
Journal i = journalsRepo.getOne(journalId);
OtherMail add = new OtherMail();
add.setJournalId(journalId);
add.setToMail(toMailID);
add.setFromMail(fromMailID);
add.setSubject(mailSubject);
add.setMailBody(mailBody);
if (i != null && i.getJournalMetadata() != null) {
if (i.getJournalMetadata().getJournalEmail() != null
&& !i.getJournalMetadata().getJournalEmail().isEmpty()) {

MailQueue addMailQueue = new MailQueue();
addMailQueue.setMailFrom(fromMailID);
addMailQueue.setMailTo(toMailID);
addMailQueue.setJournalId(journalId);
addMailQueue.setManuscriptId(0);
addMailQueue.setMailSubject(mailSubject);
addMailQueue.setMailBody(mailBody);
addMailQueue.setMail_send_date(new Date());
addMailQueue.setIsActive(0);
mailQueueRepo.saveAndFlush(addMailQueue);

add.setStatus("Insert into mailQueue");
add.setCreatedAt(new Date());
otherMailRepo.save(add);

} else {

MailQueue addMailQueue = new MailQueue();
addMailQueue.setMailFrom(fromMailID);
addMailQueue.setMailTo(toMailID);
addMailQueue.setJournalId(journalId);
addMailQueue.setManuscriptId(0);
addMailQueue.setMailSubject(mailSubject);
addMailQueue.setMailBody(mailBody);
addMailQueue.setMail_send_date(new Date());
addMailQueue.setIsActive(0);
mailQueueRepo.saveAndFlush(addMailQueue);

add.setStatus("Insert into mailQueue");
add.setCreatedAt(new Date());
otherMailRepo.save(add);
}
} else {

MailQueue addMailQueue = new MailQueue();
addMailQueue.setMailFrom(fromMailID);
addMailQueue.setMailTo(toMailID);
addMailQueue.setJournalId(journalId);
addMailQueue.setManuscriptId(0);
addMailQueue.setMailSubject(mailSubject);
addMailQueue.setMailBody(mailBody);
addMailQueue.setMail_send_date(new Date());
addMailQueue.setIsActive(0);
mailQueueRepo.saveAndFlush(addMailQueue);

add.setStatus("Insert into mailQueue");
add.setCreatedAt(new Date());
otherMailRepo.save(add);

}
String message = "Password reset URL send on mail successfully.";
return message;
}
	
	private String activateUserMailTemplateForReviewer(String email, String token, String activationUrl,
			   String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
			   Integer journalId, String journalCode, List<String> journalWebsiteUrl) {
StringBuilder builder = new StringBuilder();
String resetUrl = activationUrl;
String resetPasswordURL = null;
Journal detailsOfJournal =journalsRepo.getJournalById(journalId);
if(detailsOfJournal !=null) {
builder.append("<p> Dear " + salutation + "." + lastName + "</p>");
builder.append("<p></p>");
builder.append("<p>Greetings!</p>");
builder.append("<p></p>");
builder.append("<p>We are pleased to add you as a reviewer for the journal “"+ detailsOfJournal.getJournalTitle()+"” on our new manuscript management application,</p>");
builder.append("<p>Review JOW: <a href=" +"https://review.jow.medknow.com/"+ detailsOfJournal.getJournalCode() + "  target=\"blank\">"+"https://review.jow.medknow.com/"+ detailsOfJournal.getJournalCode() + "</a></p>");
builder.append("<p></p>");
builder.append("<p>Your Username is: "+email+"</p>");
builder.append("<p>Your Default Password is: "+tempPassword.trim()+"</p>");
builder.append("<p></p>");
builder.append("<p>Please click on the following link or copy and paste the link in your browser to create a new password for your account.</p>");
builder.append("<p></p>");
resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
+ "&journalCode=" + detailsOfJournal.getJournalCode();
builder.append("<p> (<a href=" + resetPasswordURL + "  target=\"blank\">"+ resetPasswordURL + "</a>)</p>");
builder.append("<p></p>");
builder.append("<p> If you have any questions or concerns regarding log in or accessing your account, kindly write an email to our techsupport team at</p>");
builder.append("<p>wkhlrpmedknow_techsupport@wolterskluwer.com. We shall be obliged to help you.</p>");
builder.append("<p></p>");
builder.append("<p>Regards,</p>");
builder.append("<p></p>");
builder.append("<p> " + detailsOfJournal.getJournalTitle() + "</p>");
}
builder.append("<p>Wolters Kluwer - Medknow</p>");
return builder.toString();
}

//private String activateUserMailTemplateForReviewer(String email, String token, String activationUrl,
//String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
//Integer journalId, String journalCode, List<String> journalWebsiteUrl) {
//StringBuilder builder = new StringBuilder();
//String resetUrl = activationUrl;
//String resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
//+ "&journalCode=" + journalCode;
//builder.append("<p> Dear " + salutation + "." + lastName + "</p>");
//builder.append("<p> Greetings! </p>");
//
//builder.append("<p>You have been invited to " + journalTitle + " to review a manuscript entitled "
//+ manuscriptTitle + " </p>");
//
//builder.append(
//"We request you to confirm your account on our new manuscript management application, Review JOW: "
//+ journalWebsiteUrl.get(0) + "");
//
//builder.append("<p> Your Username is :" + email + "," + "</p>");
//
//builder.append(
//"<p>Please click on the following link or copy and paste the link in your browser to activate your account and set a new password to log in.</p>");
//
//builder.append("<p>LINK:</p>");
//builder.append("<p>(<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
//
//builder.append(
//"<p> If you have any questions or concerns regarding log in or accessing your account, kindly write an email to our WK|Medknow Tech Support ID � wkhlrpmedknow_techsupport@wolterskluwer.com.\r\n"
//+ "We shall be obliged to help you.\r\n" + "</p>");
//
//builder.append("<p>Regards,</p>");
//builder.append("<p>" + journalTitle + "</p>");
//builder.append("<p>WK| Medknow Publications</p>");
//return builder.toString();
//}

public String sendMailForActivation(String userEmail, String token) {

userRepo.updateTokenByUser(userEmail, token);

String fromMailID = env.getProperty("spring.mail.username");
String toMailID = userEmail;
String mailSubject = "Activate your account";
String mailBody = activateUserMailTemplate(userEmail, token);

MailQueue addMailQueue = new MailQueue();
addMailQueue.setMailFrom(fromMailID);
addMailQueue.setMailTo(toMailID);
addMailQueue.setJournalId(259);
addMailQueue.setManuscriptId(0);
addMailQueue.setMailSubject(mailSubject);
addMailQueue.setMailBody(mailBody);
addMailQueue.setMail_send_date(new Date());
addMailQueue.setIsActive(0);
mailQueueRepo.saveAndFlush(addMailQueue);

// mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");

String message = "Password reset URL send on mail successfully.";

return message;
}


	private String activateUserMailTemplate(String email, String token) {
	
		StringBuilder builder = new StringBuilder();
		String resetUrl = env.getProperty("localUrl") + "/signup/userverfication";
		String resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email;
		builder.append("<p> Dear User, </p>");
		builder.append("<p> Please activate your account <a href=" + resetPasswordURL + "  target=\"blank\">"
				+ resetPasswordURL + "</a>\"</p>");
		builder.append("<p> THANK YOU !! </p>");
		return builder.toString();
	}

	public void create(AssignEditorVO assignEditorVO) {
		Journal journalData = journalsRepo.getOne(assignEditorVO.getEmailTriggerVO().getJournalId());
		// journalData.getJournalMetadata().getWorkflowId();
	
		ManuScript getManuScriptDetails = manuScriptRepo.getById(assignEditorVO.getEmailTriggerVO().getManuscriptId());
	//WorkFlow Trigger
		if (journalData.getJournalMetadata().getWorkflowId() != null) {
			List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo
					.getByWorkflowId(journalData.getJournalMetadata().getWorkflowId());
			if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
				listOfWorkflowRoleSeq.stream().forEach(i -> {
					tatEmailConfigurationRepo.updateTatEmailConfiguration(assignEditorVO.getEmailTriggerVO().getJournalId(),
							assignEditorVO.getEmailTriggerVO().getManuscriptId(), getManuScriptDetails.getManuscriptVersion(),
							assignEditorVO.getEmailTriggerVO().getUserId());
					if (i.getTaskId().equals(assignEditorVO.getEmailTriggerVO().getTaskId())) {
						TatEmailConfiguration detailsTatEmailConfiguration = tatEmailConfigurationRepo
								.getTatEmailConfigurationDetails(assignEditorVO.getEmailTriggerVO().getJournalId(),
										assignEditorVO.getEmailTriggerVO().getManuscriptId(), getManuScriptDetails.getManuscriptVersion(),
										assignEditorVO.getEmailTriggerVO().getUserId(), assignEditorVO.getEmailTriggerVO().getTaskId());
						if (detailsTatEmailConfiguration != null) {
							detailsTatEmailConfiguration.setJournalId(assignEditorVO.getEmailTriggerVO().getJournalId());
							detailsTatEmailConfiguration.setManuscriptId(assignEditorVO.getEmailTriggerVO().getManuscriptId());
							detailsTatEmailConfiguration
									.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
							detailsTatEmailConfiguration.setUserId(assignEditorVO.getEmailTriggerVO().getUserId());
							detailsTatEmailConfiguration.setTaskId(assignEditorVO.getEmailTriggerVO().getTaskId());
	
							detailsTatEmailConfiguration.setAcceptReviewerUrl(assignEditorVO.getEmailTriggerVO().getAcceptByReviewerUrl());
							detailsTatEmailConfiguration.setEditorId(assignEditorVO.getEmailTriggerVO().getEditorUserId());
							detailsTatEmailConfiguration.setJournalId(assignEditorVO.getEmailTriggerVO().getJournalId());
							detailsTatEmailConfiguration.setManagingEditorId(assignEditorVO.getEmailTriggerVO().getManagingEditorId());
							detailsTatEmailConfiguration.setManuscriptStatus(assignEditorVO.getEmailTriggerVO().getManuscriptStatus());
							detailsTatEmailConfiguration.setReciverRoleId(assignEditorVO.getEmailTriggerVO().getReceiverRoleId());
							detailsTatEmailConfiguration.setRejectReviewerUrl(assignEditorVO.getEmailTriggerVO().getRejectByReviewerUrl());
							detailsTatEmailConfiguration.setReviewerId(assignEditorVO.getEmailTriggerVO().getReviewerUserId());
							detailsTatEmailConfiguration.setRoleId(assignEditorVO.getEmailTriggerVO().getRoleId());
	
							detailsTatEmailConfiguration.setViewReviewerUrl(assignEditorVO.getEmailTriggerVO().getViewByReviewerUrl());
	
							detailsTatEmailConfiguration.setNotificationDate(new Date().from(LocalDate.now()
									.plusDays(i.getTat() - 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
							detailsTatEmailConfiguration.setEndDate(new Date().from(LocalDate.now().plusDays(i.getTat())
									.atStartOfDay(ZoneId.systemDefault()).toInstant()));
							detailsTatEmailConfiguration.setIsMailSend(0);
							detailsTatEmailConfiguration.setIsActive(1);
							System.out.println("If: Notification Date: "+detailsTatEmailConfiguration.getNotificationDate());
							tatEmailConfigurationRepo.save(detailsTatEmailConfiguration);
						} else {
							TatEmailConfiguration addTatEmailConfiguration = new TatEmailConfiguration();
							addTatEmailConfiguration.setJournalId(assignEditorVO.getEmailTriggerVO().getJournalId());
							addTatEmailConfiguration.setManuscriptId(assignEditorVO.getEmailTriggerVO().getManuscriptId());
							addTatEmailConfiguration.setManuscriptVersion(getManuScriptDetails.getManuscriptVersion());
							addTatEmailConfiguration.setTaskId(assignEditorVO.getEmailTriggerVO().getTaskId());
							addTatEmailConfiguration.setUserId(assignEditorVO.getEmailTriggerVO().getUserId());
	
							addTatEmailConfiguration.setAcceptReviewerUrl(assignEditorVO.getEmailTriggerVO().getAcceptByReviewerUrl());
							addTatEmailConfiguration.setEditorId(assignEditorVO.getEmailTriggerVO().getEditorUserId());
							addTatEmailConfiguration.setJournalId(assignEditorVO.getEmailTriggerVO().getJournalId());
							addTatEmailConfiguration.setManagingEditorId(assignEditorVO.getEmailTriggerVO().getManagingEditorId());
							addTatEmailConfiguration.setManuscriptStatus(assignEditorVO.getEmailTriggerVO().getManuscriptStatus());
							addTatEmailConfiguration.setReciverRoleId(assignEditorVO.getEmailTriggerVO().getReceiverRoleId());
							addTatEmailConfiguration.setRejectReviewerUrl(assignEditorVO.getEmailTriggerVO().getRejectByReviewerUrl());
							addTatEmailConfiguration.setReviewerId(assignEditorVO.getEmailTriggerVO().getReviewerUserId());
							addTatEmailConfiguration.setRoleId(assignEditorVO.getEmailTriggerVO().getRoleId());
							System.out.println("Else: Role Id : "+assignEditorVO.getEmailTriggerVO().getRoleId());
	
							addTatEmailConfiguration.setViewReviewerUrl(assignEditorVO.getEmailTriggerVO().getViewByReviewerUrl());
	
							addTatEmailConfiguration.setNotificationDate(new Date().from(LocalDate.now()
									.plusDays(i.getTat() - 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
							addTatEmailConfiguration.setEndDate(new Date().from(LocalDate.now().plusDays(i.getTat())
									.atStartOfDay(ZoneId.systemDefault()).toInstant()));
							addTatEmailConfiguration.setIsMailSend(0);
							addTatEmailConfiguration.setIsActive(1);
							System.out.println("Else: Notification Date: "+addTatEmailConfiguration.getNotificationDate());
							tatEmailConfigurationRepo.save(addTatEmailConfiguration);
						}
					}
				});
			}
		}
	
	}

	  @Override
	    public String authoritiesHistory(AssignEditorVO assignEditorVO) {

	        if ((assignEditorVO.getAuthoritiesHistoryVo().getCommentsToAuthor() == null || !StringUtils.isNotBlank(assignEditorVO.getAuthoritiesHistoryVo().getCommentsToAuthor())) && assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() == 33) {
	            ////// server response multiple time then comment To author in Final decision is blank ///////////////
	            log.info("data not saved for manuscript:  " + assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());
	            log.info("This logger for multiple entry for final decision ::");
	            return "data not saved OR server response multiple time";
	        } else if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() == 33) {
	            log.info("comment To author in Final decision  :  " + assignEditorVO.getAuthoritiesHistoryVo().getCommentsToAuthor());
	        }

	        if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() == 4) {
	            Integer histCheck = authoritiesHistoryRepo.hasAuthoritiesHistoryByStatusId(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId(), 33);
	            if (histCheck != null && histCheck >= 1) {
	                log.info("manuscript : " + assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId() + " already in final decision, so can't revision");
	                return "this manuscript already in final decision, so can't revision";
	            }
	        }
	        ////////////////////////////////////////////////////////////////////////////////////

	        ManuScript updateManuscript = manuScriptRepo.getOne(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());
	        if (assignEditorVO.getAuthoritiesHistoryVo().getCurrentUserId() != null && !assignEditorVO.getAuthoritiesHistoryVo().getCurrentUserId().isEmpty()) {
	        	assignEditorVO.getAuthoritiesHistoryVo().getCurrentUserId().stream().forEach(j -> {
	                AuthoritiesHistory i = new AuthoritiesHistory();

	                if (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
	                        && (assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(8) || assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(66))) {
	                    ReviewerSuggested reviewerSuggested = null;
	                    reviewerSuggested = reviewerSuggestedRepo.findBySuggestedReviewerId(j);
	                    if (reviewerSuggested != null && reviewerSuggested.getEmail() != null) {
	                        User user = reviewerSuggestedRepo.findByEmail(reviewerSuggested.getEmail());
	                        i.setCurrentUserId(user.getId());
	                    } else {
	                        i.setCurrentUserId(j);
	                    }
	                } else {
	                    i.setCurrentUserId(j);
	                }
	                i.setCurrentUserRole(assignEditorVO.getAuthoritiesHistoryVo().getCurrentUserRole());
	                i.setManuscriptStatusId(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId());
	                i.setPreviousUserId(assignEditorVO.getAuthoritiesHistoryVo().getPreviousUserId());
	                i.setPreviousUserRole(assignEditorVO.getAuthoritiesHistoryVo().getPreviousUserRole());
	                i.setManuscriptId(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());
	                i.setManuscriptVersion(assignEditorVO.getAuthoritiesHistoryVo().getManuscriptVersion());
	                i.setJournalId(assignEditorVO.getAuthoritiesHistoryVo().getJournalId());
	                i.setCommentsToAuthor(assignEditorVO.getAuthoritiesHistoryVo().getCommentsToAuthor());
	                i.setCommentsToEditor(assignEditorVO.getAuthoritiesHistoryVo().getCommentsToEditor());
	                i.setCommentsToReviewer(assignEditorVO.getAuthoritiesHistoryVo().getCommentsToReviewer());
	                i.setCommentsToEIC(assignEditorVO.getAuthoritiesHistoryVo().getCommentsToEIC());
	                i.setCreatedAt(new Date());
	                i.setDueDate(assignEditorVO.getAuthoritiesHistoryVo().getDueDate());
	                AuthoritiesHistory authoritiesHistorySaved = authoritiesHistoryRepo.save(i);

	                //Retrying the authority history saving if not saved
	                authoritiesHistoryRepo.save(i);


	                log.info("Status " + assignEditorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() + " Successfully saved in authoritiesHistory table for manuscript : " + assignEditorVO.getAuthoritiesHistoryVo().getManuscriptId());

	                if (assignEditorVO.getAuthoritiesHistoryVo().getDueDate() != null) {
	                    updateManuscript.setDueDate(assignEditorVO.getAuthoritiesHistoryVo().getDueDate());
	                    manuScriptRepo.saveAndFlush(updateManuscript);
	                }
	            });

	            return "Successfully saved";
	        }
	        return "Please current user id";
	    }
}
