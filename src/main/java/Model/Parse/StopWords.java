package Model.Parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * class that unifay the stop word actions
 * open the stop word file and check if a given word is a stop word
 */
public class StopWords {

    private Set<String> stopWordSet = new HashSet<>();

    /**
     * C'tor initialize the class parameters and opens a file that contains the stop word
     * @param path
     */
    StopWords(String path)  {
        Scanner file = null;
        try {
            file = new Scanner(new File(path+"\\stop_words.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (file.hasNext()) {
            // now dictionary is not recreated each time
            stopWordSet.add(file.next().trim());
        }

    }

    /**
     * boolean function to check if a given string is a stop word
     * @param s
     * @return true is is s is a stop word
     */
    boolean isStopWord(String s){
        return stopWordSet.contains(s.toLowerCase());
    }


    /**
     *
     */
    public void clearStopWords(){
        stopWordSet.clear();
    }



}