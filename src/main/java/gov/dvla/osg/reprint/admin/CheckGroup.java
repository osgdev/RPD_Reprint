package gov.dvla.osg.reprint.admin;

import static gov.dvla.osg.reprint.models.Session.props;
import static gov.dvla.osg.reprint.utils.ErrorHandler.ErrorMsg;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import gov.dvla.osg.reprint.models.Session;
import gov.dvla.osg.reprint.network.RestClient;
import gov.dvla.osg.reprint.utils.JsonUtils;

/**
 * Access to admin area granted to dev team only.
 * Retrieves logged in user's group from RPD (json response).
 * Passes response to utility function to check if user is a member of the Dev group.
 */
public class CheckGroup {

	public static void CheckIfAdmin() {
		
		String url = props.getProperty("protocol") 
				+ props.getProperty("host") + ":" + props.getProperty("port")
				+ props.getProperty("userUrl") + Session.userName;
		
		try (Response response = RestClient.rpdGroup(url)) {
			if (response.getStatus() == 200) {
				String jsonData = response.readEntity(String.class);
				Session.isAdmin = JsonUtils.isUserInDevGroup(jsonData);
			} else {
				// thrown exception prevents main window from opening.
				throw new Exception("Null response from RPD web server.");
			}
		} catch (ProcessingException e) {
			ErrorMsg("Connection timed out","Unable to connect to RPD web service.");
		} catch (Exception e) {
			ErrorMsg(e.getMessage(),"Unable to check User Group.");
		}
	}
}
