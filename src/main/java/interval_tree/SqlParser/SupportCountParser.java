package interval_tree.SqlParser;

import interval_tree.FrequentPatternMining.SupportCount.ColumnCount;

/**
 * Created by Richard on 2018-03-04.
 */
public class SupportCountParser extends AbstractParser {
    private ColumnCount columnCount;

    public SupportCountParser(ColumnCount columnCount){
        super();
        this.columnCount = columnCount;
    }

    @Override
    public void before() {

    }

    @Override
    public void after() {
        columnCount.updateTotalSupport(); // ToDo: Find a more clever way
    }

    @Override
    protected void finiteInterval(String column, int start, int end) {
        columnCount.get(column)[0]++;
        columnCount.get(column)[1] = Math.min(columnCount.get(column)[1], start);
        columnCount.get(column)[2] = Math.max(columnCount.get(column)[2], end);
    }


    @Override
    protected void equalsTo(String col, int point) {
        columnCount.get(col)[0]++;
        columnCount.get(col)[1] = Math.min(columnCount.get(col)[1], point);
        columnCount.get(col)[2] = Math.max(columnCount.get(col)[2], point);
    }
}
