package gov.dvla.osg.reprint.login;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.dvla.osg.reprint.utils.ErrorHandler;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.gov.dvla.osg.rpd.web.client.LogOutClient;
import uk.gov.dvla.osg.rpd.web.config.NetworkConfig;
import uk.gov.dvla.osg.rpd.web.error.RpdErrorResponse;

public class LogOut {

    private static final Logger LOGGER = LogManager.getLogger();
    
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
		    LogOutClient client = LogOutClient.getInstance(NetworkConfig.getInstance());
		    boolean success = client.logOut();
		    if (!success) {
		        RpdErrorResponse errMsg = client.getErrorResponse();
		        LOGGER.error("{} {} {} \n{}", errMsg.getCode(), errMsg.getMessage(), errMsg.getAction(), errMsg.getException());
		        ErrorMsg(errMsg.getCode(), errMsg.getMessage(), errMsg.getAction());
		    }
			// quits the application after error dialogs closed
			Platform.exit();
		}
	}
}
