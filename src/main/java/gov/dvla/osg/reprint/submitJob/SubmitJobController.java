package gov.dvla.osg.reprint.submitJob;

import static gov.dvla.osg.reprint.models.Session.props;
import static gov.dvla.osg.reprint.utils.ErrorHandler.ErrorMsg;

import java.io.IOException;
import java.util.*;

import javax.ws.rs.ProcessingException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import gov.dvla.osg.reprint.login.LogOut;
import gov.dvla.osg.reprint.models.*;
import gov.dvla.osg.reprint.report.Report;
import gov.dvla.osg.reprint.utils.ErrorHandler;
import gov.dvla.osg.reprint.utils.FileUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for the main screen.
 *
 */
public class SubmitJobController {

	// Objects on the General tab
	public Tab tabGeneral;
	public TextField txtRange;
	public TextField txtSingle;
	public ListView jList;
	public Label txtError;
	// Objects on the Cards tab
	public Tab tabCards;
	public ChoiceBox chboxApp;
	public ChoiceBox chboxCardType;
	public ChoiceBox chboxSite;
	public Menu menuAdmin;
	public Label cardError;
	public TextField txtRunNo;
	// Objects on the Hal tab
	public Tab tabHal;
	public TextField txtWorkflowId;
	public ChoiceBox chHalSite;
	public Label halError;
	// Buttons
	public Button btnSubmit;
	public Button btnLogout;
	// Private members
	private ObservableList<AbstractReprintType> model = FXCollections.observableArrayList();
	private ArrayList<String> report = new ArrayList<String>();
	private String mode;
	private FileHandler fileHandler = new FileHandler();

	/**
	 * Initialize list items when form loads.
	 */
	@FXML
	public void initialize() {
		// show admin button only for developers
		if (Session.isAdmin != null && Session.isAdmin) {
			menuAdmin.setVisible(true);
		}
		// set up the General tab
		txtRange.setCursor(Cursor.DEFAULT);
		mode = "single";
		
		// load dropdown lists in Cards tab
		chboxApp.setItems(FXCollections.observableArrayList(Arrays.asList(props.getProperty("appTypes").split(","))));
		chboxCardType.setItems(FXCollections.observableArrayList(Arrays.asList(props.getProperty("cardTypes").split(","))));
		chboxSite.setItems(FXCollections.observableArrayList(Arrays.asList(props.getProperty("sites").split(","))));
		jList.setItems(model);
		
		// load dropdown list in HAL tab
		chHalSite.setItems(FXCollections.observableArrayList(Arrays.asList(props.getProperty("sites").split(","))));

		// Set focus for form
		Platform.runLater(() -> txtSingle.requestFocus());
	}

	/**
	 * Show second textfield when single mode is selected.
	 */
	public void rbRangeSelected() {

		txtRange.setEditable(true);
		txtRange.setFocusTraversable(true);
		txtRange.setId("");
		txtRange.setCursor(Cursor.TEXT);
		txtError.setText("");
		mode = "range";
		txtSingle.requestFocus();
	}

	/**
	 * Hide second textfield when single mode is selected.
	 */
	public void rbSingleSelected() {
		txtRange.setEditable(false);
		txtRange.setFocusTraversable(false);
		txtRange.setId("hidden");
		txtRange.setText("");
		txtRange.setCursor(Cursor.DEFAULT);
		txtError.setText("");
		mode = "single";
		txtSingle.requestFocus();
	}

	/**
	 * Validate and process input when Single option is selected. Input can either
	 * be a single document (15 chars) or a whole batch (10 chars) Valid input is
	 * added to the list box.
	 */
	public void processSingleEnterAction() {

		String input = txtSingle.getText().trim();

		if (isInputValid(input)) {
			AbstractReprintType reprint;
			if (mode.equalsIgnoreCase("single")) {
				if (isWholeBatchReprint(input)) {
					reprint = new WholeBatchReprint(input);
					// add WholeBatchReprint to pdf report
					report.add(reprint.toString() + " (Batch)");
				} else {
					reprint = new SingleReprint(input);
					// add SingleReprint to pdf report
					report.add(reprint.toString() + " (Single)");
				}
				// add reprint to listbox
				model.add(reprint);
				txtSingle.setText("");
				txtSingle.requestFocus();
			} else {
				txtRange.requestFocus();
			}
			setGeneralSuccess("");
		} else {
			txtSingle.requestFocus();
		}
	}

