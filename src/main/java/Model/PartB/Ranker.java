package Model.PartB;


import Model.PartA.Index.Dictionary;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * class for ranking doc relevance
 */
public class Ranker {
    //class field
    private final double b=0.2;
    private final double k=0.3;


    /**
     * ranking function
     * @param
     */
    public float rank(int N, double avgDl, Set<String> keySet, HashMap<String, HashMap<String, String[]>> queryDocs, String docID,int D) {
        // for each term in the query
        float rankResult = 0;
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
            float idf = idf(N, df);
            float BM25 = (float) ((idf * tf * (k + 1)) / (tf + k * (1 - b + (b * (D / avgDl)))));
            rankResult += (BM25 +(titleFactor * (BM25)) + (atTheBeginFactor *(BM25/2)));
        }
        return rankResult;
    }


    /**
     * Calculate IDF for a term in the corpus
     * @param N size of documents in the corpus
     * @param df the number of shows the term appear in the corpus
     * @return
     */
    public float idf(int N, int df){
        return (float) Math.log((N-df+0.5)/(df+0.5));
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
