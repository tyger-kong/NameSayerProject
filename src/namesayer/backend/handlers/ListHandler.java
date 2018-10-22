package namesayer.backend.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import namesayer.backend.NameFile;
import namesayer.backend.SortIgnoreCase;

public class ListHandler {

	/**
	 * Populate the specified lists with name file data.
	 * 
	 * @param listOfNamesInDatabase - Full file name of audio files.
	 * @param listOfJustNames - Just the name part of the audio file.
	 * @param listOfNamesAdded - Names to use when choosing names to practice (handles multiple names using numbers).
	 * @param namesListArray - List of actual NameFile objects representing each name.
	 */
	public void fillNameList(List<String> listOfNamesInDatabase, List<String> listOfJustNames, List<String> listOfNamesAdded, List<NameFile> namesListArray, List<String> listOfNamesLowered) {
		for (String currentFile : listOfNamesInDatabase) {
			if (currentFile.endsWith(".wav")) {
				String justName = currentFile.substring((currentFile.lastIndexOf("_") + 1), currentFile.lastIndexOf("."));
				listOfJustNames.add(justName.toLowerCase()); // List of just the names used for AutoCompleteTextfield

				String listName = justName;
				// Handle duplicate names by numbering them
				int number = Collections.frequency(listOfJustNames, justName.toLowerCase());
				if (number > 1) {
					listName = justName + "(" + number + ")";
				}

				listOfNamesAdded.add(listName);
				listOfNamesLowered.add(listName.toLowerCase());

				NameFile name = new NameFile(currentFile, listName, justName.toLowerCase());
				namesListArray.add(name);
			}
			Collections.sort(listOfNamesAdded, new SortIgnoreCase());
		}
	}


	/**
	 * Checks if a name is already in the specified list representing a ListView
	 * 
	 * @param str - String to check
	 * @param list - List being checked
	 */
	public boolean checkDuplicate(String str, List<String> list) {
		for (String s : list) {
			if (str.toLowerCase().equals(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}


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

}
