package gov.dvla.osg.reprint.models;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.dvla.osg.reprint.main.Main;

public class AppCredentials {
	static final Logger LOGGER = LogManager.getLogger();
    private final String userName = "ReprintApp";
    private final String password ="@Alpha1234";
    private String token = "";
    private String passwordsFile = "";
    
    public AppCredentials () {
        try{
            /* Retrieve the path of the JAR file. The password file must be in the same folder as the JAR.
             * This is needed since relative paths are relative to the JVM, which runs from the local machine.
             * When the file is run from different environments the password file will always be relative to the JAR path.
             */
            URI uri = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File jarPath = new File(uri).getParentFile();
            if (Main.DEBUG_MODE) {
                passwordsFile = "C:\\Users\\OSG\\Documents\\eclipse\\resources\\ConfigFiles\\appCreds.txt";
            } else {
                passwordsFile = jarPath + "\\config.txt";
            }
        } catch (URISyntaxException e) {
            LOGGER.fatal("Unable to get location of JAR");
        }

    }
    
    /**
     * @return name of app in RPD
     */
    public String getUsername() {
    	return this.userName;
    }
    
    /**
     * Retrieves current password from config file.
     * @return current password
     */
    public String getPassword() {
		try {
			byte[] fileContents = Files.readAllBytes(Paths.get(passwordsFile));
			InputStream reader = new ByteArrayInputStream(fileContents);
			Properties appPasswords = new Properties();
			appPasswords.load(reader);
			return (String) appPasswords.get(userName);
		} catch (Exception ex) {
			LOGGER.fatal("Unable to load application password file.", ex);
		}
    	return this.password;
    }
    

    /**
     * Sets the App's session token.
     *
     * @param token the new token
     */
    public void setToken(String token) {
    	this.token = token;
    }
    
    /**
     * Gets the App's session token.
     *
     * @return the token
     */
    public String getToken() {
    	return token;
    }
    
}
