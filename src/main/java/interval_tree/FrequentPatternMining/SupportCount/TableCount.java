package interval_tree.FrequentPatternMining.SupportCount;

import interval_tree.FrequentPatternMining.AbstractFPTreeNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Richard on 2018-05-23.
 */
public class TableCount {

    private Map<String, Map<String, Integer[]>> tables;
    private double totalSupportCount;
    private double minsup;

    public TableCount(double minsup, String[] tableNames){
        this.minsup = minsup;
        this.totalSupportCount = 0;
        this.tables = new HashMap<>();

        for (String tableName : tableNames) {
            this.tables.put(tableName, new HashMap<>());
        }
    }

    public double getTotalSupportCount(){
        return totalSupportCount;
    }

    public Set<String> getTableNames(){
        return tables.keySet();
    }

    public void addColumn(String table, String column){
        /**
         * New Integer[]{support, min queried val, max queried val, min actual val, max actual val}
         **/
        this.tables.get(table).put(column, new Integer[]
                {0, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE});
    }

    public void addColumns(String table, String[] columns){
        /**
         * New Integer[]{support, min queried val, max queried val, min actual val, max actual val}
         **/
        for (String column : columns) {
            this.tables.get(table).put(column, new Integer[]
                    {0, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE});
        }
    }

    public int getSupport(String table, String column){
        return this.tables.get(table).get(column)[0];
    }

    public void updateMinMax(String table, String column, int start, int end){
        Integer[] col = this.tables.get(table).get(column);
        col[0]++;
        col[1] = Math.min(col[1], start);
        col[2] = Math.max(col[2], end);

        totalSupportCount++;
    }

    public boolean isSufficient(String table, String col){
        return (((double)tables.get(table).get(col)[0]) / totalSupportCount) >= minsup;
    }

    public static HashMap<String, LinkedList<AbstractFPTreeNode>> buildHeader(TableCount tableCount, String tableName){
        HashMap<String, LinkedList<AbstractFPTreeNode>> header = new HashMap<>();

        tableCount.tables.get(tableName).keySet().forEach(k -> header.put(k, new LinkedList<>()));

        return header;
    }

}
