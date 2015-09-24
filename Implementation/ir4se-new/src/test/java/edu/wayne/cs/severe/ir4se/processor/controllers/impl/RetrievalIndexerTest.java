package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25.BM25RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.CorpusException;
import edu.wayne.cs.severe.ir4se.processor.exception.IndexerException;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalIndexerTest {

	private RetrievalIndexer indexer;
	private RetrievalParser parser;
	private static File indexFolder;

	@Before
	public void setUpBefore() throws Exception {

		indexFolder = new File(TestUtils.INDEX_FILE_PATH);
		FileUtils.deleteDirectory(indexFolder);
		indexFolder.mkdirs();
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(indexFolder);
	}

	/*
	 * index checking
	 */
	@Test
	public void testBuildIndex() throws IndexerException, CorpusException,
			IOException {
		indexer = new BM25RetrievalIndexer();
		parser = new DefaultRetrievalParser();
		List<RetrievalDoc> docs = new ArrayList<RetrievalDoc>();
		docs = parser.readCorpus(TestUtils.CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		indexer.buildIndex(TestUtils.INDEX_FILE_PATH, docs, null);
		IndexReader indexReader = null;
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(new File(
					TestUtils.INDEX_FILE_PATH)));
			int actualDocNum = indexReader.numDocs();
			int expectDocNum = docs.size();
			assertEquals(expectDocNum, actualDocNum);
		} finally {
			if (indexReader != null) {
				indexReader.close();
			}
		}

	}

	/*
	 * null index path
	 */
	@Test(expected = NullPointerException.class)
	public void testBuildIndexNullIndexPath() throws IndexerException,
			CorpusException {
		indexer = new BM25RetrievalIndexer();
		DefaultRetrievalParser parser = new DefaultRetrievalParser();
		List<RetrievalDoc> docs = new ArrayList<RetrievalDoc>();
		docs = parser.readCorpus(TestUtils.CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		indexer.buildIndex(TestUtils.NULL_INDEX_FILE_PATH, docs, null);
	}

	/*
	 * index folder not empty
	 */
	@Test(expected = IndexerException.class)
	public void testBuildIndexIndexFolderNotEmpty() throws IndexerException,
			CorpusException {
		BM25RetrievalIndexer indexer = new BM25RetrievalIndexer();
		DefaultRetrievalParser parser = new DefaultRetrievalParser();
		List<RetrievalDoc> docs = new ArrayList<RetrievalDoc>();
		docs = parser.readCorpus(TestUtils.CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		indexer.buildIndex(TestUtils.NOT_EMPTY_INDEX_FOLDER, docs, null);
	}

	/*
	 * index folder not exist
	 */
	@Test(expected = IndexerException.class)
	public void testBuildIndexIndexFolderNotExist() throws IndexerException,
			CorpusException {
		BM25RetrievalIndexer indexer = new BM25RetrievalIndexer();
		DefaultRetrievalParser parser = new DefaultRetrievalParser();
		List<RetrievalDoc> docs = new ArrayList<RetrievalDoc>();
		docs = parser.readCorpus(TestUtils.CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		indexer.buildIndex(TestUtils.NOT_EXIST_INDEX_FOLDER, docs, null);
	}

	/*
	 * doclist is null
	 */
	@Test(expected = IndexerException.class)
	public void testBuildIndexDocListIsNull() throws IndexerException,
			CorpusException {
		BM25RetrievalIndexer indexer = new BM25RetrievalIndexer();
		List<RetrievalDoc> docs = new ArrayList<RetrievalDoc>();
		docs = null;
		indexer.buildIndex(TestUtils.NOT_EXIST_INDEX_FOLDER, docs, null);
	}

}
