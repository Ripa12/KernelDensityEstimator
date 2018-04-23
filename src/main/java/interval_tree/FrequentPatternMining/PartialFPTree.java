package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.CandidateIndex.PartialIndex;
import interval_tree.DataStructure.IntervalTree;
import interval_tree.Logger;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyVector;

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

        public void insertTree(Set<String> transactions, MyData[] data){
            FPTreeNode node = fpTree.insertTree(transactions);

//            for(int i = 0; i < data.length; i++) {
//                node.updateMinMax(i, data[i]);
//            }
        }

        public PartialFPTree getFPTree(){
            return fpTree;
        }

    }

    private PartialFPTree(Map<String, Integer[]> supportCount){
        super(supportCount);
        root = new PartialFPTreeNode(null, 0);
    }

    public void addData(Set<String> transactions, MyData[] data){
        Iterator<String> it = transactions.iterator();

        FPTreeNode node = root;

        while (it.hasNext()) {
            String entry = it.next();

            node = node.getChild(entry);
        }

        if(node != null && node instanceof PartialFPTreeNode){

            int[] temp = new int[data.length];

            for(int i = 0; i < data.length; i++) {
                // ToDo: Only add data to frequent items (prune FP-Tree)
                temp[i] = data[i].getLow(); // ToDo: Only consider points as of now
            }

            ((PartialFPTreeNode) node).addData(new MyVector(temp));
        }
    }

    void extractItemSet(FPTreeNode node, List<String> columns){
        if(((double)node.getFrequency() / (double)totalSupportCount < minsup))
            return;

        if(node instanceof PartialFPTreeNode)
            indices.addAll(((PartialFPTreeNode)node).extractPartialIndexes(columns));

//        CompoundPartialIndex compPartialIndex = new CompoundPartialIndex();

//        double[][] interval;
//        for (IntervalTree tree : node.intervalTrees) {
//            Logger.getInstance().setTimer();
////            tree.iterate();
//            Logger.getInstance().stopTimer("kernelLoadTime");
//
//            Logger.getInstance().setTimer();
////            interval = tree.predictIntervals(0.9);
//            Logger.getInstance().stopTimer("kernelRunTime");
//
//            CompoundPartialIndex.Predicate pred = new CompoundPartialIndex.Predicate();
//            for (double[] doubles : interval) {
//                if(((double)tree.getFrequency() * (doubles[2]) / totalSupportCount) >= minsup) {
//                    pred.addPartialIndex(new PartialIndex(((double) node.intervalTrees[0].getFrequency() * (doubles[2])), 0, tree.getColumn(), (int) doubles[0], (int) doubles[1]));
//                }
//            }
//
//            if(!pred.isEmpty())
//                compPartialIndex.addCompoundPredicate(pred);
//        }

//        if(!compPartialIndex.isEmpty())
//            indices.add(compPartialIndex);
    }
}
