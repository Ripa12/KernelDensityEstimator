package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.AbstractIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.DataStructure.IntervalTree;

import java.util.*;

public abstract class AbstractFPTree {


    /**
     * Utility class
     */
    protected class FPTreeNode{

        FPTreeNode parent;
        TreeMap<String, FPTreeNode> children;
        protected IntervalTree[] intervalTrees;

        FPTreeNode(FPTreeNode parent, int level, Set<String> transactions) {
            this.intervalTrees = new IntervalTree[level];
            Iterator<String> it = transactions.iterator();

            int index = 0;
            while (it.hasNext() && index < level){
                this.intervalTrees[index] = new IntervalTree(it.next());
                index++;
            }

            this.children = new TreeMap<>();
            this.parent = parent;
        }

        void updateMinMax(int index, IntervalTree.NodeData data){
            intervalTrees[index].setMinVal(data.getLow());
            intervalTrees[index].setMaxVal(data.getHigh());
        }

        void addData(int index, IntervalTree.NodeData data){
            intervalTrees[index].insert(data);
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
        root = new FPTreeNode(null, 0, Collections.emptySet());

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

        for(LinkedList<FPTreeNode> list : header.values()){
            for(FPTreeNode node : list){
                extractItemSet(node);
            }
        }
    }

    abstract void extractItemSet(FPTreeNode node);

    // ToDo: FPTreeNode = template
    FPTreeNode insertTree(Set<String> transactions) {

        Iterator<String> it = transactions.iterator();
        FPTreeNode node = root;

        int level = 1;

        while (it.hasNext()) {
            String entry = it.next();
            FPTreeNode temp;
            if (node.children.containsKey(entry)) {
                temp = node.children.get(entry);
                //temp.addData(entry);
            } else {
                temp = new FPTreeNode(node, level, transactions);
                //temp.addData(entry.getValue());

                node.children.put(entry, temp);
                header.get(entry).add(temp);
            }

            node = temp;
            level ++;
        }
        return node;
    }

}
