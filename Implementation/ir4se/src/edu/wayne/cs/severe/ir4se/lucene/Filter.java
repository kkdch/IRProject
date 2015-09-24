package edu.wayne.cs.severe.ir4se.lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Filter {

	public static String eliminateBlanks(String stringIn) {
		String stringOut = stringIn.replaceAll("\n", " ");
		stringOut = stringOut.replaceAll("\r", " ");
		stringOut = stringOut.replaceAll("\t", " ");
		int pos = stringOut.indexOf("  ");
		while (pos > 0) {
			stringOut = stringOut.replaceAll("  ", " ");
			pos = stringOut.indexOf("  ");
		}
		return stringOut;
	}

	public static String eliminateNumbers(String stringIn) {
		String stringOut = "";
		String[] words = stringIn.split("[ \t\n]");
		for (int j = 0; j < words.length; j++) {
			StringTokenizer st = new StringTokenizer(words[j]);

			while (st.hasMoreTokens()) {
				String word = st.nextToken();
				if (word.matches("[a-zA-Z_]+[a-zA-Z_0-9]*")) {
					stringOut += word + ' ';
				}
			}
		}
		return stringOut;
	}

	public static String eliminateNonLiterals(String stringIn) {
		String stringOut = new String();
		stringOut = stringIn.replaceAll("[^a-zA-Z0-9_]", " ");
		stringOut = eliminateBlanks(stringOut);
		return stringOut;
	}

	public static String eliminateKeywords(String stringIn, String fileName) {
		String stringOut = new String();
		ArrayList<String> keywords = getKeywordsFromFile(fileName);
		String[] words = stringIn.split("[ \t\n]");
		for (int i = 0; i < words.length; i++)
			if (!keywords.contains(words[i].toLowerCase().trim()))
				stringOut += words[i].trim() + " ";
		return stringOut;
	}

	private static ArrayList<String> getKeywordsFromFile(String fileName) {
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fileName));

			String line;
			while ((line = in.readLine()) != null) {
				String[] words = line.split("[ \t\n]");
				for (int i = 0; i < words.length; i++)
					if (!list.contains(words[i].toLowerCase().trim()))
						list.add(words[i].toLowerCase().trim());
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public static String eliminateShortStrings(String stringIn) {
		String stringOut = new String();
		String[] words = stringIn.split("[ \t\n]");
		for (int i = 0; i < words.length; i++)
			if (words[i].trim().length() >= 2)
				stringOut += words[i].trim() + " ";
		return stringOut;
	}
}
