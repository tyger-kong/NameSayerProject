package namesayer.backend.handlers;

import java.util.Collections;
import java.util.List;
import namesayer.backend.NameFile;
import namesayer.backend.SortIgnoreCase;

/**
 * Class for handling adding names to lists and checking lists for duplicate names
 */
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

}
