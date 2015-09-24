package edu.wayne.cs.severe.ir4se.processor.exception;

public class ParameterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6571289528775645549L;

	public ParameterException() {
		super("The parameter configuration file is not well formatted");
	}

	public ParameterException(String message) {
		super(message);
	}

}
