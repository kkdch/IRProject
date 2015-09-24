package edu.wayne.cs.severe.ir4se.processor.exception;

public class IndexerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3796970379579158884L;

	public IndexerException(String message) {
		super(message);
	}

	public IndexerException() {
		super("The index folder is not empty or doesn't exist");
	}
	

}
