package Indexer.Factory;

import Indexer.FrequentPatternMining.AbstractFPTreeNode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Richard on 2018-06-23.
 */
public class TableStats extends TableBaseProperties {
    private double totalSupportCount;
    private double minSup;

    LinkedHashMap<String, Map<String, ColumnStats>> tables;

    TableStats(TableBaseProperties baseProperties, double minSup){
        super(baseProperties);
        totalSupportCount = 0;
        this.minSup = minSup;

        tables = new LinkedHashMap<>();

        for (String s : baseProperties.getTableNames()) {
            HashMap<String, ColumnStats> stat = new HashMap<>();
            for (String s1 : baseProperties.getColumnNames(s)) {
                stat.put(s1, new ColumnStats());
            }
            tables.put(s, stat);
        }
    }

    public boolean isSufficient(String table, String col){
        return (tables.get(table).get(col).support / totalSupportCount) >= minSup;
    }

    public double getTotalSupportCount(){
        return totalSupportCount;
    }

    public double getSupport(String table, String col){
        return tables.get(table).get(col).support;
    }

    public HashMap<String, LinkedList<AbstractFPTreeNode>> buildHeader(String tableName) {
        HashMap<String, LinkedList<AbstractFPTreeNode>> header = new HashMap<>();

        tables.get(tableName).keySet().forEach(k ->
        {
            if (isSufficient(tableName, k))
                header.put(k, new LinkedList<>());
        });

        return header;
    }

    void updateStatistics(String table, String column, double start, double end){
        ColumnStats stat = this.tables.get(table).get(column);
        stat.addStat(start, end);

        totalSupportCount++;
    }
}
