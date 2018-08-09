package com.vendormanagement.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;

public class FileUtil {

	private List<String> files = new ArrayList<>();
	private String dirPath;
	private File logFile;

	public FileUtil(String dirPath) throws IOException {
		this.dirPath = dirPath;

		processDirectory();
	}

	private void processDirectory() throws IOException {

		File dir = new File(dirPath);

		if (!dir.exists()) {
			throw new IOException(String.format("Directory  %s does not exist", dirPath));
		}

		if (!dir.isDirectory()) {
			throw new IOException(String.format("Path %s is not a valid directory", dirPath));
		}

		File[] listFiles = dir.listFiles();

		for (File file : listFiles) {

			if (file.isFile()) {
				files.add(file.getAbsolutePath());
			}
		}
	}

	public List<String> getHtmFileList() {

		List<String> htmFiles = new ArrayList<>();

		if (Util.isEmpty(files))

		{
			return htmFiles;
		}

		for (String string : files) {

			if (string.endsWith(".htm") || string.endsWith(".html")) {
				htmFiles.add(string);
			}
		}
		return htmFiles;
	}

	public String getXlsFile() {

		if (Util.isEmpty(files))

		{
			return null;
		}

		for (String string : files) {

			if (string.endsWith(".xls") || string.endsWith(".xlsx")) {
				return string;
			}
		}
		return null;
	}

	public void createLogFile() throws IOException {

		String logFilePath = dirPath + File.separator + "invoice.log";
		Path path = Paths.get(logFilePath);

		logFile = new File(logFilePath);

		try {
			if (!logFile.exists()) {
				Files.createFile(path);
				com.google.common.io.Files.write(Util.getToday(), logFile, Charsets.UTF_8);
			} else {
				// append
				com.google.common.io.Files.append("\n", logFile, Charsets.UTF_8);
				com.google.common.io.Files.append("****************************", logFile, Charsets.UTF_8);
				com.google.common.io.Files.append(Util.getToday(), logFile, Charsets.UTF_8);
				com.google.common.io.Files.append("****************************", logFile, Charsets.UTF_8);
			}

		} catch (IOException e) {
			throw new IOException("Error trying to create log file");
		}

	}

	public void log(String msg) throws IOException {
		try {
			com.google.common.io.Files.append("\n", logFile, Charsets.UTF_8);
			com.google.common.io.Files.append(msg, logFile, Charsets.UTF_8);
		} catch (IOException e) {
			throw new IOException("Error trying to add text to log file");
		}

	}

}
