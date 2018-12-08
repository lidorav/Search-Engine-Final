package Model;

import Model.Parse.ANumbers;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class City {

   private static HashMap<String, JsonObject> dataByCity = new HashMap<>();
   private static String path = "cities.json";

   public static void loadCities(){
        try {
            ClassLoader classLoader = City.class.getClassLoader();
            File file = new File(classLoader.getResource(path).getFile());
            JsonStreamParser parser = new JsonStreamParser(new FileReader(file));
            JsonArray elements = parser.next().getAsJsonArray();
            for(int i=0;i<elements.size();i++){
                JsonObject element = elements.get(i).getAsJsonObject();
                String cityname = element.get("capital").toString().toUpperCase();
                cityname = cityname.replace("\"","");
                dataByCity.put(cityname,element);
            }
        }catch (Exception e){}
   }

   public static String getCityInfo(String cityname){
       StringBuilder sb = new StringBuilder();
       if(dataByCity.containsKey(cityname)) {
           JsonObject element = dataByCity.get(cityname);
           sb.append(element.get("name")).append(",").append(element.get("currencies").toString())
                   .append(",").append(ANumbers.parseNumber(element.get("population").toString(),""));
       }
       String res = sb.toString();
       res = res.replace("\"","");
       return res;
   }

   public static void clearCities(){
       dataByCity.clear();
   }
}