package Controller;

import Model.Model;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Controller {

    private Model model;

    public Controller(){
        model = Model.getInstance();
    }

    public String runEngine(String corpusPath, String postingPath, boolean toStemm) {
        return model.initalizeModel(corpusPath,postingPath,toStemm);
    }

    public TreeMap showDictionary(){
        return model.showDictonary();
    }

    public void resetApp() {
        model.reset();
    }

    public String loadDictionary(boolean stemSelected, String postingPath) {
        return model.loadDictionary(stemSelected,postingPath);
    }

    public void searchQuery(boolean selected, boolean toEntity, String postingPath, String query, ObservableList items) {
        model.searchQuery(selected,toEntity,postingPath,query,items);
    }

    public Set<String> getCities() {
       return model.getCities();
    }

    public Map<String, Set<String>> getDocResults(){
        return model.getDocResults();
    }

    public String saveResults(File selectedDirectory) {
        return model.saveResults(selectedDirectory);
    }

    public void searchQueries(boolean selected, boolean toEntity, String postingPath, String queryPath, ObservableList checkedItems) {
        model.searchQueries(queryPath,postingPath,toEntity,selected,checkedItems);
    }
}
