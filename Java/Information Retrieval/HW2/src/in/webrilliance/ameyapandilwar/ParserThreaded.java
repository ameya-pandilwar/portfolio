package in.webrilliance.ameyapandilwar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserThreaded implements Runnable {

	private Thread t;
	private String threadName;
	private Map<String, List<Dblock>> map = new HashMap<String, List<Dblock>>();

	ParserThreaded(String name, Map<String, List<Dblock>> _termMap){
		threadName = name;
		map = _termMap;
		System.out.println("Creating " +  threadName);
	}

	@Override
	public void run() {
		Parser.writeHashMapInFile(Integer.valueOf(threadName), map);
	}

	public void start() {
		System.out.println("Starting " +  threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
}