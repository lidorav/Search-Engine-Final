package Model.PartB;


import Model.PartA.Index.Dictionary;

/**
 * class for ranking doc relevance
 */
public class Ranker {
    //class field
    private String path;
    private ReadDoc rd;
    private final double b=0.75;
    private final double k=2.0;

    /**
     * c'tor
     * @param path
     */
    public Ranker(String path) {
        this.path = path;
        this.rd = new ReadDoc(path);
        rd.readDoc();

    }

    /**
     * ranking function
     * @param queryTerms
     */
    public double rank(String[] queryTerms, int N, double avgDl, String[] doc, int D){
        // for each term in the query
        double rankResult = 0;
        for (int i=0; i<queryTerms.length; i++){
            int df = getDF(queryTerms[i]);
            int tf = Integer.valueOf(doc[1]);
            double idf = idf(N,df);
            rankResult += (idf*tf*(k+1))/(tf+k*(1-b+b*(D/avgDl)));
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
