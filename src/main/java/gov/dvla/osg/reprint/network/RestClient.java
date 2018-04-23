package gov.dvla.osg.reprint.network;

import static gov.dvla.osg.reprint.models.Session.*;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import gov.dvla.osg.reprint.models.AppCredentials;
import gov.dvla.osg.reprint.models.Session;
import gov.dvla.osg.reprint.utils.JsonUtils;

/**
 * Utility methods to transmit messages to the RPD REST service.
 * These are set by the RPD REST api and shouldn't be amended.
 */
public class RestClient {
	
	/**
	 * Sends a login request to RPD using credentials in the Session object.
	 * @param url RPD login URL in format hostname:port address
	 * @return Response in JSON format, containing a session token for the currently logged in user
	 */
	public static Response rpdLogin(String url) {
		
				//Note: unencrypted credentials is a requirement of the RPD RESTAPI
				return ClientBuilder.newClient()
				.target(url)
				.queryParam("name", Session.userName)
				.queryParam("pwd", Session.password)
				.request(MediaType.APPLICATION_JSON)
				.get();
	}
	
	/**
	 * Requests the RPD Group the currently logged in user belongs to. Requires
	 * a session token to already be set.
	 * @param url RPD RestApi URL in format hostname:port address
	 * @return Response in JSON format includes an array of groups to which the user belongs
	 */
	public static Response rpdGroup(String url) {
		return ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.target(url)
				.queryParam("attribute", "User.Groups")
				.request(MediaType.APPLICATION_JSON)
				.header("token", Session.token)
				.get();
	}

	/**
	 * Sends files to data input device in RPD. 
	 * @param url RPD RestApi URL in format hostname:port address
	 * @param multiPart Should contain the file(s) to transmit
	 * @return 202 status code if file was transmitted successfully
	 */
	public static Response rpdSubmit(String url, MultiPart multiPart) {
		
		/*********** Retrieve token for the AIW user ****************/
		String loginUrl = props.getProperty("protocol") 
        		+ props.getProperty("host") + ":" + props.getProperty("port")
                + props.getProperty("loginUrl");
		
		AppCredentials appCredentials = new AppCredentials();
		
		Response response = ClientBuilder.newClient()
			.target(loginUrl)
			.queryParam("name", appCredentials.getUsername())
			.queryParam("pwd", appCredentials.getPassword())
			.request(MediaType.APPLICATION_JSON)
			.get();
		
		String data = response.readEntity(String.class);
		appCredentials.setToken(JsonUtils.getTokenFromJson(data));
		/**********************************************************/
		
		// Send file using AIW user's token
		return ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.target(url)
				.request(MediaType.APPLICATION_JSON)
		        .header("ippdcredential", "<credential token='" + appCredentials.getToken() + "'/>")
		        .post(Entity.entity(multiPart, multiPart.getMediaType()));
	}
	
	/**
	 * Log user out of RPD.
	 * @param url RPD logout URL in format hostname:port address
	 * @return Response in JSON format
	 */
	public static Response rpdLogOut(String url) {
		return ClientBuilder.newClient().target(url)
				.path(Session.userName)
				.request(MediaType.APPLICATION_JSON)
				.header("token", Session.token)
				.post(null);
	}
}
