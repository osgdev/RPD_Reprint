package gov.dvla.osg.reprint.admin;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.dvla.osg.reprint.models.Config;
import gov.dvla.osg.reprint.models.Session;
import gov.dvla.osg.reprint.network.RestClient;
import gov.dvla.osg.reprint.utils.JsonUtils;

/**
 * Access to admin area granted to dev team only.
 * Retrieves logged in user's group from RPD (json response).
 * Passes response to utility function to check if user is a member of the Dev group.
 */
public class CheckGroup {
	
	static final Logger LOGGER = LogManager.getLogger();
	
	public static void CheckIfAdmin() {
			    
		String url = Config.getProtocol() + Config.getHost() + ":" + Config.getPort() + Config.getUserUrl() + Session.getInstance().getUserName();
		
		try (Response response = RestClient.rpdGroup(url)) {
			if (response.getStatus() == 200) {
				String jsonData = response.readEntity(String.class);
				Session.getInstance().setIsAdmin(JsonUtils.isUserInDevGroup(jsonData));
			} else {
				// thrown exception prevents main window from opening.
				String msg = "Null response from RPD web server.";
				LOGGER.fatal(msg);
				throw new Exception(msg);
			}
		} catch (ProcessingException e) {
			ErrorMsg("Connection timed out","Unable to connect to RPD web service.");
		} catch (Exception e) {
			ErrorMsg(e.getMessage(),"Unable to check User Group.");
		}
	}
}
