package gov.dvla.osg.reprint.login;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import java.util.Optional;
import java.util.Properties;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import gov.dvla.osg.reprint.models.Session;
import gov.dvla.osg.reprint.network.RestClient;
import gov.dvla.osg.reprint.utils.ErrorHandler;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LogOut {

	/**
	 * Displays a confirmation dialog to the user to check they really do want to
	 * log off. If user chooses OK, the application closes regardless of the
	 * server's response.
	 */
	public static void logout() {
		// create dialog to confirm logout
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Logout");
		// add logo to dialog window frame
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons()
				.add(new Image(ErrorHandler.class.getResource("/Images/logo.jpg").toString()));
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContentText("Do you really want to log out?");
		// display dialog and wait for a button to be clicked
		Optional<ButtonType> result = dialog.showAndWait();

		// logout if user clicks the OK button
		if (result.isPresent() && result.get() == ButtonType.OK) {
		    Properties props = Session.getProps();
			String url = props.getProperty("protocol") + props.getProperty("host") + ":" + props.getProperty("port")
					+ props.getProperty("logoutUrl");

			try {
				Response response = RestClient.rpdLogOut(url);
				if (response.getStatus() != 200) {
					ErrorMsg("Logout failed", "Unable to log user out of RPD web service.");
				}
			} catch (ProcessingException e) {
				ErrorMsg("Connection timed out", "Unable to log user out of RPD web service.");
			} catch (Exception e) {
				ErrorMsg(e.getClass().getSimpleName(), e.getMessage());
			}
			// quits the application after error dialogs closed
			Platform.exit();
		}
	}
}
