package namesayer;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 * 
 * Taken from https://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 */
public class JavaSoundRecorder {
	// the line from which audio data is captured
	TargetDataLine line;

	
	/**
	 * Defines an audio format
	 */
	AudioFormat getAudioFormat() {
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		return format;
	}

	
	/**
	 * Captures the sound and record into a WAV file
	 */
	public void startRecording(File wavFile) {
		try {
			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			// checks if system supports the data line
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				System.exit(0);
			}
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();   // start capturing

			System.out.println("Start capturing...");

			AudioInputStream ais = new AudioInputStream(line);

			System.out.println("Start recording...");

			// start recording
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
			System.out.println("JUST DID THE WRITE METHOD");
			
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Closes the target data line to finish capturing and recording
	 */
	public void finishRecording() {
		System.out.println("ENTERED finishRecording() method");
		line.stop();
		line.close();
		System.out.println("Finished recording");
	}

}