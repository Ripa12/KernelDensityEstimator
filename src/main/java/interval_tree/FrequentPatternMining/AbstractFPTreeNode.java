package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Richard on 2018-05-19.
 */
public abstract class AbstractFPTreeNode{

    List<? extends IIndex> indices;
    TreeMap<String, AbstractFPTreeNode> children;
    private AbstractFPTreeNode parent;
    private int frequency;

    protected String column;

    protected AbstractFPTreeNode(AbstractFPTreeNode parent, String column) {
        this.children = new TreeMap<>();
        indices = new ArrayList<>();
        this.parent = parent;
        this.frequency = 0;

        this.column = column;
    }

    protected abstract AbstractFPTreeNode makeChild(String column);

    public abstract List<IIndex> extractIndexes(double frequency, String tableName, List<String> columns);

    final AbstractFPTreeNode incrementFrequencyOfChild(String name){
        AbstractFPTreeNode temp;
        if (children.containsKey(name)) {
            temp = children.get(name);
        } else {
            temp = makeChild(name); // ToDo: rename makeChild() to makeChild()

            children.put(name, temp);
        }
        temp.frequency++;
        return temp;
    }

    public final AbstractFPTreeNode getChild(String name){
        AbstractFPTreeNode temp = null;
        if (children.containsKey(name)) {
            temp = children.get(name);
        }

        return temp;
    }

    protected List<? extends IIndex> getIndices(){
        return indices;
    }

    protected void setIndices(List<? extends IIndex> n){
        indices = n;
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
            child.parent = this;
        }
    }

    public final AbstractFPTreeNode getParent(){
        return parent;
    }
    final void setParent(AbstractFPTreeNode parent){
        this.parent = parent;
    }

    final void setFrequency(int freq){
        this.frequency = freq;
    }
    final void incFrequency(){
        this.frequency += 1;
    }


    final TreeMap<String, AbstractFPTreeNode> getChildren(){
        return this.children;
    }

    public Set<String> getNamesOfChildren(){
        return children.keySet();
    }

    AbstractFPTreeNode clone(AbstractFPTreeNode other){
        return doClone(other, other.indices);
    }
    protected abstract AbstractFPTreeNode doClone(AbstractFPTreeNode other, List<? extends IIndex> indices);
}
