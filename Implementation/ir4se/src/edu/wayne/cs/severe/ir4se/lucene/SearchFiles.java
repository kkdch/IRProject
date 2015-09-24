package edu.wayne.cs.severe.ir4se.lucene;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchFiles {

	// private static int documentType; // 1 - file document; 2 - paragraph
	// document;

	private SearchFiles() {
	}

	@SuppressWarnings("deprecation")
	public static void search(String system, int documentType, int queryNumber, String indexDirectoryPath,
			String queryString, String fileOutput, String[] targetClasses, boolean runIndividualTerms, boolean append)
			throws Exception {

		String index = indexDirectoryPath;
		FileWriter f = new FileWriter(index + "../NotFound.txt", true);

		for (int i = 0; i < targetClasses.length; i++) {
			String target = targetClasses[i];
			boolean found = Indexer.isFileDocInIndex(indexDirectoryPath, target);
			if (!found)
				f.append("Target doc " + i + " - " + target + " not found in index!\n");
		}
		f.close();
		IndexReader reader = IndexReader.open(FSDirectory.open(new File(index)), true);

		int numDocs = reader.numDocs();
		System.out.println("The number of documents in the index is: " + numDocs);

		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

		String[] fields;
		fields = new String[1];
		fields[0] = "contents";

		if (!runIndividualTerms) {
			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, fields, analyzer);
			int hitsPerPage = numDocs;
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			Query query = parser.parse(queryString);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			System.out.println("The number of hits is: " + hits.length);

			// file with the results (score and position) only for the relevant
			// documents
			// the file contains entries in the following format:
			// (queryNumber,relDoc1,posRelDoc1,scoreRelDoc1,relDoc2,posRelDoc2,scoreRelDoc2,...)
			FileWriter fwRelevant = new FileWriter(fileOutput, append);

			String path = "";
			String docName = "";
			String docPathAndName = "";
			for (String target : targetClasses) {
				boolean found = false;
				for (int i = 0; i < hits.length; i++) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					path = d.get("path");

					float score = hits[i].score;

					if (documentType == 2) {
						docName = d.get("docName");

						docPathAndName = path.toLowerCase() + "." + docName.toLowerCase();

						if (target.equalsIgnoreCase(docPathAndName)) {
							fwRelevant.write(system + ";" + queryNumber + ";" + target + ";" + (i + 1) + ";"
									+ hits.length + ";" + numDocs + ";" + score + "\n");
							found = true;
							break;
						}
					} else if (documentType == 1) {
						File pathDir = new File(path.trim());
						String fileName = pathDir.getName();
						docName = fileName.replaceAll(".txt", "");
						fwRelevant.write((i + 1) + ". doc = " + docName + " score = " + score + "\n");
					}
				}
				if (found == false)
					fwRelevant.write(system + ";" + queryNumber + ";" + target + "; NOT_RETRIEVED" + "\n");

			}
			// fw.close();
			fwRelevant.close();
			reader.close();
		} else // runIndividualTerms = true
		{
			/**
			 * each query will be divided in its constituent terms and each term
			 * will be run as a separate query
			 **/
			/**
			 * this is useful to determine the similarity of each of the terms
			 * in a query to a target document so that we determine which terms
			 * in the query tend to lead to the best results, i.e., to finding
			 * the targets sooner
			 **/

			SearchFiles.search(system, documentType, queryNumber, indexDirectoryPath, queryString, fileOutput
					.replaceAll(".txt", "_wholeQuery.txt"), targetClasses, false, append);

			FileWriter fw = new FileWriter(fileOutput.replaceAll(".txt", "_terms.txt"));
			fw.write("\n\n\n------------------------------------------------------------------------------------\n\n");
			fw.write("                               Results for query " + queryNumber + "\n");
			fw.write("------------------------------------------------------------------------------------\n\n");

			// file with the results (score and position) only for the relevant
			// documents
			// the file contains entries in the following format:
			// (queryNumber,term1,term1TF,term1DF,relDoc1,posRelDoc1Term1,scoreRelDoc1Term1,relDoc2,posRelDoc2Term1,scoreRelDoc2Term1,...)
			// (queryNumber,term2,term2TF,term2DF,relDoc1,posRelDoc1Term2,scoreRelDoc1Term2,relDoc2,posRelDoc2Term2,scoreRelDoc2Term2,...)
			// ...
			FileWriter fwRelevant = new FileWriter(fileOutput.replaceAll(".txt", "_terms_RelevantDocsPositions.txt"));

			String[] queryTerms = queryString.split(" ");
			for (int l = 0; l < queryTerms.length; l++) {
				MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, fields, analyzer);
				int hitsPerPage = numDocs;
				TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);

				String q = queryTerms[l];
				Query query = parser.parse(q);
				searcher.search(query, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;
				fw.write("TERM " + (l + 1) + ": " + q + "\n\n");
				fwRelevant.write("\n" + queryNumber + "," + q);
				for (int i = 0; i < hits.length; i++) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					String path = d.get("path");
					float score = hits[i].score;
					if (documentType == 2) {
						String docName = d.get("docName");
						fw.write((i + 1) + ". doc = " + path + " " + docName + " - score = " + score + "\n");
						for (int k = 0; k < targetClasses.length; k++) {
							if (docName.equalsIgnoreCase(targetClasses[k])) {
								String contents = d.get("contents");
								int frequency = countOccurrences(contents, q);// tf
								fwRelevant.write("," + frequency);

								fwRelevant.write("," + reader.docFreq(new Term("contents", q)));// df
								fwRelevant.write("," + path + "." + docName + "," + (i + 1) + "," + score);
								break;
							}
						}
					} else if (documentType == 1) {
						File pathDir = new File(path);
						String fileName = pathDir.getName();
						String docName = fileName.replaceAll(".txt", "");
						fw.write((i + 1) + ". doc = " + docName + " score = " + score + "\n");
					}
				}
				fw.write("\n\n\n");
			}
			fw.close();
			f.close();
			fwRelevant.close();
			reader.close();
		}
	}

	public static int countOccurrences(String arg1, String arg2) {
		int count = 0;
		int index = 0;
		while ((index = arg1.indexOf(arg2, index)) != -1) {
			++index;
			++count;
		}
		return count;
	}

	public static void runQueriesFromDir(String system, int documentType, String indexDirectoryPath,
			String queryDirPath, String resultsDirPath, boolean runIndividualTerms) {
		File dirQuery = new File(queryDirPath);
		int queryNumber = 1;
		if (dirQuery.isDirectory()) {
			String[] children = dirQuery.list();
			for (int i = 0; i < children.length; i++) {
				File child = new File(queryDirPath + "/" + children[i]);
				if (child.isFile()) {
					try {
						BufferedReader inQuery = new BufferedReader(new FileReader(child));
						String line;
						String[] targetDocs = null;
						int numberTargetDocs = 0;
						// first line in each query file is the query number
						// second line is the query
						// third line is the number of relevant docs
						// the following lines are the paths and names of the
						// relevant docs
						int k = -3;
						String query = "";
						while ((line = inQuery.readLine()) != null) {
							if (line.trim().length() >= 1) {
								if (k == -3)// first line - query number
									queryNumber = Integer.parseInt(line.trim());
								if (k == -2)// second line - query text
									query = line.trim();
								else {
									if (k == -1)// third line - number of
												// relevant docs
									{
										numberTargetDocs = Integer.parseInt(line.trim());
										targetDocs = new String[numberTargetDocs];
									} else if (k >= 0)// other lines - the
														// relevant docs paths
														// and names
									{
										targetDocs[k] = line.trim();
									}
								}
								k++;

							}
						}
						inQuery.close();
						String resultFilePath = resultsDirPath + "/results_" + child.getName();
						if (runIndividualTerms == false) {
							SearchFiles
									.search(system, documentType, queryNumber, indexDirectoryPath, query, resultFilePath, targetDocs, false, false);
						} else // runIndividualTerms = true
						/**
						 * each query will be divided in its constituent terms
						 * and each term will be run as a separate query
						 **/
						/**
						 * this is useful to determine the similarity of each of
						 * the terms in a query to a target document so that we
						 * determine which terms in the query tend to lead to
						 * the best results, i.e., to finding the targets sooner
						 **/
						{
							SearchFiles
									.search(system, documentType, queryNumber, indexDirectoryPath, query, resultFilePath, targetDocs, true, false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else if (child.isDirectory())
					runQueriesFromDir(system, documentType, indexDirectoryPath, child.getAbsolutePath(), resultsDirPath
							+ "/" + child.getName(), runIndividualTerms);
			}
		}
	}

	public static void runQueriesFromFile(String system, int documentType, String indexDirectoryPath,
			String queryFilePath, String resultsFilePath, boolean runIndividualTerms, boolean append) {
		/*
		 * All queries are located in the same file. The query entries are
		 * separated by a blank line. Each entry consist of: query number (1st
		 * line), query terms (2nd line), number of relevant docs (3rd line),
		 * paths and names of the relevant docs (rest of the lines)
		 */
		File fileQuery = new File(queryFilePath);
		if (fileQuery.isFile()) {
			try {
				BufferedReader inQuery = new BufferedReader(new FileReader(fileQuery));
				String line;
				String[] targetDocs = null;
				int numberTargetDocs = 0;
				int lineNumber = 0;
				int queryNumber = 1;
				String query = "";

				while ((line = inQuery.readLine()) != null) {
					// it is not a blank line
					if (!line.trim().isEmpty()) {
						lineNumber++;
						switch (lineNumber) {
						case 1:
							queryNumber = Integer.parseInt(line.trim());
							break;
						case 2:
							query = line.trim().toLowerCase();
							break;

						case 3:
							numberTargetDocs = Integer.parseInt(line.trim());
							targetDocs = new String[numberTargetDocs];
							break;

						default:
							if (lineNumber >= 4)
								targetDocs[lineNumber - 4] = processPath(line.trim());
							break;
						}
					} else {
						if (!runIndividualTerms) {
							SearchFiles
									.search(system, documentType, queryNumber, indexDirectoryPath, query, resultsFilePath, targetDocs, false, append);
						} else {
							/*
							 * Each query will be divided in its constituent
							 * terms and each term will be run as a separate
							 * query. This is useful to determine the similarity
							 * of each of the terms in a query to a target
							 * document so that we determine which terms in the
							 * query tend to lead to the best results, i.e., to
							 * finding the targets sooner
							 */
							SearchFiles
									.search(system, documentType, queryNumber, indexDirectoryPath, query, resultsFilePath, targetDocs, true, append);
						}
						lineNumber = 0;
					}
				}

				if (!runIndividualTerms) {
					SearchFiles
							.search(system, documentType, queryNumber, indexDirectoryPath, query, resultsFilePath, targetDocs, false, append);
				} else {
					/*
					 * each query will be divided in its constituent terms and
					 * each term will be run as a separate query
					 * 
					 * this is useful to determine the similarity of each of the
					 * terms in a query to a target document so that we
					 * determine which terms in the query tend to lead to the
					 * best results, i.e., to finding the targets sooner
					 */
					SearchFiles
							.search(system, documentType, queryNumber, indexDirectoryPath, query, resultsFilePath, targetDocs, true, append);
				}

				inQuery.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Query file (" + queryFilePath + ") is not valid!");
		}
	}

	/**
	 * This method uses a custom HitCollector implementation which simply prints
	 * out the docId and score of every matching document.
	 * 
	 * This simulates the streaming search use case, where all hits are supposed
	 * to be processed, regardless of their relevance.
	 */
	public static void doStreamingSearch(final IndexSearcher searcher, Query query) throws IOException {
		Collector streamingHitCollector = new Collector() {
			private Scorer scorer;
			private int i = 0;
			private int docBase;

			// simply print docId and score of every matching document
			public void collect(int docNumber) throws IOException {
				try {

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public boolean acceptsDocsOutOfOrder() {
				return true;
			}

			public void setNextReader(IndexReader reader, int docBase) throws IOException {
				this.docBase = docBase;
			}

			public void setScorer(Scorer scorer) throws IOException {
				this.scorer = scorer;
			}
		};

		searcher.search(query, streamingHitCollector);

	}

	public static String processPath(String path) {
		String res = path.toLowerCase();
		res = res.replaceAll("\n", "");
		res = res.replaceAll("\r", " ");
		res = res.replaceAll("\t", " ");
		res = res.replaceAll("/", ".");
		// res = res.replaceAll("::", ".");
		res = res.replaceAll(", ", ",");
		if (res.startsWith("."))
			res = path.replaceFirst(".", "");
		return res;
	}

}
