package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyVector;

import java.util.*;

public class PartialFPTree extends AbstractFPTree{

    interface DataProcessor{
        void delegate(AbstractFPTreeNode node, int dim, MyData[] data);
    }

    public static class PartialFPTreeBuilder{

        private Map<String, PartialFPTree> fpTree;

        public PartialFPTreeBuilder(TableProperties tableProperties){
            this.fpTree = new HashMap<>();

            for (String tableName : tableProperties.getTableNames()) {
                fpTree.put(tableName, new PartialFPTree(tableProperties, tableName));
            }
        }

        public void insertTree(String tableName, Set<String> transactions, MyData[] data){
            PartialFPTreeNode node = ((PartialFPTreeNode)fpTree.get(tableName).insertTree(transactions.iterator()));

            for(int i = data.length-1; i >= 0; i--) {

                // ToDo: Maybe better to pass MyData without having to wrap it in MyVector
                node.updateMinMax(new MyVector(Arrays.copyOfRange(data, 0, i+1)));
                node = (PartialFPTreeNode)node.getParent();
            }
        }

        public Map<String, PartialFPTree> getFPTree(){
            for (PartialFPTree partialFPTree : fpTree.values()) {
                partialFPTree.initializeAllUnits();
            }
            return fpTree;
        }

    }

    private List<IIndex> fullIndexes;
    private List<IIndex> partialIndexs;

    private PartialFPTree(TableProperties tableProperties, String tableName){
        super(tableProperties, tableName, new PartialFPTreeNode(null,"", 0));
        fullIndexes = new LinkedList<>();
        partialIndexs = new LinkedList<>();
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
            if(node != null && ((double)node.getFrequency() / totalSupportCount >= minsup)) {
                proc.delegate(node, dim, data);
                dim++;
            }
            else {
                terminate = true;
            }
        }
    }

    public List<IIndex> getFullIndexes(){
        return fullIndexes;
    }

    public List<IIndex> getPartialIndexes(){
        return partialIndexs;
    }

    // ToDo: Maybe closed item-sets would be better memory-wise for CLIQUE (maybe not)
    private void addData(AbstractFPTreeNode node, int dim, MyData[] data){
        if(node != null && node instanceof PartialFPTreeNode){
            ((PartialFPTreeNode) node).addData(new MyVector(Arrays.copyOfRange(data, 0, dim)));
        }
    }

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

    public void extractItemSets(double minsup) {
        this.minsup = minsup;
        LinkedList<String> cols = new LinkedList<>();
        extractItemSets(getRoot(), cols);
    }

    private void extractItemSets(AbstractFPTreeNode node, LinkedList<String> cols) {

        for (String col : node.children.keySet()) {
            cols.add(col);
            extractItemSets(node.children.get(col), cols);
            cols.removeLast();
        }

        extractItemSet(node, cols);
    }

    private void extractItemSet(AbstractFPTreeNode node, List<String> columns){
        if(((double)node.getFrequency() / totalSupportCount >= minsup)) {

            if (node instanceof PartialFPTreeNode) {
                node.extractIndexes(tableName, columns, tableProperties);
            }
        }
    }

    @Override
    void extractItemSet(double frequency, List<String> columns, List<AbstractFPTreeNode> treeNodes) {
        if(treeNodes.size() == 0)
            return;

        List<IIndex> tempList = treeNodes.get(0).extractIndexes(frequency, tableName, columns);
        fullIndexes.add(tempList.remove(0));
        partialIndexs.addAll(tempList);
        for (int i = 1; i < treeNodes.size(); i++) {
            tempList = treeNodes.get(i).extractIndexes(frequency, tableName, columns);
            tempList.remove(0);
            partialIndexs.addAll(tempList);
        }
    }

    @Override
    public AbstractFPTreeNode createRoot() {
        return new PartialFPTreeNode(null, "");
    }

}
