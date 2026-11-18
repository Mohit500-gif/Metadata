package com.digi.uniprr.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONValue;

import com.digi.uniprr.VO.ResubmissionManuscriptFileVo;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import static java.io.File.separator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public final class BundlingUtils {

	private final static Logger logger = LoggerFactory.getLogger(BundlingUtils.class);

	private BundlingUtils() {
	}

	public static String prepareManuscriptBasePath(String pathPrefix, int manuscriptVersion, int journalId,
			int manuscriptId) {
		StringBuilder pathBuilder = new StringBuilder(pathPrefix);
		pathBuilder.append(File.separator).append("UniprrFileFolder").append(File.separator);
		pathBuilder.append("MNT_LWW_JOURNAL_R").append(manuscriptVersion).append("_").append(journalId).append("_")
				.append(manuscriptId);

		return pathBuilder.toString();
	}

	public static String prepareApiUrl(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, ManuScript manu,
			Integer pageNumber, String serverENV, String serverIP, String serverPort, String pythonAPIAWS,
			String pythonAPI, String staticServerPort) throws UnknownHostException {
		String url = "";

		String finalPath;
		if (serverENV.equalsIgnoreCase("AWS")) {
			logger.info("Inside in python call for aws");
			finalPath = "@@" + serverIP + "@" + "UniprrFileFolder" + "@" + "MNT_LWW_JOURNAL_" + "R"
					+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
					+ resubmissionManuscriptFileVo.getManuScriptId() + "@" + "Manuscript.docx";
			logger.info("Inside in python call for aws and final path:--------------->>>>>>>>>>>>>>" + finalPath);
			url = pythonAPIAWS + "/" + finalPath + "@" + pageNumber;
			logger.info("Inside in python call for aws python url :----------------->>>>>>>>>>>" + url);
		} else if (serverENV.equalsIgnoreCase("LOCAL")) {
			logger.info("Inside in python call for local");
			finalPath = "@@" + InetAddress.getLocalHost().getHostAddress() + ":" + staticServerPort + "@"
					+ "UniprrFileFolder" + "@" + "MNT_LWW_JOURNAL_" + "R"
					+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
					+ resubmissionManuscriptFileVo.getManuScriptId() + "@" + "Manuscript.docx";
			logger.info("Inside in python call for local and final path:------------------->>>>>>>>" + finalPath);
			url = pythonAPI + "/" + finalPath + "@" + pageNumber;
			logger.info("Inside in python call for local and url:--------------->>>>>>>>>>>>>" + url);
		} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
			logger.info("Inside in python call for local");
			finalPath = "@@" + serverIP + ":" + serverPort + "@" + "UniprrFileFolder" + "@" + "MNT_LWW_JOURNAL_" + "R"
					+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
					+ resubmissionManuscriptFileVo.getManuScriptId() + "@" + "Manuscript.docx";
			logger.info("Inside in python call for local and final path:------------------->>>>>>>>" + finalPath);
			url = pythonAPIAWS + "/" + finalPath + "@" + pageNumber;
			logger.info("Inside in python call for local and url:--------------->>>>>>>>>>>>>" + url);
		}
		return url;
	}

	public static String generateServerFilePathPrefix(String serverEnvironment, String serverIP, String serverPort) {
		String fileUrlPrefix = null;
		if (serverEnvironment.equalsIgnoreCase("AWS")) {
			fileUrlPrefix = "https://" + serverIP;
		} else if (serverEnvironment.equalsIgnoreCase("LOCAL")) {
			fileUrlPrefix = "http://" + serverIP + ":" + serverPort;
		} else if (serverEnvironment.equalsIgnoreCase("AWSDEMO")) {
			fileUrlPrefix = "https://" + serverIP + ":" + serverPort;
		}

		return fileUrlPrefix;
	}

	public static String generateManuscriptFolderPath(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo,
			int journalId, String uniprrFolderPath) {
		String manuscriptPath = uniprrFolderPath + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_" + "R"
				+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + journalId + "_"
				+ resubmissionManuscriptFileVo.getManuScriptId();
		logger.info("Method generateManuscriptFolderPath generated path : " + manuscriptPath);

		return manuscriptPath;
	}

	public static String generateFilePath(int manuscriptVersion, int journalId, int manuscriptId, String baseFolder,
			String fileName, String serverENV, String serverIP, String serverPort) {
		String fileServerPath = "";
		if (serverENV.equalsIgnoreCase("AWS")) {
			String ip = "https://" + serverIP;
			fileServerPath = ip + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_" + "R"
					+ manuscriptVersion + "_" + journalId + "_" + manuscriptId + separator + fileName;
		} else if (serverENV.equalsIgnoreCase("LOCAL")) {
			fileServerPath = baseFolder + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_" + "R"
					+ manuscriptVersion + "_" + journalId + "_" + manuscriptId + separator + fileName;
		} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
			String ip = "https://" + serverIP + ":" + serverPort;
			fileServerPath = ip + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_" + "R"
					+ manuscriptVersion + "_" + journalId + "_" + manuscriptId + separator + fileName;
		}

		logger.info("Server Path for AWSDEMO : " + fileServerPath);
		return fileServerPath;
	}

	public static String generateServerFilePath(int manuscriptVersion, int journalId, int manuscriptId, String filePath,
			String fileName, String serverENV, String serverIP, String serverPort) {
		String serverFilePath = "";
		if (serverENV.equalsIgnoreCase("AWS")) {
			String ip = "https://" + serverIP;
			serverFilePath = ip + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_" + "R"
					+ manuscriptVersion + "_" + journalId + "_" + manuscriptId + separator + fileName;
		} else if (serverENV.equalsIgnoreCase("LOCAL")) {
			String[] fileTokens = filePath.split("\\\\");
			int length = fileTokens.length;
			String fileNamePart = fileTokens[length - 1];
			serverFilePath = filePath.replace(fileNamePart, fileName);
		} else if (serverENV.equalsIgnoreCase("AWSDEMO")) {
			String ip = "https://" + serverIP + ":" + serverPort;
			serverFilePath = ip + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_" + "R"
					+ manuscriptVersion + "_" + journalId + "_" + manuscriptId + separator + fileName;
		}

		return serverFilePath;
	}

	public static String generateBundledFilePath(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo,
			ManuScript manu, String fileName, String serverENV, String serverIP, String serverPort) {
		String fileUrlPrefix = generateServerFilePathPrefix(serverENV, serverIP, serverPort);
		String filePath = fileUrlPrefix + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_" + "R"
				+ resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manu.getJournalId() + "_"
				+ resubmissionManuscriptFileVo.getManuScriptId() + separator + "output" + separator + fileName;

		logger.info("file location :" + filePath);

		return filePath;
	}

	public static void deleteFolderPDFForImage(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo,
			ManuScript manuscript, String uploadFolder) {
		int manuscriptId = manuscript.getManuScriptId();

		if (resubmissionManuscriptFileVo.getSubmissionType() != null
				&& !resubmissionManuscriptFileVo.getSubmissionType().isEmpty()
				&& resubmissionManuscriptFileVo.getSubmissionType().equalsIgnoreCase("TECHNICAL-MODIFICATION")) {
			logger.info(
					"-***************---------------------Before Calling Bundaling In Technical Modification Delete Folder PDFForImage AND PDF---------------------------*************-");
			String pdfFolderLocation = uploadFolder + separator + "UniprrFileFolder" + separator + "MNT_LWW_JOURNAL_"
					+ "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_" + manuscript.getJournalId() + "_"
					+ manuscriptId + separator + "PDF";
			logger.info("**********************Path For PDF Folder****************" + pdfFolderLocation);
			File dir = new File(pdfFolderLocation);
			if (dir.exists()) {
				for (File fileEntry : dir.listFiles()) {
					fileEntry.delete();
					logger.info("File Deleted");
				}

				logger.info("**********************Delete Folder PDF Successfully****************");
			}

			String pdfForImageFolderLocation = uploadFolder + separator + "UniprrFileFolder" + separator
					+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_"
					+ manuscript.getJournalId() + "_" + manuscriptId + separator + "PDFForImage";
			logger.info("**********************Path For PDFForImageFolderLocation Folder****************"
					+ pdfForImageFolderLocation);
			File dir1 = new File(pdfForImageFolderLocation);
			if (dir1.exists()) {
				for (File fileEntry : dir1.listFiles()) {
					fileEntry.delete();
					logger.info("File Deleted");
				}
				// dir1.deleteOnExit();
				logger.info("**********************Delete Folder PDFForImage Successfully****************");
			}
			
			String OutputFolderLocation = uploadFolder + separator + "UniprrFileFolder" + separator
					+ "MNT_LWW_JOURNAL_" + "R" + resubmissionManuscriptFileVo.getManuscriptVersion() + "_"
					+ manuscript.getJournalId() + "_" + manuscriptId + separator + "output";
			logger.info("**********************Path For OutputFolderLocation Folder****************"
					+ OutputFolderLocation);
			File dir2 = new File(OutputFolderLocation);
			if (dir2.exists() && dir2.isDirectory()) {
			    File[] files = dir2.listFiles();
			    if (files != null) {
			        for (File fileEntry : files) {
			            fileEntry.delete();
			            logger.info("File Deleted");
			        }
			        logger.info("**********************Delete Folder OutputFolder Successfully****************");
			    } else {
			        logger.warn("Directory is empty or cannot be read");
			    }
			} else {
			    logger.warn("Directory does not exist or is not a directory");
			}
		}
	}

	/**
	 * Rename the existing file to provided value of @param documentName
	 * 
	 * @throws IOException
	 * 
	 */
