package edu.wayne.cs.severe.ir4se.results;

import org.junit.Before;
import org.junit.Test;

public class EffectivenessProcessorTest {

	String dataDirPath;

	@Before
	public void setUp() throws Exception {
		dataDirPath = "/Users/daichenhan/Documents/IR_PROJ_DATA/Raw Data";
	}

	@Test
	public void test() {
		EffectivenessProcessor proc = new EffectivenessProcessor(dataDirPath);
		proc.process();
	}

}
