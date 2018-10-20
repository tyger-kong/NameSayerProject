package namesayer;

import javafx.scene.control.Button;

public class ButtonHandler {

	public void setDisabled(Boolean bool, Button... btns) {
		for (Button btn : btns) {
			btn.setDisable(bool);
		}
	}
}
