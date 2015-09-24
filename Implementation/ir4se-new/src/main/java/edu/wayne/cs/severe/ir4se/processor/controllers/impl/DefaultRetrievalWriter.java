package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalWriter;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.WritingException;
import edu.wayne.cs.severe.ir4se.processor.utils.ExceptionUtils;

public class DefaultRetrievalWriter implements RetrievalWriter {

	@Override
	public void writeStats(RetrievalStats stats, String statsFilePath)
			throws WritingException {

		FileWriter writer = null;
		try {

			File stFile = new File(statsFilePath);
			if (stFile.isDirectory()) {
				throw new RuntimeException("Output file invalid: "
						+ statsFilePath);
			}

			if (stats == null) {
				throw new RuntimeException("No statistics to write");
			}

			writer = new FileWriter(statsFilePath);

			writer.write("mean rec. rank;" + stats.getMeanRecipRank());
			writer.write("\n");

			writer.write("query;rank;rec. rank");
			writer.write("\n");

			Map<Query, List<Double>> queryStats = stats.getQueryStats();
			Set<Entry<Query, List<Double>>> qStatsSet = queryStats.entrySet();

			for (Entry<Query, List<Double>> entry : qStatsSet) {

				String quResult = entry.getKey().getQueryId().toString();
				quResult += (";" + entry.getValue().get(0));
				quResult += (";" + entry.getValue().get(1));

				writer.write(quResult);
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