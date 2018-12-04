package Model;

public class Document {

    private int maxTf;
    private int uniqueTf;
    private String city;
    private String fileName;
    private String title;
    private int position ;
    private String date;
    private String text;
    private String docID;

    public Document(String fileName){
        this.fileName = fileName;
    }

    public Document(String fileName, String title, int position, String city, String date, String data, String docID) {
        this.city = city;
        this.fileName = fileName;
        this.title = title;
        this.position= position;
        this.date = date;
        this.text = data;
        this.docID = docID;
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

    public void cleanText(){
        text=null;
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

    @Override
    public String toString() {
        return  ", fileName='" + fileName + '\'' +
                ", title='" + title + '\'' +
                ", position=" + position +
                '}';
    }
}
