package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NameSelectionMenu implements Initializable {
	@FXML
	private Button mainMenuBtn;
	@FXML
	private ComboBox<String> inputMethodChoice;
	@FXML
	private TextField nameInputField;
	@FXML
	private Button addNameBtn;
	@FXML
	private ListView<String[]> namesListView;
	@FXML
	private Button practiceButton;
	@FXML
	private Button deleteBtn;

	private static List<String> listOfNamesSelected = new ArrayList<String>();
	private static Parent nameSelectionMenuRoot;

	private String[] testData1 = new String[]{"this", "is", "a", "test"};
	private String[] testData2 = new String[]{"Mike", "-", "John", " ", "Lee"};
	private String[] testData3 = new String[]{"John", "-", "Fernando", " ", "Cena", "-", "Wison", " ", "Anderson"};
	private String[] testData4 = new String[]{"Tyger"};
	private String[] testData5 = new String[]{"Ryan", " ", "Lim", " ", "En", "-", "Wei"};
	private ObservableList<String[]> namesObsList = FXCollections.observableArrayList();

	private boolean selectedManual;
	private boolean noneSelected = false;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> rateList = FXCollections.observableArrayList("Browse for text file", "Manual input");
		inputMethodChoice.setItems(rateList);
		inputMethodChoice.setValue("Browse for text file");

		namesObsList.addAll(testData2, testData1, testData3, testData4, testData5);
		namesListView.setItems(namesObsList);
		namesListView.setCellFactory(names -> new NameListCell());
		
		namesListView.setMouseTransparent(false);
		namesListView.setFocusTraversable(true);
	}


	public void mainMenuBtnClicked(ActionEvent actionEvent) {
		mainMenuBtn.getScene().setRoot(MainMenu.getMainMenuRoot());
	}


	public void addNameBtnClicked(ActionEvent actionEvent) {
//		if(noneSelected) {
//			Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
//			nonSelectedAlert.setTitle("ERROR - Please select input method");
//			nonSelectedAlert.setHeaderText(null);
//			nonSelectedAlert.setContentText("No input method selected. Please select an option from the dropdown menu");
//			nonSelectedAlert.showAndWait();
//		} else {
			
			if (selectedManual) {
				
			} else if (!selectedManual) {
				listOfNamesSelected.clear();
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open txt file");
				File selectedFile = fileChooser.showOpenDialog(addNameBtn.getScene().getWindow());
				
				
				if (selectedFile != null) {
					try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
						String line;
						while ((line = br.readLine()) != null) {
							listOfNamesSelected.add(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		//}
	}


	public void practiceBtnClicked(ActionEvent actionEvent) {
		if (listOfNamesSelected.isEmpty()) {
			Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
			nonSelectedAlert.setTitle("ERROR - Please select some names");
			nonSelectedAlert.setHeaderText(null);
			nonSelectedAlert.setContentText("No name(s) have been entered. Please enter at least one name to practice");
			nonSelectedAlert.showAndWait();
		} else {
			try {
				nameSelectionMenuRoot = practiceButton.getScene().getRoot();
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PracticeMenu.fxml"));
				Parent root = fxmlLoader.load();
				namesListView.getScene().setRoot(root);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed to open practice menu");
			}
		}
	}
	
	
	public void deleteBtnClicked(ActionEvent actionEvent) {
		namesObsList.add(new String[]{"Mike", "-", "John", " ", "Lee"});
	}


	public Parent getControllerRoot() {
		return nameSelectionMenuRoot;
	}


	public static List<String> getAddedList() {
		return listOfNamesSelected;
	}


	public void onInputSelected(ActionEvent actionEvent) {
		noneSelected = false;
		if(inputMethodChoice.getValue().equals("Manual input")){
			listOfNamesSelected.clear();
			selectedManual = true;
		} else if (inputMethodChoice.getValue().equals("Browse for text file")){
			selectedManual = false;
		}
	}
}
