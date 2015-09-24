package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalEvaluator;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalEvaluator;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.EvaluationException;

public class RetrievalEvaluatorTest {

	private static RetrievalEvaluator eval;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		eval = new DefaultRetrievalEvaluator();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * normal scenario, 1rst position
	 */
	@Test
	public void testEvaluateRelJudgmentResFirst() throws EvaluationException {

		// retrieved list
		List<RetrievalDoc> retrievedDocList = new ArrayList<RetrievalDoc>();
		int[] retDocsIds = { 1, 2, 3 };
		for (int docId : retDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			retrievedDocList.add(doc);
		}

		// get the relevant judgments
		RelJudgment relJudgment = new RelJudgment();
		List<RetrievalDoc> relevantDocs = new ArrayList<RetrievalDoc>();
		int[] relJudDocsIds = { 1, 2, 3 };
		for (int docId : relJudDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			relevantDocs.add(doc);
		}
		relJudgment.setRelevantDocs(relevantDocs);

		List<Double> evalRelJud = eval.evaluateRelJudgment(relJudgment,
				retrievedDocList);

		assertEquals(new Double(1), evalRelJud.get(0));
		assertEquals(new Double(1), evalRelJud.get(1));
	}

	/**
	 * normal scenario, 2nd position
	 */
	@Test
	public void testEvaluateRelJudgmentResSecond() throws EvaluationException {

		// retrieved list
		List<RetrievalDoc> retrievedDocList = new ArrayList<RetrievalDoc>();
		int[] retDocsIds = { 5, 2, 3 };
		for (int docId : retDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			retrievedDocList.add(doc);
		}

		// get the relevant judgments
		RelJudgment relJudgment = new RelJudgment();
		List<RetrievalDoc> relevantDocs = new ArrayList<RetrievalDoc>();
		int[] relJudDocsIds = { 1, 2, 3 };
		for (int docId : relJudDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			relevantDocs.add(doc);
		}
		relJudgment.setRelevantDocs(relevantDocs);

		List<Double> evalRelJud = eval.evaluateRelJudgment(relJudgment,
				retrievedDocList);

		assertEquals(new Double(2), evalRelJud.get(0));
		assertEquals(new Double(0.5), evalRelJud.get(1));
	}

	/**
	 * normal scenario, 3rd position
	 */
	@Test
	public void testEvaluateRelJudgmentResThird() throws EvaluationException {

		// retrieved list
		List<RetrievalDoc> retrievedDocList = new ArrayList<RetrievalDoc>();
		int[] retDocsIds = { 5, 10, 3 };
		for (int docId : retDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			retrievedDocList.add(doc);
		}

		// get the relevant judgments
		RelJudgment relJudgment = new RelJudgment();
		List<RetrievalDoc> relevantDocs = new ArrayList<RetrievalDoc>();
		int[] relJudDocsIds = { 1, 2, 3 };
		for (int docId : relJudDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			relevantDocs.add(doc);
		}
		relJudgment.setRelevantDocs(relevantDocs);

		List<Double> evalRelJud = eval.evaluateRelJudgment(relJudgment,
				retrievedDocList);

		assertEquals(new Double(3), evalRelJud.get(0));
		assertEquals(new Double(1.0 / 3.0), evalRelJud.get(1));
	}

	/**
	 * normal scenario, no matches
	 */
	@Test
	public void testEvaluateRelJudgmentNoMatches() throws EvaluationException {

		// retrieved list
		List<RetrievalDoc> retrievedDocList = new ArrayList<RetrievalDoc>();
		int[] retDocsIds = { 5, 10, 12, 231, 1324, 1234, 1234, 1234, 134, 134,
				1234 };
		for (int docId : retDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			retrievedDocList.add(doc);
		}

		// get the relevant judgments
		RelJudgment relJudgment = new RelJudgment();
		List<RetrievalDoc> relevantDocs = new ArrayList<RetrievalDoc>();
		int[] relJudDocsIds = { 1, 2, 3, -1, -2 };
		for (int docId : relJudDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			relevantDocs.add(doc);
		}
		relJudgment.setRelevantDocs(relevantDocs);

		List<Double> evalRelJud = eval.evaluateRelJudgment(relJudgment,
				retrievedDocList);

		assertEquals(new Double(0.0), evalRelJud.get(0));
		assertEquals(new Double(0.0), evalRelJud.get(1));
	}

	/**
	 * no relevant judgments
	 */
	@Test
	public void testEvaluateRelJudgmentNoRJ() throws EvaluationException {

		// retrieved list
		List<RetrievalDoc> retrievedDocList = new ArrayList<RetrievalDoc>();
		int[] retDocsIds = { 5, 10, 12 };
		for (int docId : retDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			retrievedDocList.add(doc);
		}

		// get the relevant judgments
		RelJudgment relJudgment = new RelJudgment();
		List<RetrievalDoc> relevantDocs = new ArrayList<RetrievalDoc>();
		int[] relJudDocsIds = {};
		for (int docId : relJudDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			relevantDocs.add(doc);
		}
		relJudgment.setRelevantDocs(relevantDocs);

		List<Double> evalRelJud = eval.evaluateRelJudgment(relJudgment,
				retrievedDocList);

		assertEquals(new Double(0.0), evalRelJud.get(0));
		assertEquals(new Double(0.0), evalRelJud.get(1));
	}

