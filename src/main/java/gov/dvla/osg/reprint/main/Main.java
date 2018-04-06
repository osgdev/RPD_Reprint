package gov.dvla.osg.reprint.main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import gov.dvla.osg.reprint.models.Session;
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
 * VERSION       DATE         DEVELOPER         DESCRIPTION
 * 4.0.3   -  07/02/2018 - Pete Broomhall - Added Card Types dropdown menu to card tab
 * 4.0.2   -  18/01/2018 - Pete Broomhall - Application given its own credentials in RPD - Username = ReprintApp
 * 4.0.1   -  17/12/2017 - Pete Broomhall - Initial development
 * ********************************************************************************************************
 * 
 * @author Pete Broomhall
 * @version 4.0.3
 */
public class Main extends Application {
	
	private static final String APPLICATION_VERSION = "4.0.3";
	public static final boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/FXML/LoginGui.fxml"));
		primaryStage.setTitle("RPD Reprints " + APPLICATION_VERSION);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/logo.jpg")));
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}

	public static void main(String[] args) {
		//System.out.println("In debug : " + DEBUG_MODE);
		Session.props = new Properties();
		
		try {
			if (args.length != 1) {
				System.out.println("Incorrect number of args,\nUsage: {file}.jar {properties_filepath}");
				System.exit(1);
			} else {
				Session.propsFile = args[0];
				if (!(new File(Session.propsFile).exists())) {
					System.out.println("Props file '" + Session.propsFile + " doesn't exist!");
					System.exit(1);
				} else {
					setProperties();
				}
			}
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Decrypt properties file and store its data in a static props object
	 */
	private static void setProperties() {
		try {
			// decrypt file
			 byte[] fileContents = Files.readAllBytes(Paths.get(Session.propsFile));
			 byte[] decryptedBytes = Cryptifier.decrypt(fileContents);
			
			/**********************FOR TESTING ONLY******************************
			// encrypt file
			String path = "C:\\Users\\OSG\\Desktop\\rpdwsprops.copy.properties";
			byte[] fileContents = Files.readAllBytes(Paths.get(path));
			byte[] decryptedBytes = Cryptifier.encrypt(fileContents);
			Files.write(Paths.get(Session.propsFile), decryptedBytes);
			System.out.println("Done!");
			System.exit(0);
			/*********************************************************************/
			
			// load properties file
			InputStream reader = new ByteArrayInputStream(decryptedBytes);
			Session.props.load(reader);
		} catch (IOException ex) {
			System.out.println("Unable to load application properties.");
			System.exit(1);
		}
	}
}
