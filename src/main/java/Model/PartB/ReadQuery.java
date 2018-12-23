package Model.PartB;


import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ReadQuery {

    private String path;

    public ReadQuery(String path) {
        this.path = path;
    }

    public List<Query> read(){
        List<Query> queryList = new LinkedList<>();
        File inputQueries = new File(path);
        try {
            Document doc = Jsoup.parse(inputQueries,"UTF-8");
            Elements queries = doc.getElementsByTag("top");
            for (Element element:queries) {
                String num =element.getElementsByTag("num").get(0).text();
                String title =element.getElementsByTag("title").get(0).text();
                String desc =element.getElementsByTag("desc").get(0).text();
                num = num.substring(num.indexOf(' ')+1, StringUtils.ordinalIndexOf(num," ",2));
                desc = desc.substring(desc.indexOf(' ')+1);
                Query query = new Query(num,title,desc);
                queryList.add(query);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryList;
    }
}
