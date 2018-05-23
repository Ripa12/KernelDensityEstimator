package interval_tree.FrequentPatternMining;

import interval_tree.FrequentPatternMining.SupportCount.ColumnCount;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyVector;

import java.util.*;

public class PartialFPTree extends AbstractFPTree{

    interface DataProcessor{
        void delegate(AbstractFPTreeNode node, int dim, MyData[] data);
    }

    public static class PartialFPTreeBuilder{

        private PartialFPTree fpTree;

        public PartialFPTreeBuilder(ColumnCount columnCount){
            fpTree = new PartialFPTree(columnCount);
        }

        public void insertTree(Set<String> transactions, MyData[] data){
            PartialFPTreeNode node = ((PartialFPTreeNode)fpTree.insertTree(transactions.iterator()));

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

    private PartialFPTree(ColumnCount columnCount){
        super(columnCount, new PartialFPTreeNode(null, 0));
    }

    public void addData(Set<String> transactions, MyData[] data){
        processTransaction(this::addData, transactions, data);
    }

    public void validateData(Set<String> transactions, MyData[] data){
        processTransaction(this::validateData, transactions, data);
    }

    private void processTransaction(DataProcessor proc, Set<String> transactions, MyData[] data){
        Iterator<String> it = transactions.iterator();

        AbstractFPTreeNode node = getRoot();

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
    private void addData(AbstractFPTreeNode node, int dim, MyData[] data){
        if(node != null && node instanceof PartialFPTreeNode){
            ((PartialFPTreeNode) node).addData(new MyVector(Arrays.copyOfRange(data, 0, dim)));
        }
    }

    // ToDo: Maybe closed item-sets would be better memory-wise for CLIQUE
    private void validateData(AbstractFPTreeNode node, int dim, MyData[] data){
        if(node != null && node instanceof PartialFPTreeNode){
            ((PartialFPTreeNode) node).validateClusters(new MyVector(Arrays.copyOfRange(data, 0, dim)));
        }
    }

    private void initializeAllUnits(){
        for(LinkedList<AbstractFPTreeNode> list : header.values()){
            for(AbstractFPTreeNode node : list){
                ((PartialFPTreeNode) node).initDimensions();
            }
        }
    }

    public void generateAllClusters(){
        for(LinkedList<AbstractFPTreeNode> list : header.values()){
            for(AbstractFPTreeNode node : list){
                ((PartialFPTreeNode) node).findClusters();
            }
        }
    }

    void extractItemSet(AbstractFPTreeNode node, List<String> columns){
        if(((double)node.getFrequency() / totalSupportCount >= minsup)) {

            if (node instanceof PartialFPTreeNode) {
                indices.addAll(((PartialFPTreeNode) node).extractIndexes(columns));
            }
        }
    }
}
