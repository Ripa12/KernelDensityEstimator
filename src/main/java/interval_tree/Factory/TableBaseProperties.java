package interval_tree.Factory;

import java.util.*;

/**
 * Created by Richard on 2018-05-23.
 */
public class TableBaseProperties {
    private LinkedHashMap<String, Map<String, ColumnBaseProperties>> tables;

    public TableBaseProperties(String[] tableNames){
        this.tables = new LinkedHashMap<>();

        for (String tableName : tableNames) {
            this.tables.put(tableName, new HashMap<>());
        }
    }

    TableBaseProperties(TableBaseProperties other) {
        this.tables = other.tables;
    }

    void addColumns(String table, String[] labels, double[][] boundaries) {
        assert boundaries.length == labels.length;

        /**
         * New Double[]{support, min queried val, max queried val, min actual val, max actual val}
         **/
        int index = 0;
        for (String column : labels) {
            this.tables.get(table).put(column, new ColumnBaseProperties(boundaries[index][0], boundaries[index][1],
                    boundaries[index][2]==1));
            index++;
        }
    }

    public Set<String> getTableNames() {
        return tables.keySet();
    }

    public Set<String> getColumnNames(String tableName) {
        return tables.get(tableName).keySet();
    }


    public Number getCorrectType(String table, String column, double val) {
        if (this.tables.get(table).get(column).isDecimal) {
            return val;
        } else {
            return (int) val;
        }
    }
}
