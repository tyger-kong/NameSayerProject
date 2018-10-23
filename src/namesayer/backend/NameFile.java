package namesayer.backend;

import javafx.beans.property.SimpleStringProperty;
import namesayer.backend.handlers.FileHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A class for representing individual names in the database as an object. Used to handle 
 */
public class NameFile {
	private static final String BAD_RATINGS_TXT = "Bad_Ratings.txt";
	private SimpleStringProperty _fileName;
	private SimpleStringProperty _listName;
	private List<String> attemptList = new ArrayList<String>();
	private List<String> attemptListNameOnly = new ArrayList<String>();
	private SimpleStringProperty _justName;
	private FileHandler fileHandler = new FileHandler();

	public enum rating {
		GOOD, BAD;
	}


	public NameFile(String fileName, String listName, String justName) {
		_fileName= new SimpleStringProperty(fileName);
		_listName = new SimpleStringProperty(listName);
		_justName = new SimpleStringProperty(justName);
	}

	
	/**
	 * @return Just the English name part of the audio file
	 */
	public String getName() {
		int point = _fileName.get().lastIndexOf("_") + 1;
		int last = _fileName.get().lastIndexOf(".");
		return (_fileName.get().substring(point, last));
	}


	/**
	 * @return The full file name of the audio file
	 */
	public String getFileName() {
		return _fileName.get();
	}
	
	
	/**
	 * @return Just the English name part of the audio file
	 */
	public String getJustName(){
		return _justName.get();
	}

	
	/**
	 * @return The entire file name without the .wav extension
	 */
	public String getFileNameWithoutWAV() {
		return _fileName.get().substring(0, _fileName.get().length()-4);
	}

	
	/**
	 * @return True if the name has a bad rating in Bad_Ratings.txt, false otherwise
	 */
	public boolean checkIfBadRating() {
		return fileHandler.checkIfBadRating(BAD_RATINGS_TXT, this.getFileName());
	}


	/**
	 * Sets rating field as well as writes to Bad_ratings.txt
	 * 
	 * @param rating - true if rating to be set is bad and false if good
	 */
	public void setBadRating(boolean rating) {
		if (rating) {
			fileHandler.addBadRating(BAD_RATINGS_TXT, this.getFileName());
		} else {
			fileHandler.removeBadRating(BAD_RATINGS_TXT, this.getFileName()); 
		}
	}


	public String toString() {
		return _listName.get();
	}

	
	/**
	 * Associates a user made recording attempt with the name
	 * @param attemptFileName - Name of the recorded attempt audio file
	 */
	public void addAttempt(String attemptFileName) {
		attemptList.add(attemptFileName);
		attemptListNameOnly.add(attemptFileName.substring(attemptFileName.lastIndexOf("_")+1));
	}

	
	/**
	 * @return A list of all the file names of attempts corresponding to this name
	 */
	public List<String> getAttemptList() {
		if(!attemptList.isEmpty()) {
			Collections.sort(attemptList);
		}
		return attemptList;
	}

	
	/**
	 * @return Just the simple English names of the user made attempt list associated with the name
	 */
	public List<String> getAttemptListNameOnly() {
		if(!attemptListNameOnly.isEmpty()) {
			Collections.sort(attemptListNameOnly);
		}
		return attemptListNameOnly;
	}

	
	/**
	 * Removes the specified user made attempt from the name
	 * @param attemptFileName - Name of the recorded attempt audio file
	 */
	public void deleteAttempt(String attemptFileName) {
		attemptList.remove(attemptFileName);
		String nameInListNameOnly = attemptFileName.substring(attemptFileName.lastIndexOf("_")+1);
		attemptListNameOnly.remove(nameInListNameOnly);
	}

}