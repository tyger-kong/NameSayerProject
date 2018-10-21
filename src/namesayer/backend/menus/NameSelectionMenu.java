package namesayer.backend.menus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import namesayer.backend.AutoCompleteTextField;
import namesayer.backend.NameListCell;
import namesayer.backend.handlers.FXMLHandler;
import namesayer.backend.handlers.NameChecker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class NameSelectionMenu implements Initializable {

	private static final String PRACTICE_MENU = "/namesayer/frontend/fxml/PracticeMenu.fxml";
	private static final String SAVED_FOLDER = "USER_SAVED_LISTS";

	@FXML
	private VBox vBoxRoot;
	@FXML
	private Button mainMenuBtn;
	@FXML
	private ComboBox<String> inputMethodChoice;
	@FXML
	private AnchorPane inputPane;
	private AutoCompleteTextField nameInputField;
	@FXML
	private Button addNameBtn;
	@FXML
	private ListView<String[]> namesSelectedListView;
	@FXML
	private Button deleteBtn;
	@FXML
	private Button deleteAllBtn;
	@FXML
	private RadioButton shuffleBtn;
	@FXML
	private Button exportBtn;
	@FXML
	private Button practiceBtn;

	private static List<String> listOfNamesFromFile = new ArrayList<String>();
	private static Parent nameSelectionMenuRoot;

	private List<String> listOfUserInput = new ArrayList<>();

	private static ObservableList<String[]> namesObsListManual = FXCollections.observableArrayList();
	private static ObservableList<String[]> namesObsListFile = FXCollections.observableArrayList();

	private static boolean selectedManual;
	private String[] selectedNameArray;

	private static String[] justDeletedSingle;
	private static ObservableList<String[]> justDeletedList;
	private static boolean singleDeleted;

	private static boolean shuffleSelected;
	private static List<String> namesNotInDatabase = new ArrayList<>();

	private static String fileChosen;

	private FXMLHandler fxmlHandler = new FXMLHandler();


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> rateList = FXCollections.observableArrayList("Browse for text file", "Manual input");
		inputMethodChoice.setItems(rateList);
		inputMethodChoice.setValue("Browse for text file");
		exportBtn.setDisable(true);
		selectedManual = false;

		nameInputField = new AutoCompleteTextField();

		List<String> popupList = new ArrayList<String>();
		popupList.addAll(MainMenu.getAddedList());
		popupList.replaceAll(String::toLowerCase);
		nameInputField.getEntries().addAll(popupList);

		String prompt = (fileChosen != null) ? fileChosen : "Browse for a text file by clicking the button -->";
		nameInputField.setPromptText(prompt);
		nameInputField.setDisable(true);
		nameInputField.setPrefWidth(292);
		inputPane.getChildren().add(nameInputField);

		namesSelectedListView.setItems(namesObsListFile);
		namesSelectedListView.setCellFactory(names -> new NameListCell());
		namesSelectedListView.setMouseTransparent(false);
		namesSelectedListView.setFocusTraversable(true);

		setShortcuts();
	}


	/**
	 * Set keyboard shortcuts for the user input TextField and ListView
	 */
	private void setShortcuts() {
		// Handle enter and backspace keys being pressed in the user input TextField
		nameInputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent k) {
				if (k.getCode().equals(KeyCode.ENTER)) {
					addNameBtnClicked();
				}
				if (k.getCode().equals(KeyCode.BACK_SPACE) && (nameInputField.getText() != null) && !nameInputField.getText().isEmpty()) {
					nameInputField.setText(nameInputField.getText().substring(0, nameInputField.getText().length() - 1));
					nameInputField.positionCaret(nameInputField.getText().length());
				}
			}

		});

		// Shortcuts to delete and undo previous delete
		namesSelectedListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
			final KeyCombination delete = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);
			final KeyCombination undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
			@Override
			public void handle(KeyEvent k) {
				if (delete.match(k) || k.getCode().equals(KeyCode.DELETE)) {
					deleteBtnClicked(null);
				} 
				if (undo.match(k)) {
					if (singleDeleted && (justDeletedSingle != null)) {
						namesSelectedListView.getItems().add(justDeletedSingle);
						justDeletedSingle = null;

					} else if (!singleDeleted && (justDeletedList != null)) {
						namesSelectedListView.getItems().addAll(justDeletedList);
						justDeletedList = null;
					}
				}
			}

		});
	}


	/**
	 * Go back to main menu
	 */
	public void mainMenuBtnClicked(ActionEvent actionEvent) {
		mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
	}


	/**
	 * 
	 */
	public void addNameBtnClicked() {
		if (selectedManual) {
			if (((nameInputField.getText() == null) || nameInputField.getText().trim().equals(""))) { // If no name is entered
				Alert noInputAlert = new Alert(Alert.AlertType.INFORMATION);
				noInputAlert.setTitle("ERROR - Please enter a name");
				noInputAlert.setHeaderText(null);
				noInputAlert.setContentText("No name entered. Please enter a name to practice");
				noInputAlert.showAndWait();
			} else {
				// Trim leading and trailing white space and hyphens, and replace multiple spaces/hyphens with single ones
				String userInput = nameInputField.getText().toLowerCase().trim().replaceAll(" +", " ").replaceAll("\\s*-\\s*", "-").replaceAll("-+", "-").replaceAll("^-", "").replaceAll("-$", "");

				// Convert the name to a string array and 
				if (!userInput.isEmpty()) {
					String[] inputArray = NameChecker.nameAsArray(userInput);

					boolean isInList = false;
					for (String s : listOfUserInput) { // Checks if the input has already been entered
						if (userInput.toLowerCase().equals(s.toLowerCase())) {
							Alert alreadyExistsAlert = new Alert(Alert.AlertType.INFORMATION);
							alreadyExistsAlert.setTitle("ERROR - Name Already in List");
							alreadyExistsAlert.setHeaderText(null);
							alreadyExistsAlert.setContentText("The name has already been selected. Please enter another name to practice");
							alreadyExistsAlert.showAndWait();
							nameInputField.setText(null);
							isInList = true;
							break;
						}

					}
					if (!isInList) { // Adds the input to lists
						namesObsListManual.add(inputArray); // Fills listview corresponding to manual input
						listOfUserInput.add(userInput); // Used for exporting to .txt
						nameInputField.setText(null);
					}
				}
			}
		} else { // .txt Input

			listOfNamesFromFile.clear();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Choose a txt file");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File selectedFile = fileChooser.showOpenDialog(addNameBtn.getScene().getWindow());

			// Reads .txt file line by line and adds it to the list
			if ((selectedFile != null) && (selectedFile.getPath().substring(selectedFile.getAbsolutePath().lastIndexOf('.')).equals(".txt"))) {
				System.out.println(selectedFile.getPath().substring(selectedFile.getAbsolutePath().lastIndexOf('.')));
				fileChosen = selectedFile.getAbsolutePath();
				nameInputField.setText(fileChosen);
				try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
					String line;
					while ((line = br.readLine()) != null) {
						if (!line.trim().equals("")) {
							if (!listOfNamesFromFile.contains(line)) {
								listOfNamesFromFile.add(line);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Fills the Listview corresponding to .txt input
				namesObsListFile.clear();
				for (String input : listOfNamesFromFile) {
					input = input.trim().replaceAll(" +", " ").replaceAll("\\s*-\\s*", "-").replaceAll("-+", "-").replaceAll("^-", "").replaceAll("-$", "");
					String[] inputArray = NameChecker.nameAsArray(input);
					namesObsListFile.add(inputArray);
				}

			}

		}
	}


	public void practiceBtnClicked(ActionEvent actionEvent) {
		if (namesSelectedListView.getItems().isEmpty()) {
			showAlert(false, "ERROR - Please select some names", "No name(s) have been entered. Please enter at least one name to practice");	
			
		} else if (namesNotInDatabase.size() > 0) {
			// Check if there are any names in the list that are not in database
			Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
			nonSelectedAlert.setTitle("ERROR - Name doesn't exist");
			nonSelectedAlert.setHeaderText(null);
			nonSelectedAlert.setContentText("There is at least one name entered that is not in the database. Please delete it and enter another name.");
			nonSelectedAlert.showAndWait();
		} else {

			// Checks if shuffle is selected or not
			if (shuffleBtn.isSelected()) {
				shuffleSelected = true;
			} else {
				shuffleSelected = false;
			}

			nameSelectionMenuRoot = practiceBtn.getScene().getRoot();
			fxmlHandler.load(PRACTICE_MENU, practiceBtn);
		}
	}


	public void deleteBtnClicked(ActionEvent actionEvent) {
		if (selectedNameArray != null) {
			setDeleteShortcuts(); // Allows using delete/undo shortcuts
			clearHasNone();// Clears the list of any names not in database
			// Removes the name from lists
			namesSelectedListView.getItems().remove(selectedNameArray);
			listOfUserInput.remove(String.join("", selectedNameArray));
			// For undo function
			singleDeleted = true;
			justDeletedSingle = selectedNameArray;
			selectedNameArray = null;

		}
		namesSelectedListView.getSelectionModel().clearSelection();
	}


	public void deleteAllBtnClicked(ActionEvent actionEvent) {
		setDeleteShortcuts();
		justDeletedList = FXCollections.observableArrayList(namesSelectedListView.getItems()); // Gets the list of names deleted for undo functionality
		// Clear lists
		listOfUserInput.clear();
		singleDeleted = false;
		namesSelectedListView.getItems().clear();
		clearHasNone();
	}


	private void setDeleteShortcuts() {
		// Shortcuts to delete and undo previous delete
		vBoxRoot.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			final KeyCombination undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);

			@Override
			public void handle(KeyEvent k) {
				if (undo.match(k)) {
					if (singleDeleted && (justDeletedSingle != null)) {
						namesSelectedListView.getItems().add(justDeletedSingle);
						justDeletedSingle = null;

					} else if (!singleDeleted && (justDeletedList != null)) {
						namesSelectedListView.getItems().addAll(justDeletedList);
						justDeletedList = null;
					}
				}
			}
		});
	}


	// Manages the selection of manual or .txt input (Disable textfields, butons, etc)
	public void onInputSelected(ActionEvent actionEvent) {
		if (inputMethodChoice.getValue().equals("Manual input")) {
			justDeletedList = null;
			justDeletedSingle = null;
			listOfNamesFromFile.clear();
			selectedManual = true;
			nameInputField.clear();
			nameInputField.setPromptText("Enter a name");
			nameInputField.setDisable(false);
			nameInputField.requestFocus();
			namesSelectedListView.setItems(namesObsListManual);
			exportBtn.setDisable(false);


		} else if (inputMethodChoice.getValue().equals("Browse for text file")) {
			justDeletedList = null;
			justDeletedSingle = null;
			selectedManual = false;
			String prompt = (fileChosen != null) ? fileChosen : "Browse for a text file by clicking the button -->";
			nameInputField.setPromptText(prompt);
			nameInputField.setDisable(true);
			namesSelectedListView.setItems(namesObsListFile);
			exportBtn.setDisable(true);
		}
	}


	public void handleListSelected(MouseEvent mouseEvent) {
		selectedNameArray = namesSelectedListView.getSelectionModel().getSelectedItem();
	}


	/**
	 *  Returns the names to practice
	 */
	public static ObservableList<String[]> getNamesObList() { 
		if (selectedManual) {
			return namesObsListManual;
		}
		return namesObsListFile;
	}


	/**
	 *  Returns if shuffle has been selected
	 */
	public static boolean isShuffleSelected() { 
		return shuffleSelected;
	}


	/**
	 *  Allows for switching back to this scene
	 */
	public Parent getControllerRoot() { 
		return nameSelectionMenuRoot;
	}


	/**
	 *  Adds a name to list of names not in database
	 */
	public static void addToNoneList(String name) {
		namesNotInDatabase.add(name);
	}


	// Exports the list of selected names to a .txt file
	public void exportBtnClicked(ActionEvent actionEvent) {
		if (!namesObsListManual.isEmpty()) {
			// Creates folder if it doesn't already exist
			File savedFileFolder = new File(SAVED_FOLDER);
			if (!savedFileFolder.exists()) {
				savedFileFolder.mkdirs();
			}

			//Checks if the .txt already exists. Number increases if yes
			int attempt = 0;
			String fileName = SAVED_FOLDER + "/savedList.txt";
			File currentFile = new File(fileName);
			while (currentFile.exists()) {
				attempt++;
				fileName = SAVED_FOLDER + "/savedList_" + attempt + ".txt";
				currentFile = new File(fileName);
			}

			// For every name the user inputed, Write to .txt
			try {
				FileWriter fw = new FileWriter(fileName, true);
				for (String[] nameArr : namesObsListManual) {
					String name = "";
					for (String n : nameArr) {
						name += n;
					}
					fw.write(name + "\n");
				}
				fw.close();
				showAlert(true, "File Created", fileName);

			} catch (IOException e) {
				showAlert(false, "ERROR: Failed to create file", "An error occured while trying to export");
			}
		} else {
			showAlert(false, "ERROR - Please select some names", "No name(s) have been entered. Please enter at least one name before exporting");
		}
	}


	/**
	 * Shows alert notifying no names have been inputed
	 * 
	 * @param isSuccess - Whether exporting was successful or not
	 * @param title - Title of the alert window
	 * @param message - If successful then this is the fileName that was created, otherwise it is an error message
	 */
	private void showAlert(boolean isSuccess, String title, String message) {
		String content = (isSuccess) 
				? message.substring(SAVED_FOLDER.length()+1)+" has been created in the "+SAVED_FOLDER +" folder." 
						: message;
		Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
		nonSelectedAlert.setTitle(title);
		nonSelectedAlert.setHeaderText(null);
		nonSelectedAlert.setContentText(content);
		nonSelectedAlert.showAndWait();
	}


	// Clears the list of names not in database
	public static void clearHasNone() {
		namesNotInDatabase.clear();
	}

}
