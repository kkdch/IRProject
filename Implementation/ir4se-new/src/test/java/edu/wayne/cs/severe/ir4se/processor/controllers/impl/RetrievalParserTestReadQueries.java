package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultParamsParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.exception.QueryException;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalParserTestReadQueries {
	static RetrievalParser parser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DefaultRetrievalParser();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/*
	 * check the query list
	 */
	@Test
	public void testReadQueriesCheckQueryList() throws QueryException {
		List<Query> queryList = new ArrayList<Query>();
		queryList = parser.readQueries(TestUtils.QUERY_FILE_PATH);

		Integer expectedQueryId = 3;
		Query actualQuery = queryList.get(3);

		String expectedQuery = "renam file exist name  updat file list";

		assertEquals(expectedQuery, actualQuery.getTxt());
		assertEquals(expectedQueryId, actualQuery.getQueryId());

		for (int i = 0; i < queryList.size(); i++) {
			Query query = queryList.get(i);
			System.out.println(query);
		}
	}

	/*
	 * check the query list 2
	 */
	@Test
	public void testReadQueriesCheckQueryList2() throws Exception {

		// get the configuration parameters
		ParamParser paramParser = new DefaultParamsParser();
		Map<String, String> params = paramParser
				.readParamFile(TestUtils.CONF_FILE_DUMMY_PATH);

		List<Query> queryList = parser.readQueries(ParameterUtils
				.getQueriesFilePath(params));
		Integer expectedQueryId = 2;
		String expectedQuery = "set us medium larg icon";
		Query actualQuery = queryList.get(2);

		assertEquals(expectedQuery, actualQuery.getTxt());
		assertEquals(expectedQueryId, actualQuery.getQueryId());
		assertEquals(3, queryList.size());

	}

	/*
	 * file is not valid, the file is a directory
	 */
	@Test(expected = QueryException.class)
	public void testReadQueriesInvalidFile() throws QueryException {
		parser.readQueries(TestUtils.INVALID_QUERY_FILE_PATH);

	}

	/*
	 * file null
	 */
	@Test(expected = NullPointerException.class)
	public void testReadQueriesInvalidFileNull() throws QueryException {
		parser.readQueries(null);

	}

	/*
	 * file doesn't exist
	 */
	@Test(expected = QueryException.class)
	public void testReadQueriesFileNotExist() throws QueryException {
		parser.readQueries(TestUtils.NOT_EXIST_QUERY_FILE_PATH);

	}

	/*
	 * query file is in wrong format
	 */
	@Test
	public void testReadQueriesWrongFoemattedFile() throws QueryException {
		List<Query> queryList = new ArrayList<Query>();
		queryList = parser
				.readQueries(TestUtils.WRONG_FORMATTED_QUERY_FILE_PATH);
		Integer expectedQueryId = 3;
		String expectedQuery = "renam file exist name  updat file list";
		Query actualQuery = queryList.get(3);
		assertEquals(expectedQuery, actualQuery.getTxt());
		assertEquals(expectedQueryId, actualQuery.getQueryId());

	}

}
