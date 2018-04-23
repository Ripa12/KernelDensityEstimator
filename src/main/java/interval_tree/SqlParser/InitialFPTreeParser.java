package interval_tree.SqlParser;

import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
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
public class InitialFPTreeParser extends AbstractParser {

    private PartialFPTree.PartialFPTreeBuilder fpTreeBuilder;

    private TreeMap<String, MyData> list;

    public InitialFPTreeParser(Map<String, Integer[]> supportCount){
        this.fpTreeBuilder = new PartialFPTree.PartialFPTreeBuilder(supportCount);
        list = new TreeMap<>(Comparator.comparingInt(o -> supportCount.get(o)[0]));
    }

    public FPTreeParser buildFPTreeParser(){
        return new FPTreeParser(this.fpTreeBuilder.getFPTree(), list);
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
    void equalsTo(String col, int point) {
        list.put(col, new MyPoint(point));
    }

    @Override
    void finiteInterval(String column, int start, int end) {
        list.put(column, new MyInterval(start, end));
    }

}
