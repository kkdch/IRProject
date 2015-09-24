package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalTopicModeler;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25.BM25RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.plsa.PLSATopicModeler;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalTopicModelerTest {

	private static RetrievalIndexer indexer;
	private static RetrievalParser parser;
	private static RetrievalTopicModeler modeler;
	private static File idxFile;
	private static List<RetrievalDoc> docs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		indexer = new BM25RetrievalIndexer();
		parser = new DefaultRetrievalParser();

		idxFile = new File(TestUtils.PLSA_INDEX_PATH);
		FileUtils.deleteDirectory(idxFile);
		idxFile.mkdir();

		docs = parser.readCorpus(TestUtils.PLSA_CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		indexer.buildIndex(TestUtils.PLSA_INDEX_PATH, docs, null);

		modeler = new PLSATopicModeler(TestUtils.PLSA_INDEX_PATH);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		modeler.closeIndex();
		FileUtils.deleteDirectory(idxFile);
	}

	@Test
	public void testCreateTopicDistr() throws Exception {
		Map<String, String> params = new HashMap<String, String>();

		ParameterUtils.setNumberOfIterations(params, "100");
		ParameterUtils.setNumberOfTopics(params, "8");

		modeler.createTopicDistr(docs, params);
	}

}
