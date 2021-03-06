package View;

import Controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

public class MainView extends AView implements Initializable {

    @FXML
    private TextField corpus_txt;
    @FXML
    private TextField posting_txt;
    @FXML
    private CheckBox stem_chk;
    @FXML
    private CheckBox entity_chk;
    @FXML
    private CheckBox semant_chk;
    @FXML
    private ChoiceBox lang_box;
    @FXML
    private SplitPane mainWindow;
    @FXML
    private TextField query_txt;
    @FXML
    private CheckComboBox city_ccb;
    @FXML
    private TextField queries_txt;

    private Controller controller;
    private File root;

    public MainView() {
        controller = new Controller();
        this.root = new File("./");
    }

    public void openCorpusBrowser(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null){
                corpus_txt.setText(selectedDirectory.getAbsolutePath());
            }
    }

    public void showCities(){
        try {
            ObservableList<String> observableList = FXCollections.observableArrayList();
            Set<String> citySet = controller.getCities();
            observableList.addAll(citySet);
            city_ccb.getItems().addAll(observableList);
        }catch (Exception e){
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

    private void showDocResults(){
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getClassLoader().getResource("searchResults.fxml"));
            Stage newWindow = new Stage();
            newWindow.setTitle("Search Results");
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

    public void getLanguages() {
        ArrayList<String> arr = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(root.getAbsoluteFile()+"\\src\\main\\resources\\languages.txt"))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                arr.add(sCurrentLine);
            }
            lang_box.setItems(FXCollections.observableArrayList(arr));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetApp(){
        controller.resetApp();
        stem_chk.setSelected(false);
        entity_chk.setSelected(false);
        corpus_txt.clear();
        posting_txt.clear();
        showInformationAlert("Reset Successfully");
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

    public void searchQuery(){
        String query = query_txt.getText();
        String postingPath = posting_txt.getText();
        boolean toEntity = entity_chk.isSelected();
        boolean toSemant = semant_chk.isSelected();
        if(query.isEmpty()){
            showErrorAlert("Query Is Empty");
        }
        else if(postingPath.isEmpty())
            showErrorAlert("Posting Directory Not Found");
        else{
            controller.searchQuery(stem_chk.isSelected(),toEntity,toSemant,postingPath,query,city_ccb.getCheckModel().getCheckedItems());
            showDocResults();
        }
    }

    public void loadQueries(){
        FileChooser directoryChooser = new FileChooser();
        File selectedFile = directoryChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            queries_txt.setText(selectedFile.getAbsolutePath());
        }
    }

    public void searchQueries(){
        String queryPath = queries_txt.getText();
        String postingPath = posting_txt.getText();
        boolean toEntity = entity_chk.isSelected();
        boolean toSemant = semant_chk.isSelected();
        if(queryPath.isEmpty()){
            showErrorAlert("Query Path File Is Empty");
        }
        else if(postingPath.isEmpty())
            showErrorAlert("Posting Directory Not Found");
        else{
            controller.searchQueries(stem_chk.isSelected(),toEntity,toSemant,postingPath,queryPath,city_ccb.getCheckModel().getCheckedItems());
            showDocResults();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getLanguages();
        SplitPane.Divider divider = mainWindow.getDividers().get(0);
        divider.positionProperty().addListener((observable, oldvalue, newvalue) -> divider.setPosition(0.1));
        city_ccb.addEventHandler(ComboBox.ON_SHOWING,event -> showCities());
    }
}
