package edu.wayne.cs.severe.ir4se.processor.boundary;

import java.util.List;

import edu.wayne.cs.severe.ir4se.processor.exception.ArgsException;
import edu.wayne.cs.severe.ir4se.processor.exception.ParameterException;

public class MainMassive {

	public static String paramFile;

	public static void main(String[] args) {
		try {
			paramFile = parseArguments(args);

			MassiveProcessor processor = new MassiveProcessor();
			List<String> configFiles = processor.createConfigFiles(paramFile);
			processor.runConfigurations(configFiles);
			
			processor.summarize(configFiles);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String parseArguments(String[] args)
			throws ParameterException, ArgsException {

		if (args == null) {
			throw new ArgsException();
		}

		if (args.length != 1) {
			throw new ArgsException();
		}

		return args[0];
	}
}
