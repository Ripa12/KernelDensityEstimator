package interval_tree.FrequentPatternMining;

import de.lmu.ifi.dbs.elki.utilities.datastructures.iterator.Iter;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyVector;

import java.util.*;

/**
 * Created by Richard on 2018-05-19.
 */
public class FullFPTree extends AbstractFPTree{

    public static class FullFPTreeBuilder{

        private FullFPTree fpTree;

        public FullFPTreeBuilder(SupportCount supportCount){
            fpTree = new FullFPTree(supportCount);
        }

        public void insertTree(Iterator<String> transactions){
            AbstractFPTreeNode node = (fpTree.insertTree(transactions));
        }

        public FullFPTree getFPTree(){
            return fpTree;
        }

    }

    private FullFPTree(SupportCount supportCount){
        super(supportCount, new FullFPTreeNode(null));
    }

    void extractItemSet(AbstractFPTreeNode node, List<String> columns){
        if(((double)node.getFrequency() / totalSupportCount >= minsup)) {
            indices.addAll(node.extractIndexes(columns));
        }
    }
}
