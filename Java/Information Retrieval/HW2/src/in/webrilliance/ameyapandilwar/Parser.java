package in.webrilliance.ameyapandilwar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Parser {

	public static void main(String[] args) {
		String[] files = Helper.getFilesInFolder();
		long startTime = System.currentTimeMillis();

		Tokenizer.loadStoplist();
		Tokenizer.loadStemmerWords();
		
		int counter = 10;
		for (int i=0; i<files.length; i++) {
			Helper.storeDocNoTextFromFileInHashMap(Constants.DATA_DIRECTORY + "\\" + files[i]);
			
			if (i%25==0) {
				System.out.println(Tokenizer.termMap.size());
				ParserThreaded threaded = new ParserThreaded(String.valueOf(counter++), new TreeMap<String, List<Dblock>>(Tokenizer.termMap));
				threaded.start();
				reinitializeHashMap();
				System.out.println(Tokenizer.termMap.size());
			}
		}
		
		System.out.println(Tokenizer.termMap.size());
		Parser.writeHashMapInFile(counter++, Tokenizer.termMap);
		reinitializeHashMap();
		System.out.println(Tokenizer.termMap.size());

		long endTime = System.currentTimeMillis();
		System.err.println("total time taken to write files :: " + (endTime - startTime));
		System.out.println("--------====-------== starting merging");
		
		Helper.mergeFilesIntoOne();

		DecimalFormat df = new DecimalFormat("#.##");
		System.out.println("The time taken is to process " + files.length + " files is " + df.format((endTime - startTime) / 60000.0) + " minutes");
	}

	public static void reinitializeHashMap() {
		Map<String, List<Dblock>> map = new HashMap<String, List<Dblock>>();
		Tokenizer.termMap = map;
		System.err.println("*** hashmap reinitialized***");
	}

	public static void writeHashMapInFile(int n, Map<String, List<Dblock>> map) {
		try {
			File file = new File("C:\\Users\\Ameya\\Desktop\\outputHW2\\sample"+n+".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			Set<String> keys = map.keySet();
			for (String key : keys) {
				String content = getContent(map.get(key));
				bw.write(key+"="+content+"\n");
			}
			
			bw.close();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getContent(List<Dblock> list) {
		String result = "";
		for (Dblock dblock : list) {
			result = result.concat(dblock.toString());
		}
		return result;
	}

}