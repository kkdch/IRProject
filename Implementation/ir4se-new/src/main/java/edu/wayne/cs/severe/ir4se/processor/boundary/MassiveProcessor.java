package edu.wayne.cs.severe.ir4se.processor.boundary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultParamsParser;
import edu.wayne.cs.severe.ir4se.processor.exception.ParamConfigException;
import edu.wayne.cs.severe.ir4se.processor.utils.ExceptionUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.GenericConstants;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;

public class MassiveProcessor {

	public static final String NAME_FOLDER = "configs";

	public void runConfigurations(List<String> files)
			throws ParamConfigException {

		for (String file : files) {
			String[] args = { file };
			Main.main(args);
		}
	}

	public List<String> createConfigFiles(String paramFile)
			throws ParamConfigException {

		BufferedReader reader = null;
		List<String> files = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(paramFile));

			String line = reader.readLine();
			String retModel = getRetModel(line);
			String template = getTemplate(retModel);

			line = reader.readLine();
			String baseDir = getBaseDir(line);

			line = reader.readLine();

			List<List<String>> paramData = new ArrayList<List<String>>();
			while ((line = reader.readLine()) != null) {

				List<String> params = getParams(line);
				paramData.add(params);

			}

			int fileId = 1;
			for (List<String> params : paramData) {

				String nameConfig = getNameConfig(retModel, params);

				String pathFile = writeConfigFile(fileId, template, nameConfig,
						retModel, baseDir, params);

				files.add(pathFile);
				fileId++;
			}

		} catch (Exception e) {
			ParamConfigException e2 = new ParamConfigException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				throw new ParamConfigException(e.getMessage());
			}
		}

		return files;
	}

	private String writeConfigFile(int fileId, String template,
			String nameConfig, String retModel, String baseDir,
			List<String> params) throws ParamConfigException {

		FileWriter writer = null;
		try {

			// if (stFile.exists() || stFile.isDirectory()) {
			// throw new RuntimeException("Output file invalid: "
			// + statsFilePath);
			// }

			File folder = new File(NAME_FOLDER);
			if (!folder.exists()) {
				boolean mkdir = folder.mkdir();
				if (!mkdir) {
					throw new RuntimeException(
							"Could not create the folder for the config. files");
				}
			}

			File configFile = new File(NAME_FOLDER + "/" + fileId + "_"
					+ nameConfig + ".xml");
			writer = new FileWriter(configFile);
			String[] paramsTempl = new String[params.size() + 3];
			paramsTempl[0] = nameConfig;
			paramsTempl[1] = retModel;
			paramsTempl[2] = baseDir;

			for (int i = 0; i < params.size(); i++) {
				String param = params.get(i);
				paramsTempl[3 + i] = param;
			}

			String confContent = replaceInTemplate(template, paramsTempl);

			writer.write(confContent);

			return configFile.getAbsolutePath();
		} catch (Exception e) {
			ParamConfigException e2 = new ParamConfigException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new ParamConfigException(e.getMessage());
				}
			}
		}

	}

	private String replaceInTemplate(String template, String[] paramsTempl) {

		String templRepl = template;

		// System.out.println(Arrays.toString(paramsTempl));
		//
		// System.out.println(template);

		for (int i = 0; i < paramsTempl.length; i++) {

			String parTempl = paramsTempl[i];
			String templParam = "\\[ " + new Integer(i + 1).toString() + " \\]";

			templRepl = templRepl.replaceAll(templParam, parTempl);
		}

		// System.out.println(templRepl);

		return templRepl;
	}

	private String getTemplate(String retModel) throws ParamConfigException {
		String template = null;

		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("<parameters>");
		buffer.append("\n");
		buffer.append("	");
		buffer.append("\n");
		buffer.append("	<!-- General parameters (mandatory) -->");
		buffer.append("\n");
		buffer.append("	<param name=\"name_config\">[ 1 ]</param>");
		buffer.append("\n");
		buffer.append("	<param name=\"ret_model\">[ 2 ]</param>");
		buffer.append("\n");
		buffer.append("	<param name=\"base_dir\">[ 3 ]</param>");
		buffer.append("\n");
		buffer.append("	<param name=\"system\">[ 4 ]</param>");
		buffer.append("\n");
		buffer.append("	");
		buffer.append("\n");
		buffer.append("	<!-- Specific parameter model -->");
		buffer.append("\n");

		if (GenericConstants.BM25.equalsIgnoreCase(retModel)) {
			buffer.append("	<param name=\"k1\">[ 5 ]</param>");
			buffer.append("\n");
			buffer.append("	<param name=\"b\">[ 6 ]</param>");
			buffer.append("\n");
		} else if (GenericConstants.PLSA.equalsIgnoreCase(retModel)) {
			buffer.append("	<param name=\"k\">[ 5 ]</param>");
			buffer.append("\n");
			buffer.append("	<param name=\"iters\">[ 6 ]</param>");
			buffer.append("\n");
		} else {
			throw new ParamConfigException("No supported retrieval model");
		}

		buffer.append("	");
		buffer.append("\n");
		buffer.append("</parameters>");
		template = buffer.toString();
		return template;
	}

	private String getNameConfig(String retModel, List<String> paramData) {
		StringBuffer buffer = new StringBuffer(retModel);
		for (String param : paramData) {
			buffer.append("-");
			buffer.append(param);
		}
		return buffer.toString();
	}

	private List<String> getParams(String line) {
		String[] lineSplit = getLineSplit(line);
		return Arrays.asList(lineSplit);
	}

	private String getBaseDir(String line) {
		String[] lineSplit = getLineSplit(line.replace("\\", "/"));
		return lineSplit[1];
	}

	private String getRetModel(String line) {
		String[] lineSplit = getLineSplit(line);
		return lineSplit[1];
	}

	private String[] getLineSplit(String line) {
		return line.split(";");
	}

	public void summarize(List<String> configFiles) throws Exception {

		FileWriter writer = null;
		try {

			writer = new FileWriter("summary.csv");
			writer.write("Configuration;Mean rec. rank");
			writer.write("\n");

			for (String confFile : configFiles) {
				try {
					ParamParser parser = new DefaultParamsParser();
					Map<String, String> params = parser.readParamFile(confFile);

					String resultFileName = ParameterUtils
							.getResultFileName(params);
					String statsFile = ParameterUtils.getStatsFilePath(params);

					String meanRecpRank = readMeanRecRank(statsFile);

					writer.write(resultFileName + ";" + meanRecpRank);
					writer.write("\n");
				} catch (Exception e) {
					System.out.println(e);
				}
			}

		} catch (Exception e) {
			ParamConfigException e2 = new ParamConfigException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new ParamConfigException(e.getMessage());
				}
			}
		}

	}

	private String readMeanRecRank(String resultFileName)
			throws ParamConfigException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(resultFileName));

			String line = reader.readLine();
			String[] lineSplit = getLineSplit(line);

			return lineSplit[1];

		} catch (Exception e) {
			ParamConfigException e2 = new ParamConfigException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				throw new ParamConfigException(e.getMessage());
			}
		}
	}

}
