package gov.dvla.osg.reprint.models;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.dvla.osg.reprint.main.Main;

public class AppCredentials {
	static final Logger LOGGER = LogManager.getLogger();
    private final String userName = "ReprintApp";
    private final String password ="@Juliet1234";
    private String token = "";
    private String passwordsFile = "";
    
    {
    	if (Main.DEBUG_MODE) {
    		passwordsFile = "C:\\Users\\OSG\\Documents\\eclipse\\resources\\ConfigFiles\\appCreds.txt";
    	} else {
    		passwordsFile = "\\OSG\\OIs\\RPD\\config.txt";
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
			// load application properties
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
     * @param token
     */
    public void setToken(String token) {
    	this.token = token;
    }
    
    /**
     * @return
     */
    public String getToken() {
    	return token;
    }
    
}
