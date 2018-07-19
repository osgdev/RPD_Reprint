package gov.dvla.osg.reprint.submitJob;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;

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
     * @param fileName the temp file to save the data to
     * @param content the content to put in the file
     * @throws IOException
     */
    private void writeFile(String fileName, String content) throws IOException {
        File f = new File(fileName);
        // Check that the user has write access to the temp directory
        if (f.canWrite()) {
            FileUtils.writeStringToFile(f, content, Charset.forName("UTF-8"));
        } else {
            throw new IOException("User '" + Session.getInstance().getUserName() + "' does not have write permission to " + f.getParent());
        }
    }

    /**
     * Gets the DAT file name.
     *
     * @return the dat file name
     */
    public String getDatFileName() {
        return datFileName;
    }
    
    /**
     * Gets the EOT file name.
     *
     * @return the eot file name
     */
    public String getEotFileName() {
        return eotFileName;
    }
}
