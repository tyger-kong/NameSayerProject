package namesayer;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;

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
	private ProgressBar micBar = new ProgressBar();

	@FXML
	private Label playingLabel;

	private List<String[]> namesToPractice;
	private List<NameFile> namesDatabase;

	private File creations = new File("./Creations");


	private List<String> attemptDatabase;
	private List<String> listOfAttempts = new ArrayList<String>();
	private List<String> listOfJustNames;

	private boolean closePractice = false;

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
		initialiseDatabases();
		listToDisplay = FXCollections.observableArrayList();
		getlistToDisplay();
		if(NameSelectionMenu.isShuffleSelected()){
			Collections.shuffle(listToDisplay);
		}
		displayListView.setItems(listToDisplay);
		displayListView.getSelectionModel().clearSelection();
		displayListView.getSelectionModel().selectFirst();
		selectedIndex = 0;
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		playingLabel.setText(selectedName);
		makeNewAudio();
		newNameSelected();

		// Show microphone level on a ProgressBar
		//        new Thread() {
		//            @Override
		//            public void run() {
		//                micBar.setStyle("-fx-accent: red;");
		//                // Taken from https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
		//                // Open a TargetDataLine for getting microphone input & sound level
		//                TargetDataLine line = null;
		//                AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
		//                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 	// format is an AudioFormat object
		//                if (!AudioSystem.isLineSupported(info)) {
		//                    System.out.println("The line is not supported.");
		//                }
		//                // Obtain and open the line.
		//                try {
		//                    line = (TargetDataLine) AudioSystem.getLine(info);
		//                    line.open(format);
		//                    line.start();
		//                } catch (LineUnavailableException ex) {
		//                    System.out.println("The TargetDataLine is Unavailable.");
		//                }
		//
		//                while (true) {
		//                    byte[] bytes = new byte[line.getBufferSize() / 5];
		//                    line.read(bytes, 0, bytes.length);
		//                    double prog = (double) calculateRMSLevel(bytes) / 65;
		//                    micBar.setProgress(prog);
		//
		//                    if (closePractice || !micBar.getScene().getWindow().isShowing()) {
		//                        line.close();
		//                        return;
		//                    }
		//                }
		//            }
		//        }.start();

	}


	public void handlePrevButton(ActionEvent actionEvent) {
		if (selectedIndex == 0) {

		} else {
			selectedIndex--;
			makeNewAudio();
			displayListView.scrollTo(selectedIndex);
			displayListView.getSelectionModel().select(selectedIndex);
			//        }
			selectedName = displayListView.getSelectionModel().getSelectedItem();
			playingLabel.setText(selectedName);
			newNameSelected();
		}
	}


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


	public void handlePlayButton(ActionEvent actionEvent) {
		playAudio(listOfAudioCreated.get(selectedIndex));
	}


	public void handleArcListClicked(MouseEvent mouseEvent) {
		selectedArchive = availableListView.getSelectionModel().getSelectedItem();
	}


	public void handlePlayArc(ActionEvent actionEvent) {
		if (selectedArchive == null) {
			noFileAlert();
		} else {
			toPlay = selectedName;
			String fileToPlay = toPlay.substring(0, toPlay.lastIndexOf("_")+1) + selectedArchive;
			//playAudio("Creations/" + fileToPlay + ".wav");
		}

	}


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
		//		updateArchive();
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

		// ********************************************************************************
		// MIGHT NEED TO RETURN FROM THIS THREAD SOME HOW

	}


	private void finishRecording() {
		recorder.finishRecording();
	}


	public void noFileAlert() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("ERROR");
		alert.setHeaderText(null);
		alert.setContentText("No file selected");
		alert.showAndWait();
	}


	// Taken from https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
	protected static int calculateRMSLevel(byte[] audioData) {
		// audioData might be buffered data read from a data line
		long lSum = 0;
		for (int i = 0; i < audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;

		double sumMeanSquare = 0d;
		for (int j = 0; j < audioData.length; j++)
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;
		return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
	}


	public void newNameSelected() {

		fillAttemptList();
		updateArchive();
		selectedArchive = null;
	}


	public void initialiseAttemptDatabase() {
		attemptDatabase = new ArrayList<String>(Arrays.asList(creations.list()));
	}


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


	// Update attempts list
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


	private void setAllButtonsDisabled(boolean b) {
		playButton.setDisable(b);
		prevButton.setDisable(b);
		nextButton.setDisable(b);
		recordButton.setDisable(b);
		playArcButton.setDisable(b);
		deleteArcButton.setDisable(b);
	}


	public void returnToNameSelection() {
		NameSelectionMenu.clearHasNone();
		closePractice = true;
		NameSelectionMenu ctrl = new NameSelectionMenu();
		returnButton.getScene().setRoot(ctrl.getControllerRoot());
	}


	public void getlistToDisplay(){
		for( String[] s : namesToPractice){
			String displayName = String.join("", s);
			listToDisplay.add(displayName);
		}
	}


	public void makeNewAudio(){
		if(listOfAudioCreated.get(selectedIndex) == null) {
			String[] nameArray = namesToPractice.get(selectedIndex);
			namesToPlay = new ArrayList<>();
			for(String s : nameArray){
				if(Collections.frequency(MainMenu.getListOfJustNames(), s) > 1) {
					List<File> duplicates = new ArrayList<>();
					for (NameFile namefile : namesDatabase) {
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


	public void handleDisplayListClicked(MouseEvent mouseEvent) {
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		selectedIndex = listToDisplay.indexOf(selectedName);
		newNameSelected();
		makeNewAudio();
		playingLabel.setText(selectedName);
	}


	private void initialiseDatabases(){
		namesDatabase = MainMenu.getAddedNames();
		listOfJustNames = MainMenu.getListOfJustNames();
		namesToPractice = NameSelectionMenu.getNamesObList();
		numberToPractice = namesToPractice.size();
		listOfAudioCreated = new ArrayList<List>(Collections.nCopies(numberToPractice, null));

		initialiseAttemptDatabase();
	}

}
