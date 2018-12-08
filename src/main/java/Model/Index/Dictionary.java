package Model.Index;

import Model.PreTerm;
import Model.PostTerm;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {

    private String folder;
    private String path;
    private static ConcurrentHashMap<String, PostTerm> dictionary;
    private int countNum = 0;

    public Dictionary(String path, boolean toStemm) {
        this.path = path;
        folder = getFolderName(toStemm);
        dictionary = new ConcurrentHashMap<>();
    }

    private String getFolderName(boolean stemm){
        if (stemm)
            return "Stemmed";
        return "notStemmed";
    }

    public static TreeMap<String, String> getSorted() {
        TreeMap sortedDic = new TreeMap();
        for (Map.Entry<String,PostTerm> entry: dictionary.entrySet()) {
            sortedDic.put(entry.getKey(),String.valueOf(entry.getValue().getDf()));
        }
        return sortedDic;
    }

    boolean isInDictionary(String term) {
        return dictionary.containsKey(term);
    }

    void addNewTerm(PreTerm preTerm) {
        //temp just for part2
        String str = preTerm.getName().replaceAll("[./]","");
        if(NumberUtils.isDigits(str))
            countNum++;
        //
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
        System.out.println(countNum);
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
    public void clearDic(){
        dictionary.clear();
    }

    public String load() {
        File file = new File(path+"\\"+folder+"\\Dic.txt");
            try {
                BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                dictionary.clear();
                String line, term, data;
                while((line = br.readLine())!= null){
                    String[] parts = line.split(":");
                    term = parts[0];
                    data = parts[1];
                    String[] dataParts = data.split(",");
                    PostTerm postTerm = new PostTerm(term,dataParts[0],dataParts[1],dataParts[2]);
                    dictionary.put(term,postTerm);
                }
                br.close();
                return "Dictionary Uploaded";
            } catch (IOException e) {
                return "File Not Found";
            }
        }

    public int getSize() {
        return dictionary.size();
    }
}