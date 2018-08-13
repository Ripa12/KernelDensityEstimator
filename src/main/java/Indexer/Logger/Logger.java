package Indexer.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static Indexer.Utility.getOutputDirectory;

public class Logger {

    private static class Timer{
        Timer(long s, long e){
            start = s;
            end = e;
        }

        void reset(){
            start = end = 0L;
        }

        double getSeconds(){
            return (end - start) / 1000000000.0;
        }

        long start;
        long end;
    }

    /**
     * Singleton
     */
    private static Logger instance = null;

    /**
     * Measure time
     */
    private Map<String, Timer> stopTimer;

    /**
     * Record Indexes
     */
    private int nrOfIndexes;

    private Logger(){
        stopTimer = new HashMap<>();
        nrOfIndexes = 0;
    }

    public Logger addTimer(String timer){
        stopTimer.put(timer, new Timer(0L, 0L));
        return this;
    }

    public void reset(){
        for (Map.Entry<String, Timer> entry : stopTimer.entrySet())
        {
            entry.getValue().reset();
        }
        nrOfIndexes = 0;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(" --- Estimated time --- \n");
        for (Map.Entry<String, Timer> entry : stopTimer.entrySet())
        {
            builder.append(entry.getKey() + " : " + (entry.getValue().getSeconds()) + "\n");
        }
        return builder.toString();
    }

    public void dump(File file, String header, boolean append){
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream(file, append)), "UTF-8"));

            long totalTime = 0;
            for (Timer time : stopTimer.values()) {
                totalTime += time.getSeconds();
            }

            out.println(header);
            out.println("Query time: " + (totalTime));
            out.println("Indexes: " + nrOfIndexes);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void setTimer(String timer){
        stopTimer.get(timer).start = System.nanoTime();
    }
    public void stopTimer(String timer){
        stopTimer.get(timer).end = System.nanoTime();
    }

    public void setNrOfIndexes(int n){
        nrOfIndexes = n;
    }

    public static Logger getInstance(){
        if(instance == null)
            instance = new Logger();
        return instance;
    }
}
