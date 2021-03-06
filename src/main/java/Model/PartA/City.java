package Model.PartA;

import Model.PartA.Parse.ANumbers;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that responsible in loading the city file and support in given information for each city.
 */
public class City {

   private static HashMap<String, JsonObject> dataByCity = new HashMap<>();
   private static String path = "cities.json";
   private static ConcurrentHashMap<String,HashMap<String,StringBuilder>> cityOccurences = new ConcurrentHashMap<>();
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
       if(term.isEmpty())
           return false;
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

    public static ConcurrentHashMap<String, HashMap<String, StringBuilder>> getCityOccurences() {
        return cityOccurences;
    }

    /**
     * @return Set of citys
     */
   public static Set<String> getCityList(){
       return cityOccurences.keySet();
   }

   public static void removeUnUsed(String city){
           cityOccurences.remove(city);
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
       cityOccurences.clear();
       dataByCity.clear();
   }

    public static void loadFromFile(boolean stemSelected, String postingPath) {
       String folder="notStemmed";
       if(stemSelected)
           folder = "Stemmed";
        File file = new File(postingPath+"\\"+folder+"\\cities.txt");
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            cityOccurences.clear();
            String line, city;
            while((line = br.readLine())!= null){
                String[] parts = line.split("~");
                city = parts[0];
                cityOccurences.put(city,new HashMap<>());
            }
            br.close();
        } catch (IOException e) {}
    }
}