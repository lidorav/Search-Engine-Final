package Model.PartB;


import Model.PartA.Index.Dictionary;
import Model.PartA.Parse.Parser;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import de.linguatools.disco.CorruptConfigFileException;
import de.linguatools.disco.DISCO;
import de.linguatools.disco.DISCOLuceneIndex;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Searcher {

    private Map<String, Double> docMap;
    private HashMap<String, HashMap<String, String[]>> queryDocs;
    private TreeMap<String, Set<String>> queriesResults;
    private Ranker ranker;
    private Parser parser;
    private ReadDoc rd;
    private boolean toEntity;
    private DISCO disco;

    public Searcher(String path, boolean toEntity, boolean toStemm) {
        docMap = new HashMap<>();
        ranker = new Ranker(path);
        rd = new ReadDoc(path, toStemm);
        this.toEntity = toEntity;
        rd.readDoc();
        parser = new Parser(toStemm);
        queryDocs = new HashMap<>();
        loadSimFile();

    }

    private void loadSimFile(){
        try {
            disco = DISCO.load("C:\\Users\\nkutsky\\IdeaProjects\\Search-Engine-Final\\src\\main\\resources\\enwiki-20130403-word2vec-lm-mwl-lc-sim.denseMatrix");
        }catch (IOException | CorruptConfigFileException e){
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * Search
     *
     * @param query
     * @param items
     */
    public void search(String query, ArrayList<String> items) {
        double rank;
        int N = rd.getDocAmount();
        double avgDl = rd.getAvgDl();
        List<String> queryTerms = parser.parseQuery(query);
        addSemanticTerms(queryTerms);
        initializeQueryMap(queryTerms);
        for (Map.Entry<String, HashMap<String, String[]>> mapEntry : queryDocs.entrySet()) {
            for (Map.Entry<String, String[]> entry : mapEntry.getValue().entrySet()) {
                try {
                    rank = ranker.rank(N, avgDl, queryDocs.keySet(), queryDocs, entry.getKey(), Integer.valueOf(entry.getValue()[9]));
                    docMap.put(entry.getKey(), rank);
                }catch (Exception e){
                    for(int i=0;i<entry.getValue().length;i++)
                        System.out.println(entry.getValue()[i]);
                }
            }
        }
        filterByCity(items);
        getTopScore();
    }

    /**
     * create a new map that contains onky relevant key
     *
     * @param items
     */
    private void filterByCity(ArrayList<String> items) {
        if (items.isEmpty())
            return;
        List<String> filterdDoc = rd.readCities(items);
        HashMap<String, Double> filteredDocMap = new HashMap<>();
        for (String doc : filterdDoc) {
            if (docMap.containsKey(doc))
                filteredDocMap.put(doc, docMap.get(doc));
        }
        docMap = filteredDocMap;
    }

    private void addSemanticTerms(List<String>terms){
        try {
            for(String term:terms) {
                if (Dictionary.checkExist(term)) {
                    Map<String, Float> wordVector = disco.getWordvector(term);
                    // get word embedding for "Haus" as float array
                    for (Map.Entry<String, Float> entry : wordVector.entrySet())
                        System.out.println(entry);
                }
            }
        }catch (IOException e){}
    }

    private void initializeQueryMap(List<String> queryTerms) {
        for (int i = 0; i < queryTerms.size(); i++) {
            int ptr = getPointer(queryTerms.get(i));
            if (ptr == -1)
                continue;
            String[] doc = rd.readDocFromPosting(getFileName(queryTerms.get(i)), ptr);
            if (doc != null) {
                queryDocs.put(queryTerms.get(i), new HashMap<>());
                for (int j = 0; j < doc.length; j++) {
                    String[] docParts = doc[j].split(";");
                    String[] docInfo = rd.readDocLine(Integer.valueOf(docParts[1]));
                    String[] both = concatArrays(docParts,docInfo);
                    queryDocs.get(queryTerms.get(i)).put(docInfo[0], both);
                }
            }
        }
        rd.closeAccess();
    }

    private String[] concatArrays(String[] arrayA, String[] arrayB){
        String[] res = new String[arrayA.length+arrayB.length];
        for(int i=0;i<arrayA.length;i++){
            res[i] = arrayA[i];
        }
        for(int j=0;j<arrayB.length;j++){
            res[arrayA.length+j] = arrayB[j];
        }
        return res;
    }

    /**
     * print
     *
     * @param selectedDirectory
     */
    public String printMap(File selectedDirectory) {
        PrintWriter outputfile = null;
        try {
            outputfile = new PrintWriter(selectedDirectory + "\\results.txt");
            for (Map.Entry<String, Double> entry : docMap.entrySet()) {
                String doc;
                if(entry.getKey().contains("entities"))
                    doc = entry.getKey().substring(0,entry.getKey().indexOf(" "));
                else
                    doc = entry.getKey();
                outputfile.println("1 1 " + doc + " 1 42.0 mt");
            }
            outputfile.close();
            return "Saved Successfully";
        } catch (Exception e) {
            return "Error in Saving";
        }
    }

    /**
     *
     * @param selectedDirectory
     * @return
     */
    public String printMaps(File selectedDirectory) {
        int i;
        int bounder = 50;
        PrintWriter outputfile = null;
        try {
            outputfile = new PrintWriter(selectedDirectory + "\\results.txt");
            for (Map.Entry<String, Set<String>> entry : queriesResults.entrySet()) {
                i=0;
                for (String doc : entry.getValue()) {
                    if (i >= bounder)
                        break;
                    if(doc.contains("entities"))
                        doc = doc.substring(0,doc.indexOf(" "));
                    outputfile.println(entry.getKey() + " 0 " + doc + " 0 0 mt");
                    i++;
                }
            }
            outputfile.close();
            return "Saved Successfully";
        } catch (Exception e) {
            return "Error in Saving";
        }
    }

    /**
     * get pointer of a term in the posting file
     *
     * @param term
     * @return int ptr
     */
    private int getPointer(String term) {
        return Dictionary.getPointer(term);
    }

    /**
     * Get the posting filename from a given term by calculating it's first character
     *
     * @param term the line in the merged posting file
     * @return the filename the line is associated
     */
    private String getFileName(String term) {
        char c = term.toLowerCase().charAt(0);
        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))
            return String.valueOf(c);
        return "symbol";
    }

    private void getTopScore() {
        int i=0;
        Map<String, Double> sorted = docMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(e -> e.getValue())))
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        LinkedHashMap<String,Double> top50 = new LinkedHashMap();
        for(Map.Entry<String,Double> entry : sorted.entrySet()) {
            if (i >= 50)
                break;
            else {
                i++;
                top50.put(entry.getKey(), entry.getValue());
            }
        }
        docMap = top50;
    }

    public Set<String> getResults() {
        Set<String> res = new LinkedHashSet<>();
        if (toEntity) {
            for (String str : docMap.keySet()) {
                String entities = "entities: ";
                for (HashMap<String, String[]> map : queryDocs.values()) {
                    if (map.containsKey(str)) {
                        for (int i = 10; i < map.get(str).length; i++)
                            entities = entities + map.get(str)[i] + ",";
                        res.add(str + " " + entities);
                        break;
                    }
                }
            }
        } else {
            res = docMap.keySet();
        }
        return res;
    }

    public void searchList(List<Query> queries, ArrayList<String> items) {
        queriesResults = new TreeMap<>();
        for(Query query: queries){
            search(query.getTitle() + " " + query.getDescription(),items);
            Set<String> docResults = getResults();
            queriesResults.put(query.getQueryID(),docResults);
        }
    }

    public TreeMap<String,Set<String>> getQueriesResults(){ return queriesResults;
    }
}