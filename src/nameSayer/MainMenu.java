package nameSayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainMenu implements Initializable {


    private List<String> listOfNamesAdded;
    private static List<NameFile> namesListArray = new ArrayList<NameFile>();
    private static Parent controllerRoot;

    Stage practiceStage = new Stage();

    @FXML
    private Button practiceMenuButton;

    @FXML
    private Button nameMenuButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseListNotSelected();
    }

    public void initialiseListNotSelected() {
        listOfNamesAdded = new ArrayList<>();

        File nameFolder = new File("names");
        List<String> listOfNamesInDatabase = new ArrayList<String>(Arrays.asList(nameFolder.list()));

        for (String currentFile : listOfNamesInDatabase) {
            String justName = currentFile.substring((currentFile.lastIndexOf("_") + 1), currentFile.lastIndexOf("."));
            String listName = justName;
            int attempt = 0;

            // Handle duplicate names by numbering them
            while (listOfNamesAdded.contains(listName)) {
                attempt++;
                listName = justName + "-" + attempt;
            }
            listOfNamesAdded.add(listName);

            NameFile name = new NameFile(currentFile, listName);
            if (name.checkIfBadRating()) {
                name.setBadRatingField(true);
            }
            namesListArray.add(name);
        }

        Collections.sort(listOfNamesAdded);
    }


    public static List<NameFile> getAddedNames() {
        return namesListArray;
    }

    public Parent getControllerRoot() {
        return controllerRoot;
    }


    public void handlePracticeMenuPressed(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nameSayer/nameSelectionMenu.fxml"));
            Parent root = fxmlLoader.load();
            practiceMenuButton.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        controllerRoot = practiceMenuButton.getScene().getRoot();

    }

    public void handleNameMenuPressed(ActionEvent actionEvent) {
        controllerRoot = nameMenuButton.getScene().getRoot();

    }


}
