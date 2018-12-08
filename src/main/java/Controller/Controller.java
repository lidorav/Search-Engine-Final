package Controller;

import Model.Model;

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
}
