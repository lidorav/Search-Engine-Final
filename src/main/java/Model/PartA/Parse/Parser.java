package Model.PartA.Parse;

import Model.PartA.Document;
import Model.PartA.PreTerm;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * a main class for parsing rules on the text
 */
public class Parser implements Runnable {
    protected static int index;
    private static List<String> tokenList;
    private static ConcurrentHashMap<String, PreTerm> tempDictionary;
    private StopWords stopWord;
    private BlockingQueue<Document> read_parse;
    private BlockingQueue<Document> indexer_parse;
    private static PorterStemmer porterStemmer;
    private static Pattern pattern;
    private Document doc;
    private boolean toStemm;
    private static final int bound = 100;

    /**
     * C'tor initialize the class parameters
     * @param bqA
     * @param bqB
     * @param toStemm
     * @param stopwordPath
     */
    public Parser(BlockingQueue bqA, BlockingQueue bqB, boolean toStemm, String stopwordPath) {
        stopWord = new StopWords(stopwordPath);
        porterStemmer = new PorterStemmer();
        read_parse = bqA;
        this.toStemm = toStemm;
        indexer_parse = bqB;
        pattern = Pattern.compile("[ \\*\\#\\|\\&\\(\\)\\[\\]:\\;\\!\\?\\{\\}]|-{2}|((?=[a-zA-Z]?)/(?=[a-zA-Z]))|((?<=[a-zA-Z])/(?=[\\d]))|((?=[\\d]?)/(?<=[a-zA-Z]))");
    }

