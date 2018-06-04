package interval_tree.SqlParser.PartialParser;

import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.FrequentPatternMining.SupportCount.TableCount;
import interval_tree.SqlParser.AbstractParser;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyInterval;
import interval_tree.SubspaceClustering.MyPoint;

import java.util.*;

/**
 * Created by Richard on 2018-05-15.
 */
public class ValidateFPTreeParser extends AbstractParser {

    private Map<String, PartialFPTree> fpTree;
    private TreeMap<String, MyData> list;

    ValidateFPTreeParser(TableCount tc, Map<String, PartialFPTree> fpTree, TreeMap<String, MyData> list){
        super(tc);
        this.fpTree = fpTree;
        this.list = list;

        for (PartialFPTree partialFPTree : this.fpTree.values()) {
            partialFPTree.generateAllClusters();
        }
    }

    public List<PartialFPTree> getFpTree(){
        return new ArrayList<>(fpTree.values());
    }

    @Override
    protected void finiteInterval(String column, double start, double end) {
        list.put(column, new MyInterval(start, end));
    }

    @Override
    protected void equalsTo(String col, double point) {
        list.put(col, new MyPoint(point));
    }

    @Override
    protected void greaterThan(String col, double point) {
        list.put(col, new MyInterval(point,
                tableCount.getPositiveInfinityLimit(getCurrentTable(), col)));
    }

    @Override
    protected void MinorThan(String col, double point) {
        list.put(col, new MyInterval(tableCount.getNegativeInfinityLimit(getCurrentTable(), col),
                point));
    }

    @Override
    public void before() {
        list.clear();
    }

    @Override
    public void after() {
        fpTree.get(getCurrentTable()).validateData(list.keySet(), list.values().toArray(new MyData[0]));
    }

}
