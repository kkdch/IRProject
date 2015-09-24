package edu.wayne.cs.severe.ir4se.results;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class EffectivenessProcessor {

	private String dataDirPath;

	private HashMap<String, List<Query>> systems;

	private List<String> engines;

	public EffectivenessProcessor(String dataDirPath) {
		this.dataDirPath = dataDirPath;
		systems = new HashMap<String, List<Query>>();
		engines = new LinkedList<String>();
	}

	public void process() {
		File dataDir = new File(dataDirPath);

		for (File subDir : dataDir.listFiles()) {
			try{
			exploreSubDir(subDir);
			}catch(Exception e){
				System.out.println(subDir.getName()+": "+e.getMessage());
			}
		}
		printResults();
	}

	private void printResults() {
		FileWriter resultWriter = null;
		try {
			resultWriter = new FileWriter(new File(dataDirPath + "summary.txt"));
			StringBuilder line = new StringBuilder();
			line.append("system;");
			line.append("query;");
			for (String engine : engines) {
				line.append(engine + ";");
			}
			line.append("selected-ir");
			resultWriter.write(line + "\n");

			for (Entry<String, List<Query>> system : systems.entrySet()) {
				for (Query query : system.getValue()) {
					line = new StringBuilder();
					line.append(system.getKey());
					line.append(";");
					line.append(query.getId() + ";");
					for (Rank rank : query.getRanks()) {
						line.append(rank.getRank() + ";");
					}
					Collections.sort(query.getRanks(), new RankComparator());
					line.append(query.getRanks().get(0).getIrEngine());
					resultWriter.write(line.toString() + "\n");
				}
			}

			resultWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exploreSubDir(File subDir) {
		for (File subFile : subDir.listFiles()) {
			if (subFile.isDirectory()) {
				exploreSubDir(subFile);
			} else if (subFile.getName().startsWith("result") && subFile.getName().endsWith(".txt")) {
				analyzeFile(subFile);
			}
		}
	}

	private void analyzeFile(File resultFile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(resultFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		String irEngine = resultFile.getParentFile().getName();
		if (!engines.contains(irEngine)) {
			engines.add(irEngine);
		}
		int nLine = 0;
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				nLine++;
				if (!line.trim().isEmpty()) {
					String[] tokens = line.split(";");
					String system = tokens[0];
					int queryId = Integer.valueOf(tokens[1]);
					int rank = Integer.MAX_VALUE;

					if (tokens.length == 7) {
						rank = Integer.valueOf(tokens[3]);
					}

					addRank(system, queryId, rank, irEngine);
				}
			}

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Wrong format in " + resultFile.getAbsolutePath() + " at line " + nLine);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void addRank(String system, int queryId, int rank, String irEngine) {
		List<Query> queries;
		if (systems.containsKey(system)) {
			queries = systems.get(system);
		} else {
			queries = new LinkedList<Query>();
		}

		Query query = new Query(queryId);
		int queryIndex = queries.indexOf(query);
		if (queryIndex == -1) {
			queries.add(query);
		} else {
			query = queries.get(queryIndex);
		}

		Rank newRank = new Rank(rank, irEngine);
		int rankIndex = query.getRanks().indexOf(newRank);
		if (rankIndex != -1) {
			Rank oldRank = query.getRanks().get(rankIndex);
			if (newRank.getRank() < oldRank.getRank())
				;
			oldRank.setRank(rank);
		} else {
			query.addRank(newRank);
		}

		systems.put(system, queries);
	}
}

class Query {
	private int id;
	List<Rank> ranks;

	public Query(int id) {
		super();
		this.id = id;
		ranks = new ArrayList<Rank>();
	}

	public void addRank(Rank rank) {
		ranks.add(rank);
	}

	public int getId() {
		return id;
	}

	public List<Rank> getRanks() {
		return ranks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Query other = (Query) obj;
		if (id != other.id)
			return false;
		return true;
	}

}

class Rank {
	private int rank;
	private String irEngine;

	public Rank(int rank, String irEngine) {
		super();
		this.rank = rank;
		this.irEngine = irEngine;
	}

	public int getRank() {
		return rank;
	}

	public String getIrEngine() {
		return irEngine;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((irEngine == null) ? 0 : irEngine.hashCode());
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
		Rank other = (Rank) obj;
		if (irEngine == null) {
			if (other.irEngine != null)
				return false;
		} else if (!irEngine.equals(other.irEngine))
			return false;
		return true;
	}

}

class RankComparator implements Comparator<Rank> {

	@Override
	public int compare(Rank o1, Rank o2) {
		return Integer.compare(o1.getRank(), o2.getRank());
	}

}