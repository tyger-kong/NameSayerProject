package namesayer;


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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class PracticeMenu implements Initializable {

	private String selectedName;

	private int selectedIndex = 0;
	private Random r = new Random();


	private ObservableList<String> listToDisplay;

	private ObservableList<String> recordedList;

	private String selectedArchive;

	private boolean contains;

	private String toPlay;

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

	private File creations = new File("./Creations");


	private List<String> attemptDatabase;
	private List<String> listOfAttempts = new ArrayList<String>();
	private List<String> listOfJustNames;


	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-HHmmss");
	private Date date;
	private List<File> namesToPlay;
	private List<List> listOfAudioCreated;

	private boolean btnIsRecord;

	private JavaSoundRecorder recorder = new JavaSoundRecorder();

	private int numberToPractice;
	private int numberOfRecordings;



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnIsRecord = true;
		initialiseDatabases(); // Initialises the database lists
		listToDisplay = FXCollections.observableArrayList();
		getlistToDisplay();
		if(NameSelectionMenu.isShuffleSelected()){ // Checks if shuffle has been selected
			Collections.shuffle(listToDisplay);
		}
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

	// Chooses a new name. 
	public void handlePrevButton(ActionEvent actionEvent) {
		if (selectedIndex == 0) {
		} else {
			selectedIndex--; // Changes current index
			makeNewAudio(); // Creates new list of names to play
			displayListView.scrollTo(selectedIndex);
			displayListView.getSelectionModel().select(selectedIndex);
			selectedName = displayListView.getSelectionModel().getSelectedItem(); // Gets new selected name
			playingLabel.setText(selectedName);
			newNameSelected(); // Updates attemptlist
		}
	}
	// Same functionality as handlePrevButton()
	public void handleNextButton(ActionEvent actionEvent) {
		if (selectedIndex == listToDisplay.size() - 1) {
		} else {
			selectedIndex++;
			makeNewAudio();
			displayListView.scrollTo(selectedIndex);
			displayListView.getSelectionModel().select(selectedIndex);

			selectedName = displayListView.getSelectionModel().getSelectedItem();

			playingLabel.setText(selectedName);
			newNameSelected();
		}
	}

	// Plays the names in the list of the selectedName
	public void handlePlayButton(ActionEvent actionEvent) {
		playAudio(listOfAudioCreated.get(selectedIndex));
	}

	// Gets the selected recording
	public void handleArcListClicked(MouseEvent mouseEvent) {
		selectedArchive = availableListView.getSelectionModel().getSelectedItem();
	}

	// Plays the selected recording
	public void handlePlayArc(ActionEvent actionEvent) {
		if (selectedArchive == null) {
			noFileAlert();
		} else {
			toPlay = selectedName;
			String fileToPlay = toPlay.substring(0, toPlay.lastIndexOf("_")+1) + selectedArchive;
			//playAudio("Creations/" + fileToPlay + ".wav");
		}

	}

	// Plays the files in the given list one at a time
	private void playAudio(List<File> toPlay) {
		new Thread() {
			@Override
			public void run() {
				try {
					byte[] buffer = new byte[4096];
					setAllButtonsDisabled(true);
					for (File file : toPlay) {
						try {
							AudioInputStream is = AudioSystem.getAudioInputStream(file);
							AudioFormat format = is.getFormat();
							SourceDataLine line = AudioSystem.getSourceDataLine(format);
							line.open(format);
							line.start();
							while (is.available() > 0) {
								int len = is.read(buffer);
								line.write(buffer, 0, len);
							}
							line.drain(); //**[DEIT]** wait for the buffer to empty before closing the line
							line.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					setAllButtonsDisabled(false);

				} catch (Exception e) {

				}
			}
		}.start();
	}

	// Deletes the recording
	public void handleDeleteArc(ActionEvent actionEvent) {
		if (selectedArchive == null) {
			noFileAlert();
		} else {
			toPlay = selectedName;
			String fileToDelete = toPlay.substring(0, toPlay.lastIndexOf("_")+1) + selectedArchive;
			String fileString = "Creations/" + fileToDelete + ".wav";
			File toDelete = new File(fileString);
			if (toDelete.exists()) {
				Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete:" + selectedArchive + "?", ButtonType.YES, ButtonType.NO);
				deleteConfirm.showAndWait();
				if (deleteConfirm.getResult() == ButtonType.YES) {

					try {
						Files.deleteIfExists(toDelete.toPath());
					} catch (IOException e) {
						System.out.println("FAILED TO DELETE");
						e.printStackTrace();
					}

					listOfAttempts.remove(fileToDelete);
					updateArchive();
					availableListView.getSelectionModel().clearSelection();
					selectedArchive = null;
				}
			} else {
				if (!contains) {
					availableListView.setMouseTransparent(true);
					availableListView.setFocusTraversable(false);
				}
			}
		}
		updateArchive();
	}


	public void handleRecordAction(ActionEvent actionEvent) {
		date = new Date();
		String currentTime = formatter.format(date);
		String recordingName = selectedName + "_" + currentTime;
		setAllButtonsDisabled(true);
		recordButton.setDisable(false);
		if (btnIsRecord) {

			System.out.println("JUST ABOUT TO RECORD");
			startRecording(recordingName);
			System.out.println("JUST STARTED RECORDING");
			recordButton.setText("STOP");
			btnIsRecord = false;

		} else {
			System.out.println("RYAN LIM IS COOL");
			finishRecording();
			listOfAttempts.add(recordingName);
			updateArchive();
			setAllButtonsDisabled(false);
			recordButton.setText("Record");
			btnIsRecord = true;
		}
	}


	private void startRecording(String recordingName) {
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		date = new Date();
		String currentTime = formatter.format(date);
		String fileName = "Creations/" + selectedName + "_" + currentTime + ".wav";

		File wavFile = new File(fileName);
		System.out.println("ABOUT TO do recorder.startRecording(wavFile)");
		new Thread() {
			@Override
			public void run() {
				recorder.startRecording(wavFile);
			}
		}.start();

	}


	private void finishRecording() {
		recorder.finishRecording();
	}

// Load test mic window
	public void testMicBtnClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MicTest.fxml"));
			Parent root = fxmlLoader.load();
			Stage parentStage = (Stage)testMicBtn.getScene().getWindow();
			Stage stage = new Stage();
			stage.initOwner(parentStage);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setScene(new Scene(root));
			stage.setTitle("Mic Test");
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void noFileAlert() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("ERROR");
		alert.setHeaderText(null);
		alert.setContentText("No file selected");
		alert.showAndWait();
	}

// Fill the recording list with corresponding recordings
	public void newNameSelected() {

		fillAttemptList();
		updateArchive();
		selectedArchive = null;
	}

	// Gets the files in the creations folder as a list
	public void initialiseAttemptDatabase() {
		attemptDatabase = new ArrayList<String>(Arrays.asList(creations.list()));
	}

	// Finds corresponding names in creations folder and adds to recordinglist
	public void fillAttemptList() {
		for (String s : attemptDatabase) {
			if (s.lastIndexOf("_") != -1) {
				String nameMatch = s.substring(0, s.lastIndexOf("_"));
				if (selectedName.equals(nameMatch)) {
					String toAddToList = s.substring(0, s.lastIndexOf("."));
					if (!listOfAttempts.contains(toAddToList)) {
						listOfAttempts.add(toAddToList);
					}
				}
			}
		}
	}


	// Update recording list
	public void updateArchive() {
		recordedList = FXCollections.observableArrayList(listOfAttempts);
		if (recordedList.size() == 0) {
			contains = false;
			availableListView.setMouseTransparent(true);
			availableListView.setFocusTraversable(false);
		} else {
			contains = true;
			availableListView.setMouseTransparent(false);
			availableListView.setFocusTraversable(true);
		}
		availableListView.setItems(recordedList);
		availableListView.getSelectionModel().clearSelection();
	}

// Disable all buttons
	private void setAllButtonsDisabled(boolean b) {
		playButton.setDisable(b);
		prevButton.setDisable(b);
		nextButton.setDisable(b);
		recordButton.setDisable(b);
		playArcButton.setDisable(b);
		deleteArcButton.setDisable(b);
		returnButton.setDisable(b);
		displayListView.setMouseTransparent(b);
	}

// Goes back to name selection menu
	public void returnToNameSelection() {
		NameSelectionMenu.clearHasNone();
		NameSelectionMenu ctrl = new NameSelectionMenu();
		returnButton.getScene().setRoot(ctrl.getControllerRoot());
	}


// Fills up the names to practice
	public void getlistToDisplay() {
		for (String[] s : namesToPractice) {
			String displayName = String.join("", s);
			listToDisplay.add(displayName);
		}
	}


// Creates a new list that has the file names of the names that needs to be concatenated
	public void makeNewAudio() {
		if(listOfAudioCreated.get(selectedIndex) == null) { // Checks if the selected name already has a list
			String[] nameArray = namesToPractice.get(selectedIndex);
			namesToPlay = new ArrayList<>();
			for(String s : nameArray){ // Goes through every name in the String array
				if(Collections.frequency(MainMenu.getListOfJustNames(), s) > 1) { // Checks if there are more than one of the same name
					// Adds all of the files to a temp list and randomly chooses a file from that list
					List<File> duplicates = new ArrayList<>();
					for (NameFile namefile : namesDatabase) { // Goes through each name in the database to find corresponding name
						if (namefile.getName().equals(s)) {
							duplicates.add(new File("names/" + namefile.getFileName()));
						}
					}
					int index = r.nextInt(duplicates.size());
					System.out.println(index);
					System.out.println("Size"+duplicates.size());
					namesToPlay.add(duplicates.get(index));
				} else {
					for (NameFile namefile : namesDatabase) {
						if (namefile.toString().equals(s)) {
							namesToPlay.add(new File("names/" + namefile.getFileName()));
						}
					}
				}
			}
			listOfAudioCreated.set(selectedIndex, namesToPlay);
		}
	}

// Gets new selected name from mouse click
	public void handleDisplayListClicked(MouseEvent mouseEvent) {
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		selectedIndex = listToDisplay.indexOf(selectedName);
		newNameSelected();
		makeNewAudio();
		playingLabel.setText(selectedName);
	}

// Initialises lists by getting them from other menus
	private void initialiseDatabases(){
		namesDatabase = MainMenu.getAddedNames();
		listOfJustNames = MainMenu.getListOfJustNames();
		namesToPractice = NameSelectionMenu.getNamesObList();
		numberToPractice = namesToPractice.size();
		listOfAudioCreated = new ArrayList<List>(Collections.nCopies(numberToPractice, null));

		initialiseAttemptDatabase();
	}

}
