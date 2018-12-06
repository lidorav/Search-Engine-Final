package Controller;

import Model.Model;

import java.util.TreeMap;

public class Controller {

    private Model model;

    public Controller(){
        model = Model.getInstance();
    }

    public void runEngine(String corpusPath, String postingPath, boolean toStemm) {
        model.initalizeModel(corpusPath,postingPath,toStemm);
    }

    public TreeMap showDictionary(){
        return model.showDictonary();
    }
}
