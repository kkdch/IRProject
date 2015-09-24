package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalEvaluator;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalProcessor;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalTopicModeler;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalWriter;
import edu.wayne.cs.severe.ir4se.processor.controllers.factory.RetrievalFactory;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.entity.TopicDistribution;
import edu.wayne.cs.severe.ir4se.processor.utils.FileRetrievalUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;

public class DefaultRetrievalProcessor implements RetrievalProcessor {

	@Override
	public void processSystem(String filepath) throws Exception {

		// parse the configuration file
		ParamParser parParser = new DefaultParamsParser();
		Map<String, String> params = parParser.readParamFile(filepath);
		String resultFileName = ParameterUtils.getResultFileName(params);
		System.out.println("Running configuration: " + resultFileName);

		// read the corpus
		RetrievalParser parser = new DefaultRetrievalParser();
		System.out.println("- Reading the corpus...");
		List<RetrievalDoc> docs = parser.readCorpus(
				ParameterUtils.getCorpFilePath(params),
				ParameterUtils.getDocMapPath(params));

		// get the retrieval from the configuration file
		String retModel = ParameterUtils.getRetrievalModel(params);

		// index the corpus
		RetrievalIndexer indexer = RetrievalFactory.createIndexer(retModel);
		String indxFolder = ParameterUtils.getIndexFolderPath(params);
		FileRetrievalUtils.createEmptyFolder(indxFolder);
		System.out.println("- Indexing...");
		indexer.buildIndex(indxFolder, docs, params);

		// create the topic distributions
		//FIXME: PLSA is not working
		TopicDistribution topicDistr;
		RetrievalTopicModeler topicModeler = null;
		try {
			topicModeler = RetrievalFactory.getTopicModeler(indxFolder,
					retModel);
			topicDistr = null;
			if (topicModeler != null) {
				System.out.println("- Creating the topic distributions...");
				topicDistr = topicModeler.createTopicDistr(docs, params);
			}
		} finally {
			if (topicModeler != null) {
				topicModeler.closeIndex();
			}
		}

		// read the queries
		System.out.println("- Reading the queries...");
		List<Query> queries = parser.readQueries(ParameterUtils
				.getQueriesFilePath(params));

		// initialize the searcher and evaluator
		RetrievalSearcher searcher = RetrievalFactory.getSearcher(retModel,
				params, indxFolder, topicDistr);
		RetrievalEvaluator evaluator = new DefaultRetrievalEvaluator();

		// initialize the relevance judgments
		System.out.println("- Reading the relevance judgments...");
		Map<Query, RelJudgment> relJudments = parser.readReleJudgments(
				ParameterUtils.getRelJudFilePath(params),
				ParameterUtils.getDocMapPath(params));

		// create the query evaluation results
		Map<Query, List<Double>> queryEvals = new LinkedHashMap<Query, List<Double>>();

		// search for every query
		System.out.println("- Searching...");
		try {
			for (Query query : queries) {

				// get the results
				List<RetrievalDoc> results = null;
				try {
					results = searcher.searchQuery(query);
				} catch (Exception e) {
					System.out.println(e);
				}

				// evaluate the relevance judgments
				RelJudgment relJudgment = relJudments.get(query);

				if (relJudgment.getRelevantDocs().isEmpty()) {
					System.out.println("No rel jud evaluation for query: "
							+ query.getTxt());
				}
				List<Double> resultsRelJud = evaluator.evaluateRelJudgment(
						relJudgment, results);

				// store the results of the evaluation
				queryEvals.put(query, resultsRelJud);
			}
		} finally {
			if (searcher != null) {
				searcher.close();
			}
		}

		// evaluate the retrieval model
		System.out.println("- Evaluating the model...");
		RetrievalStats modelStats = evaluator.evaluateModel(queryEvals);

		// write the results
		RetrievalWriter writer = new DefaultRetrievalWriter();

		System.out.println("- Writing the results...");
		String resFilePath = ParameterUtils.getResultsFilePath(params);
		File resFolder = new File(resFilePath);
		if (!resFolder.exists()) {
			resFolder.mkdir();
		}
		FileRetrievalUtils.removeFile(ParameterUtils.getStatsFilePath(params));
		writer.writeStats(modelStats, ParameterUtils.getStatsFilePath(params));

		System.out.println("- Done!");

	}
}
