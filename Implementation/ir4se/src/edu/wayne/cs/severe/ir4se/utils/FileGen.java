package edu.wayne.cs.severe.ir4se.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileGen {
	public static void copyFile(String inputPath, String outputPath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
		FileWriter writer = new FileWriter(outputPath);
		try {
			String line;

			while ((line = reader.readLine()) != null) {
				writer.write(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		reader.close();
		writer.close();
	}
}
