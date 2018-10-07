package namesayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        initialise();
        launch(args);
    }
	
	
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/MainMenu.fxml"));
        primaryStage.setTitle("NameSayer - Main Menu");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    

    public static void initialise() {
        File archiveFolder = new File("Creations");
        if (!archiveFolder.exists()) {
            try {
                archiveFolder.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File ratingFile = new File("Bad_Ratings.txt");
        if(!ratingFile.exists()) {
            try {
                ratingFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

}
