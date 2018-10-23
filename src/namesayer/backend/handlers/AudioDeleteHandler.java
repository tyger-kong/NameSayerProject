package namesayer.backend.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AudioDeleteHandler {

	public void delete(File toDelete) {
		Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this attempt?", ButtonType.YES, ButtonType.NO);
		deleteConfirm.showAndWait();
		if (deleteConfirm.getResult() == ButtonType.YES) {
			try {
				Files.deleteIfExists(toDelete.toPath());
			} catch (IOException e) {
				System.out.println("FAILED TO DELETE");
			}
		}
	}
}