	/**
	 * Validate and process input when Range option is selected. Valid input is
	 * added to the list box.
	 */
	public void processRangeEnterAction() {

		String input_start = txtSingle.getText().trim();
		String input_end = txtRange.getText().trim();

		if (!Strings.isNullOrEmpty(input_start) && isInputValid(input_start) && isInputValid(input_end)
				&& isValidRange(input_start, input_end)) {
			AbstractReprintType reprint = new RangeReprint(input_start, input_end);
			// add range to the listbox (uses toString() to diplay)
			model.add(reprint);
			// add range to the pdf report
			report.add(reprint.toString() + " (Range)");
			// cleanup fields
			txtSingle.setText("");
			txtRange.setText("");
			txtSingle.requestFocus();
			setGeneralSuccess("");
		} else {
			txtSingle.requestFocus();
		}
	}

	/**
	 * Process click event for admin button.
	 */
	public void openAdmin() {

		if (Session.isAdmin != null && Session.isAdmin) {
			try {
				// credentials accepted - load admin page
				Parent root = FXMLLoader.load(getClass().getResource("/FXML/AdminGui.fxml"));
				Stage adminStage = new Stage();
				adminStage.setTitle("Admin Section");
				adminStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/logo.jpg")));
				adminStage.setResizable(false);
				adminStage.setScene(new Scene(root, 650, 600));
				adminStage.show();
			} catch (Exception e) {
				ErrorMsg(e.getClass().getSimpleName(), e.getMessage());
			}
		}
	}

	/**
	 * Files older than 7 days are deleted from the temp dir & then the user is
	 * logged out and application shut down.
	 */
	public void logout() {
		String msg = "Logging out...";
		disableButtons();
		setGeneralSuccess(msg);
		setCardSuccess(msg);
		setHalSuccess(msg);
		// contact RPD on background thread to prevent main window from freezing
		new Thread(() -> {
			Platform.runLater(() -> {
				String workingDir = props.getProperty("reprintWorkingDir");
				int daysBack = 14;
				FileUtils.deleteFilesOlderThanNdays(workingDir, daysBack);
				LogOut.logout();
				enableButtons();
				setGeneralSuccess("");
				setCardSuccess("");
				setHalSuccess("");
			});
		}).start();
	}

	/**
	 * Calls the relevant method when the submit button is pressed.
	 */
	public void actionPerformed() {
		if (tabGeneral.isSelected()) {
			generalAction();
		} else if (tabCards.isSelected()) {
			cardAction();
		} else if (tabHal.isSelected()) {
			halAction();
		}
	}

	/**
	 * We need to use runLater to tell the java to requestFocus after the tab is
	 * selected, else the tab itself retains focus not the control on the form.
	 */
	public void tabChanged() {
		if (tabGeneral.isSelected()) {
			Platform.runLater(() -> txtSingle.requestFocus());
		} else if (tabCards.isSelected()) {
			Platform.runLater(() -> chboxApp.requestFocus());
		} else if (tabHal.isSelected()) {
			Platform.runLater(() -> txtWorkflowId.requestFocus());
		}
	}

	/**
	 * Create dat and eot files for the general tab
	 */
	public void generalAction() {

		if (model.size() == 0) {
			setGeneralError("Nothing input! Please try again.");
		} else {
			disableButtons();
			setGeneralSuccess("Sending reprint file...");

			new Thread(() -> {
				try {
					int noOfRecords = 0;
					String datFileContent = "";
					// sort the list in jobID order
					Collections.sort(model);
					Collections.sort(report);
					// write list items to dat file
					for (AbstractReprintType reprint : model) {
						datFileContent += reprint.output() + "\n";
						noOfRecords += reprint.getNoOfRecords();
					}
					// write data to files and send
					fileHandler.setFileNames(props.getProperty("FileNamePrefixGeneral"));
					fileHandler.setDatFileContent(datFileContent);
					fileHandler.setEotFileContent("RUNVOL=" + noOfRecords + "\nUSER=" + Session.userName);
					fileHandler.submit();
					// write pdf report to disk
					Report.writePDFreport(report);

					Platform.runLater(() -> {
						// cleanup fields
						setGeneralSuccess("Reprint file sent!");
						model.clear();
						report.clear();
						txtSingle.setText("");
						txtRange.setText("");
						txtSingle.requestFocus();
					});
				} catch (ProcessingException e) {
					Platform.runLater(() -> {
						setGeneralError("Unable to transmit data file.\nContact Dev Team if problem persists.");
					});
				} catch (IOException e) {
					Platform.runLater(() -> {
						setGeneralError("Unable to create data file.\nContact Dev Team if problem persists.");
					});
				} catch (Exception e) {
					Platform.runLater(() -> {
						setGeneralError(e.getMessage());
					});
				} finally {
					enableButtons();
				}
			}).start();
		}
	}

