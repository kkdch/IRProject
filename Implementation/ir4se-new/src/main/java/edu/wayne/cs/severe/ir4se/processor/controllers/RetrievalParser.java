package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.CorpusException;
import edu.wayne.cs.severe.ir4se.processor.exception.QueryException;
import edu.wayne.cs.severe.ir4se.processor.exception.RelJudgException;

/*
 */
public interface RetrievalParser {

  public List<RetrievalDoc> readCorpus(String corpFilePath, String mapDocsPath) throws CorpusException;

  public List<Query> readQueries(String queriesPath) throws QueryException;

  public Map<Query,RelJudgment> readReleJudgments(String releJudgmentPath, String mapDocsPath) throws RelJudgException;

}