//	public static ManuScriptFile prepareManuscriptDocument(ManuScriptFile manuscriptDocument, String fileName,
//			String uploadFolder, String serverENV, String serverIP, String serverPort) throws IOException {
//		int manuscriptVersion = manuscriptDocument.getFileVersion();
//		int journalId = manuscriptDocument.getManuScript().getJournalId();
//		int manuscriptId = manuscriptDocument.getManuScriptId();
//
//		String basePath = uploadFolder + separator;
//		String envBaseURL = generateServerFilePathPrefix(serverENV, serverIP, serverPort);
//		String serverFilePathMain = manuscriptDocument.getFilePath();
//		String filePath = serverFilePathMain.replace(envBaseURL, basePath);
//
//		Path sourceFile = Paths.get(filePath);
//		Path targetPath = Paths.get(sourceFile.getParent() + separator + fileName);
//
//		targetPath = Files.move(sourceFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
//		if (targetPath != null) {
//			manuscriptDocument.setFileName(fileName);
//			String serverFilePath = generateServerFilePath(manuscriptVersion, journalId, manuscriptId, filePath,
//					fileName, serverENV, serverIP, serverPort);
//			manuscriptDocument.setFilePath(serverFilePath);
//		}
//
//		return manuscriptDocument;
//	}
	
	public static ManuScriptFile prepareManuscriptDocument(ManuScriptFile manuscriptDocument, String fileName,
	        String uploadFolder, String serverENV, String serverIP, String serverPort) throws IOException {

		logger.info("Starting prepareManuscriptDocument method...");

	    int manuscriptVersion = manuscriptDocument.getFileVersion();
	    int journalId = manuscriptDocument.getManuScript().getJournalId();
	    int manuscriptId = manuscriptDocument.getManuScriptId();

	    String basePath = uploadFolder + separator;
	    String envBaseURL = generateServerFilePathPrefix(serverENV, serverIP, serverPort);
	    String serverFilePathMain = manuscriptDocument.getFilePath();
	    String filePath = serverFilePathMain.replace(envBaseURL, basePath);

	    logger.info("Resolved source file path: " + filePath);

	    Path sourceFile = Paths.get(filePath);
	    Path targetPath = Paths.get(sourceFile.getParent() + separator + fileName);

	    if (!Files.exists(sourceFile)) {
	    	logger.error("Source file does not exist: " + sourceFile.toString());
	        throw new FileNotFoundException("Source file not found: " + sourceFile);
	    }

	    logger.info("Attempting to rename file to: " + targetPath);

	    try {
	        targetPath = Files.move(sourceFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
	        logger.info("File successfully renamed to: " + targetPath);
	    } catch (IOException e) {
	    	logger.error("Failed to move/rename file from " + sourceFile + " to " + targetPath);
	        e.printStackTrace();
	        throw e;
	    }

	    if (targetPath != null && Files.exists(targetPath)) {
	        manuscriptDocument.setFileName(fileName);
	        String serverFilePath = generateServerFilePath(manuscriptVersion, journalId, manuscriptId, filePath,
	                fileName, serverENV, serverIP, serverPort);
	        manuscriptDocument.setFilePath(serverFilePath);

	        logger.info("Updated manuscriptDocument with new file name and path.");
	    } else {
	    	logger.error("File rename operation failed. Target file not found: " + targetPath);
	    }

	    logger.info("prepareManuscriptDocument method completed.");

	    return manuscriptDocument;
	}


	public static String moveRenameOldFile(ManuScriptFile manuscriptFile, int journalId, String basePath)
			throws IOException {
		String drivePath = basePath + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
				+ manuscriptFile.getFileVersion() + "_" + journalId + "_" + manuscriptFile.getManuScriptId()
				+ File.separator + manuscriptFile.getFileName();

		Random rand = new Random();
		int i = rand.nextInt(1000000000);
		String fileName = manuscriptFile.getFileName();
		String basename = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		String finalNameFile = basename + i + "." + extension;

		File rename = new File(drivePath);
		if (rename.exists()) {
			File name = new File(rename.getParent() + File.separator + finalNameFile);
			if (rename.renameTo(name)) {
				logger.info("File Rename Success ------------------------->>>>>>>>>>");
			} else {
				logger.info("File Rename Failure ------------------------->>>>>>>>>>");
			}
		} else {
			File name = new File(rename.getParent() + File.separator + finalNameFile);
			name.createNewFile();
		}

		// Step 5. Setup For the file folder in the same Manuscript Folder
		String previousFolderPath = basePath + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R"
				+ manuscriptFile.getFileVersion() + "_" + journalId + "_" + manuscriptFile.getManuScriptId()
				+ File.separator + "PreviousFileFolder";
		logger.info("Previous File Folder Location------------------>" + previousFolderPath);

		// Create Folder In Respective Drive
		File dir = new File(previousFolderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Move File from current folder into New Created folder
		FileUtils.moveFileToDirectory(new File(rename.getParent() + File.separator + finalNameFile),
				new File(previousFolderPath), true);
		logger.info("File Moved Successfully in Previous File Folder Location-------------------------");

		return finalNameFile;
	}

	public static HttpServiceRequestInfo prepareHttpServiceRequestInfo(String apiURL, int manuscriptId,
			int manuscriptVersion) {
		HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
		requestInfo.setRequestUri(apiURL);
		requestInfo.setCharset(StandardCharsets.UTF_8.name());
		requestInfo.setMethodType(AppConstants.GET);

		List<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("manuscriptId", String.valueOf(manuscriptId)));
		nameValuePairs.add(new BasicNameValuePair("manuscriptVersion", String.valueOf(manuscriptVersion)));
		requestInfo.setQueryStringParameters(nameValuePairs);

		return requestInfo;
	}

	public static HttpServiceRequestInfo prepareBundlingHttpServiceRequestInfo(String apiURL, String pathPrefix,
			List<String> fileNames) {
		HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
		requestInfo.setRequestUri(apiURL);
		requestInfo.setCharset(StandardCharsets.UTF_8.name());
		requestInfo.setMethodType(AppConstants.POST);

		String requestBody = prepareBundlingRequestBody(pathPrefix, fileNames);
		requestInfo.setRequestBody(requestBody);

		return requestInfo;
	}

	public static String prepareBundlingRequestBody(String pathPrefix, List<String> fileNames) {
		StringBuilder jsonStringBuilder = new StringBuilder();
		jsonStringBuilder.append("{");
		jsonStringBuilder.append("\"path\":");
		jsonStringBuilder.append(JSONValue.toJSONString(pathPrefix));
		jsonStringBuilder.append(",\"files\":");
		String fileNameJson = JSONValue.toJSONString(fileNames);
		jsonStringBuilder.append(fileNameJson);
		jsonStringBuilder.append("}");
		return jsonStringBuilder.toString();
	}

}
