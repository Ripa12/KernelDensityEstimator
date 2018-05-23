package interval_tree.FrequentPatternMining.SupportCount;

import java.util.HashMap;

public class ColumnCount extends HashMap<String, Integer[]> {

    ColumnCount(){
//        for (String column : columns) {
//            put(column, new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE});
//        }
    }



    void updateTotalSupport(){
        this.totalSupportCount = 0; // ToDo: Maybe store totalSupportCount in a class wrapping supportCount?
        values().forEach(k -> this.totalSupportCount += k[0]);
    }

    boolean isSufficient(String col){
        return (((double)get(col)[0]) / totalSupportCount) >= minsup;
    }
}
