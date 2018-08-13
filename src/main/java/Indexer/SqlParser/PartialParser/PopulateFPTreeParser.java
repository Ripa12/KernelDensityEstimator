package Indexer.SqlParser.PartialParser;

import Indexer.Factory.TableStats;
import Indexer.FrequentPatternMining.Partial.PartialFPTree;
import Indexer.SqlParser.AbstractParser;
import Indexer.SubspaceClustering.MyData;
import Indexer.SubspaceClustering.MyInterval;
import Indexer.SubspaceClustering.MyPoint;

import java.util.Map;
import java.util.TreeMap;


// ToDo: maybe create an abstract parent for both PopulateFPTreeParser & InitializeFPTreeParser
public class PopulateFPTreeParser extends AbstractParser {

    private Map<String, PartialFPTree> fpTree;
    private TreeMap<String, MyData> list;

    PopulateFPTreeParser(TableStats tc, Map<String, PartialFPTree> fpTree, TreeMap<String, MyData> list){
        super(tc);
        this.fpTree = fpTree;
        this.list = list;
    }

    public ValidateFPTreeParser buildValidateFPTreeParser(){
        return new ValidateFPTreeParser(tableStats, fpTree, list);
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
        if(tableStats.isSufficient(getCurrentTable(), column)) {
            list.put(column, new MyInterval(start, end));
        }
    }

    @Override
    protected void equalsTo(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.put(col, new MyPoint(point));
        }
    }

    @Override
    protected void greaterThan(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.put(col, new MyInterval(point,
                    Double.POSITIVE_INFINITY));
        }
    }

    @Override
    protected void MinorThan(String col, double point) {
        if(tableStats.isSufficient(getCurrentTable(), col)) {
            list.put(col, new MyInterval(Double.NEGATIVE_INFINITY,
                    point));
        }
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        fpTree.get(getCurrentTable()).addData(list.keySet(), list.values().toArray(new MyData[0]));
    }
}
