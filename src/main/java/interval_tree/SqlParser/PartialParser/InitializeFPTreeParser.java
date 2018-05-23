package interval_tree.SqlParser.PartialParser;

import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.FrequentPatternMining.SupportCount.ColumnCount;
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

    private ColumnCount columnCount;

    public InitializeFPTreeParser(ColumnCount columnCount){
        this.columnCount = columnCount;

        this.fpTreeBuilder = new PartialFPTree.PartialFPTreeBuilder(columnCount);
        this.list = new TreeMap<>(Comparator.comparingInt(o -> this.columnCount.get(o)[0]));
    }

    public PopulateFPTreeParser buildFPTreeParser(){
        return new PopulateFPTreeParser(this.fpTreeBuilder.getFPTree(), list);
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        fpTreeBuilder.insertTree(list.keySet(), list.values().toArray(new MyData[0]));
    }

    @Override
    protected void equalsTo(String col, int point) {
        if(columnCount.isSufficient(col)) {
            list.put(col, new MyPoint(point));
        }
    }

    @Override
    protected void finiteInterval(String column, int start, int end) {
        if(columnCount.isSufficient(column)) {
            list.put(column, new MyInterval(start, end));
        }
    }

}
