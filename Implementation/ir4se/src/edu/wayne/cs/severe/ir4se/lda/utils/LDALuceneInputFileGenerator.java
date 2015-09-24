package edu.wayne.cs.severe.ir4se.lda.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.ir4se.utils.FileGen;

public class LDALuceneInputFileGenerator {

	private List<String> systems;

	private String wordmapPath;
	private String phiPath;
	private String thetaPath;

	private String vocabOutPath;
	private String wordsOutPath;
	private String thetaOutPath;

	private String dataDirPath;
	private String ldaDirPath;

	private boolean singleSystem;

	public LDALuceneInputFileGenerator(String dataDirPath, boolean singleSystem) {
		this.dataDirPath = dataDirPath;
		this.singleSystem = singleSystem;
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
	}

	public void processDataDir() {
		if (singleSystem) {
			processSystem(dataDirPath);
		} else {
			initialize();
			for (String system : systems) {
				System.out.println("Processing " + system + " data...");
				String currentDataDir = dataDirPath + "/" + system;
				processSystem(currentDataDir);
			}
		}
	}

	private void processSystem(String currentDataDir) {
		ldaDirPath = currentDataDir + "/lda";

		wordmapPath = currentDataDir + "/model-wordmap.txt";
		phiPath = currentDataDir + "/model-final.phi";
		thetaPath = currentDataDir + "/model-final.theta";

		vocabOutPath = ldaDirPath + "/vocab.dat";
		wordsOutPath = ldaDirPath + "/words.dat";
		thetaOutPath = ldaDirPath + "/theta.dat";

		try {
			genVocabFile(wordmapPath, vocabOutPath);
			genWordsFile(phiPath, wordsOutPath);
			genThetaFile(thetaPath, thetaOutPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void genVocabFile(String inputPath, String outputPath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
		FileWriter writer = new FileWriter(outputPath);
		try {
			String line;
			int wordCount = 0;

			// First line = # of lines in the file
			if ((line = reader.readLine()) != null) {
				wordCount = Integer.valueOf(line);
				// skip second line
				line = reader.readLine();
			}
			String[] words = new String[wordCount];
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(" ");
				int index = Integer.valueOf(tokens[1]);
				words[index] = tokens[0];
			}
			for (String word : words) {
				writer.write(word + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		reader.close();
		writer.close();
	}

	private void genWordsFile(String inputPath, String outputPath) throws IOException {
		FileGen.copyFile(inputPath, outputPath);
	}

	private void genThetaFile(String inputPath, String outputPath) throws IOException {
		FileGen.copyFile(inputPath, outputPath);
	}

}
