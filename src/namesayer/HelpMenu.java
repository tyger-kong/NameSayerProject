package namesayer;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class HelpMenu {
	@FXML
	private WebView instructionsWebView;
	@FXML
	private Button mainMenuBtn;


//	// Loads the webview with html file
//	@FXML
//	private void initialize() {
//		WebEngine engine = instructionsWebView.getEngine();
//		String fileloc = "/src/instructions.html";
//		String fullLink = getClass().getResource(fileloc).toExternalForm();
//		engine.load(fullLink);
//	}

	
	// Goes back to main menu
	public void mainMenuBtnClicked(ActionEvent actionEvent) {
		mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
	}

}
