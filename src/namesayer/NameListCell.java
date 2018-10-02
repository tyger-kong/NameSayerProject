package namesayer;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

// Based on https://www.turais.de/how-to-custom-listview-cell-in-javafx/
public class NameListCell extends ListCell<String[]> {
	@FXML
	GridPane cellGridPane;

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
		} else {
			
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
				cellGridPane.getChildren().add(nameLabel);

				// Set background color of the label
				// USE HELPER CLASS THAT CHECKS THE STRING (if it's in database, or if it's a "-" or " "
			}
		}
	}

}
