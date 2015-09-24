package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.CorpusException;
import edu.wayne.cs.severe.ir4se.processor.exception.QueryException;
import edu.wayne.cs.severe.ir4se.processor.exception.RelJudgException;
import edu.wayne.cs.severe.ir4se.processor.utils.ExceptionUtils;

public class DefaultRetrievalParser implements RetrievalParser {

	private List<String> mapDocsStr;

	@Override
	public List<RetrievalDoc> readCorpus(String corpFilePath, String mapDocsPath)
			throws CorpusException {
		int docId = 0;
		String lineCorpus;
		List<RetrievalDoc> docList = new ArrayList<RetrievalDoc>();

		BufferedReader inCorpus = null;

		try {
			// add every document into the list
			inCorpus = new BufferedReader(new FileReader(corpFilePath));

			while ((lineCorpus = inCorpus.readLine()) != null) {

				String lineTrimmed = lineCorpus.trim();
				if (!lineTrimmed.isEmpty()) {

					RetrievalDoc doc = new RetrievalDoc();
					doc.setDocId(docId);
					doc.setDocText(lineTrimmed);

					docList.add(doc);
					docId++;
				}
			}
			if (docId == 0) {
				throw new CorpusException();
			}
		} catch (Exception e) {
			CorpusException e2 = new CorpusException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			try {
				if (inCorpus != null) {
					inCorpus.close();
				}
			} catch (IOException e) {
				CorpusException e2 = new CorpusException(e.getMessage());
				ExceptionUtils.addStackTrace(e, e2);
				throw e2;
			}
		}

		setDocNames(docList, mapDocsPath);

		return docList;
	}

	private void setDocNames(List<RetrievalDoc> docList, String mapDocsPath)
			throws CorpusException {
		String line;
		List<String> docNames = new ArrayList<String>();

		BufferedReader inMapping = null;

		try {
			inMapping = new BufferedReader(new FileReader(mapDocsPath));

			while ((line = inMapping.readLine()) != null) {

				String lineTrimmed = line.trim();
				if (!lineTrimmed.isEmpty()) {
//					docNames.add(lineTrimmed);
					docNames.add(getDocQueryStr(lineTrimmed));
				}
			}
		} catch (Exception e) {
			CorpusException e2 = new CorpusException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			try {
				if (inMapping != null) {
					inMapping.close();
				}
			} catch (IOException e) {
				CorpusException e2 = new CorpusException(e.getMessage());
				ExceptionUtils.addStackTrace(e, e2);
				throw e2;
			}
		}
		for (int i = 0; i < docList.size(); i++) {
			RetrievalDoc doc = docList.get(i);
			doc.setNameDoc(docNames.get(i));
		}

	}

	@Override
	public List<Query> readQueries(String queriesPath) throws QueryException {

		List<Query> queryList = new ArrayList<Query>();
		File fileQuery = new File(queriesPath);

		if (!fileQuery.isFile() || !fileQuery.exists()) {
			throw new QueryException("Query file (" + queriesPath
					+ ") is not valid!");
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileQuery));

			String line;
			int lineNumber = 0;
			Integer queryId = 0;

