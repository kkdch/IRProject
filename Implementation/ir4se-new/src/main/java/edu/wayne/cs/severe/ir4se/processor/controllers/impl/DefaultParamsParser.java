package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.wayne.cs.severe.ir4se.processor.controllers.ParamParser;
import edu.wayne.cs.severe.ir4se.processor.entity.params.ConfParam;
import edu.wayne.cs.severe.ir4se.processor.entity.params.ParamsFile;
import edu.wayne.cs.severe.ir4se.processor.exception.ParameterException;
import edu.wayne.cs.severe.ir4se.processor.utils.ExceptionUtils;
import edu.wayne.cs.severe.ir4se.processor.utils.ParameterUtils;

/**
 * Default configuration file parser/reader
 * 
 * @author ojcchar
 * 
 */
public class DefaultParamsParser implements ParamParser {

	private List<String> mandatoryParams;

	public DefaultParamsParser() {
		mandatoryParams = Arrays.asList(ParameterUtils.RET_MODEL,
				ParameterUtils.SYSTEM, ParameterUtils.BASE_DIR,
				ParameterUtils.NAME_CONFIG);
	}

	@Override
	public Map<String, String> readParamFile(String filepath)
			throws ParameterException {

		Map<String, String> params = new HashMap<String, String>();

		try {

			// unmarshal the configuration file
			JAXBContext context = JAXBContext.newInstance(ParamsFile.class);
			Unmarshaller um = context.createUnmarshaller();
			ParamsFile paramsFile = (ParamsFile) um
					.unmarshal(new FileInputStream(new File(filepath)));

			int numMandatory = 0;
			// each parameter is added to the map
			List<ConfParam> confParms = paramsFile.getConfParams();
			for (ConfParam confParam : confParms) {
				String name = confParam.getName();
				String value = confParam.getValue();

				// check if the name or value are empty
				if (name == null || value == null) {
					throw new ParameterException();
				}
				if (name.trim().isEmpty() || value.trim().isEmpty()) {
					throw new ParameterException();
				}

				// check the mandatory parameters
				if (mandatoryParams.contains(name)) {
					numMandatory++;
				}

				params.put(name, value);
			}

			// missing mandatory parameters
			if (numMandatory != mandatoryParams.size()) {
				throw new ParameterException(
						"At least one mandatory parameter is missing");
			}

		} catch (JAXBException e) {
			ParameterException e2 = new ParameterException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} catch (FileNotFoundException e) {
			ParameterException e2 = new ParameterException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}

		return params;
	}

}