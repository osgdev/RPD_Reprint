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
    private static String protocol;
    private static String host;
    private static String port;
    private static String userUrl;
    private static String loginUrl;
    private static String logoutUrl;
    private static String reportWorkingDir;
    private static String fileNamePrefixReport;
    private static String submitJobUrl;
    private static String appTypes;
    private static String cardTypes;
    private static String sites;
    private static String fileNamePrefixCards;
    private static String fileNamePrefixGeneral;
    private static String fileNamePrefixHal;
    
    /**
     * @return the propsFile
     */
    public static String getPropsFile() {
        return propsFile;
    }
    
    /**
     * @param props the props to set
     */
    public static void setPropsFile(String file) {
        propsFile = file;
    }
        
    public static byte[] getEncryptedProps() throws IOException {
            // Get props as byte array
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            props.store(output, null);
            // return encrypted props byte array
            return Cryptifier.encrypt(output.toByteArray());
        }
    
    /**
     * @param reader
     * @throws IOException
     */
    public static void setProps(InputStream reader) throws IOException {
        props.load(reader);
        protocol = props.getProperty("protocol");
        host = props.getProperty("host");
        port = props.getProperty("port");
        userUrl = props.getProperty("userUrl");
        loginUrl = props.getProperty("loginUrl");
        logoutUrl = props.getProperty("logoutUrl");
        reportWorkingDir = props.getProperty("reportWorkingDir");        
        reprintWorkingDir = props.getProperty("reprintWorkingDir");
        fileNamePrefixReport = props.getProperty("fileNamePrefixReport");
        submitJobUrl = props.getProperty("submitJobUrl");
        appTypes = props.getProperty("appTypes");
        cardTypes = props.getProperty("cardTypes");
        sites = props.getProperty("sites");
        fileNamePrefixCards = props.getProperty("fileNamePrefixCards");
        fileNamePrefixGeneral = props.getProperty("fileNamePrefixGeneral");
        fileNamePrefixHal = props.getProperty("fileNamePrefixHal");
    }
    /**
     * @param propKey
     * @param propValue
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
    
    /**
     * @return the reprintWorkingDir
     */
    public static String getReprintWorkingDir() {
        return reprintWorkingDir;
    }
    /**
     * @return the internet protocol
     */
    public static String getProtocol() {
        return protocol;
    }
    
    /**
     * @return the host IP address
     */
    public static String getHost() {
        return host;
    }
    
    public static String getPort() {
        return port;
    }
    
    public static String getUserUrl() {
        return userUrl;
    }
    
    public static String getLoginUrl() {
        return loginUrl;
    }
    
    public static String getLogoutUrl() {
        return logoutUrl;
    }
    
    public static String getReportWorkingDir() {
        return reportWorkingDir;
    }
    
    public static String getFileNamePrefixReport() {
        return fileNamePrefixReport;
    }
    
    public static String getSubmitJobUrl() {
        return submitJobUrl;
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
