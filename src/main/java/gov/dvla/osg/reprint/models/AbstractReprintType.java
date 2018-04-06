package gov.dvla.osg.reprint.models;

/**
 * Abstract class enables us to hold all job types in the same list.
 */
public abstract class AbstractReprintType implements Comparable<AbstractReprintType> {

	protected String jobId;
	protected String pieceId;
	protected Integer noOfRecords;
	
	/**
	 * Returns a complete list of all document numbers in a range or just a single document/batch number
	 * @return document number(s)
	 */
	public abstract String output();

	/* 
	 * The data file that we send to RPD needs to be sorted in JobId order.
	 */
	public int compareTo(AbstractReprintType compareReprint) {

	      String compareJobId = compareReprint.getFullJobNumber();
	      return this.getFullJobNumber().compareTo(compareJobId);
	      
	}
	
	/**
	 * Will be 1 for single and batch but multiple for range reprints
	 * @return total number of documents
	 */
	public Integer getNoOfRecords() {
		return noOfRecords;
	}
	
	public String getFullJobNumber() {
		String piece = this.pieceId != null ? this.pieceId : "";
		return jobId + piece;
	}

	public String getPieceId() {
		return pieceId;
	}
	
}
