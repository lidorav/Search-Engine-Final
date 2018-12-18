package Model.PartA.Index;

import Model.PartA.City;
import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class responsible in writing the posting data to files
 */
public class Posting {

    private String path;
    private static int fileID=1000;
    private Queue<String> postingQueue;
    private String folder;

    /**
     * C'tor initialize class fields and create a new folder depends on the given stem boolean
     * @param path a given path for the Posting folder
     * @param toStemm a given boolean to decide the folder name
     */
    public Posting(String path, boolean toStemm) {
        this.path = path;
        if(toStemm)
            folder = "Stemmed";
        else
            folder = "notStemmed";
        new File(path+"\\"+folder).mkdir();
        postingQueue = new LinkedList();
    }

    /**
     * Write all the initial positing files to disk
     * @param tempPosting a Tree-map that contains the posting data
     */
    public void initTempPosting(TreeMap<String, StringBuilder> tempPosting) {
        File file = new File( path + "\\" + folder + "\\" + fileID);
        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            postingQueue.add(String.valueOf(fileID));
            for (Map.Entry entry : tempPosting.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue() + "\r\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileID++;
    }

    /**
     * Merge all the inital posting files into 1 posting file
     */
    public void mergePosting() {
        fileID=2000;
        while (postingQueue.size() > 1) {
            String postA = postingQueue.poll();
            String postB = postingQueue.poll();
            File fileFromA = new File(path + "\\" + folder + "\\" + postA);
            File fileFromB = new File(path + "\\" + folder + "\\" + postB);
            String newFile = String.valueOf(fileID);
            fileID++;
            File fileTo = new File(path + "\\" + folder + "\\" + newFile);
            try {
                BufferedReader brA = new BufferedReader(new InputStreamReader(new FileInputStream(fileFromA), StandardCharsets.UTF_8));
                BufferedReader brB = new BufferedReader(new InputStreamReader(new FileInputStream(fileFromB), StandardCharsets.UTF_8));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileTo), StandardCharsets.UTF_8));
                String lineA = brA.readLine();
                String lineB = brB.readLine();
                while ((lineA != null) && (lineB != null)) {
                    String[] termA = lineA.split(":");
                    String[] termB = lineB.split(":");
                    int compareRes = termA[0].compareTo(termB[0]);
                    //if both terms are the same write both lines in one line to the merged file
                    if (compareRes == 0) {
                        bw.write(lineA + termB[1]+"\r\n");
                        lineA = brA.readLine();
                        lineB = brB.readLine();
                        continue;
                    }
                    //if line B is bigger then Line A write line A to the merged file
                    if (compareRes < 0) {
                        bw.write(lineA+"\r\n");
                        lineA = brA.readLine();
                        continue;
                    }
                    //if line A is bigger then Line B write line B to the merged file
                    if (compareRes > 0) {
                        bw.write(lineB+"\r\n");
                        lineB = brB.readLine();
                    }
                }
                //if we finished all lines at file A, write all the lines left in file B to the merged file
                if (lineA == null) {
                    while (lineB != null) {
                        bw.write(lineB+"\r\n");
                        lineB = brB.readLine();
                    }
                }
                //if we finished all lines at file B, write all the lines left in file A to the merged file
                if (lineB == null) {
                    while (lineA != null) {
                        bw.write(lineA+"\r\n");
                        lineA = brB.readLine();
                    }
                }
                brA.close();
                brB.close();
                //delete the older posting files
                FileUtils.deleteQuietly(fileFromA);
                FileUtils.deleteQuietly(fileFromB);
                bw.close();
                //add the queue the new merged posting file
                postingQueue.add(newFile);
            } catch (Exception e) {
            }
        }
        fileID=1000;
        orderBy(postingQueue.poll());
    }

    /**
     * Create posting file according to the english alphabetic and numbers 0-1
     * Sort the merged posting file into separate files
     * @param postingFile the given posting file name
     */
    private void orderBy(String postingFile) {
        HashMap<String, Pair<BufferedWriter, AtomicInteger>> buffers = new HashMap<>();
        File fileFrom = new File(path + "\\" + folder + "\\" + postingFile);
        try {
            for(char ch='a';ch<='z';ch++){
                File fileTo = new File(path + "\\" + folder + "\\" + ch);
                buffers.put(String.valueOf(ch),new MutablePair<>(new BufferedWriter
                        (new OutputStreamWriter(new FileOutputStream(fileTo), StandardCharsets.UTF_8)),new AtomicInteger(0)));
            }
            for(int i=0;i<=9;i++){
                File fileTo = new File(path + "\\" + folder + "\\" + i);
                buffers.put(String.valueOf(i),new MutablePair<>(new BufferedWriter
                        (new OutputStreamWriter(new FileOutputStream(fileTo), StandardCharsets.UTF_8)),new AtomicInteger(0)));
            }

            File fileTo = new File(path + "\\" + folder + "\\symbol");
            buffers.put("symbol",new MutablePair<>(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileTo), StandardCharsets.UTF_8)),new AtomicInteger(0)));

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileFrom), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null){
                String[] parts = line.split(":");
                int i = buffers.get(getFileName(line)).getRight().getAndIncrement();
                line = Dictionary.addPtrToTerm(parts[0],i);
                buffers.get(getFileName(line)).getLeft().write(parts[1]+"\r\n");

            }

            for(Pair<BufferedWriter, AtomicInteger> pair: buffers.values()){
                pair.getLeft().close();
            }
            br.close();
            FileUtils.deleteQuietly(fileFrom);
        }catch (IOException e){}
    }

    /**
     * Get the posting filename from a given term by calculating it's first character
     * @param line the line in the merged posting file
     * @return the filename the line is associated
     */
       private String getFileName(String line) {
        char c = line.toLowerCase().charAt(0);
        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))
            return String.valueOf(c);
        return "symbol";
    }

    /**
     * write the documents posting into a file at the given path
     * @param docPost a Tree-map that holds all the documents information
     */
    public void writeDocIndex(LinkedHashMap<String, StringBuilder> docPost) {
        try {
            PrintWriter outputfile = new PrintWriter(new FileWriter(path + "\\" + folder + "\\documents.txt", true));
            for (Map.Entry<String, StringBuilder> doc : docPost.entrySet())
                outputfile.println(Strings.padEnd(doc.getValue().toString(),50,' '));
            outputfile.close();
        } catch (IOException e) {
        }
    }


    /**
     * write the city posting into a file at the given path
     * @param cityPost a Tree-map that holds all the documents City information
     */
    public void writeCityIndex(TreeMap<String, StringBuilder> cityPost) {
        PrintWriter outputfile = null;
        try {
            outputfile = new PrintWriter(path + "\\" + folder +"\\cities.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String,StringBuilder> city : cityPost.entrySet()
        ) {
            String info = City.getCityInfo(city.getKey());
            outputfile.println(city.getKey() + "-" + info + "-" + city.getValue());
        }
        outputfile.close();
    }

    /**
     * Clean the posting information by deleted the recursively in the given posting path directory
     */
    public void deletePosting() {
        try {
            FileUtils.cleanDirectory(new File(path));
        }catch (IOException e) { }
    }
}
