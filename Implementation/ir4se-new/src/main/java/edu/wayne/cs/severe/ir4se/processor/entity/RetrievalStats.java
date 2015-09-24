package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.List;
import java.util.Map;

/*
 */
public class RetrievalStats {

	private Double meanRecipRank;
	private Map<Query, List<Double>> queryStats;

	public Double getMeanRecipRank() {
		return meanRecipRank;
	}

	public void setMeanRecipRank(Double meanRecipRank) {
		this.meanRecipRank = meanRecipRank;
	}

	public Map<Query, List<Double>> getQueryStats() {
		return queryStats;
	}

	public void setQueryStats(Map<Query, List<Double>> queryStats) {
		this.queryStats = queryStats;
	}

}