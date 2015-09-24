package edu.wayne.cs.severe.ir4se.lucene;

import java.io.File;

public class Utils {

	// Deletes all files under dir, but not the directories
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		} else
			dir.delete();
		// The directory is now empty so delete it
		return true;
	}

	public static String transformPath(String path) {

		char[] keywordFileArray = path.toCharArray();
		for (int i = 0; i < keywordFileArray.length; i++) {
			if (keywordFileArray[i] == '\\')
				keywordFileArray[i] = '/';
		}
		String res = new String(keywordFileArray);
		return res;
	}
}
