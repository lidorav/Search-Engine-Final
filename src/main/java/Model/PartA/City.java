package Model.PartA;

import Model.PartA.Parse.ANumbers;
import com.google.gson.*;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Class that responsible in loading the city file and support in given information for each city.
 */
public class City {

   private static HashMap<String, JsonObject> dataByCity = new HashMap<>();
   private static String path = "cities.json";
   private static HashMap<String,HashMap<String,StringBuilder>> cityOccurences = new HashMap<>();
    /**
     * load to a hash-map a json file of all cities and their information.
     */
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
                cityOccurences.put(cityname,new HashMap<>());
            }
        }catch (Exception e){}
   }

   public static boolean isCityInDoc(String term){
       return(dataByCity.containsKey(term));
   }
    /**
     * Get the city information data by a given city name
     * @param cityname a given city name
     * @return a string that holds all the city information
     */
   public static String getCityInfo(String cityname){
       StringBuilder sb = new StringBuilder();
       if(dataByCity.containsKey(cityname)) {
           JsonObject element = dataByCity.get(cityname);
           sb.append(element.get("name")).append("~").append(element.get("currencies").toString())
                   .append("~").append(ANumbers.parseNumber(element.get("population").toString(),""));
       }
       String res = sb.toString();
       res = res.replace("\"","");
       return res;
   }

    public static HashMap<String, HashMap<String, StringBuilder>> getCityOccurences() {
        return cityOccurences;
    }

    /**
     * @return Set of citys
     */
   public static Set<String> getCityList(){
       return dataByCity.keySet();
   }

   public static void addShow(String city,String docID, int index){
       if(cityOccurences.get(city).containsKey(docID)){
           cityOccurences.get(city).get(docID).append(index).append(",");
       }
       else{
           cityOccurences.get(city).put(docID,new StringBuilder().append(index).append(","));
       }
   }

    /**
     * Clear the city hash-map
     */
   public static void clearCities(){
       dataByCity.clear();
   }
}