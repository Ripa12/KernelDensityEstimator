package interval_tree.FrequentPatternMining.Partial;

import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.Factory.TableStats;
import interval_tree.FrequentPatternMining.AbstractFPTree;
import interval_tree.FrequentPatternMining.AbstractFPTreeNode;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyVector;

import javax.xml.transform.Result;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import static interval_tree.Globals.CLUSTER_MIN_SUP;
import static interval_tree.Globals.IDEAL_COVERAGE;
import static interval_tree.Globals.NR_OF_CELLS;

public class PartialFPTree extends AbstractFPTree {

    interface DataProcessor {
        void delegate(AbstractFPTreeNode node, int dim, MyData[] data);
    }

    public static class PartialFPTreeBuilder {

        private Map<String, PartialFPTree> fpTree;

        public PartialFPTreeBuilder(TableStats tableBaseProperties) {
            this.fpTree = new HashMap<>();

            for (String tableName : tableBaseProperties.getTableNames()) {
                fpTree.put(tableName, new PartialFPTree(tableBaseProperties, tableName));
            }
        }

        public void insertTree(String tableName, Set<String> transactions, MyData[] data) {
            PartialFPTreeNode node = ((PartialFPTreeNode) fpTree.get(tableName).insertTree(transactions.iterator()));

            for (int i = data.length - 1; i >= 0; i--) {

                // ToDo: Maybe better to pass MyData without having to wrap it in MyVector
                node.getClique().updateMinMax(new MyVector(Arrays.copyOfRange(data, 0, i + 1)));
                node = (PartialFPTreeNode) node.getParent();
            }
        }

        public Map<String, PartialFPTree> getFPTree() {
            for (PartialFPTree partialFPTree : fpTree.values()) {
                partialFPTree.initializeAllUnits();
            }
            return fpTree;
        }

    }

    private List<IIndex> fullIndexes;
    private List<IIndex> partialIndexs;

    private PartialFPTree(TableStats tableBaseProperties, String tableName) {
        super(tableBaseProperties, tableName, new PartialFPTreeNode(null, "", 0));
        fullIndexes = new LinkedList<>();
        partialIndexs = new LinkedList<>();
    }

    public void addData(Set<String> transactions, MyData[] data) {
        processTransaction(this::addData, transactions, data);
    }

    public void validateData(Set<String> transactions, MyData[] data) {
        processTransaction(this::validateData, transactions, data);
    }

    private void processTransaction(DataProcessor proc, Set<String> transactions, MyData[] data) {
        Iterator<String> it = transactions.iterator();

        AbstractFPTreeNode node = getRoot();

        boolean terminate = false;
        int dim = 1;
        while (it.hasNext() && !terminate) {
            String entry = it.next();

            node = node.getChild(entry);
//            if(node != null && ((double)node.getFrequency() / totalSupportCount >= minSup)) {
            if (node != null) {
                proc.delegate(node, dim, data);
                dim++;
            } else {
                terminate = true;
            }
        }
    }

    public List<IIndex> getFullIndexes() {
        return fullIndexes;
    }

    public List<IIndex> getPartialIndexes() {
        return partialIndexs;
    }

    // ToDo: Maybe closed item-sets would be better memory-wise for CLIQUE (maybe not)
    private void addData(AbstractFPTreeNode node, int dim, MyData[] data) {
        if (node != null && node instanceof PartialFPTreeNode) {
            ((PartialFPTreeNode) node).getClique().insertData(new MyVector(Arrays.copyOfRange(data, 0, dim)));
        }
    }

    private void validateData(AbstractFPTreeNode node, int dim, MyData[] data) {
        if (node != null && node instanceof PartialFPTreeNode) {
            ((PartialFPTreeNode) node).getClique().validateClusters(new MyVector(Arrays.copyOfRange(data, 0, dim)));
        }
    }

