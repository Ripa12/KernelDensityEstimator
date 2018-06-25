package interval_tree.SqlParser.PartialParser;

import interval_tree.Factory.TableStats;
import interval_tree.FrequentPatternMining.Partial.PartialFPTree;
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

    public InitializeFPTreeParser(TableStats tableBaseProperties){
        super(tableBaseProperties);

        this.fpTreeBuilder = new PartialFPTree.PartialFPTreeBuilder(tableBaseProperties);
        this.list = new TreeMap<>(Comparator.comparingDouble(o ->
                tableBaseProperties.getSupport(getCurrentTable(), o)));
    }

    public PopulateFPTreeParser buildFPTreeParser(){
        return new PopulateFPTreeParser(tableStats, this.fpTreeBuilder.getFPTree(), list);
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
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            // ToDo: Instead of clamping, ignore if point is outside constrained range!
            list.put(col, new MyPoint(point));
        }
    }

    @Override
    protected void greaterThan(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.put(col, new MyPoint(point));
        }
    }

    @Override
    protected void MinorThan(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.put(col, new MyPoint(point));
        }
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
        if(tableStats.isSufficient(getCurrentTable(), column)) {
            list.put(column, new MyInterval(start, end));
        }
    }
}