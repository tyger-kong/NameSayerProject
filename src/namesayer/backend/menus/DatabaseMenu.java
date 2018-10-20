package namesayer.backend.menus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import namesayer.backend.NameFile;
import namesayer.backend.NameFile.rating;
import namesayer.backend.handlers.AudioPlayHandler;
import namesayer.backend.handlers.ButtonHandler;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseMenu implements Initializable {
	@FXML
	private Button mainMenuBtn;
	@FXML
	private Button playBtn;
	@FXML
	private Button rateBtn;
	@FXML
	private ListView<String> databaseListView;
	private ObservableList<String> namesList;
	private List<NameFile> nameDatabase;

	private String selectedName;
	private NameFile currentName;
	private AudioPlayHandler audioPlayHandler = new AudioPlayHandler();
	private ButtonHandler btnHandler = new ButtonHandler();


	public void mainMenuBtnClicked(ActionEvent actionEvent) {
		mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
	}


	/**
	 * Initialises the listview
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Get the list of name objects
		nameDatabase = MainMenu.getAddedNames();
		namesList = FXCollections.observableArrayList(MainMenu.getAddedList());
		databaseListView.setItems(namesList);
		databaseListView.getSelectionModel().clearSelection();
		rateBtn.setDisable(true);
	}


	/**
	 * Gets the selected item in the List view as a string and gets the corresponding name object
	 */
	public void handleListClicked(MouseEvent mouseEvent) {
		selectedName = databaseListView.getSelectionModel().getSelectedItem();
		getCurrentName();
		rateBtn.setDisable(false);
		setBadRating(currentName.checkIfBadRating());
	}


	/**
	 * Asks user if they want to change the rating of a name in the database
	 */
	public void rateBtnClicked(ActionEvent actionEvent) {
		Alert rateConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Change " + selectedName + "'s rating?", ButtonType.YES, ButtonType.NO);
		rateConfirm.showAndWait();

		if (rateConfirm.getResult() == ButtonType.YES) {
			// If file already has a bad rating, change it to good, and vice versa.
			setBadRating(!currentName.checkIfBadRating());
		}
	}


	/**
	 * Changes the rating of the currently selected name
	 */
	private void setBadRating(boolean badRating) {
		currentName.setBadRating(badRating);
		NameFile.rating rating = (badRating) ? NameFile.rating.BAD : NameFile.rating.GOOD;
		btnHandler.setRatingButton(rateBtn, rating);
	}


	/**
	 * Gets the name object based on selected name in the ListView
	 */
	public void getCurrentName() {
		// Search through whole names database for the corresponding name
		for (NameFile n : nameDatabase) { 
			if (n.toString().equals(selectedName)) {
				currentName = n;
			}
		}
	}


	/**
	 * Plays the selected audio
	 * 
	 * @param fileToPlay - Name of file to play.
	 */
	public void playBtnClicked(ActionEvent actionEvent) {
		audioPlayHandler.play("names/"+currentName.getFileName(), playBtn);
	}

}
