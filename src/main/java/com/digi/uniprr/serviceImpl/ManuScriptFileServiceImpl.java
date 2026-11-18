package com.digi.uniprr.serviceImpl;

import static com.digi.uniprr.utils.AppConstants.Avg_Syllables_Count;
import static com.digi.uniprr.utils.AppConstants.Avg_Word_Length;
import static com.digi.uniprr.utils.AppConstants.Difficult_Words_Ratio;
import static com.digi.uniprr.utils.AppConstants.FILE_PATH;
import static com.digi.uniprr.utils.AppConstants.Flesch_Kincaid_Grade;
import static com.digi.uniprr.utils.AppConstants.Flesch_Reading_Ease;
import static com.digi.uniprr.utils.AppConstants.Gunning_Fog_Index;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_ABSTRACT;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_ACKNOWLEDGMENT;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_BLACK_WHITE_FIGURES;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_COLOR_FIGURES;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_FIGURES;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_INTRODUCTION;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_PAGES;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_PARAGRAPH;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_REFERENCES;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_RUNNING_HEAD;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_SHORT_TITLE;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_TABLES;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_TITLE;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_TYPE;
import static com.digi.uniprr.utils.AppConstants.MANUSCRIPT_WORDS;
import static com.digi.uniprr.utils.AppConstants.POS_Ratio;
import static com.digi.uniprr.utils.AppConstants.Sentence_Length;
import static com.digi.uniprr.utils.AppConstants.Sentence_No;
import static com.digi.uniprr.utils.AppConstants.Type_To_Token_Ratio;
import static java.io.File.separator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.digi.uniprr.VO.*;
import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.utils.DateUtil;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.digi.uniprr.service.ManuScriptFileService;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.BundlingUtils;
import com.digi.uniprr.utils.SftpUtil;
import com.google.common.net.HttpHeaders;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