    public Parser(boolean toStemm){
        this.toStemm = toStemm;
        porterStemmer = new PorterStemmer();
        pattern = Pattern.compile("[ \\*\\#\\|\\&\\(\\)\\[\\]:\\;\\!\\?\\{\\}]|-{2}|((?=[a-zA-Z]?)/(?=[a-zA-Z]))|((?<=[a-zA-Z])/(?=[\\d]))|((?=[\\d]?)/(?<=[a-zA-Z]))");
    }
    /**
     * run method of the working thread
     */
    public void run() {
        try {
            //consuming messages until exit message is received
            while(!(doc = read_parse.take()).getFileName().equals("fin")){
                String docID = doc.getDocID();
                String text = doc.getText();
                parse(docID,text,true);
                doc.cleanText(tempDictionary);
                }
                indexer_parse.put(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * main parse function
     * @param docID
     * @param text
     */
    public void parse(String docID, String text, boolean toIndex) {
        tempDictionary = new ConcurrentHashMap<>();
        index = 0;
        Splitter splitter = Splitter.on(pattern).omitEmptyStrings();
        tokenList = new ArrayList<>(splitter.splitToList(text));
        classify(docID, toIndex);
        if(toIndex) {
            updateDoc();
            try {
                indexer_parse.put(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * update document tf field
     */
    private void updateDoc() {
        int currentMaxValue = Integer.MIN_VALUE;
        doc.setUniqueTf(tempDictionary.size());
        for (PreTerm preTerm : tempDictionary.values()){
            if (preTerm.getTf() > currentMaxValue) {
                currentMaxValue = preTerm.getTf();
            }
        }
        doc.setMaxTf(currentMaxValue);
    }

    /**
     * main classify function of a text - to numbers or text tokens
     * @param docID
     */
    private void classify(String docID, boolean toIndex) {
        for (; index < tokenList.size(); index++) {
            String token = getTokenFromList(index);
            if (token.isEmpty())
                continue;
            if(stopWord != null)
                if(stopWord.isStopWord(token))
                    continue;
            if(toStemm) {
                token = porterStemmer.stem(token);
                porterStemmer.reset();
            }
            if (token.matches(".*\\d+.*")) {
                String term = numParse(token);
                if (term.isEmpty())
                    term = ANumbers.parseNumber(index, token);
                addTerm(term, docID, toIndex);
            } else {
                String term =letterParse(token);
                if (term.isEmpty()) {
                    term = Text.parseText(index, token);
                    if (term.isEmpty())
                        term = token;
                }
                addTerm(term, docID, toIndex);
            }
        }
    }

    /**
     * aggregated numbers parsing rules function
     * @param token
     * @return legal numbers token
     */
    private String numParse (String token){
        //Price.parsePrice(index, token) + Percentage.parsePercent(index, token) + Date.dateParse(index, token)
          //      + Hyphen.parseHyphen(index, token) + Quotation.parseQuotation(index, token);
        String res = Price.parsePrice(index, token);
        if(res.isEmpty()){
            res = Percentage.parsePercent(index, token);
        }
        if(res.isEmpty()){
            res = Date.dateParse(index, token);
        }
        if(res.isEmpty()){
            res = Hyphen.parseHyphen(index, token);
        }
        if(res.isEmpty()){
            res = Quotation.parseQuotation(index, token);
        }
        return res;
    }

    /**
     * aggregated letters parsing rules function
     * @param token
     * @return legal text token
     */
    private String letterParse ( String token){
        //Date.dateParse(index, token) + Combo.parseCombo(index, token) +
        // Hyphen.parseHyphen(index, token) + Quotation.parseQuotation(index, token)
        String res= Date.dateParse(index, token);
        //if(res.isEmpty()) {
            //res = Combo.parseCombo(index, token);
        //}
        if(res.isEmpty()){
            res = Hyphen.parseHyphen(index, token);
        }
        if(res.isEmpty()){
            res = Quotation.parseQuotation(index, token);
        }
        return res;
    }

    /**
     * get Token From List of tokens
     * @param index
     * @return token
     */
    static String getTokenFromList(int index) {
        if (index >= tokenList.size())
            return "eof";
        String token = tokenList.get(index);
        token = token.replaceAll("[,'` ]", "");
        token = StringUtils.stripEnd(token,".");
        if(token.isEmpty())
            token = getTokenFromList(index+1);
        return token;
    }

    /**
     * add term to dictionary function
     * @param token
     * @param docID
     */
    private void addTerm(String token, String docID, boolean toIndex) {
        PreTerm term;
        boolean isAtBegin = false;
        token = cleanToken(token);
        if((token.length()==1 && !StringUtil.isNumeric(token)) || token.isEmpty())
            return;
        if(stopWord != null) {
            if (stopWord.isStopWord(token))
                return;
        }
        if(toIndex) {
            checkCityInDoc(token);
            if (index <= bound) {
                isAtBegin = true;
            }
             term = new PreTerm(token, docID, isAtBegin, doc.getTitle());
        }
        else{
             term = new PreTerm(token);
        }
        if (tempDictionary.containsKey(token))
            tempDictionary.get(token).increaseTf();
        else {
            tempDictionary.put(token, term);
        }
    }

    /**
     * cleaning function of terms
     * @param token
     * @return "clean" term
     */
    private String cleanToken(String token) {
        String res =StringUtils.stripStart(token,null);
        res =StringUtils.stripEnd(res,null);
        res =StringUtils.stripStart(res,"\"-./");
        res =StringUtils.stripEnd(res,"\"-./");
        return res;
    }

    /**
     * function that replace 2 obj of token
     * @param index
     * @param newToken
     */
    static void replaceToken(int index, String newToken) {
        tokenList.set(index, newToken);
    }

    /**
     * boolean function to check if a term is in the dictionary
     * @param token
     * @return true if temp dictionary contains term
     */
    static boolean checkExist(String token){
        return tempDictionary.containsKey(token);
    }

    /**
     * function that add city to doc if its not in doc
     * @param term - a optional city
     */
    public void checkCityInDoc(String term){
        if(term.toUpperCase().contains(doc.getCity()))
            doc.setCityOccurence(String.valueOf(index));
    }

    /**
     * function that replace 2 obj of term
     * @param currentTerm
     * @param newTerm
     */
    static void replaceTerm(String currentTerm, String newTerm){
        PreTerm term = tempDictionary.get(currentTerm);
        term.setName(newTerm);
        tempDictionary.remove(currentTerm);
        tempDictionary.put(newTerm,term);
    }

    /**
     * clears the stop words when rest butten is activated
     */
    public void clear(){
        stopWord.clearStopWords();
    }

    public List<String> parseQuery(String query){
        List<String> terms = new LinkedList<>();
        parse("Q",query,false);
        for (PreTerm preTerm:tempDictionary.values()) {
            terms.add(preTerm.getName());
        }
        return terms;
    }
}
