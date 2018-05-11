package gov.dvla.osg.reprint.models;

import java.util.Properties;

/**
 * Session information for the logged in user.
 */
public class Session {
    private static String userName;
    private static String password;
    private static String token;
    private static Boolean isAdmin;
    // absolute filepath for the rpdws.properties file
    private static String propsFile;
    // decrypted properties holding configuration data
    private static Properties props = new Properties();

    /**
     * @return the userName
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public static void setUserName(String userName) {
        Session.userName = userName;
    }

    /**
     * @return the password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public static void setPassword(String password) {
        Session.password = password;
    }

    /**
     * @return the token
     */
    public static String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public static void setToken(String token) {
        Session.token = token;
    }

    /**
     * @return the isAdmin
     */
    public static Boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     * @param isAdmin the isAdmin to set
     */
    public static void setIsAdmin(Boolean isAdmin) {
        Session.isAdmin = isAdmin;
    }

    /**
     * @return the propsFile
     */
    public static String getPropsFile() {
        return propsFile;
    }

    /**
     * @return the props
     */
    public static Properties getProps() {
        return props;
    }

    /**
     * @param props the props to set
     */
    public static void setPropsFile(String file) {
        Session.propsFile = file;
    }

}
