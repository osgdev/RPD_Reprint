package gov.dvla.osg.reprint.models;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class RangeReprint extends AbstractReprintType {
    private String endPiece;

    public RangeReprint(String input, String input2) {
    	// first ten digits are job ID
        this.jobId = input.substring(0, 10);
        // starting document number
        this.pieceId = input.substring(10,15);
        // final document number in range
        this.endPiece = input2.substring(10,15);
        noOfRecords = 0;
    }

    @Override
    public String toString() {
        return jobId + " " + pieceId + " - " + endPiece;
    }

    /* (non-Javadoc)
     * @see gov.dvla.osg.reprint.models.AbstractReprintType#output()
     */
    public String output() {
        return LongStream.rangeClosed(Long.valueOf(jobId + pieceId), Long.valueOf(jobId + endPiece))
                .mapToObj(Long::toString)
                .collect(Collectors.joining("\n"));
    }
}
