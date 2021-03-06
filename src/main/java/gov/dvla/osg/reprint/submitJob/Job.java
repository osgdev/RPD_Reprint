package gov.dvla.osg.reprint.submitJob;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import gov.dvla.osg.reprint.models.Config;
import gov.dvla.osg.reprint.network.RestClient;
import javafx.application.Platform;

public class Job {

	static final Logger LOGGER = LogManager.getLogger();
	
    /**
     * Constructs the header and file body for the HTML message as a MultiPart object
     * and then passes it to the RestClient to send to RPD.
     * @param filename Full path to the file in the working directory
     * @throws Exception
     */
    public static void submit(String filename) throws Exception {

        String url = Config.getProtocol() + Config.getHost() + ":" + Config.getPort() + Config.getSubmitJobUrl();

        try {
            LOGGER.debug("Creating MultiPart...");
        	// construct html body with file as attachment
        	MultiPart multiPart = new MultiPart();
            multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
            multiPart.bodyPart(new FileDataBodyPart("file", new File(filename)));
            LOGGER.debug("MultiPart Created!");
            
            // send the message to RPD - errors thrown by outer catch block
        	Response response = RestClient.rpdSubmit(url, multiPart);
        	
        	// 202 response means file received by RPD
            if (response.getStatus() == 200) {
            	LOGGER.info("Success!");
            	// File received by RPD, file can be safely deleted
            	if (Files.exists(Paths.get(filename), LinkOption.NOFOLLOW_LINKS)) {
            		Files.deleteIfExists(Paths.get(filename));
            	} 
            } else {
            	LOGGER.error("Failure: Status='{}' Reason='{}'", String.valueOf(response.getStatus()), response.getStatusInfo().getReasonPhrase());
            	Platform.runLater(() -> ErrorMsg(String.valueOf(response.getStatus()), response.getStatusInfo().getReasonPhrase()));
        	}	
        } catch (Exception ex) {
        	// submission errors are handled by the SubmitJobController
			throw ex;
        }
    }
}
