package namesayer;

import java.util.Collections;
import java.util.List;

public class ListHandler {
	
/**
 * Populate the specified lists with name file data.
 * 
 * @param listOfNamesInDatabase - Full file name of audio files.
 * @param listOfJustNames - Just the name part of the audio file.
 * @param listOfNamesAdded - Names to use when choosing names to practice (handles multiple names using numbers).
 * @param namesListArray - List of actual NameFile objects representing each name.
 */
	public void fillNameList(List<String> listOfNamesInDatabase, List<String> listOfJustNames, List<String> listOfNamesAdded, List<NameFile> namesListArray) {
		for (String currentFile : listOfNamesInDatabase) {
			String justName = currentFile.substring((currentFile.lastIndexOf("_") + 1), currentFile.lastIndexOf("."));
			listOfJustNames.add(justName.toLowerCase()); // List of just the names used for AutoCompleteTextfield

			String listName = justName;
			// Handle duplicate names by numbering them
			int attempt = 0;
			while (listOfNamesAdded.contains(listName)) {
				attempt++;
				listName = justName + "("+attempt+")";
			}

			listOfNamesAdded.add(listName); 

			NameFile name = new NameFile(currentFile, listName, justName.toLowerCase());
			if (name.checkIfBadRating()) {
				name.setBadRatingField(true);
			}
			namesListArray.add(name);
		}
		Collections.sort(listOfNamesAdded, new SortIgnoreCase());
	}
	
	
}
