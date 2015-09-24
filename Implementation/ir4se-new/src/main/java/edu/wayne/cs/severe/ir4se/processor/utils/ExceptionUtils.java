package edu.wayne.cs.severe.ir4se.processor.utils;


public class ExceptionUtils {

	public static void addStackTrace(Exception sourceExc, Exception targetExc) {
		targetExc.setStackTrace(sourceExc.getStackTrace());
	}

}
