package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.CandidateIndex.PartialIndex;
import interval_tree.DataStructure.IntervalTree;

import java.util.*;

// http://www.programering.com/a/MDM2QTNwATg.html
// https://wimleers.com/sites/wimleers.com/files/FP-Growth%20presentation%20handouts%20—%C2%A0Florian%20Verhein.pdf
// http://www.cis.hut.fi/Opinnot/T-61.6020/2008/fptree.pdf


public class PartialFPTree {

    public class FPTreeNode{

        private FPTreeNode parent;
        private TreeMap<String, FPTreeNode> children;
        private IntervalTree[] intervalTrees;

        public FPTreeNode(FPTreeNode parent, int level) {
            this.intervalTrees = new IntervalTree[level];
            for (int i = 0; i < level; i++) {
                this.intervalTrees[i] = new IntervalTree("");
            }
//            Arrays.fill(this.intervalTree, new IntervalTree());

            this.children = new TreeMap<>();
            this.parent = parent;
        }

        public void addData(int index, IntervalTree.NodeData data){
            intervalTrees[index].insert(data);
        }

    }

    private FPTreeNode root;
    private HashMap<String, LinkedList<FPTreeNode>> header;
    private int totalSupportCount;
    private double minsup;

    private List<CompoundPartialIndex> partialIndices;

    public PartialFPTree(Map<String, Integer[]> supportCount){
        root = new FPTreeNode(null, 0);

        header = new HashMap<>();
        supportCount.keySet().forEach(k -> header.put(k, new LinkedList<>()));

        totalSupportCount = 0;
        supportCount.values().forEach(k -> totalSupportCount += k[0]);
    }

    public void insertTree(Set<String> transactions, IntervalTree.NodeData[] data){

        insertTree(transactions.iterator(), data, 1, root);
    }

    private void insertTree(Iterator<String> transaction, IntervalTree.NodeData[] data, int level, FPTreeNode node){

        if(!transaction.hasNext()){

            for(int i = 0; i < data.length; i++) {
                node.addData(i, data[i]);
            }

            return;
        }


        String entry = transaction.next();
        FPTreeNode temp;
        if(node.children.containsKey(entry)){
            temp = node.children.get(entry);
            //temp.addData(entry);
        }
        else{
            temp = new FPTreeNode(node, level);
            //temp.addData(entry.getValue());

            node.children.put(entry, temp);
            header.get(entry).add(temp);
        }

        insertTree(transaction, data, level + 1, temp);
    }

    public List<CompoundPartialIndex> getPartialIndices(){
        return partialIndices;
    }

    public void extractItemSets(double minsup){
        assert minsup <= 1.0 && minsup >= 0.0;
        this.minsup = minsup;
        partialIndices = new LinkedList<>();

        for(LinkedList<FPTreeNode> list : header.values()){
            for(FPTreeNode node : list){
                extractItemSet(node);
            }
        }
    }

    private void extractItemSet(FPTreeNode node){
        if((node.intervalTrees[0].getFrequency() / totalSupportCount) < minsup)
            return;

        CompoundPartialIndex compPartialIndex = new CompoundPartialIndex();

        double[][] interval;
        for (IntervalTree tree : node.intervalTrees) {
            tree.iterate();

            interval = tree.predictIntervals(0.9);

            CompoundPartialIndex.Predicate pred = new CompoundPartialIndex.Predicate();
            for (double[] doubles : interval) {
                if(((double)tree.getFrequency() * (doubles[2]) / totalSupportCount) >= minsup) {
                    pred.addPartialIndex(new PartialIndex(((double) node.intervalTrees[node.intervalTrees.length-1].getFrequency() * (doubles[2])), 0, tree.getColumn(), (int) doubles[0], (int) doubles[1]));
                }
            }
            compPartialIndex.addCompoundPredicate(pred);
        }
        partialIndices.add(compPartialIndex);
    }
}
