package com.digi.uniprr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileBackupUtil {

//    private static final Logger logger = Logger.getLogger(FileBackupUtil.class.getName());

    public static boolean backupFile(File sourceFile, int journalId, int version, int manuscriptId, String backupRootFolder) {
        String backupPath = backupRootFolder + File.separator + journalId + "_v" + version + "_" + manuscriptId;

        File backupDir = new File(backupPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        File destFile = new File(backupDir, sourceFile.getName());
        try {
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Backup successful: " + destFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Failed to backup file: " + sourceFile.getName(), e);
            return false;
        }
    }
    
    
    public static boolean safeDelete(File file) {
        if (file == null) {
        	logger.info("safeDelete called with null file.");
            return false;
        }
        if (!file.exists()) {
        	logger.info("File {} does not exist. Skipping backup.", file.getAbsolutePath());
            return false;
        }

        try {
            File backupDir = new File(file.getParentFile(), "Backup");
            if (!backupDir.exists()) {
                boolean created = backupDir.mkdirs();
                logger.info("Backup directory created: {} => {}", backupDir.getAbsolutePath(), created);
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String name = file.getName();
            int dot = name.lastIndexOf(".");
            String backupName = (dot > 0)
                    ? name.substring(0, dot) + "_" + timeStamp + name.substring(dot)
                    : name + "_" + timeStamp;

            File backupFile = new File(backupDir, backupName);
            Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            logger.info("File [{}] moved to backup as [{}]", file.getAbsolutePath(), backupFile.getAbsolutePath());
            return true;

        } catch (IOException e) {
        	logger.error("Failed to backup file: {}", file.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * Restore latest backup into the original location if missing.
     */
    public static boolean restoreIfMissing(File originalFile) {
        if (originalFile == null) {
        	logger.info("restoreIfMissing called with null file.");
            return false;
        }

        if (originalFile.exists()) {
        	logger.info("File {} already exists. No restore needed.", originalFile.getAbsolutePath());
            return true;
        }

        File backupDir = new File(originalFile.getParentFile(), "Backup");
        if (!backupDir.exists()) {
        	logger.info("Backup directory does not exist: {}", backupDir.getAbsolutePath());
            return false;
        }

        String baseName = stripExtension(originalFile.getName());
        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith(baseName + "_"));

        if (backups == null || backups.length == 0) {
        	logger.info("No backups found for {}", originalFile.getName());
            return false;
        }

        File latest = Arrays.stream(backups)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        if (latest == null) {
        	logger.info("Could not determine latest backup for {}", originalFile.getName());
            return false;
        }

        try {
            Files.copy(latest.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Restored [{}] from backup [{}]", originalFile.getAbsolutePath(), latest.getAbsolutePath());
            return true;

        } catch (IOException e) {
        	logger.error("Restore failed for {} from backup {}", originalFile.getAbsolutePath(), latest.getAbsolutePath(), e);
            return false;
        }
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? fileName : fileName.substring(0, dot);
    }
}

