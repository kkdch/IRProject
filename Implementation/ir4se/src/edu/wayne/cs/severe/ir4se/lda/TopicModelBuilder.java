package edu.wayne.cs.severe.ir4se.lda;

import java.io.File;

import jgibblda.Estimator;
import jgibblda.LDACmdOption;
import edu.wayne.cs.severe.ir4se.lda.utils.LDALuceneInputFileGenerator;

public class TopicModelBuilder {

	private boolean est = true;
	private double alpha = -1.0;
	private double beta = -1.0;
	private int niters = 1000;
	private int ntopics = 100;
	private String wordMapFileName = "model-wordmap.txt";

	private String systemDirPath;
	private String corpusPath;

	public TopicModelBuilder(boolean est, double alpha, double beta, int niters, int ntopics, String systemDir,
			String corpusPath) {
		super();
		this.est = est;
		this.alpha = alpha;
		this.beta = beta;
		this.niters = niters;
		this.ntopics = ntopics;
		this.systemDirPath = systemDir;
		this.corpusPath = corpusPath;
	}

	public void estimateModel() {
		LDACmdOption ldaOptions = new LDACmdOption();
		ldaOptions.est = est;
		ldaOptions.alpha = alpha;
		ldaOptions.beta = beta;
		ldaOptions.K = ntopics;
		ldaOptions.niters = niters;
		ldaOptions.dir = systemDirPath;
		ldaOptions.dfile = corpusPath;
		ldaOptions.wordMapFileName = wordMapFileName;

		Estimator estimator = new Estimator();
		estimator.init(ldaOptions);
		estimator.estimate();
	}

	public void optimize() {
		LDALuceneInputFileGenerator gen = new LDALuceneInputFileGenerator(systemDirPath, true);
		gen.processDataDir();

		File systemDir = new File(systemDirPath);
		for (File f : systemDir.listFiles()) {
			if (f.isFile() && f.getName().startsWith("model-")) {
				f.delete();
			}
		}
	}
}
