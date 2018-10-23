package namesayer.backend.handlers;

import javafx.scene.control.Button;
import namesayer.backend.NameFile;

/**
 * Class for handling actions for buttons, such as disabling multiple buttons and setting button colour for ratings
 */
public class ButtonHandler {
	
	/**
	 * Disables or enables all specified buttons
	 * @param bool - True if disabling is intended, false if enabling is intended
	 * @param btns - Buttons to disable/enable
	 */
	public void setDisabled(Boolean bool, Button... btns) {
		for (Button btn : btns) {
			btn.setDisable(bool);
		}
	}

	/**
	 * Sets the colour of a button depending on whether the selected file (in the names database) has a good or bad rating.
	 */
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
