package com.digi.uniprr.serviceImpl;

import com.digi.uniprr.VO.CommentaryUsersVO;
import com.digi.uniprr.VO.CommentaryVo;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.ManuScriptReviewerCommonVO;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.exception.HttpCommunicationException;
import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.service.CommentaryService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.utils.SendMailUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class CommentaryServiceImpl implements CommentaryService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommentaryRepo commentaryRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	CountriesService countriesService;

	@Autowired
	UserLoginRepo userLoginRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	ManuScriptRepo manuscriptRepo;

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	Environment env;

	@Autowired
	MailQueueRepo mailQueueRepo;

	@Autowired
	ManuScriptFileRepo manuScriptFileRepo;

	@Autowired
	UserRoleRepo userRoleRepo;

	@Autowired
	CountriesRepo countriesRepo;

	@Value("${tempPassword}")
	String tempPassword;

	@Override
	public void inviteCommentary(CommentaryVo commentaryVo) {

		commentaryVo.getAuthorsList().stream().forEach(auth -> auth.setUserRole(4)); // seting role to Author Role
		commentaryVo.getReviewersList().stream().forEach(review -> review.setUserRole(5)); // Setting role to Reviewer
																							// Role

		List<CommentaryUsersVO> list = Stream.of(commentaryVo.getAuthorsList(), commentaryVo.getReviewersList())
				.flatMap(Collection::stream).collect(Collectors.toList()); // Combining both list to one list

		ManuScriptFile manuScriptFile = manuScriptFileRepo.getManuscriptFileById(commentaryVo.getManuscriptId())
				.stream().filter(obj -> StringUtils.equalsIgnoreCase(obj.getFileType(), "MergedFile"))
				.sorted(Comparator.comparing(ManuScriptFile::getFileVersion).reversed()).findFirst().get();

		if (list != null && !list.isEmpty()) {
			list.stream().forEach(user -> {

				Integer count = commentaryRepo.getCommentaryCount(commentaryVo.getJournalId(), user.getUserId(),
						user.getUserRole(), commentaryVo.getManuscriptId());
				if (count == 0) {
					Commentary add = new Commentary();
					add.setEditorId(commentaryVo.getEditorId());
					add.setEditorRole(commentaryVo.getEditorRole());
					add.setUserId(user.getUserId());
					add.setUserRole(user.getUserRole());
					add.setJournalId(commentaryVo.getJournalId());
					add.setManuscriptId(commentaryVo.getManuscriptId());
					add.setFileName(manuScriptFile.getFileName());
					add.setFilePath(manuScriptFile.getFilePath());
					add.setFileSize(manuScriptFile.getFileSize());
					add.setFileType(manuScriptFile.getFileType());
					add.setCreatedOn(new Date());
					commentaryRepo.save(add);
				}

				// Email Trigger
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(commentaryVo.getJournalId());
				emailTriggerVO.setUserId(user.getUserId());
				if (commentaryVo.getEditorRole().equals(2))
					emailTriggerVO.setManagingEditorId(commentaryVo.getEditorId());
				else
					emailTriggerVO.setEditorUserId(commentaryVo.getEditorId());
				emailTriggerVO.setManuscriptId(commentaryVo.getManuscriptId());
				emailTriggerVO.setManuscriptStatus(commentaryVo.getManuscriptStatusId());
				if (user.getUserRole() != null && user.getUserRole().equals(4)) {
					emailTriggerVO.setReceiverRoleId(user.getUserRole());
					emailTriggerVO.setAuthorUserId(user.getUserId());
				}
				if (user.getUserRole() != null && user.getUserRole().equals(5)) {
					emailTriggerVO.setReceiverRoleId(user.getUserRole());
					emailTriggerVO.setReviewerUserId(user.getUserId());
				}
				if (commentaryVo.getTemplateId() != null) {
					emailTriggerVO.setTaskId(commentaryVo.getTemplateId());
				} else {
					emailTriggerVO.setTaskId(41);
				}
				emailTriggerVO.setRoleId(commentaryVo.getEditorRole());
				if (commentaryVo.getJournalMailTemplatesBody() != null
						&& !commentaryVo.getJournalMailTemplatesBody().isEmpty()) {
					emailTriggerVO.setEmailTemplateBody(commentaryVo.getJournalMailTemplatesBody());
				}
				if (commentaryVo.getJournalMailTemplatesSubject() != null
						&& !commentaryVo.getJournalMailTemplatesSubject().isEmpty()) {
					emailTriggerVO.setEmailTemplateSubject(commentaryVo.getJournalMailTemplatesSubject());
				}
				String message = emailTriggerService.emailTrigger(emailTriggerVO);

			});
		}
		if (commentaryVo.getReviewerSuggestedVO() != null && !commentaryVo.getReviewerSuggestedVO().isEmpty()) {
			commentaryVo.getReviewerSuggestedVO().stream().forEach(suggestedVO -> {
				if (suggestedVO.getEmail() != null && !suggestedVO.getEmail().isEmpty()) {
					User user = userRepo.findByEmail(suggestedVO.getEmail());

					if (user != null) {
						Integer count = commentaryRepo.getCommentaryCount(commentaryVo.getJournalId(), user.getId(), 5,
								commentaryVo.getManuscriptId());
						UserJournal userJournal = userJournalRepo.getUserJournal(user.getId(),
								commentaryVo.getJournalId(), commentaryVo.getUserRole());

						if (userJournal != null) {
							userJournal.setUpdatedBy(user.getId());
							userJournal.setIsActive(1);
							userJournalRepo.save(userJournal);
						} else {
							UserJournal userJournalData = new UserJournal();
							userJournalData.setJournalId(commentaryVo.getJournalId());
							userJournalData.setUserId(user.getId());
							userJournalData.setRoleId(commentaryVo.getUserRole());
							userJournalData.setUpdatedBy(user.getId());
							userJournalData.setIsActive(1);
							userJournalRepo.save(userJournalData);
						}

						if (count == 0) {
							Commentary add = new Commentary();
							add.setEditorId(commentaryVo.getEditorId());
							add.setEditorRole(commentaryVo.getEditorRole());
							add.setUserId(user.getId());
							add.setUserRole(4);
							add.setJournalId(commentaryVo.getJournalId());
							add.setManuscriptId(commentaryVo.getManuscriptId());
							add.setCreatedOn(new Date());
							add.setFileName(manuScriptFile.getFileName());
							add.setFilePath(manuScriptFile.getFilePath());
							add.setFileSize(manuScriptFile.getFileSize());
							add.setFileType(manuScriptFile.getFileType());
							commentaryRepo.save(add);
						}
						// Email Trigger
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(commentaryVo.getJournalId());
						emailTriggerVO.setUserId(user.getId());
						if (commentaryVo.getEditorRole().equals(2))
							emailTriggerVO.setManagingEditorId(commentaryVo.getEditorId());
						else
							emailTriggerVO.setEditorUserId(commentaryVo.getEditorId());
						emailTriggerVO.setManuscriptId(commentaryVo.getManuscriptId());
						emailTriggerVO.setManuscriptStatus(commentaryVo.getManuscriptStatusId());
						emailTriggerVO.setReceiverRoleId(4);
						emailTriggerVO.setAuthorUserId(user.getId());
						if (commentaryVo.getTemplateId() != null) {
							emailTriggerVO.setTaskId(commentaryVo.getTemplateId());
						} else {
							emailTriggerVO.setTaskId(41);
						}
						emailTriggerVO.setRoleId(commentaryVo.getEditorRole());
						if (commentaryVo.getJournalMailTemplatesBody() != null
								&& !commentaryVo.getJournalMailTemplatesBody().isEmpty()) {
							emailTriggerVO.setEmailTemplateBody(commentaryVo.getJournalMailTemplatesBody());
						}
						if (commentaryVo.getJournalMailTemplatesSubject() != null
								&& !commentaryVo.getJournalMailTemplatesSubject().isEmpty()) {
							emailTriggerVO.setEmailTemplateSubject(commentaryVo.getJournalMailTemplatesSubject());
						}
						String message = emailTriggerService.emailTrigger(emailTriggerVO);

					} else {
						User createUser = new User();
						createUser.setAccountLocked(1);
						createUser.setIsActive(1);
						createUser.setSalutation(Enum.valueOf(Salutation.class, suggestedVO.getSalutation()));
						createUser.setFirstName(suggestedVO.getFirstname());
						createUser.setLastName(suggestedVO.getLastname());
						createUser.setUsername(suggestedVO.getEmail());
						createUser.setEmail(suggestedVO.getEmail());
						createUser.setSpecialization(suggestedVO.getSpecialization());
						createUser.setExpertise(suggestedVO.getSubject());
						createUser.setInstitute(suggestedVO.getInstitution());
						if (suggestedVO.getCountry() != null && !suggestedVO.getCountry().isEmpty()) {
							Countries countries = countriesService
									.getCountryByName(suggestedVO.getCountry().toString().trim());
							if (countries != null) {
								createUser.setCountryId(countries.getId());
							}

						}
						String token = UUID.randomUUID().toString();
						createUser.setPassword(null);
						createUser.setToken(token);
						User usr = userLoginRepo.save(createUser);
						if (usr != null) {
							// Send Reviewer Invitation For Creating Account
							ManuScript getManuscriptData = manuscriptRepo.getById(commentaryVo.getManuscriptId());
							if (getManuscriptData != null) {
								List<String> journalWebsiteUrl = new ArrayList<>();
								if (getManuscriptData.getJournal().getJournalMetadata() != null) {
									journalWebsiteUrl.add(getManuscriptData.getJournal().getJournalMetadata().getUrl());
								}

								sendMailForActivationForReviewer(suggestedVO.getEmail(), token,
										commentaryVo.getSendEmailUrl(), suggestedVO.getSalutation(),
										suggestedVO.getFirstname(), suggestedVO.getLastname(),
										getManuscriptData.getManuscriptTitle(),
										getManuscriptData.getJournal().getJournalTitle(),
										getManuscriptData.getJournal().getJournalId(),
										getManuscriptData.getJournal().getJournalCode(), journalWebsiteUrl);
							}

							UserJournal userJournal = userJournalRepo.getUserJournal(usr.getId(),
									commentaryVo.getJournalId(), commentaryVo.getUserRole());
							List<Integer> userRoles = Arrays.asList(4, 5);
							if (userJournal != null) {
								userJournal.setUpdatedBy(usr.getId());
								userJournal.setIsActive(1);
								userJournalRepo.save(userJournal);
							} else {
								for (Integer roleId : userRoles) {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(commentaryVo.getJournalId());
									userJournalData.setUserId(usr.getId());
									userJournalData.setRoleId(roleId);
									userJournalData.setUpdatedBy(usr.getId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);

								}

							}

							Integer count = commentaryRepo.getCommentaryCount(commentaryVo.getJournalId(), usr.getId(),
									4, commentaryVo.getManuscriptId());

							if (count == 0) {
								Commentary add = new Commentary();
								add.setEditorId(commentaryVo.getEditorId());
								add.setEditorRole(commentaryVo.getEditorRole());
								add.setUserId(usr.getId());
								add.setUserRole(4);
								add.setJournalId(commentaryVo.getJournalId());
								add.setManuscriptId(commentaryVo.getManuscriptId());
								add.setCreatedOn(new Date());
								add.setFileName(manuScriptFile.getFileName());
								add.setFilePath(manuScriptFile.getFilePath());
								add.setFileSize(manuScriptFile.getFileSize());
								add.setFileType(manuScriptFile.getFileType());
								commentaryRepo.save(add);
							}
							// Email Trigger
							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(commentaryVo.getJournalId());
							emailTriggerVO.setUserId(usr.getId());
							if (commentaryVo.getEditorRole().equals(2))
								emailTriggerVO.setManagingEditorId(commentaryVo.getEditorId());
							else
								emailTriggerVO.setEditorUserId(commentaryVo.getEditorId());
							emailTriggerVO.setManuscriptId(commentaryVo.getManuscriptId());
							emailTriggerVO.setManuscriptStatus(commentaryVo.getManuscriptStatusId());
							if (commentaryVo.getUserRole() != null && commentaryVo.getUserRole().equals(4)) {
								emailTriggerVO.setReceiverRoleId(commentaryVo.getUserRole());
								emailTriggerVO.setAuthorUserId(usr.getId());
							}
							if (commentaryVo.getUserRole() != null && commentaryVo.getUserRole().equals(5)) {
								emailTriggerVO.setReceiverRoleId(commentaryVo.getUserRole());
								emailTriggerVO.setReviewerUserId(usr.getId());
							} else {
								emailTriggerVO.setReceiverRoleId(4);
								emailTriggerVO.setAuthorUserId(usr.getId());
							}
							if (commentaryVo.getTemplateId() != null) {
								emailTriggerVO.setTaskId(commentaryVo.getTemplateId());
							} else {
								emailTriggerVO.setTaskId(41);
							}
							emailTriggerVO.setRoleId(commentaryVo.getEditorRole());
							if (commentaryVo.getJournalMailTemplatesBody() != null
									&& !commentaryVo.getJournalMailTemplatesBody().isEmpty()) {
								emailTriggerVO.setEmailTemplateBody(commentaryVo.getJournalMailTemplatesBody());
							}
							if (commentaryVo.getJournalMailTemplatesSubject() != null
									&& !commentaryVo.getJournalMailTemplatesSubject().isEmpty()) {
								emailTriggerVO.setEmailTemplateSubject(commentaryVo.getJournalMailTemplatesSubject());
							}
							String message = emailTriggerService.emailTrigger(emailTriggerVO);

						}

					}

				}

			});
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
		Journal i = journalsRepo.getById(journalId);
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
		Journal detailsOfJournal = journalsRepo.getJournalById(journalId);
		if (detailsOfJournal != null) {
			builder.append("<p> Dear " + salutation + "." + lastName + "</p>");
			builder.append("<p></p>");
			builder.append("<p>Greetings!</p>");
			builder.append("<p></p>");
			builder.append("<p>We are pleased to add you as a reviewer for the journal “"
					+ detailsOfJournal.getJournalTitle() + "” on our new manuscript management application,</p>");
			builder.append("<p>Review JOW: <a href=" + "https://review.jow.medknow.com/"
					+ detailsOfJournal.getJournalCode() + "  target=\"blank\">" + "https://review.jow.medknow.com/"
					+ detailsOfJournal.getJournalCode() + "</a></p>");
			builder.append("<p></p>");
			builder.append("<p>Your Username is: " + email + "</p>");
			builder.append("<p>Your Default Password is: " + tempPassword.trim() + "</p>");
			builder.append("<p></p>");
			builder.append(
					"<p>Please click on the following link or copy and paste the link in your browser to create a new password for your account.</p>");
			builder.append("<p></p>");
			resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
					+ "&journalCode=" + detailsOfJournal.getJournalCode();
			builder.append("<p> (<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
			builder.append("<p></p>");
			builder.append(
					"<p> If you have any questions or concerns regarding log in or accessing your account, kindly write an email to our techsupport team at</p>");
			builder.append("<p>wkhlrpmedknow_techsupport@wolterskluwer.com. We shall be obliged to help you.</p>");
			builder.append("<p></p>");
			builder.append("<p>Regards,</p>");
			builder.append("<p></p>");
			builder.append("<p> " + detailsOfJournal.getJournalTitle() + "</p>");
		}
		builder.append("<p>Wolters Kluwer - Medknow</p>");
		return builder.toString();
	}
//	private String activateUserMailTemplateForReviewer(String email, String token, String activationUrl,
//			String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
//			Integer journalId, String journalCode, List<String> journalWebsiteUrl) {
//		StringBuilder builder = new StringBuilder();
//		String resetUrl = activationUrl;
//		String resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
//				+ "&journalCode=" + journalCode;
//		builder.append("<p> Dear " + salutation + "." + lastName + "</p>");
//		builder.append("<p> Greetings! </p>");
//
//		builder.append("<p>You’ve been invited to " + journalTitle + " to review a manuscript entitled "
//				+ manuscriptTitle + " </p>");
//
//		builder.append(
//				"We request you to confirm your account on our new manuscript management application, Review JOW: "
//						+ journalWebsiteUrl.get(0) + "");
//
//		builder.append("<p> Your Username is :" + email + "," + "</p>");
//
//		builder.append(
//				"<p>Please click on the following link or copy and paste the link in your browser to activate your account and set a new password to log in.</p>");
//
//		builder.append("<p>LINK:</p>");
//		builder.append("<p>(<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
//
//		builder.append(
//				"<p> If you have any questions or concerns regarding log in or accessing your account, kindly write an email to our WK|Medknow Tech Support ID – wkhlrpmedknow_techsupport@wolterskluwer.com.\r\n"
//						+ "We shall be obliged to help you.\r\n" + "</p>");
//
//		builder.append("<p>Regards,</p>");
//		builder.append("<p>" + journalTitle + "</p>");
//		builder.append("<p>WK| Medknow Publications</p>");
//		return builder.toString();
//	}

	@Override
	public List<CommentaryVo> getCommentary(CommentaryVo commentaryVo) {
		List<CommentaryVo> commentaryList = new ArrayList<>();

		if (commentaryVo.getJournalId() != null && commentaryVo.getUserId() != null
				&& commentaryVo.getUserRole() != null) {
			List<Commentary> commentaries = commentaryRepo
					.getCommentaryList(commentaryVo.getJournalId(), commentaryVo.getUserRole(),
							commentaryVo.getUserId())
					.stream().filter(obj -> !StringUtils.equalsIgnoreCase(obj.getAction(), "submitted"))
					.collect(Collectors.toList());

			commentaries.stream().forEach(commentary -> {
				CommentaryVo addComment = new CommentaryVo();
				addComment.setJournalId(commentary.getJournalId());
				addComment.setEditorId(commentary.getEditorId());
				addComment.setEditorRole(commentary.getEditorRole());
				if (commentary.getEditorId() != null) {
					User user = userRepo.getById(commentary.getEditorId());

					addComment.setSentEditorName(
							user.getSalutation() + "." + user.getFirstName() + " " + user.getLastName());
					addComment.setSentEditorEmail(user.getEmail());
				}
				addComment.setSubmittedDate(commentary.getManuScript().getSubmissionDate());
				addComment.setManuscriptCode(commentary.getManuScript().getManuscriptCode());
				addComment.setManuscriptTitle(commentary.getManuScript().getManuscriptTitle());
				addComment.setManuscriptId(commentary.getManuscriptId());
				addComment.setManuscriptType(commentary.getManuScript().getManuscriptTypeDetails().getManuscriptType());
				addComment.setManuscriptVersion(commentary.getManuScript().getManuscriptVersion());
				addComment.setCreatedOn(commentary.getCreatedOn());
				addComment.setStatusCode(commentary.getManuScript().getStatusMaster().getStatusValueForAuthor());
				addComment.setUserId(commentary.getUserId());
				addComment.setUserRole(commentary.getUserRole());
				addComment.setFilePath(commentary.getFilePath());
				addComment.setFileSize(commentary.getFileSize());
				addComment.setAction(commentary.getAction());
				addComment.setFileName(commentary.getFileName());
				addComment.setId(commentary.getId());
				commentaryList.add(addComment);

			});

		}
		return commentaryList;
	}

	@Override
	public void updateUser(CommentaryVo userVo) {

		UserJournal journalInfo = userJournalRepo.getUserJournalInfo(userVo.getUserId(), userVo.getJournalId(),
				userVo.getUserRole());

		if (journalInfo == null) {
			UserJournal add = new UserJournal();
			add.setUserId(userVo.getUserId());
			add.setJournalId(userVo.getJournalId());
			add.setRoleId(userVo.getUserRole());
			add.setIsActive(1);
			add.setUpdatedDate(new Date());
			add.setUpdatedBy(userVo.getUserId());
			userJournalRepo.save(add);

			UserRole role = new UserRole();
			role.setRole(userVo.getUserRole());
			role.setUser(userVo.getUserId());
			role.setUpdatedBy(userVo.getUserId());
			role.setUpdatedOn(new Date());
			role.setIsActive(1);
			userRoleRepo.save(role);

		}
	}

	@Override
	public List<CommentaryVo> getCommentaryList(Integer manuscriptId, Integer journalId) {
		List<CommentaryVo> list = new ArrayList<>();

		List<Commentary> userList = commentaryRepo.getCommentaryList(manuscriptId, journalId);

		for (Commentary commentary : userList) {
			CommentaryVo vo = new CommentaryVo();
			User usr = userRepo.getById(commentary.getUserId());
			vo.setId(commentary.getId());
			vo.setUserName(usr.getSalutation() + "." + usr.getFirstName() + " " + usr.getLastName());
			vo.setUserId(usr.getId());
			vo.setRoleName(commentary.getUserRole() == 4 ? "Author" : "Reviewer");
			vo.setAction(commentary.getAction());
			vo.setUserRole(commentary.getUserRole());
			vo.setCreatedOn(commentary.getCreatedOn());
			vo.setManuscriptId(commentary.getManuscriptId());
			vo.setUserRole(commentary.getUserRole());
			vo.setFilePath(commentary.getFilePath());
			vo.setFileSize(commentary.getFileSize());
			vo.setFileName(commentary.getFileName());
			list.add(vo);
		}

		return list;
	}

	@Override
	public void linkCommentaryToManuscript(Integer manuscriptId, Integer commentaryId, Integer journalId) {

		manuscriptRepo.linkCommentaryToManuscript(manuscriptId, commentaryId, journalId);
	}

	@Override
	public String deleteInvitation(CommentaryVo commentaryVo) {

		Integer commentry = commentaryRepo.deleteInvitation(commentaryVo.getJournalId(), commentaryVo.getUserId(),
				commentaryVo.getUserRole(), commentaryVo.getManuscriptId(), commentaryVo.getId());

		// Email Trigger
		String message = "";
		if (commentry > 0) {
			EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
			emailTriggerVO.setJournalId(commentaryVo.getJournalId());
			emailTriggerVO.setUserId(commentaryVo.getUserId());
			if (commentaryVo.getEditorRole().equals(2))
				emailTriggerVO.setManagingEditorId(commentaryVo.getEditorId());
			else
				emailTriggerVO.setEditorUserId(commentaryVo.getEditorId());
			emailTriggerVO.setManuscriptId(commentaryVo.getManuscriptId());
			emailTriggerVO.setManuscriptStatus(commentaryVo.getManuscriptStatusId());
			emailTriggerVO.setReceiverRoleId(commentaryVo.getUserRole());
			if (commentaryVo.getUserRole() == 5) {
				emailTriggerVO.setReviewerUserId(commentaryVo.getUserId());
			}
			if (commentaryVo.getUserRole() == 4) {
				emailTriggerVO.setAuthorUserId(commentaryVo.getUserId());
			}
			if (commentaryVo.getTemplateId() != null) {
				emailTriggerVO.setTaskId(commentaryVo.getTemplateId());
			} else {
				emailTriggerVO.setTaskId(70);
			}
			emailTriggerVO.setRoleId(commentaryVo.getEditorRole());
			if (commentaryVo.getJournalMailTemplatesBody() != null
					&& !commentaryVo.getJournalMailTemplatesBody().isEmpty()) {
				emailTriggerVO.setEmailTemplateBody(commentaryVo.getJournalMailTemplatesBody());
			}
			if (commentaryVo.getJournalMailTemplatesSubject() != null
					&& !commentaryVo.getJournalMailTemplatesSubject().isEmpty()) {
				emailTriggerVO.setEmailTemplateSubject(commentaryVo.getJournalMailTemplatesSubject());
			}
			message = emailTriggerService.emailTrigger(emailTriggerVO);
		} else {
			message = "Commentry invitation already deleted or not present";
		}

		return message;
	}

	@Override
	public void approveInvitationStatus(Integer id, Integer userId, Date approvedOn, Integer manuscriptId,
			Integer userRole, String action) {
		commentaryRepo.approveInvitationStatus(userId, approvedOn, manuscriptId, userRole, action, id);
	}

	@Override
	public void rejectInvitationStatus(Integer id, Integer userId, Date rejectOn, Integer manuscriptId,
			Integer userRole, String action) {
		commentaryRepo.rejectInvitationStatus(userId, rejectOn, manuscriptId, userRole, action, id);
	}

	@Override
	public void submitInvitationStatus(Integer id, Integer userId, Date submittedDate, Integer manuscriptId,
			Integer userRole, String action) throws HttpCommunicationException {
		try {
			// Null checks for mandatory parameters
			if (id == null || userId == null || submittedDate == null || manuscriptId == null || userRole == null
					|| action == null) {
				logger.error("One or more mandatory parameters are missing.");
				throw new IllegalArgumentException("One or more mandatory parameters are missing.");
			}

			commentaryRepo.submitInvitationStatus(userId, submittedDate, manuscriptId, userRole, action, id);
		} catch (Exception e) {
			logger.error("An error occurred while submitting invitation status.", e);
			// You can handle the exception here, e.g., throw a custom exception or log and
			throw new HttpCommunicationException("An error occurred while submitting invitation status.");
		}
	}

	@Override
	public List<ManuScriptReviewerCommonVO> getReviewerDatabaseSuggested(
			ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
		List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();
		Pageable paging = PageRequest.of(manuScriptReviewerCommonVO.getPageNo(), manuScriptReviewerCommonVO.getLimit());

		List<UserJournal> userList = new ArrayList<>();

		if (manuScriptReviewerCommonVO != null && StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			userList = userJournalRepo.getDatabaseAuthorsAndReviewers(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getRoleId(), manuScriptReviewerCommonVO.getManuscriptId(), paging);

		} else if (manuScriptReviewerCommonVO != null && StringUtils.isNotBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			userList = userJournalRepo.getCommentaryReviewerName(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getName(),
					manuScriptReviewerCommonVO.getRoleId(), paging);

		} else if (manuScriptReviewerCommonVO != null && StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			userList = userJournalRepo.getCommentaryReviewerEmail(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getEmail(),
					manuScriptReviewerCommonVO.getRoleId(), paging);

		} else if (manuScriptReviewerCommonVO != null && StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			userList = userJournalRepo.getCommentaryReviewerSpecialization(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getSpecialization(),
					manuScriptReviewerCommonVO.getRoleId(), paging);

		} else if (StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			userList = userJournalRepo.getCommentaryReviewerSpecializationExperties(
					manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
					manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getExperties(),
					manuScriptReviewerCommonVO.getRoleId(), paging);

		} else if (StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() != null) {
			userList = userJournalRepo.getCommentaryReviewerCountry(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getRoleId(),
					manuScriptReviewerCommonVO.getCountryId(), paging);

		} else {
			userList = userJournalRepo.getCommentaryReviewerSearchAny(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getSpecialization(),
					manuScriptReviewerCommonVO.getCountryId(), manuScriptReviewerCommonVO.getExperties(),
					manuScriptReviewerCommonVO.getName(), manuScriptReviewerCommonVO.getEmail(),
					manuScriptReviewerCommonVO.getRoleId(), paging);
		}

		if (userList != null && !userList.isEmpty()) {
			for (UserJournal i : userList) {
				ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
				commonvo.setName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
				commonvo.setEmail(i.getUser().getEmail());
				commonvo.setRoleId(i.getRoleId());
				commonvo.setReviewerId(i.getUser().getId());
				commonvo.setSpecialization(i.getUser().getSpecialization());
				commonvo.setInstitution(i.getUser().getInstitute());
				if (i.getUser().getExpertise() != null)
					commonvo.setSubSpecialization(i.getUser().getExpertise());
				if (i.getUser().getCountryId() != null) {
					Countries countries = countriesRepo.getOne(i.getUser().getCountryId());
					if (countries != null) {
						commonvo.setCountry(countries.getName());
					}
				}
				rsd.add(commonvo);

			}
		}
		return rsd;
	}

	@Override
	public List<ManuScriptReviewerCommonVO> commentaryGlobalSearch(
			ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
		List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();
		Pageable paging = PageRequest.of(manuScriptReviewerCommonVO.getPageNo(), manuScriptReviewerCommonVO.getLimit());
		List<UserJournal> unAssignedReviewer = new ArrayList<UserJournal>();
		if (manuScriptReviewerCommonVO.getRoleId() == null)
			manuScriptReviewerCommonVO.setRoleId(5);

		if (manuScriptReviewerCommonVO != null && StringUtils.isNotBlank(manuScriptReviewerCommonVO.getSearch())) {
			unAssignedReviewer = userJournalRepo.commentaryGlobalSearch(manuScriptReviewerCommonVO.getRoleId(),
					manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
					manuScriptReviewerCommonVO.getSearch(), paging);
		}

		if (unAssignedReviewer != null && !unAssignedReviewer.isEmpty()) {
			for (UserJournal journal : unAssignedReviewer) {
				ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
				commonvo.setName(journal.getUser().getFirstName() + " " + journal.getUser().getLastName());
				commonvo.setEmail(journal.getUser().getEmail());
				commonvo.setReviewerId(journal.getUser().getId());
				commonvo.setSpecialization(journal.getUser().getSpecialization());
				commonvo.setInstitution(journal.getUser().getInstitute());
				if (StringUtils.isNotBlank(journal.getUser().getExpertise()))
					commonvo.setSubSpecialization(journal.getUser().getExpertise());
				if (journal.getUser().getCountryId() != null) {
					Countries countries = countriesRepo.getById(journal.getUser().getCountryId());
					if (countries != null) {
						commonvo.setCountry(countries.getName());
					}
				}
				rsd.add(commonvo);
			}
		}
		return rsd;
	}

	@Override
	public Integer totalReviewerSuggestedBtDatabaseCount(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {

		Integer totalCount = 0;

		if (manuScriptReviewerCommonVO.getRoleId() == null)
			manuScriptReviewerCommonVO.setRoleId(5);

		if (manuScriptReviewerCommonVO != null && StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			totalCount = userJournalRepo.commentaryReviewerCount(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getRoleId());

		} else if (manuScriptReviewerCommonVO != null && StringUtils.isNotBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			totalCount = userJournalRepo.commentaryReviewerNameCount(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getName(),
					manuScriptReviewerCommonVO.getRoleId());

		} else if (manuScriptReviewerCommonVO != null && StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			totalCount = userJournalRepo.commentaryReviewerEmailCount(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getEmail(),
					manuScriptReviewerCommonVO.getRoleId());

		} else if (manuScriptReviewerCommonVO != null && StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			totalCount = userJournalRepo.commentaryReviewerSpecializationCount(
					manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
					manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getRoleId());

		} else if (StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isNotBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() == null) {
			totalCount = userJournalRepo.commentarySpecializationExpertiesCount(
					manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
					manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getExperties(),
					manuScriptReviewerCommonVO.getRoleId());

		} else if (StringUtils.isBlank(manuScriptReviewerCommonVO.getName())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties())
				&& StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization())
				&& manuScriptReviewerCommonVO.getCountryId() != null) {
			totalCount = userJournalRepo.commentaryReviewerCountryCount(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getRoleId(),
					manuScriptReviewerCommonVO.getCountryId());

		} else {
			totalCount = userJournalRepo.commentaryReviewerSearchAnyCount(manuScriptReviewerCommonVO.getJournalId(),
					manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getSpecialization(),
					manuScriptReviewerCommonVO.getCountryId(), manuScriptReviewerCommonVO.getExperties(),
					manuScriptReviewerCommonVO.getName(), manuScriptReviewerCommonVO.getEmail(),
					manuScriptReviewerCommonVO.getRoleId());
		}

		return totalCount;
	}

}
