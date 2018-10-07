package namesayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {

	@FXML
	private Button practiceBtn;
	@FXML
	private Button namesBtn;
	@FXML
	private Button helpBtn;
	@FXML
	private Button quitBtn;

	private static List<String> listOfNamesAdded;
	private static List<String> listOfJustNames;
	private static List<NameFile> namesListArray = new ArrayList<NameFile>();
	private static Parent mainMenuRoot;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialiseListNotSelected();
	}

	
	// Goes through the database folder and converts each name into a NameFile object and stores it in namesListArray
	public void initialiseListNotSelected() {
		listOfNamesAdded = new ArrayList<>();
		listOfJustNames = new ArrayList<>();

		File nameFolder = new File("names");
		List<String> listOfNamesInDatabase = new ArrayList<String>(Arrays.asList(nameFolder.list())); // Gets all files in the folder as a List

		for (String currentFile : listOfNamesInDatabase) {
			String justName = currentFile.substring((currentFile.lastIndexOf("_") + 1), currentFile.lastIndexOf("."));
			listOfJustNames.add(justName); // List of just the names used for AutoCompleteTextfield
			String listName = justName;

			int attempt = 0;
			// Handle duplicate names by numbering them
			while (listOfNamesAdded.contains(listName)) {
				attempt++;
				listName = justName + "-" + attempt;
			}

			listOfNamesAdded.add(listName); 

			NameFile name = new NameFile(currentFile, listName);
			if (name.checkIfBadRating()) {
				name.setBadRatingField(true);
			}
			namesListArray.add(name);
		}

		Collections.sort(listOfNamesAdded);
	}


	//Starts practicing menu
	public void practiceBtnClicked(ActionEvent actionEvent) {
		try {
			mainMenuRoot = practiceBtn.getScene().getRoot();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/NameSelectionMenu.fxml"));
			Parent root = fxmlLoader.load();
			practiceBtn.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	// Loads database menu
	public void namesBtnClicked(ActionEvent actionEvent) {
		try {
			mainMenuRoot = practiceBtn.getScene().getRoot();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/DatabaseMenu.fxml"));
			Parent root = fxmlLoader.load();
			practiceBtn.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	// Loads the instructions menu
	public void helpBtnClicked(ActionEvent actionEvent) {
		try {
			mainMenuRoot = helpBtn.getScene().getRoot();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/HelpMenu.fxml"));
			Parent root = fxmlLoader.load();
			helpBtn.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	// Exits the app
	public void quitBtnClicked(ActionEvent actionEvent) {
		Stage stage = (Stage)quitBtn.getScene().getWindow();
		stage.close();
	}

	
	// Allows switching scene back to main menu
	public static Parent getMainMenuRoot() {
		return mainMenuRoot;
	}

	
	// Getters for different lists
	public static List<NameFile> getAddedNames() {
		return namesListArray;
	}


	public static List<String> getAddedList() {
		return listOfNamesAdded;
	}


	public static List<String> getListOfJustNames() {
		return listOfJustNames;
	}

}
