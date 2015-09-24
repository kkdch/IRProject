package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalEvaluator;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.EvaluationException;

public class DefaultRetrievalEvaluator implements RetrievalEvaluator {

	@Override
	public List<Double> evaluateRelJudgment(RelJudgment relJudgment,
			List<RetrievalDoc> retrievedDocList) throws EvaluationException {

		if (relJudgment == null || retrievedDocList == null) {
			throw new EvaluationException("The parameters cannot be null");
		}

		if (relJudgment != null) {
			if (relJudgment.getRelevantDocs() == null) {
				throw new EvaluationException(
						"The relevant judgements cannot be null");
			}
		}

		List<Double> queryStats = new ArrayList<Double>();
		List<RetrievalDoc> relJudgDocs = relJudgment.getRelevantDocs();
		double rank = 0.0;

		List<Integer> possibleRanks = new ArrayList<Integer>();

		for (RetrievalDoc relJudgDoc : relJudgDocs) {

			int indexOf = retrievedDocList.indexOf(relJudgDoc);
			if (indexOf != -1) {
				possibleRanks.add(indexOf);
			}
		}

		if (!possibleRanks.isEmpty()) {
			rank = Collections.min(possibleRanks) + 1;
		}

		queryStats.add(rank);
		if (rank != 0.0) {
			queryStats.add(1 / rank);
		} else {
			queryStats.add(rank);
		}

		return queryStats;
	}

	@Override
	public RetrievalStats evaluateModel(Map<Query, List<Double>> queryEvals)
			throws EvaluationException {

		if (queryEvals == null) {
			throw new EvaluationException("No retrieval evaluation data");
		}

		RetrievalStats stats = new RetrievalStats();
		stats.setQueryStats(queryEvals);

		Double meanRecipRank = 0.0;

		double sum = 0.0;
		Set<Entry<Query, List<Double>>> entrySet = queryEvals.entrySet();
		for (Entry<Query, List<Double>> entry : entrySet) {
			sum += entry.getValue().get(1);
		}
		if (!queryEvals.isEmpty()) {
			meanRecipRank = sum / queryEvals.size();
		}

		stats.setMeanRecipRank(meanRecipRank);

		return stats;
	}
}