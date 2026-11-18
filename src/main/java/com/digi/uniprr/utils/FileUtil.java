package com.digi.uniprr.utils;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {

	private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil() {
		// Disable instatiation of utility class
	}

	public static void updateFilePermission(File file) {
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		try {
			Files.setPosixFilePermissions(file.toPath(), perms);
		} catch (Exception e) {
			logger.error("Exception while setting permission of file : " + file.getName());
		}
	}

	public static long getFileSize(Path filePath) {
		long fileSize = 1;
		if (Files.exists(filePath)) {
			try (FileChannel fileChannel = FileChannel.open(filePath);) {
				fileSize = fileChannel.size();
			} catch (IOException e) {
				logger.error("Error while getting file sise : " + filePath, e);
			}
		}

		return fileSize;
	}
}