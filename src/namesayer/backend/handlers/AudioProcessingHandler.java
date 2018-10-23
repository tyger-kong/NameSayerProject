package namesayer.backend.handlers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AudioProcessingHandler {
	
	public void process(Parent root, List<File> namesToPlay) {
		Stage processingStage = new Stage();
		processingStage.setTitle("Working Hard");
		processingStage.setScene(new Scene(root, 200, 50));
		processingStage.setResizable(false);
		processingStage.initStyle(StageStyle.UNDECORATED);
		processingStage.show();

		Service<Void> backgroundThread = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						for (File f : namesToPlay) {
							String fileName = f.toString();
							String trimCommand = "ffmpeg -y -i " + fileName + " -af silenceremove=1:0:-50dB " + fileName;
							ProcessBuilder trimProcess = new ProcessBuilder("/bin/bash", "-c", trimCommand);

							try {
								Process trim = trimProcess.start();
								trim.waitFor();

							} catch (IOException e) {
								System.out.println("trim error");
							} catch (InterruptedException e) {
								System.out.println("wait error");
							}
						}
						return null;
					}
				};
			}
		};

		backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				processingStage.close();
			}
		});

		backgroundThread.start();
	}
}
