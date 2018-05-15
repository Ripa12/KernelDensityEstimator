package interval_tree.SqlParser.PartialParser;

import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.SqlParser.AbstractParser;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
import interval_tree.SubspaceClustering.MyPoint;

import java.util.TreeMap;

/**
 * Created by Richard on 2018-05-15.
 */
public class ValidateFPTreeParser extends AbstractParser {

    private PartialFPTree fpTree;
    private TreeMap<String, MyData> list;

    ValidateFPTreeParser(PartialFPTree fpTree, TreeMap<String, MyData> list){
        this.fpTree = fpTree;
        this.list = list;

        this.fpTree.generateAllClusters();
    }

    public PartialFPTree getFpTree(){
        return fpTree;
    }

    @Override
    protected void finiteInterval(String column, int start, int end) {
        list.put(column, new MyInterval(start, end));
    }

    @Override
    protected void equalsTo(String col, int point) {
        list.put(col, new MyPoint(point));
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        fpTree.validateData(list.keySet(), list.values().toArray(new MyData[0]));
    }

}
