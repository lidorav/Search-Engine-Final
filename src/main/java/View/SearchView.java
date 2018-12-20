package View;

import Controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class SearchView implements Initializable {
    private Controller controller;

    @FXML
    private ListView resView;

    public SearchView() {
        controller = new Controller();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ObservableList<String> observableList = FXCollections.observableArrayList();
            Set<String> results = controller.getDocResults();
            observableList.addAll(results);
            resView.getItems().addAll(observableList);
        }catch (Exception e){
        }
    }
}


