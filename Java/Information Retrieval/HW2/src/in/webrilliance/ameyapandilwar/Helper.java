package in.webrilliance.ameyapandilwar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

	public static void main(String[] args) {
		mergeFilesIntoOne();
	}

	public static void mergeFilesIntoOne() {
		String OUTPUT_DIR = "C:\\Users\\Ameya\\Desktop\\outputHW2\\";
		File directory = new File(OUTPUT_DIR);
		String[] files = directory.list();
		for (String file : files) {
			System.out.println(file);
		}

		String f1 = OUTPUT_DIR + files[0];
		String f2 = OUTPUT_DIR + files[1];

		try {
			File file = new File(OUTPUT_DIR + "merge.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			BufferedReader f1Br = new BufferedReader(new FileReader(f1));
			BufferedReader f2Br = new BufferedReader(new FileReader(f2));

			String f1Line = f1Br.readLine();
			String f2Line = f2Br.readLine();
			while(true) {
				String f1Word = "", f2Word = "";
				if (f1Line == null || f2Line == null) {
					if (f1Line == null && f2Line != null) {
						f1Word = "";
						f2Word = f2Line.split("-")[0];
					} else if (f2Line == null && f1Line != null) {
						f1Word = f1Line.split("-")[0];
						f2Word = "";
					} else {
						break;
					}
				} else {
					f1Word = f1Line.split("-")[0];
					f2Word = f2Line.split("-")[0];
				}

				if (f1Word.compareTo(f2Word) == 0) {
					bw.write(f1Word + " = " + f2Word + "\n");
					f1Line = f1Br.readLine();
					f2Line = f2Br.readLine();
				} else if (f1Word.compareTo(f2Word) > 0) {
//					bw.write(f2Word + "\n");
					bw.write(f1Word + " > " + f2Word + "\n");
					if (!f2Word.equalsIgnoreCase("")) {
						f2Line = f2Br.readLine();
					} else {
						f1Line = f1Br.readLine();
					}
				} else if (f1Word.compareTo(f2Word) < 0) {
//					bw.write(f1Word + "\n");
					bw.write(f1Word + " < " + f2Word + "\n");
					if (!f1Word.equalsIgnoreCase("")) {
						f1Line = f1Br.readLine();
					} else {
						f2Line = f2Br.readLine();
					}
				}
			}

			bw.close();
			System.out.println("Done");

			f1Br.close();
			f2Br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}