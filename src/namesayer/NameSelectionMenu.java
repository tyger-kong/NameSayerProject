package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NameSelectionMenu implements Initializable {
    @FXML
    private Button mainMenuBtn;
    @FXML
    private ComboBox<String> inputMethodChoice;
    @FXML
    private TextField nameInputField;
    @FXML
    private Button addNameBtn;
    @FXML
    private ListView<String[]> namesListView;
    @FXML
    private Button practiceButton;
    @FXML
    private Button deleteBtn;

    private static List<String> listOfNamesSelected = new ArrayList<String>();
    private static Parent nameSelectionMenuRoot;
    
    private String[] testData1 = new String[]{"this", "is", "a", "test"};
    private String[] testData2= new String[]{"Mike", "-", "John", " ", "Lee"};
    private ObservableList<String[]> namesObsList = FXCollections.observableArrayList();
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> rateList = FXCollections.observableArrayList("Manual input", "Browse for text file");
        inputMethodChoice.setItems(rateList);
        
        namesObsList.addAll(testData1, testData2);
        namesListView.setItems(namesObsList);
        namesListView.setCellFactory(names -> new NameListCell());
    }

    
    public void mainMenuBtnClicked(ActionEvent actionEvent) {
        mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
    }
    
    
    public void addNameBtnClicked(ActionEvent actionEvent) {
    	
    }
    

    public void practiceBtnClicked(ActionEvent actionEvent) {
        if (listOfNamesSelected.isEmpty()) {
            Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
            nonSelectedAlert.setTitle("ERROR - Please select some names");
            nonSelectedAlert.setHeaderText(null);
            nonSelectedAlert.setContentText("No name(s) have been selected. Please select at least one name to practice");
            nonSelectedAlert.showAndWait();
        } else {
            try {
                nameSelectionMenuRoot = practiceButton.getScene().getRoot();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PracticeMenu.fxml"));
                Parent root = fxmlLoader.load();
                namesListView.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to open practice menu");
            }
        }
    }


    public Parent getControllerRoot() {
        return nameSelectionMenuRoot;
    }
    
    
    public static List<String> getAddedList() {
        return listOfNamesSelected;
    }

    
}
