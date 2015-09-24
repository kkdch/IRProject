package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.CorpusException;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

public class RetrievalParserTestReadCorpus {
	static DefaultRetrievalParser parser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/*
	 * right path
	 */
	@Test
	public void RetrievalParserTestDocumentCheck() throws CorpusException {
		parser = new DefaultRetrievalParser();

		RetrievalDoc expectedDoc = new RetrievalDoc();
		expectedDoc.setDocId(5);
		expectedDoc
				.setDocText("list command clistcommand list command clistcommand refresh refresh m_refresh refresh");

		List<RetrievalDoc> docList = new ArrayList<RetrievalDoc>();
		docList = parser.readCorpus(TestUtils.CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		RetrievalDoc actualDoc = docList.get(5);

		// doc id checking
		assertEquals(expectedDoc, actualDoc);

		// doc text checking
		assertEquals(expectedDoc.getDocText(), actualDoc.getDocText());
	}

	/*
	 * wrong file path
	 */
	@Test(expected = CorpusException.class)
	public void RetrievalParserTestWrongPath() throws CorpusException {
		parser = new DefaultRetrievalParser();
		parser.readCorpus(TestUtils.WRONG_CORPUS_FILE_PATH, null);
	}

	/*
	 * null file path
	 */
	@Test(expected = CorpusException.class)
	public void RetrievalParserTestNullPath() throws CorpusException {
		parser = new DefaultRetrievalParser();
		parser.readCorpus(null, null);
	}

	/*
	 * empty file
	 */
	@Test(expected = CorpusException.class)
	public void RetrievalParserTestEmptyFile() throws CorpusException {
		parser = new DefaultRetrievalParser();
		parser.readCorpus(TestUtils.EMPTY_CORPUS_FILE_PATH, null);
	}

	/*
	 * wrong formatted file
	 */
	@Test
	public void RetrievalParserTestWrongFormattedFile() throws CorpusException {
		parser = new DefaultRetrievalParser();

		RetrievalDoc expectedDoc = new RetrievalDoc();
		expectedDoc.setDocId(5);
		expectedDoc.setNameDoc("src.engine.commands.cpp.clistcommand.clistcommand(bool)");
		expectedDoc
				.setDocText("list command clistcommand list command clistcommand refresh refresh m_refresh refresh");

		List<RetrievalDoc> docList = new ArrayList<RetrievalDoc>();
		docList = parser.readCorpus(TestUtils.WRONGFORMAT_CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		RetrievalDoc actualDoc = docList.get(5);

		// doc id checking
		assertEquals(expectedDoc, actualDoc);
		// doc text checking
		assertEquals(expectedDoc.getDocText(), actualDoc.getDocText());
		// name checking
		assertEquals(expectedDoc.getNameDoc(), actualDoc.getNameDoc());
	}

	/*
	 * invalid file
	 */
	@Test
	public void RetrievalParserTestInvalidFile() throws CorpusException {
		parser = new DefaultRetrievalParser();

		RetrievalDoc expectedDoc = new RetrievalDoc();
		expectedDoc.setDocId(0);
		expectedDoc.setDocText("abc");

		List<RetrievalDoc> docList = new ArrayList<RetrievalDoc>();
		docList = parser.readCorpus(TestUtils.INVALID_CORPUS_FILE_PATH, TestUtils.MAPPING_FILE_PATH);
		RetrievalDoc actualDoc = docList.get(0);

		// doc id checking
		assertEquals(expectedDoc, actualDoc);

		// doc text checking
		assertEquals(expectedDoc.getDocText(), actualDoc.getDocText());
	}

}
