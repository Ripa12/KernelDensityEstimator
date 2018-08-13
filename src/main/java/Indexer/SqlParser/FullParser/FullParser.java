package Indexer.SqlParser.FullParser;

import Indexer.Factory.TableStats;
import Indexer.FrequentPatternMining.Full.FullFPTree;
import Indexer.GUI.Menu;
import Indexer.SqlParser.AbstractParser;

import java.util.*;

/**
 * Created by Richard on 2018-03-04.
 */
public class FullParser extends AbstractParser {

    private FullFPTree.FullFPTreeBuilder fpTreeBuilder;

    private TreeSet<String> list;

    public FullParser(Menu menu, TableStats tableBaseProperties){
        super(tableBaseProperties);

        this.fpTreeBuilder = new FullFPTree.FullFPTreeBuilder(menu, tableBaseProperties);
        this.list = new TreeSet<>(Comparator.comparingDouble(o -> this.tableStats.getSupport(getCurrentTable(), o)));
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
        if(tableStats.isSufficient(getCurrentTable(), column)) {
            list.add(column);
        }
    }

    @Override
    protected void equalsTo(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.add(col);
        }
    }

    @Override
    protected void greaterThan(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.add(col);
        }
    }

    @Override
    protected void MinorThan(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.add(col);
        }
    }
}
