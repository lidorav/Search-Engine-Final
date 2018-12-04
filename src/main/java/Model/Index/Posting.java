package Model.Index;

import java.io.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class Posting {

    private String path = "D:\\Posting";
    private static int fileID = 1;
    private Queue<String> postingQueue;

    public Posting() {
        postingQueue = new LinkedList();
    }

    public void initTempPosting(TreeMap<String, StringBuilder> tempPosting) {
        File file = new File(path + "\\" + fileID);
        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
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

    public void mergePosting() {
        fileID=1000;
        while (postingQueue.size() > 1) {
            String postA = postingQueue.poll();
            String postB = postingQueue.poll();
            File fileFromA = new File(path + "\\" + postA);
            File fileFromB = new File(path + "\\" + postB);
            String newFile = String.valueOf(fileID);
            fileID++;
            File fileTo = new File(path + "\\" + newFile);
            try {
                BufferedReader brA = new BufferedReader(new FileReader(fileFromA));
                BufferedReader brB = new BufferedReader(new FileReader(fileFromB));
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileTo));
                String lineA = brA.readLine(), lineB = brB.readLine();
                while ((lineA != null) && (lineB != null)) {
                    String[] termA = lineA.split(":");
                    String[] termB = lineB.split(":");
                    int compareRes = termA[0].compareTo(termB[0]);
                    if (compareRes == 0) {
                        bw.write(lineA + termB[1]+"\r\n");
                        lineA = brA.readLine();
                        lineB = brB.readLine();
                        continue;
                    }
                    if (compareRes < 0) {
                        bw.write(lineA+"\r\n");
                        lineA = brA.readLine();
                        continue;
                    }
                    if (compareRes > 0) {
                        bw.write(lineB+"\r\n");
                        lineB = brB.readLine();
                    }
                }
                if (lineA == null) {
                    while (lineB != null) {
                        bw.write(lineB);
                        lineB = brB.readLine();
                    }
                }
                if (lineB == null) {
                    while (lineA != null) {
                        bw.write(lineA);
                        lineA = brB.readLine();
                    }
                }
                fileFromA.delete();
                fileFromB.delete();
                brA.close();
                brB.close();
                bw.close();
                postingQueue.add(newFile);
            } catch (Exception e) {
            }
        }
    }
}
