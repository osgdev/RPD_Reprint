package gov.dvla.osg.reprint.models;

import java.util.Properties;

/**
 * Session information for the logged in user.
 */
public class Session {
    public static String userName;
    public static String password;
    public static String token;
    public static Boolean isAdmin;
	// absolute filepath for the rpdws.properties file
	public static String propsFile;
	// decrypted properties holding configuration data
	public static Properties props;
}
