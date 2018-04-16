package interval_tree;

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

    private Logger(){
        stopTimer = new HashMap<>();
        startTime = 0L;
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
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(" --- Estimated time --- \n");
        for (Map.Entry<String, Long> entry : stopTimer.entrySet())
        {
            builder.append(entry.getKey() + " : " + (entry.getValue() / 1000000000.0) + "\n");
        }
        return builder.toString();
    }

    public void setTimer(){
        startTime = System.nanoTime();
    }
    public void stopTimer(String timer){
        stopTimer.put(timer, stopTimer.get(timer) + (System.nanoTime() - startTime));
    }


    public static Logger getInstance(){
        if(instance == null)
            instance = new Logger();
        return instance;
    }
}
