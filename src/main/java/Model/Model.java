package Model;

import Model.PartA.City;
import Model.PartA.Document;
import Model.PartA.Index.Dictionary;
import Model.PartA.Index.Indexer;
import Model.PartA.Parse.Parser;
import Model.PartA.Read.ReadFile;
import Model.PartB.Query;
import Model.PartB.ReadQuery;
import Model.PartB.Searcher;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * class that connect all the pieces together, all parts of the engine with different threads
 */
public class Model {
    private BlockingQueue<Document> queueA;
    private BlockingQueue<Document> queueB;
    private Indexer indexer;
    private ReadFile reader;
    private Parser parser;
    private Searcher searcher;
    private boolean isSingleQuery;

    private static Model instance;

    /**
     * Private C'tor initialize the blocking queue fields
     */
    private Model() {
        queueA = new LinkedBlockingQueue<>();
        queueB = new LinkedBlockingQueue<>();
    }

    /**
     * get the class instance (singleton)
     * @return class instance
     */
    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    /**
     * Initialize the model pieces with different threads and major the time elapsed
     * @param corpusPath the path to the corpus directory
     * @param postingPath the path to the posting directory
     * @param toStemm a boolean to stem or not
     * @return a string with information of the indexing process
     */
    public String initalizeModel(String corpusPath, String postingPath, boolean toStemm) {
        String res;
        reader = new ReadFile(corpusPath, queueA);
        parser = new Parser(queueA, queueB,toStemm,corpusPath);
        indexer = new Indexer(queueB,toStemm,postingPath);
        long startTime = System.nanoTime();
            Thread t1 = new Thread(reader);
            Thread t2 = new Thread(parser);
            Thread t3 = new Thread(indexer);
        t1.start();
        t2.start();
        t3.start();
        try{
        t3.join();
        t2.join();
        t1.join();
        } catch (Exception e) { }

        //starting consumer to consume messages from queue
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        res =  "Total Running Time: " + timeElapsed/1000000000 + "sec\n" + indexer.resultData();
        return res;
    }

    /**
     * get the dictionary sorted by term name
     * @return tree-map holding a sorted dictionary
     */
    public TreeMap showDictonary(){
        return Dictionary.getSorted();
    }

    /**
     * Clean all of the model objects
     */
    public void reset() {
        if(reader != null)
            reader.clear();
        if(parser != null)
            parser.clear();
        if(indexer != null)
            indexer.deleteFiles();
        instance = new Model();

    }

    /**
     * Load data from file to the dictionary hash-map
     * @param stemSelected a boolean that indicates if loading the dictionary stemmed or not
     * @param postingPath the path of the posting directory
     * @return a string that indicates if the loading was successful or not.
     */
    public String loadDictionary(boolean stemSelected, String postingPath) {
        City.loadFromFile(stemSelected,postingPath);
        indexer = new Indexer(queueB,stemSelected,postingPath);
        return indexer.loadDictionary();
    }


    public void searchQuery(boolean selected, boolean toEntity,boolean toSemant, String postingPath, String query, ObservableList items) {
        isSingleQuery = true;
        searcher = new Searcher(postingPath,toEntity,selected,toSemant);
        searcher.search(query,new ArrayList<String>(items));
    }

    public Set<String> getCities() {
        return City.getCityList();
    }

    public TreeMap<String, Set<String>> getDocResults() {
        if(isSingleQuery) {
            TreeMap<String, Set<String>> map = new TreeMap<>();
            map.put("1", searcher.getResults());
            return map;
        }
        else{
            return searcher.getQueriesResults();
        }
    }

    public String saveResults(File selectedDirectory) {
        if(isSingleQuery)
            return searcher.printMap(selectedDirectory);
        else
            return searcher.printMaps(selectedDirectory);
    }

    public void searchQueries(String queriesPath,String postingPath, boolean toEntity, boolean selected,boolean toSemant, ObservableList items){
        isSingleQuery = false;
        ReadQuery rq = new ReadQuery(queriesPath);
        List<Query> queries = rq.read();
        searcher = new Searcher(postingPath,toEntity,selected,toSemant);
        searcher.searchList(queries,new ArrayList<String>(items));
    }
}
