package Model.PartB;


import Model.PartA.Index.Dictionary;
import Model.PartA.Parse.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Searcher {

    private HashMap<String,Double> docMap;
    private Ranker ranker;
    private Parser parser;
    private ReadDoc rd;

    public Searcher(String path, boolean toStemm){
        docMap = new HashMap<>();
        ranker = new Ranker(path);
        rd = new ReadDoc(path,toStemm);
        rd.readDoc();
        parser = new Parser(toStemm);
    }

    /**
     * Search
     * @param query
     */
    public void search(String query) {
        double rank = 0;
        int N = rd.getDocAmount();
        double avgDl = rd.getAvgDl();
        List<String> queryTerms = parser.parseQuery(query);
        for (int i = 0; i < queryTerms.size(); i++) {
            int ptr = getPointer(queryTerms.get(i));
            if (ptr == -1)
                continue;
            String[] doc = rd.readDocFromPosting(getFileName(queryTerms.get(i)), ptr);
            if (doc != null) {
                for (int j = 0; j < doc.length; j++) {
                    String[] docParts = doc[j].split(";");
                    String[] docInfo = rd.readDocLine(Integer.valueOf(docParts[1]));
                    rank = ranker.rank(queryTerms, N, avgDl, docParts,Integer.valueOf(docInfo[4]));
                    docMap.put(docParts[0], rank);
                }
            }
        }
    }

    /**
     * print
     */
    public void printMap(){
        for(Map.Entry<String,Double> entry:docMap.entrySet()){
            System.out.println(entry);
        }
    }

    /**
     * get pointer of a term in the posting file
     * @param term
     * @return int ptr
     */
    private int getPointer (String term){
        return Dictionary.getPointer(term);
    }

    /**
     * Get the posting filename from a given term by calculating it's first character
     * @param term the line in the merged posting file
     * @return the filename the line is associated
     */
    private String getFileName(String term) {
        char c = term.toLowerCase().charAt(0);
        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))
            return String.valueOf(c);
        return "symbol";
    }
}
