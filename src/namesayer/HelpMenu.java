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

	
	// Loads the webview with html file 
	@FXML
	private void initialize() {
		WebEngine engine = instructionsWebView.getEngine();
		File file = new File("src/instructions.html");
		engine.load(file.toURI().toString());
	}

	
	// Goes back to main menu
	public void mainMenuBtnClicked(ActionEvent actionEvent) {
		mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
	}

}
