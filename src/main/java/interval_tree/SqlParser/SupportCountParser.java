package interval_tree.SqlParser;

import interval_tree.FrequentPatternMining.SupportCount.TableProperties;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
import interval_tree.SubspaceClustering.MyPoint;

import java.util.HashMap;

/**
 * Created by Richard on 2018-03-04.
 */
public class SupportCountParser extends AbstractParser {

    private HashMap<String, MyData> list;

    public SupportCountParser(TableProperties tableProperties){
        super(tableProperties);

        list = new HashMap<>();
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        list.forEach((col, val) ->
                tableProperties.updateMinMax(getCurrentTable(), col, val.getLow(), val.getHigh()));
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
//        tableProperties.updateMinMax(getCurrentTable(), column, start, end);
        list.put(column, new MyInterval(start, end));
    }

    @Override
    protected void equalsTo(String col, double point) {
//        tableProperties.updateMinMax(getCurrentTable(), col, point, point);
        list.put(col, new MyPoint(point));
    }

    @Override
    protected void greaterThan(String col, double point) {
        // ToDo: constrain point to range
//        tableProperties.updateMinMax(getCurrentTable(), col, point,
//                (double)tableProperties.getPositiveInfinityLimit(getCurrentTable(), col));
        list.put(col, new MyInterval(point, Double.POSITIVE_INFINITY));
    }

    @Override
    protected void MinorThan(String col, double point) {
        // ToDo: constrain point to range. Change data-type to double
//        tableProperties.updateMinMax(getCurrentTable(), col,
//                (double)tableProperties.getNegativeInfinityLimit(getCurrentTable(), col), point);
        list.put(col, new MyInterval(Double.NEGATIVE_INFINITY, point));
    }
}
