package edu.wayne.cs.severe.ir4se.lda.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LDAInputFileGenerator {

	private List<String> systems;

	private String corpusPath;
	private String mappingPath;
	private String queryPath;

	private String corpusOutPath;
	private String filesOutPath;
	private String mappingOutPath;
	private String queryOutPath;
	private String targetOutPath;

	private String dataDirPath;
	private String outDirPath;
	private String ldaDirPath;

	public LDAInputFileGenerator(String dataDirPath) {
		this.dataDirPath = dataDirPath;
	}

	private void initialize() {
		File dataDir = new File(dataDirPath);
		systems = new ArrayList<String>();

		// populate systems
		for (int i = 0; i < dataDir.listFiles().length; i++) {
			File file = dataDir.listFiles()[i];
			if (file.isDirectory()) {
				systems.add(file.getName());
			}
		}

		// create output dir
		outDirPath = dataDir.getParentFile().getAbsolutePath().concat("/LDA Data");
		File outDir = new File(outDirPath);
		outDir.mkdir();
	}

	public void processDataDir() {
		System.out.println("Initializing variables...");
		initialize();

		for (String system : systems) {
			// String system = systems.get(6);
			try {
				String systemOutDirPath = outDirPath + "/" + system;
				File outDir = new File(systemOutDirPath);
				outDir.mkdir();

				ldaDirPath = systemOutDirPath + "/lda";
				File indexDir = new File(ldaDirPath);
				indexDir.mkdir();

				System.out.println("Processing " + system + " data...");
				corpusPath = dataDirPath + "/" + system + "/" + system + "_corpus.txt";
				mappingPath = dataDirPath + "/" + system + "/" + system + "_mapping.txt";
				queryPath = dataDirPath + "/" + system + "/" + system + "_queries.txt";

				// create output dir for the system

				corpusOutPath = systemOutDirPath + "/corpus.txt";
				filesOutPath = ldaDirPath + "/files.dat";
				mappingOutPath = systemOutDirPath + "/mapping.txt";
				queryOutPath = systemOutDirPath + "/queries.txt";
				targetOutPath = systemOutDirPath + "/target.txt";

				int nFiles = genMappingFile(mappingPath, mappingOutPath);
				genFilesFile(mappingPath, filesOutPath);
				genCorpusFile(corpusPath, corpusOutPath, nFiles);
				genQueryFiles(queryPath, queryOutPath, targetOutPath);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void genQueryFiles(String inputPath, String queryOutPath, String targetOutPath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
		FileWriter queryWriter = new FileWriter(queryOutPath);
		FileWriter targetWriter = new FileWriter(targetOutPath);
		int nLine = 0;

		try {
			String line;
			String queryId = "";

			while ((line = reader.readLine()) != null) {
				nLine++;

				switch (nLine) {
				case 1:
					queryId = line;
					break;
				case 2:
					queryWriter.write(queryId + " " + line + "\n");
					break;
				case 3:
					int nTarget = Integer.valueOf(line);
					for (int i = 0; i < nTarget; i++) {
						line = reader.readLine();
						line = processCodePath(line);
						targetWriter.write(queryId + " " + line + "\n");
					}
					break;
				default:
					nLine = 0;
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		reader.close();
		queryWriter.close();
		targetWriter.close();
	}

	private int genMappingFile(String inputPath, String outputPath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
		FileWriter writer = new FileWriter(outputPath);
		int fileCount = 0;
		try {
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() >= 1) {
					line = processCodePath(line);
					writer.write(line + "\n");
					fileCount++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		reader.close();
		writer.close();
		return fileCount;
	}

	private String processCodePath(String line) {
		line = line.toLowerCase();
		line = line.replace('/', '.');
		return line.replace(' ', '.');
	}

	private void genFilesFile(String inputPath, String outputPath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
		FileWriter writer = new FileWriter(outputPath);
		try {
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() >= 1) {
					line = processCodePath(line);
					line = "666 " + line + " 666";
					writer.write(line + "\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		reader.close();
		writer.close();
	}

	private void genCorpusFile(String inputPath, String outputPath, int nFiles) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
		FileWriter writer = new FileWriter(outputPath);
		try {
			String line;
			writer.write(nFiles + "\n");
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() >= 1) {
					writer.write(line + "\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		reader.close();
		writer.close();
	}

}
