package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;

import java.util.*;

public abstract class AbstractFPTree {

    /**
     * Member class
     */
    public static class FPTreeNode {

        protected FPTreeNode parent;
        protected TreeMap<String, FPTreeNode> children;
        protected int frequency;

        FPTreeNode(FPTreeNode parent) {
            this.children = new TreeMap<>();
            this.parent = parent;
            this.frequency = 0;
        }

        FPTreeNode getOrCreateChild(String name){
            FPTreeNode temp;
            if (children.containsKey(name)) {
                temp = children.get(name);
            } else {
                temp = new FPTreeNode(this);

                children.put(name, temp);
            }
            frequency++;
            return temp;
        }

        FPTreeNode getChild(String name){
            FPTreeNode temp = null;
            if (children.containsKey(name)) {
                temp = children.get(name);
            }
            return temp;
        }

        int getFrequency(){
            return frequency;
        }
    }

    /**
     * Member variables
     */
    protected FPTreeNode root;
    protected HashMap<String, LinkedList<FPTreeNode>> header;
    protected int totalSupportCount;
    protected double minsup;
    protected List<IIndex> indices;


    /**
     * Constructor
     */
    public AbstractFPTree(Map<String, Integer[]> supportCount){
        root = new FPTreeNode(null);

        header = new HashMap<>();
        supportCount.keySet().forEach(k -> header.put(k, new LinkedList<>()));

        totalSupportCount = 0;
        supportCount.values().forEach(k -> totalSupportCount += k[0]);
    }

    public List<IIndex> getIndices(){
        return indices;
    }

    public void extractItemSets(double minsup){
        assert minsup <= 1.0 && minsup >= 0.0;
        this.minsup = minsup;
        indices = new LinkedList<>();

        LinkedList<String> cols = new LinkedList<>();

        extractItemSets(root, cols);

//        for(LinkedList<FPTreeNode> list : header.values()){
//            for(FPTreeNode node : list){
//                extractItemSet(node);
//            }
//        }
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
