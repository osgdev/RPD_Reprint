package gov.dvla.osg.reprint.login;

import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.dvla.osg.reprint.main.Main;
import gov.dvla.osg.reprint.submitJob.SubmitJobController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.gov.dvla.osg.rpd.web.client.CheckGroupClient;
import uk.gov.dvla.osg.rpd.web.client.LoginClient;
import uk.gov.dvla.osg.rpd.web.config.NetworkConfig;
import uk.gov.dvla.osg.rpd.web.config.Session;
import uk.gov.dvla.osg.rpd.web.error.RpdErrorResponse;

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

		Session.getInstance().setUserName(nameField.getText().trim());
		Session.getInstance().setPassword(passwordField.getText().trim());
		// Enable admin window to be loaded so Devs can set IP address prior to logon
		if (Session.getInstance().getUserName().equals("dev") && Session.getInstance().getPassword().equals("@Devteam2")) {
			try {
				// close login page and load admin view
				Parent root = FXMLLoader.load(getClass().getResource("/FXML/AdminGui.fxml"));
				Stage adminJobStage = new Stage();
				adminJobStage.setResizable(true);
				adminJobStage.setTitle("RPD Reprints");
				adminJobStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/logo.jpg")));
				adminJobStage.setScene(new Scene(root));
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

			final LoginClient login = LoginClient.getInstance(NetworkConfig.getInstance());

			// Login performed on background thread to prevent GUI freezing
			new Thread(() -> {
				LOGGER.trace("Attempting to login...");
				// bypass login while testing
				Optional<String> token = Optional.empty();
				if (!Main.DEBUG_MODE) {
					token = login.getSessionToken(Session.getInstance().getUserName(), Session.getInstance().getPassword());
				}
				// if token wasn't retrieved & not in debug mode, display error dialog
				if (!token.isPresent() && !Main.DEBUG_MODE) {
				    Platform.runLater(() -> {
					    RpdErrorResponse errMsg = login.getErrorResponse();
						ErrorMsg(errMsg.getCode(), errMsg.getMessage(), errMsg.getAction());
						// cleanup fields
						lblMessage.setText("");
						passwordField.setText("");
						nameField.setDisable(false);
						passwordField.setDisable(false);
						passwordField.requestFocus();
					});
				} else {
					LOGGER.trace("Login Complete.");
					Session.getInstance().setToken(token.get());

					Platform.runLater(() -> {
					    Optional<Boolean> isAdmin = Optional.empty();	
					    CheckGroupClient client = null;
					    // bypass group check if running in debug mode
						if (!Main.DEBUG_MODE) {
							client = CheckGroupClient.getInstance(NetworkConfig.getInstance());
							isAdmin = client.IsUserAdmin();
						}
						if (isAdmin.isPresent()) {
						    Session.getInstance().setIsAdmin(isAdmin.get());
							// close login page and load main view
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SubmitJobGui.fxml"));
							Parent root;
                            try {
                                root = loader.load();
                                Stage submitJobStage = new Stage();
                                submitJobStage.setResizable(false);
                                // Display logged in user in titlebar
                                submitJobStage.setTitle("RPD Reprints - " + Session.getInstance().getUserName());
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
                            } catch (IOException ex) {
                                LOGGER.fatal(ExceptionUtils.getStackTrace(ex));
                                ErrorMsg("Form load error", "Unable to load main form");
                            }
						} else {
						    RpdErrorResponse errMsg = client.getErrorResponse();
						    LOGGER.error("{} {} {} \n{}", errMsg.getCode(), errMsg.getMessage(), errMsg.getAction(), errMsg.getException());
						    ErrorMsg(errMsg.getCode(), errMsg.getMessage(), errMsg.getAction());
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
