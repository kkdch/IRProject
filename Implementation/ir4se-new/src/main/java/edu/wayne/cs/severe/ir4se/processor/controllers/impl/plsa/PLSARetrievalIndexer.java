package edu.wayne.cs.severe.ir4se.processor.controllers.impl.plsa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.TopicDistribution;
import edu.wayne.cs.severe.ir4se.processor.exception.IndexerException;
import edu.wayne.cs.severe.ir4se.processor.exception.WritingException;
import edu.wayne.cs.severe.ir4se.processor.utils.ExceptionUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;

public class PLSARetrievalIndexer implements RetrievalIndexer {

	@Override
	public void buildIndex(String indexPath, List<RetrievalDoc> docs,
			Map<String, String> params) throws IndexerException {

		DefaultRetrievalIndexer defIndexer = new DefaultRetrievalIndexer();
		defIndexer.buildIndex(indexPath, docs, params);

		PLSATopicModeler modeler = null;
		try {
			modeler = new PLSATopicModeler(indexPath);

			TopicDistribution topicDistr = modeler.createTopicDistr(docs,
					params);

			modeler.closeIndex();

			String plsaFolder = ParameterUtils.getTopicDistrPath(params);
			File dirPlsa = new File(plsaFolder);
			FileUtils.deleteDirectory(dirPlsa);
			dirPlsa.mkdir();

			writeTopicDistr(topicDistr, params, docs);

			String indxFolder = ParameterUtils.getIndexFolderPath(params);
			File directory = new File(indxFolder);
			FileUtils.deleteDirectory(directory);
			directory.mkdir();

//			String mappingPath = ParameterUtils.getDocMapPath(params);
//			String ldaHelper = ParameterUtils.getLdaHelperPath(params);
//			// String resultDirPath = ParameterUtils.getResultsFilePath(params);
//			String topics = String.valueOf(ParameterUtils
//					.getNumberOfTopics(params));
//
//			String[] args = { ParameterUtils.getCorpFilePath(params),
//					indexPath, ldaHelper, "--fileCodes", mappingPath,
//					"--ldaConfig",
//					topics + "," + ParameterUtils.getTopicDistrPath(params) };
//			String argsStr = "";
//			for (String arg : args) {
//				argsStr += (arg + " ");
//			}
//			Runtime rt = Runtime.getRuntime();
//			String command = "java -jar lucene-lda.jar " + argsStr;
//			System.out.println(command);
//
//			Process pr = rt.exec(command);
//
//			int exitVal = pr.waitFor();
//
//			if (exitVal != 0) {
//				throw new Exception("Could not generate the PLSA index");
//			}

		} catch (Exception e) {
			IndexerException e2 = new IndexerException(e.getClass()
					.getCanonicalName() + ": " + e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (modeler != null) {
				try {
					modeler.closeIndex();
				} catch (Exception e) {
					IndexerException e2 = new IndexerException(e.getMessage());
					ExceptionUtils.addStackTrace(e, e2);
					throw e2;
				}
			}
		}
	}

	private void writeTopicDistr(TopicDistribution topicDistr,
			Map<String, String> params, List<RetrievalDoc> docs)
			throws IOException, WritingException {
		String topicDistrPath = ParameterUtils.getTopicDistrPath(params);

		File directory = new File(topicDistrPath);
		FileUtils.deleteDirectory(directory);
		directory.mkdir();

		System.out.println("writing in " + directory.getAbsolutePath());

		writeTerms(topicDistr.getTermsIds().keySet(), directory);
		writeWordsTopics(topicDistr.getProbTopsWords(), directory);
		writeDocuments(docs, directory);
		writeTheta(topicDistr.getProbTopsDocs(), directory);

	}

	private void writeTheta(double[][] probTopsDocs, File directory)
			throws WritingException {

		FileWriter writer = null;
		try {

			writer = new FileWriter(directory.getAbsolutePath() + "/theta.dat");

			int K = probTopsDocs.length;
			int D = probTopsDocs[0].length;
			for (int i = 0; i < D; i++) {

				StringBuffer probStr = new StringBuffer();
				for (int j = 0; j < K; j++) {
					double probDoc = probTopsDocs[j][i];
					probStr.append(probDoc);
					probStr.append(" ");
				}

				writer.write(probStr.toString());
				writer.write("\n");
			}

		} catch (Exception e) {
			WritingException e2 = new WritingException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new WritingException(e.getMessage());
				}
			}
		}

	}

	private void writeDocuments(List<RetrievalDoc> docs, File directory)
			throws WritingException {

		FileWriter writer = null;
		try {

			writer = new FileWriter(directory.getAbsolutePath() + "/files.dat");

			for (RetrievalDoc doc : docs) {

				writer.write("666 " + doc.getNameDoc() + " 666");
				writer.write("\n");
			}

		} catch (Exception e) {
			WritingException e2 = new WritingException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new WritingException(e.getMessage());
				}
			}
		}

	}

	private void writeWordsTopics(double[][] topsWords, File directory)
			throws WritingException {

		FileWriter writer = null;
		try {

			writer = new FileWriter(directory.getAbsolutePath() + "/words.dat");

			for (double[] words : topsWords) {

				StringBuffer probStr = new StringBuffer();
				for (double probWord : words) {
					probStr.append(probWord);
					probStr.append(" ");
				}

				writer.write(probStr.toString());
				writer.write("\n");
			}

		} catch (Exception e) {
			WritingException e2 = new WritingException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new WritingException(e.getMessage());
				}
			}
		}

	}

	private void writeTerms(Set<String> terms, File directory)
			throws WritingException {

		FileWriter writer = null;
		try {

			writer = new FileWriter(directory.getAbsolutePath() + "/vocab.dat");

			for (String term : terms) {
				writer.write(term);
				writer.write("\n");
			}

		} catch (Exception e) {
			WritingException e2 = new WritingException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new WritingException(e.getMessage());
				}
			}
		}

	}

}
