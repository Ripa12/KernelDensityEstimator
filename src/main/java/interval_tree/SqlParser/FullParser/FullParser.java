package interval_tree.SqlParser.FullParser;

import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.FullFPTree;
import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.FrequentPatternMining.SupportCount;
import interval_tree.SqlParser.AbstractParser;
import interval_tree.SqlParser.PartialParser.PopulateFPTreeParser;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyPoint;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.*;

/**
 * Created by Richard on 2018-03-04.
 */
public class FullParser extends AbstractParser {

    private FullFPTree.FullFPTreeBuilder fpTreeBuilder;

    private PriorityQueue<String> list;

    private SupportCount supportCount;

    public FullParser(SupportCount supportCount){
        this.supportCount = supportCount;

        this.fpTreeBuilder = new FullFPTree.FullFPTreeBuilder(supportCount);
        this.list = new PriorityQueue<>(Comparator.comparingInt(o -> this.supportCount.get(o)[0]));
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
        if(supportCount.isSufficient(column)) {
            list.add(column);
        }
    }

    @Override
    protected void equalsTo(String col, int point) {
        if(supportCount.isSufficient(col)) {
            list.add(col);
        }
    }
}
