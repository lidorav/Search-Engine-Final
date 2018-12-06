package Model.Read;

import Model.City;
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
    private BlockingQueue<Model.Document> parse_read;


    //constructor
    public ReadFile(String path, BlockingQueue bq){
        corpus = new File(path);
        City.loadCities();
        parse_read = bq;
    }

    @Override
    public void run() {
        read();
    }

    public void read() {
        int counter=0;
        for (File dir : corpus.listFiles()) {
            if(dir.getName().equals("stop_words.txt"))
                continue;
            for (File file : dir.listFiles()) {
                try {
                    Document doc = Jsoup.parse(file, "UTF-8");
                    Elements documents = doc.getElementsByTag("DOC");
                    int i = 0;//mark the place of the doc in the file
                    for (Element element : documents) {
                        counter++;
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

                        //check if text exists and retrieve from documenet
                        String data="";
                        Elements docTextElement = element.getElementsByTag("TEXT");
                        if (!(docTextElement.isEmpty()))
                            data = element.getElementsByTag("TEXT").get(0).text();
                        Model.Document modelDoc = new Model.Document(file.getName(), docTitle, i++, docCity, data, docNum, docLanguage);
                        //send document data for parsing
                        parse_read.put(modelDoc);
                    }
                } catch (Exception e) {}
            }
            //after file is complete we deploy it to the posting file
        }
        try {
            parse_read.put(new Model.Document("fin"));
            System.out.println(counter);
        }catch (Exception e){}
    }
}
