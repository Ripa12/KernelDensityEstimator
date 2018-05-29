package interval_tree.FrequentPatternMining;

import interval_tree.FrequentPatternMining.SupportCount.TableCount;

import java.util.*;

/**
 * Created by Richard on 2018-05-19.
 */
public class FullFPTree extends AbstractFPTree{

    public static class FullFPTreeBuilder{

        private Map<String, FullFPTree> fpTree;

        public FullFPTreeBuilder(TableCount tableCount){
            this.fpTree = new HashMap<>();

            for (String tableName : tableCount.getTableNames()) {
                fpTree.put(tableName, new FullFPTree(tableCount, tableName));
            }
        }

        public void insertTree(String tableName, Iterator<String> transactions){
            AbstractFPTreeNode node = (fpTree.get(tableName).insertTree(transactions));
        }

        public List<FullFPTree> getFPTree(){
            return new ArrayList<>(fpTree.values());
        }

    }

    private FullFPTree(TableCount tableCount, String tableName){
        super(tableCount, tableName, new FullFPTreeNode(null, ""));
    }


    void extractItemSet(AbstractFPTreeNode node, List<String> columns){
        if(((double)node.getFrequency() / totalSupportCount >= minsup)) {
            indices.addAll(node.extractIndexes(tableName, columns));
        }
    }


    //// new

//    public AbstractFPTree conditionalTree(List<List<AbstractFPTreeNode>> paths) {
//        AbstractFPTree tree = new FullFPTree(tableCount, tableName);
//
//        Set<String> items = new HashSet<>();
//        String conditionItem = null;
//        for (List<AbstractFPTreeNode> path : paths) {
//            AbstractFPTreeNode point = tree.getRoot();
//            if (conditionItem == null) {
//                conditionItem = path.get(path.size() - 1).column;
//            }
//
//            for (AbstractFPTreeNode node : path) {
//                AbstractFPTreeNode nextPoint = point.getChild(node.column);
//                if (nextPoint == null) {
//                    items.add(node.column);
//                    int count;
//                    if (node.column.equals(conditionItem)) {
//                        count = node.getFrequency();
//                    } else {
//                        count = 0; // nextPoint is already zero when initialized
//                    }
//                    nextPoint = nextPoint.clone(node.column);
//                    nextPoint.setFrequency(count);
//
//                    point.addChild(nextPoint);
//                    tree.header.get(nextPoint.column).add(nextPoint);//tree.updateNeighbors(nextPoint);
//                }
//                point = nextPoint;
//            }
//        }
//
//        for (List<AbstractFPTreeNode> path : tree.getPrefixPaths(conditionItem)) {
//            Integer count = path.get(path.size() - 1).getFrequency();
//            for (int i = path.size() - 1; i >= 0; i--) {
//                AbstractFPTreeNode node = path.get(i);
//                node.setFrequency(count);
//            }
//        }
//
//        for (String item : items) {
//            double support = 0;
//            for (AbstractFPTreeNode node : tree.getNodes(item)) {
//                support += node.getFrequency();
//            }
//            for (AbstractFPTreeNode node : tree.getNodes(item)) {
//                if (support / totalSupportCount < minsup) {
//                    if (node.parent != null) {
//                        node.getParent().removeChild(node);
//                    }
//                }
//            }
//        }
//
//        for (FPNode node : tree.getNodes(conditionItem)) {
//            if (node.hasParent()) {
//                node.getParent().removeChild(node);
//            }
//        }
//        return tree;
//    }

    @Override
    public AbstractFPTreeNode createRoot() {
        return new FullFPTreeNode(null, "");
    }
}
