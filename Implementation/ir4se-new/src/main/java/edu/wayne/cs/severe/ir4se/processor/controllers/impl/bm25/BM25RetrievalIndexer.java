package edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25;

import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.IndexerException;

public class BM25RetrievalIndexer implements RetrievalIndexer {

	@Override
	public void buildIndex(String indexPath, List<RetrievalDoc> docs,
			Map<String, String> params) throws IndexerException {
		DefaultRetrievalIndexer indexer = new DefaultRetrievalIndexer();
		indexer.buildIndex(indexPath, docs, params);
	}
}