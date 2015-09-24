package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.exception.ParameterException;

public interface ParamParser {

	Map<String, String> readParamFile(String filepath)
			throws ParameterException;

}