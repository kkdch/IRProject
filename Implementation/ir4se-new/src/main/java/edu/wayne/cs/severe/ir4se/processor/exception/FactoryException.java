package edu.wayne.cs.severe.ir4se.processor.exception;

public class FactoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7538720030537725640L;

	public FactoryException(String message) {
		super(message);
	}

	public FactoryException() {
		super("Could create an instance of a class");
	}

}
