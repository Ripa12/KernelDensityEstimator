package Indexer.SqlParser.PartialParser;

import Indexer.Factory.TableStats;
import Indexer.FrequentPatternMining.Partial.PartialFPTree;
import Indexer.GUI.Menu;
import Indexer.SqlParser.AbstractParser;
import Indexer.SubspaceClustering.MyData;
import Indexer.SubspaceClustering.MyInterval;
import Indexer.SubspaceClustering.MyPoint;

import java.util.*;

/**
 * Created by Richard on 2018-03-04.
 */
public class InitializeFPTreeParser extends AbstractParser {

    private PartialFPTree.PartialFPTreeBuilder fpTreeBuilder;

    private TreeMap<String, MyData> list;
    private Menu menu;

    public InitializeFPTreeParser(Menu menu, TableStats tableBaseProperties){
        super(tableBaseProperties);

        this.fpTreeBuilder = new PartialFPTree.PartialFPTreeBuilder(menu, tableBaseProperties);
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