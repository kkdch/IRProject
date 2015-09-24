package edu.wayne.cs.severe.ir4se.lda.utils;

import org.junit.Before;
import org.junit.Test;

public class LDAInputFileGeneratorTest {

	String dataDirPath;

	@Before
	public void setUp() throws Exception {
		dataDirPath = "/Users/daichenhan/Documents/IR_PROJ_DATA/Raw Data";
	}

	@Test
	public void test() {
		LDAInputFileGenerator gen = new LDAInputFileGenerator(dataDirPath);
		gen.processDataDir();
	}
}
