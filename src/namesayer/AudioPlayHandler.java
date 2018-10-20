package namesayer;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class AudioPlayHandler {
	ButtonHandler btnHandler = new ButtonHandler();
	
	public void play(String fileToPlay, Button... btns) {
		new Thread() {
			@Override
			public void run() {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(new File(fileToPlay));
					AudioFormat format = stream.getFormat();
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
					sourceLine.open(format);
					sourceLine.start();

					// Disable buttons while audio file plays
					long frames = stream.getFrameLength();
					long durationInSeconds = (frames / (long)format.getFrameRate());
					btnHandler.setDisabled(true, btns);
					PauseTransition pause = new PauseTransition(Duration.seconds(durationInSeconds));
					pause.setOnFinished(event -> {
						btnHandler.setDisabled(false, btns);
						Thread.currentThread().interrupt();
					});
					pause.play();

					int nBytesRead = 0;
					int BUFFER_SIZE = 128000;
					byte[] abData = new byte[BUFFER_SIZE];
					while (nBytesRead != -1) {
						try {
							nBytesRead = stream.read(abData, 0, abData.length);
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (nBytesRead >= 0) {
							@SuppressWarnings("unused")
							int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
						}
					}
					sourceLine.drain();
					sourceLine.close();
					return;
				} catch (Exception e) {
					System.out.println("FAILED TO PLAY FILE");
					e.printStackTrace();
				}
			}
		}.start();
	}
}
