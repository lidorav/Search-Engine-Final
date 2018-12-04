package View;

import Controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;

public class View {
    @FXML
    private Button corpus_btn ;
    @FXML
    private Button posting_btn;
    @FXML
    private TextField corpus_txt;
    @FXML
    private TextField posting_txt;
    @FXML
    private CheckBox stem_chk;


    private Stage primaryStage;
    private Controller controller;

    public View() {
        controller = new Controller();
    }


    public void openCorpusBrowser(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null){
                corpus_txt.setText(selectedDirectory.getAbsolutePath());
            }
    }

    public void openPostingBrowser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            posting_txt.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void runEngine(){
        String corpusPath = corpus_txt.getText();
        String postingPath = posting_txt.getText();
        boolean toStemm = stem_chk.isSelected();
        if(corpusPath.isEmpty() || postingPath.isEmpty()){
            showErrorAlert("Directory Not Found");
        }
        else{
            controller.runEngine(corpusPath,postingPath,toStemm);
        }
    }

    private void showErrorAlert(String stringAlert){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setContentText(stringAlert);
        alert.showAndWait();
    }

        public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
}
