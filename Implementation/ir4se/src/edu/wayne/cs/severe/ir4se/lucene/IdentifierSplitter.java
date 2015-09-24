package edu.wayne.cs.severe.ir4se.lucene;

import java.util.StringTokenizer;

public class IdentifierSplitter {

	public static String splitIdentifiers(String stringIn, boolean keepOriginal) {
		String stringOut = new String();

		String[] words = stringIn.split("[ \t\n]");
		for (int j = 0; j < words.length; j++) {
			StringTokenizer st = new StringTokenizer(words[j]);
			String strReturn = "";

			while (st.hasMoreTokens()) {
				String word = st.nextToken();
				String temp_word = new String();
				int len = word.length() - 1;

				boolean lastCharWasNumber = false;
				boolean lastCharWasSmallLetter = false;
				boolean lastCharWasBigLetter = false;
				boolean lastCharWasNoneOfAbove = false;
				boolean letterFoundBeforeNumber = false;

				int i = 0;
				while (i < len) {
					if (word.charAt(i) >= '0' && word.charAt(i) <= '9') {

						temp_word += word.charAt(i);
						/*
						 * if (lastCharWasNumber == false) { if (i<len) if
						 * ((word.charAt(i+1) >= 'A' && word.charAt(i+1) <= 'Z')
						 * ||word.charAt(i+1) >= 'a' && word.charAt(i+1) <= 'z'
						 * ); temp_word += ' '; } /* else { temp_word +=
						 * word.charAt(i); }
						 */
						lastCharWasNumber = true;
						lastCharWasBigLetter = false;
						lastCharWasSmallLetter = false;
						lastCharWasNoneOfAbove = false;
					} else if (word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') {
						if (lastCharWasBigLetter == false) {
							if (i > 0)
								temp_word += ' ';
							temp_word += word.charAt(i);
						} else {
							if (i < len - 1 && ((word.charAt(i + 1) >= 'a' && word.charAt(i + 1) <= 'z'))) {
								if (i > 0)
									temp_word += ' ';
								temp_word += word.charAt(i);
							} else {
								temp_word += word.charAt(i);
							}
						}
						lastCharWasBigLetter = true;
						lastCharWasNumber = false;
						lastCharWasSmallLetter = false;
						lastCharWasNoneOfAbove = false;
						letterFoundBeforeNumber = true;
					} else if ((word.charAt(i) >= 'a' && word.charAt(i) <= 'z')) {
						if (lastCharWasNoneOfAbove || lastCharWasBigLetter || lastCharWasSmallLetter) {
							temp_word += word.charAt(i);
						} else {
							if (i > 0)
								temp_word += ' ';
							temp_word += word.charAt(i);
						}
						lastCharWasBigLetter = false;
						lastCharWasNumber = false;
						lastCharWasSmallLetter = true;
						lastCharWasNoneOfAbove = false;
						letterFoundBeforeNumber = true;
					} else if (word.charAt(i) == '_') {
						if (i > 0)
							temp_word += ' '; // Replace underscore by space
						lastCharWasBigLetter = false;
						lastCharWasNumber = false;
						lastCharWasSmallLetter = false;
						lastCharWasNoneOfAbove = true;
						letterFoundBeforeNumber = false;
					} else {
						temp_word += word.charAt(i);
						lastCharWasBigLetter = false;
						lastCharWasNumber = false;
						lastCharWasSmallLetter = false;
						lastCharWasNoneOfAbove = true;
					}

					i++;
				}

				if (word.charAt(i) != '_') // so that if the last character is
											// an _, it is ignored
					temp_word += word.charAt(i);

				strReturn += temp_word;
				strReturn = strReturn.toLowerCase().trim();
				strReturn += ' ';
				if (keepOriginal == true && (!temp_word.trim().equals(word.trim()))) {
					strReturn += word;
					strReturn += ' ';
				}
			}
			stringOut += strReturn;
		}
		return stringOut;
	}
}
