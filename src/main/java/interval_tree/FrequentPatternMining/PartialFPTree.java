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

    interface DataProcessor{
        void delegate(FPTreeNode node, int dim, MyData[] data);
    }

    public static class PartialFPTreeBuilder{

        private PartialFPTree fpTree;

        public PartialFPTreeBuilder(SupportCount supportCount){
            fpTree = new PartialFPTree(supportCount);
        }

        public void insertTree(Set<String> transactions, MyData[] data){
            PartialFPTreeNode node = ((PartialFPTreeNode)fpTree.insertTree(transactions));

            for(int i = data.length-1; i >= 0; i--) {

                // ToDo: Maybe better to pass MyData without having to wrap it in MyVector
                node.updateMinMax(new MyVector(Arrays.copyOfRange(data, 0, i+1)));
                node = (PartialFPTreeNode)node.parent;
            }
        }

        public PartialFPTree getFPTree(){
            fpTree.initializeAllUnits();
            return fpTree;
        }

    }

    private PartialFPTree(SupportCount supportCount){
        super(supportCount);
        root = new PartialFPTreeNode(null, 0);
    }

    public void addData(Set<String> transactions, MyData[] data){
        processTransaction(this::addData, transactions, data);
    }

    public void validateData(Set<String> transactions, MyData[] data){
        processTransaction(this::validateData, transactions, data);
    }

    private void processTransaction(DataProcessor proc, Set<String> transactions, MyData[] data){
        Iterator<String> it = transactions.iterator();

        FPTreeNode node = root;

        boolean terminate = false;
        int dim = 1;
        while (it.hasNext() && !terminate) {
            String entry = it.next();

            node = node.getChild(entry);
            if(((double)node.getFrequency() / totalSupportCount >= minsup)) {
                proc.delegate(node, dim, data);
                dim++;
            }
            else {
                terminate = true;
            }
        }
    }

    // ToDo: Maybe closed item-sets would be better memory-wise for CLIQUE
    private void addData(FPTreeNode node, int dim, MyData[] data){
        if(node != null && node instanceof PartialFPTreeNode){
            ((PartialFPTreeNode) node).addData(new MyVector(Arrays.copyOfRange(data, 0, dim)));
        }
    }

    // ToDo: Maybe closed item-sets would be better memory-wise for CLIQUE
    private void validateData(FPTreeNode node, int dim, MyData[] data){
        if(node != null && node instanceof PartialFPTreeNode){
            ((PartialFPTreeNode) node).validateClusters(new MyVector(Arrays.copyOfRange(data, 0, dim)));
        }
    }

    private void initializeAllUnits(){
        for(LinkedList<FPTreeNode> list : header.values()){
            for(FPTreeNode node : list){
                ((PartialFPTreeNode) node).initDimensions();
            }
        }
    }

    public void generateAllClusters(){
        for(LinkedList<FPTreeNode> list : header.values()){
            for(FPTreeNode node : list){
                ((PartialFPTreeNode) node).findClusters();
            }
        }
    }

    void extractItemSet(FPTreeNode node, List<String> columns){
        if(((double)node.getFrequency() / totalSupportCount >= minsup)) {

            if (node instanceof PartialFPTreeNode) {
                indices.addAll(((PartialFPTreeNode) node).extractPartialIndexes(columns));
            }
        }
    }
}
