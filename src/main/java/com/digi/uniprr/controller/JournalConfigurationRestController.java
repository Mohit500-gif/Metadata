package com.digi.uniprr.controller;

import static com.digi.uniprr.utils.AppConstants.CONFIGURE_JOURNAL;
import static com.digi.uniprr.utils.AppConstants.YES;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.digi.uniprr.VO.*;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.JournalTatConfigurationTransformationUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.model.CharacterLimit;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalconfigFileUpload;
import com.digi.uniprr.model.JournalconfigMasterfileType;
import com.digi.uniprr.model.Keywords;
import com.digi.uniprr.model.ManuscriptType;
import com.digi.uniprr.model.Subject;
import com.digi.uniprr.repository.CharacterLimitRepo;
import com.digi.uniprr.service.JournalConfigurationService;
import com.digi.uniprr.service.JournalService;
import com.digi.uniprr.service.ManuscriptTypeChargeService;
import com.digi.uniprr.service.ManuscriptTypeService;
import com.digi.uniprr.service.SubjectService;
import com.digi.uniprr.service.WorkflowRoleService;
import com.digi.uniprr.service.WorkflowService;

@RestController
@RequestMapping("/journalconfig")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JournalConfigurationRestController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ManuscriptTypeChargeService manuscriptTypeChargeService;
    @Autowired
    Environment environment;
    @Autowired
    WorkflowRoleService workflowRoleService;
    @Autowired
    JournalService journalService;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    ManuscriptTypeService manuscriptTypeService;
    @Autowired
    private JournalConfigurationService journalConfigurationService;
    
    @Autowired
    private CharacterLimitRepo characterLimitRepo;
    @Value("${uniprrFolderPath}")
    private String UPLOAD_FOLDER;

    @Value("${thumbnailFilePath}")
    private String THUMBNAIL_UPLOAD_FOLDER;

    @Value("${thumbnailUrl}")
    private String THUMBNAIL_URL;
    
    private static final String TITLE_LIMIT = "100";
    private static final String RUNNING_TITLE_LIMIT = "50";
    private static final String ABSTRACT_LIMIT = "250";

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public JournalsConfigurationVO journalConfigurationInitiate(@RequestBody ManageJournalVO manageJournalVO) {
        log.info("Inside JournalConfigurationRestController journalConfigurationInitiate");
        Journal journal;
        JournalsConfigurationVO journalsConfigurationVO;
        if (manageJournalVO.getJournalConfiguration().equals(CONFIGURE_JOURNAL)) {
            journalsConfigurationVO = new JournalsConfigurationVO();
            if (manageJournalVO.getIsCopyConfiguration()) {
                journal = journalConfigurationService.getJournalById(manageJournalVO.getExistingJournalId());
                journalsConfigurationVO = journalConfigurationService.mapJournalProperties(journal);
            }
            journalsConfigurationVO.setIsNew(true);
        } else {
            journal = journalConfigurationService.getJournalById(manageJournalVO.getExistingJournalId());
            journalsConfigurationVO = journalConfigurationService.mapJournalProperties(journal);
            journalsConfigurationVO.setIsNew(false);
        }
        return journalsConfigurationVO;
    }

    @RequestMapping(value = "/saveJournalThumbnail", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> uploadJournalThumbnail(@RequestParam("file") MultipartFile file,
                                                                  @RequestParam("journalCode") String journalCode, HttpServletRequest request) throws IOException {

        String location = THUMBNAIL_UPLOAD_FOLDER;
        String fileBaseUrl = THUMBNAIL_URL;
        JSONObject obj = new JSONObject();
        BufferedOutputStream stream = null;
        String filename = "one.jpg";
        // Save thumbnail file
        if (file != null) {
            try {
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                filename = journalCode + "-thumbnail" + "." + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                if (!new File(location).isDirectory()) {
                    new File(location).mkdir();
                }
                File serverFile = new File(location + filename);
                stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
            } catch (IOException e) {
                obj.put("You failed to upload", file.getOriginalFilename());
                log.error("Image Upload failes {}, {}", e.getMessage(), e);
                return new ResponseEntity<Object>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        obj.put("filePath", fileBaseUrl + filename);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveJournalConfiguration", method = RequestMethod.POST)
    public ResponseEntity<?> saveJournalConfiguration(@RequestBody JournalsConfigurationVO journalsConfigurationVO) {
        log.info("Inside JournalConfigurationRestController saveJournalConfiguration");
        JSONObject obj = new JSONObject();

        if(journalsConfigurationVO.getManuscriptTypeJournals()!=null
                && !journalsConfigurationVO.getManuscriptTypeJournals().isEmpty())
        {
            journalsConfigurationVO=JournalTatConfigurationTransformationUtils.checkIfDuplicateManuscriptTypes(journalsConfigurationVO);
        }

        try {
            if (journalsConfigurationVO == null) {
                obj.put("message", "Invalid request body.");
                return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
            }
            // Save journal details & manuscript type for journal
//			journalsConfigurationVO.setManusriptTypeCharges(new ArrayList<>());
//			journalsConfigurationVO.setManusriptTypeCharge(new ArrayList<>());

            if (journalsConfigurationVO.getManusriptTypeCharges() != null) {
                List<JournalManuscriptTypeChargeVo> journalManuscriptTypeChargeVos = journalsConfigurationVO.getManusriptTypeCharges();

                for (JournalManuscriptTypeChargeVo journalManuscriptTypeChargeVo : journalManuscriptTypeChargeVos) {
                    if (journalManuscriptTypeChargeVo.getCountryChargeOnSubmission() != null
                            && !journalManuscriptTypeChargeVo.getCountryChargeOnSubmission().equals("No")) {
                        journalManuscriptTypeChargeVo.setChargeOnSubmission("Yes");
                    }
                    if (journalManuscriptTypeChargeVo.getCountryChargeOnPublication() != null
                            && !journalManuscriptTypeChargeVo.getCountryChargeOnPublication().equals("false")) {
                        journalManuscriptTypeChargeVo.setChargeOnPublication("Yes");
                    }
                }
                journalsConfigurationVO.setManusriptTypeCharges(journalManuscriptTypeChargeVos);
            }


            Integer journalId = journalConfigurationService.configureAndSaveJournal(journalsConfigurationVO);
            if (journalId == null || journalId <= 0) {
                obj.put("message", "Failed to configure and save journal.");
                return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // Journal metadata/configuration save
            journalConfigurationService.saveJournalMetadata(journalsConfigurationVO, journalId);

            Integer userId = getUserId(journalsConfigurationVO);
            // Default workflow create, if workflow not exist for journal
            workflowService.createJournalWorkflow(journalId, userId);
            // Re-map manuscript charge with manuscript type.
//			if (isChargeEnabled(journalsConfigurationVO)) {
            saveCharge(journalsConfigurationVO);
//			}
            obj.put("message", "Successfully Created");
            obj.put("journalId", journalId);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while saving journal configuration.", e);
            obj.put("message", "Something went wrong.");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Integer getUserId(JournalsConfigurationVO journalsConfigurationVO) {
        if (journalsConfigurationVO != null && journalsConfigurationVO.getCreatedBy() != null) {
            try {
                return Integer.valueOf(journalsConfigurationVO.getCreatedBy());
            } catch (NumberFormatException e) {
                log.error("Failed to parse createdBy as Integer.", e);
            }
        }
        return 0;
    }

    private boolean isChargeEnabled(JournalsConfigurationVO journalsConfigurationVO) {
        return journalsConfigurationVO != null && YES.equalsIgnoreCase(journalsConfigurationVO.getChargeFlag());
    }

    private void saveCharge(JournalsConfigurationVO journalsConfigurationVO) {
        if (journalsConfigurationVO != null) {
            try {
                if ("manuscript_type".equalsIgnoreCase(journalsConfigurationVO.getChargeOn())) {
                    manuscriptTypeChargeService.saveJournalManuscriptTypeCharge(journalsConfigurationVO);
                } else{
                    journalConfigurationService.saveJournalChargeDetails(journalsConfigurationVO, journalsConfigurationVO.getJournalId());
                }
                log.info("Payment setting updated for journal from journal configuration tab {}  {} {}", journalsConfigurationVO.getJournalId(), new Date(), journalsConfigurationVO.getCreatedBy());
            } catch (Exception e) {
                log.error("An error occurred while updating journal charge.", e);
            }
        }
    }

    @RequestMapping(value = "/saveJournalPaymentSetting", method = RequestMethod.POST)
    public ResponseEntity<?> saveJournalPaymentSetting(@RequestBody JournalsConfigurationVO journalsConfigurationVO) {
        log.info("Inside JournalConfigurationRestController saveJournalConfiguration");
        JSONObject obj = new JSONObject();

        try {
            if (journalsConfigurationVO == null) {
                log.error("Invalid input: journalsConfigurationVO is null");
                obj.put("message", "Invalid input");
                return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
            }
            String chargeStatus = journalService.updateJournalCharge(journalsConfigurationVO.getJournalId(), journalsConfigurationVO.getChargeFlag(), journalsConfigurationVO.getChargeOn());

            log.info("Journal charge updated {}  {} {}", journalsConfigurationVO.getJournalId(), new Date(), chargeStatus + ":" + journalsConfigurationVO.getChargeFlag());
            if (YES.equalsIgnoreCase(journalsConfigurationVO.getChargeFlag())) {
                if ("manuscript_type".equalsIgnoreCase(journalsConfigurationVO.getChargeOn())) {
                    manuscriptTypeChargeService.updateJournalManuscriptTypeCharge(journalsConfigurationVO);
                } else {
                    journalConfigurationService.saveJournalChargeDetails(journalsConfigurationVO, journalsConfigurationVO.getJournalId());
                }
            } else {
                manuscriptTypeChargeService.deleteJournalManuscriptTypeCharge(journalsConfigurationVO);
            }
            log.info("Payment setting updated for journal from Payment setting tab {}  {} {}", journalsConfigurationVO.getJournalId(), new Date(), journalsConfigurationVO.getCreatedBy());
            obj.put("message", "Payment setting updated successfully");
            obj.put("journalId", journalsConfigurationVO.getJournalId());
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while saving journal configuration.", e);
            obj.put("message", "Something went wrong.");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/deleteManuscriptTypeJournal", method = RequestMethod.POST)
    public ResponseEntity<?> deleteManuscriptTypeJournal(@RequestBody IDVO vo) {
        log.info("Inside JournalConfigurationRestController deleteManuscriptTypeJournal");
        journalConfigurationService.deleteManuscriptTypeJournal(vo.getId());
        JSONObject obj = new JSONObject();
        obj.put("message", "Succesfully Deleted");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveJournalBlindType", method = RequestMethod.POST)
    public ResponseEntity<?> saveJournalBlindType(@RequestParam Integer id, @RequestParam String blindType) {
        log.info("Inside JournalConfigurationRestController saveJournalBlindType");
        journalConfigurationService.saveJournalBlindType(id, blindType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Upload Files
    @PostMapping("getFileList")
    public List<JournalconfigFileUpload> getFileList(@RequestBody JournalVO journalVO) {
        log.info("Inside JournalConfigurationRestController getFileList");
        return journalConfigurationService.getFileList(journalVO.getJournalId());
    }

    @PostMapping("deleteJournalFile")
    public ResponseEntity<Object> deleteJournalFile(@RequestBody JournalVO journalVO) {
        log.info("Inside JournalConfigurationRestController deleteJournalFile");
        JSONObject obj = new JSONObject();
        try {
            String result = journalConfigurationService.deleteJournalconfigFile(journalVO);
            obj.put("data", result);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> uploadMultipleFileHandler(@RequestParam("files") MultipartFile[] files,
                                                                     @RequestParam("journalId") Integer journalId, HttpServletRequest request) throws IOException {
        log.info("Inside JournalConfigurationRestController uploadMultipleFileHandler");
        log.info("Give Permission to Medknow Folder");
        // String location = System.getProperty("catalina.base");
        String location = UPLOAD_FOLDER;
        String attachmentFolderPath = location + File.separator + "UniprrFileJournalConfigFile" + File.separator
                + "MNT_LWW_JOURNAL_" + journalId;

        File dir = new File(attachmentFolderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        JSONObject obj = new JSONObject();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            BufferedOutputStream stream = null;
            try {
                byte[] bytes = file.getBytes();

                File serverFile = new File(dir + File.separator + file.getOriginalFilename());
                stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                double fileSizeinBytes = file.getSize();
                double fileSizeInKB = Math.round((fileSizeinBytes / 1024));

                Integer port = request.getServerPort();
                InetAddress inetAddress = InetAddress.getLocalHost();
                String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;

                /*
                 * String FilePath = Ip + File.separator + "UniprrFileJournalConfigFile" +
                 * File.separator + "MNT_LWW_JOURNAL_" + journalId + File.separator +
                 * file.getOriginalFilename();
                 */
                String FilePath = null;

                if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
                    String Ip1 = "https://" + environment.getProperty("awsFileIp");
                    FilePath = Ip1 + File.separator + "UniprrFileJournalConfigFile" + File.separator
                            + "MNT_LWW_JOURNAL_" + journalId + File.separator + file.getOriginalFilename();
                } else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
                    FilePath = Ip + File.separator + "UniprrFileJournalConfigFile" + File.separator + "MNT_LWW_JOURNAL_"
                            + journalId + File.separator + file.getOriginalFilename();

                } else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
                    String Ip1 = "https://" + environment.getProperty("awsFileIp") + ":"
                            + environment.getProperty("server.status.port");
                    FilePath = Ip1 + File.separator + "UniprrFileJournalConfigFile" + File.separator
                            + "MNT_LWW_JOURNAL_" + journalId + File.separator + file.getOriginalFilename();
                }
                JournalconfigFileUpload JournalconfigFileUpload = new JournalconfigFileUpload();
                JournalconfigFileUpload.setFileName(file.getOriginalFilename());
                JournalconfigFileUpload.setFilePath(FilePath);
                JournalconfigFileUpload.setFileSize((int) fileSizeInKB);
                JournalconfigFileUpload.setJournalId(journalId);
                journalConfigurationService.saveFile(JournalconfigFileUpload);
                List<JournalconfigFileUpload> fileList = journalConfigurationService.getFileList(journalId);
            } catch (Exception e) {
                obj.put("You failed to upload", file.getOriginalFilename());
                return new ResponseEntity<Object>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        obj.put("You successfully uploaded file=", files.length);
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @GetMapping("getFileTypeList")
    public List<JournalconfigMasterfileType> getFileTypeList() {
        log.info("Inside JournalConfigurationRestController getFileTypeList");
        return journalConfigurationService.getFileTypeList();
    }

    @PostMapping("saveList")
    public ResponseEntity<?> saveList(@RequestBody List<JournalconfigFileUpload> journalconfigFileUpload) {
        log.info("Inside JournalConfigurationRestController saveList");
        JSONObject obj = new JSONObject();
        try {
            String result = journalConfigurationService.saveList(journalconfigFileUpload);
            obj.put("data", result);
        } catch (Exception e) {
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @PostMapping("downloadJournalFile")
    public ResponseEntity<?> downloadJournalFile(@RequestBody JournalVO journalVO) {
        log.info("Inside JournalConfigurationRestController downloadJournalFile");
        JSONObject obj = new JSONObject();
        try {
            String result = journalConfigurationService.downloadJournalFile(journalVO);
            obj.put("FilePath", result);
        } catch (Exception e) {
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<Object>(obj, HttpStatus.OK);
    }

    @PostMapping("/downloadJournalFileWithFileType")
    public ResponseEntity<Resource> downloadJournalFileWithFileType(
            @RequestBody JournalVO journalVO) {

        log.info("=======================================================");
        log.info("Inside JournalConfigurationRestController");
        log.info("downloadJournalFileWithFileType");
        log.info("Journal Id : {}", journalVO.getJournalId());
        log.info("File Type Id : {}", journalVO.getId());

        return journalConfigurationService
                .downloadJournalFileWithFileType(journalVO);
    }

    /********* Keywords ******/
    @PostMapping("getJournalKeyWordList")
    public List<KeywordsVO> getJournalKeyWordList(@RequestBody JournalVO journalVO) {
        log.info("Inside JournalConfigurationRestController getJournalKeyWordList");
        List<KeywordsVO> voList = journalConfigurationService.getJournalKeyWordList(journalVO.getId());
        return voList;
    }

    @PostMapping("createKeyWords")
    public ResponseEntity<?> createKeyWords(@RequestBody List<Keywords> KeyWords) {
        log.info("Inside JournalConfigurationRestController createKeyWords");
        JSONObject obj = new JSONObject();
        try {
            journalConfigurationService.saveKeyWords(KeyWords);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Some Error occured");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

        obj.put("message", "Keywords created Successfully");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/editKeywords", method = RequestMethod.POST)
    public ResponseEntity<?> editKeywords(@RequestBody Keywords vo) {
        log.info("Inside JournalConfigurationRestController editKeywords");
        JSONObject obj = new JSONObject();
        try {
            Keywords keywords = journalConfigurationService.updateKeyword(vo);
            obj.put("data", "Updated...");
        } catch (Exception e) {
            obj.put("data", "Something Went Wrong");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/editKeywordsList", method = RequestMethod.POST)
    public ResponseEntity<?> editKeywordsList(@RequestBody CommonVO reviewer) {
        log.info("Inside JournalConfigurationRestController editKeywordsList");
        journalConfigurationService.updateKeywordList(reviewer.getReviewer());
        JSONObject obj = new JSONObject();
        obj.put("message", "Object List updated succesfully");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @GetMapping("getJournalsList")
    public List<Journal> getJournalsList() {
        log.info("Inside JournalConfigurationRestController getJournalsList");
        return journalConfigurationService.getJournalsList();
    }

    // Dev Here New

    @PostMapping("getPlagiarismlist")
    public ResponseEntity<?> getjournalPlagiarismlist(@RequestParam("journalId") Integer journalId) {
        log.info("Inside JournalConfigurationRestController getjournalPlagiarismlist");
        JSONObject obj = new JSONObject();
        try {
            List<JournalPlagiarismMasterVO> list = journalConfigurationService.getjournalPlagiarismlist(journalId);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("updatePlagiarism")
    public ResponseEntity<?> updatePlagiarism(@RequestBody List<JournalPlagiarismUpdateVO> journalPlagiarismUpdateVO) {
        log.info("Inside JournalConfigurationRestController updatePlagiarism");
        JSONObject obj = new JSONObject();
        try {
            journalConfigurationService.updatePlagiarism(journalPlagiarismUpdateVO);
            obj.put("message", "Data is Updated");
        } catch (Exception e) {
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    /********* Assign Admin ***********/
    @RequestMapping(value = "/assignAdminList", method = RequestMethod.POST)
    public ResponseEntity<?> getAssignAdminList(@RequestBody IDVO vo) {
        log.info("Inside JournalConfigurationRestController getAssignAdminList");
        List<Integer> userId = journalConfigurationService.getAssignAdminIdList(vo.getId());
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }

    @RequestMapping(value = "/assignAdmin", method = RequestMethod.POST)
    public ResponseEntity<?> assignAdmin(@RequestBody AssignAdminVO assignAdminVO) {
        log.info("Inside JournalConfigurationRestController assignAdmin");
        journalConfigurationService.assignAdmin(assignAdminVO);
        JSONObject obj = new JSONObject();
        obj.put("message", "Object saved successfully");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getjournalTatConfigurationList")
    public ResponseEntity<?> getjournalTatConfigurationList(@RequestBody TurnAroundTimeVO turnAroundTimeVO) {
        log.info("Inside JournalConfigurationRestController getjournalTatConfigurationList");
        return new ResponseEntity<>(journalConfigurationService.getjournalTatConfigurationList(turnAroundTimeVO),
                HttpStatus.OK);
    }

    // Save And Update
    @PostMapping("saveJournalTatConfiguration")
    public ResponseEntity<?> saveJournalTatConfiguration(
            @RequestBody JournalTatConfigurationVO journalTatConfigurationVO) {
        log.info("Inside JournalConfigurationRestController saveJournalTatConfiguration");
        JSONObject obj = new JSONObject();
        String message = journalConfigurationService.saveJournalTatConfiguration(journalTatConfigurationVO);
        obj.put("message", message);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @GetMapping("getEmailTemplateData")
    public ResponseEntity<?> getEmailTemplateData() {
        log.info("Inside JournalConfigurationRestController getEmailTemplateData");
        return new ResponseEntity<>(journalConfigurationService.getEmailTemplateData(), HttpStatus.OK);
    }

    // Save And Update Journal Mail Template
    @PostMapping("saveJournalMailTemplate")
    public ResponseEntity<?> saveJournalMailTemplate(@RequestBody JournalMailTemplatesVO journalMailTemplatesVO) {
        log.info("Inside JournalConfigurationRestController saveJournalMailTemplate");
        JSONObject obj = new JSONObject();
        boolean isJournalMailTemplate = false;
        isJournalMailTemplate = journalConfigurationService.isJournalMailTemplate(journalMailTemplatesVO);
        try {
            if (isJournalMailTemplate && journalMailTemplatesVO.getJournalEmailTemplateId() == null) {
                obj.put("message", "MailTemplate For This Journal Is Already Created");
            } else {
                String message = journalConfigurationService.saveJournalMailTemplate(journalMailTemplatesVO);
                obj.put("message", message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("deleteJournalTemplate")
    public ResponseEntity<?> deleteJournalTemplate(@RequestBody JournalMailTemplatesVO journalMailTemplatesVO) {
        log.info("Inside JournalConfigurationRestController deleteJournalTemplate");
        JSONObject obj = new JSONObject();
        try {
            journalConfigurationService.deleteJournalTemplate(journalMailTemplatesVO);
            obj.put("data", "Delete successfully");
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Some Error occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    // Bulk Add Up Mail Template
    @PostMapping("bulkSaveJournalMailTemplate")
    public ResponseEntity<?> bulkSaveJournalMailTemplate(
            @RequestBody List<JournalMailTemplatesVO> journalMailTemplatesVO) {
        log.info("Inside JournalConfigurationRestController bulkSaveJournalMailTemplate");
        JSONObject obj = new JSONObject();
        try {
            String message = journalConfigurationService.bulkSaveJournalMailTemplate(journalMailTemplatesVO);
            obj.put("data", message);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Some Error occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getEmailTemplatesForAutoMailers")
    public ResponseEntity<?> getEmailTemplates(@RequestBody IDVO id) {
        log.info("Inside JournalConfigurationRestController getEmailTemplates");
        JSONObject obj = new JSONObject();
        try {
            List<JournalMailTemplatesVO> data = journalConfigurationService.getEmailTemplatesForAutoMailers(id);
            if (data != null && !data.isEmpty()) {
                return new ResponseEntity<>(data, HttpStatus.OK);
            } else {
                obj.put("message", "Data not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getEmailTemplates")
    public ResponseEntity<?> getEmailTemplates(@RequestParam("journalId") Integer journalId) {
        log.info("Inside JournalConfigurationRestController getEmailTemplates");
        JSONObject obj = new JSONObject();
        try {
            List<JournalMailTemplatesVO> data = journalConfigurationService.getEmailTemplates(journalId);
            if (data != null && !data.isEmpty()) {
                return new ResponseEntity<>(data, HttpStatus.OK);
            } else {
                obj.put("message", "Data not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Some Error occured");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    // Auto Mailers

    @GetMapping("getTaskName")
    public ResponseEntity<?> getTaskName() {
        log.info("Inside JournalConfigurationRestController getTaskName");
        return new ResponseEntity<>(journalConfigurationService.getTaskName(), HttpStatus.OK);
    }

    @GetMapping("getTaskNameForReminder")
    public ResponseEntity<?> getTaskNameForReminder() {
        log.info("Inside JournalConfigurationRestController getTaskName");
        return new ResponseEntity<>(journalConfigurationService.getTaskNameForReminder(), HttpStatus.OK);
    }

    @PostMapping("saveAutoMailersForReminder")
    public ResponseEntity<?> saveAutoMailersForReminder(@RequestBody AutoMailersVO autoMailersVO) {
        log.info("Inside JournalConfigurationRestController saveAutoMailers");
        JSONObject obj = new JSONObject();
        boolean isAutomailerExist = false;
        isAutomailerExist = journalConfigurationService.isAutomailerForReminderExist(autoMailersVO);
        if (!isAutomailerExist) {
            try {
                String data = journalConfigurationService.saveAutoMailersForReminder(autoMailersVO);
                obj.put("message", data);
                return new ResponseEntity<>(obj, HttpStatus.OK);

            } catch (Exception e) {
                log.info("Some Error occured");
                obj.put("message", "Some Error occured");
                return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            obj.put("message", "Reminder For This Journal Is Already Assign");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
    }

    @PostMapping("saveAutoMailers")
    public ResponseEntity<?> saveAutoMailers(@RequestBody AutoMailersVO autoMailersVO) {
        log.info("Inside JournalConfigurationRestController saveAutoMailers");
        JSONObject obj = new JSONObject();
        boolean isAutomailerExist = false;
        isAutomailerExist = journalConfigurationService.isAutomailerExist(autoMailersVO);
        if (!isAutomailerExist) {
            try {
                String data = journalConfigurationService.saveAutoMailers(autoMailersVO);
                obj.put("message", data);
                return new ResponseEntity<>(obj, HttpStatus.OK);

            } catch (Exception e) {
                log.info("Some Error occured");
                obj.put("message", "Some Error occured");
                return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            obj.put("message", "Task For This Journal Is Already Assign");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
    }

    @PostMapping("getAutoMailersListForReminder")
    public ResponseEntity<?> getAutoMailersListForReminder(@RequestBody AutoMailersVO autoMailersVO) {
        log.info("Inside JournalConfigurationRestController getAutoMailesList");
        JSONObject obj = new JSONObject();
        List<AutoMailersVO> list = journalConfigurationService.getAutoMailersListForReminder(autoMailersVO);
        if (list != null && !list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            obj.put("message", "Data not found");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
    }

    @PostMapping("getAutoMailersList")
    public ResponseEntity<?> getAutoMailesList(@RequestBody AutoMailersVO autoMailersVO) {
        log.info("Inside JournalConfigurationRestController getAutoMailesList");
        JSONObject obj = new JSONObject();
        List<AutoMailersVO> list = journalConfigurationService.getAutoMailesList(autoMailersVO);
        if (list != null && !list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            obj.put("message", "Data not found");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
    }

    @PostMapping("deleteAutoMailerForReminder")
    public ResponseEntity<?> deleteAutoMailerForReminder(@RequestBody AutoMailersVO autoMailersVO) {
        log.info("Inside JournalConfigurationRestController deleteAutoMailer");
        JSONObject obj = new JSONObject();
        String message = journalConfigurationService.deleteAutoMailerForReminder(autoMailersVO);
        obj.put("message", message);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("deleteAutoMailer")
    public ResponseEntity<?> deleteAutoMailer(@RequestBody AutoMailersVO autoMailersVO) {
        log.info("Inside JournalConfigurationRestController deleteAutoMailer");
        JSONObject obj = new JSONObject();
        String message = journalConfigurationService.deleteAutoMailer(autoMailersVO);
        obj.put("message", message);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("updateJournalWorkflow")
    public ResponseEntity<?> updateJournalWorkflow(@RequestBody WorkFlowSetUpVO workFlowSetUpVO) {
        JSONObject obj = new JSONObject();
        String result = "";
        if (workFlowSetUpVO.getWorkFlowId() != null) {
            try {
                result = workflowRoleService.workFlowUpdateForJournal(workFlowSetUpVO);
                String[] temp = result.split(",");
                Integer workflowId = Integer.parseInt(temp[0]);
                result = journalConfigurationService.updateJournalWorkflow(workflowId, workFlowSetUpVO.getJournalId());

            } catch (Exception e) {
                result = "Something Went Wrong";
            }
        } else {
            result = "workflow Id is null";
        }
        obj.put("data", result);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("getWorkflowByJournalId")
    public ResponseEntity<?> getWorkflowByJournalId(@RequestBody WorkFlowSetUpVO workFlowSetUpVO) {
        JSONObject obj = new JSONObject();
        if (workFlowSetUpVO.getJournalId() != null) {
            try {
                WorkFlowSetUpVO workFlowSetUpVODetail = workflowService
                        .getJournalWorkFlowById(workFlowSetUpVO.getJournalId());
                if (workFlowSetUpVODetail != null) {
                    obj.put("data", workFlowSetUpVODetail);
                } else {
                    obj.put("data", "No Data Found");
                }
            } catch (Exception e) {
                obj.put("data", "Something Went Wrong");
            }
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    /********************* OPERATIONAL POLICIES AND BLIND ************************/
    @PostMapping("getOperationalPolices")
    public ResponseEntity<?> getOperationalPolices(@RequestBody JournalPoliciesVO journalPoliciesVO) {
        JSONObject obj = new JSONObject();
        log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST +
                AppConstants.REQUEST_URI + " :: /journalconfig/getOperationalPolices" +
                AppConstants.REQUEST_BODY + " :: " + journalPoliciesVO.toString());
        try {
            JournalPoliciesVO getJournalPolicies = journalService.getOperationalPolices(journalPoliciesVO);
            if (getJournalPolicies != null) {
                obj.put("data", getJournalPolicies);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "No Data Found");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something Went Wrong");
            return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("setupOperationalPolices")
    public ResponseEntity<?> setupOperationalPolices(@RequestBody JournalPoliciesVO journalPoliciesVO) {
        JSONObject obj = new JSONObject();
        try {
            String message = journalService.setupOperationalPolices(journalPoliciesVO);
            if (message != null && !message.isEmpty()) {
                obj.put("data", message);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "No Data Found");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something Went Wrong");
            return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("getJournalSubjectList")
    public ResponseEntity<?> getJournalSubjectList(@RequestBody JournalSubjectVo journalSubjectVo) {
        log.info("Inside JournalConfigurationRestController getAutoMailesList");
        JSONObject obj = new JSONObject();
        try {
            List<JournalSubjectVo> list = journalConfigurationService.getJournalSubjectList(journalSubjectVo);
            if (list != null && !list.isEmpty()) {
                obj.put("data", list);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "Data not found");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something Went Wrong");
            return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("saveJournalSubject")
    public ResponseEntity<?> saveJournalSubject(@RequestBody JournalSubjectVo journalSubjectVo) {
        log.info("Inside JournalConfigurationRestController getAutoMailesList");
        JSONObject obj = new JSONObject();
        try {
            boolean isSubjectExist = false;
//			isSubjectExist = journalConfigurationService.isSubjectExist(journalSubjectVo.getJournalSubject());

            isSubjectExist = journalConfigurationService.isSubjectExist(journalSubjectVo.getJournalSubject(), journalSubjectVo.getJournalId());

            if (isSubjectExist) {
                obj.put("data", "Subject is already present");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                String saveJournalSubject = journalConfigurationService.saveJournalSubject(journalSubjectVo);
                if (saveJournalSubject != null && !saveJournalSubject.isEmpty()) {
                    obj.put("data", saveJournalSubject);
                    return new ResponseEntity<>(obj, HttpStatus.OK);
                } else {
                    obj.put("data", "Data not found");
                    return new ResponseEntity<>(obj, HttpStatus.OK);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something Went Wrong");
            return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("deleteJournalSubject")
    public ResponseEntity<?> deleteJournalSubject(@RequestBody JournalSubjectVo journalSubjectVo) {
        log.info("Inside JournalConfigurationRestController getAutoMailesList");
        JSONObject obj = new JSONObject();
        try {
            String deleteJournalSubject = journalConfigurationService.deleteJournalSubject(journalSubjectVo);
            if (deleteJournalSubject != null && !deleteJournalSubject.isEmpty()) {
                obj.put("data", deleteJournalSubject);
                return new ResponseEntity<>(obj, HttpStatus.OK);
            } else {
                obj.put("data", "Data not found");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            obj.put("data", "Something Went Wrong");
            return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("TestDataUpload")
    public ResponseEntity<?> TestDataUpload() {

        String str = "Test";

        try {
            // FileWriter myWriter = new FileWriter("");
            JournalsConfigurationVO journalsConfigurationVO = new JournalsConfigurationVO();

            File file = new File("C:\\Users\\80076\\Desktop\\JournalDatamigration.xlsx"); // creating a new file
            // instance
            FileInputStream fis = new FileInputStream(file); // obtaining bytes from the file
            // creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator(); // iterating over excel file
            int j = 0;
            while (itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
                int i = 0;
                if (j != 0) {
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (i == 0) {
                            // journalsConfigurationVO.setJournalCode(cell.toString());
                        } else if (i == 1) {
                            // journalsConfigurationVO.setJournalShortTitle(cell.toString());
                        } else if (i == 2) {
                            journalsConfigurationVO.setJournalTitle(cell.toString());
                        } else if (i == 3) {
                            // journalsConfigurationVO.setPubCode(cell.toString());
                            journalsConfigurationVO.setJournalCode(cell.toString());
                        } else if (i == 4) {

                        } else if (i == 5) {
                            // journalsConfigurationVO.setJournalMode(cell.toString() + "Only");
                            if (cell.toString().equalsIgnoreCase("1.0")) {
                                journalsConfigurationVO.setJournalFrequency("One");
                            } else if (cell.toString().equalsIgnoreCase("2.0")) {
                                journalsConfigurationVO.setJournalFrequency("Two");
                            } else if (cell.toString().equalsIgnoreCase("3.0")) {
                                journalsConfigurationVO.setJournalFrequency("Three");
                            } else if (cell.toString().equalsIgnoreCase("4.0")) {
                                journalsConfigurationVO.setJournalFrequency("Four");
                            } else if (cell.toString().equalsIgnoreCase("6.0")) {
                                journalsConfigurationVO.setJournalFrequency("Six");
                            } else if (cell.toString().equalsIgnoreCase("12.0")) {
                                journalsConfigurationVO.setJournalFrequency("Year");
                            } else {
                                journalsConfigurationVO.setJournalFrequency(cell.toString());
                            }

                        } else if (i == 6) {
                            /*
                             * List<Subject> subjectList =
                             * subjectService.getSubjectByName(cell.toString().trim()); if (subjectList !=
                             * null && !subjectList.isEmpty()) { Subject subject = subjectList.get(0);
                             * subjectList.add(subject); } journalsConfigurationVO.setSubject(subjectList);
                             */
                        } else if (i == 7) {
                            journalsConfigurationVO.setUrl(cell.toString());
                        } else if (i == 8) {
                            // journalsConfigurationVO.setJournalIssn(cell.toString());
                        } else if (i == 9) {
                            // journalsConfigurationVO.setJournalEissn(cell.toString());
                        } else if (i == 10) {
                            journalsConfigurationVO.setJournalDoi("10.4103/");
                        } else if (i == 11) {
                            /*
                             * List<ManuscriptTypeJournalVO> manuscriptTypeJournalVOList = new ArrayList();
                             * String strData[] = cell.toString().trim().split(","); for (String string :
                             * strData) { ManuscriptType manuscriptType = manuscriptTypeService
                             * .getManuscriptTypeByName(string.trim()); ManuscriptTypeJournalVO
                             * manuscriptTypeJournalVO = new ManuscriptTypeJournalVO();
                             * manuscriptTypeJournalVO.setMtjId(manuscriptType.getId());
                             * manuscriptTypeJournalVO.setManutypeName(manuscriptType.getName());
                             * manuscriptTypeJournalVO.setIsActive(manuscriptType.getIsActive());
                             * manuscriptTypeJournalVOList.add(manuscriptTypeJournalVO); }
                             * journalsConfigurationVO.setManuscriptTypeJournals(manuscriptTypeJournalVOList
                             * );
                             */
                        }
                        journalsConfigurationVO.setIsNew(true);
                        i++;
                    }
                    log.info("Inside JournalConfigurationRestController saveJournalConfiguration");
                    Integer journalId = journalConfigurationService.configureAndSaveJournal(journalsConfigurationVO);
                    log.debug("<<<<<<<<<----------------------->>>>>>" + journalId);
                    journalConfigurationService.saveJournalMetadata(journalsConfigurationVO, journalId);

                    if (journalsConfigurationVO.getChargeFlag() != null
                            && journalsConfigurationVO.getChargeFlag().equalsIgnoreCase(YES)) {
                        if (journalsConfigurationVO.getChargeOn() != null
                                && journalsConfigurationVO.getChargeOn().equalsIgnoreCase("manuscript_type")) {
                            manuscriptTypeChargeService.saveJournalManuscriptTypeCharge(journalsConfigurationVO);
                        } else {
                            journalConfigurationService.saveJournalChargeDetails(journalsConfigurationVO, journalId);
                        }

                    }
                }
                // myWriter.write(System.getProperty("line.separator"));
                log.debug("");
                log.debug("One Row COmplete" + j);
                j++;
            }
            // myWriter.close();
            log.debug("Journal All Set");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject();
        if (str != null && str != "") {
            obj.put("data", str);
        } else {
            obj.put("data", "Data Not Found");
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping("/saveJournalHistoryData")
    public ResponseEntity<?> saveJournalHistoryData(@RequestBody JournalHistoryDataVO journalHistoryDataVO) {
        log.info("Inside JournalConfigurationRestController saveJournalHistoryData");
        JSONObject obj = new JSONObject();
        try {
            journalConfigurationService.saveJournalHistoryData(journalHistoryDataVO);
            obj.put("message", "Successfully saved");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Something went wrong.");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/getCharacterLimits")
    public ResponseEntity<Map<String, Object>> getCharacterLimits(@RequestBody CharacterLimitVo chaLimitVo) {
        log.info("Inside JournalConfigurationRestController getCharacterLimits");
        
        CharacterLimit data = characterLimitRepo.findById(chaLimitVo.getJournalId()).orElse(null);
        Map<String, String> limits = new LinkedHashMap<>();
        Map<String, Object> response = new HashMap<>();
        if (data==null) {
        
	        limits.put("title", TITLE_LIMIT);
	        limits.put("runningTitle", RUNNING_TITLE_LIMIT);
	        limits.put("abstract", ABSTRACT_LIMIT);
	        response.put("data", limits);
	       
	        return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            limits.put("title", data.getTitle());
            limits.put("runningTitle", data.getRunningTitle());
            limits.put("abstract", data.getAbstractTitle());
            response.put("data", limits);
            
        	return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    
//    @PostMapping("/saveCharacterLimit")
//    public ResponseEntity<?> saveCharacterLimits(@RequestBody CharacterLimitVo characterLimitVo) {
//        log.info("Inside JournalConfigurationRestController saveCharacterLimit");
//        
//        JSONObject obj = new JSONObject();
//        try {
//            String message = journalConfigurationService.saveCharacterLimit(characterLimitVo);
//            obj.put("message", message);
//            return ResponseEntity.ok(obj);
//        } catch (Exception e) {
//            log.error("Error in saveCharacterLimits: ", e);
//            obj.put("message", "Something went wrong.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
//        }
//    }

    @PostMapping("/createJournalworkflow")
    public ResponseEntity<?> createJournalworkflow() {
        log.info("Inside JournalConfigurationRestController save workflow for all journal if workflow not available");
        JSONObject obj = new JSONObject();
        try {

            List<JournalListVO> journalList = journalService.getJournalDetails();
            for (JournalListVO journalListVO : journalList) {

                workflowService.createJournalWorkflow(journalListVO.getJournalId(), 10995);

            }

            obj.put("message", "workflow created succesfully");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", "Something went wrong.");
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }

    }

}
