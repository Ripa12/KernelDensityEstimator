package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Richard on 2018-05-19.
 */
public abstract class AbstractFPTreeNode{

    protected AbstractFPTreeNode parent;
    protected TreeMap<String, AbstractFPTreeNode> children;
    protected int frequency;

    protected String column;

    protected AbstractFPTreeNode(AbstractFPTreeNode parent, String column) {
        this.children = new TreeMap<>();
        this.parent = parent;
        this.frequency = 0;

        this.column = column;
    }

    protected abstract AbstractFPTreeNode clone(String column);
    protected abstract AbstractFPTreeNode cloneRoot();
    public abstract List<IIndex> extractIndexes(String tableName, List<String> columns);

    final AbstractFPTreeNode getOrCreateChild(String name){ // ToDo: Is this a good name? Maybe increment frequency in an package private function instead; easier to understand that way.
        AbstractFPTreeNode temp;
        if (children.containsKey(name)) {
            temp = children.get(name);
        } else {
            temp = clone(name); // ToDo: rename clone() to makeChild()

            children.put(name, temp);
        }
        temp.frequency++;
        return temp;
    }

    final AbstractFPTreeNode getChild(String name){
        AbstractFPTreeNode temp = null;
        if (children.containsKey(name)) {
            temp = children.get(name);
        }

        return temp;
    }



    final boolean hasChild(String name){
        return children.containsKey(name);
    }
    final int getChildCount(){
        return children.size();
    }

    final int getFrequency(){
        return frequency;
    }


    /// new

    public void addChild(AbstractFPTreeNode child) {
        if(!this.children.containsKey(child.column)) {
            this.children.put(child.column, child);
            child.parent = this; // Redundant, already done in clone()
        }
    }

    final void removeChild(AbstractFPTreeNode child){
        this.children.remove(child.column);
    }

    final void setFrequency(int freq){
        this.frequency = freq;
    }

    final void incFrequency(){
        this.frequency += 1;
    }

    final AbstractFPTreeNode getParent(){
        return parent;
    }

    final TreeMap<String, AbstractFPTreeNode> getChildren(){
        return this.children;
    }

    abstract void combineNode(AbstractFPTreeNode other);

//    final AbstractFPTreeNode getFirstChild(){
//        AbstractFPTreeNode temp = null;
//        if (!children.firstKey().isEmpty()) {
//            temp = children.get(children.firstKey());
//        }
//
//        return temp;
//    }
}
