package Model.PartB;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that read a doc and extract the meta data
 */
public class ReadDoc {
    private String path;
    private int docAmount;
    private double avgDl;

    /**
     * c'tor
     * @param path
     */
    public ReadDoc(String path) {
        this.path = path;
        this.docAmount=0;

    }

    /**
     * read doc files and extract meta data
     */
    public void readDoc(){
        String[] parts ;
        int sumUniqueTF=0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                docAmount++;
                parts = sCurrentLine.split(":");
                sumUniqueTF = sumUniqueTF +Integer.valueOf(parts[4]);

            }
            avgDl=(double)sumUniqueTF/docAmount;
            br.close();
           // lang_box.setItems(FXCollections.observableArrayList(arr));
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
    public String[] readDocFromPosting(String fileName, int line){
        int counterLine =0;
        try (BufferedReader br = new BufferedReader(new FileReader(path+"\\"+fileName))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if( line == counterLine){
                    return sCurrentLine.split(",");
                }

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getDocAmount() {
        return docAmount;
    }

    public double getAvgDl() {
        return avgDl;
    }
}
