package com.digi.uniprr.controller;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.digi.uniprr.VO.*;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.FileUploadValidation;
//import com.digi.uniprr.utils.FileUploadValidator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuScriptService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/manuScriptReviewer")
public class ManuScriptReviewerController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ManuScriptReviewerService manuScriptReviewerService;

    @Autowired
    JournalsRepo journalsRepo;

    @Autowired
    UserLoginRepo userLoginRepo;

    @Autowired
    ManuScriptRepo manuScriptRepo;

    @Autowired
    ManuScriptReviewerRepo manuScriptReviewerRepo;

    @Autowired
    ManuScriptService manuScriptService;

    @Autowired
    EmailTriggerService emailTriggerService;

    @Autowired
    AuthoritiesHistoryRepo authoritiesHistoryRepo;

    @Autowired
    WorkflowRoleRepo workflowRoleRepo;

    JSONObject obj = new JSONObject();

    @RequestMapping(value = "/addManuScriptReviewer", method = RequestMethod.POST)
    public ResponseEntity<?> addManuScriptReviewer(@RequestBody ManuScriptReviewer manuScriptReviewer) {
        manuScriptReviewerService.addReviewerDeatils(manuScriptReviewer);
        return new ResponseEntity<>("Added Sucessfully", HttpStatus.OK);
    }

    @PostMapping("addReviwerIdWithArray")
    public ResponseEntity<?> addCheckListMaster(@RequestBody ManuScriptReviewerVO manuScriptReviewerVo) {
        return new ResponseEntity<>(manuScriptReviewerService.saveManuScriptReviewer(manuScriptReviewerVo),
                HttpStatus.OK);
    }

    /*
     * @RequestMapping(value = "/updateManuScriptReviewer", method =
     * RequestMethod.POST) public ResponseEntity<?>
     * updateManuScriptReviewer(@RequestBody ManuScriptReviewer manuScriptReviewer)
     * { if (manuScriptReviewerService.isExist(manuScriptReviewer.getManuscriptId(),
     * manuScriptReviewer.getReviewerId())) {
     * manuScriptReviewerService.updateReviewerDeatils(manuScriptReviewer); return
     * new ResponseEntity<Object>("Updated successfully", HttpStatus.OK); } return
     * new ResponseEntity<Object>("Does not exist", HttpStatus.FORBIDDEN); }
     */

    @RequestMapping(value = "/updateManuScriptReviewer", method = RequestMethod.POST)
    public ResponseEntity<?> updateManuScriptReviewer(@RequestBody ManuScriptReviewerVO manuScriptReviewerVo) {
        log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST +
                AppConstants.REQUEST_URI + " :: /manuScriptReviewer/updateManuScriptReviewer" +
                AppConstants.REQUEST_BODY + " :: " + manuScriptReviewerVo.toString());
        JSONObject obj = new JSONObject();
        try {

            boolean isAssignExist = false;
            boolean isDeclineExist = false;

            Integer invitedReviewerCount = manuScriptReviewerService.getInvitedreviewerCount(manuScriptReviewerVo);
            if (invitedReviewerCount < 1) {
                isAssignExist = manuScriptReviewerService.isAssignExist(manuScriptReviewerVo);
                isDeclineExist = manuScriptReviewerService.isDeclineExist(manuScriptReviewerVo);
            }
            if (isDeclineExist) {
                obj.put("updated", "Task is already decline,link expire.");
            } else if (isAssignExist) {
                obj.put("updated", "Task is already accepted.");
            } else {
                String message1 = manuScriptReviewerService.updateReviewerDeatils(manuScriptReviewerVo);
                if (message1.equalsIgnoreCase("Updated Data Sucessfully.")) {
                    obj.put("updated", "Updated Data Sucessfully.");

                    // Email Trigger
                    if (manuScriptReviewerVo.getManuscriptId() != null && manuScriptReviewerVo.getJournalId() != null
                            && manuScriptReviewerVo.getReviewerId() != null) {
                        ManuScriptReviewer getManuScriptReviewerDetails = manuScriptReviewerRepo
                                .getManuScriptReviewerEditor(manuScriptReviewerVo.getManuscriptId(),
                                        manuScriptReviewerVo.getManuscriptVersion(),
                                        manuScriptReviewerVo.getReviewerId(), manuScriptReviewerVo.getJournalId());
                        if (getManuScriptReviewerDetails != null) {

                            ManuScript getManuScript1 = manuScriptRepo.getById(manuScriptReviewerVo.getManuscriptId());
                            if (getManuScript1 != null) {
                                if (manuScriptReviewerVo.getManuscriptStatus().equals(35)) {
                                    EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                                    emailTriggerVO.setJournalId(manuScriptReviewerVo.getJournalId());
                                    emailTriggerVO.setUserId(manuScriptReviewerVo.getReviewerId());
                                    emailTriggerVO.setReviewerUserId(manuScriptReviewerVo.getReviewerId());
                                    emailTriggerVO.setManuscriptId(manuScriptReviewerVo.getManuscriptId());
                                    emailTriggerVO.setManuscriptStatus(manuScriptReviewerVo.getManuscriptStatus());
                                    emailTriggerVO.setReceiverRoleId(5);
                                    if (getManuScriptReviewerDetails.getEditorRoleId() != null
                                            && getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
                                        emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
                                    } else {
                                        emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());
                                    }
                                    if (getManuScript1.getManuscriptStatusId().equals(66)) {
                                        emailTriggerVO.setTaskId(77);
                                    } else {
                                        emailTriggerVO.setTaskId(32);
                                    }

                                    //emailTriggerVO.setTaskId(32);
                                    emailTriggerVO.setRoleId(5);
                                    String message = emailTriggerService.emailTrigger(emailTriggerVO);
                                    obj.put("data", message);

                                }

                                if (manuScriptReviewerVo.getManuscriptStatus().equals(35)) {
                                    EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                                    emailTriggerVO.setJournalId(manuScriptReviewerVo.getJournalId());
                                    emailTriggerVO.setUserId(getManuScriptReviewerDetails.getEditorId());
                                    emailTriggerVO.setReviewerUserId(manuScriptReviewerVo.getReviewerId());
                                    emailTriggerVO.setManuscriptId(manuScriptReviewerVo.getManuscriptId());
                                    emailTriggerVO.setManuscriptStatus(manuScriptReviewerVo.getManuscriptStatus());
                                    emailTriggerVO.setReceiverRoleId(getManuScriptReviewerDetails.getEditorRoleId());
                                    if (getManuScriptReviewerDetails.getEditorRoleId() != null
                                            && getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
                                        emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
                                    } else {
                                        emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());
                                    }
                                    if (getManuScript1.getManuscriptStatusId().equals(66)) {
                                        emailTriggerVO.setTaskId(78);
                                    } else {
                                        emailTriggerVO.setTaskId(5);
                                    }
                                    //emailTriggerVO.setTaskId(5);
                                    emailTriggerVO.setRoleId(5);
                                    String message = emailTriggerService.emailTrigger(emailTriggerVO);
                                    obj.put("data", message);
                                }

                                if (manuScriptReviewerVo.getManuscriptStatus().equals(36)) {
                                    EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                                    emailTriggerVO.setJournalId(manuScriptReviewerVo.getJournalId());
                                    emailTriggerVO.setUserId(getManuScriptReviewerDetails.getEditorId());
                                    emailTriggerVO.setReviewerUserId(manuScriptReviewerVo.getReviewerId());
                                    emailTriggerVO.setManuscriptId(manuScriptReviewerVo.getManuscriptId());
                                    emailTriggerVO.setManuscriptStatus(manuScriptReviewerVo.getManuscriptStatus());
                                    emailTriggerVO.setReceiverRoleId(getManuScriptReviewerDetails.getEditorRoleId());
                                    if (getManuScriptReviewerDetails.getEditorRoleId() != null
                                            && getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
                                        emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
                                    } else {
                                        emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());

                                    }
                                    if (getManuScript1.getManuscriptStatusId().equals(66)) {
                                        emailTriggerVO.setTaskId(80);
                                    } else {
                                        emailTriggerVO.setTaskId(6);
                                    }
//								emailTriggerVO.setTaskId(6);
                                    emailTriggerVO.setRoleId(5);
                                    String message = emailTriggerService.emailTrigger(emailTriggerVO);
                                    obj.put("data", message);

                                }

                                if (manuScriptReviewerVo.getManuscriptStatus().equals(36)) {
                                    EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                                    emailTriggerVO.setJournalId(manuScriptReviewerVo.getJournalId());
                                    emailTriggerVO.setUserId(getManuScriptReviewerDetails.getReviewerId());
                                    emailTriggerVO.setReviewerUserId(manuScriptReviewerVo.getReviewerId());
                                    emailTriggerVO.setManuscriptId(manuScriptReviewerVo.getManuscriptId());
                                    emailTriggerVO.setManuscriptStatus(manuScriptReviewerVo.getManuscriptStatus());
                                    emailTriggerVO.setReceiverRoleId(getManuScriptReviewerDetails.getEditorRoleId());
                                    emailTriggerVO.setReceiverRoleId(5);
                                    if (getManuScriptReviewerDetails.getEditorRoleId() != null
                                            && getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
                                        emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
                                    } else {
                                        emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());
                                    }

                                    if (getManuScript1.getManuscriptStatusId().equals(66)) {
                                        emailTriggerVO.setTaskId(79);
                                    } else {
                                        emailTriggerVO.setTaskId(33);
                                    }
//								emailTriggerVO.setTaskId(33);
                                    emailTriggerVO.setRoleId(5);
                                    String message = emailTriggerService.emailTrigger(emailTriggerVO);
                                    obj.put("data", message);
                                }

                            }


                            ///
                        } else {
                            obj.put("data", "There is no editor to trigger email.");
                        }
                    }

                } else {
                    obj.put("updated", "There is no data to update.");
                }

            }

            return new ResponseEntity<Object>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("updated", "Does not exist Table.");
            return new ResponseEntity<Object>(obj, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("addMultipleRecord")
    public ResponseEntity<?> addMultipleRecord(@RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        try {
            manuScriptReviewerService.addAllDetailsMultipleTableRCC(manuScriptReviewerCommonVO);
            obj.put("data", "Data inserted Succesfully...");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong..");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

    @PostMapping("getManuscriptReviewer")
    public ResponseEntity<?> getManuscriptEditor(@RequestBody UserJournalManuscriptVo userJournalManuscriptVo) {
        List<ManuScriptReviewer> manuscriptReviewerList = manuScriptReviewerService
                .getManuscriptReviewer(userJournalManuscriptVo);
        obj.put("data", manuscriptReviewerList);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getReviewerByManuScriptIdAndVersion")
    public ResponseEntity<?> getReviewerByManuScriptIdAndVersion(
            @RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        try {
            List<ManuScriptReviewerCommonVO> data = manuScriptReviewerService.getReviewerByManuScriptIdAndVersion(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion());
            obj.put("data", data);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong..");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
    }

    @PostMapping("getReviewerDataByManuscriptId")
    public ResponseEntity<?> getReviewerDataByManuscriptId(
            @RequestBody ManuscriptReviewerCommentsVO manuScriptReviewerCommonVO) {
        try {
            List<ManuscriptReviewerCommentsVO> data = manuScriptReviewerService.getReviewerDataByManuscriptId(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getReviewerId());
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
            } else {
                obj.put("data", "No data found");
            }
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong..");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
    }

    @PostMapping("getReviewerDataById")
    public ResponseEntity<?> getReviewerDataById(@RequestBody CommonVO id) {
        try {
            List<ReviewerAvailabilityStatusVO> data = manuScriptReviewerService.getReviewerDataById(id);
            obj.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getReviewerStaticsData")
    public ResponseEntity<?> getReviewerStaticsData(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<ReviewerAvailabilityStatusVO> data = manuScriptReviewerService.getReviewerStaticsData(id);
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "Data not found");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

    @PostMapping("getReviewerDataByManuscriptVersion")
    public ResponseEntity<?> getReviewerDataByManuscriptVersion(
            @RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        try {
            List<ManuScriptReviewerCommonVO> data = manuScriptReviewerService.getReviewerDataByManuscriptVersion(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());
            obj.put("data", data);
        } catch (Exception e) {
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getUniqueReviewer")
    public ResponseEntity<?> getUniqueReviewer(@RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        int count = 0;
        try {

            List<ManuScriptReviewerCommonVO> assignedReviewer = manuScriptReviewerService.getAssignedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());
            List<ManuScriptReviewerCommonVO> unassignedReviewer = manuScriptReviewerService
                    .getReviewerDataByManuscriptVersion(manuScriptReviewerCommonVO.getManuscriptId(),
                            manuScriptReviewerCommonVO.getManuscriptVersion(),
                            manuScriptReviewerCommonVO.getJournalId());
            List<AuthorSuggestedReviewerVo> authorSuggestedReviewer = manuScriptReviewerService.authorSuggestedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());
            List<ManuScriptReviewerCommonVO> systemSuggestedReviewer = manuScriptReviewerService
                    .systemSuggestedReviewer(manuScriptReviewerCommonVO.getManuscriptId(),
                            manuScriptReviewerCommonVO.getManuscriptVersion(),
                            manuScriptReviewerCommonVO.getJournalId());
            List<ManuScriptReviewerCommonVO> intrestedToReAccess = manuScriptReviewerService.intrestedToReAccess(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());
            Date getDueDate = manuScriptReviewerService.getDueDate(manuScriptReviewerCommonVO);
            List<ManuScript> manuScriptDetail = manuScriptService
                    .trackManuScript(manuScriptReviewerCommonVO.getManuscriptId());
            List<AuthorSuggestedReviewerVo> authorOpposedReviewer = manuScriptReviewerService.authorOpposedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getJournalId());
            if (getDueDate != null)
                obj.put("duedate", getDueDate);
            else
                obj.put("duedate", "There is no duedate");

            if (assignedReviewer != null && !assignedReviewer.isEmpty()) {
                for (ManuScriptReviewerCommonVO tempData : assignedReviewer) {
                    tempData.setTempFlag("true");
                }
            }
            obj.put("copyrightAgreement", manuScriptDetail.get(0).getCopyrightAgreement());
            obj.put("copyrightAgreementContent", manuScriptDetail.get(0).getCopyrightAgreementContent());
            obj.put("declarationOfInterest", manuScriptDetail.get(0).getDeclarationOfInterest());
            obj.put("declarationOfInterestContent", manuScriptDetail.get(0).getDeclarationOfInterestContent());
            obj.put("assignedReviewer", assignedReviewer);
            obj.put("unassignedReviewer", unassignedReviewer);
            obj.put("systemSuggestedReviewer", systemSuggestedReviewer);
            obj.put("authorSuggestedReviewer", authorSuggestedReviewer);
            obj.put("intrestedToReAccess", intrestedToReAccess);
            obj.put("authorOpposedReviewer", authorOpposedReviewer);

            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            log.error("Exception : " + e.getMessage());
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

    @PostMapping("getReviewedManuscript")
    public ResponseEntity<?> getReviewedManuscript(@RequestBody IDVO id) {
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getReviewedManuscript(id);

            if (id.getSortBy().equalsIgnoreCase("manuscripCode")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripCode,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripCode,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("manuscripTitle")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripTitle,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripTitle,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("manuScriptType")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuScriptType,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuScriptType,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("editorName")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getEditorName,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getEditorName,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("authorName")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAuthorName,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAuthorName,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("submissionDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            if (id.getSortBy().equalsIgnoreCase("assignDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            if (id.getSortBy().equalsIgnoreCase("dueDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getNoResponseManuscript")
    public ResponseEntity<?> getNoResponseManuscript(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getNoResponseManuscript(id);

            if (id.getSortBy().equalsIgnoreCase("manuscripCode")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripCode,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripCode,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("manuscripTitle")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripTitle,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripTitle,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("manuScriptType")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuScriptType,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuScriptType,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("editorName")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getEditorName,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getEditorName,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("authorName")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAuthorName,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAuthorName,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("submissionDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            if (id.getSortBy().equalsIgnoreCase("assignDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            if (id.getSortBy().equalsIgnoreCase("dueDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getCommentaryData")
    public ResponseEntity<?> getCommentaryData(@RequestBody IDVO id) {
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getCommentaryData(id.getJournalId());
            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getCommentaryInvitation")
    public ResponseEntity<?> getCommentaryInvitation(@RequestBody IDVO id) {
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getCommentaryInvitation(id.getJournalId(),
                    id.getUserId());
            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getReviewerStatistics")
    public ResponseEntity<?> getReviewerStatistics(@RequestBody IDVO id) {
        try {
            ManuscriptListVO data = manuScriptReviewerService.getReviewerStatistics(id.getJournalId(), id.getUserId());
            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            // return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getReviewerByJournal")
    public ResponseEntity<?> getReviewerByJournal(@RequestBody IDVO id) {
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getReviewerByJournal(id.getJournalId());
            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            // return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getInReviewManuscript")
    public ResponseEntity<?> getInReviewManuscript(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getInReviewManuscript(id);

            if (id.getSortBy().equalsIgnoreCase("manuscripCode")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripCode,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripCode,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("manuscripTitle")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripTitle,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuscripTitle,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("manuScriptType")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuScriptType,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getManuScriptType,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("editorName")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getEditorName,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getEditorName,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("authorName")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAuthorName,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAuthorName,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }

            }

            if (id.getSortBy().equalsIgnoreCase("submissionDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getSubmissionDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            if (id.getSortBy().equalsIgnoreCase("assignDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getAssignDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }

            if (id.getSortBy().equalsIgnoreCase("dueDate")) {

                if (id.getSort().equalsIgnoreCase("asc")) {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
                            Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                } else {
                    data = data.stream().sorted(Comparator.comparing(ManuscriptListVO::getDueDate,
                            Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                }
            }


            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getReviewerOnManuscript")
    public ResponseEntity<?> getReviewerOnManuscript(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getReviewerOnManuscript(id);
            obj.put("data", data);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("deActivateReviewer")
    public ResponseEntity<?> deActivateReviewer(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<ManuScriptReviewer> list = manuScriptReviewerRepo.isArticleReviewedByReviewer(id.getManuscriptId(),
                    id.getManuscriptVersion(), id.getId());
            if (list.size() > 0) {
                ManuScriptReviewer manuscriptReviewer = list.get(0);
                if (manuscriptReviewer != null && manuscriptReviewer.getIsActive() != null) {
                    if (manuscriptReviewer.getIsActive() == 0) {
                        log.info("Reviewer already deactivate ");
                        // obj.put("data", "Reviewer already deactivate");
                    } else if (manuscriptReviewer.getIsActive() == 1
                            && ((manuscriptReviewer.getManuscriptStatus() == 12)
                            || (manuscriptReviewer.getManuscriptStatus() == 13)
                            || (manuscriptReviewer.getManuscriptStatus() == 14)
                            || (manuscriptReviewer.getManuscriptStatus() == 15))) {
                        log.info("Reviewer has already reviewed manuscript");
                        // obj.put("data", "Reviewer has already reviewed manuscript");
                    } else {
                        manuScriptReviewerService.deActivateReviewer(id, manuscriptReviewer.getId());

                        if (id.getId() != null && id.getManuscriptId() != null && id.getJournalId() != null
                                && id.getUserId() != null && id.getRoleId() != null) {

                            AuthoritiesHistory i = new AuthoritiesHistory();
                            i.setCurrentUserId(id.getUserId());
                            i.setCurrentUserRole(id.getRoleId());
                            i.setManuscriptStatusId(67);// reviewer removed
                            i.setPreviousUserId(id.getId());
                            i.setPreviousUserRole(5);// Reviewer
                            i.setManuscriptId(id.getManuscriptId());
                            i.setManuscriptVersion(id.getManuscriptVersion());
                            i.setJournalId(id.getJournalId());
                            i.setCreatedAt(new Date());
                            authoritiesHistoryRepo.save(i);

                            Integer revCount = manuScriptReviewerRepo.getRemovedReViewerCount(id.getManuscriptId(),
                                    id.getJournalId(), id.getManuscriptVersion());
                            if (revCount.equals(0)) {
                                ManuScript manu = manuScriptRepo.getById(id.getManuscriptId());
                                AuthoritiesHistory authHis = new AuthoritiesHistory();
                                Journal getjournalDetails = journalsRepo.getOne(id.getJournalId());

                                if (getjournalDetails != null) {
                                    if (getjournalDetails.getJournalMetadata() != null) {
                                        if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
                                            List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo
                                                    .getJournalWorkflow(
                                                            getjournalDetails.getJournalMetadata().getWorkflowId(),
                                                            getjournalDetails.getJournalId());
                                            if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
                                                for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
                                                    Integer tat = 0;
                                                    if (manu.getManuscriptStatusId().equals(26)
                                                            && l.getTaskId().equals(19)) {
                                                        tat = l.getTat();
                                                        authHis.setDueDate(tat.equals(0) ? new Date()
                                                                : new Date().from(LocalDate.now().plusDays(l.getTat())
                                                                .atStartOfDay(ZoneId.systemDefault())
                                                                .toInstant()));
                                                    }
                                                    if (manu.getManuscriptStatusId().equals(50)
                                                            && l.getTaskId().equals(20)) {
                                                        tat = l.getTat();
                                                        authHis.setDueDate(tat.equals(0) ? new Date()
                                                                : new Date().from(LocalDate.now().plusDays(l.getTat())
                                                                .atStartOfDay(ZoneId.systemDefault())
                                                                .toInstant()));
                                                    }
                                                    if (manu.getManuscriptStatusId().equals(3)
                                                            && l.getTaskId().equals(3)) {
                                                        tat = l.getTat();
                                                        authHis.setDueDate(tat.equals(0) ? new Date()
                                                                : new Date().from(LocalDate.now().plusDays(l.getTat())
                                                                .atStartOfDay(ZoneId.systemDefault())
                                                                .toInstant()));
                                                    }
                                                    if (manu.getManuscriptStatusId().equals(2)
                                                            && l.getTaskId().equals(13)) {
                                                        tat = l.getTat();
                                                        authHis.setDueDate(tat.equals(0) ? new Date()
                                                                : new Date().from(LocalDate.now().plusDays(l.getTat())
                                                                .atStartOfDay(ZoneId.systemDefault())
                                                                .toInstant()));
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                                authHis.setCurrentUserId(id.getUserId());
                                authHis.setCurrentUserRole(id.getRoleId());
                                authHis.setManuscriptStatusId(manu.getManuscriptStatusId());// reviewer removed
                                authHis.setPreviousUserId(id.getId());
                                authHis.setPreviousUserRole(5);// Reviewer
                                authHis.setManuscriptId(id.getManuscriptId());
                                authHis.setManuscriptVersion(id.getManuscriptVersion());
                                authHis.setJournalId(id.getJournalId());
                                authHis.setCreatedAt(new Date());
                                authoritiesHistoryRepo.save(authHis);
                                manu.setDueDate(authHis.getDueDate());
                                manuScriptRepo.save(manu);

                            }

                            EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                            emailTriggerVO.setJournalId(id.getJournalId());
                            emailTriggerVO.setUserId(id.getId());
                            emailTriggerVO.setReviewerUserId(id.getId());
                            if (id.getRoleId().equals(2))
                                emailTriggerVO.setManagingEditorId(id.getUserId());
                            else
                                emailTriggerVO.setEditorUserId(id.getUserId());
                            emailTriggerVO.setManuscriptId(id.getManuscriptId());
                            emailTriggerVO.setManuscriptStatus(35);
                            emailTriggerVO.setReceiverRoleId(5);
                            emailTriggerVO.setTaskId(21);
                            emailTriggerVO.setRoleId(id.getRoleId());
                            if (id.getJournalMailTemplatesSubject() != null && !id.getJournalMailTemplatesSubject().isEmpty())
                                emailTriggerVO.setEmailTemplateSubject(id.getJournalMailTemplatesSubject());
                            if (id.getJournalMailTemplatesBody() != null && !id.getJournalMailTemplatesBody().isEmpty())
                                emailTriggerVO.setEmailTemplateBody(id.getJournalMailTemplatesBody());
                            log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
                                    + emailTriggerVO.toString());
                            log.info("User Receiver Id:->" + id.getId());
                            log.info("User Receiver Role Id:->" + 5);
                            log.info("User Sender Role Id:->" + id.getRoleId());
                            log.info("User Sender Id:->" + id.getUserId());
                            log.info("Task Id:->" + 21);
                            String message = emailTriggerService.emailTrigger(emailTriggerVO);
                            obj.put("data", message);
                            log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
                            log.info("End of assignUser Role For Reviewer For Email Trigger ");
                            obj.put("data", "Reviewer deactivated...");
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("decliendReview")
    public ResponseEntity<?> decliendReview(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            manuScriptReviewerService.decliendReview(id);
            obj.put("data", "Reviewer decliend...");

            //=============
            // Email Trigger
            if (id.getManuscriptId() != null && id.getJournalId() != null
                    && id.getUserId() != null) {
                List<ManuScriptReviewer> getManuScriptReviewers = manuScriptReviewerRepo
                        .getManuScriptReviewerDeclineManuscriptDecline(id.getManuscriptId(),
                                id.getManuscriptVersion(), id.getUserId(),
                                id.getJournalId());
                ManuScriptReviewer getManuScriptReviewerDetails= getManuScriptReviewers.get(0);
                String message = "Email trigger successfully";
                if (getManuScriptReviewerDetails != null) {
                    if (id.getManuId().equals(60)) {
                        EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                        emailTriggerVO.setJournalId(id.getJournalId());
                        emailTriggerVO.setUserId(getManuScriptReviewerDetails.getEditorId());
                        emailTriggerVO.setReviewerUserId(id.getUserId());
                        emailTriggerVO.setManuscriptId(id.getManuscriptId());
                        emailTriggerVO.setManuscriptStatus(60);
                        emailTriggerVO.setReceiverRoleId(getManuScriptReviewerDetails.getEditorRoleId());
                        if (getManuScriptReviewerDetails.getEditorRoleId() != null
                                && getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
                            emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
                        } else {
                            emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());

                        }

                        ManuScript getManuScript1 = manuScriptRepo.getById(id.getManuscriptId());
                        if (getManuScript1 != null) {
                            if (id.getFolder().equals("Manuscript for Re-review")) {
                                emailTriggerVO.setTaskId(80);
                            } else {
                                emailTriggerVO.setTaskId(6);
                            }
                        }
//									emailTriggerVO.setTaskId(6);
                        emailTriggerVO.setRoleId(5);
                        message = emailTriggerService.emailTrigger(emailTriggerVO);
                    }

                    if (id.getManuId().equals(60)) {
                        EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                        emailTriggerVO.setJournalId(id.getJournalId());
                        emailTriggerVO.setUserId(getManuScriptReviewerDetails.getReviewerId());
                        emailTriggerVO.setReviewerUserId(id.getUserId());
                        emailTriggerVO.setManuscriptId(id.getManuscriptId());
                        emailTriggerVO.setManuscriptStatus(60);
                        emailTriggerVO.setReceiverRoleId(5);
                        if (getManuScriptReviewerDetails.getEditorRoleId() != null
                                && getManuScriptReviewerDetails.getEditorRoleId().equals(2)) {
                            emailTriggerVO.setManagingEditorId(getManuScriptReviewerDetails.getEditorId());
                        } else {
                            emailTriggerVO.setEditorUserId(getManuScriptReviewerDetails.getEditorId());
                        }

                        ManuScript getManuScript1 = manuScriptRepo.getById(id.getManuscriptId());
                        if (getManuScript1 != null) {
                            if (id.getFolder().equals("Manuscript for Re-review")) {
                                emailTriggerVO.setTaskId(79);
                            } else {
                                emailTriggerVO.setTaskId(33);
                            }
                        }
//									emailTriggerVO.setTaskId(33);
                        emailTriggerVO.setRoleId(5);
                        message = emailTriggerService.emailTrigger(emailTriggerVO);
                    }
                }

                obj.put("data", message);
            }
            //=============
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("reviewerPreviousComment")
    public ResponseEntity<?> reviewerPreviousComment(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<ManuScriptReviewerVO> data = manuScriptReviewerService.reviewerPreviousComment(id);
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
            } else {
                obj.put("data", "Data not found");
            }
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getReviewManuscriptDashboardCount")
    public ResponseEntity<?> getReviewManuscriptDashboardCount(@RequestBody IDVO id) {
        log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST +
                AppConstants.REQUEST_URI + " :: /manuScriptReviewer/getReviewManuscriptDashboardCount" +
                AppConstants.REQUEST_BODY + " ::" + id.toString());
        JSONObject obj = new JSONObject();
        try {
            ReviewerDashboardCountVO data = manuScriptReviewerService.getReviewerDashboardCount(id);
            if (data != null) {
                obj.put("data", data);
            } else {
                obj.put("data", "Data not found");
            }
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getReRivewManuscript")
    public ResponseEntity<?> getReRivewManuscript(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<EditorDashboardVO> data = manuScriptReviewerService.getReRivewManuscript(id);
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
            } else {
                obj.put("data", "Data not found");
            }
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getManuscriptForReview")
    public ResponseEntity<?> getManuscriptForReview(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<EditorDashboardVO> data = manuScriptReviewerService.getManuscriptForReview(id);
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
            } else {
                obj.put("data", "Data not found");
            }
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("updateManuscriptReviewerResponse")
    public ResponseEntity<?> updateManuscriptReviewerResponse(@ModelAttribute ReviewerResponseVo reviewerResponseVo,
                                                              HttpServletRequest request) {
        JSONObject obj = new JSONObject();
        try {
            if (reviewerResponseVo == null) {
                log.error("Reviewer response data is null.");
                obj.put("data", "Reviewer response data is null.");
                return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
            }

            String data = manuScriptReviewerService.updateManuscriptReviewerResponse(reviewerResponseVo, request);

            if ("Successfully updated".equalsIgnoreCase(data)) {
                obj.put("data", data);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                log.error("Failed to update manuscript reviewer response: " + data);
                obj.put("data", data);
                return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Exception occurred while updating manuscript reviewer response: " + e.getMessage(), e);
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("getAllReviewedManuscript")
    public ResponseEntity<?> getAllReviewedManuscript(@RequestBody IDVO id, HttpServletRequest request) {
        JSONObject obj = new JSONObject();
        try {
            List<ManuscriptListVO> data = manuScriptReviewerService.getAllReviewedManuscript(id);
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
            } else {
                obj.put("data", "Data not found");
            }

            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/getReviewedManuscriptForReviewer", method = RequestMethod.POST)
    public ResponseEntity<?> getReviewedManuscriptForReviewer(
            @RequestBody UserJournalManuscriptVo userJournalManuscriptVo) {
        log.info("Inside dashboard getManuscriptByData");
        JSONObject obj = new JSONObject();
        try {
            List<EditorDashboardVO> resultData = manuScriptReviewerService
                    .getReviewedManuscriptForReviewer(userJournalManuscriptVo);
            if (resultData != null) {
                obj.put("data", resultData);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "Data not found");
                return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }

    }

    @PostMapping("getReviewerForAuthor")
    public ResponseEntity<?> getReviewerForAuthor(@RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        int count = 0;
        try {

            List<AuthorSuggestedReviewerVo> authorSuggestedReviewer = manuScriptReviewerService.authorSuggestedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());
            Date getDueDate = manuScriptReviewerService.getDueDate(manuScriptReviewerCommonVO);
            List<ManuScript> manuScriptDetail = manuScriptService
                    .trackManuScript(manuScriptReviewerCommonVO.getManuscriptId());

            List<AuthorSuggestedReviewerVo> authorOpposedReviewer = manuScriptReviewerService.authorOpposedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getJournalId());
            if (getDueDate != null)
                obj.put("duedate", getDueDate);
            else
                obj.put("duedate", "There is no duedate");

            obj.put("copyrightAgreement", manuScriptDetail.get(0).getCopyrightAgreement());
            obj.put("copyrightAgreementContent", manuScriptDetail.get(0).getCopyrightAgreementContent());
            obj.put("declarationOfInterest", manuScriptDetail.get(0).getDeclarationOfInterest());
            obj.put("declarationOfInterestContent", manuScriptDetail.get(0).getDeclarationOfInterestContent());
            obj.put("authorSuggestedReviewer", authorSuggestedReviewer);
            obj.put("authorOpposedReviewer", authorOpposedReviewer);

            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            log.error("Exception : " + e.getMessage());
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

    @PostMapping("getReviewerDatabaseSuggested")
    public ResponseEntity<?> getReviewerDatabaseSuggested(
            @RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        JSONObject obj = new JSONObject();
        try {

            List<ManuScriptReviewerCommonVO> databaseSuggestedreviewer = manuScriptReviewerService
                    .getReviewerDatabaseSuggested(manuScriptReviewerCommonVO);

            // Total count of reviewer for pagination

            Integer totalReviewerSuggestedByDatabase = manuScriptReviewerService
                    .totalReviewerSuggestedBtDatabaseCount(manuScriptReviewerCommonVO);

            // Sorting criteria put
            if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("name")) {

                Comparator<ManuScriptReviewerCommonVO> compareById = (ManuScriptReviewerCommonVO o1,
                                                                      ManuScriptReviewerCommonVO o2) -> o1.getName().compareTo(o2.getName());

                if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
                    Collections.sort(databaseSuggestedreviewer, compareById);
                else
                    Collections.sort(databaseSuggestedreviewer, compareById.reversed());

            }

            if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("email")) {

                Comparator<ManuScriptReviewerCommonVO> compareById = (ManuScriptReviewerCommonVO o1,
                                                                      ManuScriptReviewerCommonVO o2) -> o1.getEmail().compareTo(o2.getEmail());

                if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
                    Collections.sort(databaseSuggestedreviewer, compareById);
                else
                    Collections.sort(databaseSuggestedreviewer, compareById.reversed());

            }

            if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("country")) {

                Comparator<ManuScriptReviewerCommonVO> compareById = (ManuScriptReviewerCommonVO o1,
                                                                      ManuScriptReviewerCommonVO o2) -> o1.getCountry().compareTo(o2.getCountry());

                if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
                    Collections.sort(databaseSuggestedreviewer, compareById);
                else
                    Collections.sort(databaseSuggestedreviewer, compareById.reversed());

            }

            if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("specialization")) {

                Comparator<ManuScriptReviewerCommonVO> compareById = (ManuScriptReviewerCommonVO o1,
                                                                      ManuScriptReviewerCommonVO o2) -> o1.getSpecialization().compareTo(o2.getSpecialization());

                if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
                    Collections.sort(databaseSuggestedreviewer, compareById);
                else
                    Collections.sort(databaseSuggestedreviewer, compareById.reversed());

            }

            if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("subSpecialization")) {

                Comparator<ManuScriptReviewerCommonVO> compareById = (ManuScriptReviewerCommonVO o1,
                                                                      ManuScriptReviewerCommonVO o2) -> o1.getSubSpecialization()
                        .compareTo(o2.getSubSpecialization());

                if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
                    Collections.sort(databaseSuggestedreviewer, compareById);
                else
                    Collections.sort(databaseSuggestedreviewer, compareById.reversed());

            }

            obj.put("dtabaseSuggestedReviewer", databaseSuggestedreviewer);
            obj.put("totalReviewerSuggestedBydatabase", totalReviewerSuggestedByDatabase);

            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            log.error("Exception : " + e.getMessage());
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

//    @PostMapping("getReviewerGlobalSearch")
//    public ResponseEntity<?> getReviewerGlobalSearch(
//            @RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
//        JSONObject obj = new JSONObject();
//        try {
//
//            List<ManuScriptReviewerCommonVO> databaseSuggestedreviewer = manuScriptReviewerService
//                    .getReviewerGlobalSearch(manuScriptReviewerCommonVO);
//
//            // Total count of reviewer for pagination
//
//            /*
//             * Integer totalReviewerSuggestedByDatabase = manuScriptReviewerService
//             * .totalReviewerGlobalSearchCount(manuScriptReviewerCommonVO);
//             */
//
//            // Sorting criteria put
//            if (StringUtils.equalsIgnoreCase(manuScriptReviewerCommonVO.getSortBy(), "name")) {
//                Comparator<ManuScriptReviewerCommonVO> compareById = (ManuScriptReviewerCommonVO o1,
//                                                                      ManuScriptReviewerCommonVO o2) -> o1.getName().compareTo(o2.getName());
//
//                if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
////					Collections.sort(databaseSuggestedreviewer, compareById);
//                    databaseSuggestedreviewer.stream().sorted(compareById);
//
//                else
////					Collections.sort(databaseSuggestedreviewer, compareById.reversed());
//                    databaseSuggestedreviewer.stream().sorted(compareById.reversed());
//
//            }
//
//            obj.put("dtabaseSuggestedReviewer", databaseSuggestedreviewer);
////			obj.put("totalReviewerSuggestedBydatabase", totalReviewerSuggestedByDatabase);
//            obj.put("totalReviewerSuggestedBydatabase", databaseSuggestedreviewer.size());
//
//            return new ResponseEntity<>(obj, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            obj.put("data", "Something went wrong");
//            log.error("Exception : " + e.getMessage());
//            return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
//        }
//
//    }
    
    @PostMapping("getReviewerGlobalSearch")
    public ResponseEntity<?> getReviewerGlobalSearch(@RequestBody ManuScriptReviewerCommonVO vo) {
        JSONObject obj = new JSONObject();
        try {
            Pair<Integer, List<ManuScriptReviewerCommonVO>> result = manuScriptReviewerService.getReviewerGlobalSearch(vo);
            obj.put("dtabaseSuggestedReviewer", result.getRight()); // paginated data
            obj.put("totalReviewerSuggestedBydatabase", result.getLeft()); // total matching records
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("getReviewerSystemSuggested")
    public ResponseEntity<?> getReviewerSystemSuggested(
            @RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        JSONObject obj = new JSONObject();
        try {
            log.info("Fetching manuscript reviewers for Manuscript ID: {}, Version: {}, Journal ID: {}", 
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(), 
                    manuScriptReviewerCommonVO.getJournalId());

            List<ManuScriptReviewerCommonVO> assignedReviewer = manuScriptReviewerService.getAssignedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());
            log.debug("Assigned reviewers fetched. Count: {}", assignedReviewer.size());

            List<AuthorSuggestedReviewerVo> authorSuggestedReviewer = manuScriptReviewerService.authorSuggestedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());
            log.debug("Author suggested reviewers fetched. Count: {}", authorSuggestedReviewer.size());

            ManuScriptReviewerInvitationVO manuScriptReviewerInvitationVO = manuScriptReviewerService
                    .systemSuggestedReviewerPaggingImpl(manuScriptReviewerCommonVO);
            List<ManuScriptReviewerCommonVO> systemSuggestedReviewer = manuScriptReviewerInvitationVO
                    .getManuScriptReviewerCommonVO();
            log.debug("System suggested reviewers fetched. Count: {}", systemSuggestedReviewer.size());

            List<ManuScriptReviewerCommonVO> intrestedToReAccess = manuScriptReviewerService.intrestedToReAccessBySearch(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId(), manuScriptReviewerCommonVO.getSearch());
            log.info("Interested to re-access reviewers fetched. Count: {}", intrestedToReAccess.size());

            List<AuthorSuggestedReviewerVo> authorOpposedReviewer = manuScriptReviewerService.authorOpposedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getJournalId());
            log.info("Author opposed reviewers fetched. Count: {}", authorOpposedReviewer.size());

            Date getDueDate = manuScriptReviewerService.getDueDate(manuScriptReviewerCommonVO);
            if (getDueDate != null) {
                obj.put("duedate", getDueDate);
                log.debug("Due date fetched: {}", getDueDate);
            } else {
                obj.put("duedate", "There is no duedate");
                log.debug("No due date available.");
            }

            int totalCountSystemSuggestedReviewer = manuScriptReviewerInvitationVO.getTotalCount();
            obj.put("totalCountSystemSuggestedReviewer", totalCountSystemSuggestedReviewer);
            obj.put("systemSuggestedReviewer", systemSuggestedReviewer);
            log.debug("Total count of system suggested reviewers: {}", totalCountSystemSuggestedReviewer);

            int startIndex = manuScriptReviewerCommonVO.getPageNo() * manuScriptReviewerCommonVO.getLimit();
            int endIndex = ((manuScriptReviewerCommonVO.getPageNo() + 1) * manuScriptReviewerCommonVO.getLimit());
            log.debug("Pagination - Start index: {}, End index: {}", startIndex, endIndex);

            List<ManuScriptReviewerCommonVO> finalAssignedReviewer = new ArrayList<>();
            List<AuthorSuggestedReviewerVo> finalAuthorSuggestedReviewer = new ArrayList<>();
            List<ManuScriptReviewerCommonVO> finalIntrestedToReAccess = new ArrayList<>();
            List<AuthorSuggestedReviewerVo> finalAuthorOpposedReviewer = new ArrayList<>();

            if (startIndex < assignedReviewer.size()) {
                if (endIndex < assignedReviewer.size() || endIndex == assignedReviewer.size()) {
                    finalAssignedReviewer = assignedReviewer.subList(startIndex, endIndex);
                    obj.put("assignedReviewer", finalAssignedReviewer);
                    log.debug("Assigned reviewers paginated and added. Count: {}", finalAssignedReviewer.size());
                } else {
                    finalAssignedReviewer = assignedReviewer.subList(startIndex, assignedReviewer.size());
                    finalAssignedReviewer.add(assignedReviewer.get(assignedReviewer.size() - 1));
                    obj.put("assignedReviewer", finalAssignedReviewer);
                    log.debug("Assigned reviewers paginated with extra last reviewer. Count: {}", finalAssignedReviewer.size());
                }
            } else {
                obj.put("assignedReviewer", new ArrayList<>());
                log.debug("No assigned reviewers for the given pagination.");
            }

            if (startIndex < authorSuggestedReviewer.size()) {
                if (endIndex < authorSuggestedReviewer.size() || endIndex == authorSuggestedReviewer.size()) {
                    finalAuthorSuggestedReviewer = authorSuggestedReviewer.subList(startIndex, endIndex);
                    obj.put("authorSuggestedReviewer", finalAuthorSuggestedReviewer);
                    log.debug("Author suggested reviewers paginated and added. Count: {}", finalAuthorSuggestedReviewer.size());
                } else {
                    finalAuthorSuggestedReviewer = authorSuggestedReviewer.subList(startIndex, authorSuggestedReviewer.size());
//                    finalAuthorSuggestedReviewer.add(authorSuggestedReviewer.get(authorSuggestedReviewer.size() - 1));
                    obj.put("authorSuggestedReviewer", finalAuthorSuggestedReviewer);
                    log.debug("Author suggested reviewers paginated with extra last reviewer. Count: {}", finalAuthorSuggestedReviewer.size());
                }
            } else {
                obj.put("authorSuggestedReviewer", new ArrayList<>());
                log.debug("No author suggested reviewers for the given pagination.");
            }

            if (startIndex < intrestedToReAccess.size()) { 
                if (endIndex <= intrestedToReAccess.size()) { 
                    finalIntrestedToReAccess = intrestedToReAccess.subList(startIndex, endIndex);
                    obj.put("intrestedToReAccess", finalIntrestedToReAccess);
                    log.info("Interested to re-access reviewers paginated and added. Count: {}", finalIntrestedToReAccess.size());
                } else { 
                    finalIntrestedToReAccess = intrestedToReAccess.subList(startIndex, intrestedToReAccess.size());
                    obj.put("intrestedToReAccess", finalIntrestedToReAccess);
                    log.info("Interested to re-access reviewers paginated. Count: {}", finalIntrestedToReAccess.size());
                }
            } else {
                obj.put("intrestedToReAccess", new ArrayList<>());
                log.debug("No interested to re-access reviewers for the given pagination.");
            }


            if (startIndex < authorOpposedReviewer.size()) {
                if (endIndex < authorOpposedReviewer.size() || endIndex == authorOpposedReviewer.size()) {
                    finalAuthorOpposedReviewer = authorOpposedReviewer.subList(startIndex, endIndex);
                    obj.put("authorOpposedReviewer", finalAuthorOpposedReviewer);
                    log.debug("Author opposed reviewers paginated and added. Count: {}", finalAuthorOpposedReviewer.size());
                } else {
                    finalAuthorOpposedReviewer = authorOpposedReviewer.subList(startIndex, authorOpposedReviewer.size());
//                    finalAuthorOpposedReviewer.add(authorOpposedReviewer.get(authorOpposedReviewer.size() - 1));
                    obj.put("authorOpposedReviewer", finalAuthorOpposedReviewer);
                    log.debug("Author opposed reviewers paginated with extra last reviewer. Count: {}", finalAuthorOpposedReviewer.size());
                }
            } else {
                obj.put("authorOpposedReviewer", new ArrayList<>());
                log.debug("No author opposed reviewers for the given pagination.");
            }

            obj.put("totalCountForOpposedReviewer", authorOpposedReviewer.size());
            obj.put("assignedReviewerCount", assignedReviewer.size());
            obj.put("intrestedToReAccessCount", intrestedToReAccess.size());
            obj.put("authorSuggestedReviewerCount", authorSuggestedReviewer.size());

            log.info("Successfully fetched manuscript reviewers data.");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while fetching manuscript reviewers: {}", e.getMessage(), e);
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
     }

    

    @PostMapping("reviewerOpposedByAuthor")
    public ResponseEntity<?> reviewerOpposeedByAuthor(
            @RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        JSONObject obj = new JSONObject();
        try {

            List<AuthorSuggestedReviewerVo> authorOpposedReviewer = manuScriptReviewerService.authorOpposedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getJournalId());

            Date getDueDate = manuScriptReviewerService.getDueDate(manuScriptReviewerCommonVO);
            if (getDueDate != null)
                obj.put("duedate", getDueDate);
            else
                obj.put("duedate", "There is no duedate");
            obj.put("authorOpposedReviewer", authorOpposedReviewer);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("data", "Something went wrong");
            log.error("Exception : " + e.getMessage(), e);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

    @PostMapping("reviewerSuggestedByAuthor")
    public ResponseEntity<?> reviewerSuggestedByAuthor(
            @RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        JSONObject obj = new JSONObject();
        try {

            List<AuthorSuggestedReviewerVo> authorSuggestedReviewer = manuScriptReviewerService.authorSuggestedReviewer(
                    manuScriptReviewerCommonVO.getManuscriptId(), manuScriptReviewerCommonVO.getManuscriptVersion(),
                    manuScriptReviewerCommonVO.getJournalId());

            Date getDueDate = manuScriptReviewerService.getDueDate(manuScriptReviewerCommonVO);
            if (getDueDate != null)
                obj.put("duedate", getDueDate);
            else
                obj.put("duedate", "There is no duedate");
            obj.put("authorSuggestedReviewer", authorSuggestedReviewer);

            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("data", "Something went wrong");
            log.error("Exception : " + e.getMessage(), e);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

    @PostMapping("getReviewerStaticsDetails")
    public ResponseEntity<?> getReviewerStaticsDetails(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            List<ReviewerAvailabilityStatusVO> data = manuScriptReviewerService.getReviewerStaticsDetails(id);
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "Data not found");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/getOtherReviewerFlagValue", method = RequestMethod.POST)
    public ResponseEntity<?> getOtherReviewerFlagValue(@RequestBody UserJournalManuscriptVo userJournalManuscriptVo) {
        log.info("Inside dashboard getOtherReviewerFlagValue");
        JSONObject obj = new JSONObject();
        try {
            String otherReviewerFlag = manuScriptReviewerService.getOtherReviewerFlagValue(userJournalManuscriptVo);
            obj.put("data", otherReviewerFlag);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
    }


    @PostMapping("getReviewerCommentData")
    public ResponseEntity<?> getReviewerCommentData(@RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
        JSONObject obj = new JSONObject();
        try {
            List<ManuScriptReviewerCommonVO> data = manuScriptReviewerService.getReviewerCommentData(manuScriptReviewerCommonVO);
            if (data != null && !data.isEmpty()) {
                obj.put("data", data);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "Data not found");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/getReviewedCertificate", method = RequestMethod.POST)
    public ResponseEntity<Resource> getReviewedCertificate(
            @RequestBody EditorDashboardVO requestVo) {

        log.info("Inside getReviewedCertificate");

        return manuScriptReviewerService.getReviewedCertificateForReviewer(requestVo);
    }

    @PostMapping("/validateFile")
    public ResponseEntity<String> validateFile(@RequestParam("file") MultipartFile file) {
        // Map of expected MIME types
        Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
        
        try {
            // Convert MultipartFile to File
            File serverFile = FileUploadValidation.convertMultipartFileToFile(file);
            
            // Validate file
            boolean isValidFile = FileUploadValidation.validateFile(serverFile, expectedMimeTypes);
            
            if (!isValidFile) {
                log.error("Uploaded file validation failed");
                log.info("Deleting invalid file: " + serverFile.delete());
                return ResponseEntity.ok("There is no file to update");
            }
            
            return ResponseEntity.ok("File is valid.");
            
        } catch (Exception e) {
            log.error("File validation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File validation failed.");
        }
    }
}
