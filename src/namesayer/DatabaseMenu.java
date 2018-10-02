package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class DatabaseMenu implements Initializable {
    @FXML
    private Button mainMenuBtn;
    @FXML

    private ListView<String> databaseListView;
    private ObservableList<String> namesInDatabase;

    public void mainMenuBtnClicked(ActionEvent actionEvent) {
        mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        namesInDatabase = FXCollections.observableArrayList(MainMenu.getAddedList());
        databaseListView.setItems(namesInDatabase);
        databaseListView.getSelectionModel().clearSelection();
    }

    public void handleListClicked(MouseEvent mouseEvent) {
        databaseListView.getSelectionModel().clearSelection();
    }
}
