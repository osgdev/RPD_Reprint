package gov.dvla.osg.reprint.utils;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import com.google.gson.*;

/**
 * Utility methods to extract information from the JSON data responses that are
 * returned from the RPD REST api.
 */
public class RPDJsonParser {
	
	/**
	 * Extracts the user group from the message body of a request.
	 * @param jsonString Json to search in.
	 * @return true if developer, otherwise false.
	 */
	public static Boolean isUserInDevGroup(String jsonString) {
		try {
			JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
			// loop through and check all groups the user is a member of
			if (json.has("User.Groups") && json.get("User.Groups").isJsonArray()) {
			    for (JsonElement group : json.get("User.Groups").getAsJsonArray()) {
			        if (group.getAsString().equalsIgnoreCase("dev")) {
			            return true;
			        }
			    }
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
