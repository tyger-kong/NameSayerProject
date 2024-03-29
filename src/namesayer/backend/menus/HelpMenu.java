package namesayer.backend.menus;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Controller for HelpMenu.fxml
 */
public class HelpMenu implements Initializable {

	@FXML
	private WebView instructionsWebView;
	@FXML
	private Button mainMenuBtn;

	private final static String INSTRUCTIONS = "/namesayer/resources/instructions.html";


	/**
	 * Loads the WebView with the instructions.html file
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		WebEngine engine = instructionsWebView.getEngine();
		try {
			String s = getClass().getResource(INSTRUCTIONS).toURI().toString();
			engine.load(s);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Goes back to main menu
	 */
	public void mainMenuBtnClicked(ActionEvent actionEvent) {
		mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
	}

}
