package Model.PartB;


import Model.PartA.Index.Dictionary;
import Model.PartA.Parse.Parser;

import java.util.HashMap;
import java.util.List;

public class Searcher {

    private HashMap<String,Integer> docMap;
    private String path;
    private Ranker ranker;
    private Parser parser;
    private ReadDoc rd;

    public Searcher(String path){
        this.path = path;
        docMap = new HashMap<>();
        ranker = new Ranker(path);
        rd = new ReadDoc(path);
        //parser = new Parser()
    }

    public void search(String query) {
        List<String> queryTerms = parser.parseQuery(query);
        for(int i=0;i<queryTerms.size();i++) {
            int ptr = getPointer(queryTerms[i]);
            String[] docsInfo = rd.readDocFromPosting(getFileName(queryTerms[i]), ptr);
            int N = rd.getDocAmount();
            double avgDl = rd.getAvgDl();
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
