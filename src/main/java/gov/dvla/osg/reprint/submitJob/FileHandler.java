package gov.dvla.osg.reprint.submitJob;

import static gov.dvla.osg.reprint.models.Session.props;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Constructs the dat and eot files in a temporary folder and then sends them to RPD.
 * This enables admins to manually move files in the event that the REST service 
 * becomes unavailable.
 */
public class FileHandler {
	
    private String datFileName;
    private String eotFileName;
        
    
	/**
	 * Files are all written to the same working directory but each tab
	 * is associated with its own prefix.
	 * @param prefix denotes the type of file being sent (general, card or hal)
	 */
	public void setFileNames(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        String timestamp = now.format(formatter);

        datFileName = props.getProperty("reprintWorkingDir") + prefix + timestamp + ".DAT";
        eotFileName = props.getProperty("reprintWorkingDir") + prefix + timestamp + ".EOT";
    }
    
	/**
	 * dat file content is unique for each tab. 
	 * @param datFileContent
	 * @throws IOException
	 */
	public void setDatFileContent(String datFileContent) throws IOException {
		writeFile(datFileName, datFileContent);
	}

	/**
	 * eot file content is unique for each tab.
	 * @param eotFileContent
	 * @throws IOException
	 */
	public void setEotFileContent(String eotFileContent) throws IOException {
		writeFile(eotFileName, eotFileContent);
	}
    
    /**
     * Writes the file to the temp folder before sending.
     * @param fileName
     * @param content
     * @throws IOException
     */
    private void writeFile(String fileName, String content) throws IOException {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(content);
            bw.close();
    	};
    }
    
    /**
     * Files are in the temp folder and are ready to send to RPD.
     * @throws Exception
     */
    public void submit() throws Exception {
    	Job.submit(datFileName);
        Job.submit(eotFileName);
    }
}
