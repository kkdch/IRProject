package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultParamsParser;
import edu.wayne.cs.severe.ir4se.processor.exception.ParameterException;
import edu.wayne.cs.severe.ir4se.processor.utils.TestUtils;

/**
 * Test class for the ParamParser class
 * 
 * @author ojcchar
 * 
 */
public class ParamParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Normal scenario test
	 * 
	 * @throws ParameterException
	 */
	@Test
	public void testReadParamFile() throws ParameterException {
		ParamParser parser = new DefaultParamsParser();
		parser.readParamFile(TestUtils.CONF_FILE_PATH);
	}

	/**
	 * No file exists
	 * 
	 * @throws ParameterException
	 */
	@Test(expected = ParameterException.class)
	public void testReadParamNoFile() throws ParameterException {
		ParamParser parser = new DefaultParamsParser();
		parser.readParamFile(TestUtils.CONF_FILE_PATH + "_test");
	}

	/**
	 * Null file path
	 * 
	 * @throws ParameterException
	 */
	@Test(expected = NullPointerException.class)
	public void testReadParamFileNullPath() throws ParameterException {
		ParamParser parser = new DefaultParamsParser();
		parser.readParamFile(null);
	}

	/**
	 * Wrong formatted conf file
	 * 
	 * @throws ParameterException
	 */
	@Test(expected = ParameterException.class)
	public void testReadParamFileWrongFormatted() throws ParameterException {
		ParamParser parser = new DefaultParamsParser();
		parser.readParamFile(TestUtils.WRONG_CONF_FILE_PATH);
	}

	/**
	 * Empty names of parameters
	 * 
	 * @throws ParameterException
	 */
	@Test(expected = ParameterException.class)
	public void testReadParamFileEmptyParams() throws ParameterException {
		ParamParser parser = new DefaultParamsParser();
		parser.readParamFile(TestUtils.WRONG_CONF_FILE_EMPTY1);
	}

	/**
	 * Empty values of parameters
	 * 
	 * @throws ParameterException
	 */
	@Test(expected = ParameterException.class)
	public void testReadParamFileEmptyParams2() throws ParameterException {
		ParamParser parser = new DefaultParamsParser();
		parser.readParamFile(TestUtils.WRONG_CONF_FILE_EMPTY2);
	}

	/**
	 * Mandatory parameters missing
	 * 
	 * @throws ParameterException
	 */
	@Test(expected = ParameterException.class)
	public void testReadParamFileParamsMissing() throws ParameterException {
		ParamParser parser = new DefaultParamsParser();
		parser.readParamFile(TestUtils.NO_MAND_CONF_FILE);
	}

}
