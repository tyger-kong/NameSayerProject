package namesayer.backend.menus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import namesayer.backend.NameFile;
import namesayer.backend.handlers.AudioDeleteHandler;
import namesayer.backend.handlers.AudioPlayHandler;
import namesayer.backend.handlers.AudioProcessingHandler;
import namesayer.backend.handlers.AudioRecordingHandler;
import namesayer.backend.handlers.ButtonHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for PracticeMenu.fxml
 */
public class PracticeMenu implements Initializable {

	private static final String PROCESSING_MENU = "/namesayer/frontend/fxml/ProcessingMenu.fxml";
	private static final String MIC_TEST = "/namesayer/frontend/fxml/MicTest.fxml";

	@FXML
	private Button returnButton;
	@FXML
	private Button testMicBtn;
	@FXML
	private Button prevButton;
	@FXML
	private Button playButton;
	@FXML
	private Button nextButton;
	@FXML
	private Button playArcButton;
	@FXML
	private Button deleteArcButton;
	@FXML
	private Button recordButton;
	@FXML
	private ListView<String> availableListView;
	@FXML
	private ListView<String> displayListView;
	@FXML
	private ProgressBar recordingIndicator;
	@FXML
	private Label playingLabel;

	private List<String[]> namesToPractice;
	private List<NameFile> namesDatabase;
	private File creationsFile = new File("./Creations");
	private List<String> attemptDatabase;
	private List<String> listOfAttempts = new ArrayList<String>();

	private String selectedName;
	private int selectedIndex = 0;
	private ObservableList<String> listToDisplay;
	private ObservableList<String> recordedList;
	private String selectedArchive;
	private boolean contains;
	private String toPlay;
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-HHmmss");
	private Date date;
	private List<File> namesToPlay;
	private List<List<File>> listOfAudioCreated;

