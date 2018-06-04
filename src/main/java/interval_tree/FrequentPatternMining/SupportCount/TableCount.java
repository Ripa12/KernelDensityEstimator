package interval_tree.FrequentPatternMining.SupportCount;

import interval_tree.FrequentPatternMining.AbstractFPTreeNode;

import java.util.*;

/**
 * Created by Richard on 2018-05-23.
 */
public class TableCount {

    private class ColumnProperties{
        Double[] info;
        boolean isDecimal;

        ColumnProperties(Double[] i, boolean t){
            info = i;
            isDecimal = t;
        }
    }

    private LinkedHashMap<String, Map<String, ColumnProperties>> tables;
    private double totalSupportCount;
    private double minsup;

    public TableCount(double minsup, String[] tableNames){
        this.minsup = minsup;
        this.totalSupportCount = 0;
        this.tables = new LinkedHashMap<>();

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

    public void addColumn(String table, String column, boolean type){
        /**
         * New Integer[]{support, min queried val, max queried val, min actual val, max actual val}
         **/
        this.tables.get(table).put(column, new ColumnProperties(new Double[]
                {0.0, Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE}, type));
    }

    public void addColumns(String table, String[] labels, double boundaries[][]){
        assert boundaries.length == labels.length;

        /**
         * New Integer[]{support, min queried val, max queried val, min actual val, max actual val}
         **/
        int index = 0;
        for (String column : labels) {
            this.tables.get(table).put(column, new ColumnProperties(new Double[]
                    {0.0, Double.MAX_VALUE, Double.MIN_VALUE, boundaries[index][0], boundaries[index][1]},
                    boundaries[index][2]==1));
            index++;
        }
    }

    public double getSupport(String table, String column){
        return this.tables.get(table).get(column).info[0];
    }
    
    public void updateMinMax(String table, String column, double start, double end){
        Double[] col = this.tables.get(table).get(column).info;
        col[0]++;
        col[1] = Math.min(col[1], start);
        col[2] = Math.max(col[2], end);

        totalSupportCount++;
    }

    public Number getCorrectType(String table, String column, double val){
        if(this.tables.get(table).get(column).isDecimal){
            return val;
        }else{
            return (int) val;
        }
    }

    public boolean isSufficient(String table, String col){
        return (((double)tables.get(table).get(col).info[0]) / totalSupportCount) >= minsup;
    }

    public double getPositiveInfinityLimit(String table, String col){
        return this.tables.get(table).get(col).info[4];
    }

    public double getNegativeInfinityLimit(String table, String col){
        return this.tables.get(table).get(col).info[3];
    }

    public double constrainToRange(String table, String col, double val){
        return Math.min(Math.max(val, this.tables.get(table).get(col).info[3]), this.tables.get(table).get(col).info[4]);
    }

    public HashMap<String, LinkedList<AbstractFPTreeNode>> buildHeader(String tableName){
        HashMap<String, LinkedList<AbstractFPTreeNode>> header = new HashMap<>();

        tables.get(tableName).keySet().forEach(k ->
        {
            if(isSufficient(tableName, k))
                header.put(k, new LinkedList<>());
        });

        return header;
    }

}
