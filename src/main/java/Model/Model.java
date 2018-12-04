package Model;

import Model.Index.Indexer;
import Model.Parse.Parser;
import Model.Read.ReadFile;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Model {
    private BlockingQueue<Document> queueA;
    private BlockingQueue<ConcurrentHashMap<String, PreTerm>> queueB;

    public Model() {
        queueA = new LinkedBlockingQueue<>();
        queueB = new LinkedBlockingQueue<>();
    }

    public void initalizeModel(String corpusPath, String postingPath, boolean toStemm) {
        ReadFile reader = new ReadFile("D:\\corpus", queueA);

        Parser parser = new Parser(queueA, queueB);
        Indexer indexer = new Indexer(queueB);
        long startTime = System.nanoTime();
        Thread t1 = new Thread(reader);
        Thread t2 = new Thread(parser);
        Thread t3 = new Thread(indexer);
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (Exception e) {
        }

        //starting consumer to consume messages from queue
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        indexer.printDic();

        System.out.println("Execution time in sec : " +
                timeElapsed / 1000000000);
    }
}
