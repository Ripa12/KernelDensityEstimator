package interval_tree.FrequentPatternMining;

import interval_tree.FrequentPatternMining.SupportCount.ColumnCount;

import java.util.*;

/**
 * Created by Richard on 2018-05-19.
 */
public class FullFPTree extends AbstractFPTree{

    public static class FullFPTreeBuilder{

        private FullFPTree fpTree;

        public FullFPTreeBuilder(ColumnCount columnCount){
            fpTree = new FullFPTree(columnCount);
        }

        public void insertTree(Iterator<String> transactions){
            AbstractFPTreeNode node = (fpTree.insertTree(transactions));
        }

        public FullFPTree getFPTree(){
            return fpTree;
        }

    }

    private FullFPTree(ColumnCount columnCount){
        super(columnCount, new FullFPTreeNode(null));
    }

    void extractItemSet(AbstractFPTreeNode node, List<String> columns){
        if(((double)node.getFrequency() / totalSupportCount >= minsup)) {
            indices.addAll(node.extractIndexes(columns));
        }
    }
}
