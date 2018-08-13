package Indexer.FrequentPatternMining.Full;

import Indexer.CandidateIndex.IIndex;
import Indexer.Factory.TableStats;
import Indexer.FrequentPatternMining.AbstractFPTree;
import Indexer.FrequentPatternMining.AbstractFPTreeNode;
import Indexer.GUI.Menu;

import java.util.*;

/**
 * Created by Richard on 2018-05-19.
 */
public class FullFPTree extends AbstractFPTree {

    public static class FullFPTreeBuilder{

        private Map<String, FullFPTree> fpTree;
        private Menu menu;

        public FullFPTreeBuilder(Menu menu, TableStats tableBaseProperties){
            this.fpTree = new HashMap<>();

            for (String tableName : tableBaseProperties.getTableNames()) {
                fpTree.put(tableName, new FullFPTree(menu, tableBaseProperties, tableName));
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

    private FullFPTree(Menu menu, TableStats tableBaseProperties, String tableName){
        super(menu, tableBaseProperties, tableName, new FullFPTreeNode(null, ""));

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
