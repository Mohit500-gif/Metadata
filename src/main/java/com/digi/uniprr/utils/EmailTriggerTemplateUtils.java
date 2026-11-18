package com.digi.uniprr.utils;

import static com.digi.uniprr.utils.AppConstants.ADMIN_NAME;
import static com.digi.uniprr.utils.AppConstants.ARTICLE_DOWNLOAD_URL;
import static com.digi.uniprr.utils.AppConstants.AUTHOR_NAME;
import static com.digi.uniprr.utils.AppConstants.EDITOR_DECISION;
import static com.digi.uniprr.utils.AppConstants.EDITOR_DESSION_COMMENTS_TO_AUTHOR;
import static com.digi.uniprr.utils.AppConstants.EDITOR_NAME;
import static com.digi.uniprr.utils.AppConstants.JOURNAL_TITLE;
import static com.digi.uniprr.utils.AppConstants.MANAGING_EDITOR_NAME;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_ID;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_TITLES;
import static com.digi.uniprr.utils.AppConstants.REVIEWER_NAME;
import static com.digi.uniprr.utils.AppConstants.REVIEWER_RATING;
import static com.digi.uniprr.utils.AppConstants.REVIEWER_RECOMMENDATION;
import static com.digi.uniprr.utils.AppConstants.SIGNATURE;
import static com.digi.uniprr.utils.AppConstants.SUBMISSION_DATE;
import static com.digi.uniprr.utils.AppConstants.URL;
import static com.digi.uniprr.utils.AppConstants.JOURNAL_NAME;
import static com.digi.uniprr.utils.AppConstants.CORRESPONDING_AUTHOR_NAME;
import static com.digi.uniprr.utils.AppConstants.AUTHOR_COUNTRY;
import static com.digi.uniprr.utils.AppConstants.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EmailTemplateDataVO;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.ImpactArticleDTO;
import com.digi.uniprr.jdbcConfig.RawJDBC;
import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.StatusMaster;
import com.digi.uniprr.model.TaskEmailMap;
import com.digi.uniprr.model.TaskEmailMapReminder;
import com.digi.uniprr.model.TatEmailConfiguration;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.RoleRepo;
import com.digi.uniprr.repository.StatusMasterRepo;
import com.digi.uniprr.repository.TaskEmailMapRepo;
import com.digi.uniprr.repository.TatEmailConfigurationRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;

@Component
@Service
public class EmailTriggerTemplateUtils {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	TaskEmailMapRepo taskEmailMapRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

	@Autowired
	TatEmailConfigurationRepo tatEmailConfigurationRepo;

	@Autowired
	StatusMasterRepo statusMasterRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	UserRoleRepo UserRoleRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	RoleRepo roleRepo;

	@Autowired
	Environment env;

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	CountriesRepo countriesRepo;

	@Transactional
	public String getSubject(EmailTriggerVO emailTriggerVO, TaskEmailMap taskEmailMapDetails) {
		String body = null;
		if (taskEmailMapDetails != null) {
			ManuScript getManuScriptDetails = manuScriptRepo.getById(emailTriggerVO.getManuscriptId());
			EmailTemplateDataVO addEmailTemplateDataVO = new EmailTemplateDataVO();
			// Manuscript Id
			if (getManuScriptDetails.getManuscriptCode() != null)
				addEmailTemplateDataVO.setManuscriptId(getManuScriptDetails.getManuscriptCode());
			if (taskEmailMapDetails.getTaskId() != null && taskEmailMapDetails.getTaskId() == 71) {
				addEmailTemplateDataVO.setManuscriptTitle(
						manuScriptRepo.getByIdForLteManuscriptTitile(getManuScriptDetails.getLteManuscriptId()));
			} else {
				addEmailTemplateDataVO.setManuscriptTitle(getManuScriptDetails.getManuscriptTitle());
			}
			Journal journal_code = journalsRepo.getJournalById(getManuScriptDetails.getJournalId());
			addEmailTemplateDataVO.setJournalCode(journal_code.getJournalCode());
			if (emailTriggerVO.getJournalMailTemplatesSubject() != null
					&& !emailTriggerVO.getJournalMailTemplatesSubject().isEmpty()) {
				body = reModifiedMailTemplateBody(emailTriggerVO.getJournalMailTemplatesSubject(),
						addEmailTemplateDataVO);

			} else if (emailTriggerVO.getEmailTemplateSubject() != null
					&& !emailTriggerVO.getEmailTemplateSubject().isEmpty()) {
				body = reModifiedMailTemplateBody(emailTriggerVO.getEmailTemplateSubject(), addEmailTemplateDataVO);

			} else {
				body = reModifiedMailTemplateBody(taskEmailMapDetails.getJournalMailTemplates().getMailSubject(),
						addEmailTemplateDataVO);
			}

		}
		return body;
	};

	@Transactional
	public String getSubjectForReminder(EmailTriggerVO emailTriggerVO, TaskEmailMapReminder taskEmailMapDetails) {
		String body = null;
		if (taskEmailMapDetails != null) {
			ManuScript getManuScriptDetails = manuScriptRepo.getById(emailTriggerVO.getManuscriptId());
			EmailTemplateDataVO addEmailTemplateDataVO = new EmailTemplateDataVO();
			// Manuscript Id
			if (getManuScriptDetails.getManuscriptCode() != null) {
				addEmailTemplateDataVO.setManuscriptId(getManuScriptDetails.getManuscriptCode());
				addEmailTemplateDataVO.setJournalCode(getManuScriptDetails.getJournal().getJournalCode());
				addEmailTemplateDataVO.setManuscriptTitle(getManuScriptDetails.getManuscriptTitle());
			}

			body = reModifiedMailTemplateBody(taskEmailMapDetails.getJournalMailTemplates().getMailSubject(),
					addEmailTemplateDataVO);
		}
		return body;
	};

