package edu.wayne.cs.severe.ir4se.processor.utils;

import java.util.Map;

public class ParameterUtils {

	public static final String BASE_DIR = "base_dir";
	public static final String SYSTEM = "system";
	public static final String RET_MODEL = "ret_model";
	private static final String NUM_TOPICS = "num_topics";
	private static final String NUM_ITERS = "num_iters";
	public static final String NAME_CONFIG = "name_config";

	public static String getCorpFilePath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + "/" + sys + "/" + sys + "_Corpus.txt";
	}

	public static String getIndexFolderPath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + "/" + sys + "/index";
	}

	public static String getTopicDistrPath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + "/" + sys + "/plsa_data";
	}

	public static int getNumberOfTopics(Map<String, String> params) {
		String numOfTopics = params.get(NUM_TOPICS);
		return Integer.valueOf(numOfTopics);
	}

	public static Integer getNumberOfIterations(Map<String, String> params) {
		String numOfIters = params.get(NUM_ITERS);
		return Integer.valueOf(numOfIters);
	}

	public static void setNumberOfIterations(Map<String, String> params,
			String value) {
		params.put(NUM_ITERS, value);
	}

	public static void setNumberOfTopics(Map<String, String> params,
			String value) {
		params.put(NUM_TOPICS, value);
	}

	public static String getQueriesFilePath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + "/" + sys + "/" + sys + "_Queries.txt";
	}

	public static String getRelJudFilePath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + "/" + sys + "/" + sys + "_Queries.txt";
	}

	public static String getResultsFilePath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + "/" + sys + "/results";
	}

	public static String getStatsFilePath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		String resultFileName = getResultFileName(params);
		return baseDir + "/" + sys + "/results/" + resultFileName + ".csv";
	}

	public static String getResultFileName(Map<String, String> params) {
		return params.get(NAME_CONFIG);
	}

	public static String getRetrievalModel(Map<String, String> params) {
		return params.get(RET_MODEL);
	}

	public static String getDocMapPath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + "/" + sys + "/" + sys + "_Mapping.txt";
	}

	public static String getLdaHelperPath(Map<String, String> params) {
		String distrPath = getTopicDistrPath(params);
		return distrPath + "/ldaHelper.obj";
	}

}