	/**
	 * Create dat and eot files for the card tab
	 */
	public void cardAction() {

		String runNo = txtRunNo.getText().trim();

		if (chboxApp.getSelectionModel().isEmpty()) {
			setCardError("No app selected");
			chboxApp.requestFocus();
		} else if (chboxCardType.getSelectionModel().isEmpty()) {
			setCardError("No card type selected");
			chboxCardType.requestFocus();
		} else if (chboxSite.getSelectionModel().isEmpty()) {
			setCardError("No site selected");
			chboxSite.requestFocus();
		} else if (StringUtils.isBlank(runNo)) {
			setCardError("A run number must be provided.");
		} else if (!StringUtils.isNumeric(runNo)) {
			setCardError("'" + runNo + "' contains letters.");
		} else if (runNo.length() > 5) {
			setCardError("'" + runNo + "' is too long. Max length is 5 digits.");
		} else {
			String selectedApp = chboxApp.getSelectionModel().getSelectedItem().toString();
			String selectedCardType = chboxCardType.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("ALL")
					? "" : chboxCardType.getSelectionModel().getSelectedItem().toString();
			String selectedSite = chboxSite.getSelectionModel().getSelectedItem().toString();

			setCardSuccess("Sending card reprint file...");
			disableButtons();
			new Thread(() -> {
				try {
					// write data to files and send
					fileHandler.setFileNames(props.getProperty("FileNamePrefixCards"));
					fileHandler.setDatFileContent("");
					// write data to eot file
					fileHandler.setEotFileContent("APP=" + selectedApp + "\nCARDTYPE=" + selectedCardType 
							+ "\nLOCATION=" + selectedSite + "\nUSER="
							+ Session.userName + "\nRUNNO=" + runNo);
					fileHandler.submit();

					Platform.runLater(() -> {
						// cleanup fields
						setCardSuccess("Card file sent!");
						chboxApp.getSelectionModel().clearSelection();
						chboxSite.getSelectionModel().clearSelection();
						chboxApp.requestFocus();
					});
				} catch (ProcessingException e) {
					Platform.runLater(() -> {
						setCardError("Unable to transmit reprint file.\nContact Dev Team if problem persists.");
					});
				} catch (IOException e) {
					Platform.runLater(() -> {
						setCardError("Unable to create reprint file.\nContact Dev Team if problem persists.");
					});
				} catch (Exception e) {
					Platform.runLater(() -> {
						setCardError(e.getMessage());
					});
				} finally {
					enableButtons();
				}
			}).start();
		}
	}

	/**
	 * Create dat and eot files for the hal tab
	 */
	private void halAction() {
		// set mode for validation to work correctly
		mode = "single";
		String workflowId = txtWorkflowId.getText().trim();
		String selectedSite = chHalSite.getSelectionModel().getSelectedItem().toString();

		// check text input is valid 10 digits
		if (!isInputValid(workflowId) && !isWholeBatchReprint(workflowId)) {
			setHalError("WorkFlow ID is invalid.");
			txtWorkflowId.requestFocus();
			// check site has been selected
		} else if (chHalSite.getSelectionModel().isEmpty()) {
			setHalError("No site selected");
			chHalSite.requestFocus();
		} else {
			// input data is OK so process
			setHalSuccess("Sending Hal reprint file...");
			disableButtons();

			new Thread(() -> {
				try {
					// write data to files and send
					fileHandler.setFileNames(props.getProperty("FileNamePrefixHal"));
					fileHandler.setDatFileContent("");
					fileHandler.setEotFileContent(
							"WID=" + workflowId + "\nLOCATION=" + selectedSite + "\nUSER=" + Session.userName);
					fileHandler.submit();

					Platform.runLater(() -> {
						// cleanup fields
						setHalSuccess("Hal files sent!");
						chboxApp.getSelectionModel().clearSelection();
						chboxSite.getSelectionModel().clearSelection();
						chboxApp.requestFocus();
					});

				} catch (ProcessingException e) {
					Platform.runLater(() -> {
						setHalError("Unable to transmit files.\nContact Dev Team if problem persists.");
					});
				} catch (IOException e) {
					Platform.runLater(() -> {
						setHalError("Unable to create files.\nContact Dev Team if problem persists.");
					});
				} catch (Exception e) {
					Platform.runLater(() -> {
						setHalError(e.getMessage());
					});
				} finally {
					enableButtons();
				}
			}).start();
		}
	}

