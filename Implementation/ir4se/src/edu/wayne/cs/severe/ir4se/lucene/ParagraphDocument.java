package edu.wayne.cs.severe.ir4se.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class ParagraphDocument {
	/**
	 * Makes a document for a String (a paragraph in a corpus file) representing
	 * a document.
	 * <p>
	 * The document has three fields:
	 * <ul>
	 * <li><code>path</code>--containing the path (path to the file where it's
	 * found, plus class name if the document it's a method) of the document, as
	 * a stored, untokenized field;
	 * <li><code>docName</code>--containing the name (name of class or name of
	 * method) of the document, as a stored, untokenized field;
	 * <li><code>contents</code>--containing the full contents of the document,
	 * as a Reader field;
	 */
	public static Document Document(String contents, String path, String docName) throws java.io.FileNotFoundException {

		// make a new, empty document
		Document doc = new Document();

		// Add the path of the file as a field named "path". Use a field that is
		// indexed (i.e. searchable), but don't tokenize the field into words.

		char[] docPathArray = path.toCharArray();
		for (int j = 0; j < docPathArray.length; j++) {
			if (docPathArray[j] == '\\')
				docPathArray[j] = '/';
		}
		String docIndexPath = new String(docPathArray);

		doc.add(new Field("path", docIndexPath.toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

		// Add the contents of the document to a field named "contents". Specify
		// a Reader,
		// so that the text of the document is tokenized and indexed, but not
		// stored.
		// Note that FileReader expects the file to be in the system's default
		// encoding.
		// If that's not the case searching for special characters will fail.
		doc.add(new Field("docName", docName, Field.Store.YES, Field.Index.NOT_ANALYZED));

		doc.add(new Field("contents", contents, Field.Store.YES, Field.Index.ANALYZED,
				Field.TermVector.WITH_POSITIONS_OFFSETS));

		// return the document
		return doc;
	}

	private ParagraphDocument() {
	}

}
