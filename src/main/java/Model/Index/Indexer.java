package Model.Index;

import Model.Document;
import Model.PreTerm;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer implements Runnable {
    private TreeMap<String,StringBuilder> docPost;
    private TreeMap<String,StringBuilder> cityPost;
    private TreeMap<String, StringBuilder> tempPost;
    private BlockingQueue<Document> parser_indexer;
    private Dictionary dictionary;
    private Posting posting;
    private AtomicInteger counter;

    public Indexer(BlockingQueue bq, boolean toStemm, String postingPath) {
        posting = new Posting(postingPath, toStemm);
        parser_indexer = bq;
        tempPost = new TreeMap<>();
        cityPost = new TreeMap<>();
        dictionary = new Dictionary(postingPath, toStemm);
        docPost = new TreeMap<>();
        counter = new AtomicInteger(0);
    }

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
                        sb.append(preTerm.getDocID()).append("-").append(preTerm.getTf()).append(",");
                    } else {
                        //create new term in post
                        tempPost.put(entry.getKey(),
                                new StringBuilder().append(preTerm.getDocID()).append("-").append(preTerm.getTf()).append(","));
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
        posting.writeDocIndex(docPost);
        posting.initTempPosting(tempPost);
        posting.writeCityIndex(cityPost);
        posting.mergePosting();
    }

    private void addDocToCityIndex(Document doc) {
        String city = doc.getCity();
        if(!city.isEmpty()) {
            StringBuilder sb;
            if (cityPost.containsKey(city)){
                sb = cityPost.get(city);
                sb.append(doc.getDocID()).append("[").append(doc.getCityOccurence()).append("],");
            }
            else{
                sb = new StringBuilder(doc.getDocID());
                sb.append("[").append(doc.getCityOccurence()).append("],");
                cityPost.put(city,sb);
            }
        }
    }

    private void addDocToDocIndex(Document doc) {
        StringBuilder sb = doc.getDocInfo();
        docPost.put(doc.getDocID(), sb);
    }

    private boolean isInTempPosting(String key) {
        return tempPost.containsKey(key);
    }

    public void printDic(){dictionary.printDic();}


}
