package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25.BM25RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.exception.ParameterException;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalSearcherInitializeTest {

	private static Map<String, String> params;
	@SuppressWarnings("unused")
	private BM25RetrievalSearcher searcher;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		params = new HashMap<String, String>();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/*
	 * Wrong formatted conf file
	 */
	@Test(expected = SearchException.class)
	public void testRetrievalSearcherWrongFormattedParams()
			throws SearchException, IOException {
		params.put("k1", "1asfadf");
		params.put("b", "kafkladjf");
		searcher = new BM25RetrievalSearcher(TestUtils.NOT_EMPTY_INDEX_FOLDER,
				params);
	}

	/*
	 * Empty values of parameters
	 */
	@Test(expected = SearchException.class)
	public void testRetrievalSearcherEmptyParams() throws SearchException,
			IOException {
		params.put("k1", "");
		params.put("b", "");
		searcher = new BM25RetrievalSearcher(TestUtils.NOT_EMPTY_INDEX_FOLDER,
				params);
	}

	/*
	 * Null values of parameters
	 */
	@Test(expected = SearchException.class)
	public void testRetrievalSearcherNullParams() throws SearchException,
			ParameterException, IOException {
		params.put("k1", null);
		params.put("b", null);
		searcher = new BM25RetrievalSearcher(TestUtils.NOT_EMPTY_INDEX_FOLDER,
				params);
	}

}
