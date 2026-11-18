package com.digi.uniprr.serviceImpl;

import com.digi.uniprr.VO.*;
import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.service.AllCommentService;
import com.digi.uniprr.utils.ManuscriptTransformationUtil;
import com.digi.uniprr.utils.UserTransformationalUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AllCommentServiceImpl implements AllCommentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ManuScriptRepo manuScriptRepo;

    @Autowired
    JournalsRepo journalsRepo;

    @Autowired
    ManuScriptReviewerRepo manuScriptReviewerRepo;

    @Autowired
    ManuScriptFileRepo manuScriptFileRepo;

    @Autowired
    ManuscriptReviewerCommentsRepo manuscriptReviewerCommentsRepo;

    @Autowired
    ManuscriptEditorCommentsRepo manuscriptEditorCommentsRepo;

    @Autowired
    UserLoginRepo userLoginRepo;

    @Autowired
    UserJournalRepo userJournalRepo;

    @Autowired
    ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ManuScriptKeywordsRepo manuScriptKeywordsRepo;

    @Autowired
    ManuscriptEditorCommentRepo manuscriptEditorCommentRepo;

    @Autowired
    KeywordsRepo keywordsRepo;

    @Autowired
    ManuscriptEditorRepo manuscriptEditorRepo;

    @Autowired
    UserInvoiceRepo userInvoiceRepo;

    @Autowired
    ManuscriptRatingRepo manuscriptRatingRepo;

    @Autowired
    StatusMasterRepo statusMasterRepo;

    @Autowired
    ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;

    @Autowired
    ManuscriptJournalSubjectRepo manuscriptJournalSubjectRepo;

    @Autowired
    AuthoritiesHistoryRepo authoritiesHistoryRepo;

    @Autowired
    BundleTrackerRepo bundleTrackerRepo;

    @Override
    public CommentVO getAllComment(IDVO id) {

        CommentVO commentVO = new CommentVO();

        List<ManuscriptFileVO> fileVo = new ArrayList<ManuscriptFileVO>();
        List<ManuscriptReviewerCommentsVO> reviewerCommentVo = new ArrayList<>();
        List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsVO = new ArrayList<>();
        List<ManuscriptEditorCommentsVO> manuscriptCommentsToAuthorVO = new ArrayList<>();

        List<ManuscriptReviewerCommentsVO> manuscriptReviewerCommentsForAllVersionVO = new ArrayList<>();
        List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForAllVersionVO = new ArrayList<>();
        List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForAllVersionVORFSend = new ArrayList<>();

        List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForFinalRecommandation = new ArrayList<>();

        List<UserVO> userVO = new ArrayList<UserVO>();
        List<KeywordsVO> keywordVO = new ArrayList<KeywordsVO>();
        List<ManuscriptFileVO> previousVerisonManuscriptFileVO = new ArrayList<>();
        List<FileVo> reviewerListFile = new ArrayList<>();
        List<ManuScriptTrackVO> manuscriptNames = new ArrayList<>();

        List<ManuScript> manuscript = manuScriptRepo.getManuScripByVersion(id.getId(), id.getVersion());
        List<ManuScript> commentaryIds = new ArrayList<>();
        if (manuscript != null && manuscript.get(0) != null && StringUtils
                .equalsIgnoreCase(manuscript.get(0).getManuscriptTypeDetails().getManuscriptType(), "commentary")) {
            ManuScript manuScript = manuScriptRepo.getById(manuscript.get(0).getCommentaryId());
            if (null != manuScript)
                commentaryIds.add(manuScript);
        } else
            commentaryIds = manuScriptRepo.getCommentaryIds(id.getId());

        if (commentaryIds.size() > 0) {
            commentaryIds.stream().forEach(obj -> {
                ManuScriptTrackVO vo = new ManuScriptTrackVO();
                vo.setManuScriptId(obj.getManuScriptId());
                vo.setManuScriptTitle(obj.getManuscriptTitle());
                vo.setManuscriptCode(obj.getManuscriptCode());
                vo.setVersion(obj.getManuscriptVersion());
                vo.setAuthorId(obj.getAuthorId());
                manuscriptNames.add(vo);
            });
        }


        if (manuscript != null && !manuscript.isEmpty()) {
            ManuscriptListVO mv = ManuscriptTransformationUtil.mapManuscriptModelToManuscriptVO(manuscript.get(0));
            mv.setCommentaryId(manuscriptNames);

            if (manuscript != null && manuscript.size() > 0) {
                if (manuscript.get(0).getLteManuscriptId() != null && manuscript.get(0).getLteManuscriptId() != -1) {
                    ManuScript manuScript = manuScriptRepo.getById(manuscript.get(0).getLteManuscriptId());
                    ManuScriptTrackVO vo = new ManuScriptTrackVO();
                    vo.setManuScriptId(manuScript.getManuScriptId());
                    vo.setManuScriptTitle(manuScript.getManuscriptTitle());
                    vo.setManuscriptCode(manuScript.getManuscriptCode());
                    vo.setVersion(manuScript.getManuscriptVersion());
                    vo.setAuthorId(manuScript.getAuthorId());
                    mv.setLteManuscriptId(vo);
                } else
                    mv.setLteManuscriptId(null);
            }
            commentVO.setManuscriptListVO(mv);

            if (id.getEdiotrialDashboard() != null && !id.getEdiotrialDashboard().isEmpty()
                    && id.getEdiotrialDashboard().equalsIgnoreCase("Yes")) {

                List<ManuScriptFile> manuscriptFileAllVersion = manuScriptFileRepo
                        .getAllVersionFileManuscriptByactive(id.getId());
                List<BundleTracker> manuscriptStatus = bundleTrackerRepo.manuscriptFailedStatus(id.getId());
                List<ManuScriptFile> manuscriptFile = new ArrayList<>();
                if (manuscriptStatus != null && manuscriptStatus.size() > 0) {
                    manuscriptFile = manuscriptFileAllVersion.stream().filter(i -> !((i.getFileName().equalsIgnoreCase("Bundled.pdf") ||
                            i.getFileName().equalsIgnoreCase("Bundled.docx") || i.getFileName().equalsIgnoreCase("Bundled.doc") || i.getFileName().equalsIgnoreCase("BundledPdfForReviewer.pdf")) && i.getIsActive().equals(1))).collect(Collectors.toList());
                } else {
                    manuscriptFile = manuscriptFileAllVersion;
                }
                if (manuscriptFile != null && !manuscriptFile.isEmpty()) {
                    for (ManuScriptFile i : manuscriptFile) {
                        if (id.getVersion() != null && id.getVersion().equals(0)) {
                            if (i.getIsActive() != null && i.getIsActive().equals(0)) {
                                ManuscriptFileVO manuscriptFileVO = manuscriptFile(i);
                                Date uploadedDate = getUploadedDate(manuscriptFileVO.getFilePath());
                                if(uploadedDate!=null) {
                                	manuscriptFileVO.setCreatedOn(uploadedDate);
                                }
                                previousVerisonManuscriptFileVO.add(manuscriptFileVO);
                            }
                        }

                        if (i.getFileVersion() != id.getVersion()) {
                            ManuscriptFileVO manuscriptFileVO = manuscriptFile(i);
                            Date uploadedDate = getUploadedDate(manuscriptFileVO.getFilePath());
                            if(uploadedDate!=null) {
                            	manuscriptFileVO.setCreatedOn(uploadedDate);
                            	manuscriptFileVO.setUpdatedOn(uploadedDate);
                            }
                            previousVerisonManuscriptFileVO.add(manuscriptFileVO);
                        } else if (i.getFileVersion() == id.getVersion() && i.getIsActive() == 1) {
                            ManuscriptFileVO manuscriptFileVO = manuscriptFile(i);
                            Date uploadedDate = getUploadedDate(manuscriptFileVO.getFilePath());
                            if(uploadedDate!=null) {
                            	manuscriptFileVO.setCreatedOn(uploadedDate);
                            	manuscriptFileVO.setUpdatedOn(uploadedDate);
                            }
                            fileVo.add(manuscriptFileVO);
                        }
                    }
                    commentVO.setManuscriptFileVO(fileVo);
                    commentVO.setPreviousVerisonManuscriptFileVO(previousVerisonManuscriptFileVO);
                }

                List<ManuscriptEditor> getmanuscriptEditorData = manuscriptEditorRepo
                        .getAllAssignedEdiorsOnManuscript(id.getId(), id.getVersion());
                List<IDVO> editorsNameList = new ArrayList<>();
                if (getmanuscriptEditorData != null && !getmanuscriptEditorData.isEmpty()) {
                    getmanuscriptEditorData.stream().forEach(i -> {
                        IDVO add = getEditorData(i);
                        editorsNameList.add(add);

                    });
                    commentVO.setEditorsNameList(editorsNameList);
                }

                commentVO.setArticleStatus(manuscript.get(0).getStatusMaster().getStatusValue());

                List<UserJournal> getAllUserJournal = userJournalRepo
                        .getEICJournalList(manuscript.get(0).getJournalId());
                List<IDVO> managingEditorNameList = new ArrayList<>();
                if (getAllUserJournal != null && !getAllUserJournal.isEmpty()) {
                    getAllUserJournal.stream().forEach(i -> {
                        IDVO add = new IDVO();
                        add.setUserId(i.getUser().getId());
                        add.setEmail(i.getUser().getEmail());
                        add.setName(i.getUser().getSalutation() + "." + i.getUser().getFirstName() + " "
                                + i.getUser().getLastName());
                        add.setSalutation(i.getUser().getSalutation().toString());
                        add.setFirstName(i.getUser().getFirstName());
                        add.setMiddleName(i.getUser().getMiddleName());
                        add.setLastName(i.getUser().getLastName());
                        add.setManagingEditorAssignDate(manuscript.get(0).getSubmissionDate());
                        add.setManagingEditoDecisionDate(manuscript.get(0).getFinalDecisionDate());
                        managingEditorNameList.add(add);
                    });
                    commentVO.setManagingEditorNameList(managingEditorNameList);
                }

            } else {
                // ManuscriptFile
                List<ManuScriptFile> manuscriptFile = manuScriptFileRepo.getManuscriptByactive(id.getId(),
                        id.getVersion(), 1);
                if (manuscriptFile != null && !manuscriptFile.isEmpty()) {
                    for (ManuScriptFile mf : manuscriptFile) {
                        ManuscriptFileVO manuscriptFileVO = manuscriptFile(mf);
                        Date uploadedDate = getUploadedDate(manuscriptFileVO.getFilePath());
                        if(uploadedDate!=null) {
                        	manuscriptFileVO.setCreatedOn(uploadedDate);
                        	manuscriptFileVO.setUpdatedOn(uploadedDate);
                        }
                        fileVo.add(manuscriptFileVO);
                    }
                    commentVO.setManuscriptFileVO(fileVo);
                }
            }
            // Add Co Author
            List<ManuScriptCoAuthor> getManuScriptCoAuthorList = manuScriptCoAuthorRepo
                    .sendEmailToCoAuthorOnManuscriptList(id.getId());
            List<ManuscriptCoAuthorVO> manuscriptCoAuthorVOList = new ArrayList<>();
            if (getManuScriptCoAuthorList != null && !getManuScriptCoAuthorList.isEmpty()) {
                getManuScriptCoAuthorList.stream().forEach(i -> {
                    ManuscriptCoAuthorVO addManuScriptCoAuthor = coAuthorData(i, manuscript.get(0));

                    manuscriptCoAuthorVOList.add(addManuScriptCoAuthor);
                });
            }
            Collections.sort(manuscriptCoAuthorVOList, (v1, v2) -> v1.getOrder().compareTo(v2.getOrder()));
            commentVO.setManuscriptCoAuthorVOList(manuscriptCoAuthorVOList);

            // Co-Author Detail
            List<Integer> coAuthorId = new ArrayList<>();
            if (getManuScriptCoAuthorList != null && !getManuScriptCoAuthorList.isEmpty()) {
                for (ManuScriptCoAuthor coAuthor : getManuScriptCoAuthorList) {
                    coAuthorId.add(coAuthor.getCoAuthorId());
                }
                if (coAuthorId != null && !coAuthorId.isEmpty()) {
                    List<User> userDetails = userRepo.getAllActiveUserByUserList(coAuthorId);
                    userVO = UserTransformationalUtils.mapUserListToUserVOList(userDetails);

                }
            }
            commentVO.setCoAuthorVO(userVO);

            // ManuscriptJournal Subject
            List<ManuscriptJournalSubject> i = manuscriptJournalSubjectRepo.findByManuscriptId(id.getId(), 1);
            if (i != null && !i.isEmpty()) {
                List<ManuscriptJournalSubjectVo> p = new ArrayList<>();
                i.stream().forEach(j -> {
                    ManuscriptJournalSubjectVo k = new ManuscriptJournalSubjectVo();
                    k.setId(j.getId());
                    k.setManuscriptId(j.getManuscriptId());
                    k.setManuscriptVersion(j.getManuscriptVersion());
                    k.setJournalId(j.getJournalId());
                    k.setJournalSubjectName(j.getJournalSubject().getJournalSubject());
                    k.setIsActive(j.getIsActive());
                    k.setCreatedAt(j.getCreatedAt());
                    p.add(k);
                });
                commentVO.setManuscriptJournalSubject(p);
            }

            // ManuscriptEIC/EditorComment

            // Added code by Utkarsh

            if (id.getRoleId() == 1) {
                List<AuthoritiesHistory> authoritiesHistories = authoritiesHistoryRepo.getByManuscriptId(id.getId());
                if (!authoritiesHistories.isEmpty()) {
                    for (AuthoritiesHistory authoritiesHistory : authoritiesHistories) {
                        if (authoritiesHistory.getManuscriptStatusId() != 72
                                && authoritiesHistory.getManuscriptStatusId() != 73
                                && authoritiesHistory.getManuscriptStatusId() != 8
                                && authoritiesHistory.getManuscriptStatusId() != 2
                                && authoritiesHistory.getManuscriptStatusId() != 50
                                && authoritiesHistory.getManuscriptStatusId() != 26
                                && authoritiesHistory.getManuscriptStatusId() != 51
                                && authoritiesHistory.getManuscriptStatusId() != 52
                                && authoritiesHistory.getManuscriptStatusId() != 68
                                && authoritiesHistory.getManuscriptStatusId() != 66
                                && authoritiesHistory.getManuscriptStatusId() != null) {
                            if (authoritiesHistory.getPreviousUserRole() != null) {
                                if (authoritiesHistory.getPreviousUserRole() == 2 || authoritiesHistory.getPreviousUserRole() == 3) {
                                    ManuscriptEditorCommentsVO mecvo = getCommentByEditorAndEIC(authoritiesHistory);
                                    manuscriptEditorCommentsVO.add(mecvo);
                                    manuscriptEditorCommentsForAllVersionVO.add(mecvo);
                                    if (authoritiesHistory.getCommentsToAuthor() != null
                                            && !authoritiesHistory.getCommentsToAuthor().isEmpty()) {
                                        manuscriptCommentsToAuthorVO.add(mecvo);

                                    }
                                }
                            }
                        }
                    }
                }

                commentVO.setManuscriptEditorCommentsVO(manuscriptEditorCommentsVO);
                commentVO.setManuscriptEditorCommentsForAllVersionVO(manuscriptEditorCommentsForAllVersionVO);
                commentVO.setManuscriptCommentsToAuthorVO(manuscriptCommentsToAuthorVO);
            } else if (id.getRoleId() == 2) {
                List<AuthoritiesHistory> authoritiesHistories = authoritiesHistoryRepo.getByManuscriptId(id.getId());
                if (!authoritiesHistories.isEmpty()) {
                    for (AuthoritiesHistory authoritiesHistory : authoritiesHistories) {
                        if (authoritiesHistory.getManuscriptStatusId() != 72
                                && authoritiesHistory.getManuscriptStatusId() != 73
                                && authoritiesHistory.getManuscriptStatusId() != 8
                                && authoritiesHistory.getManuscriptStatusId() != 2
                                && authoritiesHistory.getManuscriptStatusId() != 50
                                && authoritiesHistory.getManuscriptStatusId() != 26
                                && authoritiesHistory.getManuscriptStatusId() != 51
                                && authoritiesHistory.getManuscriptStatusId() != 52
                                && authoritiesHistory.getManuscriptStatusId() != 68
                                && authoritiesHistory.getManuscriptStatusId() != 66
                                && authoritiesHistory.getManuscriptStatusId() != null) {
                            if (authoritiesHistory.getPreviousUserRole() != null) {
                                if (authoritiesHistory.getPreviousUserRole() == 2 || authoritiesHistory.getPreviousUserRole() == 3) {
                                    ManuscriptEditorCommentsVO mecvo = getCommentByEditorAndEIC(authoritiesHistory);
                                    manuscriptEditorCommentsVO.add(mecvo);
                                    manuscriptEditorCommentsForAllVersionVO.add(mecvo);
                                    if (authoritiesHistory.getCommentsToAuthor() != null
                                            && !authoritiesHistory.getCommentsToAuthor().isEmpty()) {
                                        manuscriptCommentsToAuthorVO.add(mecvo);

                                    }
                                }
                            }
                        }
                    }
                }

                commentVO.setManuscriptEditorCommentsVO(manuscriptEditorCommentsVO);
                commentVO.setManuscriptEditorCommentsForAllVersionVO(manuscriptEditorCommentsForAllVersionVO);
                commentVO.setManuscriptCommentsToAuthorVO(manuscriptCommentsToAuthorVO);
            } else if (id.getRoleId() == 3) {
                if (id.getUserId() != null && id.getUserId() != 0) {
                    id.setEditor_id(id.getUserId());
                    List<AuthoritiesHistory> authoritiesHistories = authoritiesHistoryRepo.getByManuscriptId(id.getId());
                    if (!authoritiesHistories.isEmpty()) {
                        for (AuthoritiesHistory authoritiesHistory : authoritiesHistories) {
                            if (authoritiesHistory.getCommentsToAuthor() != null
                                    && !authoritiesHistory.getCommentsToAuthor().isEmpty()
                                    && authoritiesHistory.getManuscriptStatusId() != 72
                                    && authoritiesHistory.getManuscriptStatusId() != 73
                                    && authoritiesHistory.getManuscriptStatusId() != 8
                                    && authoritiesHistory.getManuscriptStatusId() != 2
                                    && authoritiesHistory.getManuscriptStatusId() != 50
                                    && authoritiesHistory.getManuscriptStatusId() != 26
                                    && authoritiesHistory.getManuscriptStatusId() != 51
                                    && authoritiesHistory.getManuscriptStatusId() != 52
                                    && authoritiesHistory.getManuscriptStatusId() != 68
                                    && authoritiesHistory.getManuscriptStatusId() != 66
                                    && authoritiesHistory.getManuscriptStatusId() != null) {
                                if (authoritiesHistory.getPreviousUserRole() != null) {
                                    if (authoritiesHistory.getPreviousUserRole() == 2 || authoritiesHistory.getPreviousUserRole() == 3) {
                                        ManuscriptEditorCommentsVO mecvo = getCommentByEditorAndEIC(authoritiesHistory);
                                        manuscriptCommentsToAuthorVO.add(mecvo);
                                    }
                                }
                            }
                        }
                    }
                }
                List<AuthoritiesHistory> authoritiesHistories = authoritiesHistoryRepo
                        .getByManuscriptIdAndRoleIdAndPreviousUserIdEditor(id.getId(),
                                id.getEditor_id());
                List<AuthoritiesHistory> authoritiesHistories1 = authoritiesHistoryRepo
                        .getByManuscriptIdAndRoleIdEIC(id.getId(),
                                id.getEditor_id());
                List<AuthoritiesHistory> authoritiesHistories2 = authoritiesHistoryRepo
                        .getByManuscriptIdEditorToEditor(id.getId(), id.getEditor_id());


                authoritiesHistories.addAll(authoritiesHistories1);

                authoritiesHistories.addAll(authoritiesHistories2);
                authoritiesHistories = authoritiesHistories
                        .stream()
                        .sorted(Comparator.comparingInt(AuthoritiesHistory::getId).reversed())
                        .collect(Collectors.toList());
                if (!authoritiesHistories.isEmpty()) {
                    for (AuthoritiesHistory authoritiesHistory : authoritiesHistories) {
                        if (authoritiesHistory.getManuscriptStatusId() != 72
                                && authoritiesHistory.getManuscriptStatusId() != 73
                                && authoritiesHistory.getManuscriptStatusId() != 8
                                && authoritiesHistory.getManuscriptStatusId() != 2
                                && authoritiesHistory.getManuscriptStatusId() != 50
                                && authoritiesHistory.getManuscriptStatusId() != 26
                                && authoritiesHistory.getManuscriptStatusId() != 51
                                && authoritiesHistory.getManuscriptStatusId() != 52
                                && authoritiesHistory.getManuscriptStatusId() != 68
                                && authoritiesHistory.getManuscriptStatusId() != 66
                                && authoritiesHistory.getManuscriptStatusId() != null) {
                            if (authoritiesHistory.getPreviousUserRole() != null) {
                                if (authoritiesHistory.getPreviousUserRole() == 2 || authoritiesHistory.getPreviousUserRole() == 3) {
                                    ManuscriptEditorCommentsVO mecvo = getCommentByEditorAndEIC(authoritiesHistory);
                                    manuscriptEditorCommentsVO.add(mecvo);
                                    manuscriptEditorCommentsForAllVersionVO.add(mecvo);
                                }
                            }
                        }
                    }
                }

                commentVO.setManuscriptEditorCommentsVO(manuscriptEditorCommentsVO);
                commentVO.setManuscriptEditorCommentsForAllVersionVO(manuscriptEditorCommentsForAllVersionVO);
                commentVO.setManuscriptCommentsToAuthorVO(manuscriptCommentsToAuthorVO);
            } else if (id.getRoleId() == 4) {
                List<AuthoritiesHistory> authoritiesHistories = authoritiesHistoryRepo.getByManuscriptId(id.getId());
                if (!authoritiesHistories.isEmpty()) {
                    for (AuthoritiesHistory authoritiesHistory : authoritiesHistories) {
                        if (authoritiesHistory.getManuscriptStatusId() != null
                                && authoritiesHistory.getManuscriptStatusId() == 46) {
                            if (authoritiesHistory.getPreviousUserRole() != null) {
                                if (authoritiesHistory.getPreviousUserRole() == 2 || authoritiesHistory.getPreviousUserRole() == 3) {
                                    ManuscriptEditorCommentsVO mecvo = getCommentByEditorAndEIC(authoritiesHistory);
                                    manuscriptEditorCommentsVO.add(mecvo);
                                    manuscriptEditorCommentsForAllVersionVO.add(mecvo);
//                                    manuscriptCommentsToAuthorVO.add(mecvo);
                                }
                            }
                        }
                    }
                }

                commentVO.setManuscriptEditorCommentsVO(manuscriptEditorCommentsVO);
                commentVO.setManuscriptEditorCommentsForAllVersionVO(manuscriptEditorCommentsForAllVersionVO);
//                commentVO.setManuscriptCommentsToAuthorVO(manuscriptCommentsToAuthorVO);

            }


            // Setting manuscriptEditorCommentsForAllVersionVORFSend for embed comment section
//            List<ManuscriptEditorCommentsVO> BlanckVersionVORFSend = new ArrayList<>();
//            List<ManuscriptEditorCommentsVO> manuscriptEditorForAllVersion1 = null;
//            if (id.getRoleId() == 3  & id.getRoleId() != null) {
//                manuscriptEditorForAllVersion1 = manuscriptEditorRepo.getManuscriptEditor(id.getId());
//            }else
//            {
//                manuscriptEditorForAllVersion1 = manuscriptEditorRepo.getManuscriptEditor(id.getId());
//            }
//
//				if (manuscriptEditorForAllVersion1 != null && !manuscriptEditorForAllVersion1.isEmpty()) {
//				for (ManuscriptEditor mec1 : manuscriptEditorForAllVersion1) {
//					if (mec1.getManuscriptStatus()!=50 && mec1.getManuscriptStatus()!=26 && mec1.getManuscriptStatus() != 51 || mec1.getManuscriptStatus() != 52 || mec1.getManuscriptStatus() != 4 &&  mec1.getManuscriptStatus() != null) {
//						ManuscriptEditorCommentsVO mecv1 = getEditorComment(mec1);
//						manuscriptEditorCommentsForAllVersionVORFSend.add(mecv1);
//					}
//				}
//				commentVO.setManuscriptEditorCommentsForAllVersionVORFSend(manuscriptEditorCommentsForAllVersionVORFSend);
//			}
//				else {
//					commentVO.setManuscriptEditorCommentsForAllVersionVORFSend(BlanckVersionVORFSend);
//				}

            List<ManuscriptEditorCommentsVO> BlanckVersionVORFSend = new ArrayList<>();
//            if (id.getEditor_id()==null && id.getUserId() != null && id.getUserId() != 0) {
//                id.setEditor_id(id.getUserId());
//            }
            List<AuthoritiesHistory> authoritiesHistoriesVORFSend = authoritiesHistoryRepo.getByManuscriptId(id.getId());
            if (!authoritiesHistoriesVORFSend.isEmpty()) {
                for (AuthoritiesHistory authoritiesHistory : authoritiesHistoriesVORFSend) {
                    if (authoritiesHistory.getManuscriptStatusId() != null) {
                        if (authoritiesHistory.getPreviousUserRole() != null) {
                            if (authoritiesHistory.getPreviousUserRole() == 2 || authoritiesHistory.getPreviousUserRole() == 3) {
                                ManuscriptEditorCommentsVO mecvo = getCommentByEditorAndEIC(authoritiesHistory);
                                if (mecvo.getCommentsForAuthor() != null) {
                                    mecvo.setCommentsForAuthor(null);
                                }
                                manuscriptEditorCommentsForAllVersionVORFSend.add(mecvo);
                            }
                        }
                    }
                }
            }

            if (!manuscriptEditorCommentsForAllVersionVORFSend.isEmpty()) {
                commentVO.setManuscriptEditorCommentsForAllVersionVORFSend(manuscriptEditorCommentsForAllVersionVORFSend);
            } else {
                commentVO.setManuscriptEditorCommentsForAllVersionVORFSend(BlanckVersionVORFSend);
            }


            // ManuscriptReviewerCommet
            List<ManuScriptReviewer> manuScriptReviewerListForAllVersion = manuScriptReviewerRepo
                    .getByManuscriptId(id.getId());

            List<AuthoritiesHistory> reviewersList = new ArrayList<>();
            List<AuthoritiesHistory> reviewersAcceptedList = new ArrayList<>();

            if (id.getRoleId() == 2) {
                reviewersList = authoritiesHistoryRepo.getReviewersRemoved(id.getId());
                reviewersAcceptedList = authoritiesHistoryRepo.getReviewersAccepted(id.getId());

            } else if (id.getRoleId() == 3) {

                reviewersList = authoritiesHistoryRepo.getReviewersRemovedForEditor(id.getId(), id.getEditor_id());
                reviewersAcceptedList = authoritiesHistoryRepo.getReviewersAcceptedForEditor(id.getId(), id.getEditor_id());
            }
            List<Integer> reviewersAcceptedNames = reviewersAcceptedList.stream().map(AuthoritiesHistory::getPreviousUserId).collect(Collectors.toList());
            List<Integer> reviewersRemoved = reviewersList.stream().map(AuthoritiesHistory::getPreviousUserId).collect(Collectors.toList());
            List<IDVO> ReviewerNameList = new ArrayList<>();
            if (manuScriptReviewerListForAllVersion != null && !manuScriptReviewerListForAllVersion.isEmpty()) {
                for (ManuScriptReviewer mrc : manuScriptReviewerListForAllVersion) {
                    if (!reviewersRemoved.contains(mrc.getReviewerId())) {
                        ManuscriptReviewerCommentsVO mrcVO = reviewercommentData(mrc);
                        manuscriptReviewerCommentsForAllVersionVO.add(mrcVO);
                        if (mrc.getManuscriptId().equals(id.getId())) {
//							&& mrc.getManuscriptVersion().equals(id.getVersion())) {
                            reviewerCommentVo.add(mrcVO);
                        }

                        if (mrc.getReviewerFileName() != null && !mrc.getReviewerFileName().isEmpty()
                                && mrc.getReviewerFilePath() != null && !mrc.getReviewerFilePath().isEmpty()) {
                            FileVo a = reviewerFile(mrc);
                            
                            String uploadedDate = getUploadedDateFromPath(mrc.getReviewerFilePath());
                            a.setReviewerUploadedDate(uploadedDate);
                            
                            reviewerListFile.add(a);
                        }

                        if (mrc.getManuscriptId().equals(id.getId()) && mrc.getManuscriptVersion().equals(id.getVersion())) {
                            IDVO add = getReviewerData(mrc);
                            ReviewerNameList.add(add);
                        }
                    }else if(reviewersRemoved.contains(mrc.getReviewerId())) {
                        if (mrc.getManuscriptId().equals(id.getId()) && mrc.getManuscriptVersion().equals(id.getVersion())) {
                            IDVO add = getReviewerData(mrc);
                            ReviewerNameList.add(add);
                        }
                        
                        if (reviewersAcceptedNames.contains(mrc.getReviewerId())
                                && (mrc.getManuscriptStatus() == 12 || mrc.getManuscriptStatus() == 13
                                 || mrc.getManuscriptStatus() == 14 || mrc.getManuscriptStatus() == 15)) {

                            ManuscriptReviewerCommentsVO mrcVO = reviewercommentData(mrc);
                            manuscriptReviewerCommentsForAllVersionVO.add(mrcVO);
                            if (mrc.getManuscriptId().equals(id.getId())) {
                                reviewerCommentVo.add(mrcVO);
                            }

                            if (mrc.getReviewerFileName() != null && !mrc.getReviewerFileName().isEmpty()
                                    && mrc.getReviewerFilePath() != null && !mrc.getReviewerFilePath().isEmpty()) {
                                FileVo a = reviewerFile(mrc);

                                String uploadedDate = getUploadedDateFromPath(mrc.getReviewerFilePath());
                                a.setReviewerUploadedDate(uploadedDate);

                                reviewerListFile.add(a);
                            }

                            if (mrc.getManuscriptId().equals(id.getId()) && mrc.getManuscriptVersion().equals(id.getVersion())) {
                                IDVO add = getReviewerData(mrc);
                                ReviewerNameList.add(add);
                            }
                        }
                    }
                    
                    else if (reviewersRemoved.contains(mrc.getReviewerId()) && reviewersAcceptedNames.contains(mrc.getReviewerId())) {
                        if (mrc.getManuscriptStatus() == 12 || mrc.getManuscriptStatus() == 13
                                || mrc.getManuscriptStatus() == 14 || mrc.getManuscriptStatus() == 15) {
                            ManuscriptReviewerCommentsVO mrcVO = reviewercommentData(mrc);
                            manuscriptReviewerCommentsForAllVersionVO.add(mrcVO);
                            if (mrc.getManuscriptId().equals(id.getId())) {
//							&& mrc.getManuscriptVersion().equals(id.getVersion())) {
                                reviewerCommentVo.add(mrcVO);
                            }

                            if (mrc.getReviewerFileName() != null && !mrc.getReviewerFileName().isEmpty()
                                    && mrc.getReviewerFilePath() != null && !mrc.getReviewerFilePath().isEmpty()) {
                                FileVo a = reviewerFile(mrc);
                                
                                String uploadedDate = getUploadedDateFromPath(mrc.getReviewerFilePath());
                                a.setReviewerUploadedDate(uploadedDate);
                                
                                reviewerListFile.add(a);
                            }

                            if (mrc.getManuscriptId().equals(id.getId()) && mrc.getManuscriptVersion().equals(id.getVersion())) {
                                IDVO add = getReviewerData(mrc);
                                ReviewerNameList.add(add);
                            }
                        }
                    }
                }
                if (id.getRoleId() == 2) {
                    List<AuthoritiesHistory> authoritiesHistories = authoritiesHistoryRepo.getByManuscriptId(id.getId());

                    for (AuthoritiesHistory authoritiesHistory : authoritiesHistories) {
                        if (authoritiesHistory.getPreviousUserRole() != null) {
                            if (authoritiesHistory.getPreviousUserRole() == 5 && authoritiesHistory.getManuscriptStatusId() == 67) {
                                ManuscriptReviewerCommentsVO mecvo = getReviewerCommentsByAuth(authoritiesHistory);
                                manuscriptReviewerCommentsForAllVersionVO.add(mecvo);
                                reviewerCommentVo.add(mecvo);
                            }
                        }
                    }


                } else if (id.getRoleId() == 3) {
                    List<AuthoritiesHistory> authoritiesHistories = authoritiesHistoryRepo
                            .getAuthorityHistoryForReviewerAndEditor(id.getId(), 67, id.getEditor_id());

                    for (AuthoritiesHistory authoritiesHistory : authoritiesHistories) {
                        if (authoritiesHistory.getPreviousUserRole() != null) {
                            if (authoritiesHistory.getPreviousUserRole() == 5) {
                                ManuscriptReviewerCommentsVO mecvo = getReviewerCommentsByAuth(authoritiesHistory);
                                manuscriptReviewerCommentsForAllVersionVO.add(mecvo);
                                reviewerCommentVo.add(mecvo);
                            }
                        }
                    }
                }
                commentVO.setManuscriptReviewerCommentsVO(reviewerCommentVo);
                commentVO.setManuscriptReviewerCommentsForAllVersionVO(manuscriptReviewerCommentsForAllVersionVO);
                commentVO.setReviewerListFile(reviewerListFile);
                commentVO.setReviewerNameList(ReviewerNameList);

            }


            // ManuscriptEditorComment
//			List<ManuscriptEditorCommentsVO> BlankManuscriptEditorCommentsVO = new ArrayList<>();
//			List<ManuscriptEditor> manuscriptEditor = manuscriptEditorRepo.getByVersion(id.getId(), id.getVersion());
//			if (manuscriptEditor != null && !manuscriptEditor.isEmpty()) {
//				for (ManuscriptEditor mec : manuscriptEditor) {
//					ManuscriptEditorCommentsVO mecv = getEditorComment(mec);
//					manuscriptEditorCommentsVO.add(mecv);
//				}
//				commentVO.setManuscriptEditorCommentsVO(manuscriptEditorCommentsVO);
//			}
            List<ManuscriptEditor> manuscriptEditorForAllVersion = null;
            if (id.getRoleId() == 3 && id.getRoleId() != null) {
                manuscriptEditorForAllVersion = manuscriptEditorRepo.getManuscriptEditorOnly(id.getId(),
                        id.getEditor_id());
            } else {
                manuscriptEditorForAllVersion = manuscriptEditorRepo.getManuscriptEditor(id.getId());
            }

//			if (manuscriptEditorForAllVersion != null && !manuscriptEditorForAllVersion.isEmpty()) {
//				for (ManuscriptEditor mec : manuscriptEditorForAllVersion) {
//					if (mec.getManuscriptStatus() != 51 && mec.getManuscriptStatus() != 52 && mec.getManuscriptStatus() != 68
//							&& mec.getManuscriptStatus() != null) {
//						ManuscriptEditorCommentsVO mecv = getEditorComment(mec);
//						manuscriptEditorCommentsForAllVersionVO.add(mecv);
//					}
//
//				}
//				commentVO.setManuscriptEditorCommentsForAllVersionVO(manuscriptEditorCommentsForAllVersionVO);
//			}
//			else {
//				commentVO.setManuscriptEditorCommentsForAllVersionVO(BlankManuscriptEditorCommentsVO);
//			}
//

            // Getting final recommendation comments



            if (id.getRoleId() == 2 || id.getRoleId()==1) {
                if (manuscriptEditorForAllVersion != null && !manuscriptEditorForAllVersion.isEmpty()) {
//                List<ManuscriptEditor> finalRecommendation = manuscriptEditorForAllVersion.stream().filter(manu -> (manu.getManuscriptStatus().equals(52) || manu.getManuscriptStatus().equals(51))).collect(Collectors.toList());
                    List<AuthoritiesHistory> finalRecommendation = authoritiesHistoryRepo.getByManuscriptId(id.getId());
//                for (ManuscriptEditor mec1 : finalRecommendation) {
                    for (AuthoritiesHistory mec1 : finalRecommendation) {
//                    ManuscriptEditorCommentsVO mecv = getEditorComment(mec1);
                        if (mec1.getManuscriptStatusId() == 52 || mec1.getManuscriptStatusId() == 51) {
                            if (mec1.getPreviousUserRole() != null) {
                                if (mec1.getPreviousUserRole() == 2 || mec1.getPreviousUserRole() == 3) {
                                    ManuscriptEditorCommentsVO mecv = getCommentByEditorAndEIC(mec1);
                                    manuscriptEditorCommentsForFinalRecommandation.add(mecv);
                                }
                            }
                        }
                    }
//                    List<AuthoritiesHistory> finalRecommedentationHistoryData = authoritiesHistoryRepo.getAuthoritiesHistoryByStatusIdMultiple(id.getId(), 68);
                    List<AuthoritiesHistory> finalRecommedentationHistoryData = authoritiesHistoryRepo.getAuthoritiesHistoryByStatusIdAcceptRejectRevision(id.getId());

                    List<ManuScriptFile> finalRevisionFiles = manuScriptFileRepo.getManuscriptFileByStatus(id.getId(), "FINAL REVISION");



                    if (finalRecommedentationHistoryData != null && !finalRecommedentationHistoryData.isEmpty() && finalRevisionFiles != null && !finalRevisionFiles.isEmpty()) {
                        int fileCount = 0;
                        for (AuthoritiesHistory manuEditor : finalRecommedentationHistoryData) {

                            if(manuEditor.getManuscriptStatusId()==51 || manuEditor.getManuscriptStatusId()==52)
                            {

                                ManuscriptEditorCommentsVO mecv =
                                        manuscriptEditorCommentsForFinalRecommandation.stream()
                                                .filter(manuscriptEditorCommentsVO1 -> manuscriptEditorCommentsVO1.getId().equals(manuEditor.getId()))
                                                .findFirst().get();
                                int indexOfOldObject= manuscriptEditorCommentsForFinalRecommandation.indexOf(mecv);
                                if (fileCount < finalRevisionFiles.size()) {
                                    mecv = getFinalRecommedentationEditorCommentWithFileForAcceptReject(manuEditor, finalRevisionFiles.get(fileCount), mecv);
                                } else {
                                    mecv = getFinalRecommedentationEditorCommentWithFileForAcceptReject(manuEditor, null, mecv);
                                }
                                manuscriptEditorCommentsForFinalRecommandation.set(indexOfOldObject,mecv);
                            } else{
                                ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
                                if (fileCount < finalRevisionFiles.size()) {
                                    mecv = getFinalRecommedentationEditorCommentWithFile(manuEditor, finalRevisionFiles.get(fileCount));
                                } else {
                                    mecv = getFinalRecommedentationEditorCommentWithFile(manuEditor, null);
                                }
                                manuscriptEditorCommentsForFinalRecommandation.add(mecv);
                            }

                            manuscriptEditorCommentsForFinalRecommandation = manuscriptEditorCommentsForFinalRecommandation
                                    .stream()
                                    .sorted(Comparator.comparingInt(ManuscriptEditorCommentsVO::getId).reversed())
                                    .collect(Collectors.toList());
                            fileCount++;
                        }
                    } else if (finalRecommedentationHistoryData != null && !finalRecommedentationHistoryData.isEmpty()) {
                        for (AuthoritiesHistory manuEditor : finalRecommedentationHistoryData) {
                            if(!manuEditor.getManuscriptStatusId().equals(51)
                                    && !manuEditor.getManuscriptStatusId().equals(52)) {
                                ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
                                mecv = getCommentByEditorAndEIC(manuEditor);
                                manuscriptEditorCommentsForFinalRecommandation.add(mecv);
                                manuscriptEditorCommentsForFinalRecommandation = manuscriptEditorCommentsForFinalRecommandation
                                        .stream()
                                        .sorted(Comparator.comparingInt(ManuscriptEditorCommentsVO::getId).reversed())

                                        .collect(Collectors.toList());
                            }
                        }
                    }

                    commentVO.setManuscriptEditorCommentsForFinalRecommandation(
                            manuscriptEditorCommentsForFinalRecommandation);
                } else {
                    commentVO.setManuscriptEditorCommentsForFinalRecommandation(BlanckVersionVORFSend);
                }
            } else if (id.getRoleId() == 3) {
                if (manuscriptEditorForAllVersion != null && !manuscriptEditorForAllVersion.isEmpty()) {
//                List<ManuscriptEditor> finalRecommendation = manuscriptEditorForAllVersion.stream().filter(manu -> (manu.getManuscriptStatus().equals(52) || manu.getManuscriptStatus().equals(51))).collect(Collectors.toList());
                    List<AuthoritiesHistory> finalRecommendation = authoritiesHistoryRepo.getByManuscriptIdAndRoleIdAndPreviousUserIdEditor(id.getId(), id.getEditor_id());
//                for (ManuscriptEditor mec1 : finalRecommendation) {
                    List<AuthoritiesHistory> finalRecommendation1 = authoritiesHistoryRepo.getByManuscriptIdAndRoleIdEIC(id.getId(), id.getEditor_id());
                    finalRecommendation.addAll(finalRecommendation1);
                    finalRecommendation = finalRecommendation
                            .stream()
                            .sorted(Comparator.comparingInt(AuthoritiesHistory::getId).reversed())
                            .collect(Collectors.toList());
                    for (AuthoritiesHistory mec1 : finalRecommendation) {
//                    ManuscriptEditorCommentsVO mecv = getEditorComment(mec1);
                        if (mec1.getManuscriptStatusId() == 52 || mec1.getManuscriptStatusId() == 51) {
                            if (mec1.getPreviousUserRole() != null) {
                                if (mec1.getPreviousUserRole() == 2 || mec1.getPreviousUserRole() == 3) {
                                    ManuscriptEditorCommentsVO mecv = getCommentByEditorAndEIC(mec1);
                                    manuscriptEditorCommentsForFinalRecommandation.add(mecv);
                                }
                            }
                        }
                    }
//                    List<AuthoritiesHistory> finalRecommedentationHistoryData = authoritiesHistoryRepo.getAuthoritiesHistoryByStatusIdMultipleByRoleIdEditor(id.getId(), 68, id.getEditor_id());
                    List<AuthoritiesHistory> finalRecommedentationHistoryData = authoritiesHistoryRepo.getAuthoritiesHistoryByStatusIdMultipleByRoleIdEditorAcceptRejectRevision(id.getId(), id.getEditor_id());
                    List<ManuScriptFile> finalRevisionFiles = manuScriptFileRepo.getManuscriptFileByStatus(id.getId(), "FINAL REVISION");
                    if (finalRecommedentationHistoryData != null && !finalRecommedentationHistoryData.isEmpty() && finalRevisionFiles != null && !finalRevisionFiles.isEmpty()) {
                        int fileCount = 0;
                        for (AuthoritiesHistory manuEditor : finalRecommedentationHistoryData) {
                            if(manuEditor.getManuscriptStatusId()==51 || manuEditor.getManuscriptStatusId()==52)
                            {
                                ManuscriptEditorCommentsVO mecv =
                                        manuscriptEditorCommentsForFinalRecommandation.stream()
                                                .filter(manuscriptEditorCommentsVO1 -> manuscriptEditorCommentsVO1.getId().equals(manuEditor.getId()))
                                                .findFirst().get();
                                int indexOfOldObject= manuscriptEditorCommentsForFinalRecommandation.indexOf(mecv);
                                if (fileCount < finalRevisionFiles.size()) {
                                    mecv = getFinalRecommedentationEditorCommentWithFileForAcceptReject(manuEditor, finalRevisionFiles.get(fileCount), mecv);
                                } else {
                                    mecv = getFinalRecommedentationEditorCommentWithFileForAcceptReject(manuEditor, null, mecv);
                                }
                                manuscriptEditorCommentsForFinalRecommandation.set(indexOfOldObject,mecv);
                            } else{
                                ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
                                if (fileCount < finalRevisionFiles.size()) {
                                    mecv = getFinalRecommedentationEditorCommentWithFile(manuEditor, finalRevisionFiles.get(fileCount));
                                } else {
                                    mecv = getFinalRecommedentationEditorCommentWithFile(manuEditor, null);
                                }
                                manuscriptEditorCommentsForFinalRecommandation.add(mecv);
                            }

                            manuscriptEditorCommentsForFinalRecommandation = manuscriptEditorCommentsForFinalRecommandation
                                    .stream()
                                    .sorted(Comparator.comparingInt(ManuscriptEditorCommentsVO::getId).reversed())
                                    .collect(Collectors.toList());
                            fileCount++;
                        }
                    } else if (finalRecommedentationHistoryData != null && !finalRecommedentationHistoryData.isEmpty()) {
                        for (AuthoritiesHistory manuEditor : finalRecommedentationHistoryData) {
                            if (!manuEditor.getManuscriptStatusId().equals(51)
                                    && !manuEditor.getManuscriptStatusId().equals(52)) {
                                ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
                                mecv = getCommentByEditorAndEIC(manuEditor);
                                manuscriptEditorCommentsForFinalRecommandation.add(mecv);
                                manuscriptEditorCommentsForFinalRecommandation = manuscriptEditorCommentsForFinalRecommandation
                                        .stream()
                                        .sorted(Comparator.comparingInt(ManuscriptEditorCommentsVO::getId).reversed())
                                        .collect(Collectors.toList());
                            }
                        }
                    }

                    commentVO.setManuscriptEditorCommentsForFinalRecommandation(
                            manuscriptEditorCommentsForFinalRecommandation);
                } else {
                    commentVO.setManuscriptEditorCommentsForFinalRecommandation(BlanckVersionVORFSend);
                }

            }
            // All EditorsCmments
            List<ManuscriptEditorCommentsVO> sendBackEICVOblack = new ArrayList<>();
            List<ManuscriptEditorComment> p = manuscriptEditorCommentRepo.getManuscriptEditorComment(id.getId());
            if (p != null && !p.isEmpty()) {
                List<EditorCommentsVo> editorCommentVo = new ArrayList<>();
                List<ManuscriptEditorCommentsVO> sendBackEICVO = new ArrayList<>();
                p.stream().forEach(l -> {
                    EditorCommentsVo k = getEditorComment(l);
                    editorCommentVo.add(k);
                    if (l.getFileType() != null && l.getFileType().equalsIgnoreCase("Send Back To EIC")) {
                        ManuscriptEditorCommentsVO mecvSend = getSendBackEICComment(l);
                        // Added by Utkarsh
                        mecvSend.setCommentRole("Editor");
                        mecvSend.setStatusId(k.getStatusId());
                        sendBackEICVO.add(mecvSend);
                    }

                });
                commentVO.setEditorCommentVo(editorCommentVo);
                commentVO.setSendBackToEICComment(sendBackEICVO);
            } else {
                commentVO.setSendBackToEICComment(sendBackEICVOblack);
            }

            //Author dashboard
            List<ManuscriptEditorComment> pp = manuscriptEditorCommentRepo.getManuscriptEditorCommentAuthor(id.getId());
            if (pp != null && !pp.isEmpty()) {
                List<EditorCommentsVo> editorCommentVo1 = new ArrayList<>();
                pp.stream().forEach(ll -> {
                    EditorCommentsVo k1 = getEditorComment(ll);
                    editorCommentVo1.add(k1);
                });
                commentVO.setEditorCommentAuthorVo(editorCommentVo1);

            }


            // keyword
            List<ManuScriptKeywords> manuScriptKeywordDetails = manuScriptKeywordsRepo
                    .getManuscriptKeywordsFromManuscriptId(id.getId());
            if (manuScriptKeywordDetails != null && !manuScriptKeywordDetails.isEmpty()) {
                for (ManuScriptKeywords manuScriptKeyword : manuScriptKeywordDetails) {
                    if (manuScriptKeyword.getKeywords().getIsActive().equals(1)) {
                        KeywordsVO keywordVOData = new KeywordsVO();
                        keywordVOData.setId(manuScriptKeyword.getKeywords().getId());
                        keywordVOData.setJournalId(manuScriptKeyword.getKeywords().getJournalId());
                        keywordVOData.setKeyword(manuScriptKeyword.getKeywords().getKeyword());
                        keywordVOData.setType(manuScriptKeyword.getKeywords().getType());
                        keywordVO.add(keywordVOData);
                    }
                }
            }
            // Journal Charges
            if (manuscript.get(0).getJournalId() != null && manuscript.get(0).getJournal().getCharge() != null) {
                JournalChargeDetails journalChargeDetails = new JournalChargeDetails();
                journalChargeDetails.setJournalId(manuscript.get(0).getJournal().getCharge().getJournalId());
                journalChargeDetails.setComponent(manuscript.get(0).getJournal().getCharge().getComponent());
                journalChargeDetails.setChargeAmount(manuscript.get(0).getJournal().getCharge().getChargeAmount());
                journalChargeDetails.setFixedCharges(manuscript.get(0).getJournal().getCharge().getFixedCharges());
                journalChargeDetails.setColorCharges(manuscript.get(0).getJournal().getCharge().getColorCharges());
                journalChargeDetails
                        .setBlackWhiteCharges(manuscript.get(0).getJournal().getCharge().getBlackWhiteCharges());
                journalChargeDetails.setTableCharges(manuscript.get(0).getJournal().getCharge().getTableCharges());
                journalChargeDetails
                        .setTypeSettingCharges(manuscript.get(0).getJournal().getCharge().getTypeSettingCharges());
                journalChargeDetails
                        .setAdditionalCharges(manuscript.get(0).getJournal().getCharge().getAdditionalCharges());
                journalChargeDetails
                        .setProductionCharges(manuscript.get(0).getJournal().getCharge().getProductionCharges());
                journalChargeDetails.setCurrency(manuscript.get(0).getJournal().getCharge().getCurrency());
                journalChargeDetails
                        .setFixedChargeAmount(manuscript.get(0).getJournal().getCharge().getFixedChargeAmount());
                commentVO.setJournalChargeDetails(journalChargeDetails);
            }
            commentVO.setKeyword(keywordVO);

            // Journal MetaData Content
            if (manuscript.get(0).getJournal().getJournalMetadata() != null) {
                commentVO
                        .setCopyrightContent(manuscript.get(0).getJournal().getJournalMetadata().getCopyrightContent());
                commentVO.setSubmissionMessage(
                        manuscript.get(0).getJournal().getJournalMetadata().getSubmissionMessage());
            }
            UserInvoice userInvoice = userInvoiceRepo.getPaymentDetailByMid(id.getId());
            if (userInvoice != null) {
                PaymentHistoryVO paymentHistoryVO = new PaymentHistoryVO();
                paymentHistoryVO.setId(userInvoice.getId());
                paymentHistoryVO.setTransactionId(userInvoice.getTransactionId());
                paymentHistoryVO.setManuscriptId(userInvoice.getManuscriptId());
                paymentHistoryVO.setUserId(userInvoice.getUserId());
                paymentHistoryVO.setJournalId(userInvoice.getJournalId());
                paymentHistoryVO.setPaymentMode(userInvoice.getPaymentMode());
                paymentHistoryVO.setTransactionMode(userInvoice.getTransactionMode());
                paymentHistoryVO.setAmount(userInvoice.getAmount());
                paymentHistoryVO.setTransactionStatus(userInvoice.getTransactionStatus());

                if (userInvoice.getPaymentMode() != null && userInvoice.equals("OFFLINE")) {
                    paymentHistoryVO.setChequeNo(userInvoice.getChequeNo());
                    paymentHistoryVO.setChequeDate(userInvoice.getChequeDate());
                    paymentHistoryVO.setBankDetail(userInvoice.getBankDetail());
                }
                commentVO.setUserInvoice(paymentHistoryVO);
            }

            // Manuscript rating
            ManuscriptRating manuscriptRatingData = manuscriptRatingRepo.getManuscriptRating(id.getUserId(),
                    id.getRoleId(), id.getId());
            if (manuscriptRatingData != null) {
                ManuscriptRatingVo manuscriptRatingVoAdd = new ManuscriptRatingVo();
                manuscriptRatingVoAdd.setId(manuscriptRatingData.getId());
                manuscriptRatingVoAdd.setManuscriptId(manuscriptRatingData.getManuscriptId());
                manuscriptRatingVoAdd.setManusciptVersion(manuscriptRatingData.getManusciptVersion());
                manuscriptRatingVoAdd.setJournalId(manuscriptRatingData.getJournalId());
                manuscriptRatingVoAdd.setUserId(manuscriptRatingData.getUserId());
                manuscriptRatingVoAdd.setRating(manuscriptRatingData.getRating());
                manuscriptRatingVoAdd.setThumbsRating(manuscriptRatingData.getThumbsRating());
                manuscriptRatingVoAdd.setUpdatedBy(manuscriptRatingData.getUpdatedBy());
                manuscriptRatingVoAdd.setRoleId(manuscriptRatingData.getRoleId());
                manuscriptRatingVoAdd.setCreatedBy(manuscriptRatingData.getCreatedBy());
                manuscriptRatingVoAdd.setRatingComments(manuscriptRatingData.getRatingComments());
                commentVO.setManuscriptRatingVo(manuscriptRatingVoAdd);
            }

        }
        return commentVO;
    }

    @Override
    public CommentVO getAllCommentAuthor(IDVO id) {

        CommentVO commentVO = new CommentVO();

        List<ManuscriptFileVO> fileVo = new ArrayList<>();
        List<ManuscriptReviewerCommentsVO> reviewerCommentVo = new ArrayList<>();
        List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsVO = new ArrayList<>();

        List<ManuscriptReviewerCommentsVO> manuscriptReviewerCommentsForAllVersionVO = new ArrayList<>();
        List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForAllVersionVO = new ArrayList<>();

        List<UserVO> userVO = new ArrayList<>();
        List<KeywordsVO> keywordVO = new ArrayList<>();
        List<ManuscriptFileVO> previousVerisonManuscriptFileVO = new ArrayList<>();
        List<FileVo> reviewerListFile = new ArrayList<>();
        List<FileVo> reviewerListFile1 = new ArrayList<>();

        List<ManuScript> manuscript = manuScriptRepo.getManuScripByVersion(id.getId(), id.getVersion());
        if (manuscript != null && !manuscript.isEmpty()) {
            ManuscriptListVO mv = ManuscriptTransformationUtil.mapManuscriptModelToManuscriptVO(manuscript.get(0));
            commentVO.setManuscriptListVO(mv);

            if (id.getEdiotrialDashboard() != null && !id.getEdiotrialDashboard().isEmpty()
                    && id.getEdiotrialDashboard().equalsIgnoreCase("Yes")) {
                Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(id.getId());
                List<ManuScriptFile> listManuscriptFile = manuScriptFileRepo.getManuscriptByactive(id.getId(), maxVersion,
                        1);
                List<BundleTracker> manuscriptStatus = bundleTrackerRepo.manuscriptFailedStatus(id.getId());
                List<ManuScriptFile> manuscriptFile = new ArrayList<>();
                if (manuscriptStatus != null && manuscriptStatus.size() > 0) {
                    manuscriptFile = listManuscriptFile.stream().filter(i -> !(i.getFileName().equalsIgnoreCase("Bundled.pdf") ||
                            i.getFileName().equalsIgnoreCase("Bundled.docx") || i.getFileName().equalsIgnoreCase("Bundled.doc") || i.getFileName().equalsIgnoreCase("BundledPdfForReviewer.pdf"))).collect(Collectors.toList());
                } else {
                    manuscriptFile = listManuscriptFile;
                }
                if (manuscriptFile != null && !manuscriptFile.isEmpty()) {
                    for (ManuScriptFile mf : manuscriptFile) {
                        ManuscriptFileVO manuscriptFileVO = new ManuscriptFileVO();
                        manuscriptFileVO.setManuscriptFileId(mf.getManuscriptFileId());
                        manuscriptFileVO.setFileName(mf.getFileName());
                        manuscriptFileVO.setFileOrder(mf.getFileOrder());
                        manuscriptFileVO.setFilePath(mf.getFilePath());
                        manuscriptFileVO.setFileStatus(mf.getFileStatus());
                        manuscriptFileVO.setFileSize(mf.getFileSize());
                        manuscriptFileVO.setFileVersion(mf.getFileVersion());
                        manuscriptFileVO.setCreatedOn(mf.getCreatedOn());
                        manuscriptFileVO.setLegend(mf.getLegend());
                        manuscriptFileVO.setUpdatedOn(mf.getUpdatedOn());

                        // Added by Mudit
                        manuscriptFileVO.setFileType(mf.getFileType());
                        fileVo.add(manuscriptFileVO);
                    }
                    commentVO.setManuscriptFileVO(fileVo);
                }

                List<ManuScriptFile> manuscriptFileAllVersion = manuScriptFileRepo
                        .getAllVersionFileManuscriptByactive(id.getId());
                if (manuscriptFileAllVersion != null && !manuscriptFileAllVersion.isEmpty()) {

                    for (ManuScriptFile i : manuscriptFileAllVersion) {
                        if (id.getVersion() != null && id.getVersion().equals(0)) {
                            if (i.getIsActive() != null && i.getIsActive().equals(0)) {
                                ManuscriptFileVO manuscriptFileVO = new ManuscriptFileVO();
                                manuscriptFileVO.setManuscriptFileId(i.getManuscriptFileId());
                                manuscriptFileVO.setFileName(i.getFileName());
                                manuscriptFileVO.setFilePath(i.getFilePath());
                                manuscriptFileVO.setFileOrder(i.getFileOrder());
                                manuscriptFileVO.setFileSize(i.getFileSize());
                                manuscriptFileVO.setFileStatus(i.getFileStatus());
                                manuscriptFileVO.setFileVersion(i.getFileVersion());
                                manuscriptFileVO.setCreatedOn(i.getCreatedOn());
                                manuscriptFileVO.setLegend(i.getLegend());
                                manuscriptFileVO.setFileType(i.getFileType());
                                manuscriptFileVO.setUpdatedOn(i.getUpdatedOn());
                                previousVerisonManuscriptFileVO.add(manuscriptFileVO);
                            }
                        }

                        if (i.getFileVersion() != id.getVersion()) {
                            ManuscriptFileVO manuscriptFileVO = new ManuscriptFileVO();
                            manuscriptFileVO.setManuscriptFileId(i.getManuscriptFileId());
                            manuscriptFileVO.setFileName(i.getFileName());
                            manuscriptFileVO.setFilePath(i.getFilePath());
                            manuscriptFileVO.setFileOrder(i.getFileOrder());
                            manuscriptFileVO.setFileSize(i.getFileSize());
                            manuscriptFileVO.setFileStatus(i.getFileStatus());
                            manuscriptFileVO.setFileVersion(i.getFileVersion());
                            manuscriptFileVO.setCreatedOn(i.getCreatedOn());
                            manuscriptFileVO.setLegend(i.getLegend());
                            manuscriptFileVO.setFileType(i.getFileType());
                            manuscriptFileVO.setUpdatedOn(i.getUpdatedOn());
                            previousVerisonManuscriptFileVO.add(manuscriptFileVO);
                        }
                        commentVO.setPreviousVerisonManuscriptFileVO(previousVerisonManuscriptFileVO);
                    }
                }

                List<ManuscriptEditor> getmanuscriptEditorData = manuscriptEditorRepo
                        .getAllAssignedEdiorsOnManuscript(id.getId(), id.getVersion());
                List<IDVO> editorsNameList = new ArrayList<>();
                if (getmanuscriptEditorData != null && !getmanuscriptEditorData.isEmpty()) {
                    getmanuscriptEditorData.stream().forEach(i -> {
                        User getUserData = userRepo.getOne(i.getEditorId());
                        if (getUserData != null) {
                            IDVO add = new IDVO();
                            add.setUserId(getUserData.getId());
                            add.setEmail(getUserData.getEmail());
                            add.setName(getUserData.getSalutation() + "." + getUserData.getFirstName() + " "
                                    + getUserData.getLastName());
                            add.setSalutation(getUserData.getSalutation().toString());
                            add.setFirstName(getUserData.getFirstName());
                            add.setMiddleName(getUserData.getMiddleName());
                            add.setLastName(getUserData.getLastName());
                            if (i.getAssignedDate() != null)
                                add.setEditorAssignDate(i.getAssignedDate());
                            if (i.getEditorDecisionDate() != null)
                                add.setEditorDecisionDate(i.getEditorDecisionDate());
                            editorsNameList.add(add);
                        }
                    });
                    commentVO.setEditorsNameList(editorsNameList);
                }

                commentVO.setArticleStatus(manuscript.get(0).getStatusMaster().getStatusValue());

                List<ManuScriptReviewer> getManuScriptReviewerData = manuScriptReviewerRepo
                        .getManuscriptReviewerListByManuScriptIdAndVersion(id.getId(), id.getVersion());
                if (getManuScriptReviewerData != null && !getManuScriptReviewerData.isEmpty()) {
                    List<IDVO> ReviewerNameList = new ArrayList<>();
                    getManuScriptReviewerData.stream().forEach(i -> {
                        User getUserData = userRepo.getByID(i.getReviewerId());
                        if (getUserData != null) {
                            IDVO add = new IDVO();
                            add.setUserId(getUserData.getId());
                            add.setEmail(getUserData.getEmail());
                            add.setName(getUserData.getSalutation() + "." + getUserData.getFirstName() + " "
                                    + getUserData.getLastName());
                            add.setSalutation(getUserData.getSalutation().toString());
                            add.setFirstName(getUserData.getFirstName());
                            add.setMiddleName(getUserData.getMiddleName());
                            add.setLastName(getUserData.getLastName());
                            add.setManuscriptStatus(i.getStatusMaster().getStatusValue());
                            if (i.getAssignedDate() != null)
                                add.setReviewerAssignDate(i.getAssignedDate());
                            if (i.getUnassignedDate() != null)
                                add.setReviewerUnAssignDate(i.getUnassignedDate());
                            if (i.getCompleteDate() != null)
                                add.setReviewerCompleteDate(i.getCompleteDate());
                            if (i.getDueDate() != null)
                                add.setReviewerDueDate(i.getDueDate());
                            if (i.getRejectDate() != null)
                                add.setReviewerRejectDate(i.getRejectDate());
                            if (i.getStartDate() != null)
                                add.setReviewerStartDate(i.getStartDate());
                            add.setIsReviewerActive(i.getIsActive());
                            add.setManuscriptId(i.getManuscriptId());
                            add.setManuscriptVersion(i.getManuscriptVersion());
                            add.setManuscriptReviewerId(i.getId());
                            ReviewerNameList.add(add);
                        }
                    });
                    commentVO.setReviewerNameList(ReviewerNameList);
                }

                List<UserJournal> getAllUserJournal = userJournalRepo
                        .getEICJournalList(manuscript.get(0).getJournalId());
                List<IDVO> managingEditorNameList = new ArrayList<>();
                if (getAllUserJournal != null && !getAllUserJournal.isEmpty()) {
                    getAllUserJournal.stream().forEach(i -> {
                        i.getUser().getRole().stream().forEach(j -> {
                            if (j.getRoleId().equals(2)) {
                                IDVO add = new IDVO();
                                add.setUserId(i.getUser().getId());
                                add.setEmail(i.getUser().getEmail());
                                add.setName(i.getUser().getSalutation() + "." + i.getUser().getFirstName() + " "
                                        + i.getUser().getLastName());
                                add.setSalutation(i.getUser().getSalutation().toString());
                                add.setFirstName(i.getUser().getFirstName());
                                add.setMiddleName(i.getUser().getMiddleName());
                                add.setLastName(i.getUser().getLastName());
                                add.setManagingEditorAssignDate(manuscript.get(0).getSubmissionDate());
                                add.setManagingEditoDecisionDate(manuscript.get(0).getFinalDecisionDate());
                                managingEditorNameList.add(add);
                            }
                        });
                    });
                    commentVO.setManagingEditorNameList(managingEditorNameList);
                }

                List<ManuScriptReviewer> i = manuScriptReviewerRepo.getmanuScriptAllReviewer(
                        manuscript.get(0).getManuScriptId(), manuscript.get(0).getJournalId());
                if (i != null && !i.isEmpty()) {
                    i.stream().forEach(j -> {
                        if (j.getReviewerFileName() != null && !j.getReviewerFileName().isEmpty()
                                && j.getReviewerFilePath() != null && !j.getReviewerFilePath().isEmpty()) {
                            FileVo a = new FileVo();
                            a.setId(j.getId());
                            a.setJournalId(j.getJournalId());
                            a.setManuscriptId(j.getManuscriptId());
                            a.setManuscriptVersion(j.getManuscriptVersion());
                            a.setReviewerFileName(j.getReviewerFileName());
                            a.setReviewerFilePath(j.getReviewerFilePath());
                            a.setReviewerFileSize(j.getReviewerFileSize());
                            a.setReviewerId(j.getReviewerId());
                            a.setCommentToAuthor(j.getCommentsToAuthor());
                            a.setCommentToEditor(j.getCommentsToEditor());
                            a.setReviewerResponse(j.getReviewerResponse());
                            if (j.getReviewerId() != null) {
                                User u = userRepo.getOne(j.getReviewerId());
                                if (u != null) {
                                    a.setReviewerEmail(u.getEmail());
                                    a.setReviewerFirstName(u.getFirstName());
                                    a.setReviewerLastName(u.getLastName());
                                    a.setReviewerMiddleName(u.getMiddleName());
                                    a.setReviewerSalutation(u.getSalutation().toString());
                                }
                            }
                            reviewerListFile.add(a);
                        }
                    });
                    commentVO.setReviewerListFile(reviewerListFile);
                }
            } else {
                // ManuscriptFile
                Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(id.getId());
                List<ManuScriptFile> listManuscriptFile = manuScriptFileRepo.getManuscriptByactive(id.getId(), maxVersion,
                        1);
                List<BundleTracker> manuscriptStatus = bundleTrackerRepo.manuscriptFailedStatus(id.getId());
                List<ManuScriptFile> manuscriptFile = new ArrayList<>();
                if (manuscriptStatus != null && manuscriptStatus.size() > 0) {
                    manuscriptFile = listManuscriptFile.stream().filter(i -> !(i.getFileName().equalsIgnoreCase("Bundled.pdf") ||
                            i.getFileName().equalsIgnoreCase("Bundled.docx") || i.getFileName().equalsIgnoreCase("Bundled.doc") || i.getFileName().equalsIgnoreCase("BundledPdfForReviewer.pdf"))).collect(Collectors.toList());
                } else {
                    manuscriptFile = listManuscriptFile;
                }
                if (manuscriptFile != null && !manuscriptFile.isEmpty()) {
                    for (ManuScriptFile mf : manuscriptFile) {
                        ManuscriptFileVO manuscriptFileVO = new ManuscriptFileVO();
                        manuscriptFileVO.setManuscriptFileId(mf.getManuscriptFileId());
                        manuscriptFileVO.setFileName(mf.getFileName());
                        manuscriptFileVO.setFilePath(mf.getFilePath());
                        manuscriptFileVO.setFileOrder(mf.getFileOrder());
                        manuscriptFileVO.setFileSize(mf.getFileSize());
                        manuscriptFileVO.setFileVersion(mf.getFileVersion());
                        manuscriptFileVO.setCreatedOn(mf.getCreatedOn());
                        manuscriptFileVO.setLegend(mf.getLegend());
                        manuscriptFileVO.setFileStatus(mf.getFileStatus());
                        manuscriptFileVO.setUpdatedOn(mf.getUpdatedOn());
                        // Added by Mudit
                        manuscriptFileVO.setFileType(mf.getFileType());
                        fileVo.add(manuscriptFileVO);
                    }
                    commentVO.setManuscriptFileVO(fileVo);
                }
            }
            // Add Co Author
            List<ManuScriptCoAuthor> getManuScriptCoAuthorList = manuScriptCoAuthorRepo
                    .sendEmailToCoAuthorOnManuscriptList(id.getId());
            List<ManuscriptCoAuthorVO> manuscriptCoAuthorVOList = new ArrayList<>();
            if (getManuScriptCoAuthorList != null && !getManuScriptCoAuthorList.isEmpty()) {
                getManuScriptCoAuthorList.stream().forEach(i -> {
                    ManuscriptCoAuthorVO addManuScriptCoAuthor = new ManuscriptCoAuthorVO();
                    addManuScriptCoAuthor.setId(i.getId());
                    addManuScriptCoAuthor.setOrder(i.getAuthorOrder() == null ? 0 : i.getAuthorOrder());
                    User getUSer = userRepo.getById(i.getCoAuthorId());
                    if (getUSer != null) {
                        addManuScriptCoAuthor.setSalutation(getUSer.getSalutation());
                        addManuScriptCoAuthor.setInstitution(getUSer.getInstitute());
                        addManuScriptCoAuthor.setCoAuthorFullName(getUSer.getFirstName() + " " + getUSer.getLastName());
                        if (getUSer.getSalutation() != null)
                            addManuScriptCoAuthor.setSalutation(getUSer.getSalutation());
                        addManuScriptCoAuthor.setFirstName(getUSer.getFirstName());
                        addManuScriptCoAuthor.setMiddleName(getUSer.getMiddleName());
                        addManuScriptCoAuthor.setLastName(getUSer.getLastName());
                        addManuScriptCoAuthor.setEmail(getUSer.getEmail());
                        addManuScriptCoAuthor.setOrcidId(getUSer.getOrcidId());
                        addManuScriptCoAuthor.setResearcherId(getUSer.getResearcherId());
                        addManuScriptCoAuthor.setTrialRegistrationNo(getUSer.getTrialRegistrationNo());
                        addManuScriptCoAuthor.setCountryId(getUSer.getCountryId());
                        addManuScriptCoAuthor.setState(getUSer.getState());
                        addManuScriptCoAuthor.setAcademic(getUSer.getAcademic());
                        addManuScriptCoAuthor.setMobileNumber(getUSer.getMobileNumber());
                    }
                    addManuScriptCoAuthor.setCoAuthorId(i.getCoAuthorId());
                    if (i.getCopyrightAgreement() != null) {
                        addManuScriptCoAuthor.setCopyrightAgreement(i.getCopyrightAgreement());
                    } else {
                        addManuScriptCoAuthor.setCopyrightAgreement("No Response");
                    }
                    addManuScriptCoAuthor.setCopyrightAgreementContent(i.getCopyrightAgreementContent());
                    if (i.getCoAuthorId() != null && i.getCoAuthorId().equals(manuscript.get(0).getAuthorId())) {

                        if (i.getIsCorrespondingAuthor() != null && i.getIsCorrespondingAuthor().equals("Y")) {
                            addManuScriptCoAuthor.setType("Primary");
                            addManuScriptCoAuthor.setAuthorType("Primary");
                        } else {
                            addManuScriptCoAuthor.setType("Co-author");
                            addManuScriptCoAuthor.setAuthorType("Co-author");
                        }
                        addManuScriptCoAuthor.setTrialRegistrationNo(manuscript.get(0).getTrialRegistrationNo());
                        addManuScriptCoAuthor
                                .setCopyrightAgreementContent(manuscript.get(0).getCopyrightAgreementContent());
                        addManuScriptCoAuthor.setCopyrightAgreement(manuscript.get(0).getCopyrightAgreement());
                    } else {
                        if (i.getIsCorrespondingAuthor() != null && i.getIsCorrespondingAuthor().equals("Y")) {
                            addManuScriptCoAuthor.setType("Primary");
                            addManuScriptCoAuthor.setAuthorType("Primary");
                        } else {
                            addManuScriptCoAuthor.setType("Co-author");
                            addManuScriptCoAuthor.setAuthorType("Co-author");
                        }
                    }
                    addManuScriptCoAuthor.setManuscriptId(i.getCoAuthorManuscriptId());
                    addManuScriptCoAuthor.setCorrespondingAuthor(i.getCorrespondingAuthor());
                    addManuScriptCoAuthor.setIsCorrespondingAuthor(i.getIsCorrespondingAuthor());
                    addManuScriptCoAuthor.setCoAuthorStatus(i.getCoAuthorStatus());
                    manuscriptCoAuthorVOList.add(addManuScriptCoAuthor);
                });
            }
            List<ManuScriptCoAuthor> coAuthorDatas = manuScriptCoAuthorRepo
                    .getManuScriptCoAuthorDatas(manuscript.get(0).getAuthorId(), manuscript.get(0).getManuScriptId());
            if (coAuthorDatas == null || coAuthorDatas.isEmpty()) {
                ManuScriptCoAuthor p = new ManuScriptCoAuthor();
                p.setCoAuthorId(manuscript.get(0).getAuthorId());
                p.setCoAuthorManuscriptId(manuscript.get(0).getManuScriptId());
                p.setAuthorOrder(0);
                ManuScriptCoAuthor j = manuScriptCoAuthorRepo.save(p);
                ManuscriptCoAuthorVO addManuScriptCoAuthor = new ManuscriptCoAuthorVO();
                addManuScriptCoAuthor.setId(j.getId());
                addManuScriptCoAuthor.setOrder(j.getAuthorOrder());
                User getUSer = userRepo.getById(j.getCoAuthorId());
                if (getUSer != null) {
                    addManuScriptCoAuthor.setSalutation(getUSer.getSalutation());
                    addManuScriptCoAuthor.setInstitution(getUSer.getInstitute());
                    addManuScriptCoAuthor.setCoAuthorFullName(getUSer.getFirstName() + " " + getUSer.getLastName());
                    addManuScriptCoAuthor.setEmail(getUSer.getEmail());
                    addManuScriptCoAuthor.setSalutation(getUSer.getSalutation());
                    addManuScriptCoAuthor.setFirstName(getUSer.getFirstName());
                    addManuScriptCoAuthor.setMiddleName(getUSer.getMiddleName());
                    addManuScriptCoAuthor.setLastName(getUSer.getLastName());
                    addManuScriptCoAuthor.setOrcidId(getUSer.getOrcidId());
                    addManuScriptCoAuthor.setResearcherId(getUSer.getResearcherId());
                    addManuScriptCoAuthor.setCountryId(getUSer.getCountryId());
                    addManuScriptCoAuthor.setState(getUSer.getState());
                    addManuScriptCoAuthor.setAcademic(getUSer.getAcademic());
                    addManuScriptCoAuthor.setMobileNumber(getUSer.getMobileNumber());
                }
                addManuScriptCoAuthor.setCoAuthorId(j.getCoAuthorId());
                addManuScriptCoAuthor.setCoAuthorStatus(j.getCoAuthorStatus());
                addManuScriptCoAuthor.setCopyrightAgreement(manuscript.get(0).getCopyrightAgreement());
                addManuScriptCoAuthor.setCopyrightAgreementContent(manuscript.get(0).getCopyrightAgreementContent());
                addManuScriptCoAuthor.setType("Primary");
                addManuScriptCoAuthor.setAuthorType("Primary");
                addManuScriptCoAuthor.setTrialRegistrationNo(manuscript.get(0).getTrialRegistrationNo());
                addManuScriptCoAuthor.setManuscriptId(manuscript.get(0).getManuScriptId());
                manuscriptCoAuthorVOList.add(addManuScriptCoAuthor);
            }
            Collections.sort(manuscriptCoAuthorVOList, (v1, v2) -> v1.getOrder().compareTo(v2.getOrder()));
            commentVO.setManuscriptCoAuthorVOList(manuscriptCoAuthorVOList);
            // ManuscriptReviewerComment
            List<ManuScriptReviewer> manuScriptReviewerList = manuScriptReviewerRepo
                    .getManuscriptReviewerListByManuScriptIdAndVersion(id.getId(), id.getVersion());

            if (manuScriptReviewerList != null && !manuScriptReviewerList.isEmpty()) {
                for (ManuScriptReviewer mrc : manuScriptReviewerList) {
                    User user = userLoginRepo.getById(mrc.getReviewerId());
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
                    mrcv.setRejectionReason(mrc.getRejectionReason());
                    mrcv.setCommentsForReviewer(mrc.getCommentsForReviewer());
                    mrcv.setDueDate(mrc.getDueDate());
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
                    reviewerCommentVo.add(mrcv);
                }
                commentVO.setManuscriptReviewerCommentsVO(reviewerCommentVo);
            }

            // ManuscriptJournal Subject
            List<ManuscriptJournalSubject> i = manuscriptJournalSubjectRepo.findByManuscriptId(id.getId(), 1);
            if (i != null && !i.isEmpty()) {
                List<ManuscriptJournalSubjectVo> p = new ArrayList<>();
                i.stream().forEach(j -> {
                    ManuscriptJournalSubjectVo k = new ManuscriptJournalSubjectVo();
                    k.setId(j.getId());
                    k.setManuscriptId(j.getManuscriptId());
                    k.setManuscriptVersion(j.getManuscriptVersion());
                    k.setJournalId(j.getJournalId());
                    k.setJournalSubjectName(j.getJournalSubject().getJournalSubject());
                    k.setIsActive(j.getIsActive());
                    k.setCreatedAt(j.getCreatedAt());
                    p.add(k);
                });
                commentVO.setManuscriptJournalSubject(p);
            }

            // ManuscriptReviewerCommentForAllVereion

            List<ManuScriptReviewer> manuScriptReviewerListForAllVersion = manuScriptReviewerRepo
                    .getByManuscriptId(id.getId());

            if (manuScriptReviewerListForAllVersion != null && !manuScriptReviewerListForAllVersion.isEmpty()) {
                for (ManuScriptReviewer mrc : manuScriptReviewerListForAllVersion) {
                    User user = userLoginRepo.getById(mrc.getReviewerId());
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
                    mrcv.setDueDate(mrc.getDueDate());
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
                    manuscriptReviewerCommentsForAllVersionVO.add(mrcv);
                }
                commentVO.setManuscriptReviewerCommentsForAllVersionVO(manuscriptReviewerCommentsForAllVersionVO);
            }

            // ManuscriptEditorComment
            List<ManuscriptEditor> manuscriptEditor = manuscriptEditorRepo.getByVersion(id.getId(), id.getVersion());
            if (manuscriptEditor != null && !manuscriptEditor.isEmpty()) {
                for (ManuscriptEditor mec : manuscriptEditor) {
                    User user = userLoginRepo.getById(mec.getEditorId());
                    List<StatusMaster> mstatus = statusMasterRepo.getStatusMasterDetailsById(mec.getManuscriptStatus());
                    ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
                    mecv.setManuscriptStatus(mstatus.get(0).getStatusValue());
                    mecv.setCommentsForAuthor(mec.getCommentsForAuthor());
                    mecv.setManuscriptId(mec.getManuscriptId());
                    mecv.setManuscriptVersion(mec.getManuscriptVersion());
                    mecv.setCommentsForEditor(mec.getCommentsForEditor());
                    mecv.setCommentsForManagingEditor(mec.getCommentsForManagingEditor());
                    mecv.setCommentsForReviewer(mec.getCommentsForReviewer());
                    if (mec.getDecisionLetter() != null && !mec.getDecisionLetter().equalsIgnoreCase(""))
                        mecv.setDecisionLetter(mec.getDecisionLetter());

                    if (user != null) {
                        mecv.setUserName(user.getSalutation() + "." + user.getFirstName() + " " + user.getLastName());
                    }
                    manuscriptEditorCommentsVO.add(mecv);
                }
                commentVO.setManuscriptEditorCommentsVO(manuscriptEditorCommentsVO);
            } else {
                commentVO.setManuscriptEditorCommentsVO(new ArrayList<>());
            }

            List<ManuscriptEditorCommentsVO> BlanckVersionVORFSend = new ArrayList<>();
            List<ManuscriptEditor> manuscriptEditorForAllVersion = manuscriptEditorRepo.getManuscriptEditor(id.getId());
            if (manuscriptEditorForAllVersion != null && !manuscriptEditorForAllVersion.isEmpty()) {
                for (ManuscriptEditor mec : manuscriptEditorForAllVersion) {
                    User user = userLoginRepo.getById(mec.getEditorId());
                    List<StatusMaster> mstatus = statusMasterRepo.getStatusMasterDetailsById(mec.getManuscriptStatus());
                    ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
                    mecv.setManuscriptStatus(mstatus.get(0).getStatusValue());
                    // Added by Mudit
                    mecv.setCommentsForAuthor(mec.getCommentsForAuthor());
                    mecv.setManuscriptId(mec.getManuscriptId());
                    mecv.setManuscriptVersion(mec.getManuscriptVersion());
                    mecv.setCommentsForEditor(mec.getCommentsForEditor());
                    mecv.setCommentsForManagingEditor(mec.getCommentsForManagingEditor());
                    mecv.setCommentsForReviewer(mec.getCommentsForReviewer());
                    if (mec.getDecisionLetter() != null && !mec.getDecisionLetter().equalsIgnoreCase(""))
                        mecv.setDecisionLetter(mec.getDecisionLetter());
                    // mecv.setComments(mec.getComments());
                    if (user != null) {
                        mecv.setUserName(user.getSalutation() + "." + user.getFirstName() + " " + user.getLastName());
                    }
                    manuscriptEditorCommentsForAllVersionVO.add(mecv);
                }
                commentVO.setManuscriptEditorCommentsForAllVersionVO(manuscriptEditorCommentsForAllVersionVO);
            } else {
                commentVO.setManuscriptEditorCommentsForAllVersionVO(BlanckVersionVORFSend);
            }

            // All EditorsCmments
            List<ManuscriptEditorComment> p = manuscriptEditorCommentRepo.getManuscriptEditorComment(id.getId());
            if (p != null && !p.isEmpty()) {
                List<EditorCommentsVo> editorCommentVo = new ArrayList<>();
                p.stream().forEach(l -> {
                    List<FileVo> add = new ArrayList<>();
                    EditorCommentsVo k = new EditorCommentsVo();
                    k.setComment(l.getComment());
                    k.setCommentDate(l.getCreatedAt());
                    k.setEditorName(l.getUser().getSalutation() + "." + l.getUser().getFirstName() + " "
                            + l.getUser().getLastName());
                    if (l.getUser().getSalutation() != null)
                        k.setSalutation(l.getUser().getSalutation());
                    k.setFirstName(l.getUser().getFirstName());
                    k.setMiddleName(l.getUser().getMiddleName());
                    k.setLastName(l.getUser().getLastName());
                    k.setEditorRoleId(l.getRoleId());
                    k.setEditorUserId(l.getUserId());
                    k.setManuscriptId(l.getManuscriptId());
                    k.setManuscriptVersion(l.getManuscriptVersion());
                    k.setStatusId(l.getStatusId());
                    k.setFileType(l.getFileType());
                    k.setFileName(l.getFileName());
                    k.setFilePath(l.getFilePath());
                    k.setFileSize(l.getFileSize());
                    if (l.getReviewerFileId() != null && !l.getReviewerFileId().isEmpty()) {
                        String[] reviewerPrimaryId = l.getReviewerFileId().split(";");
                        if (reviewerPrimaryId != null && !reviewerPrimaryId.toString().isEmpty()) {
                            for (String s : reviewerPrimaryId) {
                                if (s != null && !s.isEmpty()) {
                                    ManuScriptReviewer b = manuScriptReviewerRepo.getById(Integer.parseInt(s));
                                    if (b != null) {
                                        FileVo a = new FileVo();
                                        a.setId(b.getId());
                                        a.setJournalId(b.getJournalId());
                                        a.setManuscriptId(b.getManuscriptId());
                                        a.setManuscriptVersion(b.getManuscriptVersion());
                                        a.setReviewerFileName(b.getReviewerFileName());
//                                        a.setReviewerFileDisplayName(b.getReviewerFileDisplayName());
                                        a.setReviewerFilePath(b.getReviewerFilePath());
                                        a.setReviewerFileSize(b.getReviewerFileSize());
                                        a.setReviewerId(b.getReviewerId());
                                        a.setCommentToAuthor(b.getCommentsToAuthor());
                                        a.setCommentToEditor(b.getCommentsToEditor());
                                        a.setReviewerResponse(b.getReviewerResponse());
                                        if (b.getReviewerId() != null) {
                                            User u = userRepo.getOne(b.getReviewerId());
                                            if (u != null) {
                                                a.setReviewerEmail(u.getEmail());
                                                a.setReviewerFirstName(u.getFirstName());
                                                a.setReviewerLastName(u.getLastName());
                                                a.setReviewerMiddleName(u.getMiddleName());
                                                a.setReviewerSalutation(u.getSalutation().toString());
                                            }
                                        }
                                        add.add(a);
                                    }
                                }
                            }
                        }
                        k.setReviewerFile(add);
                    }
                    editorCommentVo.add(k);
                });
                commentVO.setEditorCommentVo(editorCommentVo);
            }
            // Co-Author Detail
            List<ManuScriptCoAuthor> coAuthorDetail = manuScriptCoAuthorRepo
                    .getCoAuthorListFromManuscriptId(id.getId());
            List<Integer> coAuthorId = new ArrayList<>();
            if (coAuthorDetail != null && !coAuthorDetail.isEmpty()) {
                for (ManuScriptCoAuthor coAuthor : coAuthorDetail) {
                    coAuthorId.add(coAuthor.getCoAuthorId());
                }
                if (coAuthorId != null && !coAuthorId.isEmpty()) {
                    List<User> userDetails = userRepo.getAllActiveUserByUserList(coAuthorId);
                    userVO = UserTransformationalUtils.mapUserListToUserVOList(userDetails);

                }
            }
            commentVO.setCoAuthorVO(userVO);
            // keyword
            List<ManuScriptKeywords> manuScriptKeywordDetails = manuScriptKeywordsRepo
                    .getManuscriptKeywordsFromManuscriptId(id.getId());
            List<Integer> keywordId = new ArrayList<Integer>();
            if (manuScriptKeywordDetails != null && !manuScriptKeywordDetails.isEmpty()) {
                for (ManuScriptKeywords manuScriptKeyword : manuScriptKeywordDetails) {
                    keywordId.add(manuScriptKeyword.getKeyId());
                }
                if (keywordId != null && !keywordId.isEmpty()) {
                    List<Keywords> keywordList = keywordsRepo.getKeywordListByIdList(keywordId);
                    for (Keywords keyword : keywordList) {
                        KeywordsVO keywordVOData = new KeywordsVO();
                        keywordVOData.setId(keyword.getId());
                        keywordVOData.setJournalId(keyword.getJournalId());
                        keywordVOData.setKeyword(keyword.getKeyword());
                        keywordVOData.setType(keyword.getType());
                        keywordVO.add(keywordVOData);
                    }
                }
            }
            // Journal Charges
            if (manuscript.get(0).getJournalId() != null && manuscript.get(0).getJournal().getCharge() != null) {
                JournalChargeDetails journalChargeDetails = new JournalChargeDetails();
                journalChargeDetails.setJournalId(manuscript.get(0).getJournal().getCharge().getJournalId());
                journalChargeDetails.setComponent(manuscript.get(0).getJournal().getCharge().getComponent());
                journalChargeDetails.setChargeAmount(manuscript.get(0).getJournal().getCharge().getChargeAmount());
                journalChargeDetails.setFixedCharges(manuscript.get(0).getJournal().getCharge().getFixedCharges());
                journalChargeDetails.setColorCharges(manuscript.get(0).getJournal().getCharge().getColorCharges());
                journalChargeDetails
                        .setBlackWhiteCharges(manuscript.get(0).getJournal().getCharge().getBlackWhiteCharges());
                journalChargeDetails.setTableCharges(manuscript.get(0).getJournal().getCharge().getTableCharges());
                journalChargeDetails
                        .setTypeSettingCharges(manuscript.get(0).getJournal().getCharge().getTypeSettingCharges());
                journalChargeDetails
                        .setAdditionalCharges(manuscript.get(0).getJournal().getCharge().getAdditionalCharges());
                journalChargeDetails
                        .setProductionCharges(manuscript.get(0).getJournal().getCharge().getProductionCharges());
                journalChargeDetails.setCurrency(manuscript.get(0).getJournal().getCharge().getCurrency());
                journalChargeDetails
                        .setFixedChargeAmount(manuscript.get(0).getJournal().getCharge().getFixedChargeAmount());
                commentVO.setJournalChargeDetails(journalChargeDetails);
            }
            commentVO.setKeyword(keywordVO);

            // Journal MetaData Content
            if (manuscript.get(0).getJournal().getJournalMetadata() != null) {
                commentVO
                        .setCopyrightContent(manuscript.get(0).getJournal().getJournalMetadata().getCopyrightContent());
                commentVO.setSubmissionMessage(
                        manuscript.get(0).getJournal().getJournalMetadata().getSubmissionMessage());
            }
            UserInvoice userInvoice = userInvoiceRepo.getPaymentDetailByMid(id.getId());
            if (userInvoice != null) {
                PaymentHistoryVO paymentHistoryVO = new PaymentHistoryVO();
                paymentHistoryVO.setId(userInvoice.getId());
                paymentHistoryVO.setTransactionId(userInvoice.getTransactionId());
                paymentHistoryVO.setManuscriptId(userInvoice.getManuscriptId());
                paymentHistoryVO.setUserId(userInvoice.getUserId());
                paymentHistoryVO.setJournalId(userInvoice.getJournalId());
                paymentHistoryVO.setPaymentMode(userInvoice.getPaymentMode());
                paymentHistoryVO.setTransactionMode(userInvoice.getTransactionMode());
                paymentHistoryVO.setAmount(userInvoice.getAmount());
                paymentHistoryVO.setTransactionStatus(userInvoice.getTransactionStatus());

                if (userInvoice.getPaymentMode() != null && userInvoice.equals("OFFLINE")) {
                    paymentHistoryVO.setChequeNo(userInvoice.getChequeNo());
                    paymentHistoryVO.setChequeDate(userInvoice.getChequeDate());
                    paymentHistoryVO.setBankDetail(userInvoice.getBankDetail());
                }
                commentVO.setUserInvoice(paymentHistoryVO);
            }

            // Manuscript rating
            ManuscriptRating manuscriptRatingData = manuscriptRatingRepo.getManuscriptRating(id.getUserId(),
                    id.getRoleId(), id.getId());
            if (manuscriptRatingData != null) {
                ManuscriptRatingVo manuscriptRatingVoAdd = new ManuscriptRatingVo();
                manuscriptRatingVoAdd.setId(manuscriptRatingData.getId());
                manuscriptRatingVoAdd.setManuscriptId(manuscriptRatingData.getManuscriptId());
                manuscriptRatingVoAdd.setManusciptVersion(manuscriptRatingData.getManusciptVersion());
                manuscriptRatingVoAdd.setJournalId(manuscriptRatingData.getJournalId());
                manuscriptRatingVoAdd.setUserId(manuscriptRatingData.getUserId());
                manuscriptRatingVoAdd.setRating(manuscriptRatingData.getRating());
                manuscriptRatingVoAdd.setThumbsRating(manuscriptRatingData.getThumbsRating());
                manuscriptRatingVoAdd.setUpdatedBy(manuscriptRatingData.getUpdatedBy());
                manuscriptRatingVoAdd.setRoleId(manuscriptRatingData.getRoleId());
                manuscriptRatingVoAdd.setCreatedBy(manuscriptRatingData.getCreatedBy());
                manuscriptRatingVoAdd.setRatingComments(manuscriptRatingData.getRatingComments());
                commentVO.setManuscriptRatingVo(manuscriptRatingVoAdd);
            }

            List<ManuScriptReviewer> p1 = manuScriptReviewerRepo
                    .getmanuScriptAllReviewer(manuscript.get(0).getManuScriptId(), manuscript.get(0).getJournalId());
            if (p1 != null && !p1.isEmpty()) {
                p1.stream().forEach(j -> {
                    if (j.getReviewerFileName() != null && !j.getReviewerFileName().isEmpty()
                            && j.getReviewerFilePath() != null && !j.getReviewerFilePath().isEmpty()) {
                        FileVo a = new FileVo();
                        a.setId(j.getId());
                        a.setJournalId(j.getJournalId());
                        a.setManuscriptId(j.getManuscriptId());
                        a.setManuscriptVersion(j.getManuscriptVersion());
                        a.setReviewerFileName(j.getReviewerFileName());
//                        a.setReviewerFileDisplayName(j.getReviewerFileDisplayName());
                        a.setReviewerFilePath(j.getReviewerFilePath());
                        a.setReviewerFileSize(j.getReviewerFileSize());
                        a.setReviewerId(j.getReviewerId());
                        a.setCommentToAuthor(j.getCommentsToAuthor());
                        a.setCommentToEditor(j.getCommentsToEditor());
                        a.setReviewerResponse(j.getReviewerResponse());
                        if (j.getReviewerId() != null) {
                            User u = userRepo.getById(j.getReviewerId());
                            if (u != null) {
                                a.setReviewerEmail(u.getEmail());
                                a.setReviewerFirstName(u.getFirstName());
                                a.setReviewerLastName(u.getLastName());
                                a.setReviewerMiddleName(u.getMiddleName());
                                a.setReviewerSalutation(u.getSalutation().toString());
                            }
                        }
                        reviewerListFile1.add(a);
                    }
                });
                commentVO.setReviewerListFile(reviewerListFile1);
            }
        }
        return commentVO;
    }

    @Override
    public String ratingSetup(ManuscriptRatingVo manuscriptRatingVo) {
        ManuscriptRating manuscriptRatingData = manuscriptRatingRepo.getManuscriptRating(manuscriptRatingVo.getUserId(),
                manuscriptRatingVo.getRoleId(), manuscriptRatingVo.getManuscriptId());
        if (manuscriptRatingData != null) {
            manuscriptRatingData.setJournalId(manuscriptRatingVo.getJournalId());
            manuscriptRatingData.setRating(manuscriptRatingVo.getRating());
            manuscriptRatingData.setRatingComments(manuscriptRatingVo.getRatingComments());
            manuscriptRatingData.setThumbsRating(manuscriptRatingVo.getThumbsRating());
            manuscriptRatingData.setRoleId(manuscriptRatingVo.getRoleId());
            manuscriptRatingData.setUserId(manuscriptRatingVo.getUserId());
            manuscriptRatingData.setUpdatedBy(manuscriptRatingVo.getUserId());
            manuscriptRatingData.setUpdatedOn(new Date());
            manuscriptRatingRepo.save(manuscriptRatingData);
            return "Rating Updated";
        } else {
            ManuscriptRating manuscriptRatingDataSave = new ManuscriptRating();
            manuscriptRatingDataSave.setManuscriptId(manuscriptRatingVo.getManuscriptId());
            manuscriptRatingDataSave.setManusciptVersion(manuscriptRatingVo.getManusciptVersion());
            manuscriptRatingDataSave.setJournalId(manuscriptRatingVo.getJournalId());
            manuscriptRatingDataSave.setRating(manuscriptRatingVo.getRating());
            manuscriptRatingDataSave.setRatingComments(manuscriptRatingVo.getRatingComments());
            manuscriptRatingDataSave.setThumbsRating(manuscriptRatingVo.getThumbsRating());
            manuscriptRatingDataSave.setRoleId(manuscriptRatingVo.getRoleId());
            manuscriptRatingDataSave.setUserId(manuscriptRatingVo.getUserId());
            manuscriptRatingDataSave.setCreatedOn(new Date());
            manuscriptRatingDataSave.setCreatedBy(manuscriptRatingVo.getUserId());
            manuscriptRatingRepo.save(manuscriptRatingDataSave);
            return "Rating Created";
        }
    }

    @Override
    public ManuscriptRatingVo getRatingSetupData(ManuscriptRatingVo manuscriptRatingVo) {
        ManuscriptRating manuscriptRatingData = manuscriptRatingRepo.getManuscriptRating(manuscriptRatingVo.getUserId(),
                manuscriptRatingVo.getRoleId(), manuscriptRatingVo.getManuscriptId());
        ManuscriptRatingVo manuscriptRatingVoAdd = new ManuscriptRatingVo();
        if (manuscriptRatingData != null) {
            manuscriptRatingVoAdd.setId(manuscriptRatingData.getId());
            manuscriptRatingVoAdd.setManuscriptId(manuscriptRatingData.getManuscriptId());
            manuscriptRatingVoAdd.setManusciptVersion(manuscriptRatingData.getManusciptVersion());
            manuscriptRatingVoAdd.setJournalId(manuscriptRatingData.getJournalId());
            manuscriptRatingVoAdd.setRating(manuscriptRatingData.getRating());
            manuscriptRatingVoAdd.setThumbsRating(manuscriptRatingData.getThumbsRating());
            manuscriptRatingVoAdd.setUpdatedBy(manuscriptRatingData.getUpdatedBy());
            manuscriptRatingVoAdd.setUserId(manuscriptRatingData.getUserId());
            manuscriptRatingVoAdd.setRoleId(manuscriptRatingData.getRoleId());
            manuscriptRatingVoAdd.setCreatedBy(manuscriptRatingData.getCreatedBy());
            manuscriptRatingVoAdd.setRatingComments(manuscriptRatingData.getRatingComments());
        }
        return manuscriptRatingVoAdd;
    }

    @Override
    public OverDueManuscriptVO getOverDueManuscript(Integer journalId) {
        OverDueManuscriptVO overDueManuscriptVO = new OverDueManuscriptVO();
        Integer managingEditor = 0;
        Integer editor = 0;
        Integer reviewer = 0;
        Integer author = 0;

        Integer managingEditorSevenDay = 0;
        Integer editorSevenDay = 0;
        Integer reviewerSevenDay = 0;
        Integer authorSevenDay = 0;

        // ManagingEditor Data
        // DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();

        List<Integer> meStatus = new ArrayList<>();
        meStatus.add(2);
        List<Integer> authorStatus = new ArrayList<>();
        authorStatus.add(4);
        // authorStatus.add(50);
        List<Integer> editorrStatus = new ArrayList<>();
        editorrStatus.add(3);

        List<ManuScript> managingEditorData = manuScriptRepo.getManuScriptDataByStatusIsActive(journalId, meStatus);
        if (managingEditorData != null && !managingEditorData.isEmpty()) {
            for (ManuScript manuScript : managingEditorData) {
                if (manuScript.getSubmissionDate() != null) {
                    try {
                        Date dateObj1 = df.parse(df.format(dateobj));
                        Date dateObj2 = df.parse(df.format(manuScript.getSubmissionDate()));
                        DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");
                        long diff = dateObj2.getTime() - dateObj1.getTime();
                        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                        log.debug("difference between days: " + diffDays);
                        if (diffDays < -7) {
                            managingEditorSevenDay++;
                        } else {
                            managingEditor++;
                        }
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }
                }
            }
        }

        // Author Data
        List<ManuScript> authorData = manuScriptRepo.getManuScriptDataByStatusIsActive(journalId, authorStatus);
        if (managingEditorData != null && !managingEditorData.isEmpty()) {
            for (ManuScript manuScript : authorData) {
                if (manuScript.getUpdatedOn() != null) {
                    try {
                        Date dateObj1 = df.parse(df.format(dateobj));
                        Date dateObj2 = df.parse(df.format(manuScript.getUpdatedOn()));
                        DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");
                        long diff = dateObj2.getTime() - dateObj1.getTime();
                        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                        log.debug("difference between days: " + diffDays);
                        if (diffDays < -7) {
                            authorSevenDay++;
                        } else {
                            author++;
                        }
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }
                }
            }
        }
        // Editor
        List<ManuScript> editorData = manuScriptRepo.getManuScriptDataByStatusIsActive(journalId, editorrStatus);
        if (managingEditorData != null && !managingEditorData.isEmpty()) {
            for (ManuScript manuScript : editorData) {
                if (manuScript.getSubmissionDate() != null) {
                    try {
                        Date dateObj1 = df.parse(df.format(dateobj));
                        Date dateObj2 = df.parse(df.format(manuScript.getSubmissionDate()));
                        DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");
                        long diff = dateObj2.getTime() - dateObj1.getTime();
                        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                        log.debug("difference between days: " + diffDays);
                        if (diffDays < -7) {
                            editorSevenDay++;
                        } else {
                            editor++;
                        }
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }
                }
            }
        }
        overDueManuscriptVO.setAuthor(author);
        overDueManuscriptVO.setAuthorSevenDay(authorSevenDay);
        overDueManuscriptVO.setEditor(editor);
        overDueManuscriptVO.setEditorSevenDay(editorSevenDay);
        overDueManuscriptVO.setManagingEditor(managingEditor);
        overDueManuscriptVO.setManagingEditorSevenDay(managingEditor);
        overDueManuscriptVO.setReviewer(reviewerSevenDay);
        overDueManuscriptVO.setReviewerSevenDay(reviewer);
        return overDueManuscriptVO;
    }

    @Override
    public List<FileVo> getAllReviewerFileOnManuscript(IDVO id) {
        List<FileVo> add = new ArrayList<>();
        List<ManuScriptReviewer> i = manuScriptReviewerRepo.getmanuScriptReviewer(id.getManuscriptId(),
                id.getManuscriptVersion(), id.getJournalId());
        if (i != null && !i.isEmpty()) {
            i.stream().forEach(j -> {
                if (j.getReviewerFileName() != null && !j.getReviewerFileName().isEmpty()
                        && j.getReviewerFilePath() != null && !j.getReviewerFilePath().isEmpty()) {
                    FileVo a = new FileVo();
                    a.setId(j.getId());
                    a.setJournalId(j.getJournalId());
                    a.setManuscriptId(j.getManuscriptId());
                    a.setManuscriptVersion(j.getManuscriptVersion());
                    a.setReviewerFileName(j.getReviewerFileName());
                    a.setReviewerFilePath(j.getReviewerFilePath());
                    a.setReviewerFileSize(j.getReviewerFileSize());
                    a.setReviewerId(j.getReviewerId());
                    a.setCommentToAuthor(j.getCommentsToAuthor());
                    a.setCommentToEditor(j.getCommentsToEditor());
                    a.setReviewerResponse(j.getReviewerResponse());
                    if (j.getReviewerId() != null) {
                        User u = userRepo.getOne(j.getReviewerId());
                        if (u != null) {
                            a.setReviewerEmail(u.getEmail());
                            a.setReviewerFirstName(u.getFirstName());
                            a.setReviewerLastName(u.getLastName());
                            a.setReviewerMiddleName(u.getMiddleName());
                            a.setReviewerSalutation(u.getSalutation().toString());
                        }
                    }
                    add.add(a);
                }
            });
        }
        return add;
    }

    @Override
    public void insertRole() {
        List<Journal> journalList = journalsRepo.getActivejournal();
        if (journalList != null && !journalList.isEmpty()) {
            for (Journal journal : journalList) {
                List<UserJournal> userList = userJournalRepo.getUserRoleJournalId(journal.getJournalId(), 2);
                if (userList != null && !userList.isEmpty()) {
                    for (UserJournal user : userList) {
                        manuScriptRepo.updateRoleByid(user.getUserId(), journal.getJournalId());
                    }
                }

            }
        }

    }

    @Override
    public String submitPaymentDetailsByEic(IDVO id) {
        ManuScript m = manuScriptRepo.getOne(id.getManuscriptId());
        if (m != null) {
            m.setManuscriptStatusId(id.getId());
            m.setCommentForProduction(id.getComment());
            manuScriptRepo.save(m);

            return "submitted";
        }
        return "data not found";
    }

    @Override
    public CommentVO getBlinkerComment(IDVO id) {

        CommentVO commentVO = new CommentVO();

        List<ManuscriptReviewerCommentsVO> reviewerCommentVo = new ArrayList<>();

        // ManuscriptReviewerComment
        List<ManuScriptReviewer> manuScriptReviewerList = manuScriptReviewerRepo
                .getManuscriptReviewerListForblinker(id.getId(), id.getVersion());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date todayDate = df.parse(df.format(new Date()));

            if (manuScriptReviewerList != null && !manuScriptReviewerList.isEmpty()) {
                for (ManuScriptReviewer mrc : manuScriptReviewerList) {
                    Date reviewerDueDate = df.parse(df.format(mrc.getDueDate()));
                    if (reviewerDueDate.after(todayDate) || todayDate.equals(reviewerDueDate)) {
                        User user = userLoginRepo.getOne(mrc.getReviewerId());
                        List<StatusMaster> mstatus = statusMasterRepo
                                .getStatusMasterDetailsById(mrc.getManuscriptStatus());
                        ManuscriptReviewerCommentsVO mrcv = new ManuscriptReviewerCommentsVO();
                        mrcv.setManuscriptStatus(mstatus.get(0).getStatusValue());
                        mrcv.setIsActive(mrc.getIsActive());
                        mrcv.setReviewerId(mrc.getReviewerId());
                        mrcv.setReviewerFilePath(mrc.getReviewerFilePath());
                        mrcv.setReviewerFileName(mrc.getReviewerFileName());
                        mrcv.setReviewerFileSize(mrc.getReviewerFileSize());
                        mrcv.setManuscriptId(mrc.getManuscriptId());
                        mrcv.setManuscriptVersion(mrc.getManuscriptVersion());
                        mrcv.setRejectionReason(mrc.getRejectionReason());
                        mrcv.setCommentsForReviewer(mrc.getCommentsForReviewer());
                        mrcv.setDueDate(mrc.getDueDate());
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
                            mrcv.setUserName(
                                    user.getSalutation() + "." + user.getFirstName() + " " + user.getLastName());
                        }

                        reviewerCommentVo.add(mrcv);
                    }
                }
                commentVO.setManuscriptReviewerCommentsVO(reviewerCommentVo);
            }
        } catch (Exception ex) {

            log.info("Error occured during fetching data for blinker comment" + ex.getMessage());
        }

        return commentVO;
    }

    // UN-10 CR changes
    @Override
    public CommentVO getOtherReviewersComment(IDVO id) {
        CommentVO commentVO = new CommentVO();
        List<ManuscriptReviewerCommentsVO> manuscriptReviewerCommentsForAllVersionVO = new ArrayList<>();
        List<ManuScriptReviewer> manuScriptReviewerListForAllVersion = manuScriptReviewerRepo
                .getByManuscriptId(id.getId());

        manuScriptReviewerListForAllVersion = manuScriptReviewerListForAllVersion.stream()
                .filter(obj -> !obj.getReviewerId().equals(id.getUserId())
                        && (obj.getManuscriptStatus().equals(12) || obj.getManuscriptStatus().equals(13)
                        || obj.getManuscriptStatus().equals(14) || obj.getManuscriptStatus().equals(15)))
                .collect(Collectors.toList());

        /*
         * manuScriptReviewerListForAllVersion =
         * manuScriptReviewerListForAllVersion.stream() .filter(obj ->
         * !obj.getReviewerId().equals(id.getUserId()) &&
         * !obj.getManuscriptStatus().equals(13) &&
         * !obj.getManuscriptStatus().equals(36) &&
         * !obj.getManuscriptStatus().equals(60)) .collect(Collectors.toList());
         */

        if (manuScriptReviewerListForAllVersion != null && !manuScriptReviewerListForAllVersion.isEmpty()) {
            for (ManuScriptReviewer mrc : manuScriptReviewerListForAllVersion) {
                User user = userLoginRepo.getById(mrc.getReviewerId());
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
                mrcv.setDueDate(mrc.getDueDate());
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
                manuscriptReviewerCommentsForAllVersionVO.add(mrcv);
            }
        }
        commentVO.setManuscriptReviewerCommentsForAllVersionVO(manuscriptReviewerCommentsForAllVersionVO);
        return commentVO;
    }

    private ManuscriptReviewerCommentsVO reviewercommentData(ManuScriptReviewer mrc) {

        ManuscriptReviewerCommentsVO mrcv = new ManuscriptReviewerCommentsVO();
        mrcv.setManuscriptStatus(mrc.getStatusMaster().getStatusValue());
        mrcv.setIsActive(mrc.getIsActive());
        mrcv.setReviewerId(mrc.getReviewerId());
        mrcv.setReviewerFilePath(mrc.getReviewerFilePath());
        mrcv.setReviewerFileName(mrc.getReviewerFileName());
        mrcv.setReviewerFileSize(mrc.getReviewerFileSize());
        mrcv.setManuscriptId(mrc.getManuscriptId());
        mrcv.setManuscriptVersion(mrc.getManuscriptVersion());
        mrcv.setRejectionReason(mrc.getRejectionReason());
        mrcv.setCommentsForReviewer(mrc.getCommentsForReviewer());
        mrcv.setDueDate(mrc.getDueDate());
        mrcv.setAssignDate(mrc.getAssignedDate());
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
        mrcv.setUserName(
                mrc.getUser().getSalutation() + "." + mrc.getUser().getFirstName() + " " + mrc.getUser().getLastName());

        return mrcv;
    }

    private ManuscriptCoAuthorVO coAuthorData(ManuScriptCoAuthor i, ManuScript manuscript) {
        ManuscriptCoAuthorVO addManuScriptCoAuthor = new ManuscriptCoAuthorVO();
        addManuScriptCoAuthor.setId(i.getId());
        addManuScriptCoAuthor.setOrder(i.getAuthorOrder() == null ? 0 : i.getAuthorOrder());
        if (i.getUser() != null) {
            addManuScriptCoAuthor.setSalutation(i.getUser().getSalutation());
            addManuScriptCoAuthor.setInstitution(i.getUser().getInstitute());
            addManuScriptCoAuthor.setCoAuthorFullName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
            if (i.getUser().getSalutation() != null)
                addManuScriptCoAuthor.setSalutation(i.getUser().getSalutation());
            addManuScriptCoAuthor.setFirstName(i.getUser().getFirstName());
            addManuScriptCoAuthor.setMiddleName(i.getUser().getMiddleName());
            addManuScriptCoAuthor.setLastName(i.getUser().getLastName());
            addManuScriptCoAuthor.setEmail(i.getUser().getEmail());
            addManuScriptCoAuthor.setOrcidId(i.getUser().getOrcidId());
            addManuScriptCoAuthor.setResearcherId(i.getUser().getResearcherId());
            addManuScriptCoAuthor.setMobileNumber(i.getUser().getMobileNumber());
            addManuScriptCoAuthor.setCountryCode(i.getUser().getCountryCode());
            addManuScriptCoAuthor.setTrialRegistrationNo(i.getUser().getTrialRegistrationNo());
            addManuScriptCoAuthor.setCountryId(i.getUser().getCountryId());
            addManuScriptCoAuthor.setState(i.getUser().getState());
            addManuScriptCoAuthor.setAcademic(i.getUser().getAcademic());
        }
        addManuScriptCoAuthor.setCoAuthorId(i.getCoAuthorId());
        if (i.getCopyrightAgreement() != null) {
            addManuScriptCoAuthor.setCopyrightAgreement(i.getCopyrightAgreement());
        } else {
            addManuScriptCoAuthor.setCopyrightAgreement("No Response");
        }
        addManuScriptCoAuthor.setCopyrightAgreementContent(i.getCopyrightAgreementContent());
        if (i.getCoAuthorId() != null && i.getCoAuthorId().equals(manuscript.getAuthorId())) {

            if (i.getIsCorrespondingAuthor() != null && i.getIsCorrespondingAuthor().equals("Y")) {
                addManuScriptCoAuthor.setType("Primary");
                addManuScriptCoAuthor.setAuthorType("Primary");
            } else {
                addManuScriptCoAuthor.setType("Co-author");
                addManuScriptCoAuthor.setAuthorType("Co-author");
            }
            addManuScriptCoAuthor.setTrialRegistrationNo(manuscript.getTrialRegistrationNo());
            addManuScriptCoAuthor.setCopyrightAgreementContent(manuscript.getCopyrightAgreementContent());
            addManuScriptCoAuthor.setCopyrightAgreement(manuscript.getCopyrightAgreement());
        } else {
            if (i.getIsCorrespondingAuthor() != null && i.getIsCorrespondingAuthor().equals("Y")) {
                addManuScriptCoAuthor.setType("Primary");
                addManuScriptCoAuthor.setAuthorType("Primary");
            } else {
                addManuScriptCoAuthor.setType("Co-author");
                addManuScriptCoAuthor.setAuthorType("Co-author");
            }
        }
        addManuScriptCoAuthor.setManuscriptId(i.getCoAuthorManuscriptId());
        addManuScriptCoAuthor.setCorrespondingAuthor(i.getCorrespondingAuthor());
        addManuScriptCoAuthor.setIsCorrespondingAuthor(i.getIsCorrespondingAuthor());
        addManuScriptCoAuthor.setCoAuthorStatus(i.getCoAuthorStatus());

        return addManuScriptCoAuthor;

    }

    private ManuscriptFileVO manuscriptFile(ManuScriptFile i) {

        ManuscriptFileVO manuscriptFileVO = new ManuscriptFileVO();
        manuscriptFileVO.setManuscriptFileId(i.getManuscriptFileId());
        manuscriptFileVO.setFileName(i.getFileName());
        manuscriptFileVO.setFilePath(i.getFilePath());
        manuscriptFileVO.setFileOrder(i.getFileOrder());
        manuscriptFileVO.setFileSize(i.getFileSize());
        manuscriptFileVO.setFileStatus(i.getFileStatus());
        manuscriptFileVO.setFileVersion(i.getFileVersion());
        manuscriptFileVO.setCreatedOn(i.getCreatedOn());
        manuscriptFileVO.setLegend(i.getLegend());
        manuscriptFileVO.setFileType(i.getFileType());
        manuscriptFileVO.setUpdatedOn(i.getUpdatedOn());

        return manuscriptFileVO;

    }

    private FileVo reviewerFile(ManuScriptReviewer j) {
        FileVo a = new FileVo();
        a.setId(j.getId());
        a.setJournalId(j.getJournalId());
        a.setManuscriptId(j.getManuscriptId());
        a.setManuscriptVersion(j.getManuscriptVersion());
        a.setReviewerFileName(j.getReviewerFileName());
//        a.setReviewerFileDisplayName(j.getReviewerFileDisplayName());
        a.setReviewerFilePath(j.getReviewerFilePath());
        a.setReviewerFileSize(j.getReviewerFileSize());
        a.setReviewerId(j.getReviewerId());
        a.setCommentToAuthor(j.getCommentsToAuthor());
        a.setCommentToEditor(j.getCommentsToEditor());
        a.setReviewerResponse(j.getReviewerResponse());
        a.setReviewerEmail(j.getUser().getEmail());
        a.setReviewerFirstName(j.getUser().getFirstName());
        a.setReviewerLastName(j.getUser().getLastName());
        a.setReviewerMiddleName(j.getUser().getMiddleName());
        a.setReviewerSalutation(j.getUser().getSalutation().toString());

        return a;
    }

    private EditorCommentsVo getEditorComment(ManuscriptEditorComment l) {
        List<FileVo> add = new ArrayList<>();
        EditorCommentsVo k = new EditorCommentsVo();
        k.setComment(l.getComment());
        k.setCommentDate(l.getCreatedAt());
        k.setEditorName(
                l.getUser().getSalutation() + "." + l.getUser().getFirstName() + " " + l.getUser().getLastName());
        if (l.getUser().getSalutation() != null)
            k.setSalutation(l.getUser().getSalutation());
        k.setFirstName(l.getUser().getFirstName());
        k.setMiddleName(l.getUser().getMiddleName());
        k.setLastName(l.getUser().getLastName());
        k.setEditorRoleId(l.getRoleId());
        k.setEditorUserId(l.getUserId());
        k.setManuscriptId(l.getManuscriptId());
        k.setManuscriptVersion(l.getManuscriptVersion());
        k.setStatusId(l.getStatusId());
        k.setFileType(l.getFileType());
        k.setFileName(l.getFileName());
        k.setFilePath(l.getFilePath());
        k.setFileSize(l.getFileSize());
        if (l.getReviewerFileId() != null && !l.getReviewerFileId().isEmpty()) {
            String[] reviewerPrimaryId = l.getReviewerFileId().split(";");
            if (reviewerPrimaryId != null && !reviewerPrimaryId.toString().isEmpty()) {
                for (String s : reviewerPrimaryId) {
                    if (s != null && !s.isEmpty()) {
                        ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo.getById(Integer.parseInt(s));
                        if (manuscriptReviewer != null) {
                            FileVo fileVo = reviewerFile(manuscriptReviewer);
                            add.add(fileVo);
                        }
                    }
                }
            }
            k.setReviewerFile(add);
        }

        return k;
    }

    private IDVO getReviewerData(ManuScriptReviewer i) {
        IDVO add = new IDVO();
        add.setUserId(i.getUser().getId());
        add.setEmail(i.getUser().getEmail());
        add.setName(i.getUser().getSalutation() + "." + i.getUser().getFirstName() + " " + i.getUser().getLastName());
        add.setSalutation(i.getUser().getSalutation().toString());
        add.setFirstName(i.getUser().getFirstName());
        add.setMiddleName(i.getUser().getMiddleName());
        add.setLastName(i.getUser().getLastName());
        add.setManuscriptStatus(i.getStatusMaster().getStatusValue());
        if (i.getAssignedDate() != null)
            add.setReviewerAssignDate(i.getAssignedDate());
        if (i.getUnassignedDate() != null)
            add.setReviewerUnAssignDate(i.getUnassignedDate());
        if (i.getCompleteDate() != null)
            add.setReviewerCompleteDate(i.getCompleteDate());
        if (i.getDueDate() != null)
            add.setReviewerDueDate(i.getDueDate());
        if (i.getRejectDate() != null)
            add.setReviewerRejectDate(i.getRejectDate());
        if (i.getStartDate() != null)
            add.setReviewerStartDate(i.getStartDate());
        add.setIsReviewerActive(i.getIsActive());
        add.setManuscriptId(i.getManuscriptId());
        add.setManuscriptVersion(i.getManuscriptVersion());
        add.setManuscriptReviewerId(i.getId());
        return add;
    }

    private IDVO getEditorData(ManuscriptEditor i) {
        IDVO add = new IDVO();
        add.setUserId(i.getUser().getId());
        add.setEmail(i.getUser().getEmail());
        add.setName(i.getUser().getSalutation() + "." + i.getUser().getFirstName() + " "
                + i.getUser().getLastName());
        add.setSalutation(i.getUser().getSalutation().toString());
        add.setFirstName(i.getUser().getFirstName());
        add.setMiddleName(i.getUser().getMiddleName());
        add.setLastName(i.getUser().getLastName());
        if (i.getAssignedDate() != null)
            add.setEditorAssignDate(i.getAssignedDate());
        if (i.getEditorDecisionDate() != null)
            add.setEditorDecisionDate(i.getEditorDecisionDate());
        return add;
    }

    // Added by Utkarsh
    private ManuscriptEditorCommentsVO getCommentByEditorAndEIC(AuthoritiesHistory mec) {

        ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
        mecv.setId(mec.getId());
        mecv.setManuscriptStatus(mec.getStatusMaster().getStatusValue());
        mecv.setCommentsForAuthor(mec.getCommentsToAuthor());
        mecv.setManuscriptId(mec.getManuscriptId());
        mecv.setManuscriptVersion(mec.getManuscriptVersion());
        mecv.setCommentsForEditor(mec.getCommentsToEditor());
        mecv.setCommentsForManagingEditor(mec.getCommentsToEIC());
        mecv.setCommentsForReviewer(mec.getCommentsToReviewer());
        mecv.setCreatedOn(mec.getCreatedAt());
        mecv.setAssignDate(mec.getDueDate());
        Integer previousUserRole;
        if (mec.getPreviousUserRole() != null) {
            previousUserRole = mec.getPreviousUserRole();
        } else {
            previousUserRole = 0;
        }
        mecv.setStatusId(-1);
        Integer currentUserRole = mec.getCurrentUserRole();
        if (currentUserRole == 2) {
            mecv.setCurrentRole(2);
        } else if (currentUserRole == 3) {
            mecv.setCurrentRole(3);
        }
        if (previousUserRole == 2) {
            mecv.setCommentRole("EIC");
        } else if (previousUserRole == 3) {
            mecv.setCommentRole("Editor");
        }
        if (mec.getPreviousUserId() != null) {
            User user = userRepo.getByID(mec.getPreviousUserId());
            String salutation = String.valueOf(user.getSalutation());
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            mecv.setUserName(salutation + "." + firstName + " " + lastName);

        }
        return mecv;

    }

    public ManuscriptReviewerCommentsVO getReviewerCommentsByAuth(AuthoritiesHistory mec) {
        ManuscriptReviewerCommentsVO mecv = new ManuscriptReviewerCommentsVO();
        mecv.setManuscriptStatus(mec.getStatusMaster().getStatusValue());
        mecv.setManuscriptId(mec.getManuscriptId());
        mecv.setManuscriptVersion(mec.getManuscriptVersion());
        mecv.setCommentsForReviewer(mec.getCommentsToReviewer());
        mecv.setCreatedOn(mec.getCreatedAt());
        mecv.setAssignDate(mec.getDueDate());
        mecv.setStatusId(-1);
        if (mec.getPreviousUserId() != null) {
            User user = userRepo.getByID(mec.getPreviousUserId());
            String salutation = String.valueOf(user.getSalutation());
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            mecv.setUserName(salutation + "." + firstName + " " + lastName);

        }


        return mecv;
    }


    private ManuscriptEditorCommentsVO getEditorComment(ManuscriptEditor mec) {
        ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
        mecv.setManuscriptStatus(mec.getStatusMaster().getStatusValue());
        // Added by Mudit
        mecv.setCommentsForAuthor(mec.getCommentsForAuthor());
        mecv.setManuscriptId(mec.getManuscriptId());
        mecv.setManuscriptVersion(mec.getManuscriptVersion());
        mecv.setCommentsForEditor(mec.getCommentsForEditor());
        mecv.setCommentsForManagingEditor(mec.getCommentsForManagingEditor());
        mecv.setCommentsForReviewer(mec.getCommentsForReviewer());
        mecv.setCreatedOn(mec.getCreatedOn());
        mecv.setAssignDate(mec.getAssignedDate());
        // Added by Utkarsh
//        if (mec.getManagingEditorId() != null) {
//            User user = userRepo.getByID(mec.getManagingEditorId());
//            String salutation = String.valueOf(user.getSalutation());
//            String firstName = user.getFirstName();
//            String lastName = user.getLastName();
//            mecv.setUserName(salutation + "." + firstName + " " + lastName);
//
//        } else {
//            mecv.setUserName("");
//        }
        if (StringUtils.isNotBlank(mec.getDecisionLetter()))
            mecv.setDecisionLetter(mec.getDecisionLetter());
        mecv.setUserName(mec.getUser().getSalutation() + "." + mec.getUser().getFirstName() + " " + mec.getUser().getLastName());
        return mecv;
    }

    private ManuscriptEditorCommentsVO getFinalRecommedentationEditorCommentWithFile(AuthoritiesHistory mec, ManuScriptFile revisionFile) {
        ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
        mecv.setId(mec.getId());
        mecv.setManuscriptStatus(mec.getStatusMaster().getStatusValue());
        mecv.setCommentsForAuthor(mec.getCommentsToAuthor());
        mecv.setManuscriptId(mec.getManuscriptId());
        mecv.setManuscriptVersion(mec.getManuscriptVersion());
        mecv.setCommentsForEditor(mec.getCommentsToEditor());
        mecv.setCommentsForManagingEditor(mec.getCommentsToEIC());
        mecv.setCommentsForReviewer(mec.getCommentsToReviewer());
        mecv.setCreatedOn(mec.getCreatedAt());
        Integer previousUserRole;
        if (mec.getPreviousUserRole() != null) {
            previousUserRole = mec.getPreviousUserRole();
        } else {
            previousUserRole = 0;
        }
        Integer currentUserRole = mec.getCurrentUserRole();
        if (previousUserRole == 2) {
            mecv.setCommentRole("EIC");
        } else if (previousUserRole == 3) {
            mecv.setCommentRole("Editor");
        }

        if (currentUserRole == 2) {
            mecv.setCurrentRole(2);
        } else if (currentUserRole == 3) {
            mecv.setCurrentRole(3);
        }
        if (revisionFile != null) {
            mecv.setReviewerFileName(revisionFile.getFileName());
            mecv.setReviewerFilePath(revisionFile.getFilePath());
            mecv.setReviewerFileSize(revisionFile.getFileSize());
            mecv.setReviewerFileType(revisionFile.getFileType());
            mecv.setUpdatedOn(revisionFile.getUpdatedOn());
        }
        if (mec.getPreviousUserId() > 0) {
            User user = userRepo.getById(mec.getPreviousUserId());
            mecv.setUserName(user.getSalutation() + "." + user.getFirstName() + " " + user.getLastName());
        }
        return mecv;
    }

    private ManuscriptEditorCommentsVO getFinalRecommedentationEditorCommentWithFileForAcceptReject(AuthoritiesHistory mec, ManuScriptFile revisionFile, ManuscriptEditorCommentsVO mecv) {
        if (revisionFile != null) {
            mecv.setReviewerFileName(revisionFile.getFileName());
            mecv.setReviewerFilePath(revisionFile.getFilePath());
            mecv.setReviewerFileSize(revisionFile.getFileSize());
            mecv.setReviewerFileType(revisionFile.getFileType());
            mecv.setUpdatedOn(revisionFile.getUpdatedOn());

            System.out.println(mecv.getUpdatedOn());
            System.out.println(revisionFile.getUpdatedOn());

        }
        return mecv;
    }


    private ManuscriptEditorCommentsVO getSendBackEICComment(ManuscriptEditorComment mec) {
        ManuscriptEditorCommentsVO mecv = new ManuscriptEditorCommentsVO();
        mecv.setManuscriptStatus("Send Back To EIC");
        mecv.setManuscriptId(mec.getManuscriptId());
        mecv.setManuscriptVersion(mec.getManuscriptVersion());
        mecv.setCommentsForManagingEditor(mec.getComment());
        mecv.setSendBackEicComment(mec.getComment());
        mecv.setCreatedOn(mec.getCreatedAt());
        mecv.setUserName(mec.getUser().getSalutation() + "." + mec.getUser().getFirstName() + " " + mec.getUser().getLastName());
        mecv.setReviewerFileName(mec.getFileName());
        mecv.setReviewerFilePath(mec.getFilePath());
        mecv.setReviewerFileSize(mec.getFileSize());
        mecv.setReviewerFileType(mec.getFileType());
        return mecv;
    }
    
    public String getUploadedDateFromPath(String reviewerFileUrl) {
        try {
            // Remove base domain dynamically
            String relativePath = reviewerFileUrl
                    .replace("https://uniprrnode.digiscapetech.com", "")
                    .replace("https://uniprrcdn.digiscapetech.com", "");

            // Convert to Linux path
            String linuxPath = "/var/www/download" + relativePath;

            File file = new File(linuxPath);
            if (file.exists()) {
                long lastModified = file.lastModified();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy"); // date only
                return sdf.format(new Date(lastModified));
            }
        } catch (Exception e) {
            // Optional: log the error
        }
        return null;
    }
    
    public Date getUploadedDate(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                return null;
            }

            // Remove base domain dynamically
            String relativePath = fileUrl
                    .replace("https://uniprrnode.digiscapetech.com", "")
                    .replace("https://uniprrcdn.digiscapetech.com", "");

            // Convert to Linux path
            String linuxPath = "/var/www/download" + relativePath;

            File file = new File(linuxPath);
            if (file.exists()) {
                long lastModified = file.lastModified();
                return new Date(lastModified); // ✅ directly return Date
            }
        } catch (Exception e) {
            e.printStackTrace(); // or use proper logging
        }
        return null;
    }




}
