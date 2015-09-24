package edu.wayne.cs.severe.ir4se.processor.boundary;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.exception.ParamConfigException;

public class MassiveProcessorTest {

	private static MassiveProcessor proc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		proc = new MassiveProcessor();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		FileUtils.deleteDirectory(new File(MassiveProcessor.NAME_FOLDER));
	}

	@Test(expected = ParamConfigException.class)
	public void testCreateConfigFilesNull() throws ParamConfigException {
		proc.createConfigFiles(null);
	}

	@Test(expected = ParamConfigException.class)
	public void testCreateConfigFilesNoExists() throws ParamConfigException {
		proc.createConfigFiles("234234.txt");
	}

	@Test
	public void testCreateConfigFiles() throws ParamConfigException {
		List<String> files = proc.createConfigFiles("test_data/massive_params.csv");
		
		assertNotNull(files);
		
	}
}
