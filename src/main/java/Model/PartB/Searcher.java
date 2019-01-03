package Model.PartB;


import Model.PartA.Index.Dictionary;
import Model.PartA.Parse.Parser;
import de.linguatools.disco.CorruptConfigFileException;
import de.linguatools.disco.DISCO;
import de.linguatools.disco.WrongWordspaceTypeException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Class that manages the process of ranking documents by a given query / queries set
 */
public class Searcher {

    private Map<String, Float> docMap;
    private HashMap<String, HashMap<String, String[]>> queryDocs;
    private TreeMap<String, Set<String>> queriesResults;
    private Ranker ranker;
    private Parser parser;
    private ReadDoc rd;
    private boolean toEntity;
    private static DISCO disco;

    static {
        try {
            disco = DISCO.load("C:\\Users\\nkutsky\\IdeaProjects\\Search-Engine-Final\\src\\main\\resources\\enwiki-20130403-word2vec-lm-mwl-lc-sim.denseMatrix");
        } catch (IOException | CorruptConfigFileException e) {
            e.printStackTrace();
        }
    }
    private boolean toSemant;

    /**
     * C'tor creating a searching object
     * @param path a given path to the documents file
     * @param toEntity a given boolean to include entities in the results
     * @param toStemm classify the path to the correct folder stemmed/notStemmed
     * @param toSemant to include semantic terms in the query
     */
    public Searcher(String path, boolean toEntity, boolean toStemm, boolean toSemant) {
        ranker = new Ranker();
        rd = new ReadDoc(path, toStemm);
        this.toEntity = toEntity;
        rd.readDoc();
        parser = new Parser(toStemm);
        this.toSemant = toSemant;
    }


    /**
     * Search function responsible for fetching data from the different files.
     * By sending each document and query to the ranking class it receive the document score
     * and rank the top 50 documents
     *  @param query is a given query
     * @param items a list of cities to filter
     */
    public void search(String query, ArrayList<String> items) {
        float rank;
        docMap = new HashMap<>();
        queryDocs = new HashMap<>();
        int N = rd.getDocAmount();
        double avgDl = rd.getAvgDl();
        List<String> queryTerms = parser.parseQuery(query);
        if(toSemant) {
            String semanticAddition=addSemanticTerms(query);
            queryTerms.addAll(parser.parseQuery(semanticAddition));
        }
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
        docMap = getTopScore(50,docMap);
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
        HashMap<String, Float> filteredDocMap = new HashMap<>();
        for (String doc : filterdDoc) {
            if (docMap.containsKey(doc))
                filteredDocMap.put(doc, docMap.get(doc));
        }
        docMap = filteredDocMap;
    }

    private String addSemanticTerms(String query) {
        StringBuilder sb = new StringBuilder();
        String[] queryTerms = query.split(" ");
        try {
            for (String term : queryTerms) {
                if (getPointer(term) != -1) {
                    Map<String, Float> wordVector = disco.getSecondOrderWordvector(term);
                    if(wordVector != null) {
                        for (Map.Entry<String,Float> entry : wordVector.entrySet()) {
                            if(entry.getValue()>=0.69)
                                sb.append(entry.getKey().replace("_"," ")).append(" ");
                        }
                    }
                }
            }
        } catch (IOException | WrongWordspaceTypeException e) {
        }
        return sb.toString();
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
        PrintWriter outputFile;
        try {
            File file = new File(selectedDirectory + "\\results.txt");
            FileUtils.deleteQuietly(file);
            outputFile = new PrintWriter(file);
            for (Map.Entry<String, Float> entry : docMap.entrySet()) {
                String doc;
                if(entry.getKey().contains("entities"))
                    doc = entry.getKey().substring(0,entry.getKey().indexOf(" "));
                else
                    doc = entry.getKey();
                outputFile.println("1 1 " + doc + " 1 42.0 mt");
            }
            outputFile.close();
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
        PrintWriter outputFile;
        try {
            File file = new File(selectedDirectory + "\\results.txt");
            FileUtils.deleteQuietly(file);
            outputFile = new PrintWriter(file);
            for (Map.Entry<String, Set<String>> entry : queriesResults.entrySet()) {
                for (String doc : entry.getValue()) {
                    if(doc.contains("entities"))
                        doc = doc.substring(0,doc.indexOf(" "));
                    outputFile.println(entry.getKey() + " 0 " + doc + " 0 0 mt");
                }
            }
            outputFile.close();
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

    private Map<String,Float> getTopScore(int N, Map<String,Float> map) {
        int i=0;
        Map<String, Float> sorted = map
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(Map.Entry::getValue)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        LinkedHashMap<String,Float> topN = new LinkedHashMap();
        for(Map.Entry<String,Float> entry : sorted.entrySet()) {
            if (i >= N)
                break;
            else {
                i++;
                topN.put(entry.getKey(), entry.getValue());
            }
        }
        return topN;
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
            res.addAll(docMap.keySet());
        }
        return res;
    }

    public void searchList(List<Query> queries, ArrayList<String> items) {
        queriesResults = new TreeMap<>();
        for(Query query: queries){
            search(query.getTitle(),items);
            Set<String> docResults = getResults();
            queriesResults.put(query.getQueryID(),docResults);
        }
    }

    public TreeMap<String,Set<String>> getQueriesResults(){ return queriesResults;
    }
}