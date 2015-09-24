package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalWriter;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultParamsParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalWriter;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.WritingException;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalWriterTest {

	private static RetrievalWriter writer;
	private static Map<String, String> params;
	private static String statsFilePath;
	private static File resFolder;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		writer = new DefaultRetrievalWriter();

		// get the configuration parameters
		ParamParser paramParser = new DefaultParamsParser();
		params = paramParser.readParamFile(TestUtils.CONF_FILE_PATH);

		String resFilePath = ParameterUtils.getResultsFilePath(params);
		resFolder = new File(resFilePath);
		if (!resFolder.exists()) {
			resFolder.mkdir();
		}

		statsFilePath = ParameterUtils.getStatsFilePath(params);
		File f = new File(statsFilePath);
		if (f.exists()) {
			f.delete();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File f = new File(statsFilePath);
		if (f.exists()) {
			f.delete();
		}
		FileUtils.deleteDirectory(resFolder);
	}

	/**
	 * basic scenario
	 * 
	 * @throws WritingException
	 */
	@Test
	public void testWriteStats() throws WritingException {

		RetrievalStats stats = new RetrievalStats();

		Map<Query, List<Double>> queryStats = new LinkedHashMap<Query, List<Double>>();
		Query query = new Query();
		query.setQueryId(0);

		List<Double> retStat = new ArrayList<>();
		retStat.add(0.28376283);
		retStat.add(0.123);

		queryStats.put(query, retStat);

		stats.setQueryStats(queryStats);
		stats.setMeanRecipRank(0.1231231);
		writer.writeStats(stats, statsFilePath);

	}

	/**
	 * null parameters
	 * 
	 * @throws WritingException
	 */
	@Test(expected = WritingException.class)
	public void testWriteStatsNullParams() throws WritingException {
		writer.writeStats(null, null);
	}

	/**
	 * null parameters
	 * 
	 * @throws WritingException
	 */
	@Test(expected = WritingException.class)
	public void testWriteStatsNullParams2() throws WritingException {
		writer.writeStats(null, statsFilePath);
	}

	/**
	 * null parameters
	 * 
	 * @throws WritingException
	 */
	@Test(expected = WritingException.class)
	public void testWriteStatsNullParams3() throws WritingException {
		RetrievalStats stats = new RetrievalStats();

		Map<Query, List<Double>> queryStats = new LinkedHashMap<Query, List<Double>>();
		Query query = new Query();
		query.setQueryId(0);

		List<Double> retStat = new ArrayList<>();
		retStat.add(0.28376283);
		retStat.add(0.123);

		queryStats.put(query, retStat);

		stats.setQueryStats(queryStats);
		stats.setMeanRecipRank(0.1231231);
		writer.writeStats(stats, null);
	}

}
