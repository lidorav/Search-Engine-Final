package Model.PartA.Read;

import Model.PartA.City;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

/**
 * Class that responsible of reading files, parsing the different tags and creating Document objects
 */
public class ReadFile implements Runnable {
    private File corpus;
    private BlockingQueue<Model.PartA.Document> parse_read;


    /**
     * C'tor initialize the class parameters and activate the cities loading
     * @param path a given path to the corpus
     * @param bq Blocking queue between this class and the parsing class
     */
    public ReadFile(String path, BlockingQueue bq){
        corpus = new File(path);
        City.loadCities();
        parse_read = bq;
    }

    /**
     * run method of the working thread
     */
    @Override
    public void run() {
        read();
    }

    /**
     * Responsible of reading all the files and documents in the given corpus path.
     * parsing the document with the important tags and creating the doucment object for each document.
     * sending the created document object to the given queue.
     */
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
                        Model.PartA.Document modelDoc = new Model.PartA.Document(file.getName(), docTitle, i++, docCity, data, docNum, docLanguage);
                        //send document data for parsing
                        parse_read.put(modelDoc);
                    }
                } catch (Exception e) {}
            }
            //after file is complete we deploy it to the posting file
        }
        try {
            parse_read.put(new Model.PartA.Document("fin"));
            System.out.println(counter);
        }catch (Exception e){}
    }

    /**
     * clear the loaded cities
     */
    public void clear(){
        City.clearCities();
    }
}
