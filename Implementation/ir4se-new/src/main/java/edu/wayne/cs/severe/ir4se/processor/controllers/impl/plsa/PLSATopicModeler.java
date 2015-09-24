package edu.wayne.cs.severe.ir4se.processor.controllers.impl.plsa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalTopicModeler;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.TopicDistribution;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;

@SuppressWarnings("unused")
public class PLSATopicModeler implements RetrievalTopicModeler {

	private int numDocs = -1; // number of docs, aka M
	private int numWords = -1; // number of words, aka K
	private int numTopics = -1; // number of topics, aka V

	// index reader
	private IndexReader reader;

	// index data
	private HashMap<String, Integer> termsIds;
	private HashMap<String, List<DocFreq>> wordDocsPos = new HashMap<>();
	private LinkedHashSet<Integer> docsIds;

	public PLSATopicModeler(String idxDirPath) throws IOException {
		reader = DirectoryReader.open(FSDirectory.open(new File(idxDirPath)));
		setIndexData();
	}

	@Override
	public TopicDistribution createTopicDistr(List<RetrievalDoc> docs,
			Map<String, String> params) throws IOException {

		numTopics = ParameterUtils.getNumberOfTopics(params);
		Integer iters = ParameterUtils.getNumberOfIterations(params);

		System.out.println("PLSA - numDocs: " + numDocs + ", numWords: "
				+ numWords + ", numTopics: " + numTopics + ", iter: " + iters);

		return runEM(iters);

	}

	private void setIndexData() throws IOException {

		// set the number of docs
		numDocs = reader.numDocs();

		// get the fields
		String field = "text";
		Fields fields = MultiFields.getFields(reader);
		if (fields == null) {
			throw new RuntimeException("field " + field + " not found");
		}

		// get the terms
		Terms terms = fields.terms(field);
		if (terms == null) {
			throw new RuntimeException("No terms found");
		}

		// get the terms iterator
		TermsEnum termsEnum = terms.iterator(null);
		BytesRef term;

		// initialize index data and num of docs
		termsIds = new HashMap<String, Integer>();
		numWords = 0;

		// iterate every term
		while ((term = termsEnum.next()) != null) {

			// get the term name
			String termName = new String(term.bytes).substring(term.offset,
					term.offset + term.length);

			// get the documents and positions of the current term
			DocsAndPositionsEnum docsPos = termsEnum.docsAndPositions(null,
					null);

			// build the document frequencies for this term
			ArrayList<DocFreq> arrayList = new ArrayList<DocFreq>();
			while (docsPos.nextDoc() != DocsAndPositionsEnum.NO_MORE_DOCS) {

				double freq = docsPos.freq();
				int docId = docsPos.docID();

				DocFreq docFreq = new DocFreq();
				docFreq.docId = docId;
				docFreq.freq = freq;

				arrayList.add(docFreq);
			}
			wordDocsPos.put(termName, arrayList);

			// add the term id, basically it is the current number of words
			termsIds.put(termName, numWords);

			// increment the number of words
			numWords++;
		}

		docsIds = new LinkedHashSet<Integer>();

		Collection<List<DocFreq>> values = wordDocsPos.values();
		for (List<DocFreq> docs : values) {
			for (DocFreq docFreq : docs) {
				docsIds.add(docFreq.docId);
			}
		}

	}

	private TopicDistribution runEM(int iters) throws IOException {

		TopicDistribution topicDistr = new TopicDistribution();

		// p(z), size: K
		double[] Pz = new double[numTopics];

		// p(d|z), size: K x M
		double[][] Pd_z = new double[numTopics][numDocs];

		// p(w|z), size: K x V
		double[][] Pw_z = new double[numTopics][numWords];

		// p(z|d,w), size: K x M x doc.size()
		double[][][] Pz_dw = new double[numTopics][numDocs][];

		// L: log-likelihood value
		// double L = -1;

		// run EM algorithm
		initializeProbabilities(Pz, Pd_z, Pw_z, Pz_dw);

		for (int it = 0; it < iters; it++) {

			// E-step
			if (!performEstep(Pz, Pd_z, Pw_z, Pz_dw)) {
				System.out.println("EM,  in E-step");
			}

			// M-step
			if (!performMstep(Pz_dw, Pw_z, Pd_z, Pz)) {
				System.out.println("EM, in M-step");
			}

			// L = calculateLoglikelihood(Pz, Pd_z, Pw_z);
			// System.out.println("[" + it + "]" + "\tlikelihood: " + L);
		}

//		// print result
//		for (int doc = 0; doc < numDocs; doc++) {
//			double norm = 0.0;
//			for (int topic = 0; topic < numTopics; topic++) {
//				norm += Pd_z[topic][doc];
//			}
//			if (norm <= 0.0)
//				norm = 1.0;
//			for (int topic = 0; topic < numTopics; topic++) {
//				System.out.format("%10.4f", Pd_z[topic][doc] / norm);
//			}
//			System.out.println();
//		}

		topicDistr.setProbTopics(Pz);
		topicDistr.setProbTopsDocs(Pd_z);
		topicDistr.setProbTopsWords(Pw_z);
		topicDistr.setProbTopsDocsWords(Pz_dw);
		topicDistr.setTermsIds(termsIds);

		return topicDistr;
	}

