package gov.dvla.osg.reprint.models;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        int start = Integer.parseInt(pieceId);
        int end = Integer.parseInt(endPiece);
        String result = "";
        
        IntStream.range(Integer.parseInt(jobId + pieceId), Integer.parseInt(jobId + endPiece))
                 .mapToObj(Integer::toString)
                 .collect(Collectors.joining("\n"));
        
        for (int count = start; count <= end; count++) {
            if (count < end) {
                result = result + jobId + String.format("%05d", count) + "\n";
                noOfRecords++;
            } else {
                result = result + jobId + String.format("%05d", count);
                noOfRecords++;
            }
        }
        return result;
    }
}
