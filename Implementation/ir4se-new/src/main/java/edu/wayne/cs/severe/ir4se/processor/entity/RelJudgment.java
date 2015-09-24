package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.List;

public class RelJudgment {

	private List<RetrievalDoc> relevantDocs;

	public List<RetrievalDoc> getRelevantDocs() {
		return relevantDocs;
	}

	public void setRelevantDocs(List<RetrievalDoc> relevantDocs) {
		this.relevantDocs = relevantDocs;
	}

	@Override
	public String toString() {
		return "RelJudgment [relevantDocs=" + relevantDocs + "]";
	}

}