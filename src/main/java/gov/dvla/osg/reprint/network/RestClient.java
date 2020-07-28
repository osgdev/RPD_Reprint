package gov.dvla.osg.reprint.network;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import gov.dvla.osg.reprint.models.Session;

/**
 * Utility methods to transmit messages to the RPD REST service.
 * These are set by the RPD REST api and shouldn't be amended.
 */
public class RestClient {
	
	static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Sends a login request to RPD using credentials in the Session object.
	 * @param url RPD login URL in format hostname:port address
	 * @return Response in JSON format, containing a session token for the currently logged in user
	 */
	public static Response rpdLogin(String url) {
		// webform data encoded in body of the HTTP request
		Form formData = new Form();
		formData.param("name", Session.getUserName());
		formData.param("pwd", Session.getPassword());
		
		return ClientBuilder.newClient()
							.target(url)
							.request(MediaType.APPLICATION_JSON)
							.post(Entity.form(formData));
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
							.queryParam("criteria", "\"" + Session.getUserName() + "\"")
							.request(MediaType.APPLICATION_JSON)
							.header("token", Session.getToken())
							.get();
	}

	/**
	 * Sends files to data input device in RPD. 
	 * @param url RPD RestApi URL in format hostname:port address
	 * @param multiPart Should contain the file(s) to transmit
	 * @return 202 status code if file was transmitted successfully
	 */
	public static Response rpdSubmit(String url, MultiPart multiPart) {
	    
		// Send file using the new token
		return ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.target(url)
				.request(MediaType.APPLICATION_JSON)
		        .header("token", Session.getToken())
		        .post(Entity.entity(multiPart, multiPart.getMediaType()));
	}
	
	/**
	 * Log user out of RPD.
	 * @param url RPD logout URL in format hostname:port address
	 * @return Response in JSON format
	 */
	public static Response rpdLogOut(String url) {
		return ClientBuilder.newClient().target(url)
				.path(Session.getUserName())
				.request(MediaType.APPLICATION_JSON)
				.header("token", Session.getToken())
				.post(null);
	}
}
