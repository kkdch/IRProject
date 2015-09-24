package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;

/*
 */
public abstract class RetrievalSearcher {

	public String indexPath;
	public Map<String, String> params;

	public RetrievalSearcher(String indexPath, Map<String, String> params) {
		this.indexPath = indexPath;
		this.params = params;
	}

	public abstract List<RetrievalDoc> searchQuery(Query query) throws SearchException;
	
	public abstract void close() throws SearchException;

}