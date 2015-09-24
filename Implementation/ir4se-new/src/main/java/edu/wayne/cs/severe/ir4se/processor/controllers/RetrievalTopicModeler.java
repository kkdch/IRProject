package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.TopicDistribution;

public interface RetrievalTopicModeler {

	TopicDistribution createTopicDistr(List<RetrievalDoc> docs,
			Map<String, String> plsaParams) throws IOException;

	void closeIndex() throws Exception;

}