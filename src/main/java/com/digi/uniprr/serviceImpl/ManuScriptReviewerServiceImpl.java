package com.digi.uniprr.serviceImpl;

import static java.io.File.separator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.digi.uniprr.VO.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalPolicies;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptForDashboard;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuScriptReviewerChecklist;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.ManuscriptJournalSubject;
import com.digi.uniprr.model.ReviewerAvailability;
import com.digi.uniprr.model.ReviewerOpposed;
import com.digi.uniprr.model.ReviewerRating;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.StatusMaster;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.JournalPoliciesRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.ManuscriptJournalSubjectRepo;
import com.digi.uniprr.repository.ManuscriptReviewerChecklistRepo;
import com.digi.uniprr.repository.ManuscriptReviewerCommentsRepo;
import com.digi.uniprr.repository.ManuscriptTypeDetailsRepo;
import com.digi.uniprr.repository.ReviewerAvailabilityRepo;
import com.digi.uniprr.repository.ReviewerOpposedRepo;
import com.digi.uniprr.repository.ReviewerRatingRepo;
import com.digi.uniprr.repository.ReviewerSuggestedRepo;
import com.digi.uniprr.repository.StatusMasterRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.AllCommentService;
import com.digi.uniprr.service.JournalConfigurationService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuscriptDashboardArticleService;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.CreatePdf;
import com.digi.uniprr.utils.FileUploadValidation;
import com.google.common.net.HttpHeaders;
//import com.digi.uniprr.utils.FileUploadValidator;

