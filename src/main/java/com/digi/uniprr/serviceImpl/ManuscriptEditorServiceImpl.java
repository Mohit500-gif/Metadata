package com.digi.uniprr.serviceImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManagingEditorVO;
import com.digi.uniprr.VO.ManuScriptEditorCommonVO;
import com.digi.uniprr.VO.ManuScriptReviewerCommonVO;
import com.digi.uniprr.VO.ManuscriptEditorVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptManagingEditorVO;
import com.digi.uniprr.VO.SendBackToAuthorVo;
import com.digi.uniprr.VO.UserJournalManuscriptVo;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptFile;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuScriptReviewerChecklist;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.ManuscriptEditorChecklist;
import com.digi.uniprr.model.ManuscriptEditorComment;
import com.digi.uniprr.model.ManuscriptEditorComments;
import com.digi.uniprr.model.ManuscriptManagingEditor;
import com.digi.uniprr.model.ManuscriptReviewerComments;
import com.digi.uniprr.model.ManuscriptTypeDetails;
import com.digi.uniprr.model.StatusMaster;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorChecklistRepo;
import com.digi.uniprr.repository.ManuscriptEditorCommentRepo;
import com.digi.uniprr.repository.ManuscriptEditorCommentsRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.ManuscriptManagingEditorRepo;
import com.digi.uniprr.repository.ManuscriptTypeDetailsRepo;
import com.digi.uniprr.repository.StatusMasterRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuscriptEditorService;

