package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class NameSelectionMenu implements Initializable {
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
    private Button practiceButton;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button deleteAllButton;
    @FXML
    private RadioButton shuffleButton;
    @FXML
    private Button exportButton;
    private String savedFolder = "Saved Lists/";

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> rateList = FXCollections.observableArrayList("Browse for text file", "Manual input");
        inputMethodChoice.setItems(rateList);
        inputMethodChoice.setValue("Browse for text file");
        exportButton.setDisable(true);

        nameInputField = new AutoCompleteTextField();

        // Handle keyboard buttons being pressed
        nameInputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                if (k.getCode().equals(KeyCode.ENTER)) {
                    addNameBtnClicked();
                }
                if (k.getCode().equals(KeyCode.BACK_SPACE) && (nameInputField.getText() != null || !nameInputField.getText().isEmpty() || !nameInputField.getText().trim().equals(""))) {
                    nameInputField.setText(nameInputField.getText().substring(0, nameInputField.getText().length() - 1));
                    nameInputField.positionCaret(nameInputField.getText().length());
                }
            }

        });

        nameInputField.getEntries().addAll(MainMenu.getListOfJustNames());
        String prompt = (fileChosen != null) ? fileChosen : "Browse for a text file by clicking the button -->";
        nameInputField.setPromptText(prompt);
        nameInputField.setDisable(true);
        nameInputField.setPrefWidth(292);
        inputPane.getChildren().add(nameInputField);

        namesSelectedListView.setItems(namesObsListFile);
        namesSelectedListView.setCellFactory(names -> new NameListCell());

        namesSelectedListView.setMouseTransparent(false);
        namesSelectedListView.setFocusTraversable(true);

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


    public void mainMenuBtnClicked(ActionEvent actionEvent) {
        mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
    }


    public void addNameBtnClicked() {
        if (selectedManual) {
            if (((nameInputField.getText() == null) || nameInputField.getText().trim().equals(""))) {
                Alert noInputAlert = new Alert(Alert.AlertType.INFORMATION);
                noInputAlert.setTitle("ERROR - Please enter a name");
                noInputAlert.setHeaderText(null);
                noInputAlert.setContentText("No name entered. Please enter a name to practice");
                noInputAlert.showAndWait();
            } else {
                // Trim leading and trailing white space and hyphens, and replace multiple spaces/hyphens with single ones
                String userInput = nameInputField.getText().trim().replaceAll(" +", " ").replaceAll("\\s*-\\s*", "-").replaceAll("-+", "-").replaceAll("^-", "").replaceAll("-$", "");

                if (!userInput.isEmpty()) {
                    String[] inputArray = NameChecker.nameAsArray(userInput);

                    boolean isInList = false;
                    for (String[] s : namesObsListManual) {
                        if (Arrays.equals(s, inputArray)) {
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
                    if (!isInList) {
                        namesObsListManual.add(inputArray);
                        listOfUserInput.add(userInput);
                        nameInputField.setText(null);
                    }
                }
            }
        } else {

            listOfNamesFromFile.clear();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open txt file");
            File selectedFile = fileChooser.showOpenDialog(addNameBtn.getScene().getWindow());

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
            showNoNamesEnteredAlert();
        } else if (namesNotInDatabase.size() > 0) {
            Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
            nonSelectedAlert.setTitle("ERROR - Name doesn't exist");
            nonSelectedAlert.setHeaderText(null);
            nonSelectedAlert.setContentText("There is at least one name entered that is not in the database. Please delete it and enter another name.");
            nonSelectedAlert.showAndWait();
        } else {

            if (shuffleButton.isSelected()) {
                shuffleSelected = true;
            } else {
                shuffleSelected = false;
            }
            try {
                nameSelectionMenuRoot = practiceButton.getScene().getRoot();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PracticeMenu.fxml"));
                Parent root = fxmlLoader.load();
                namesSelectedListView.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to open practice menu");
            }
        }
    }


    public void deleteBtnClicked(ActionEvent actionEvent) {
        if (selectedNameArray != null) {
            setDeleteShortcuts();
            namesNotInDatabase.clear();
            namesSelectedListView.getItems().remove(selectedNameArray);
            listOfUserInput.remove(String.join("", selectedNameArray));
            singleDeleted = true;
            justDeletedSingle = selectedNameArray;
            selectedNameArray = null;

        }
        namesSelectedListView.getSelectionModel().clearSelection();
    }


    public void handleDeleteAll(ActionEvent actionEvent) {
        setDeleteShortcuts();
        justDeletedList = FXCollections.observableArrayList(namesSelectedListView.getItems());
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


    public Parent getControllerRoot() {
        return nameSelectionMenuRoot;
    }


    public void onInputSelected(ActionEvent actionEvent) {
        if (inputMethodChoice.getValue().equals("Manual input")) {
            justDeletedList = null;
            justDeletedSingle = null;
            listOfNamesFromFile.clear();
            selectedManual = true;
            nameInputField.clear();
            nameInputField.setPromptText("Enter a name");
            nameInputField.setDisable(false);
            namesSelectedListView.setItems(namesObsListManual);
            exportButton.setDisable(false);


        } else if (inputMethodChoice.getValue().equals("Browse for text file")) {
            justDeletedList = null;
            justDeletedSingle = null;
            selectedManual = false;
            String prompt = (fileChosen != null) ? fileChosen : "Browse for a text file by clicking the button -->";
            nameInputField.setPromptText(prompt);
            nameInputField.setDisable(true);
            namesSelectedListView.setItems(namesObsListFile);
            exportButton.setDisable(true);
        }
    }


    public static ObservableList<String[]> getNamesObList() {
        if (selectedManual) {
            return namesObsListManual;
        }
        return namesObsListFile;
    }


    public void handleListSelected(MouseEvent mouseEvent) {
        selectedNameArray = namesSelectedListView.getSelectionModel().getSelectedItem();
    }


    public static boolean isShuffleSelected() {
        return shuffleSelected;
    }


    public static void addToNoneList(String name) {
        namesNotInDatabase.add(name);
    }


    public static void clearHasNone() {
        namesNotInDatabase.clear();
    }

    
    public void handleExportButton(ActionEvent actionEvent) {
        if(!listOfUserInput.isEmpty()) {
            File savedFileFolder = new File("Saved Lists");
            if (!savedFileFolder.exists()) {
                savedFileFolder.mkdirs();
            }
            int attempt = 0;
            String fileName = savedFolder + "savedList.txt";
            File currentFile = new File(fileName);
            while (currentFile.exists()) {
                attempt++;
                fileName = savedFolder + "savedList_" + attempt + ".txt";
                currentFile = new File(fileName);
            }

            try {
                FileWriter fw = new FileWriter(fileName, true);
                for (String name : listOfUserInput) {
                    fw.write(name + "\n");
                }
                fw.close();
                Alert alreadyExistsAlert = new Alert(Alert.AlertType.INFORMATION);
                alreadyExistsAlert.setTitle("File Created");
                alreadyExistsAlert.setHeaderText(null);

                alreadyExistsAlert.setContentText(fileName.substring(12) + " has been created.");
                alreadyExistsAlert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showNoNamesEnteredAlert();
        }


    }

    public void showNoNamesEnteredAlert(){
        Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
        nonSelectedAlert.setTitle("ERROR - Please select some names");
        nonSelectedAlert.setHeaderText(null);
        nonSelectedAlert.setContentText("No name(s) have been entered. Please enter at least one name to practice");
        nonSelectedAlert.showAndWait();
    }
}
