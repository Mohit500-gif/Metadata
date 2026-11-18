package com.digi.uniprr.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.BundleStatusResponse;
import com.digi.uniprr.model.BundleTracker;
import com.digi.uniprr.model.ManuScriptFile;
import com.digi.uniprr.repository.BundleTrackerRepo;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.service.BundleTrackerService;
import com.digi.uniprr.utils.FileUploadValidation;

@Service
@Transactional
public class BundleTrackerServiceImpl implements BundleTrackerService {
	
    @Value("${uniprrFolderPath}")
    private String UPLOAD_FOLDER;
    
    @Value("${baseUrl}")
    private String baseUrl;
    
    @Value("${uniprrFolderPath}")
    private String uniprrFolderPath;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BundleTrackerRepo bundleTrackerRepo;
	
	@Autowired
	ManuScriptFileRepo manuScriptFileRepo;

	@Override
	public BundleTracker getFailedBundelFIleById(Integer manuscriptId) {
		return bundleTrackerRepo.getFailedManuscriptById(manuscriptId);
	}

	@Override
	public List<BundleTracker> failedListArticle(Integer size) {
		return bundleTrackerRepo.failedListArticle(size);
	}

	@Override
	public String getBundelStatus(Integer manuscriptId) {
		BundleTracker failedManuscript = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);
		if(failedManuscript != null) {
		return failedManuscript.getBundleStatus();
		}else {
			return "Success";
		}
	}
	