@Service
@Transactional
public class ManuscriptEditorServiceImpl implements ManuscriptEditorService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	ManuscriptEditorChecklistRepo manuscriptEditorChecklistRepo;

	@Autowired
	ManuscriptEditorCommentsRepo manuscriptEditorCommentsRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	ManuscriptManagingEditorRepo manuscriptManagingEditorRepo;

	@Autowired
	StatusMasterRepo statusMasterRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;

	@Autowired
	ManuscriptEditorCommentRepo manuscriptEditorCommentRepo;
	
	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	Environment environment;

	@Value("${uniprrFolderPath}")
	private String UPLOAD_FOLDER;

	@Override
	public void createManuscriptEditor(ManuscriptEditor manuscriptEditor) {
		manuscriptEditorRepo.save(manuscriptEditor);

	}

	@Override
	public List<ManuscriptEditor> getManuscriptEditor(UserJournalManuscriptVo userJournalManuscriptVo) {
		List<ManuscriptEditor> dataList = new ArrayList<>();
		for (Integer statusList : userJournalManuscriptVo.getManuscriptStatusId()) {
			List<ManuscriptEditor> list = new ArrayList<>();
			Integer status = statusList;
			System.out.println(status);
			list = manuscriptEditorRepo.getManuscriptEditor(status, userJournalManuscriptVo.getUserId(),
					userJournalManuscriptVo.getJournalId());
			if (list != null && !list.isEmpty()) {
				for (ManuscriptEditor listOfData : list) {
					dataList.add(listOfData);
				}
			}

		}
		return dataList;
	}

	@Override
	public List<ManuscriptEditor> getManuscriptEditorListById(UserJournalManuscriptVo userJournalManuscriptVo) {
		List<ManuscriptEditor> dataList = new ArrayList<>();
		if (userJournalManuscriptVo.getJournalId() != null && userJournalManuscriptVo.getManuscriptStatusId() != null) {
			for (Integer statusList : userJournalManuscriptVo.getManuscriptStatusId()) {
				List<ManuscriptEditor> list = new ArrayList<>();
				Integer status = statusList;
				list = manuscriptEditorRepo.getManuscriptEditorById(status, userJournalManuscriptVo.getJournalId());
				if (list != null && !list.isEmpty()) {
					for (ManuscriptEditor listOfData : list) {
						dataList.add(listOfData);
					}
				}
			}
		}
		return dataList;
	}

	@Override
	public void saveManuScriptReviewerDetails(ManuscriptEditor manuscriptEditor) {
		manuscriptEditorRepo.save(manuscriptEditor);
	}

	@Override
	public void updateEditorDetails(ManuscriptListVO manuscriptListVO) {
		manuscriptEditorRepo.updateEditorDetails(manuscriptListVO.getManuStatusId(),
				manuscriptListVO.getManuScriptId());
	}

	@Override
	public ManuscriptEditor findManuscriptEditorDetails(Integer manuScriptId, Integer manuscriptVersion,
			Integer journalId, Integer userId, Integer assignToId) {
		return manuscriptEditorRepo.findManuscriptEditorDetails(manuScriptId, manuscriptVersion, journalId, assignToId);
	}

	@Override
	public String addAllDetailsMultipleTableRCC(ManuScriptEditorCommonVO manuScriptEditorCommonVO) {

		List<ManuscriptEditor> data = manuscriptEditorRepo.getmanuScriptEditorByIdAddMultiple(
				manuScriptEditorCommonVO.getManuscriptId(), manuScriptEditorCommonVO.getEditorId(),
				manuScriptEditorCommonVO.getManuscriptVersion());

		if (data != null && !data.isEmpty()) {
			ManuscriptEditor manuScriptEditorData1 = data.get(0);
			ManuscriptEditor manuScriptEditorData = manuscriptEditorRepo.getOne(manuScriptEditorData1.getId());

			if (manuScriptEditorCommonVO.getManuscriptStatus() != null) {
				manuScriptEditorData.setManuscriptStatus(manuScriptEditorCommonVO.getManuscriptStatus());
			}
			if (manuScriptEditorCommonVO.getDueDate() != null) {
				manuScriptEditorData.setDueDate(manuScriptEditorCommonVO.getDueDate());
			}
			if (manuScriptEditorCommonVO.getJournalId() != null) {
				manuScriptEditorData.setJournalId(manuScriptEditorCommonVO.getJournalId());
			}
			if (manuScriptEditorCommonVO.getDecisionLetter() != null) {
				manuScriptEditorData.setDecisionLetter(manuScriptEditorCommonVO.getDecisionLetter());
			}
			if (manuScriptEditorCommonVO.getCommentsForEditor() != null) {
				manuScriptEditorData.setCommentsForEditor(manuScriptEditorCommonVO.getCommentsForEditor());
			}
			if (manuScriptEditorCommonVO.getCommentsForAuthor() != null) {
				manuScriptEditorData.setCommentsForAuthor(manuScriptEditorCommonVO.getCommentsForAuthor());
			}
			if (manuScriptEditorCommonVO.getCommentsForReviewer() != null) {
				manuScriptEditorData.setCommentsForReviewer(manuScriptEditorCommonVO.getCommentsForReviewer());
			}
			if (manuScriptEditorCommonVO.getCommentsForManagingEditor() != null) {
				manuScriptEditorData
						.setCommentsForManagingEditor(manuScriptEditorCommonVO.getCommentsForManagingEditor());
			}
			if (manuScriptEditorCommonVO.getCreatedBy() != null) {
				manuScriptEditorData.setCreatedBy(manuScriptEditorCommonVO.getCreatedBy());
			}
			if (manuScriptEditorCommonVO.getCreatedOn() != null) {
				manuScriptEditorData.setCreatedOn(manuScriptEditorCommonVO.getCreatedOn());
			}
			if (manuScriptEditorCommonVO.getUpdatedBy() != null) {
				manuScriptEditorData.setUpdatedBy(manuScriptEditorCommonVO.getUpdatedBy());
			}
			if (manuScriptEditorCommonVO.getUpdatedOn() != null) {
				manuScriptEditorData.setUpdatedOn(manuScriptEditorCommonVO.getUpdatedOn());
			}
			if (manuScriptEditorCommonVO.getIsActive() != null) {
				manuScriptEditorData.setIsActive(manuScriptEditorCommonVO.getIsActive());
			}
			manuscriptEditorRepo.save(manuScriptEditorData);
		} else {
			ManuscriptEditor manuScriptEditorData = new ManuscriptEditor();
			manuScriptEditorData.setManuscriptId(manuScriptEditorCommonVO.getManuscriptId());
			manuScriptEditorData.setManuscriptVersion(manuScriptEditorCommonVO.getManuscriptVersion());
			manuScriptEditorData.setEditorId(manuScriptEditorCommonVO.getEditorId());
			manuScriptEditorData.setManuscriptStatus(manuScriptEditorCommonVO.getManuscriptStatus());
			manuScriptEditorData.setJournalId(manuScriptEditorCommonVO.getJournalId());
			manuScriptEditorData.setDecisionLetter(manuScriptEditorCommonVO.getDecisionLetter());
			manuScriptEditorData.setCommentsForAuthor(manuScriptEditorCommonVO.getCommentsForAuthor());
			manuScriptEditorData.setCommentsForEditor(manuScriptEditorCommonVO.getCommentsForEditor());
			manuScriptEditorData.setCommentsForReviewer(manuScriptEditorCommonVO.getCommentsForReviewer());
			manuScriptEditorData.setCommentsForManagingEditor(manuScriptEditorCommonVO.getCommentsForManagingEditor());
			manuScriptEditorData.setCreatedBy(manuScriptEditorCommonVO.getCreatedBy());
			manuScriptEditorData.setCreatedOn(manuScriptEditorCommonVO.getCreatedOn());
			manuScriptEditorData.setUpdatedBy(manuScriptEditorCommonVO.getUpdatedBy());
			manuScriptEditorData.setUpdatedOn(manuScriptEditorCommonVO.getUpdatedOn());
			manuScriptEditorData.setIsActive(manuScriptEditorCommonVO.getIsActive());
			manuscriptEditorRepo.save(manuScriptEditorData);

		}

		for (int i = 0; i <= manuScriptEditorCommonVO.getEditor().size() - 1; i++) {
			ManuscriptEditorChecklist saved1 = new ManuscriptEditorChecklist();
			saved1.setManuscriptId(manuScriptEditorCommonVO.getManuscriptId());
			saved1.setManuscriptVersion(manuScriptEditorCommonVO.getManuscriptVersion());
			saved1.setEditorId(manuScriptEditorCommonVO.getEditorId());
			saved1.setChkId(manuScriptEditorCommonVO.getEditor().get(i).getChkId());
			saved1.setChkType(manuScriptEditorCommonVO.getEditor().get(i).getChkType());
			saved1.setChckComments(manuScriptEditorCommonVO.getEditor().get(i).getChckComments());
			saved1.setCreatedBy(manuScriptEditorCommonVO.getCreatedBy());
			saved1.setCreatedOn(manuScriptEditorCommonVO.getCreatedOn());
			saved1.setUpdatedBy(manuScriptEditorCommonVO.getUpdatedBy());
			saved1.setUpdatedOn(manuScriptEditorCommonVO.getUpdatedOn());
			manuscriptEditorChecklistRepo.save(saved1);
		}

		return "Data Insert Succesfully";
	}

	@Override
	public void addsendBackToAuthor(ManuScriptEditorCommonVO manuScriptEditorCommonVO) {
		List<ManuScript> Data1 = manuScriptRepo.getAllDetails(manuScriptEditorCommonVO.getManuscriptId());
		if (manuScriptEditorCommonVO.getManuscriptStatus() != null
				&& manuScriptEditorCommonVO.getManuscriptStatus().equals(4)) {
			manuScriptReviewerRepo.updateManuScriptEditorReviewer(manuScriptEditorCommonVO.getManuscriptId(),
					manuScriptEditorCommonVO.getManuscriptVersion(), 0);
		}
		if (manuScriptEditorCommonVO.getManuscriptStatus() != null) {
			ManuscriptEditorComment addManuscriptEditorComment = new ManuscriptEditorComment();
			addManuscriptEditorComment.setManuscriptId(manuScriptEditorCommonVO.getManuscriptId());
			addManuscriptEditorComment.setManuscriptVersion(manuScriptEditorCommonVO.getManuscriptVersion());
			addManuscriptEditorComment.setUserId(manuScriptEditorCommonVO.getEditorId());
			addManuscriptEditorComment.setRoleId(manuScriptEditorCommonVO.getRoleId());
			addManuscriptEditorComment.setStatusId(manuScriptEditorCommonVO.getManuscriptStatus());
			addManuscriptEditorComment.setComment(manuScriptEditorCommonVO.getCommentsForAuthor());
			addManuscriptEditorComment.setCreatedAt(new Date());
			manuscriptEditorCommentRepo.save(addManuscriptEditorComment);

		}
		if (Data1 != null && !Data1.isEmpty()) {
			ManuScript manuData1 = Data1.get(0);
			ManuScript manuData = manuScriptRepo.getOne(manuData1.getManuScriptId());

			if (manuScriptEditorCommonVO.getManuscriptStatus() != null) {
				manuData.setManuscriptStatusId(manuScriptEditorCommonVO.getManuscriptStatus());
			}

			if (manuScriptEditorCommonVO.getUpdatedOn() != null) {
				manuData.setUpdatedOn(manuScriptEditorCommonVO.getUpdatedOn());
			}

		}
		List<ManuscriptEditor> data2 = manuscriptEditorRepo.getmanuScriptEditorByIdAddMultiple(
				manuScriptEditorCommonVO.getManuscriptId(), manuScriptEditorCommonVO.getEditorId(),
				manuScriptEditorCommonVO.getManuscriptVersion());
		if (data2 != null && !data2.isEmpty()) {
			ManuscriptEditor manuScriptEditorData1 = data2.get(0);
			ManuscriptEditor manuScriptEditorData = manuscriptEditorRepo.getOne(manuScriptEditorData1.getId());

			if (manuScriptEditorCommonVO.getManuscriptStatus() != null) {
				manuScriptEditorData.setManuscriptStatus(manuScriptEditorCommonVO.getManuscriptStatus());
			}
			if (manuScriptEditorCommonVO.getUpdatedOn() != null) {
				manuScriptEditorData.setUpdatedOn(manuScriptEditorCommonVO.getUpdatedOn());
			}
			if (manuScriptEditorCommonVO.getUpdatedBy() != null) {
				manuScriptEditorData.setUpdatedBy(manuScriptEditorCommonVO.getUpdatedBy());
			}
			if (manuScriptEditorCommonVO.getCreatedOn() != null) {
				manuScriptEditorData.setCreatedOn(manuScriptEditorCommonVO.getCreatedOn());
			}
			if (manuScriptEditorCommonVO.getCreatedBy() != null) {
				manuScriptEditorData.setCreatedBy(manuScriptEditorCommonVO.getCreatedBy());
			}
			if (manuScriptEditorCommonVO.getCommentsForAuthor() != null) {
				manuScriptEditorData.setCommentsForAuthor(manuScriptEditorCommonVO.getCommentsForAuthor());
			}

			manuscriptEditorRepo.save(manuScriptEditorData);
			manuScriptRepo.updateStage(manuScriptEditorCommonVO.getManuscriptStatus(),
					manuScriptEditorCommonVO.getManuscriptId());

		} else {
			ManuscriptEditor manuScriptEditorData = new ManuscriptEditor();

			manuScriptEditorData.setManuscriptId(manuScriptEditorCommonVO.getManuscriptId());
			manuScriptEditorData.setManuscriptVersion(manuScriptEditorCommonVO.getManuscriptVersion());
			manuScriptEditorData.setEditorId(manuScriptEditorCommonVO.getEditorId());
			manuScriptEditorData.setManuscriptStatus(manuScriptEditorCommonVO.getManuscriptStatus());
			manuScriptEditorData.setCommentsForAuthor(manuScriptEditorCommonVO.getCommentsForAuthor());
			manuScriptEditorData.setUpdatedOn(manuScriptEditorCommonVO.getUpdatedOn());
			manuScriptEditorData.setUpdatedBy(manuScriptEditorCommonVO.getUpdatedBy());
			manuScriptEditorData.setCreatedOn(manuScriptEditorCommonVO.getCreatedOn());
			manuScriptEditorData.setCreatedBy(manuScriptEditorCommonVO.getCreatedBy());
			manuScriptEditorData.setJournalId(manuScriptEditorCommonVO.getJournalId());

			manuscriptEditorRepo.save(manuScriptEditorData);
			manuScriptRepo.updateStage(manuScriptEditorCommonVO.getManuscriptStatus(),
					manuScriptEditorCommonVO.getManuscriptId());

		}

	}

	@Override
	public List<ManuscriptListVO> getEditorRecord(ManuscriptListVO manuscriptListVO) {
		List<ManuscriptListVO> newList = new ArrayList<>();
		List<ManuscriptEditor> getEditorRecord = manuscriptEditorRepo
				.getEditorRecord(manuscriptListVO.getManuscriptStatusId(), manuscriptListVO.getJournalId());
		if (getEditorRecord != null && !getEditorRecord.isEmpty()) {
			getEditorRecord.stream().forEach(i -> {
				ManuscriptListVO manuscriptList = new ManuscriptListVO();
				ManuScript j = manuScriptRepo.getOne(i.getManuscriptId());
				if (j != null) {
					manuscriptListVO.setJournalId(i.getJournalId());
					manuscriptList.setManuscripCode(j.getManuscriptCode());
					manuscriptList.setManuscriptAuthorId(j.getAuthorId());
					manuscriptList.setManuscripTitle(j.getManuscriptTitle());
					if (j.getManuscriptTypeDetails() != null)
						manuscriptList.setManuScriptType(j.getManuscriptTypeDetails().getManuscriptType());
					manuscriptList.setManuScriptId(j.getManuScriptId());
					manuscriptList.setManuscriptVersion(j.getManuscriptVersion());
					manuscriptList.setSubmissionDate(j.getSubmissionDate());
					manuscriptList.setSubmittedBy(j.getSubmittedBy());
					manuscriptList.setAuthorName(j.getUser().getFirstName() + " " + j.getUser().getLastName());
					manuscriptList.setCreatedBy(j.getCreatedBy());
					manuscriptList.setCreatedOn(j.getCreatedOn());
					manuscriptList.setUpdatedBy(j.getUpdatedBy());
				}
				newList.add(manuscriptList);
			});
			return newList;
		}
		return null;
	}

	@Override
	public String updateComment(ManuscriptEditorVO manuscriptEditor) {
		ManuScript manuScript = manuScriptRepo.getById(manuscriptEditor.getManuscriptId());
		if(!manuScript.getAssignedUserRole().equals(2))
		{
		List<UserJournal> userJournalList = userJournalRepo.getUserRoleJournalId(manuscriptEditor.getJournalId(), 2);
		List<Integer> activeUsers = new ArrayList<>();
		if(userJournalList != null && !userJournalList.isEmpty()) {
			activeUsers = userJournalList.stream().map(j -> j.getUserId()).collect(Collectors.toList());
		}
		if (!activeUsers.contains(manuscriptEditor.getManagingEditorId())) {
			Integer assignedUserId = activeUsers.get(0);
			manuscriptEditor.setManagingEditorId(assignedUserId);
			}
		manuScript.setAssignedUser(manuscriptEditor.getManagingEditorId());	
		manuScript.setManuscriptStatusId(manuscriptEditor.getManuscriptStatus());
		manuScript.setAssignedUserRole(2);
		manuScriptRepo.save(manuScript);

		List<ManuScriptReviewer> withdrwanreviewerList = manuScriptReviewerRepo
				.getWorkingReviewerList(manuScript.getManuScriptId(), manuScript.getManuscriptVersion());

		if (withdrwanreviewerList != null && !withdrwanreviewerList.isEmpty()) {
			withdrwanreviewerList.stream().forEach(j -> {
				
					if(manuscriptEditor.getManuscriptStatus()!=null && (manuscriptEditor.getManuscriptStatus().equals(51) || manuscriptEditor.getManuscriptStatus().equals(52)|| manuscriptEditor.getManuscriptStatus().equals(68)) &&
							(j.getManuscriptStatus().equals(8) || j.getManuscriptStatus().equals(66) || j.getManuscriptStatus().equals(35)))
					{
						j.setIsActive(0);
						manuScriptReviewerRepo.save(j);
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(manuscriptEditor.getJournalId());
						emailTriggerVO.setUserId(j.getReviewerId());
						emailTriggerVO.setReviewerUserId(j.getReviewerId());
						emailTriggerVO.setManuscriptId(manuscriptEditor.getManuscriptId());
						emailTriggerVO.setTaskId(87);
						emailTriggerVO.setReceiverRoleId(5);
						emailTriggerVO.setEditorUserId(manuscriptEditor.getEditorId());
						emailTriggerVO.setRoleId(3);
						emailTriggerVO.setManuscriptStatus(manuscriptEditor.getManuscriptStatus());
						log.info(
								"Inside in updateComment as Mail Will Trigger To Eic Email Trigger Data are:->"
										+ emailTriggerVO.toString());
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						log.info("Message of updateComment as a Mail Will Trigger To Reviewer Email Trigger "
								+ message);						
						
					}
			});
		}
		
		ManuscriptEditor manuscriptEditorData = manuscriptEditorRepo.findManuscriptEditorDetails(
				manuscriptEditor.getManuscriptId(), manuscriptEditor.getManuscriptVersion(),
				manuscriptEditor.getJournalId(), manuscriptEditor.getEditorId());

		if (manuscriptEditorData != null && manuscriptEditor.getManagingEditorId() != null)
		{
			if( manuscriptEditor.getManuscriptStatus()!=null && manuscriptEditor.getManuscriptStatus().equals(51) || manuscriptEditor.getManuscriptStatus().equals(52)|| manuscriptEditor.getManuscriptStatus().equals(68))
			{

				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(manuscriptEditor.getJournalId());
				emailTriggerVO.setUserId(manuscriptEditor.getManagingEditorId());
				emailTriggerVO.setManuscriptId(manuscriptEditor.getManuscriptId());
				emailTriggerVO.setTaskId(8);
				emailTriggerVO.setReceiverRoleId(2);
				emailTriggerVO.setManuscriptStatus(manuscriptEditor.getManuscriptStatus());
				if (manuscriptEditor.getJournalMailTemplatesBody() != null && !manuscriptEditor.getJournalMailTemplatesBody().isEmpty())
					emailTriggerVO.setEmailTemplateBody(manuscriptEditor.getJournalMailTemplatesBody());
				
				if (manuscriptEditor.getJournalMailTemplatesSubject() != null && !manuscriptEditor.getJournalMailTemplatesSubject().isEmpty())
					emailTriggerVO.setEmailTemplateSubject(manuscriptEditor.getJournalMailTemplatesSubject());
				
				
				log.info(
						"Inside in updateComment as Mail Will Trigger To Eic Email Trigger Data are:->"
								+ emailTriggerVO.toString());
				log.info("User Receiver Id:->" + manuscriptEditor.getManagingEditorId());
				log.info("User Receiver Role Id:->" + 3);
				log.info("User Sender Role Id:->" + 2);
				log.info("User Sender Id:->" + manuscriptEditor.getEditorId());
				log.info("Task Id:->" + 8);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				log.debug("Emil Trigger" + message);
				log.info("Message of updateComment as a Mail Will Trigger To Eic Email Trigger "
						+ message);
				log.info("End of updateComment as a Mail Will Trigger To Eic Email Trigger ");
				
				
			}
			//
			List<ManuscriptEditor> data = manuscriptEditorRepo.getByVersion(
					manuscriptEditor.getManuscriptId(), manuscriptEditor.getManuscriptVersion());
			if (data != null && !data.isEmpty()) {
				for (ManuscriptEditor manuscriptEditor2 : data) {
					manuscriptEditor2.setIsActive(0);
					manuscriptEditor2.setSendBackEic(0);
					manuscriptEditorRepo.save(manuscriptEditor2);
				}
			}
			ManuscriptEditor manuscriptEditorData1=new ManuscriptEditor();
			manuscriptEditorData1.setManuscriptId(manuscriptEditor.getManuscriptId());
			manuscriptEditorData1.setManuscriptVersion(manuscriptEditor.getManuscriptVersion());
			manuscriptEditorData1.setJournalId(manuscriptEditor.getJournalId());
			manuscriptEditorData1.setEditorId(manuscriptEditor.getEditorId());
			manuscriptEditorData1.setManagingEditorId(manuscriptEditor.getManagingEditorId());
			manuscriptEditorData1.setManuscriptStatus(manuscriptEditor.getManuscriptStatus());
			manuscriptEditorData1.setCommentsForEditor(manuscriptEditor.getCommentsForAuthor());
			manuscriptEditorData1.setIsActive(0);
			manuscriptEditorData1.setSendBackEic(0);

			manuscriptEditorRepo.save(manuscriptEditorData1);
			
		}
		 return "Comments updated successfully.";
	} else {
		
		return "Article already processed.";
	}
		

	}

	@Override
	public List<ManuscriptListVO> getAssignedEditor(IDVO id) {
		List<ManuscriptListVO> newList = new ArrayList<>();
		List<ManuscriptEditor> manuscriptEditorList = manuscriptEditorRepo.getAssignedEditor(id.getJournalId(),
				id.getUserId(), 3);
		List<Integer> manuscripId = new ArrayList();
		if (manuscriptEditorList != null && !manuscriptEditorList.isEmpty()) {
			for (ManuscriptEditor manuscriptEditor : manuscriptEditorList) {
				manuscripId.add(manuscriptEditor.getManuscriptId());
			}
		}
		if (manuscripId != null && !manuscripId.isEmpty()) {
			List<ManuScript> manuscriptList = manuScriptRepo.getSubmittedManuscriptsList(manuscripId);
			if (manuscriptList != null && !manuscriptList.isEmpty()) {
				StatusMaster statusCode = statusMasterRepo.getOne(3);
				for (ManuScript data : manuscriptList) {

					List<ManuscriptTypeDetails> manuScriptType = manuscriptTypeDetailsRepo
							.getManuscriptTypeDetailsById(data.getManuScriptId());
					User user = userRepo.getOne(data.getAuthorId());
					ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
					manuscriptListVO.setCreatedBy(data.getCreatedBy());
					manuscriptListVO.setCreatedOn(data.getCreatedOn());
					manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setStatusValueForAuthor(statusCode.getStatusValueForAuthor());
					manuscriptListVO.setStatusValueForEditor(statusCode.getStatusValueForEditor());
					manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
					manuscriptListVO.setManuStatusCode(statusCode.getStatusValue());
					manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
					manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
					manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
					manuscriptListVO.setManuStatusId(3);
					if (manuScriptType != null && !manuScriptType.isEmpty()) {
						manuscriptListVO.setManuScriptType(manuScriptType.get(0).getManuscriptType());
					}

					List<ManuscriptManagingEditor> manuscriptManagingEditor = manuscriptManagingEditorRepo
							.getByJournalAndManuscriptId(data.getManuScriptId(), data.getJournalId());
					if (manuscriptManagingEditor != null && !manuscriptManagingEditor.isEmpty()) {
						List<ManuscriptManagingEditorVO> mmeVoList = new ArrayList<>();
						for (ManuscriptManagingEditor mme : manuscriptManagingEditor) {
							User managingEditor = userRepo.getOne(data.getAuthorId());
							ManuscriptManagingEditorVO mmeVO = new ManuscriptManagingEditorVO();
							mmeVO.setUserId(mme.getUserId());
							mmeVO.setName(managingEditor.getFirstName() + " " + managingEditor.getLastName());
							mmeVoList.add(mmeVO);

						}
						manuscriptListVO.setManagingEditor(mmeVoList);
					}

					List<ManuscriptEditor> me = manuscriptEditorRepo.getByJournalAndManuscript(data.getManuScriptId(),
							data.getJournalId());
					String editorName = "";
					if (me != null && !me.isEmpty()) {
						List<ManagingEditorVO> meVOList = new ArrayList<>();
						for (ManuscriptEditor meData : me) {
							User editor = userRepo.getOne(meData.getEditorId());
							if (meData.getManagingEditorId() != null) {
								if (editorName.equalsIgnoreCase(""))
									editorName = editor.getFirstName() + " " + editor.getLastName();
								else
									editorName = editorName + "," + editor.getFirstName() + " " + editor.getLastName();
							}
							ManagingEditorVO meVO = new ManagingEditorVO();
							meVO.setEditorId(meData.getEditorId());
							meVO.setManagingEditorId(meData.getManagingEditorId());
							meVO.setName(editor.getFirstName() + " " + editor.getLastName());
							manuscriptListVO.setManagingEditorId(meData.getManagingEditorId());
							meVOList.add(meVO);
						}
						manuscriptListVO.setEditorList(meVOList);
						manuscriptListVO.setEditorName(editorName);

					}
					newList.add(manuscriptListVO);

				}
			}
		}
		return newList;
	}

	@Override
	public String uploadSendBackToAuthor(SendBackToAuthorVo sendBackToAuthorVo, HttpServletRequest request) {
		log.info("-------------------Inside In  uploadSendBackToAuthor------------------");
		BufferedOutputStream stream = null;
		try {
			List<ManuScript> Data1 = manuScriptRepo.getAllDetails(sendBackToAuthorVo.getManuscriptId());
			if (sendBackToAuthorVo.getManuscriptStatus() != null
					&& sendBackToAuthorVo.getManuscriptStatus().equals(4)) {

				List<ManuScriptReviewer> withdrwanreviewerList = manuScriptReviewerRepo
						.getWorkingReviewerList(sendBackToAuthorVo.getManuscriptId(), sendBackToAuthorVo.getManuscriptVersion());

				if (withdrwanreviewerList != null && !withdrwanreviewerList.isEmpty()) {
					for(ManuScriptReviewer j : withdrwanreviewerList) {

							if(j.getManuscriptStatus().equals(8) || j.getManuscriptStatus().equals(66) || j.getManuscriptStatus().equals(35))
							{
								j.setIsActive(0);
								manuScriptReviewerRepo.save(j);
								EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
								emailTriggerVO.setJournalId(sendBackToAuthorVo.getJournalId());
								emailTriggerVO.setUserId(j.getReviewerId());
								emailTriggerVO.setReviewerUserId(j.getReviewerId());
								emailTriggerVO.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
								emailTriggerVO.setTaskId(87);
								emailTriggerVO.setReceiverRoleId(5);
								emailTriggerVO.setEditorUserId(sendBackToAuthorVo.getEditorId());
								emailTriggerVO.setRoleId(sendBackToAuthorVo.getRoleId());
								emailTriggerVO.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
								String message = emailTriggerService.emailTrigger(emailTriggerVO);
								log.info(" Email Trigger to reviewer who has not reviewed the manuscript ::::"+ "user id = "+ j.getReviewerId() +" Message :::"
										+ message);														
							}
					}
				}
				
			}
			if (Data1 != null && !Data1.isEmpty()) {
				ManuScript manuData1 = Data1.get(0);
				ManuScript manuData = manuScriptRepo.getOne(manuData1.getManuScriptId());

				if (sendBackToAuthorVo.getManuscriptStatus() != null) {
					manuData.setManuscriptStatusId(sendBackToAuthorVo.getManuscriptStatus());
				}

				if (sendBackToAuthorVo.getUpdatedOn() != null) {
					manuData.setUpdatedOn(sendBackToAuthorVo.getUpdatedOn());
				}

			}
			List<ManuscriptEditor> data2 = manuscriptEditorRepo.getmanuScriptEditorByIdAddMultiple(
					sendBackToAuthorVo.getManuscriptId(), sendBackToAuthorVo.getEditorId(),
					sendBackToAuthorVo.getManuscriptVersion());
			if (data2 != null && !data2.isEmpty()) {
				log.info("-------------------Get Manuscript Editor------------------");
				ManuscriptEditor manuScriptEditorData1 = data2.get(0);
				ManuscriptEditor manuScriptEditorData = manuscriptEditorRepo.getOne(manuScriptEditorData1.getId());
				if (sendBackToAuthorVo.getManuscriptStatus() != null) {
					manuScriptEditorData.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
				}
				if (sendBackToAuthorVo.getUpdatedOn() != null) {
					manuScriptEditorData.setUpdatedOn(sendBackToAuthorVo.getUpdatedOn());
				}
				if (sendBackToAuthorVo.getUpdatedBy() != null) {
					manuScriptEditorData.setUpdatedBy(sendBackToAuthorVo.getUpdatedBy());
				}
				if (sendBackToAuthorVo.getCreatedOn() != null) {
					manuScriptEditorData.setCreatedOn(sendBackToAuthorVo.getCreatedOn());
				}
				if (sendBackToAuthorVo.getCreatedBy() != null) {
					manuScriptEditorData.setCreatedBy(sendBackToAuthorVo.getCreatedBy());
				}
				if (sendBackToAuthorVo.getCommentsForAuthor() != null) {
					manuScriptEditorData.setCommentsForAuthor(sendBackToAuthorVo.getCommentsForAuthor());
				}
				manuscriptEditorRepo.save(manuScriptEditorData);
				manuScriptRepo.updateStage1(sendBackToAuthorVo.getManuscriptStatus(),
						sendBackToAuthorVo.getManuscriptId(),sendBackToAuthorVo.getEditorId(), sendBackToAuthorVo.getRoleId());
				log.info("-------------------Updated manuscript Editor AND Manuscript------------------");
			} else {
				log.info("-------------------Creation Of Manuscipt Editor------------------");
				ManuscriptEditor manuScriptEditorData = new ManuscriptEditor();
				manuScriptEditorData.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
				manuScriptEditorData.setManuscriptVersion(sendBackToAuthorVo.getManuscriptVersion());
				manuScriptEditorData.setEditorId(sendBackToAuthorVo.getEditorId());
				manuScriptEditorData.setManuscriptStatus(sendBackToAuthorVo.getManuscriptStatus());
				manuScriptEditorData.setCommentsForAuthor(sendBackToAuthorVo.getCommentsForAuthor());
				manuScriptEditorData.setUpdatedOn(sendBackToAuthorVo.getUpdatedOn());
				manuScriptEditorData.setUpdatedBy(sendBackToAuthorVo.getUpdatedBy());
				manuScriptEditorData.setCreatedOn(sendBackToAuthorVo.getCreatedOn());
				manuScriptEditorData.setCreatedBy(sendBackToAuthorVo.getCreatedBy());
				manuScriptEditorData.setJournalId(sendBackToAuthorVo.getJournalId());
				manuscriptEditorRepo.save(manuScriptEditorData);
//				manuScriptRepo.updateStage(sendBackToAuthorVo.getManuscriptStatus(),
//						sendBackToAuthorVo.getManuscriptId());
				manuScriptRepo.updateStage1(sendBackToAuthorVo.getManuscriptStatus(),
						sendBackToAuthorVo.getManuscriptId(),sendBackToAuthorVo.getEditorId(), sendBackToAuthorVo.getRoleId());
				log.info("-------------------Creation manuscript Editor AND Update Manuscript------------------");
			}
			if (sendBackToAuthorVo.getManuscriptStatus() != null) {
				log.info("-------------------Creation manuscript Editor Comment ------------------");
				ManuscriptEditorComment addManuscriptEditorComment = new ManuscriptEditorComment();
				addManuscriptEditorComment.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
				addManuscriptEditorComment.setManuscriptVersion(sendBackToAuthorVo.getManuscriptVersion());
				addManuscriptEditorComment.setUserId(sendBackToAuthorVo.getEditorId());
				addManuscriptEditorComment.setRoleId(sendBackToAuthorVo.getRoleId());
				addManuscriptEditorComment.setStatusId(sendBackToAuthorVo.getManuscriptStatus());
				addManuscriptEditorComment.setComment(sendBackToAuthorVo.getCommentsForAuthor());
				addManuscriptEditorComment.setCreatedAt(new Date());
				addManuscriptEditorComment.setReviewerFileId(sendBackToAuthorVo.getReviewerFileId());
				log.info("--------------------------------------------------------------------------");
				if (sendBackToAuthorVo.getFile() != null && !sendBackToAuthorVo.getFile().isEmpty()) {
					log.info("-------------------EDITOR COMMENT FILE IS NOT NULL------------------");
					String location = UPLOAD_FOLDER;
					String attachmentFolderPath = null;
					if (sendBackToAuthorVo.getManuscriptStatus() != null
							&& sendBackToAuthorVo.getManuscriptStatus().equals(46)) {
						log.info("-------------------Technical Modification File Folder------------------");
						attachmentFolderPath = location + File.separator + "UniprrTechnicalModificationFolder"
								+ File.separator + "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion()
								+ "_" + sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId();
						log.info("-------------------Technical Modification Folder Location------------------"
								+ attachmentFolderPath);
					} else {
						log.info("-------------------Revision File Folder------------------");
						attachmentFolderPath = location + File.separator + "UniprrRevisonEicFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId();
						log.info("-------------------Revision File Folder------------------" + attachmentFolderPath);
					}
					File dir = new File(attachmentFolderPath);
					if (!dir.exists()) {
						log.info("-------------------File Folder Created------------------");
						dir.mkdirs();
					}
					byte[] bytes = sendBackToAuthorVo.getFile().getBytes();
					File serverFile = new File(dir.getAbsolutePath() + File.separator
							+ sendBackToAuthorVo.getFile().getOriginalFilename());
					stream = new BufferedOutputStream(new FileOutputStream(serverFile));
					stream.write(bytes);
					double fileSizeinBytes = sendBackToAuthorVo.getFile().getSize();
					double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
					Integer port = request.getServerPort();
					InetAddress inetAddress = InetAddress.getLocalHost();
					String fileServerPath = null;
					if (sendBackToAuthorVo.getManuscriptStatus() != null
							&& sendBackToAuthorVo.getManuscriptStatus().equals(46)) {
						log.info("-------------------Inside In Technical Modification File Path------------------");
						if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
							String Ip = "https://" + environment.getProperty("awsFileIp");
							fileServerPath = Ip + File.separator + "UniprrTechnicalModificationFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
									+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
									+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
							log.info("Server Path for AWS : " + fileServerPath);
						} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
							String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
							fileServerPath = Ip + File.separator + "UniprrTechnicalModificationFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
									+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
									+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
									+ environment.getProperty("server.status.port");
							fileServerPath = Ip + File.separator + "UniprrTechnicalModificationFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
									+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
									+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
							log.info("Server Path for AWS : " + fileServerPath);
						}
					} else {
						log.info("-------------------Inside In Revision File Path------------------");
						if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
							String Ip = "https://" + environment.getProperty("awsFileIp");
							fileServerPath = Ip + File.separator + "UniprrRevisonEicFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
									+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
									+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
							log.info("Server Path for AWS : " + fileServerPath);
						} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
							String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
							fileServerPath = Ip + File.separator + "UniprrRevisonEicFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
									+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
									+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
									+ environment.getProperty("server.status.port");
							fileServerPath = Ip + File.separator + "UniprrRevisonEicFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
									+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
									+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
							log.info("Server Path for AWS : " + fileServerPath);
						}
					}
					addManuscriptEditorComment.setFileName(sendBackToAuthorVo.getFile().getOriginalFilename());
					addManuscriptEditorComment.setFilePath(fileServerPath);
					addManuscriptEditorComment.setFileSize((int) fileSizeInKB);
					if (sendBackToAuthorVo.getManuscriptStatus() != null
							&& sendBackToAuthorVo.getManuscriptStatus().equals(46))
						addManuscriptEditorComment.setFileType("Technical Modification Editor To Author");
					else
						addManuscriptEditorComment.setFileType("Revision Editor To Author");
					log.info("----------------------File Details has been added in Editor Comment--------------------");
				}
				manuscriptEditorCommentRepo.save(addManuscriptEditorComment);
				log.info("----------------------Editor Comment Has Been Created--------------------");
			}
			if(!Data1.get(0).getAssignedUserRole().equals(sendBackToAuthorVo.getRoleId())){
			ManuscriptEditor manuscriptEditor = manuscriptEditorRepo.getAllAssignedEdiorsOnManuscriptActive(sendBackToAuthorVo.getManuscriptId(), sendBackToAuthorVo.getManuscriptVersion());
			if(manuscriptEditor != null && manuscriptEditor.getManuscriptStatus() != 51 && manuscriptEditor.getManuscriptStatus() != 52 && manuscriptEditor.getManuscriptStatus() != 68) {
			manuscriptEditorRepo.disableEditor(sendBackToAuthorVo.getManuscriptId());
			}
		  }
			
			if (sendBackToAuthorVo.getReviewerFileId() != null
					&& sendBackToAuthorVo.getReviewerFileDisplayName() != null) {

				String[] reviewerIds =
						sendBackToAuthorVo.getReviewerFileId().split(";");

				String[] displayNames =
						sendBackToAuthorVo.getReviewerFileDisplayName().split(";");

				for (int i = 0; i < reviewerIds.length; i++) {

					if (reviewerIds[i] == null || reviewerIds[i].trim().isEmpty()) {
						continue;
					}

					Integer reviewerFileId = Integer.parseInt(reviewerIds[i]);

					ManuScriptReviewer reviewer =
							manuScriptReviewerRepo.findById(reviewerFileId).orElse(null);

					if (reviewer != null && i < displayNames.length) {
//						reviewer.setReviewerFileDisplayName(displayNames[i]);
						manuScriptReviewerRepo.save(reviewer);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("-------------------Exception due to code ------------------>" + e);
		} finally {
			try {
				if (sendBackToAuthorVo.getFile() != null)
					stream.close();
				log.info("-------------------File Close------------------");
			} catch (IOException e) {
				e.printStackTrace();
				log.info("-------------------Exception due to file ------------------>" + e);
			}
		}
		log.info("-------------------There is no file to update------------------");
		return "successfully added";
	}

	@Override
	public String uploadFile(SendBackToAuthorVo sendBackToAuthorVo, HttpServletRequest request) {
		log.info("-------------------Inside In  uploadFile------------------");
		BufferedOutputStream stream = null;
		try {
			log.info("-------------------Creation manuscript Editor Comment ------------------");
			ManuscriptEditorComment addManuscriptEditorComment = new ManuscriptEditorComment();
			addManuscriptEditorComment.setManuscriptId(sendBackToAuthorVo.getManuscriptId());
			addManuscriptEditorComment.setManuscriptVersion(sendBackToAuthorVo.getManuscriptVersion());
			addManuscriptEditorComment.setUserId(sendBackToAuthorVo.getEditorId());
			addManuscriptEditorComment.setRoleId(sendBackToAuthorVo.getRoleId());
			addManuscriptEditorComment.setStatusId(sendBackToAuthorVo.getManuscriptStatus());
			addManuscriptEditorComment.setComment(sendBackToAuthorVo.getCommentsForAuthor());
			addManuscriptEditorComment.setCreatedAt(new Date());
			log.info("--------------------------------------------------------------------------");
			if (sendBackToAuthorVo.getFile() != null && !sendBackToAuthorVo.getFile().isEmpty()) {
				log.info("-------------------EDITOR COMMENT FILE IS NOT NULL------------------");
				String location = UPLOAD_FOLDER;
				String attachmentFolderPath = null;
				if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(3)) {
					log.info("-------------------Editor assign file upload by editor Folder------------------");
					attachmentFolderPath = location + File.separator + "UniprrEditorAssignFolder" + File.separator
							+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
							+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId();
					log.info("-------------------Editor assign file upload by editor Folder Location------------------"
							+ attachmentFolderPath);
				} else if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(8)) {
					log.info("------------------Reviewer Assign File Folder------------------");
					attachmentFolderPath = location + File.separator + "UniprrReviewerAssignFolder" + File.separator
							+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
							+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId();
					log.info("-------------------Reviewer Assign File Folder------------------" + attachmentFolderPath);
				} else if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(30)) {
					log.info("------------------Reject For Production File Folder------------------");
					attachmentFolderPath = location + File.separator + "UniprrRejectForProductionFolder"
							+ File.separator + "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion()
							+ "_" + sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId();
					log.info("-------------------Reject For Production File Folder------------------"
							+ attachmentFolderPath);
				}
				File dir = new File(attachmentFolderPath);
				if (!dir.exists()) {
					log.info("-------------------File Folder Created------------------");
					dir.mkdirs();
				}
				byte[] bytes = sendBackToAuthorVo.getFile().getBytes();
				File serverFile = new File(
						dir.getAbsolutePath() + File.separator + sendBackToAuthorVo.getFile().getOriginalFilename());
				stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				double fileSizeinBytes = sendBackToAuthorVo.getFile().getSize();
				double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
				Integer port = request.getServerPort();
				InetAddress inetAddress = InetAddress.getLocalHost();
				String fileServerPath = null;
				if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(3)) {
					log.info("-------------------Editor assign file upload by editor Folder------------------");
					if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
						String Ip = "https://" + environment.getProperty("awsFileIp");
						fileServerPath = Ip + File.separator + "UniprrEditorAssignFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						log.info("Server Path for AWS : " + fileServerPath);
					} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
						String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
						fileServerPath = Ip + File.separator + "UniprrEditorAssignFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
					} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
								+ environment.getProperty("server.status.port");
						fileServerPath = Ip + File.separator + "UniprrEditorAssignFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						log.info("Server Path for AWS : " + fileServerPath);
					}
				} else if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(8)) {
					log.info("-------------------Reviewer Assign File Folder------------------");
					if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
						String Ip = "https://" + environment.getProperty("awsFileIp");
						fileServerPath = Ip + File.separator + "UniprrReviewerAssignFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						log.info("Server Path for AWS : " + fileServerPath);
					} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
						String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
						fileServerPath = Ip + File.separator + "UniprrReviewerAssignFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
					} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
								+ environment.getProperty("server.status.port");
						fileServerPath = Ip + File.separator + "UniprrReviewerAssignFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						log.info("Server Path for AWS : " + fileServerPath);
					}
				} else if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(30)) {
					log.info("-------------------Reject For Production File Folder------------------");
					if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
						String Ip = "https://" + environment.getProperty("awsFileIp");
						fileServerPath = Ip + File.separator + "UniprrRejectForProductionFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						log.info("Server Path for AWS : " + fileServerPath);
					} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
						String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
						fileServerPath = Ip + File.separator + "UniprrRejectForProductionFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
					} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
								+ environment.getProperty("server.status.port");
						fileServerPath = Ip + File.separator + "UniprrRejectForProductionFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + sendBackToAuthorVo.getManuscriptVersion() + "_"
								+ sendBackToAuthorVo.getJournalId() + "_" + sendBackToAuthorVo.getManuscriptId()
								+ File.separator + sendBackToAuthorVo.getFile().getOriginalFilename();
						log.info("Server Path for AWS : " + fileServerPath);
					}
				}
				addManuscriptEditorComment.setFileName(sendBackToAuthorVo.getFile().getOriginalFilename());
				addManuscriptEditorComment.setFilePath(fileServerPath);
				addManuscriptEditorComment.setFileSize((int) fileSizeInKB);
				if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(3))
					addManuscriptEditorComment.setFileType("Editor assign to Editor file");
				else if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(8))
					addManuscriptEditorComment.setFileType("Editor assign to Reviwer file");
				else if (sendBackToAuthorVo.getManuscriptStatus() != null
						&& sendBackToAuthorVo.getManuscriptStatus().equals(30))
					addManuscriptEditorComment.setFileType("Reject for production file");
				log.info("----------------------File Details has been added in Editor Comment--------------------");
			}
			manuscriptEditorCommentRepo.save(addManuscriptEditorComment);
			log.info("----------------------Editor Comment Has Been Created--------------------");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("-------------------Exception due to code ------------------>" + e);
		} finally {
			try {
				if (sendBackToAuthorVo.getFile() != null)
					stream.close();
				log.info("-------------------File Close------------------");
			} catch (IOException e) {
				e.printStackTrace();
				log.info("-------------------Exception due to file ------------------>" + e);
			}
		}
		log.info("-------------------There is no file to update------------------");
		return "successfully added";
	}

}
