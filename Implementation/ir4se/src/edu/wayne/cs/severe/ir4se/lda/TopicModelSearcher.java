package edu.wayne.cs.severe.ir4se.lda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.queensu.cs.sail.lucenelda.IndexDirectory;
import ca.queensu.cs.sail.lucenelda.LDAQueryAllInDirectory;
import edu.wayne.cs.severe.ir4se.utils.ResultHolder;
import edu.wayne.cs.severe.ir4se.utils.StringUtils;

public class TopicModelSearcher {

	private String corpusPath;
	private String indexPath;
	private String ldaHelper;
	private String mappingPath;
	private String queriesPath;
	private int ntopics;
	private String ldaDir;
	private String resultDirPath;
	private String resultSummaryPath;
	private String system;

	/**
	 * 
	 * @param systemDir
	 *            Path of the directory to analyze containing the corpus.txt and
	 *            mapping.txt files. It also contains the lda directory with the
	 *            optimized model.
	 * @param ntopics
	 */
	public TopicModelSearcher(String systemDir, int ntopics) {
		super();

		this.ntopics = ntopics;

		this.corpusPath = systemDir + "corpus.txt";
		this.mappingPath = systemDir + "mapping.txt";
		this.ldaDir = systemDir + "lda/";
		this.indexPath = ldaDir + "index/";
		this.ldaHelper = ldaDir + "ldaHelper.obj";
		this.resultDirPath = ldaDir + "results/";
		this.resultSummaryPath = ldaDir + "results.txt";
		this.system = StringUtils.getSystemName(systemDir);
	}

	private void index() {
		System.out.println("Indexing " + corpusPath +"...");
		String[] args = { corpusPath, indexPath, ldaHelper, "--fileCodes", mappingPath, "--ldaConfig",
				ntopics + "," + ldaDir };
		try {
			IndexDirectory.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param queryFilePath
	 *            File containing the queries to be executed in the model. Each
	 *            line in the query file represents a query. The first word in
	 *            the line (before the first space) is the query identifier. The
	 *            rest of the words form the query.
	 */
	public void search(String queryFilePath) {
		index();

		this.queriesPath = queryFilePath;

		String[] args = { indexPath, ldaHelper, queriesPath, resultDirPath, "--K", String.valueOf(ntopics),
				"--scoringCode", "1" };
		try {
			LDAQueryAllInDirectory.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param targetFilePath
	 *            File containing the target documents for the queries. Each
	 *            line represents a target document for a query. The first word
	 *            in the line (before the first space) is the query identifier.
	 *            The rest of the line is the name of the target document.
	 */
	public void summarizeResults(String targetFilePath) {
		try {
			FileWriter writer = new FileWriter(resultSummaryPath);
			int numDocs = extractNumDocs();
			HashMap<Integer, Set<String>> targetDocs = loadTargetFile(targetFilePath);

			List<Integer> queries = new ArrayList<>(targetDocs.keySet());
			Collections.sort(queries);
			for (Integer queryId : queries) {
				HashMap<String, ResultHolder> queryResults = loadQueryResults(queryId);

				for (String target : targetDocs.get(queryId)) {
					StringBuilder out = new StringBuilder(system + ";");
					out.append(queryId + ";");
					out.append(target + ";");
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

	private int extractNumDocs() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(corpusPath)));
		String line;
		int n = 0;
		if ((line = reader.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				n = Integer.valueOf(line);
			}
		}
		reader.close();
		return n;
	}

	private HashMap<Integer, Set<String>> loadTargetFile(String targetFilePath) throws IOException {
		HashMap<Integer, Set<String>> targetDocs = new HashMap<Integer, Set<String>>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(targetFilePath)));
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				int index = line.indexOf(' ');
				Integer queryId = Integer.valueOf(line.substring(0, index));
				String target = line.substring(index + 1);

				Set<String> targetSet = null;
				if (targetDocs.containsKey(queryId)) {
					targetSet = targetDocs.get(queryId);

				} else {
					targetSet = new HashSet<String>();
				}
				targetSet.add(target);
				targetDocs.put(queryId, targetSet);
			}
		}

		reader.close();
		return targetDocs;
	}

	private HashMap<String, ResultHolder> loadQueryResults(Integer queryId) throws FileNotFoundException, IOException {
		HashMap<String, ResultHolder> queryResults = new HashMap<String, ResultHolder>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(resultDirPath + queryId)));
		String line;
		int rank = 0;
		while ((line = reader.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				rank++;
				int index = line.indexOf(';');
				String target = line.substring(0, index);
				double score = Double.parseDouble(line.substring(index + 1));

				ResultHolder result = new ResultHolder(rank, score);
				queryResults.put(target, result);
			}
		}

		reader.close();
		return queryResults;
	}

}
