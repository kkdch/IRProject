package edu.wayne.cs.severe.ir4se.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	// 1 - file document; 2 - paragraph document;
	private static int documentType = 2; 
	
	private static String baseDir = "/Users/daichenhan/Documents/IR_PROJ_DATA/Raw Data";
	
	private String system = "";
	private String indexDirectoryPath;
	private String corpusFilePathAndName;
	private String mappingFilePathAndName;
	private String queryFilePath;
	private String resultsFilePath;

	private String queriesDirectory = null;
	private String resultsDirectory = null;
	private String corpusDirectory = null;

	public static void main(String args[]) throws Exception {
		Main main = new Main();
		main.run();
	}

	public void run() {
		try {
			File dataDir;
			
			system = "Adempiere";// default system
			while (true) {
				System.out.println("-------------------------------------------------------\n");
				System.out.println("\nChoose the action you want to perform:\n");
				// index a corpus file - all documents are in this file;
				// documents are separated by a blank line, all contents of a
				// document will be placed int he same field - "contents"
				System.out.println("0. Choose the name of the system from a given list or enter it");
				System.out.println("1. Index a corpus found in a file");
				System.out.println("2. Search an existing index using an individual query");
				System.out.println("3. Perform a bunch of searches on the index, using a query file");
				System.out
						.println("4. Perform a bunch of searches on the index, using a query file, and using also the individual terms in each of the input queries as queries");
				System.out.println("5. Execute 1 and 3 for all the systems in the dir");
				System.out.println("6. EXIT");

				BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

				String line = in.readLine();
				int val = Integer.parseInt(line);
				switch (val) {
				case 0:
					System.out.println("-------------------------------------------------------");
					System.out
							.println("\n\nPlease select the number associated with the name of the desired system, or enter it from the console. Your options are:\n");

					dataDir = new File(baseDir);
					for (int i = 0; i < dataDir.listFiles().length; i++) {
						File file = dataDir.listFiles()[i];
						if (file.isDirectory()) {
							System.out.println(i + ". " + file.getName());
						}
					}

					String line2 = in.readLine();
					try {
						int opt = Integer.parseInt(line2);
						initSystemVariables(dataDir, opt);

					} catch (NumberFormatException e) {
						System.out.println("Invalid choice!");
					} catch (Exception e) {
						System.out.println("Invalid choice!");
					}

					System.out.println("Chosen system: " + system + "\n");
					break;

				case 1: // index a corpus file, where each paragraph, separated
						// by a blank line, represents a document
					indexCorpus();
					break;

				case 2: // search an existing index using an individual query
						// entered from the console
					resultsFilePath = resultsDirectory + system + "_Results" + system + ".txt";
					System.out.println("Enter the query: (type !X to return to previous menu)");
					String query = in.readLine().trim();
					if (query.trim().equalsIgnoreCase("!X"))
						break;
					System.out
							.println("Now you need to input the target documents for the query (in order to automatically detect their position in the list of results). \n Enter the number of relevant documents: ");
					line = in.readLine().trim();
					int numberTargetClasses = Integer.parseInt(line);
					String[] targetClasses = new String[numberTargetClasses];
					for (int a = 0; a < numberTargetClasses; a++) {
						System.out.println("Enter target document " + (a + 1) + ": ");
						line = in.readLine().trim();
						targetClasses[a] = line;
					}
					try {
						SearchFiles
								.search(system, documentType, 1, indexDirectoryPath, query, resultsFilePath, targetClasses, false, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Done searching!");
					// /write the results to the output
					break;

				case 3: // search an existing index with many queries; the query
						// files contain also the names of the relevant classes
						// or methods
					searchQueriesFromFile();
					break;

				case 4:
					queryFilePath = queriesDirectory + system + "_Queries.txt";
					resultsFilePath = resultsDirectory + "results_" + system + ".txt";
					SearchFiles
							.runQueriesFromFile(system, documentType, indexDirectoryPath, queryFilePath, resultsFilePath, true, false);
					System.out.println("Done searching!");
					break;

				case 5:
					dataDir = new File(baseDir);
					for (int i = 0; i < dataDir.listFiles().length; i++) {
						File file = dataDir.listFiles()[i];
						if (file.isDirectory()) {
							initSystemVariables(dataDir, i);
							indexCorpus();
							searchQueriesFromFile();
						}
					}
					break;
					
				case 6:
					System.exit(0);
					break;

				default:
					System.out.println("The option is not a valid option!!");
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void searchQueriesFromFile() {
		queryFilePath = queriesDirectory + system + "_Queries.txt";
		resultsFilePath = resultsDirectory + "results.txt";

		File f = new File(resultsFilePath);
		if (f.exists()) {
			f.delete();
		}
		f = new File(resultsDirectory + "NotFound.txt");
		if (f.exists()) {
			f.delete();
		}

		System.out.println("Queries file :" + queryFilePath);
		SearchFiles
				.runQueriesFromFile(system, documentType, indexDirectoryPath, queryFilePath, resultsFilePath, false, true);

		System.out.println("Done searching!");
	}

	private void indexCorpus() {
		indexDirectoryPath = resultsDirectory + "index/";

		corpusFilePathAndName = corpusDirectory + system + "_Corpus.txt";
		mappingFilePathAndName = corpusDirectory + system + "_Mapping.txt";

		System.out.println("Indexing corpus file\n");
		System.out.println("Corpus file: " + corpusFilePathAndName);
		System.out.println("Mapping file :" + mappingFilePathAndName);
		Indexer.indexParagraphDocuments(indexDirectoryPath, corpusFilePathAndName, mappingFilePathAndName);
		System.out
				.println("Indexing done! The directory \"Results/index\" was created at the specified location, "
						+ " which contains "
						+ Indexer.getNumberOfDocsInIndex(indexDirectoryPath)
						+ " documents in the index");
	}

	private void initSystemVariables(File dataDir, int opt) {
		system = dataDir.listFiles()[opt].getName();

		queriesDirectory = baseDir + "/" + system + "/";
		resultsDirectory = baseDir + "/" + system + "/Results/";
		corpusDirectory = baseDir + "/" + system + "/";
	}
}
