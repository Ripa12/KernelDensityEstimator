package interval_tree.FrequentPatternMining;

import java.util.HashMap;
import java.util.Map;

public class SupportCount extends HashMap<String, Integer[]> {

    private double totalSupportCount;
    private double minsup;

    public SupportCount(double minsup, String[] columns){
        totalSupportCount = 0;
        this.minsup = minsup;

        for (String column : columns) {
            put(column, new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        }
    }

    public double getTotalSupportCount(){
        return totalSupportCount;
    }

    public void updateTotalSupport(){
        this.totalSupportCount = 0; // ToDo: Maybe store totalSupportCount in a class wrapping supportCount?
        values().forEach(k -> this.totalSupportCount += k[0]);
    }

    public boolean isSufficient(String col){
        return (((double)get(col)[0]) / totalSupportCount) >= minsup;
    }
}
