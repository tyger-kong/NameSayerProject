package namesayer.backend.handlers;

import javafx.scene.control.Button;
import namesayer.backend.NameFile;
import namesayer.backend.NameFile.rating;

public class ButtonHandler {

	public void setDisabled(Boolean bool, Button... btns) {
		for (Button btn : btns) {
			btn.setDisable(bool);
		}
	}

	public void setRatingButton(Button btn, NameFile.rating rating) {
		if (rating.equals(NameFile.rating.GOOD)) {
			btn.setText("Rate Bad");
			btn.setStyle("-fx-background-color: green;");
		} else {
			btn.setText("Rate Good");
			btn.setStyle("-fx-background-color: red;");
		}
	}
}
