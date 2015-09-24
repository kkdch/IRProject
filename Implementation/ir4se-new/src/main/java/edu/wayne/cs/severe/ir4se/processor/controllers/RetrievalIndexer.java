package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.IndexerException;

public interface RetrievalIndexer {

	public void buildIndex(String indexPath, List<RetrievalDoc> docs,
			Map<String, String> params) throws IndexerException;

}