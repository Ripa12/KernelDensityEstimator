package interval_tree.SqlParser;

import interval_tree.FrequentPatternMining.SupportCount.TableCount;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
import interval_tree.SubspaceClustering.MyPoint;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Richard on 2018-03-04.
 */
public class SupportCountParser extends AbstractParser {

    private HashMap<String, MyData> list;

    public SupportCountParser(TableCount tableCount){
        super(tableCount);

        list = new HashMap<>();
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        list.forEach((col, val) ->
                tableCount.updateMinMax(getCurrentTable(), col, val.getLow(), val.getHigh()));
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
//        tableCount.updateMinMax(getCurrentTable(), column, start, end);
        list.put(column, new MyInterval(start, end));
    }

    @Override
    protected void equalsTo(String col, double point) {
//        tableCount.updateMinMax(getCurrentTable(), col, point, point);
        list.put(col, new MyPoint(point));
    }

    @Override
    protected void greaterThan(String col, double point) {
        // ToDo: constrain point to range
//        tableCount.updateMinMax(getCurrentTable(), col, point,
//                (double)tableCount.getPositiveInfinityLimit(getCurrentTable(), col));
        list.put(col, new MyInterval(point, (double)tableCount.getPositiveInfinityLimit(getCurrentTable(), col)));
    }

    @Override
    protected void MinorThan(String col, double point) {
        // ToDo: constrain point to range. Change data-type to double
//        tableCount.updateMinMax(getCurrentTable(), col,
//                (double)tableCount.getNegativeInfinityLimit(getCurrentTable(), col), point);
        list.put(col, new MyInterval((double)tableCount.getNegativeInfinityLimit(getCurrentTable(), col), point));
    }
}
