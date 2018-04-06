package gov.dvla.osg.reprint.admin;

import static gov.dvla.osg.reprint.models.Session.props;
import static gov.dvla.osg.reprint.models.Session.propsFile;
import static gov.dvla.osg.reprint.utils.ErrorHandler.ErrorMsg;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;

import gov.dvla.osg.reprint.utils.Cryptifier;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


/**
 * Property key textfields are kept locked as these are hard-coded into the application.
 * Values are locked to begin with to prevent accidental changes but are unlocked
 * using the amend button. This button changes to 'save' when editing is enabled.
 * Changes are saved to the props object in the main class and are also written
 * to a file in an encoded form using the cryptography class in the Utils package.
 */
public class adminGuiController {

    public VBox vboxValues;
    public VBox vboxKeys;
    public Button button;
    ObservableList<Node> nodes;
    Iterator<Node> tfKeys;
    Iterator<Node> tfValues;
    Enumeration em;

    /**
     * Loops through all text fields in vboxValues to either enable or disable editing
     * of these fields. CSS styling is applied to locked fields to disable the blue highlighting
     * that javafx gives to selected fields.
     */
    public void enableEditing() {

        // Lock and unlock text fields to enable/disable editing of property values
        nodes = vboxValues.getChildren();

        for (Node node : nodes) {
            if (node.getId().equalsIgnoreCase("locked")) {
                // Clears the CSS styling
                node.setId("");
                // Cast node as TextField to change properties
                ((TextField) node).setEditable(true);
                node.setFocusTraversable(true);
            } else {
                node.setId("locked");
                ((TextField) node).setEditable(false);
                node.setFocusTraversable(false);
            }
        }
        
        // Swap labels on the button & process save action
        if (button.getText().equalsIgnoreCase("AMEND")) {
            button.setText("Save");
        } else {
            saveProperties();
            button.setText("AMEND");
        }
    }

    /**
     * Updates keys and values in properties object then 
     * encrypts the properties and writes them to disk.
     */
    private void saveProperties() {
        setIterators();
    
        // Update properties object
        while (tfKeys.hasNext() && tfValues.hasNext()) {
            String propKey = ((TextField) tfKeys.next()).getText();
            String propValue = ((TextField) tfValues.next()).getText();
            props.setProperty(propKey, propValue);
        }

        // Save propeties object back to disk
        try {
            // Get props as byte array
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            props.store(output, null);
            // encrypt props byte array
            byte[] encryptedBytes = Cryptifier.encrypt(output.toByteArray());
            // save to file
            Files.write(Paths.get(propsFile), encryptedBytes);
        } catch (Exception ex) {
            ErrorMsg(ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    /**
     * Inserts properties into text fields when the form is loaded.
     * Enumerator hasnext() is used to loop through three lists simultaneously.
     */
    @FXML
    public void initialize() {
        setIterators();

        // Add property keys and values to text fields
        while (tfKeys.hasNext() && tfValues.hasNext() && em.hasMoreElements()) {
            // Get key value pairs from props
            String propKey = (String) em.nextElement();
            String propValue = props.getProperty(propKey);
            // Put property keys into vboxKeys
            ((TextField) tfKeys.next()).setText(propKey);
            // Put property vaues into vboxValues
            ((TextField) tfValues.next()).setText(propValue);
        }
    }

    /**
     * Iterators are applied to collections in order for them
     * to be processed in parallel within 'while' loops.
     */
    private void setIterators() {
        // Get Text Fields to insert property keys
        tfKeys = vboxKeys.getChildren().iterator();
        // Get Text Fields to insert property values
        tfValues = vboxValues.getChildren().iterator();
        // Get the individual key/value pairs within the property file
        em = props.keys();
    }
}
