package Model.PartB;


import Model.PartA.Index.Dictionary;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Class that responsible in the mathematical process of ranking the document by it relevance to the given query
 */
public class Ranker {
    //class field
    private final double b=0.2; // influence the ranking process
    private final double k=0.3; // influence the ranking process


    /**
     * Compute the ranking of a given document and a given query by a set of mathematical rules
     * @param N - the number of documents in the corpus
     * @param avgDl - the average length of all documents
     * @param keySet - a set of all query terms
     * @param queryDocs - HashMap with all the data of the documents
     * @param docID - the ID of the document being ranked
     * @param D the length of the document being ranked
     * @return the document rank as a decimal number
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
     * @return the idf score as decimal number
     */
    public float idf(int N, int df){
        return (float) Math.log((N-df+0.5)/(df+0.5));
    }


    /**
     * get df number of a term in the corpus
     * @param term a given term from the query
     * @return the DF for the term
     */
    private int getDF(String term){
        return Dictionary.getDf(term);
    }
}
