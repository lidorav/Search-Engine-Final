package Model.PartA.Index;

import Model.PartA.Document;
import Model.PartA.PreTerm;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that responsible of indexing the given terms,
 * the class connect between Dictionary class and Posting class
 */
public class Indexer implements Runnable {
    private TreeMap<String,StringBuilder> docPost;
    private TreeMap<String,StringBuilder> cityPost;
    private TreeMap<String, StringBuilder> tempPost;
    private BlockingQueue<Document> parser_indexer;
    private Dictionary dictionary;
    private Posting posting;
    private AtomicInteger counter;
    private int numOfDocsIndex;

    /**
     * C'tor initialize all class fields
     * @param bq given Blocking Queue between parser and indexer
     * @param toStemm a boolean flag if to stem or not
     * @param postingPath a given path for the posting files.
     */
    public Indexer(BlockingQueue bq, boolean toStemm, String postingPath) {
        posting = new Posting(postingPath, toStemm);
        parser_indexer = bq;
        tempPost = new TreeMap<>();
        cityPost = new TreeMap<>();
        dictionary = new Dictionary(postingPath, toStemm);
        docPost = new TreeMap<>();
        counter = new AtomicInteger(0);
        numOfDocsIndex = 0;
    }

    /**
     * run method of the working thread
     */
    @Override
    public void run() {
        try {
            Document doc;
            //con suming messages until exit message is received
            while (!((doc = parser_indexer.take()).getFileName().equals("fin"))) {
                boolean newDoc = true;
                ConcurrentHashMap<String,PreTerm> tempDic = doc.getTermsInDoc();
                if(tempDic == null)
                    continue;
                int i = counter.incrementAndGet();
                numOfDocsIndex++;
                for (Map.Entry<String, PreTerm> entry : tempDic.entrySet()) {
                    if(i==5000) {
                        posting.initTempPosting(tempPost);
                        posting.writeDocIndex(docPost);
                        docPost = new TreeMap<>();
                        tempPost = new TreeMap<>();
                        i=0;
                        counter.set(0);
                    }
                    PreTerm preTerm = entry.getValue();
                    if(newDoc) {
                        addDocToDocIndex(doc);
                        addDocToCityIndex(doc);
                        newDoc=false;
                    }
                    if (isInTempPosting(entry.getKey())) {
                        StringBuilder sb = tempPost.get(entry.getKey());
                        sb.append(preTerm.getDocID()).append(";").append(preTerm.getTf()).append(";").append(toChar(preTerm.getInTitle())).append(";").append(toChar(preTerm.getAtBeginOfDoc())).append(",");
                    } else {
                        //create new term in post
                        tempPost.put(entry.getKey(),
                                new StringBuilder().append(preTerm.getDocID()).append(";").append(preTerm.getTf()).append(";").append(toChar(preTerm.getInTitle())).append(";").append(toChar(preTerm.getAtBeginOfDoc())).append(","));
                    }
                    if (dictionary.isInDictionary(entry.getKey())) {
                        dictionary.updateTerm(preTerm);
                    } else {
                        dictionary.addNewTerm(preTerm);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(counter.get()>0) {
            posting.writeDocIndex(docPost);
            posting.initTempPosting(tempPost);
            docPost = new TreeMap<>();
            tempPost = new TreeMap<>();
        }
        posting.writeCityIndex(cityPost);
        posting.mergePosting();
        printDic();
    }

    /**
     * Add the doc city to the city Tree-map data structure
     * @param doc a given document object that contain city field
     */
    private void addDocToCityIndex(Document doc) {
        String city = doc.getCity();
        if(!city.isEmpty()) {
            StringBuilder sb;
            //if the tree-map contains the city then chain the doc info
            if (cityPost.containsKey(city)){
                sb = cityPost.get(city);
                sb.append(doc.getDocID()).append("[").append(doc.getCityOccurence()).append("],");
            }
            //if the tree-map doesn't contain the city, add new.
            else{
                sb = new StringBuilder(doc.getDocID());
                sb.append("[").append(doc.getCityOccurence()).append("],");
                cityPost.put(city,sb);
            }
        }
    }
    /**
     * Add the doc information to the documents Tree-map data structure
     * @param doc a given document object that contain information fields
     */
    private void addDocToDocIndex(Document doc) {
        StringBuilder sb = doc.getDocInfo();
        docPost.put(doc.getDocID(), sb);
    }

    /**
     * Check if a tree-map contains a given key
     * @param key a given string key
     * @return true if the key exists in the tree-map, false otherwise.
     */
    private boolean isInTempPosting(String key) {
        return tempPost.containsKey(key);
    }

    /**
     * Print dictionary to a file
     */
    public void printDic(){dictionary.printDic();}

    /**
     * Clear all class data structures
     */
    public void deleteFiles() {
        docPost.clear();
        cityPost.clear();
        tempPost.clear();
        posting.deletePosting();
        dictionary.clearDic();
    }

    /**
     * Activate the dictionary loading operation from the disk
     * @return a string with a proper message if the operation succeeded or not
     */
    public String loadDictionary() {
        return dictionary.load();
    }

    /**
     * Send information regarding the index operations
     * @return a string with the unique terms and documents being indexed.
     */
    public String resultData(){
        return "Num of Unique Terms: " + dictionary.getSize() +"\nNum of Indexed Docs: " + numOfDocsIndex;
    }

    /**
     * Convert a boolean to 0 or 1 for lowering the file size
     * @param b a given boolean parameter
     * @return a string false=0 / true = 1
     */
    private String toChar(final Boolean b) {
       if(b == true)
           return "1";
       else{
           return "0";
       }
    }
}
