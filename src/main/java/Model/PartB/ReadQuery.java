package Model.PartB;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class ReadQuery {

    private String path;

    public ReadQuery(String path) {
        this.path = path;
    }

    public void read(){
        File inputQueries = new File(path);
        try {
            Document doc = Jsoup.parse(inputQueries,"UTF-8");
            Elements queries = doc.getElementsByTag("top");
            for (Element element:queries) {
                String num =element.getElementsByTag("num").get(0).text();
                String title =element.getElementsByTag("title").get(0).text();
                String desc =element.getElementsByTag("desc").get(0).text();
                System.out.println(num + " " + title + " " + desc );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
