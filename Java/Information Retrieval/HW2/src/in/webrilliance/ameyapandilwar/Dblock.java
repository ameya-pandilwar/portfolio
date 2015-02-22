package in.webrilliance.ameyapandilwar;

public class Dblock {

	private String docId;
	private int termFrequency;
	private String termPositions;
	
	public String getDocId() {
		return docId;
	}
	
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public int getTermFrequency() {
		return termFrequency;
	}
	
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}
	
	public String getTermPositions() {
		return termPositions;
	}
	
	public void setTermPositions(String termPositions) {
		this.termPositions = termPositions;
	}
	
	public void appendToTermPositions(int termPosition) {
		if (this.termPositions != null && !this.termPositions.equalsIgnoreCase("")) {
			this.termPositions = this.termPositions + "," + String.valueOf(termPosition);
			this.termFrequency += 1;
		} else {
			this.termPositions = String.valueOf(termPosition);
			this.termFrequency = 1;
		}
	}
	
	@Override
	public String toString() {
		String result = "";
		result = this.docId + ":" + this.termFrequency + ":" + this.termPositions + ";";
		return result;
	}
	
}