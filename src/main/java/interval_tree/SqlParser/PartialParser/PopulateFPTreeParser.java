package interval_tree.SqlParser.PartialParser;

import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.SqlParser.AbstractParser;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
import interval_tree.SubspaceClustering.MyPoint;

import java.util.Comparator;
import java.util.TreeMap;


// ToDo: maybe create an abstract parent for both PopulateFPTreeParser & InitializeFPTreeParser
public class PopulateFPTreeParser extends AbstractParser {

    private PartialFPTree fpTree;
    private TreeMap<String, MyData> list;

    PopulateFPTreeParser(PartialFPTree fpTree, TreeMap<String, MyData> list){
        this.fpTree = fpTree;
        this.list = list;
    }

//    public PartialFPTree getFpTree(){
//        return fpTree;
//    }

    public ValidateFPTreeParser buildValidateFPTreeParser(){
        return new ValidateFPTreeParser(fpTree, list);
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
        fpTree.addData(list.keySet(), list.values().toArray(new MyData[0]));
    }
}
