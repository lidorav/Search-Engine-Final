package View;

import Controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class DictionaryView implements Initializable {
    @FXML
    private TableView<TableDataType> tbl_view;

    @FXML
    private TableColumn<TableDataType,String> tbl_col1;
    @FXML
    private TableColumn<TableDataType,String> tbl_col2;

    private Controller controller;

    public DictionaryView() {
        controller = new Controller();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeMap<String, String> dictionary = controller.showDictionary();
        tbl_col1.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbl_col2.setCellValueFactory(new PropertyValueFactory<>("data"));
        try {
            for (Map.Entry<String, String> entry : dictionary.entrySet())
                tbl_view.getItems().add(new TableDataType(entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
