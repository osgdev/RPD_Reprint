package gov.dvla.osg.reprint.models;

public class SingleReprint extends AbstractReprintType {

    public SingleReprint(String input) {

        this.jobId = input.substring(0, 10);
        this.pieceId = input.substring(10, 15);
        noOfRecords = 1;
    }

    public String output() {
        return this.jobId + this.pieceId;
    }

    @Override
    public String toString() {
        return jobId + " " + pieceId;
    }
}
