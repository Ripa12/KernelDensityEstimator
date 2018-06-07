package interval_tree.SqlParser.PartialParser;

import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;
import interval_tree.SqlParser.AbstractParser;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
import interval_tree.SubspaceClustering.MyPoint;

import java.util.*;

/**
 * Created by Richard on 2018-03-04.
 */
public class InitializeFPTreeParser extends AbstractParser {

    private PartialFPTree.PartialFPTreeBuilder fpTreeBuilder;

    private TreeMap<String, MyData> list;

    public InitializeFPTreeParser(TableProperties tableProperties){
        super(tableProperties);

        this.fpTreeBuilder = new PartialFPTree.PartialFPTreeBuilder(tableProperties);
        this.list = new TreeMap<>(Comparator.comparingDouble(o -> this.tableProperties.getSupport(getCurrentTable(), o)));
    }

    public PopulateFPTreeParser buildFPTreeParser(){
        return new PopulateFPTreeParser(tableProperties, this.fpTreeBuilder.getFPTree(), list);
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        fpTreeBuilder.insertTree(getCurrentTable(), list.keySet(), list.values().toArray(new MyData[0]));
    }

    @Override
    protected void equalsTo(String col, double point) {
        if(tableProperties.isSufficient(getCurrentTable(), col)) {
            // ToDo: Instead of clamping, ignore if point is outside constrained range!
            list.put(col, new MyPoint(tableProperties.constrainToRange(getCurrentTable(), col, point)));
//            list.put(col, new MyPoint(point));
        }
    }

    @Override
    protected void greaterThan(String col, double point) {
        if(tableProperties.isSufficient(getCurrentTable(), col)) {
            list.put(col, new MyInterval(tableProperties.constrainToRange(getCurrentTable(), col, point),
                        tableProperties.getPositiveInfinityLimit(getCurrentTable(), col)));
        }
    }

    @Override
    protected void MinorThan(String col, double point) {
        if(tableProperties.isSufficient(getCurrentTable(), col)) {
            list.put(col, new MyInterval(tableProperties.getNegativeInfinityLimit(getCurrentTable(), col),
                    tableProperties.constrainToRange(getCurrentTable(), col, point)));
        }
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
        if(tableProperties.isSufficient(getCurrentTable(), column)) {
            list.put(column, new MyInterval(tableProperties.constrainToRange(getCurrentTable(), column, start),
                    tableProperties.constrainToRange(getCurrentTable(), column, end)));
//            list.put(column, new MyInterval(start, end));
        }
    }
}