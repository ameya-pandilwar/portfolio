package edu.neu.husky.a.pandilwar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.indices.IndexAlreadyExistsException;

public class Helper {

	public static Map<String, String> information = new HashMap<String, String>();

	public static Map<String, String> getInformation() {
		return information;
	}

	public static void setInformation(Map<String, String> information) {
		Helper.information = information;
	}

	public static String[] getFilesInFolder() {
		File directory = new File(Constants.DATA_DIRECTORY);
		return directory.list();
	}

	public static void storeDocNoTextFromFile(String file) {
		String docNo = "";
		StringBuilder text = new StringBuilder();
		boolean flag = false;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;

			while ((line = br.readLine()) != null) {
				if (line.contains("<DOCNO>")) {
					docNo = line.split(" ")[1];
					System.out.print(docNo + " ");
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
					information.put(docNo, text.toString());
					text.setLength(0);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void initializeIndex(Client client) {
		try {
			client.admin().indices().prepareCreate("panda-java-index")
			.setSettings(ImmutableSettings.settingsBuilder().put("number_of_shards", 1).put("number_of_replicas", 0))
			.execute().actionGet();
		} catch (  Exception e) {
			if (ExceptionsHelper.unwrapCause(e) instanceof IndexAlreadyExistsException) {
				System.err.println("index already exists :: " + e.getMessage());
			}
		}
	}

}