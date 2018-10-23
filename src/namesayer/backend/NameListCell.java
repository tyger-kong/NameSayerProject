package namesayer.backend;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import namesayer.backend.handlers.NameChecker;
import namesayer.backend.menus.NameSelectionMenu;

/**
 * Based on https://www.turais.de/how-to-custom-listview-cell-in-javafx/
 * @author Dinith Wannigama
 * 
 * This class is used in the NameSelectionMenu's ListView to show which parts of the names are in the database using
 * different colours
 *
 */
public class NameListCell extends ListCell<String[]> {
	@FXML
	HBox cellHBox;
	private FXMLLoader loader;

	@Override
	protected void updateItem(String[] names, boolean empty) {
		super.updateItem(names,  empty);

		if (empty || (names == null)) {
			setText(null);
			setGraphic(null);
		} else {

			// This prevents the ListView from bugging out and appending cells together (IMPORTANT)
			if (cellHBox != null) {
				cellHBox.getChildren().clear();
			}

			if (loader == null) {
				loader = new FXMLLoader(getClass().getResource("/namesayer/frontend/fxml/NameListCell.fxml"));
				loader.setController(this);

				try {
					loader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Add strings to the cell and set background colours
			for (String n : names) {

				Label nameLabel = new Label(n);
				nameLabel.setId("cellLabel");


				// Set background color of the label appropriately
				// Green = one occurrence, orange = more than one occurrence, red = no occurrences in database
				if ((n != "-") && (n != " ")) {

					if (NameChecker.checkNameDuplicates(n.toLowerCase())) {
						nameLabel.setStyle("-fx-background-color: #ffd633;");
					} else if (NameChecker.checkNameExists(n.toLowerCase())) {
						nameLabel.setStyle("-fx-background-color: #34ff35;");
					} else {
						nameLabel.setStyle("-fx-background-color: #ff3524;");
							NameSelectionMenu.addToNoneList(n);

					}
				}

				cellHBox.getChildren().add(nameLabel);
			}

			setText(null);
			setGraphic(cellHBox);
		}
	}

}
