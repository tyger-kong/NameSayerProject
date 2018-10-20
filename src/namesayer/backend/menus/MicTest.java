package namesayer.backend.menus;

import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;

public class MicTest implements Initializable {
	@FXML
	private ProgressBar micBar = new ProgressBar();
	
	private boolean closeMicTest;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Show microphone level on a ProgressBar
		new Thread() {
			@Override
			public void run() {
				micBar.setStyle("-fx-accent: red;");
				// Taken from https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
				// Open a TargetDataLine for getting microphone input & sound level
				TargetDataLine line = null;
				AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 	// format is an AudioFormat object
				
				if (!AudioSystem.isLineSupported(info)) {
					System.out.println("The line is not supported.");
				}
				
				// Obtain and open the line.
				try {
					line = (TargetDataLine) AudioSystem.getLine(info);
					line.open(format);
					line.start();
				} catch (LineUnavailableException ex) {
					System.out.println("The TargetDataLine is Unavailable.");
				}

				while (true) {
					byte[] bytes = new byte[line.getBufferSize() / 5];
					line.read(bytes, 0, bytes.length);
					double prog = (double) calculateRMSLevel(bytes) / 65;
					micBar.setProgress(prog);

					if (closeMicTest || !micBar.getScene().getWindow().isShowing()) {
						line.close();
						return;
					}
				}
			}
		}.start();
	}


	// Taken from https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
	protected static int calculateRMSLevel(byte[] audioData) {
		// audioData might be buffered data read from a data line
		long lSum = 0;
		for (int i = 0; i < audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;

		double sumMeanSquare = 0d;
		for (int j = 0; j < audioData.length; j++)
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;
		return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
	}

}