@Service
@Transactional
public class ManuScriptReviewerServiceImpl implements ManuScriptReviewerService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ManuScriptReviewerRepo manuScriptReviewerRepo;

    @Autowired
    WorkflowRoleRepo workflowRoleRepo;

    @Autowired
    ManuscriptJournalSubjectRepo manuscriptJournalSubjectRepo;

    @Autowired
    ManuscriptEditorRepo manuscriptEditorRepo;

    @Autowired
    ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;

    @Autowired
    ManuscriptReviewerChecklistRepo manuscriptReviewerChecklistRepo;

    @Autowired
    ManuscriptReviewerCommentsRepo manuscriptReviewerCommentsRepo;

    @Autowired
    ManuscriptDashboardArticleService manuscriptDashboardArticleService;

    @Autowired
    ManuScriptServiceImpl manuScriptServiceImpl;

    @Autowired
    ReviewerRatingRepo reviewerRatingRepo;

    @Autowired
    CountriesRepo countriesRepo;

    @Autowired
    StatusMasterRepo statusMasterRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserLoginRepo userLoginRepo;

    @Autowired
    ManuScriptRepo manuScriptRepo;

    @Autowired
    ReviewerAvailabilityRepo reviewerAvailabilityRepo;

    @Autowired
    UserJournalRepo userJournalRepo;

    @Autowired
    JournalsRepo journalsRepo;

    @Autowired
    ReviewerSuggestedRepo ReviewerSuggestedRepo;

    @Autowired
    AllCommentService allCommentService;

    @Autowired
    ReviewerOpposedRepo reviewerOpposedRepo;

    @Autowired
    JournalPoliciesRepo journalPoliciesRepo;

    @Autowired
    ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;
    @Autowired
    Environment environment;
    JSONObject obj = new JSONObject();
    @Value("${uniprrFolderPath}")
    private String UPLOAD_FOLDER;
    @Autowired
    private JournalConfigurationService journalConfigurationService;
    @Autowired
    private JournalsRepo journalRepo;
    @Value("${reviewerCertificatePath}")
    private String uploadFolder;
    @Value("${server.status}")
    private String serverENV;
    @Value("${awsFileIp}")
    private String serverIP;
    @Value("${server.status.port}")
    private String serverPort;
    @Value("${certificateDefaultImagePath}")
    private String certificateDefaultImagePath;
    @Value("${feImageUrl}")
    private String feImageUrl;
    @Value("${journalImagePath}")
    private String journalImagePath;
    @Value("${thumbnailUrl}")
    private String thumbnailUrl;

    @Override
    public String addReviewerDeatils(ManuScriptReviewer manuScriptReviewer) {

        manuScriptReviewerRepo.save(manuScriptReviewer);
        return "Added successfully";
    }

    @Override
    public JSONObject saveManuScriptReviewer(ManuScriptReviewerVO manuScriptReviewerVo) {
//		for (int i = 0; i <= manuScriptReviewerVo.getReviewer().size() - 1; i++)
        for (RIDVO ridvo : manuScriptReviewerVo.getReviewer())
            manuScriptReviewerRepo.saveManuScriptReviewer(manuScriptReviewerVo.getManuscriptId(),
                    manuScriptReviewerVo.getManuscriptVersion(), ridvo.getReviewerId(),
                    manuScriptReviewerVo.getEditorId(), manuScriptReviewerVo.getManuscriptStatus(),
                    manuScriptReviewerVo.getPublonCredit(), manuScriptReviewerVo.getAssignedDate(),
                    manuScriptReviewerVo.getJournalId(), manuScriptReviewerVo.getIsAssigned());
        obj.put("message", "manuscriptReviewer added data successfully");
        return obj;
    }

    /*
     * @Override public String updateReviewerDeatils(ManuScriptReviewer
     * manuScriptReviewer) { manuScriptReviewerRepo.save(manuScriptReviewer); return
     * "Updated successfully"; }
     */

    @Override
    public boolean isExist(Integer manuscriptId, Integer reviewerId, Integer manuscriptVersion) {
        List<ManuScriptReviewer> data = manuScriptReviewerRepo.getmanuScriptReviewerById(manuscriptId, reviewerId,
                manuscriptVersion);
        if (data != null && !data.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public String updateReviewerDeatils(ManuScriptReviewerVO manuScriptReviewerVo) {

        List<ManuScriptReviewer> data = manuScriptReviewerRepo.getmanuScriptReviewerById(
                manuScriptReviewerVo.getManuscriptId(), manuScriptReviewerVo.getReviewerId(),
                manuScriptReviewerVo.getManuscriptVersion());
        if (data != null && !data.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewerData1 : data) {
                if (manuScriptReviewerData1.getIsActive().equals(1)
                        && (manuScriptReviewerData1.getManuscriptStatus().equals(8)
                        || manuScriptReviewerData1.getManuscriptStatus().equals(66))) {
                    ManuScriptReviewer manuScriptReviewerData = manuScriptReviewerRepo
                            .getOne(manuScriptReviewerData1.getId());
                    if (manuScriptReviewerVo.getEditorId() != null) {
                        manuScriptReviewerData.setEditorId(manuScriptReviewerVo.getEditorId());
                    }

                    if (manuScriptReviewerVo.getManuscriptStatus() != null) {
                        manuScriptReviewerData.setManuscriptStatus(manuScriptReviewerVo.getManuscriptStatus());
                    }

                    if (manuScriptReviewerVo.getPublonCredit() != null) {
                        manuScriptReviewerData.setPublonCredit(manuScriptReviewerVo.getPublonCredit());
                    }
                    if (manuScriptReviewerVo.getUnassignedDate() != null) {
                        manuScriptReviewerData.setUnassignedDate(manuScriptReviewerVo.getUnassignedDate());
                    }
                    if (manuScriptReviewerVo.getStartDate() != null) {
                        manuScriptReviewerData.setStartDate(manuScriptReviewerVo.getStartDate());
                    }
                    if (manuScriptReviewerVo.getCompleteDate() != null) {
                        manuScriptReviewerData.setCompleteDate(manuScriptReviewerVo.getCompleteDate());
                    }
                    if (manuScriptReviewerVo.getRejectDate() != null) {
                        manuScriptReviewerData.setRejectDate(manuScriptReviewerVo.getRejectDate());
                    }
                    if (manuScriptReviewerVo.getAcceptDate() != null) {
                        manuScriptReviewerData.setAcceptDate(manuScriptReviewerVo.getAcceptDate());
                    }
                    if (manuScriptReviewerVo.getIsAssigned() != null) {
                        manuScriptReviewerData.setIsAssigned(manuScriptReviewerVo.getIsAssigned());
                    }
                    if (manuScriptReviewerVo.getRejectionReason() != null) {
                        manuScriptReviewerData.setRejectionReason(manuScriptReviewerVo.getRejectionReason());
                    }
                    if (manuScriptReviewerVo.getUpdatedBy() != null) {
                        manuScriptReviewerData.setUpdatedBy(manuScriptReviewerVo.getUpdatedBy());
                    }
                    if (manuScriptReviewerVo.getUpdatedOn() != null) {
                        manuScriptReviewerData.setUpdatedOn(manuScriptReviewerVo.getUpdatedOn());
                    }
                    if (manuScriptReviewerVo.getDecisionLetter() != null) {
                        manuScriptReviewerData.setDecisionLetter(manuScriptReviewerVo.getDecisionLetter());
                    }
                    if (manuScriptReviewerVo.getCommentsToAuthor() != null) {
                        manuScriptReviewerData.setCommentsToAuthor(manuScriptReviewerVo.getCommentsToAuthor());
                    }
                    if (manuScriptReviewerVo.getCommentsToEditor() != null) {
                        manuScriptReviewerData.setCommentsToEditor(manuScriptReviewerVo.getCommentsToEditor());
                    }

                    manuScriptReviewerRepo.save(manuScriptReviewerData);
                }
            }
            return "Updated Data Sucessfully.";
        }
        return "There is no data to update.";

    }

    @Override
    public String addAllDetailsMultipleTableRCC(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {

        List<ManuScriptReviewer> data = manuScriptReviewerRepo.getmanuScriptReviewerById(
                manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getReviewerId(),
                manuScriptReviewerCommonVO.getManuscriptVersion());

        if (data != null && !data.isEmpty()) {
            ManuScriptReviewer manuScriptReviewerData1 = data.get(0);
            ManuScriptReviewer manuScriptReviewerData = manuScriptReviewerRepo.getOne(manuScriptReviewerData1.getId());

            if (manuScriptReviewerCommonVO.getEditorId() != null) {
                manuScriptReviewerData.setEditorId(manuScriptReviewerCommonVO.getEditorId());
            }
            if (manuScriptReviewerCommonVO.getManuscriptStatus() != null) {
                manuScriptReviewerData.setManuscriptStatus(manuScriptReviewerCommonVO.getManuscriptStatus());
            }
            if (manuScriptReviewerCommonVO.getPublonCredit() != null) {
                manuScriptReviewerData.setPublonCredit(manuScriptReviewerCommonVO.getPublonCredit());
            }
            if (manuScriptReviewerCommonVO.getAssignedDate() != null) {
                manuScriptReviewerData.setRejectDate(manuScriptReviewerCommonVO.getAssignedDate());
            }
            if (manuScriptReviewerCommonVO.getIsAssigned() != null) {
                manuScriptReviewerData.setIsAssigned(manuScriptReviewerCommonVO.getIsAssigned());
            }
            if (manuScriptReviewerCommonVO.getRejectionReason() != null) {
                manuScriptReviewerData.setRejectionReason(manuScriptReviewerCommonVO.getRejectionReason());
            }
            if (manuScriptReviewerCommonVO.getJournalId() != null) {
                manuScriptReviewerData.setJournalId(manuScriptReviewerCommonVO.getJournalId());
            }
            if (manuScriptReviewerCommonVO.getAcceptDate() != null) {
                manuScriptReviewerData.setAcceptDate(manuScriptReviewerCommonVO.getAcceptDate());
            }
            if (manuScriptReviewerCommonVO.getIsActive() != null) {
                manuScriptReviewerData.setIsActive(manuScriptReviewerCommonVO.getIsActive());
            }
            if (manuScriptReviewerCommonVO.getUpdatedBy() != null) {
                manuScriptReviewerData.setUpdatedBy(manuScriptReviewerCommonVO.getUpdatedBy());
            }
            if (manuScriptReviewerCommonVO.getUpdatedOn() != null) {
                manuScriptReviewerData.setUpdatedOn(manuScriptReviewerCommonVO.getUpdatedOn());
            }
            if (manuScriptReviewerCommonVO.getDecisionLetter() != null) {
                manuScriptReviewerData.setDecisionLetter(manuScriptReviewerCommonVO.getDecisionLetter());
            }
            if (manuScriptReviewerCommonVO.getCommentsToAuthor() != null) {
                manuScriptReviewerData.setCommentsToAuthor(manuScriptReviewerCommonVO.getCommentsToAuthor());
            }
            if (manuScriptReviewerCommonVO.getCommentsToEditor() != null) {
                manuScriptReviewerData.setCommentsToEditor(manuScriptReviewerCommonVO.getCommentsToEditor());
            }
            manuScriptReviewerRepo.save(manuScriptReviewerData);
        } else {
            ManuScriptReviewer manuScriptReviewerData = new ManuScriptReviewer();
            manuScriptReviewerData.setManuscriptId(manuScriptReviewerCommonVO.getManuscriptId());
            manuScriptReviewerData.setManuscriptVersion(manuScriptReviewerCommonVO.getManuscriptVersion());
            manuScriptReviewerData.setReviewerId(manuScriptReviewerCommonVO.getReviewerId());
            manuScriptReviewerData.setManuscriptStatus(manuScriptReviewerCommonVO.getManuscriptStatus());
            manuScriptReviewerData.setJournalId(manuScriptReviewerCommonVO.getJournalId());
            manuScriptReviewerData.setEditorId(manuScriptReviewerCommonVO.getEditorId());
            manuScriptReviewerData.setIsAssigned(manuScriptReviewerCommonVO.getIsAssigned());
            manuScriptReviewerData.setPublonCredit(manuScriptReviewerCommonVO.getPublonCredit());
            manuScriptReviewerData.setAssignedDate(manuScriptReviewerCommonVO.getAssignedDate());
            manuScriptReviewerData.setAcceptDate(manuScriptReviewerCommonVO.getAcceptDate());
            manuScriptReviewerData.setRejectionReason(manuScriptReviewerCommonVO.getRejectionReason());
            manuScriptReviewerData.setDecisionLetter(manuScriptReviewerCommonVO.getDecisionLetter());
            manuScriptReviewerData.setCommentsToAuthor(manuScriptReviewerCommonVO.getCommentsToAuthor());
            manuScriptReviewerData.setCommentsToEditor(manuScriptReviewerCommonVO.getCommentsToEditor());
            manuScriptReviewerData.setIsActive(manuScriptReviewerCommonVO.getIsActive());
            manuScriptReviewerRepo.save(manuScriptReviewerData);

        }

        for (int i = 0; i <= manuScriptReviewerCommonVO.getReviewer().size() - 1; i++) {
            ManuScriptReviewerChecklist saved1 = new ManuScriptReviewerChecklist();
            saved1.setManuscriptId(manuScriptReviewerCommonVO.getManuscriptId());
            saved1.setManuscriptVersion(manuScriptReviewerCommonVO.getManuscriptVersion());
            saved1.setReviewerId(manuScriptReviewerCommonVO.getReviewerId());
            saved1.setChkId(manuScriptReviewerCommonVO.getReviewer().get(i).getChkId());
            saved1.setChkType(manuScriptReviewerCommonVO.getReviewer().get(i).getChkType());
            saved1.setChckComments(manuScriptReviewerCommonVO.getReviewer().get(i).getChckComments());
            saved1.setCreatedBy(manuScriptReviewerCommonVO.getCreatedBy());
            saved1.setCreatedOn(manuScriptReviewerCommonVO.getCreatedOn());
            saved1.setUpdatedBy(manuScriptReviewerCommonVO.getUpdatedBy());
            saved1.setUpdatedOn(manuScriptReviewerCommonVO.getUpdatedOn());
            manuscriptReviewerChecklistRepo.save(saved1);
        }

        return "Data Insert Succesfully";
    }

    @Override
    public List<ManuScriptReviewer> getManuscriptReviewer(UserJournalManuscriptVo userJournalManuscriptVo) {

        List<ManuScriptReviewer> dataList = new ArrayList<>();
        for (Integer statusList : userJournalManuscriptVo.getManuscriptStatusId()) {
            List<ManuScriptReviewer> list = new ArrayList<>();
            Integer status = statusList;
            list = manuScriptReviewerRepo.getManuscriptReviewer(status, userJournalManuscriptVo.getJournalId(),
                    userJournalManuscriptVo.getUserId());
            if (list != null && !list.isEmpty()) {
                for (ManuScriptReviewer listOfData : list) {
                    dataList.add(listOfData);
                }
            }

        }
        dataList.sort((a, b) -> b.getAssignedDate().compareTo(a.getAssignedDate()));
        
        return dataList;
    }

    @Override
    public List<ManuScriptReviewer> getManuscriptReviewerListById(UserJournalManuscriptVo userJournalManuscriptVo) {
        List<ManuScriptReviewer> dataList = new ArrayList<>();
        for (Integer statusList : userJournalManuscriptVo.getManuscriptStatusId()) {
            List<ManuScriptReviewer> list = new ArrayList<>();
            Integer status = statusList;
            list = manuScriptReviewerRepo.getManuscriptReviewerListById(status, userJournalManuscriptVo.getJournalId());
            if (list != null && !list.isEmpty()) {
                for (ManuScriptReviewer listOfData : list) {
                    dataList.add(listOfData);
                }
            }
        }
        return dataList;
    }

    @Override
    public void saveManuScriptReviewerDetails(ManuScriptReviewer manuscriptReviewer) {
        manuscriptReviewer.setIsActive(1);
        manuScriptReviewerRepo.save(manuscriptReviewer);
    }

    @Override
    public List<ManuScriptReviewerCommonVO> getReviewerByManuScriptIdAndVersion(Integer manuscriptId,
                                                                                Integer manuscriptVersion) {
        List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();
        List<ManuScriptReviewer> reviewerData = manuScriptReviewerRepo
                .getManuscriptReviewerListByManuScriptIdAndVersion(manuscriptId, manuscriptVersion);
        if (reviewerData != null && !reviewerData.isEmpty()) {
            for (ManuScriptReviewer list : reviewerData) {
                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                commonvo.setManuscriptId(list.getManuscriptId());
                commonvo.setManuscriptVersion(list.getManuscriptVersion());
                commonvo.setReviewerId(list.getReviewerId());
                commonvo.setManuscriptStatus(list.getManuscriptStatus());
                commonvo.setDecisionLetter(list.getDecisionLetter());
                commonvo.setCommentsToAuthor(list.getCommentsToAuthor());
                commonvo.setCommentsToEditor(list.getCommentsToEditor());

                StatusMaster st = statusMasterRepo.getOne(list.getManuscriptStatus());
                commonvo.setStatusValue(st.getStatusValue());
                if (list.getReviewerId() != null) {

                    User user = userRepo.getOne(list.getReviewerId());
                    if (user != null && user.getFirstName() != null) {
                        commonvo.setName(user.getFirstName() + " " + user.getLastName());
                    }
                }
                ManuScript ms = manuScriptRepo.getOne(list.getManuscriptId());
                commonvo.setManuscriptCode(ms.getManuscriptCode());

                rsd.add(commonvo);
            }
        }
        return rsd;
    }

    @Override
    public List<ReviewerAvailabilityStatusVO> getReviewerDataById(CommonVO id) {

        List<ReviewerAvailabilityStatusVO> reviewerAvailabilityStatus = new ArrayList<>();

        for (Integer reviewerId : id.getRoleId()) {
            ReviewerAvailabilityStatusVO tempReviewerAvailabilityStatus = new ReviewerAvailabilityStatusVO();
            User usr = userRepo.getById(reviewerId);

            Long articlesInHandCount = getArticlesInHands(id.getJournalId(), reviewerId);

            tempReviewerAvailabilityStatus.setAcceptedByReviewer(manuScriptReviewerRepo
                    .getReviewedManuscriptForReviewer(Arrays.asList(12), id.getJournalId(), reviewerId).size());

            List<ManuScriptReviewer> invitedList = manuScriptReviewerRepo
                    .getRevieweManuscriptForStaticActive(Arrays.asList(8, 66), id.getJournalId(), reviewerId);

            Long invitedCount = invitedList.stream()
                    .filter(obj -> (obj.getManuscriptStatus() == 8 || obj.getManuscriptStatus() == 66)).count();

            tempReviewerAvailabilityStatus.setInvitedManuscript(invitedCount.intValue());

            List<ManuScriptReviewer> reviewerRejectList = manuScriptReviewerRepo
                    .getRevieweManuscriptForStatic(Arrays.asList(60, 36), id.getJournalId(), reviewerId);

            Long declinedCount = reviewerRejectList.stream().filter(obj -> obj.getManuscriptStatus() == 60).count();
            Long rejectedCount = reviewerRejectList.stream().filter(obj -> obj.getManuscriptStatus() == 36).count();

            Long reviewCompleted = manuScriptReviewerRepo
                    .getReviewedManuscriptForReviewer(Arrays.asList(15, 14, 13, 12), id.getJournalId(), reviewerId)
                    .stream().count();

            Long totalCount = invitedCount + articlesInHandCount + rejectedCount + declinedCount + reviewCompleted;

            tempReviewerAvailabilityStatus.setDecliendAfterAcceptInvitation(declinedCount.intValue());
            tempReviewerAvailabilityStatus.setRejectReviewInvitation(rejectedCount.intValue());

            tempReviewerAvailabilityStatus.setEmail(usr.getEmail());
            tempReviewerAvailabilityStatus.setName(usr.getFirstName() + " " + usr.getLastName());
            tempReviewerAvailabilityStatus.setArticlesInHand(articlesInHandCount.intValue());
            tempReviewerAvailabilityStatus.setReviewCompleted(reviewCompleted.intValue());
            tempReviewerAvailabilityStatus.setTotalCount(totalCount.intValue());
            reviewerAvailabilityStatus.add(tempReviewerAvailabilityStatus);
        }
        return reviewerAvailabilityStatus;
    }

    public Long getArticlesInHands(Integer journalId, Integer reviewerId) {

        Long articlesInHandCount = manuScriptReviewerRepo.getManuscriptReviewer(35, journalId, reviewerId).stream()
                .count();
        return articlesInHandCount;
    }

    @Override
    public List<ManuScriptReviewerCommonVO> getReviewerDataByManuscriptVersion(Integer manuscriptId,
                                                                               Integer manuscriptVersion, Integer journalId) {
        List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();

        List<UserJournal> getUnassignedReviewer = userJournalRepo.getAllReviewerList(journalId);
        if (getUnassignedReviewer != null && !getUnassignedReviewer.isEmpty()) {
            for (UserJournal i : getUnassignedReviewer) {

                // getUnassignedReviewer.stream().forEach(i -> {
                if (i.getUser().getIsActive() == 1) {
//					i.getUser().getRole().stream().forEach(j -> {
//						if (j.getRoleId().equals(5)) {
                    ManuScriptReviewer getData = manuScriptReviewerRepo.getReviewerDataByManuscriptVersionAssigned(
                            manuscriptId, manuscriptVersion, journalId, i.getUser().getId());
                    if (getData == null) {
                        ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                        commonvo.setName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
                        commonvo.setEmail(i.getUser().getEmail());
                        commonvo.setReviewerId(i.getUser().getId());
                        commonvo.setSpecialization(i.getUser().getSpecialization());
                        if (i.getUser().getExpertise() != null)
                            commonvo.setSubSpecialization(i.getUser().getExpertise());
                        if (i.getUser().getCountryId() != null) {
                            Countries countries = countriesRepo.getOne(i.getUser().getCountryId());
                            if (countries != null) {
                                commonvo.setCountry(countries.getName());
                            }
                        }
                        List<ReviewerAvailability> getReviewerAvailabilityData = reviewerAvailabilityRepo
                                .getReviewerAvailabilityById(i.getUser().getId());
                        if (getReviewerAvailabilityData != null && !getReviewerAvailabilityData.isEmpty()) {
                            getReviewerAvailabilityData.stream().forEach(l -> {
                                List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                                tempReviewer.add(l);
                                commonvo.setReviewerAvailability(tempReviewer);
                            });
                        }
                        rsd.add(commonvo);
                    }
                    // }
//					});
                }
            }
        }

        return rsd;
    }

    @Override
    public List<ManuScriptReviewerCommonVO> getAssignedReviewer(Integer manuscriptId, Integer manuscriptVersion,
                                                                Integer journalId) {
        List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();
        List<ManuScriptReviewer> reviewerData = manuScriptReviewerRepo.getUniqueUnassignedReviewerData(manuscriptId,
                manuscriptVersion, journalId);

        if (reviewerData != null && !reviewerData.isEmpty()) {
            reviewerData.stream().forEach(list -> {
                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                commonvo.setManuscriptId(list.getManuscriptId());
                commonvo.setManuscriptVersion(list.getManuscriptVersion());
                commonvo.setReviewerId(list.getReviewerId());
                commonvo.setManuscriptStatus(list.getManuscriptStatus());
                commonvo.setDecisionLetter(list.getDecisionLetter());
                commonvo.setCommentsToAuthor(list.getCommentsToAuthor());
                commonvo.setCommentsToEditor(list.getCommentsToEditor());
                commonvo.setAssignedDate(list.getAssignedDate());
                commonvo.setAcceptDate(list.getAcceptDate());
                commonvo.setRejectDate(list.getRejectDate());
                commonvo.setIsAssigned(list.getIsAssigned());
                commonvo.setDueDate(list.getDueDate());
                commonvo.setJournalId(list.getJournalId());
                StatusMaster st = statusMasterRepo.getOne(list.getManuscriptStatus());
                commonvo.setStatusValue(st.getStatusValue());
                if (list.getReviewerId() != null) {
                    User user = userRepo.getOne(list.getReviewerId());
                    if (user != null && user.getFirstName() != null) {
                        commonvo.setName(user.getFirstName() + " " + user.getLastName());
                        commonvo.setEmail(user.getEmail());
                        commonvo.setSpecialization(user.getSpecialization());
                        commonvo.setInstitution(user.getInstitute());
                        if (user.getExpertise() != null)
                            commonvo.setSubSpecialization(user.getExpertise());
                        if (user.getCountryId() != null) {
                            Countries countries = countriesRepo.getOne(user.getCountryId());
                            if (countries != null) {
                                commonvo.setCountry(countries.getName());
                            }
                        }
                    }
                }
                ManuScript ms = manuScriptRepo.getOne(list.getManuscriptId());
                commonvo.setManuscriptCode(ms.getManuscriptCode());
                List<ReviewerAvailability> getReviewerAvailabilityData = reviewerAvailabilityRepo
                        .getReviewerAvailabilityById(list.getReviewerId());
                if (getReviewerAvailabilityData != null && !getReviewerAvailabilityData.isEmpty()) {
                    getReviewerAvailabilityData.stream().forEach(l -> {
                        List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                        tempReviewer.add(l);
                        commonvo.setReviewerAvailability(tempReviewer);
                    });
                }
                rsd.add(commonvo);
            });

        }

        return rsd;
    }

    public List<ManuScriptReviewerCommonVO> getAssignedReviewerPagination(Integer manuscriptId, Integer manuscriptVersion,
                                                                          Integer journalId, Integer startIndex, Integer limit) {
        List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();
        List<ManuScriptReviewer> reviewerData = manuScriptReviewerRepo.getUniqueUnassignedReviewerDataPagination(manuscriptId,
                manuscriptVersion, journalId, startIndex, limit);

        if (reviewerData != null && !reviewerData.isEmpty()) {
            reviewerData.stream().forEach(list -> {
                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                commonvo.setManuscriptId(list.getManuscriptId());
                commonvo.setManuscriptVersion(list.getManuscriptVersion());
                commonvo.setReviewerId(list.getReviewerId());
                commonvo.setManuscriptStatus(list.getManuscriptStatus());
                commonvo.setDecisionLetter(list.getDecisionLetter());
                commonvo.setCommentsToAuthor(list.getCommentsToAuthor());
                commonvo.setCommentsToEditor(list.getCommentsToEditor());
                commonvo.setAssignedDate(list.getAssignedDate());
                commonvo.setAcceptDate(list.getAcceptDate());
                commonvo.setRejectDate(list.getRejectDate());
                commonvo.setIsAssigned(list.getIsAssigned());
                commonvo.setDueDate(list.getDueDate());
                commonvo.setJournalId(list.getJournalId());
                StatusMaster st = statusMasterRepo.getOne(list.getManuscriptStatus());
                commonvo.setStatusValue(st.getStatusValue());
                if (list.getReviewerId() != null) {
                    User user = userRepo.getOne(list.getReviewerId());
                    if (user != null && user.getFirstName() != null) {
                        commonvo.setName(user.getFirstName() + " " + user.getLastName());
                        commonvo.setEmail(user.getEmail());
                        commonvo.setSpecialization(user.getSpecialization());
                        commonvo.setInstitution(user.getInstitute());
                        if (user.getExpertise() != null)
                            commonvo.setSubSpecialization(user.getExpertise());
                        if (user.getCountryId() != null) {
                            Countries countries = countriesRepo.getOne(user.getCountryId());
                            if (countries != null) {
                                commonvo.setCountry(countries.getName());
                            }
                        }
                    }
                }
                ManuScript ms = manuScriptRepo.getOne(list.getManuscriptId());
                commonvo.setManuscriptCode(ms.getManuscriptCode());
                List<ReviewerAvailability> getReviewerAvailabilityData = reviewerAvailabilityRepo
                        .getReviewerAvailabilityById(list.getReviewerId());
                if (getReviewerAvailabilityData != null && !getReviewerAvailabilityData.isEmpty()) {
                    getReviewerAvailabilityData.stream().forEach(l -> {
                        List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                        tempReviewer.add(l);
                        commonvo.setReviewerAvailability(tempReviewer);
                    });
                }
                rsd.add(commonvo);
            });

        }
        return rsd;
    }

    public Integer getAssignedReviewerCount(Integer manuscriptId, Integer manuscriptVersion,
                                            Integer journalId) {
        return manuScriptReviewerRepo.getUniqueUnassignedReviewerCount(manuscriptId, manuscriptVersion, journalId);
    }

    @Override
    public List<ManuscriptListVO> getReviewedManuscript(IDVO id) {
        List<ManuscriptListVO> manuscriptList = new ArrayList();

        if (id != null && id.getRoleId() != null && id.getSearch() != null
                && id.getSearch() != "") {
            Map<String, ManuscriptListVO> map = new HashMap<>();
            List<ManuScript> editorDataList = manuScriptRepo.getManuscriptByJournalAndIdEditorAndRoleByManuId(
                    id.getJournalId(), id.getUserId(), id.getRoleId(), id.getSearch());
            List<Integer> manuscriptIdList = new ArrayList();
            if (editorDataList != null && !editorDataList.isEmpty()) {
                for (ManuScript editorDataListData : editorDataList) {
                    manuscriptIdList.add(editorDataListData.getManuScriptId());
                }

                List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
                        .getReviewedManuscriptEditor(id.getJournalId(), manuscriptIdList);
                Map<Integer, List<ManuScriptReviewer>> reviewerMapList = getUniqueDataByjournal.stream()
                        .collect(Collectors.groupingBy(w -> w.getManuscriptId()));

                if (editorDataList != null && !editorDataList.isEmpty() && getUniqueDataByjournal != null
                        && !getUniqueDataByjournal.isEmpty()) {
                    for (ManuScript uniqueData : editorDataList) {
                        int mcount = 0;
                        int mvalidcount = 0;
                        int delayCount = 0;
                        int reviewedCount = 0;
                        int totalReviewCount = 0;
                        int blinkerCount = 0;
                        try {
                            for (ManuScriptReviewer allData : reviewerMapList.get(uniqueData.getManuScriptId())) {
                                if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
                                        && allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
                                    mcount++;
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
                                        Date todayDate = df.parse(df.format(new Date()));
                                        if (reviewerDueDate.after(todayDate) || todayDate.equals(reviewerDueDate)) {
                                            blinkerCount++;
                                        }
                                        if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 12
                                                || ((allData.getManuscriptStatus() == 8
                                                || allData.getManuscriptStatus() == 35
                                                || allData.getManuscriptStatus() == 66)
                                                && reviewerDueDate.before(todayDate))
                                                || allData.getManuscriptStatus() == 13
                                                || allData.getManuscriptStatus() == 14
                                                || allData.getManuscriptStatus() == 15
                                                || allData.getManuscriptStatus() == 36
                                                || allData.getManuscriptStatus() == 60) {
                                            mvalidcount++;
                                        }

                                        if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 36
                                                || allData.getManuscriptStatus() == 60
                                                || ((allData.getManuscriptStatus() == 8
                                                || allData.getManuscriptStatus() == 35
                                                || allData.getManuscriptStatus() == 66)
                                                && reviewerDueDate.before(todayDate))) {
                                            delayCount++;
                                        }

                                        if (allData.getManuscriptStatus().equals(12)
                                                || allData.getManuscriptStatus().equals(13)
                                                || allData.getManuscriptStatus().equals(14)
                                                || allData.getManuscriptStatus().equals(15)) {
                                            reviewedCount++;
                                        }
                                        if (allData.getManuscriptStatus().equals(12)
                                                || allData.getManuscriptStatus().equals(13)
                                                || allData.getManuscriptStatus().equals(14)
                                                || allData.getManuscriptStatus().equals(15)
                                                || allData.getManuscriptStatus().equals(8)
                                                || allData.getManuscriptStatus().equals(35)
                                                || allData.getManuscriptStatus().equals(66)
                                                || allData.getManuscriptStatus().equals(36)
                                                || allData.getManuscriptStatus().equals(60)) {
                                            totalReviewCount++;
                                        }

                                    } catch (ParseException e) {
                                        log.debug(
                                                "Exception occured during finding the valid condition for Reviewed manuscript"
                                                        + e.getMessage());
                                    }
                                }
                            }
                            if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount && delayCount != mcount) {
                                ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                                manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                manuscriptListVO.setTotalReviewCount(totalReviewCount);
                                manuscriptListVO.setReviewdCount(reviewedCount);
                                manuscriptListVO.setBlinkerCount(blinkerCount);
                                manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                                manuscriptListVO.setManuscriptAuthorId(uniqueData.getAuthorId());
                                manuscriptListVO.setAuthorName(
                                        uniqueData.getUser().getFirstName() + " " + uniqueData.getUser().getLastName());
                                if (uniqueData.getManuscriptTypeDetails() != null
                                        && uniqueData.getManuscriptTypeDetails().getManuscriptType() != null) {
                                    manuscriptListVO.setManuScriptType(
                                            uniqueData.getManuscriptTypeDetails().getManuscriptType());
                                }
                                manuscriptListVO.setManuscripCode(uniqueData.getManuscriptCode());
                                manuscriptListVO.setManuscripTitle(uniqueData.getManuscriptTitle());
                                manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());
                                manuscriptListVO.setEditorStatus(uniqueData.getStatusMaster().getStatusValue());
                                ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(uniqueData.getJournalId(), uniqueData.getManuScriptId(), uniqueData.getManuscriptVersion(), uniqueData.getAssignedUser());
                                manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
                                manuscriptListVO.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
                                manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                                manuscriptListVO.setOtherReviewer("Yes");
                                // add Due Date
                                List<ManuScriptReviewer> reviewerDuedate = reviewerMapList
                                        .get(uniqueData.getManuScriptId()).stream()
                                        .filter(manu -> manu.getManuscriptStatus() == 8
                                                || manu.getManuscriptStatus() == 35 || manu.getManuscriptStatus() == 66)
                                        .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                        .collect(Collectors.toList());
                                if (reviewerDuedate != null && reviewerDuedate.size() > 0
                                        && reviewerDuedate.get(0).getDueDate() != null) {
                                    manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
                                    manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
                                } else {
                                    List<ManuScriptReviewer> reviewDuedate = reviewerMapList
                                            .get(uniqueData.getManuScriptId()).stream()
                                            .filter(manu -> manu.getManuscriptStatus().equals(12)
                                                    || manu.getManuscriptStatus().equals(13)
                                                    || manu.getManuscriptStatus().equals(14)
                                                    || manu.getManuscriptStatus().equals(15))
                                            .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                            .collect(Collectors.toList());
                                    if (null != reviewDuedate && reviewDuedate.size() > 0) {
                                        manuscriptListVO.setAssignDate(reviewDuedate.get(0).getAssignedDate());
                                        manuscriptListVO.setDueDate(reviewDuedate.get(0).getDueDate());
                                    }

                                }
                                // add reviewer Names start
                                List<ManuScriptReviewer> reviewerList = reviewerMapList
                                        .get(uniqueData.getManuScriptId());
                                List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
                                    ReviewerDetailsVO r = new ReviewerDetailsVO();
                                    r.setName(x.getUser().getFirstName() + " " + x.getUser().getLastName());
                                    r.setStatus(x.getStatusMaster().getStatusValue());
                                    r.setDueDate(x.getDueDate());
                                    r.setIsActive(x.getIsActive());
                                    return r;
                                }).collect(Collectors.toList());
                                manuscriptListVO.setManuScriptReviewers(reviewers); // add reviewer Names end
                                map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);
                            }
                        } catch (Exception ex) {
                            log.debug("Exception occured during finding the valid condition for No response reviewer"
                                    + ex.getMessage());
                        }
                    }
                }

            }
            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
                    manuscriptList.add(entry.getValue());
                }
            }
        } else {
            Map<String, ManuscriptListVO> map = new HashMap<>();
            List<ManuScript> editorDataList = manuScriptRepo
                    .getManuscriptByJournalAndIdEditorAndRole(id.getJournalId(), id.getUserId(), id.getRoleId());
            List<Integer> manuscriptIdList = new ArrayList();
            if (editorDataList != null && !editorDataList.isEmpty()) {
                for (ManuScript editorDataListData : editorDataList) {
                    manuscriptIdList.add(editorDataListData.getManuScriptId());
                }
                List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                        .getReviewedManuscriptEditor(id.getJournalId(), manuscriptIdList);
                Map<Integer, List<ManuScriptReviewer>> reviewerMapList = getAllDataByjournal.stream()
                        .collect(Collectors.groupingBy(w -> w.getManuscriptId()));

                if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty() && editorDataList != null
                        && !editorDataList.isEmpty()) {
                    for (ManuScript uniqueData : editorDataList) {
                        int mcount = 0;
                        int mvalidcount = 0;
                        int delayCount = 0;
                        int reviewedCount = 0;
                        int totalReviewCount = 0;
                        int blinkerCount = 0;
                        try {
                            for (ManuScriptReviewer allData : reviewerMapList.get(uniqueData.getManuScriptId())) {
                                if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
                                        && allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
                                    mcount++;
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
                                        Date todayDate = df.parse(df.format(new Date()));
                                        if (reviewerDueDate.after(todayDate) || todayDate.equals(reviewerDueDate)) {
                                            blinkerCount++;
                                        }
                                        if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 12
                                                || ((allData.getManuscriptStatus() == 8
                                                || allData.getManuscriptStatus() == 35
                                                || allData.getManuscriptStatus() == 66)
                                                && reviewerDueDate.before(todayDate))
                                                || allData.getManuscriptStatus() == 13
                                                || allData.getManuscriptStatus() == 14
                                                || allData.getManuscriptStatus() == 15
                                                || allData.getManuscriptStatus() == 36
                                                || allData.getManuscriptStatus() == 60) {
                                            mvalidcount++;
                                        }

                                        if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 36
                                                || allData.getManuscriptStatus() == 60
                                                || ((allData.getManuscriptStatus() == 8
                                                || allData.getManuscriptStatus() == 35
                                                || allData.getManuscriptStatus() == 66)
                                                && reviewerDueDate.before(todayDate))) {
                                            delayCount++;
                                        }

                                        if (allData.getManuscriptStatus().equals(12)
                                                || allData.getManuscriptStatus().equals(13)
                                                || allData.getManuscriptStatus().equals(14)
                                                || allData.getManuscriptStatus().equals(15)) {
                                            reviewedCount++;
                                        }
                                        if (allData.getManuscriptStatus().equals(12)
                                                || allData.getManuscriptStatus().equals(13)
                                                || allData.getManuscriptStatus().equals(14)
                                                || allData.getManuscriptStatus().equals(15)
                                                || allData.getManuscriptStatus().equals(8)
                                                || allData.getManuscriptStatus().equals(35)
                                                || allData.getManuscriptStatus().equals(66)
                                                || allData.getManuscriptStatus().equals(36)
                                                || allData.getManuscriptStatus().equals(60)) {
                                            totalReviewCount++;
                                        }

                                    } catch (ParseException e) {
                                        log.debug(
                                                "Exception occured during finding the valid condition for Reviewed manuscript"
                                                        + e.getMessage());
                                    }
                                }
                            }
                            if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount && delayCount != mcount) {
                                ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                                manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                manuscriptListVO.setTotalReviewCount(totalReviewCount);
                                manuscriptListVO.setReviewdCount(reviewedCount);
                                manuscriptListVO.setBlinkerCount(blinkerCount);
                                manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                                manuscriptListVO.setManuscriptAuthorId(uniqueData.getAuthorId());
                                manuscriptListVO.setAuthorName(
                                        uniqueData.getUser().getFirstName() + " " + uniqueData.getUser().getLastName());
                                if (uniqueData.getManuscriptTypeDetails() != null
                                        && uniqueData.getManuscriptTypeDetails().getManuscriptType() != null) {
                                    manuscriptListVO.setManuScriptType(
                                            uniqueData.getManuscriptTypeDetails().getManuscriptType());
                                }
                                manuscriptListVO.setManuscripCode(uniqueData.getManuscriptCode());
                                manuscriptListVO.setManuscripTitle(uniqueData.getManuscriptTitle());
                                manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());
                                manuscriptListVO.setEditorStatus(uniqueData.getStatusMaster().getStatusValue());
                                ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(uniqueData.getJournalId(), uniqueData.getManuScriptId(), uniqueData.getManuscriptVersion(), uniqueData.getAssignedUser());
                                manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
                                manuscriptListVO.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
                                manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                                manuscriptListVO.setOtherReviewer("Yes");
                                // add Due Date
                                List<ManuScriptReviewer> reviewerDuedate = reviewerMapList
                                        .get(uniqueData.getManuScriptId()).stream()
                                        .filter(manu -> manu.getManuscriptStatus() == 8
                                                || manu.getManuscriptStatus() == 35 || manu.getManuscriptStatus() == 66)
                                        .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                        .collect(Collectors.toList());
                                if (reviewerDuedate != null && reviewerDuedate.size() > 0
                                        && reviewerDuedate.get(0).getDueDate() != null) {
                                    manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
                                    manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
                                } else {
                                    List<ManuScriptReviewer> reviewDuedate = reviewerMapList
                                            .get(uniqueData.getManuScriptId()).stream()
                                            .filter(manu -> manu.getManuscriptStatus().equals(12)
                                                    || manu.getManuscriptStatus().equals(13)
                                                    || manu.getManuscriptStatus().equals(14)
                                                    || manu.getManuscriptStatus().equals(15))
                                            .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                            .collect(Collectors.toList());
                                    if (null != reviewDuedate && reviewDuedate.size() > 0) {
                                        manuscriptListVO.setAssignDate(reviewDuedate.get(0).getAssignedDate());
                                        manuscriptListVO.setDueDate(reviewDuedate.get(0).getDueDate());
                                    }

                                }
                                // add reviewer Names start
                                List<ManuScriptReviewer> reviewerList = reviewerMapList
                                        .get(uniqueData.getManuScriptId());
                                List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
                                    ReviewerDetailsVO r = new ReviewerDetailsVO();
                                    r.setName(x.getUser().getFirstName() + " " + x.getUser().getLastName());
                                    r.setStatus(x.getStatusMaster().getStatusValue());
                                    r.setDueDate(x.getDueDate());
                                    r.setIsActive(x.getIsActive());
                                    return r;
                                }).collect(Collectors.toList());
                                manuscriptListVO.setManuScriptReviewers(reviewers); // add reviewer Names end
                                map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);
                            }
                        } catch (Exception ex) {
                            log.debug("Exception occured during finding the valid condition for No response reviewer"
                                    + ex.getMessage());
                        }
                    }
                }

            }
            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
                    manuscriptList.add(entry.getValue());
                }
            }

        }

        return manuscriptList;
    }

    @Override
    public List<AuthorSuggestedReviewerVo> authorSuggestedReviewer(Integer manuscriptId, Integer manuscriptVersion,
                                                                   Integer journalId) {
        List<AuthorSuggestedReviewerVo> rsd = new ArrayList<>();
        List<ReviewerSuggested> getauthorSuggestedReviewerData = ReviewerSuggestedRepo
                .getauthorSuggestedReviewer(manuscriptId, journalId);
        Integer authorId = manuScriptRepo.getAuthorId(manuscriptId);
        List<Integer> coAuthorList = manuScriptCoAuthorRepo.getCoAuthorForManuscript(manuscriptId);

        if (getauthorSuggestedReviewerData != null && !getauthorSuggestedReviewerData.isEmpty()) {
            getauthorSuggestedReviewerData.stream().forEach(i -> {
                if (authorId != null && !authorId.equals(i.getReviewerId()) && !coAuthorList.contains(i.getReviewerId())) {

                    AuthorSuggestedReviewerVo commonvo = new AuthorSuggestedReviewerVo();
                    if (i.getCountry() != null) {
                        Countries countries = countriesRepo.getOne(i.getCountry());
                        if (countries != null) {
                            commonvo.setCountry(countries.getName());
                        }
                    }
                    commonvo.setReviewerSuggestedId(i.getId());
                    commonvo.setEmail(i.getEmail());
                    commonvo.setReviewerName(i.getFirstName() + " " + i.getLastName());
                    commonvo.setSalutation(i.getSalutation());
                    commonvo.setFirstName(i.getFirstName());
                    commonvo.setLastName(i.getLastName());
                    commonvo.setSpecialization(i.getSpecialization());
                    commonvo.setSubSpecialization(i.getSubject());
                    commonvo.setInstitution(i.getInstitution());
                    commonvo.setIsSuggested(i.getIsSuggested());
                    commonvo.setManuscriptId(i.getManuscriptId());
                    commonvo.setManuscriptVersion(i.getManuscriptVersion());
                    commonvo.setSuggestedDate(i.getSuggestedOn());
                    commonvo.setJournalId(i.getJournalId());
                    rsd.add(commonvo);
                }
            });
        }
        Set<AuthorSuggestedReviewerVo> uniqueReviewers = new HashSet<>(rsd);
        
        return new ArrayList<>(uniqueReviewers);
    }

    @Override
    public List<AuthorSuggestedReviewerVo> authorSuggestedReviewerPagination(Integer manuscriptId, Integer manuscriptVersion,
                                                                             Integer journalId, Integer startIndex, Integer limit) {
        List<AuthorSuggestedReviewerVo> rsd = new ArrayList<>();
        List<ReviewerSuggested> getauthorSuggestedReviewerData = ReviewerSuggestedRepo
                .getauthorSuggestedReviewerPagination(manuscriptId, journalId, startIndex, limit);
        Integer authorId = manuScriptRepo.getAuthorId(manuscriptId);
        List<Integer> coAuthorList = manuScriptCoAuthorRepo.getCoAuthorForManuscript(manuscriptId);

        if (getauthorSuggestedReviewerData != null && !getauthorSuggestedReviewerData.isEmpty()) {
            getauthorSuggestedReviewerData.stream().forEach(i -> {
                if (authorId != null && !authorId.equals(i.getReviewerId()) && !coAuthorList.contains(i.getReviewerId())) {

                    AuthorSuggestedReviewerVo commonvo = new AuthorSuggestedReviewerVo();
                    if (i.getCountry() != null) {
                        Countries countries = countriesRepo.getOne(i.getCountry());
                        if (countries != null) {
                            commonvo.setCountry(countries.getName());
                        }
                    }
                    commonvo.setReviewerSuggestedId(i.getId());
                    commonvo.setEmail(i.getEmail());
                    commonvo.setReviewerName(i.getFirstName() + " " + i.getLastName());
                    commonvo.setSalutation(i.getSalutation());
                    commonvo.setFirstName(i.getFirstName());
                    commonvo.setLastName(i.getLastName());
                    commonvo.setSpecialization(i.getSpecialization());
                    commonvo.setSubSpecialization(i.getSubject());
                    commonvo.setInstitution(i.getInstitution());
                    commonvo.setIsSuggested(i.getIsSuggested());
                    commonvo.setManuscriptId(i.getManuscriptId());
                    commonvo.setManuscriptVersion(i.getManuscriptVersion());
                    commonvo.setSuggestedDate(i.getSuggestedOn());
                    commonvo.setJournalId(i.getJournalId());
                    rsd.add(commonvo);
                }
            });
        }
        return rsd;
    }

    @Override
    public Integer authorSuggestedReviewerCount(Integer manuscriptId,
                                                Integer journalId) {
        return ReviewerSuggestedRepo.getauthorSuggestedReviewerCount(manuscriptId, journalId);
    }

    @Override
    public List<ManuscriptListVO> getNoResponseManuscript(IDVO id) {
        List<ManuscriptListVO> manuscriptList = new ArrayList();

        if (id != null && id.getUserId() != null && id.getSearch() != null
                && id.getSearch() != "") {

            Map<String, ManuscriptListVO> map = new HashMap<>();
            List<ManuScript> editorDataList = manuScriptRepo.getManuscriptJournalAndEditorAndRoleSearch(
                    id.getJournalId(), id.getUserId(), id.getRoleId(), id.getSearch());
            List<Integer> manuscriptIdList = new ArrayList();
            if (editorDataList != null && !editorDataList.isEmpty()) {
                for (ManuScript editorDataListData : editorDataList) {
                    manuscriptIdList.add(editorDataListData.getManuScriptId());
                }

                if (manuscriptIdList.size() > 0) {
                    List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                            .getManuscriptByJournalEditor(id.getJournalId(), manuscriptIdList);
                    Map<Integer, List<ManuScriptReviewer>> reviewerGroup = getAllDataByjournal.stream()
                            .collect(Collectors.groupingBy(w -> w.getManuscriptId()));

                    if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
                        for (ManuScript uniqueData : editorDataList) {
                            int mcount = 0;
                            int mvalidcount = 0;
                            boolean isDueDateExpire = false;
                            try {
                                for (ManuScriptReviewer allData : reviewerGroup.get(uniqueData.getManuScriptId())) {
                                    if (uniqueData.getManuScriptId().equals(allData.getManuscriptId()) && allData
                                            .getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {

                                        mcount++;
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                        try {
                                            Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
                                            Date todayDate = df.parse(df.format(new Date()));

                                            if (allData.getIsActive() == 0
                                                    || ((allData.getManuscriptStatus() == 8
                                                    || allData.getManuscriptStatus() == 66
                                                    || allData.getManuscriptStatus() == 35)
                                                    && reviewerDueDate.before(todayDate))
                                                    || allData.getManuscriptStatus() == 60
                                                    || allData.getManuscriptStatus() == 36) {
                                                mvalidcount++;
                                            }

                                        } catch (ParseException e) {
                                            log.debug(
                                                    "Exception occured during finding the valid condition for No response reviewer"
                                                            + e.getMessage());
                                        }

                                    }
                                }
                                if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {
                                    ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                                    manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                    manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                                    manuscriptListVO.setAuthorName(uniqueData.getUser().getFirstName() + " "
                                            + uniqueData.getUser().getLastName());
                                    manuscriptListVO.setManuscriptAuthorId(uniqueData.getAuthorId());
                                    if (uniqueData.getManuscriptTypeDetails() != null
                                            && uniqueData.getManuscriptTypeDetails().getManuscriptType() != null) {
                                        manuscriptListVO.setManuScriptType(
                                                uniqueData.getManuscriptTypeDetails().getManuscriptType());
                                    }
                                    manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                    manuscriptListVO.setManuscripCode(uniqueData.getManuscriptCode());
                                    manuscriptListVO.setManuscripTitle(uniqueData.getManuscriptTitle());
                                    manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());

                                    manuscriptListVO.setEditorStatus(uniqueData.getStatusMaster().getStatusValue());
                                    if (isDueDateExpire) {
                                        manuscriptListVO.setSomeUserDeclined(0);
                                    }

                                    manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                                    // add due Date
                                    List<ManuScriptReviewer> reviewerDuedate = reviewerGroup
                                            .get(uniqueData.getManuScriptId()).stream()
                                            .filter(manu -> manu.getManuscriptStatus() == 8
                                                    || manu.getManuscriptStatus() == 35
                                                    || manu.getManuscriptStatus() == 66)
                                            .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                            .collect(Collectors.toList());
                                    if (reviewerDuedate != null && reviewerDuedate.size() > 0
                                            && reviewerDuedate.get(0).getDueDate() != null) {
                                        manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
                                        manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
                                    } else {
                                        List<ManuScriptReviewer> reviewDuedate = reviewerGroup
                                                .get(uniqueData.getManuScriptId()).stream()
                                                .filter(manu -> manu.getManuscriptStatus().equals(12)
                                                        || manu.getManuscriptStatus().equals(13)
                                                        || manu.getManuscriptStatus().equals(14)
                                                        || manu.getManuscriptStatus().equals(15)
                                                        || manu.getManuscriptStatus().equals(36)
                                                        || manu.getManuscriptStatus().equals(60)
                                                        )
                                                .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                                .collect(Collectors.toList());
                                        if (null != reviewDuedate && reviewDuedate.size() > 0) {
                                            manuscriptListVO.setAssignDate(reviewDuedate.get(0).getAssignedDate());
                                            manuscriptListVO.setDueDate(reviewDuedate.get(0).getDueDate());
                                        }

                                    }
                                    // add reviewer Names start
                                    List<ManuScriptReviewer> reviewerList = reviewerGroup
                                            .get(uniqueData.getManuScriptId());
                                    List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
                                        ReviewerDetailsVO r = new ReviewerDetailsVO();
                                        r.setName(x.getUser().getFirstName() + " " + x.getUser().getLastName());
                                        r.setStatus(x.getStatusMaster().getStatusValue());
                                        r.setDueDate(x.getDueDate());
                                        r.setIsActive(x.getIsActive());
                                        return r;
                                    }).collect(Collectors.toList());
                                    manuscriptListVO.setManuScriptReviewers(reviewers);
                                    // add reviewer Names end
                                    ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(uniqueData.getJournalId(), uniqueData.getManuScriptId(), uniqueData.getManuscriptVersion(), uniqueData.getAssignedUser());
                                    manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
                                    manuscriptListVO.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
                                    // manuscriptListEditor.add(manuscriptListVO);
                                    if (uniqueData.getIsActive().equals(1)) {
                                        map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);
                                    }
                                }

                            } catch (Exception e) {
                                log.debug(
                                        "Exception occured during finding the valid condition for No response reviewer"
                                                + e.getMessage());
                            }
                        }

                    }
                }

            }
            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
                    manuscriptList.add(entry.getValue());
                }
            }

        } else {

            Map<String, ManuscriptListVO> map = new HashMap<>();
            List<ManuScript> editorDataList = manuScriptRepo
                    .getManuscriptByJournalAndIdEditorAndRole(id.getJournalId(), id.getUserId(), id.getRoleId());
            List<Integer> manuscriptIdList = new ArrayList();
            if (editorDataList != null && !editorDataList.isEmpty()) {
                for (ManuScript editorDataListData : editorDataList) {
                    manuscriptIdList.add(editorDataListData.getManuScriptId());

                }

                if (manuscriptIdList.size() > 0) {
                    List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                            .getManuscriptByJournalEditor(id.getJournalId(), manuscriptIdList);
                    Map<Integer, List<ManuScriptReviewer>> reviewerGroup = getAllDataByjournal.stream()
                            .collect(Collectors.groupingBy(w -> w.getManuscriptId()));

                    if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
                        for (ManuScript uniqueData : editorDataList) {
                            int mcount = 0;
                            int mvalidcount = 0;
                            boolean isDueDateExpire = false;
                            try {
                                for (ManuScriptReviewer allData : reviewerGroup.get(uniqueData.getManuScriptId())) {
                                    if (uniqueData.getManuScriptId().equals(allData.getManuscriptId()) && allData
                                            .getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {

                                        mcount++;
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                        try {
                                            Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
                                            Date todayDate = df.parse(df.format(new Date()));

                                            if (allData.getIsActive() == 0
                                                    || ((allData.getManuscriptStatus() == 8
                                                    || allData.getManuscriptStatus() == 66
                                                    || allData.getManuscriptStatus() == 35)
                                                    && reviewerDueDate.before(todayDate))
                                                    || allData.getManuscriptStatus() == 60
                                                    || allData.getManuscriptStatus() == 36) {
                                                mvalidcount++;
                                            }

                                        } catch (ParseException e) {
                                            log.debug(
                                                    "Exception occured during finding the valid condition for No response reviewer"
                                                            + e.getMessage());
                                        }

                                    }
                                }
                                if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {
                                    ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                                    manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                    manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                                    manuscriptListVO.setAuthorName(uniqueData.getUser().getFirstName() + " "
                                            + uniqueData.getUser().getLastName());
                                    manuscriptListVO.setManuscriptAuthorId(uniqueData.getAuthorId());
                                    if (uniqueData.getManuscriptTypeDetails() != null
                                            && uniqueData.getManuscriptTypeDetails().getManuscriptType() != null) {
                                        manuscriptListVO.setManuScriptType(
                                                uniqueData.getManuscriptTypeDetails().getManuscriptType());
                                    }
                                    manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                    manuscriptListVO.setManuscripCode(uniqueData.getManuscriptCode());
                                    manuscriptListVO.setManuscripTitle(uniqueData.getManuscriptTitle());
                                    manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());

                                    manuscriptListVO.setEditorStatus(uniqueData.getStatusMaster().getStatusValue());
                                    if (isDueDateExpire) {
                                        manuscriptListVO.setSomeUserDeclined(0);
                                    }

                                    manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                                    // add due Date
                                    List<ManuScriptReviewer> reviewerDuedate = reviewerGroup
                                            .get(uniqueData.getManuScriptId()).stream()
                                            .filter(manu -> manu.getManuscriptStatus() == 8
                                                    || manu.getManuscriptStatus() == 35
                                                    || manu.getManuscriptStatus() == 66)
                                            .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                            .collect(Collectors.toList());
                                    if (reviewerDuedate != null && reviewerDuedate.size() > 0
                                            && reviewerDuedate.get(0).getDueDate() != null) {
                                        manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
                                        manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
                                    } else {
                                        List<ManuScriptReviewer> reviewDuedate = reviewerGroup
                                                .get(uniqueData.getManuScriptId()).stream()
                                                .filter(manu -> manu.getManuscriptStatus().equals(12)
                                                        || manu.getManuscriptStatus().equals(13)
                                                        || manu.getManuscriptStatus().equals(14)
                                                        || manu.getManuscriptStatus().equals(15)
                                                        || manu.getManuscriptStatus().equals(36)
                                                        || manu.getManuscriptStatus().equals(60)
                                                        )
                                                .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                                .collect(Collectors.toList());
                                        if (null != reviewDuedate && reviewDuedate.size() > 0) {
                                            manuscriptListVO.setAssignDate(reviewDuedate.get(0).getAssignedDate());
                                            manuscriptListVO.setDueDate(reviewDuedate.get(0).getDueDate());
                                        }

                                    }
                                    // add reviewer Names start
                                    List<ManuScriptReviewer> reviewerList = reviewerGroup
                                            .get(uniqueData.getManuScriptId());
                                    List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
                                        ReviewerDetailsVO r = new ReviewerDetailsVO();
                                        r.setName(x.getUser().getFirstName() + " " + x.getUser().getLastName());
                                        r.setStatus(x.getStatusMaster().getStatusValue());
                                        r.setDueDate(x.getDueDate());
                                        r.setIsActive(x.getIsActive());
                                        return r;
                                    }).collect(Collectors.toList());
                                    manuscriptListVO.setManuScriptReviewers(reviewers);
                                    // add reviewer Names end
                                    ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(uniqueData.getJournalId(), uniqueData.getManuScriptId(), uniqueData.getManuscriptVersion(), uniqueData.getAssignedUser());
                                    manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
                                    manuscriptListVO.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
                                    // manuscriptListEditor.add(manuscriptListVO);
                                    if (uniqueData.getIsActive().equals(1)) {
                                        map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);
                                    }
                                }

                            } catch (Exception e) {
                                log.debug(
                                        "Exception occured during finding the valid condition for No response reviewer"
                                                + e.getMessage());
                            }
                        }

                    }
                }

            }
            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
                    manuscriptList.add(entry.getValue());
                }
            }

        }

        return manuscriptList;

    }

    @Override
    public List<ManuscriptListVO> getCommentaryData(Integer journalId) {
        List<ManuscriptListVO> manuscriptList = new ArrayList();
        List<ManuScriptReviewer> getData = manuScriptReviewerRepo.getCommentaryData(journalId);
        if (getData != null && !getData.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewer : getData) {

                User user = userRepo.getOne(manuScriptReviewer.getManuscript().getAuthorId());
                ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                manuscriptListVO.setJournalId(manuScriptReviewer.getJournalId());
                manuscriptListVO.setManuscripCode(manuScriptReviewer.getManuscript().getManuscriptCode());
                manuscriptListVO.setManuscriptAuthorId(manuScriptReviewer.getManuscript().getAuthorId());
                manuscriptListVO.setManuscripTitle(manuScriptReviewer.getManuscript().getManuscriptTitle());
                manuscriptListVO.setManuScriptId(manuScriptReviewer.getManuscript().getManuScriptId());
                manuscriptListVO.setUserId(manuScriptReviewer.getReviewerId());
                manuscriptListVO.setManuscriptVersion(manuScriptReviewer.getManuscript().getManuscriptVersion());
                manuscriptListVO.setSubmissionDate(manuScriptReviewer.getManuscript().getSubmissionDate());
                manuscriptListVO.setSubmittedBy(manuScriptReviewer.getManuscript().getSubmittedBy());
                manuscriptListVO.setCreatedBy(manuScriptReviewer.getManuscript().getCreatedBy());
                manuscriptListVO.setCreatedOn(manuScriptReviewer.getManuscript().getCreatedOn());
                manuscriptListVO.setUpdatedBy(manuScriptReviewer.getManuscript().getUpdatedBy());
                manuscriptListVO.setUpdatedOn(manuScriptReviewer.getManuscript().getUpdatedOn());
                manuscriptListVO.setManuStatusCode(manuScriptReviewer.getStatusMaster().getStatusValue());
                manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
                manuscriptListVO.setManuStatusId(manuScriptReviewer.getManuscriptStatus());

                manuscriptList.add(manuscriptListVO);

            }
        }

        return manuscriptList;
    }

    @Override
    public void updateInvitation(IDVO id) {
        ManuScriptReviewer manuScriptReviewer = manuScriptReviewerRepo.findManuscriptReviewerDetails(
                id.getManuscriptId(), id.getManuscriptVersion(), id.getJournalId(), id.getUserId());
        if (manuScriptReviewer != null) {
            manuScriptReviewer.setIsInvitedForCommentary(1);
            manuScriptReviewerRepo.save(manuScriptReviewer);
        }
    }

    @Override
    public List<ManuscriptListVO> getCommentaryInvitation(Integer journalId, Integer userId) {
        List<ManuscriptListVO> manuscriptList = new ArrayList();
        List<ManuScriptReviewer> getData = manuScriptReviewerRepo.getCommentaryInvitation(journalId, userId);
        if (getData != null && !getData.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewer : getData) {

                User user = userRepo.getOne(manuScriptReviewer.getManuscript().getAuthorId());
                ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                manuscriptListVO.setJournalId(manuScriptReviewer.getJournalId());
                manuscriptListVO.setManuscripCode(manuScriptReviewer.getManuscript().getManuscriptCode());
                manuscriptListVO.setManuscriptAuthorId(manuScriptReviewer.getManuscript().getAuthorId());
                manuscriptListVO.setManuscripTitle(manuScriptReviewer.getManuscript().getManuscriptTitle());
                manuscriptListVO.setManuScriptId(manuScriptReviewer.getManuscript().getManuScriptId());
                manuscriptListVO.setUserId(manuScriptReviewer.getReviewerId());
                manuscriptListVO.setManuscriptVersion(manuScriptReviewer.getManuscript().getManuscriptVersion());
                manuscriptListVO.setSubmissionDate(manuScriptReviewer.getManuscript().getSubmissionDate());
                manuscriptListVO.setSubmittedBy(manuScriptReviewer.getManuscript().getSubmittedBy());
                manuscriptListVO.setCreatedBy(manuScriptReviewer.getManuscript().getCreatedBy());
                manuscriptListVO.setCreatedOn(manuScriptReviewer.getManuscript().getCreatedOn());
                manuscriptListVO.setUpdatedBy(manuScriptReviewer.getManuscript().getUpdatedBy());
                manuscriptListVO.setUpdatedOn(manuScriptReviewer.getManuscript().getUpdatedOn());
                manuscriptListVO.setManuStatusCode(manuScriptReviewer.getStatusMaster().getStatusValue());
                manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
                manuscriptListVO.setManuStatusId(manuScriptReviewer.getManuscriptStatus());

                manuscriptList.add(manuscriptListVO);

            }
        }

        return manuscriptList;
    }

    @Override
    public ManuscriptListVO getReviewerStatistics(Integer journalId, Integer userId) {
        ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
        List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo.getReviewerStatistics(journalId, userId);
        Integer repliedOnTime = 0;
        Integer repliedAfterDueDate = 0;
        Integer didNotReply = 0;
        Integer currentlyInHand = 0;
        if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewer : getAllDataByjournal) {
                if (manuScriptReviewer.getIsActive() != null && manuScriptReviewer.getIsActive() == 0) {
                    repliedAfterDueDate++;
                } else if (manuScriptReviewer.getManuscriptStatus() == 8) {
                    didNotReply++;
                } else if (manuScriptReviewer.getManuscriptStatus() == 12
                        && manuScriptReviewer.getManuscriptStatus() == 13
                        && manuScriptReviewer.getManuscriptStatus() == 14
                        && manuScriptReviewer.getManuscriptStatus() == 15) {
                    repliedOnTime++;
                } else {
                    currentlyInHand++;
                }
            }
        }
        manuscriptListVO.setRepliedOnTime(repliedOnTime);
        manuscriptListVO.setRepliedAfterDueDate(repliedAfterDueDate);
        manuscriptListVO.setDidNotReply(didNotReply);
        manuscriptListVO.setCurrentlyInHand(currentlyInHand);
        return manuscriptListVO;
    }

    @Override
    public List<ManuscriptListVO> getReviewerByJournal(Integer journalId) {
        List<ManuscriptListVO> data = new ArrayList();
        List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                .getManuscriptReviewerListByJournalId(journalId);
        if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewer : getAllDataByjournal) {
                User user = userRepo.getOne(manuScriptReviewer.getReviewerId());
                ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                manuscriptListVO.setUserId(manuScriptReviewer.getReviewerId());
                if (user.getFirstName() != null && user.getLastName() != null)
                    manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
                ;
                data.add(manuscriptListVO);
            }
        }
        return data;
    }

    @Override
    public List<ManuscriptListVO> getInReviewManuscript(IDVO id) {
        List<ManuscriptListVO> manuscriptList = new ArrayList();

        List<ManuScriptForDashboard> list = new ArrayList<>();
        if (id != null && id.getRoleId() != null && id.getSearch() != null && id.getSearch() != "") {

            List<ManuScript> editorDataList = manuScriptRepo.getManuscriptByJournalAndIdEditorAndRoleByManuId(
                    id.getJournalId(), id.getUserId(), id.getRoleId(), id.getSearch());
            List<Integer> manuscriptIdList = new ArrayList();

            if (editorDataList != null && !editorDataList.isEmpty()) {
                for (ManuScript editorDataListData : editorDataList) {
                    if (editorDataListData.getManuscriptStatusId() != null) {
                        manuscriptIdList.add(editorDataListData.getManuScriptId());
                    }
                }

                if (manuscriptIdList.size() > 0) {
                    List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
                            .getInReviewManuscriptForEditor(id.getJournalId(), manuscriptIdList);

                    List<ManuScriptReviewer> getAllData = manuScriptReviewerRepo
                            .getReviewedManuscriptEditor(id.getJournalId(), manuscriptIdList);
                    Map<Integer, List<ManuScriptReviewer>> reviewerMapList = getAllData.stream()
                            .collect(Collectors.groupingBy(w -> w.getManuscriptId()));

                    if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
                        for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
                            boolean isDueDateExpire = false;
                            Integer reviewedCount = 0;
                            Integer totalReviewCount = 0;
                            Integer blinkerCount = 0;
                            try {
                                if (reviewerMapList != null && !reviewerMapList.isEmpty()) {
                                    for (ManuScriptReviewer manuScriptReviewer : reviewerMapList
                                            .get(uniqueData.getManuscriptId())) {
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                        Date reviewerDueDate = df.parse(df.format(manuScriptReviewer.getDueDate()));
                                        Date todayDate = df.parse(df.format(new Date()));
                                        if (reviewerDueDate.after(todayDate) || todayDate.equals(reviewerDueDate)) {
                                            blinkerCount++;
                                        }
                                        try {
                                            if (manuScriptReviewer.getManuscriptStatus().equals(12)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(13)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(14)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(15)) {
                                                reviewedCount++;
                                            }
                                            if (manuScriptReviewer.getManuscriptStatus().equals(12)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(13)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(14)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(15)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(8)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(66)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(35)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(36)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(60)) {
                                                totalReviewCount++;
                                            }
                                            if (uniqueData.getDueDate() != null) {
                                                Calendar c = Calendar.getInstance();
                                                c.setTime(new Date()); // Now use today date.
                                                c.add(Calendar.DATE, 2);
                                                String output = df.format(c.getTime());
                                                Date d1 = df.parse(uniqueData.getDueDate().toString());
                                                Date d2 = df.parse(output.toString());

                                                if (d1.compareTo(d2) < 0) {
                                                    isDueDateExpire = true;
                                                }
                                            }

                                        } catch (ParseException e) {
                                            log.debug(
                                                    "Exception occured during finding the valid condition for In review manuscript"
                                                            + e.getMessage());
                                        }

                                    }
                                }
                                ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                                manuscriptListVO.setReviewdCount(reviewedCount);
                                manuscriptListVO.setTotalReviewCount(totalReviewCount);
                                IDVO id1 = new IDVO();
                                id1.setId(uniqueData.getManuscriptId());
                                id1.setVersion(uniqueData.getManuscriptVersion());

                                manuscriptListVO.setBlinkerCount(blinkerCount);
                                manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
                                manuscriptListVO.setManuscriptAuthorId(uniqueData.getManuscript().getAuthorId());
                                manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                                List<ManuScriptReviewer> reviewerDuedate = reviewerMapList
                                        .get(uniqueData.getManuscriptId()).stream()
                                        .filter(manu -> manu.getManuscriptStatus() == 8
                                                || manu.getManuscriptStatus() == 35 || manu.getManuscriptStatus() == 66)
                                        .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                        .collect(Collectors.toList());
                                if (reviewerDuedate != null && reviewerDuedate.size() > 0
                                        && reviewerDuedate.get(0).getDueDate() != null) {
                                    manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
                                    manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
                                }
                                // add reviewer Names start
                                List<ManuScriptReviewer> reviewerList = reviewerMapList
                                        .get(uniqueData.getManuscriptId());
                                List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
                                    ReviewerDetailsVO r = new ReviewerDetailsVO();
                                    r.setName(x.getUser().getFirstName() + " " + x.getUser().getLastName());
                                    r.setStatus(x.getStatusMaster().getStatusValue());
                                    r.setDueDate(x.getDueDate());
                                    r.setIsActive(x.getIsActive());
                                    return r;
                                }).collect(Collectors.toList());
                                manuscriptListVO.setManuScriptReviewers(reviewers);
                                // add reviewer Names end
                                manuscriptListVO.setAuthorName(uniqueData.getManuscript().getUser().getFirstName() + " "
                                        + uniqueData.getManuscript().getUser().getLastName());
                                if (uniqueData.getManuscript().getManuscriptTypeDetails() != null && uniqueData
                                        .getManuscript().getManuscriptTypeDetails().getManuscriptType() != null) {
                                    manuscriptListVO.setManuScriptType(
                                            uniqueData.getManuscript().getManuscriptTypeDetails().getManuscriptType());
                                }
                                manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
                                manuscriptListVO.setManuscripCode(uniqueData.getManuscript().getManuscriptCode());
                                manuscriptListVO.setManuscripTitle(uniqueData.getManuscript().getManuscriptTitle());
                                manuscriptListVO.setSubmissionDate(uniqueData.getManuscript().getSubmissionDate());
                                manuscriptListVO.setEditorStatus(uniqueData.getStatusMaster().getStatusValue());

                                manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                                manuscriptListVO.setStatusValueForAuthor(
                                        uniqueData.getStatusMaster().getStatusValueForAuthor());
                                if (isDueDateExpire) {
                                    manuscriptListVO.setSomeUserDeclined(0);
                                }
                                ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(uniqueData.getJournalId(), uniqueData.getManuscriptId(), uniqueData.getManuscript().getManuscriptVersion(), uniqueData.getManuscript().getAssignedUser());
                                manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
                                manuscriptListVO.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
                                manuscriptListVO.setStatusValueForEditor(
                                        uniqueData.getStatusMaster().getStatusValueForEditor());
                                manuscriptList.add(manuscriptListVO);
                            } catch (Exception ex) {
                                log.debug(
                                        "Exception occured during finding the valid condition for In review manuscript"
                                                + ex.getMessage());
                            }
                        }
                    }
                }

            }

        } else {
            List<ManuScript> editorDataList = manuScriptRepo
                    .getManuscriptByJournalAndIdEditorAndRole(id.getJournalId(), id.getUserId(), id.getRoleId());
            List<Integer> manuscriptIdList = new ArrayList();
            if (editorDataList != null && !editorDataList.isEmpty()) {
                for (ManuScript editorDataListData : editorDataList) {
                    if (editorDataListData.getManuscriptStatusId() != null) {
                        manuscriptIdList.add(editorDataListData.getManuScriptId());
                    }
                }

                if (manuscriptIdList.size() > 0) {
                    List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
                            .getInReviewManuscriptForEditor(id.getJournalId(), manuscriptIdList);

                    List<ManuScriptReviewer> getAllData = manuScriptReviewerRepo
                            .getReviewedManuscriptEditor(id.getJournalId(), manuscriptIdList);
                    Map<Integer, List<ManuScriptReviewer>> reviewerMapList = getAllData.stream()
                            .collect(Collectors.groupingBy(w -> w.getManuscriptId()));

                    if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
                        for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
                            boolean isDueDateExpire = false;
                            Integer reviewedCount = 0;
                            Integer totalReviewCount = 0;
                            Integer blinkerCount = 0;
                            try {
                                if (reviewerMapList != null && !reviewerMapList.isEmpty()) {
                                    for (ManuScriptReviewer manuScriptReviewer : reviewerMapList
                                            .get(uniqueData.getManuscriptId())) {
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                        Date reviewerDueDate = df.parse(df.format(manuScriptReviewer.getDueDate()));
                                        Date todayDate = df.parse(df.format(new Date()));
                                        if (reviewerDueDate.after(todayDate) || todayDate.equals(reviewerDueDate)) {
                                            blinkerCount++;
                                        }
                                        try {
                                            if (manuScriptReviewer.getManuscriptStatus().equals(12)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(13)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(14)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(15)) {
                                                reviewedCount++;
                                            }
                                            if (manuScriptReviewer.getManuscriptStatus().equals(12)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(13)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(14)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(15)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(8)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(66)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(35)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(36)
                                                    || manuScriptReviewer.getManuscriptStatus().equals(60)) {
                                                totalReviewCount++;
                                            }
                                            if (uniqueData.getDueDate() != null) {
                                                Calendar c = Calendar.getInstance();
                                                c.setTime(new Date()); // Now use today date.
                                                c.add(Calendar.DATE, 2);
                                                String output = df.format(c.getTime());
                                                Date d1 = df.parse(uniqueData.getDueDate().toString());
                                                Date d2 = df.parse(output.toString());

                                                if (d1.compareTo(d2) < 0) {
                                                    isDueDateExpire = true;
                                                }
                                            }

                                        } catch (ParseException e) {
                                            log.debug(
                                                    "Exception occured during finding the valid condition for In review manuscript"
                                                            + e.getMessage());
                                        }

                                    }
                                }
                                ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                                manuscriptListVO.setReviewdCount(reviewedCount);
                                manuscriptListVO.setTotalReviewCount(totalReviewCount);
                                IDVO id1 = new IDVO();
                                id1.setId(uniqueData.getManuscriptId());
                                id1.setVersion(uniqueData.getManuscriptVersion());

                                manuscriptListVO.setBlinkerCount(blinkerCount);
                                manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
                                manuscriptListVO.setManuscriptAuthorId(uniqueData.getManuscript().getAuthorId());
                                manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                                List<ManuScriptReviewer> reviewerDuedate = reviewerMapList
                                        .get(uniqueData.getManuscriptId()).stream()
                                        .filter(manus -> manus.getManuscriptStatus() == 8
                                                || manus.getManuscriptStatus() == 35
                                                || manus.getManuscriptStatus() == 66)
                                        .sorted(Comparator.comparing(ManuScriptReviewer::getDueDate).reversed())
                                        .collect(Collectors.toList());
                                if (reviewerDuedate != null && reviewerDuedate.size() > 0
                                        && reviewerDuedate.get(0).getDueDate() != null) {
                                    manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
                                    manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
                                }
                                // add reviewer Names start
                                List<ManuScriptReviewer> reviewerList = reviewerMapList
                                        .get(uniqueData.getManuscriptId());
                                List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
                                    ReviewerDetailsVO r = new ReviewerDetailsVO();
                                    r.setName(x.getUser().getFirstName() + " " + x.getUser().getLastName());
                                    r.setStatus(x.getStatusMaster().getStatusValue());
                                    r.setDueDate(x.getDueDate());
                                    r.setIsActive(x.getIsActive());
                                    return r;
                                }).collect(Collectors.toList());
                                manuscriptListVO.setManuScriptReviewers(reviewers);
                                // add reviewer Names end
                                manuscriptListVO.setAuthorName(uniqueData.getManuscript().getUser().getFirstName() + " "
                                        + uniqueData.getManuscript().getUser().getLastName());
                                if (uniqueData.getManuscript().getManuscriptTypeDetails() != null && uniqueData
                                        .getManuscript().getManuscriptTypeDetails().getManuscriptType() != null) {
                                    manuscriptListVO.setManuScriptType(
                                            uniqueData.getManuscript().getManuscriptTypeDetails().getManuscriptType());
                                }
                                manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
                                manuscriptListVO.setManuscripCode(uniqueData.getManuscript().getManuscriptCode());
                                manuscriptListVO.setManuscripTitle(uniqueData.getManuscript().getManuscriptTitle());
                                manuscriptListVO.setSubmissionDate(uniqueData.getManuscript().getSubmissionDate());
                                manuscriptListVO.setEditorStatus(uniqueData.getStatusMaster().getStatusValue());

                                manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                                manuscriptListVO.setStatusValueForAuthor(
                                        uniqueData.getStatusMaster().getStatusValueForAuthor());
                                if (isDueDateExpire) {
                                    manuscriptListVO.setSomeUserDeclined(0);
                                }
                                ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(uniqueData.getJournalId(), uniqueData.getManuscriptId(), uniqueData.getManuscript().getManuscriptVersion(), uniqueData.getManuscript().getAssignedUser());
                                manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
                                manuscriptListVO.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
                                manuscriptListVO.setStatusValueForEditor(
                                        uniqueData.getStatusMaster().getStatusValueForEditor());
                                manuscriptList.add(manuscriptListVO);
                            } catch (Exception ex) {
                                log.debug(
                                        "Exception occured during finding the valid condition for In review manuscript"
                                                + ex.getMessage());
                            }
                        }
                    }
                }

            }
        }

        return manuscriptList;
    }

    @Override
    public List<ManuscriptListVO> getReviewerOnManuscript(IDVO id) {
        List<ManuscriptListVO> dataList = new ArrayList();
        List<ManuScriptReviewer> data = manuScriptReviewerRepo
                .getManuscriptReviewerListByManuScriptIdAndVersionIsActive(id.getManuscriptId(),
                        id.getManuscriptVersion());
        for (ManuScriptReviewer manuScriptReviewer : data) {
            ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
            manuscriptListVO.setManuScriptId(manuScriptReviewer.getManuscriptId());
            manuscriptListVO.setManuscriptVersion(manuScriptReviewer.getManuscriptVersion());
            manuscriptListVO.setManuStatusId(manuScriptReviewer.getManuscriptStatus());
            manuscriptListVO.setRoleId(manuScriptReviewer.getReviewerId());
            if (manuScriptReviewer.getManuscriptStatus() != null) {
                StatusMaster statusMaster = statusMasterRepo.getOne(manuScriptReviewer.getManuscriptStatus());
                manuscriptListVO.setMessage(statusMaster.getStatusValue());
            }
            if (manuScriptReviewer.getReviewerId() != null) {
                User user = userRepo.getOne(manuScriptReviewer.getReviewerId());
                manuscriptListVO.setAuthorName(user.getFirstName() + " " + user.getLastName());
            }
            dataList.add(manuscriptListVO);
        }
        return dataList;
    }

    @Override
    public void deActivateReviewer(IDVO id, Integer manuscriptReviewerId) {
        manuScriptReviewerRepo.deActivateReviewer(id.getManuscriptId(), id.getManuscriptVersion(), id.getId(),
                manuscriptReviewerId);
        Integer reviewerCount = manuScriptReviewerRepo.activeReviewerCount(id.getManuscriptId(), id.getJournalId(),
                id.getManuscriptVersion());
        ManuScript manuscriptDetails = manuScriptRepo.getOne(id.getManuscriptId());
        if (reviewerCount.equals(0) && id.getManuscriptVersion() > 0) {
            manuscriptDetails.setManuscriptStatusId(26);
            manuScriptRepo.saveAndFlush(manuscriptDetails);
        } else if (reviewerCount.equals(0) && manuscriptDetails.getTechnicalModificationDate() != null) {
            manuscriptDetails.setManuscriptStatusId(50);
            manuScriptRepo.saveAndFlush(manuscriptDetails);
        } else if (reviewerCount.equals(0) && id.getManuscriptVersion() == 0
                && manuscriptDetails.getTechnicalModificationDate() == null && id.getRoleId().equals(3)) {
            manuscriptDetails.setManuscriptStatusId(3);
            manuScriptRepo.saveAndFlush(manuscriptDetails);
        } else if (reviewerCount.equals(0) && id.getManuscriptVersion() == 0
                && manuscriptDetails.getTechnicalModificationDate() == null && id.getRoleId().equals(2)) {
            manuscriptDetails.setManuscriptStatusId(2);
            manuScriptRepo.saveAndFlush(manuscriptDetails);
        }
    }

    @Override
    public void decliendReview(IDVO id) {
        manuScriptReviewerRepo.decliendReview(id.getJournalId(), id.getManuId(), id.getManuscriptVersion(),
                id.getUserId(), id.getManuscriptId());

    }

    @Override
    public List<ManuScriptReviewerCommonVO> systemSuggestedReviewer(Integer manuscriptId, Integer manuscriptVersion,
                                                                    Integer journalId) {
        List<String> keywordList = new ArrayList();
        List<ManuScriptReviewerCommonVO> reviewerList = new ArrayList<>();
        List<UsersDupVo> user = new ArrayList();
        ManuScript manuscript = manuScriptRepo.getById(manuscriptId);
        List<ManuscriptJournalSubject> manuscriptJournalSubjectList = manuscriptJournalSubjectRepo
                .findByManuscriptId(manuscriptId, 1);
        if (manuscriptJournalSubjectList != null && !manuscriptJournalSubjectList.isEmpty()) {
            for (ManuscriptJournalSubject manuscriptJournalSubject : manuscriptJournalSubjectList) {
                keywordList.add(manuscriptJournalSubject.getJournalSubject().getJournalSubject());
            }
        }
        if (manuscript != null && manuscript.getManuscriptKeywords() != null
                && manuscript.getManuscriptKeywords() != "") {
            String keyWordArray[] = manuscript.getManuscriptKeywords().split(",");
            for (String string : keyWordArray) {
                keywordList.add(string);
            }
        }
        if (keywordList != null && !keywordList.isEmpty()) {
            List<Integer> inProcessReviewer = manuScriptReviewerRepo
                    .getInprocessReviewerList(manuscript.getManuScriptId(), manuscript.getManuscriptVersion());
            inProcessReviewer.add(manuscript.getAuthorId());
            List<Integer> userId = new ArrayList<>();
            if (inProcessReviewer.size() > 0) {
                userId = userJournalRepo.getAllReviewerList(journalId, inProcessReviewer);
            } else {
                userId = userJournalRepo.getAllReviewerIdList(journalId, manuscript.getAuthorId());

            }
            user = userRepo.getUserBySpecializationAndSubjectVoList(keywordList, userId);
        }
        if (user != null && !user.isEmpty()) {

            int count = 0;

            for (UsersDupVo userData : user) {

                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo.getReviewerDataById(userData.getId(),
                        journalId);
                Long articlesInHandCount = getArticlesInHands(journalId, userData.getId());
                commonvo.setArticlesInHandCount(articlesInHandCount.intValue());
                List<Integer> reviewStatusCode = Arrays.asList(12, 13, 14, 15);
                Long articlesInReviewCount = manuScriptReviewerRepo
                        .getReviewedManuscriptForReviewer(reviewStatusCode, journalId, userData.getId()).stream()
                        .count();
                commonvo.setTotalArticlesReviewed(articlesInReviewCount.intValue());
                List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream()
                        .filter(obj -> obj.getCompleteDate() != null).collect(Collectors.toList());
                Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream()
                        .filter(obj -> reviewStatusCode.contains(obj.getManuscriptStatus()))
                        .sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed()).findFirst();
                if (lastReviewedDate.isPresent() && lastReviewedDate.get().getCompleteDate() != null) {
                    commonvo.setLastReviewedDate(lastReviewedDate.get().getCompleteDate());
                }
                commonvo.setName(userData.getFirstName() + " " + userData.getLastName());
                commonvo.setEmail(userData.getEmail());
                commonvo.setReviewerId(userData.getId());
                commonvo.setSpecialization(userData.getSpecialization());
                commonvo.setInstitution(userData.getInstitute());
                if (userData.getExpertise() != null)
                    commonvo.setSubSpecialization(userData.getExpertise());
                if (userData.getCountries() != null) {
                    commonvo.setCountry(userData.getCountries().getName());
                }
                List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                commonvo.setReviewerAvailability(tempReviewer);
                reviewerList.add(commonvo);
                count++;
                if (count >= 100) {
                    break;
                }
            }
        }
        return reviewerList;
    }

    @Override
    public boolean isAssignExist(ManuScriptReviewerVO manuScriptReviewerVo) {
        List<ManuScriptReviewer> getManuScriptReviewerData = manuScriptReviewerRepo.isAssignExist(
                manuScriptReviewerVo.getManuscriptId(), manuScriptReviewerVo.getManuscriptVersion(),
                manuScriptReviewerVo.getReviewerId(), manuScriptReviewerVo.getManuscriptStatus(),
                manuScriptReviewerVo.getJournalId());
        if (getManuScriptReviewerData != null && !getManuScriptReviewerData.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDeclineExist(ManuScriptReviewerVO manuScriptReviewerVo) {
        List<ManuScriptReviewer> getManuScriptReviewerData = manuScriptReviewerRepo.isAssignExist(
                manuScriptReviewerVo.getManuscriptId(), manuScriptReviewerVo.getManuscriptVersion(),
                manuScriptReviewerVo.getReviewerId(), 36, manuScriptReviewerVo.getJournalId());
        if (getManuScriptReviewerData != null && !getManuScriptReviewerData.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public List<ManuScriptReviewerVO> reviewerPreviousComment(IDVO id) {
        List<ManuScriptReviewer> manuScriptReviewList = manuScriptReviewerRepo.reviewerPreviousComment(id.getUserId(),
                id.getManuscriptId());
        List<ManuScriptReviewerVO> dataList = new ArrayList();
        if (manuScriptReviewList != null && !manuScriptReviewList.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewer : manuScriptReviewList) {
                ManuScriptReviewerVO manuScriptReviewerVO = new ManuScriptReviewerVO();
                manuScriptReviewerVO.setCommentsToAuthor(manuScriptReviewer.getCommentsToAuthor());
                manuScriptReviewerVO.setCommentsToEditor(manuScriptReviewer.getCommentsToEditor());
                manuScriptReviewerVO.setDecisionLetter(manuScriptReviewer.getDecisionLetter());
                manuScriptReviewerVO.setManuscriptId(manuScriptReviewer.getManuscriptId());
                manuScriptReviewerVO.setManuscriptVersion(manuScriptReviewer.getManuscriptVersion());
                manuScriptReviewerVO.setReviewerId(manuScriptReviewer.getReviewerId());
                dataList.add(manuScriptReviewerVO);
            }
        }
        return dataList;
    }

    @Override
    public List<ManuScriptReviewerCommonVO> intrestedToReAccess(Integer manuscriptId, Integer manuscriptVersion,
                                                                Integer journalId) {
        List<ManuScriptReviewerCommonVO> reviewerList = new ArrayList<>();
        List<ManuScriptReviewer> reviewerData = manuScriptReviewerRepo.intrestedToReAccess(manuscriptId);
        if (reviewerData != null && !reviewerData.isEmpty()) {
            reviewerData.stream().forEach(list -> {
                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                List<Integer> inReviewStatus = Arrays.asList(8, 35, 66);
                List<Integer> reviewdStatus = Arrays.asList(12, 13, 14, 15, 36, 60, 30);
                long reviewedCount = manuScriptReviewerRepo
                        .getReviewerForIntrAcess(reviewdStatus, manuscriptId, list.getReviewerId()).stream().count();
                long inRevieweCount = manuScriptReviewerRepo
                        .getReviewerForManuscript(inReviewStatus, manuscriptId, list.getReviewerId(), manuscriptVersion)
                        .stream().count();
                if (reviewedCount > 0 && inRevieweCount == 0) {
                    List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo
                            .getReviewerDataById(list.getReviewerId(), list.getJournalId());
                    Long articlesInHandCount = getArticlesInHands(list.getJournalId(), list.getReviewerId());
                    commonvo.setArticlesInHandCount(articlesInHandCount.intValue());
                    List<Integer> reviewStatusCode = Arrays.asList(12, 13, 14, 15, 36, 60, 30);
                    Long articlesInReviewCount = manuScriptReviewerRepo
                            .getReviewedManuscriptForReviewer(reviewStatusCode, list.getJournalId(),
                                    list.getReviewerId())
                            .stream().count();
                    commonvo.setTotalArticlesReviewed(articlesInReviewCount.intValue());
                    List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream()
                            .filter(obj -> obj.getCompleteDate() != null).collect(Collectors.toList());
                    Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream()
                            .filter(obj -> reviewStatusCode.contains(obj.getManuscriptStatus()))
                            .sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed()).findFirst();
                    if (lastReviewedDate.isPresent() && lastReviewedDate.get().getCompleteDate() != null) {
                        commonvo.setLastReviewedDate(lastReviewedDate.get().getCompleteDate());
                    }
                    commonvo.setManuscriptId(list.getManuscriptId());
                    commonvo.setManuscriptVersion(list.getManuscriptVersion());
                    commonvo.setReviewerId(list.getReviewerId());
                    commonvo.setManuscriptStatus(list.getManuscriptStatus());
                    commonvo.setDecisionLetter(list.getDecisionLetter());
                    commonvo.setCommentsToAuthor(list.getCommentsToAuthor());
                    commonvo.setCommentsToEditor(list.getCommentsToEditor());
                    commonvo.setAssignedDate(list.getAssignedDate());
                    commonvo.setAcceptDate(list.getAcceptDate());
                    commonvo.setRejectDate(list.getRejectDate());
                    commonvo.setIsAssigned(list.getIsAssigned());
                    commonvo.setDueDate(list.getDueDate());
                    commonvo.setJournalId(list.getJournalId());
                    if (list.getReasses() != null && list.getReasses().equalsIgnoreCase("Yes")) {
                        commonvo.setTempFlag("YES");
                    }
                    StatusMaster st = statusMasterRepo.getOne(list.getManuscriptStatus());
                    commonvo.setStatusValue(st.getStatusValue());
                    if (list.getReviewerId() != null) {
                        User user = userRepo.getOne(list.getReviewerId());
                        if (user != null && user.getFirstName() != null) {
                            commonvo.setName(user.getFirstName() + " " + user.getLastName());
                            commonvo.setEmail(user.getEmail());
                            commonvo.setSpecialization(user.getSpecialization());
                            commonvo.setInstitution(user.getInstitute());
                            if (user.getExpertise() != null)
                                commonvo.setSubSpecialization(user.getExpertise());
                            if (user.getCountryId() != null) {
                                Countries countries = countriesRepo.getOne(user.getCountryId());
                                if (countries != null) {
                                    commonvo.setCountry(countries.getName());
                                }
                            }
                        }
                    }
                    ManuScript ms = manuScriptRepo.getOne(list.getManuscriptId());
                    commonvo.setManuscriptCode(ms.getManuscriptCode());
                    List<ReviewerAvailability> getReviewerAvailabilityData = reviewerAvailabilityRepo
                            .getReviewerAvailabilityById(list.getReviewerId());
                    if (getReviewerAvailabilityData != null && !getReviewerAvailabilityData.isEmpty()) {
                        getReviewerAvailabilityData.stream().forEach(l -> {
                            List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                            tempReviewer.add(l);
                            commonvo.setReviewerAvailability(tempReviewer);
                        });
                    }
                    reviewerList.add(commonvo);
                }
            });
        }
        return reviewerList;

    }
    @Override
    public List<ManuScriptReviewerCommonVO> intrestedToReAccessBySearch(Integer manuscriptId, Integer manuscriptVersion,
                                                                        Integer journalId, String search) {
        String finalSearch = (search == null) ? "" : search.toLowerCase();
        
        List<ManuScriptReviewerCommonVO> reviewerList = new ArrayList<>();
        
        List<ManuScriptReviewer> reviewerData = manuScriptReviewerRepo.findLatestPerReviewerByMaxId(manuscriptId);
        
        if (reviewerData != null && !reviewerData.isEmpty()) {
            for (ManuScriptReviewer list : reviewerData) {
                Integer reviewerId = list.getReviewerId();
                
                if (reviewerId == null) {
                    System.out.println("Skipping null reviewerId for manuscriptId: " + manuscriptId);
                    continue;
                }
                
                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                
                List<Integer> inReviewStatus = Arrays.asList(8, 35, 66);
                List<Integer> reviewedStatus = Arrays.asList(12, 13, 14, 15, 36, 60, 30);
                
                long reviewedCount = manuScriptReviewerRepo
                        .getReviewerForIntrAcess(reviewedStatus, manuscriptId, reviewerId)
                        .stream().count();
                long inRevieweCount = manuScriptReviewerRepo
                        .getReviewerForManuscript(inReviewStatus, manuscriptId, reviewerId, manuscriptVersion)
                        .stream().count();
                
                if (reviewedCount > 0 && inRevieweCount == 0) {
                    List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo
                            .getReviewerDataById(reviewerId, list.getJournalId());
                    Long articlesInHandCount = getArticlesInHands(list.getJournalId(), reviewerId);
                    commonvo.setArticlesInHandCount(articlesInHandCount.intValue());
                    
                    Long articlesInReviewCount = manuScriptReviewerRepo
                            .getReviewedManuscriptForReviewer(reviewedStatus, list.getJournalId(), reviewerId)
                            .stream().count();
                    commonvo.setTotalArticlesReviewed(articlesInReviewCount.intValue());
                    
                    List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream()
                            .filter(obj -> obj.getCompleteDate() != null)
                            .collect(Collectors.toList());
                    
                    Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream()
                            .filter(obj -> reviewedStatus.contains(obj.getManuscriptStatus()))
                            .sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed())
                            .findFirst();
                    
                    lastReviewedDate.ifPresent(obj -> commonvo.setLastReviewedDate(obj.getCompleteDate()));
                    
                    // Setting reviewer details
                    commonvo.setManuscriptId(list.getManuscriptId());
                    commonvo.setManuscriptVersion(list.getManuscriptVersion());
                    commonvo.setReviewerId(reviewerId);
                    commonvo.setManuscriptStatus(list.getManuscriptStatus());
                    commonvo.setDecisionLetter(list.getDecisionLetter());
                    commonvo.setCommentsToAuthor(list.getCommentsToAuthor());
                    commonvo.setCommentsToEditor(list.getCommentsToEditor());
                    commonvo.setAssignedDate(list.getAssignedDate());
                    commonvo.setAcceptDate(list.getAcceptDate());
                    commonvo.setRejectDate(list.getRejectDate());
                    commonvo.setIsAssigned(list.getIsAssigned());
                    commonvo.setDueDate(list.getDueDate());
                    commonvo.setJournalId(list.getJournalId());
                    
                    if ("Yes".equalsIgnoreCase(list.getReasses())) {
                        commonvo.setTempFlag("YES");
                    }
                    
                    StatusMaster st = statusMasterRepo.getOne(list.getManuscriptStatus());
                    commonvo.setStatusValue(st.getStatusValue());
                    
                    // Fetching reviewer user details
                    User user = userRepo.findById(reviewerId).orElse(null);
                    if (user != null && user.getFirstName() != null) {
                        commonvo.setName(user.getFirstName() + " " + user.getLastName());
                        commonvo.setEmail(user.getEmail());
                        commonvo.setSpecialization(user.getSpecialization());
                        commonvo.setInstitution(user.getInstitute());
                        commonvo.setSubSpecialization(user.getExpertise());
                        
                        if (user.getCountryId() != null) {
                            Countries country = countriesRepo.findById(user.getCountryId()).orElse(null);
                            if (country != null) {
                                commonvo.setCountry(country.getName());
                            }
                        }
                    }
                    
                    // Fetch manuscript code
                    ManuScript ms = manuScriptRepo.findById(list.getManuscriptId()).orElse(null);
                    if (ms != null) {
                        commonvo.setManuscriptCode(ms.getManuscriptCode());
                    }
                    
                    // Fetch reviewer availability
                    List<ReviewerAvailability> reviewerAvailabilityData =
                            reviewerAvailabilityRepo.getReviewerAvailabilityById(reviewerId);
                    
                    if (reviewerAvailabilityData != null && !reviewerAvailabilityData.isEmpty()) {
                        commonvo.setReviewerAvailability(new ArrayList<>(reviewerAvailabilityData));
                    }
                    
                    // Filter based on search input
                    if (finalSearch.isEmpty() || matchesSearch(commonvo, finalSearch)) {
                        reviewerList.add(commonvo);
                    }
                }
            }
        }

        // Remove duplicates using Set
        Set<ManuScriptReviewerCommonVO> uniqueReviewers = new HashSet<>(reviewerList);
        
        return new ArrayList<>(uniqueReviewers);
    }


    // Helper method to check if the object matches the search criteria
    private boolean matchesSearch(ManuScriptReviewerCommonVO commonvo, String search) {
        return (commonvo.getName() != null && commonvo.getName().toLowerCase().contains(search)) ||
               (commonvo.getEmail() != null && commonvo.getEmail().toLowerCase().contains(search)) ||
               (commonvo.getSpecialization() != null && commonvo.getSpecialization().toLowerCase().contains(search)) ||
               (commonvo.getInstitution() != null && commonvo.getInstitution().toLowerCase().contains(search)) ||
               (commonvo.getSubSpecialization() != null && commonvo.getSubSpecialization().toLowerCase().contains(search)) ||
               (commonvo.getCountry() != null && commonvo.getCountry().toLowerCase().contains(search)) ||
               (commonvo.getArticlesInHandCount() != null && String.valueOf(commonvo.getArticlesInHandCount()).contains(search));
    }


    public Integer intrestedToReAccessCount(Integer manuscriptId) {
        return manuScriptReviewerRepo.intrestedToReAccessCount(manuscriptId);
    }

    @Override
    public List<EditorDashboardVO> getReRivewManuscript(IDVO id) {

        if (StringUtils.equalsIgnoreCase(id.getSortBy(), "assignDate")) {
            id.setSortBy("updatedOn");
        }
        if (StringUtils.equalsIgnoreCase(id.getSortBy(), "manuScriptType")) {
            id.setSortBy("manuscriptTypeDetails.manuscriptType");
        }
        int page = id.getPageNo() != null ? id.getPageNo() : 0;
        int limit = id.getLimit() != null ? id.getLimit() : 10;
        String sortingCol = StringUtils.isEmpty(id.getSortBy()) ? "submissionDate" : id.getSortBy();
        String sortType = StringUtils.isEmpty(id.getSort()) ? "ASC" : id.getSort();

        Pageable paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, sortingCol));
        if (StringUtils.equalsIgnoreCase(sortType, "ASC")) {
            paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, sortingCol));
        }

        List<ManuScriptReviewer> dataList = manuScriptReviewerRepo.getReRivewManuscript(id.getJournalId(), id.getUserId());
        List<EditorDashboardVO> editorDashboardVOList = new ArrayList();

        if (dataList != null && !dataList.isEmpty()) {
            for (ManuScriptReviewer reviewerData : dataList) {
                ManuScript data = reviewerData.getManuscript();
                if (data != null) {
                    EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
                    editorDashboardVO.setManuscriptId(data.getManuScriptId());
                    editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
                    editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
                    editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
                    editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());

                    editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
                    editorDashboardVO.setUpdateDate(data.getUpdatedOn());
                    editorDashboardVO.setStatusId(data.getStatusMaster().getId());
                    editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
                    editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
                    editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
                    editorDashboardVO.setAuthorId(data.getUser().getId());
                    editorDashboardVO
                            .setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
                    if (data.getManuscriptTypeDetails() != null) {
                        editorDashboardVO.setManuscriptType(data.getManuscriptTypeDetails().getManuscriptType());
                    }
                    editorDashboardVO.setAssignedDate(reviewerData.getAssignedDate());
                    editorDashboardVO.setEditorId(reviewerData.getEditorId());
                    if (reviewerData.getEditorId() != null) {
                        User u = userRepo.getOne(reviewerData.getEditorId());
                        if (u != null) {
                            editorDashboardVO.setEditorUsername(
                                    u.getSalutation() + " " + u.getFirstName() + " " + u.getLastName());
                        }
                    }
                    editorDashboardVO.setEditorRoleId(reviewerData.getEditorRoleId());
                    editorDashboardVO.setUnassignedDate(reviewerData.getUnassignedDate());
                    editorDashboardVO.setStartDate(reviewerData.getStartDate());
                    editorDashboardVO.setCompleteDate(reviewerData.getCompleteDate());
                    editorDashboardVO.setRejectDate(reviewerData.getRejectDate());
                    editorDashboardVO.setAcceptDate(reviewerData.getAcceptDate());
                    editorDashboardVO.setDueDate(reviewerData.getDueDate());
                    editorDashboardVO.setStatusValueForUser(reviewerData.getStatusMaster().getStatusValue());
                    editorDashboardVOList.add(editorDashboardVO);

                }

            }
        }

        editorDashboardVOList.sort((a, b) -> {
            if (a.getAssignedDate() == null) return 1;
            if (b.getAssignedDate() == null) return -1;
            return b.getAssignedDate().compareTo(a.getAssignedDate()); 
        });
        
        return editorDashboardVOList;
    }

    @Override
    public List<EditorDashboardVO> getManuscriptForReview(IDVO id) {

        if (StringUtils.equalsIgnoreCase(id.getSortBy(), "assignDate")) {
            id.setSortBy("updatedOn");
        }
        if (StringUtils.equalsIgnoreCase(id.getSortBy(), "manuScriptType")) {
            id.setSortBy("manuscriptTypeDetails.manuscriptType");
        }
        int page = id.getPageNo() != null ? id.getPageNo() : 0;
        int limit = id.getLimit() != null ? id.getLimit() : 10;
        String sortingCol = StringUtils.isEmpty(id.getSortBy()) ? "submissionDate" : id.getSortBy();
        String sortType = StringUtils.isEmpty(id.getSort()) ? "ASC" : id.getSort();

        Pageable paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, sortingCol));
        if (StringUtils.equalsIgnoreCase(sortType, "ASC")) {
            paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, sortingCol));
        }

        List<ManuScriptReviewer> dataList = manuScriptReviewerRepo.getManuscriptForReview(id.getJournalId(), id.getUserId());
        List<EditorDashboardVO> editorDashboardVOList = new ArrayList();

        if (dataList != null && !dataList.isEmpty()) {
            for (ManuScriptReviewer reviewerData : dataList) {
                ManuScript data = reviewerData.getManuscript();
                if (data != null) {
                    EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
                    editorDashboardVO.setManuscriptId(data.getManuScriptId());
                    editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
                    editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
                    editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
                    editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());

                    editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
                    editorDashboardVO.setUpdateDate(data.getUpdatedOn());
                    editorDashboardVO.setStatusId(data.getStatusMaster().getId());
                    editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
                    editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
                    editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
                    editorDashboardVO.setAuthorId(data.getUser().getId());
                    editorDashboardVO
                            .setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
                    if (data.getManuscriptTypeDetails() != null) {
                        editorDashboardVO.setManuscriptType(data.getManuscriptTypeDetails().getManuscriptType());
                    }
                    editorDashboardVO.setAssignedDate(reviewerData.getAssignedDate());
                    editorDashboardVO.setEditorId(reviewerData.getEditorId());
                    if (reviewerData.getEditorId() != null) {
                        User u = userRepo.getOne(reviewerData.getEditorId());
                        if (u != null) {
                            editorDashboardVO.setEditorUsername(
                                    u.getSalutation() + " " + u.getFirstName() + " " + u.getLastName());
                        }
                    }
                    editorDashboardVO.setEditorRoleId(reviewerData.getEditorRoleId());
                    editorDashboardVO.setUnassignedDate(reviewerData.getUnassignedDate());
                    editorDashboardVO.setStartDate(reviewerData.getStartDate());
                    editorDashboardVO.setCompleteDate(reviewerData.getCompleteDate());
                    editorDashboardVO.setRejectDate(reviewerData.getRejectDate());
                    editorDashboardVO.setAcceptDate(reviewerData.getAcceptDate());
                    editorDashboardVO.setDueDate(reviewerData.getDueDate());
                    editorDashboardVO.setStatusValueForUser(reviewerData.getStatusMaster().getStatusValue());
                    editorDashboardVOList.add(editorDashboardVO);
                }

            }
        }