	private boolean initializeProbabilities(double[] Pz, double[][] Pd_z,
			double[][] Pw_z, double[][][] Pz_dw) throws IOException {

		// p(z), size: K
		double probTopic = (double) 1 / (double) numTopics;
		for (int topic = 0; topic < numTopics; topic++) {
			Pz[topic] = probTopic;
		}

		// p(d|z), size: K x M
		for (int topic = 0; topic < numTopics; topic++) {

			double norm = 0.0;

			for (int doc = 0; doc < numDocs; doc++) {
				Pd_z[topic][doc] = Math.random();
				norm += Pd_z[topic][doc];
			}

			for (int doc = 0; doc < numDocs; doc++) {
				Pd_z[topic][doc] /= norm;
			}
		}

		// p(w|z), size: K x V
		for (int topic = 0; topic < numTopics; topic++) {

			double norm = 0.0;

			for (int word = 0; word < numWords; word++) {
				Pw_z[topic][word] = Math.random();
				norm += Pw_z[topic][word];
			}

			for (int word = 0; word < numWords; word++) {
				Pw_z[topic][word] /= norm;
			}
		}

		// p(z|d,w), size: K x M x doc.size()
		for (int topic = 0; topic < numTopics; topic++) {
			for (int doc = 0; doc < numDocs; doc++) {

				// boolean contains = docsIds.contains(doc);
				// if (!contains) {
				// throw new RuntimeException("does exist: " + doc);
				// }

				Terms terms = reader.getTermVector(doc, "text");
				int numTermsInDoc = (int) terms.size();
				// System.out.println("Num terms for doc " + i + ": " +
				// numTerms);

				Pz_dw[topic][doc] = new double[numTermsInDoc];
			}
		}

		return false;
	}

	private boolean performEstep(double[] Pz, double[][] Pd_z, double[][] Pw_z,
			double[][][] Pz_dw) throws IOException {

		// for every document
		for (int doc = 0; doc < numDocs; doc++) {

			// get the terms and an iterator
			Terms termsofDoc = reader.getTermVectors(doc).terms("text");
			TermsEnum termsIter = termsofDoc.iterator(null);
			BytesRef currTerm;

			// System.out.println("doc: " + doc + " termSize: "
			// + termsofDoc.size());
			int termPositionInDoc = 0;

			// iterate every term
			while ((currTerm = termsIter.next()) != null) {

				// get the term id
				String termName = new String(currTerm.bytes).substring(
						currTerm.offset, currTerm.offset + currTerm.length);
				Integer termId = termsIds.get(termName);

				// calculate the probability of the topic, given the word and
				// document
				// TODO: REVIEW THE Pd_z[topic][doc]
				double norm = 0.0;
				for (int topic = 0; topic < numTopics; topic++) {
					double prob = Pz[topic] * Pd_z[topic][doc]
							* Pw_z[topic][termId];
					Pz_dw[topic][doc][termPositionInDoc] = prob;
					norm += prob;
				}

				// normalize the probabilities
				for (int z = 0; z < numTopics; z++) {
					Pz_dw[z][doc][termPositionInDoc] /= norm;
				}

				termPositionInDoc++;
			}
		}

		return true;
	}

	class DocFreq {
		public int docId;
		public double freq;
	}