	/**
	 * no retrieved docs
	 */
	@Test
	public void testEvaluateRelJudgmentNoRetDocs() throws EvaluationException {

		// retrieved list
		List<RetrievalDoc> retrievedDocList = new ArrayList<RetrievalDoc>();
		int[] retDocsIds = {};
		for (int docId : retDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			retrievedDocList.add(doc);
		}

		// get the relevant judgments
		RelJudgment relJudgment = new RelJudgment();
		List<RetrievalDoc> relevantDocs = new ArrayList<RetrievalDoc>();
		int[] relJudDocsIds = { 1, 2, 3 };
		for (int docId : relJudDocsIds) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);

			relevantDocs.add(doc);
		}
		relJudgment.setRelevantDocs(relevantDocs);

		List<Double> evalRelJud = eval.evaluateRelJudgment(relJudgment,
				retrievedDocList);

		assertEquals(new Double(0.0), evalRelJud.get(0));
		assertEquals(new Double(0.0), evalRelJud.get(1));
	}

	@Test(expected = EvaluationException.class)
	public void testEvaluateRelJudgmentNull() throws EvaluationException {
		eval.evaluateRelJudgment(null, null);
	}

	@Test(expected = EvaluationException.class)
	public void testEvaluateModelNull() throws EvaluationException {
		eval.evaluateModel(null);
	}

	@Test
	public void testEvaluateModelNoData() throws EvaluationException {

		Map<Query, List<Double>> queryEvals = new HashMap<Query, List<Double>>();
		RetrievalStats stats = eval.evaluateModel(queryEvals);
		Double meanRecipRank = stats.getMeanRecipRank();

		assertEquals(new Double(0.0), meanRecipRank);
	}

	@Test
	public void testEvaluateModelOneResultZero() throws EvaluationException {

		Map<Query, List<Double>> queryEvals = new HashMap<Query, List<Double>>();

		List<Double> qStats = new ArrayList<Double>();
		qStats.add(1.0);
		qStats.add(0.0);

		Query query = new Query(1);
		queryEvals.put(query, qStats);

		RetrievalStats stats = eval.evaluateModel(queryEvals);
		Double meanRecipRank = stats.getMeanRecipRank();

		assertEquals(new Double(0.0), meanRecipRank);
	}

	@Test
	public void testEvaluateModelOneResult() throws EvaluationException {

		Map<Query, List<Double>> queryEvals = new HashMap<Query, List<Double>>();

		List<Double> qStats = new ArrayList<Double>();
		qStats.add(1.0);
		qStats.add(0.345353);

		Query query = new Query(1);
		queryEvals.put(query, qStats);

		RetrievalStats stats = eval.evaluateModel(queryEvals);
		Double meanRecipRank = stats.getMeanRecipRank();

		assertEquals(new Double(0.345353), meanRecipRank);
	}

	@Test
	public void testEvaluateModelTwoResults() throws EvaluationException {

		Map<Query, List<Double>> queryEvals = new HashMap<Query, List<Double>>();

		List<Double> qStats = new ArrayList<Double>();
		qStats.add(1.0);
		qStats.add(0.345353);
		Query query = new Query(1);
		queryEvals.put(query, qStats);

		qStats = new ArrayList<Double>();
		qStats.add(2.0);
		qStats.add(0.45634643563);
		query = new Query(2);
		queryEvals.put(query, qStats);

		RetrievalStats stats = eval.evaluateModel(queryEvals);
		Double meanRecipRank = stats.getMeanRecipRank();

		assertEquals(new Double((0.345353 + 0.45634643563) / 2), meanRecipRank);
	}

	@Test
	public void testEvaluateModelNResults() throws EvaluationException {

		Map<Query, List<Double>> queryEvals = new HashMap<Query, List<Double>>();

		int[] ranks = { 2, 4, 7, 3, 2, 7, 9, 4, 4, 2, 4, 5 };

		double sum = 0.0;
		for (int i = 0; i < ranks.length; i++) {
			double rank = ranks[i];

			List<Double> qStats = new ArrayList<Double>();
			qStats.add((double) i);
			qStats.add(1.0 / rank);
			Query query = new Query(i);
			queryEvals.put(query, qStats);

			sum += (1 / rank);
		}

		RetrievalStats stats = eval.evaluateModel(queryEvals);
		Double meanRecipRank = stats.getMeanRecipRank();

		assertFalse(meanRecipRank == 0.0);
		assertEquals(new Double(sum / ranks.length), meanRecipRank);
	}
}
