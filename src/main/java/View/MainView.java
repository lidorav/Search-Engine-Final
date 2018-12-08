package View;

import Controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainView implements Initializable {

    @FXML
    private TextField corpus_txt;
    @FXML
    private TextField posting_txt;
    @FXML
    private CheckBox stem_chk;
    @FXML
    private ChoiceBox lang_box;
    @FXML
    private SplitPane mainWindow;

    private Controller controller;

    public MainView() {
        controller = new Controller();
    }

    public void openCorpusBrowser(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null){
                corpus_txt.setText(selectedDirectory.getAbsolutePath());
            }
    }

    public void openPostingBrowser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());
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
            String msg = controller.runEngine(corpusPath,postingPath,toStemm);
            showInformationAlert(msg);
        }
    }

    private void showErrorAlert(String stringAlert){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setContentText(stringAlert);
        alert.showAndWait();
    }

    private void showInformationAlert(String stringAlert){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setContentText(stringAlert);
        alert.show();
    }

    public void showDictionary() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getClassLoader().getResource("dictionary.fxml"));
            Stage newWindow = new Stage();
            newWindow.setTitle("Dictionary");
            newWindow.setScene(new Scene(root));
            Main main = Main.getInstance();
            // Specifies the modality for new window.
            newWindow.initModality(Modality.WINDOW_MODAL);

            // Specifies the owner Window (parent) for new window
            newWindow.initOwner(main.getPrimaryStage());

            newWindow.show();
        }catch (IOException e) {
            showErrorAlert("No Data");
        }
    }

    public void getLanguages(){
        List<String> myList;
        try {
            myList = Files.lines(Paths.get(getClass().getClassLoader().getResource("languages.txt").toURI())).collect(Collectors.toList());
            lang_box.setItems(FXCollections.observableArrayList(myList));
        } catch (IOException e) {
            System.out.println(e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void resetApp(){
        controller.resetApp();
        stem_chk.setSelected(false);
        corpus_txt.clear();
        posting_txt.clear();
    }

    public void loadDictionary(){
        String postingPath = posting_txt.getText();
        if(postingPath.isEmpty()){
            showErrorAlert("Directory Not Found");
        }
        else {
            String msg = controller.loadDictionary(stem_chk.isSelected(), postingPath);
            showInformationAlert(msg);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getLanguages();
        SplitPane.Divider divider = mainWindow.getDividers().get(0);
        divider.positionProperty().addListener((observable, oldvalue, newvalue) -> divider.setPosition(0.1));
    }
}
