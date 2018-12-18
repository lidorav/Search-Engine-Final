package Controller;

import Model.Model;

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

    public void searchQuery(boolean selected, String postingPath, String query) {
        model.searchQuery(selected,postingPath,query);
    }

    public Set<String> getCities() {
        model.getCities();
        
    }
}
