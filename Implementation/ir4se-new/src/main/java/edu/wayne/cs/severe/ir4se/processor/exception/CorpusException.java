package edu.wayne.cs.severe.ir4se.processor.exception;

public class CorpusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2904437902500490600L;

	public CorpusException(String message) {
		super(message);
	}
	
	public CorpusException() {
		super("The corpus file is empty");
	}

}