package namesayer;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


public class PracticeMenu implements Initializable {
    private String selectedName;

    private int selectedIndex = 0;

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

    @FXML
    private Service<Void> backgroundThread;

    private List<String[]> namesToPractice;
    private List<NameFile> namesDatabase;

    private File creationsFile = new File("./Creations");



    private List<String> attemptDatabase;
    private List<String> listOfAttempts = new ArrayList<String>();

    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-HHmmss");
    private Date date;
    private List<File> namesToPlay;
    private List<List> listOfAudioCreated;

    private boolean btnIsRecord;

    private JavaSoundRecorder recorder = new JavaSoundRecorder();
    
    private int numberToPractice;
    private String recordingName;
    private List<String> recordingNameList;
    private final int TARGET_DECIBEL = -35;


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
            playSingleAudio("Creations/" + selectedArchive + ".wav");
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
                            line.drain(); // Wait for the buffer to empty before closing the line
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


    // Plays the files in the given list one at a time
    private void playSingleAudio(String fileToPlay) {
        new Thread() {
            @Override
            public void run() {
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(new File(fileToPlay));
                    AudioFormat format = stream.getFormat();
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                    SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
                    sourceLine.open(format);
                    sourceLine.start();

                    // Disable buttons while audio file plays
                    long frames = stream.getFrameLength();
                    long durationInSeconds = (frames / (long) format.getFrameRate());
                    setAllButtonsDisabled(true);
                    PauseTransition pause = new PauseTransition(Duration.seconds(durationInSeconds));
                    pause.setOnFinished(event -> {
                        setAllButtonsDisabled(false);
                        Thread.currentThread().interrupt();
                    });
                    pause.play();

                    int nBytesRead = 0;
                    int BUFFER_SIZE = 128000;
                    byte[] abData = new byte[BUFFER_SIZE];
                    while (nBytesRead != -1) {
                        try {
                            nBytesRead = stream.read(abData, 0, abData.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (nBytesRead >= 0) {
                            @SuppressWarnings("unused")
                            int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
                        }
                    }
                    sourceLine.drain();
                    sourceLine.close();
                } catch (Exception e) {
                    System.out.println("FAILED TO PLAY FILE");
                    e.printStackTrace();
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
            String fileToDelete = toPlay.substring(0, toPlay.lastIndexOf("_") + 1) + selectedArchive;
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

                    //Removes the recording from lists and updates
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

        if (btnIsRecord) {
            date = new Date();
            String currentTime = formatter.format(date);
            recordingName = recordingNameList.get(selectedIndex) + "_" + currentTime;

            setAllButtonsDisabled(true);
            recordButton.setDisable(false);
            System.out.println("JUST ABOUT TO RECORD");
            recordingIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            startRecording(recordingName);
            System.out.println("JUST STARTED RECORDING");
            recordButton.setText("STOP");
            btnIsRecord = false;


        } else {
            System.out.println("RYAN LIM IS COOL");
            recordingIndicator.setProgress(0);
            finishRecording();
            listOfAttempts.add(recordingName);
            updateArchive();
            setAllButtonsDisabled(false);
            recordButton.setText("Record");
            btnIsRecord = true;
        }

    }


    private void startRecording(String recordingName) {
        File wavFile = new File("Creations/" + recordingName + ".wav");
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


    // Load test microphone window
    public void testMicBtnClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MicTest.fxml"));
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
        initialiseAttemptDatabase();
        fillAttemptList();
        updateArchive();
        selectedArchive = null;
    }


    // Gets the files in the creations folder as a list
    public void initialiseAttemptDatabase() {
        attemptDatabase = new ArrayList<String>(Arrays.asList(creationsFile.list()));
    }


    // Finds corresponding names in creations folder and adds to recordinglist
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
        availableListView.setMouseTransparent(b);
        testMicBtn.setDisable(b);
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
            recordingNameList.set(selectedIndex, tempName.trim());
            processRecordings();
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
    private void initialiseDatabases() {
        namesDatabase = MainMenu.getAddedNames();
        namesToPractice = NameSelectionMenu.getNamesObList();
        if (NameSelectionMenu.isShuffleSelected()) { // Checks if shuffle has been selected
            Collections.shuffle(namesToPractice);
        }
        numberToPractice = namesToPractice.size();
        listOfAudioCreated = new ArrayList<List>(Collections.nCopies(numberToPractice, null));
        recordingNameList = new ArrayList<String>(Collections.nCopies(numberToPractice, null));
        initialiseAttemptDatabase();
    }

    private void processRecordings() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ProcessingMenu.fxml"));
            Stage processingStage = new Stage();
            processingStage.setTitle("Working Hard");
            processingStage.setScene(new Scene(root, 200, 50));
            processingStage.setResizable(false);
            processingStage.initStyle(StageStyle.UNDECORATED);
            processingStage.show();


            backgroundThread = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {

                            for (File f : namesToPlay) {
                                String fileName = f.toString();
                                normaliseAudio(fileName);
                                String trimCommand = "ffmpeg -y -i " + fileName + " -af silenceremove=1:0:-35dB " + fileName; // names/se206................wav
                                ProcessBuilder trimProcess = new ProcessBuilder("/bin/bash", "-c", trimCommand);

                                try {
                                    Process trim = trimProcess.start();
                                    trim.waitFor();

                                } catch (IOException e) {
                                    System.out.println("trim error");

                                } catch (InterruptedException e) {
                                    System.out.println("waiterror");
                                }
                            }

                            return null;
                        }
                    };
                }
            };

            backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    processingStage.close();
                }
            });

            backgroundThread.start();
        } catch (IOException e) {
        }

    }

    public void normaliseAudio(String fileName){
        String getVolumeCommand = "ffmpeg -i " + fileName+ " -filter:a volumedetect -f null /dev/null 2>&1 | grep mean_volume";
        ProcessBuilder getVolumeBuilder = new ProcessBuilder("/bin/bash", "-c", getVolumeCommand);
        try{
            Process getVolumeProcess = getVolumeBuilder.start();
            getVolumeProcess.waitFor();
            InputStream stdOut = getVolumeProcess.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));

            String line = reader.readLine();
            int first = line.lastIndexOf("-");
            int last = line.lastIndexOf(".");
            String meanVolume = line.substring(first, last);
            int change = TARGET_DECIBEL - Integer.parseInt(meanVolume);

            String adjustVolumeCommand = "ffmpeg -y -i \"" + fileName +"\" -filter:a \"volume=" + Integer.toString(change) +"dB\" " + fileName;
            ProcessBuilder adjustVolumeBuilder = new ProcessBuilder("/bin/bash", "-c", adjustVolumeCommand);
            Process adjustVolumeProcess = adjustVolumeBuilder.start();
            adjustVolumeProcess.waitFor();

        }catch(IOException e){
            e.printStackTrace();
        } catch(InterruptedException e){
            e.printStackTrace();
        }


    }

}
