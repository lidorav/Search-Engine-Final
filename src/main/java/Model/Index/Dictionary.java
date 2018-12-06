package Model.Index;

import Model.PreTerm;
import Model.PostTerm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Dictionary {

    private String folder;
    private String path;
    private static ConcurrentHashMap<String, PostTerm> dictionary;

    public Dictionary(String path, boolean toStemm) {
        this.path = path;
        if (toStemm)
            folder = "Stemmed";
        else
            folder = "notStemmed";
        dictionary = new ConcurrentHashMap<>();
    }

    public static TreeMap<String, String> getSorted() {
        TreeMap sortedDic = new TreeMap();
        for (Map.Entry<String,PostTerm> entry: dictionary.entrySet()) {
            sortedDic.put(entry.getKey(),entry.getValue().toString());
        }
        return sortedDic;
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
            outputfile = new PrintWriter(path + "\\" + folder + "\\Dic.txt");
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

    public static String addPtrToTerm(String term, int index) {
        String res = term;
        if (dictionary.containsKey(term)) {
            dictionary.get(term).setPtr(index);
        } else {
            if (dictionary.containsKey(term.toUpperCase())) {
                dictionary.get(term.toUpperCase()).setPtr(index);
                res = term.toUpperCase();
            }
            if (dictionary.containsKey(term.toLowerCase())) {
                dictionary.get(term.toLowerCase()).setPtr(index);
                res = term.toLowerCase();
            }
        }
        return res;
    }
}