package edu.neu.husky.a.pandilwar;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.text.DecimalFormat;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

public class Parser {

	public static void main(String[] args) {

		Node node = nodeBuilder().node();
		Client client = node.client();

		Helper.initializeIndex(client);

		String[] files = Helper.getFilesInFolder();

		long startTime = System.currentTimeMillis();

		for (String file : files) {
			Helper.storeDocNoTextFromFile(Constants.DATA_DIRECTORY + "\\" + file);
		}

		long endTime = System.currentTimeMillis();

		System.err.println("total time taken :: " + (endTime - startTime));

		Map<String, String> information = Helper.getInformation();
		System.out.println(information.get("AP890101-0071"));

		DecimalFormat df = new DecimalFormat("#.##");
		System.out.println("The time taken is to process " + files.length + " files is " + df.format((endTime - startTime) / 60000.0) + " minutes");

		node.close();
	}

}