package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultParamsParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25.BM25RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25.BM25RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalSearcherTest {
	static RetrievalSearcher searcher;
	private static BM25RetrievalIndexer indexer;
	private static DefaultRetrievalParser parser;
	private static File idxFile;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// initialize the class
		indexer = new BM25RetrievalIndexer();
		parser = new DefaultRetrievalParser();

		// get the configuration parameters
		ParamParser paramParser = new DefaultParamsParser();
		Map<String, String> params = paramParser
				.readParamFile(TestUtils.CONF_FILE_PATH);
		String indexPath = ParameterUtils.getIndexFolderPath(params);

		// create the index folder
		idxFile = new File(indexPath);
		FileUtils.deleteDirectory(idxFile);
		idxFile.mkdir();

		// build the index
		List<RetrievalDoc> docs = parser.readCorpus(ParameterUtils
				.getCorpFilePath(params), TestUtils.MAPPING_FILE_PATH);
		indexer.buildIndex(indexPath, docs, null);

		// initialize the searcher
		searcher = new BM25RetrievalSearcher(indexPath, params);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		searcher.close();
		FileUtils.deleteDirectory(idxFile);

	}

	/*
	 * normal test
	 */
	@Test
	public void testSearchQuery() throws SearchException {
		Query query = new Query();
		query.setTxt("list command clistcommand list command clistcommand refresh refresh m_refresh refresh");

		List<RetrievalDoc> results = searcher.searchQuery(query);

		RetrievalDoc doc = results.get(0);
		assertEquals(new Integer(368), doc.getDocId());

	}

	/*
	 * null query
	 */
	@Test(expected = SearchException.class)
	public void testSearchQueryNullQuery() throws SearchException {

		searcher.searchQuery(null);

	}

	/*
	 * empty query
	 */
	@Test(expected = SearchException.class)
	public void testSearchQueryEmptyQuery() throws SearchException {

		Query query = new Query();
		query.setTxt("");

		searcher.searchQuery(query);

	}

	/*
	 * wrong query
	 */
	@Test
	public void testSearchQueryWrongQuery() throws SearchException {

		Query query = new Query();
		query.setTxt("123934758237591235817");

		List<RetrievalDoc> results = searcher.searchQuery(query);

		assertEquals(0, results.size());

	}
}
