package gov.dvla.osg.reprint.login;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import gov.dvla.osg.reprint.admin.CheckGroup;
import gov.dvla.osg.reprint.main.Main;
import gov.dvla.osg.reprint.models.Session;
import gov.dvla.osg.reprint.submitJob.SubmitJobController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Form methods and authentication.
 */
public class LoginController {

	static final Logger LOGGER = LogManager.getLogger();
	
	public TextField nameField;
	public PasswordField passwordField;
	public Button btnLogin;
	public Label lblMessage;

	/**
	 * Submits login request to RPD webservice. If token is retrieved then the user
	 * is authenticated, else the RPD error message is displayed.
	 */
	public void btnLoginClicked() {

		Session.setUserName(nameField.getText().trim());
		Session.setPassword(passwordField.getText().trim());
		// Enable admin window to be loaded so Devs can set IP address prior to logon
		if (Session.getUserName().equals("dev") && Session.getPassword().equals("@Devteam2")) {
			try {
				// close login page and load admin view
				Parent root = FXMLLoader.load(getClass().getResource("/FXML/AdminGui.fxml"));
				Stage adminJobStage = new Stage();
				adminJobStage.setResizable(true);
				adminJobStage.setTitle("RPD Reprints");
				adminJobStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/logo.jpg")));
				adminJobStage.setScene(new Scene(root, 600, 700));
				adminJobStage.show();
				closeLogin();
			} catch (IOException e) {
				ErrorMsg(e.getClass().getSimpleName(), e.getMessage());
			}
		} else {
			// disable buttons while RPD is contacted
			lblMessage.setText("Please wait...");
			btnLogin.setDisable(true);
			nameField.setDisable(true);
			passwordField.setDisable(true);

			final LogIn login = new LogIn();

			// Login performed on background thread to prevent GUI freezing
			new Thread(() -> {
				LOGGER.trace("Attempting to login...");
				// bypass login while testing
				if (!Main.DEBUG_MODE) {
					login.login();
				}
				// if token wasn't retrieved & not in debug mode, display error dialog
				if (Strings.nullToEmpty(Session.getToken()).isEmpty() && !Main.DEBUG_MODE) {
					Platform.runLater(() -> {
						ErrorMsg(login.getErrorCode(), login.getErrorMessage(), login.getErrorAction());
						// cleanup fields
						lblMessage.setText("");
						passwordField.setText("");
						nameField.setDisable(false);
						passwordField.setDisable(false);
						passwordField.requestFocus();
					});
				} else {
					LOGGER.trace("Login Complete.");
					Platform.runLater(() -> {
						try {
							// bypass group check if running in debug mode
							if (!Main.DEBUG_MODE) {
								CheckGroup.CheckIfAdmin();
							}
							// close login page and load main view
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SubmitJobGui.fxml"));
							Parent root = loader.load();
							Stage submitJobStage = new Stage();
							submitJobStage.setResizable(false);
							// Display logged in user in titlebar
							submitJobStage.setTitle("RPD Reprints - " + Session.getUserName());
							submitJobStage.getIcons()
									.add(new Image(getClass().getResourceAsStream("/Images/logo.jpg")));
							submitJobStage.setScene(new Scene(root));
							submitJobStage.show();
							// force logout by routing the close request to the logout method
							submitJobStage.setOnCloseRequest(we -> {
								we.consume();
								((SubmitJobController)loader.getController()).logout();
							});
							closeLogin();
						} catch (IOException e) {
							Platform.runLater(() -> {
								ErrorMsg(e.getClass().getSimpleName(), e.getMessage());
							});
						}
					});
				}
			}).start();
		}
	}

	/**
	 * Only way to automatically close a window is via an object on the form with a
	 * binding on the controller.
	 */
	private void closeLogin() {
		((Stage) btnLogin.getScene().getWindow()).close();
	}

	/**
	 * Method responds to keypress events on the user and password fields. Login
	 * button is only enabled when both fields contain text.
	 */
	public void txtChanged() {
		if (nameField.getText().trim().equals("") || passwordField.getText().trim().equals("")) {
			btnLogin.setDisable(true);
		} else {
			btnLogin.setDisable(false);
		}
	}
}
