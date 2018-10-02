package namesayer;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

// Based on https://www.turais.de/how-to-custom-listview-cell-in-javafx/
public class NameListCell extends ListCell<String[]> {
	@FXML
	HBox cellHBox;

	private FXMLLoader loader;

	@Override
	protected void updateItem(String[] names, boolean empty) {

		System.out.println("\n\nUPDATEITEM METHOD CALLED");
		System.out.println("***" + names);
		System.out.println(empty);

		super.updateItem(names,  empty);

		if (empty || (names == null)) {
			setText(null);
			setGraphic(null);
			System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
		} else {
			
			// This prevents the ListView from bugging out and appending cells together
			if (cellHBox != null) {
				cellHBox.getChildren().clear();
			}

			if (loader == null) {
				loader = new FXMLLoader(getClass().getResource("NameListCell.fxml"));
				loader.setController(this);

				try {
					loader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Add strings to the cell and set background colours
			for (String n : names) {
				System.out.println("..........................."  + n);
				Label nameLabel = new Label(n);

				// Set background color of the label
				// USE HELPER CLASS THAT CHECKS THE STRING (if it's in database, or if it's a "-" or " ")
				if ((n == "Mike") || (n == "Ryan")) {
					nameLabel.setStyle("-fx-background-color: #ff3524;");
				}
				if ((n == "Cena") || (n == "Tyger")) {
					nameLabel.setStyle("-fx-background-color: #34ff35;");
				}
				if (n == "En") {
					nameLabel.setStyle("-fx-background-color: #44355f;");
				}

				cellHBox.getChildren().add(nameLabel);
			}

			setText(null);
			setGraphic(cellHBox);
		}
	}

}
