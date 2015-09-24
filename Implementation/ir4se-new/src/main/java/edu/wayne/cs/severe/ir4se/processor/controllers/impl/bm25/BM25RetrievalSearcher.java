package edu.wayne.cs.severe.ir4se.processor.controllers.impl.bm25;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;
import edu.wayne.cs.severe.ir4se.processor.utils.ExceptionUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.GenericConstants;

public class BM25RetrievalSearcher extends RetrievalSearcher {

	private IndexSearcher searcher;
	private IndexReader reader;
	private QueryParser parser;

	public BM25RetrievalSearcher(String indexPath, Map<String, String> params)
			throws IOException, SearchException {
		super(indexPath, params);
		Analyzer analyzer = new StandardAnalyzer(
				GenericConstants.LUCENE_VERSION);
		String field = "text";

		parser = new QueryParser(GenericConstants.LUCENE_VERSION, field,
				analyzer);

		File fileIndex = new File(indexPath);

		if (!fileIndex.exists() || !fileIndex.isDirectory()) {
			throw new SearchException("Invalid index path!");
		}

		reader = DirectoryReader.open(FSDirectory.open(fileIndex));
		searcher = new IndexSearcher(reader);
		if (params.get("k1") == "" || params.get("b") == "") {
			throw new SearchException("There are no values for k1 and b");
		}
		try {
			float k1 = Float.parseFloat(params.get("k1"));
			float b = Float.parseFloat(params.get("b"));
			BM25Similarity similarity = new BM25Similarity(k1, b);
			searcher.setSimilarity(similarity);
		} catch (NullPointerException | NumberFormatException e) {
			throw new SearchException(e.getMessage());
		}

	}

	@Override
	public List<RetrievalDoc> searchQuery(Query query) throws SearchException {

		List<RetrievalDoc> retrievedDocs = new ArrayList<RetrievalDoc>();
		try {

			String txtQuery = query.getTxt();
			org.apache.lucene.search.Query lucceneQuery = parser
					.parse(txtQuery);

			int resultsNumber = reader.numDocs();
			ScoreDoc[] hits = searcher
					.search(lucceneQuery, null, resultsNumber).scoreDocs;
			for (int i = 0; i < hits.length; i++) {

				RetrievalDoc doc = new RetrievalDoc();
				doc.setDocRank(i + 1);
				doc.setDocId(hits[i].doc);

				retrievedDocs.add(doc);
			}
		} catch (IOException | ParseException | NullPointerException e) {
			SearchException e2 = new SearchException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}

		return retrievedDocs;
	}

	@Override
	public void close() throws SearchException {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				throw new SearchException(e.getMessage());
			}
		}
	}
}
