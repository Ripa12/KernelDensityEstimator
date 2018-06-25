package interval_tree.Logger;

import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Logger {


    /**
     * Singleton
     */
    private static Logger instance = null;

    /**
     * Measure time
     */
    private Map<String, Long> stopTimer;
    private long startTime;

    private long queryTime;

    /**
     * Record Indexes
     */
    private int nrOfIndexes;

    private Logger(){
        stopTimer = new HashMap<>();
        startTime = 0L;
        nrOfIndexes = 0;
        queryTime = 0;
    }

    public Logger addTimer(String timer){
        stopTimer.put(timer, 0L);
        return this;
    }

    public void reset(){
        for (Map.Entry<String, Long> entry : stopTimer.entrySet())
        {
            entry.setValue(0L);
        }
        nrOfIndexes = 0;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(" --- Estimated time --- \n");
        for (Map.Entry<String, Long> entry : stopTimer.entrySet())
        {
            builder.append(entry.getKey() + " : " + (entry.getValue() / 1000000000.0) + "\n");
        }
        return builder.toString();
    }

    public void dump(String filename, String header, boolean append){
        String targetPath = "data/testdata/unittests/" + filename;
        if (SystemUtils.IS_OS_WINDOWS) {
            targetPath = targetPath.replaceFirst("/", "//");
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream(targetPath, append)), "UTF-8"));

            long totalTime = 0;
            for (Long time : stopTimer.values()) {
                totalTime += time;
            }

            out.println(header);
            out.println("Execution time: " + (totalTime / 1000000000.0));
            out.println("Query time: " + (queryTime / 1000000000.0));
            out.println("Indexes: " + nrOfIndexes);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void setTimer(){
        startTime = System.nanoTime();
    }
    public void stopTimer(String timer){
        stopTimer.put(timer, stopTimer.get(timer) + (System.nanoTime() - startTime));
    }

    public void setNrOfIndexes(int n){
        nrOfIndexes = n;
    }

    public void setQueryTime(long n){
        queryTime = n;
    }

    public static Logger getInstance(){
        if(instance == null)
            instance = new Logger();
        return instance;
    }
}
