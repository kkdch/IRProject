package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.IndexerException;
import edu.wayne.cs.severe.ir4se.processor.utils.GenericConstants;

public class DefaultRetrievalIndexer implements RetrievalIndexer {

	@Override
	public void buildIndex(String indexPath, List<RetrievalDoc> docs,
			Map<String, String> params) throws IndexerException {
		File indexFile = new File(indexPath);

		if (docs == null)
			throw new IndexerException("The document list is missing");

		if (!indexFile.exists() || !indexFile.isDirectory())
			throw new IndexerException(
					"The index folder doesn't exist or is not a directory: "
							+ indexPath);

		if (indexFile.list().length != 0)
			throw new IndexerException("The index folder is not empty: "
					+ indexPath);

		IndexWriterConfig config = new IndexWriterConfig(
				GenericConstants.LUCENE_VERSION, new WhitespaceAnalyzer(
						GenericConstants.LUCENE_VERSION));
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(FSDirectory.open(indexFile), config);

			for (RetrievalDoc retDoc : docs) {
				Document luceneDoc = getLuceneDocument(retDoc);
				writer.addDocument(luceneDoc);
			}
		} catch (IOException e) {
			throw new IndexerException(e.getMessage());
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				throw new IndexerException(e.getMessage());
			}
		}
	}

	private Document getLuceneDocument(RetrievalDoc doc) {
		Document luceneDoc = new Document();

		FieldType type = new FieldType();
		type.setStoreTermVectors(true);
		type.setTokenized(true);
		type.setIndexed(true);
		type.setStoreTermVectorPositions(true);

		luceneDoc.add(new Field("docNo", doc.getDocId().toString(), type));

		String text = doc.getDocText();
		luceneDoc.add(new Field("text", text, type));

		return luceneDoc;

	}
}