package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.CandidateIndex.PartialIndex;
import interval_tree.DataStructure.IntervalTree;
import interval_tree.Logger;

import java.util.*;

// http://www.programering.com/a/MDM2QTNwATg.html
// https://wimleers.com/sites/wimleers.com/files/FP-Growth%20presentation%20handouts%20—%C2%A0Florian%20Verhein.pdf
// http://www.cis.hut.fi/Opinnot/T-61.6020/2008/fptree.pdf


public class PartialFPTree extends AbstractFPTree{

    public static class PartialFPTreeBuilder{

        private PartialFPTree fpTree;

        public PartialFPTreeBuilder(Map<String, Integer[]> supportCount){
            fpTree = new PartialFPTree(supportCount);
        }

        public void insertTree(Set<String> transactions, IntervalTree.NodeData[] data){
            FPTreeNode node = fpTree.insertTree(transactions);

            for(int i = 0; i < data.length; i++) {
                node.updateMinMax(i, data[i]);
            }
        }

        public PartialFPTree getFPTree(){
            return fpTree;
        }

    }

    private PartialFPTree(Map<String, Integer[]> supportCount){
        super(supportCount);
    }

    public void addData(Set<String> transactions, IntervalTree.NodeData[] data){
        Iterator<String> it = transactions.iterator();

        FPTreeNode node = root;

        while (it.hasNext()) {
            String entry = it.next();

            if (node.children.containsKey(entry)) {
                node = node.children.get(entry);
            }
        }

        if(node != null){
            for(int i = 0; i < data.length; i++) {
                // ToDo: Only add data to frequent items (prune FP-Tree)
                node.addData(i, data[i]);
            }
        }
    }

    void extractItemSet(FPTreeNode node){
        if(((double)node.intervalTrees[0].getFrequency() / (double)totalSupportCount) < minsup)
            return;

        CompoundPartialIndex compPartialIndex = new CompoundPartialIndex();

        double[][] interval;
        for (IntervalTree tree : node.intervalTrees) {
            Logger.getInstance().setTimer();
            tree.iterate();
            Logger.getInstance().stopTimer("kernelLoadTime");

            Logger.getInstance().setTimer();
            interval = tree.predictIntervals(0.9);
            Logger.getInstance().stopTimer("kernelRunTime");

            CompoundPartialIndex.Predicate pred = new CompoundPartialIndex.Predicate();
            for (double[] doubles : interval) {
                if(((double)tree.getFrequency() * (doubles[2]) / totalSupportCount) >= minsup) {
                    pred.addPartialIndex(new PartialIndex(((double) node.intervalTrees[0].getFrequency() * (doubles[2])), 0, tree.getColumn(), (int) doubles[0], (int) doubles[1]));
                }
            }

            if(!pred.isEmpty())
                compPartialIndex.addCompoundPredicate(pred);
        }

        if(!compPartialIndex.isEmpty())
            indices.add(compPartialIndex);
    }
}