@Service
@Transactional
public class ManuScriptFileServiceImpl implements ManuScriptFileService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ManuScriptRepo manuScriptRepo;
    @Autowired
    private ManuScriptFileRepo manuScriptFileRepo;
    @Autowired
    private ManuscriptCommentRepo manuscriptCommentRepo;

    @Autowired
    private Environment environment;

    @Autowired
    private FunderDetailsRepo funderDetailsRepo;

    @Autowired
    private ManuscriptFunderRepo manuscriptFunderRepo;

    @Autowired
    private AuthorSubmissionFeedbackRepo authorSubmissionFeedbackRepo;

    @Autowired
    private PlagiarismCheckRepo plagiarismCheckRepo;


    @Value("${uniprrFolderPath}")
    private String UPLOAD_FOLDER;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${uniprrFolderPath}")
    private String uniprrFolderPath;

    @Value("${awsIp}")
    private String sftpIp;

    @Value("${awsPort}")
    private int sftpPort;

    @Value("${awsHost}")
    private String sftpUser;

    @Value("${awsPassword}")
    private String sftpPassword;

    @Value("${server.status}")
    private String serverENV;

    @Value("${awsFileIp}")
    private String serverIP;

    @Value("${server.status.port}")
    private String serverPort;

    @Value("${enable.unitouch.integration}")
    private boolean enableUnitouchIntegration;

    @Value("${unitouch.cdn.url}")
    private String unitouchCdnUrl;

    @Value("${unitouch.path}")
    private String unitouchPath;

    @Value("${environment.type}")
    private String environmentType;

    @Override
    public String createManuScriptFile(ManuScriptFile manuScriptFile) {
        manuScriptFileRepo.save(manuScriptFile);
        return "Created successfully";
    }

    @Override
    public List<ManuScriptFile> getManuScriptFileDetails() {

        return manuScriptFileRepo.findAll();
    }

    @Override
    public String updateManuScriptFileDetails(ManuScriptFile manuScriptFile) {
        manuScriptFileRepo.save(manuScriptFile);
        return "Updated successfully";
    }

    @Override
    public String deleteManuScriptFileById(Integer id) {
        manuScriptFileRepo.deleteById(id);
        return "deleted successfully";
    }

    @Override
    public boolean isIdExist(Integer id) {
        return manuScriptFileRepo.existsById(id);
    }

    @Override
    public List<ManuScriptFile> getFileDetailsById(ManuScriptFile manuScriptFile) {
        return manuScriptFileRepo.getFileDetailsById(manuScriptFile.getManuscriptFileId());
    }

    @Override
    public ManuScriptFile saveFile(ManuScriptFile manuScriptFile) {
        return manuScriptFileRepo.saveAndFlush(manuScriptFile);
    }

    @Override
    public void saveAll(List<ManuScriptFile> manuScriptFiles) {
        manuScriptFileRepo.saveAll(manuScriptFiles);
    }

    @Override
    public ManuScriptFile getListByManuscriptId(Integer id) {
        return manuScriptFileRepo.getById(id);
    }

    @Override
    public ManuScriptFile getManuScriptFile(ManuScriptFile manuscriptFile) {

        return manuScriptFileRepo.getManuscriptFileDetilsById(manuscriptFile.getManuscriptFileId(),
                manuscriptFile.getFileName());
    }

    @Override
    public List<ManuscriptFileVO> getManuscriptByactive(Integer manuscriptId, Integer manuscriptVersion) {
        log.info("Inside getManuscriptByactive Impl");
        Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(manuscriptId);
        List<ManuScriptFile> manu = manuScriptFileRepo.getManuscriptByactive(manuscriptId, maxVersion, 1);
        List<ManuscriptFileVO> listVo = new ArrayList<ManuscriptFileVO>();
        if (manu != null && !manu.isEmpty()) {
            manu.stream().forEach(i -> {
                if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("MergedFile")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("PDF")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("Technical Modification Editor To Author")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("REVIEWERPDF")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("Revision Editor To Author")) {
                } else {
                    ManuscriptFileVO file = new ManuscriptFileVO();
                    file.setManuscriptFileId(i.getManuscriptFileId());
                    file.setManuScriptId(i.getManuScriptId());
                    file.setFileType(i.getFileType());
                    file.setFileName(i.getFileName());
                    file.setFileOrder(i.getFileOrder());
                    file.setFilePath(i.getFilePath());
                    file.setFileStatus(i.getFileStatus());
                    file.setContentType(i.getContentType());
                    file.setFileSize(i.getFileSize());
                    file.setFileVersion(i.getFileVersion());
                    file.setLegend(i.getLegend());
                    file.setManuscriptVersion(manuscriptVersion);
                    file.setCreatedBy(i.getCreatedBy());
                    file.setCreatedOn(i.getCreatedOn());
                    file.setUpdatedBy(i.getUpdatedBy());
                    file.setUpdatedOn(i.getUpdatedOn());
                    listVo.add(file);
                }
            });
            return listVo;
        }
        return null;
    }

    @Override
    public List<ManuscriptFileVO> getManuscriptByactiveAlt(Integer manuscriptId, Integer manuscriptVersion) {
        log.info("Inside getManuscriptByactive Impl");
        Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(manuscriptId);
        ManuScript manuScript = manuScriptRepo.getById(manuscriptId);
        List<ManuScriptFile> getFilesFromMaxVersion = manuScriptFileRepo.getDataByManuscriptIdAndVersionAll(manuscriptId, maxVersion);
        if (getFilesFromMaxVersion != null && !getFilesFromMaxVersion.isEmpty()) {
            for (ManuScriptFile manuScriptFile : getFilesFromMaxVersion) {
                manuScriptFileRepo.deleteById(manuScriptFile.getManuscriptFileId());
            }
        }
        String folderLocation = uniprrFolderPath + separator + "UniprrFileFolder" + separator
                + "MNT_LWW_JOURNAL_" + "R" + maxVersion + "_"
                + manuScript.getJournalId() + "_" + manuscriptId + separator + "output";
        log.info("**********************Path For Folder****************"
                + folderLocation);
        File dir2 = new File(folderLocation);
        if (dir2.exists() && dir2.isDirectory()) {
            File[] files = dir2.listFiles();
            if (files != null) {
                for (File fileEntry : files) {
                    fileEntry.delete();
                    log.info("File Deleted");
                }
                log.info("**********************Deleted " + folderLocation + " Successfully****************");
            } else {
                log.warn("Directory is empty or cannot be read");
            }
        } else {
            log.warn("Directory does not exist or is not a directory");
        }
        IDVO idvo = new IDVO();
        idvo.setManuscriptId(manuscriptId);
        idvo.setManuscriptVersion(manuscriptVersion);
        idvo.setJournalId(manuScript.getJournalId());
        copyAllFile(idvo);

        List<ManuScriptFile> manu = manuScriptFileRepo.getManuscriptByactive(manuscriptId, maxVersion, 1);
        List<ManuscriptFileVO> listVo = new ArrayList<ManuscriptFileVO>();
        if (manu != null && !manu.isEmpty()) {
            manu.stream().forEach(i -> {
                if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("MergedFile")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("PDF")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("Technical Modification Editor To Author")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("REVIEWERPDF")) {
                } else if (i.getFileType() != null && !i.getFileType().isEmpty()
                        && i.getFileType().equalsIgnoreCase("Revision Editor To Author")) {
                } else {
                    ManuscriptFileVO file = new ManuscriptFileVO();
                    file.setManuscriptFileId(i.getManuscriptFileId());
                    file.setManuScriptId(i.getManuScriptId());
                    file.setFileType(i.getFileType());
                    file.setFileName(i.getFileName());
                    file.setFileOrder(i.getFileOrder());
                    file.setFilePath(i.getFilePath());
                    file.setFileStatus(i.getFileStatus());
                    file.setContentType(i.getContentType());
                    file.setFileSize(i.getFileSize());
                    file.setFileVersion(i.getFileVersion());
                    file.setLegend(i.getLegend());
                    file.setManuscriptVersion(manuscriptVersion);
                    file.setCreatedBy(i.getCreatedBy());
                    file.setCreatedOn(i.getCreatedOn());
                    file.setUpdatedBy(i.getUpdatedBy());
                    file.setUpdatedOn(i.getUpdatedOn());
                    listVo.add(file);
                }
            });
        }
        return listVo;
    }


    @Override
    public String deleteManuscriptFile(Integer manuscriptFileId, HttpServletRequest request) {
        log.info("deleteManuscriptFile Impl");
        try {
            ManuScriptFile manuscriptFile = manuScriptFileRepo.downloadFileByManuScriptFileID(manuscriptFileId);
            if (manuscriptFile != null && manuscriptFile.getFileActive() != null && manuscriptFile.getFileActive().equals(1)) {
                int journalId = manuscriptFile.getManuScript().getJournalId();
                int manuscriptId = manuscriptFile.getManuScriptId();
                String basePath = UPLOAD_FOLDER + File.separator;

                String movedOldFileName = BundlingUtils.moveRenameOldFile(manuscriptFile, journalId, basePath);
                manuscriptFile.setFileName(movedOldFileName);
                manuscriptFile.setFileStatus(manuscriptFile.getFileStatus() + "-" + "oldFile");
                manuscriptFile.setIsActive(0);
                String movedFileFolderName = "PreviousFileFolder" + File.separator + movedOldFileName;
                String oldFileServerPath = BundlingUtils.generateFilePath(manuscriptFile.getFileVersion(), journalId, manuscriptId, basePath, movedFileFolderName, serverENV, serverIP, serverPort);
                manuscriptFile.setFilePath(oldFileServerPath);
                manuScriptFileRepo.save(manuscriptFile);
                log.info("File Deleted");
                return "File Deleted";
            } else if (manuscriptFile != null && manuscriptFile.getFileActive() != null && manuscriptFile.getFileActive().equals(0)) {
                String basePath = UPLOAD_FOLDER + File.separator;

                String drivePath = basePath + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
                        + manuscriptFile.getFileVersion() + "_" + manuscriptFile.getManuScript().getJournalId() + "_"
                        + manuscriptFile.getManuScriptId() + File.separator + manuscriptFile.getFileName();
                try {
                    FileUtils.forceDelete(new File(drivePath));
                    log.info("File Deleted");
                } catch (IOException e) {
                    log.info("Error");
                }
                manuScriptFileRepo.deleteById(manuscriptFile.getManuscriptFileId());
                return "File Deleted";
            } else {
                return "Please provide manusccript file id";
            }

        } catch (Exception e) {
            log.info("Error");
            return "Something went wrong";
        }
    }

//    @Override
//    public String downloadFileByManuScriptFileID(Integer manuscriptFileId) {
//        log.info("Inside downloadFileByManuScriptFileID Impl");
//        String returnFilePath;
//
//        ManuScriptFile filePath = manuScriptFileRepo.downloadFileByManuScriptFileID(manuscriptFileId);
//        String bundleFilePath = filePath.getFilePath().replace(baseUrl, uniprrFolderPath);
//        File bundleFile = new File(bundleFilePath);
//        if (bundleFile.exists() && bundleFile.length() > 0) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//            String timestamp = dateFormat.format(new Date());
//            returnFilePath = filePath.getFilePath() + "?" + timestamp;
//        } else {
//            returnFilePath = "Not available";
//        }
//        return returnFilePath;
//    }
    
    @Override
    public String downloadFileByManuScriptFileID(Integer manuscriptFileId) {
        log.info("Inside downloadFileByManuScriptFileID Impl");

        ManuScriptFile manuscriptFile = manuScriptFileRepo.downloadFileByManuScriptFileID(manuscriptFileId);
        String returnFilePath;

        // Actual file location on disk
        String bundleFilePath = manuscriptFile.getFilePath().replace(baseUrl, uniprrFolderPath);
        File bundleFile = new File(bundleFilePath);

        if (bundleFile.exists() && bundleFile.length() > 0) {
            // ✅ File exists → return with timestamp query param
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            returnFilePath = manuscriptFile.getFilePath() + "?" + timestamp;
            log.info("File available: {}", returnFilePath);
        } else {
            log.warn("File missing or corrupted: {} → Attempting restore...", bundleFilePath);

            // Try to restore from Backup
            boolean restored = restoreFromBackup(bundleFile, manuscriptFile.getFileName());

            if (restored && bundleFile.exists() && bundleFile.length() > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timestamp = dateFormat.format(new Date());
                returnFilePath = manuscriptFile.getFilePath() + "?" + timestamp;
                log.info("File restored successfully: {}", returnFilePath);
            } else {
                returnFilePath = "Not available";
                log.error("Restore failed for file: {}", manuscriptFile.getFileName());
            }
        }

        return returnFilePath;
    }

    /**
     * Restore missing or corrupted file from latest backup.
     */
