package edu.wayne.cs.severe.ir4se.processor.entity;

public class RetrievalResult {

	public Double score;
	public Integer rank;
	public RetrievalDoc retrievedDoc;

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public RetrievalDoc getRetrievedDoc() {
		return retrievedDoc;
	}

	public void setRetrievedDoc(RetrievalDoc retrievedDoc) {
		this.retrievedDoc = retrievedDoc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((retrievedDoc == null) ? 0 : retrievedDoc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetrievalResult other = (RetrievalResult) obj;
		if (retrievedDoc == null) {
			if (other.retrievedDoc != null)
				return false;
		} else if (!retrievedDoc.equals(other.retrievedDoc))
			return false;
		return true;
	}

}