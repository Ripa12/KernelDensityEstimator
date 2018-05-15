package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;

import javax.annotation.processing.SupportedOptions;
import java.util.*;

public abstract class AbstractFPTree {

    /**
     * Member class
     */
    public static abstract class FPTreeNode {

        protected FPTreeNode parent;
        protected TreeMap<String, FPTreeNode> children;
        protected int frequency;

        protected FPTreeNode(FPTreeNode parent) {
            this.children = new TreeMap<>();
            this.parent = parent;
            this.frequency = 0;
        }

        protected abstract FPTreeNode clone();

        final FPTreeNode getOrCreateChild(String name){
            FPTreeNode temp;
            if (children.containsKey(name)) {
                temp = children.get(name);
            } else {
                temp = clone();

                children.put(name, temp);
            }
            temp.frequency++;
            return temp;
        }

        final FPTreeNode getChild(String name){
            FPTreeNode temp = null;
            if (children.containsKey(name)) {
                temp = children.get(name);
            }
            return temp;
        }

        final int getFrequency(){
            return frequency;
        }
    }

    /**
     * Member variables
     */
    protected FPTreeNode root;
    protected HashMap<String, LinkedList<FPTreeNode>> header;
    protected double minsup;
    protected double totalSupportCount;
    protected List<IIndex> indices;


    /**
     * Constructor
     */
    public AbstractFPTree(SupportCount supportCount){
        this.totalSupportCount = supportCount.getTotalSupportCount();
        header = new HashMap<>();
        supportCount.keySet().forEach(k -> header.put(k, new LinkedList<>()));
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

    private void extractItemSets(FPTreeNode node, LinkedList<String> cols){

        for (String col : node.children.keySet()) {
            cols.add(col);
            extractItemSets(node.children.get(col), cols);
            cols.removeLast();
        }

        extractItemSet(node, cols);
    }

    abstract void extractItemSet(FPTreeNode node, List<String> columns);

    // ToDo: FPTreeNode = template
    FPTreeNode insertTree(Set<String> transactions) {

        Iterator<String> it = transactions.iterator();
        FPTreeNode node = root;

        while (it.hasNext()) {
            String entry = it.next();

            header.get(entry).add(node = node.getOrCreateChild(entry));
        }
        return node;
    }

}