	private boolean btnIsRecord;
	private AudioRecordingHandler recorder = new AudioRecordingHandler();
	private int numberToPractice;
	private String recordingName;

	
	ButtonHandler btnHandler = new ButtonHandler();
	AudioPlayHandler audioPlayHandler = new AudioPlayHandler();
	AudioDeleteHandler audioDeleteHandler = new AudioDeleteHandler();
	AudioProcessingHandler audioProHandler = new AudioProcessingHandler();

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		recordingIndicator.setProgress(0);
		btnIsRecord = true;
		initialiseDatabases(); // Initialises the database lists
		listToDisplay = FXCollections.observableArrayList();
		getlistToDisplay();
		// Sets items to the listView
		displayListView.setItems(listToDisplay);
		displayListView.getSelectionModel().clearSelection();
		displayListView.getSelectionModel().selectFirst();
		selectedIndex = 0;
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		playingLabel.setText(selectedName);
		makeNewAudio(); // Make audio list for the first name
		newNameSelected(); // Method to update attempt list
	}


	/**
	 * Chooses a new name.
	 */
	public void handlePrevButton(ActionEvent actionEvent) {
		if (selectedIndex != 0) {
			selectedIndex--; // Changes current index
			updateLists();
		}
	}


	/**
	 * Same functionality as handlePrevButton()
	 */
	public void handleNextButton(ActionEvent actionEvent) {
		if (selectedIndex != (listToDisplay.size()-1)) {
			selectedIndex++;
			updateLists();
		 }
	}


	private void updateLists() {
		makeNewAudio(); // Creates new list of names to play
		displayListView.scrollTo(selectedIndex);
		displayListView.getSelectionModel().select(selectedIndex);
		selectedName = displayListView.getSelectionModel().getSelectedItem(); // Gets new selected name
		playingLabel.setText(selectedName);
		newNameSelected(); // Updates attemptList
	}


	/** 
	 * Plays the names in the list of the selectedName
	 */
	public void handlePlayButton(ActionEvent actionEvent) {
		audioPlayHandler.playAudio(this, listOfAudioCreated.get(selectedIndex));
	}


	/**
	 * Gets the selected recording
	 */
	public void handleArcListClicked(MouseEvent mouseEvent) {
		selectedArchive = availableListView.getSelectionModel().getSelectedItem();
	}


	/**
	 * Plays the selected recording 
	 */
	public void handlePlayArc(ActionEvent actionEvent) {
		if (selectedArchive == null) {
			noFileAlert();
		} else {
			audioPlayHandler.playSingleAudio(this, "Creations/"+selectedArchive+".wav");
		}
	}


	/**
	 * Deletes the selected recording
	 */
	public void handleDeleteArc(ActionEvent actionEvent) {
		if (selectedArchive == null) {
			noFileAlert();
		} else if (!contains) {
			availableListView.setMouseTransparent(true);
			availableListView.setFocusTraversable(false);
		} else {
			toPlay = selectedName;
			String fileToDelete = toPlay.substring(0, toPlay.lastIndexOf("_") + 1) + selectedArchive;
			String fileString = "Creations/" + fileToDelete + ".wav";
			File toDelete = new File(fileString);

			if (toDelete.exists()) {
				audioDeleteHandler.delete(toDelete);
				//Removes the recording from lists and updates
				listOfAttempts.remove(fileToDelete);
				updateArchive();
				availableListView.getSelectionModel().clearSelection();
				selectedArchive = null;
			}
		} 
		updateArchive();
	}

	/**
	 * Handles starting/stopping recording for user attempts of names
	 */
	public void handleRecordAction(ActionEvent actionEvent) {
		if (btnIsRecord) {
			// Start recording
			date = new Date();
			String currentTime = formatter.format(date);
			recordingName = listToDisplay.get(selectedIndex) + "_" + currentTime;

			setAllButtonsDisabled(true);
			recordButton.setDisable(false);
			recordingIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			startRecording(recordingName);
			recordButton.setText("STOP");
			btnIsRecord = false;

		} else {
			// Finish recording
			recordingIndicator.setProgress(0);
			recorder.finishRecording();
			listOfAttempts.add(recordingName);
			updateArchive();
			setAllButtonsDisabled(false);
			recordButton.setText("Record");
			btnIsRecord = true;
		}

	}


	private void startRecording(String recordingName) {
		File wavFile = new File("Creations/" + recordingName + ".wav");
		new Thread() {
			@Override
			public void run() {
				recorder.startRecording(wavFile);
			}
		}.start();
	}


	/**
	 * Launches a window to check the level of the user's microphone
	 */
	public void testMicBtnClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MIC_TEST));
			Parent root = fxmlLoader.load();
			Stage parentStage = (Stage) testMicBtn.getScene().getWindow();
			Stage stage = new Stage();
			stage.initOwner(parentStage);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setScene(new Scene(root));
			stage.setTitle("Mic Test");
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("ERROR OPENING MIC TEST WINDOW");
			alert.setHeaderText(null);
			alert.setContentText("ERROR OPENING MIC TEST WINDOW");
			alert.showAndWait();
		}
	}

	
	/**
	 * Creates a pop up alert indicating that no file has been selected
	 */
	public void noFileAlert() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("ERROR");
		alert.setHeaderText(null);
		alert.setContentText("No file selected");
		alert.showAndWait();
	}


	/**
	 * Fill the recording list with corresponding recordings
	 */
	public void newNameSelected() {
		initialiseAttemptDatabase();
		fillAttemptList();
		updateArchive();
		selectedArchive = null;
	}


	/**
	 * Gets the files in the creations folder as a list
	 */
	public void initialiseAttemptDatabase() {
		attemptDatabase = new ArrayList<String>(Arrays.asList(creationsFile.list()));
	}


	/**
	 * Finds corresponding names in creations folder and adds to recordingList
	 */
	public void fillAttemptList() {
		listOfAttempts = new ArrayList<>();
		for (String s : attemptDatabase) {
			if (s.lastIndexOf("_") != -1) {
				String nameMatch = s.substring(0, s.lastIndexOf("_"));
				if (selectedName.toLowerCase().equals(nameMatch.toLowerCase())) {
					String toAddToList = s.substring(0, s.lastIndexOf("."));
					if (!listOfAttempts.contains(toAddToList)) {
						listOfAttempts.add(toAddToList);
					}
				}
			}
		}
	}


	/**
	 * Update recording list
	 */
	public void updateArchive() {
		recordedList = FXCollections.observableArrayList(listOfAttempts);
		contains = !(recordedList.size() == 0);
		availableListView.setMouseTransparent(!contains);
		availableListView.setFocusTraversable(contains);
		availableListView.setItems(recordedList);
		availableListView.getSelectionModel().clearSelection();
	}


	/**
	 * Disable all buttons
	 * 
	 * @param b - True if all buttons should be disabled, false if they should be enabled
	 */
	public void setAllButtonsDisabled(boolean b) {
		btnHandler.setDisabled(b, playButton, prevButton, nextButton, recordButton, playArcButton, deleteArcButton, returnButton, testMicBtn);
		displayListView.setMouseTransparent(b);
		availableListView.setMouseTransparent(b);
	}


	/**
	 * Goes back to name selection menu
	 */
	public void returnToNameSelection() {
		NameSelectionMenu.clearHasNone();
		NameSelectionMenu ctrl = new NameSelectionMenu();
		returnButton.getScene().setRoot(ctrl.getControllerRoot());
	}


	/**
	 * Fills up the names to practice
	 */
	public void getlistToDisplay() {
		for (String[] s : namesToPractice) {
			String displayName = String.join("", s);
			listToDisplay.add(displayName);
		}
	}


	/**
	 * Creates a new list that has the file names of the names that needs to be concatenated
	 */
	public void makeNewAudio() {
		if (listOfAudioCreated.get(selectedIndex) == null) { // Checks if the selected name already has a list
			String tempName = "";
			String[] nameArray = namesToPractice.get(selectedIndex);
			namesToPlay = new ArrayList<>();
			for (String s : nameArray) { // Goes through every name in the String array
				for (NameFile namefile : namesDatabase) {
					if (namefile.toString().toLowerCase().equals(s.toLowerCase())) {
						namesToPlay.add(new File("names/" + namefile.getFileName()));
						tempName = tempName + " " + namefile.toString();
					}
				}
			}
			listOfAudioCreated.set(selectedIndex, namesToPlay);

			processRecordings();
		}
	}


	/**
	 * Gets new selected name from mouse click
	 */
	public void handleDisplayListClicked(MouseEvent mouseEvent) {
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		selectedIndex = listToDisplay.indexOf(selectedName);
		newNameSelected();
		makeNewAudio();
		playingLabel.setText(selectedName);
	}


	/**
	 * Initialises lists by getting them from other menus
	 */
	private void initialiseDatabases() {
		namesDatabase = MainMenu.getAddedNames();
		namesToPractice = NameSelectionMenu.getNamesObList();
		if (NameSelectionMenu.isShuffleSelected()) { // Checks if shuffle has been selected
			Collections.shuffle(namesToPractice);
		}
		numberToPractice = namesToPractice.size();
		listOfAudioCreated = new ArrayList<List<File>>(Collections.nCopies(numberToPractice, null));

		initialiseAttemptDatabase();
	}

	
	/**
	 * Trim silent parts of the audio files
	 */
	private void processRecordings() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(PROCESSING_MENU));
			audioProHandler.process(root, namesToPlay);
		} catch (IOException e) {

		}
	}

}