//	public BundleStatusResponse getBundleDetails(Integer manuscriptId) {
//	    BundleStatusResponse response = new BundleStatusResponse();
//		BundleTracker bundleTracker = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);
//	    String existingReason= null;
//	    if (bundleTracker == null) {
//	        return null;
//	    }
//
//	    String bundleStatus = bundleTracker.getBundleStatus();
//
//	    // Fetch the latest version of manuscript files
//	    Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(manuscriptId);
//	    List<ManuScriptFile> manuscriptFiles = manuScriptFileRepo.getManuscriptByactive(manuscriptId, maxVersion, 1);
//
//	    // Exclude bundled files from validation
//	    List<String> excludedFiles = Arrays.asList("Bundled.pdf", "Bundled.docx", "Bundled.doc", "BundledPdfForReviewer.pdf");
//	    List<String> fileUrls = manuscriptFiles.stream()
//	    	    .filter(file -> file.getFilePath() != null 
//	    	            && !excludedFiles.contains(file.getFileName()) 
//	    	            && !"FINALREVISION".equalsIgnoreCase(file.getFileType()))  // Exclude FINALREVISION files
//	    	    .map(ManuScriptFile::getFilePath)
//	    	    .collect(Collectors.toList());
//	    System.out.println(fileUrls);
//
//	    // Check for missing files
//	    List<String> missingFiles = fileUrls.stream()
//	            .filter(fileUrl -> !isFileAvailable(fileUrl))
//	            .map(this::getFileNameFromUrl)
//	            .collect(Collectors.toList());
//
//	    // Handle missing files for all cases (Success, Failed, Failed2, Others)
//	    if (!missingFiles.isEmpty()) {
//	        existingReason = bundleTracker.getBundleErrorMessage();
//	        LOGGER.warn("Missing files detected for manuscriptId {}: {}", manuscriptId, missingFiles);
//
//	        // Only replace default success messages for "Failed" and "Failed2" cases
//	        if ((existingReason == null || existingReason.trim().isEmpty() ||
//	            "Bundle file creation completed !".equalsIgnoreCase(existingReason.trim()))
//	            && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
//	            existingReason = "";
//	        }
//	        if (existingReason != null && existingReason.contains("Error occured during renaming of Main Document/First Page files") && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
//	            existingReason =""; 
//	        }
//
//	        // If the status is "Success" but files are missing, update the status to "Failed"
//	        if ("Success".equalsIgnoreCase(bundleStatus)) {
//	            bundleTracker.setBundleStatus("Failed2");
//	            bundleStatus ="Failed2";
//	            LOGGER.info("Status changed to 'Failed' due to missing files: "+String.join(", ", missingFiles));
//		        bundleTrackerRepo.save(bundleTracker);
//	        }
//	        response.setBundleStatus(bundleStatus);
//	        response.setFailureReason(existingReason + ". Missing files: " + String.join(", ", missingFiles));
//	    }
//	    else {
//	        existingReason = bundleTracker.getBundleErrorMessage();
//
//	        // Only replace default success messages for "Failed" and "Failed2" cases
//	        if ((existingReason == null || existingReason.trim().isEmpty() ||
//	            "Bundle file creation completed".equalsIgnoreCase(existingReason.trim()))
//	            && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
//	            existingReason = "";
//	        }
//	        if (existingReason != null && existingReason.contains("Error occured during renaming of Main Document/First Page files") && ("Failed".equalsIgnoreCase(bundleStatus) || "Failed2".equalsIgnoreCase(bundleStatus))) {
//	            existingReason =""; 
//	        }
//	        response.setBundleStatus(bundleStatus);
//	        response.setFailureReason(existingReason);
//	    }
//
//	    return response;
//	}
//	
	
	public BundleStatusResponse getBundleDetails(Integer manuscriptId) {
	    BundleStatusResponse response = new BundleStatusResponse();
	    BundleTracker bundleTracker = bundleTrackerRepo.getFailedManuscriptById(manuscriptId);
	    
	    if (bundleTracker == null) {
	        return null;
	    }

	    String bundleStatus = bundleTracker.getBundleStatus();

	    // Fetch the latest version of manuscript files
	    Integer maxVersion = manuScriptFileRepo.getMaxManuscriptFileVersion(manuscriptId);
	    List<ManuScriptFile> manuscriptFiles = manuScriptFileRepo.getManuscriptByactiveCM(manuscriptId, maxVersion, 1);

	    // Exclude bundled files from validation
	    List<String> excludedFiles = Arrays.asList("Bundled.pdf", "Bundled.docx", "Bundled.doc", "BundledPdfForReviewer.pdf");
	    List<String> fileUrls = manuscriptFiles.stream()
	            .filter(file -> file.getFilePath() != null 
	                    && !excludedFiles.contains(file.getFileName()) 
	                    && !"FINALREVISION".equalsIgnoreCase(file.getFileType()))  // Exclude FINALREVISION files
	            .map(ManuScriptFile::getFilePath)
	            .collect(Collectors.toList());
	    
	    // Check for missing files
//	    List<String> missingFiles = fileUrls.stream()
//	            .filter(fileUrl -> !isFileAvailable(fileUrl))
//	            .map(this::getFileNameFromUrl)
//	            .collect(Collectors.toList());
	    
	    List<String> missingFiles = new ArrayList<>();

	    // Check for corrupt files
	    List<String> corruptFiles = new ArrayList<>();
	    Integer journalId = manuScriptFileRepo.getJorunalId(manuscriptId);
	    Map<String, String> expectedMimeTypes = FileUploadValidation.getExpectedMimeTypes();
	    
	    for (ManuScriptFile mf : manuscriptFiles) {
	        String mansucriptBasePath = UPLOAD_FOLDER + File.separator + "UniprrFileFolder" + File.separator + "MNT_LWW_JOURNAL_" + "R" + mf.getFileVersion() + "_" + journalId + "_" + mf.getManuScriptId();
	        File dir = new File(mansucriptBasePath);
	        File serverFile = new File(dir.getAbsolutePath() + File.separator + mf.getFileName());
	        
            String bundleFilePath = mf.getFilePath().replace(baseUrl, uniprrFolderPath);
            File bundleFile = new File(bundleFilePath);
	        
	        if (!serverFile.exists() || serverFile.length() == 0) {
	            LOGGER.info("Missing/empty file: {}. Attempting restore...", mf.getFileName());
	            boolean restored = restoreFromBackup(bundleFile, mf.getFileName());
	            if (restored && bundleFile.exists() && bundleFile.length() > 0) {
	                LOGGER.info("Restored successfully: {}", mf.getFileName());
	            } else {
	                LOGGER.error("Restore failed for missing file: {}", mf.getFileName());
	                missingFiles.add(mf.getFileName());
	                continue;
	            }
	        }

	        if (!missingFiles.contains(mf.getFileName())) { // Ensure file is not in missing list
	            boolean isValidFile = FileUploadValidation.validateFile(serverFile, expectedMimeTypes);
	            if (!isValidFile) {
	            	LOGGER.info("Corrupt File: " + mf.getFileName());
	                corruptFiles.add(mf.getFileName());
	            }
	        }
	    }
	    
	    if (!missingFiles.isEmpty() || !corruptFiles.isEmpty()) {
	        LOGGER.warn("Issues detected for manuscriptId {}: Missing files: {}, Corrupt files: {}", 
	                manuscriptId, missingFiles, corruptFiles);

	        // If the status is "Success" but issues are found, update the status to "Failed2"
	        if (!"Success".equalsIgnoreCase(bundleStatus)) {
	            bundleTracker.setBundleStatus("Failed2");
	            bundleStatus = "Failed2";
	            LOGGER.info("Status changed to 'Failed' due to file issues: Missing - {} | Corrupt - {}", 
	                    missingFiles, corruptFiles);
	            bundleTrackerRepo.save(bundleTracker);
	            
	            manuscriptFiles.stream()
	            .filter(file -> {
	                String fileName = file.getFileName();
	                return "Bundled.pdf".equalsIgnoreCase(fileName) || "Bundled.docx".equalsIgnoreCase(fileName);
	            })
	            .forEach(file -> {
	                file.setIsActive(0);
	                manuScriptFileRepo.save(file);  // Save the updated record
	                LOGGER.info("Set isActive=0 for bundled file: {}", file.getFileName());
	            });
	        }
	        
	        String failureReason = "";
	        if (!missingFiles.isEmpty()) {
	            failureReason += "Missing files: " + String.join(", ", missingFiles);
	        }
	        if (!corruptFiles.isEmpty()) {
	            if (!failureReason.isEmpty()) {
	                failureReason += " | ";
	            }
	            failureReason += "Corrupt files: " + String.join(", ", corruptFiles);
	        }
	        
	        response.setBundleStatus(bundleStatus);
	        response.setFailureReason(failureReason);
	    } else {
	        response.setBundleStatus(bundleStatus);
	        response.setFailureReason(""); // No issues found
	    }

	    return response;
	}
	
	private boolean restoreFromBackup(File originalFile, String realFileName) {
	    File parentDir = originalFile.getParentFile();
	    File backupDir = new File(parentDir, "Backup");

	    // 1️⃣ Try Backup folder first
	    if (backupDir.exists() && backupDir.isDirectory()) {
	        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith(realFileName + "_"));
	        if (backups != null && backups.length > 0) {
	            Arrays.sort(backups, Comparator.comparingLong(File::lastModified).reversed());
	            File latestBackup = backups[0];
	            try {
	                Files.copy(latestBackup.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	                LOGGER.info("Restored {} from latest backup {}", realFileName, latestBackup.getName());
	                return true;
	            } catch (IOException e) {
	            	LOGGER.error("Error restoring {} from backup {}", realFileName, latestBackup.getName(), e);
	                return false;
	            }
	        } else {
	        	LOGGER.warn("No backup files found for {} in {}", realFileName, backupDir.getAbsolutePath());
	        }
	    } else {
	    	LOGGER.warn("No Backup folder found in: {}", parentDir.getAbsolutePath());
	    }

	    // 2️⃣ If Backup fails, check previous version folder
	    File prevVersionDir = getPreviousVersionDir(parentDir);
	    if (prevVersionDir != null) {
	        File prevFile = new File(prevVersionDir, realFileName);
	        if (prevFile.exists() && prevFile.length() > 0) {
	            try {
	                Files.copy(prevFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	                LOGGER.info("Restored {} from previous version folder {}", realFileName, prevFile.getAbsolutePath());
	                return true;
	            } catch (IOException e) {
	            	LOGGER.error("Error restoring {} from previous version {}", realFileName, prevFile.getAbsolutePath(), e);
	                return false;
	            }
	        } else {
	        	LOGGER.error("Previous version file not found for {}", realFileName);
	        }
	    }

	    // 3️⃣ Nothing worked
	    return false;
	}

	/**
	 * Finds the previous version directory (e.g., R2 → R1, R1 → R0).
	 */
	private File getPreviousVersionDir(File currentDir) {
	    String path = currentDir.getAbsolutePath();
	    Pattern pattern = Pattern.compile("R(\\d+)_");
	    Matcher matcher = pattern.matcher(path);

	    if (matcher.find()) {
	        int currentVersion = Integer.parseInt(matcher.group(1));
	        if (currentVersion > 0) {
	            int prevVersion = currentVersion - 1;
	            String prevPath = path.replace("R" + currentVersion + "_", "R" + prevVersion + "_");
	            File prevDir = new File(prevPath);
	            if (prevDir.exists() && prevDir.isDirectory()) {
	                return prevDir;
	            } else {
	            	LOGGER.warn("Previous version directory not found: {}", prevPath);
	            }
	        }
	    }
	    return null;
	}

	
//    private boolean restoreFromBackup(File originalFile, String realFileName) {
//        File parentDir = originalFile.getParentFile();
//        File backupDir = new File(parentDir, "Backup");
//
//        if (!backupDir.exists() || !backupDir.isDirectory()) {
//        	LOGGER.error("No Backup folder found in: {}", parentDir.getAbsolutePath());
//            return false;
//        }
//
//        // Find all backups with pattern <realFileName>_timestamp
//        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith(realFileName + "_"));
//        if (backups == null || backups.length == 0) {
//        	LOGGER.error("No backup files found for {}", realFileName);
//            return false;
//        }
//
//        // Sort by last modified → latest first
//        Arrays.sort(backups, Comparator.comparingLong(File::lastModified).reversed());
//        File latestBackup = backups[0];
//
//        try {
//            Files.copy(latestBackup.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            LOGGER.info("Restored {} from latest backup {}", realFileName, latestBackup.getName());
//            return true;
//        } catch (IOException e) {
//        	LOGGER.error("Error restoring {} from backup {}", realFileName, latestBackup.getName(), e);
//            return false;
//        }
//    }

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

    private String getFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }


}
