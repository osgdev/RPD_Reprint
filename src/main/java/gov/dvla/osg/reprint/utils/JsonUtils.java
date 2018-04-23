package gov.dvla.osg.reprint.utils;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Utility methods to extract information from the JSON data responses that are
 * returned from the RPD REST api.
 */
public class JsonUtils {
	
	/**
	 * Extracts the user group from the message body of a request.
	 * @param jsonString Json to search in.
	 * @return true if developer, otherwise false.
	 */
	public static Boolean isUserInDevGroup(String jsonString) {
		try {
			
			/* User.Groups contains a json array with a single element getAsString() will
			 * fail if there is more than one group in the User.Groups array */
			
			JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
			System.out.println(json.toString());
			// Check if user has been assigned to a group
			boolean hasGroup = json.has("User.Groups");
			if (hasGroup) {
				return json.get("User.Groups").getAsString().equalsIgnoreCase("dev");
			}
		} catch (JsonSyntaxException e) {
			ErrorMsg("getTokenFromJson", "String is not valid JSON.", e.getMessage());
		} catch (Exception e) {
			ErrorMsg("getTokenFromJson", e.getClass().getSimpleName(), e.getMessage());
		}
		return false;
	}

	/**
	 * Extracts the user token from message body of a successful RPD login request
	 * 
	 * @param jsonString RPD login request message body
	 * @return session token, or blank string if token not available
	 */
	public static String getTokenFromJson(String jsonString) {
		try {
			return new JsonParser().parse(jsonString).getAsJsonObject().get("token").getAsString();
		} catch (JsonSyntaxException e) {
			ErrorMsg("getTokenFromJson", "String is not valid JSON.", e.getMessage());
		} catch (Exception e) {
			ErrorMsg("getTokenFromJson", e.getClass().getSimpleName(), e.getMessage());
		}
		return "";
	}
}
