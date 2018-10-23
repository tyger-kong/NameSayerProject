package namesayer.backend.handlers;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

/**
 * Class for handling loading of FXML files for the different scenes of the application
 */
public class FXMLHandler {
	
	/**
	 * Changes the scene to the specified FXML document
	 * 
	 * @param fxmlFile - Path to fxml file to load.
	 * @param btn - Button that was pressed to load the specified file.
	 */
	public void load(String fxmlFile, Button btn) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
			Parent root = fxmlLoader.load();
			btn.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FAILED TO OPEN THIS MENU");
		}
	}

}
