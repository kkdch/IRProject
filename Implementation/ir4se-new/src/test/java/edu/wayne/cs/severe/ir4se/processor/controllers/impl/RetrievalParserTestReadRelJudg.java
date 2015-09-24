package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultParamsParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalParserTestReadRelJudg {
	static RetrievalParser parser;

	@Before
	public void setUpBefore() throws Exception {
		parser = new DefaultRetrievalParser();
	}

	@After
	public void tearDownAfter() throws Exception {
	}

	/**
	 * null parameters
	 * 
	 * @throws Exception
	 */
	@Test(expected = NullPointerException.class)
	public void testReadReleJudgmentsNull() throws Exception {
		parser.readReleJudgments(null, null);
	}

	/**
	 * null parameters 2
	 * 
	 * @throws Exception
	 */
	@Test(expected = NullPointerException.class)
	public void testReadReleJudgmentsNull2() throws Exception {
		ParamParser paramParser = new DefaultParamsParser();
		Map<String, String> params = paramParser
				.readParamFile(TestUtils.CONF_FILE_PATH);
		parser.readReleJudgments(ParameterUtils.getRelJudFilePath(params), null);
	}

	/**
	 * null parameters 3
	 * 
	 * @throws Exception
	 */
	@Test(expected = NullPointerException.class)
	public void testReadReleJudgmentsNull3() throws Exception {
		ParamParser paramParser = new DefaultParamsParser();
		Map<String, String> params = paramParser
				.readParamFile(TestUtils.CONF_FILE_PATH);
		parser.readReleJudgments(null, ParameterUtils.getDocMapPath(params));
	}

	/**
	 * basic scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadReleJudgments() throws Exception {

		// get the configuration parameters
		ParamParser paramParser = new DefaultParamsParser();
		Map<String, String> params = paramParser
				.readParamFile(TestUtils.CONF_FILE_PATH);
		Map<Query, RelJudgment> releJudgments = parser.readReleJudgments(
				ParameterUtils.getRelJudFilePath(params),
				ParameterUtils.getDocMapPath(params));

		assertNotNull(releJudgments);
		assertFalse(releJudgments.isEmpty());
		assertEquals(87, releJudgments.size());

		Query qTest = new Query();
		qTest.setQueryId(0);
		RelJudgment relJudgment = releJudgments.get(qTest);

		// build the expected relevance judgments for the first one
		int[] docIds = { 1744, 1745 };
		ArrayList<RetrievalDoc> expected = new ArrayList<RetrievalDoc>();
		for (int docId : docIds) {
			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);
			expected.add(doc);
		}
		assertEquals(expected, relJudgment.getRelevantDocs());

		qTest.setQueryId(releJudgments.size() - 1);
		relJudgment = releJudgments.get(qTest);

		// build the expected relevance judgments for the last one
		int[] docIds2 = { 1597, 1598, 1621 };
		expected = new ArrayList<RetrievalDoc>();
		for (int docId : docIds2) {
			RetrievalDoc doc = new RetrievalDoc();
			doc.setDocId(docId);
			expected.add(doc);
		}
		assertEquals(expected, relJudgment.getRelevantDocs());

	}

	/**
	 * basic scenario (dummy)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadReleJudgmentsDummy() throws Exception {

		// get the configuration parameters
		ParamParser paramParser = new DefaultParamsParser();
		Map<String, String> params = paramParser
				.readParamFile(TestUtils.CONF_FILE_DUMMY_PATH);
		Map<Query, RelJudgment> releJudgments = parser.readReleJudgments(
				ParameterUtils.getRelJudFilePath(params),
				ParameterUtils.getDocMapPath(params));

		// assert the whole relevance judgments
		assertNotNull(releJudgments);
		assertFalse(releJudgments.isEmpty());
		assertEquals(3, releJudgments.size());

		// assert the first one
		Query qTest = new Query();
		qTest.setQueryId(0);
		RelJudgment relJudgment = releJudgments.get(qTest);
		assertEquals(2, relJudgment.getRelevantDocs().size());

		// assert the last one
		qTest.setQueryId(releJudgments.size() - 1);
		relJudgment = releJudgments.get(qTest);
		assertEquals(7, relJudgment.getRelevantDocs().size());

	}

	/**
	 * basic scenario (dummy)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadReleJudgmentsDummy2() throws Exception {

		// get the configuration parameters
		ParamParser paramParser = new DefaultParamsParser();
		Map<String, String> params = paramParser
				.readParamFile(TestUtils.CONF_FILE_DUMMY_PATH2);
		Map<Query, RelJudgment> releJudgments = parser.readReleJudgments(
				ParameterUtils.getRelJudFilePath(params),
				ParameterUtils.getDocMapPath(params));

		// assert the whole relevance judgments
		assertNotNull(releJudgments);
		assertFalse(releJudgments.isEmpty());
		assertEquals(4, releJudgments.size());

		System.out.println(releJudgments);

		// assert the first one
		Query qTest = new Query();
		qTest.setQueryId(0);
		RelJudgment relJudgment = releJudgments.get(qTest);
		assertEquals(1, relJudgment.getRelevantDocs().size());

		 // assert the second
		 qTest.setQueryId(1);
		 relJudgment = releJudgments.get(qTest);
		 assertEquals(2, relJudgment.getRelevantDocs().size());

		 // assert the third
		 qTest.setQueryId(2);
		 relJudgment = releJudgments.get(qTest);
		 assertEquals(1, relJudgment.getRelevantDocs().size());

		 // assert the last one
		 qTest.setQueryId(releJudgments.size() - 1);
		 relJudgment = releJudgments.get(qTest);
		 assertEquals(1, relJudgment.getRelevantDocs().size());

	}
}
