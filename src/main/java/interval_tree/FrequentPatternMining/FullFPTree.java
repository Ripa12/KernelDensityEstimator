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
//            indices.addAll(node.extractIndexes(tableName, columns, tableCount));
        }
    }

    @Override
    public AbstractFPTreeNode createRoot() {
        return new FullFPTreeNode(null, "");
    }
}
