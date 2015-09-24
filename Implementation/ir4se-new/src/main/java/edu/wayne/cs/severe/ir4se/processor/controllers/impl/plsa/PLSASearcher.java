package edu.wayne.cs.severe.ir4se.processor.controllers.impl.plsa;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.TopicDistribution;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;

@SuppressWarnings("unused")
public class PLSASearcher extends RetrievalSearcher {

	private IndexSearcher searcher;
	private IndexReader reader;

	private TopicDistribution topicDistr;

	public PLSASearcher(String indexPath, Map<String, String> params,
			TopicDistribution topicDistr) throws IOException {
		super(indexPath, params);
		this.topicDistr = topicDistr;

		reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		searcher = new IndexSearcher(reader);

	}

	@Override
	public List<RetrievalDoc> searchQuery(Query query) throws SearchException {
		return null;
	}

	@Override
	public void close() throws SearchException {
		// TODO Auto-generated method stub

	}
}