//		editorDashboardVOList = ManuscriptUtils.sortAndLimitEditorDashboardUtil(page, limit, sortingCol, sortType, editorDashboardVOList);

        editorDashboardVOList.sort((a, b) -> {
            if (a.getAssignedDate() == null) return 1;
            if (b.getAssignedDate() == null) return -1;
            return b.getAssignedDate().compareTo(a.getAssignedDate()); 
        });

        return editorDashboardVOList;
    }

    @Override
    public List<ManuscriptListVO> getReviewedManuscriptByManuscriptId(List<ManuScript> manuScriptListDataFetch) {

        List<ManuscriptListVO> manuscriptList = new ArrayList();
        Map<String, ManuscriptListVO> map = new HashMap<>();
        List<Integer> manuscriptId = new ArrayList();
        if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
            for (ManuScript manuScriptData : manuScriptListDataFetch) {
                manuscriptId.add(manuScriptData.getManuScriptId());
            }
        }

        if (manuscriptId != null && !manuscriptId.isEmpty()) {

            List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptByManuscriptList(manuscriptId);
            for (ManuScript uniqueData : manuScriptListDataFetch) {
                int mcount = 0;
                int mvalidcount = 0;
                int delayCount = 0;
                try {
                    for (ManuScriptReviewer allData : getAllDataByjournal) {
                        if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
                                && allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
                            mcount++;
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
                                Date todayDate = df.parse(df.format(new Date()));

                                if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 12
                                        || ((allData.getManuscriptStatus() == 8
                                        || allData.getManuscriptStatus() == 35
                                        || allData.getManuscriptStatus() == 66)
                                        && reviewerDueDate.before(todayDate))
                                        || allData.getManuscriptStatus() == 13
                                        || allData.getManuscriptStatus() == 14
                                        || allData.getManuscriptStatus() == 15
                                        || allData.getManuscriptStatus() == 36
                                        || allData.getManuscriptStatus() == 60) {
                                    mvalidcount++;
                                }

                                if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 36
                                        || allData.getManuscriptStatus() == 60
                                        || ((allData.getManuscriptStatus() == 8
                                        || allData.getManuscriptStatus() == 35
                                        || allData.getManuscriptStatus() == 66)
                                        && reviewerDueDate.before(todayDate))) {
                                    delayCount++;
                                }
                            } catch (ParseException e) {
                                log.debug(e.getMessage());
                            }
                        }
                    }
                    if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount && delayCount != mcount) {

                        ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                        manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                        manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                        if (uniqueData != null) {
                            if (uniqueData.getManuscriptTypeDetails() != null
                                    && uniqueData.getManuscriptTypeDetails().getManuscriptType() != null) {
                                manuscriptListVO.setManuScriptType(
                                        uniqueData.getManuscriptTypeDetails().getManuscriptType());
                            }
                            manuscriptListVO.setManuscripCode(uniqueData.getManuscriptCode());
                            User user = userRepo.getOne(uniqueData.getAssignedUser());
                            if (user != null && user.getFirstName() != null) {
                                manuscriptListVO.setEditorName(user.getFirstName() + " " + user.getLastName());
                            }
                            manuscriptListVO.setManuscripTitle(uniqueData.getManuscriptTitle());
                            manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());
                            manuscriptListVO.setEditorStatus(uniqueData.getStatusMaster().getStatusValue());
                            manuscriptListVO.setStatusValueForAuthor(
                                    uniqueData.getStatusMaster().getStatusValueForAuthor());
                            manuscriptListVO.setStatusValueForEditor(
                                    uniqueData.getStatusMaster().getStatusValueForEditor());
                            manuscriptListVO.setSomeUserDeclined(0);
                        }
                        manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                        map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);
                    }
                } catch (Exception e) {
                    log.debug(e.getMessage());
                }

            }

            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
                    manuscriptList.add(entry.getValue());
                }
            }
        }
        return manuscriptList;

    }

    @Override
    public List<ManuscriptListVO> getNoResponseManuscriptByManuscriptId(List<ManuScript> manuScriptListDataFetch) {

        List<ManuscriptListVO> manuscriptList = new ArrayList();
        Map<String, ManuscriptListVO> map = new HashMap<>();
        List<Integer> manuscriptId = new ArrayList();
        if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
            for (ManuScript manuScriptData : manuScriptListDataFetch) {
                manuscriptId.add(manuScriptData.getManuScriptId());
            }
        }
        if (manuscriptId != null && !manuscriptId.isEmpty()) {
            List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptByManuscriptListNoResponse(manuscriptId);
            if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
                for (ManuScript uniqueData : manuScriptListDataFetch) {

                    int mcount = 0;
                    int npResponseValidCount = 0;
                    try {
                        for (ManuScriptReviewer allData : getAllDataByjournal) {
                            if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
                                    && allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
                                mcount++;
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
                                    Date todayDate = df.parse(df.format(new Date()));

                                    if (allData.getIsActive() == 0
                                            || ((allData.getManuscriptStatus() == 8
                                            || allData.getManuscriptStatus() == 66
                                            || allData.getManuscriptStatus() == 35)
                                            && reviewerDueDate.before(todayDate))
                                            || allData.getManuscriptStatus() == 60
                                            || allData.getManuscriptStatus() == 36) {
                                        npResponseValidCount++;
                                    }

                                } catch (ParseException e) {
                                    log.debug(e.getMessage());
                                }
                            }
                        }
                        if (mcount != 0 && npResponseValidCount != 0 && mcount == npResponseValidCount) {

                            ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                            manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                            manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                            if (uniqueData != null) {
                                if (uniqueData.getManuscriptTypeDetails() != null && uniqueData
                                        .getManuscriptTypeDetails().getManuscriptType() != null) {
                                    manuscriptListVO.setManuScriptType(
                                            uniqueData.getManuscriptTypeDetails().getManuscriptType());
                                }
                                manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                User user = userRepo.getOne(uniqueData.getAssignedUser());
                                if (user != null && user.getFirstName() != null) {
                                    manuscriptListVO.setEditorName(user.getFirstName() + " " + user.getLastName());
                                }
                                manuscriptListVO.setManuscripCode(uniqueData.getManuscriptCode());
                                manuscriptListVO.setManuscripTitle(uniqueData.getManuscriptTitle());
                                manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());
                                manuscriptListVO
                                        .setEditorStatus(uniqueData.getStatusMaster().getStatusValue());
                                manuscriptListVO.setStatusValueForAuthor(
                                        uniqueData.getStatusMaster().getStatusValueForAuthor());
                                manuscriptListVO.setStatusValueForEditor(
                                        uniqueData.getStatusMaster().getStatusValueForEditor());
                            }
                            manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                            map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);
                        }
                    } catch (Exception e) {
                        log.debug(e.getMessage());
                    }


                }
                if (map != null && !map.isEmpty()) {
                    for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
                        manuscriptList.add(entry.getValue());
                    }
                }
            }
        }
        return manuscriptList;
    }

    @Override
    public List<ManuscriptListVO> getInReviewManuscriptByManuscriptId(List<ManuScript> manuScriptListDataFetch) {

        List<ManuscriptListVO> manuscriptList = new ArrayList();

        List<Integer> manuscriptId = new ArrayList();
        if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
            for (ManuScript manuScriptData : manuScriptListDataFetch) {
                manuscriptId.add(manuScriptData.getManuScriptId());
            }
        }
        if (manuscriptId != null && !manuscriptId.isEmpty()) {
            List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                    .getInReviewManuscript(manuscriptId);

            if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
                for (ManuScript uniqueData : manuScriptListDataFetch) {
                    for (ManuScriptReviewer manuScriptReviewer : getAllDataByjournal) {

                        if (uniqueData.getManuScriptId().equals(manuScriptReviewer.getManuscriptId()) && uniqueData.getManuscriptVersion().equals(manuScriptReviewer.getManuscriptVersion())) {
                            ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                            manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                            manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                            if (uniqueData != null) {
                                if (uniqueData.getManuscriptTypeDetails() != null && uniqueData
                                        .getManuscriptTypeDetails().getManuscriptType() != null) {
                                    manuscriptListVO.setManuScriptType(
                                            uniqueData.getManuscriptTypeDetails().getManuscriptType());
                                }
                                manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
                                User user = userRepo.getOne(uniqueData.getAssignedUser());
                                if (user != null && user.getFirstName() != null) {
                                    manuscriptListVO.setEditorName(user.getFirstName() + " " + user.getLastName());
                                }
                                manuscriptListVO.setManuscripCode(uniqueData.getManuscriptCode());
                                manuscriptListVO.setManuscripTitle(uniqueData.getManuscriptTitle());
                                manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());
                                manuscriptListVO
                                        .setEditorStatus(uniqueData.getStatusMaster().getStatusValue());
                                manuscriptListVO.setStatusValueForAuthor(
                                        uniqueData.getStatusMaster().getStatusValueForAuthor());
                                manuscriptListVO.setStatusValueForEditor(
                                        uniqueData.getStatusMaster().getStatusValueForEditor());
                            }
                            manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                            manuscriptList.add(manuscriptListVO);
                        }


                    }

                }
            }
        }

        return manuscriptList;

    }

