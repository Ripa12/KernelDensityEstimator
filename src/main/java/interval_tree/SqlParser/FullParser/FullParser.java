package interval_tree.SqlParser.FullParser;

import interval_tree.FrequentPatternMining.FullFPTree;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;
import interval_tree.SqlParser.AbstractParser;

import java.util.*;

/**
 * Created by Richard on 2018-03-04.
 */
public class FullParser extends AbstractParser {

    private FullFPTree.FullFPTreeBuilder fpTreeBuilder;

    private TreeSet<String> list;

    public FullParser(TableProperties tableProperties){
        super(tableProperties);

        this.fpTreeBuilder = new FullFPTree.FullFPTreeBuilder(tableProperties);
        this.list = new TreeSet<>(Comparator.comparingDouble(o -> this.tableProperties.getSupport(getCurrentTable(), o)));
    }

    public List<FullFPTree> getFpTree(){
        return fpTreeBuilder.getFPTree();
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        fpTreeBuilder.insertTree(getCurrentTable(), list.iterator());
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
        if(tableProperties.isSufficient(getCurrentTable(), column)) {
            list.add(column);
        }
    }

    @Override
    protected void equalsTo(String col, double point) {
        if(tableProperties.isSufficient(getCurrentTable(), col)) {
            list.add(col);
        }
    }

    @Override
    protected void greaterThan(String col, double point) {
        if(tableProperties.isSufficient(getCurrentTable(), col)) {
            list.add(col);
        }
    }

    @Override
    protected void MinorThan(String col, double point) {
        if(tableProperties.isSufficient(getCurrentTable(), col)) {
            list.add(col);
        }
    }
}
