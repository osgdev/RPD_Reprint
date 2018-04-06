package gov.dvla.osg.reprint.submitJob;

import static gov.dvla.osg.reprint.models.Session.props;
import static gov.dvla.osg.reprint.utils.ErrorHandler.ErrorMsg;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import gov.dvla.osg.reprint.network.RestClient;
import javafx.application.Platform;

public class Job {

    /**
     * Constructs the header and file body for the HTML message as a MultiPart object
     * and then passes it to the RestClient to send to RPD.
     * @param filename Full path to the file in the working directory
     * @throws Exception
     */
    public static void submit(String filename) throws Exception {

        String url = props.getProperty("protocol") 
        		+ props.getProperty("host") + ":" + props.getProperty("port")
                + props.getProperty("submitJobUrl");

        try {
        	// construct html body with file as attachment
        	System.out.println("\tCreating MultiPart...");
        	MultiPart multiPart = new MultiPart();
            multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
            multiPart.bodyPart(new FileDataBodyPart("file", new File(filename)));
            System.out.println("\tMultiPart Created!");
            // send the message to RPD - errors thrown by outer catch block
        	Response response = RestClient.rpdSubmit(url, multiPart);
        	// 202 response means file received by RPD
            if (response.getStatus() == 202) {
            	System.out.println("Success!");
            	// File received by RPD, file can be safely deleted
            	if (Files.exists(Paths.get(filename), LinkOption.NOFOLLOW_LINKS)) {
            		Files.deleteIfExists(Paths.get(filename));
            	} 
            } else {
            	System.out.println("Failure "+String.valueOf(response.getStatus())+response.getStatusInfo().getReasonPhrase());
            	// File failed to transmit, show response status in dialog to user
            	Platform.runLater(() -> {
            		ErrorMsg(String.valueOf(response.getStatus()), response.getStatusInfo().getReasonPhrase());
				});
        	}	
        } catch (Exception e) {
        	// submission errors are handled by the SubmitJobController
			throw e;
        }
    }
}
