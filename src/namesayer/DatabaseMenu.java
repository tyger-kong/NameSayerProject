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
    private Button playDatabaseButton;

    private ObservableList<String> namesList;
    private List<NameFile> nameDatabase;

    private String selectedName;

    private NameFile currentName;
    private String toPlay;

    public void mainMenuBtnClicked(ActionEvent actionEvent) {
        mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameDatabase = MainMenu.getAddedNames();
        namesList = FXCollections.observableArrayList(MainMenu.getAddedList());
        databaseListView.setItems(namesList);
        databaseListView.getSelectionModel().clearSelection();
        rateButton.setDisable(true);
    }

    public void handleListClicked(MouseEvent mouseEvent) {
        selectedName = databaseListView.getSelectionModel().getSelectedItem();
        getCurrentName();
        System.out.println(currentName.getFileName());
        rateButton.setDisable(false);
        setRatingButton();
    }

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

    private void setRatingButton() {
        if (currentName.checkIfBadRating()) {
            rateButton.setText("Rate Good");
            rateButton.setStyle("-fx-background-color: red;");
        } else {
            rateButton.setText("Rate Bad");
            rateButton.setStyle("-fx-background-color: green;");
        }
    }
    public void getCurrentName(){
        for (NameFile n : nameDatabase) {
            if (n.toString().equals(selectedName)) {
                currentName = n;
            }
        }

    }

    private void playAudio(String fileToPlay) {
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
                    long durationInSeconds = (frames / (long)format.getFrameRate());
                    playDatabaseButton.setDisable(true);
                    PauseTransition pause = new PauseTransition(Duration.seconds(durationInSeconds));
                    pause.setOnFinished(event -> {
                        playDatabaseButton.setDisable(false);
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

    public void handlePlayButton(ActionEvent actionEvent) {
        toPlay = currentName.getFileName();
        playAudio("names/" + toPlay);
    }
}
