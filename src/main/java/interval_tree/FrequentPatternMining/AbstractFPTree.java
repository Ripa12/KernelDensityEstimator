package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;

import javax.annotation.processing.SupportedOptions;
import java.util.*;

public abstract class AbstractFPTree {

    /**
     * Member class
     */

    /**
     * Member variables
     */
    private AbstractFPTreeNode root;
    protected HashMap<String, LinkedList<AbstractFPTreeNode>> header;
    protected double minsup;
    protected double totalSupportCount;
    protected List<IIndex> indices;


    /**
     * Constructor
     */
    public AbstractFPTree(SupportCount supportCount, AbstractFPTreeNode root){
        this.totalSupportCount = supportCount.getTotalSupportCount();
        this.root = root;
        header = new HashMap<>();
        supportCount.keySet().forEach(k -> header.put(k, new LinkedList<>()));
    }

    protected AbstractFPTreeNode getRoot(){
        return this.root;
    }

    final public List<IIndex> getIndices(){
        return indices;
    }

    public void extractItemSets(double minsup){
        indices = new LinkedList<>();
        this.minsup = minsup;

        LinkedList<String> cols = new LinkedList<>();

        extractItemSets(root, cols);
    }

    private void extractItemSets(AbstractFPTreeNode node, LinkedList<String> cols){

        for (String col : node.children.keySet()) {
            cols.add(col);
            extractItemSets(node.children.get(col), cols);
            cols.removeLast();
        }

        extractItemSet(node, cols);
    }

    abstract void extractItemSet(AbstractFPTreeNode node, List<String> columns);

    // ToDo: FPTreeNode = template
    AbstractFPTreeNode insertTree(Iterator<String> it) {
        AbstractFPTreeNode node = root;

        while (it.hasNext()) {
            String entry = it.next();

            header.get(entry).add(node = node.getOrCreateChild(entry));
        }
        return node;
    }

}
