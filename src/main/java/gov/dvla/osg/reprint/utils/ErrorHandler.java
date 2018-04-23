/**
 * 
 */
package gov.dvla.osg.reprint.utils;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Displays a standard error dialog to users where a system or network
 * error has occurred. The parameters are taken from the standard error
 * response received from the RPD REST api. 
 * 
 * Note: standard errors, such as validation failures, are displayed in error labels
 *       on the active form.
 */
public class ErrorHandler {

	static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * @param errorCode Type of error.
	 * @param errorMsg Details of the error.
	 */
	public static void ErrorMsg(String errorCode, String errorMsg) {
		ErrorMsg(errorCode, errorMsg, "If problem persists please contact dev team.");
	}

	/**
	 * @param errorCode Type of error e.g. HTTP response code
	 * @param errorMsg Details of the error.
	 * @param errorAction Prompt for action from the user.
	 */
	public static void ErrorMsg(String errorCode, String errorMsg, String errorAction) {
		
		LOGGER.warn("{} {} {}", errorCode, errorMsg, errorAction);
		
		// create dialog for error message
		Dialog<ButtonType> dialog = new Dialog<>();
		// set the title on the window
		dialog.setTitle("RPD Error");
		// add logo to dialog
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons()
				.add(new Image(ErrorHandler.class.getResource("/Images/logo.jpg").toString()));
		// add content to the pane
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getButtonTypes().add(ButtonType.OK);
		dialogPane.setHeaderText("Error:  " + errorCode);
		dialogPane.setContentText(errorMsg + "\n\n" + errorAction);
		// display dialog and wait for a button to be clicked
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() != ButtonType.OK) {
			LOGGER.fatal("ErrorHandler: error closing dialog.");
		}
	}
}
