package edu.wayne.cs.severe.ir4se.lda.utils;

import org.junit.Before;
import org.junit.Test;

public class LDALuceneInputFileGeneratorTest {
	String dataDirPath;

	@Before
	public void setUp() throws Exception {
		dataDirPath = "E:/U/WSU/Research/2014 ICSE Query-based IR/Data/LDA Data";
	}

	@Test
	public void test() {
		LDALuceneInputFileGenerator gen = new LDALuceneInputFileGenerator(dataDirPath, false);
		gen.processDataDir();
	}

}
