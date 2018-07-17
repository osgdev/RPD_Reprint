package gov.dvla.osg.reprint.submitJob;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import gov.dvla.osg.reprint.models.Config;
import uk.gov.dvla.osg.rpd.config.Session;

/**
 * Constructs the dat and eot files in a temporary folder and then sends them to RPD. This enables admins to manually move files in the event that the REST service becomes unavailable.
 */
public class FileHandler {

    private String datFileName;
    private String eotFileName;

    /**
     * Files are all written to the same working directory but each tab is associated with its own prefix.
     * @param prefix denotes the type of file being sent (general, card or hal)
     */
    public void setFileNames(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        String timestamp = now.format(formatter);
        datFileName = Config.getReprintWorkingDir() + prefix + timestamp + ".DAT";
        eotFileName = Config.getReprintWorkingDir() + prefix + timestamp + ".EOT";
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
        File f = new File(fileName);
        if (f.canWrite()) {
            // write access
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
                bw.write(content);
                bw.close();
            }
        } else {
            // no write access
            throw new IOException("User '" + Session.getInstance().getUserName() + "' does not have write permission to " + f.getParent() + ".");
        }
    }

    public String getDatFileName() {
        return datFileName;
    }
    
    public String getEotFileName() {
        return eotFileName;
    }
    /**
     * Files are in the temp folder and are ready to send to RPD.
     * @throws Exception
     */
/*    public void submit() {
        SubmitJobClient client = SubmitJobClient.getInstance(NetworkConfig.getInstance());
        boolean success = client.submit(datFileName);
        if (success) {
            client.submit(eotFileName);
        }
    }*/
}
