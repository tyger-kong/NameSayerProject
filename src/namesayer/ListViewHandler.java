package namesayer;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

// Class to handle clicking and updating ListViews
public class ListViewHandler {

    // Updates the specified ListView with the specified list
    public void updateList(ListView<String> lv, List<String> l) {
        ObservableList<String> listToView = FXCollections.observableArrayList(l);
        lv.setItems(listToView);
        lv.getSelectionModel().clearSelection();
    }

}