//    @Override
//    public String updateManuscriptReviewerResponse(ReviewerResponseVo reviewerResponseVo, HttpServletRequest request) {
//        BufferedOutputStream stream = null;
//        try {
//            log.info("--------Inside in Update Manuscript Reviewer Response--------");
//
//            if (reviewerResponseVo == null) {
//                log.error("Review response object is null.");
//                return "Review response object is null.";
//            }
//
//            ManuScriptReviewer manuScriptReviewer = manuScriptReviewerRepo.getManuScriptReviewerEditor(
//                    reviewerResponseVo.getManuscriptId(), reviewerResponseVo.getManuscriptVersion(),
//                    reviewerResponseVo.getReviewerUserId(), reviewerResponseVo.getJournalId());
//
//            if (manuScriptReviewer == null) {
//                log.error("Manuscript reviewer not found.");
//                return "Manuscript reviewer not found.";
//            }
//
//            if (manuScriptReviewer.getManuscriptStatus() != 35) {
//                log.error("Reviewer already submit him/her comment");
//                return "Reviewer already submit him/her comment.";
//            }
//
//            log.info("--------STEP--1--To--Update--Manuscript--Response--------");
//
//            manuScriptReviewer.setCommentsToEditor(reviewerResponseVo.getCommentsToEditor());
//            manuScriptReviewer.setCommentsToAuthor(reviewerResponseVo.getCommentsToAuthor());
//            manuScriptReviewer.setCompleteDate(new Date());
//            manuScriptReviewer.setManuscriptStatus(reviewerResponseVo.getManuscriptStatusId());
//            manuScriptReviewer.setManuscriptPriority(reviewerResponseVo.getManuscriptPriorty());
//            manuScriptReviewer.setCompetingInterests(reviewerResponseVo.getCompetingInterests());
//            manuScriptReviewer.setMessageFromManuscript(reviewerResponseVo.getMessageFromManuscript());
//            manuScriptReviewer.setReviewerResponse(reviewerResponseVo.getReviewerResponse());
//            manuScriptReviewer.setRequiresCommentary(reviewerResponseVo.getRequireCommentary());
//            manuScriptReviewer.setWriteommentary(reviewerResponseVo.getWriteCommentary());
//            manuScriptReviewer.setReasses(reviewerResponseVo.getReasses());
//
//            if (reviewerResponseVo.getFile() != null && !reviewerResponseVo.getFile().isEmpty()) {
//                log.info("--------Step--2---Inside in Manuscript Reviewer File-------");
//                String location = UPLOAD_FOLDER; // Assuming UPLOAD_FOLDER is defined somewhere in your code
//                log.info("-------UPLOAD--FOLDER--Directory--------" + location);
//
//                String attachmentFolderPath = location + File.separator + "UniprrReviewerResponseFolder"
//                        + File.separator + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion()
//                        + "_" + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId();
//                log.info("--------UPLOAD--Folder--Location--------" + attachmentFolderPath);
//
//                File dir = new File(attachmentFolderPath);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//
//                byte[] bytes = reviewerResponseVo.getFile().getBytes();
//                File serverFile = new File(dir.getAbsolutePath() + File.separator
//                        + reviewerResponseVo.getFile().getOriginalFilename());
//
//                stream = new BufferedOutputStream(new FileOutputStream(serverFile));
//                stream.write(bytes);
//                
//                double fileSizeinBytes = reviewerResponseVo.getFile().getSize();
//                double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
//
//                Integer port = request.getServerPort();
//                InetAddress inetAddress = InetAddress.getLocalHost();
//                String fileServerPath = null;
//
//                if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
//                    String Ip = "https://" + environment.getProperty("awsFileIp");
//                    fileServerPath = Ip + File.separator + "UniprrReviewerResponseFolder" + File.separator
//                            + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion() + "_"
//                            + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId()
//                            + File.separator + reviewerResponseVo.getFile().getOriginalFilename();
//                    log.info("--------Download Server Path--------" + fileServerPath);
//                } else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
//                    String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
//                    fileServerPath = Ip + File.separator + "UniprrReviewerResponseFolder" + File.separator
//                            + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion() + "_"
//                            + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId()
//                            + File.separator + reviewerResponseVo.getFile().getOriginalFilename();
//                    log.info("--------Download Server Path--------" + fileServerPath);
//                } else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
//                    String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
//                            + environment.getProperty("server.status.port");
//                    fileServerPath = Ip + File.separator + "UniprrReviewerResponseFolder" + File.separator
//                            + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion() + "_"
//                            + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId()
//                            + File.separator + reviewerResponseVo.getFile().getOriginalFilename();
//                    log.info("--------Download Server Path--------" + fileServerPath);
//                }
//
//                manuScriptReviewer.setReviewerFileName(reviewerResponseVo.getFile().getOriginalFilename());
//                log.info("--------Reviewer File Name -------" + reviewerResponseVo.getFile().getOriginalFilename());
//                manuScriptReviewer.setReviewerFilePath(fileServerPath);
//                log.info("--------Reviewer Download File Path--------" + fileServerPath);
//                manuScriptReviewer.setReviewerFileSize((int) fileSizeInKB);
//                log.info("--------Reviewer File Size--------" + (int) fileSizeInKB);
//            }
//
//            if (StringUtils.isNotBlank(reviewerResponseVo.getPublonCredit())
//                    && !("null".equals(reviewerResponseVo.getPublonCredit()))) {
//                manuScriptReviewer.setPublonCredit(reviewerResponseVo.getPublonCredit());
//            } else {
//                manuScriptReviewer.setPublonCredit("N");
//            }
//
//            if ("Y".equals(reviewerResponseVo.getPublonCredit())) {
//                manuScriptReviewer.setSentoPublons("N");
//            }
//
//            manuScriptReviewerRepo.save(manuScriptReviewer);
//            log.info("--------Successfully Updated--------");
//            return "Successfully updated";
//        } catch (Exception e) {
//            log.error("Error while updating manuscript reviewer response.", e);
//            return "Error while updating manuscript reviewer response.";
//        } finally {
//            try {
//                if (stream != null)
//                    stream.close();
//                log.info("--------Reviewer File Close Successfully--------");
//            } catch (IOException e) {
//                log.error("Error while closing reviewer file.", e);
//            }
//        }
//    }

    @Override
    public String updateManuscriptReviewerResponse(ReviewerResponseVo reviewerResponseVo, HttpServletRequest request) {
        BufferedOutputStream stream = null;
        try {
            log.info("--------Inside in Update Manuscript Reviewer Response--------");

            if (reviewerResponseVo == null) {
                log.error("Review response object is null.");
                return "Review response object is null.";
            }

            ManuScriptReviewer manuScriptReviewer = manuScriptReviewerRepo.getManuScriptReviewerEditor(
                    reviewerResponseVo.getManuscriptId(), reviewerResponseVo.getManuscriptVersion(),
                    reviewerResponseVo.getReviewerUserId(), reviewerResponseVo.getJournalId());

            if (manuScriptReviewer == null) {
                log.error("Manuscript reviewer not found.");
                return "Manuscript reviewer not found.";
            }

            if (manuScriptReviewer.getManuscriptStatus() != 35) {
                log.error("Reviewer already submit him/her comment");
                return "Reviewer already submit him/her comment.";
            }

            log.info("--------STEP--1--To--Update--Manuscript--Response--------");

            manuScriptReviewer.setCommentsToEditor(reviewerResponseVo.getCommentsToEditor());
            manuScriptReviewer.setCommentsToAuthor(reviewerResponseVo.getCommentsToAuthor());
            manuScriptReviewer.setCompleteDate(new Date());
            manuScriptReviewer.setManuscriptStatus(reviewerResponseVo.getManuscriptStatusId());
            manuScriptReviewer.setManuscriptPriority(setNullIfNotApplicable(reviewerResponseVo.getManuscriptPriorty()));
            manuScriptReviewer.setCompetingInterests(reviewerResponseVo.getCompetingInterests());
            manuScriptReviewer.setMessageFromManuscript(reviewerResponseVo.getMessageFromManuscript());
            manuScriptReviewer.setReviewerResponse(reviewerResponseVo.getReviewerResponse());
            manuScriptReviewer.setRequiresCommentary(reviewerResponseVo.getRequireCommentary());
            manuScriptReviewer.setWriteommentary(reviewerResponseVo.getWriteCommentary());
            manuScriptReviewer.setReasses(reviewerResponseVo.getReasses());

            if (reviewerResponseVo.getFile() != null && !reviewerResponseVo.getFile().isEmpty()) {
                log.info("--------Step--2---Inside in Manuscript Reviewer File-------");

                String location = UPLOAD_FOLDER;
                log.info("-------UPLOAD--FOLDER--Directory--------" + location);

                String attachmentFolderPath = location + File.separator + "UniprrReviewerResponseFolder"
                        + File.separator + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion()
                        + "_" + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId();
                log.info("--------UPLOAD--Folder--Location--------" + attachmentFolderPath);

                File dir = new File(attachmentFolderPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // -------- HANDLE DUPLICATE FILE NAME --------
                String originalFilename = reviewerResponseVo.getFile().getOriginalFilename();
                String fileBaseName = FilenameUtils.getBaseName(originalFilename);
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                String finalFileName = originalFilename;
                File serverFile = new File(dir, finalFileName);

                int counter = 1;
                while (serverFile.exists()) {
                    finalFileName = fileBaseName + "(" + counter + ")." + fileExtension;
                    serverFile = new File(dir, finalFileName);
                    counter++;
                }

                // Save the file
                byte[] bytes = reviewerResponseVo.getFile().getBytes();
                stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);

                double fileSizeinBytes = reviewerResponseVo.getFile().getSize();
                double fileSizeInKB = Math.round((fileSizeinBytes / 1024));

                Integer port = request.getServerPort();
                InetAddress inetAddress = InetAddress.getLocalHost();
                String fileServerPath = null;

                if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
                    String Ip = "https://" + environment.getProperty("awsFileIp");
                    fileServerPath = Ip + File.separator + "UniprrReviewerResponseFolder" + File.separator
                            + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion() + "_"
                            + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId()
                            + File.separator + finalFileName;
                    log.info("--------Download Server Path--------" + fileServerPath);
                } else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
                    String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
                    fileServerPath = Ip + File.separator + "UniprrReviewerResponseFolder" + File.separator
                            + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion() + "_"
                            + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId()
                            + File.separator + finalFileName;
                    log.info("--------Download Server Path--------" + fileServerPath);
                } else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
                    String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
                            + environment.getProperty("server.status.port");
                    fileServerPath = Ip + File.separator + "UniprrReviewerResponseFolder" + File.separator
                            + "MNT_LWW_JOURNAL_" + "R" + reviewerResponseVo.getManuscriptVersion() + "_"
                            + reviewerResponseVo.getJournalId() + "_" + reviewerResponseVo.getManuscriptId()
                            + File.separator + finalFileName;
                    log.info("--------Download Server Path--------" + fileServerPath);
                }

                manuScriptReviewer.setReviewerFileName(finalFileName);
                log.info("--------Reviewer File Name -------" + finalFileName);
                manuScriptReviewer.setReviewerFilePath(fileServerPath);
                log.info("--------Reviewer Download File Path--------" + fileServerPath);
                manuScriptReviewer.setReviewerFileSize((int) fileSizeInKB);
                log.info("--------Reviewer File Size--------" + (int) fileSizeInKB);
            }

            if (StringUtils.isNotBlank(reviewerResponseVo.getPublonCredit())
                    && !("null".equals(reviewerResponseVo.getPublonCredit()))) {
                manuScriptReviewer.setPublonCredit(reviewerResponseVo.getPublonCredit());
            } else {
                manuScriptReviewer.setPublonCredit("N");
            }

            if ("Y".equals(reviewerResponseVo.getPublonCredit())) {
                manuScriptReviewer.setSentoPublons("N");
            }

            manuScriptReviewerRepo.save(manuScriptReviewer);
            log.info("--------Successfully Updated--------");
            return "Successfully updated";
        } catch (Exception e) {
            log.error("Error while updating manuscript reviewer response.", e);
            return "Error while updating manuscript reviewer response.";
        } finally {
            try {
                if (stream != null)
                    stream.close();
                log.info("--------Reviewer File Close Successfully--------");
            } catch (IOException e) {
                log.error("Error while closing reviewer file.", e);
            }
        }
    }

    private String setNullIfNotApplicable(String val){
        if(val != null && val.equalsIgnoreCase("NOT APPLICABLE")){
            return null;
        }else{
            return val;
        }
    }

    
    @Override
    public Date getDueDate(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        Date c = new Date();
        Integer journalId = manuScriptReviewerCommonVO.getJournalId();
        int manuVersion = manuScriptReviewerCommonVO.getManuscriptVersion();
        if (journalId != null) {
            Journal getjournalDetails = journalsRepo.getOne(journalId);
            if (getjournalDetails != null) {
                if (getjournalDetails.getJournalMetadata() != null) {
                    if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
                        List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo
                                .getJournalWorkflow(getjournalDetails.getJournalMetadata().getWorkflowId(), journalId);
                        if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
                            for (WorkflowRoleSeq i : listOfWorkflowRoleSeq) {
                                if (manuVersion < 1) {
                                    if (i.getTaskId().equals(4))
                                        c = (i.getTat().equals(0) ? new Date()
                                                : new Date().from(LocalDate.now().plusDays(i.getTat())
                                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

                                } else {

                                    if (i.getTaskId().equals(26))
                                        c = i.getTat().equals(0) ? new Date()
                                                : new Date().from(LocalDate.now().plusDays(i.getTat())
                                                .atStartOfDay(ZoneId.systemDefault()).toInstant());

                                }
                            }
                        }
                    }
                }
            }
        }
        return c;
    }

    @Override
    public List<ManuscriptReviewerCommentsVO> getReviewerDataByManuscriptId(Integer manuscriptId, Integer reviewerId) {
        List<ManuscriptReviewerCommentsVO> add = new ArrayList<>();
        List<ManuScriptReviewer> manuScriptReviewerList = manuScriptReviewerRepo
                .getDataReviewerAndMnuscriptId(manuscriptId, reviewerId);
        if (manuScriptReviewerList != null && !manuScriptReviewerList.isEmpty()) {
            for (ManuScriptReviewer mrc : manuScriptReviewerList) {
                User user = userLoginRepo.getOne(mrc.getReviewerId());
                List<StatusMaster> mstatus = statusMasterRepo.getStatusMasterDetailsById(mrc.getManuscriptStatus());
                ManuscriptReviewerCommentsVO mrcv = new ManuscriptReviewerCommentsVO();
                mrcv.setManuscriptStatus(mstatus.get(0).getStatusValue());
                mrcv.setIsActive(mrc.getIsActive());
                mrcv.setReviewerId(mrc.getReviewerId());
                mrcv.setReviewerFilePath(mrc.getReviewerFilePath());
                mrcv.setReviewerFileName(mrc.getReviewerFileName());
                mrcv.setReviewerFileSize(mrc.getReviewerFileSize());
                mrcv.setManuscriptId(mrc.getManuscriptId());
                mrcv.setManuscriptVersion(mrc.getManuscriptVersion());
                if (mrc.getRequiresCommentary() != null && !mrc.getRequiresCommentary().isEmpty())
                    mrcv.setRequiresCommentary(mrc.getRequiresCommentary());
                if (mrc.getWriteommentary() != null && !mrc.getWriteommentary().isEmpty())
                    mrcv.setWritecommentary(mrc.getWriteommentary());
                if (mrc.getIsInvitedForCommentary() != null)
                    mrcv.setIsInvitedForCommentary(mrc.getIsInvitedForCommentary());
                if (mrc.getReasses() != null && !mrc.getReasses().isEmpty())
                    mrcv.setReasses(mrc.getReasses());
                if (mrc.getManuscriptPriority() != null && !mrc.getManuscriptPriority().isEmpty())
                    mrcv.setManuscriptPriority(mrc.getManuscriptPriority());
                if (mrc.getCompetingInterests() != null && !mrc.getCompetingInterests().isEmpty())
                    mrcv.setCompetingInterests(mrc.getCompetingInterests());
                if (mrc.getMessageFromManuscript() != null && !mrc.getMessageFromManuscript().isEmpty())
                    mrcv.setMessageFromManuscript(mrc.getMessageFromManuscript());
                if (mrc.getReviewerResponse() != null && !mrc.getReviewerResponse().isEmpty())
                    mrcv.setReviewerResponse(mrc.getReviewerResponse());
                if (mrc.getDecisionLetter() != null && !mrc.getDecisionLetter().equalsIgnoreCase(""))
                    mrcv.setDecisionLetter(mrc.getDecisionLetter());
                if (mrc.getCommentsToAuthor() != null && !mrc.getCommentsToAuthor().equalsIgnoreCase(""))
                    mrcv.setCommentsToAuthor(mrc.getCommentsToAuthor());
                if (mrc.getCommentsToEditor() != null && !mrc.getCommentsToEditor().equalsIgnoreCase(""))
                    mrcv.setCommentsToEditor(mrc.getCommentsToEditor());
                if (user != null && user.getFirstName() != null) {
                    mrcv.setUserName(user.getSalutation() + "." + user.getFirstName() + " " + user.getLastName());
                }

                add.add(mrcv);
            }
        }

        return add;
    }

    @Override
    public List<ManuscriptListVO> getAllReviewedManuscript(IDVO id) {
        List<ManuscriptListVO> reviewedManuscript = new ArrayList();
        List<ManuScript> manuScriptListDataFetch = manuScriptRepo.getAllReviewedManuscript(id.getJournalId());
        if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
            if (id.getId() == 1) {
                reviewedManuscript = getReviewedManuscriptByManuscriptId(manuScriptListDataFetch);
            } else if (id.getId() == 2) {
                reviewedManuscript = getNoResponseManuscriptByManuscriptId(manuScriptListDataFetch);
            } else if (id.getId() == 3) {
                reviewedManuscript = getInReviewManuscriptByManuscriptId(manuScriptListDataFetch);
            }

        }
        return reviewedManuscript;
    }

    @Override
    public List<EditorDashboardVO> getReviewedManuscriptForReviewer(UserJournalManuscriptVo userJournalManuscriptVo) {
        List<EditorDashboardVO> newList = new ArrayList<>();

        if (StringUtils.equalsIgnoreCase(userJournalManuscriptVo.getSortBy(), "assignDate")) {
            userJournalManuscriptVo.setSortBy("updatedOn");
        }
        if (StringUtils.equalsIgnoreCase(userJournalManuscriptVo.getSortBy(), "manuScriptType")) {
            userJournalManuscriptVo.setSortBy("manuscriptTypeDetails.manuscriptType");
        }
        int page = userJournalManuscriptVo.getPageNo() != null ? userJournalManuscriptVo.getPageNo() : 0;
        int limit = userJournalManuscriptVo.getLimit() != null ? userJournalManuscriptVo.getLimit() : 10;
        String sortingCol = StringUtils.isEmpty(userJournalManuscriptVo.getSortBy()) ? "submissionDate"
                : userJournalManuscriptVo.getSortBy();
        String sortType = StringUtils.isEmpty(userJournalManuscriptVo.getSort()) ? "ASC" : "DESC";

        Pageable paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, sortingCol));
        if (StringUtils.equalsIgnoreCase(sortType, "ASC")) {
            paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, sortingCol));
        }

        Journal journalCode = journalsRepo.getOne(userJournalManuscriptVo.getJournalId());
        List<ManuScriptReviewer> dataList = manuScriptReviewerRepo.getReviewedManuscriptForReviewer(
                userJournalManuscriptVo.getManuscriptStatusId(), userJournalManuscriptVo.getJournalId(),
                userJournalManuscriptVo.getUserId());
        if (dataList != null) {
            for (ManuScriptReviewer reviewerData : dataList) {
                ManuScript data = reviewerData.getManuscript();
                if (data != null) {
                    EditorDashboardVO editorDashboardVO = new EditorDashboardVO();
                    editorDashboardVO.setManuscriptId(data.getManuScriptId());
                    editorDashboardVO.setManuscriptCode(data.getManuscriptCode());
                    editorDashboardVO.setManuscriptTitle(data.getManuscriptTitle());
                    editorDashboardVO.setManuscriptAbstract(data.getManuscriptAbstract());
                    editorDashboardVO.setWithdrawalComment(data.getWithdrawalComment());

                    editorDashboardVO.setWithdrawnDate(data.getWithdrawnDate());
                    editorDashboardVO.setUpdateDate(data.getUpdatedOn());
                    editorDashboardVO.setJournalCode(journalCode.getJournalCode());
                    editorDashboardVO.setJournalTitle(journalCode.getJournalTitle());
                    editorDashboardVO.setJournalId(journalCode.getJournalId());
                    editorDashboardVO.setStatusId(data.getStatusMaster().getId());
                    editorDashboardVO.setStatusValue(data.getStatusMaster().getStatusValue());
                    editorDashboardVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
                    editorDashboardVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
                    editorDashboardVO.setManuscriptVersion(data.getManuscriptVersion());
                    editorDashboardVO.setAuthorId(data.getUser().getId());
                    editorDashboardVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
                    if (data.getManuscriptTypeDetails() != null) {
                        editorDashboardVO.setManuscriptType(data.getManuscriptTypeDetails().getManuscriptType());
                    }
                    editorDashboardVO.setSubmittedDate(data.getSubmissionDate());
                    editorDashboardVO.setAssignedDate(reviewerData.getAssignedDate());
                    editorDashboardVO.setEditorId(reviewerData.getEditorId());
                    editorDashboardVO.setEditorRoleId(reviewerData.getEditorRoleId());
                    editorDashboardVO.setUnassignedDate(reviewerData.getUnassignedDate());
                    editorDashboardVO.setStartDate(reviewerData.getStartDate());
                    editorDashboardVO.setCompleteDate(reviewerData.getCompleteDate());
                    editorDashboardVO.setRejectDate(reviewerData.getRejectDate());
                    editorDashboardVO.setAcceptDate(reviewerData.getAcceptDate());
                    editorDashboardVO.setDueDate(reviewerData.getDueDate());
                    editorDashboardVO.setStatusValueForUser(reviewerData.getStatusMaster().getStatusValue());
                    newList.add(editorDashboardVO);

                }
            }
        }
        
        newList.sort((a, b) -> {
            if (a.getAssignedDate() == null) return 1;
            if (b.getAssignedDate() == null) return -1;
            return b.getAssignedDate().compareTo(a.getAssignedDate()); 
        });

        return newList;
    }

    @Override
    public List<ReviewerAvailabilityStatusVO> getReviewerStaticsData(IDVO id) {
        List<ReviewerAvailabilityStatusVO> add = new ArrayList<>();
        List<ManuScriptReviewer> reviewerData = new ArrayList<>();
        if (id.getManuscriptStatusId().size() == 3) {
            reviewerData = getArticleInHandsData(id.getJournalId(), id.getUserId());
        } else if (id.getManuscriptStatusId().get(0).equals(8)) {

            reviewerData = manuScriptReviewerRepo.getRevieweManuscriptForStaticActive(
                    new ArrayList<>(Arrays.asList(8, 66)), id.getJournalId(), id.getUserId());
        } else if (id.getManuscriptStatusId().get(0).equals(35)) {
            reviewerData = manuScriptReviewerRepo.getRevieweManuscriptForStaticActive(id.getManuscriptStatusId(),
                    id.getJournalId(), id.getUserId());
        } else {
            reviewerData = manuScriptReviewerRepo.getReviewedManuscriptForReviewer(id.getManuscriptStatusId(),
                    id.getJournalId(), id.getUserId());
        }
        if (reviewerData != null && !reviewerData.isEmpty()) {
            reviewerData.stream().forEach(i -> {
                ReviewerAvailabilityStatusVO addData = new ReviewerAvailabilityStatusVO();
                addData.setAcceptDate(i.getAcceptDate());
                addData.setDueDate(i.getDueDate());
                addData.setUnassignedDate(i.getUnassignedDate());
                addData.setStartDate(i.getStartDate());
                addData.setCompleteDate(i.getCompleteDate());
                addData.setRejectDate(i.getRejectDate());
                addData.setManuscriptCode(i.getManuscript().getManuscriptCode());
                addData.setManuscriptTitle(i.getManuscript().getManuscriptTitle());
                addData.setManuscriptStatus(i.getManuscript().getStatusMaster().getStatusValueForEditor());
                addData.setCompetingInterests(i.getCompetingInterests());
                addData.setMessageFromManuscript(i.getMessageFromManuscript());
                addData.setReviewerResponse(i.getReviewerResponse());
                addData.setRejectionReason(i.getRejectionReason());
                addData.setColour(setLegendColors(addData, i));
                addData.setCommentsForReviewer(i.getCommentsForReviewer());
                addData.setCommentsToAuthor(i.getCommentsToAuthor());
                addData.setCommentsToEditor(i.getCommentsToEditor());
                ReviewerRating getData = reviewerRatingRepo.getReviewerRatingOnManuscript(id.getUserId(),
                        i.getManuscriptId(), i.getManuscriptVersion());
                if (getData != null) {
                    addData.setQuality(getData.getQuality());
                    addData.setTimeliness(getData.getTimeliness());
                }
                add.add(addData);
            });
        }
        return add;
    }

    private String setLegendColors(ReviewerAvailabilityStatusVO addData, ManuScriptReviewer reviewer) {
        String colour = "";

        if (reviewer.getCompleteDate() != null && reviewer.getCompleteDate().before(reviewer.getDueDate()))
            colour = "green";
        else if (reviewer.getCompleteDate() != null && reviewer.getCompleteDate().after(reviewer.getDueDate()))
            colour = "orange";
        else if (reviewer.getCompleteDate() == null && reviewer.getDueDate().after(new Date()))
            colour = "green";
        else if (reviewer.getCompleteDate() == null && reviewer.getDueDate().before(new Date()))
            colour = "orange";
        else
            colour = "blue";

        return colour;
    }

    @Override
    public List<ManuScriptReviewerCommonVO> getReviewerDatabaseSuggested(
            ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();
        Pageable paging = PageRequest.of(manuScriptReviewerCommonVO.getPageNo(), manuScriptReviewerCommonVO.getLimit());
        List<UserJournal> getUnassignedReviewer = new ArrayList<UserJournal>();


        if (StringUtils.isBlank(manuScriptReviewerCommonVO.getSpecialization()))
            manuScriptReviewerCommonVO.setSpecialization(null);
        if (StringUtils.isBlank(manuScriptReviewerCommonVO.getExperties()))
            manuScriptReviewerCommonVO.setExperties(null);
        if (StringUtils.isBlank(manuScriptReviewerCommonVO.getName()))
            manuScriptReviewerCommonVO.setName(null);
        if (StringUtils.isBlank(manuScriptReviewerCommonVO.getEmail()))
            manuScriptReviewerCommonVO.setEmail(null);
        if (StringUtils.isBlank(manuScriptReviewerCommonVO.getAlphabet()))
            manuScriptReviewerCommonVO.setAlphabet(null);

        if (manuScriptReviewerCommonVO.getRoleId() == null) {
            manuScriptReviewerCommonVO.setRoleId(5);
        }

        if (StringUtils.isBlank(manuScriptReviewerCommonVO.getAlphabet())) {
            if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() == null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {

                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewer(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getRoleId(), paging);

            } else if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSpecialization(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(),
                        manuScriptReviewerCommonVO.getRoleId(), paging);

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSpecializationExperties(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(),
                        manuScriptReviewerCommonVO.getExperties(), manuScriptReviewerCommonVO.getRoleId(), paging);

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSearchSpeAndSubSpe(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(),
                        manuScriptReviewerCommonVO.getExperties(), paging);

            } else {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSearchAny(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(),
                        manuScriptReviewerCommonVO.getExperties(), manuScriptReviewerCommonVO.getName(),
                        manuScriptReviewerCommonVO.getEmail(), manuScriptReviewerCommonVO.getRoleId(), paging);
            }

        } else {

            if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() == null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {

                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerAlphabet(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getAlphabet(),
                        manuScriptReviewerCommonVO.getRoleId(), paging);

            } else if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSpecializationAlphabet(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(),
                        manuScriptReviewerCommonVO.getAlphabet(), manuScriptReviewerCommonVO.getRoleId(), paging);

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSpecializationExpertiesAlphabet(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(),
                        manuScriptReviewerCommonVO.getExperties(), manuScriptReviewerCommonVO.getAlphabet(), manuScriptReviewerCommonVO.getRoleId(),
                        paging);

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSpecializationExpertiesAl(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getExperties(),
                        manuScriptReviewerCommonVO.getAlphabet(), paging);
            } else {
                getUnassignedReviewer = userJournalRepo.getDatabaseSuggestedReviewerSearchAnyAlphabet(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(),
                        manuScriptReviewerCommonVO.getExperties(), manuScriptReviewerCommonVO.getName(),
                        manuScriptReviewerCommonVO.getEmail(), manuScriptReviewerCommonVO.getAlphabet(), manuScriptReviewerCommonVO.getRoleId(), paging);
            }
        }

        Integer authorId = manuScriptRepo.getAuthorId(manuScriptReviewerCommonVO.getManuscriptId());
        List<Integer> coAuthorList = manuScriptCoAuthorRepo.getCoAuthorForManuscript(manuScriptReviewerCommonVO.getManuscriptId());

        if (getUnassignedReviewer != null && !getUnassignedReviewer.isEmpty()) {
            for (UserJournal i : getUnassignedReviewer) {
                if (authorId != null && !authorId.equals(i.getUserId()) && !coAuthorList.contains(i.getUserId())) {
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
                        commonvo.setCountry(i.getUser().getCountries().getName());

                    }

                    List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo.getReviewerDataById(i.getUserId(),
                            manuScriptReviewerCommonVO.getJournalId());
                    Long articlesInHandCount = getArticlesInHands(i.getJournalId(), i.getUserId());
                    List<Integer> reviewStatusCode = Arrays.asList(12, 13, 14, 15);
                    Long articlesInReviewCount = manuScriptReviewerRepo
                            .getReviewedManuscriptForReviewer(reviewStatusCode, i.getJournalId(), i.getUserId())
                            .stream().count();
                    List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream()
                            .filter(obj -> obj.getCompleteDate() != null).collect(Collectors.toList());
                    Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream()
                            .filter(obj -> reviewStatusCode.contains(obj.getManuscriptStatus()))
                            .sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed()).findFirst();
                    if (lastReviewedDate.isPresent() && lastReviewedDate.get().getCompleteDate() != null) {
                        commonvo.setLastReviewedDate(lastReviewedDate.get().getCompleteDate());
                    }

                    commonvo.setArticlesInHandCount(articlesInHandCount.intValue());
                    commonvo.setTotalArticlesReviewed(articlesInReviewCount.intValue());

                    List<ReviewerAvailability> getReviewerAvailabilityData = reviewerAvailabilityRepo
                            .getReviewerAvailabilityById(i.getUser().getId());
                    if (getReviewerAvailabilityData != null && !getReviewerAvailabilityData.isEmpty()) {
                        getReviewerAvailabilityData.stream().forEach(l -> {
                            List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                            tempReviewer.add(l);
                            commonvo.setReviewerAvailability(tempReviewer);
                        });
                    }

                    rsd.add(commonvo);
                }

            }
        }

        return rsd;
    }

    @Override
    public Pair<Integer, List<ManuScriptReviewerCommonVO>> getReviewerGlobalSearch(
            ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        List<ManuScriptReviewerCommonVO> rsd = new ArrayList<>();
        Pageable paging = PageRequest.of(manuScriptReviewerCommonVO.getPageNo(), manuScriptReviewerCommonVO.getLimit());

        if (manuScriptReviewerCommonVO.getRoleId() == null)
        	manuScriptReviewerCommonVO.setRoleId(5);

        List<UserJournal> unAssignedReviewer = userJournalRepo.getGlobalSearchReviewer(manuScriptReviewerCommonVO.getRoleId(), manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getSearch(), paging);
        Long total = userJournalRepo.countGlobalSearchReviewer(manuScriptReviewerCommonVO.getRoleId(), manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getSearch());

        log.info("count: "+total);
        
        if (manuScriptReviewerCommonVO != null && StringUtils.isNotBlank(manuScriptReviewerCommonVO.getSearch())) {
            unAssignedReviewer = userJournalRepo.getGlobalSearchReviewer(manuScriptReviewerCommonVO.getRoleId(), manuScriptReviewerCommonVO.getJournalId(),
                    manuScriptReviewerCommonVO.getManuscriptId(),
                    manuScriptReviewerCommonVO.getSearch(), paging);
        }

        Integer authorId = manuScriptRepo.getAuthorId(manuScriptReviewerCommonVO.getManuscriptId());
        List<Integer> coAuthorList = manuScriptCoAuthorRepo.getCoAuthorForManuscript(manuScriptReviewerCommonVO.getManuscriptId());
        if (unAssignedReviewer != null && !unAssignedReviewer.isEmpty()) {
            for (UserJournal journal : unAssignedReviewer) {
                if (authorId != null && !authorId.equals(journal.getUserId()) && !coAuthorList.contains(journal.getUserId())) {
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
                    List<Integer> reviewStatusCode = Arrays.asList(12, 13, 14, 15);
                    Long articlesInReviewCount = manuScriptReviewerRepo
                            .getReviewedManuscriptForReviewer(reviewStatusCode, journal.getJournalId(),
                                    journal.getUserId())
                            .stream().count();
                    List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo
                            .getReviewerDataById(journal.getUserId(), journal.getJournalId());
                    List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream()
                            .filter(obj -> obj.getCompleteDate() != null).collect(Collectors.toList());
                    Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream()
                            .filter(obj -> reviewStatusCode.contains(obj.getManuscriptStatus()))
                            .sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed()).findFirst();

                    commonvo.setTotalArticlesReviewed(articlesInReviewCount.intValue());
                    commonvo.setArticlesInHandCount(
                            getArticlesInHands(journal.getJournalId(), journal.getUserId()).intValue());
                    if (lastReviewedDate.isPresent() && lastReviewedDate.get().getCompleteDate() != null) {
                        commonvo.setLastReviewedDate(lastReviewedDate.get().getCompleteDate());
                    }

                    List<ReviewerAvailability> reviewerAvailabilityData = reviewerAvailabilityRepo
                            .getReviewerAvailabilityById(journal.getUser().getId());
                    if (reviewerAvailabilityData != null && !reviewerAvailabilityData.isEmpty()) {
                        commonvo.setReviewerAvailability(reviewerAvailabilityData);
//					reviewerAvailabilityData.stream().forEach(l -> {
//						List<ReviewerAvailability> tempReviewer = new ArrayList<>();
//						tempReviewer.add(l);
//						commonvo.setReviewerAvailability(tempReviewer);
//					});
                    }
                    rsd.add(commonvo);
                }
            }
        }
        return Pair.of(total.intValue(), rsd);
    }

    @Override
    public Integer totalReviewerSuggestedBtDatabaseCount(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        Integer totalReviewerSuggestedBtDatabaseCount = 0;

        ManuScript manu = manuScriptRepo.getById(manuScriptReviewerCommonVO.getManuscriptId());
        if (manuScriptReviewerCommonVO.getSpecialization() == "")
            manuScriptReviewerCommonVO.setSpecialization(null);
        if (manuScriptReviewerCommonVO.getExperties() == "")
            manuScriptReviewerCommonVO.setExperties(null);
        if (manuScriptReviewerCommonVO.getName() == "")
            manuScriptReviewerCommonVO.setName(null);
        if (manuScriptReviewerCommonVO.getEmail() == "")
            manuScriptReviewerCommonVO.setEmail(null);
        if (manuScriptReviewerCommonVO.getAlphabet() == "")
            manuScriptReviewerCommonVO.setAlphabet(null);
        if (manuScriptReviewerCommonVO.getRoleId() == null)
            manuScriptReviewerCommonVO.setRoleId(5);

        if (manuScriptReviewerCommonVO.getAlphabet() == null) {
            if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() == null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {

                totalReviewerSuggestedBtDatabaseCount = userJournalRepo.getDatabaseSuggestedReviewerCount(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());

            } else if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo.getDatabaseSuggestedReviewerSpecializationCount(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo
                        .getDatabaseSuggestedReviewerSpecializationExpertiesCount(
                                manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                                manuScriptReviewerCommonVO.getSpecialization(),
                                manuScriptReviewerCommonVO.getCountryId(), manuScriptReviewerCommonVO.getExperties(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo
                        .getDatabaseSuggestedReviewerSpecializationExpertiesCounts(
                                manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                                manuScriptReviewerCommonVO.getSpecialization(),
                                manuScriptReviewerCommonVO.getExperties(), manu.getAuthorId());
            } else {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo.getDatabaseSuggestedReviewerSearchAnyCount(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getCountryId(),
                        manuScriptReviewerCommonVO.getExperties(), manuScriptReviewerCommonVO.getName(),
                        manuScriptReviewerCommonVO.getEmail(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());
            }
        } else {

            if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() == null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {

                totalReviewerSuggestedBtDatabaseCount = userJournalRepo.getDatabaseSuggestedReviewerCountAlphabet(
                        manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                        manuScriptReviewerCommonVO.getAlphabet(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());

            } else if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getName() == null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getExperties() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo
                        .getDatabaseSuggestedReviewerSpecializationCountAlphabet(
                                manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                                manuScriptReviewerCommonVO.getSpecialization(),
                                manuScriptReviewerCommonVO.getAlphabet(), manuScriptReviewerCommonVO.getCountryId(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() != null) {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo
                        .getDatabaseSuggestedReviewerSpecializationExpertiesCountAlphabet(
                                manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                                manuScriptReviewerCommonVO.getSpecialization(),
                                manuScriptReviewerCommonVO.getCountryId(), manuScriptReviewerCommonVO.getExperties(),
                                manuScriptReviewerCommonVO.getAlphabet(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());

            } else if (manuScriptReviewerCommonVO.getName() == null && manuScriptReviewerCommonVO.getExperties() != null
                    && manuScriptReviewerCommonVO.getEmail() == null
                    && manuScriptReviewerCommonVO.getSpecialization() != null
                    && manuScriptReviewerCommonVO.getCountryId() == null) {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo
                        .getDatabaseSuggestedReviewerSpecializationExpertiesCountAlphabets(
                                manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(),
                                manuScriptReviewerCommonVO.getSpecialization(), manuScriptReviewerCommonVO.getExperties(),
                                manuScriptReviewerCommonVO.getAlphabet(), manu.getAuthorId());

            } else {
                totalReviewerSuggestedBtDatabaseCount = userJournalRepo
                        .getDatabaseSuggestedReviewerSearchAnyCountAlphabet(manuScriptReviewerCommonVO.getJournalId(),
                                manuScriptReviewerCommonVO.getManuscriptId(),
                                manuScriptReviewerCommonVO.getSpecialization(),
                                manuScriptReviewerCommonVO.getCountryId(), manuScriptReviewerCommonVO.getExperties(),
                                manuScriptReviewerCommonVO.getName(), manuScriptReviewerCommonVO.getEmail(),
                                manuScriptReviewerCommonVO.getAlphabet(), manu.getAuthorId(), manuScriptReviewerCommonVO.getRoleId());
            }
        }

        return totalReviewerSuggestedBtDatabaseCount;
    }

    @Override
    public Integer totalReviewerGlobalSearchCount(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        Integer totalReviewerSuggestedBtDatabaseCount = 0;

        if (manuScriptReviewerCommonVO != null && manuScriptReviewerCommonVO.getSearch() != null
                && manuScriptReviewerCommonVO.getSearch() != "") {
            totalReviewerSuggestedBtDatabaseCount = userJournalRepo.getGlobalSearchReviewerCount(
                    manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getSearch());
        }

        return totalReviewerSuggestedBtDatabaseCount;
    }

    @Override
    public boolean checkReviewCategory(List<ManuScriptReviewer> getAllDataByjournal, Integer manuscriptId,
                                       Integer manuscriptVersion, String checkFor) {
        // must read before use -: CheckFor = "NoResponse" : "Reviewed" : "InReview"
        boolean isCategoryMatch = false;
        if (checkFor != null && (checkFor.equals(AppConstants.NORESPONSE) || checkFor.equals(AppConstants.REVIEWED)
                || checkFor.equals(AppConstants.INREVIEW))) {
            try {
                int mcount = 0; // total record with same manuscript_id and maximum version
                int mvalidcount = 0; // total no of assigned reviewer and is_Active 1
                int delayCount = 0; // assigned to reviewer but reviewer either reject invitation or not accepted
                // invitation
                int totalreviewed = 0;
                int totalNoResponse = 0;
                int inReview = 0;
                for (ManuScriptReviewer allData : getAllDataByjournal) {
                    try {
                        if (manuscriptId.equals(allData.getManuscriptId())
                                && allData.getManuscriptVersion().equals(manuscriptVersion)) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
                            Date todayDate = df.parse(df.format(new Date()));
                            if (allData.getIsActive() != null) {
                                mcount++; // total matched manuscript_id and manuscript_version
                                // Condition only for no response
                                if (allData.getIsActive() == 0 || allData.getManuscriptStatus() == 60 || allData.getManuscriptStatus() == 36 || ((allData.getManuscriptStatus() == 8 || allData.getManuscriptStatus() == 66
                                        || allData.getManuscriptStatus() == 35) && reviewerDueDate.before(todayDate))) {
                                    mvalidcount++;
                                    delayCount++;
                                } else {

                                    boolean isDueDateExpired = reviewerDueDate.before(todayDate);

                                    if (allData.getManuscriptStatus() == 8 || allData.getManuscriptStatus() == 66
                                            || allData.getManuscriptStatus() == 35) {
                                        mvalidcount++;
                                        if (isDueDateExpired) {
                                            delayCount++;
                                        } else {
                                            inReview++; // inReview article
                                        }
                                    }
                                    if (inReview == 0) {
                                        if (allData.getManuscriptStatus() != null
                                                && (allData.getManuscriptStatus() == 12
                                                || allData.getManuscriptStatus() == 13
                                                || allData.getManuscriptStatus() == 14
                                                || allData.getManuscriptStatus() == 15)) {

                                            mvalidcount++; // matched status

                                            // due date code pending
                                        } else if (allData.getManuscriptStatus() != null
                                                && allData.getManuscriptStatus() == 36
                                                || allData.getManuscriptStatus() == 60) {
                                            // reject review invitation : 36 ; reviewer decline :60 ;
                                            mvalidcount++;
                                            delayCount++;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        log.info("Error occur in checkReviewCategory() :: ManuscriptId : " + manuscriptId
                                + " :: ManuscriptVersion : " + manuscriptVersion + " :: Exception : " + e);
                        e.printStackTrace();
                    }
                }
                if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {
                    if (inReview == 0) {
                        if (delayCount == mcount) {
                            totalNoResponse++;
                        } else if (delayCount != mcount) { // delayCount != mcount :: Any reviewer acted on a manuscript
                            totalreviewed++;
                        }
                    }
                }
                if (checkFor.equals(AppConstants.NORESPONSE)) {
                    if (totalNoResponse > 0) {
                        isCategoryMatch = true;
                    }
                } else if (checkFor.equals(AppConstants.REVIEWED)) {
                    if (totalreviewed > 0) {
                        isCategoryMatch = true;
                    }
                } else {
                    if (inReview > 0) {
                        isCategoryMatch = true;
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.info("Error occur in checkReviewCategory() :: ManuscriptId : " + manuscriptId
                        + " :: ManuscriptVersion : " + manuscriptVersion + " :: Exception : " + e);
                e.printStackTrace();
            }
        }
        return isCategoryMatch;
    }

    @Override
    public List<ManuscriptListVO> getReviewedManuscriptByManuscriptIdForDashboard(List<Integer> manuscriptId) {

        List<ManuscriptListVO> manuscriptList = new ArrayList();

        if (manuscriptId != null && !manuscriptId.isEmpty()) {

            List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptByManuscriptList(manuscriptId);
            List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptUniqueByManuscriptList(manuscriptId);
            for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
                boolean isReviewManuscript = checkReviewCategory(getAllDataByjournal, uniqueData.getManuscriptId(),
                        uniqueData.getManuscriptVersion(), AppConstants.REVIEWED);
                if (isReviewManuscript) {
                    ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                    manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
                    manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                    manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                    manuscriptList.add(manuscriptListVO);
                }
            }
        }
        return manuscriptList;

    }

    @Override
    public List<ManuscriptListVO> getNoResponseManuscriptByManuscriptIdForDashboard(List<Integer> manuscriptId) {

        List<ManuscriptListVO> manuscriptList = new ArrayList();
        if (manuscriptId != null && !manuscriptId.isEmpty()) {
            List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptByManuscriptListNoResponse(manuscriptId);
            List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptUniqueByManuscriptList(manuscriptId);
            if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
                for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
                    boolean isNoResponseManuscript = checkReviewCategory(getAllDataByjournal,
                            uniqueData.getManuscriptId(), uniqueData.getManuscriptVersion(), AppConstants.NORESPONSE);
                    if (isNoResponseManuscript) {
                        ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                        manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
                        manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                        manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                        manuscriptList.add(manuscriptListVO);
                    }
                }
            }
        }
        return manuscriptList;
    }

    @Override
    public List<ManuscriptListVO> getInReviewManuscriptByManuscriptIdForDashboard(List<Integer> manuscriptId) {

        List<ManuscriptListVO> manuscriptList = new ArrayList();
        if (manuscriptId != null && !manuscriptId.isEmpty()) {
            List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptByManuscriptList(manuscriptId);
            List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
                    .getReviewedManuscriptUniqueByManuscriptList(manuscriptId);
            if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
                for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
                    boolean isInReviewManuscript = checkReviewCategory(getAllDataByjournal,
                            uniqueData.getManuscriptId(), uniqueData.getManuscriptVersion(), AppConstants.INREVIEW);
                    if (isInReviewManuscript) {
                        ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
                        manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
                        manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
                        manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
                        manuscriptList.add(manuscriptListVO);
                    }
                }
            }
        }

        return manuscriptList;
    }

    @Override
    public List<ReviewerPublonsCommentVO> findReviewerCommentsForPublons(Integer journalId) {
        return manuScriptReviewerRepo.findReviewerCommentsForPublons(journalId);
    }

    @Override
    public List<AuthorSuggestedReviewerVo> authorOpposedReviewer(Integer manuscriptId, Integer journalId) {
        List<AuthorSuggestedReviewerVo> rsd = new ArrayList<>();
        List<ReviewerOpposed> getauthorOpposedReviewerData = reviewerOpposedRepo.getauthorOpposedReviewer(manuscriptId,
                journalId);
        Integer authorId = manuScriptRepo.getAuthorId(manuscriptId);
        List<Integer> coAuthorList = manuScriptCoAuthorRepo.getCoAuthorForManuscript(manuscriptId);

        if (getauthorOpposedReviewerData != null && !getauthorOpposedReviewerData.isEmpty()) {
            getauthorOpposedReviewerData.stream().forEach(i -> {
                if (authorId != null && !authorId.equals(i.getId()) && !coAuthorList.contains(i.getId())) {
                    AuthorSuggestedReviewerVo commonvo = new AuthorSuggestedReviewerVo();
                    commonvo.setReviewerSuggestedId(i.getId());
                    commonvo.setSalutation(i.getSalutation());
                    commonvo.setFirstName(i.getFirstName());
                    commonvo.setLastName(i.getLastName());
                    commonvo.setReviewerName(i.getFirstName() + " " + i.getLastName());
                    commonvo.setEmail(i.getEmail());
                    commonvo.setSpecialization(i.getSpecialization());
                    commonvo.setSubSpecialization(i.getSubject());
                    commonvo.setCountry(i.getCountry());
                    commonvo.setManuscriptId(i.getManuscriptId());
                    commonvo.setManuscriptVersion(i.getManuscriptVersion());
                    commonvo.setSuggestedDate(i.getOpposedDate());
                    commonvo.setJournalId(i.getJournalId());
                    commonvo.setOpposedReviewerId(i.getOpposedReviewerId());
                    commonvo.setComments(i.getComments());
                    commonvo.setInstitution(i.getInstitution());
                    rsd.add(commonvo);
                }
            });
        }
        return rsd;
    }

    @Override
    public List<AuthorSuggestedReviewerVo> authorOpposedReviewerPagination(Integer manuscriptId, Integer journalId, Integer startIndex, Integer limit) {
        List<AuthorSuggestedReviewerVo> rsd = new ArrayList<>();
        List<ReviewerOpposed> getauthorOpposedReviewerData = reviewerOpposedRepo.getauthorOpposedReviewerPagination(manuscriptId,
                journalId, startIndex, limit);
        Integer authorId = manuScriptRepo.getAuthorId(manuscriptId);
        List<Integer> coAuthorList = manuScriptCoAuthorRepo.getCoAuthorForManuscript(manuscriptId);

        if (getauthorOpposedReviewerData != null && !getauthorOpposedReviewerData.isEmpty()) {
            getauthorOpposedReviewerData.stream().forEach(i -> {
                if (authorId != null && !authorId.equals(i.getId()) && !coAuthorList.contains(i.getId())) {
                    AuthorSuggestedReviewerVo commonvo = new AuthorSuggestedReviewerVo();
                    commonvo.setReviewerSuggestedId(i.getId());
                    commonvo.setSalutation(i.getSalutation());
                    commonvo.setFirstName(i.getFirstName());
                    commonvo.setLastName(i.getLastName());
                    commonvo.setReviewerName(i.getFirstName() + " " + i.getLastName());
                    commonvo.setEmail(i.getEmail());
                    commonvo.setSpecialization(i.getSpecialization());
                    commonvo.setSubSpecialization(i.getSubject());
                    commonvo.setCountry(i.getCountry());
                    commonvo.setManuscriptId(i.getManuscriptId());
                    commonvo.setManuscriptVersion(i.getManuscriptVersion());
                    commonvo.setSuggestedDate(i.getOpposedDate());
                    commonvo.setJournalId(i.getJournalId());
                    commonvo.setOpposedReviewerId(i.getOpposedReviewerId());
                    commonvo.setComments(i.getComments());
                    commonvo.setInstitution(i.getInstitution());
                    rsd.add(commonvo);
                }
            });
        }
        return rsd;
    }

    @Override
    public Integer authorOpposedReviewerCount(Integer manuscriptId,
                                              Integer journalId) {
        return reviewerOpposedRepo.getauthorOpposedReviewerCount(manuscriptId, journalId);
    }

    @Override
    public void updateSentToPublonsYes(Integer id, List<Integer> idList) {
        manuScriptReviewerRepo.updateSentToPublonsYes(id, idList);
    }

    public List<ManuScriptReviewer> getArticleInHandsData(Integer journalId, Integer reviewerId) {
//		List<ManuScriptReviewer> reReviewList = manuScriptReviewerRepo.getReRivewManuscript(journalId, reviewerId);
//		List<ManuScriptReviewer> reviewList = manuScriptReviewerRepo.getManuscriptForReview(journalId, reviewerId);
        List<ManuScriptReviewer> reviewCountList = manuScriptReviewerRepo.getManuscriptReviewer(35, journalId,
                reviewerId);
//		List<ManuScriptReviewer> reviewDetails = manuScriptReviewerRepo.getManuscriptReviewer(66, journalId,
//				reviewerId);
//		List<ManuScriptReviewer> articlesInHandsDetails = Stream
//				.of(reReviewList, reviewList, reviewCountList, reviewDetails).flatMap(Collection::stream)
//				.collect(Collectors.toList());

        return reviewCountList;

    }

    @Override
    public List<ReviewerAvailabilityStatusVO> getReviewerStaticsDetails(IDVO id) {
        List<ReviewerAvailabilityStatusVO> add = new ArrayList<>();
        List<ManuScriptReviewer> reviewerData = new ArrayList<>();

        if (StringUtils.equalsIgnoreCase(id.getManuscriptStatus(), "articlesInHands")) {
            reviewerData = getArticleInHandsData(id.getJournalId(), id.getUserId());
        } else if (StringUtils.equalsIgnoreCase(id.getManuscriptStatus(), "invited")) {
            reviewerData = manuScriptReviewerRepo.getRevieweManuscriptForStaticActive(id.getManuscriptStatusId(),
                    id.getJournalId(), id.getUserId());
        } else if (StringUtils.equalsIgnoreCase(id.getManuscriptStatus(), "completed")) {
            reviewerData = manuScriptReviewerRepo.getReviewedManuscriptForReviewer(id.getManuscriptStatusId(),
                    id.getJournalId(), id.getUserId());
        } else if (StringUtils.equalsIgnoreCase(id.getManuscriptStatus(), "reject")
                || StringUtils.equalsIgnoreCase(id.getManuscriptStatus(), "declined")) {
            reviewerData = manuScriptReviewerRepo.getRevieweManuscriptForStatic(id.getManuscriptStatusId(),
                    id.getJournalId(), id.getUserId());
        } else if (StringUtils.equalsIgnoreCase(id.getManuscriptStatus(), "all")) {
            reviewerData = getAllData(reviewerData, id.getJournalId(), id.getUserId());
        }

        if (reviewerData != null && !reviewerData.isEmpty()) {
            reviewerData.stream().forEach(i -> {
                ReviewerAvailabilityStatusVO addData = new ReviewerAvailabilityStatusVO();
                addData.setAcceptDate(i.getAcceptDate());
                addData.setDueDate(i.getDueDate());
                addData.setUnassignedDate(i.getUnassignedDate());
                addData.setStartDate(i.getStartDate());
                addData.setCompleteDate(i.getCompleteDate());
                addData.setRejectDate(i.getRejectDate());
                addData.setManuscriptCode(i.getManuscript().getManuscriptCode());
                addData.setManuscriptTitle(i.getManuscript().getManuscriptTitle());
                addData.setManuscriptStatus(i.getManuscript().getStatusMaster().getStatusValueForEditor());
                addData.setCompetingInterests(i.getCompetingInterests());
                addData.setMessageFromManuscript(i.getMessageFromManuscript());
                addData.setReviewerResponse(i.getReviewerResponse());
                addData.setRejectionReason(i.getRejectionReason());
                addData.setColour(setLegendColors(addData, i));
                addData.setCommentsForReviewer(i.getCommentsForReviewer());
                addData.setCommentsToAuthor(i.getCommentsToAuthor());
                addData.setCommentsToEditor(i.getCommentsToEditor());
                ReviewerRating getData = reviewerRatingRepo.getReviewerRatingOnManuscript(id.getUserId(),
                        i.getManuscriptId(), i.getManuscriptVersion());
                if (getData != null) {
                    addData.setQuality(getData.getQuality());
                    addData.setTimeliness(getData.getTimeliness());
                }
                add.add(addData);
            });
        }
        return add;
    }

    private List<ManuScriptReviewer> getAllData(List<ManuScriptReviewer> reviewerData, Integer journalId,
                                                Integer userId) {
        List<ManuScriptReviewer> articlesInHands = getArticleInHandsData(journalId, userId);
        List<ManuScriptReviewer> invitedList = manuScriptReviewerRepo
                .getRevieweManuscriptForStaticActive(Arrays.asList(8, 66), journalId, userId);

        List<ManuScriptReviewer> completedList = manuScriptReviewerRepo
                .getReviewedManuscriptForReviewer(Arrays.asList(12, 13, 14, 15), journalId, userId);

        List<ManuScriptReviewer> rejectedAndDeclinedList = manuScriptReviewerRepo
                .getRevieweManuscriptForStatic(Arrays.asList(36, 60), journalId, userId);

        reviewerData = Stream.of(articlesInHands, invitedList, completedList, rejectedAndDeclinedList)
                .flatMap(Collection::stream).collect(Collectors.toList());

        return reviewerData;
    }

    public String getOtherReviewerFlagValue(UserJournalManuscriptVo userJournalManuscriptVo) {
        JournalPolicies p = journalPoliciesRepo.getJournalPoliciesDetails(userJournalManuscriptVo.getJournalId());
        String otherReviewerFlag = null;
        if (p != null) {
            otherReviewerFlag = p.getOtherReviewersFlag();
        }
        return otherReviewerFlag;
    }

    @Override
    public Integer getInvitedreviewerCount(ManuScriptReviewerVO manuScriptReviewerVo) {

        Integer reviewerCount = 0;

        reviewerCount = manuScriptReviewerRepo.getInvitedreviewer(manuScriptReviewerVo.getManuscriptId(),
                manuScriptReviewerVo.getReviewerId(), manuScriptReviewerVo.getJournalId(),
                manuScriptReviewerVo.getManuscriptVersion());

        return reviewerCount;
    }

    //	manuScriptReviewerCommonVO.getManuscriptId(),manuScriptReviewerCommonVO.getManuscriptVersion(),manuScriptReviewerCommonVO.getJournalId(),
    @Override
    public ManuScriptReviewerInvitationVO systemSuggestedReviewerPaggingImpl(
            ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        Integer manuscriptId = manuScriptReviewerCommonVO.getManuscriptId();
        Integer journalId = manuScriptReviewerCommonVO.getJournalId();
        String search = manuScriptReviewerCommonVO.getSearch();
        List<String> keywordList = new ArrayList();
        int totalCount = 0;

        ManuScriptReviewerInvitationVO manuScriptReviewerInvitationVO = new ManuScriptReviewerInvitationVO();
        List<ManuScriptReviewerCommonVO> reviewerList = new ArrayList<>();
        // Pagination start
        int page = manuScriptReviewerCommonVO.getPageNo() != null ? manuScriptReviewerCommonVO.getPageNo() : 0;
        int limit = manuScriptReviewerCommonVO.getLimit() != null ? manuScriptReviewerCommonVO.getLimit() : 10;
        String sortingCol = StringUtils.isEmpty(manuScriptReviewerCommonVO.getSortBy()) ? "firstName"
                : manuScriptReviewerCommonVO.getSortBy();
        String sortType = StringUtils.isEmpty(manuScriptReviewerCommonVO.getSort()) ? "ASC"
                : manuScriptReviewerCommonVO.getSort();
        Pageable paging = null;
        if (sortingCol.equals("firstName")) {
            if (StringUtils.equalsIgnoreCase(sortType, "ASC")) {
                paging = PageRequest.of(page, limit,
                        Sort.by(Sort.Direction.ASC, sortingCol).and(Sort.by(Sort.Direction.ASC, "lastName")));
            } else {
                paging = PageRequest.of(page, limit,
                        Sort.by(Sort.Direction.DESC, sortingCol).and(Sort.by(Sort.Direction.DESC, "lastName")));
            }
        } else if (sortingCol.equalsIgnoreCase("Countries.name")) {
            paging = PageRequest.of(page, limit);
        } else {
            if (StringUtils.equalsIgnoreCase(sortType, "ASC")) {
                paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, sortingCol));
            } else {
                paging = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, sortingCol));
            }
        }

        List<UsersDupVo> user = new ArrayList();

        ManuScript manuscript = manuScriptRepo.getById(manuscriptId);
        List<ManuscriptJournalSubject> manuscriptJournalSubjectList = manuscriptJournalSubjectRepo
                .findByManuscriptId(manuscriptId, 1);
        if (manuscriptJournalSubjectList != null && !manuscriptJournalSubjectList.isEmpty()) {
            for (ManuscriptJournalSubject manuscriptJournalSubject : manuscriptJournalSubjectList) {
                keywordList.add(manuscriptJournalSubject.getJournalSubject().getJournalSubject());
            }
        }
        if (manuscript != null && manuscript.getManuscriptKeywords() != null
                && manuscript.getManuscriptKeywords() != "") {
            String keyWordArray[] = manuscript.getManuscriptKeywords().split(",");
            for (String string : keyWordArray) {
                keywordList.add((string != null && "" != string) ? string.trim() : "");
            }
        }

        if (keywordList != null && !keywordList.isEmpty()) {
            List<Integer> inProcessReviewer = manuScriptReviewerRepo
                    .getInprocessReviewerList(manuscript.getManuScriptId(), manuscript.getManuscriptVersion());
            inProcessReviewer.add(manuscript.getAuthorId());
            List<Integer> userId = new ArrayList<>();
            if (inProcessReviewer.size() > 0) {
                userId = userJournalRepo.getAllReviewerList(journalId, inProcessReviewer);
            } else {
                userId = userJournalRepo.getAllReviewerIdList(journalId, manuscript.getAuthorId());

            }
            if (sortingCol.equalsIgnoreCase("Countries.name")) {
                if (StringUtils.isBlank(search)) {
                    if (StringUtils.equalsIgnoreCase(sortType, "ASC")) {
                        user = userRepo.getUserBySpecializationAndSubjectVoListCountrySortAsc(keywordList, userId,
                                paging);
                    } else {
                        user = userRepo.getUserBySpecializationAndSubjectVoListCountrySortDesc(keywordList, userId,
                                paging);
                    }
                    totalCount = userRepo.getUserBySpecializationAndSubjectVoListCount(keywordList, userId);
                } else {
                    if (StringUtils.equalsIgnoreCase(sortType, "ASC")) {
                        user = userRepo.getUserBySpecializationAndSubjectVoListCountrySortAsc(keywordList, userId,
                                paging, search);
                    } else {
                        user = userRepo.getUserBySpecializationAndSubjectVoListCountrySortDesc(keywordList, userId,
                                paging, search);
                    }
                    totalCount = userRepo.getUserBySpecializationAndSubjectVoListCount(keywordList, userId, search);
                }
            } else {
                if (StringUtils.isBlank(search)) {
                    user = userRepo.getUserBySpecializationAndSubjectVoList(keywordList, userId, paging);
                    totalCount = userRepo.getUserBySpecializationAndSubjectVoListCount(keywordList, userId);
                } else {
                    user = userRepo.getUserBySpecializationAndSubjectVoList(keywordList, userId, paging, search);
                    totalCount = userRepo.getUserBySpecializationAndSubjectVoListCount(keywordList, userId, search);
                }
            }
        }

        Integer authorId = manuScriptRepo.getAuthorId(manuScriptReviewerCommonVO.getManuscriptId());
        List<Integer> coAuthorList = manuScriptCoAuthorRepo.getCoAuthorForManuscript(manuScriptReviewerCommonVO.getManuscriptId());

        if (user != null && !user.isEmpty()) {
            for (UsersDupVo userData : user) {
                if (authorId != null && !authorId.equals(userData.getId()) && !coAuthorList.contains(userData.getId())) {
                    ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                    List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo.getReviewerDataById(userData.getId(),
                            journalId);
                    Long articlesInHandCount = getArticlesInHands(journalId, userData.getId());
                    commonvo.setArticlesInHandCount(articlesInHandCount.intValue());
                    List<Integer> reviewStatusCode = Arrays.asList(12, 13, 14, 15);
                    Long articlesInReviewCount = manuScriptReviewerRepo
                            .getReviewedManuscriptForReviewer(reviewStatusCode, journalId, userData.getId()).stream()
                            .count();
                    commonvo.setTotalArticlesReviewed(articlesInReviewCount.intValue());
                    List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream()
                            .filter(obj -> obj.getCompleteDate() != null).collect(Collectors.toList());
                    Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream()
                            .filter(obj -> reviewStatusCode.contains(obj.getManuscriptStatus()))
                            .sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed()).findFirst();
                    if (lastReviewedDate.isPresent() && lastReviewedDate.get().getCompleteDate() != null) {
                        commonvo.setLastReviewedDate(lastReviewedDate.get().getCompleteDate());
                    }
                    commonvo.setName(userData.getFirstName() + " " + userData.getLastName());
                    commonvo.setEmail(userData.getEmail());
                    commonvo.setReviewerId(userData.getId());
                    commonvo.setSpecialization(userData.getSpecialization());
                    commonvo.setInstitution(userData.getInstitute());
                    if (userData.getExpertise() != null)
                        commonvo.setSubSpecialization(userData.getExpertise());
                    if (userData.getCountries() != null) {
                        commonvo.setCountry(userData.getCountries().getName());
                    }
                    List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                    commonvo.setReviewerAvailability(tempReviewer);
                    reviewerList.add(commonvo);
                }
            }
        }
        manuScriptReviewerInvitationVO.setManuScriptReviewerCommonVO(reviewerList);
        manuScriptReviewerInvitationVO.setTotalCount(totalCount);
        return manuScriptReviewerInvitationVO;
    }


    @Override
    public List<ManuScriptReviewerCommonVO> getCommentaryDataForManuscript(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        List<ManuScriptReviewerCommonVO> manuscriptList = new ArrayList();
        Pageable paging = PageRequest.of(manuScriptReviewerCommonVO.getPageNo(), manuScriptReviewerCommonVO.getLimit());
        Integer journalId = manuScriptReviewerCommonVO.getJournalId();
        try {
            List<ManuScriptReviewer> getData = manuScriptReviewerRepo.getCommentaryDataForManuscript(journalId, manuScriptReviewerCommonVO.getManuscriptId(), paging);
            if (getData != null && !getData.isEmpty()) {
                for (ManuScriptReviewer manuScriptReviewer : getData) {

                    User user = userRepo.getUser(manuScriptReviewer.getReviewerId());

                    ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                    commonvo.setName(user.getFirstName() + " " + user.getLastName());
                    commonvo.setEmail(user.getEmail());
                    commonvo.setRoleId(5);
                    commonvo.setReviewerId(user.getId());
                    commonvo.setSpecialization(user.getSpecialization());
                    commonvo.setInstitution(user.getInstitute());
                    if (user.getExpertise() != null)
                        commonvo.setSubSpecialization(user.getExpertise());
                    if (user.getCountryId() != null) {
                        Countries countries = countriesRepo.getById(user.getCountryId());
                        if (countries != null) {
                            commonvo.setCountry(countries.getName());
                        }
                    }

                    List<ReviewerAvailability> getReviewerAvailabilityData = reviewerAvailabilityRepo
                            .getReviewerAvailabilityById(user.getId());
                    if (getReviewerAvailabilityData != null && !getReviewerAvailabilityData.isEmpty()) {
                        getReviewerAvailabilityData.stream().forEach(l -> {
                            List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                            tempReviewer.add(l);
                            commonvo.setReviewerAvailability(tempReviewer);
                        });
                    }
                    manuscriptList.add(commonvo);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return manuscriptList;
    }

    @Override
    public Integer getCommentaryDataForManuscriptCount(ManuScriptReviewerCommonVO commentaryVo) {
        return manuScriptReviewerRepo.getCommentaryDataForManuscriptCount(commentaryVo.getJournalId(), commentaryVo.getManuscriptId());
    }

    @Override
    public List<ManuScriptReviewerCommonVO> getRefineReviewerData(ManuScriptReviewerCommonVO commentaryVo) {
        List<ManuScriptReviewerCommonVO> manuscriptList = new ArrayList();
        Pageable paging = PageRequest.of(commentaryVo.getPageNo(), commentaryVo.getLimit());

        List<ManuScriptReviewer> getData = manuScriptReviewerRepo.getRefineReviewerData(commentaryVo.getJournalId(), commentaryVo.getManuscriptId(), commentaryVo.getSearch(), paging);


        if (getData != null && !getData.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewer : getData) {

                User user = userRepo.getUser(manuScriptReviewer.getReviewerId());

                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                commonvo.setName(user.getFirstName() + " " + user.getLastName());
                commonvo.setEmail(user.getEmail());
                commonvo.setRoleId(5);
                commonvo.setReviewerId(user.getId());
                commonvo.setSpecialization(user.getSpecialization());
                commonvo.setInstitution(user.getInstitute());
                if (user.getExpertise() != null)
                    commonvo.setSubSpecialization(user.getExpertise());
                if (user.getCountryId() != null) {
                    Countries countries = countriesRepo.getById(user.getCountryId());
                    if (countries != null) {
                        commonvo.setCountry(countries.getName());
                    }
                }

                List<ReviewerAvailability> getReviewerAvailabilityData = reviewerAvailabilityRepo
                        .getReviewerAvailabilityById(user.getId());
                if (getReviewerAvailabilityData != null && !getReviewerAvailabilityData.isEmpty()) {
                    getReviewerAvailabilityData.stream().forEach(l -> {
                        List<ReviewerAvailability> tempReviewer = new ArrayList<>();
                        tempReviewer.add(l);
                        commonvo.setReviewerAvailability(tempReviewer);
                    });
                }
                manuscriptList.add(commonvo);
            }
        }
        return manuscriptList;
    }


    public List<ManuScriptReviewerCommonVO> getReviewerCommentData(ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        List<ManuScriptReviewerCommonVO> manuscriptList = new ArrayList();
        Pageable paging = PageRequest.of(manuScriptReviewerCommonVO.getPageNo(), manuScriptReviewerCommonVO.getLimit());

        List<ManuScriptReviewer> getData = manuScriptReviewerRepo.getReviewerCommentData(manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getManuscriptId(), paging);


        if (getData != null && !getData.isEmpty()) {
            for (ManuScriptReviewer manuScriptReviewer : getData) {

                User user = userRepo.getUser(manuScriptReviewer.getReviewerId());

                ManuScriptReviewerCommonVO commonvo = new ManuScriptReviewerCommonVO();
                commonvo.setName(user.getFirstName() + " " + user.getLastName());
                commonvo.setEmail(user.getEmail());
                commonvo.setRoleId(5);
                commonvo.setReviewerId(user.getId());
                commonvo.setSpecialization(user.getSpecialization());
                commonvo.setCommentsToEditor(manuScriptReviewer.getCommentsToEditor());
                commonvo.setCommentsToAuthor(manuScriptReviewer.getCommentsToAuthor());
                commonvo.setInstitution(user.getInstitute());
                if (user.getExpertise() != null)
                    commonvo.setSubSpecialization(user.getExpertise());
                if (user.getCountryId() != null) {
                    Countries countries = countriesRepo.getById(user.getCountryId());
                    if (countries != null) {
                        commonvo.setCountry(countries.getName());
                    }
                }

                manuscriptList.add(commonvo);
            }
        }
        return manuscriptList;
    }

    @Override
    public ReviewerDashboardCountVO getReviewerDashboardCount(IDVO id) {
        ReviewerDashboardCountVO reviewerDashboardCountVO = new ReviewerDashboardCountVO();
        List<Integer> invite = Arrays.asList(8, 66);
        List<Integer> reviewed = Arrays.asList(12, 13, 14, 15);

        Integer inviteCount = manuScriptReviewerRepo.getManuscriptReviewerCount(invite, id.getJournalId(), id.getUserId());
        Integer reviewCount = manuScriptReviewerRepo.getManuscriptForReviewCount(id.getJournalId(), id.getUserId());
        Integer reviewedManuscriptCount = manuScriptReviewerRepo.getReviewedManuscriptForReviewerCount(reviewed, id.getJournalId(), id.getUserId());
        Integer reReviewCount = manuScriptReviewerRepo.getReRivewManuscriptCount(id.getJournalId(), id.getUserId());
        reviewerDashboardCountVO.setInvitationCount(inviteCount);
        reviewerDashboardCountVO.setReviewCount(reviewCount);
        reviewerDashboardCountVO.setReviewedCount(reviewedManuscriptCount);
        reviewerDashboardCountVO.setReReviewCount(reReviewCount);
        return reviewerDashboardCountVO;
    }

//    @Override
//    public String getReviewedCertificateForReviewer(EditorDashboardVO requestVo) {
//        Journal journal;
//        try {
//            journal = journalRepo.getJournalById(requestVo.getJournalId());
//            User u = userRepo.getByID(requestVo.getAuthorId());
//            String fullName = StringUtils.defaultString(u.getFirstName(), "") + " " + StringUtils.defaultString(u.getLastName(), "");
//            if (u != null && u.getSalutation() != null) {
//                fullName = u.getSalutation() == null ? fullName : u.getSalutation() + ". " + fullName;
//            }
//            requestVo.setAuthorName(fullName);
//            Date date = Calendar.getInstance().getTime();
//            DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
//            String strDate = dateFormat.format(date);
//            String fileName = uploadFolder + File.separator + "Reviewer_Certificates" + File.separator + journal.getJournalCode() + "_" + strDate + ".pdf";
//            File uploadDirectory = new File(uploadFolder + File.separator + "Reviewer_Certificates");
//            if (!uploadDirectory.exists()) {
//                uploadDirectory.mkdirs();
//            }
//
//            long day = 1000 * 60 * 60 * 24;
//            long now = System.currentTimeMillis();
//            for (File file : new File(uploadFolder + File.separator + "Reviewer_Certificates").listFiles()) {
//                if (!file.isDirectory() && (now - file.lastModified() > day)) {
//                    file.delete();
//                }
//            }
//
//            new CreatePdf().genrateCertificate(requestVo, journal, fileName, certificateDefaultImagePath, feImageUrl, journalImagePath, thumbnailUrl);
//
//            String fileServerPath = "";
//            if (serverENV.equalsIgnoreCase("AWS")) {
//                String ip = "https://" + serverIP;
//                fileServerPath = ip + separator + "Reviewer_Certificates" + File.separator + journal.getJournalCode() + "_" + strDate + ".pdf";
//            } else if (serverENV.equalsIgnoreCase("LOCAL")) {
//                fileServerPath = "file:///" + uploadFolder + separator + "Reviewer_Certificates" + File.separator + journal.getJournalCode() + "_" + strDate + ".pdf";
//            } else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
//                String ip = "https://" + serverIP + ":" + serverPort;
//                fileServerPath = ip + separator + "Reviewer_Certificates" + File.separator + journal.getJournalCode() + "_" + strDate + ".pdf";
//            }
//            log.info("Reviewer Cert path : " + fileServerPath);
//
//            return fileServerPath;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    
    @Override
    public ResponseEntity<Resource> getReviewedCertificateForReviewer(EditorDashboardVO requestVo) {

        try {

            Journal journal = journalRepo.getJournalById(requestVo.getJournalId());
            
            if (journal.getJournalId() == 865) {

                log.info(
                        "Indian Journal of Cancer detected. Generating new reviewer certificate.");

                return getIndianCancerReviewerCertificate(
                        requestVo,
                        journal);
            }

            User u = userRepo.getByID(requestVo.getAuthorId());

            String fullName = StringUtils.defaultString(u.getFirstName(), "") + " "
                    + StringUtils.defaultString(u.getLastName(), "");

            if (u != null && u.getSalutation() != null) {
                fullName = u.getSalutation() + ". " + fullName;
            }

            requestVo.setAuthorName(fullName);

            Date date = Calendar.getInstance().getTime();

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

            String strDate = dateFormat.format(date);

            String fileName = uploadFolder
                    + File.separator
                    + "Reviewer_Certificates"
                    + File.separator
                    + journal.getJournalCode()
                    + "_"
                    + strDate
                    + ".pdf";

            File uploadDirectory =
                    new File(uploadFolder + File.separator + "Reviewer_Certificates");

            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            long day = 1000 * 60 * 60 * 24;
            long now = System.currentTimeMillis();

            for (File file : uploadDirectory.listFiles()) {

                if (!file.isDirectory()
                        && (now - file.lastModified() > day)) {

                    file.delete();
                }

            }

            new CreatePdf().genrateCertificate(
                    requestVo,
                    journal,
                    fileName,
                    certificateDefaultImagePath,
                    feImageUrl,
                    journalImagePath,
                    thumbnailUrl);

            File pdfFile = new File(fileName);

            if (!pdfFile.exists()) {

                log.error("Certificate not generated.");

                return ResponseEntity.notFound().build();

            }

            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(pdfFile));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfFile.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + pdfFile.getName() + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                            HttpHeaders.CONTENT_DISPOSITION)
                    .header(HttpHeaders.CACHE_CONTROL,
                            "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(resource);

        } catch (Exception e) {

            log.error("Error generating reviewer certificate.", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    
    private ResponseEntity<Resource> getIndianCancerReviewerCertificate(EditorDashboardVO requestVo,Journal journal) throws Exception {

        log.info("Generating Indian Journal of Cancer reviewer certificate");


        User reviewer =userRepo.getByID(requestVo.getAuthorId());

        if (reviewer == null) {
            log.error("Reviewer not found for authorId={}",requestVo.getAuthorId());
            return ResponseEntity.notFound().build();
        }


        String reviewerName = StringUtils.defaultString(reviewer.getFirstName(),"");

        if (StringUtils.isNotBlank(reviewer.getMiddleName())) {
            reviewerName =reviewerName+ " "+ reviewer.getMiddleName();
        }

        if (StringUtils.isNotBlank(reviewer.getLastName())) {
            reviewerName =reviewerName+ " " + reviewer.getLastName();
        }

        if (reviewer.getSalutation() != null) {
            reviewerName =reviewer.getSalutation()+ ". "+ reviewerName;
        }

        reviewerName =reviewerName.trim();

        requestVo.setAuthorName(reviewerName);

        String journalLogoPath =certificateDefaultImagePath + "indian_cancer_society_logo.png";
        String jubileeLogoPath =certificateDefaultImagePath+ "indian_cancer_jubilee.png";


        File uploadDirectory =new File(uploadFolder+File.separator+"Reviewer_Certificates");

        if (!uploadDirectory.exists()) {
        	
            boolean created =uploadDirectory.mkdirs();

            if (!created&& !uploadDirectory.exists()) {
                log.error("Unable to create reviewer certificate directory: {}",uploadDirectory.getAbsolutePath());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }


        String strDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String fileName =
                uploadDirectory.getAbsolutePath()
                + File.separator
                + journal.getJournalCode()
                + "_IndianCancer_Reviewer_"
                + strDate
                + ".pdf";

        new CreatePdf()
                .genrateIndianCancerReviewerCertificate(
                        requestVo,
                        journal,
                        fileName,
                        certificateDefaultImagePath,
                        feImageUrl,
                        journalLogoPath,
                        jubileeLogoPath);


        File pdfFile = new File(fileName);

        if (!pdfFile.exists()) {
            log.error("Indian Cancer reviewer certificate was not generated: {}",fileName);
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource =new InputStreamResource(new FileInputStream(pdfFile));

        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.APPLICATION_PDF)
                .contentLength(
                        pdfFile.length())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                        + pdfFile.getName()
                        + "\"")
                .header(
                        HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        HttpHeaders.CONTENT_DISPOSITION)
                .header(
                        HttpHeaders.CACHE_CONTROL,
                        "no-cache, no-store, must-revalidate")
                .header(
                        HttpHeaders.PRAGMA,
                        "no-cache")
                .header(
                        HttpHeaders.EXPIRES,
                        "0")
                .body(resource);
    }

}
