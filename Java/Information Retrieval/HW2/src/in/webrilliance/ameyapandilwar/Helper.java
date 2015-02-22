package in.webrilliance.ameyapandilwar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Helper {

	public static String[] getFilesInFolder() {
		File directory = new File(Constants.DATA_DIRECTORY);
		return directory.list();
	}

	public static void storeDocNoTextFromFileInHashMap(String file) {
		String docNo = "";
		StringBuilder text = new StringBuilder();
		boolean flag = false;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;

			while ((line = br.readLine()) != null) {
				if (line.contains("<DOCNO>")) {
					docNo = line.split(" ")[1];
				}

				if (line.contains("<TEXT>") || flag) {
					flag = true;

					if (!line.contains("</TEXT>")) {
						if (!line.contains("<TEXT>")) {
							text = text.append(line + "\n");
						}
					} else {
						flag = false;
					}
				}

				if (line.contains("</DOC>")) {
					flag = false;
					Tokenizer.generateTermMap(docNo, text.toString().toLowerCase());
					text.setLength(0);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}