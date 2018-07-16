package gov.dvla.osg.reprint.models;

/**
 * Session information for the logged in user.
 */
public class Session {
    
    /******************************************************************************************
     *                              SINGLETON PATTERN
     ******************************************************************************************/

    private static class SingletonHelper {
        private static final Session INSTANCE = new Session();
    }

    public static Session getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    private Session() { }
    
    /*****************************************************************************************/
    
    private String userName;
    private String password;
    private String token;
    private Boolean isAdmin;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the isAdmin
     */
    public Boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     * @param isAdmin the isAdmin to set
     */
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
