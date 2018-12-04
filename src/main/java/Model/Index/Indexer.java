package Model.Index;

import Model.Document;
import Model.Read.ReadFile;
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
    private BlockingQueue<ConcurrentHashMap<String, PreTerm>> parser_indexer;
    private Dictionary dictionary;
    private Posting posting;
    private AtomicInteger counter;

    public Indexer(BlockingQueue bq, String postingPath) {
        posting = new Posting(postingPath);
        parser_indexer = bq;
        tempPost = new TreeMap<>();
        cityPost = new TreeMap<>();
        dictionary = new Dictionary(postingPath);
        docPost = new TreeMap<>();
        counter = new AtomicInteger(0);
    }

    @Override
    public void run() {
        try {
            ConcurrentHashMap<String, PreTerm> tempDic;
            //con suming messages until exit message is received
            while (!((tempDic = parser_indexer.take()).isEmpty())) {
                boolean newDoc = true;
                int i = counter.incrementAndGet();
                for (Map.Entry<String, PreTerm> entry : tempDic.entrySet()) {
                    if(i==2000) {
                        posting.initTempPosting(tempPost);
                        tempPost = new TreeMap<>();
                        i=0;
                        counter.set(0);
                    }
                    PreTerm preTerm = entry.getValue();
                    Document doc = ReadFile.getDoc(preTerm.getDocID());
                    if(newDoc) {
                        addDocToCityIndex(doc);
                        newDoc=false;
                    }
                    if(!docPost.containsKey(preTerm.getDocID())){
                        addDocToDocIndex(doc,preTerm);
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
        posting.initTempPosting(tempPost);
        posting.writeDocIndex(docPost);
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

    private void addDocToDocIndex(Document doc, PreTerm preTerm){
        StringBuilder sb = doc.getDocInfo();
        docPost.put(preTerm.getDocID(),sb);
        ReadFile.removeDoc(preTerm.getName());
        doc = null;
    }

    private boolean isInTempPosting(String key) {
        return tempPost.containsKey(key);
    }

    public void printDic(){dictionary.printDic();}


}
