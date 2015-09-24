package edu.wayne.cs.severe.ir4se.utils;

public class StringUtils {

	public static String cleanPath(String path) {
		String res = path.toLowerCase();
		res = res.replaceAll("\n", "");
		res = res.replaceAll("\r", "");
		res = res.replaceAll("\t", "");
		res = res.replaceAll("/", "\\.");
		// res = res.replaceAll("::","\\.");
		res = res.replaceAll(", ", ",");
		if (res.charAt(0) == '.')
			res = res.substring(1);
		return res;
	}

	public static String getSystemName(String systemDir) {
		String temp = systemDir.substring(0, systemDir.length() - 1);
		int index = temp.lastIndexOf('/');
		return temp.substring(index + 1);
	}

}
