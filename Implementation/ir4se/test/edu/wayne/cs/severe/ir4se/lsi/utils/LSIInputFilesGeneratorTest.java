package edu.wayne.cs.severe.ir4se.lsi.utils;

import org.junit.Before;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.lsi.utils.LSIInputFilesGenerator;

public class LSIInputFilesGeneratorTest {

	String dataDirPath;

	@Before
	public void setUp() throws Exception {
		dataDirPath = "C:/Users/ojcchar/Documents/Temp/ir_proj/Raw Data";
	}

	@Test
	public void test() {
		LSIInputFilesGenerator gen = new LSIInputFilesGenerator(dataDirPath, false);
		gen.processDataDir();
	}

}
