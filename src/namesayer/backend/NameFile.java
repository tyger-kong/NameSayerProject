package namesayer.backend;

import javafx.beans.property.SimpleStringProperty;
import namesayer.backend.handlers.FileHandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameFile {
	private static final String BAD_RATINGS_TXT = "Bad_Ratings.txt";
    private SimpleStringProperty _fileName;
    private SimpleStringProperty _listName;
    private List<String> attemptList = new ArrayList<String>();
    private List<String> attemptListNameOnly = new ArrayList<String>();
    private SimpleStringProperty _justName;
    private FileHandler fileHandler = new FileHandler();
    
    public enum rating {
    	BAD, GOOD;
    }


    public NameFile(String fileName, String listName, String justName) {
        _fileName= new SimpleStringProperty(fileName);
        _listName = new SimpleStringProperty(listName);
        _justName = new SimpleStringProperty(justName);
    }


    public String getName() {
        int point = _fileName.get().lastIndexOf("_") + 1;
        int last = _fileName.get().lastIndexOf(".");
        return (_fileName.get().substring(point, last));
    }


    public String getFileName() {
        return _fileName.get();
    }


    public String getFileNameWithoutWAV() {
        return _fileName.get().substring(0, _fileName.get().length()-4);
    }


    /**
     *  Checks Bad_Ratings.txt file
     */
    public boolean checkIfBadRating() {
        List<String> ratingsList = new ArrayList<String>();
        try {
            FileInputStream stream = new FileInputStream(BAD_RATINGS_TXT);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String ratedFile;

            while ((ratedFile = br.readLine()) != null)   {
                ratingsList.add(ratedFile);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ratingsList.contains(this.getFileName());
    }


    /**
     * Sets rating field as well as writes to Bad_ratings.txt
     * 
     * @param rating - true if rating to be set is bad and false if good
     */
    public void setBadRating(boolean rating) {
        if (rating) {
            addBadRating();
        } else {
            removeBadRating();
        }
    }


    private void addBadRating() {
    	fileHandler.addBadRating(BAD_RATINGS_TXT, this.getFileName());
    }


    private void removeBadRating() {
    	fileHandler.removeBadRating(BAD_RATINGS_TXT, this.getFileName()); 
    }


    public String toString(){
        return _listName.get();
    }


    public void addAttempt(String attemptFileName) {
        attemptList.add(attemptFileName);
        attemptListNameOnly.add(attemptFileName.substring(attemptFileName.lastIndexOf("_")+1));
    }


    public List<String> getAttemptList() {
        if(!attemptList.isEmpty()) {
            Collections.sort(attemptList);
        }
        return attemptList;
    }


    public List<String> getAttemptListNameOnly() {
        if(!attemptListNameOnly.isEmpty()) {
            Collections.sort(attemptListNameOnly);
        }
        return attemptListNameOnly;
    }


    public void deleteAttempt(String attemptFileName) {
        attemptList.remove(attemptFileName);

        String nameInListNameOnly = attemptFileName.substring(attemptFileName.lastIndexOf("_")+1);
        attemptListNameOnly.remove(nameInListNameOnly);
    }

    
    public String getJustName(){
        return _justName.get();
    }

}