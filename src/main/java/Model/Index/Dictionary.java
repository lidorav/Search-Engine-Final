package Model.Index;

import Model.PreTerm;
import Model.PostTerm;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for holding the overall dictionary of the application
 * the class give all the services that concern with the dictionary
 */
public class Dictionary {

    private String folder;
    private String path;
    private static ConcurrentHashMap<String, PostTerm> dictionary;
    private int countNum = 0;

    /**
     * C'tor initialize the class fields
     * @param path a path to write the dictionary file
     * @param toStemm a boolean that indicates
     */
    public Dictionary(String path, boolean toStemm) {
        this.path = path;
        folder = getFolderName(toStemm);
        dictionary = new ConcurrentHashMap<>();
    }

    /**
     * determine if the folder is stemmed or notStemmed by the given boolean
     * @param stemm a boolean parameter if to stemm or not
     * @return a string true = stemmed / false = notStemmed
     */
    private String getFolderName(boolean stemm){
        if (stemm)
            return "Stemmed";
        return "notStemmed";
    }

    /**
     * Static method to get a the dictionary in a sorted by the key in lexicographic
     * @return a tree-map sorted by terms
     */
    public static TreeMap<String, String> getSorted() {
        TreeMap sortedDic = new TreeMap();
        for (Map.Entry<String,PostTerm> entry: dictionary.entrySet()) {
            sortedDic.put(entry.getKey(),String.valueOf(entry.getValue().getDf()));
        }
        return sortedDic;
    }

    /**
     * Check if a given term is exists in the dictionary
     * @param term a given term
     * @return true if the term exists, false otherwise
     */
    boolean isInDictionary(String term) {
        return dictionary.containsKey(term);
    }

    /**
     * Add a new term to the dictionary
     * @param preTerm a given preTerm object
     */
    void addNewTerm(PreTerm preTerm) {
        //temp just for part2
        boolean flag = true;
        String str = preTerm.getName().replaceAll("[ ./KMBkmbt+/-]","");
        for(int i=0;i<str.length();i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9')
                flag = false;
        }
        if(flag)
            countNum++;
        //
        dictionary.put(preTerm.getName(), new PostTerm(preTerm));
    }

    public PostTerm getTerm(String term) {
        return dictionary.get(term);
    }

    /**
     * update the term in the dictionary with extra information
     * @param preTerm a given preTerm object
     */
    public synchronized void updateTerm(PreTerm preTerm) {
        try {
            PostTerm pterm = dictionary.get(preTerm.getName());
            pterm.increaseTf(preTerm.getTf());
            pterm.increaseDf();
        }catch (NullPointerException e){
            PostTerm pterm = dictionary.get(preTerm.getName().toLowerCase());
            pterm.increaseTf(preTerm.getTf());
            pterm.increaseDf();
        }
        //add values by ptr
    }

    /**
     * Write the dictionary into a file in the given path
     */
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

    /**
     * Check if a given term is exists in the dictionary
     * @param token a given term
     * @return true if the term exists, false otherwise
     */
    public static boolean checkExist(String token) {
        return dictionary.containsKey(token);
    }

    /**
     * Replace the name of a term in the dictionary with a different name
     * @param currentTerm the current name of a term
     * @param newTerm the new name of a term
     */
    public static void replaceTerm(String currentTerm, String newTerm) {
        PostTerm term = dictionary.get(currentTerm);
        term.setName(newTerm);
        dictionary.remove(currentTerm);
        dictionary.put(newTerm, term);
    }

    /**
     * Add pointer (line in posting file) to a given term
     * @param term the given term in dictionary
     * @param index the line number in the file
     * @return the term as he exists in the dictionary
     */
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

    /**
     * Clear the dictionary data
     */
    public void clearDic(){
        dictionary.clear();
    }

    /**
     * Load data from file to the dictionary hash-map
     * @return a string that indicates if the loading was successful or not
     */
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

    /**
     * Get the number of unique terms in the dictionary
     * @return the size of the dictionary
     */
    public int getSize() {
        return dictionary.size();
    }
}