	@Transactional
	public String getAllData(EmailTriggerVO emailTriggerVO, TaskEmailMap taskEmailMapDetails)
			throws MalformedURLException, UnsupportedEncodingException {
		log.info("Inside In EmailTriggerTemplateUtils getAllData");
		String body = null;
		if (taskEmailMapDetails != null) {
			log.info("taskEmailMapDetails Data" + taskEmailMapDetails.toString());
			ManuScript getManuScriptDetails = manuScriptRepo.getById(emailTriggerVO.getManuscriptId());
			// Setup for the Dynamic Email Template
			EmailTemplateDataVO addEmailTemplateDataVO = new EmailTemplateDataVO();

			if (emailTriggerVO.getManuscriptStatus() == null)
				emailTriggerVO.setManuscriptStatus(getManuScriptDetails.getManuscriptStatusId());
			// Journal Title && Journal Code && Journal Website url
			if (taskEmailMapDetails.getJournalMailTemplates() != null) {
				addEmailTemplateDataVO
						.setJournalTitle(taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalTitle());
				addEmailTemplateDataVO
						.setJournalCode(taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalCode());
				addEmailTemplateDataVO.setJournalWebsiteUrl(taskEmailMapDetails.getJournalMailTemplates().getJournal()
						.getJournalMetadata().getWebsiteUrl());
				addEmailTemplateDataVO.setTaskId(taskEmailMapDetails.getTaskId());
			}

			// Production Url
			if (env.getProperty("production.site.url") != null && !env.getProperty("production.site.url").isEmpty()) {
				addEmailTemplateDataVO.setProductionWebsiteUrl(env.getProperty("production.site.url"));
			}

			// Role Type
			if (emailTriggerVO.getReceiverRoleId() != null) {
				Role getRole = roleRepo.getById(emailTriggerVO.getReceiverRoleId());
				if (getRole != null) {
					addEmailTemplateDataVO.setRoleType(getRole.getRoleName());
				}
			}

			// Editor in chief Email
			if (getManuScriptDetails.getAssignedUser() != null) {
				User eicDetails = userRepo.getById(getManuScriptDetails.getAssignedUser());
				if (eicDetails != null) {
					addEmailTemplateDataVO.setEditorInchiefEmail(eicDetails.getEmail());
					addEmailTemplateDataVO.setManagingEditorName(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setManagingEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setManagingEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setManagingEditorLastName(eicDetails.getLastName());

					addEmailTemplateDataVO.setDefaultEditor(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setDefaultEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setDefaultEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setDefaultEditorLastName(eicDetails.getLastName());

					addEmailTemplateDataVO.setEditorName(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setEditorLastName(eicDetails.getLastName());
				}

			}

			if (emailTriggerVO.getManuscriptStatus() != null && (emailTriggerVO.getManuscriptStatus() == 51
					|| emailTriggerVO.getManuscriptStatus() == 52 || emailTriggerVO.getManuscriptStatus() == 68)) {

				Integer manuscriptE = null;
				try {
					manuscriptE = manuscriptEditorRepo.getManuscriptEditorListFileR(emailTriggerVO.getManuscriptId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (manuscriptE != null) {
					User eicDetails1 = userRepo.getById(manuscriptE);
					if (eicDetails1 != null) {
						addEmailTemplateDataVO.setEditorName(eicDetails1.getSalutation() + "." + " "
								+ eicDetails1.getFirstName() + " " + eicDetails1.getLastName());
						addEmailTemplateDataVO.setEditorSalutation(eicDetails1.getSalutation().toString());
						addEmailTemplateDataVO.setEditorFirstName(eicDetails1.getFirstName());
						addEmailTemplateDataVO.setEditorLastName(eicDetails1.getLastName());
					}
				}
			}

			// Phase Name
			if (emailTriggerVO.getManuscriptStatus() != null
					&& (emailTriggerVO.getTaskId().equals(88) || emailTriggerVO.getTaskId().equals(89))
					&& (emailTriggerVO.getManuscriptStatus().equals(33)
							|| emailTriggerVO.getManuscriptStatus().equals(63))) {
				addEmailTemplateDataVO.setPhaseName("Accepted");

			} else if (emailTriggerVO.getManuscriptStatus() != null
					&& (emailTriggerVO.getTaskId().equals(88) || emailTriggerVO.getTaskId().equals(89))
					&& emailTriggerVO.getManuscriptStatus().equals(30)) {
				addEmailTemplateDataVO.setPhaseName("Rejected");

			} else if (getManuScriptDetails.getManuscriptStatusId() != null) {
				StatusMaster getStatusMaster = statusMasterRepo.getById(getManuScriptDetails.getManuscriptStatusId());
				if (getStatusMaster != null) {
					addEmailTemplateDataVO.setPhaseName(getStatusMaster.getStatusValueForArticleLifeCycle());
				}
			}

			// Date Sent
			addEmailTemplateDataVO.setDateSent(new Date());

			// Manuscript Id
			if (getManuScriptDetails.getManuscriptCode() != null)
				addEmailTemplateDataVO.setManuscriptId(getManuScriptDetails.getManuscriptCode());
			// completion Due Date
			if (emailTriggerVO.getExtendDate() != null) {
				log.info("Due Date for invite reviewer : "+emailTriggerVO.getExtendDate());
				addEmailTemplateDataVO.setCompletionDueDate(emailTriggerVO.getExtendDate());
			}
				

			// Article Type
			if (getManuScriptDetails.getManuscriptTypeDetails() != null)
				addEmailTemplateDataVO
						.setArticleType(getManuScriptDetails.getManuscriptTypeDetails().getManuscriptType());

			// Comment To Author
			if (emailTriggerVO.getCommentToAuthor() != null && !emailTriggerVO.getCommentToAuthor().isEmpty())
				addEmailTemplateDataVO.setCommentToAuthor(emailTriggerVO.getCommentToAuthor());

			// Comment To Editor
			if (emailTriggerVO.getCommentToEditor() != null && !emailTriggerVO.getCommentToEditor().isEmpty())
				addEmailTemplateDataVO.setCommentToEditor(emailTriggerVO.getCommentToEditor());

			// Submission Date
			if (getManuScriptDetails.getSubmissionDate() != null)
				addEmailTemplateDataVO.setSubmissionDate(getManuScriptDetails.getSubmissionDate());

			// Re-Submission Date
			if (getManuScriptDetails.getResubmissionDate() != null)
				addEmailTemplateDataVO.setReSubmissionDate(getManuScriptDetails.getResubmissionDate());

			// Manuscript Title
			if (taskEmailMapDetails.getTaskId() != null && taskEmailMapDetails.getTaskId() == 71) {
				addEmailTemplateDataVO.setManuscriptTitle(
						manuScriptRepo.getByIdForLteManuscriptTitile(getManuScriptDetails.getLteManuscriptId()));

			} else if (getManuScriptDetails.getManuscriptTitle() != null) {
				addEmailTemplateDataVO.setManuscriptTitle(getManuScriptDetails.getManuscriptTitle());
			}

			// Author Name
			if (getManuScriptDetails.getUser() != null) {
				addEmailTemplateDataVO.setAuthorName(getManuScriptDetails.getUser().getSalutation() + "." + " "
						+ getManuScriptDetails.getUser().getFirstName() + " "
						+ getManuScriptDetails.getUser().getLastName());
				addEmailTemplateDataVO.setAuthorSalutation(getManuScriptDetails.getUser().getSalutation().toString());
				addEmailTemplateDataVO.setAuthorFirstName(getManuScriptDetails.getUser().getFirstName());
				addEmailTemplateDataVO.setAuthorLastName(getManuScriptDetails.getUser().getLastName());
			}
			// Author Country Name
			if (getManuScriptDetails.getUser().getCountryId() != null) {
				Countries getcountriesName = countriesRepo.getById(getManuScriptDetails.getUser().getCountryId());
				addEmailTemplateDataVO.setCountryName(getcountriesName.getName());

			}

			// Url
			if (taskEmailMapDetails.getJournalMailTemplates() != null)
				addEmailTemplateDataVO.setUrl("<a href="
						+ taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata().getUrl()
						+ " target=\"blank\">"
						+ taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata().getUrl()
						+ "</a>");

			// Admin Name
			if (taskEmailMapDetails.getJournalMailTemplates().getJournal().getCreatedBy() != null) {
				User userdetails = userRepo
						.getById(taskEmailMapDetails.getJournalMailTemplates().getJournal().getCreatedBy());
				if (userdetails != null) {
					addEmailTemplateDataVO.setAdminName(userdetails.getSalutation() + "." + " "
							+ userdetails.getFirstName() + " " + userdetails.getLastName());
					addEmailTemplateDataVO.setAdminSalutation(userdetails.getSalutation().toString());
					addEmailTemplateDataVO.setAdminFirstName(userdetails.getFirstName());
					addEmailTemplateDataVO.setAdminLastName(userdetails.getLastName());
				}
			}

			// Managing Editor Name
			if (emailTriggerVO.getManagingEditorId() != null) {
				User eicDetails = userRepo.getById(emailTriggerVO.getManagingEditorId());
				if (eicDetails != null) {
					addEmailTemplateDataVO.setManagingEditorName(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setManagingEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setManagingEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setManagingEditorLastName(eicDetails.getLastName());

					addEmailTemplateDataVO.setDefaultEditor(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setDefaultEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setDefaultEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setDefaultEditorLastName(eicDetails.getLastName());

					if (emailTriggerVO.getManuscriptStatus() == 30 || emailTriggerVO.getManuscriptStatus() == 33) {
						addEmailTemplateDataVO.setEditorName(eicDetails.getSalutation() + "." + " "
								+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
						addEmailTemplateDataVO.setEditorSalutation(eicDetails.getSalutation().toString());
						addEmailTemplateDataVO.setEditorFirstName(eicDetails.getFirstName());
						addEmailTemplateDataVO.setEditorLastName(eicDetails.getLastName());

					}

				}
			}

			// Editor Name
			if (emailTriggerVO.getEditorUserId() != null) {
				User editorDetails = userRepo.getById(emailTriggerVO.getEditorUserId());
				if (editorDetails != null) {
					addEmailTemplateDataVO.setEditorName(editorDetails.getSalutation() + "." + " "
							+ editorDetails.getFirstName() + " " + editorDetails.getLastName());
					addEmailTemplateDataVO.setEditorSalutation(editorDetails.getSalutation().toString());
					addEmailTemplateDataVO.setEditorFirstName(editorDetails.getFirstName());
					addEmailTemplateDataVO.setEditorLastName(editorDetails.getLastName());

					addEmailTemplateDataVO.setDefaultEditor(editorDetails.getSalutation() + "." + " "
							+ editorDetails.getFirstName() + " " + editorDetails.getLastName());
					addEmailTemplateDataVO.setDefaultEditorSalutation(editorDetails.getSalutation().toString());
					addEmailTemplateDataVO.setDefaultEditorFirstName(editorDetails.getFirstName());
					addEmailTemplateDataVO.setDefaultEditorLastName(editorDetails.getLastName());
				}
			}

			// Reviewer Name
			if (emailTriggerVO.getReviewerUserId() != null) {
				User reviewerDetails = userRepo.getById(emailTriggerVO.getReviewerUserId());
				if (reviewerDetails != null) {
					addEmailTemplateDataVO.setReviewerName(reviewerDetails.getSalutation() + "." + " "
							+ reviewerDetails.getFirstName() + " " + reviewerDetails.getLastName());
					addEmailTemplateDataVO.setReviewerSalutation(reviewerDetails.getSalutation().toString());
					addEmailTemplateDataVO.setReviewerFirstName(reviewerDetails.getFirstName());
					addEmailTemplateDataVO.setReviewerLastName(reviewerDetails.getLastName());
					addEmailTemplateDataVO.setReviewerEmailId(reviewerDetails.getEmail());
				}
			}

			// Author Name
			if (emailTriggerVO.getAuthorUserId() != null) {
				User authorDetails = userRepo.getById(emailTriggerVO.getAuthorUserId());
				if (authorDetails != null) {
					addEmailTemplateDataVO.setAuthorName(authorDetails.getSalutation() + "." + " "
							+ authorDetails.getFirstName() + " " + authorDetails.getLastName());
					addEmailTemplateDataVO.setAuthorSalutation(authorDetails.getSalutation().toString());
					addEmailTemplateDataVO.setAuthorFirstName(authorDetails.getFirstName());
					addEmailTemplateDataVO.setAuthorLastName(authorDetails.getLastName());
					ManuScript manuScript = manuScriptRepo.getFindByManuScriptDetail(emailTriggerVO.getManuscriptId());
					boolean isImpact = RawJDBC.isImpactJournalById(manuScript.getJournal().getJournalCode());
				    List<ImpactArticleDTO> impactList = RawJDBC.fetchImpactData(manuScript.getManuscriptCode());					    
				    if (isImpact&&!impactList.isEmpty()) {
				    	log.info("IMPACT : "+impactList);
				        ImpactArticleDTO impactDTO = impactList.get(0); // only expecting one row per AID
				        if (impactDTO.getAuthorReviewUrl() != null) {
				        	
//				        	addEmailTemplateDataVO.setAuthorImpactUrl(impactDTO.getAuthorReviewUrl());
				        	addEmailTemplateDataVO.setAuthorImpactUrl("<a href=" + impactDTO.getAuthorReviewUrl() + " target=\"blank\">"
									+ impactDTO.getAuthorReviewUrl() + "</a>");
				        }
				    }
				}
			}

			// Reviewer URL creation
			if (emailTriggerVO.getAcceptByReviewerUrl() != null && !emailTriggerVO.getAcceptByReviewerUrl().isEmpty()
					|| emailTriggerVO.getRejectByReviewerUrl() != null
							&& !emailTriggerVO.getRejectByReviewerUrl().isEmpty()
					|| emailTriggerVO.getViewByReviewerUrl() != null
							&& !emailTriggerVO.getViewByReviewerUrl().isEmpty()) {
				// Encode URL For Accept By Reviewer
				URL acceptByReviewerUrlEncoded = new URL(emailTriggerVO.getAcceptByReviewerUrl() + "/?q="
						+ URLEncoder.encode("manuscriptId=" + emailTriggerVO.getUrlManuscript() + "&reviewerId="
								+ emailTriggerVO.getUrlReviewerId() + "&manuscriptStatus="
								+ emailTriggerVO.getUrlManuscriptStatus() + "&journalId="
								+ emailTriggerVO.getUrlJournalId() + "&manuscriptVersion="
								+ emailTriggerVO.getUrlManuscriptVersion(), "UTF-8"));
				addEmailTemplateDataVO.setAcceptByReviewerUrl("<a href=" + acceptByReviewerUrlEncoded
						+ " target=\"blank\">" + acceptByReviewerUrlEncoded + "</a>");
				log.info("AcceptByReviewerUrl Data : " + "<a href=" + acceptByReviewerUrlEncoded + " target=\"blank\">"
						+ acceptByReviewerUrlEncoded + "</a>");
				// Encode URL For Reject By Reviewer
				URL rejectByReviewerUrlEncoded = new URL(emailTriggerVO.getRejectByReviewerUrl() + "/?q="
						+ URLEncoder.encode("manuscriptId=" + emailTriggerVO.getUrlManuscript() + "&reviewerId="
								+ emailTriggerVO.getUrlReviewerId() + "&manuscriptStatus="
								+ emailTriggerVO.getUrlManuscriptStatus() + "&journalId="
								+ emailTriggerVO.getUrlJournalId() + "&manuscriptVersion="
								+ emailTriggerVO.getUrlManuscriptVersion(), "UTF-8"));
				addEmailTemplateDataVO.setRejectByReviewerUrl("<a href=" + rejectByReviewerUrlEncoded
						+ " target=\"blank\">" + rejectByReviewerUrlEncoded + "</a>");
				log.info("RejectByReviewerUrl Data : " + "<a href=" + rejectByReviewerUrlEncoded + " target=\"blank\">"
						+ rejectByReviewerUrlEncoded + "</a>");
				// Encode URL For View By Reviewer
				URL viewByReviewerUrlEncoded = new URL(emailTriggerVO.getViewByReviewerUrl() + "/?q="
						+ URLEncoder.encode("manuscriptId=" + emailTriggerVO.getUrlManuscript() + "&reviewerId="
								+ emailTriggerVO.getUrlReviewerId() + "&manuscriptStatus="
								+ emailTriggerVO.getUrlManuscriptStatus() + "&journalId="
								+ emailTriggerVO.getUrlJournalId() + "&manuscriptVersion="
								+ emailTriggerVO.getUrlManuscriptVersion(), "UTF-8"));
				addEmailTemplateDataVO.setViewByReviewerUrl("<a href=" + viewByReviewerUrlEncoded + " target=\"blank\">"
						+ viewByReviewerUrlEncoded + "</a>");
				log.info("ViewByReviewerUrl Data : " + "<a href=" + viewByReviewerUrlEncoded + " target=\"blank\">"
						+ viewByReviewerUrlEncoded + "</a>");
			}

			// Editor Decission
			if (emailTriggerVO.getManuscriptId() != null && emailTriggerVO.getJournalId() != null) {
				ManuscriptEditor getManuscriptEditorData = manuscriptEditorRepo.getManuscriptEditorDetails(
						emailTriggerVO.getManuscriptId(), getManuScriptDetails.getAssignedUser(),
						emailTriggerVO.getJournalId(), getManuScriptDetails.getManuscriptVersion());
				if (getManuscriptEditorData != null) {
					if (getManuscriptEditorData.getEditorDecision() != null)
						addEmailTemplateDataVO
								.setEditorDecission("Editor Decision : " + getManuscriptEditorData.getEditorDecision());
					if (getManuscriptEditorData.getCommentsForAuthor() != null)
						addEmailTemplateDataVO.setEditorDecissionComments(
								"<br>Editor Commnet for Author : " + getManuscriptEditorData.getCommentsForAuthor());

					if (emailTriggerVO.getCommentToEditor() == null && emailTriggerVO.getCommentToEditor() == ""
							&& getManuscriptEditorData.getCommentsForManagingEditor() != null)
						addEmailTemplateDataVO.setCommentToEditor("<br>Editor Commnet for EIC : "
								+ getManuscriptEditorData.getCommentsForManagingEditor());
					if (emailTriggerVO.getCommentToAuthor() == null && emailTriggerVO.getCommentToAuthor() == ""
							&& getManuscriptEditorData.getCommentsForAuthor() != null)
						addEmailTemplateDataVO.setCommentToAuthor(
								"<br>Editor Commnet for Author : " + getManuscriptEditorData.getCommentsForAuthor());
				}
			}

			// ReviewerDecision
			// ReviewerCommentsToEditor
			// ReviewerCommentsToAuthor
			if (emailTriggerVO.getManuscriptId() != null && emailTriggerVO.getJournalId() != null) {
				String reviewerDEscion = "Reviewer Decision : <br>";
				String reviewerCommentForAuthor = "Reviewer Comment For Author : <br>";
				String reviewerCommentForEditor = "Reviewer Comment For Editor : <br>";

				List<ManuScriptReviewer> getManuscriptReviewerList = manuScriptReviewerRepo
						.getReviewerDataForManuscript(emailTriggerVO.getManuscriptId(), emailTriggerVO.getJournalId());
				for (ManuScriptReviewer manuscriptreviewer : getManuscriptReviewerList) {

					User usr = userRepo.getByID(manuscriptreviewer.getReviewerId());

					try {
						JSONParser parser = new JSONParser();
						if (manuscriptreviewer.getReviewerResponse() != null
								&& manuscriptreviewer.getReviewerResponse() != "") {
							reviewerDEscion = reviewerDEscion + "<br> Reviewer Name : " + usr.getSalutation() + ". "
									+ usr.getFirstName() + " " + usr.getLastName();
							JSONArray array = (JSONArray) parser.parse(manuscriptreviewer.getReviewerResponse());
							for (int i = 0; i < array.size(); i++) {
								JSONObject jobj = (JSONObject) parser.parse(array.get(i).toString());
								reviewerDEscion = reviewerDEscion + "<br>CheckComment: " + jobj.get("checkcoment")
										+ "<br>" + "Answer: " + jobj.get("answer");
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (manuscriptreviewer.getCommentsToAuthor() != null
							&& manuscriptreviewer.getCommentsToAuthor() != "")

						reviewerCommentForAuthor = reviewerCommentForAuthor + "<br> Reviewer Name : "
								+ usr.getSalutation() + ". " + usr.getFirstName() + " " + usr.getLastName() + "<br>"
								+ manuscriptreviewer.getCommentsToAuthor();
					if (manuscriptreviewer.getCommentsToEditor() != null
							&& manuscriptreviewer.getCommentsToEditor() != "")

						reviewerCommentForEditor = reviewerCommentForEditor + "<br> Reviewer Name : "
								+ usr.getSalutation() + ". " + usr.getFirstName() + " " + usr.getLastName() + "<br>"
								+ manuscriptreviewer.getCommentsToEditor();

				}

				addEmailTemplateDataVO.setReviewerDecision(reviewerDEscion);
				addEmailTemplateDataVO.setReviewerCommentsToEditor(reviewerCommentForEditor);
				addEmailTemplateDataVO.setReviewerCommentsToAuthor(reviewerCommentForAuthor);

			}

			if (!StringUtils.isEmpty(emailTriggerVO.getEicFolderName())) {
				addEmailTemplateDataVO.setEicFolderName(emailTriggerVO.getEicFolderName());
			}

			// }
			if (emailTriggerVO.getEmailTemplateBody() != null && !emailTriggerVO.getEmailTemplateBody().isEmpty()) {
                    
				body = reModifiedMailTemplateBody(emailTriggerVO.getEmailTemplateBody(), addEmailTemplateDataVO);

			} else if (emailTriggerVO.getJournalMailTemplatesBody() != null
					&& !emailTriggerVO.getJournalMailTemplatesBody().isEmpty()) {

				body = reModifiedMailTemplateBody(emailTriggerVO.getJournalMailTemplatesBody(), addEmailTemplateDataVO);

			} else {
				body = reModifiedMailTemplateBody(taskEmailMapDetails.getJournalMailTemplates().getMailBody(),
						addEmailTemplateDataVO);
			}
		}
		return body;
	}

	@Transactional
	public String getAllDataForReminder(EmailTriggerVO emailTriggerVO, TaskEmailMapReminder taskEmailMapDetails)
			throws MalformedURLException, UnsupportedEncodingException {
		log.info("Inside In EmailTriggerTemplateUtils getAllData");
		String body = null;
		if (taskEmailMapDetails != null) {
			log.info("taskEmailMapDetails Data" + taskEmailMapDetails.toString());
			ManuScript getManuScriptDetails = manuScriptRepo.findById(emailTriggerVO.getManuscriptId()).orElse(null);
			// Setup for the Dynamic Email Template
			EmailTemplateDataVO addEmailTemplateDataVO = new EmailTemplateDataVO();
			// if (emailTriggerVO.getRoleId() != null && emailTriggerVO.getUserid() != null)
			// {
			// Journal Title && Journal Code && Journal Website url
			if (taskEmailMapDetails.getJournalMailTemplates() != null) {
				addEmailTemplateDataVO
						.setJournalTitle(taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalTitle());
				addEmailTemplateDataVO
						.setJournalCode(taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalCode());
				addEmailTemplateDataVO.setJournalWebsiteUrl(taskEmailMapDetails.getJournalMailTemplates().getJournal()
						.getJournalMetadata().getWebsiteUrl());
			}

			// Production Url
			if (env.getProperty("production.site.url") != null && !env.getProperty("production.site.url").isEmpty()) {
				addEmailTemplateDataVO.setProductionWebsiteUrl(env.getProperty("production.site.url"));
			}
			// Role Type
			if (emailTriggerVO.getReceiverRoleId() != null) {
				Role getRole = roleRepo.getOne(emailTriggerVO.getReceiverRoleId());
				if (getRole != null) {
					addEmailTemplateDataVO.setRoleType(getRole.getRoleName());
				}
			}
			// Editor in chief Email
			if (getManuScriptDetails.getAssignedUser() != null) {
				User eicDetails = userRepo.getByID(getManuScriptDetails.getAssignedUser());
				if (eicDetails != null) {
					addEmailTemplateDataVO.setEditorInchiefEmail(eicDetails.getEmail());
					addEmailTemplateDataVO.setManagingEditorName(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setManagingEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setManagingEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setManagingEditorLastName(eicDetails.getLastName());

					addEmailTemplateDataVO.setDefaultEditor(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setDefaultEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setDefaultEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setDefaultEditorLastName(eicDetails.getLastName());

					addEmailTemplateDataVO.setEditorName(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setEditorLastName(eicDetails.getLastName());
				}
			}

			// Phase Name
			if (emailTriggerVO.getManuscriptStatusId() != null
					&& (emailTriggerVO.getTaskId().equals(88) || emailTriggerVO.getTaskId().equals(89))
					&& (emailTriggerVO.getManuscriptStatus().equals(33)
							|| emailTriggerVO.getManuscriptStatus().equals(63))) {
				addEmailTemplateDataVO.setPhaseName("Accepted");

			} else if (emailTriggerVO.getManuscriptStatusId() != null
					&& (emailTriggerVO.getTaskId().equals(88) || emailTriggerVO.getTaskId().equals(89))
					&& emailTriggerVO.getManuscriptStatus().equals(30)) {
				addEmailTemplateDataVO.setPhaseName("Rejected");

			} else if (getManuScriptDetails.getManuscriptStatusId() != null) {
				StatusMaster getStatusMaster = statusMasterRepo.getById(getManuScriptDetails.getManuscriptStatusId());
				if (getStatusMaster != null) {
					addEmailTemplateDataVO.setPhaseName(getStatusMaster.getStatusValueForArticleLifeCycle());
				}
			}

			// Date Sent
			addEmailTemplateDataVO.setDateSent(new Date());
			// Manuscript Id
			if (getManuScriptDetails.getManuscriptCode() != null)
				addEmailTemplateDataVO.setManuscriptId(getManuScriptDetails.getManuscriptCode());

			// completion Due Date
			if (getManuScriptDetails.getManuscriptCode() != null) {
				if(emailTriggerVO.getReceiverRoleId()==5) {
					log.info("Due date For reviewer:  "+emailTriggerVO.getReviewerDueDate());
					addEmailTemplateDataVO.setCompletionDueDate(emailTriggerVO.getReviewerDueDate());
				}else if(emailTriggerVO.getReceiverRoleId()==4) {
			        LocalDateTime dueDateTime = getManuScriptDetails.getDueDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
//			        LocalDateTime adjustedDateTime = dueDateTime.minusDays(1);
			        Date adjustedDate = Date.from(dueDateTime.atZone(ZoneId.systemDefault()).toInstant());
					log.info("Due date For Author:  "+adjustedDate);
					addEmailTemplateDataVO.setCompletionDueDate(adjustedDate);
				}else {
					addEmailTemplateDataVO.setCompletionDueDate(getManuScriptDetails.getDueDate());
				}
			}

			// Article Type
			if (getManuScriptDetails.getManuscriptTypeDetails() != null)
				addEmailTemplateDataVO
						.setArticleType(getManuScriptDetails.getManuscriptTypeDetails().getManuscriptType());

			// Comment To Author
			if (emailTriggerVO.getCommentToAuthor() != null && !emailTriggerVO.getCommentToAuthor().isEmpty())
				addEmailTemplateDataVO.setCommentToAuthor(emailTriggerVO.getCommentToAuthor());

			// Submission Date
			if (getManuScriptDetails.getSubmissionDate() != null)
				addEmailTemplateDataVO.setSubmissionDate(getManuScriptDetails.getSubmissionDate());

			// Re-Submission Date
			if (getManuScriptDetails.getResubmissionDate() != null)
				addEmailTemplateDataVO.setReSubmissionDate(getManuScriptDetails.getResubmissionDate());

			// Manuscript Title
			if (getManuScriptDetails.getManuscriptTitle() != null)
				addEmailTemplateDataVO.setManuscriptTitle(getManuScriptDetails.getManuscriptTitle());

			// Author Name
			if (getManuScriptDetails.getUser() != null) {
				addEmailTemplateDataVO.setAuthorName(getManuScriptDetails.getUser().getSalutation() + "." + " "
						+ getManuScriptDetails.getUser().getFirstName() + " "
						+ getManuScriptDetails.getUser().getLastName());
				addEmailTemplateDataVO.setAuthorSalutation(getManuScriptDetails.getUser().getSalutation().toString());
				addEmailTemplateDataVO.setAuthorFirstName(getManuScriptDetails.getUser().getFirstName());
				addEmailTemplateDataVO.setAuthorLastName(getManuScriptDetails.getUser().getLastName());
			}

			// Author Country Name
			if (getManuScriptDetails.getUser().getCountryId() != null) {
				Countries getcountriesName = countriesRepo.getById(getManuScriptDetails.getUser().getCountryId());
				addEmailTemplateDataVO.setCountryName(getcountriesName.getName());

			}

			// Url
			if (taskEmailMapDetails.getJournalMailTemplates() != null)
				addEmailTemplateDataVO.setUrl("<a href="
						+ taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata().getUrl()
						+ " target=\"blank\">"
						+ taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata().getUrl()
						+ "</a>");

			// Admin Name
			if (taskEmailMapDetails.getJournalMailTemplates().getJournal().getCreatedBy() != null) {
				User userdetails = userRepo
						.getByID(taskEmailMapDetails.getJournalMailTemplates().getJournal().getCreatedBy());
				if (userdetails != null) {
					addEmailTemplateDataVO.setAdminName(userdetails.getSalutation() + "." + " "
							+ userdetails.getFirstName() + " " + userdetails.getLastName());
					addEmailTemplateDataVO.setAdminSalutation(userdetails.getSalutation().toString());
					addEmailTemplateDataVO.setAdminFirstName(userdetails.getFirstName());
					addEmailTemplateDataVO.setAdminLastName(userdetails.getLastName());
				}
			}

			// Managing Editor Name
			if (emailTriggerVO.getManagingEditorId() != null) {
				User eicDetails = userRepo.getByID(emailTriggerVO.getManagingEditorId());
				if (eicDetails != null) {
					addEmailTemplateDataVO.setManagingEditorName(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setManagingEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setManagingEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setManagingEditorLastName(eicDetails.getLastName());

					addEmailTemplateDataVO.setDefaultEditor(eicDetails.getSalutation() + "." + " "
							+ eicDetails.getFirstName() + " " + eicDetails.getLastName());
					addEmailTemplateDataVO.setDefaultEditorSalutation(eicDetails.getSalutation().toString());
					addEmailTemplateDataVO.setDefaultEditorFirstName(eicDetails.getFirstName());
					addEmailTemplateDataVO.setDefaultEditorLastName(eicDetails.getLastName());

				}
			}

			// Editor Name
			if (emailTriggerVO.getEditorUserId() != null) {
				User editorDetails = userRepo.getByID(emailTriggerVO.getEditorUserId());
				if (editorDetails != null) {
					addEmailTemplateDataVO.setEditorName(editorDetails.getSalutation() + "." + " "
							+ editorDetails.getFirstName() + " " + editorDetails.getLastName());
					addEmailTemplateDataVO.setEditorSalutation(editorDetails.getSalutation().toString());
					addEmailTemplateDataVO.setEditorFirstName(editorDetails.getFirstName());
					addEmailTemplateDataVO.setEditorLastName(editorDetails.getLastName());

					addEmailTemplateDataVO.setDefaultEditor(editorDetails.getSalutation() + "." + " "
							+ editorDetails.getFirstName() + " " + editorDetails.getLastName());
					addEmailTemplateDataVO.setDefaultEditorSalutation(editorDetails.getSalutation().toString());
					addEmailTemplateDataVO.setDefaultEditorFirstName(editorDetails.getFirstName());
					addEmailTemplateDataVO.setDefaultEditorLastName(editorDetails.getLastName());
				}
			}

			// Reviewer Name
			if (emailTriggerVO.getReviewerUserId() != null) {
				User reviewerDetails = userRepo.getByID(emailTriggerVO.getReviewerUserId());
				if (reviewerDetails != null) {
					addEmailTemplateDataVO.setReviewerName(reviewerDetails.getSalutation() + "." + " "
							+ reviewerDetails.getFirstName() + " " + reviewerDetails.getLastName());
					addEmailTemplateDataVO.setReviewerSalutation(reviewerDetails.getSalutation().toString());
					addEmailTemplateDataVO.setReviewerFirstName(reviewerDetails.getFirstName());
					addEmailTemplateDataVO.setReviewerLastName(reviewerDetails.getLastName());
					addEmailTemplateDataVO.setReviewerEmailId(reviewerDetails.getEmail());

				}
			}

			// Author Name
			if (emailTriggerVO.getAuthorUserId() != null) {
				User authorDetails = userRepo.getByID(emailTriggerVO.getAuthorUserId());
				if (authorDetails != null) {
					addEmailTemplateDataVO.setAuthorName(authorDetails.getSalutation() + "." + " "
							+ authorDetails.getFirstName() + " " + authorDetails.getLastName());
					addEmailTemplateDataVO.setAuthorSalutation(authorDetails.getSalutation().toString());
					addEmailTemplateDataVO.setAuthorFirstName(authorDetails.getFirstName());
					addEmailTemplateDataVO.setAuthorLastName(authorDetails.getLastName());
					
				}
			}
			
			boolean isImpact = RawJDBC.isImpactJournalById(getManuScriptDetails.getJournal().getJournalCode());
		    List<ImpactArticleDTO> impactList = RawJDBC.fetchImpactData(getManuScriptDetails.getManuscriptCode());					    
		    if (isImpact&&!impactList.isEmpty()) {
		    	log.info("IMPACT : "+impactList);
		        ImpactArticleDTO impactDTO = impactList.get(0); // only expecting one row per AID
		        if (impactDTO.getAuthorReviewUrl() != null) {
		        	addEmailTemplateDataVO.setAuthorImpactUrl("<a href=" + impactDTO.getAuthorReviewUrl() + " target=\"blank\">"
							+ impactDTO.getAuthorReviewUrl() + "</a>");
		        }
		    }

			// Reviewer URL creation
			if (emailTriggerVO.getAcceptByReviewerUrl() != null && !emailTriggerVO.getAcceptByReviewerUrl().isEmpty()
					|| emailTriggerVO.getRejectByReviewerUrl() != null
							&& !emailTriggerVO.getRejectByReviewerUrl().isEmpty()
					|| emailTriggerVO.getViewByReviewerUrl() != null
							&& !emailTriggerVO.getViewByReviewerUrl().isEmpty()) {
				// Encode URL For Accept By Reviewer
				URL acceptByReviewerUrlEncoded = new URL(emailTriggerVO.getAcceptByReviewerUrl() + "/?q="
						+ URLEncoder.encode("manuscriptId=" + emailTriggerVO.getUrlManuscript() + "&reviewerId="
								+ emailTriggerVO.getUrlReviewerId() + "&manuscriptStatus="
								+ emailTriggerVO.getUrlManuscriptStatus() + "&journalId="
								+ emailTriggerVO.getUrlJournalId() + "&manuscriptVersion="
								+ emailTriggerVO.getUrlManuscriptVersion(), "UTF-8"));
				addEmailTemplateDataVO.setAcceptByReviewerUrl("<a href=" + acceptByReviewerUrlEncoded
						+ " target=\"blank\">" + acceptByReviewerUrlEncoded + "</a>");
				log.info("AcceptByReviewerUrl Data : " + "<a href=" + acceptByReviewerUrlEncoded + " target=\"blank\">"
						+ acceptByReviewerUrlEncoded + "</a>");
				// Encode URL For Reject By Reviewer
				URL rejectByReviewerUrlEncoded = new URL(emailTriggerVO.getRejectByReviewerUrl() + "/?q="
						+ URLEncoder.encode("manuscriptId=" + emailTriggerVO.getUrlManuscript() + "&reviewerId="
								+ emailTriggerVO.getUrlReviewerId() + "&manuscriptStatus="
								+ emailTriggerVO.getUrlManuscriptStatus() + "&journalId="
								+ emailTriggerVO.getUrlJournalId() + "&manuscriptVersion="
								+ emailTriggerVO.getUrlManuscriptVersion(), "UTF-8"));
				addEmailTemplateDataVO.setRejectByReviewerUrl("<a href=" + rejectByReviewerUrlEncoded
						+ " target=\"blank\">" + rejectByReviewerUrlEncoded + "</a>");
				log.info("RejectByReviewerUrl Data : " + "<a href=" + rejectByReviewerUrlEncoded + " target=\"blank\">"
						+ rejectByReviewerUrlEncoded + "</a>");
				// Encode URL For View By Reviewer
				URL viewByReviewerUrlEncoded = new URL(emailTriggerVO.getViewByReviewerUrl() + "/?q="
						+ URLEncoder.encode("manuscriptId=" + emailTriggerVO.getUrlManuscript() + "&reviewerId="
								+ emailTriggerVO.getUrlReviewerId() + "&manuscriptStatus="
								+ emailTriggerVO.getUrlManuscriptStatus() + "&journalId="
								+ emailTriggerVO.getUrlJournalId() + "&manuscriptVersion="
								+ emailTriggerVO.getUrlManuscriptVersion(), "UTF-8"));
				addEmailTemplateDataVO.setViewByReviewerUrl("<a href=" + viewByReviewerUrlEncoded + " target=\"blank\">"
						+ viewByReviewerUrlEncoded + "</a>");
				log.info("ViewByReviewerUrl Data : " + "<a href=" + viewByReviewerUrlEncoded + " target=\"blank\">"
						+ viewByReviewerUrlEncoded + "</a>");
			}

			// Editor Decission
			if (emailTriggerVO.getManuscriptId() != null && emailTriggerVO.getJournalId() != null) {
				ManuscriptEditor getManuscriptEditorData = manuscriptEditorRepo.getManuscriptEditorDetails(
						emailTriggerVO.getManuscriptId(), getManuScriptDetails.getAssignedUser(),
						emailTriggerVO.getJournalId(), getManuScriptDetails.getManuscriptVersion());
				if (getManuscriptEditorData != null) {
					if (getManuscriptEditorData.getEditorDecision() != null)
						addEmailTemplateDataVO
								.setEditorDecission("Editor Decision : " + getManuscriptEditorData.getEditorDecision());
					if (getManuscriptEditorData.getCommentsForAuthor() != null)
						addEmailTemplateDataVO.setEditorDecissionComments(
								"<br>Editor Commnet for Author : " + getManuscriptEditorData.getCommentsForAuthor());

					if (emailTriggerVO.getCommentToEditor() == null && emailTriggerVO.getCommentToEditor() == ""
							&& getManuscriptEditorData.getCommentsForManagingEditor() != null)
						addEmailTemplateDataVO.setCommentToEditor("<br>Editor Commnet for EIC : "
								+ getManuscriptEditorData.getCommentsForManagingEditor());
					if (emailTriggerVO.getCommentToAuthor() == null && emailTriggerVO.getCommentToAuthor() == ""
							&& getManuscriptEditorData.getCommentsForAuthor() != null)
						addEmailTemplateDataVO.setCommentToAuthor(
								"<br>Editor Commnet for Author : " + getManuscriptEditorData.getCommentsForAuthor());
				}
			}

			// ReviewerDecision
			// ReviewerCommentsToEditor
			// ReviewerCommentsToAuthor
			if (emailTriggerVO.getManuscriptId() != null && emailTriggerVO.getJournalId() != null) {
				String reviewerDEscion = "<br>Reviewer Decision : <br>";
				String reviewerCommentForAuthor = "<br>Reviewer Comment For Author : <br>";
				String reviewerCommentForEditor = "<br>Reviewer Comment For Editor : <br>";

				List<ManuScriptReviewer> getManuscriptReviewerList = manuScriptReviewerRepo
						.getReviewerDataForManuscript(emailTriggerVO.getManuscriptId(), emailTriggerVO.getJournalId());
				for (ManuScriptReviewer manuscriptreviewer : getManuscriptReviewerList) {

					User usr = userRepo.getByID(manuscriptreviewer.getReviewerId());

					try {
						JSONParser parser = new JSONParser();
						if (manuscriptreviewer.getReviewerResponse() != null
								&& manuscriptreviewer.getReviewerResponse() != "") {
							reviewerDEscion = reviewerDEscion + "<br> Reviewer Name : " + usr.getSalutation() + ". "
									+ usr.getFirstName() + " " + usr.getLastName();
							JSONArray array = (JSONArray) parser.parse(manuscriptreviewer.getReviewerResponse());
							for (int i = 0; i < array.size(); i++) {
								JSONObject jobj = (JSONObject) parser.parse(array.get(i).toString());
								reviewerDEscion = reviewerDEscion + "<br>CheckComment: " + jobj.get("checkcoment")
										+ "<br>" + "Answer: " + jobj.get("answer");
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (manuscriptreviewer.getCommentsToAuthor() != null
							&& manuscriptreviewer.getCommentsToAuthor() != "")

						reviewerCommentForAuthor = reviewerCommentForAuthor + "<br> Reviewer Name : "
								+ usr.getSalutation() + ". " + usr.getFirstName() + " " + usr.getLastName() + "<br>"
								+ manuscriptreviewer.getCommentsToAuthor();
					if (manuscriptreviewer.getCommentsToEditor() != null
							&& manuscriptreviewer.getCommentsToEditor() != "")

						reviewerCommentForEditor = reviewerCommentForEditor + "<br> Reviewer Name : "
								+ usr.getSalutation() + ". " + usr.getFirstName() + " " + usr.getLastName() + "<br>"
								+ manuscriptreviewer.getCommentsToEditor();

				}

				addEmailTemplateDataVO.setReviewerDecision(reviewerDEscion);
				addEmailTemplateDataVO.setReviewerCommentsToEditor(reviewerCommentForEditor);
				addEmailTemplateDataVO.setReviewerCommentsToAuthor(reviewerCommentForAuthor);

			}

			if (emailTriggerVO.getEmailTemplateBody() != null && !emailTriggerVO.getEmailTemplateBody().isEmpty()) {

				body = reModifiedMailTemplateBody(emailTriggerVO.getEmailTemplateBody(), addEmailTemplateDataVO);
			} else if (emailTriggerVO.getJournalMailTemplatesBody() != null
					&& emailTriggerVO.getJournalMailTemplatesBody().isEmpty()) {

				body = reModifiedMailTemplateBody(emailTriggerVO.getJournalMailTemplatesBody(), addEmailTemplateDataVO);

			} else {
				body = reModifiedMailTemplateBody(taskEmailMapDetails.getJournalMailTemplates().getMailBody(),
						addEmailTemplateDataVO);
			}
		}
		return body;
	}

	public String reModifiedMailTemplateBody(String Mailbody, EmailTemplateDataVO emailTemplateDataVO) {
		String body = Mailbody;
		if (body.contains(MANUSCRIPT_ID) && emailTemplateDataVO.getManuscriptId() != null)
			body = body.replace(MANUSCRIPT_ID, emailTemplateDataVO.getManuscriptId());
		else
			body = body.replace(MANUSCRIPT_ID, "");
		if (body.contains(AUTHOR_NAME) && emailTemplateDataVO.getAuthorName() != null)
			body = body.replace(AUTHOR_NAME, emailTemplateDataVO.getAuthorName());
		else
			body = body.replace(AUTHOR_NAME, "");
		if (body.contains(JOURNAL_TITLE) && emailTemplateDataVO.getJournalTitle() != null)
			body = body.replace(JOURNAL_TITLE, emailTemplateDataVO.getJournalTitle());
		else
			body = body.replace(JOURNAL_TITLE, "");
		if (body.contains(JOURNAL_NAME) && emailTemplateDataVO.getJournalTitle() != null)
			body = body.replace(JOURNAL_NAME, emailTemplateDataVO.getJournalTitle());
		else
			body = body.replace(JOURNAL_NAME, "");
		if (body.contains(JOURNAL_CODE) && emailTemplateDataVO.getJournalCode() != null)
			body = body.replace(JOURNAL_CODE, emailTemplateDataVO.getJournalCode());
		else
			body = body.replace(JOURNAL_CODE, "");
		if (body.contains(MANUSCRIPT_TITLES) && emailTemplateDataVO.getManuscriptTitle() != null) {
			body = body.replace(MANUSCRIPT_TITLES, emailTemplateDataVO.getManuscriptTitle());
		} else {
			body = body.replace(MANUSCRIPT_TITLES, "");
		}
		if (body.contains(ARTICLE_TYPE) && emailTemplateDataVO.getArticleType() != null)
			body = body.replace(ARTICLE_TYPE, emailTemplateDataVO.getArticleType());
		else
			body = body.replace(ARTICLE_TYPE, "");
		if (body.contains(SUBMISSION_DATE) && emailTemplateDataVO.getSubmissionDate() != null)
			body = body.replace(SUBMISSION_DATE, emailTemplateDataVO.getSubmissionDate().toLocaleString());
		else
			body = body.replace(SUBMISSION_DATE, "");
		if (body.contains(ARTICLE_DOWNLOAD_URL) && emailTemplateDataVO.getArticleDownloadUrl() != null)
			body = body.replace(ARTICLE_DOWNLOAD_URL, emailTemplateDataVO.getArticleDownloadUrl());
		else
			body = body.replace(ARTICLE_DOWNLOAD_URL, "");
		if (body.contains(SIGNATURE) && emailTemplateDataVO.getSignature() != null)
			body = body.replace(SIGNATURE, emailTemplateDataVO.getSignature());
		else
			body = body.replace(SIGNATURE, "");
		if (body.contains(ADMIN_NAME) && emailTemplateDataVO.getAdminName() != null)
			body = body.replace(ADMIN_NAME, emailTemplateDataVO.getAdminName());
		else
			body = body.replace(ADMIN_NAME, "");
		if (body.contains(URL) && emailTemplateDataVO.getUrl() != null)
			body = body.replace(URL, emailTemplateDataVO.getUrl());
		else
			body = body.replace(URL, "");
		if (body.contains(EDITOR_NAME) && emailTemplateDataVO.getEditorName() != null)
			body = body.replace(EDITOR_NAME, emailTemplateDataVO.getEditorName());
		else
			body = body.replace(EDITOR_NAME, "");
		if (body.contains(REVIEWER_NAME) && emailTemplateDataVO.getReviewerName() != null)
			body = body.replace(REVIEWER_NAME, emailTemplateDataVO.getReviewerName());
		else
			body = body.replace(REVIEWER_NAME, "");
		if (body.contains(REVIEWER_RECOMMENDATION) && emailTemplateDataVO.getReviewerRecommendation() != null)
			body = body.replace(REVIEWER_RECOMMENDATION, emailTemplateDataVO.getReviewerRecommendation());
		else
			body = body.replace(REVIEWER_RECOMMENDATION, "");
		if (body.contains(REVIEWER_RATING) && emailTemplateDataVO.getReviewerRating() != null)
			body = body.replace(REVIEWER_RATING, emailTemplateDataVO.getReviewerRating());
		else
			body = body.replace(REVIEWER_RATING, "");
		if (body.contains(MANAGING_EDITOR_NAME) && emailTemplateDataVO.getManagingEditorName() != null)
			body = body.replace(MANAGING_EDITOR_NAME, emailTemplateDataVO.getManagingEditorName());
		else
			body = body.replace(MANAGING_EDITOR_NAME, "");
		if (body.contains(EDITOR_DESSION_COMMENTS_TO_AUTHOR) && emailTemplateDataVO.getEditorDecission() != null)
			body = body.replace(EDITOR_DESSION_COMMENTS_TO_AUTHOR, emailTemplateDataVO.getEditorDecissionComments());
		else
			body = body.replace(EDITOR_DESSION_COMMENTS_TO_AUTHOR, "");
		if (body.contains(EDITOR_DECISION) && emailTemplateDataVO.getEditorDecission() != null)
			body = body.replace(EDITOR_DECISION, emailTemplateDataVO.getEditorDecission());
		else
			body = body.replace(EDITOR_DECISION, "");
		if (body.contains(REVIEWER_DECISION) && emailTemplateDataVO.getReviewerDecision() != null)
			body = body.replace(REVIEWER_DECISION, emailTemplateDataVO.getReviewerDecision());
		else
			body = body.replace(REVIEWER_DECISION, "");
		if (body.contains(REVIEWER_COMMENTS_TO_EDITOR) && emailTemplateDataVO.getReviewerCommentsToEditor() != null)
			body = body.replace(REVIEWER_COMMENTS_TO_EDITOR, emailTemplateDataVO.getReviewerCommentsToEditor());
		else
			body = body.replace(REVIEWER_COMMENTS_TO_EDITOR, "");
		if (body.contains(REVIEWER_COMMENTS_TO_AUTHOR) && emailTemplateDataVO.getReviewerCommentsToAuthor() != null)
			body = body.replace(REVIEWER_COMMENTS_TO_AUTHOR, emailTemplateDataVO.getReviewerCommentsToAuthor());
		else
			body = body.replace(REVIEWER_COMMENTS_TO_AUTHOR, "");

		if (body.contains(REVIEWER_SALUTATION) && emailTemplateDataVO.getReviewerSalutation() != null)
			body = body.replace(REVIEWER_SALUTATION, emailTemplateDataVO.getReviewerSalutation());
		else
			body = body.replace(REVIEWER_SALUTATION, "");

		if (body.contains(REVIEWER_FIRST_NAME) && emailTemplateDataVO.getReviewerFirstName() != null)
			body = body.replace(REVIEWER_FIRST_NAME, emailTemplateDataVO.getReviewerFirstName());
		else
			body = body.replace(REVIEWER_FIRST_NAME, "");

		if (body.contains(REVIEWER_LAST_NAME) && emailTemplateDataVO.getReviewerLastName() != null)
			body = body.replace(REVIEWER_LAST_NAME, emailTemplateDataVO.getReviewerLastName());
		else
			body = body.replace(REVIEWER_LAST_NAME, "");

		if (body.contains(AUTHOR_SALUTATION) && emailTemplateDataVO.getAuthorSalutation() != null)
			body = body.replace(AUTHOR_SALUTATION, emailTemplateDataVO.getAuthorSalutation());
		else
			body = body.replace(AUTHOR_SALUTATION, "");
		if (body.contains(AUTHOR_FIRST_NAME) && emailTemplateDataVO.getAuthorFirstName() != null)
			body = body.replace(AUTHOR_FIRST_NAME, emailTemplateDataVO.getAuthorFirstName());
		else
			body = body.replace(AUTHOR_FIRST_NAME, "");

		if (body.contains(AUTHOR_LAST_NAME) && emailTemplateDataVO.getAuthorLastName() != null)
			body = body.replace(AUTHOR_LAST_NAME, emailTemplateDataVO.getAuthorLastName());
		else
			body = body.replace(AUTHOR_LAST_NAME, "");

		if (body.contains(EDITOR_SALUTATION) && emailTemplateDataVO.getEditorSalutation() != null)
			body = body.replace(EDITOR_SALUTATION, emailTemplateDataVO.getEditorSalutation());
		else
			body = body.replace(EDITOR_SALUTATION, "");
		if (body.contains(EDITOR_FIRST_NAME) && emailTemplateDataVO.getEditorFirstName() != null)
			body = body.replace(EDITOR_FIRST_NAME, emailTemplateDataVO.getEditorFirstName());
		else
			body = body.replace(EDITOR_FIRST_NAME, "");
		if (body.contains(EDITOR_LAST_NAME) && emailTemplateDataVO.getEditorLastName() != null)
			body = body.replace(EDITOR_LAST_NAME, emailTemplateDataVO.getEditorLastName());
		else
			body = body.replace(EDITOR_LAST_NAME, "");
		if (body.contains(ADMIN_SALUTATION) && emailTemplateDataVO.getAdminSalutation() != null)
			body = body.replace(ADMIN_SALUTATION, emailTemplateDataVO.getAdminSalutation());
		else
			body = body.replace(ADMIN_SALUTATION, "");
		if (body.contains(ADMIN_FIRST_NAME) && emailTemplateDataVO.getAdminFirstName() != null)
			body = body.replace(ADMIN_FIRST_NAME, emailTemplateDataVO.getAdminFirstName());
		else
			body = body.replace(ADMIN_FIRST_NAME, "");
		if (body.contains(ADMIN_LAST_NAME) && emailTemplateDataVO.getAdminLastName() != null)
			body = body.replace(ADMIN_LAST_NAME, emailTemplateDataVO.getAdminLastName());
		else
			body = body.replace(ADMIN_LAST_NAME, "");
		if (body.contains(MANAGING_EDITOR_SALUTATION) && emailTemplateDataVO.getManagingEditorSalutation() != null)
			body = body.replace(MANAGING_EDITOR_SALUTATION, emailTemplateDataVO.getManagingEditorSalutation());
		else
			body = body.replace(MANAGING_EDITOR_SALUTATION, "");
		if (body.contains(MANAGING_EDITOR_FIRST_NAME) && emailTemplateDataVO.getManagingEditorFirstName() != null)
			body = body.replace(MANAGING_EDITOR_FIRST_NAME, emailTemplateDataVO.getManagingEditorFirstName());
		else
			body = body.replace(MANAGING_EDITOR_FIRST_NAME, "");
		if (body.contains(MANAGING_EDITOR_LAST_NAME) && emailTemplateDataVO.getManagingEditorLastName() != null)
			body = body.replace(MANAGING_EDITOR_LAST_NAME, emailTemplateDataVO.getManagingEditorLastName());
		else
			body = body.replace(MANAGING_EDITOR_LAST_NAME, "");

		if (body.contains(DEFAULT_EDITOR) && emailTemplateDataVO.getDefaultEditor() != null)
			body = body.replace(DEFAULT_EDITOR, emailTemplateDataVO.getDefaultEditor());
		else
			body = body.replace(DEFAULT_EDITOR, "");

		if (body.contains(DEFAULT_EDITOR_FIRST_NAME) && emailTemplateDataVO.getDefaultEditorFirstName() != null)
			body = body.replace(DEFAULT_EDITOR_FIRST_NAME, emailTemplateDataVO.getDefaultEditorFirstName());
		else
			body = body.replace(DEFAULT_EDITOR_FIRST_NAME, "");

		if (body.contains(DEFAULT_EDITOR_LAST_NAME) && emailTemplateDataVO.getDefaultEditorLastName() != null)
			body = body.replace(DEFAULT_EDITOR_LAST_NAME, emailTemplateDataVO.getDefaultEditorLastName());
		else
			body = body.replace(DEFAULT_EDITOR_LAST_NAME, "");

		if (body.contains(DEFAULT_EDITOR_SALUTATION) && emailTemplateDataVO.getDefaultEditorSalutation() != null)
			body = body.replace(DEFAULT_EDITOR_SALUTATION, emailTemplateDataVO.getDefaultEditorSalutation());
		else
			body = body.replace(DEFAULT_EDITOR_SALUTATION, "");

		if (body.contains(JOURNAL_EMAIL) && emailTemplateDataVO.getJournalEmail() != null)
			body = body.replace(JOURNAL_EMAIL, emailTemplateDataVO.getJournalEmail());
		else
			body = body.replace(JOURNAL_EMAIL, "");
		if (body.contains(ACCEPT_BY_REVIEWER_URL) && emailTemplateDataVO.getAcceptByReviewerUrl() != null)
			body = body.replace(ACCEPT_BY_REVIEWER_URL, emailTemplateDataVO.getAcceptByReviewerUrl());
		else
			body = body.replace(ACCEPT_BY_REVIEWER_URL, "");
		if (body.contains(REJECT_BY_REVIEWER_URL) && emailTemplateDataVO.getRejectByReviewerUrl() != null)
			body = body.replace(REJECT_BY_REVIEWER_URL, emailTemplateDataVO.getRejectByReviewerUrl());
		else
			body = body.replace(REJECT_BY_REVIEWER_URL, "");
		if (body.contains(VIEW_BY_REVIEWER_URL) && emailTemplateDataVO.getViewByReviewerUrl() != null)
			body = body.replace(VIEW_BY_REVIEWER_URL, emailTemplateDataVO.getViewByReviewerUrl());
		else
			body = body.replace(VIEW_BY_REVIEWER_URL, "");
		if (body.contains(COMMENT_TO_AUTHOR) && emailTemplateDataVO.getCommentToAuthor() != null)
			body = body.replace(COMMENT_TO_AUTHOR, emailTemplateDataVO.getCommentToAuthor());
		else
			body = body.replace(COMMENT_TO_AUTHOR, "");
		if (body.contains(EDITOR_COMMENT) && emailTemplateDataVO.getCommentToEditor() != null)
			body = body.replace(EDITOR_COMMENT, emailTemplateDataVO.getCommentToEditor());
		else
			body = body.replace(EDITOR_COMMENT, "");
		if (body.contains(ROLE_TYPE) && emailTemplateDataVO.getRoleType() != null)
			body = body.replace(ROLE_TYPE, emailTemplateDataVO.getRoleType());
		else
			body = body.replace(ROLE_TYPE, "");
		if (body.contains(RESUBMISSION_DATE) && emailTemplateDataVO.getReSubmissionDate() != null)
			body = body.replace(RESUBMISSION_DATE, emailTemplateDataVO.getReSubmissionDate().toLocaleString());
		else
			body = body.replace(RESUBMISSION_DATE, "");
		if (body.contains(DATE_SENT) && emailTemplateDataVO.getDateSent() != null)
			body = body.replace(DATE_SENT, emailTemplateDataVO.getDateSent().toLocaleString());
		else
			body = body.replace(DATE_SENT, "");
		if (body.contains(EDITOR_IN_CHIEF_EMAIL) && emailTemplateDataVO.getEditorInchiefEmail() != null)
			body = body.replace(EDITOR_IN_CHIEF_EMAIL, emailTemplateDataVO.getEditorInchiefEmail());
		else
			body = body.replace(EDITOR_IN_CHIEF_EMAIL, "");
		if (body.contains(PHASE_NAME) && emailTemplateDataVO.getPhaseName() != null)
			body = body.replace(PHASE_NAME, emailTemplateDataVO.getPhaseName());
		else
			body = body.replace(PHASE_NAME, "");
		if (body.contains(JOURNAL_WEBSITE_URL) && emailTemplateDataVO.getJournalWebsiteUrl() != null)
			body = body.replace(JOURNAL_WEBSITE_URL, emailTemplateDataVO.getJournalWebsiteUrl());
		else
			body = body.replace(JOURNAL_WEBSITE_URL, "");
		if (body.contains(PRODUCTION_SITE_URL) && emailTemplateDataVO.getProductionWebsiteUrl() != null)
			body = body.replace(PRODUCTION_SITE_URL, emailTemplateDataVO.getProductionWebsiteUrl());
		else
			body = body.replace(PRODUCTION_SITE_URL, "");

		if (body.contains(REVIEWER_EMAIL_ID) && emailTemplateDataVO.getReviewerEmailId() != null)
			body = body.replace(REVIEWER_EMAIL_ID, emailTemplateDataVO.getReviewerEmailId());
		else
			body = body.replace(REVIEWER_EMAIL_ID, "");
		if (body.contains(COMPLETION_DUE_DATE) && emailTemplateDataVO.getCompletionDueDate() != null)
			body = body.replace(COMPLETION_DUE_DATE, Optional.ofNullable(emailTemplateDataVO.getCompletionDueDate())
					.map(date -> new SimpleDateFormat("MMM d, yyyy").format(date)).orElse(""));
		else
			body = body.replace(COMPLETION_DUE_DATE, "");

		if (body.contains(DUE_DATE) && emailTemplateDataVO.getCompletionDueDate() != null)
			body = body.replace(DUE_DATE, Optional.ofNullable(emailTemplateDataVO.getCompletionDueDate())
					.map(date -> new SimpleDateFormat("MMM d, yyyy").format(date)).orElse(""));
		else
			body = body.replace(DUE_DATE, "");

		if (body.contains(SALUTATION) && StringUtils.isNotBlank(emailTemplateDataVO.getReviewerSalutation()))
			body = body.replace(SALUTATION, emailTemplateDataVO.getReviewerSalutation());
		else if (body.contains(SALUTATION) && StringUtils.isNotBlank(emailTemplateDataVO.getAuthorSalutation()))
			body = body.replace(SALUTATION, emailTemplateDataVO.getAuthorSalutation());
		else
			body = body.replace(SALUTATION, "");

		if (body.contains(LAST_NAME) && StringUtils.isNotBlank(emailTemplateDataVO.getReviewerLastName()))
			body = body.replace(LAST_NAME, emailTemplateDataVO.getReviewerLastName());
		else if (body.contains(LAST_NAME) && StringUtils.isNotBlank(emailTemplateDataVO.getAuthorLastName()))
			body = body.replace(LAST_NAME, emailTemplateDataVO.getAuthorLastName());
		else
			body = body.replace(LAST_NAME, "");
		if (body.contains(CORRESPONDING_AUTHOR_NAME) && emailTemplateDataVO.getAuthorName() != null)
			body = body.replace(CORRESPONDING_AUTHOR_NAME, emailTemplateDataVO.getAuthorName());
		else
			body = body.replace(CORRESPONDING_AUTHOR_NAME, "");

		if (body.contains(AUTHOR_COUNTRY) && emailTemplateDataVO.getCountryName() != null)
			body = body.replace(AUTHOR_COUNTRY, emailTemplateDataVO.getCountryName());
		else
			body = body.replace(AUTHOR_COUNTRY, "");

		if (body.contains(EIC_FOLDER_NAME) && emailTemplateDataVO.getCountryName() != null)
			body = body.replace(EIC_FOLDER_NAME, emailTemplateDataVO.getEicFolderName());
		else
			body = body.replace(EIC_FOLDER_NAME, "");
		
		
//		if(emailTemplateDataVO.getJournalCode()!=null) {
//        boolean isImpact = RawJDBC.isImpactJournalById(emailTemplateDataVO.getJournalCode());
//	    List<ImpactArticleDTO> impactList = RawJDBC.fetchImpactData(emailTemplateDataVO.getManuscriptId());
//        log.info("jornal is impact: "+isImpact);
//        if (isImpact && !impactList.isEmpty() && emailTemplateDataVO.getTaskId() != null && emailTemplateDataVO.getTaskId() == 94) {
//            if (body == null) body = "";
//            ImpactArticleDTO impactArticleDTO = impactList.get(0);
//            log.info("after /n "+body);
//            // Avoid duplicate appending
//            if (!body.contains("[AUTHOR_IMPACT_URL]")) {
//            	if(impactArticleDTO.getAuthorReviewUrl()!=null) {
////                String impactHtml = "<p><b>Please review this link:</b> [AUTHOR_IMPACT_URL] <br>&nbsp;</p>";
//            	String impactHtml = "<p><b>Please review this link:</b> <a href="+impactArticleDTO.getAuthorReviewUrl()+" target='_blank'>Click here</a><br>&nbsp;</p>";
//            	// Insert just before the thank you line
//                if (body.contains("Thanking you")) {
//                	body = body.replaceFirst("(?i)(<p>\\s*Thanking you,)", impactHtml + "$1");
//                } else {
//                	body += impactHtml;
//                }
//              }
//            }else {
//            	body = body.replace(AUTHOR_IMPACT_URL, emailTemplateDataVO.getAuthorImpactUrl());
//            }
//            log.info("before /n "+body);
//        }
//	  }
	
		
		
		if (body.contains(AUTHOR_IMPACT_URL) && emailTemplateDataVO.getAuthorName() != null)
			body = body.replace(AUTHOR_IMPACT_URL, emailTemplateDataVO.getAuthorImpactUrl());
		else
			body = body.replace(AUTHOR_IMPACT_URL, "");

		return body.toString();
	}
}
