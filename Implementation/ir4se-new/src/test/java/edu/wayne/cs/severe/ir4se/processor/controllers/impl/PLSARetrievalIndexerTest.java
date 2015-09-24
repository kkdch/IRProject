package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultParamsParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.plsa.PLSARetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class PLSARetrievalIndexerTest {

	private RetrievalIndexer indexer;
	private RetrievalParser parser;
	private static File indexFolder;
	private Map<String, String> params;

	@Before
	public void setUpBefore() throws Exception {

		indexer = new PLSARetrievalIndexer();
		parser = new DefaultRetrievalParser();
		DefaultParamsParser parParser = new DefaultParamsParser();
		params = parParser.readParamFile(TestUtils.CONF_FILE_PATH_PLSA);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(indexFolder);
		Thread.sleep(3000);
		String plsaFolder = ParameterUtils.getTopicDistrPath(params);
		System.out.println("removing "+plsaFolder);
		FileUtils.deleteDirectory(new File(plsaFolder));
	}

	@Test
	public void testBuildIndex() throws Exception {

		List<RetrievalDoc> docs = parser.readCorpus(
				ParameterUtils.getCorpFilePath(params),
				ParameterUtils.getDocMapPath(params));

		String indexPath = ParameterUtils.getIndexFolderPath(params);
		indexFolder = new File(indexPath);
		FileUtils.deleteDirectory(indexFolder);
		indexFolder.mkdirs();

		indexer.buildIndex(indexPath, docs, params);
	}

}