//    private boolean restoreFromBackup(File originalFile, String realFileName) {
//        File parentDir = originalFile.getParentFile();
//        File backupDir = new File(parentDir, "Backup");
//
//        if (!backupDir.exists() || !backupDir.isDirectory()) {
//            log.error("No Backup folder found in: {}", parentDir.getAbsolutePath());
//            return false;
//        }
//
//        // Find all backups with pattern <realFileName>_timestamp
//        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith(realFileName + "_"));
//        if (backups == null || backups.length == 0) {
//            log.error("No backup files found for {}", realFileName);
//            return false;
//        }
//
//        // Sort by last modified → latest first
//        Arrays.sort(backups, Comparator.comparingLong(File::lastModified).reversed());
//        File latestBackup = backups[0];
//
//        try {
//            Files.copy(latestBackup.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            log.info("Restored {} from latest backup {}", realFileName, latestBackup.getName());
//            return true;
//        } catch (IOException e) {
//            log.error("Error restoring {} from backup {}", realFileName, latestBackup.getName(), e);
//            return false;
//        }
//    }


    @Override
    public String updateManuscriptFileUpload(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo,
                                             HttpServletRequest request) {
        log.info("Inside updateManuscriptFileUpload Impl");
        try {
            resubmissionManuscriptFileVo.getManuscriptFileVO().stream().forEach(i -> {
                manuScriptFileRepo.submitManuscript(i.getManuscriptFileId(), i.getFileType(), i.getLegend(),
                        resubmissionManuscriptFileVo.getManuscriptVersion());
            });

            Random random = new Random();
            int number = random.nextInt(4);
            String result = environment.getProperty("tomcat.server.path") + number + ".json";
            JSONParser jsonParser = new JSONParser();
            Object obj1 = jsonParser.parse(new FileReader(result));
            JSONObject obj = (JSONObject) obj1;
            String manuscriptType = (String) obj.get(MANUSCRIPT_TYPE);
            String manuscriptTitle = (String) obj.get(MANUSCRIPT_TITLE);
            String manuscriptRunningHead = (String) obj.get(MANUSCRIPT_RUNNING_HEAD);
            String manuscriptAbstract = (String) obj.get(MANUSCRIPT_ABSTRACT);
            String manuscriptFigures = (String) obj.get(MANUSCRIPT_FIGURES);
            String manuscriptBlackWhiteFigures = (String) obj.get(MANUSCRIPT_BLACK_WHITE_FIGURES);
            String manuscriptColorFigures = (String) obj.get(MANUSCRIPT_COLOR_FIGURES);
            String manuscriptTables = (String) obj.get(MANUSCRIPT_TABLES);
            String manuscriptWords = (String) obj.get(MANUSCRIPT_WORDS);
            String manuscriptPages = (String) obj.get(MANUSCRIPT_PAGES);
            String manuscriptParagraph = (String) obj.get(MANUSCRIPT_PARAGRAPH);
            String manuscriptIntroduction = (String) obj.get(MANUSCRIPT_INTRODUCTION);
            String manuscriptAcknowledgment = (String) obj.get(MANUSCRIPT_ACKNOWLEDGMENT);
            String manuscriptReferences = (String) obj.get(MANUSCRIPT_REFERENCES);
            String manuscriptShortTitle = (String) obj.get(MANUSCRIPT_SHORT_TITLE);
            String filePathh = (String) obj.get(FILE_PATH);
            Double avgSyllablesCount = (Double) obj.get(Avg_Syllables_Count);
            Double avgWordLength = (Double) obj.get(Avg_Word_Length);
            Double difficultWordsRatio = (Double) obj.get(Difficult_Words_Ratio);
            Double fleschKincaidGrade = (Double) obj.get(Flesch_Kincaid_Grade);
            Double fleschReadingEase = (Double) obj.get(Flesch_Reading_Ease);
            Double gunningFogIndex = (Double) obj.get(Gunning_Fog_Index);
            Double posRatio = (Double) obj.get(POS_Ratio);
            Double sentenceLength = (Double) obj.get(Sentence_Length);
            Double sentenceNo = (Double) obj.get(Sentence_No);
            Double typeToTokenRatio = (Double) obj.get(Type_To_Token_Ratio);
            /*
             * ManscriptScore manuscriptScore =
             * manscriptScoreRepo.getManscriptScoreData(resubmissionManuscriptFileVo.
             * getManuScriptId()); if (manuscriptScore != null) {
             * manuscriptScore.setManuscriptVersion(resubmissionManuscriptFileVo.
             * getManuscriptVersion());
             * manuscriptScore.setAvgSyllablesCount(avgSyllablesCount);
             * manuscriptScore.setAvgWordLength(avgWordLength);
             * manuscriptScore.setDifficultWordsRatio(difficultWordsRatio);
             * manuscriptScore.setFleschKincaidGrade(fleschKincaidGrade);
             * manuscriptScore.setFleschReadingEase(fleschReadingEase);
             * manuscriptScore.setGunningFogIndex(gunningFogIndex);
             * manuscriptScore.setPosRatio(posRatio);
             * manuscriptScore.setSentenceLength(sentenceLength);
             * manuscriptScore.setSentenceNo(sentenceNo);
             * manuscriptScore.setTypeToTokenRatio(typeToTokenRatio);
             * manscriptScoreRepo.save(manuscriptScore); } else { ManscriptScore
             * manuscriptScoreNew = new ManscriptScore();
             * manuscriptScoreNew.setManuScriptId(resubmissionManuscriptFileVo.
             * getManuScriptId());
             * manuscriptScoreNew.setManuscriptVersion(resubmissionManuscriptFileVo.
             * getManuscriptVersion());
             * manuscriptScoreNew.setAvgSyllablesCount(avgSyllablesCount);
             * manuscriptScoreNew.setAvgWordLength(avgWordLength);
             * manuscriptScoreNew.setDifficultWordsRatio(difficultWordsRatio);
             * manuscriptScoreNew.setFleschKincaidGrade(fleschKincaidGrade);
             * manuscriptScoreNew.setFleschReadingEase(fleschReadingEase);
             * manuscriptScoreNew.setGunningFogIndex(gunningFogIndex);
             * manuscriptScoreNew.setPosRatio(posRatio);
             * manuscriptScoreNew.setSentenceLength(sentenceLength);
             * manuscriptScoreNew.setSentenceNo(sentenceNo);
             * manuscriptScoreNew.setTypeToTokenRatio(typeToTokenRatio);
             * manscriptScoreRepo.save(manuscriptScoreNew); } ManuScript manuscript =
             * manuscriptRepo.getOne(resubmissionManuscriptFileVo.getManuScriptId()); if
             * (manuscript != null) { manuscript.setManuscriptTitle(manuscriptTitle);
             * manuscript.setManuscriptShortTitle(manuscriptShortTitle);
             * manuscript.setManuscriptRunningHead(manuscriptRunningHead);
             * manuscript.setManuscriptAbstract(manuscriptAbstract);
             * manuscript.setManuscriptVersion(resubmissionManuscriptFileVo.
             * getManuscriptVersion()); manuscriptRepo.save(manuscript); } ManuScriptFile
             * manu1 =
             * manuScriptFileRepo.getManuScriptFileData(resubmissionManuscriptFileVo.
             * getManuScriptId()); if (manu1 != null) { manu1.setFileType(MERGED_FILE);
             * manu1.setFileName(FINAL_DOC); manu1.setFilePath(filePathh);
             * manu1.setIsActive(1);
             * manu1.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
             * manuScriptFileRepo.save(manu1); } else { ManuScriptFile manuScript = new
             * ManuScriptFile();
             * manuScript.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
             * manuScript.setFileType(MERGED_FILE); manuScript.setFileName(FINAL_DOC);
             * manuScript.setFilePath(filePathh); manuScript.setIsActive(1);
             * manuScript.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion()
             * ); manuScriptFileRepo.save(manuScript); } ManuscriptTypeDetails
             * manusciptDetail = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(
             * resubmissionManuscriptFileVo.getManuScriptId()); if (manusciptDetail != null)
             * { manusciptDetail.setManuscriptType(manuscriptType);
             * manusciptDetail.setManuscriptFigures(manuscriptFigures);
             * manusciptDetail.setManuscriptBlackWhiteFigures(manuscriptBlackWhiteFigures);
             * manusciptDetail.setManuscriptColorFigures(manuscriptColorFigures);
             * manusciptDetail.setManuscriptTables(manuscriptTables);
             * manusciptDetail.setManuscriptWords(manuscriptWords);
             * manusciptDetail.setManuscriptPages(manuscriptPages);
             * manusciptDetail.setManuscriptParagraph(manuscriptParagraph);
             * manusciptDetail.setManuscriptIntroduction(manuscriptIntroduction);
             * manusciptDetail.setManuscriptAcknowledgment(manuscriptAcknowledgment);
             * manusciptDetail.setManuscriptReferences(manuscriptReferences);
             * manusciptDetail.setManuscriptVersion(resubmissionManuscriptFileVo.
             * getManuscriptVersion()); manuscriptTypeDetailsRepo.save(manusciptDetail); }
             * else { ManuscriptTypeDetails manuscriptTypeDetails = new
             * ManuscriptTypeDetails();
             * manuscriptTypeDetails.setManuscriptId(resubmissionManuscriptFileVo.
             * getManuScriptId()); manuscriptTypeDetails.setManuscriptType(manuscriptType);
             * manuscriptTypeDetails.setManuscriptFigures(manuscriptFigures);
             * manuscriptTypeDetails.setManuscriptBlackWhiteFigures(
             * manuscriptBlackWhiteFigures);
             * manuscriptTypeDetails.setManuscriptColorFigures(manuscriptColorFigures);
             * manuscriptTypeDetails.setManuscriptTables(manuscriptTables);
             * manuscriptTypeDetails.setManuscriptWords(manuscriptWords);
             * manuscriptTypeDetails.setManuscriptPages(manuscriptPages);
             * manuscriptTypeDetails.setManuscriptParagraph(manuscriptParagraph);
             * manuscriptTypeDetails.setManuscriptIntroduction(manuscriptIntroduction);
             * manuscriptTypeDetails.setManuscriptAcknowledgment(manuscriptAcknowledgment);
             * manuscriptTypeDetails.setManuscriptReferences(manuscriptReferences);
             * manuscriptTypeDetails.setManuscriptVersion(resubmissionManuscriptFileVo.
             * getManuscriptVersion());
             * manuscriptTypeDetailsRepo.save(manuscriptTypeDetails); }
             */
        } catch (Exception e) {
        }
        return "File Updated Successfully";
    }

    @Override
    public List<FunderDetailsVO> getResearchFunders(FunderDetailsVO funderDetailsVO) {
        try {
            if (funderDetailsVO.getFunderId() != null) {
                String url = "https://api.crossref.org/funders/" + funderDetailsVO.getFunderId();
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(url, String.class);
                if (result != null && !result.isEmpty()) {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject obj = (JSONObject) jsonParser.parse(result);
                    JSONObject message = (JSONObject) obj.get("message");
                    String funderName = (String) message.get("name");
                    String funderUri = (String) message.get("uri");
                    String funderLocation = (String) message.get("location");
                    String funderId = (String) message.get("id");
                    List<FunderDetailsVO> newfunder = new ArrayList<>();
                    FunderDetailsVO newfunderDetail = new FunderDetailsVO();
                    newfunderDetail.setFunderId(funderId);
                    newfunderDetail.setFunderName(funderName);
                    newfunderDetail.setFunderUri(funderUri);
                    newfunderDetail.setFunderLocation(funderLocation);
                    newfunder.add(newfunderDetail);
                    return newfunder;
                }
            } else {
                String url = "https://api.crossref.org/funders?query=" + funderDetailsVO.getFunderName();
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(url, String.class);
                if (result != null && !result.isEmpty()) {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject obj = (JSONObject) jsonParser.parse(result);
                    JSONObject message = (JSONObject) obj.get("message");
                    List<JSONObject> funderItems = (List<JSONObject>) message.get("items");
                    List<FunderDetailsVO> newCreatefunderByname = new ArrayList<>();
                    for (JSONObject i : funderItems) {
                        String funderId = (String) i.get("id");
                        String funderLocation = (String) i.get("location");
                        String funderName = (String) i.get("name");
                        String funderUri = (String) i.get("uri");
                        FunderDetailsVO createdFunder = new FunderDetailsVO();
                        createdFunder.setFunderId(funderId);
                        createdFunder.setFunderName(funderName);
                        createdFunder.setFunderUri(funderUri);
                        createdFunder.setFunderLocation(funderLocation);
                        newCreatefunderByname.add(createdFunder);
                    }
                    return newCreatefunderByname;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveManuscriptFunders(List<FunderDetailsVO> funderDetailsVO) {
        funderDetailsVO.stream().forEach(i -> {
            FunderDetails funderDetails = funderDetailsRepo.getResearchFunderByFunderId(i.getFunderId());
            if (funderDetails != null) {
                funderDetails.setFunderId(i.getFunderId());
                funderDetails.setFunderName(i.getFunderName());
                funderDetails.setFunderUri(i.getFunderUri());
                funderDetails.setFunderLocation(i.getFunderLocation());
                funderDetailsRepo.save(funderDetails);
                ManuscriptFunder manu = manuscriptFunderRepo.getManuscriptFunder(i.getManuScriptId(),
                        funderDetails.getId());
                if (manu == null) {
                    ManuscriptFunder manuFunder = new ManuscriptFunder();
                    manuFunder.setManuscriptId(i.getManuScriptId());
                    manuFunder.setFunderId(funderDetails.getId());
                    manuscriptFunderRepo.save(manuFunder);
                } else {
                    manu.setManuscriptId(i.getManuScriptId());
                    manu.setFunderId(manu.getId());
                    manuscriptFunderRepo.save(manu);
                }
            } else {
                FunderDetails newFunderDetail = new FunderDetails();
                newFunderDetail.setFunderId(i.getFunderId());
                newFunderDetail.setFunderName(i.getFunderName());
                newFunderDetail.setFunderUri(i.getFunderUri());
                newFunderDetail.setFunderLocation(i.getFunderLocation());
                funderDetailsRepo.save(newFunderDetail);
                ManuscriptFunder manu = manuscriptFunderRepo.getManuscriptFunder(i.getManuScriptId(),
                        funderDetails.getId());
                if (manu == null) {
                    ManuscriptFunder manuFunder = new ManuscriptFunder();
                    manuFunder.setManuscriptId(i.getManuScriptId());
                    manuFunder.setFunderId(funderDetails.getId());
                    manuscriptFunderRepo.save(manuFunder);
                } else {
                    manu.setManuscriptId(i.getManuScriptId());
                    manu.setFunderId(manu.getId());
                    manuscriptFunderRepo.save(manu);
                }
            }
        });
    }

    @Override
    public List<FunderDetailsVO> getManuscriptFunders(FunderDetailsVO funderDetailsVO) {
        List<ManuscriptFunder> manu = manuscriptFunderRepo.getManuscriptFundersList(funderDetailsVO.getManuScriptId());
        List<FunderDetailsVO> manuList = new ArrayList<>();
        if (manu != null && !manu.isEmpty()) {
            manu.stream().forEach(i -> {
                FunderDetailsVO list = new FunderDetailsVO();
                list.setManuscriptFunderId(i.getId());
                list.setManuScriptId(i.getManuscriptId());
                list.setFunderDetailsId(i.getFunderDetails().getId());
                list.setFunderId(i.getFunderDetails().getFunderId());
                list.setFunderName(i.getFunderDetails().getFunderName());
                list.setFunderLocation(i.getFunderDetails().getFunderLocation());
                list.setFunderUri(i.getFunderDetails().getFunderUri());
                manuList.add(list);
            });
        }
        return manuList;
    }

    @Override
    public String authorSubmissionFeedBack(IDVO Vo) {
        if (Vo.getJournalIdList() != null && !Vo.getJournalIdList().isEmpty() && Vo.getAuthorIdList() != null
                && !Vo.getAuthorIdList().isEmpty()) {
            AuthorSubmissionFeedback getAuthorSubmissionFeedback = authorSubmissionFeedbackRepo
                    .getAuthorSubmissionFeedbackDetails(Vo.getAuthorIdList(), Vo.getJournalIdList());
            if (getAuthorSubmissionFeedback != null) {
                getAuthorSubmissionFeedback.setFeedback(Vo.getFeedback().toString());
                getAuthorSubmissionFeedback.setUpdatedAt(new Date());
                authorSubmissionFeedbackRepo.save(getAuthorSubmissionFeedback);
                return "Author Submission FeedBack Form Updated";
            } else {
                Vo.getJournalIdList().stream().forEach(i -> {
                    Vo.getAuthorIdList().stream().forEach(j -> {
                        AuthorSubmissionFeedback addAuthorSubmissionFeedback = new AuthorSubmissionFeedback();
                        addAuthorSubmissionFeedback.setJournalId(i);
                        addAuthorSubmissionFeedback.setAuthorId(j);
                        addAuthorSubmissionFeedback.setFeedback(Vo.getFeedback().toString());
                        authorSubmissionFeedbackRepo.save(addAuthorSubmissionFeedback);
                    });
                });
                return "Author Submission FeedBack Form Saved";
            }
        }
        return null;
    }

    @Override
    public void copyAllFile(IDVO id) {
        log.info("Inside copyAllFile" + id.getManuscriptId() + " " + id.getManuscriptVersion() + " "
                + id.getJournalId());
        List<ManuScriptFile> fileList = manuScriptFileRepo
                .getAllTheManuscriptFileWithMnuscriptVerison(id.getManuscriptId());
        String fileLocation = "";
        String currentFolderLocation = "";
        if (fileList != null && !fileList.isEmpty()) {
            for (ManuScriptFile manuScriptFile : fileList) {
                if (manuScriptFile.getFileType().equalsIgnoreCase("Main Documents/Article File")) {
                    fileLocation = manuScriptFile.getFilePath();
                    log.info("fileLocation : " + fileLocation);
                    String fileName = fileLocation.substring(fileLocation.lastIndexOf("/") + 1);
                    String firstreplace = "";
                    firstreplace = firstreplace.concat("/").concat(fileName);
                    currentFolderLocation = fileLocation.replace(firstreplace, "");
                    log.info("currentFolderLocation : " + currentFolderLocation);
                    String temp2 = currentFolderLocation.substring(currentFolderLocation.lastIndexOf("/") + 1);
                    String secondreplace = "";
                    secondreplace = secondreplace.concat("/").concat(temp2);
                    fileLocation = fileLocation.replace(secondreplace, "").replace(firstreplace, "");
                    log.info("fileLocation : " + fileLocation);
                }
            }
        }

        int version = id.getManuscriptVersion() + 1;
        log.info("version : " + version);

        String folderName = fileLocation + "/" + "MNT_LWW_JOURNAL_" + "R" + version + "_" + id.getJournalId() + "_"
                + id.getManuscriptId();
        String saveFolderName = folderName;
        log.info("FolderName : " + folderName);
        folderName = folderName.replace(baseUrl, uniprrFolderPath);
        File newFolder = new File(folderName);
        if (!newFolder.exists()) {
            if (newFolder.mkdir()) {
                log.info("Directory is created!");
            } else {
                log.info("Failed to create directory!");
            }
        }

        /*
         * currentFolderLocation = currentFolderLocation.replace("http:/54.214.210.6",
         * location + File.separator + "webapps");
         */
        // FolderName = FolderName.replace("http:/54.214.210.6", location+
        // File.separator + "webapps");
        log.info(currentFolderLocation);
        currentFolderLocation = currentFolderLocation.replace(baseUrl, uniprrFolderPath);
        File source = new File(currentFolderLocation);
        log.info("source : " + source);
        File destination = new File(folderName);
        log.info("destination : " + destination);

        log.info("call CopyFolder");
        List<String> allFileName = copyFolder(source, destination);
        log.info("call CopyFolder completed");

        List<ManuScriptFile> fileExist = manuScriptFileRepo.getDataByManuscriptIdAndVersion(id.getManuscriptId(),
                version);
        if (fileExist == null || fileExist.isEmpty()) {
            for (ManuScriptFile manuScriptFile : fileList) {
                for (String string : allFileName) {
                    ManuScriptFile manuScriptFileData = new ManuScriptFile();
                    fileLocation = fileLocation.replace("//", File.separator).replace("\\", File.separator);
                    String[] dataChcek = manuScriptFile.getFilePath().replace("//", File.separator)
                            .replace("\\", File.separator).split(File.separator);
                    String checkName = dataChcek[dataChcek.length - 1];
                    log.info("checkName : " + checkName);
                    log.info("string : " + string);
                    if (string.equalsIgnoreCase(checkName)) {
                        log.info("string : " + string);
                        manuScriptFileData.setManuScriptId(manuScriptFile.getManuScriptId());
                        manuScriptFileData.setFileVersion(version);
                        manuScriptFileData.setFileType(manuScriptFile.getFileType());
                        manuScriptFileData.setFileName(manuScriptFile.getFileName());
                        manuScriptFileData.setFileSize(manuScriptFile.getFileSize());
                        manuScriptFileData.setLegend(manuScriptFile.getLegend());
                        manuScriptFileData.setFileStatus(manuScriptFile.getFileStatus());
                        manuScriptFileData.setIsActive(manuScriptFile.getIsActive());
                        if (manuScriptFile.getFileOrder() != null)
                            manuScriptFileData.setFileOrder(manuScriptFile.getFileOrder());
                        if (manuScriptFile.getFileNumber() != null)
                            manuScriptFileData.setFileNumber(manuScriptFile.getFileNumber());
                        if (manuScriptFile.getFileActive() != null)
                            manuScriptFileData.setFileActive(1);
                        //savePathUrl = savePathUrl.replace(location + File.separator + "webapps", "http:/54.214.210.6");
                        manuScriptFileData.setFilePath(saveFolderName + File.separator + string);
                        log.info("manuScriptFile sorted:  " + manuScriptFile.getFileName());
                        manuScriptFileRepo.save(manuScriptFileData);
                    }
                }
            }
        }

    }
    
    public List<String> copyFolder(File source, File destination) {
        log.info("Inside copyFolder - Source: {}, Destination: {}", source, destination);
        List<String> fileNames = new ArrayList<>();

        try {
            boolean isSamePath = source.getCanonicalPath().equals(destination.getCanonicalPath());

            if (!isSamePath) {
                FileUtils.copyDirectory(source, destination);
                log.info("Copied contents from source to destination.");

                // Delete unwanted folders
                deleteIfExists(new File(destination, "PDF"));
                deleteIfExists(new File(destination, "output"));
                deleteIfExists(new File(destination, "images"));

                File firstPage = new File(destination, "ZYX.docx");
                if (firstPage.exists()) {
                    firstPage.delete();
                    log.info("Deleted unwanted file: ZYX.docx");
                }
            } else {
                log.warn("Source and destination are the same. Skipping copy.");
            }

            // List files from the correct location
            File[] files = (isSamePath ? source : destination).listFiles();

            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                for (File fileEntry : files) {
                    if (fileEntry.isFile()) {
                        log.info("File: {} (Modified: {})", fileEntry.getName(), fileEntry.lastModified());
                        fileNames.add(fileEntry.getName());
                    }
                }
            }

        } catch (IOException e) {
            log.error("Exception in copyFolder: {}", e.getMessage(), e);
        }

        return fileNames;
    }

    private void deleteIfExists(File file) throws IOException {
        if (file.exists()) {
            FileUtils.deleteDirectory(file);
            log.info("Deleted directory: {}", file.getAbsolutePath());
        }
    }


    @Override
    public Map<String, AuthorProofFileVo> readFileFromFolder(String pathValue, String stepName) {
        Map<String, AuthorProofFileVo> fileLocationMap = new HashMap<>();
        if (enableUnitouchIntegration) {
            String pathUrl = pathValue;
            String folderPath = pathUrl.replace(unitouchCdnUrl, unitouchPath);
            int idx = folderPath.lastIndexOf(separator);
            String sourceFolderName = folderPath.substring(0, idx);
            if (stepName.trim().equalsIgnoreCase("Proofing")) {
                sourceFolderName = sourceFolderName + separator + "Author_Proofing_Uniprr";
                pathUrl = sourceFolderName;
            } else if (stepName.trim().equalsIgnoreCase("Checking")) {
                sourceFolderName = sourceFolderName + separator + "Author_Checking_Uniprr";
                pathUrl = sourceFolderName;
            }
            if (AppConstants.ENVIRONMENT_PROD.equalsIgnoreCase(environmentType)) {
                try {
                    ChannelSftp channelSftp = SftpUtil.createSFTPChannel(sftpIp, sftpPort, sftpUser, sftpPassword);
                    if (channelSftp != null) {
                        Vector<?> filelist = null;

                        try {
                            log.info("sourceFolderName ---->" + sourceFolderName);
                            if (channelSftp.lstat(sourceFolderName) != null) {
                                filelist = channelSftp.ls(sourceFolderName);
                            } else {
                                filelist = channelSftp.ls(folderPath);
                                pathUrl = folderPath;
                            }
                        } catch (Exception e) {
                            log.info("folderPath ---->" + folderPath);
                            filelist = channelSftp.ls(folderPath);
                            pathUrl = folderPath;
                        }

                        for (int i = 0; i < filelist.size(); i++) {
                            AuthorProofFileVo authorProofFile = new AuthorProofFileVo();
                            LsEntry entry = (LsEntry) filelist.get(i);
                            if (!entry.getFilename().equals(".") && !entry.getFilename().equals("..")&& !entry.getFilename().toLowerCase().contains("_ref.pdf")) {
                                authorProofFile.setFileName(entry.getFilename());
                                pathUrl = pathUrl.replace(unitouchPath, unitouchCdnUrl);
                                authorProofFile.setFilePath(pathUrl.concat("/" + entry.getFilename()));
                                authorProofFile.setFileCreatedDate(entry.getAttrs().getMtimeString());
                                log.info("Created Date ******************  :::: " + entry.getAttrs().getMtimeString());
                                fileLocationMap.put(entry.getFilename(), authorProofFile);
                            }
                        }
                        SftpUtil.closeSftpConnection(channelSftp);
                    }
                } catch (SftpException ex) {
                    log.error("Exception while getting file list from sftp: " + ex.getMessage(), ex);
                }
            } else {
                String pathName = folderPath;
                if (new File(sourceFolderName).exists()) {
                    pathName = sourceFolderName;
                }
                try (Stream<Path> path = Files.walk(Paths.get(pathName))) {
                    String pathVal = pathName.replace(unitouchPath, unitouchCdnUrl);
                    log.info("path value ---->" + pathVal);
//                    fileLocationMap = path.filter(Files::isRegularFile).collect(Collectors.toMap(p -> p.getFileName().toString(), p -> new AuthorProofFileVo(p.getFileName().toString(), pathVal.concat("/" + p.getFileName().toString()), new Date(p.toFile().lastModified()).toString())));
                    fileLocationMap = path
                            .filter(Files::isRegularFile)
                            .filter(p -> !p.getFileName().toString().toLowerCase().contains("_ref.pdf"))
                            .collect(Collectors.toMap(
                                p -> p.getFileName().toString(),
                                p -> new AuthorProofFileVo(
                                    p.getFileName().toString(),
                                    pathVal + "/" + p.getFileName().toString(),
                                    new Date(p.toFile().lastModified()).toString()
                                )
                            ));
                } catch (IOException e) {
                    log.error("Exception while getting file list : " + e.getMessage());
                }
            }
        }

        return fileLocationMap;
    }
    
    @Override
    public ResponseEntity<Resource> downloadProofFile(String filePath) {

        log.info("==================================================");
        log.info("Inside downloadProofFile()");
        log.info("Requested File Path : {}", filePath);

        ChannelSftp channelSftp = null;

        try {

            if (AppConstants.ENVIRONMENT_PROD.equalsIgnoreCase(environmentType)) {

                log.info("Environment : PROD");

                channelSftp = SftpUtil.createSFTPChannel(
                        sftpIp,
                        sftpPort,
                        sftpUser,
                        sftpPassword);

                if (channelSftp == null) {

                    log.error("Unable to establish SFTP connection.");

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build();
                }

                String sftpFilePath = filePath.replace(unitouchCdnUrl, unitouchPath);

                log.info("SFTP File Path : {}", sftpFilePath);

                InputStream inputStream = channelSftp.get(sftpFilePath);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[8192];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();

                byte[] fileBytes = outputStream.toByteArray();

                ByteArrayResource resource = new ByteArrayResource(fileBytes);

                String fileName = new File(sftpFilePath).getName();

                log.info("Downloading File : {}", fileName);
                log.info("File Size : {} Bytes", fileBytes.length);

                return ResponseEntity.ok()
                        .contentLength(fileBytes.length)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileName + "\"")
                        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                                HttpHeaders.CONTENT_DISPOSITION)
                        .header(HttpHeaders.CACHE_CONTROL,
                                "no-cache, no-store, must-revalidate")
                        .header(HttpHeaders.PRAGMA,
                                "no-cache")
                        .header(HttpHeaders.EXPIRES,
                                "0")
                        .body(resource);

            } else {

                log.info("Environment : LOCAL");

                String actualPath = filePath.replace(unitouchCdnUrl, unitouchPath);

                File file = new File(actualPath);

                log.info("Actual File Path : {}", file.getAbsolutePath());

                if (!file.exists()) {

                    log.error("File not found : {}", actualPath);

                    return ResponseEntity.notFound().build();
                }

                InputStreamResource resource =
                        new InputStreamResource(new FileInputStream(file));

                log.info("Downloading File : {}", file.getName());
                log.info("File Size : {} Bytes", file.length());

                return ResponseEntity.ok()
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + file.getName() + "\"")
                        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                                HttpHeaders.CONTENT_DISPOSITION)
                        .header(HttpHeaders.CACHE_CONTROL,
                                "no-cache, no-store, must-revalidate")
                        .header(HttpHeaders.PRAGMA,
                                "no-cache")
                        .header(HttpHeaders.EXPIRES,
                                "0")
                        .body(resource);
            }

        } catch (Exception ex) {

            log.error("Error while downloading proof file.", ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

        } finally {

            if (channelSftp != null) {

                try {

                    SftpUtil.closeSftpConnection(channelSftp);

                    log.info("SFTP Connection Closed Successfully.");

                } catch (Exception ex) {

                    log.error("Error while closing SFTP connection.", ex);
                }
            }
        }
    }
    
    public ResponseEntity<Resource> downloadTechnicalFile(String filePath) {

        log.info("=======================================");
        log.info("Inside downloadTechnicalFile()");
        log.info("File Path : {}", filePath);
        
        String actualFilePath = filePath.replace(baseUrl, uniprrFolderPath);

        File file = new File(actualFilePath);

        log.info("Actual Path : {}", file.getAbsolutePath());

        if (!file.exists()) {

            log.error("File not found : {}", filePath);

            return ResponseEntity.notFound().build();
        }

        try {

            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getName() + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                            HttpHeaders.CONTENT_DISPOSITION)
                    .header(HttpHeaders.CACHE_CONTROL,
                            "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA,
                            "no-cache")
                    .header(HttpHeaders.EXPIRES,
                            "0")
                    .body(resource);

        } catch (Exception ex) {

            log.error("Download Error", ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
//    public List<ManuscriptFileVO> getMainDocumentByIdVersion(IDVO id) {
//        List<ManuscriptFileVO> manuscriptFileVOList = new ArrayList();
//        List<ManuScriptFile> mainDocumentList = manuScriptFileRepo.getMainDocumentOfManuscriptFileWithversionIthenticateLatestFile(id.getManuscriptId(), id.getManuscriptVersion());
//        if (mainDocumentList != null && !mainDocumentList.isEmpty()) {
//            for (ManuScriptFile manuScriptFile : mainDocumentList) {
//                PlagiarismCheck plagiarismCheck = plagiarismCheckRepo.getReportByManuscript(manuScriptFile.getManuScriptId(), manuScriptFile.getFileVersion(), manuScriptFile.getFileNumber());
//                ManuscriptFileVO manuscriptFileVO = new ManuscriptFileVO();
//                manuscriptFileVO.setManuScriptId(manuScriptFile.getManuScriptId());
//                manuscriptFileVO.setManuscriptVersion(manuScriptFile.getFileVersion());
//                manuscriptFileVO.setFileName(manuScriptFile.getFileName());
//                manuscriptFileVO.setFileType(manuScriptFile.getFileType());
//                manuscriptFileVO.setFileNumber(manuScriptFile.getFileNumber());
//                manuscriptFileVO.setUpdatedOn(manuScriptFile.getUpdatedOn());
//                if (plagiarismCheck != null) {
//                    manuscriptFileVO.setParlagiarismIsActive(plagiarismCheck.getIsActive());
//                    manuscriptFileVO.setParlagiarismURL(plagiarismCheck.getUrl());
//                } else {
//                    manuscriptFileVO.setParlagiarismIsActive(3);
//                    manuscriptFileVO.setParlagiarismURL("");
//                }
//                manuscriptFileVOList.add(manuscriptFileVO);
//            }
//        }
//        return manuscriptFileVOList;
//    }
    
    public List<ManuscriptFileVO> getMainDocumentByIdVersion(IDVO id) {
        log.info("Entering getMainDocumentByIdVersion with ManuscriptId: {}, ManuscriptVersion: {}", id.getManuscriptId(), id.getManuscriptVersion());
        
        List<ManuscriptFileVO> manuscriptFileVOList = new ArrayList<>();
        List<ManuScriptFile> mainDocumentList = manuScriptFileRepo.getMainDocumentOfManuscriptFileWithversionIthenticateLatestFile(id.getManuscriptId(), id.getManuscriptVersion());
        
        log.info("Fetched {} main documents from repository", (mainDocumentList != null ? mainDocumentList.size() : 0));

        if (mainDocumentList != null && !mainDocumentList.isEmpty()) {
            for (ManuScriptFile manuScriptFile : mainDocumentList) {
                log.info("Processing ManuscriptFile: Id={}, Version={}, FileNumber={}, FileName={}",
                        manuScriptFile.getManuScriptId(),
                        manuScriptFile.getFileVersion(),
                        manuScriptFile.getFileNumber(),
                        manuScriptFile.getFileName());

                PlagiarismCheck plagiarismCheck = plagiarismCheckRepo.getReportByManuscript(
                        manuScriptFile.getManuScriptId(),
                        manuScriptFile.getFileVersion(),
                        manuScriptFile.getFileNumber());

                ManuscriptFileVO manuscriptFileVO = new ManuscriptFileVO();
                manuscriptFileVO.setManuScriptId(manuScriptFile.getManuScriptId());
                manuscriptFileVO.setManuscriptVersion(manuScriptFile.getFileVersion());
                manuscriptFileVO.setFileName(manuScriptFile.getFileName());
                manuscriptFileVO.setFileType(manuScriptFile.getFileType());
                manuscriptFileVO.setFileNumber(manuScriptFile.getFileNumber());
                manuscriptFileVO.setUpdatedOn(manuScriptFile.getUpdatedOn());

                if (plagiarismCheck != null) {
                    log.info("Plagiarism report found for ManuscriptId: {} | isActive: {}, URL: {}", 
                            manuScriptFile.getManuScriptId(), 
                            plagiarismCheck.getIsActive(), 
                            plagiarismCheck.getUrl());
                    manuscriptFileVO.setParlagiarismIsActive(plagiarismCheck.getIsActive());
                    manuscriptFileVO.setParlagiarismURL(plagiarismCheck.getUrl());
                } else {
                    log.info("No plagiarism report found for ManuscriptId: {}, FileNumber: {}", 
                            manuScriptFile.getManuScriptId(), 
                            manuScriptFile.getFileNumber());
                    manuscriptFileVO.setParlagiarismIsActive(3);  // Default/fallback value
                    manuscriptFileVO.setParlagiarismURL("");
                }

                manuscriptFileVOList.add(manuscriptFileVO);
            }
        } else {
            log.info("No main documents found for ManuscriptId: {}, Version: {}", id.getManuscriptId(), id.getManuscriptVersion());
        }

        log.info("Returning {} ManuscriptFileVO objects", manuscriptFileVOList.size());
        return manuscriptFileVOList;
    }


    @Override
    public int yearlyPcount(Integer journalId, Integer year) {
        Integer yearlyPcount = plagiarismCheckRepo.getYearlyPcount(journalId, year);
        return yearlyPcount;
    }

    @Override
    public int monthlyPcount(Integer journalId, Integer month) {

        Integer monthlyPcount = plagiarismCheckRepo.getMonthlyPcount(journalId, month);
        return monthlyPcount;
    }

    @Override
    public List<ManuscriptCommentVO> getCommentsByManuscriptId(Integer manuscriptId) {
        List<Tuple> manu = manuscriptCommentRepo.getListOfCommentsByManuscriptId(manuscriptId);

        List<ManuscriptCommentVO> listVo = new ArrayList<>();

        if (manu != null) {
            listVo = manu.stream().map(t -> new ManuscriptCommentVO(t.get(0, BigInteger.class).longValue(), t.get(1, Integer.class), t.get(2, String.class),
                            t.get(3, Integer.class), t.get(4, String.class), t.get(5, String.class), t.get(6, Date.class), t.get(7, String.class)))
                    .collect(Collectors.toList());
            listVo.forEach(manuscriptCommentVO -> manuscriptCommentVO.setCreatedOn(DateUtil.addHours(manuscriptCommentVO.getCreatedOn())));
        }
        return listVo;
    }
    
    @Override
    public ResponseEntity<Resource> downloadFile(Integer manuscriptFileId,
                                                 HttpServletRequest request) {

        log.info("=======================================================");
        log.info("Inside downloadFile()");
        log.info("Manuscript File Id : {}", manuscriptFileId);

        ManuScriptFile manuscriptFile =
                manuScriptFileRepo.downloadFileByManuScriptFileID(manuscriptFileId);

        if (manuscriptFile == null) {

            log.error("File not found.");

            return ResponseEntity.notFound().build();
        }

        String actualFilePath =
                manuscriptFile.getFilePath().replace(baseUrl, uniprrFolderPath);

        File file = new File(actualFilePath);

        log.info("Actual Path : {}", file.getAbsolutePath());

        if (!file.exists() || file.length() == 0) {

            log.warn("Original file missing. Trying restore...");

            boolean restored =
                    restoreFromBackup(file, manuscriptFile.getFileName());

            if (!restored) {

                log.error("Restore failed.");

                return ResponseEntity.notFound().build();
            }
        }

        try {

            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(file));

            log.info("File Name : {}", manuscriptFile.getFileName());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())

                    // IMPORTANT
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + manuscriptFile.getFileName() + "\"")

                    // VERY IMPORTANT FOR ANGULAR
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                            HttpHeaders.CONTENT_DISPOSITION)

                    .header(HttpHeaders.CACHE_CONTROL,
                            "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA,
                            "no-cache")
                    .header(HttpHeaders.EXPIRES,
                            "0")
                    .body(resource);

        } catch (Exception ex) {

            log.error("Download Error", ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    

    @Override
    public ResponseEntity<Resource> previewFile(Integer manuscriptFileId,HttpServletRequest request) {

        log.info("Inside previewFile()");

        ManuScriptFile manuscriptFile =
                manuScriptFileRepo.downloadFileByManuScriptFileID(manuscriptFileId);

        if(manuscriptFile == null) {
            return ResponseEntity.notFound().build();
        }

        String actualFilePath =
                manuscriptFile.getFilePath()
                .replace(baseUrl, uniprrFolderPath);

        File file = new File(actualFilePath);

        if(!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {

            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(file));


            String fileName = manuscriptFile.getFileName().toLowerCase();


            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;


            if(fileName.endsWith(".pdf")) {
                mediaType = MediaType.APPLICATION_PDF;
            }
            else if(fileName.endsWith(".doc")) {
                mediaType = MediaType.parseMediaType("application/msword");
            }
            else if(fileName.endsWith(".docx")) {
                mediaType = MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                );
            }
            else if(fileName.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            }
            else if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            }


            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(file.length())
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + manuscriptFile.getFileName() + "\""
                    )
                    .header(
                        HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        HttpHeaders.CONTENT_DISPOSITION
                    )
                    .cacheControl(CacheControl.noCache())
                    .body(resource);


        } catch(Exception e) {

            log.error("Preview file error",e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Restore file from latest backup.
     */
    private boolean restoreFromBackup(File originalFile,
                                      String realFileName) {

        log.info("Searching backup for {}", realFileName);

        File parentDir = originalFile.getParentFile();

        File backupDir = new File(parentDir, "Backup");

        if (!backupDir.exists()) {

            log.error("Backup folder not found.");

            return false;
        }

        File[] backups =
                backupDir.listFiles((dir, name) ->
                        name.startsWith(realFileName + "_"));

        if (backups == null || backups.length == 0) {

            log.error("No backup found.");

            return false;
        }

        Arrays.sort(backups,
                Comparator.comparingLong(File::lastModified).reversed());

        File latest = backups[0];

        try {

            Files.copy(latest.toPath(),
                    originalFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            log.info("Restored from {}", latest.getName());

            return true;

        } catch (Exception e) {

            log.error("Restore failed.", e);

            return false;
        }

    }
}
