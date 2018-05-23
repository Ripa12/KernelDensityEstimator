package interval_tree.FrequentPatternMining.SupportCount;

import java.util.HashMap;

/**
 * Created by Richard on 2018-05-23.
 */
public class TableCount {

    private HashMap<String, ColumnCount> tables;
    private double totalSupportCount;
    private double minsup;

    public TableCount(double minsup, String[] tableNames){
        this.minsup = minsup;
        this.totalSupportCount = 0;
        this.tables = new HashMap<>();

        for (String tableName : tableNames) {
            this.tables.put(tableName, new ColumnCount());
        }
    }

    public double getTotalSupportCount(){
        return totalSupportCount;
    }

    public void addColumn(String table, String column){
        /**
         * New Integer[]{support, min queried val, max queried val, min actual val, max actual val}
         **/
        this.tables.get(table).put(column, new Integer[]
                {0, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE});
    }

    public boolean isSufficient(String col){
        return (((double)get(col)[0]) / totalSupportCount) >= minsup;
    }

}
