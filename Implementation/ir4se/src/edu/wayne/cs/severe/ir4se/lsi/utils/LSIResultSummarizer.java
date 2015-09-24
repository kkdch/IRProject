package edu.wayne.cs.severe.ir4se.lsi.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wayne.cs.severe.ir4se.utils.ResultHolder;
import edu.wayne.cs.severe.ir4se.utils.StringUtils;

public class LSIResultSummarizer {

	private String dataDirPath;
	private boolean singleSystem;

	private String indexDirPath;
	private String mappingPath;
	private String resultsPath;
	private String qrelsPath;

	public LSIResultSummarizer(String dataDirPath, boolean singleSystem) {
		this.dataDirPath = dataDirPath;
		this.singleSystem = singleSystem;

	}

	public void summarizeResults() {
		if (singleSystem) {
			summarizeForSystem(StringUtils.getSystemName(dataDirPath));
		} else {
			File dataDir = new File(dataDirPath);
			for (File system : dataDir.listFiles()) {
				if (dataDir.isDirectory()) {
					summarizeForSystem(system.getName());
				}
			}
		}

	}

	private void initVariables(String system) {
		indexDirPath = dataDirPath;
		mappingPath = dataDirPath;
		resultsPath = indexDirPath;
		qrelsPath = indexDirPath;

		if (!singleSystem) {
			indexDirPath = indexDirPath + system + "/";
			mappingPath = mappingPath + system + "/";
			qrelsPath = qrelsPath + system + "/";
		}
		indexDirPath = indexDirPath + "index/";
		mappingPath = mappingPath + "mapping.txt";
		resultsPath = indexDirPath + "results.txt";
		qrelsPath = qrelsPath + "/qrels.text";

	}

	private void summarizeForSystem(String system) {
		System.out.println("Summarizing " + system + " results...");

		try {
			initVariables(system);

			FileWriter writer = new FileWriter(new File(resultsPath));

			HashMap<Integer, String> mapping = loadMapping(mappingPath);
			int numDocs = mapping.size();

			HashMap<Integer, Set<Integer>> targetDocs = loadTargetFile(qrelsPath);
			List<Integer> queries = new ArrayList<>(targetDocs.keySet());
			Collections.sort(queries);
			for (Integer queryId : queries) {
				HashMap<Integer, ResultHolder> queryResults = loadQueryResults(queryId);

				for (Integer target : targetDocs.get(queryId)) {
					StringBuilder out = new StringBuilder(system + ";");
					out.append(queryId + ";");
					out.append(mapping.get(target) + ";");
					ResultHolder results = queryResults.get(target);
					if (results != null) {
						out.append(results.getRank() + ";");
						out.append(queryResults.size() + ";");
						out.append(numDocs + ";");
						out.append(results.getScore() + "\n");
					} else {
						out.append("NOT_RETRIEVED\n");
					}
					writer.write(out.toString());
				}
			}

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<Integer, Set<Integer>> loadTargetFile(String targetFilePath) throws IOException {
		HashMap<Integer, Set<Integer>> targetDocs = new HashMap<Integer, Set<Integer>>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(targetFilePath)));
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				int index = line.indexOf(' ');
				Integer queryId = Integer.valueOf(line.substring(0, index));
				Integer target = Integer.valueOf(line.substring(index + 1));

				Set<Integer> targetSet = null;
				if (targetDocs.containsKey(queryId)) {
					targetSet = targetDocs.get(queryId);

				} else {
					targetSet = new HashSet<Integer>();
				}
				targetSet.add(target);
				targetDocs.put(queryId, targetSet);
			}
		}

		reader.close();
		return targetDocs;
	}

	private HashMap<Integer, ResultHolder> loadQueryResults(Integer queryId) throws FileNotFoundException, IOException {
		HashMap<Integer, ResultHolder> queryResults = new HashMap<Integer, ResultHolder>();

		String resultsFileName = findResultsFileName(queryId);
		if (resultsFileName == null) {
			System.out.println("Not found:" + queryId);
			return queryResults;
		}
		BufferedReader reader = new BufferedReader(new FileReader(new File(resultsFileName)));
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				String[] tokens = line.split(" ");
				Integer doc = Integer.valueOf(tokens[0]);
				double score = Double.parseDouble(tokens[1]);
				int rank = Integer.valueOf(tokens[2]);

				ResultHolder result = new ResultHolder(rank, score);
				queryResults.put(doc, result);
			}
		}

		reader.close();
		return queryResults;
	}

	private String findResultsFileName(Integer queryId) {
		File indexDir = new File(indexDirPath);
		String path = null;
		for (File f : indexDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".res");
			}
		})) {
			if (f.isFile()) {
				String fName = f.getName();
				int dot = fName.indexOf('.');
				fName = fName.substring(0, dot);
				if (queryId == Integer.valueOf(fName).intValue()) {
					return f.getAbsolutePath();
				}

			}
		}
		return path;
	}

	private HashMap<Integer, String> loadMapping(String mappingPath) throws FileNotFoundException, IOException {
		HashMap<Integer, String> mapping = new HashMap<Integer, String>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(mappingPath)));
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				count++;
				mapping.put(count, line);
			}
		}

		reader.close();
		return mapping;
	}
}
