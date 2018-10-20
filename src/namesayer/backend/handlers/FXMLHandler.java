package namesayer.backend.handlers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class FXMLHandler {
	
	/**
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
		}
	}

}
