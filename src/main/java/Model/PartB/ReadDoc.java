package Model.PartB;



import java.io.*;

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
        int offset = 52;
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
}
