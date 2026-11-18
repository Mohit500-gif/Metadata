package com.digi.uniprr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonFileUtils {
	private static Logger log = LoggerFactory.getLogger(CommonFileUtils.class);

	private CommonFileUtils() {
	}

	public static void deleteFolderRecursively(Path folderPath) {
		try (Stream<Path> path = Files.walk(folderPath)) {
			path.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			log.error("Exception while deleting folder after upload for manuscript : " + e.getMessage());
		}
	}

}
