package gov.dvla.osg.reprint.models;

public class WholeBatchReprint extends AbstractReprintType {

	public WholeBatchReprint(String input) {
		this.jobId = input.substring(0, 10);
		noOfRecords = 1;
	}

	@Override
	public String toString() {
		return jobId + " full batch";
	}

	public String output() {
		return this.jobId;
	}
}
