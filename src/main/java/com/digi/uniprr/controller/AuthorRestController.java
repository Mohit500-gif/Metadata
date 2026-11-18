package com.digi.uniprr.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import com.digi.uniprr.model.*;
import com.digi.uniprr.utils.AppConstants;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.ArticleMappingDetail;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.FunderDetailsVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.VO.ManuScriptVO;
import com.digi.uniprr.VO.ManuscriptCoAuthorVO;
import com.digi.uniprr.VO.ManuscriptCommentVO;
import com.digi.uniprr.VO.ManuscriptFileVO;
import com.digi.uniprr.VO.ManuscriptTypeJournalVO;
import com.digi.uniprr.VO.ManuscriptTypeTitleAbstractVO;
import com.digi.uniprr.VO.ResubmissionManuscriptFileVo;
import com.digi.uniprr.repository.BundleTrackerRepo;
import com.digi.uniprr.repository.JournalMetadataRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.service.ArticleSyncService;
import com.digi.uniprr.service.AuthorInvitationService;
import com.digi.uniprr.service.CommentaryService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.JournalService;
import com.digi.uniprr.service.LetterToEditorService;
import com.digi.uniprr.service.ManuScriptFileService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ReviewerSuggestedService;
import com.digi.uniprr.service.UserService;
import com.digi.uniprr.utils.BundlingUtils;
import com.digi.uniprr.utils.FileBackupUtil;
import com.digi.uniprr.utils.FileUploadValidation;
//import com.digi.uniprr.utils.FileUploadValidator;
import com.digi.uniprr.utils.FileUtil;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AuthorRestController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    JournalService journalService;
    @Autowired
    ReviewerSuggestedService reviewerSuggestedService;
    @Autowired
    ManuscriptEditorRepo manuscriptEditorRepo;
    @Autowired
    CommentaryService commentaryService;
    @Autowired
    LetterToEditorService letterToEditorService;
    @Autowired
    ArticleSyncService articleSyncService;
    @Autowired
    private ManuScriptFileService manuScriptFileService;
    @Autowired
    private ManuScriptService manuscriptService;
    @Autowired
    private AuthorInvitationService authorInvitationService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailTriggerService emailTriggerService;
    @Autowired
    private UserJournalRepo userJournalRepo;
    @Autowired
    private ManuScriptFileRepo manuScriptFileRepo;
    @Autowired
    private BundleTrackerRepo bundleTrackerRepo;
    @Autowired
    private ManuScriptRepo manuScriptRepo;
    @Autowired
    private JournalsRepo journalsRepo;
    @Autowired
    private JournalMetadataRepo journalMetadataRepo;
    @Value("${uniprrFolderPath}")
    private String UPLOAD_FOLDER;

    @Value("${copyRightUrlForCoAuthor}")
    private String copyRightUrlForCoAuthor;

    @Value("${server.status}")
    private String serverENV;

    @Value("${awsFileIp}")
    private String serverIP;

    @Value("${server.status.port}")
    private String serverPort;

    @Value("${unitouch.url}")
    private String unitouchUrl;
    
    @Value("${baseUrl}")
    private String baseUrl;
    
    @Value("${uniprrFolderPath}")
    private String uniprrFolderPath;

    @PostMapping("submitManuscriptFileUpload")
    public ResponseEntity<String> submitManuscriptFileUpload(@RequestParam("manuScriptId") Integer manuScriptId,
                                                             MultipartFile[] files, @RequestParam("manuscriptVersion") Integer manuscriptVersion,@RequestParam("fileStatus") String fileStatus,
                                                             @RequestParam("journalId") Integer journalId, HttpServletRequest request) {
        log.info("Inside AuthorRestController submitManuscriptFileUpload");

        String mansucriptBasePath = UPLOAD_FOLDER + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R" + manuscriptVersion + "_" + journalId + "_" + manuScriptId;
        String BACKUP_FOLDER = UPLOAD_FOLDER + File.separator + "FileBackups";
        
        File dir = new File(mansucriptBasePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        MultipartFile file = null;
        File serverFile = null;
        List<ManuScriptFile> manuscriptFiles = new ArrayList<>();
        ManuScriptFile manucriptFile = null;
        boolean isUploaded = true;
        for (int i = 0; i < files.length; i++) {
            file = files[i];
            serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
            try(BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                byte[] bytes = file.getBytes();
                stream.write(bytes);
                stream.flush();
                log.info("serverFile length: "+serverFile.length());
                log.info("File size: "+file.getSize());
                Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
                boolean isValidFile = FileUploadValidation.validateFile(serverFile, expectedMimeTypes);
                if (serverFile.length() != file.getSize()) {
                    isUploaded = false;
                    log.error("Uploaded file size does not match original file size");
                    log.info("file delete: "+serverFile.delete());
                }else if (!isValidFile) {
                    isUploaded = false;
                    log.error("Invalid file: " + file.getOriginalFilename());
                    log.info("Deleting invalid file: " + serverFile.delete());
                    // Get extension(s) and return friendly format
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    return new ResponseEntity<>("InvalidFile::" + extension, HttpStatus.OK);
                }
                else {
                    FileUtil.updateFilePermission(serverFile);

                    if(serverFile.exists() && serverFile.canRead()) {
                    	
                        boolean backupSuccess = FileBackupUtil.backupFile(serverFile, journalId, manuscriptVersion, manuScriptId, BACKUP_FOLDER);
                        if (!backupSuccess) {
                            log.warn("Backup failed for file: " + file.getOriginalFilename());
                        }
                    	
                        double fileSizeinBytes = file.getSize();
                        double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
                        String fileServerPath = BundlingUtils.generateFilePath(manuscriptVersion, journalId, manuScriptId, UPLOAD_FOLDER, file.getOriginalFilename(), serverENV, serverIP, serverPort);

                        manucriptFile = manuScriptFileRepo.uploadUniqueFileWithTheName(manuScriptId,manuscriptVersion,file.getOriginalFilename());
                        if(manucriptFile == null) {
                            manucriptFile = new ManuScriptFile();
                        }
                        manucriptFile.setFileName(file.getOriginalFilename());
                        manucriptFile.setFilePath(fileServerPath);
                        manucriptFile.setFileSize((int) fileSizeInKB);
                        manucriptFile.setManuScriptId(manuScriptId);
                        manucriptFile.setFileType("");
                        manucriptFile.setFileVersion(manuscriptVersion);
                        manucriptFile.setFileStatus(fileStatus);
                        manucriptFile.setIsActive(1);
                        manucriptFile.setFileActive(0);

                        manuscriptFiles.add(manucriptFile);
                    }else {
                        isUploaded = false;
                        log.error("Uploaded file does not exist or cannot be read");
                    }
                }
            } catch (IOException e) {
                isUploaded = false;
                log.error("Error while uploading file before submission for bundling : " + e.getMessage(), e);
            }
        }

        if(!manuscriptFiles.isEmpty()) {
            try {
                manuScriptFileService.saveAll(manuscriptFiles);
            } catch (Exception e) {
                isUploaded = false;
                log.error("Error while saving manuscript file details in DB : " + e.getMessage(), e);
            }
        }

        String message =  !isUploaded ? "Failed to upload file(s)" : "";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("getManuscriptByactive")
    public ResponseEntity<?> getManuscriptByactive(@PathParam("manuscriptId") Integer manuscriptId,
                                                   @PathParam("manuscriptVersion") Integer manuscriptVersion) {
        log.info("Inside AuthorRestController of getManuscriptByactive");
        log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST +
                AppConstants.REQUEST_URI + " :: /getManuscriptByactive" +
                AppConstants.REQUEST_PARAMS + " :: manuscriptId : " + manuscriptId + ", manuscriptVersion : " + manuscriptVersion);
        JSONObject obj = new JSONObject();
        try {
            List<ManuscriptFileVO> manu = manuScriptFileService.getManuscriptByactive(manuscriptId, manuscriptVersion);
            if (manu != null && !manu.isEmpty()) {
                log.info("Data Found");
                
                Integer journalId = manuScriptRepo.getJournalId(manuscriptId);
                Optional<String> chargeOnOpt = Optional.ofNullable(journalsRepo.getChargeFlag(journalId));
                // Check if ChargeOn is "yes" for the journal
                chargeOnOpt.filter(chargeStatus -> "Yes".equalsIgnoreCase(chargeStatus)).ifPresent(chargeStatus -> {
                    Optional<JournalMetadata> journalMetaDataOpt = Optional.ofNullable(journalMetadataRepo.metaDatalistByJournalId(journalId));
                    journalMetaDataOpt.ifPresent(journalMetaData -> {
                    	journalMetaData.setIsChargingRequired(chargeStatus);
                        journalMetadataRepo.save(journalMetaData);
                        log.info("Journal metadata updated with chargeOn for : "+journalId);
                    });
                });
                
                return new ResponseEntity<Object>(manu, HttpStatus.OK);
            } else {
                List<ManuscriptFileVO> manuAlt = manuScriptFileService.getManuscriptByactiveAlt(manuscriptId, manuscriptVersion);
                if (manuAlt != null && !manuAlt.isEmpty()) {
                    log.info("Data Found");
                    return new ResponseEntity<Object>(manuAlt, HttpStatus.OK);
                } else {
                    log.info("Data Not Found");
                    obj.put("message", "Data Not Found");
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            if (e.getMessage().contains("StaleStateException")) {
                List<ManuscriptFileVO> manuAlt2 = manuScriptFileService.getManuscriptByactive(manuscriptId, manuscriptVersion);
                return new ResponseEntity<Object>(manuAlt2, HttpStatus.OK);
            }
            obj.put("message", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

//    @PostMapping("downloadFileByManuScriptFileID")
//    public ResponseEntity<?> downloadFileByManuScriptFileID(@PathParam("manuscriptFileId") Integer manuscriptFileId) {
//        log.info("Inside AuthorRestController downloadFileByManuScriptFileID");
//        JSONObject obj = new JSONObject();
//        try {
//            String data = manuScriptFileService.downloadFileByManuScriptFileID(manuscriptFileId);
//            obj.put("FilePath", data);
//        } catch (Exception e) {
//            obj.put("data", "Something went wrong");
//        }
//        return new ResponseEntity<Object>(obj, HttpStatus.OK);
//    }
    
    @GetMapping("/downloadFileByManuScriptFileID")
    public ResponseEntity<Resource> downloadFileByManuScriptFileID(
            @RequestParam("manuscriptFileId") Integer manuscriptFileId,
            HttpServletRequest request) {

        log.info("=======================================================");
        log.info("Download Request Received");
        log.info("Manuscript File Id : {}", manuscriptFileId);
        log.info("Client IP          : {}", request.getRemoteAddr());

        return manuScriptFileService.downloadFile(manuscriptFileId, request);
    }
    
    @GetMapping("/previewFileByManuScriptFileID")
    public ResponseEntity<Resource> previewFileByManuScriptFileID(
            @RequestParam("manuscriptFileId") Integer manuscriptFileId,
            HttpServletRequest request) {


        log.info("-----------------------------------------");
        log.info("Preview File Request");
        log.info("Manuscript File Id : {}", manuscriptFileId);


        return manuScriptFileService.previewFile(
                manuscriptFileId,
                request
        );

    }

    @PostMapping("deleteManuscriptFile")
    public ResponseEntity<?> deleteManuscriptFile(@PathParam("manuscriptFileId") Integer manuscriptFileId, HttpServletRequest request) {
        log.info("Inside AuthorRestController deleteManuscriptFile");
        JSONObject obj = new JSONObject();
        try {
            obj.put("message", manuScriptFileService.deleteManuscriptFile(manuscriptFileId, request));
        } catch (Exception e) {
            log.info("Error");
            obj.put("message", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @PostMapping("updateManuscriptFileUpload")
    public ResponseEntity<?> updateManuscriptFileUpload(
            @RequestBody ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
        log.info("Inside AuthorRestController updateManuscriptFileUpload");
        log.info("Mnuscript Id :" + resubmissionManuscriptFileVo.getManuScriptId() + " " + "Manuscript Version :"
                + resubmissionManuscriptFileVo.getManuscriptVersion());
        JSONObject obj = new JSONObject();
        try {
            String message = manuScriptFileService.updateManuscriptFileUpload(resubmissionManuscriptFileVo, request);
            if (message == "Success") {
                log.info("Submission proccess Done");
                obj.put("data", message);
            } else {
                log.info("Submission proccess Fail");
                obj.put("data", message);
            }
        } catch (Exception e) {
            log.info("Exception" + e);
            obj.put("data", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/manuscriptTypeJournal/list", method = RequestMethod.POST)
    public ResponseEntity<?> getManuscriptTypeListByJournalId(@RequestBody IDVO vo) {
        log.info("Inside AuthorRestController getManuscriptTypeListByJournalId");
        List<ManuscriptTypeJournalVO> list;
        try {
            list = manuscriptService.getManuscriptTypeJournalListByJournalId(vo.getId());
        } catch (Exception e) {
            JSONObject obj = new JSONObject();
            log.info("Something went wrong");
            obj.put("message", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/manuscriptTypeJournal/getManuscriptCharge", method = RequestMethod.POST)
    public ResponseEntity<?> getManuscriptCharge(@RequestBody IDVO vo) {
        log.info("Inside AuthorRestController getManuscriptTypeListByJournalId");
        log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST +
                AppConstants.REQUEST_URI + " :: /manuscriptTypeJournal/getManuscriptCharge" +
                AppConstants.REQUEST_BODY + " ::" + vo.toString());
        ManuscriptTypeJournalVO list;
        try {
//            list = manuscriptService.getManuscriptCharge(vo.getJournalId(), vo.getComment());
            list = manuscriptService.getManuscriptCharge(vo.getJournalId(), vo.getComment(), vo.getManuscriptId());
        } catch (Exception e) {
            JSONObject obj = new JSONObject();
            log.info("Something went wrong");
            obj.put("message", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/author/typeTitleAbstract", method = RequestMethod.POST)
    public ResponseEntity<?> getManuscriptTypeTitleAbstractDetails(@RequestBody IDVO vo) {
        log.info("Inside AuthorRestController getManuscriptTypeTitleAbstractDetails");
        ManuscriptTypeTitleAbstractVO manuscriptTypeTitleAbstractVO = manuscriptService
                .getManuscriptTypeAbstractDetails(vo);
        return new ResponseEntity<>(manuscriptTypeTitleAbstractVO, HttpStatus.OK);
    }

    @RequestMapping(value = "/author/saveTypeTitleAbstract", method = RequestMethod.POST)
    public ResponseEntity<?> saveTypeTitleAbstractDetails(@RequestBody ManuscriptTypeTitleAbstractVO vo) {
        log.info("Inside AuthorRestController saveTypeTitleAbstractDetails");
        JSONObject obj = new JSONObject();
        vo.setTitle(vo.getTitle().replaceAll("\\s+", " "));
        // boolean isTitleExist = false;
        // isTitleExist = manuscriptService.titleIsExist(vo);
        // if (!isTitleExist) {
        try {
            ManuScript manuscript = manuScriptRepo.getOne(vo.getManuscriptId());

            manuscriptService.saveTypeTitleAbstractDetails(vo, manuscript);
            log.info("Details Saved Successfullly");
            obj.put("message", "Details Saved Successfullly");

            // Email Trigger
            log.info("Inside in saveTypeTitleAbstractDetails For Email Trigger");
            if (manuscript.getManuscriptStatusId().equals(1) && vo.getSaveAndDraft() != null && vo.getSaveAndDraft()) {
                EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                emailTriggerVO.setJournalId(vo.getJournalId());
                emailTriggerVO.setUserId(vo.getAuthorId());
                emailTriggerVO.setAuthorUserId(vo.getAuthorId());
                emailTriggerVO.setManuscriptId(vo.getManuscriptId());
                emailTriggerVO.setManuscriptStatus(1);
                emailTriggerVO.setReceiverRoleId(4);
                emailTriggerVO.setTaskId(1);
                emailTriggerVO.setRoleId(4);
                log.info("Inside in saveTypeTitleAbstractDetails For Email Trigger Data are:->"
                        + emailTriggerVO.toString());
                log.info("User Receiver Id:->" + vo.getAuthorId());
                log.info("User Receiver Role Id:->" + 4);
                log.info("User Sender Role Id:->" + 4);
                log.info("User Sender Id:->" + vo.getAuthorId());
                log.info("Task Id:->" + 1);
                String message = emailTriggerService.emailTrigger(emailTriggerVO);
                obj.put("data", message);
                log.debug("Emil Trigger" + message);
                log.info("Message of saveTypeTitleAbstractDetails For Email Trigger " + message);
                log.info("End of saveTypeTitleAbstractDetails For Email Trigger ");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Some Error occured");
            obj.put("message", "Some Error occured");
        }
        log.info("End of AuthorRestController saveTypeTitleAbstractDetails");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/author/getCoAuthorList", method = RequestMethod.POST)
    public ResponseEntity<?> getCoAuthorList(@RequestBody IDVO vo) {
        log.info("Inside AuthorRestController getCoAuthorList");
        List<ManuscriptCoAuthorVO> list = manuscriptService.getCoAuthorList(vo.getId());
        Long count = list.stream().filter(obj -> StringUtils.equalsIgnoreCase(obj.getIsCorrespondingAuthor(), "Y")).count();
        if (count < 1)
            return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/author/deleteCoAuthorList", method = RequestMethod.POST)
    public ResponseEntity<?> deleteCoAuthorList(@RequestBody IDVO vo) {
        log.info("Inside AuthorRestController getCoAuthorList");
        JSONObject obj = new JSONObject();
        try {
            manuscriptService.deleteCoAuthorList(vo.getId());
            obj.put("mes", "Co-Author Deleted");
        } catch (Exception e) {
            log.info("Exception :" + e);
            obj.put("mes", "Something Went Wrong");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/author/saveCoAuthorOrder", method = RequestMethod.POST)
    public ResponseEntity<?> saveCoAuthorOrder(@RequestBody List<ManuscriptCoAuthorVO> voList) {
        log.info("Inside AuthorRestController saveCoAuthorOrder");
        JSONObject obj = new JSONObject();
        try {
            String getData = manuscriptService.saveCoAuthorOrderList(voList);
            obj.put("message", getData);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Some Error Occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/author/saveWordCountDetails", method = RequestMethod.POST)
    public ResponseEntity<?> saveWordCountDetails(@RequestBody ManuScriptVO vo) {
        log.info("Inside AuthorRestController saveWordCountDetails");
        JSONObject obj = new JSONObject();
        try {
            manuscriptService.saveWordCountDetails(vo);
            obj.put("message", "Saved Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Error");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("coAuthorSendMail")
    public ResponseEntity<?> coAuthorSendMail(@RequestBody ManuScriptVO vo) {
        log.info("Inside AuthorRestController getResearchFunders");
        JSONObject obj = new JSONObject();
        try {
            vo.setCopyRightUrlForCoAuthor(copyRightUrlForCoAuthor);

            String message = manuscriptService.coAuthorSendMail(vo);
            obj.put("data", message);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }


    @RequestMapping(value = "/author/submitManuscript", method = RequestMethod.POST)
    public ResponseEntity<?> submitManuscript(@RequestBody ManuScriptVO vo) {
        log.info("Inside AuthorRestController submitManuscript");

        vo.setCopyRightUrlForCoAuthor(copyRightUrlForCoAuthor);

        long startTime = Instant.now().toEpochMilli();
        log.info("Request Time :" + Instant.now());
        JSONObject obj = new JSONObject();


        //Changes for UN-17 starts
        JournalVO journalVO = journalService.getJournalById(vo.getJournalId());

        Integer count = reviewerSuggestedService.getReviewerSuggestionCount(vo.getId(), 1,
                vo.getJournalId()).intValue();

        if (StringUtils.equalsIgnoreCase("Y", journalVO.getReviewerSuggestion()) && journalVO.getReviewerCount() > count) {
            obj.put("count", journalVO.getReviewerCount());
            return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
        }


        // changes for UN-17 ends

        try {

            JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuscriptService.getManuscriptChargeByManuscriptId(vo.getId(), vo.getJournalId());
            //added code to link commentary article to the main article.
            if (StringUtils.equalsIgnoreCase(vo.getManuscriptAbstract(), "Commentary")) {
                commentaryService.linkCommentaryToManuscript(vo.getParentManuscriptId(), vo.getId(), vo.getJournalId());
            }
            if (StringUtils.equalsIgnoreCase(vo.getManuscriptAbstract(), "Authors' Response")) {
                letterToEditorService.updateManuscriptToLTEArticle(vo.getParentManuscriptId(), vo.getId(), vo.getJournalId());
            }

            String manuscriptCode = manuscriptService.submitManuscript(vo, journalManuscriptTypeCharge);
            obj.put("manuscriptCode", manuscriptCode);

            if (StringUtils.equalsIgnoreCase(vo.getManuscriptAbstract(), "Commentary")
                    || StringUtils.equalsIgnoreCase(vo.getManuscriptAbstract(), "Authors' Response")) {
                // Sync commentary and LTE linking information to Unitouch
                ManuScript childMansucript = manuscriptService.findManuscriptById(vo.getId());
                ManuScript mainMansucript = manuscriptService.findManuscriptById(vo.getParentManuscriptId());
                articleSyncService
                        .syncArticleDetails(mainMansucript.getManuscriptCode(), childMansucript.getManuscriptCode(), vo.getManuscriptAbstract());
            }

            int userCountryStatus = Integer.parseInt(manuscriptService.checkDevelopingCountryUser(vo.getAuthorId()));
            obj.put("coauthor", manuscriptService.sendEmailToCoAuthorOnManuscript(vo));
            String submissionCharge = "";
            String paymentCountryRequired = "";
            if (journalManuscriptTypeCharge != null) {
                submissionCharge = journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null ? journalManuscriptTypeCharge.getCountryChargeOnSubmission() : "";
            }
            // Email Trigger
            ManuScript manuScript = manuScriptRepo.getById(vo.getId());

            log.info("Inside AuthorRestController submitManuscript For Email Trigger");
            if (vo.getStatusCode().equals(2) || vo.getStatusCode().equals(64) && vo.getJournalId() != null && vo.getAuthorId() != null
                    && vo.getId() != null) {
                EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
//                if (manuScript.getManuscriptStatusId() == 64
//                        && (submissionCharge.equalsIgnoreCase("SameForAll")
//                        || ((journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null ? journalManuscriptTypeCharge.getCountryChargeOnSubmission() : "")
//                        .equalsIgnoreCase("FromDevelopingCountry")
//                        && userCountryStatus == 0))) {
                if (manuScript.getManuscriptStatusId() == 64) {

                        ManuScript updatePayement = manuScriptRepo.getById(vo.getId());
                        updatePayement.setPaymentStatus("Submission Payment is required");
                        manuScriptRepo.saveAndFlush(updatePayement);
                        // Sending Mail To Author when charge is required on payment
                        emailTriggerVO.setJournalId(vo.getJournalId());
                        emailTriggerVO.setUserId(vo.getAuthorId());
                        emailTriggerVO.setAuthorUserId(vo.getAuthorId());
                        emailTriggerVO.setManuscriptId(vo.getId());
                        emailTriggerVO.setManuscriptStatus(2);
                        emailTriggerVO.setReceiverRoleId(4);
                        emailTriggerVO.setTaskId(45);
                        emailTriggerVO.setRoleId(4);
                        log.info("Inside in AuthorRestController submitManuscript For Email Trigger Data are:->"
                                + emailTriggerVO.toString());
                        log.info("User Receiver Id:->" + vo.getAuthorId());
                        log.info("User Receiver Role Id:->" + 4);
                        log.info("User Sender Role Id:->" + 4);
                        log.info("User Sender Id:->" + vo.getAuthorId());
                        log.info("Task Id:->" + 45);
                        String message = emailTriggerService.emailTrigger(emailTriggerVO);
                        obj.put("data", message);
                        log.info("Message of AuthorRestController submitManuscript For Email Trigger " + message);
                        log.info("End of AuthorRestController submitManuscript For Email Trigger ");
                }else {
                    // Sending Mail To Author when there is no charge on submission
                    emailTriggerVO.setJournalId(vo.getJournalId());
                    emailTriggerVO.setUserId(vo.getAuthorId());
                    emailTriggerVO.setAuthorUserId(vo.getAuthorId());
                    emailTriggerVO.setManuscriptId(vo.getId());
                    emailTriggerVO.setManuscriptStatus(2);
                    emailTriggerVO.setReceiverRoleId(4);
                    emailTriggerVO.setTaskId(2);
                    emailTriggerVO.setRoleId(4);
                    log.info("Inside in AuthorRestController submitManuscript For Email Trigger Data are:->"
                            + emailTriggerVO.toString());
                    log.info("User Receiver Id:->" + vo.getAuthorId());
                    log.info("User Receiver Role Id:->" + 4);
                    log.info("User Sender Role Id:->" + 4);
                    log.info("User Sender Id:->" + vo.getAuthorId());
                    log.info("Task Id:->" + 2);

                    String message = emailTriggerService.emailTrigger(emailTriggerVO);
                    obj.put("data", message);
                    log.info("Message of AuthorRestController submitManuscript For Email Trigger " + message);
                    log.info("End of AuthorRestController submitManuscript For Email Trigger ");
                }

                // Sending Email to Assign Editor And Managing Editor On that Journal
                //	List<UserJournal> getAllUserJournal = userJournalRepo.getAllUserJournalList(vo.getJournalId());
                ManuScript i = manuScriptRepo.getOne(vo.getId());
                if (i != null && !vo.getStatusCode().equals(64)) {
                    log.info("Inside in AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal");
                    List<UserJournal> j = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 2);
                    if (j != null && !j.isEmpty()) {
                        j.stream().forEach(p -> {
                            EmailTriggerVO emailTrigger = new EmailTriggerVO();
                            emailTrigger.setJournalId(vo.getJournalId());
                            emailTrigger.setUserId(p.getUserId());
                            emailTrigger.setManagingEditorId(p.getUserId());
                            emailTrigger.setManuscriptId(vo.getId());
                            emailTrigger.setManuscriptStatus(2);
                            log.info("User Receiver Role Id:->" + 2);
                            emailTrigger.setReceiverRoleId(2);
                            emailTrigger.setTaskId(13);
                            emailTrigger.setRoleId(4);
                            log.info(
                                    "Inside in AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal Data are:->" + emailTriggerVO.toString());
                            log.info("User Receiver Id:->" + p.getUserId());
                            log.info("Task Id:->" + 13);
                            String messageS = emailTriggerService.emailTrigger(emailTrigger);
                            obj.put("data", messageS);
                            log.info(
                                    "Message of AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal "
                                            + messageS);
                            log.info(
                                    "End of AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal ");
                        });
                    }
                }

            } else if (vo.getStatusCode().equals(26) && vo.getJournalId() != null && vo.getAuthorId() != null
                    && vo.getId() != null) {
                try {
                    manuscriptEditorRepo.updateStatusInEditor(vo.getStatusCode(), vo.getId(), vo.getManuscriptVersion());

                } catch (Exception e) {
                    log.info("Update status code :26 for manuscript editor table " + e);
                }


                log.info("Inside AuthorRestController submitManuscript Sending Mail To Author For Re submission ");
                // Sending Mail To Author For Re submission
                EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                emailTriggerVO.setJournalId(vo.getJournalId());
                emailTriggerVO.setUserId(vo.getAuthorId());
                emailTriggerVO.setAuthorUserId(vo.getAuthorId());
                emailTriggerVO.setManuscriptId(vo.getId());
                emailTriggerVO.setManuscriptStatus(26);
                emailTriggerVO.setReceiverRoleId(4);
                emailTriggerVO.setTaskId(9);
                emailTriggerVO.setRoleId(4);
                log.info(
                        "Inside in AuthorRestController submitManuscript Sending Mail To Author For Re submission Data are:->"
                                + emailTriggerVO.toString());
                log.info("User Receiver Id:->" + vo.getAuthorId());
                log.info("User Receiver Role Id:->" + 4);
                log.info("User Sender Role Id:->" + 4);
                log.info("User Sender Id:->" + vo.getAuthorId());
                log.info("Task Id:->" + 9);
                String message = emailTriggerService.emailTrigger(emailTriggerVO);
                obj.put("data", message);
                log.info(
                        "Message of AuthorRestController submitManuscript Sending Mail To Author For Re submission Email Trigger "
                                + message);
                log.info(
                        "End of AuthorRestController submitManuscript Sending Mail To Author For Re submission Email Trigger ");

                // Sending Email to Assign Editor And Managing Editor On that Journal
                //	List<UserJournal> getAllUserJournal = userJournalRepo.getAllUserJournalList(vo.getJournalId());
                ManuScript i = manuScriptRepo.getOne(vo.getId());
                if (i != null) {
                    log.info(
                            "Inside AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission ");
                    //	getAllUserJournal.stream().forEach(i -> {
                    //	i.getUser().getRole().stream().forEach(j -> {
                    //	if (j.getRoleId().equals(2)) {
                    EmailTriggerVO emailTrigger = new EmailTriggerVO();
                    emailTrigger.setJournalId(vo.getJournalId());
                    emailTrigger.setUserId(i.getAssignedUser());
                    emailTrigger.setManagingEditorId(i.getAssignedUser());
                    emailTrigger.setManuscriptId(vo.getId());
                    emailTrigger.setManuscriptStatus(26);
                    //if (j.getRoleId().equals(2)) {
                    log.info("User Receiver Role Id:->" + vo.getAssignedUserRole());
//									emailTrigger.setReceiverRoleId(2);
                    emailTrigger.setReceiverRoleId(vo.getAssignedUserRole());
                    //}
								/*else {
									log.info("User Receiver Role Id:->" + 3);
									emailTrigger.setReceiverRoleId(3);
								}*/
                    emailTrigger.setTaskId(19);
                    emailTrigger.setRoleId(4);
                    log.info(
                            "Inside in AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission Data are:->"
                                    + emailTriggerVO.toString());
                    log.info("User Receiver Id:->" + i.getAssignedUser());
                    log.info("Task Id:->" + 13);
                    String messageS = emailTriggerService.emailTrigger(emailTrigger);
                    obj.put("data", messageS);
                    log.info(
                            "Message of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission Email Trigger "
                                    + message);
                    log.info(
                            "End of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Re submission Email Trigger ");
                }
                //});
                //});
                //}

            }
            if (vo.getStatusCode().equals(50) && vo.getJournalId() != null && vo.getAuthorId() != null
                    && vo.getId() != null) {

                try {
                    manuscriptEditorRepo.updateStatusInEditor(vo.getStatusCode(), vo.getId(), vo.getManuscriptVersion());

                } catch (Exception e) {
                    log.info("Update status code :50 for manuscript editor table " + e);
                }

                log.info(
                        "Inside AuthorRestController submitManuscript Sending Mail To Author For Technical Modification ");
                // Sending Mail To Author For Technical Modification
                EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
                emailTriggerVO.setJournalId(vo.getJournalId());
                emailTriggerVO.setUserId(vo.getAuthorId());
                emailTriggerVO.setAuthorUserId(vo.getAuthorId());
                emailTriggerVO.setManuscriptId(vo.getId());
                emailTriggerVO.setManuscriptStatus(50);
                emailTriggerVO.setReceiverRoleId(4);
                emailTriggerVO.setTaskId(14);
                emailTriggerVO.setRoleId(4);
                log.info(
                        "Inside in AuthorRestController submitManuscript Sending Mail To Author For Technical Modification Data are:->"
                                + emailTriggerVO.toString());
                log.info("User Receiver Id:->" + vo.getAuthorId());
                log.info("User Receiver Role Id:->" + 4);
                log.info("User Sender Role Id:->" + 4);
                log.info("User Sender Id:->" + vo.getAuthorId());
                log.info("Task Id:->" + 9);
                String message = emailTriggerService.emailTrigger(emailTriggerVO);
                obj.put("data", message);
                log.info(
                        "Message of AuthorRestController submitManuscript Sending Mail To Author For Technical Modification Email Trigger "
                                + message);
                log.info(
                        "End of AuthorRestController submitManuscript Sending Mail To Author For Technical Modification Email Trigger ");

                // Sending Email to Assign Editor And Managing Editor On that Journal
                //List<UserJournal> getAllUserJournal = userJournalRepo.getAllUserJournalList(vo.getJournalId());
                ManuScript i = manuScriptRepo.getOne(vo.getId());
                if (i != null) {
                    log.info(
                            "Inside AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification ");
                    //	getAllUserJournal.stream().forEach(i -> {
                    //	i.getUser().getRole().stream().forEach(j -> {
                    //	if (j.getRoleId().equals(2)) {
                    EmailTriggerVO emailTrigger = new EmailTriggerVO();
                    emailTrigger.setJournalId(vo.getJournalId());
                    emailTrigger.setUserId(i.getAssignedUser());
                    emailTrigger.setManagingEditorId(i.getAssignedUser());
                    emailTrigger.setManuscriptId(vo.getId());
                    emailTrigger.setManuscriptStatus(50);
                    //	if (j.getRoleId().equals(2)) {
                    log.info("User Receiver Role Id:->" + 2);
                    emailTrigger.setReceiverRoleId(2);
								/*} else {
									log.info("User Receiver Role Id:->" + 3);
									emailTrigger.setReceiverRoleId(3);
								}*/
                    emailTrigger.setTaskId(20);
                    emailTrigger.setRoleId(4);
                    log.info(
                            "Inside in AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification Data are:->"
                                    + emailTriggerVO.toString());
                    log.info("User Receiver Id:->" + i.getAssignedUser());
                    log.info("Task Id:->" + 13);
                    String messageS = emailTriggerService.emailTrigger(emailTrigger);
                    obj.put("data", messageS);
                    log.info(
                            "Message of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification Email Trigger "
                                    + message);
                    log.info(
                            "End of AuthorRestController submitManuscript Sending Email to Assign Editor And Managing Editor On that Journal For Technical Modification Email Trigger ");
                    //	}
                    ///	});
                    //	});
                }

            }

            obj.put("message", "Data Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error in AuthorRestController submitManuscript" + e);
            log.info("Error Time :" + (Instant.now().toEpochMilli() - startTime));
            obj.put("message", "Submission Updation Failure: "+e.getMessage());
        }
        log.info("End of AuthorRestController submitManuscript");
        log.info("Response Time :" + (Instant.now().toEpochMilli() - startTime));
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }


    @RequestMapping(value = "/author/saveExitManuscript", method = RequestMethod.POST)
    public ResponseEntity<?> saveExitManuscript(@RequestBody ManuScriptVO vo) {
        log.info("Inside AuthorRestController submitManuscript");
        long startTime = Instant.now().toEpochMilli();
        log.info("Request Time :" + Instant.now());
        JSONObject obj = new JSONObject();
        try {

            obj.put("manuscriptCode", manuscriptService.saveExitManuscript(vo));
            obj.put("message", "Data Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error in AuthorRestController submitManuscript" + e);
            log.info("Error Time :" + (Instant.now().toEpochMilli() - startTime));
            obj.put("message", "Submission Date Updation Failure->");
        }
        log.info("End of AuthorRestController submitManuscript");
        log.info("Response Time :" + (Instant.now().toEpochMilli() - startTime));
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }


    @PostMapping("getResearchFunders")
    public ResponseEntity<?> getResearchFunders(@RequestBody FunderDetailsVO funderDetailsVO) {
        log.info("Inside AuthorRestController getResearchFunders");
        JSONObject obj = new JSONObject();
        try {
            List<FunderDetailsVO> funderList = manuScriptFileService.getResearchFunders(funderDetailsVO);
            if (funderList != null && !funderList.isEmpty()) {
                obj.put("data", funderList);
            } else {
                obj.put("message", "Funder not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @PostMapping("saveManuscriptFunders")
    public ResponseEntity<?> saveResearchFundersForManuscript(@RequestBody List<FunderDetailsVO> funderDetailsVO) {
        log.info("Inside AuthorRestController saveResearchFundersForManuscript");
        JSONObject obj = new JSONObject();
        try {
            manuScriptFileService.saveManuscriptFunders(funderDetailsVO);
            obj.put("message", "Manuscript Funder Save");
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @PostMapping("getManuscriptFunders")
    public ResponseEntity<?> getManuscriptFunders(@RequestBody FunderDetailsVO funderDetailsVO) {
        log.info("Inside AuthorRestController getManuscriptFunders");
        JSONObject obj = new JSONObject();
        try {
            List<FunderDetailsVO> list = manuScriptFileService.getManuscriptFunders(funderDetailsVO);
            if (list != null && !list.isEmpty()) {
                obj.put("Data", list);
            } else {
                obj.put("Data", list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @PostMapping("getAuthorInvitationByUserId")
    public ResponseEntity<?> getAuthorInvitationByUserId(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        List<AuthorInvitation> authorInvitation = authorInvitationService.getAuthorInvitationByUserId(id);
        if (authorInvitation != null && !authorInvitation.isEmpty()) {
            obj.put("data", authorInvitation);
        } else {
            obj.put("data", "Data Not Found");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("createAuthorInvitation")
    public ResponseEntity<?> createAuthorInvitation(@RequestBody AuthorInvitation authorInvitation) {
        JSONObject obj = new JSONObject();
        try {
            authorInvitationService.createAuthorInvitation(authorInvitation);
            obj.put("data", "AuthorInvitation Send Successfully");
        } catch (Exception e) {
            obj.put("data", "Something Went Wrong");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("readInvitation")
    public ResponseEntity<?> readInvitation(@RequestBody IDVO id) {
        JSONObject obj = new JSONObject();
        try {
            authorInvitationService.readInvitation(id);
            obj.put("data", "Invitation read Successfully");
        } catch (Exception e) {
            obj.put("data", "Something Went Wrong");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    /***************** Ravi ******************/

    @RequestMapping(value = "/listUserMakedReviewer", method = RequestMethod.GET)
    public ResponseEntity<?> listUserMakedReviewer() {
        JSONObject obj = new JSONObject();
        List<User> userList = userService.getlistUserMakedReviewer();
        if (userList != null) {
            obj.put("data", userList);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        obj.put("data", "Data not found");
        return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/approvedByEditorMakeReviewer", method = RequestMethod.POST)
    public ResponseEntity<?> approvedByEditorMakeReviewer(@RequestBody IDVO Vo) {
        JSONObject obj = new JSONObject();
        try {
            userService.approvedByEditorMakeReviewer(Vo.getId());
        } catch (Exception e) {
            obj.put("data", "Something went wrong.");
            return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
        }
        obj.put("data", "Mailed transfer Successful");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("authorSubmissionFeedBack")
    public ResponseEntity<?> authorSubmissionFeedBack(@RequestBody IDVO Vo) {
        JSONObject obj = new JSONObject();
        try {
            String message = manuScriptFileService.authorSubmissionFeedBack(Vo);
            if (message != null && !message.isEmpty()) {
                obj.put("data", message);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "Data Not Saved");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        } catch (Exception e) {
            obj.put("data", "Something went wrong.");
            return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @PostMapping("acceptForProductionFileUpload")
    public ResponseEntity<?> acceptForProductionFileUpload(@RequestParam("manuScriptId") Integer manuScriptId,
                                                           MultipartFile[] files, @RequestParam("manuscriptVersion") Integer manuscriptVersion, @RequestParam("fileStatus") String fileStatus,
                                                           @RequestParam("journalId") Integer journalId, HttpServletRequest request) {
        log.info("Accept for production file upload.");
        long startTime = Instant.now().toEpochMilli();
        log.info("Request Time :" + Instant.now());
        JSONObject obj = new JSONObject();
        List<ManuScriptFile> manuscriptFileList = new ArrayList<ManuScriptFile>();

        String attachmentFolderPath = UPLOAD_FOLDER + File.separator + "UniprrFileFolder"
                + File.separator + "MNT_LWW_JOURNAL_" + "R" + manuscriptVersion + "_" + journalId + "_" + manuScriptId;
        log.info("Folder Path Create : " + attachmentFolderPath);
        File dir = new File(attachmentFolderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            BufferedOutputStream stream = null;
            try {
                byte[] bytes = file.getBytes();
                File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
                stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                double fileSizeinBytes = file.getSize();
                double fileSizeInKB = Math.round((fileSizeinBytes / 1024));

                String serverFilePath = BundlingUtils.generateFilePath(manuscriptVersion, journalId, manuScriptId, UPLOAD_FOLDER, file.getOriginalFilename(), serverENV, serverIP, serverPort);

                ManuScriptFile getManuScriptFileDetails = manuScriptFileRepo.uploadUniqueFileWithTheName(manuScriptId, manuscriptVersion, file.getOriginalFilename());
                if (getManuScriptFileDetails != null) {
                    obj.put("message", "File is already present -:> " + file.getOriginalFilename());
                } else {
                    ManuScriptFile ManuScriptFile = new ManuScriptFile();
                    ManuScriptFile.setFileName(file.getOriginalFilename());
                    ManuScriptFile.setFilePath(serverFilePath);
                    ManuScriptFile.setFileSize((int) fileSizeInKB);
                    ManuScriptFile.setManuScriptId(manuScriptId);
                    ManuScriptFile.setFileType("Accept for production");
                    ManuScriptFile.setFileVersion(manuscriptVersion);
                    ManuScriptFile.setFileStatus(fileStatus);
                    ManuScriptFile.setIsActive(1);
                    ManuScriptFile.setFileActive(0);
                    ManuScriptFile manuScriptFile = manuScriptFileService.saveFile(ManuScriptFile);
                    manuscriptFileList.add(manuScriptFile);
                    obj.put("You successfully uploaded accept for roduction file=", files.length);
                }
            } catch (Exception e) {
                log.info("You failed to upload File");
                obj.put("You failed to upload", file.getOriginalFilename());
                log.info("Failue Time :" + (Instant.now().toEpochMilli() - startTime));
                return new ResponseEntity<Object>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("You successfully uploaded accept for roduction file");
        log.info("Response Time :" + (Instant.now().toEpochMilli() - startTime));
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @PostMapping("getManuscriptComments")
    public ResponseEntity<?> getCommentsByManuscriptId(@RequestParam("manuscriptId") Integer manuscriptId) {
        log.info("Inside AuthorRestController of getCommentsByManuscriptId");
        JSONObject obj = new JSONObject();
        try {
            List<ManuscriptCommentVO> manu = manuScriptFileService.getCommentsByManuscriptId(manuscriptId);
//            List<ManuscriptComment> manu = manuScriptFileService.getCommentsByManuscriptId(manuscriptId);

            if (manu != null && !manu.isEmpty()) {
                log.info("Data Found");
                return new ResponseEntity<Object>(manu, HttpStatus.OK);
            } else {
                log.info("Data Not Found");
                obj.put("message", "Data Not Found");
            }

        } catch (Exception e) {
            log.info("Error");
            obj.put("message", "Error");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }


    @RequestMapping(value = "/author/uniqueEmailCount", method = RequestMethod.POST)
    public ResponseEntity<?> getUniqueEmailCount(@PathParam("manuscriptId") Integer manuscriptId,
                                                 @PathParam("email") String email) {
        log.info("Inside AuthorRestController getCoAuthorList");
        Long emailCount = manuscriptService.getCoAuthorList(manuscriptId).stream().
                filter(obj -> StringUtils.equalsIgnoreCase(obj.getEmail(), email)).count();
        JSONObject obj = new JSONObject();
        if (emailCount > 0)
            return new ResponseEntity<>(obj.put("message", "Duplicate Email Found"), HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(obj.put("message", "Unique Emails Found"), HttpStatus.OK);
    }


    @RequestMapping(value = "/manuscriptTypeJournal/articleList")
    public ResponseEntity<?> getArticleTypeListByJournalId(@RequestBody IDVO vo) {
        log.info("Inside AuthorRestController getManuscriptTypeListByJournalId");
        List<ManuscriptTypeJournalVO> list;
        try {
            list = manuscriptService.getArticleTypeListByJournalId(vo.getJournalId());
        } catch (Exception e) {
            JSONObject obj = new JSONObject();
            log.info("Something went wrong");
            obj.put("message", "Something went wrong");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/manuscriptTypeJournal/updateArticleNumberFlag")
    public ResponseEntity<?> updateArticleNumberFlag(@RequestBody List<ManuscriptTypeJournalVO> articleList) {
        log.info("Inside AuthorRestController updateArticleNumberFlag");
        for (ManuscriptTypeJournalVO vo : articleList)
            manuscriptService.updateArticleNumberFlag(vo);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/author/correspondingAuthorPresent", method = RequestMethod.POST)
    public ResponseEntity<?> correspondingAuthorPresent(@RequestBody IDVO vo) {

        log.info("Inside AuthorRestController getCoAuthorList");
        JSONObject jsonObject = new JSONObject();
        List<ManuscriptCoAuthorVO> list = manuscriptService.getCoAuthorList(vo.getId());
        Long count = list.stream().filter(obj -> StringUtils.equalsIgnoreCase(obj.getIsCorrespondingAuthor(), "Y")).count();
        JournalVO journal = journalService.getJournalById(vo.getJournalId());
        String result = (count.intValue() == 0 || count.intValue() > journal.getCoAuthorCount()) ? "N" : "Y";
        jsonObject.put("result", result);
        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }

    @GetMapping(value = "/getManuscriptSyncData/{manuscriptCode}/{manuscriptType}")
    public ResponseEntity<?> manuscriptSyncData(@PathVariable String manuscriptCode, @PathVariable String manuscriptType) {
        log.info("AuthorRestController/manuscriptSyncData started");
        log.info(AppConstants.REQUEST_METHOD + " :: " + "GET" +
                AppConstants.REQUEST_URI + " :: " + "/getManuscriptSyncData" +
                AppConstants.REQUEST_PARAMS + " :: " + manuscriptCode + " " + manuscriptType);
        ManuScript manuscriptToSync = manuscriptService.findManuscriptByCode(manuscriptCode);
        List<ArticleMappingDetail> articleMappingDetails = new ArrayList<>();
        if ("Commentary".equals(manuscriptType)) {
            //Get main manuscript details for the Commentary
            ManuScript manuscript = manuscriptService.findManuscriptByCommentary(manuscriptCode);
            if (manuscript != null) {
                ArticleMappingDetail articleCommentaryMappingDetail = new ArticleMappingDetail(manuscriptCode, manuscript.getManuscriptCode(), manuscript.getManuscriptTypeDetails().getManuscriptType());
                articleMappingDetails.add(articleCommentaryMappingDetail);
            }
        } else if ("Authors Response".equals(manuscriptType)) {
            //Get main manuscript details for Author Response
            ManuScript manuscript = manuscriptService.findManuscriptByAuthorResponse(manuscriptCode);
            if (manuscript != null) {
                ArticleMappingDetail articleLTEMappingDetail = new ArticleMappingDetail(manuscriptCode, manuscript.getManuscriptCode(), manuscript.getManuscriptTypeDetails().getManuscriptType());
                articleMappingDetails.add(articleLTEMappingDetail);
            }
        } else if (StringUtils.containsIgnoreCase(manuscriptType, "letter") || StringUtils.containsIgnoreCase(manuscriptType, "correspondence")) {
            //Get Linked Manuscript for LTE
            List<ManuScript> lteLinkedManuscripts = manuscriptService.findManuscriptByLTE(manuscriptCode);
            if (lteLinkedManuscripts != null && !lteLinkedManuscripts.isEmpty()) {
                List<ArticleMappingDetail> mapppingDetails = lteLinkedManuscripts.stream()
                        .map(mapppingDetail -> new ArticleMappingDetail(manuscriptCode, mapppingDetail.getManuscriptCode(), mapppingDetail
                                .getManuscriptTypeDetails()
                                .getManuscriptType()))
                        .collect(Collectors.toList());
                articleMappingDetails.addAll(mapppingDetails);
            }

            //Get Author Response for LTE
            List<ManuScript> authorResponseManuscripts = manuscriptService.findAuthorResponseByLTE(manuscriptCode);
            if (authorResponseManuscripts != null && !authorResponseManuscripts.isEmpty()) {
                List<ArticleMappingDetail> mapppingDetails = authorResponseManuscripts.stream()
                        .map(mapppingDetail -> new ArticleMappingDetail(manuscriptCode, mapppingDetail.getManuscriptCode(), mapppingDetail
                                .getManuscriptTypeDetails()
                                .getManuscriptType()))
                        .collect(Collectors.toList());
                articleMappingDetails.addAll(mapppingDetails);
            }

            //Get list of commentaries linked with the manuscript
            List<ManuScript> commentaries = manuScriptRepo.getCommentaryIds(manuscriptToSync.getManuScriptId());
            if (commentaries != null && !commentaries.isEmpty()) {
                List<ArticleMappingDetail> comms = commentaries.stream()
                        .map(mapppingDetails -> new ArticleMappingDetail(manuscriptCode, mapppingDetails.getManuscriptCode(), "Commentary"))
                        .collect(Collectors.toList());
                articleMappingDetails.addAll(comms);
            }
        } else {
            //Get list of commentaries linked with the manuscript
            List<ManuScript> commentaries = manuScriptRepo.getCommentaryIds(manuscriptToSync.getManuScriptId());
            if (commentaries != null && !commentaries.isEmpty()) {
                List<ArticleMappingDetail> comms = commentaries.stream()
                        .map(mapppingDetails -> new ArticleMappingDetail(manuscriptCode, mapppingDetails.getManuscriptCode(), "Commentary"))
                        .collect(Collectors.toList());
                articleMappingDetails.addAll(comms);
            }

            //Get letter to editor linked with the manuscript
            if (manuscriptToSync.getLteManuscriptId() != null && manuscriptToSync.getLteManuscriptId() != -1) {
                ManuScript lte = manuscriptService.findManuscriptById(manuscriptToSync.getLteManuscriptId());
                if (lte != null) {
                    ArticleMappingDetail articleLTEMappingDetail = new ArticleMappingDetail(manuscriptCode, lte.getManuscriptCode(), lte
                            .getManuscriptTypeDetails()
                            .getManuscriptType());
                    articleMappingDetails.add(articleLTEMappingDetail);
                }
            }
        }


        return new ResponseEntity<>(articleMappingDetails, HttpStatus.OK);
    }

    @PostMapping("/author/finalRecommendationFileUpload")
    public ResponseEntity<Object> finalRecommendationFileUpload(@RequestParam("manuscriptId") Integer manuscriptId,
                                                                MultipartFile[] files, @RequestParam("manuscriptVersion") Integer manuscriptVersion, @RequestParam("fileStatus") String fileStatus,
                                                                @RequestParam("journalId") Integer journalId, HttpServletRequest request) {

        log.info("Inside AuthorRestController finalRecommendationFileUpload");

        JSONObject obj = new JSONObject();

        String manuscriptBasePath = UPLOAD_FOLDER + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R" + manuscriptVersion + "_" + journalId + "_" + manuscriptId;

        File dir = new File(manuscriptBasePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        MultipartFile file;
        File serverFile = null;
        List<ManuScriptFile> manuscriptFiles = new ArrayList<>();
        ManuScriptFile manucriptFile;
        boolean isUploaded = true;
        for (int i = 0; i < files.length; i++) {
            file = files[i];
            serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));) {
                byte[] bytes = file.getBytes();
                stream.write(bytes);
                FileUtil.updateFilePermission(serverFile);
                
                //File Validtion check
        		Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
        		boolean isValidFile = FileUploadValidation.validateFile(serverFile, expectedMimeTypes);
        		if (!isValidFile) {
        	        isUploaded = false;
        	        log.error("Uploaded file validation failed for file: " + file.getOriginalFilename());
        	        log.info("Deleting invalid file: " + serverFile.delete());
        	        continue; 
        	    }

                double fileSizeinBytes = file.getSize();
                double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
                String fileServerPath = BundlingUtils.generateFilePath(manuscriptVersion, journalId, manuscriptId, UPLOAD_FOLDER, file.getOriginalFilename(), serverENV, serverIP, serverPort);

                manucriptFile = manuScriptFileRepo.uploadUniqueFileWithTheName(manuscriptId, manuscriptVersion, file.getOriginalFilename());
                if (manucriptFile == null) {
                    manucriptFile = new ManuScriptFile();
                }
                manucriptFile.setFileName(file.getOriginalFilename());
                manucriptFile.setFilePath(fileServerPath);
                manucriptFile.setFileSize((int) fileSizeInKB);
                manucriptFile.setManuScriptId(manuscriptId);
                manucriptFile.setFileType("FINALREVISION");
                manucriptFile.setFileVersion(manuscriptVersion);
                manucriptFile.setFileStatus(fileStatus);
                manucriptFile.setIsActive(1);
                manucriptFile.setFileActive(0);

                manuscriptFiles.add(manucriptFile);
            } catch (IOException e) {
                isUploaded = false;
                log.error("Error while uploading file before submission for bundling : " + e.getMessage(), e);
            }
        }

        if (!manuscriptFiles.isEmpty()&&isUploaded) {
            try {
                manuScriptFileService.saveAll(manuscriptFiles);
            } catch (Exception e) {
                isUploaded = false;
                log.error("Error while saving manuscript file details in DB : " + e.getMessage(), e);
            }
        }

        String message = !isUploaded ? "Failed to upload file(s)" : "success";
        obj.put("message", message);
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }
    
//    @PostMapping("getMissingAndCorruptedFiles")
//    public ResponseEntity<?> downloadFileByManuScriptFileID(@RequestBody IDVO id) {
//        log.info("Inside AuthorRestController downloadFileByManuScriptFileID");
//        JSONObject obj = new JSONObject();
//        
//        try {
//
//            // Fetch missing and corrupt files
//        	String FailureReason="";
//            List<String> missingFiles = new ArrayList<>();
//            List<String> corruptFiles = new ArrayList<>();
//            Integer manuscriptId = id.getId();
//            Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(manuscriptId);
//            List<ManuScriptFile> listManuscriptFile = manuScriptFileRepo.getManuscriptByactiveCM(manuscriptId, maxVersion, 1);
//            List<BundleTracker> manuscriptStatus = bundleTrackerRepo.manuscriptFailedStatus(manuscriptId);
//            List<ManuScriptFile> manuscriptFile;
//            BundleTracker bundleTracker = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);
//            if (manuscriptStatus != null && !manuscriptStatus.isEmpty()) {
//                manuscriptFile = listManuscriptFile.stream()
//                    .filter(i -> !(i.getFileName().equalsIgnoreCase("Bundled.pdf") ||
//                                   i.getFileName().equalsIgnoreCase("Bundled.docx") ||
//                                   i.getFileName().equalsIgnoreCase("Bundled.doc") ||
//                                   i.getFileName().equalsIgnoreCase("BundledPdfForReviewer.pdf")))
//                    .collect(Collectors.toList());
//            } else {
//                manuscriptFile = listManuscriptFile;
//            }
//
//            if (manuscriptFile != null && !manuscriptFile.isEmpty()) {
//                for (ManuScriptFile mf : manuscriptFile) {
//                	Integer journalId= manuScriptFileRepo.getJorunalId(manuscriptId);
//                    String mansucriptBasePath = UPLOAD_FOLDER + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R" + mf.getFileVersion() + "_" + journalId + "_" + mf.getManuScriptId();
//
//                    File dir = new File(mansucriptBasePath);
//                	File serverFile = new File(dir.getAbsolutePath() + File.separator + mf.getFileName());
//                    Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
//
//                	if (!isFileAvailable(mf.getFilePath())) {
//                        log.info("Missing File: " + mf.getFileName());
//                        missingFiles.add(mf.getFileName());
//                    } 
//                   
//                    // Check for corrupt files
//                    else if (!missingFiles.contains(mf.getFileName())) { // Ensure file is not in missing list
//        	            boolean isValidFile = FileUploadValidation.validateFile(serverFile, expectedMimeTypes);
//        	            if (!isValidFile) {
//        	            	log.info("Corrupt File: " + mf.getFileName());
//        	                corruptFiles.add(mf.getFileName());
//        	            }
//        	        }
//                	
//                	String bundleStatus = bundleTracker.getBundleStatus();
//                	if("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus)){
//                		FailureReason = bundleTracker.getBundleErrorMessage();
//                	}
//        	        // Only replace default success messages for "Failed" and "Failed2" cases
//        	        if ((FailureReason == null || FailureReason.trim().isEmpty() ||
//        	            "Bundle file creation completed".contains(FailureReason.trim()))
//        	            && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
//        	        	FailureReason = "";
//        	        }
//        	        if (FailureReason != null && FailureReason.contains("Error occured during renaming of Main Document/First Page files") && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
//        	        	FailureReason =""; 
//        	        }
//                }
//            }
//
//            obj.put("missingFiles", missingFiles);
//            obj.put("corruptFiles", corruptFiles);
//            obj.put("FailureReason", FailureReason);
//            
//        } catch (Exception e) {
//            log.error("Error in downloadFileByManuScriptFileID", e);
//            obj.put("data", "Something went wrong");
//        }
//        
//        return new ResponseEntity<>(obj, HttpStatus.OK);
//    }
    
    @PostMapping("getMissingAndCorruptedFiles")
    public ResponseEntity<?> getMissingAndCorruptedFiles(@RequestBody IDVO id) {
        log.info("Inside AuthorRestController getMissingAndCorruptedFiles");
        JSONObject obj = new JSONObject();

        try {
            String FailureReason = "";
            List<String> missingFiles = new ArrayList<>();
            List<String> corruptFiles = new ArrayList<>();
            Integer manuscriptId = id.getId();
            Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(manuscriptId);
            List<ManuScriptFile> listManuscriptFile = manuScriptFileRepo.getManuscriptByactiveCM(manuscriptId, maxVersion, 1);
            List<BundleTracker> manuscriptStatus = bundleTrackerRepo.manuscriptFailedStatus(manuscriptId);
            List<ManuScriptFile> manuscriptFile;
            BundleTracker bundleTracker = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);

            if (manuscriptStatus != null && !manuscriptStatus.isEmpty()) {
                manuscriptFile = listManuscriptFile.stream()
                        .filter(i -> !(i.getFileName().equalsIgnoreCase("Bundled.pdf") ||
                                i.getFileName().equalsIgnoreCase("Bundled.docx") ||
                                i.getFileName().equalsIgnoreCase("Bundled.doc") ||
                                i.getFileName().equalsIgnoreCase("BundledPdfForReviewer.pdf")))
                        .collect(Collectors.toList());
            } else {
                manuscriptFile = listManuscriptFile;
            }

            if (manuscriptFile != null && !manuscriptFile.isEmpty()) {
                for (ManuScriptFile mf : manuscriptFile) {
                    Integer journalId = manuScriptFileRepo.getJorunalId(manuscriptId);
                    String mansucriptBasePath = UPLOAD_FOLDER + File.separator + "UniprrFileFolder" +
                            File.separator + "MNT_LWW_JOURNAL_" + "R" + mf.getFileVersion() + "_" + journalId + "_" + mf.getManuScriptId();
                    File dir = new File(mansucriptBasePath);
                    File serverFile = new File(dir.getAbsolutePath() + File.separator + mf.getFileName());
                    Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
                    String bundleFilePath = mf.getFilePath().replace(baseUrl, uniprrFolderPath);
                    File bundleFile = new File(bundleFilePath);
                    
                    // Check if file exists
                    if (!serverFile.exists() || serverFile.length() == 0) {
                        log.info("Missing or empty file: {}. Attempting restore from backup...", mf.getFileName());
                        boolean restored = restoreFromBackup(bundleFile, mf.getFileName());
                        if (restored && bundleFile.exists() && bundleFile.length() > 0) {
                            log.info("File restored successfully: {}", mf.getFileName());
                        } else {
                            log.error("Backup not found for file: {}", mf.getFileName());
                            missingFiles.add(mf.getFileName());
                            continue;
                        }
                    }

                    // Check for corrupt files
                    boolean isValidFile = FileUploadValidation.validateFile(serverFile, expectedMimeTypes);
                    if (!isValidFile) {
                        log.warn("Corrupt file detected: {}", mf.getFileName());
                        corruptFiles.add(mf.getFileName());
                    }

                    // Handle FailureReason
                    String bundleStatus = bundleTracker.getBundleStatus();
                    if ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus)) {
                        FailureReason = bundleTracker.getBundleErrorMessage();
                    }

                    if ((FailureReason == null || FailureReason.trim().isEmpty() ||
                            "Bundle file creation completed".contains(FailureReason.trim()))
                            && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
                        FailureReason = "";
                    }

                    if (FailureReason != null && FailureReason.contains("Error occured during renaming of Main Document/First Page files")
                            && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
                        FailureReason = "";
                    }
                }
            }

            obj.put("missingFiles", missingFiles);
            obj.put("corruptFiles", corruptFiles);
            obj.put("FailureReason", FailureReason);

        } catch (Exception e) {
            log.error("Error in getMissingAndCorruptedFiles", e);
            obj.put("data", "Something went wrong");
        }

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    /**
     * Restore missing or empty file from latest backup.
     */
    private boolean restoreFromBackup(File originalFile, String realFileName) {
        File parentDir = originalFile.getParentFile();
        File backupDir = new File(parentDir, "Backup");

        if (!backupDir.exists() || !backupDir.isDirectory()) {
            log.error("No Backup folder found in: {}", parentDir.getAbsolutePath());
            return false;
        }

        // Find all backups with pattern <realFileName>_timestamp
        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith(realFileName + "_"));
        if (backups == null || backups.length == 0) {
            log.error("No backup files found for {}", realFileName);
            return false;
        }

        // Sort by last modified → latest first
        Arrays.sort(backups, Comparator.comparingLong(File::lastModified).reversed());
        File latestBackup = backups[0];

        try {
            Files.copy(latestBackup.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("Restored {} from latest backup {}", realFileName, latestBackup.getName());
            return true;
        } catch (IOException e) {
            log.error("Error restoring {} from backup {}", realFileName, latestBackup.getName(), e);
            return false;
        }
    }

    
    private boolean isFileAvailable(String fileUrl) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false; // File not found or inaccessible
        }
    }
    
    private boolean isFileValid(String fileUrl) {
        try (InputStream inputStream = new URL(fileUrl).openStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            return bytesRead > 0; // If content is present, it's not corrupt
        } catch (IOException e) {
            return false; // Corrupt or unreadable file
        }
    }
}