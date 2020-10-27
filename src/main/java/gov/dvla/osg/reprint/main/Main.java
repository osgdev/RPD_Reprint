package gov.dvla.osg.reprint.main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import gov.dvla.osg.reprint.models.Config;
import gov.dvla.osg.reprint.utils.Cryptifier;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Send details of wrecks and reprints batches to RPD.
 * 
 * ***************************** VERSION HISTORY **********************************************************
 * VERSION  DATE            DEVELOPER           DESCRIPTION 
 * 4.0.6 -  10/05/2018 -    Pete Broomhall -    Modified Rest API for RPD 3.8.3
 * 4.0.5 -  10/05/2018 -    Pete Broomhall -    Removed the App Credentials temporary fix for submitting files 
 * 4.0.4 -  23/04/2018 -    Pete Broomhall -    Added log4j and changed appCredentials to read from config file
 * 4.0.3 -  07/02/2018 -    Pete Broomhall -    Added Card Types dropdown menu to card tab 
 * 4.0.2 -  18/01/2018 -    Pete Broomhall -    Application given its own credentials in RPD - Username = ReprintApp 
 * 4.0.1 -  17/12/2017 -    Pete Broomhall -    Initial development
 * ********************************************************************************************************
 * 
 * @author Pete Broomhall
 * @version 4.0.6
 */
public class Main extends Application {

    static final Logger LOGGER = LogManager.getLogger();

    private static final String APPLICATION_VERSION = "4.0.6";
    public static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.trace("Loading GUI...");
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/LoginGui.fxml"));
        primaryStage.setTitle("RPD Reprints " + APPLICATION_VERSION + (DEBUG_MODE ? " DEBUG" : ""));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/logo.jpg")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // System.out.println("In debug : " + DEBUG_MODE);
        LOGGER.trace("Starting App...");
        try {
            if (args.length != 1) {
                LOGGER.fatal("Incorrect number of args, Usage: {jar} {properties_filepath}");
                System.exit(1);
            }

            Config.setPropsFile(args[0]);

            if (!(new File(Config.getPropsFile()).exists())) {
                LOGGER.fatal("Props file '" + Config.getPropsFile() + " doesn't exist!");
                System.exit(1);
            }

            setProperties();

            launch(args);
        } catch (Exception e) {
            LOGGER.fatal(ExceptionUtils.exceptionStackTraceAsString(e));
        }
    }

    /**
     * Decrypt properties file and store its data in a static props object
     */
    private static void setProperties() {
        try {
            // decrypt file
            byte[] fileContents = Files.readAllBytes(Paths.get(Config.getPropsFile()));
            byte[] decryptedBytes = Cryptifier.decrypt(fileContents);

            /**********************
             * FOR TESTING ONLY****************************** // encrypt file String path =
             * "C:\\Users\\OSG\\Desktop\\rpdwsprops.copy.properties"; byte[] fileContents =
             * Files.readAllBytes(Paths.get(path)); byte[] decryptedBytes =
             * Cryptifier.encrypt(fileContents); Files.write(Paths.get(Session.propsFile),
             * decryptedBytes); System.out.println("Done!"); System.exit(0); /
             *********************************************************************/

            // load properties file
            Config.setProps(new ByteArrayInputStream(decryptedBytes));
        } catch (IOException ex) {
            LOGGER.fatal("Unable to load application properties.");
            System.exit(1);
        }
    }
}
