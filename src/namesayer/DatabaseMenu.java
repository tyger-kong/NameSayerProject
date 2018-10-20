package namesayer;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseMenu implements Initializable {
	@FXML
	private Button mainMenuBtn;
	@FXML
	private Button rateButton;
	@FXML
	private ListView<String> databaseListView;
	@FXML
	private Button playBtn;

	private ObservableList<String> namesList;
	private List<NameFile> nameDatabase;

	private String selectedName;
	private NameFile currentName;
	private String toPlay;
	private AudioPlayHandler audioPlayHandler = new AudioPlayHandler();


	public void mainMenuBtnClicked(ActionEvent actionEvent) {
		mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
	}


	/**
	 * Initialises the listview
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameDatabase = MainMenu.getAddedNames(); // Gets the list of name objects
		namesList = FXCollections.observableArrayList(MainMenu.getAddedList());
		databaseListView.setItems(namesList);
		databaseListView.getSelectionModel().clearSelection();
		rateButton.setDisable(true);
	}


	/**
	 * Gets the selected item in the List view as a string and gets the corresponding name object
	 */
	public void handleListClicked(MouseEvent mouseEvent) {
		selectedName = databaseListView.getSelectionModel().getSelectedItem();
		getCurrentName();
		rateButton.setDisable(false);
		setRatingButton();
	}


	/**
	 * Calls the methods inside the name objects to change the rating
	 */
	public void handleRateAction(ActionEvent actionEvent) {
		Alert rateConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Change " + selectedName + "'s rating?", ButtonType.YES, ButtonType.NO);
		rateConfirm.showAndWait();

		if (rateConfirm.getResult() == ButtonType.YES) {
			toPlay = currentName.getFileName();

			if(!currentName.checkIfBadRating()) {
				currentName.setBadRating(true);
			} else {
				currentName.setBadRating(false);
			}
			setRatingButton(); 
		}
	}


	/**
	 * Changes the colour and text of the rating button accordingly
	 */
	private void setRatingButton() {
		if (currentName.checkIfBadRating()) {
			rateButton.setText("Rate Good");
			rateButton.setStyle("-fx-background-color: red;");
		} else {
			rateButton.setText("Rate Bad");
			rateButton.setStyle("-fx-background-color: green;");
		}
	}


	/**
	 * Gets the name object based on selected name in listview
	 */
	public void getCurrentName() {
		for (NameFile n : nameDatabase) { // Searches through whole names database for the corresponding name
			if (n.toString().equals(selectedName)) {
				currentName = n;
			}
		}

	}


	/**
	 * Plays the audio
	 * @param fileToPlay
	 */
	private void playAudio(String fileToPlay) {
		audioPlayHandler.play(fileToPlay, playBtn);
	}


	public void playBtnClicked(ActionEvent actionEvent) {
		toPlay = currentName.getFileName();
		playAudio("names/" + toPlay);
	}

}