	/**
	 * Check values are for the same job.
	 * 
	 * @param in1 input from first text field
	 * @param in2 input from second text field
	 * @return true if inputs represent a valid range
	 */
	private boolean isValidRange(String in1, String in2) {

		int first = Integer.parseInt(in1.substring(10, 15));
		int second = Integer.parseInt(in2.substring(10, 15));

		if (first > second) {
			setGeneralError("'" + first + "' is bigger than '" + second + "'");
			return false;
		}
		if (!(in1.substring(0, 10).equals(in2.substring(0, 10)))) {
			setGeneralError("Job IDs don't match! " + in1.substring(0, 10) + " - " + in2.substring(0, 10));
			return false;
		}
		return true;
	}

	/**
	 * Validates length of textfields according to the mode set by the radio button.
	 * Single fields can be 10 digits (whole batch) or 15 digits. Range fields must
	 * be 15 digits.
	 * 
	 * @param input
	 * @return true if input is correct length and numbers only
	 */
	private boolean isInputValid(String input) {

		// validate only first 15 digits of input
		if (input.length() > 15) {
			input = input.substring(0, 15);
		}

		// reject any input with letters
		if (!StringUtils.isNumeric(input)) {
			setGeneralError("'" + input + "' contains letters");
			return false;
		}

		// singles can be 10 or 15+ digits, range must both be 15+
		if (mode.equalsIgnoreCase("single")) {
			if (!((input.length() == 10) || (input.length() == 15))) {
				setGeneralError("'" + input + "' is incorrect length: " + input.length());
				return false;
			}
		} else if (input.length() < 15) {
			setGeneralError("'" + input + "' is incorrect length: " + input.length());
			return false;
		}

		setGeneralSuccess("");
		return true;
	}

	/**
	 * Whole batch reprints are 10 digits long.
	 * 
	 * @param textField
	 * @return true if input is correct length
	 */
	private boolean isWholeBatchReprint(String textField) {
		return textField.length() == 10;
	}

	/**
	 * Remove item from listbox and reprint list when left mouse button is clicked
	 * 
	 * @param e mouse click
	 */
	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseButton.SECONDARY) {
			String selectedItem = jList.getSelectionModel().getSelectedItem().toString();
			// create dialog to confirm logout
			Dialog<ButtonType> dialog = new Dialog<>();
			dialog.setTitle("Delete Selected Item");
			// add logo to dialog window frame
			((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons()
					.add(new Image(ErrorHandler.class.getResource("/Images/logo.jpg").toString()));
			DialogPane dialogPane = dialog.getDialogPane();
			dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
			dialogPane.setContentText("Do you want to remove " + selectedItem + "?");
			// display dialog and wait for a button to be clicked
			Optional<ButtonType> result = dialog.showAndWait();
			// logout if user clicks the OK button
			if (result.isPresent() && result.get() == ButtonType.YES) {
				int index = jList.getSelectionModel().getSelectedIndex();
				if (index != -1) {
					model.remove(index);
					report.remove(index);
				}
			}
		}
	}

	// methods for displaying messages, IDs are css attributes to change text colour
	private void setGeneralError(String msg) {
		txtError.setText(msg);
		txtError.setId("error");
	}

	private void setGeneralSuccess(String msg) {
		txtError.setText(msg);
		txtError.setId("success");
	}

	private void setCardError(String msg) {
		cardError.setText(msg);
		cardError.setId("error");
	}

	private void setCardSuccess(String msg) {
		cardError.setText(msg);
		cardError.setId("success");
	}

	private void setHalError(String msg) {
		halError.setText(msg);
		halError.setId("error");
	}

	private void setHalSuccess(String msg) {
		halError.setText(msg);
		halError.setId("success");
	}

	private void enableButtons() {
		btnSubmit.setDisable(false);
		btnLogout.setDisable(false);
	}

	private void disableButtons() {
		btnSubmit.setDisable(true);
		btnLogout.setDisable(true);
	}
}
