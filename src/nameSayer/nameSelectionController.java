package nameSayer;

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

public class nameSelectionController implements Initializable {
    @FXML
    private ComboBox<String> inputMethodComboBox;
    @FXML
    private Button fieldComfirmationButton;
    @FXML
    private TextField nameInputField;
    @FXML
    private ListView<String> namesSelectedListView;
    @FXML
    private Button returnFromSelectionButton;
    @FXML
    private Button practiceButton;

    private static List<String> listOfNamesSelected = new ArrayList<String>();
    private static Parent controllerRoot;

    public void onPracticeAction(ActionEvent actionEvent) {

        if (listOfNamesSelected.isEmpty()) {
            Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
            nonSelectedAlert.setTitle("ERROR");
            nonSelectedAlert.setHeaderText(null);
            nonSelectedAlert.setContentText("No name(s) have been selected. Please select at least one name to practice");
            nonSelectedAlert.showAndWait();
        } else {


            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nameSayer/practiceMenu.fxml"));
                Parent root = fxmlLoader.load();
                namesSelectedListView.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to open practice menu");
            }
        }
        controllerRoot = practiceButton.getScene().getRoot();

    }

    public static List<String> getAddedList() {
        return listOfNamesSelected;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<String> rateList = FXCollections.observableArrayList("Manual", "Text File");
        inputMethodComboBox.setItems(rateList);

    }

    public void handleReturnFromSelection(ActionEvent actionEvent) {
        Controller ctrl = new Controller();
        returnFromSelectionButton.getScene().setRoot(ctrl.getControllerRoot());
    }

    public void handleConfirmationPressed(ActionEvent actionEvent) {
    }

    public Parent getControllerRoot() {
        return controllerRoot;
    }
}