    private void initializeAllUnits() {
//        for (LinkedList<AbstractFPTreeNode> list : header.values()) {
//            //ToDo: This takes a lot of time!
//            for (AbstractFPTreeNode node : list) {
//                ((PartialFPTreeNode) node).getClique().initOneDimensionalUnits();
//            }
//        }

        // ToDo: Multi-threading does seem to help here slightly
        ExecutorService EXEC = Executors.newCachedThreadPool();
        List<Callable<Result>> tasks = new ArrayList<>();
        for (LinkedList<AbstractFPTreeNode> list : header.values()) {
            //ToDo: perhaps make AbstractFPTreeNode extend Callable
            for (AbstractFPTreeNode node : list) {
                Callable<Result> c = () -> {
                    ((PartialFPTreeNode) node).getClique().initOneDimensionalUnits();
                    return null;
                };
                tasks.add(c);
            }
        }
        try {
            List<Future<Result>> results = EXEC.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void generateAllClusters() {
//        for (LinkedList<AbstractFPTreeNode> list : header.values()) {
//            for (AbstractFPTreeNode node : list) {
//                ((PartialFPTreeNode) node).getClique().findClusters();
//            }
//        }

        ExecutorService EXEC = Executors.newCachedThreadPool();
        List<Callable<Result>> tasks = new ArrayList<>();
        for (LinkedList<AbstractFPTreeNode> list : header.values()) {
            for (AbstractFPTreeNode node : list) {
                Callable<Result> c = () -> {
                    ((PartialFPTreeNode) node).getClique().findClusters();
                    return null;
                };
                tasks.add(c);
            }
        }
        try {
            List<Future<Result>> results = EXEC.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractFPTreeNode createRoot() {
        return new PartialFPTreeNode(null, "");
    }

    public void extractItemSets(double minsup) {
        this.minSup = minsup;
        LinkedList<String> cols = new LinkedList<>();
        extractItemSets(getRoot(), cols);
    }

    private void extractItemSets(AbstractFPTreeNode node, LinkedList<String> cols) {

        for (String col : node.getNamesOfChildren()) {
            cols.add(col);
            extractItemSets(node.getChild(col), cols);
            cols.removeLast();
        }

        extractItemSet(node, cols);
    }

    private void extractItemSet(AbstractFPTreeNode node, List<String> columns) {
//        if(((double)node.getFrequency() / totalSupportCount >= minSup))
        {
            if (node instanceof PartialFPTreeNode) {
                ((PartialFPTreeNode) node).extractIndexes(tableName, columns);
            }
        }
    }

    @Override
    protected void extractItemSet(double frequency, List<String> columns, List<AbstractFPTreeNode> treeNodes) {
        if (treeNodes.size() == 0)
            return;

        List<IIndex> tempList = treeNodes.get(0).extractIndexes(frequency, tableName, columns);
        fullIndexes.add(tempList.remove(0));

        for (int i = 1; i < treeNodes.size(); i++) {
            tempList.addAll(0, treeNodes.get(i).extractIndexes(frequency, tableName, columns));
            tempList.remove(0);
        }

//        merge(columns, 0, tempList, partialIndexs);
        merge(columns, tempList, partialIndexs);
    }

    private void merge(List<String> columns, List<IIndex> indexes, List<IIndex> finalList) {
        Clique clique = new Clique<>(NR_OF_CELLS, CLUSTER_MIN_SUP, IDEAL_COVERAGE, false, columns.size());


        List<MyVector> vecs = new ArrayList<>(indexes.size());
        for (int i = 0; i < indexes.size(); i++) {
            CompoundPartialIndex tempIdx = ((CompoundPartialIndex) indexes.get(i));
            MyVector vec = tempIdx.getVector(columns);
            vecs.add(vec);
            clique.updateMinMax(vec);
        }

        clique.initOneDimensionalUnits();
        for (int i = 0; i < vecs.size(); i++) {
            clique.insertData(vecs.get(i), ((int) indexes.get(i).getValue()));
        }

        clique.findClusters();

        for (int i = 0; i < vecs.size(); i++) {
            clique.validateClusters(vecs.get(i), ((int) indexes.get(i).getValue()));
        }

        finalList.addAll(clique.getClusters(tableName, columns));
    }

//    private void merge(List<String> columns, int dim, List<IIndex> indexes, List<IIndex> finalList) {
//        if (dim >= columns.size()) {
//            for (int j = indexes.size() - 1; j > 0; j--) {
//                ((CompoundPartialIndex) indexes.get(j - 1)).merge(((CompoundPartialIndex) indexes.get(j)));
//                indexes.remove(j);
//            }
//
//            indexes.removeIf(iIndex -> (iIndex.getValue()) < minSup);
//            finalList.addAll(indexes);
//            return;
//        }
//
//        List<List<IIndex>> idxsList = CompoundPartialIndex.merge(indexes, columns.get(dim));
//        for (List<IIndex> idxs : idxsList) {
//            merge(columns, dim + 1, idxs, finalList);
//        }
//    }

}
