package Model.PartA;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that represent a document, responsible in holding and fetching the document information
 */
public class Document {

    private String language;
    private int maxTf;
    private int uniqueTf;
    private String city;
    private String fileName;
    private String title;
    private int position ;
    private String text;
    private ConcurrentHashMap<String, PreTerm> termsInDoc;
    private String docID;
    private String cityOccurence;

    /**
     * C'tor create a document by a filename
     * @param fileName a given document filename
     */
    public Document(String fileName){
        this.fileName = fileName;
    }

    /**
     * C'tor create a document by a given paramters
     * @param fileName the document filename
     * @param title the document title
     * @param position the document position in file (1st,2nd,3rd,...)
     * @param city the document city tag
     * @param data the doucment text
     * @param docID the document ID
     * @param language the doucment language tag
     */
    public Document(String fileName, String title, int position, String city, String data, String docID, String language) {
        this.city = city;
        this.language = language;
        this.fileName = fileName;
        this.title = title;
        this.position= position;
        this.text = data;
        this.docID = docID;
        this.maxTf = 0;
        this.uniqueTf = 0;
        this.cityOccurence="";
    }

    /**
     * @return the document language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @return the document city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the docuement city occurences in text
     */
    public String getCityOccurence() {
        return cityOccurence;
    }

    /**
     * @return a hash-map of terms in doc
     */
    public ConcurrentHashMap<String, PreTerm> getTermsInDoc() {
        return termsInDoc;
    }

    /**
     * @return the document ID
     */
    public String getDocID() {
        return docID;
    }

    /**
     * @return the document filename
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set document max tf
     * @param maxTf given maxTf number
     */
    public void setMaxTf(int maxTf) {
        this.maxTf = maxTf;
    }

    /**
     * Set document unique tf
     * @param uniqueTf given uniqueTf number
     */
    public void setUniqueTf(int uniqueTf) {
        this.uniqueTf = uniqueTf;
    }

    /**
     * @return document title
     */
    public String getTitle() {
        return title;
    }

    /**
     * clean the document text data and set a hash-map with the document terms
     * @param tempDic hash-map with the document terms
     */
    public void cleanText(ConcurrentHashMap<String, PreTerm> tempDic){
        text=null;
        termsInDoc = tempDic;
    }

    /**
     * @return a string with the document information
     */
    public StringBuilder getDocInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append(docID).append(":").append(fileName).append(":").append(position)
                .append(":").append(maxTf).append(":").append(uniqueTf);
        return sb;
    }

    /**
     * @return the document text
     */
    public String getText() {
        return text;
    }

    /**
     * set to the document city occurrence index and concat them together
     * @param index the term index in document
     */
    public void setCityOccurence(String index){
        cityOccurence = cityOccurence + "," + index;
    }

    @Override
    public String toString() {
        return  ", fileName='" + fileName + '\'' +
                ", title='" + title + '\'' +
                ", position=" + position +
                '}';
    }
}
