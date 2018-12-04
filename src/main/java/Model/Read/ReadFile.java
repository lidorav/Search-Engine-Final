package Model.Read;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ReadFile implements Runnable {
    private File corpus;
    private static ConcurrentHashMap<String, Model.Document> docMap;
    private BlockingQueue<Model.Document> parse_read;


    //constructor
    public ReadFile(String path, BlockingQueue bq){
        corpus = new File(path);
        City.loadCities();
        docMap = new ConcurrentHashMap<>();
        parse_read = bq;
    }

    @Override
    public void run() {
        read();
    }

    public void read() {
        for (File dir : corpus.listFiles()) {
            if(dir.getName().equals("stop_words.txt"))
                continue;
            for (File file : dir.listFiles()) {
                try {
                    System.out.println(file.getName());
                    Document doc = Jsoup.parse(file, "UTF-8");
                    Elements documents = doc.getElementsByTag("DOC");
                    int i = 0;//mark the place of the doc in the file
                    for (Element element : documents) {
                        String docNum = element.getElementsByTag("DOCNO").get(0).text();

                        //check if title/headline exists and retrieve from document
                        String docTitle = "";
                        Elements docTitleElement = element.getElementsByTag("TI");
                        if (docTitleElement.isEmpty())
                            docTitleElement = element.getElementsByTag("HEADLINE");
                        if (!docTitleElement.isEmpty()) {
                            docTitle = docTitleElement.get(0).text();
                        }

                        //check if city exists and retrieve from document
                        String docCity = "";
                        String docLanguage = "";
                        Elements docCityElement = element.getElementsByTag("F");
                        if (!docCityElement.isEmpty()) {
                            if (docCityElement.eachAttr("p").contains("104")) {
                                Iterator iter = docCityElement.iterator();
                                while (iter.hasNext()) {
                                    Element elem = (Element) iter.next();
                                    if (elem.attributes().get("p").equals("104"))
                                        docCity = elem.text();
                                    if(elem.attributes().get("p").equals("105"))
                                        docLanguage = elem.text();
                                }
                                docCity = StringUtils.stripStart(docCity,null);
                                String[] parts = docCity.split(" ");
                                docCity = parts[0].toUpperCase();
                            }
                        }

                        //check if date exists and retrieve from document
                        String docDate = "";
                        Elements docDateElement = element.getElementsByTag("DATE");
                        if (!docDateElement.isEmpty()) {
                            docDate = docDateElement.get(0).text();
                        }
                        //check if text exists and retrieve from documenet
                        Elements docTextElement = element.getElementsByTag("TEXT");
                        if (docTextElement.isEmpty())
                            continue;
                        String data = element.getElementsByTag("TEXT").get(0).text();
                        Model.Document modelDoc = new Model.Document(file.getName(), docTitle, i++, docCity, docDate, data, docNum, docLanguage);
                        docMap.put(docNum, modelDoc);
                        //send document data for parsing
                        parse_read.put(modelDoc);
                    }
                } catch (Exception e) {
                    System.out.println(e + "error");
                }
            }
            //after file is complete we deploy it to the posting file
        }
        try {
            //parser.shutDownExecutor();
            parse_read.put(new Model.Document("fin"));
        }catch (Exception e){
            System.out.println("error queue");
        }
    }

    public static Model.Document getDoc(String docID){
        return docMap.get(docID);
    }

    public static void removeDoc(String docID){
        docMap.remove(docID);
    }
}
