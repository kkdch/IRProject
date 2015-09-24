package edu.wayne.cs.severe.ir4se.processor.controllers;

import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.WritingException;

public interface RetrievalWriter {

	public void writeStats(RetrievalStats stats, String statsFilePath)
			throws WritingException;

}