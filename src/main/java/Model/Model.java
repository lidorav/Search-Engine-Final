package Model;

import Model.Index.Dictionary;
import Model.Index.Indexer;
import Model.Parse.Parser;
import Model.Read.ReadFile;

import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Model {
    private BlockingQueue<Document> queueA;
    private BlockingQueue<Document> queueB;
    private Indexer indexer;
    private ReadFile reader;
    private Parser parser;

    private static Model instance;

    private Model() {
        queueA = new LinkedBlockingQueue<>();
        queueB = new LinkedBlockingQueue<>();
    }
    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public void initalizeModel(String corpusPath, String postingPath, boolean toStemm) {
        reader = new ReadFile(corpusPath, queueA);
        parser = new Parser(queueA, queueB,toStemm,corpusPath);
        indexer = new Indexer(queueB,toStemm,postingPath);
        long startTime = System.nanoTime();
        try {
            Thread t1 = new Thread(reader);
            Thread t2 = new Thread(parser);
            Thread t3 = new Thread(indexer);

        t1.start();
        t2.start();
        t3.start();
        t3.join();
        t2.join();
        t1.join();
        } catch (Exception e) {
            System.out.println(e + "Error");
        }

        //starting consumer to consume messages from queue
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        indexer.printDic();

        System.out.println("Execution time in sec : " +
                timeElapsed / 1000000000);
    }

    public TreeMap showDictonary(){
        return Dictionary.getSorted();
    }
}
