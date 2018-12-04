package Controller;

import Model.Model;

public class Controller {

    private Model model;

    public Controller(){
        model = new Model();
    }

    public void runEngine(String corpusPath, String postingPath, boolean toStemm) {
        model.initalizeModel(corpusPath,postingPath,toStemm);
    }
}