	private boolean performMstep(double[][][] Pz_dw, double[][] Pw_z,
			double[][] Pd_z, double[] Pz) throws IOException {

		// System.out.println("--------------------------");

		// calculation of p(w|z)
		// iterate every topic
		for (int topic = 0; topic < numTopics; topic++) {
			double norm = 0.0;

			// get the terms of the collection
			Terms terms = MultiFields.getFields(reader).terms("text");
			TermsEnum termsIter = terms.iterator(null);
			BytesRef currTerm;

			// word
			// TODO: THIS IS THE WORD ID, WHICH CAN BE OBTAINED FROM THE HASHMAP
			int word = 0;

			// iterate every term
			while ((currTerm = termsIter.next()) != null) {

				double probTopicWord = 0.0;

				String termName = new String(currTerm.bytes).substring(
						currTerm.offset, currTerm.offset + currTerm.length);
				List<DocFreq> docsPos = wordDocsPos.get(termName);
				// get the document positions
				// DocsAndPositionsEnum docsPos =
				// termsIter.docsAndPositions(null,
				// null);
				// String nameTerm = new String(currTerm.bytes).substring(
				// currTerm.offset, currTerm.offset + currTerm.length);

				// int position = 0;
				// iterate the doc-pos
				for (int i = 0; i < docsPos.size(); i++) {
					DocFreq docFreq = docsPos.get(i);

					double[] ds = Pz_dw[topic][docFreq.docId];
					for (int j = 0; j < ds.length; j++) {
						double d = ds[j];
						probTopicWord += docFreq.freq * d;
					}
				}
				// while (docsPos.nextDoc() !=
				// DocsAndPositionsEnum.NO_MORE_DOCS) {
				//
				// // get the frequency and doc id
				// double freq = docsPos.freq();
				// int docId = docsPos.docID();
				//
				// System.out.println("Topic: " + topic + ", position: "
				// + position + ", docId: " + docId + ", l:"
				// + Pz_dw[topic][docId].length);
				//
				// probTopicWord += freq * Pz_dw[topic][docId][position];
				// position++;
				// }

				// assign the probability
				Pw_z[topic][word] = probTopicWord;
				norm += probTopicWord;

				word++;
			}

			// normalization
			for (word = 0; word < numWords; word++) {
				Pw_z[topic][word] /= norm;
			}
		}

		// System.out.println("--------------------------*************");

		// p(d|z)
		// iterate every topic
		for (int topic = 0; topic < numTopics; topic++) {

			double norm = 0.0;

			// iterate every document
			for (int doc = 0; doc < numDocs; doc++) {
				double sum = 0.0;

				// get the terms
				Terms terms = reader.getTermVectors(doc).terms("text");
				TermsEnum termsEnum = terms.iterator(null);
				BytesRef term;

				int position = 0;
				// Data data = dataset.getDataAt(docId);

				// iterate every term
				while ((term = termsEnum.next()) != null) {
					// DocsEnum docs = termsEnum.docs(null, null);

					double freq = 0;

					// get the doc frequencies
					String termName = new String(term.bytes).substring(
							term.offset, term.offset + term.length);
					List<DocFreq> docsPos = wordDocsPos.get(termName);

					// obtain the tf
					for (DocFreq docFreq : docsPos) {
						if (doc == docFreq.docId) {
							freq = docFreq.freq;
							break;
						}
					}

					// System.out.println("Topic: " + topic + ", term: " +
					// termName
					// + ", doc: " + doc + ", tf: " + tf);

					sum += freq * Pz_dw[topic][doc][position];

					position++;
				}

				Pd_z[topic][doc] = sum;
				norm += sum;
			}

			// normalization
			for (int doc = 0; doc < numDocs; doc++) {
				Pd_z[topic][doc] /= norm;
			}
		}

		// This is definitely a bug
		// p(z) values are even, but they should not be even
		// TODO: REVIEW THIS BUG
		double norm = 0.0;
		for (int topic = 0; topic < numTopics; topic++) {
			double prob = 0.0;
			for (int doc = 0; doc < numDocs; doc++) {
				prob += Pd_z[topic][doc];
			}
			Pz[topic] = prob;
			norm += prob;
		}

		// normalization
		for (int topic = 0; topic < numTopics; topic++) {
			Pz[topic] /= norm;
		}

		return true;
	}

	private double calculateLoglikelihood(double[] Pz, double[][] Pd_z,
			double[][] Pw_z) throws IOException {

		double logLikelihood = 0.0;

		// iterate every doc
		for (int doc = 0; doc < numDocs; doc++) {

			// get the terms of the document
			Terms terms = reader.getTermVectors(doc).terms("text");
			TermsEnum termsIter = terms.iterator(null);
			BytesRef currTerm;

			// iterate every term
			while ((currTerm = termsIter.next()) != null) {

				// get the term id
				String termName = new String(currTerm.bytes).substring(
						currTerm.offset, currTerm.offset + currTerm.length);
				Integer word = termsIds.get(termName);

				// get the frequency
				double freq = 0;
				List<DocFreq> docsPos = wordDocsPos.get(termName);
				for (DocFreq docFreq : docsPos) {
					if (doc == docFreq.docId) {
						freq = docFreq.freq;
						break;
					}
				}

				double sum = 0.0;
				for (int topic = 0; topic < numTopics; topic++) {
					sum += Pz[topic] * Pd_z[topic][doc] * Pw_z[topic][word];
				}
				logLikelihood += freq * Math.log10(sum);

				// -------------

			}

		}
		return logLikelihood;
	}

	@Override
	public void closeIndex() throws Exception {
		reader.close();
	}

}
