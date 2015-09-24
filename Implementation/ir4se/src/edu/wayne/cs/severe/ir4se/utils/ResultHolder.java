package edu.wayne.cs.severe.ir4se.utils;

public class ResultHolder {
	private int rank;
	private double score;

	public ResultHolder(int rank, double score) {
		super();
		this.rank = rank;
		this.score = score;
	}

	public int getRank() {
		return rank;
	}

	public double getScore() {
		return score;
	}

}
