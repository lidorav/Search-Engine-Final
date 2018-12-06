package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        this.primaryStage.setTitle("Search Engine");
        this.primaryStage.setScene(new Scene(root, 800, 600));
        this.primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
