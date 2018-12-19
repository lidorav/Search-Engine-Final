package Model.PartB;


import Model.PartA.Index.Dictionary;
import Model.PartA.Parse.Parser;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Searcher {

    private Map<String, Double> docMap;
    private HashMap<String, HashMap<String, String[]>> queryDocs;
    private Ranker ranker;
    private Parser parser;
    private ReadDoc rd;

    public Searcher(String path, boolean toStemm) {
        docMap = new HashMap<>();
        ranker = new Ranker(path);
        rd = new ReadDoc(path, toStemm);
        rd.readDoc();
        parser = new Parser(toStemm);
        queryDocs = new HashMap<>();
    }

    /**
     * Search
     *  @param query
     * @param items
     */
    public void search(String query, ArrayList<String> items) {
        double rank = 0;
        int N = rd.getDocAmount();
        double avgDl = rd.getAvgDl();
        List<String> queryTerms = parser.parseQuery(query);
        initializeQueryMap(queryTerms);
        for(Map.Entry<String,HashMap<String,String[]>> mapEntry : queryDocs.entrySet()) {
            for (Map.Entry<String, String[]> entry : mapEntry.getValue().entrySet()) {
                rank = ranker.rank(N, avgDl,queryDocs.keySet(),queryDocs, entry.getKey(),Integer.valueOf(entry.getValue()[9]));
                docMap.put(entry.getKey(), rank);
            }
        }
        filterByCity(items);
        getTopScore();
    }

    private void filterByCity(ArrayList<String> items) {
        if(items.isEmpty())
            return;
        List<String> filterdDoc = rd.readCities(items);
        HashMap<String,Double> filteredDocMap = new HashMap<>();
        for(String doc:filterdDoc){
            if(docMap.containsKey(doc))
                filteredDocMap.put(doc,docMap.get(doc));
        }
        docMap = filteredDocMap;
    }

    private void initializeQueryMap(List<String> queryTerms) {
        for (int i = 0; i < queryTerms.size(); i++) {
            int ptr = getPointer(queryTerms.get(i));
            if (ptr == -1)
                continue;
            String[] doc = rd.readDocFromPosting(getFileName(queryTerms.get(i)), ptr);
            if (doc != null) {
                queryDocs.put(queryTerms.get(i),new HashMap<>());
                for (int j = 0; j < doc.length; j++) {
                    String[] docParts = doc[j].split(";");
                    String[] docInfo = rd.readDocLine(Integer.valueOf(docParts[1]));
                    String[] both = ArrayUtils.addAll(docParts, docInfo);
                    queryDocs.get(queryTerms.get(i)).put(docInfo[0],both);
                }
            }
        }
        rd.closeAccess();
    }

    /**
     * print
     */
    public void printMap() {
        int i=0;
        int bounder=50;
        PrintWriter outputfile = null;
        try {
            outputfile = new PrintWriter("test");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Double> entry : docMap.entrySet()) {
            if(i>=bounder)
                break;
            outputfile.println("351 1000 "+entry.getKey()+" "+entry.getValue()+" 42.0 mt");
            i++;
        }
        outputfile.close();
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

    private void getTopScore(){
        Map<String, Double> sorted = docMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(e->e.getValue())))
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        docMap = sorted;
    }

}
