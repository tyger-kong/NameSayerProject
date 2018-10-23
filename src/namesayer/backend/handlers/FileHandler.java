package namesayer.backend.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileHandler {

	/**
	 * Reads lines from the specified text file and adds them to the specified list (ignores duplicate lines)
	 */
	public void readFromFileToList(File selectedFile, List<String> list) {
		try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.trim().equals("")) {
					if (!list.contains(line)) {
						list.add(line);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Writes the strings of the list to a text file in the specified folder. Each line of the file corresponds to one
	 * string array in the list.
	 * 
	 * @param list - String array list containing the strings to write to the file
	 * @param folder - Relative path to the folder where the text file will be saved
	 * 
	 * @return The name of the file created
	 */
	public String exportList(List<String[]> list, String folder) throws IOException {
		// Creates folder if it doesn't already exist
		File savedFileFolder = new File(folder);
		if (!savedFileFolder.exists()) {
			savedFileFolder.mkdirs();
		}

		//Checks if the .txt already exists. Number increases if yes
		int attempt = 0;
		String fileName = folder + "/savedList.txt";
		File currentFile = new File(fileName);
		while (currentFile.exists()) {
			attempt++;
			fileName = folder + "/savedList_" + attempt + ".txt";
			currentFile = new File(fileName);
		}

		// For every name the user inputed, Write to .txt
		FileWriter fw = new FileWriter(fileName, true);
		for (String[] nameArr : list) {
			String name = "";
			for (String n : nameArr) {
				name += n;
			}
			fw.write(name + "\n");
		}
		fw.close();

		return fileName;
	}
	
	/**
	 * Adds a bad rating to the specified name
	 * 
	 * @param file - File to save the bad rating to
	 * @param lineToAdd - File name of the name/recording
	 */
	public void addBadRating(String file, String lineToAdd) {
		try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(lineToAdd + "\n");
            fw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
	}
	
	
	/**
	 * Adds a bad rating to the specified name
	 * 
	 * @param file - File to remove the bad rating from
	 * @param lineToRemove - File name of the name/recording
	 */
	public void removeBadRating(String file, String lineToRemove) {
		try {
			File ratingsFile = new File(file);
			File tempFile = new File("temp.txt");
			tempFile.createNewFile();

			BufferedReader reader = new BufferedReader(new FileReader(ratingsFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				// trim newline when comparing with lineToRemove
				String trimmedLine = currentLine.trim();
				if(!trimmedLine.equals(lineToRemove)) {
					writer.write(currentLine + System.getProperty("line.separator"));
				}
			}
			writer.close();
			reader.close();
			ratingsFile.delete();
			tempFile.renameTo(ratingsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
