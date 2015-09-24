package edu.wayne.cs.severe.ir4se.processor.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileRetrievalUtils {

	public static void createEmptyFolder(String dirPath) throws IOException {
		File directory = new File(dirPath);
		FileUtils.deleteDirectory(directory);
		FileUtils.forceMkdir(directory);
	}

	public static void removeFile(String filePath) {
		File file = new File(filePath);
		file.delete();
	}

}
