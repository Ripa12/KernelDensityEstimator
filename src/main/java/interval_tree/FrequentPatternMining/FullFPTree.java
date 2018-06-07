package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
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

    private Set<IIndex> fullIndexes;

    private FullFPTree(TableCount tableCount, String tableName){
        super(tableCount, tableName, new FullFPTreeNode(null, ""));

        fullIndexes = new HashSet<>();
    }

    public Set<IIndex> getFullIndexes(){
        return fullIndexes;
    }

    @Override
    void extractItemSet(double frequency, List<String> columns, List<AbstractFPTreeNode> treeNodes) {
        if(treeNodes.size() == 0)
            return;

        fullIndexes.addAll(treeNodes.get(0).extractIndexes(frequency, tableName, columns));
    }

    @Override
    public AbstractFPTreeNode createRoot() {
        return new FullFPTreeNode(null, "");
    }
}
