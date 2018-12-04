package Model.Index;

import Model.PreTerm;
import Model.PostTerm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {

    private String path;
    private static ConcurrentHashMap<String, PostTerm> dictionary;

    public Dictionary(String path){
        this.path = path;
        dictionary = new ConcurrentHashMap<>();
    }

    boolean isInDictionary(String term) {
        return dictionary.containsKey(term);
    }

    void addNewTerm(PreTerm preTerm) {
        dictionary.put(preTerm.getName(), new PostTerm(preTerm));
    }

    public PostTerm getTerm(String term) {
        return dictionary.get(term);
    }

    void updateTerm(PreTerm preTerm) {
        PostTerm pterm = dictionary.get(preTerm.getName());
        pterm.increaseTf(preTerm.getTf());
        pterm.increaseDf();
        //add values by ptr
    }

    void printDic() {
        PrintWriter outputfile = null;
        try {
            outputfile = new PrintWriter(path+"\\Dic.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (PostTerm p : dictionary.values()
                ) {
            outputfile.println(p);
        }
        outputfile.close();
    }

    public static boolean checkExist(String token) {
        return dictionary.containsKey(token);
    }

    public static void replaceTerm(String currentTerm, String newTerm) {
        PostTerm term = dictionary.get(currentTerm);
        term.setName(newTerm);
        dictionary.remove(currentTerm);
        dictionary.put(newTerm, term);
    }
}
