package interval_tree.SqlParser.FullParser;

import interval_tree.FrequentPatternMining.FullFPTree;
import interval_tree.FrequentPatternMining.SupportCount.ColumnCount;
import interval_tree.SqlParser.AbstractParser;

import java.util.*;

/**
 * Created by Richard on 2018-03-04.
 */
public class FullParser extends AbstractParser {

    private FullFPTree.FullFPTreeBuilder fpTreeBuilder;

    private PriorityQueue<String> list;

    private ColumnCount columnCount;

    public FullParser(ColumnCount columnCount){
        this.columnCount = columnCount;

        this.fpTreeBuilder = new FullFPTree.FullFPTreeBuilder(columnCount);
        this.list = new PriorityQueue<>(Comparator.comparingInt(o -> this.columnCount.get(o)[0]));
    }

    public FullFPTree getFpTree(){
        return fpTreeBuilder.getFPTree();
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        fpTreeBuilder.insertTree(list.iterator());
    }

    @Override
    protected void finiteInterval(String column, int start, int end) {
        if(columnCount.isSufficient(column)) {
            list.add(column);
        }
    }

    @Override
    protected void equalsTo(String col, int point) {
        if(columnCount.isSufficient(col)) {
            list.add(col);
        }
    }
}
