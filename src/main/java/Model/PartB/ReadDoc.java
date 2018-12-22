package Model.PartB;



import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that read a doc and extract the meta data
 */
public class ReadDoc {
    private String path;
    private int docAmount;
    private double avgDl;
    private RandomAccessFile fileStore;

    /**
     * c'tor
     * @param path
     */
    public ReadDoc(String path, boolean toStemm) {
        String folder;
        if(toStemm)
            folder = "Stemmed";
        else
            folder = "notStemmed";
        this.path = path + "\\" + folder;
        this.docAmount=0;
    }

    /**
     * read doc files and extract meta data
     */
    public void readDoc() {
        String[] parts;
        int sumUniqueTF = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path+"\\documents.txt"));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                docAmount++;
                sCurrentLine = sCurrentLine.replace(" ","");
                parts = sCurrentLine.split(":");
                sumUniqueTF = sumUniqueTF + Integer.valueOf(parts[4]);

            }
            avgDl = (double) sumUniqueTF / docAmount;
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * find the line in the posting file for a given term
     * @param fileName
     * @param line
     * @return split array per doc
     */
    public String[] readDocFromPosting(String fileName, int line) {
        int counterLine = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + "\\" + fileName));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (line == counterLine) {
                    br.close();
                    return sCurrentLine.split(",");
                }
                counterLine++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] readDocLine(int ptr){
        int offset = 102;
        byte[] bytes = new byte[offset];
        try {
            File file = new File(path+"\\documents.txt");
            fileStore = new RandomAccessFile(file, "r");
            // moves file pointer to position specified
            fileStore.seek((ptr-1)*offset);
            // reading String from RandomAccessFile
            fileStore.read(bytes);
            fileStore.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String record = new String(bytes);
        record = record.replaceAll("[ \n\r]","");
        return record.split(":");

    }

    public void closeAccess(){
        try {
            fileStore.close();
        } catch (IOException e) {}
    }


    public int getDocAmount() {
        return docAmount;
    }

    public double getAvgDl() {
        return avgDl;
    }

    /**
     * gets the relevant docs with a given cities set
     * @param items
     * @return
     */
    public List<String> readCities(ArrayList<String> items) {
        BufferedReader br ;
        List<String> listOfDocs = new LinkedList();
        try {
            br = new BufferedReader(new FileReader(path + "\\cities.txt"));
            String sCurrentLine ;
            while ((sCurrentLine = br.readLine()) != null) {
                String cityToCheck = StringUtils.substringBefore(sCurrentLine, "~");
                if(items.isEmpty())
                    break;
                //for each city the user has chosen
                for (String item :items ) {
                    if(cityToCheck.equals(item)){
                        concatDoc(sCurrentLine,listOfDocs);
                        items.remove(item);
                        break;
                    }
                }
            }br.close();
        }catch (IOException io){

        }
        return listOfDocs;
    }

    /**
     * cut the string docs to get just docID
     * @param sLine
     * @param listOfDocs
     */
    private void concatDoc (String sLine, List<String> listOfDocs){
        String[] docsWithNumbers = sLine.split("\\|");
        for (int i=1;i<docsWithNumbers.length;i++){
            listOfDocs.add(StringUtils.substringBefore(docsWithNumbers[i],"["));
        }

    }
}
