package edu.wayne.cs.severe.ir4se.lsi.utils;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LSIResultSummarizerTest {
	String dataDirPath;

	@Before
	public void setUp() throws Exception {
		dataDirPath = "C:/Users/ojcchar/Documents/Temp/ir_proj/LSI Data/";
	}

	@Test
	@Ignore
	public void test() {
		LSIResultSummarizer sum = new LSIResultSummarizer(dataDirPath, false);
		sum.summarizeResults();
	}

	@Test
	public void testJEdit() {
		File folder = new File(dataDirPath);
		File[] listFiles = folder.listFiles();

		for (File file : listFiles) {

			if (file.isFile()) {
				continue;
			}

			LSIResultSummarizer sum = new LSIResultSummarizer(
					file.getAbsolutePath() + "/", true);
			sum.summarizeResults();

		}
	}

}