			Query query = new Query();
			while ((line = reader.readLine()) != null) {

				// it is not a blank line
				if (!line.trim().isEmpty()) {
					lineNumber++;
					switch (lineNumber) {
					case 1:
						query.setQueryId(queryId);
						queryId++;
						break;
					case 2:
						query.setTxt(line.trim());
						queryList.add(query);
						break;
					}
				} else {
					lineNumber = 0;
					query = new Query();
				}
			}

		} catch (Exception e) {
			QueryException e2 = new QueryException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new QueryException(e.getMessage());
				}
			}
		}
		return queryList;
	}

	@Override
	public Map<Query, RelJudgment> readReleJudgments(String releJudgmentPath,
			String mapDocsPath) throws RelJudgException {

		Map<Query, RelJudgment> relJudgMap = new LinkedHashMap<Query, RelJudgment>();
		File fileRelJudg = new File(releJudgmentPath);

		if (!fileRelJudg.isFile() || !fileRelJudg.exists()) {
			throw new RelJudgException("The Relevance Judgments file ("
					+ releJudgmentPath + ") is not valid!");
		}

		File mapDocsFile = new File(mapDocsPath);
		if (!mapDocsFile.isFile() || !mapDocsFile.exists()) {
			throw new RelJudgException("The Mappings file (" + releJudgmentPath
					+ ") is not valid!");
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileRelJudg));

			String line;
			int lineNumber = 0;
			int numberTargetDocs = -1;
			Integer queryId = 0;

			Query query = new Query();
			List<String> targetDocs = null;
			while ((line = reader.readLine()) != null) {

				// it is not a blank line
				if (!line.trim().isEmpty()) {
					lineNumber++;
					switch (lineNumber) {
					case 1:
						query.setQueryId(queryId);
						queryId++;
						break;
					// case 2:
					// query.setTxt(line.trim().toLowerCase());
					// break;
					case 3:
						numberTargetDocs = Integer.parseInt(line.trim());
						targetDocs = new ArrayList<String>(numberTargetDocs);
						break;
					default:
						if (lineNumber >= 4) {
							targetDocs
									.add(lineNumber - 4, getDocQueryStr(line));
						}
						break;
					}
				} else {

					if (targetDocs != null) {
						RelJudgment relJud = new RelJudgment();
						List<RetrievalDoc> relevantDocs = getRelevantDocs(
								targetDocs, mapDocsPath);
						relJud.setRelevantDocs(relevantDocs);
						relJudgMap.put(query, relJud);
					}

					lineNumber = 0;
					numberTargetDocs = -1;
					query = new Query();
					targetDocs = null;
				}
			}

			if (targetDocs != null) {
				RelJudgment relJud = new RelJudgment();
				List<RetrievalDoc> relevantDocs = getRelevantDocs(targetDocs,
						mapDocsPath);
				relJud.setRelevantDocs(relevantDocs);
				relJudgMap.put(query, relJud);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RelJudgException(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RelJudgException(e.getMessage());
				}
			}
		}
		return relJudgMap;
	}

	private List<RetrievalDoc> getRelevantDocs(List<String> targetDocs,
			String mapDocsPath) throws RelJudgException {

		List<RetrievalDoc> relJudgDocs = new ArrayList<>();

		if (mapDocsStr == null) {

			mapDocsStr = new ArrayList<>();

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(mapDocsPath));

				String line;
				while ((line = reader.readLine()) != null) {

					if (line.trim().isEmpty()) {
						continue;
					}
					// mapDocsStr.add();
					String docStr = getDocQueryStr(line);
					// System.out.println(docStr);
					mapDocsStr.add(docStr);

				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new RelJudgException(e.getMessage());
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						throw new RelJudgException(e.getMessage());
					}
				}
			}
		}

		for (String targetDoc : targetDocs) {
			// System.out.println("Searching " + targetDoc);
			int docId = mapDocsStr.indexOf(targetDoc);
			if (docId != -1) {

				RetrievalDoc relJudgDoc = new RetrievalDoc();
				relJudgDoc.setDocId(docId);

				relJudgDocs.add(relJudgDoc);
			}

		}

		// if (relJudgDocs.isEmpty()) {
		// System.out.println("vacio");
		// // throw new RelJudgException(
		// // "Could not find the relevant judgement documents");
		// }

		return relJudgDocs;
	}

	private String getDocQueryStr(String line) {
		String docStr = line.toLowerCase().trim();
		// docStr = docStr.replaceFirst(" ", ".");
		docStr = processPath(docStr);
		// .replaceFirst(" ", ".")
		docStr = docStr.replaceAll(" ", ".");

		// int i = docStr.lastIndexOf("(");
		// if (i == -1) {
		// i = docStr.length();
		// }
		// String subStr = docStr.substring(0, i);
		// i = subStr.lastIndexOf(" ");
		// if (i != -1) {
		// docStr = new StringBuilder(docStr).replace(i, i + 1, ".")
		// .toString();
		// }
		return docStr;
	}

	private String processPath(String path) {
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