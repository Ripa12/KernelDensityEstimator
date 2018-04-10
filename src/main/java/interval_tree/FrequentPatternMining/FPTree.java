package interval_tree.FrequentPatternMining;

import interval_tree.DataStructure.IntervalTree;

import java.util.*;

// http://www.programering.com/a/MDM2QTNwATg.html
// https://wimleers.com/sites/wimleers.com/files/FP-Growth%20presentation%20handouts%20—%C2%A0Florian%20Verhein.pdf
// http://www.cis.hut.fi/Opinnot/T-61.6020/2008/fptree.pdf


public class FPTree {

    public class FPTreeNode{

        FPTreeNode parent;
        TreeMap<String, FPTreeNode> children;
        private IntervalTree[] intervalTree;

        public FPTreeNode(FPTreeNode parent, int level) {
            this.intervalTree = new IntervalTree[level];
            for (int i = 0; i < level; i++) {
                this.intervalTree[i] = new IntervalTree();
            }
//            Arrays.fill(this.intervalTree, new IntervalTree());

            this.children = new TreeMap<>();
            this.parent = parent;
        }

        public void addData(int index, IntervalTree.NodeData data){
            intervalTree[index].insert(data);
        }

    }

    private FPTreeNode root;

    public FPTree(){
        root = new FPTreeNode(null, 0);
    }

    public void insertTree(Set<String> transactions, IntervalTree.NodeData[] data){

        insertTree(transactions.iterator(), data, 1, root);
    }

    private void insertTree(Iterator<String> transaction, IntervalTree.NodeData[] data, int level, FPTreeNode node){

        if(!transaction.hasNext()){

            for(int i = 0; i < data.length; i++)
                node.addData(i, data[i]);

            return;
        }


        String entry = transaction.next();
        FPTreeNode temp;
        if(node.children.containsKey(entry)){
            temp = node.children.get(entry);
            //temp.addData(entry);
        }
        else{
            temp = new FPTreeNode(node, level);
            //temp.addData(entry.getValue());

            node.children.put(entry, temp);
        }

        insertTree(transaction, data, level + 1, temp);
    }
}
