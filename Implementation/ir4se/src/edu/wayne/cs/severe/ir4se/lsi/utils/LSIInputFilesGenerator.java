package edu.wayne.cs.severe.ir4se.lsi.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wayne.cs.severe.ir4se.utils.FileGen;

/**
 * Generates query files for LSI (paris implementation) and Indri/Lemur from the
 * files _corpus.txt, _mapping.txt, _queries.txt
 * 
 * @author Lau
 * 
 */
public class LSIInputFilesGenerator {

	// true for Indri/Lemur format.
	// false for LSI format;
	private boolean isIndriFormat = false;

	private List<String> systems;

	private String corpusPath;
	private String mappingPath;
	private String queryPath;

	private String queryOutPath;
	private String relevantDocsOutPath;
	private String corpusOutPath;
	private String mappingOutPath;

	private String dataDirPath;
	private String outDirPath;
	private String indexDirPath;

	private HashMap<String, Integer> mapping;

	public LSIInputFilesGenerator(String dataDirPath, boolean isIndriFormat) {
		this.dataDirPath = dataDirPath;
		this.isIndriFormat = isIndriFormat;
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
		outDirPath = dataDir.getParentFile().getAbsolutePath().concat("/LSI Data");
		File outDir = new File(outDirPath);
		outDir.mkdir();
	}

	public void processDataDir() {
		System.out.println("Initializing variables...");
		initialize();

		for (String system : systems) {
			// String system = systems.get(6);
			try {
				System.out.println("Processing " + system + " data...");
				corpusPath = dataDirPath + "/" + system + "/" + system + "_corpus.txt";
				mappingPath = dataDirPath + "/" + system + "/" + system + "_mapping.txt";
				queryPath = dataDirPath + "/" + system + "/" + system + "_queries.txt";

				// create output dir for the system
				String systemOutDirPath = outDirPath + "/" + system;
				File outDir = new File(systemOutDirPath);
				outDir.mkdir();

				queryOutPath = systemOutDirPath + "/query";
				relevantDocsOutPath = systemOutDirPath + "/qrels.text";
				corpusOutPath = systemOutDirPath + "/corpus.txt";
				mappingOutPath = systemOutDirPath + "/mapping.txt";

				loadMapping(mappingPath);

				generateQueryRelFiles(queryOutPath, relevantDocsOutPath);

				FileGen.copyFile(corpusPath, corpusOutPath);
				FileGen.copyFile(mappingPath, mappingOutPath);

				indexDirPath = systemOutDirPath + "/index";
				File indexDir = new File(indexDirPath);
				indexDir.mkdir();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void generateQueryRelFiles(String queryOutPath, String relevantDocsOutPath) throws IOException {
		FileWriter fwQueryOut = new FileWriter(queryOutPath);
		FileWriter fwQueryRelOut = new FileWriter(relevantDocsOutPath);

		File queryFile = new File(queryPath);
		BufferedReader inQueryReader = new BufferedReader(new FileReader(queryFile));

		File corpusFile = new File(corpusPath);
		BufferedReader corpusReader = new BufferedReader(new FileReader(corpusFile));

		try {
			String line;
			String[] targetDocs = null;

			int numberTargetDocs = 0;
			int numberLines = 0;
			int queryNumber = 0;

			String query = "";
			String queryNumberString = "";

			while ((line = inQueryReader.readLine()) != null) {
				// it is not a blank line
				if (line.trim().length() >= 1) {
					numberLines++;
					switch (numberLines) {
					case 1:
						if (isIndriFormat) {
							queryNumber = Integer.parseInt(line.trim()) - 1;
						} else {
							queryNumber = Integer.parseInt(line.trim());
						}
						if (queryNumber < 10) {
							queryNumberString = "0" + String.valueOf(queryNumber);
						} else {
							queryNumberString = String.valueOf(queryNumber);
						}
						break;
					case 2:
						query = line.trim().toLowerCase();
						break;
					case 3:
						numberTargetDocs = Integer.parseInt(line.trim());
						targetDocs = new String[numberTargetDocs];
						break;
					default:
						if (numberLines >= 4) {
							targetDocs[numberLines - 4] = line.trim();
						}
						break;
					}
				} else {
					numberLines = 0;
					fwQueryOut.write(query + "\n\n");
					for (int j = 0; j < targetDocs.length; j++) {
						if (isIndriFormat) {
							fwQueryRelOut.write(queryNumberString + " Q0 " + getClassNumberInMapping(targetDocs[j])
									+ " 1\n");
						} else {
							fwQueryRelOut
									.write(queryNumberString + " " + getClassNumberInMapping(targetDocs[j]) + "\n");
						}
					}
				}
			}

			fwQueryOut.write(query + "\n\n");
			for (int j = 0; j < targetDocs.length; j++) {
				if (isIndriFormat) {
					fwQueryRelOut.write(queryNumberString + " Q0 " + getClassNumberInMapping(targetDocs[j]) + " 1\n");
				} else {
					fwQueryRelOut.write(queryNumberString + " " + getClassNumberInMapping(targetDocs[j]) + "\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inQueryReader.close();
			corpusReader.close();
		}
		fwQueryOut.close();
		fwQueryRelOut.close();

	}

	private String getClassNumberInMapping(String inDocName) throws IOException {
		String nameForSearch = inDocName.toLowerCase().replace(' ', '.').replace('/', '.');
		return String.valueOf(mapping.get(nameForSearch));
	}

	private void loadMapping(String mappingPath) throws IOException {
		mapping = new HashMap<String, Integer>();

		File mappingFile = new File(mappingPath);
		BufferedReader inQuery = new BufferedReader(new FileReader(mappingFile));
		int numberLines = 0;
		String docString = "";

		try {
			String line;
			numberLines = 0;

			while ((line = inQuery.readLine()) != null) {
				// it is not a blank line
				if (line.trim().length() >= 1) {
					numberLines++;
					docString = line.replace(" ", ".");
					docString = docString.replace('/', '.');
					mapping.put(docString.toLowerCase(), numberLines);
				}
			}
		} catch (Exception e) {
			System.out.println("line: " + numberLines + " docString: " + docString);
			e.printStackTrace();
		} finally {
			inQuery.close();
		}
	}

}
