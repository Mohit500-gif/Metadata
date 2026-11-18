package com.digi.uniprr.serviceImpl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.BundleTrackerManuscriptVo;
import com.digi.uniprr.VO.CommentarySubmissionVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ResubmissionManuscriptFileVo;
import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.service.AuthorBundalingService;
import com.digi.uniprr.service.HttpService;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.BundlingUtils;
import com.digi.uniprr.utils.FileCheck;
import com.digi.uniprr.utils.FileUploadValidation;
//import com.digi.uniprr.utils.FileUploadValidator;
import com.digi.uniprr.utils.FileUtil;
import com.digi.uniprr.utils.HttpServiceRequestInfo;
import com.digi.uniprr.utils.TestMergeDocx;
import com.google.common.net.HttpHeaders;

import static com.digi.uniprr.utils.AppConstants.*;

@Service
public class AuthorBundalingServiceImpl implements AuthorBundalingService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptRepo manuscriptRepo;

	@Autowired
	private JournalsRepo journalsRepo;

	@Autowired
	private ManuScriptFileRepo manuScriptFileRepo;

	@Autowired
	private ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;

	@Autowired
	private Environment environment;

	@Autowired
	private UserJournalRepo userJournalRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ManscriptScoreRepo manscriptScoreRepo;

	@Autowired
	private ManuscriptFilePreviewRepo manuscriptFilePreviewRepo;

	@Autowired
	private ManuscriptVisualizationRepo manuscriptVisualizationRepo;

	@Autowired
	private CountriesRepo countriesRepo;

	@Autowired
	private BundleTrackerRepo bundleTrackerRepo;

	@Autowired
	private HttpService httpService;

	@Autowired
	ManuscriptEditorCommentRepo manuscriptEditorCommentRepo;

	@Value("${enable.java.bundling}")
	private boolean enableJavaBundling;

	@Value("${enable.python.bundling}")
	private boolean enablePythonBundling;

	@Value("${bundling.api.url}")
	private String bundlingAPIUrl;

	@Value("${python.bundling.api.url}")
	private String pythonBundlingAPIUrl;

	@Value("${uniprrFolderPath}")
	private String uploadFolder;

	@Value("${server.status}")
	private String serverENV;

	@Value("${awsFileIp}")
	private String serverIP;

	@Value("${server.status.port}")
	private String serverPort;

	@Value("${uniprrFolderPath}")
	private String uniprrFolderPath;

	@Value("${uniprrFolderPath}")
	private String cdnUrl;
	
    @Value("${baseUrl}")
    private String baseUrl;

	@Override
	@Transactional
	public String callMetadataAPi(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
		long hitTime = 0;
		String validationMessage = "";
		try {

			ManuScript manu = manuscriptRepo.getById(resubmissionManuscriptFileVo.getManuScriptId());
			ManuScriptFile mainDocument = manuScriptFileRepo.getManuScriptFile(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion(), 1);

			if (mainDocument != null && mainDocument.getFileName() != null
					&& !(mainDocument.getFileName().isEmpty()) && mainDocument.getFileType() != null
					&& !(mainDocument.getFileType().isEmpty())) {

				String manuscriptFolderPath = BundlingUtils.generateManuscriptFolderPath(resubmissionManuscriptFileVo, manu.getJournalId(), uniprrFolderPath);
				File file = new File(manuscriptFolderPath);
				Integer wordCount = 0;
				Integer pageCount = 0;
				TestMergeDocx addTestMergeDocx = new TestMergeDocx(null);
				pageCount = addTestMergeDocx.pageCount(file, cdnUrl, uniprrFolderPath);
				wordCount = addTestMergeDocx.wordCount(file, cdnUrl, uniprrFolderPath);
				log.info("Word Count From Test Merge Docx----->>>>" + wordCount);

				Integer pageNumber = wordCount;
				String pythonAPIAWS = environment.getProperty("python.bundaling.api.aws");
				String pythonAPI = environment.getProperty("python.bundaling.api");
				String staticServerPort = environment.getProperty("static.server.port.config");
				String url = BundlingUtils.prepareApiUrl(resubmissionManuscriptFileVo, manu, pageNumber, serverENV, serverIP, serverPort, pythonAPIAWS, pythonAPI, staticServerPort);
				log.debug(url);

				hitTime = System.currentTimeMillis();
				log.info("Metadata Python API Call Time:" + hitTime + "For " + " Manuscript Id: "
						+ mainDocument.getManuScriptId() + "," + " Manuscript Version: "
						+ mainDocument.getManuScript().getManuscriptVersion());

				if (resubmissionManuscriptFileVo.getSubmissionType() != null
						&& !resubmissionManuscriptFileVo.getSubmissionType().isEmpty()
						&& resubmissionManuscriptFileVo.getSubmissionType().equalsIgnoreCase("NEW-SUBMISSION")) {

					// Python api
					RestTemplate restTemplate = new RestTemplate();
					JSONObject obj = restTemplate.getForObject(url, JSONObject.class);
					log.info("Metadata python api response :----------------->>>>>>>>" + obj);
					if (obj != null && !obj.isEmpty()) {
						log.info("Got Response from .net API" + obj.toString());
						// JSONParser jsonParser = new JSONParser();
						// JSONObject obj = (JSONObject) jsonParser.parse(result);
						String manuscriptType = (String) obj.get(MANUSCRIPT_TYPE);
						String manuscriptTitle = (String) obj.get(MANUSCRIPT_TITLE);
						String manuscriptRunningHead = (String) obj.get(MANUSCRIPT_RUNNING_HEAD);
						String manuscriptAbstract = (String) obj.get(MANUSCRIPT_ABSTRACT);
						String manuscriptFigures = (String) obj.get(MANUSCRIPT_FIGURES);
						String manuscriptBlackWhiteFigures = (String) obj.get(MANUSCRIPT_BLACK_WHITE_FIGURES);
						String manuscriptColorFigures = (String) obj.get(MANUSCRIPT_COLOR_FIGURES);
						String manuscriptTables = (String) obj.get(MANUSCRIPT_TABLES);
						// String manuscriptWords = (String) obj.get(MANUSCRIPT_WORDS);
						// String manuscriptPages = (String) obj.get(MANUSCRIPT_PAGES);
						String manuscriptParagraph = (String) obj.get(MANUSCRIPT_PARAGRAPH);
						String manuscriptIntroduction = (String) obj.get(MANUSCRIPT_INTRODUCTION);
						String manuscriptAcknowledgment = (String) obj.get(MANUSCRIPT_ACKNOWLEDGMENT);
						String manuscriptReferences = (String) obj.get(MANUSCRIPT_REFERENCES);
						String manuscriptShortTitle = (String) obj.get(MANUSCRIPT_SHORT_TITLE);
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
						Double parseTreeHeight = (Double) obj.get(parse_tree_height);
						Double sentenceCoherence = (Double) obj.get(sentence_coherence);
						Double visualWordsRatio = (Double) obj.get(visual_words_ratio);
						String manuscriptVisualizationData = (String) obj.get(MANUSCRIPT_VISUALIZATION);

						// Manuscript Visualization
						ManuscriptVisualization manuscriptVisualization = manuscriptVisualizationRepo.getManuscriptVisualizationDetails(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion(), mainDocument.getManuScript().getJournalId());
						// Update Manuscript Visualization
						if (manuscriptVisualization != null) {
							log.info("Update Manuscript Visualization");
							manuscriptVisualization.setUpdatedBy(mainDocument.getManuScript().getAuthorId());
							manuscriptVisualization.setUpdatedOn(new Date());
							manuscriptVisualization.setVisualization(manuscriptVisualizationData);
							manuscriptVisualizationRepo.save(manuscriptVisualization);
						} else {
							// Create Manuscript Visualization log.info("Create Manuscript Visualization");
							ManuscriptVisualization addManuscriptVisualization = new ManuscriptVisualization();
							addManuscriptVisualization.setManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
							addManuscriptVisualization.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							addManuscriptVisualization.setJournalId(mainDocument.getManuScript().getJournalId());
							addManuscriptVisualization.setCreatedBy(mainDocument.getManuScript().getAuthorId());
							addManuscriptVisualization.setVisualization(manuscriptVisualizationData);
							manuscriptVisualizationRepo.save(addManuscriptVisualization);
						}

						ManscriptScore manuscriptScore = manscriptScoreRepo.getManscriptScoreData(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
						// Update in manuscriptScore Table
						if (manuscriptScore != null) {
							log.info("Update in manuscriptScore Table");
							manuscriptScore.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptScore.setAvgSyllablesCount(avgSyllablesCount);
							manuscriptScore.setAvgWordLength(avgWordLength);
							manuscriptScore.setDifficultWordsRatio(difficultWordsRatio);
							manuscriptScore.setFleschKincaidGrade(fleschKincaidGrade);
							manuscriptScore.setFleschReadingEase(fleschReadingEase);
							manuscriptScore.setGunningFogIndex(gunningFogIndex);
							manuscriptScore.setPosRatio(posRatio);
							manuscriptScore.setSentenceLength(sentenceLength);
							manuscriptScore.setSentenceNo(sentenceNo);
							manuscriptScore.setTypeToTokenRatio(typeToTokenRatio);
							manuscriptScore.setParseTreeHeight(parseTreeHeight);
							manuscriptScore.setSentenceCoherence(sentenceCoherence);
							manuscriptScore.setVisualWordsRatio(visualWordsRatio);
							manscriptScoreRepo.save(manuscriptScore);
						} else {
							// Create new manuscriptScore
							log.info("Create new manuscriptScore");
							ManscriptScore manuscriptScoreNew = new ManscriptScore();
							manuscriptScoreNew.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
							manuscriptScoreNew.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptScoreNew.setAvgSyllablesCount(avgSyllablesCount);
							manuscriptScoreNew.setAvgWordLength(avgWordLength);
							manuscriptScoreNew.setDifficultWordsRatio(difficultWordsRatio);
							manuscriptScoreNew.setFleschKincaidGrade(fleschKincaidGrade);
							manuscriptScoreNew.setFleschReadingEase(fleschReadingEase);
							manuscriptScoreNew.setGunningFogIndex(gunningFogIndex);
							manuscriptScoreNew.setPosRatio(posRatio);
							manuscriptScoreNew.setSentenceLength(sentenceLength);
							manuscriptScoreNew.setSentenceNo(sentenceNo);
							manuscriptScoreNew.setTypeToTokenRatio(typeToTokenRatio);
							manuscriptScoreNew.setParseTreeHeight(parseTreeHeight);
							manuscriptScoreNew.setSentenceCoherence(sentenceCoherence);
							manuscriptScoreNew.setVisualWordsRatio(visualWordsRatio);
							manscriptScoreRepo.save(manuscriptScoreNew);
						}
						ManuScript manuscript = manuscriptRepo.getOne(resubmissionManuscriptFileVo.getManuScriptId());
						// Update in manuscript Table
						if (manuscript != null) {
							log.info("Update in manuscript Table");
							manuscript.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscript.setManuscriptTitle(manuscriptTitle);
							manuscript.setManuscriptShortTitle(manuscriptShortTitle);
							manuscript.setManuscriptRunningHead(manuscriptRunningHead);
							manuscript.setManuscriptAbstract(manuscriptAbstract);
							manuscript.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptRepo.save(manuscript);
						}

						ManuscriptTypeDetails manuscriptTypeDetails = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
						// Update in manuscriptTypeDetails
						if (manuscriptTypeDetails != null) {
							log.info("Update in manuscriptTypeDetails");

							manuscriptTypeDetails.setManuscriptFigures(manuscriptFigures);
							manuscriptTypeDetails.setManuscriptBlackWhiteFigures(manuscriptBlackWhiteFigures);
							manuscriptTypeDetails.setManuscriptColorFigures(manuscriptColorFigures);
							manuscriptTypeDetails.setManuscriptTables(manuscriptTables);
							if (wordCount != null)
								manuscriptTypeDetails.setManuscriptWords(Integer.toString(wordCount));
							if (pageCount != null)
								manuscriptTypeDetails.setManuscriptPages(Integer.toString(pageCount));
							manuscriptTypeDetails.setManuscriptParagraph(manuscriptParagraph);
							manuscriptTypeDetails.setManuscriptIntroduction(manuscriptIntroduction);
							manuscriptTypeDetails.setManuscriptAcknowledgment(manuscriptAcknowledgment);
							manuscriptTypeDetails.setManuscriptReferences(manuscriptReferences);
							manuscriptTypeDetails.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptTypeDetailsRepo.save(manuscriptTypeDetails);
						} else {
							// Create in manuscriptTypeDetails
							log.info("Create in manuscriptTypeDetails");
							ManuscriptTypeDetails manuscriptTypeDetailsCreate = new ManuscriptTypeDetails();
							manuscriptTypeDetailsCreate.setManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
							manuscriptTypeDetailsCreate.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptTypeDetailsCreate.setManuscriptType(manuscriptType);
							manuscriptTypeDetailsCreate.setManuscriptFigures(manuscriptFigures);
							manuscriptTypeDetailsCreate.setManuscriptBlackWhiteFigures(manuscriptBlackWhiteFigures);
							manuscriptTypeDetailsCreate.setManuscriptColorFigures(manuscriptColorFigures);
							manuscriptTypeDetailsCreate.setManuscriptTables(manuscriptTables);
							if (wordCount != null)
								manuscriptTypeDetailsCreate.setManuscriptWords(Integer.toString(wordCount));
							if (pageCount != null)
								manuscriptTypeDetailsCreate.setManuscriptPages(Integer.toString(pageCount));
							manuscriptTypeDetailsCreate.setManuscriptParagraph(manuscriptParagraph);
							manuscriptTypeDetailsCreate.setManuscriptIntroduction(manuscriptIntroduction);
							manuscriptTypeDetailsCreate.setManuscriptAcknowledgment(manuscriptAcknowledgment);
							manuscriptTypeDetailsCreate.setManuscriptReferences(manuscriptReferences);
							manuscriptTypeDetailsCreate.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptTypeDetailsRepo.save(manuscriptTypeDetailsCreate);
						}
					} else {
						return "Unable to extract manuscript metadata. You can fill data manually.";
					}

				} else if (resubmissionManuscriptFileVo.getSubmissionType() != null
						&& !resubmissionManuscriptFileVo.getSubmissionType().isEmpty()
						&& resubmissionManuscriptFileVo.getSubmissionType().equalsIgnoreCase("RE-SUBMISSION")) {
					// Manuscript Visualization
					ManuscriptVisualization manuscriptVisualization = manuscriptVisualizationRepo.getManuscriptVisualizationDetails(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion()
							- 1, mainDocument.getManuScript().getJournalId());
					// Update Manuscript Visualization
					if (manuscriptVisualization != null) {
						// Create Manuscript Visualization log.info("Create Manuscript Visualization");
						ManuscriptVisualization addManuscriptVisualization = new ManuscriptVisualization();
						addManuscriptVisualization.setManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
						addManuscriptVisualization.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						addManuscriptVisualization.setJournalId(mainDocument.getManuScript().getJournalId());
						addManuscriptVisualization.setCreatedBy(mainDocument.getManuScript().getAuthorId());
						addManuscriptVisualization.setVisualization(manuscriptVisualization.getVisualization());
						manuscriptVisualizationRepo.save(addManuscriptVisualization);
					}

					ManscriptScore manuscriptScore = manscriptScoreRepo.getManscriptScoreData(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion()
							- 1);
					// Update in manuscriptScore Table
					if (manuscriptScore != null) {
						// Create new manuscriptScore
						log.info("Create new manuscriptScore");
						ManscriptScore manuscriptScoreNew = new ManscriptScore();
						manuscriptScoreNew.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
						manuscriptScoreNew.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						manuscriptScoreNew.setAvgSyllablesCount(manuscriptScore.getAvgSyllablesCount());
						manuscriptScoreNew.setAvgWordLength(manuscriptScore.getAvgWordLength());
						manuscriptScoreNew.setDifficultWordsRatio(manuscriptScore.getDifficultWordsRatio());
						manuscriptScoreNew.setFleschKincaidGrade(manuscriptScore.getFleschKincaidGrade());
						manuscriptScoreNew.setFleschReadingEase(manuscriptScore.getFleschReadingEase());
						manuscriptScoreNew.setGunningFogIndex(manuscriptScore.getGunningFogIndex());
						manuscriptScoreNew.setPosRatio(manuscriptScore.getPosRatio());
						manuscriptScoreNew.setSentenceLength(manuscriptScore.getSentenceLength());
						manuscriptScoreNew.setSentenceNo(manuscriptScore.getSentenceNo());
						manuscriptScoreNew.setTypeToTokenRatio(manuscriptScore.getTypeToTokenRatio());
						manuscriptScoreNew.setParseTreeHeight(manuscriptScore.getParseTreeHeight());
						manuscriptScoreNew.setSentenceCoherence(manuscriptScore.getSentenceCoherence());
						manuscriptScoreNew.setVisualWordsRatio(manuscriptScore.getVisualWordsRatio());
						manscriptScoreRepo.save(manuscriptScoreNew);
					}
					ManuScript manuscript = manuscriptRepo.getOne(resubmissionManuscriptFileVo.getManuScriptId());
					// Update in manuscript Table
					if (manuscript != null) {
						log.info("Update in manuscript Table");
						manuscript.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						manuscriptRepo.save(manuscript);
					}

					ManuscriptTypeDetails manuscriptTypeDetails = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
					// Update in manuscriptTypeDetails
					if (manuscriptTypeDetails != null) {
						log.info("Update in manuscriptTypeDetails");
						manuscriptTypeDetails.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						manuscriptTypeDetailsRepo.save(manuscriptTypeDetails);
					}
				}

			}

		} catch (Exception ex) {
			validationMessage = "Failed";
			log.info("Error while extracting metadata using python API : " + ex);
		}

		if (mainDocumentFileNameValidator("Manuscript", resubmissionManuscriptFileVo)
				&& firstPageFileNameValidator("FirstPage", resubmissionManuscriptFileVo))
			return "Success";
		else {
			// manuScriptFileRepo.deleteTypeEmpltyFile(resubmissionManuscriptFileVo.getManuScriptId(),
			// resubmissionManuscriptFileVo.getManuscriptVersion());
			validationMessage = "Not able to generate metadata";
			log.error(validationMessage);
			saveBundleTrackerDetails(resubmissionManuscriptFileVo.getManuScriptId(), AppConstants.FAILED , validationMessage);

			return validationMessage;
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Resource> downloadManuscript(IDVO idvo) {

	    log.info("=========================================");
	    log.info("Inside downloadManuscript()");
	    log.info("Manuscript Id      : {}", idvo.getManuscriptId());
	    log.info("Version            : {}", idvo.getManuscriptVersion());
	    log.info("File Type          : {}", idvo.getFileType());

	    String filePath = null;

	    if (idvo.getFileType() != null
	            && !idvo.getFileType().isEmpty()
	            && idvo.getManuscriptId() != null
	            && idvo.getManuscriptVersion() != null) {

	        List<ManuScriptFile> getmanuScriptFile =
	                manuScriptFileRepo.getManuScriptFileDataPDfList(
	                        idvo.getManuscriptId(),
	                        idvo.getManuscriptVersion(),
	                        idvo.getFileType());

	        if (getmanuScriptFile != null && !getmanuScriptFile.isEmpty()) {
	            filePath = getmanuScriptFile.get(0).getFilePath();
	        } else {
	            log.warn("There is no file for file type : {}", idvo.getFileType());
	            return ResponseEntity.notFound().build();
	        }

	    } else if (idvo.getFileType() != null
	            && !idvo.getFileType().isEmpty()
	            && idvo.getManuscriptId() != null
	            && idvo.getManuscriptVersion() == null) {

	        filePath = manuScriptFileRepo.getManuScriptFilePath(
	                idvo.getManuscriptId(),
	                idvo.getFileType());

	        if (filePath == null || filePath.isEmpty()) {
	            log.warn("There is no file for file type : {}", idvo.getFileType());
	            return ResponseEntity.notFound().build();
	        }

	    } else {

	        log.warn("Invalid request.");

	        return ResponseEntity.badRequest().build();
	    }

	    String actualFilePath = filePath.replace(baseUrl, uniprrFolderPath);

	    File file = new File(actualFilePath);

	    log.info("Actual File Path : {}", file.getAbsolutePath());

	    if (!file.exists()) {

	        log.error("File not found : {}", actualFilePath);

	        return ResponseEntity.notFound().build();
	    }

	    try {

	        InputStreamResource	 resource =
	                new InputStreamResource(new FileInputStream(file));

	        log.info("Downloading File : {}", file.getName());

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .contentLength(file.length())
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "attachment; filename=\"" + file.getName() + "\"")
	                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
	                        HttpHeaders.CONTENT_DISPOSITION)
	                .header(HttpHeaders.CACHE_CONTROL,
	                        "no-cache, no-store, must-revalidate")
	                .header(HttpHeaders.PRAGMA, "no-cache")
	                .header(HttpHeaders.EXPIRES, "0")
	                .body(resource);

	    } catch (Exception ex) {

	        log.error("Error while downloading file.", ex);

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	@Override
	public String updateFile(Integer manuscriptFileId, String fileStatus, MultipartFile file, HttpServletRequest request,boolean isUploaded) {
		log.info("-------------------Inside in updateFile------------------");
	    File serverFile = null;
	    File tempFile = null;
	    File tempDirectory = null;
		try {
			ManuScriptFile manuscriptFile = manuScriptFileRepo.getById(manuscriptFileId);
			String oldFileName = manuscriptFile.getFileName();
			log.info("oldFileName   :  "+ oldFileName);
			if (manuscriptFile != null) {
				log.info("-------------------Get Manuscript File Data-----------------");
				int journalId = manuscriptFile.getManuScript().getJournalId();
				int manuscriptId = manuscriptFile.getManuScriptId();
				String basePath = uploadFolder + File.separator;
				
		        tempDirectory = new File(basePath + "TempUploadFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R" + manuscriptFile.getFileVersion() + "_" + journalId + "_" + manuscriptId);
		        if (!tempDirectory.exists()) {
		            tempDirectory.mkdirs();
		        }
		        
		        // Step 1: Write the new file to a temporary directory
		        tempFile = new File(tempDirectory.getAbsolutePath() + File.separator + file.getOriginalFilename());
		        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tempFile))) {
		            stream.write(file.getBytes());
		            log.info("Successfully wrote file to temp directory: " + tempFile.getAbsolutePath());
		        }catch (IOException ioException) {
	                log.error("Failed to write file to temp directory: " + tempFile.getAbsolutePath(), ioException);
	                return "There is no file to update";
	            }


				// Step 2: Validate the new file type before proceeding
				Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
				boolean isValidFile = FileUploadValidation.validateFile(tempFile, expectedMimeTypes);
				if (!isValidFile) {
					log.error("Uploaded file validation failed");
					log.info("Deleting invalid file: " + tempFile.delete());
					return "There is no file to update";
				}

				// Step 3: Move old file to PreviousFilefolder and update details in DB
				String movedOldFileName = BundlingUtils.moveRenameOldFile(manuscriptFile, journalId, basePath);
				log.info("movedOldFileName  :  "+movedOldFileName);
				ManuScriptFile oldManuscriptFile = new ManuScriptFile();
				oldManuscriptFile.setManuScriptId(manuscriptId);
				oldManuscriptFile.setFileName(manuscriptFile.getFileName());
				oldManuscriptFile.setFileSize(manuscriptFile.getFileSize());
				oldManuscriptFile.setFileType(manuscriptFile.getFileType());
				oldManuscriptFile.setFileStatus(manuscriptFile.getFileStatus() + "-" + "oldFile");
				oldManuscriptFile.setFileVersion(manuscriptFile.getFileVersion());
//				if(manuscriptFile.getFileType().equalsIgnoreCase("Main Documents/Article File")) {
//					int fileNumber = manuscriptFile.getFileNumber()+1;
//					oldManuscriptFile.setFileNumber(fileNumber);
//				}else {
//					oldManuscriptFile.setFileNumber(manuscriptFile.getFileNumber());
//				}
				oldManuscriptFile.setFileNumber(manuscriptFile.getFileNumber());
				oldManuscriptFile.setIsActive(0);
				oldManuscriptFile.setFileActive(0);
				oldManuscriptFile.setLegend(manuscriptFile.getLegend());
				String movedFileFolderName = "PreviousFileFolder" + File.separator + movedOldFileName;
				String oldFileServerPath = BundlingUtils.generateFilePath(manuscriptFile.getFileVersion(), journalId, manuscriptId, basePath, movedFileFolderName, serverENV, serverIP, serverPort);
				oldManuscriptFile.setFilePath(oldFileServerPath);
				log.info("Saving old file details to database with path: " + oldFileServerPath);
				manuScriptFileRepo.save(oldManuscriptFile);
				
		        // Step 4: Move validated temp file to actual upload location
		        File uploadDirectory = new File(basePath + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R" + manuscriptFile.getFileVersion() + "_" + journalId + "_" + manuscriptId);
		        if (!uploadDirectory.exists()) {
		            uploadDirectory.mkdirs();
		        }

		        serverFile = new File(uploadDirectory.getAbsolutePath() + File.separator + file.getOriginalFilename());
		        if (serverFile.exists()) {
		            serverFile.delete(); // Delete only after the new file is fully validated
		        }
		        tempFile.renameTo(serverFile);

		        // Step 5: Update manuscriptFile with the new file details
				double fileSizeinBytes = file.getSize();
				double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
				manuscriptFile.setFileSize((int) fileSizeInKB);
				manuscriptFile.setFileName(file.getOriginalFilename());
				String fileServerPath = BundlingUtils.generateFilePath(manuscriptFile.getFileVersion(), journalId, manuscriptId, basePath, file.getOriginalFilename(), serverENV, serverIP, serverPort);
				manuscriptFile.setFilePath(fileServerPath);
				manuscriptFile.setFileStatus(fileStatus);
				log.info("fileStatus : " + fileStatus);

				if(manuscriptFile.getFileType().equalsIgnoreCase("Main Documents/Article File")) {
					String extn = FilenameUtils.getExtension(manuscriptFile.getFilePath());
					if (extn.equalsIgnoreCase("doc")) {
						manuscriptFile = BundlingUtils.prepareManuscriptDocument(manuscriptFile, "Manuscript.doc", uploadFolder, serverENV, serverIP, serverPort);
					} else {
						manuscriptFile = BundlingUtils.prepareManuscriptDocument(manuscriptFile, "Manuscript.docx", uploadFolder, serverENV, serverIP, serverPort);
					}
				}
				if(manuscriptFile.getFileType().equalsIgnoreCase("First Page file/Cover Letter")) {
					String extn = FilenameUtils.getExtension(manuscriptFile.getFilePath());
					if (extn.equalsIgnoreCase("doc")) {
						manuscriptFile = BundlingUtils.prepareManuscriptDocument(manuscriptFile, "FirstPage.doc", uploadFolder, serverENV, serverIP, serverPort);
					} else {
						manuscriptFile = BundlingUtils.prepareManuscriptDocument(manuscriptFile, "FirstPage.docx", uploadFolder, serverENV, serverIP, serverPort);
					}
				}

				if(manuscriptFile.getFileType().equalsIgnoreCase("Main Documents/Article File")) {
					int fileNumber = manuscriptFile.getFileNumber()+1;
					manuscriptFile.setFileNumber(fileNumber);
				}
				if(manuscriptFile.getFileType().equalsIgnoreCase("Figures")) {
					figuretextlegendUpdate(oldFileName, movedOldFileName, manuscriptId,manuscriptFile.getFileVersion(), journalId);
					log.info("Legend succesfully updated");
				}

				manuscriptFile.setCreatedOn(new Date());
				manuscriptFile.setUpdatedOn(new Date());
				manuScriptFileRepo.save(manuscriptFile);

				log.info("New File Updated Successfully------------------");
				return "successfully updated";
			}
		} catch (Exception e) {
			isUploaded = false;
			log.error("Exception while uploading file : " + e.getMessage(), e);
		}finally {
	        // Cleanup: Delete temporary files and directories after completion
	        if (tempFile != null && tempFile.exists()) {
	            boolean tempFileDeleted = tempFile.delete();
	            log.info("Temp file deletion status: " + tempFileDeleted);
	        }
	        if (tempDirectory != null && tempDirectory.exists()) {
	            boolean tempDirDeleted = tempDirectory.delete();
	            log.info("Temp directory deletion status: " + tempDirDeleted);
	        }
		}

		log.info("There is no file to update------------------");
		isUploaded = false;
		return "There is no file to update";
	}


	/**
	 * Not in use
	 */
	@Deprecated
	public String updateFile_old(Integer manuscriptFileId, String fileStatus, MultipartFile file, HttpServletRequest request) {
		log.info("-------------------Inside in updateFile------------------");
		BufferedOutputStream stream = null;
		try {
			// Step 1. Get manuscript File Data By Id
			ManuScriptFile getFileDetails = manuScriptFileRepo.getOne(manuscriptFileId);
			if (getFileDetails != null) {
				log.info("-------------------Get Manuscript File Data-----------------");
				String filezipMakingdir = uploadFolder + File.separator;
				log.info("filezipMakingdir  Location :" + filezipMakingdir);
				String fileuploaddirServer = null;
				// Step 3. Server Location
				if (serverENV.equalsIgnoreCase("AWS")) {
					fileuploaddirServer = "https://" + serverIP;
					log.info("fileuploaddirServer  Location  For AWS :---------------->>>>>>>" + fileuploaddirServer);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					fileuploaddirServer = "https://" + InetAddress.getLocalHost().getHostAddress() + ":"
							+ request.getServerPort();
					log.info("fileuploaddirServer  Location  For LOCAL :--------------->>>>>>>>>" + fileuploaddirServer);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					fileuploaddirServer = "https://" + serverIP + ":"
							+ serverPort;
					log.info("fileuploaddirServer  Location  For AWS DEMO :---------------->>>>>>>"
							+ fileuploaddirServer);
				}
				// Step 4. File Drive Location
				String drivePath = filezipMakingdir + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ getFileDetails.getFileVersion() + "_" + getFileDetails.getManuScript().getJournalId() + "_"
						+ getFileDetails.getManuScriptId() + File.separator + getFileDetails.getFileName();
				// Step 4.1. Now Change the name and replace it with random number
				Random rand = new Random();
				// Generate random integers in range 0 to 999
				int i = rand.nextInt(1000000000);
				log.info("-------------------Random number------------------------->>>>>>>>>>" + i);
				String fileName = getFileDetails.getFileName();
				String basename = FilenameUtils.getBaseName(fileName);
				String extension = FilenameUtils.getExtension(fileName);
				log.info("-------------------Base File Name------------------------->>>>>>>>>>" + basename);
				log.info("-------------------Extension File Name------------------------->>>>>>>>>>" + extension);
				String finalNameFile = basename + i + "." + extension;
				log.info("-------------------Final Name Of File------------------------->>>>>>>>>>" + finalNameFile);
				// Step 4.2. Now Replace file name
				File rename = new File(drivePath);
				if (rename.exists()) {
					File name = new File(rename.getParent() + File.separator + finalNameFile);
					if (rename.renameTo(name)) {
						log.info("-------------------Re NAme File Success ------------------------->>>>>>>>>>");
					} else {
						log.info("-------------------Re NAme File Failure ------------------------->>>>>>>>>>");
					}
				} else {
					File name = new File(rename.getParent() + File.separator + finalNameFile);
					name.createNewFile();
				}
				// Step 5. Setup For the file folder in the same Manuscript Folder
				String attachmentFolderPath = filezipMakingdir + "UniprrFileFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + getFileDetails.getFileVersion() + "_"
						+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
						+ File.separator + "PreviousFileFolder";
				log.info("-------------------Previous File Folder Location------------------>" + attachmentFolderPath);
				// Step 6. Create Folder In Respective Drive
				File dir = new File(attachmentFolderPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// Step 7. Now Start Moving File from current folder into New Created folder
				FileUtils.moveFileToDirectory(new File(rename.getParent() + File.separator + finalNameFile), new File(attachmentFolderPath), true);
				log.info("-------------------File Moved Successfully in Previous File Folder Location-------------------------");
				// Step 7.1 Now Enter the previous file details in db as status oldFile And
				// Version
				ManuScriptFile a = new ManuScriptFile();
				a.setManuScriptId(getFileDetails.getManuScriptId());
				a.setFileName(getFileDetails.getFileName());
				a.setFileSize(getFileDetails.getFileSize());
				a.setFileType(getFileDetails.getFileType());
				a.setFileStatus(getFileDetails.getFileStatus() + "-" + "oldFile");
				a.setFileVersion(getFileDetails.getFileVersion());
				a.setFileNumber(getFileDetails.getFileNumber());
				a.setIsActive(0);
				a.setLegend(getFileDetails.getLegend());
				InetAddress inetAddress = InetAddress.getLocalHost();
				Integer port = request.getServerPort();
				String oldFileServerPath = null;
				// Step 7.2 Now Create path for the server to download and paste in the column
				if (serverENV.equalsIgnoreCase("AWS")) {
					String Ip = "https://" + serverIP;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for AWS : " + oldFileServerPath);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for LOCAL : " + oldFileServerPath);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					String Ip = "https://" + serverIP + ":"
							+ serverPort;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for AWS DEMO: " + oldFileServerPath);
				}
				a.setFilePath(oldFileServerPath);
				manuScriptFileRepo.save(a);
				// Step 8. Upload file in that folder which we have to replace
				String uploadFileFolder = filezipMakingdir + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
						+ "R" + getFileDetails.getFileVersion() + "_" + getFileDetails.getManuScript().getJournalId()
						+ "_" + getFileDetails.getManuScriptId();
				File dir1 = new File(uploadFileFolder);
				// Step 9. Create folder in the drive
				if (!dir1.exists()) {
					dir1.mkdirs();
				}
				byte[] bytes = file.getBytes();
				File serverFile = new File(dir1.getAbsolutePath() + File.separator + file.getOriginalFilename());
				// Step 10. Now Write file in the respective drive
				stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				log.info("-------------------New File Replace in the place of previous file in same Folder Location------------------");
				double fileSizeinBytes = file.getSize();
				double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
				String fileServerPath = null;

				// Step 11. Now Create path for the server to download and paste in the column
				if (serverENV.equalsIgnoreCase("AWS")) {
					String Ip = "https://" + serverIP;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for AWS : " + fileServerPath);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for LOCAL : " + fileServerPath);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					String Ip = "https://" + serverIP + ":"
							+ serverPort;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for AWS DEMO: " + fileServerPath);
				}
				// Step 12. Update Manuscript File for the manuscript file id
				getFileDetails.setFileName(file.getOriginalFilename());
				getFileDetails.setFilePath(fileServerPath);
				getFileDetails.setFileSize((int) fileSizeInKB);
				getFileDetails.setFileStatus(fileStatus);
				ManuScriptFile getFile = manuScriptFileRepo.save(getFileDetails);

				if(getFile.getFileType().equalsIgnoreCase("Main Documents/Article File")) {

					log.info("STEP 5 -> Start changing name of main document");
					String serverfilePath = getFile.getFilePath();
					String driveFilePath = serverfilePath.replace(fileuploaddirServer, filezipMakingdir);
					// String[] array = driveFilePath.split("\\.");
					// int len = array.length;
					// String extension = array[len - 1];
					String fileName1 = driveFilePath;
					String basename1 = FilenameUtils.getBaseName(fileName1);
					String extension1 = FilenameUtils.getExtension(fileName1);
					if (extension1.equalsIgnoreCase("doc")) {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "Manuscript.doc");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("Manuscript.doc");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "Manuscript.doc");
							if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
								String Ip = "https://" + environment.getProperty("awsFileIp");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "Manuscript.doc";
								getFile.setFilePath(fileServerPath1);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
										+ environment.getProperty("server.status.port");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "Manuscript.doc";
								getFile.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a Manuscript.doc");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has Manuscript.doc");
						}
					} else {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "Manuscript.docx");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("Manuscript.docx");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "Manuscript.docx");
							if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
								String Ip = "https://" + environment.getProperty("awsFileIp");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "Manuscript.docx";
								getFile.setFilePath(fileServerPath1);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
										+ environment.getProperty("server.status.port");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "Manuscript.docx";
								getFile.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a Manuscript.docx");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has Manuscript.dox");
						}
					}


				}

				if(getFile.getFileType().equalsIgnoreCase("First Page file/Cover Letter")) {

					log.info("STEP 5 -> Start changing name of main document");
					String serverfilePath = getFile.getFilePath();
					String driveFilePath = serverfilePath.replace(fileuploaddirServer, filezipMakingdir);
					String fileName1 = driveFilePath;
					String basename1 = FilenameUtils.getBaseName(fileName1);
					String extension1 = FilenameUtils.getExtension(fileName1);
					if (extension1.equalsIgnoreCase("doc")) {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "FirstPage.doc");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("FirstPage.doc");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "FirstPage.doc");
							if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
								String Ip = "https://" + environment.getProperty("awsFileIp");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "FirstPage.doc";
								getFile.setFilePath(fileServerPath1);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
										+ environment.getProperty("server.status.port");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "FirstPage.doc";
								getFile.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a FirstPage.doc");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has FirstPage.doc");
						}
					} else {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "FirstPage.docx");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("FirstPage.docx");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "FirstPage.docx");
							if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
								String Ip = "https://" + environment.getProperty("awsFileIp");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "FirstPage.docx";
								getFile.setFilePath(fileServerPath1);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
							} else if (environment.getProperty("server.status").equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + environment.getProperty("awsFileIp") + ":"
										+ environment.getProperty("server.status.port");
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "FirstPage.docx";
								getFile.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a FirstPage.docx");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has FirstPage.dox");
						}
					}


				}


				log.info("-------------------New File Updated Successfully------------------");
				return "successfully updated";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("-------------------Exception due to code ------------------>" + e);
		} finally {
			try {
				stream.close();
				log.info("-------------------File Close------------------");
			} catch (IOException e) {
				e.printStackTrace();
				log.info("-------------------Exception due to file ------------------>" + e);
			}
		}
		log.info("-------------------There is no file to update------------------");
		return "There is no file to update";
	}


	@Override
	@Transactional
	public JSONObject submitCommentarySubmission(CommentarySubmissionVo commentarySubmissionVo, HttpServletRequest request) {
		BufferedOutputStream stream = null;
		BufferedOutputStream stream1 = null;
		JSONObject obj = new JSONObject();
		String manuscriptCode = null;
		Integer manuscriptId = null;
		try {
			if (commentarySubmissionVo.getJournalId() != null && commentarySubmissionVo.getCommentaryUserId() != null) {
				log.info("-------------------Commentary Submission Start ------------------>");
				ManuScript manuScriptModel = new ManuScript();
				manuScriptModel.setManuscriptStatusId(2);
				manuScriptModel.setManuscriptVersion(0);
				manuScriptModel.setJournalId(commentarySubmissionVo.getJournalId());
				manuScriptModel.setIsActive(1);
				manuScriptModel.setAuthorId(commentarySubmissionVo.getCommentaryUserId());
				manuScriptModel.setAssignedUserRole(2);
				manuScriptModel.setCreatedBy(commentarySubmissionVo.getCommentaryUserId());
				User u = userRepo.getOne(commentarySubmissionVo.getCommentaryUserId());
				if (u != null && u.getCountryId() != null) {
					Countries c = countriesRepo.getOne(u.getCountryId());
					if (c != null) {
						manuScriptModel.setManuscriptOrigin(c.getName());
					}
				}
				ManuScript getManuScriptData = manuscriptRepo.save(manuScriptModel);
				if (getManuScriptData != null) {
					log.info("------------------STEP---1------------------>");
					manuscriptId = getManuScriptData.getManuScriptId();
					log.info("-------------------Manuscript ID has been created ------------------>");
					getManuScriptData.setManuscriptAbstract(commentarySubmissionVo.getManuscriptAbstract());
					getManuScriptData.setManuscriptTitle(commentarySubmissionVo.getManuscriptTtile());
					getManuScriptData.setManuscriptVersion(commentarySubmissionVo.getManuscriptVersion());
					if (commentarySubmissionVo.getManuscriptStatusId().equals(2)) {
						getManuScriptData.setSubmissionDate(new Date());
					}
					if (commentarySubmissionVo.getSubmissionStatus() != null
							&& !commentarySubmissionVo.getSubmissionStatus().isEmpty()
							&& commentarySubmissionVo.getSubmissionStatus().equalsIgnoreCase("NEW-SUBMISSION")) {
						List<UserJournal> i = userJournalRepo.getUserRoleJournalId(getManuScriptData.getJournalId(), 2);
						if (i != null && !i.isEmpty()) {
							getManuScriptData.setAssignedUser(i.get(0).getUserId());
						}
						if (commentarySubmissionVo.getManuscriptChecklistResponse() != null
								&& !commentarySubmissionVo.getManuscriptChecklistResponse().isEmpty()) {
							getManuScriptData.setManuscriptChecklistResponse(commentarySubmissionVo.getManuscriptChecklistResponse());
						}
					}
					if (getManuScriptData.getManuscriptCode() == null
							|| getManuScriptData.getManuscriptCode().isEmpty()) {
						// Journal journal = journalsRepo.getOne(vo.getJournalId());
						Date date = new Date();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						Journal journal = journalsRepo.getOne(commentarySubmissionVo.getJournalId());
						if (journal != null) {
							if (journal.getJournalManuscriptCount() != null
									&& journal.getJournalManuscriptYear() != null) {
								Integer currentYear = calendar.get(Calendar.YEAR) % 100;
								Integer updatedJournalManuscriptCount = journal.getJournalManuscriptCount() + 1;
								if (!journal.getJournalManuscriptYear().equals(currentYear)) {
									Integer updatedYear = currentYear;
									Integer updatedJournalManuscriptCountNew = 1;
									getManuScriptData.setManuscriptCode(journal.getJournalCode() + "_"
											+ updatedJournalManuscriptCountNew + "_" + updatedYear);
									manuscriptCode = getManuScriptData.getManuscriptCode();
									if (journal.getJournalMetadata().getJournalDOI() != null
											&& !journal.getJournalMetadata().getJournalDOI().isEmpty()) {
										getManuScriptData.setDoi(journal.getJournalMetadata().getJournalDOI() + journal.getJournalCode()
												+ "." + getManuScriptData.getManuscriptCode());
									}

									journal.setJournalManuscriptCount(updatedJournalManuscriptCountNew);
									journal.setJournalManuscriptYear(updatedYear);
									journalsRepo.save(journal);
								} else {
									getManuScriptData.setManuscriptCode(journal.getJournalCode() + "_"
											+ updatedJournalManuscriptCount + "_" + currentYear);
									manuscriptCode = getManuScriptData.getManuscriptCode();
									if (journal.getJournalMetadata().getJournalDOI() != null
											&& !journal.getJournalMetadata().getJournalDOI().isEmpty()) {
										getManuScriptData.setDoi(journal.getJournalMetadata().getJournalDOI() + journal.getJournalCode()
												+ "." + getManuScriptData.getManuscriptCode());
									}
									journal.setJournalManuscriptCount(updatedJournalManuscriptCount);
									journal.setJournalManuscriptYear(currentYear);
									journalsRepo.save(journal);
								}

							}
						}
					}
					getManuScriptData.setLinkManuscriptCode(commentarySubmissionVo.getReferneceMansucriptCode());
					manuscriptRepo.save(getManuScriptData);
					log.info("-------------------Manuscript Code Has Been created------------------>");
					ManuscriptTypeDetails manuscriptTypeDetails = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(getManuScriptData.getManuScriptId());
					// Update in manuscriptTypeDetails
					if (manuscriptTypeDetails != null) {
						log.info("Update in manuscriptTypeDetails");
						manuscriptTypeDetails.setManuscriptVersion(commentarySubmissionVo.getManuscriptVersion());
						manuscriptTypeDetails.setManuscriptType(commentarySubmissionVo.getManuscriptType());
						manuscriptTypeDetailsRepo.save(manuscriptTypeDetails);
					} else {
						// Create in manuscriptTypeDetails
						log.info("Create in manuscriptTypeDetails");
						ManuscriptTypeDetails manuscriptTypeDetailsCreate = new ManuscriptTypeDetails();
						manuscriptTypeDetailsCreate.setManuscriptId(getManuScriptData.getManuScriptId());
						manuscriptTypeDetailsCreate.setManuscriptVersion(commentarySubmissionVo.getManuscriptVersion());
						manuscriptTypeDetailsCreate.setManuscriptType(commentarySubmissionVo.getManuscriptType());
						manuscriptTypeDetailsRepo.save(manuscriptTypeDetailsCreate);
					}

					if (commentarySubmissionVo.getFirstPage() != null
							&& !commentarySubmissionVo.getFirstPage().isEmpty()) {
						log.info("-------------------STEP---2------------------>");
						log.info("-------------------Inside in First Page File Upload------------------>");

						String attachmentFolderPath = uploadFolder + File.separator + "UniprrFileFolder"
								+ File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ commentarySubmissionVo.getManuscriptVersion() + "_"
								+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId();
						log.info("Folder Path Create : " + attachmentFolderPath);
						File dir = new File(attachmentFolderPath);
						if (!dir.exists()) {
							dir.mkdirs();
						}

						byte[] bytes = commentarySubmissionVo.getFirstPage().getBytes();
						File serverFile = new File(dir.getAbsolutePath() + File.separator
								+ commentarySubmissionVo.getFirstPage().getOriginalFilename());
						stream = new BufferedOutputStream(new FileOutputStream(serverFile));
						stream.write(bytes);
						double fileSizeinBytes = commentarySubmissionVo.getFirstPage().getSize();
						double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
						String fileServerPath = null;
						Integer port = request.getServerPort();
						InetAddress inetAddress = InetAddress.getLocalHost();
						if (serverENV.equalsIgnoreCase("AWS")) {
							String Ip = "https://" + serverIP;
							fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + commentarySubmissionVo.getManuscriptVersion() + "_"
									+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId()
									+ File.separator + commentarySubmissionVo.getFirstPage().getOriginalFilename();
							log.info("Server Path for AWS : " + fileServerPath);
						} else if (serverENV.equalsIgnoreCase("LOCAL")) {
							String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
							fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + commentarySubmissionVo.getManuscriptVersion() + "_"
									+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId()
									+ File.separator + commentarySubmissionVo.getFirstPage().getOriginalFilename();
							log.info("Server Path for LOCAL : " + fileServerPath);
						} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + serverIP + ":"
									+ serverPort;
							fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + commentarySubmissionVo.getManuscriptVersion() + "_"
									+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId()
									+ File.separator + commentarySubmissionVo.getFirstPage().getOriginalFilename();
							log.info("Server Path for LOCAL : " + fileServerPath);
						}

						ManuScriptFile manuScriptFile = new ManuScriptFile();
						manuScriptFile.setFileName(commentarySubmissionVo.getFirstPage().getOriginalFilename());
						manuScriptFile.setFilePath(fileServerPath);
						manuScriptFile.setFileSize((int) fileSizeInKB);
						manuScriptFile.setFileType("First Page file/Cover Letter");
						manuScriptFile.setFileStatus("New Submission");
						manuScriptFile.setManuScriptId(getManuScriptData.getManuScriptId());
						manuScriptFile.setFileVersion(commentarySubmissionVo.getManuscriptVersion());
						manuScriptFile.setFileStatus(commentarySubmissionVo.getFileStatus());
						manuScriptFile.setFileActive(1);
						manuScriptFile.setIsActive(1);
						manuScriptFileRepo.save(manuScriptFile);
						log.info("-------------------First page file updated successfully------------------>");
					}

					if (commentarySubmissionVo.getManuscriptFile() != null
							&& !commentarySubmissionVo.getManuscriptFile().isEmpty()) {
						log.info("-------------------STEP---3--------------------->");
						log.info("-------------------Inside in  manuscript file------------------>");
						String attachmentFolderPath = uploadFolder + File.separator + "UniprrFileFolder"
								+ File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ commentarySubmissionVo.getManuscriptVersion() + "_"
								+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId();
						log.info("Folder Path Create : " + attachmentFolderPath);
						File dir = new File(attachmentFolderPath);
						if (!dir.exists()) {
							dir.mkdirs();
						}

						byte[] bytes = commentarySubmissionVo.getManuscriptFile().getBytes();
						File serverFile = new File(dir.getAbsolutePath() + File.separator
								+ commentarySubmissionVo.getManuscriptFile().getOriginalFilename());
						stream1 = new BufferedOutputStream(new FileOutputStream(serverFile));
						stream1.write(bytes);
						double fileSizeinBytes = commentarySubmissionVo.getManuscriptFile().getSize();
						double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
						String fileServerPath = null;
						Integer port = request.getServerPort();
						InetAddress inetAddress = InetAddress.getLocalHost();
						if (serverENV.equalsIgnoreCase("AWS")) {
							String Ip = "https://" + serverIP;
							fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + commentarySubmissionVo.getManuscriptVersion() + "_"
									+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId()
									+ File.separator + commentarySubmissionVo.getManuscriptFile().getOriginalFilename();
							log.info("Server Path for AWS : " + fileServerPath);
						} else if (serverENV.equalsIgnoreCase("LOCAL")) {
							String ip = "https://" + inetAddress.getHostAddress() + ":" + port;
							fileServerPath = ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + commentarySubmissionVo.getManuscriptVersion() + "_"
									+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId()
									+ File.separator + commentarySubmissionVo.getManuscriptFile().getOriginalFilename();
							log.info("Server Path for LOCAL : " + fileServerPath);
						} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + serverIP + ":"
									+ serverPort;
							fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + commentarySubmissionVo.getManuscriptVersion() + "_"
									+ commentarySubmissionVo.getJournalId() + "_" + getManuScriptData.getManuScriptId()
									+ File.separator + commentarySubmissionVo.getManuscriptFile().getOriginalFilename();
							log.info("Server Path for LOCAL : " + fileServerPath);
						}

						ManuScriptFile ManuScriptFile = new ManuScriptFile();
						ManuScriptFile.setFileName(commentarySubmissionVo.getManuscriptFile().getOriginalFilename());
						ManuScriptFile.setFilePath(fileServerPath);
						ManuScriptFile.setFileSize((int) fileSizeInKB);
						ManuScriptFile.setFileType("Main Documents/Article File");
						ManuScriptFile.setFileNumber(1);
						ManuScriptFile.setFileStatus("New Submission");
						ManuScriptFile.setManuScriptId(getManuScriptData.getManuScriptId());
						ManuScriptFile.setFileVersion(commentarySubmissionVo.getManuscriptVersion());
						ManuScriptFile.setFileStatus(commentarySubmissionVo.getFileStatus());
						ManuScriptFile.setFileNumber(1);
						ManuScriptFile.setFileActive(1);
						ManuScriptFile.setIsActive(1);
						manuScriptFileRepo.save(ManuScriptFile);
						log.info("-------------------Manuscript File updated successfully------------------>");
					}

					// Bundaled file
					String filezipMakingdir = uploadFolder + File.separator;

					log.info("filezipMakingdir  Location :" + filezipMakingdir);

					String fileuploaddirServer = BundlingUtils.generateServerFilePathPrefix(serverENV, serverIP, serverPort);

					log.info("-------------------STEP--4------------------>");

					// Change the name of man document if the file name is different
					ManuScriptFile manusCriptDocument = manuScriptFileRepo.getManuScriptFile(getManuScriptData.getManuScriptId(), getManuScriptData.getManuscriptVersion(), 1);
					if (manusCriptDocument != null) {
						log.info("-------------------Change main document name------------------>");
						String serverfilePath = manusCriptDocument.getFilePath();
						String driveFilePath = serverfilePath.replace(fileuploaddirServer, filezipMakingdir);
						// String[] array = driveFilePath.split("\\.");
						// int len = array.length;
						// String extension = array[len - 1];
						String fileName = driveFilePath;
						String basename = FilenameUtils.getBaseName(fileName);
						String extension = FilenameUtils.getExtension(fileName);
						if (extension.equalsIgnoreCase("doc")) {
							File rename = new File(driveFilePath);
							File name = new File(rename.getParent() + File.separator + "Manuscript.doc");
							if (rename.renameTo(name)) {
								manusCriptDocument.setFileName("Manuscript.doc");
								String dataTemp[] = serverfilePath.split("\\\\");
								int length = dataTemp.length;
								String dummy = dataTemp[length - 1].toString();
								serverfilePath = serverfilePath.replace(dummy, "Manuscript.doc");
								if (serverENV.equalsIgnoreCase("AWS")) {
									String Ip = "https://" + serverIP;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "Manuscript.doc";
									manusCriptDocument.setFilePath(fileServerPath);
								} else if (serverENV.equalsIgnoreCase("LOCAL")) {
									manusCriptDocument.setFilePath(serverfilePath);
								} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
									String Ip = "https://" + serverIP + ":"
											+ serverPort;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "Manuscript.doc";
									manusCriptDocument.setFilePath(fileServerPath);
								}
								log.info("Document Name Changed As a Manuscript.doc");
								manuScriptFileRepo.save(manusCriptDocument);
							}
						} else {
							File rename = new File(driveFilePath);
							File name = new File(rename.getParent() + File.separator + "Manuscript.docx");
							if (rename.renameTo(name)) {
								manusCriptDocument.setFileName("Manuscript.docx");
								String dataTemp[] = serverfilePath.split("\\\\");
								int length = dataTemp.length;
								String dummy = dataTemp[length - 1].toString();
								serverfilePath = serverfilePath.replace(dummy, "Manuscript.docx");
								if (serverENV.equalsIgnoreCase("AWS")) {
									String Ip = "https://" + serverIP;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "Manuscript.docx";
									manusCriptDocument.setFilePath(fileServerPath);
								} else if (serverENV.equalsIgnoreCase("LOCAL")) {
									manusCriptDocument.setFilePath(serverfilePath);
								} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
									String Ip = "https://" + serverIP + ":"
											+ serverPort;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "Manuscript.docx";
									manusCriptDocument.setFilePath(fileServerPath);
								}
								log.info("Document Name Changed As a Manuscript.docx");
								manuScriptFileRepo.save(manusCriptDocument);
							}
						}
					}
					log.info("-------------------STEP---5------------------>");
					// Change The Name OF First page
					ManuScriptFile manuScriptFirstPage = manuScriptFileRepo.getManuScriptFirstPageFile(getManuScriptData.getManuScriptId(), getManuScriptData.getManuscriptVersion(), 1);
					if (manuScriptFirstPage != null) {
						log.info("-------------------Change first page name------------------>");
						String manuScriptFirstPageserverfilePath = manuScriptFirstPage.getFilePath();
						String manuScriptFirstPagedriveFilePath = manuScriptFirstPageserverfilePath.replace(fileuploaddirServer, filezipMakingdir);
						// String[] array = manuScriptFirstPagedriveFilePath.split("\\.");
						// int len = array.length;
						// String extension = array[len - 1];
						String fileName = manuScriptFirstPagedriveFilePath;
						String basename = FilenameUtils.getBaseName(fileName);
						String extension = FilenameUtils.getExtension(fileName);
						if (extension.equalsIgnoreCase("doc")) {
							File rename = new File(manuScriptFirstPagedriveFilePath);
							File name = new File(rename.getParent() + File.separator + "FirstPage.doc");
							if (rename.renameTo(name)) {
								manuScriptFirstPage.setFileName("FirstPage.doc");
								String dataTemp[] = manuScriptFirstPageserverfilePath.split("\\\\");
								int length = dataTemp.length;
								String dummy = dataTemp[length - 1].toString();
								manuScriptFirstPageserverfilePath = manuScriptFirstPageserverfilePath.replace(dummy, "FirstPage.doc");
								if (serverENV.equalsIgnoreCase("AWS")) {
									String Ip = "https://" + serverIP;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "FirstPage.doc";
									manuScriptFirstPage.setFilePath(fileServerPath);
								} else if (serverENV.equalsIgnoreCase("LOCAL")) {
									manuScriptFirstPage.setFilePath(manuScriptFirstPageserverfilePath);
								} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
									String Ip = "https://" + serverIP + ":"
											+ serverPort;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "FirstPage.doc";
									manuScriptFirstPage.setFilePath(fileServerPath);
								}
								log.info("Document Name Changed As a FirstPage.doc");
								manuScriptFileRepo.save(manuScriptFirstPage);
							}
						} else {
							File rename = new File(manuScriptFirstPagedriveFilePath);
							File name = new File(rename.getParent() + File.separator + "FirstPage.docx");
							if (rename.renameTo(name)) {
								manuScriptFirstPage.setFileName("FirstPage.docx");
								String dataTemp[] = manuScriptFirstPageserverfilePath.split("\\\\");
								int length = dataTemp.length;
								String dummy = dataTemp[length - 1].toString();
								manuScriptFirstPageserverfilePath = manuScriptFirstPageserverfilePath.replace(dummy, "FirstPage.docx");
								if (serverENV.equalsIgnoreCase("AWS")) {
									String Ip = "https://" + serverIP;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "FirstPage.docx";
									manuScriptFirstPage.setFilePath(fileServerPath);
								} else if (serverENV.equalsIgnoreCase("LOCAL")) {
									manuScriptFirstPage.setFilePath(manuScriptFirstPageserverfilePath);
								} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
									String Ip = "https://" + serverIP + ":"
											+ serverPort;
									String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
											+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
											+ getManuScriptData.getJournalId() + "_"
											+ getManuScriptData.getManuScriptId() + File.separator + "FirstPage.docx";
									manuScriptFirstPage.setFilePath(fileServerPath);
								}
								log.info("Document Name Changed As a FirstPage.docx");
								manuScriptFileRepo.save(manuScriptFirstPage);
							}
						}
					}
					// Bundaling File
					String myPath = null;
					if (serverENV.equalsIgnoreCase("AWS")) {
						myPath = File.separator + File.separator + serverIP + File.separator
								+ "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId();
						log.info("TestMergeDocx  Location  For AWS :------------------->>>>>>>>>>>>>"
								+ fileuploaddirServer);
					} else if (serverENV.equalsIgnoreCase("LOCAL")) {
						myPath = File.separator + File.separator + InetAddress.getLocalHost().getHostAddress()
								+ File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId();
						log.info("TestMergeDocx  Location  For LOCAL :------------>>>>>>>>>>>>>>>>"
								+ fileuploaddirServer);
					} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
						myPath = File.separator + File.separator + File.separator + serverIP
								+ File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId();
						log.info("TestMergeDocx  Location  For LOCAL :------------>>>>>>>>>>>>>>>>"
								+ fileuploaddirServer);
					}

					log.info("-------------------STEP---6------------------>");
					File file = new File(myPath);
					log.info("-------------------Calling Text Merge dunction------------------>");
					List<ManuScriptFile> getData = manuScriptFileRepo.getDataByManuscriptIdAndVersion(getManuScriptData.getManuScriptId(), getManuScriptData.getManuscriptVersion());
					TestMergeDocx addTestMergeDocx = new TestMergeDocx(null);
					addTestMergeDocx.mergeTextFiles(file, getData, cdnUrl, uniprrFolderPath);
					Integer wordCount = 0;
					wordCount = addTestMergeDocx.wordCount(file, cdnUrl, uniprrFolderPath);
					log.info("Word Count For Test Merge Docx----->>>>" + wordCount);

					// File SEtup
					ManuscriptFilePreview manuscriptFilePreview = manuscriptFilePreviewRepo.getManuscriptFilePreviewDetails(getManuScriptData.getManuScriptId(), getManuScriptData.getManuscriptVersion());

					String xhtmlPath = null;
					String pdfPath = null;
					String pdfPathForReviewer = null;
					if (serverENV.equalsIgnoreCase("AWS")) {
						String Ip = "https://" + serverIP;
						xhtmlPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId() + File.separator + "output" + File.separator
								+ "Bundled.html";
						log.info("xhtmlPath  Location  For AWS :------------->>>>>>>>>>" + xhtmlPath);

						pdfPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId() + File.separator + "output" + File.separator
								+ "Bundled.pdf";

						pdfPathForReviewer = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
								+ getManuScriptData.getJournalId() + "_" + getManuScriptData.getManuScriptId()
								+ File.separator + "output" + File.separator + "BundledPdfForReviewer.pdf";

						log.info("PDF  Location  For AWS :----------------->>>>>>>>>>>>>>" + xhtmlPath);
					} else if (serverENV.equalsIgnoreCase("LOCAL")) {
						xhtmlPath = "\\" + "\\" + InetAddress.getLocalHost().getHostAddress() + File.separator
								+ "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId() + File.separator + "output" + File.separator
								+ "Bundled.html";
						log.info("xhtmlPath  Location  For LOCAL :----------->>>>>>>>>>>>>>>>>" + fileuploaddirServer);

						pdfPath = fileuploaddirServer + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
								+ getManuScriptData.getJournalId() + "_" + getManuScriptData.getManuScriptId()
								+ File.separator + "output" + File.separator + "Bundled.pdf";
						log.info("PDF  Location  For LOCAL :" + xhtmlPath);

						pdfPathForReviewer = fileuploaddirServer + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
								+ getManuScriptData.getJournalId() + "_" + getManuScriptData.getManuScriptId()
								+ File.separator + "output" + File.separator + "BundledPdfForReviewer.pdf";

					} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + serverIP + ":"
								+ serverPort;
						xhtmlPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId() + File.separator + "output" + File.separator
								+ "Bundled.html";
						log.info("xhtmlPath  Location  For AWS DEMO :------------->>>>>>>>>>" + xhtmlPath);

						pdfPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
								+ getManuScriptData.getManuscriptVersion() + "_" + getManuScriptData.getJournalId()
								+ "_" + getManuScriptData.getManuScriptId() + File.separator + "output" + File.separator
								+ "Bundled.pdf";

						pdfPathForReviewer = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + getManuScriptData.getManuscriptVersion() + "_"
								+ getManuScriptData.getJournalId() + "_" + getManuScriptData.getManuScriptId()
								+ File.separator + "output" + File.separator + "BundledPdfForReviewer.pdf";

						log.info("PDF  Location  For AWS DEMO :----------------->>>>>>>>>>>>>>" + xhtmlPath);
					}

					// update in ManuscriptFilePreview Table
					if (manuscriptFilePreview != null) {
						log.info("update in ManuscriptFilePreview Table");
						manuscriptFilePreview.setManuscriptXhtmlPath(xhtmlPath);
						manuscriptFilePreview.setManuscriptXmlpath(xhtmlPath);
						manuscriptFilePreviewRepo.save(manuscriptFilePreview);
					} else {
						// Create new ManuscriptFilePreview
						log.info("Create new ManuscriptFilePreview");
						ManuscriptFilePreview newManuscriptFilePreview = new ManuscriptFilePreview();
						newManuscriptFilePreview.setManuscriptId(getManuScriptData.getManuScriptId());
						newManuscriptFilePreview.setManuscriptVersion(getManuScriptData.getManuscriptVersion());
						newManuscriptFilePreview.setManuscriptXhtmlPath(xhtmlPath);
						newManuscriptFilePreview.setManuscriptXmlpath(xhtmlPath);
						manuscriptFilePreviewRepo.save(newManuscriptFilePreview);
					}

					// PDF FILE DATA SAVE IN MANUSCRIPT FILE

					ManuScriptFile manuScriptFileForPdf = manuScriptFileRepo.getManuScriptFileDataPDf(getManuScriptData.getManuScriptId(), getManuScriptData.getManuscriptVersion());
					// Update in manuScriptFile Table
					if (manuScriptFileForPdf != null) {
						log.info("Update in manuScriptFile Table");
						manuScriptFileForPdf.setFileType("PDF");
						manuScriptFileForPdf.setFileName("Bundled.pdf");
						manuScriptFileForPdf.setFilePath(pdfPath);
						manuScriptFileForPdf.setIsActive(1);
						manuScriptFileForPdf.setFileSize(20);
						manuScriptFileForPdf.setFileStatus("New Submission");
						manuScriptFileForPdf.setFileVersion(getManuScriptData.getManuscriptVersion());
						manuScriptFileRepo.save(manuScriptFileForPdf);
					} else {
						// Create in manuScriptFile Table
						log.info("Create in manuScriptFile Table");
						ManuScriptFile manuScriptFileForPdfCreate = new ManuScriptFile();
						manuScriptFileForPdfCreate.setManuScriptId(getManuScriptData.getManuScriptId());
						manuScriptFileForPdfCreate.setFileType("PDF");
						manuScriptFileForPdfCreate.setFileName("Bundled.pdf");
						manuScriptFileForPdfCreate.setFilePath(pdfPath);
						manuScriptFileForPdfCreate.setIsActive(1);
						manuScriptFileForPdfCreate.setFileSize(20);
						manuScriptFileForPdfCreate.setFileStatus("New Submission");
						manuScriptFileForPdfCreate.setFileVersion(getManuScriptData.getManuscriptVersion());
						manuScriptFileRepo.save(manuScriptFileForPdfCreate);
					}

					// PDF FILE For Reviewer DATA SAVE IN MANUSCRIPT FILE

					ManuScriptFile manuScriptFileForPdfForReviewer = manuScriptFileRepo.getManuScriptFileDataPDfForReviewer(getManuScriptData.getManuScriptId(), getManuScriptData.getManuscriptVersion());
					// Update in manuScriptFile Table
					if (manuScriptFileForPdfForReviewer != null) {
						log.info("Update in manuScriptFile Table");
						manuScriptFileForPdfForReviewer.setFileType("REVIEWERPDF");
						manuScriptFileForPdfForReviewer.setFileName("BundledPdfForReviewer.pdf");
						manuScriptFileForPdfForReviewer.setFilePath(pdfPathForReviewer);
						manuScriptFileForPdfForReviewer.setIsActive(1);
						manuScriptFileForPdfForReviewer.setFileSize(20);
						manuScriptFileForPdfForReviewer.setFileStatus("New Submission");
						manuScriptFileForPdfForReviewer.setFileVersion(getManuScriptData.getManuscriptVersion());
						manuScriptFileRepo.save(manuScriptFileForPdfForReviewer);
					} else {
						// Create in manuScriptFile Table
						log.info("Create in manuScriptFile Table");
						ManuScriptFile manuScriptFileForPdfCreate = new ManuScriptFile();
						manuScriptFileForPdfCreate.setManuScriptId(getManuScriptData.getManuScriptId());
						manuScriptFileForPdfCreate.setFileType("REVIEWERPDF");
						manuScriptFileForPdfCreate.setFileName("BundledPdfForReviewer.pdf");
						manuScriptFileForPdfCreate.setFilePath(pdfPathForReviewer);
						manuScriptFileForPdfCreate.setIsActive(1);
						manuScriptFileForPdfCreate.setFileSize(20);
						manuScriptFileForPdfCreate.setFileStatus("New Submission");
						manuScriptFileForPdfCreate.setFileVersion(getManuScriptData.getManuscriptVersion());
						manuScriptFileRepo.save(manuScriptFileForPdfCreate);
					}
					log.info("-------------------Commentary Submission has been successfully done------------------>");
					obj.put("data", "Commentary Submission has been successfully done");
					obj.put("manuscriptCode", manuscriptCode);
					obj.put("manuscriptId", manuscriptId);
					return obj;
				} else {
					log.info("-------------------Error while creating manuscript id------------------>");
					obj.put("data", "Manuscript id has not made");
					return obj;
				}
			} else {
				obj.put("data", "PLease provide journal id and commentary user id");
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("-------------------Exception due to code ------------------>" + e);
			obj.put("data", "Exception due to code");
			return obj;
		} finally {
			try {
				if (commentarySubmissionVo.getFirstPage() != null && !commentarySubmissionVo.getFirstPage().isEmpty()) {
					stream.close();
					log.info("-------------------First Page File Close------------------");
				}
				if (commentarySubmissionVo.getManuscriptFile() != null
						&& !commentarySubmissionVo.getManuscriptFile().isEmpty()) {
					stream1.close();
					log.info("-------------------Manuscript File Close------------------");
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.info("-------------------Exception due to file ------------------>" + e);
			}
		}
	}

	@Override
	@Transactional
	public String updateManuscriptFile(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
		try {
			log.info("Server Status :--------------->>>>>" + serverENV);
			// Update In Manuscript File
			if (resubmissionManuscriptFileVo.getManuscriptFileVO() != null
					&& !resubmissionManuscriptFileVo.getManuscriptFileVO().isEmpty()) {
				resubmissionManuscriptFileVo.getManuscriptFileVO().stream().forEach(i -> {
					ManuScriptFile k = manuScriptFileRepo.getOne(i.getManuscriptFileId());
					if (k != null) {
						k.setFileType(i.getFileType());
						if (i.getFileType() != null && !i.getFileType().isEmpty()
								&& i.getFileType().contentEquals("Main Documents/Article File")) {
							List<ManuScriptFile> j = manuScriptFileRepo.getMainDocumentOfManuscriptFileWithMnuscriptVerison(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
							if (j != null && !j.isEmpty()) {
								k.setFileNumber(j.size());
							} else {
								k.setFileNumber(1);
							}
						}
						k.setLegend(i.getLegend());
						k.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						manuScriptFileRepo.save(k);
					}
				});
			}

			// Get Manuscript Data
			ManuScript manu = manuscriptRepo.getOne(resubmissionManuscriptFileVo.getManuScriptId());
			// Server Setup
			// String filezipMakingdir = System.getProperty("catalina.base") +
			// File.separator + "webapps" + File.separator;
			String filezipMakingdir = uploadFolder + File.separator;

			log.info("filezipMakingdir  Location :" + filezipMakingdir);

			String fileuploaddirServer = BundlingUtils.generateServerFilePathPrefix(serverENV, serverIP, serverPort);

			// Change the name of man document if the file name is different
			ManuScriptFile manusCriptDocument = manuScriptFileRepo.getManuScriptFile(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion(), 1);
			if (manusCriptDocument != null) {
				String serverfilePath = manusCriptDocument.getFilePath();
				String driveFilePath = serverfilePath.replace(fileuploaddirServer, filezipMakingdir);
				// String[] array = driveFilePath.split("\\.");
				// int len = array.length;
				// String extension = array[len - 1];
				String fileName = driveFilePath;
				String basename = FilenameUtils.getBaseName(fileName);
				String extension = FilenameUtils.getExtension(fileName);
				if (extension.equalsIgnoreCase("doc")) {
					File rename = new File(driveFilePath);
					File name = new File(rename.getParent() + File.separator + "Manuscript.doc");
					if (rename.renameTo(name)) {
						manusCriptDocument.setFileName("Manuscript.doc");
						String dataTemp[] = serverfilePath.split("\\\\");
						int length = dataTemp.length;
						String dummy = dataTemp[length - 1].toString();
						serverfilePath = serverfilePath.replace(dummy, "Manuscript.doc");
						if (serverENV.equalsIgnoreCase("AWS")) {
							String Ip = "https://" + serverIP;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "Manuscript.doc";
							manusCriptDocument.setFilePath(fileServerPath);
						} else if (serverENV.equalsIgnoreCase("LOCAL")) {
							manusCriptDocument.setFilePath(serverfilePath);
						} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + serverIP + ":"
									+ serverPort;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "Manuscript.doc";
							manusCriptDocument.setFilePath(fileServerPath);
						}
						log.info("Document Name Changed As a Manuscript.doc");
						manuScriptFileRepo.save(manusCriptDocument);
					}
				} else {
					File rename = new File(driveFilePath);
					File name = new File(rename.getParent() + File.separator + "Manuscript.docx");
					if (rename.renameTo(name)) {
						manusCriptDocument.setFileName("Manuscript.docx");
						String dataTemp[] = serverfilePath.split("\\\\");
						int length = dataTemp.length;
						String dummy = dataTemp[length - 1].toString();
						serverfilePath = serverfilePath.replace(dummy, "Manuscript.docx");
						if (serverENV.equalsIgnoreCase("AWS")) {
							String Ip = "https://" + serverIP;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "Manuscript.docx";
							manusCriptDocument.setFilePath(fileServerPath);
						} else if (serverENV.equalsIgnoreCase("LOCAL")) {
							manusCriptDocument.setFilePath(serverfilePath);
						} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + serverIP + ":"
									+ serverPort;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "Manuscript.docx";
							manusCriptDocument.setFilePath(fileServerPath);
						}
						log.info("Document Name Changed As a Manuscript.docx");
						manuScriptFileRepo.save(manusCriptDocument);
					}
				}
			}
			// Change The Name OF First page
			ManuScriptFile manuScriptFirstPage = manuScriptFileRepo.getManuScriptFirstPageFile(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion(), 1);
			if (manuScriptFirstPage != null) {
				String manuScriptFirstPageserverfilePath = manuScriptFirstPage.getFilePath();
				String manuScriptFirstPagedriveFilePath = manuScriptFirstPageserverfilePath.replace(fileuploaddirServer, filezipMakingdir);
				// String[] array = manuScriptFirstPagedriveFilePath.split("\\.");
				// int len = array.length;
				// String extension = array[len - 1];
				String fileName = manuScriptFirstPagedriveFilePath;
				String basename = FilenameUtils.getBaseName(fileName);
				String extension = FilenameUtils.getExtension(fileName);
				if (extension.equalsIgnoreCase("doc")) {
					File rename = new File(manuScriptFirstPagedriveFilePath);
					File name = new File(rename.getParent() + File.separator + "FirstPage.doc");
					if (rename.renameTo(name)) {
						manuScriptFirstPage.setFileName("FirstPage.doc");
						String dataTemp[] = manuScriptFirstPageserverfilePath.split("\\\\");
						int length = dataTemp.length;
						String dummy = dataTemp[length - 1].toString();
						manuScriptFirstPageserverfilePath = manuScriptFirstPageserverfilePath.replace(dummy, "FirstPage.doc");
						if (serverENV.equalsIgnoreCase("AWS")) {
							String Ip = "https://" + serverIP;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "FirstPage.doc";
							manuScriptFirstPage.setFilePath(fileServerPath);
						} else if (serverENV.equalsIgnoreCase("LOCAL")) {
							manuScriptFirstPage.setFilePath(manuScriptFirstPageserverfilePath);
						} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + serverIP + ":"
									+ serverPort;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "FirstPage.doc";
							manuScriptFirstPage.setFilePath(fileServerPath);
						}
						log.info("Document Name Changed As a FirstPage.doc");
						manuScriptFileRepo.save(manuScriptFirstPage);
					}
				} else {
					File rename = new File(manuScriptFirstPagedriveFilePath);
					File name = new File(rename.getParent() + File.separator + "FirstPage.docx");
					if (rename.renameTo(name)) {
						manuScriptFirstPage.setFileName("FirstPage.docx");
						String dataTemp[] = manuScriptFirstPageserverfilePath.split("\\\\");
						int length = dataTemp.length;
						String dummy = dataTemp[length - 1].toString();
						manuScriptFirstPageserverfilePath = manuScriptFirstPageserverfilePath.replace(dummy, "FirstPage.docx");
						if (serverENV.equalsIgnoreCase("AWS")) {
							String Ip = "https://" + serverIP;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "FirstPage.docx";
							manuScriptFirstPage.setFilePath(fileServerPath);
						} else if (serverENV.equalsIgnoreCase("LOCAL")) {
							manuScriptFirstPage.setFilePath(manuScriptFirstPageserverfilePath);
						} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
							String Ip = "https://" + serverIP + ":"
									+ serverPort;
							String fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
									+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion()
									+ "_" + manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId()
									+ File.separator + "FirstPage.docx";
							manuScriptFirstPage.setFilePath(fileServerPath);
						}
						log.info("Document Name Changed As a FirstPage.docx");
						manuScriptFileRepo.save(manuScriptFirstPage);
					}
				}
			}
			return "Files updated";
		} catch (Exception e) {
			return "Exception due to code";
		}
	}

	@Override
	@Transactional
	public String bundledFile(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
		try {
			// Get Manuscript Data
			ManuScript manu = manuscriptRepo.getOne(resubmissionManuscriptFileVo.getManuScriptId());
			String filezipMakingdir = uploadFolder + File.separator;
			log.info("filezipMakingdir  Location :" + filezipMakingdir);

			String fileuploaddirServer = BundlingUtils.generateServerFilePathPrefix(serverENV, serverIP, serverPort);

			// Bundaling File
			String myPath = null;
			if (serverENV.equalsIgnoreCase("AWS")) {
				myPath = File.separator + File.separator + serverIP + File.separator
						+ "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId();
				log.info("TestMergeDocx  Location  For AWS :------------------->>>>>>>>>>>>>" + fileuploaddirServer);
			} else if (serverENV.equalsIgnoreCase("LOCAL")) {
				myPath = File.separator + File.separator + InetAddress.getLocalHost().getHostAddress() + File.separator
						+ "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId();
				log.info("TestMergeDocx  Location  For LOCAL :------------>>>>>>>>>>>>>>>>" + fileuploaddirServer);
			} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
				myPath = File.separator + File.separator + File.separator + serverIP
						+ File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId();
				log.info("TestMergeDocx  Location  For LOCAL :------------>>>>>>>>>>>>>>>>" + fileuploaddirServer);
			}

			// Before Calling Bundaling In Technical Modification Delete Folder PDFForImage
			// AND PDF
			if (resubmissionManuscriptFileVo.getSubmissionType() != null
					&& !resubmissionManuscriptFileVo.getSubmissionType().isEmpty()
					&& resubmissionManuscriptFileVo.getSubmissionType().equalsIgnoreCase("TECHNICAL-MODIFICATION")) {
				log.info("-***************---------------------Before Calling Bundaling In Technical Modification Delete Folder PDFForImage AND PDF---------------------------*************-");
				String pdfFolderLocation = uploadFolder + File.separator + "UniprrFileFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_"
						+ manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId() + File.separator
						+ "PDF";
				log.info("**********************Path For PDF Folder****************" + pdfFolderLocation);
				File dir = new File(pdfFolderLocation);
				if (dir.exists()) {
					for (File fileEntry : dir.listFiles()) {
						fileEntry.delete();
						log.info("File Deleted");
					}
//								dir.deleteOnExit();
					log.info("**********************Delete Folder PDF Successfully****************");
				}

				String PDFForImageFolderLocation = uploadFolder + File.separator + "UniprrFileFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_"
						+ manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId() + File.separator
						+ "PDFForImage";
				log.info("**********************Path For PDFForImageFolderLocation Folder****************"
						+ PDFForImageFolderLocation);
				File dir1 = new File(PDFForImageFolderLocation);
				if (dir1.exists()) {
					for (File fileEntry : dir1.listFiles()) {
						fileEntry.delete();
						log.info("File Deleted");
					}
					// dir1.deleteOnExit();
					log.info("**********************Delete Folder PDFForImage Successfully****************");
				}
			}

			log.info("-***************---------------------BUNDALING CALLING---------------------------*************-");
			List<ManuScriptFile> getData = manuScriptFileRepo.getDataByManuscriptIdAndVersion(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
			log.info("------File path For Bundaling------>>>>>>>>" + myPath);
			File file = new File(myPath);
			TestMergeDocx addTestMergeDocx = new TestMergeDocx(null);
			addTestMergeDocx.mergeTextFiles(file, getData, cdnUrl, uniprrFolderPath);
			Integer wordCount = 0;
			Integer pageCount = 0;
			pageCount = addTestMergeDocx.pageCount(file, cdnUrl, uniprrFolderPath);
			wordCount = addTestMergeDocx.wordCount(file, cdnUrl, uniprrFolderPath);
			log.info("Word Count Fron Test Merge Docx----->>>>" + wordCount);

			// File SEtup
			ManuscriptFilePreview manuscriptFilePreview = manuscriptFilePreviewRepo.getManuscriptFilePreviewDetails(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());

			String xhtmlPath = null;
			String pdfPath = null;
			String pdfPathForReviewer = null;
			if (serverENV.equalsIgnoreCase("AWS")) {
				String Ip = "https://" + serverIP;
				xhtmlPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.html";
				log.info("xhtmlPath  Location  For AWS :------------->>>>>>>>>>" + xhtmlPath);

				pdfPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.pdf";

				pdfPathForReviewer = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
						+ "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "BundledPdfForReviewer.pdf";

				log.info("PDF  Location  For AWS :----------------->>>>>>>>>>>>>>" + xhtmlPath);
			} else if (serverENV.equalsIgnoreCase("LOCAL")) {
				xhtmlPath = "\\" + "\\" + InetAddress.getLocalHost().getHostAddress() + File.separator
						+ "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.html";
				log.info("xhtmlPath  Location  For LOCAL :----------->>>>>>>>>>>>>>>>>" + fileuploaddirServer);

				pdfPath = fileuploaddirServer + File.separator + "UniprrFileFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_"
						+ manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId() + File.separator
						+ "output" + File.separator + "Bundled.pdf";
				log.info("PDF  Location  For LOCAL :" + xhtmlPath);

				pdfPathForReviewer = fileuploaddirServer + File.separator + "UniprrFileFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_"
						+ manu.getJournalId() + "_" + resubmissionManuscriptFileVo.getManuScriptId() + File.separator
						+ "output" + File.separator + "BundledPdfForReviewer.pdf";

			} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
				String Ip = "https://" + serverIP + ":"
						+ serverPort;
				xhtmlPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.html";
				log.info("xhtmlPath  Location  For AWS DEMO :------------->>>>>>>>>>" + xhtmlPath);

				pdfPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.pdf";

				pdfPathForReviewer = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
						+ "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "BundledPdfForReviewer.pdf";

				log.info("PDF  Location  For AWS DEMO :----------------->>>>>>>>>>>>>>" + xhtmlPath);
			}

			// update in ManuscriptFilePreview Table
			if (manuscriptFilePreview != null) {
				log.info("update in ManuscriptFilePreview Table");
				manuscriptFilePreview.setManuscriptXhtmlPath(xhtmlPath);
				manuscriptFilePreview.setManuscriptXmlpath(xhtmlPath);
				manuscriptFilePreviewRepo.save(manuscriptFilePreview);
			} else {
				// Create new ManuscriptFilePreview
				log.info("Create new ManuscriptFilePreview");
				ManuscriptFilePreview newManuscriptFilePreview = new ManuscriptFilePreview();
				newManuscriptFilePreview.setManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
				newManuscriptFilePreview.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
				newManuscriptFilePreview.setManuscriptXhtmlPath(xhtmlPath);
				newManuscriptFilePreview.setManuscriptXmlpath(xhtmlPath);
				manuscriptFilePreviewRepo.save(newManuscriptFilePreview);
			}

			// PDF FILE DATA SAVE IN MANUSCRIPT FILE

			ManuScriptFile manuScriptFileForPdf = manuScriptFileRepo.getManuScriptFileDataPDf(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
			// Update in manuScriptFile Table
			if (manuScriptFileForPdf != null) {
				log.info("Update in manuScriptFile Table");
				manuScriptFileForPdf.setFileType("PDF");
				manuScriptFileForPdf.setFileName("Bundled.pdf");
				manuScriptFileForPdf.setFilePath(pdfPath);
				manuScriptFileForPdf.setIsActive(1);
				manuScriptFileForPdf.setFileSize(20);
				manuScriptFileForPdf.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
				manuScriptFileForPdf.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
				manuScriptFileRepo.save(manuScriptFileForPdf);
			} else {
				// Create in manuScriptFile Table
				log.info("Create in manuScriptFile Table");
				ManuScriptFile manuScriptFileForPdfCreate = new ManuScriptFile();
				manuScriptFileForPdfCreate.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
				manuScriptFileForPdfCreate.setFileType("PDF");
				manuScriptFileForPdfCreate.setFileName("Bundled.pdf");
				manuScriptFileForPdfCreate.setFilePath(pdfPath);
				manuScriptFileForPdfCreate.setIsActive(1);
				manuScriptFileForPdfCreate.setFileSize(20);
				manuScriptFileForPdfCreate.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
				manuScriptFileForPdfCreate.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
				manuScriptFileRepo.save(manuScriptFileForPdfCreate);
			}

			// PDF FILE For Reviewer DATA SAVE IN MANUSCRIPT FILE

			ManuScriptFile manuScriptFileForPdfForReviewer = manuScriptFileRepo.getManuScriptFileDataPDfForReviewer(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
			// Update in manuScriptFile Table
			if (manuScriptFileForPdfForReviewer != null) {
				log.info("Update in manuScriptFile Table");
				manuScriptFileForPdfForReviewer.setFileType("REVIEWERPDF");
				manuScriptFileForPdfForReviewer.setFileName("BundledPdfForReviewer.pdf");
				manuScriptFileForPdfForReviewer.setFilePath(pdfPathForReviewer);
				manuScriptFileForPdfForReviewer.setIsActive(1);
				manuScriptFileForPdfForReviewer.setFileSize(20);
				manuScriptFileForPdfForReviewer.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
				manuScriptFileForPdfForReviewer.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
				manuScriptFileRepo.save(manuScriptFileForPdfForReviewer);
			} else {
				// Create in manuScriptFile Table
				log.info("Create in manuScriptFile Table");
				ManuScriptFile manuScriptFileForPdfCreate = new ManuScriptFile();
				manuScriptFileForPdfCreate.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
				manuScriptFileForPdfCreate.setFileType("REVIEWERPDF");
				manuScriptFileForPdfCreate.setFileName("BundledPdfForReviewer.pdf");
				manuScriptFileForPdfCreate.setFilePath(pdfPathForReviewer);
				manuScriptFileForPdfCreate.setIsActive(1);
				manuScriptFileForPdfCreate.setFileSize(20);
				manuScriptFileForPdfCreate.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
				manuScriptFileForPdfCreate.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
				manuScriptFileRepo.save(manuScriptFileForPdfCreate);
			}

			String bundaledFilePath = null;
			Integer port = request.getServerPort();
			InetAddress inetAddress = InetAddress.getLocalHost();
			if (serverENV.equalsIgnoreCase("AWS")) {
				String Ip = "https://" + serverIP;
				bundaledFilePath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.docx";
			} else if (serverENV.equalsIgnoreCase("LOCAL")) {
				String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
				bundaledFilePath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.docx";
			} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
				String Ip = "https://" + serverIP + ":"
						+ serverPort;
				bundaledFilePath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ resubmissionManuscriptFileVo.getManuScriptId() + File.separator + "output" + File.separator
						+ "Bundled.docx";
			}

			ManuScriptFile manuScriptFile = manuScriptFileRepo.getManuScriptFileData(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
			// Update in manuScriptFile Table
			if (manuScriptFile != null) {
				log.info("Update in manuScriptFile Table");
				manuScriptFile.setFileType(MERGED_FILE);
				manuScriptFile.setFileName(FINAL_DOC);
				manuScriptFile.setFilePath(bundaledFilePath);
				manuScriptFile.setIsActive(1);
				manuScriptFile.setFileSize(20);
				manuScriptFile.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
				manuScriptFile.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
				manuScriptFileRepo.save(manuScriptFile);
			} else {
				// Create in manuScriptFile Table
				log.info("Create in manuScriptFile Table");
				ManuScriptFile manuScriptFileCreate = new ManuScriptFile();
				manuScriptFileCreate.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
				manuScriptFileCreate.setFileType(MERGED_FILE);
				manuScriptFileCreate.setFileName(FINAL_DOC);
				manuScriptFileCreate.setFilePath(bundaledFilePath);
				manuScriptFileCreate.setIsActive(1);
				manuScriptFileCreate.setFileSize(20);
				manuScriptFileCreate.setFileVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
				manuScriptFileCreate.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
				manuScriptFileRepo.save(manuScriptFileCreate);
			}

			//
			ManuScriptFile mainDocument = manuScriptFileRepo.getManuScriptFile(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion(), 1);

			// Python api
			if (mainDocument != null) {

				Integer pageNumber = wordCount;
				String pythonAPIAWS = environment.getProperty("python.bundaling.api.aws");
				String pythonAPI = environment.getProperty("python.bundaling.api");
				String staticServerPort = environment.getProperty("static.server.port.config");
				String url = BundlingUtils.prepareApiUrl(resubmissionManuscriptFileVo, manu, pageNumber, serverENV, serverIP, serverPort, pythonAPIAWS, pythonAPI, staticServerPort);

				log.debug(url);
				if (resubmissionManuscriptFileVo.getSubmissionType() != null
						&& !resubmissionManuscriptFileVo.getSubmissionType().isEmpty()
						&& resubmissionManuscriptFileVo.getSubmissionType().equalsIgnoreCase("NEW-SUBMISSION")) {
					RestTemplate restTemplate = new RestTemplate();
					JSONObject obj = restTemplate.getForObject(url, JSONObject.class);
					log.info("Python Api Response :----------------->>>>>>>>" + obj);
					if (obj != null && !obj.isEmpty()) {
						log.info("Got Response from .net API" + obj.toString());
						// JSONParser jsonParser = new JSONParser();
						// JSONObject obj = (JSONObject) jsonParser.parse(result);
						String manuscriptType = (String) obj.get(MANUSCRIPT_TYPE);
						String manuscriptTitle = (String) obj.get(MANUSCRIPT_TITLE);
						String manuscriptRunningHead = (String) obj.get(MANUSCRIPT_RUNNING_HEAD);
						String manuscriptAbstract = (String) obj.get(MANUSCRIPT_ABSTRACT);
						String manuscriptFigures = (String) obj.get(MANUSCRIPT_FIGURES);
						String manuscriptBlackWhiteFigures = (String) obj.get(MANUSCRIPT_BLACK_WHITE_FIGURES);
						String manuscriptColorFigures = (String) obj.get(MANUSCRIPT_COLOR_FIGURES);
						String manuscriptTables = (String) obj.get(MANUSCRIPT_TABLES);
						// String manuscriptWords = (String) obj.get(MANUSCRIPT_WORDS);
						// String manuscriptPages = (String) obj.get(MANUSCRIPT_PAGES);
						String manuscriptParagraph = (String) obj.get(MANUSCRIPT_PARAGRAPH);
						String manuscriptIntroduction = (String) obj.get(MANUSCRIPT_INTRODUCTION);
						String manuscriptAcknowledgment = (String) obj.get(MANUSCRIPT_ACKNOWLEDGMENT);
						String manuscriptReferences = (String) obj.get(MANUSCRIPT_REFERENCES);
						String manuscriptShortTitle = (String) obj.get(MANUSCRIPT_SHORT_TITLE);
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
						Double parseTreeHeight = (Double) obj.get(parse_tree_height);
						Double sentenceCoherence = (Double) obj.get(sentence_coherence);
						Double visualWordsRatio = (Double) obj.get(visual_words_ratio);
						String manuscriptVisualizationData = (String) obj.get(MANUSCRIPT_VISUALIZATION);

						// Manuscript Visualization
						ManuscriptVisualization manuscriptVisualization = manuscriptVisualizationRepo.getManuscriptVisualizationDetails(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion(), mainDocument.getManuScript().getJournalId());
						// Update Manuscript Visualization
						if (manuscriptVisualization != null) {
							log.info("Update Manuscript Visualization");
							manuscriptVisualization.setUpdatedBy(mainDocument.getManuScript().getAuthorId());
							manuscriptVisualization.setUpdatedOn(new Date());
							manuscriptVisualization.setVisualization(manuscriptVisualizationData);
							manuscriptVisualizationRepo.save(manuscriptVisualization);
						} else {
							// Create Manuscript Visualization log.info("Create Manuscript Visualization");
							ManuscriptVisualization addManuscriptVisualization = new ManuscriptVisualization();
							addManuscriptVisualization.setManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
							addManuscriptVisualization.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							addManuscriptVisualization.setJournalId(mainDocument.getManuScript().getJournalId());
							addManuscriptVisualization.setCreatedBy(mainDocument.getManuScript().getAuthorId());
							addManuscriptVisualization.setVisualization(manuscriptVisualizationData);
							manuscriptVisualizationRepo.save(addManuscriptVisualization);
						}

						ManscriptScore manuscriptScore = manscriptScoreRepo.getManscriptScoreData(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
						// Update in manuscriptScore Table
						if (manuscriptScore != null) {
							log.info("Update in manuscriptScore Table");
							manuscriptScore.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptScore.setAvgSyllablesCount(avgSyllablesCount);
							manuscriptScore.setAvgWordLength(avgWordLength);
							manuscriptScore.setDifficultWordsRatio(difficultWordsRatio);
							manuscriptScore.setFleschKincaidGrade(fleschKincaidGrade);
							manuscriptScore.setFleschReadingEase(fleschReadingEase);
							manuscriptScore.setGunningFogIndex(gunningFogIndex);
							manuscriptScore.setPosRatio(posRatio);
							manuscriptScore.setSentenceLength(sentenceLength);
							manuscriptScore.setSentenceNo(sentenceNo);
							manuscriptScore.setTypeToTokenRatio(typeToTokenRatio);
							manuscriptScore.setParseTreeHeight(parseTreeHeight);
							manuscriptScore.setSentenceCoherence(sentenceCoherence);
							manuscriptScore.setVisualWordsRatio(visualWordsRatio);
							manscriptScoreRepo.save(manuscriptScore);
						} else {
							// Create new manuscriptScore
							log.info("Create new manuscriptScore");
							ManscriptScore manuscriptScoreNew = new ManscriptScore();
							manuscriptScoreNew.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
							manuscriptScoreNew.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptScoreNew.setAvgSyllablesCount(avgSyllablesCount);
							manuscriptScoreNew.setAvgWordLength(avgWordLength);
							manuscriptScoreNew.setDifficultWordsRatio(difficultWordsRatio);
							manuscriptScoreNew.setFleschKincaidGrade(fleschKincaidGrade);
							manuscriptScoreNew.setFleschReadingEase(fleschReadingEase);
							manuscriptScoreNew.setGunningFogIndex(gunningFogIndex);
							manuscriptScoreNew.setPosRatio(posRatio);
							manuscriptScoreNew.setSentenceLength(sentenceLength);
							manuscriptScoreNew.setSentenceNo(sentenceNo);
							manuscriptScoreNew.setTypeToTokenRatio(typeToTokenRatio);
							manuscriptScoreNew.setParseTreeHeight(parseTreeHeight);
							manuscriptScoreNew.setSentenceCoherence(sentenceCoherence);
							manuscriptScoreNew.setVisualWordsRatio(visualWordsRatio);
							manscriptScoreRepo.save(manuscriptScoreNew);
						}
						ManuScript manuscript = manuscriptRepo.getOne(resubmissionManuscriptFileVo.getManuScriptId());
						// Update in manuscript Table
						if (manuscript != null) {
							log.info("Update in manuscript Table");
							manuscript.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscript.setManuscriptTitle(manuscriptTitle);
							manuscript.setManuscriptShortTitle(manuscriptShortTitle);
							manuscript.setManuscriptRunningHead(manuscriptRunningHead);
							manuscript.setManuscriptAbstract(manuscriptAbstract);
							manuscript.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptRepo.save(manuscript);
						}

						ManuscriptTypeDetails manuscriptTypeDetails = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
						// Update in manuscriptTypeDetails
						if (manuscriptTypeDetails != null) {
							log.info("Update in manuscriptTypeDetails");

							manuscriptTypeDetails.setManuscriptFigures(manuscriptFigures);
							manuscriptTypeDetails.setManuscriptBlackWhiteFigures(manuscriptBlackWhiteFigures);
							manuscriptTypeDetails.setManuscriptColorFigures(manuscriptColorFigures);
							manuscriptTypeDetails.setManuscriptTables(manuscriptTables);
							if (wordCount != null)
								manuscriptTypeDetails.setManuscriptWords(Integer.toString(wordCount));
							if (pageCount != null)
								manuscriptTypeDetails.setManuscriptPages(Integer.toString(pageCount));
							manuscriptTypeDetails.setManuscriptParagraph(manuscriptParagraph);
							manuscriptTypeDetails.setManuscriptIntroduction(manuscriptIntroduction);
							manuscriptTypeDetails.setManuscriptAcknowledgment(manuscriptAcknowledgment);
							manuscriptTypeDetails.setManuscriptReferences(manuscriptReferences);
							manuscriptTypeDetails.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptTypeDetailsRepo.save(manuscriptTypeDetails);
						} else {
							// Create in manuscriptTypeDetails
							log.info("Create in manuscriptTypeDetails");
							ManuscriptTypeDetails manuscriptTypeDetailsCreate = new ManuscriptTypeDetails();
							manuscriptTypeDetailsCreate.setManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
							manuscriptTypeDetailsCreate.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptTypeDetailsCreate.setManuscriptType(manuscriptType);
							manuscriptTypeDetailsCreate.setManuscriptFigures(manuscriptFigures);
							manuscriptTypeDetailsCreate.setManuscriptBlackWhiteFigures(manuscriptBlackWhiteFigures);
							manuscriptTypeDetailsCreate.setManuscriptColorFigures(manuscriptColorFigures);
							manuscriptTypeDetailsCreate.setManuscriptTables(manuscriptTables);
							if (wordCount != null)
								manuscriptTypeDetailsCreate.setManuscriptWords(Integer.toString(wordCount));
							if (pageCount != null)
								manuscriptTypeDetailsCreate.setManuscriptPages(Integer.toString(pageCount));
							manuscriptTypeDetailsCreate.setManuscriptParagraph(manuscriptParagraph);
							manuscriptTypeDetailsCreate.setManuscriptIntroduction(manuscriptIntroduction);
							manuscriptTypeDetailsCreate.setManuscriptAcknowledgment(manuscriptAcknowledgment);
							manuscriptTypeDetailsCreate.setManuscriptReferences(manuscriptReferences);
							manuscriptTypeDetailsCreate.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
							manuscriptTypeDetailsRepo.save(manuscriptTypeDetailsCreate);
						}
					} else {
						return "Unable to process your files. You can fill data manually.";
					}

				} else if (resubmissionManuscriptFileVo.getSubmissionType() != null
						&& !resubmissionManuscriptFileVo.getSubmissionType().isEmpty()
						&& resubmissionManuscriptFileVo.getSubmissionType().equalsIgnoreCase("RE-SUBMISSION")) {
					// Manuscript Visualization
					ManuscriptVisualization manuscriptVisualization = manuscriptVisualizationRepo.getManuscriptVisualizationDetails(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion()
							- 1, mainDocument.getManuScript().getJournalId());
					// Update Manuscript Visualization
					if (manuscriptVisualization != null) {
						// Create Manuscript Visualization log.info("Create Manuscript Visualization");
						ManuscriptVisualization addManuscriptVisualization = new ManuscriptVisualization();
						addManuscriptVisualization.setManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
						addManuscriptVisualization.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						addManuscriptVisualization.setJournalId(mainDocument.getManuScript().getJournalId());
						addManuscriptVisualization.setCreatedBy(mainDocument.getManuScript().getAuthorId());
						addManuscriptVisualization.setVisualization(manuscriptVisualization.getVisualization());
						manuscriptVisualizationRepo.save(addManuscriptVisualization);
					}

					ManscriptScore manuscriptScore = manscriptScoreRepo.getManscriptScoreData(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion()
							- 1);
					// Update in manuscriptScore Table
					if (manuscriptScore != null) {
						// Create new manuscriptScore
						log.info("Create new manuscriptScore");
						ManscriptScore manuscriptScoreNew = new ManscriptScore();
						manuscriptScoreNew.setManuScriptId(resubmissionManuscriptFileVo.getManuScriptId());
						manuscriptScoreNew.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						manuscriptScoreNew.setAvgSyllablesCount(manuscriptScore.getAvgSyllablesCount());
						manuscriptScoreNew.setAvgWordLength(manuscriptScore.getAvgWordLength());
						manuscriptScoreNew.setDifficultWordsRatio(manuscriptScore.getDifficultWordsRatio());
						manuscriptScoreNew.setFleschKincaidGrade(manuscriptScore.getFleschKincaidGrade());
						manuscriptScoreNew.setFleschReadingEase(manuscriptScore.getFleschReadingEase());
						manuscriptScoreNew.setGunningFogIndex(manuscriptScore.getGunningFogIndex());
						manuscriptScoreNew.setPosRatio(manuscriptScore.getPosRatio());
						manuscriptScoreNew.setSentenceLength(manuscriptScore.getSentenceLength());
						manuscriptScoreNew.setSentenceNo(manuscriptScore.getSentenceNo());
						manuscriptScoreNew.setTypeToTokenRatio(manuscriptScore.getTypeToTokenRatio());
						manuscriptScoreNew.setParseTreeHeight(manuscriptScore.getParseTreeHeight());
						manuscriptScoreNew.setSentenceCoherence(manuscriptScore.getSentenceCoherence());
						manuscriptScoreNew.setVisualWordsRatio(manuscriptScore.getVisualWordsRatio());
						manscriptScoreRepo.save(manuscriptScoreNew);
					}
					ManuScript manuscript = manuscriptRepo.getOne(resubmissionManuscriptFileVo.getManuScriptId());
					// Update in manuscript Table
					if (manuscript != null) {
						log.info("Update in manuscript Table");
						manuscript.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						manuscriptRepo.save(manuscript);
					}

					ManuscriptTypeDetails manuscriptTypeDetails = manuscriptTypeDetailsRepo.getDetailsFromManuscriptId(resubmissionManuscriptFileVo.getManuScriptId());
					// Update in manuscriptTypeDetails
					if (manuscriptTypeDetails != null) {
						log.info("Update in manuscriptTypeDetails");
						manuscriptTypeDetails.setManuscriptVersion(resubmissionManuscriptFileVo.getManuscriptVersion());
						manuscriptTypeDetailsRepo.save(manuscriptTypeDetails);
					}
				}
			}
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception due to code";
		}
	}

	@Override
	@Transactional
	public String updateManDocumentFile(Integer manuscriptFileId, String fileStatus, MultipartFile file, HttpServletRequest request) {
		log.info("-------------------Inside in updateFile------------------");
		BufferedOutputStream stream = null;
		try {
			// Step 1. Get manuscript File Data By Id
			ManuScriptFile getFileDetails = manuScriptFileRepo.getOne(manuscriptFileId);
			if (getFileDetails != null) {
				log.info("-------------------Get Manuscript File Data-----------------");
				// Server Setup
				// Step 2. Drive Location
				// String filezipMakingdir = System.getProperty("catalina.base") +
				// File.separator + "webapps" + File.separator;
				String filezipMakingdir = uploadFolder + File.separator;
				log.info("filezipMakingdir  Location :" + filezipMakingdir);
				String fileuploaddirServer = null;
				// Step 3. Server Location
				if (serverENV.equalsIgnoreCase("AWS")) {
					fileuploaddirServer = "https://" + serverIP;
					log.info("fileuploaddirServer  Location  For AWS :---------------->>>>>>>" + fileuploaddirServer);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					fileuploaddirServer = "https://" + InetAddress.getLocalHost().getHostAddress() + ":"
							+ request.getServerPort();
					log.info("fileuploaddirServer  Location  For LOCAL :--------------->>>>>>>>>" + fileuploaddirServer);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					fileuploaddirServer = "https://" + serverIP + ":"
							+ serverPort;
					log.info("fileuploaddirServer  Location  For AWS DEMO :---------------->>>>>>>"
							+ fileuploaddirServer);
				}
				// Step 4. File Drive Location
				String drivePath = filezipMakingdir + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ getFileDetails.getFileVersion() + "_" + getFileDetails.getManuScript().getJournalId() + "_"
						+ getFileDetails.getManuScriptId() + File.separator + getFileDetails.getFileName();
				// Step 4.1. Now Change the name and replace it with random number
				Random rand = new Random();
				// Generate random integers in range 0 to 999
				int i = rand.nextInt(1000000000);
				log.info("-------------------Random number------------------------->>>>>>>>>>" + i);
				String fileName = getFileDetails.getFileName();
				String basename = FilenameUtils.getBaseName(fileName);
				String extension = FilenameUtils.getExtension(fileName);
				log.info("-------------------Base File Name------------------------->>>>>>>>>>" + basename);
				log.info("-------------------Extension File Name------------------------->>>>>>>>>>" + extension);
				String finalNameFile = basename + i + "." + extension;
				log.info("-------------------Final Name Of File------------------------->>>>>>>>>>" + finalNameFile);
				// Step 4.2. Now Replace file name
				File rename = new File(drivePath);
				File name = new File(rename.getParent() + File.separator + finalNameFile);
				if (rename.renameTo(name)) {
					log.info("-------------------Re NAme File Success ------------------------->>>>>>>>>>");
				} else {
					log.info("-------------------Re NAme File Failure ------------------------->>>>>>>>>>");
				}
				// Step 5. Setup For the file folder in the same Manuscript Folder
				String attachmentFolderPath = filezipMakingdir + "UniprrFileFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + getFileDetails.getFileVersion() + "_"
						+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
						+ File.separator + "PreviousFileFolder";
				log.info("-------------------Previous File Folder Location------------------>" + attachmentFolderPath);
				// Step 6. Create Folder In Respective Drive
				File dir = new File(attachmentFolderPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// Step 7. Now Start Moving File from current folder into New Created folder
				FileUtils.moveFileToDirectory(new File(rename.getParent() + File.separator + finalNameFile), new File(attachmentFolderPath), true);
				log.info("-------------------File Moved Successfully in Previous File Folder Location-------------------------");
				// Step 7.1 Now Enter the previous file details in db as status oldFile And
				// Version
				ManuScriptFile a = new ManuScriptFile();
				a.setManuScriptId(getFileDetails.getManuScriptId());
				a.setFileName(getFileDetails.getFileName());
				a.setFileSize(getFileDetails.getFileSize());
				a.setFileType(getFileDetails.getFileType());
				a.setFileStatus(getFileDetails.getFileStatus() + "-" + "oldFile");
				a.setFileVersion(getFileDetails.getFileVersion());
				a.setFileNumber(getFileDetails.getFileNumber());
				a.setFileActive(getFileDetails.getFileActive());
				a.setIsActive(0);
				a.setLegend(getFileDetails.getLegend());
				InetAddress inetAddress = InetAddress.getLocalHost();
				Integer port = request.getServerPort();
				String oldFileServerPath = null;
				// Step 7.2 Now Create path for the server to download and paste in the column
				if (serverENV.equalsIgnoreCase("AWS")) {
					String Ip = "https://" + serverIP;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for AWS : " + oldFileServerPath);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for LOCAL : " + oldFileServerPath);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					String Ip = "https://" + serverIP + ":"
							+ serverPort;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for AWS DEMO: " + oldFileServerPath);
				}
				a.setFilePath(oldFileServerPath);
				manuScriptFileRepo.save(a);
				// Step 8. Upload file in that folder which we have to replace
				String uploadFileFolder = filezipMakingdir + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
						+ "R" + getFileDetails.getFileVersion() + "_" + getFileDetails.getManuScript().getJournalId()
						+ "_" + getFileDetails.getManuScriptId();
				File dir1 = new File(uploadFileFolder);
				// Step 9. Create folder in the drive
				if (!dir1.exists()) {
					dir1.mkdirs();
				}
				byte[] bytes = file.getBytes();
				File serverFile = new File(dir1.getAbsolutePath() + File.separator + file.getOriginalFilename());
				// Step 10. Now Write file in the respective drive
				stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				log.info("-------------------New File Replace in the place of previous file in same Folder Location------------------");
				double fileSizeinBytes = file.getSize();
				double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
				String fileServerPath = null;

				// Step 11. Now Create path for the server to download and paste in the column
				if (serverENV.equalsIgnoreCase("AWS")) {
					String Ip = "https://" + serverIP;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for AWS : " + fileServerPath);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for LOCAL : " + fileServerPath);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					String Ip = "https://" + serverIP + ":"
							+ serverPort;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for AWS DEMO: " + fileServerPath);
				}
				// Step 12. Update Manuscript File for the manuscript file id
				getFileDetails.setFileName(file.getOriginalFilename());
				getFileDetails.setFilePath(fileServerPath);
				getFileDetails.setFileSize((int) fileSizeInKB);
				getFileDetails.setFileStatus(fileStatus);
				ManuScriptFile getFile = manuScriptFileRepo.save(getFileDetails);
				log.info("-------------------New File Updated Successfully------------------");

				log.info("STEP 4 -> Get main document location from manuscript file table ");
				// Change the name of man document if the file name is different
				if (getFile != null) {
					log.info("STEP 5 -> Start changing name of main document");
					String serverfilePath = getFile.getFilePath();
					String driveFilePath = serverfilePath.replace(fileuploaddirServer, filezipMakingdir);
					// String[] array = driveFilePath.split("\\.");
					// int len = array.length;
					// String extension = array[len - 1];
					String fileName1 = driveFilePath;
					String basename1 = FilenameUtils.getBaseName(fileName1);
					String extension1 = FilenameUtils.getExtension(fileName1);
					if (extension1.equalsIgnoreCase("doc")) {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "Manuscript.doc");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("Manuscript.doc");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "Manuscript.doc");
							if (serverENV.equalsIgnoreCase("AWS")) {
								String Ip = "https://" + serverIP;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "Manuscript.doc";
								getFile.setFilePath(fileServerPath1);
							} else if (serverENV.equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
							} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + serverIP + ":"
										+ serverPort;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "Manuscript.doc";
								getFile.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a Manuscript.doc");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has Manuscript.doc");
						}
					} else {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "Manuscript.docx");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("Manuscript.docx");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "Manuscript.docx");
							if (serverENV.equalsIgnoreCase("AWS")) {
								String Ip = "https://" + serverIP;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "Manuscript.docx";
								getFile.setFilePath(fileServerPath1);
							} else if (serverENV.equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
							} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + serverIP + ":"
										+ serverPort;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "Manuscript.docx";
								getFile.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a Manuscript.docx");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has Manuscript.dox");
						}
					}
				}
				return "successfully updated";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("-------------------Exception due to code ------------------>" + e);
		} finally {
			try {
				stream.close();
				log.info("-------------------File Close------------------");
			} catch (IOException e) {
				e.printStackTrace();
				log.info("-------------------Exception due to file ------------------>" + e);
			}
		}
		log.info("-------------------There is no file to update------------------");
		return "There is no file to update";
	}

	@Override
	@Transactional
	public String updateManDocumentFileWithRoleTask(Integer manuscriptFileId, Integer role, Integer manuscriptStatus, String fileStatus, Integer userId, MultipartFile file, HttpServletRequest request) {
		log.info("-------------------Inside in updateFile------------------");
		BufferedOutputStream stream = null;
		try {
			// Step 1. Get manuscript File Data By Id
			ManuScriptFile getFileDetails = manuScriptFileRepo.getOne(manuscriptFileId);
			if (getFileDetails != null) {
				log.info("-------------------Get Manuscript File Data-----------------");
				// Server Setup
				// Step 2. Drive Location
				// String filezipMakingdir = System.getProperty("catalina.base") +
				// File.separator + "webapps" + File.separator;
				String filezipMakingdir = uploadFolder + File.separator;
				log.info("filezipMakingdir  Location :" + filezipMakingdir);
				String fileuploaddirServer = null;
				// Step 3. Server Location
				if (serverENV.equalsIgnoreCase("AWS")) {
					fileuploaddirServer = "https://" + serverIP;
					log.info("fileuploaddirServer  Location  For AWS :---------------->>>>>>>" + fileuploaddirServer);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					fileuploaddirServer = "https://" + InetAddress.getLocalHost().getHostAddress() + ":"
							+ request.getServerPort();
					log.info("fileuploaddirServer  Location  For LOCAL :--------------->>>>>>>>>" + fileuploaddirServer);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					fileuploaddirServer = "https://" + serverIP + ":"
							+ serverPort;
					log.info("fileuploaddirServer  Location  For AWS DEMO :---------------->>>>>>>"
							+ fileuploaddirServer);
				}
				
				// Step 4. Upload file in that folder which we have to replace
				String uploadFileFolder = filezipMakingdir + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
						+ "R" + getFileDetails.getFileVersion() + "_" + getFileDetails.getManuScript().getJournalId()
						+ "_" + getFileDetails.getManuScriptId();
				File dir1 = new File(uploadFileFolder);
				System.out.println("uploadFileFolder: "+uploadFileFolder);
				// Step 5. Create folder in the drive
				if (!dir1.exists()) {
					dir1.mkdirs();
				}
				byte[] bytes = file.getBytes();
				File serverFile = new File(dir1.getAbsolutePath() + File.separator + file.getOriginalFilename());
				// Step 6. Now Write file in the respective drive
				stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				// Step 7. File Validation
		        Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
	            boolean isValidFile = FileUploadValidation.validateFile(serverFile, expectedMimeTypes);
	            if (!isValidFile) {
	                log.error("Uploaded file validation failed");
	                log.info("Deleting invalid file: " + serverFile.delete());
	                return "There is no file to update";
	            }
				// Step 8. File Drive Location
				String drivePath = filezipMakingdir + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ getFileDetails.getFileVersion() + "_" + getFileDetails.getManuScript().getJournalId() + "_"
						+ getFileDetails.getManuScriptId() + File.separator + getFileDetails.getFileName();
				// Step 9.1. Now Change the name and replace it with random number
				Random rand = new Random();
				// Generate random integers in range 0 to 999
				int i = rand.nextInt(1000000000);
				log.info("-------------------Random number------------------------->>>>>>>>>>" + i);
				String fileName = getFileDetails.getFileName();
				String basename = FilenameUtils.getBaseName(fileName);
				String extension = FilenameUtils.getExtension(fileName);
				log.info("-------------------Base File Name------------------------->>>>>>>>>>" + basename);
				log.info("-------------------Extension File Name------------------------->>>>>>>>>>" + extension);
				String finalNameFile = basename + i + "." + extension;
				log.info("-------------------Final Name Of File------------------------->>>>>>>>>>" + finalNameFile);
				// Step 9.2. Now Replace file name
				File rename = new File(drivePath);
				File name = new File(rename.getParent() + File.separator + finalNameFile);
				if (rename.renameTo(name)) {
					log.info("-------------------Re NAme File Success ------------------------->>>>>>>>>>");
				} else {
					log.info("-------------------Re NAme File Failure ------------------------->>>>>>>>>>");
				}
				// Step 10. Setup For the file folder in the same Manuscript Folder
				String attachmentFolderPath = filezipMakingdir + "UniprrFileFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + getFileDetails.getFileVersion() + "_"
						+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
						+ File.separator + "PreviousFileFolder";
				log.info("-------------------Previous File Folder Location------------------>" + attachmentFolderPath);
				// Step 11. Create Folder In Respective Drive
				File dir = new File(attachmentFolderPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// Step 12. Now Start Moving File from current folder into New Created folder
				FileUtils.moveFileToDirectory(new File(rename.getParent() + File.separator + finalNameFile), new File(attachmentFolderPath), true);
				log.info("-------------------File Moved Successfully in Previous File Folder Location-------------------------");
				// Step 13.1 Now Enter the previous file details in db as status oldFile And
				// Version
				ManuScriptFile a = new ManuScriptFile();
				a.setManuScriptId(getFileDetails.getManuScriptId());
				a.setFileName(getFileDetails.getFileName());
				a.setFileSize(getFileDetails.getFileSize());
				a.setFileType(getFileDetails.getFileType());
				a.setFileStatus(getFileDetails.getFileStatus() + "-" + "oldFile");
				a.setFileVersion(getFileDetails.getFileVersion());
				a.setFileNumber(getFileDetails.getFileNumber());
				a.setFileActive(getFileDetails.getFileActive());
				a.setIsActive(0);
				a.setLegend(getFileDetails.getLegend());
				InetAddress inetAddress = InetAddress.getLocalHost();
				Integer port = request.getServerPort();
				String oldFileServerPath = null;
				// Step 13.2 Now Create path for the server to download and paste in the column
				if (serverENV.equalsIgnoreCase("AWS")) {
					String Ip = "https://" + serverIP;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for AWS : " + oldFileServerPath);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for LOCAL : " + oldFileServerPath);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					String Ip = "https://" + serverIP + ":"
							+ serverPort;
					oldFileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + "PreviousFileFolder" + File.separator + finalNameFile;
					log.info("Server Path for AWS DEMO: " + oldFileServerPath);
				}
				a.setFilePath(oldFileServerPath);
				manuScriptFileRepo.save(a);

				log.info("-------------------New File Replace in the place of previous file in same Folder Location------------------");
				double fileSizeinBytes = file.getSize();
				double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
				String fileServerPath = null;

				// Step 14. Now Create path for the server to download and paste in the column
				if (serverENV.equalsIgnoreCase("AWS")) {
					String Ip = "https://" + serverIP;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for AWS : " + fileServerPath);
				} else if (serverENV.equalsIgnoreCase("LOCAL")) {
					String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for LOCAL : " + fileServerPath);
				} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
					String Ip = "https://" + serverIP + ":"
							+ serverPort;
					fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
							+ "R" + getFileDetails.getFileVersion() + "_"
							+ getFileDetails.getManuScript().getJournalId() + "_" + getFileDetails.getManuScriptId()
							+ File.separator + file.getOriginalFilename();
					log.info("Server Path for AWS DEMO: " + fileServerPath);
				}
				// Step 15. Update Manuscript File for the manuscript file id
				getFileDetails.setFileName(file.getOriginalFilename());
				getFileDetails.setFilePath(fileServerPath);
				getFileDetails.setFileSize((int) fileSizeInKB);
				getFileDetails.setFileStatus(fileStatus);
				ManuScriptFile getFile = manuScriptFileRepo.save(getFileDetails);
				log.info("-------------------New File Updated Successfully------------------");
			    ManuscriptEditorComment addManuscriptEditorComment = new ManuscriptEditorComment();
				addManuscriptEditorComment.setFileSize((int) fileSizeInKB);	
				addManuscriptEditorComment.setFileType("Accept For Production");	

				log.info("STEP 4 -> Get main document location from manuscript file table ");
				// Change the name of man document if the file name is different
				if (getFile != null) {
					log.info("STEP 5 -> Start changing name of main document");
					String serverfilePath = getFile.getFilePath();
					String driveFilePath = serverfilePath.replace(fileuploaddirServer, filezipMakingdir);
					// String[] array = driveFilePath.split("\\.");
					// int len = array.length;
					// String extension = array[len - 1];
					String fileName1 = driveFilePath;
					String basename1 = FilenameUtils.getBaseName(fileName1);
					String extension1 = FilenameUtils.getExtension(fileName1);
					if (extension1.equalsIgnoreCase("doc")) {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "Manuscript.doc");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("Manuscript.doc");
							addManuscriptEditorComment.setFileName("Manuscript.doc");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "Manuscript.doc");
							if (serverENV.equalsIgnoreCase("AWS")) {
								String Ip = "https://" + serverIP;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "Manuscript.doc";
								getFile.setFilePath(fileServerPath1);
								addManuscriptEditorComment.setFilePath(fileServerPath1);
							} else if (serverENV.equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
								addManuscriptEditorComment.setFilePath(fileServerPath);
							} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + serverIP + ":"
										+ serverPort;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator + "Manuscript.doc";
								getFile.setFilePath(fileServerPath1);
								addManuscriptEditorComment.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a Manuscript.doc");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has Manuscript.doc");
						}
					} else {
						File rename1 = new File(driveFilePath);
						File name1 = new File(rename.getParent() + File.separator + "Manuscript.docx");
						if (rename1.renameTo(name1)) {
							getFile.setFileName("Manuscript.docx");
							addManuscriptEditorComment.setFileName("Manuscript.docx");
							String dataTemp[] = serverfilePath.split("\\\\");
							int length = dataTemp.length;
							String dummy = dataTemp[length - 1].toString();
							serverfilePath = serverfilePath.replace(dummy, "Manuscript.docx");
							if (serverENV.equalsIgnoreCase("AWS")) {
								String Ip = "https://" + serverIP;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "Manuscript.docx";
								getFile.setFilePath(fileServerPath1);
								addManuscriptEditorComment.setFilePath(fileServerPath1);
							} else if (serverENV.equalsIgnoreCase("LOCAL")) {
								getFile.setFilePath(serverfilePath);
								addManuscriptEditorComment.setFilePath(fileServerPath);
							} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
								String Ip = "https://" + serverIP + ":"
										+ serverPort;
								String fileServerPath1 = Ip + File.separator + "UniprrFileFolder" + File.separator
										+ "MNT_LWW_JOURNAL_" + "R" + getFile.getManuScript().getManuscriptVersion()
										+ "_" + getFile.getManuScript().getJournalId() + "_"
										+ getFile.getManuScript().getManuScriptId() + File.separator
										+ "Manuscript.docx";
								getFile.setFilePath(fileServerPath1);
								addManuscriptEditorComment.setFilePath(fileServerPath1);
							}
							log.info("Document Name Changed As a Manuscript.docx");
							manuScriptFileRepo.save(getFile);
							log.info("STEP 6 -> main document name changed has Manuscript.dox");
						}
					}
					
					if((role.equals(3) || role.equals(2)) && (manuscriptStatus.equals(33) || manuscriptStatus.equals(63))) {
                    ManuScript manu = manuscriptRepo.getById(getFile.getManuScriptId());
					addManuscriptEditorComment.setStatusId(manuscriptStatus);					
					addManuscriptEditorComment.setCreatedAt(new Date());
					addManuscriptEditorComment.setManuscriptId(getFile.getManuScriptId());
					addManuscriptEditorComment.setManuscriptVersion(getFile.getFileVersion());
					addManuscriptEditorComment.setUserId(userId);
					addManuscriptEditorComment.setRoleId(role);
					log.info("----------------------File Details has been added in Editor Comment--------------------");
			        manuscriptEditorCommentRepo.save(addManuscriptEditorComment);
				    }
				}
				return "successfully updated";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("-------------------Exception due to code ------------------>" + e);
		} finally {
			try {
				stream.close();
				log.info("-------------------File Close------------------");
			} catch (IOException e) {
				e.printStackTrace();
				log.info("-------------------Exception due to file ------------------>" + e);
			}
		}
		log.info("-------------------There is no file to update------------------");
		return "There is no file to update";
	}

	public boolean mainDocumentFileNameValidator(String fileName, ResubmissionManuscriptFileVo resubmissionManuscriptFileVo) {

		boolean fileStatus;

		Integer manuscriptFileCheck = manuScriptFileRepo.getManuScriptFileCheck(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());

		// log.info("Manuscript main document check :::::::"+manuscriptFileCheck);

		if (manuscriptFileCheck > 0) {
			fileStatus = true;
		} else {
			fileStatus = false;
		}

		log.info("Manuscript main document final status :::::::" + fileStatus);

		return fileStatus;
	}

	public boolean firstPageFileNameValidator(String fileName, ResubmissionManuscriptFileVo resubmissionManuscriptFileVo) {

		boolean fileStatus;

		Integer manuScriptFirstPageCheck = manuScriptFileRepo.getManuScriptFirstPageFileCheck(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());

		// log.info("Manuscript First page check :::::::"+manuScriptFirstPageCheck);

		if (manuScriptFirstPageCheck > 0) {
			fileStatus = true;
		} else {
			fileStatus = false;
		}

		log.info("Manuscript First page status :::::::" + fileStatus);
		return fileStatus;
	}

	@Override
	public boolean submissionUpdateUploadedFile(ResubmissionManuscriptFileVo manuscriptFileVo) {
		boolean fileStatus;

		log.info("************************  submissionUpdateUploadedFile  ********************* ");
		try {
			if (manuscriptFileVo.getManuscriptFileVO() != null && !manuscriptFileVo.getManuscriptFileVO().isEmpty()) {
				List<ManuScriptFile> manuscriptFiles = new ArrayList<>(manuscriptFileVo.getManuscriptFileVO().size());
				TreeMap<String , String> FigureFiles = new TreeMap<>();
				ManuScript manuScript = manuscriptRepo.getById(manuscriptFileVo.getManuScriptId());
				log.info(" manuScriptId  :: {}"+manuscriptFileVo.getManuScriptId());
				manuscriptFileVo.getManuscriptFileVO().stream().forEach(manuFile -> {
					if (manuFile.getFileName() != null && StringUtils.isNotBlank(manuFile.getFileType())) {
						ManuScriptFile manuscriptFile = manuScriptFileRepo.getById(manuFile.getManuscriptFileId());
//						manuScript = manuscriptRepo.getById(manuscriptFileVo.getManuScriptId());
						if (manuscriptFile != null) {
							manuscriptFile.setFileType(manuFile.getFileType());
							if (StringUtils.isNotBlank(manuFile.getFileType()) && manuFile.getFileType().equals("Main Documents/Article File")) {
								long count = manuScriptFileRepo.countMainDocumentByIdAndVerison(manuscriptFileVo.getManuScriptId(), manuscriptFileVo.getManuscriptVersion());
								log.info("ManuscriptVersion :: {}",manuscriptFileVo.getManuscriptVersion());
								manuscriptFile.setFileNumber(count != 0 ? (int) count : 1);
							}
							if (StringUtils.isNotBlank(manuFile.getFileType()) && manuFile.getFileType().equals("Figures")) {
								log.info("manu file name   :  "+manuFile.getFileName());
								FigureFiles.put(manuFile.getFileName(), manuFile.getLegend());
							}

							manuscriptFile.setLegend(manuFile.getLegend());
							manuscriptFile.setFileVersion(manuscriptFileVo.getManuscriptVersion());
							manuscriptFile.setFileOrder(manuFile.getFileOrder());
							manuscriptFile.setFileActive(1);

							manuscriptFiles.add(manuscriptFile);
						}
					}
				});
				figuretextlegend(FigureFiles,manuscriptFileVo.getManuScriptId(),manuscriptFileVo.getManuscriptVersion(),manuScript.getJournalId());
				log.info("figure text legend");
				manuScriptFileRepo.saveAll(manuscriptFiles);

			}
			fileStatus = true;
		} catch (Exception ex) {
			String message = "Error occured while saving manuscript file details in database :" + ex.getMessage();
			log.error(message, ex);
			fileStatus = false;
			saveBundleTrackerDetails(manuscriptFileVo.getManuScriptId(), AppConstants.FAILED ,message,manuscriptFileVo.getManuscriptVersion());
		}

		return fileStatus;
	}



	/**
	 * Not in use
	 */
	@Deprecated
	public boolean submissionUpdateUploadedFile_old(ResubmissionManuscriptFileVo manuscriptFileVo) {
		boolean fileStatus;
		try {
			if (manuscriptFileVo.getManuscriptFileVO() != null
					&& !manuscriptFileVo.getManuscriptFileVO().isEmpty()) {
				manuscriptFileVo.getManuscriptFileVO().stream().forEach(i -> {
					if (i.getFileName() != null && StringUtils.isNotBlank(i.getFileType())) {
						log.debug("i.getManuscriptFileId() from UI::: " + i.getManuscriptFileId());
						ManuScriptFile k = manuScriptFileRepo.getById(i.getManuscriptFileId());

						if (k != null) {
							k.setFileType(i.getFileType());
							if (StringUtils.isNotBlank(i.getFileType()) && i.getFileType().contentEquals("Main Documents/Article File")) {
								List<ManuScriptFile> j = manuScriptFileRepo.getMainDocumentOfManuscriptFileWithMnuscriptVerison(manuscriptFileVo.getManuScriptId(), manuscriptFileVo.getManuscriptVersion());
								if (j != null && !j.isEmpty()) {
									k.setFileNumber(j.size());
								} else {
									k.setFileNumber(1);
								}
							}
							k.setLegend(i.getLegend());
							k.setFileVersion(manuscriptFileVo.getManuscriptVersion());
							k.setFileOrder(i.getFileOrder());
							k.setFileActive(1);

							manuScriptFileRepo.saveAndFlush(k);
						}
					}
				});
			}
			fileStatus = true;
		} catch (Exception ex) {
			String message = "Error occured while saving manuscript file details in database :" + ex.getMessage();
			log.error(message, ex);
			fileStatus = false;
			saveBundleTrackerDetails(manuscriptFileVo.getManuScriptId(), AppConstants.FAILED, message,manuscriptFileVo.getManuscriptVersion());
		}

		return fileStatus;
	}

	@Override
	public boolean renameMainFirstDocument(ResubmissionManuscriptFileVo manuscriptFileVo) {
		boolean renameStatus;
		try {
			String filePath = "";
			String extension = "";
			//Change the name of Main Document
			ManuScriptFile mainDocument = manuScriptFileRepo.getManuScriptFile(manuscriptFileVo.getManuScriptId(), manuscriptFileVo.getManuscriptVersion(), 1);
			log.info("ManuscriptVersion :: {}",manuscriptFileVo.getManuscriptVersion());
			if(mainDocument != null) {
				filePath = mainDocument.getFilePath();
				extension = FilenameUtils.getExtension(filePath);

				if (extension.equalsIgnoreCase("doc")) {
					mainDocument = BundlingUtils.prepareManuscriptDocument(mainDocument, "Manuscript.doc", uploadFolder, serverENV, serverIP, serverPort);
				} else {
					mainDocument = BundlingUtils.prepareManuscriptDocument(mainDocument, "Manuscript.docx", uploadFolder, serverENV, serverIP, serverPort);
				}
				manuScriptFileRepo.saveAndFlush(mainDocument);
				log.info("Main document name changed to Manuscript.doc/Manuscript.docx");
			}


			//Change the Name of First page document
			ManuScriptFile firstPage = manuScriptFileRepo.getManuScriptFirstPageFile(manuscriptFileVo.getManuScriptId(), manuscriptFileVo.getManuscriptVersion(), 1);
			if(firstPage != null) {
				filePath = firstPage.getFilePath();
				log.info("firstPage FilePath :: {}"+firstPage.getFilePath());
				extension = FilenameUtils.getExtension(filePath);
				if (extension.equalsIgnoreCase("doc")) {
					firstPage = BundlingUtils.prepareManuscriptDocument(firstPage, "FirstPage.doc", uploadFolder, serverENV, serverIP, serverPort);
				} else {
					firstPage = BundlingUtils.prepareManuscriptDocument(firstPage, "FirstPage.docx", uploadFolder, serverENV, serverIP, serverPort);
				}
				manuScriptFileRepo.saveAndFlush(firstPage);
				log.info("First Page document name changed to FirstPage.doc/FirstPage.docx");
			}


			renameStatus = true;
		} catch (Exception ex) {
			String message = "Error occured during renaming of Main Document/First Page files : " + ex.getMessage();
			log.error(message, ex);
			renameStatus = false;
			saveBundleTrackerDetails(manuscriptFileVo.getManuScriptId(), AppConstants.FAILED, message,manuscriptFileVo.getManuscriptVersion());
		}

		return renameStatus;
	}

	/**
	 * Not in use
	 */
	@Deprecated
	public boolean renameMainFirstDocument_old(ResubmissionManuscriptFileVo manuscriptFileVo) {
		boolean fileStatus;
		try {
			ManuScript manu = manuscriptRepo.getById(manuscriptFileVo.getManuScriptId());

			String filezipMakingdir = uploadFolder + File.separator;
			log.info("STEP 3 -> Create location");

			String fileuploaddirServer = BundlingUtils.generateServerFilePathPrefix(serverENV, serverIP, serverPort);

			log.info("STEP 4 -> Get main document location from manuscript file table ");

			// Change the name of main document if the file name is different
			ManuScriptFile manusCriptDocument = manuScriptFileRepo.getManuScriptFile(manuscriptFileVo.getManuScriptId(), manuscriptFileVo.getManuscriptVersion(), 1);
			log.info("STEP 5 -> Start changing name of main document");
			String serverfilePathMain = manusCriptDocument.getFilePath();
			String driveFilePathMain = serverfilePathMain.replace(fileuploaddirServer, filezipMakingdir);
			String fileName = driveFilePathMain;
			String basename = FilenameUtils.getBaseName(fileName);
			String extension = FilenameUtils.getExtension(fileName);
			if (extension.equalsIgnoreCase("doc")) {
				File renameMain = new File(driveFilePathMain);
				File nameMain = new File(renameMain.getParent() + File.separator + "Manuscript.doc");
				String fileServerPathMain = "";
				if (renameMain.renameTo(nameMain)) {
					manusCriptDocument.setFileName("Manuscript.doc");
					String dataTemp[] = serverfilePathMain.split("\\\\");
					int length = dataTemp.length;
					String dummy = dataTemp[length - 1].toString();
					serverfilePathMain = serverfilePathMain.replace(dummy, "Manuscript.doc");

					if (serverENV.equalsIgnoreCase("AWS")) {
						String ip = "https://" + serverIP;
						fileServerPathMain = ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "Manuscript.doc";
						manusCriptDocument.setFilePath(fileServerPathMain);

					} else if (serverENV.equalsIgnoreCase("LOCAL")) {
						manusCriptDocument.setFilePath(serverfilePathMain);

					} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + serverIP + ":"
								+ serverPort;
						fileServerPathMain = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "Manuscript.doc";
						manusCriptDocument.setFilePath(fileServerPathMain);
					}

					log.info("Document Name Changed As a Manuscript.doc");
					manuScriptFileRepo.save(manusCriptDocument);
					log.info("STEP 6 -> main document name changed has Manuscript.doc");
				}
			} else {
				File rename = new File(driveFilePathMain);
				File name = new File(rename.getParent() + File.separator + "Manuscript.docx");
				if (rename.renameTo(name)) {
					manusCriptDocument.setFileName("Manuscript.docx");
					String dataTemp[] = serverfilePathMain.split("\\\\");
					int length = dataTemp.length;
					String dummy = dataTemp[length - 1].toString();
					serverfilePathMain = serverfilePathMain.replace(dummy, "Manuscript.docx");
					String fileServerPath = "";

					if (serverENV.equalsIgnoreCase("AWS")) {
						String Ip = "https://" + serverIP;
						fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "Manuscript.docx";
						manusCriptDocument.setFilePath(fileServerPath);

					} else if (serverENV.equalsIgnoreCase("LOCAL")) {
						manusCriptDocument.setFilePath(serverfilePathMain);

					} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + serverIP + ":"
								+ serverPort;
						fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "Manuscript.docx";
						manusCriptDocument.setFilePath(fileServerPath);
					}

					log.info("Document Name Changed As a Manuscript.docx");
					manuScriptFileRepo.save(manusCriptDocument);

					log.info("STEP 6 -> main document name changed has Manuscript.dox");
				}
			}

			log.info("STEP 7 -> Get First page location from manuscript file table");
			// Change The Name OF First page
			ManuScriptFile manuScriptFirstPage = manuScriptFileRepo.getManuScriptFirstPageFile(manuscriptFileVo.getManuScriptId(), manuscriptFileVo.getManuscriptVersion(), 1);

			String manuScriptFirstPageserverfilePath = manuScriptFirstPage.getFilePath();
			String manuScriptFirstPagedriveFilePath = manuScriptFirstPageserverfilePath.replace(fileuploaddirServer, filezipMakingdir);
			// String[] array = manuScriptFirstPagedriveFilePath.split("\\.");
			// int len = array.length;
			// String extension = array[len - 1];
			String fileNameFirstPage = manuScriptFirstPagedriveFilePath;
			String basenameFirstPage = FilenameUtils.getBaseName(fileNameFirstPage);
			String extensionFirstPage = FilenameUtils.getExtension(fileNameFirstPage);
			if (extensionFirstPage.equalsIgnoreCase("doc")) {
				File renameFirstPage = new File(manuScriptFirstPagedriveFilePath);
				File nameFirstPage = new File(renameFirstPage.getParent() + File.separator + "FirstPage.doc");
				if (renameFirstPage.renameTo(nameFirstPage)) {
					manuScriptFirstPage.setFileName("FirstPage.doc");
					String dataTemp[] = manuScriptFirstPageserverfilePath.split("\\\\");
					int length = dataTemp.length;
					String dummy = dataTemp[length - 1].toString();

					manuScriptFirstPageserverfilePath = manuScriptFirstPageserverfilePath.replace(dummy, "FirstPage.doc");
					String fileServerPath = "";

					if (serverENV.equalsIgnoreCase("AWS")) {
						String Ip = "https://" + serverIP;
						fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "FirstPage.doc";
						manuScriptFirstPage.setFilePath(fileServerPath);

					} else if (serverENV.equalsIgnoreCase("LOCAL")) {
						manuScriptFirstPage.setFilePath(manuScriptFirstPageserverfilePath);

					} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + serverIP + ":"
								+ serverPort;
						fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "FirstPage.doc";
						manuScriptFirstPage.setFilePath(fileServerPath);
					}

					log.info("Document Name Changed As a FirstPage.doc");
					manuScriptFileRepo.save(manuScriptFirstPage);

					log.info("STEP 8 ->  First page name has been changed FirstPage.doc");
				}
			} else {
				File renamenameFirstPage = new File(manuScriptFirstPagedriveFilePath);
				File namenameFirstPage = new File(renamenameFirstPage.getParent() + File.separator + "FirstPage.docx");
				if (renamenameFirstPage.renameTo(namenameFirstPage)) {
					manuScriptFirstPage.setFileName("FirstPage.docx");
					String dataTemp[] = manuScriptFirstPageserverfilePath.split("\\\\");
					int length = dataTemp.length;
					String dummy = dataTemp[length - 1].toString();
					manuScriptFirstPageserverfilePath = manuScriptFirstPageserverfilePath.replace(dummy, "FirstPage.docx");
					String fileServerPath = "";

					if (serverENV.equalsIgnoreCase("AWS")) {
						String Ip = "https://" + serverIP;
						fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "FirstPage.docx";
						manuScriptFirstPage.setFilePath(fileServerPath);

					} else if (serverENV.equalsIgnoreCase("LOCAL")) {
						manuScriptFirstPage.setFilePath(manuScriptFirstPageserverfilePath);

					} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
						String Ip = "https://" + serverIP + ":"
								+ serverPort;
						fileServerPath = Ip + File.separator + "UniprrFileFolder" + File.separator
								+ "MNT_LWW_JOURNAL_" + "R" + manuscriptFileVo.getManuscriptVersion()
								+ "_" + manu.getJournalId() + "_" + manuscriptFileVo.getManuScriptId()
								+ File.separator + "FirstPage.docx";
						manuScriptFirstPage.setFilePath(fileServerPath);
					}

					log.info("Document Name Changed As a FirstPage.docx");
					manuScriptFileRepo.save(manuScriptFirstPage);
					log.info("STEP 8 ->  First page name has been changed FirstPage.docx");
				}
			}

			fileStatus = true;

		} catch (Exception ex) {
			String message = "Error occured during renaming of Main Document/First Page files : " + ex.getMessage();
			log.error(message, ex);
			fileStatus = false;
			saveBundleTrackerDetails(manuscriptFileVo.getManuScriptId(), AppConstants.FAILED ,message,manuscriptFileVo.getManuscriptVersion());
		}

		return fileStatus;

	}

	@Override
	public boolean generatingBundlingFile(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, boolean update) {
		boolean bundlingFileStatus = true;
		int manuscriptId = resubmissionManuscriptFileVo.getManuScriptId();
		int manuscriptVersion = resubmissionManuscriptFileVo.getManuscriptVersion();

		try {
			ManuScript manuscript = manuscriptRepo.getById(manuscriptId);
			BundlingUtils.deleteFolderPDFForImage(resubmissionManuscriptFileVo, manuscript, uploadFolder);
			TreeMap<String , String> figureFiles = new TreeMap<>();
			List<ManuScriptFile>  manuscriptFile = manuScriptFileRepo.getDataByManuscriptIdAndVersion(manuscriptId, manuscriptVersion);
			manuscriptFile.stream().forEach(manuFile -> {
				if (manuFile.getFileName() != null && StringUtils.isNotBlank(manuFile.getFileType())) {

					if (StringUtils.isNotBlank(manuFile.getFileType()) && manuFile.getFileType().equals("Figures")) {
						log.info("manu file name   :  "+manuFile.getFileName());
						figureFiles.put(manuFile.getFileName(), manuFile.getLegend());
					}


				}
			});
			figuretextlegend(figureFiles ,manuscriptId,manuscriptVersion,manuscript.getJournalId());

			//Start call of new bundling API
			log.info("Bundling Rest API called for manuscriptId : " + manuscriptId);

			CompletableFuture.supplyAsync(() -> {
				String result = "";
				String jsonResultString = "";
				StringBuilder errorMessage = new StringBuilder();
				if(enableJavaBundling) {
					try {
						HttpServiceRequestInfo requestInfo = BundlingUtils.prepareHttpServiceRequestInfo(bundlingAPIUrl, manuscriptId, manuscriptVersion);
						jsonResultString = httpService.callGETService(requestInfo);
						log.info("Java jsonResultString : " + jsonResultString);
						JSONParser parser = new JSONParser();
						JSONObject jsonObject = (JSONObject) parser.parse(jsonResultString);
						result = (String) jsonObject.get("status");
					} catch (ParseException e) {
						errorMessage.append("Java/dotNet bundling failed");
						result = "Failed";
						log.error("Failed to parse bundling API response for manuscriptId : " + manuscriptId , e);
					}
				}

				if(enablePythonBundling && !"Success".equalsIgnoreCase(result)) {
					String manuscriptFolderPath = BundlingUtils.prepareManuscriptBasePath(uniprrFolderPath, manuscriptVersion, manuscript.getJournalId(), manuscriptId);
					List<String> fileTypes = Arrays.asList("First Page file/Cover Letter","Main Documents/Article File","Comments to Referees","Tables","Figures");

					List<ManuScriptFile> fileNameList = manuScriptFileRepo.getActiveManuscriptFiles(manuscriptId, manuscriptVersion, fileTypes);
					List<String> fileNames = fileNameList.stream().map(ManuScriptFile::getFileName).collect(Collectors.toList());
					HttpServiceRequestInfo requestInfo = BundlingUtils.prepareBundlingHttpServiceRequestInfo(pythonBundlingAPIUrl, manuscriptFolderPath, fileNames);
					jsonResultString = httpService.callJsonPOSTService(requestInfo);
					log.info("Python jsonResultString : " + jsonResultString);
					JSONParser parser = new JSONParser();

					try {
						JSONObject jsonObject = (JSONObject) parser.parse(jsonResultString);
						result = (String) jsonObject.get("status");
					} catch (ParseException e) {
						errorMessage.append("---Python bundling failed");
						result = "Failed";
						log.error("Failed to parse bundling API response for manuscriptId : " + manuscriptId , e);
					}

					if("Success".equalsIgnoreCase(result)) {
						bundleTrackerRepo.updateFailedbudlefileStatus("Success", manuscriptId);
						log.info("Python bundling updated failed status for manuscriptId : " + manuscriptId);
					} else {
						result = errorMessage.toString();
					}
				}
				log.info("Bundling Rest API result - manuscriptId, result : " + manuscriptId + ", " + result);
				return result;
			}).thenAccept(result -> {
				if ("Success".equalsIgnoreCase(result)) {
					saveBundlefileInfo(resubmissionManuscriptFileVo, manuscript);
				} else {
					saveBundleTrackerDetails(manuscriptId, AppConstants.FAILED, result,resubmissionManuscriptFileVo.getManuscriptVersion());

					if (update) {
						List<ManuScriptFile> bundleFileList = manuScriptFileRepo.bundleFileList(resubmissionManuscriptFileVo.getManuScriptId(), resubmissionManuscriptFileVo.getManuscriptVersion());
						for (ManuScriptFile manuScriptFile : bundleFileList) {
							manuScriptFileRepo.bundleFileStatusUpdate(manuScriptFile.getManuscriptFileId());
						}
					}
				}
			});
		} catch (Exception ex) {
			bundlingFileStatus = false;
			String message = "Error occured while updating bundled file details in DB : " + ex.getMessage();
			log.error(message,  ex);
			saveBundleTrackerDetails(manuscriptId, AppConstants.FAILED,  message,resubmissionManuscriptFileVo.getManuscriptVersion());
		}
		return bundlingFileStatus;
	}

	private void saveBundlefileInfo(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, ManuScript manuscript) {
		int manuscriptId = resubmissionManuscriptFileVo.getManuScriptId();
		int manuscriptVersion = resubmissionManuscriptFileVo.getManuscriptVersion();
		log.info("saveBundlefileInfo start for manuscriptId : " + manuscriptId + ", manuscriptVersion : " + manuscriptVersion);

		String manuscriptFolderPath = BundlingUtils.generateManuscriptFolderPath(resubmissionManuscriptFileVo, manuscript.getJournalId(), uniprrFolderPath);
		List<ManuScriptFile> manuscriptFiles = new ArrayList<>();

		// PDF FILE DATA SAVE IN MANUSCRIPT FILE
		String pdfPath = BundlingUtils.generateBundledFilePath(resubmissionManuscriptFileVo, manuscript, "Bundled.pdf", serverENV, serverIP, serverPort);
		ManuScriptFile manuScriptFileForPdf = manuScriptFileRepo.getManuScriptFileDataPDf(manuscriptId, manuscriptVersion);
		if (manuScriptFileForPdf == null) {
			log.info("Create file details DB : Bundled.pdf");
			manuScriptFileForPdf = new ManuScriptFile();
			manuScriptFileForPdf.setManuScriptId(manuscriptId);
		}
		manuScriptFileForPdf.setCreatedOn(new Date());
		manuScriptFileForPdf.setFileType("PDF");
		manuScriptFileForPdf.setFileName("Bundled.pdf");
		manuScriptFileForPdf.setFilePath(pdfPath);
		boolean isPdfPathExits =  FileCheck.isfileAvail(pdfPath);
		if(!isPdfPathExits) {
			log.info("Bundled.pdf is not availble for manuscript : " + manuscriptId);
			manuScriptFileForPdf.setIsActive(0);
		}else {
			manuScriptFileForPdf.setIsActive(1);
		}
		manuScriptFileForPdf.setFileActive(1);
		Path bundlePdfPath = Paths.get(manuscriptFolderPath, "output", "Bundled.pdf");
		int fileSize = (int) FileUtil.getFileSize(bundlePdfPath);
		manuScriptFileForPdf.setFileSize(fileSize);
		manuScriptFileForPdf.setFileVersion(manuscriptVersion);
		if(StringUtils.isNotBlank(resubmissionManuscriptFileVo.getSubmissionType())) {
			manuScriptFileForPdf.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
		}
		manuscriptFiles.add(manuScriptFileForPdf);
//		manuScriptFileRepo.save(manuScriptFileForPdf);


		// PDF FILE For Reviewer DATA SAVE IN MANUSCRIPT FILE
		String pdfPathForReviewer = BundlingUtils.generateBundledFilePath(resubmissionManuscriptFileVo, manuscript, "BundledPdfForReviewer.pdf", serverENV, serverIP, serverPort);
		ManuScriptFile manuScriptFileForPdfForReviewer = manuScriptFileRepo.getManuScriptFileDataPDfForReviewer(manuscriptId, manuscriptVersion);
		if (manuScriptFileForPdfForReviewer == null) {
			log.info("Create file details DB : BundledPdfForReviewer.pdf");
			manuScriptFileForPdfForReviewer = new ManuScriptFile();
			manuScriptFileForPdfForReviewer.setManuScriptId(manuscriptId);
		}
		manuScriptFileForPdfForReviewer.setCreatedOn(new Date());
		manuScriptFileForPdfForReviewer.setFileType("REVIEWERPDF");
		manuScriptFileForPdfForReviewer.setFileName("BundledPdfForReviewer.pdf");
		manuScriptFileForPdfForReviewer.setFilePath(pdfPathForReviewer);
		boolean ispdfPathForReviewerExits =  FileCheck.isfileAvail(pdfPathForReviewer);
		if(!ispdfPathForReviewerExits) {
			log.info("BundledPdfForReviewer.pdf is not availble for manuscript : " + manuscriptId);
			manuScriptFileForPdfForReviewer.setIsActive(0);
		}else {
			manuScriptFileForPdfForReviewer.setIsActive(1);
		}
		manuScriptFileForPdfForReviewer.setFileActive(1);
		bundlePdfPath = Paths.get(manuscriptFolderPath, "output", "BundledPdfForReviewer.pdf");
		fileSize = (int) FileUtil.getFileSize(bundlePdfPath);
		manuScriptFileForPdfForReviewer.setFileSize(fileSize);
		manuScriptFileForPdfForReviewer.setFileVersion(manuscriptVersion);
		if(StringUtils.isNotBlank(resubmissionManuscriptFileVo.getSubmissionType())) {
			manuScriptFileForPdfForReviewer.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
		}
		manuscriptFiles.add(manuScriptFileForPdfForReviewer);
//		manuScriptFileRepo.save(manuScriptFileForPdfForReviewer);

		String bundledDocxFilePath = BundlingUtils.generateBundledFilePath(resubmissionManuscriptFileVo, manuscript, FINAL_DOC, serverENV, serverIP, serverPort);
		ManuScriptFile manuScriptFile = manuScriptFileRepo.getManuScriptFileData(manuscriptId, manuscriptVersion);
		if (manuScriptFile == null) {
			log.info("Create file details DB : " + FINAL_DOC);
			manuScriptFile = new ManuScriptFile();
			manuScriptFile.setManuScriptId(manuscriptId);
		}
		manuScriptFile.setCreatedOn(new Date());
		manuScriptFile.setFileType(MERGED_FILE);
		manuScriptFile.setFileName(FINAL_DOC);
		manuScriptFile.setFilePath(bundledDocxFilePath);
		boolean isbundledDocxFilePathExits =  FileCheck.isfileAvail(bundledDocxFilePath);
		if(!isbundledDocxFilePathExits) {
			log.info("Bundled.docx is not availble for manuscript : " + manuscriptId);
			manuScriptFile.setIsActive(0);
		}else {
			manuScriptFile.setIsActive(1);
		}
		manuScriptFile.setFileActive(1);
		bundlePdfPath = Paths.get(manuscriptFolderPath, "output", FINAL_DOC);
		fileSize = (int) FileUtil.getFileSize(bundlePdfPath);
		manuScriptFile.setFileSize(fileSize);
		manuScriptFile.setFileVersion(manuscriptVersion);
		if(StringUtils.isNotBlank(resubmissionManuscriptFileVo.getSubmissionType())) {
			manuScriptFile.setFileStatus(resubmissionManuscriptFileVo.getSubmissionType());
		}
		manuscriptFiles.add(manuScriptFile);
//		manuScriptFileRepo.save(manuScriptFile);



		if(!manuscriptFiles.isEmpty()) {
			manuScriptFileRepo.saveAll(manuscriptFiles);
			log.info("Saved manuscript files in DB for manuscript : " + manuscriptId);
		}
		if(!isPdfPathExits) {
			BundleTracker bundleTracker = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);
			bundleTracker.setBundleStatus("In Progress");
			bundleTrackerRepo.save(bundleTracker);
			log.info("Failed manuscript because bundle files is not availble on server for manuscript : " + manuscriptId);
		}
	}

	/**
	 * Not in use
	 */
	@Override
	@Deprecated
	public String generatingBundleForFailed(Integer manuscriptId) {
		String bundlingFileStatus;
		ManuScript manu = manuscriptRepo.getById(manuscriptId);

		try {

			String fileuploaddirServer = null;

			if (serverENV.equalsIgnoreCase("AWS")) {

				fileuploaddirServer = "https://" + serverIP;
				log.info("fileuploaddirServer  Location  For AWS :---------------->>>>>>>" + fileuploaddirServer);

			}

			// Bundaling File

			String myPath = null;
			log.info("STEP 1-> ##### Making Bundaling file location for failed Bundel article by grenratePdfFileForFailed. #####");
			if (serverENV.equalsIgnoreCase("AWS")) {
				myPath = File.separator + File.separator + serverIP + File.separator
						+ "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ manu.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ manu.getManuScriptId();
				log.info("TestMergeDocx  Location  For AWS :------------------->>>>>>>>>>>>>" + fileuploaddirServer);
			}

			log.info("**************---------------BUNDALING CALLING---------------------------*************");
			log.info("STEP 2 ->**** Get all the file list from manuscript file table for failed Bundel article by grenratePdfFileForFailed. ***");
			List<ManuScriptFile> getData = manuScriptFileRepo.getDataByManuscriptIdAndVersion(manu.getManuScriptId(), manu.getManuscriptVersion());
			log.info("------File path For Bundaling------>>>>>>>>" + myPath);
			File file = new File(myPath);
			TestMergeDocx addTestMergeDocx = new TestMergeDocx(null);
			log.info("STEP 3 ->%%%%%% Going to generate bundel PDF & DOCCX file for Author, EIC, & Reviewer. %%%%%%%%");
			// generating budling.pdf & budling.doc
			try {
				addTestMergeDocx.mergeTextFiles(file, getData, cdnUrl, uniprrFolderPath);

			} catch (Exception ex) {
				String message = "Exception generated during generating bundle PDF : " + ex.getMessage();
				log.error(message, ex);
				saveBundleTrackerDetails(manuscriptId, AppConstants.FAILED, message);
				return "Failed";
			}

			// File SEtup
			ManuscriptFilePreview manuscriptFilePreview = manuscriptFilePreviewRepo.getManuscriptFilePreviewDetails(manu.getManuScriptId(), manu.getManuscriptVersion());

			String xhtmlPath = null;
			String pdfPath = null;
			String pdfPathForReviewer = null;
			String bundaledFilePath = null;
			// String docPathForReviewer = null;
			if (serverENV.equalsIgnoreCase("AWS")) {
				String ip = "https://" + serverIP;
				xhtmlPath = ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ manu.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ manu.getManuScriptId() + File.separator + "output"
						+ File.separator + "Bundled.html";
				log.info("xhtmlPath  Location  For AWS :------------->>>>>>>>>>" + xhtmlPath);

				pdfPath = ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
						+ manu.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
						+ manu.getManuScriptId() + File.separator + "output"
						+ File.separator + "Bundled.pdf";

				pdfPathForReviewer = ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
						+ "R" + manu.getManuscriptVersion() + "_" + manu.getJournalId()
						+ "_" + manu.getManuScriptId() + File.separator + "output"
						+ File.separator + "BundledPdfForReviewer.pdf";

				bundaledFilePath = ip + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_"
						+ "R" + manu.getManuscriptVersion() + "_" + manu.getJournalId()
						+ "_" + manu.getManuScriptId() + File.separator + "output"
						+ File.separator + "Bundled.docx";

				log.info("PDF  Location  For AWS :----------------->>>>>>>>>>>>>>" + xhtmlPath);
			}
			log.info("STEP 4 -> ^^^^^^ Going to update in ManuscriptFilePreview Table. ^^^^^^^^^^");
			if (manuscriptFilePreview != null) {
				log.info("update in ManuscriptFilePreview Table");
				manuscriptFilePreview.setManuscriptXhtmlPath(xhtmlPath);
				manuscriptFilePreview.setManuscriptXmlpath(xhtmlPath);
				manuscriptFilePreviewRepo.save(manuscriptFilePreview);
			} else {

				ManuscriptFilePreview newManuscriptFilePreview = new ManuscriptFilePreview();
				newManuscriptFilePreview.setManuscriptId(manu.getManuScriptId());
				newManuscriptFilePreview.setManuscriptVersion(manu.getManuscriptVersion());
				newManuscriptFilePreview.setManuscriptXhtmlPath(xhtmlPath);
				newManuscriptFilePreview.setManuscriptXmlpath(xhtmlPath);
				manuscriptFilePreviewRepo.save(newManuscriptFilePreview);
			}

			// PDF FILE DATA SAVE IN MANUSCRIPT FILE

			ManuScriptFile manuScriptFileForPdf = manuScriptFileRepo.getManuScriptFileDataPDf(manu.getManuScriptId(), manu.getManuscriptVersion());
			log.info("STEP 5 -> $$$$$$$ Going to  Update in manuScriptFile Table for Bundled.pdf . $$$$$$$");

			if (manuScriptFileForPdf != null) {
				manuScriptFileForPdf.setFileType("PDF");
				manuScriptFileForPdf.setFileName("Bundled.pdf");
				manuScriptFileForPdf.setFilePath(pdfPath);
				manuScriptFileForPdf.setIsActive(1);
				manuScriptFileForPdf.setFileActive(1);
				manuScriptFileForPdf.setFileSize(20);
				manuScriptFileForPdf.setFileVersion(manu.getManuscriptVersion());
				manuScriptFileForPdf.setFileStatus(getData.get(0).getFileStatus());
				manuScriptFileRepo.save(manuScriptFileForPdf);
			} else {

				ManuScriptFile manuScriptFileForPdfCreate = new ManuScriptFile();
				manuScriptFileForPdfCreate.setManuScriptId(manu.getManuScriptId());
				manuScriptFileForPdfCreate.setFileType("PDF");
				manuScriptFileForPdfCreate.setFileName("Bundled.pdf");
				manuScriptFileForPdfCreate.setFilePath(pdfPath);
				manuScriptFileForPdfCreate.setIsActive(1);
				manuScriptFileForPdfCreate.setFileActive(1);
				manuScriptFileForPdfCreate.setFileSize(20);
				manuScriptFileForPdfCreate.setFileVersion(manu.getManuscriptVersion());
				manuScriptFileForPdfCreate.setFileStatus(getData.get(0).getFileStatus());
				manuScriptFileRepo.save(manuScriptFileForPdfCreate);
			}

			// PDF FILE For Reviewer DATA SAVE IN MANUSCRIPT FILE

			ManuScriptFile manuScriptFileForPdfForReviewer = manuScriptFileRepo.getManuScriptFileDataPDfForReviewer(manu.getManuScriptId(), manu.getManuscriptVersion());
			log.info("STEP 6 -> ####### Going to  Update in manuScriptFile Table for BundledPdfForReviewer.pdf . #########");
			// Update in manuScriptFile Table
			if (manuScriptFileForPdfForReviewer != null) {
				log.info("Update in manuScriptFile Table");
				manuScriptFileForPdfForReviewer.setFileType("REVIEWERPDF");
				manuScriptFileForPdfForReviewer.setFileName("BundledPdfForReviewer.pdf");
				manuScriptFileForPdfForReviewer.setFilePath(pdfPathForReviewer);
				manuScriptFileForPdfForReviewer.setIsActive(1);
				manuScriptFileForPdfForReviewer.setFileActive(1);
				manuScriptFileForPdfForReviewer.setFileSize(20);
				manuScriptFileForPdfForReviewer.setFileVersion(manu.getManuscriptVersion());
				manuScriptFileForPdfForReviewer.setFileStatus(getData.get(0).getFileStatus());
				manuScriptFileRepo.save(manuScriptFileForPdfForReviewer);
			} else {
				ManuScriptFile manuScriptFileForPdfCreate = new ManuScriptFile();
				manuScriptFileForPdfCreate.setManuScriptId(manu.getManuScriptId());
				manuScriptFileForPdfCreate.setFileType("REVIEWERPDF");
				manuScriptFileForPdfCreate.setFileName("BundledPdfForReviewer.pdf");
				manuScriptFileForPdfCreate.setFilePath(pdfPathForReviewer);
				manuScriptFileForPdfCreate.setIsActive(1);
				manuScriptFileForPdfCreate.setFileActive(1);
				manuScriptFileForPdfCreate.setFileSize(20);
				manuScriptFileForPdfCreate.setFileVersion(manu.getManuscriptVersion());
				manuScriptFileForPdfCreate.setFileStatus(getData.get(0).getFileStatus());
				manuScriptFileRepo.save(manuScriptFileForPdfCreate);
			}

			ManuScriptFile manuScriptFile = manuScriptFileRepo.getManuScriptFileData(manu.getManuScriptId(), manu.getManuscriptVersion());

			log.info("STEP 7 -> ******* Going to  Update in manuScriptFile Table for Bundled.docx. *******");
			if (manuScriptFile != null) {
				log.info("Update in manuScriptFile Table");
				manuScriptFile.setFileType(MERGED_FILE);
				manuScriptFile.setFileName(FINAL_DOC);
				manuScriptFile.setFilePath(bundaledFilePath);
				manuScriptFile.setIsActive(1);
				manuScriptFile.setFileSize(20);
				manuScriptFile.setFileActive(1);
				manuScriptFile.setFileVersion(manu.getManuscriptVersion());
				manuScriptFile.setFileStatus(getData.get(0).getFileStatus());
				manuScriptFileRepo.save(manuScriptFile);
			} else {
				ManuScriptFile manuScriptFileCreate = new ManuScriptFile();
				manuScriptFileCreate.setManuScriptId(manu.getManuScriptId());
				manuScriptFileCreate.setFileType(MERGED_FILE);
				manuScriptFileCreate.setFileName(FINAL_DOC);
				manuScriptFileCreate.setFilePath(bundaledFilePath);
				manuScriptFileCreate.setIsActive(1);
				manuScriptFileCreate.setFileActive(1);
				manuScriptFileCreate.setFileSize(20);
				manuScriptFileCreate.setFileVersion(manu.getManuscriptVersion());
				manuScriptFileCreate.setFileStatus(getData.get(0).getFileStatus());
				manuScriptFileRepo.save(manuScriptFileCreate);
			}
			bundlingFileStatus = "Success";
		} catch (Exception ex) {
			log.error("Exception generated during genering Bundel.PDF : " + ex.getMessage(), ex);
			saveBundleTrackerDetails(manuscriptId, AppConstants.FAILED, ex.getMessage());
			bundlingFileStatus = "Failed";
		}
		return bundlingFileStatus;
	}

	@Override
	@Transactional
	public void updateCheckListResponse(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo) {
		ManuScript manuUpdate = manuscriptRepo.getById(resubmissionManuscriptFileVo.getManuScriptId());
		log.info("********************** updateCheckListResponse method  ******************** ");
		log.info(" ManuScriptId :: {}",resubmissionManuscriptFileVo.getManuScriptId());
		manuUpdate.setManuscriptChecklistResponse(resubmissionManuscriptFileVo.getManuscriptChecklistResponse());
		log.info(" manuscriptChecklistResponse :: {}",manuUpdate.getManuscriptChecklistResponse());
		manuscriptRepo.saveAndFlush(manuUpdate);
		log.info("********************** END updateCheckListResponse method  ******************** ");
	}

	@Override
	@Transactional
	public String insertFiledBundleManuscript(Integer manuscriptId, String bundleStatus, String message,Integer manuscriptVersion) {
		String status = "";
		try {

			saveBundleTrackerDetails(manuscriptId, bundleStatus, message,manuscriptVersion);

			ManuScript manuScript = manuscriptRepo.getById(manuscriptId);
			List<ManuScriptFile> bundleFileList = manuScriptFileRepo.bundleFileList(manuscriptId, manuScript.getManuscriptVersion());
			for (ManuScriptFile manuScriptFile : bundleFileList) {
				manuScriptFileRepo.bundleFileStatusUpdate(manuScriptFile.getManuscriptFileId());
			}
			status = "success";
		} catch (Exception ex1) {
			status = "Failed";
			log.error("Exception generated during inserting failed bundle in Database : " + ex1.getMessage(), ex1);

		}

		return status;
	}

	@Override
	@Transactional
	public String insertFiledBundleManuscript(Integer manuscriptId, String bundleStatus, String message) {
		String status = "";
		try {

			saveBundleTrackerDetails(manuscriptId, bundleStatus, message);

			ManuScript manuScript = manuscriptRepo.getById(manuscriptId);
			List<ManuScriptFile> bundleFileList = manuScriptFileRepo.bundleFileList(manuscriptId, manuScript.getManuscriptVersion());
			for (ManuScriptFile manuScriptFile : bundleFileList) {
				manuScriptFileRepo.bundleFileStatusUpdate(manuScriptFile.getManuscriptFileId());
			}
			status = "success";
		} catch (Exception ex1) {
			status = "Failed";
			log.error("Exception generated during inserting failed bundle in Database : " + ex1.getMessage(), ex1);

		}

		return status;
	}

	@Override
	public void saveBundleTrackerDetails(Integer manuscriptId, String bundleStatus, String errorMessage,Integer manuscriptVersion) {
		log.info("*************************  saveBundleTrackerDetails  ********************* ");

		BundleTracker bundleTracker = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);

		if (bundleTracker == null) {
			bundleTracker = new BundleTracker();
			bundleTracker.setManuscriptId(manuscriptId);
		}else {
			log.info("Failed Manuscript Id :: {}"+manuscriptId);
		}
		bundleTracker.setBundleDate(new Date());
		bundleTracker.setBundleStatus(bundleStatus);
		bundleTracker.setBundleErrorMessage(errorMessage);
		bundleTracker.setManuscriptVersion(manuscriptVersion);
		bundleTrackerRepo.save(bundleTracker);

		log.info("*************************  END saveBundleTrackerDetails  ********************* ");
	}

	@Override
	public void saveBundleTrackerDetails(Integer manuscriptId, String bundleStatus, String errorMessage) {
		log.info("*************************  saveBundleTrackerDetails  ********************* ");

		BundleTracker bundleTracker = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);

		if (bundleTracker == null) {
			bundleTracker = new BundleTracker();
			bundleTracker.setManuscriptId(manuscriptId);
		}else {
			log.info("Failed Manuscript Id :: {}"+manuscriptId);
		}
		bundleTracker.setBundleDate(new Date());
		bundleTracker.setBundleStatus(bundleStatus);
		bundleTracker.setBundleErrorMessage(errorMessage);
		bundleTrackerRepo.save(bundleTracker);

		log.info("*************************  END saveBundleTrackerDetails  ********************* ");
	}

	@Override
	public String doPythonBundling(int manuscriptId, int manuscriptVersion) {
		String result = "";
		ManuScript manuscript = manuscriptRepo.getById(manuscriptId);
		try {
			String manuscriptFolderPath = BundlingUtils.prepareManuscriptBasePath(uniprrFolderPath, manuscriptVersion, manuscript.getJournalId(), manuscriptId);
			List<String> fileTypes = Arrays.asList("First Page file/Cover Letter","Main Documents/Article File","Comments to Referees","Tables","Figures");

			List<ManuScriptFile> fileNameList = manuScriptFileRepo.getActiveManuscriptFiles(manuscriptId, manuscriptVersion, fileTypes);
			List<String> fileNames = fileNameList.stream().map(ManuScriptFile::getFileName).collect(Collectors.toList());
			HttpServiceRequestInfo requestInfo = BundlingUtils.prepareBundlingHttpServiceRequestInfo(pythonBundlingAPIUrl, manuscriptFolderPath, fileNames);
			String jsonResultString = httpService.callJsonPOSTService(requestInfo);
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonResultString);
			result = (String) jsonObject.get("status");
		} catch (Exception e) {
			log.error("Exception generated during doPythonBundling : " + e.getMessage(), e);
			result = "Exception : " + e.getMessage();
		}

		return result;
	}
	public void figuretextlegend(TreeMap<String, String> figureFiles ,int manuScriptId, int manuscriptVersion,int journalId) {
		String ip = "https://" + serverIP;
		String mansucriptBasePath =uploadFolder + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
				+ manuscriptVersion + "_" + journalId + "_" + manuScriptId + File.separator + "figure.txt";;
		log.info("mansucriptBasePath : "+mansucriptBasePath);
		List<ManuScriptFile> manuScriptFiles = manuScriptFileRepo.getManuscriptByactive(manuScriptId, manuscriptVersion, 1);
		for(ManuScriptFile manuScriptFile:manuScriptFiles) {
			if(manuScriptFile.getFileType().equals("Figures")) {
				figureFiles.put(manuScriptFile.getFileName(), manuScriptFile.getLegend());
			}
		}
		File dir = new File(mansucriptBasePath).getParentFile();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(mansucriptBasePath))) {
			for (Map.Entry<String, String> entry : figureFiles.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				writer.write(key + "\t" + value + "\n");
			}
			BundlingUtils.generateFilePath(manuscriptVersion, journalId, manuScriptId, uploadFolder, dir.getName(), serverENV, serverIP, serverPort);
			log.info("legend written to " + mansucriptBasePath);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void figuretextlegendUpdate(String oldFileName,String newFileName,int manuScriptId, int manuscriptVersion,int journalId) {
		String ip = "https://" + serverIP;
		String mansucriptBasePath = uploadFolder + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
				+ manuscriptVersion + "_" + journalId + "_" + manuScriptId + File.separator + "figure.txt";
		log.info("mansucriptBasePath : "+mansucriptBasePath);
		Map<String, String> figureFiles = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(mansucriptBasePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\t");
				if (parts.length == 2) {
					figureFiles.put(parts[0], parts[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (figureFiles.containsKey(oldFileName)) {
			String value = figureFiles.get(oldFileName);
			figureFiles.remove(oldFileName);
			figureFiles.put(newFileName, value);
		}
		try (FileWriter writer = new FileWriter(mansucriptBasePath)) {
			for (Map.Entry<String, String> entry : figureFiles.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				writer.write(key + "\t" + value + "\n");
			}
			log.info(oldFileName + " updated to " + newFileName + " in " + mansucriptBasePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void backupFilesWithTimestamp(ResubmissionManuscriptFileVo vo) {
	    try {
	        Integer manuscriptId = vo.getManuScriptId();
	        Integer journalId = manuscriptRepo.getJournalId(manuscriptId); // fetch from DB
	        Integer version = vo.getManuscriptVersion();

	        String baseDir = "/var/www/download/UniprrFileFolder";
	        String folderName = "MNT_LWW_JOURNAL_R" + version + "_" + journalId + "_" + manuscriptId;
	        Path manuscriptDir = Paths.get(baseDir, folderName);

	        if (!Files.exists(manuscriptDir) || !Files.isDirectory(manuscriptDir)) {
	            log.info("Manuscript directory not found: {}", manuscriptDir);
	            return;
	        }

	        Path backupDir = manuscriptDir.resolve("Backup");
	        if (!Files.exists(backupDir)) {
	            Files.createDirectory(backupDir);
	        }

	        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

	        try (DirectoryStream<Path> stream = Files.newDirectoryStream(manuscriptDir, "*")) {
	            for (Path file : stream) {
	                if (Files.isRegularFile(file)) {
	                    String fileName = file.getFileName().toString();
	                    Path backupFile = backupDir.resolve(fileName + "_" + timestamp);
	                    Files.copy(file, backupFile, StandardCopyOption.REPLACE_EXISTING);
	                    log.info("Backup created: {}", backupFile);
	                }
	            }
	        }

	    } catch (Exception e) {
	        log.error("Error while creating backup for manuscript {}: {}", vo.getManuScriptId(), e.getMessage(), e);
	    }
	}



}
