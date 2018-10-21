package namesayer.backend;

import javafx.beans.property.SimpleStringProperty;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
        try {
            FileWriter fw = new FileWriter(BAD_RATINGS_TXT, true);
            fw.write(this.getFileName() + "\n");
            fw.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    private void removeBadRating() {
        try {
            File ratingsFile = new File(BAD_RATINGS_TXT);
            File tempFile = new File("temp.txt");
            tempFile.createNewFile();

            BufferedReader reader = new BufferedReader(new FileReader(ratingsFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = this.getFileName();
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