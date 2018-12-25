package View;

import Controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class SearchView extends AView implements Initializable {
    private Controller controller;
    private Map<String, Set<String>> docResults;
    private final int BOUND = 50;
    @FXML
    private ListView resView;
    @FXML
    private ComboBox query_id_cmb;


    public SearchView() {
        controller = new Controller();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        docResults = controller.getDocResults();
        for (String queryID : docResults.keySet()) {
            query_id_cmb.getItems().add(queryID);
        }
    }

    public void selectQuery(){
        int i=0;
        try{
        ObservableList<String> observableList = FXCollections.observableArrayList();
        String selectedQueryID = query_id_cmb.getSelectionModel().getSelectedItem().toString();
        Set<String> results = docResults.get(selectedQueryID);
        for(String res : results){
            if(i<BOUND) {
                observableList.add(res);
                i++;
            }
            else
                break;
        }
        resView.getItems().clear();
        resView.getItems().addAll(observableList);
    }catch (Exception e){}
    }

    public void saveResults(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            String msg = controller.saveResults(selectedDirectory);
            showInformationAlert(msg);
        }
    }
}


