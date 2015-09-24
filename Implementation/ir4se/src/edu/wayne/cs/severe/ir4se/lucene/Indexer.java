package edu.wayne.cs.severe.ir4se.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.wayne.cs.severe.ir4se.utils.StringUtils;

public class Indexer {

	private Indexer() {
	}

	/** Index all text files under a directory. */
	public static void indexFilesInDirectory(String indexDirectoryPath, String corpusDirectoryPath) {
		char[] currentDirectoryArray = corpusDirectoryPath.toCharArray();
		for (int i = 0; i < currentDirectoryArray.length; i++) {
			if (currentDirectoryArray[i] == '\\')
				currentDirectoryArray[i] = '/';
		}
		corpusDirectoryPath = new String(currentDirectoryArray);

		char[] indexDirectoryPathArray = indexDirectoryPath.toCharArray();
		for (int i = 0; i < indexDirectoryPathArray.length; i++) {
			if (indexDirectoryPathArray[i] == '\\')
				indexDirectoryPathArray[i] = '/';
		}
		indexDirectoryPath = new String(indexDirectoryPathArray);

		File indexDir = new File(indexDirectoryPath);
		if (indexDir.exists()) {
			Utils.deleteDir(indexDir);
			indexDir = new File(indexDirectoryPath);
		}

		final File corpusDir = new File(corpusDirectoryPath);
		if (!corpusDir.exists() || !corpusDir.canRead()) {
			System.out.println("Corpus directory '" + corpusDir.getAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new WhitespaceAnalyzer(
					Version.LUCENE_35));
			IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir), config);

			// IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir),
			// new WhitespaceAnalyzer(), true,
			// IndexWriter.MaxFieldLength.UNLIMITED);
			System.out.println("Indexing to directory '" + indexDirectoryPath + "'...");
			indexDocs(writer, corpusDir);
			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	/** Index all text files under a directory. */
	private static void indexDocs(IndexWriter writer, File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				System.out.println("adding " + file);
				try {
					writer.addDocument(FileDocument.Document(file));
				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
				}
			}
		}
	}

	/**
	 * Index all documents found in a corpus file. Each document is separated by
	 * a blank line from the previous one.
	 */
	public static void indexParagraphDocuments(String indexDirectoryPath, String corpusFilePathAndName,
			String mappingFilePathAndName) {
		char[] corpusPathAndNameArray = corpusFilePathAndName.toCharArray();
		for (int i = 0; i < corpusPathAndNameArray.length; i++) {
			if (corpusPathAndNameArray[i] == '\\')
				corpusPathAndNameArray[i] = '/';
		}
		corpusFilePathAndName = new String(corpusPathAndNameArray);

		char[] mappingPathAndNameArray = mappingFilePathAndName.toCharArray();
		for (int i = 0; i < mappingPathAndNameArray.length; i++) {
			if (mappingPathAndNameArray[i] == '\\')
				mappingPathAndNameArray[i] = '/';
		}
		mappingFilePathAndName = new String(mappingPathAndNameArray);

		char[] indexDirectoryPathArray = indexDirectoryPath.toCharArray();
		for (int i = 0; i < indexDirectoryPathArray.length; i++) {
			if (indexDirectoryPathArray[i] == '\\')
				indexDirectoryPathArray[i] = '/';
		}
		indexDirectoryPath = new String(indexDirectoryPathArray);

		File indexDir = new File(indexDirectoryPath);
		if (indexDir.exists()) {
			Utils.deleteDir(indexDir);
			indexDir = new File(indexDirectoryPath);
		}

		final File corpusFile = new File(corpusFilePathAndName);
		if (!corpusFile.exists() || !corpusFile.canRead()) {
			System.out.println("Corpus file '" + corpusFile.getAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new WhitespaceAnalyzer(
					Version.LUCENE_35));
			IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir), config);

			// IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir),
			// analyzer , true,IndexWriter.MaxFieldLength.UNLIMITED);
			System.out.println("Indexing to directory '" + indexDirectoryPath + "'...");

			// here read each document from file, its name and path from mapping
			// file and add it to the writer
			BufferedReader inCorpus = new BufferedReader(new FileReader(corpusFilePathAndName));
			BufferedReader inMapping = new BufferedReader(new FileReader(mappingFilePathAndName));

			String lineCorpus;
			String lineMapping;
			String contentsDoc = "";
			String pathDoc = "";
			String nameDoc = "";
			while ((lineCorpus = inCorpus.readLine()) != null) {
				if (lineCorpus.trim().length() >= 1) {
					contentsDoc = lineCorpus;
					lineMapping = inMapping.readLine();
					if (lineMapping != null) {
						int lastIndex = lineMapping.contains("(") ? lineMapping.lastIndexOf("(") : lineMapping.length();
						String subLine = lineMapping.substring(0, lastIndex);
						nameDoc = lineMapping.substring(subLine.lastIndexOf(" ") + 1);
						pathDoc = lineMapping.substring(0, subLine.lastIndexOf(" "));
						nameDoc = StringUtils.cleanPath(nameDoc);
						pathDoc = StringUtils.cleanPath(pathDoc);
					}
					writer.addDocument(ParagraphDocument.Document(contentsDoc, pathDoc, nameDoc));
				}
			}
			inCorpus.close();
			inMapping.close();
			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isFileDocInIndex(String indexDirectoryPath, String docPath) {
		boolean result = false;
		IndexReader reader = null;
		try {
			Directory dir = FSDirectory.open(new File(indexDirectoryPath));
			reader = IndexReader.open(dir);
			for (int i = 0; i < reader.numDocs(); i++) {
				Document doc = reader.document(i);

				String docIndexPath = doc.get("path") + "." + doc.get("docName");
				docIndexPath.replace('\\', '/');
				if (docPath.equalsIgnoreCase(docIndexPath)) {
					result = true;
					break;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static boolean isParagraphDocInIndex(String indexDirectoryPath, String docPath, String docName) {
		boolean result = false;
		IndexReader reader;
		try {
			Directory dir = FSDirectory.open(new File(indexDirectoryPath));
			reader = IndexReader.open(dir);
			for (int i = 0; i < reader.numDocs(); i++) {
				Document doc = reader.document(i);

				char[] docPathArray = doc.get("path").toCharArray();
				for (int j = 0; j < docPathArray.length; j++) {
					if (docPathArray[j] == '\\')
						docPathArray[j] = '/';
				}
				String docIndexPath = new String(docPathArray);

				if (docPath.equalsIgnoreCase(docIndexPath) && (doc.get("docName")).equalsIgnoreCase(docName)) {
					result = true;
					break;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getaFileDocName(String docPath) {
		String path = docPath;
		File pathDir = new File(path);
		String fileName = pathDir.getName();
		String docName = fileName.replaceAll(".txt", "");
		return docName;
	}

	public static int getFileDocNumber(String indexDirectoryPath, String docPath) {
		int docNumber = -1;
		IndexReader reader;
		try {
			Directory dir = FSDirectory.open(new File(indexDirectoryPath));
			reader = IndexReader.open(dir);
			for (int i = 0; i < reader.numDocs(); i++) {
				Document doc = reader.document(i);

				char[] docPathArray = doc.get("path").toCharArray();
				for (int j = 0; j < docPathArray.length; j++) {
					if (docPathArray[j] == '\\')
						docPathArray[j] = '/';
				}
				String docIndexPath = new String(docPathArray);

				if (docPath.equalsIgnoreCase(docIndexPath)) {
					docNumber = i;
					break;
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docNumber;
	}

	public static int getParagraphDocNumber(String indexDirectoryPath, String docPath, String docName) {
		int docNumber = -1;
		IndexReader reader;
		try {
			Directory dir = FSDirectory.open(new File(indexDirectoryPath));
			reader = IndexReader.open(dir);
			for (int i = 0; i < reader.numDocs(); i++) {
				Document doc = reader.document(i);

				char[] docPathArray = doc.get("path").toCharArray();
				for (int j = 0; j < docPathArray.length; j++) {
					if (docPathArray[j] == '\\')
						docPathArray[j] = '/';
				}
				String docIndexPath = new String(docPathArray);

				if (docPath.equalsIgnoreCase(docIndexPath) && (doc.get("docName")).equalsIgnoreCase(docName)) {
					docNumber = i;
					break;
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docNumber;
	}

	public static int getNumberOfDocsInIndex(String indexDirectoryPath) {
		int numberDocs = -1;
		IndexReader reader;
		try {
			Directory dir = FSDirectory.open(new File(indexDirectoryPath));
			reader = IndexReader.open(dir);
			numberDocs = reader.numDocs();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numberDocs;

	}

}
