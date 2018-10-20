package namesayer.backend.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import namesayer.backend.NameFile;
import namesayer.backend.handlers.FXMLHandler;
import namesayer.backend.handlers.ListHandler;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {
	
	private static final String NAME_SELECTION_MENU = "/namesayer/frontend/fxml/NameSelectionMenu.fxml";
	private static final String DATABASE_MENU = "/namesayer/frontend/fxml/DatabaseMenu.fxml";
	private static final String HELP_MENU = "/namesayer/frontend/fxml/HelpMenu.fxml";

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
	private ListHandler listHandler = new ListHandler();
	private FXMLHandler fxmlHandler = new FXMLHandler();


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialiseLists();
	}


	/**
	 * Goes through the database folder and converts each name into a NameFile object and stores it in namesListArray
	 */
	public void initialiseLists() {
		listOfNamesAdded = new ArrayList<>();
		listOfJustNames = new ArrayList<>();
		File nameFolder = new File("names");
		
		// Get all files in the folder as a List
		List<String> listOfNamesInDatabase = new ArrayList<String>(Arrays.asList(nameFolder.list())); 
		listHandler.fillNameList(listOfNamesInDatabase, listOfJustNames, listOfNamesAdded, namesListArray);
	}


	/**
	 * Starts practicing menu
	 */
	public void practiceBtnClicked(ActionEvent actionEvent) {
		mainMenuRoot = practiceBtn.getScene().getRoot();
		fxmlHandler.load(NAME_SELECTION_MENU, practiceBtn);
	}


	/** 
	 * Loads database menu
	 */
	public void namesBtnClicked(ActionEvent actionEvent) {
		mainMenuRoot = practiceBtn.getScene().getRoot();
		fxmlHandler.load(DATABASE_MENU, namesBtn);
	}


	/**
	 * Loads the instructions menu
	 */
	public void helpBtnClicked(ActionEvent actionEvent) {
		mainMenuRoot = helpBtn.getScene().getRoot();
		fxmlHandler.load(HELP_MENU, helpBtn);
	}


	/**
	 * Exits the application
	 */
	public void quitBtnClicked(ActionEvent actionEvent) {
		Stage stage = (Stage)quitBtn.getScene().getWindow();
		stage.close();
	}


	/**
	 * Allows switching scene back to main menu
	 */
	public static Parent getMainMenuRoot() {
		return mainMenuRoot;
	}


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
