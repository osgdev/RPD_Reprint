package gov.dvla.osg.reprint.login;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

import gov.dvla.osg.reprint.models.Config;
import gov.dvla.osg.reprint.models.Session;
import gov.dvla.osg.reprint.network.RestClient;
import gov.dvla.osg.reprint.utils.RPDJsonParser;;

/**
 * Sends login request to the RPD webservice. Token is obtained from
 * response to authenticate user when submitting files.
 */
public class LogIn {
	
	static final Logger LOGGER = LogManager.getLogger();
	
    private String errorMessage = "";
    private String errorAction = "";
    private String errorCode = "";

    public void login() {
        
        String url = Config.getProtocol() + Config.getHost() + ":" + Config.getPort() + Config.getLoginUrl();
        
        try {
        	Response response = RestClient.rpdLogin(url);
        	LOGGER.debug(response.toString());
        	String data = response.readEntity(String.class); 
            if (response.getStatus() == 200) {
            	Session.setToken(RPDJsonParser.getTokenFromJson(data));
            } else {
            	// RPD provides clear error information, and so is mapped to model
                LoginBadResponseModel br = new GsonBuilder().create().fromJson(data, LoginBadResponseModel.class);
                errorMessage = br.getMessage();
                errorAction = br.getAction();
                errorCode = br.getCode();
            }
        } catch (ProcessingException e) {
			errorMessage = "Unable to connect to RPD web service.";
            errorAction = "If the problem persits, please contact Dev team.";
            errorCode = "connection timed out";
        } catch (NullPointerException e) {
        	errorMessage = "Unable to connect to RPD web service.";
            errorAction = "If the problem persits, please contact Dev team.";
            errorCode = "Invalid IP address for RPD";
        }
        catch (Exception e) {
			errorMessage = e.getMessage();
            errorAction = "If the problem persits, please contact Dev team.";
            errorCode = "Login error: " + e.getClass().getSimpleName();
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorAction() {
        return errorAction;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
