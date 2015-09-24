package edu.wayne.cs.severe.ir4se.processor.boundary;

import java.util.Arrays;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalProcessor;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalProcessor;
import edu.wayne.cs.severe.ir4se.processor.exception.ArgsException;
import edu.wayne.cs.severe.ir4se.processor.exception.ParameterException;

public class Main {

	public static String paramFile;

	public static void main(String args[]) {

		try {
			paramFile = parseArguments(args);

			RetrievalProcessor procesor = new DefaultRetrievalProcessor();
			procesor.processSystem(paramFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String parseArguments(String[] args)
			throws ParameterException, ArgsException {

		if (args == null) {
			throw new ArgsException();
		}

		System.out.println(Arrays.toString(args));

		if (args.length != 1) {
			throw new ArgsException();
		}

		return args[0];
	}

}