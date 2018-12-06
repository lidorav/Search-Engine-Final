package Model;

import java.util.concurrent.ConcurrentHashMap;

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

    public Document(String fileName){
        this.fileName = fileName;
    }

    public Document(String fileName, String title, int position, String city, String data, String docID, String language) {
        this.city = city;
        this.language = language;
        this.fileName = fileName;
        this.title = title;
        this.position= position;
        this.text = data;
        this.docID = docID;
        this.cityOccurence="";
    }

    public String getLanguage() {
        return language;
    }

    public String getCity() {
        return city;
    }

    public String getCityOccurence() {
        return cityOccurence;
    }

    public ConcurrentHashMap<String, PreTerm> getTermsInDoc() {
        return termsInDoc;
    }

    public String getDocID() {
        return docID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setMaxTf(int maxTf) {
        this.maxTf = maxTf;
    }

    public void setUniqueTf(int uniqueTf) {
        this.uniqueTf = uniqueTf;
    }

    public String getTitle() {
        return title;
    }

    public void cleanText(ConcurrentHashMap<String, PreTerm> tempDic){
        text=null;
        termsInDoc = tempDic;


    }
    public StringBuilder getDocInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append(docID).append(":").append(fileName).append(":").append(position)
                .append(":").append(maxTf).append(":").append(uniqueTf);
        return sb;
    }
    public String getText() {
        return text;
    }

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
