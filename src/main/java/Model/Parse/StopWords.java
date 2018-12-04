package Model.Parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class StopWords {

    private Set<String> stopWordSet = new HashSet<>();
    private String path="";

    StopWords()  {
        Scanner file = null;
        try {
            file = new Scanner(new File("stop_words.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (file.hasNext()) {
            // now dictionary is not recreated each time
            stopWordSet.add(file.next().trim());
        }

    }

    boolean isStopWord(String s){
        return stopWordSet.contains(s);
    }

    public void print (){
        System.out.println(stopWordSet);
    }



}