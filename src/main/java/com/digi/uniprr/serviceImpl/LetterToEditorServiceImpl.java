package com.digi.uniprr.serviceImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.LetterToEditorVo;
import com.digi.uniprr.model.LetterToEditor;
import com.digi.uniprr.model.ManuScriptFile;
import com.digi.uniprr.model.ManuScriptForDashboard;
import com.digi.uniprr.model.ManuscriptDashboardList;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.LetterToEditorRepo;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.repository.ManuScriptForDashboardRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.LetterToEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class LetterToEditorServiceImpl implements LetterToEditorService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	LetterToEditorRepo letterToEditorRepo;

	@Autowired
	ManuScriptForDashboardRepo manuScriptForDashboardRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	ManuScriptRepo manuscriptRepo;

	@Autowired
	ManuScriptFileRepo manuScriptFileRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Override
	public List<ManuscriptDashboardList> getManuscriptByStatus(ManuscriptDashboardList ms) {
		List<ManuscriptDashboardList> newList = new ArrayList<>();
		Pageable paging = PageRequest.of(ms.getPageNo(), ms.getLimit());
		List<Integer> manuStatus = Arrays.asList(32, 33, 59, 69, 70, 62, 65, 57, 61);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date submitDate = null;
		Date assignDate = null;
		if (ms.getSubmissionDate() != null) {
			try {
				submitDate = sdf.parse(sdf.format(ms.getSubmissionDate()));
			} catch (ParseException e) {
				logger.error("Exception while parisng submission date", e);
			}
		}

		if (ms.getAssignDate() != null) {
			try {
				assignDate = sdf.parse(sdf.format(ms.getAssignDate()));
			} catch (ParseException e) {
				logger.error("Exception while parisng submission date", e);
			}
		}

		List<ManuScriptForDashboard> lteList = new ArrayList<>();

		lteList = manuScriptForDashboardRepo.searchLTE(manuStatus, ms.getJournalId(), ms.getManuscripCode(),
				ms.getIssueName(), ms.getAuthorName(), submitDate, assignDate, ms.getManuScriptType(),
				ms.getManuscripTitle(), paging);

		/*
		 * if(StringUtils.isNotBlank(manuscriptVO.getIssueName())) { lteList =
		 * manuScriptForDashboardRepo.searchLTEbyIssueName(manuStatus,
		 * manuscriptVO.getIssueName(), manuscriptVO.getJournalId(), paging); }else {
		 * 
		 * if (manuscriptVO != null &&
		 * StringUtils.isNotBlank(manuscriptVO.getManuscripCode()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * manuscriptVO.getSubmissionDate() == null &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * manuscriptVO.getAssignDate() == null ) { lteList =
		 * manuScriptForDashboardRepo.searchLTEbyManuscriptCode(manuStatus,
		 * manuscriptVO.getManuscripCode(), manuscriptVO.getJournalId(), paging); } else
		 * if (manuscriptVO != null &&
		 * StringUtils.isNotBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { lteList =
		 * manuScriptForDashboardRepo.searchLTEbyAuthorName(manuStatus,
		 * manuscriptVO.getAuthorName(), manuscriptVO.getJournalId(),
		 * manuscriptVO.getRoleId(), manuscriptVO.getManuScriptId(), paging); } else if
		 * (manuscriptVO != null && StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() != null && manuscriptVO.getAssignDate() ==
		 * null ) { String submitDate = sdf.format(manuscriptVO.getSubmissionDate());
		 * try {
		 * 
		 * lteList = manuScriptForDashboardRepo.searchLTEbySubmissionDate(manuStatus,
		 * sdf.parse(submitDate), manuscriptVO.getJournalId(), paging); } catch
		 * (ParseException e) { e.printStackTrace(); } } else if (manuscriptVO != null
		 * && StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() !=
		 * null ) { String acceptDate = sdf.format(manuscriptVO.getAssignDate()); try {
		 * lteList = manuScriptForDashboardRepo.searchLTEbyAssignedDate(manuStatus,
		 * sdf.parse(acceptDate), manuscriptVO.getJournalId(), paging); } catch
		 * (ParseException e) { e.printStackTrace(); } } else if (manuscriptVO != null
		 * && StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isNotBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { lteList =
		 * manuScriptForDashboardRepo.searchLTEbyArticleType(manuStatus,
		 * manuscriptVO.getManuScriptType(), manuscriptVO.getJournalId(),
		 * manuscriptVO.getRoleId(), paging); } else if (manuscriptVO != null &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { lteList =
		 * manuScriptForDashboardRepo.getManuscriptForLetterToEditor(manuStatus,
		 * manuscriptVO.getJournalId(), manuscriptVO.getRoleId(), paging); } else if
		 * (manuscriptVO != null && StringUtils.isBlank(manuscriptVO.getManuscripCode())
		 * && StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isNotBlank(manuscriptVO.getManuscripTitle()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { lteList =
		 * manuScriptForDashboardRepo.searchLTEbyManuscriptTitle(manuStatus,manuscriptVO
		 * .getManuscripTitle(), manuscriptVO.getJournalId(), paging); } }
		 */

		if (lteList != null) {
			for (ManuScriptForDashboard data : lteList) {
				// get data
				try {
					User editorNameData = userRepo.getByID(data.getAssignedUser());
					ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
					if (data.getManuscriptStatusId().equals(2)) {
						int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
						if (userCount > 1) {
							int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
							Integer revCount = manuScriptReviewerRepo.getReviewerCount(data.getManuScriptId(),
									data.getJournalId());
							if (editorCount > 0 && revCount > 0) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							} else {
								manuscriptListVO.setEditorName("All(" + userCount + ")");
							}

						} else if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());

						}
					} else {
						if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
						}
					}

					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setAssignDate(data.getUpdatedOn());
					manuscriptListVO.setCommentaryId(data.getCommentaryId());
					manuscriptListVO.setLteManuscriptId(data.getLteManuscriptId());
					manuscriptListVO.setIssueName(data.getIssueName());
					manuscriptListVO.setProductionDate(data.getProductionDate());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());

					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					if (data.getManuscriptTypeDetails() != null
							&& data.getManuscriptTypeDetails().getManuscriptType() != null
							&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
						manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
					}
					newList.add(manuscriptListVO);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		return newList;
	}

	@Override
	public void updateManuscriptToLTEArticle(Integer manuscriptId, Integer letterToEditorId, Integer journalId) {
		manuscriptRepo.updateManuscriptToLTEArticle(manuscriptId, letterToEditorId, journalId);
		letterToEditorRepo.deleteInvitationResponse(letterToEditorId, journalId);
	}

	@Override
	public void inviteAuthorForResponse(LetterToEditorVo letterToEditorVo) {

		Optional<ManuScriptFile> manuFile = manuScriptFileRepo.getManuscriptFileById(letterToEditorVo.getManuscriptId())
				.stream().filter(obj -> StringUtils.equalsIgnoreCase(obj.getFileType(), "MergedFile"))
				.sorted(Comparator.comparing(ManuScriptFile::getFileVersion).reversed()).findFirst();

		ManuScriptFile manuScriptFile = manuFile.isPresent() ? manuFile.get() : new ManuScriptFile();

		LetterToEditor letter = letterToEditorRepo.getLetterToEditorCount(letterToEditorVo.getJournalId(),
				letterToEditorVo.getUserId(), letterToEditorVo.getUserRole(), letterToEditorVo.getManuscriptId());

		if (letter == null) {
			LetterToEditor letterToEditor = new LetterToEditor();
			letterToEditor.setEditorId(letterToEditorVo.getEditorId());
			letterToEditor.setEditorRole(letterToEditorVo.getEditorRole());
			letterToEditor.setUserId(letterToEditorVo.getUserId());
			letterToEditor.setUserRole(letterToEditorVo.getUserRole());
			letterToEditor.setJournalId(letterToEditorVo.getJournalId());
			letterToEditor.setManuscriptId(letterToEditorVo.getManuscriptId());
			letterToEditor.setFileName(manuScriptFile.getFileName());
			letterToEditor.setFilePath(manuScriptFile.getFilePath());
			letterToEditor.setFileSize(manuScriptFile.getFileSize());
			letterToEditor.setFileType(manuScriptFile.getFileType());
			letterToEditor.setAction(letterToEditor.getAction());
			letterToEditor.setCreatedOn(new Date());
			letterToEditorRepo.save(letterToEditor);
		} else {
			letter.setAction(letterToEditorVo.getAction());
			letter.setCreatedOn(new Date());
			letterToEditorRepo.updateLTEInvitation(letter.getId());
		}

		// Email Trigger
		EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
		emailTriggerVO.setJournalId(letterToEditorVo.getJournalId());
		emailTriggerVO.setUserId(letterToEditorVo.getUserId());
		if (letterToEditorVo.getEditorRole().equals(2))
			emailTriggerVO.setManagingEditorId(letterToEditorVo.getEditorId());
		else
			emailTriggerVO.setEditorUserId(letterToEditorVo.getEditorId());
		emailTriggerVO.setManuscriptId(letterToEditorVo.getManuscriptId());
		emailTriggerVO.setManuscriptStatus(letterToEditorVo.getManuscriptStatusId());
		emailTriggerVO.setReceiverRoleId(letterToEditorVo.getUserRole());
		emailTriggerVO.setAuthorUserId(letterToEditorVo.getUserId());
		if (letterToEditorVo.getTemplateId() != null) {
			emailTriggerVO.setTaskId(letterToEditorVo.getTemplateId());
		} else {
			emailTriggerVO.setTaskId(71);
		}
		emailTriggerVO.setRoleId(letterToEditorVo.getEditorRole());
		if (letterToEditorVo.getJournalMailTemplatesBody() != null
				&& !letterToEditorVo.getJournalMailTemplatesBody().isEmpty()) {
			emailTriggerVO.setEmailTemplateBody(letterToEditorVo.getJournalMailTemplatesBody());
		}
		if (letterToEditorVo.getJournalMailTemplatesSubject() != null
				&& !letterToEditorVo.getJournalMailTemplatesSubject().isEmpty()) {
			emailTriggerVO.setEmailTemplateSubject(letterToEditorVo.getJournalMailTemplatesSubject());
		}
		String message = emailTriggerService.emailTrigger(emailTriggerVO);

	}

	@Override
	public List<LetterToEditorVo> getAuthorResponseList(LetterToEditorVo letterToEditorVo) {
		List<LetterToEditorVo> commentaryList = new ArrayList<>();

		if (letterToEditorVo.getJournalId() != null && letterToEditorVo.getUserId() != null
				&& letterToEditorVo.getUserRole() != null) {
			List<LetterToEditor> commentaries = letterToEditorRepo
					.getAuthorResponseList(letterToEditorVo.getJournalId(), letterToEditorVo.getUserRole(),
							letterToEditorVo.getUserId())
					.stream().filter(obj -> !StringUtils.equalsIgnoreCase(obj.getAction(), "submitted"))
					.collect(Collectors.toList());

			commentaries.stream().forEach(commentary -> {
				LetterToEditorVo addComment = new LetterToEditorVo();
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
	public List<LetterToEditorVo> getAuthorResponse(Integer manuscriptId, Integer journalId) {
		List<LetterToEditorVo> list = new ArrayList<>();

		List<LetterToEditor> userList = letterToEditorRepo.getAuthorResponse(manuscriptId, journalId);

		for (LetterToEditor letterToEditor : userList) {
			LetterToEditorVo vo = new LetterToEditorVo();
			User usr = userRepo.getById(letterToEditor.getUserId());
			vo.setId(letterToEditor.getId());
			vo.setUserName(usr.getSalutation() + "." + usr.getFirstName() + " " + usr.getLastName());
			vo.setUserId(usr.getId());
			vo.setRoleName("Author");
			vo.setAction(letterToEditor.getAction());
			vo.setUserRole(letterToEditor.getUserRole());
			vo.setCreatedOn(letterToEditor.getCreatedOn());
			vo.setManuscriptId(letterToEditor.getManuscriptId());
			vo.setUserRole(letterToEditor.getUserRole());
			vo.setFilePath(letterToEditor.getFilePath());
			vo.setFileSize(letterToEditor.getFileSize());
			vo.setFileName(letterToEditor.getFileName());
			list.add(vo);
		}

		return list;
	}

	@Override
	public void approveInvitationStatus(Integer id, Integer userId, Date approvedOn, Integer manuscriptId,
			Integer userRole, String action) {
		letterToEditorRepo.approveInvitationStatus(userId, approvedOn, manuscriptId, userRole, action, id);
	}

	@Override
	public void rejectInvitationStatus(Integer id, Integer userId, Date rejectOn, Integer manuscriptId,
			Integer userRole, String action) {
		letterToEditorRepo.rejectInvitationStatus(userId, rejectOn, manuscriptId, userRole, action, id);
	}

	@Override
	public void submitInvitationStatus(Integer id, Integer userId, Date submittedDate, Integer manuscriptId,
			Integer userRole, String action) {
		letterToEditorRepo.submitInvitationStatus(userId, submittedDate, manuscriptId, userRole, action, id);
	}

	@Override
	public String deleteInvitation(LetterToEditorVo letterToEditorVo) {
		Integer deleteCount = letterToEditorRepo.deleteInvitation(letterToEditorVo.getJournalId(),
				letterToEditorVo.getUserId(), letterToEditorVo.getUserRole(), letterToEditorVo.getManuscriptId(),
				letterToEditorVo.getId());

		// Email Trigger
		String message = "";
		if (deleteCount > 0) {
			EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
			emailTriggerVO.setJournalId(letterToEditorVo.getJournalId());
			emailTriggerVO.setUserId(letterToEditorVo.getUserId());
			if (letterToEditorVo.getEditorRole().equals(2))
				emailTriggerVO.setManagingEditorId(letterToEditorVo.getEditorId());
			else
				emailTriggerVO.setEditorUserId(letterToEditorVo.getEditorId());
			emailTriggerVO.setManuscriptId(letterToEditorVo.getManuscriptId());
			emailTriggerVO.setManuscriptStatus(letterToEditorVo.getManuscriptStatusId());
			emailTriggerVO.setReceiverRoleId(letterToEditorVo.getUserRole());
			emailTriggerVO.setAuthorUserId(letterToEditorVo.getUserId());

			if (letterToEditorVo.getTemplateId() != null) {
				emailTriggerVO.setTaskId(letterToEditorVo.getTemplateId());
			} else {
				emailTriggerVO.setTaskId(72);
			}
			emailTriggerVO.setRoleId(letterToEditorVo.getEditorRole());
			if (letterToEditorVo.getJournalMailTemplatesBody() != null
					&& !letterToEditorVo.getJournalMailTemplatesBody().isEmpty()) {
				emailTriggerVO.setEmailTemplateBody(letterToEditorVo.getJournalMailTemplatesBody());
			}
			if (letterToEditorVo.getJournalMailTemplatesSubject() != null
					&& !letterToEditorVo.getJournalMailTemplatesSubject().isEmpty()) {
				emailTriggerVO.setEmailTemplateSubject(letterToEditorVo.getJournalMailTemplatesSubject());
			}
			message = emailTriggerService.emailTrigger(emailTriggerVO);
		} else {
			message = "Letter to editor invitation already deleted or not present";
		}

		return message;
	}

	@Override
	public Integer getManuscriptByStatusCount(ManuscriptDashboardList ms) {
		Integer totalCount = 0;
		List<Integer> manuStatus = Arrays.asList(33, 32, 59, 62, 65, 57, 61);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date submitDate = null;
		Date assignDate = null;
		if (ms.getSubmissionDate() != null) {
			try {
				submitDate = sdf.parse(sdf.format(ms.getSubmissionDate()));
			} catch (ParseException e) {
				logger.error("Exception while parisng submission date", e);
			}
		}

		if (ms.getAssignDate() != null) {
			try {
				assignDate = sdf.parse(sdf.format(ms.getAssignDate()));
			} catch (ParseException e) {
				logger.error("Exception while parisng submission date", e);
			}
		}

		totalCount = manuScriptForDashboardRepo.searchLTECount(manuStatus, ms.getJournalId(), ms.getManuscripCode(),
				ms.getIssueName(), ms.getAuthorName(), submitDate, assignDate, ms.getManuScriptType(),
				ms.getManuscripTitle());

		/*
		 * if(StringUtils.isNotBlank(manuscriptVO.getIssueName())) { totalCount =
		 * manuScriptForDashboardRepo.searchLTEbyIssueNameCount(manuStatus,
		 * manuscriptVO.getIssueName(), manuscriptVO.getJournalId()); }else {
		 * 
		 * if (manuscriptVO != null &&
		 * StringUtils.isNotBlank(manuscriptVO.getManuscripCode()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { totalCount =
		 * manuScriptForDashboardRepo.searchLTEbyManuscriptCodeCount(manuStatus,
		 * manuscriptVO.getManuscripCode(), manuscriptVO.getJournalId()); } else if
		 * (manuscriptVO != null && StringUtils.isNotBlank(manuscriptVO.getAuthorName())
		 * && StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { totalCount =
		 * manuScriptForDashboardRepo.searchLTEbyAuthorNameCount(manuStatus,
		 * manuscriptVO.getAuthorName(), manuscriptVO.getJournalId(),
		 * manuscriptVO.getRoleId(), manuscriptVO.getManuScriptId()); } else if
		 * (manuscriptVO != null && StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() != null && manuscriptVO.getAssignDate() ==
		 * null ) { String submitDate = sdf.format(manuscriptVO.getSubmissionDate());
		 * try {
		 * 
		 * totalCount =
		 * manuScriptForDashboardRepo.searchLTEbySubmissionDateCount(manuStatus,
		 * sdf.parse(submitDate), manuscriptVO.getJournalId()); } catch (ParseException
		 * e) { e.printStackTrace(); } } else if (manuscriptVO != null &&
		 * StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() !=
		 * null ) { String acceptDate = sdf.format(manuscriptVO.getAssignDate()); try {
		 * totalCount =
		 * manuScriptForDashboardRepo.searchLTEbyAssignedDateCount(manuStatus,
		 * sdf.parse(acceptDate), manuscriptVO.getJournalId()); } catch (ParseException
		 * e) { e.printStackTrace(); } } else if (manuscriptVO != null &&
		 * StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isNotBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { totalCount =
		 * manuScriptForDashboardRepo.searchLTEbyArticleTypeCount(manuStatus,
		 * manuscriptVO.getManuScriptType(), manuscriptVO.getJournalId(),
		 * manuscriptVO.getRoleId()); } else if (manuscriptVO != null &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripCode()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isBlank(manuscriptVO.getManuscripTitle()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { totalCount =
		 * manuScriptForDashboardRepo.getManuscriptForLetterToEditorCount(manuStatus,
		 * manuscriptVO.getJournalId(), manuscriptVO.getRoleId()); }else if
		 * (manuscriptVO != null && StringUtils.isBlank(manuscriptVO.getManuscripCode())
		 * && StringUtils.isBlank(manuscriptVO.getManuScriptType()) &&
		 * StringUtils.isBlank(manuscriptVO.getAuthorName()) &&
		 * StringUtils.isNotBlank(manuscriptVO.getManuscripTitle()) &&
		 * manuscriptVO.getSubmissionDate() == null && manuscriptVO.getAssignDate() ==
		 * null ) { totalCount =
		 * manuScriptForDashboardRepo.searchLTEbyManuscriptTitleCount(manuStatus,
		 * manuscriptVO.getManuscripTitle(), manuscriptVO.getJournalId()); } }
		 */

		return totalCount;
	}

	@Override
	public List<String> getIssueList(Integer journalId) {
		List<String> issueList = manuScriptForDashboardRepo.getDistinctIssueList(journalId);

		return issueList.stream().filter(obj -> StringUtils.isNotBlank(obj)).collect(Collectors.toList());
	}
}
