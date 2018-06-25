package interval_tree.Factory;

import interval_tree.SqlParser.AbstractParser;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
import interval_tree.SubspaceClustering.MyPoint;

import java.util.HashMap;

/**
 * Created by Richard on 2018-03-04.
 */
public class SupportCountParser extends AbstractParser {

    private HashMap<String, MyData> list;

    public SupportCountParser(TableBaseProperties tableBaseProperties, double minSup){
        super(new TableStats(tableBaseProperties, minSup));
        list = new HashMap<>();
    }

    public TableStats getStats(){
        return tableStats;
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        list.forEach((col, val) ->
                tableStats.updateStatistics(getCurrentTable(), col, val.getLow(), val.getHigh()));
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
        list.put(column, new MyInterval(start, end));
    }

    @Override
    protected void equalsTo(String col, double point) {
        list.put(col, new MyPoint(point));
    }

    @Override
    protected void greaterThan(String col, double point) {
        // ToDo: constrain point to range
        list.put(col, new MyInterval(point, Double.POSITIVE_INFINITY));
    }

    @Override
    protected void MinorThan(String col, double point) {
        // ToDo: constrain point to range. Change data-type to double
        list.put(col, new MyInterval(Double.NEGATIVE_INFINITY, point));
    }
}
