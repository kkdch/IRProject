package edu.wayne.cs.severe.ir4se.lucene;

// general class that provides access to the stemming functions of different stemmers
// for now, only the Porter stemmer is available; future versions will offer more stemmers

public class GeneralStemmer {

	public static String stemmingPorter(String stringIn) {
		String stringOut = new String();

		String[] words = stringIn.split("[ \t\n]");
		for (int i = 0; i < words.length; i++) {
			PorterStemmer stemmer = new PorterStemmer();
			String stem = "";
			String word = words[i];
			for (int j = 0; j < word.length(); j++)
				stemmer.add(Character.toLowerCase(word.charAt(j)));
			stemmer.stem();
			stem = stemmer.toString();

			stringOut += stem.trim() + " ";
		}

		return stringOut;
	}

}
