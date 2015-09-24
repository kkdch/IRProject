package edu.wayne.cs.severe.ir4se.processor.entity;

public class RetrievalDoc {

	private Integer docId;
	private String docText;
	private Integer docRank;
	private String nameDoc;

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public String getDocText() {
		return docText;
	}

	public void setDocText(String docText) {
		this.docText = docText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((docId == null) ? 0 : docId.hashCode());
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
		RetrievalDoc other = (RetrievalDoc) obj;
		if (docId == null) {
			if (other.docId != null)
				return false;
		} else if (!docId.equals(other.docId))
			return false;
		return true;
	}

	public Integer getDocRank() {
		return docRank;
	}

	public void setDocRank(Integer docRank) {
		this.docRank = docRank;
	}

	@Override
	public String toString() {
		return "RetrievalDoc [docId=" + docId + "]";
	}

	public String getNameDoc() {
		return nameDoc;
	}

	public void setNameDoc(String nameDoc) {
		this.nameDoc = nameDoc;
	}
	
	

}