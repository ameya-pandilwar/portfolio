package in.webrilliance.ameyapandilwar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class Tokenizer {

	public static Map<String, List<Dblock>> termMap = new HashMap<String, List<Dblock>>();
	public static Map<String, String> stemwords = new HashMap<String, String>();
	public static List<String> stoplist = new ArrayList<String>();
	
	public static void main(String[] args) {
		loadStoplist();
		loadStemmerWords();
		String docId = "20";
		String docText = "The car was in the car wash.";
		String docId2 = "21A";
		String docText2 = "One person could not wash car.";

		generateTermMap(docId, docText.toLowerCase());
		generateTermMap(docId2, docText2.toLowerCase());

		printResults(termMap);
	}

	public static void generateTermMap(String _docId, String _docText) {
//		long startTime = System.currentTimeMillis();
		Map<String, Dblock> map = new HashMap<String, Dblock>();
		List<String> terms = new ArrayList<String>();
		Matcher matcher = Constants.PATTERN.matcher(_docText);
		while (matcher.find()) {
			Dblock dblock = new Dblock();
			for (int i=0; i< matcher.groupCount(); i++) {
				String term = matcher.group(i);
				if (!stoplist.contains(term)) {
					if (stemwords.get(term) != null)
						term = stemwords.get(term);
					if (!map.containsKey(term)) {
						dblock.setDocId(_docId);
						dblock.appendToTermPositions(matcher.start());
						map.put(term, dblock);
					} else {
						dblock = map.get(term);
						dblock.appendToTermPositions(matcher.start());
						map.put(term, dblock);
					}
					if (!terms.contains(term))
						terms.add(term);
				}
			}
		}

		for (String term : terms) {
			Dblock dblock = map.get(term);
			if (!termMap.containsKey(term)) {
				List<Dblock> list = new ArrayList<Dblock>();
				list.add(dblock);
				termMap.put(term, list);
			} else {
				List<Dblock> list = termMap.get(term);
				list.add(dblock);
				termMap.put(term, list);
			}
		}
//		System.out.println("time taken for doc " + _docId + " is :: " + String.valueOf(System.currentTimeMillis() - startTime) + " milliseconds");
	}
	
	private static void printResults(Map<String, List<Dblock>> map) {
		Set<String> keys = map.keySet();
		for (String key : keys) {
			List<Dblock> dblocklist = map.get(key);
			System.out.println("==="+key);
			for (Dblock dblock : dblocklist) {
				System.out.print(" :: doc_id :: " + dblock.getDocId());
				System.out.print(" :: df :: " + dblock.getTermFrequency());
				System.out.print(" :: pos :: " + dblock.getTermPositions() + "\n");
				
				System.out.println(dblock.toString());
			}
		}
	}

	public static void loadStoplist() {
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Ameya\\Desktop\\stoplist.txt"))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				stoplist.add(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void loadStemmerWords() {
		long startTime = System.currentTimeMillis();
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Ameya\\Desktop\\diffs.txt"))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				sCurrentLine = sCurrentLine.replaceAll("\\s+", " ").trim();
				try {
					stemwords.put(sCurrentLine.split(" ")[0], sCurrentLine.split(" ")[1]);
				} catch (Exception e) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("time taken for loading stemmer is :: " + String.valueOf(System.currentTimeMillis() - startTime) + " milliseconds");
	}	
}