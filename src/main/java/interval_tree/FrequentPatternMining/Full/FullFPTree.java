package interval_tree.FrequentPatternMining.Full;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.Factory.TableStats;
import interval_tree.FrequentPatternMining.AbstractFPTree;
import interval_tree.FrequentPatternMining.AbstractFPTreeNode;

import java.util.*;

/**
 * Created by Richard on 2018-05-19.
 */
public class FullFPTree extends AbstractFPTree {

    public static class FullFPTreeBuilder{

        private Map<String, FullFPTree> fpTree;

        public FullFPTreeBuilder(TableStats tableBaseProperties){
            this.fpTree = new HashMap<>();

            for (String tableName : tableBaseProperties.getTableNames()) {
                fpTree.put(tableName, new FullFPTree(tableBaseProperties, tableName));
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

    private FullFPTree(TableStats tableBaseProperties, String tableName){
        super(tableBaseProperties, tableName, new FullFPTreeNode(null, ""));

        fullIndexes = new HashSet<>();
    }

    public Set<IIndex> getFullIndexes(){
        return fullIndexes;
    }

    @Override
    protected void extractItemSet(double frequency, List<String> columns, List<AbstractFPTreeNode> treeNodes) {
        if(treeNodes.size() == 0)
            return;

        fullIndexes.addAll(treeNodes.get(0).extractIndexes(frequency, tableName, columns));
    }

    @Override
    public AbstractFPTreeNode createRoot() {
        return new FullFPTreeNode(null, "");
    }
}
