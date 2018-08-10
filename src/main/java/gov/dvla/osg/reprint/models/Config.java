package gov.dvla.osg.reprint.models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import gov.dvla.osg.reprint.utils.Cryptifier;

public class Config {

    // absolute filepath for the rpdws.properties file
    private static String propsFile;
    // decrypted properties holding configuration data
    private static Properties props = new Properties();
    private static String reprintWorkingDir;
    private static String reportWorkingDir;
    private static String fileNamePrefixReport;
    private static String appTypes;
    private static String cardTypes;
    private static String sites;
    private static String fileNamePrefixCards;
    private static String fileNamePrefixGeneral;
    private static String fileNamePrefixHal;
    
    /**
     * Gets the props file.
     *
     * @return the props file
     */
    public static String getPropsFile() {
        return propsFile;
    }
    
    /**
     * Sets the props file.
     *
     * @param file the new props file
     */
    public static void setPropsFile(String file) {
        propsFile = file;
    }
        
    /**
     * Gets the encrypted props.
     *
     * @return the encrypted props
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] getEncryptedProps() throws IOException {
            // Get props as byte array
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            props.store(output, null);
            // return encrypted props byte array
            return Cryptifier.encrypt(output.toByteArray());
        }
    
    /**
     * Sets the props.
     *
     * @param reader the new props
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void setProps(InputStream reader) throws IOException {
        props.load(reader);
        reportWorkingDir = props.getProperty("reportWorkingDir");        
        reprintWorkingDir = props.getProperty("reprintWorkingDir");
        fileNamePrefixReport = props.getProperty("fileNamePrefixReport");
        appTypes = props.getProperty("appTypes");
        cardTypes = props.getProperty("cardTypes");
        sites = props.getProperty("sites");
        fileNamePrefixCards = props.getProperty("fileNamePrefixCards");
        fileNamePrefixGeneral = props.getProperty("fileNamePrefixGeneral");
        fileNamePrefixHal = props.getProperty("fileNamePrefixHal");
    }
    
    /**
     * Update property.
     *
     * @param propKey the prop key
     * @param propValue the prop value
     */
    public static void updateProperty(String propKey, String propValue) {
        props.setProperty(propKey, propValue);
    }
    
    public static String getPropertyValue(String propKey) {
        return props.getProperty(propKey);
    }
    
    public static Iterator<Object> getPropsIterator() {
        return props.keySet().stream().sorted().iterator();
    }
    
    public static String getReprintWorkingDir() {
        return reprintWorkingDir;
    }
    
    public static String getReportWorkingDir() {
        return reportWorkingDir;
    }
    
    public static String getFileNamePrefixReport() {
        return fileNamePrefixReport;
    }
    
    public static String getAppTypes() {
        return appTypes;
    }
    
    public static String getCardTypes() {
        return cardTypes;
    }
    
    public static String getSites() {
        return sites;
    }
    
    public static String getFileNamePrefixCards() {
        return fileNamePrefixCards;
    }
    
    public static String getFileNamePrefixGeneral() {
        return fileNamePrefixGeneral;
    }
    
    public static String getFileNamePrefixHal() {
        return fileNamePrefixHal;
    }

}
