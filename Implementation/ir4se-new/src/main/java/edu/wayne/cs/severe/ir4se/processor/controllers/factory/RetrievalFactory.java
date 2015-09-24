package edu.wayne.cs.severe.ir4se.processor.controllers.factory;

import java.io.IOException;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalTopicModeler;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25.BM25RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25.BM25RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.plsa.PLSARetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.plsa.PLSATopicModeler;
import edu.wayne.cs.severe.ir4se.processor.entity.TopicDistribution;
import edu.wayne.cs.severe.ir4se.processor.exception.FactoryException;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;
import edu.wayne.cs.severe.ir4se.processor.utils.GenericConstants;

public class RetrievalFactory {

	public static RetrievalIndexer createIndexer(String retModel)
			throws FactoryException {

		RetrievalIndexer indexer = null;
		if (GenericConstants.BM25.equalsIgnoreCase(retModel)) {
			indexer = new BM25RetrievalIndexer();
		} else if (GenericConstants.PLSA.equalsIgnoreCase(retModel)) {
			indexer = new PLSARetrievalIndexer();
		}
		// else if (GenericConstants.LSI.equalsIgnoreCase(retModel)) {
		//
		// }
		else {
			throw new FactoryException(
					"Cannot create an indexer for the retrieval model "
							+ retModel);
		}

		return indexer;
	}

	public static RetrievalSearcher getSearcher(String retModel,
			Map<String, String> params, String indxFolder,
			TopicDistribution topicDistr) throws FactoryException, IOException,
			SearchException {

		RetrievalSearcher searcher = null;
		if (GenericConstants.BM25.equalsIgnoreCase(retModel)) {
			searcher = new BM25RetrievalSearcher(indxFolder, params);
		} else if (GenericConstants.PLSA.equalsIgnoreCase(retModel)) {
//			searcher = new PLSASearcher(indxFolder, params, topicDistr);
		}
		// else if (GenericConstants.LSI.equalsIgnoreCase(retModel)) {
		//
		// }
		else {
			throw new FactoryException(
					"Cannot create an indexer for the retrieval model "
							+ retModel);
		}

		return searcher;
	}

	public static RetrievalTopicModeler getTopicModeler(String indxFolder,
			String retModel) throws IOException {

		RetrievalTopicModeler modeler = null;
		if (GenericConstants.PLSA.equalsIgnoreCase(retModel)) {
			modeler = new PLSATopicModeler(indxFolder);
		}

		return modeler;
	}
}
