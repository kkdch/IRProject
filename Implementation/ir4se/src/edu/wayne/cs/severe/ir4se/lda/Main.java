package edu.wayne.cs.severe.ir4se.lda;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	private static String baseDir = "/Users/daichenhan/Documents/IR_PROJ_DATA/LDA Data";

	private static boolean est = true;
	private static double alpha = -0.1;
	private static double beta = 0.5;
	private static int niters = 500;
	private static int ntopics = 400;

	public static void main(String args[]) throws Exception {
		try {
			String systemDir = null;

			while (true) {
				System.out.println("-------------------------------------------------------\n");
				System.out.println("\nChoose the action you want to perform:\n");
				// index a corpus file - all documents are in this file;
				// documents are separated by a blank line, all contents of a
				// document will be placed int he same field - "contents"
				System.out.println("0. Choose the name of the system from a given list");
				System.out.println("1. Create topic model for a corpus found in a file");
				System.out.println("2. Perform a bunch of searches on the index, using a query file");
				System.out.println("3. Execute 1 and 2 for all the systems in the dir");
				System.out.println("4. EXIT");

				BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

				String line = in.readLine();
				int val = Integer.parseInt(line);
				switch (val) {
				case 0:
					System.out.println("-------------------------------------------------------");
					System.out.println("\n\nPlease select the number associated with the name of the "
							+ "desired system, or enter it from the console. Your options are:\n");

					File dataDir = showSystems();

					line = in.readLine();
					systemDir = checkChosenSystem(systemDir, line, dataDir);

					break;

				case 1:
					// create a topic model for a corpus file where the first
					// line is the number of documents and the each of the
					// following lines represents a document

					buildTopicModel(systemDir);

					break;

				case 2:
					// Search many queries using the topic model previously
					// built.
					searchQueriesFromFile(systemDir);
					break;

				case 3:
					dataDir = new File(baseDir);
					for (int i = 0; i < dataDir.listFiles().length; i++) {
						File file = dataDir.listFiles()[i];
						if (file.isDirectory()) {
							systemDir = checkChosenSystem(systemDir, String.valueOf(i), dataDir);
							buildTopicModel(systemDir);
							searchQueriesFromFile(systemDir);
						}
					}
					break;
					
				case 4:
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

	private static void searchQueriesFromFile(String systemDir) {
		String queryPath;
		String targetPath;
		queryPath = systemDir + "queries.txt";
		TopicModelSearcher searcher = new TopicModelSearcher(systemDir, ntopics);
		searcher.search(queryPath);
		targetPath = systemDir + "target.txt";
		searcher.summarizeResults(targetPath);

		System.out.println("Done searching!");
	}

	private static void buildTopicModel(String systemDir) {
		String corpusPath;
		corpusPath = "corpus.txt";

		System.out.println("Creating topic model for corpus file: " + systemDir + corpusPath);
		TopicModelBuilder modelBuilder = new TopicModelBuilder(est, alpha, beta, niters, ntopics,
				systemDir, corpusPath);
		modelBuilder.estimateModel();
		modelBuilder.optimize();

		System.out.println("Model built! The directory \"lda\" was" + " created at the specified location");
	}

	private static String checkChosenSystem(String systemDir, String line, File dataDir) {
		String system;
		try {
			int opt = Integer.parseInt(line);
			system = dataDir.listFiles()[opt].getName();

			systemDir = baseDir + "/" + system + "/";
			System.out.println("Chosen system: " + system + "\n");

		} catch (NumberFormatException e) {
			System.out.println("Invalid choice!");
		} catch (Exception e) {
			System.out.println("Invalid choice!");
		}
		return systemDir;
	}

	private static File showSystems() {
		File dataDir = new File(baseDir);
		for (int i = 0; i < dataDir.listFiles().length; i++) {
			File file = dataDir.listFiles()[i];
			if (file.isDirectory()) {
				System.out.println(i + ". " + file.getName());
			}
		}
		return dataDir;
	}
}
