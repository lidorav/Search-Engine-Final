package Model.PartB;

import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that responsible for reading from the disk the files needed for the ranking process
 */
public class ReadDoc {
    private String path;
    private int docAmount;
    private double avgDl;
    private RandomAccessFile fileStore;

    /**
     * C'tor for creating ReadDoc object
     * @param path the path for the posting folder
     * @param toStemm a boolean to decide if to stem or not
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
     * Calculate the documents size and average length by accessing the documents file
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
     * Retrieve documents list from the posting file from a given line number
     * @param fileName the posting file name
     * @param line the line number in the posting file
     * @return array of all documents ID's in the line
     */
    public String[] readDocFromPosting(String fileName, int line) {
        int counterLine = 1;
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

    /**
     * Retrieve the document information from the documents file by a given ptr to the line
     * @param ptr the line number
     * @return string array contains the document information
     */
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

    /**
     * close the connection to the posting file
     */
    public void closeAccess(){
        try {
            fileStore.close();
        } catch (IOException e) {}
    }


    /**
     * @return the documents size
     */
    public int getDocAmount() {
        return docAmount;
    }

    /**
     * @return the documents average length
     */
    public double getAvgDl() {
        return avgDl;
    }

    /**
     * gets the relevant docs with a given cities set
     * @param items a list of chosen cities
     * @return a list of docs associated with the given cities.
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
     * @param sLine A line of the posting file
     * @param listOfDocs a list of relevant docs need to be ranked
     */
    private void concatDoc (String sLine, List<String> listOfDocs){
        String[] docsWithNumbers = sLine.split("\\|");
        for (int i=1;i<docsWithNumbers.length;i++){
            listOfDocs.add(StringUtils.substringBefore(docsWithNumbers[i],"["));
        }

    }
}
