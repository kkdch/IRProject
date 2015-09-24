package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.HashMap;

public class TopicDistribution {

	private double[] probTopics;
	private double[][] probTopsDocs;
	private double[][] probTopsWords;
	private double[][][] probTopsDocsWords;
	
	private HashMap<String, Integer> termsIds;

	public double[] getProbTopics() {
		return probTopics;
	}

	public void setProbTopics(double[] probTopics) {
		this.probTopics = probTopics;
	}

	public double[][] getProbTopsDocs() {
		return probTopsDocs;
	}

	public void setProbTopsDocs(double[][] probTopsDocs) {
		this.probTopsDocs = probTopsDocs;
	}

	public double[][] getProbTopsWords() {
		return probTopsWords;
	}

	public void setProbTopsWords(double[][] probTopsWords) {
		this.probTopsWords = probTopsWords;
	}

	public double[][][] getProbTopsDocsWords() {
		return probTopsDocsWords;
	}

	public void setProbTopsDocsWords(double[][][] probTopsDocsWords) {
		this.probTopsDocsWords = probTopsDocsWords;
	}

	public HashMap<String, Integer> getTermsIds() {
		return termsIds;
	}

	public void setTermsIds(HashMap<String, Integer> termsIds) {
		this.termsIds = termsIds;
	}

}