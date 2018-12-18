package Model.PartB;


import Model.PartA.Index.Dictionary;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.D;

/**
 * class for ranking doc relevance
 */
public class Ranker {
    //class field
    private String path;
    private final double b=0.75;
    private final double k=2.0;

    /**
     * c'tor
     * @param path
     */
    public Ranker(String path) {
        this.path = path;
    }

    /**
     * ranking function
     * @param
     */
    public double rank(int N, double avgDl, Set<String> keySet, HashMap<String, HashMap<String, String[]>> queryDocs, String docID,int D) {
        // for each term in the query
        double rankResult = 0;
        Iterator<String> entries = keySet.iterator();
        while (entries.hasNext()) {
            String term = entries.next();
            int df = getDF(term);
            int tf = 0;
            int titleFactor = 0;
            int atTheBeginFactor = 0;
            if (queryDocs.get(term).containsKey(docID)) {
                titleFactor = Integer.valueOf(queryDocs.get(term).get(docID)[3]);
                atTheBeginFactor = Integer.valueOf(queryDocs.get(term).get(docID)[4]);
                tf = Integer.valueOf(queryDocs.get(term).get(docID)[2]);
            }
            double idf = idf(N, df);
            double BM25 = (idf * tf * (k + 1)) / (tf + k * (1 - b + b * (D / avgDl)));
            rankResult += BM25 + titleFactor * (BM25/2) + atTheBeginFactor *(BM25/4);
        }
        return rankResult;
    }


    /**
     *
     * @param N
     * @param df
     * @return
     */
    public double idf(int N, int df){
        return Math.log10((N-df+0.5)/(df+0.5));
    }


    /**
     * get df number of a term in the corpus
     * @param term
     * @return int df
     */
    private int getDF(String term){
        return Dictionary.getDf(term);
    }
}
