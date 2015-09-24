package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.EvaluationException;

/*
 */
public interface RetrievalEvaluator {

	public List<Double> evaluateRelJudgment(RelJudgment relJudgment,
			List<RetrievalDoc> retrievedDocList) throws EvaluationException;

	public RetrievalStats evaluateModel(Map<Query, List<Double>> queryEvals)
			throws EvaluationException;

}