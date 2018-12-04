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
    private TreeMap<String, StringBuilder> tempPost;
    private BlockingQueue<ConcurrentHashMap<String, PreTerm>> parser_indexer;
    private Dictionary dictionary;
    private Posting posting;
    private AtomicInteger counter;

    public Indexer(BlockingQueue bq) {
        posting = new Posting();
        parser_indexer = bq;
        tempPost = new TreeMap<>();
        dictionary = new Dictionary();
        docPost = new TreeMap<>();
        counter = new AtomicInteger(0);
    }

    @Override
    public void run() {
        try {
            ConcurrentHashMap<String, PreTerm> tempDic;
            //con suming messages until exit message is received
            while (!((tempDic = parser_indexer.take()).isEmpty())) {
                int i = counter.incrementAndGet();
                for (Map.Entry<String, PreTerm> entry : tempDic.entrySet()) {
                    if(i==2000) {
                        posting.initTempPosting(tempPost);
                        tempPost = new TreeMap<>();
                        i=0;
                        counter.set(0);
                        //System.gc();
                    }
                    PreTerm preTerm = entry.getValue();
                    if(!docPost.containsKey(preTerm.getDocID())){
                        Document doc = ReadFile.getDoc(preTerm.getDocID());
                        StringBuilder sb = doc.getDocInfo();
                        docPost.put(preTerm.getDocID(),sb.append("\r\n"));
                        ReadFile.removeDoc(entry.getKey());
                        doc = null;
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
        posting.mergePosting();
    }

    private boolean isInTempPosting(String key) {
        return tempPost.containsKey(key);
    }

    public void printDic(){dictionary.printDic();}


}
