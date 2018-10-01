package namesayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {

    @FXML
    private Button practiceBtn;
    @FXML
    private Button namesBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button quitBtn;

    private List<String> listOfNamesAdded;
    private static List<NameFile> namesListArray = new ArrayList<NameFile>();
    private static Parent mainMenuRoot;

    
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


    public void practiceBtnClicked(ActionEvent actionEvent) {
        try {
        	mainMenuRoot = practiceBtn.getScene().getRoot();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NameSelectionMenu.fxml"));
            Parent root = fxmlLoader.load();
            practiceBtn.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mainMenuRoot = practiceBtn.getScene().getRoot();
    }
    

    public void namesBtnClicked(ActionEvent actionEvent) {
    	mainMenuRoot = namesBtn.getScene().getRoot();
    }
    
    
    public void settingsBtnClicked(ActionEvent actionEvent) {
    	
    }

    
    public void quitBtnClicked(ActionEvent actionEvent) {
    	Stage stage = (Stage)quitBtn.getScene().getWindow();
    	stage.close();
    }
    
    
    public static Parent getMainMenuRoot() {
        return mainMenuRoot;
    }

}
